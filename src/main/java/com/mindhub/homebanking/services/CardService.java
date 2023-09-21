package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface CardService {
    List<CardDTO> getCardsDTO();
    CardDTO getCardDTO (long id);
    Card findById (long id);
    void saveCard(Card card);
    Card findByNumber(String number);

}
