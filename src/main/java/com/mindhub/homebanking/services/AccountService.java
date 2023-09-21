package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface AccountService {
    List<AccountDTO> getAccountsDTO();
    AccountDTO getAccountDTOCurrent (long id);
    List<Account> accounts (Client client);
    void saveAccount(Account account);
    List<Account> findByClientList (Client client);
    Account findByNumber(String number);
    boolean existsByNumber(String number);
}
