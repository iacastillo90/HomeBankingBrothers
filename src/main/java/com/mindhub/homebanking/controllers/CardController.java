package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    private CardRepository cardRepository;


    @Autowired
    private CardService cardService;
    @Autowired
    private ClientService clientService;

    @GetMapping("/cards")
    public List<CardDTO> getCardsDTO() {
        return cardService.getCardsDTO();
    }

    @GetMapping("/cards/{id}")
    public CardDTO getCardDTO(@PathVariable Long id) {
        return cardService.getCardDTO(id);
    }

    @GetMapping("clients/current/cards/true")
    public List<CardDTO> getCardsTrue(){
        List<CardDTO> CardDTOList= cardRepository.findAll().stream().map(CardDTO::new).collect(Collectors.toList());
        List<CardDTO> CardDTOListTrue=CardDTOList.stream().filter(CardDTO::getIsActive).collect(Collectors.toList());
        return  CardDTOListTrue;

    }



    @PostMapping("clients/current/cards")
    public ResponseEntity<Object> createCard(Authentication authentication,
                                             @RequestParam CardType cardType,
                                             @RequestParam CardColor cardColor) {

        Client clientCurrent = clientService.findByEmail(authentication.getName());
        String mensaje = " ";

        // Verificar si el cliente ya tiene una tarjeta del mismo tipo y color
        boolean hasDuplicateCard = clientCurrent.getCards()
                .stream()
                .anyMatch(card -> card.getType() == cardType && card.getColor() == cardColor);

        if (hasDuplicateCard) {
            mensaje = "Cannot create another card of the same type and color.";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }

        List<Card> listCard = clientCurrent
                .getCards()
                .stream()
                .filter(card ->
                        card.getType() == cardType
                )
                .collect(Collectors.toList());

        if (listCard.size() >= 3) {
            mensaje = "Cannot create more cards of this type.";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }

        String cardNumber = getRandomStringCard();
        int cvv = getRandomNumber(100, 999);

        LocalDate fromDate = LocalDate.now();
        LocalDate thruDate = fromDate.plusYears(5);
        String cardHolder = clientCurrent.getFirstName() + " " + clientCurrent.getLastName();

        Card card = new Card(cardHolder, cardType, cardColor, cardNumber, cvv, thruDate, fromDate, clientCurrent, true);
        clientCurrent.addCard(card);
        cardService.saveCard(card);

        mensaje= "You have successfully created a card.";
        return new ResponseEntity<>(mensaje, HttpStatus.CREATED);
    }


    int min1 = 100;
    int max1 = 999;

    public int getRandomNumber(int min1, int max1) {
        return (int) ((Math.random() * (this.max1 - this.min1)) + this.min1);
    }

    int min2 = 0001;
    int max2 = 9999;

    public int getRandomCardNumber(int min2, int max2) {
        return (int) ((Math.random() * (max2 - min2)) - min2);
    }

    public String getRandomCardNumber() {
        int randomCardNumber = getRandomCardNumber(min2, max2);
        return String.valueOf(randomCardNumber);
    }
    public String getRandomStringCard() {
        String cardNumber = "";
        for (int i = 0; i <= 4; i++) {
            String targserie = getRandomCardNumber();
            cardNumber += ("-" + targserie);
        }
        return cardNumber.substring(1);
    }


    @PatchMapping("/clients/current/cards/delete/{id}")
    public ResponseEntity<Object> deleteCard(Authentication authentication,@PathVariable Long id){
        Client client = clientService.findByEmail(authentication.getName());
        Card card = cardService.findById(id);
        Boolean exists= client.getCards().contains(card);

        if (card == null ){
            return new ResponseEntity<>("Card not found", HttpStatus.NOT_FOUND);
        }if (!exists){
            return new ResponseEntity<>("Card not exits", HttpStatus.NOT_FOUND);
        }if (card.getIsActive() == false){
            return new ResponseEntity<>("Card already deleted ", HttpStatus.CREATED);
        }
        card.setIsActive(false);
        cardService.saveCard(card);

        return new ResponseEntity<>("Set Good: false", HttpStatus.CREATED);
    }

}


