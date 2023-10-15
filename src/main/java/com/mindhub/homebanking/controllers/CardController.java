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
    public List<CardDTO> getCardsTrue() {
        List<CardDTO> CardDTOList = cardRepository.findAll().stream().map(CardDTO::new).collect(Collectors.toList());
        List<CardDTO> CardDTOListTrue = CardDTOList.stream().filter(CardDTO::getIsActive).collect(Collectors.toList());
        return CardDTOListTrue;
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
                .filter(card -> card.getType() == cardType)
                .collect(Collectors.toList());

        if (listCard.size() >= 3) {
            mensaje = "Cannot create more cards of this type.";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }

        String cardNumber = generateValidCardNumber(); // Genera un número de tarjeta válido
        int cvv = getRandomNumber(100, 999);

        LocalDate fromDate = LocalDate.now();
        LocalDate thruDate = fromDate.plusYears(5);
        String cardHolder = clientCurrent.getFirstName() + " " + clientCurrent.getLastName();

        Card card = new Card(cardHolder, cardType, cardColor, cardNumber, cvv, thruDate, fromDate, clientCurrent, true);
        clientCurrent.addCard(card);
        cardService.saveCard(card);

        mensaje = "You have successfully created a card.";
        return new ResponseEntity<>(mensaje, HttpStatus.CREATED);
    }

    // Función para generar un número de tarjeta válido utilizando el algoritmo de Luhn
    public String generateValidCardNumber() {
        String cardNumber = "";
        for (int i = 0; i < 16; i++) {
            int digit = getRandomDigit();
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            cardNumber += digit;
            if (i == 3 || i == 7 || i == 11) {
                cardNumber += '-';
            }
        }
        return cardNumber;
    }

    // Genera un dígito aleatorio del 0 al 9
    public int getRandomDigit() {
        return (int) (Math.random() * 10);
    }

    public int getRandomNumber(int min1, int max1) {
        return (int) ((Math.random() * (max1 - min1)) + min1);
    }

    @PatchMapping("/clients/current/cards/delete/{id}")
    public ResponseEntity<Object> deleteCard(Authentication authentication, @PathVariable Long id) {
        Client client = clientService.findByEmail(authentication.getName());
        Card card = cardService.findById(id);
        Boolean exists = client.getCards().contains(card);
        String mensaje = " ";

        if (card == null) {
            mensaje = "Card not found.";
            return new ResponseEntity<>(mensaje, HttpStatus.NOT_FOUND);
        }
        if (!exists) {
            mensaje ="Card not exists";
            return new ResponseEntity<>(mensaje, HttpStatus.NOT_FOUND);
        }
        if (!card.getIsActive()) {
            mensaje = "Card already deleted";
            return new ResponseEntity<>(mensaje, HttpStatus.CREATED);
        }
        card.setIsActive(false);
        cardService.saveCard(card);

        mensaje="Set Good: false";
        return new ResponseEntity<>(mensaje, HttpStatus.CREATED);
    }
}
