package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.PaymentsDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardServiceImplement implements CardService {

    @Autowired
    private CardRepository cardRepository;
    @Override
    public List<CardDTO> getCardsDTO() {
        return cardRepository.findAll()
                .stream().map(CardDTO::new)
                .collect(Collectors.toList());
    }
    @Override
    public CardDTO getCardDTO(long id) {
        return new CardDTO(this.findById(id));
    }
    @Override
    public Card findById(long id) {
        return cardRepository.findById(id).orElse(null);
    }
    @Override
    public void saveCard(Card card) {
        cardRepository.save(card);
    }

    @Override
    public Card findByNumber(String number) {
        return cardRepository.findByNumber(number);
    }


}
