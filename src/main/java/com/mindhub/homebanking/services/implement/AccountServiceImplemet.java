package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class AccountServiceImplemet implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Override
    public List<AccountDTO> getAccountsDTO() {
        return accountRepository.findAll()
                .stream()
                .map(account -> new AccountDTO(account))
                .collect(Collectors.toList());
    }
    @Override
    public AccountDTO getAccountDTOCurrent(long id) {
        return new AccountDTO(accountRepository.findById(id).orElse(null));
    }
    @Override
    public List<Account> accounts(Client client) {
        return accountRepository.findByClient(client);
    }
    @Override
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    public List<Account> findByClientList(Client client) {
        return accountRepository.findByClient(client);
    }

    @Override
    public Account findByNumber(String number) {
        return accountRepository.findByNumber(number );
    }

}
