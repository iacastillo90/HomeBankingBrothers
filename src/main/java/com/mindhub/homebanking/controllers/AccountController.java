package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.TypeAccounts;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;

    @RequestMapping("/accounts")
    public List<AccountDTO> getAccountsDTO(){
        return accountService.getAccountsDTO();
    }
    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        return accountService.getAccountDTOCurrent(id);
    }

    @GetMapping("/clients/current/accounts")
    public ResponseEntity<List<AccountDTO>> getClientAccounts(Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());
        List<Account> accounts = accountService.findByClientList(client);
        return new ResponseEntity<>(accounts.stream()
                .map(AccountDTO::new)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<Object> registerAccount(Authentication authentication,
                                                  @RequestParam String typeAccounts) {
        Client client = clientService.findByEmail(authentication.getName());
        String mensaje = "";

        if (client == null) {
            mensaje = "Client not found";
            return new ResponseEntity<>(mensaje, HttpStatus.NOT_FOUND);
        }

        List<Account> accounts = accountService.findByClientList(client);
        if (accounts.size() >=3) {
            mensaje = "Can't create more accounts";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }

        String number = "VIN" + getStringRandomNumber();
        TypeAccounts typeAccount = TypeAccounts.valueOf(typeAccounts);
        Account account = new Account( number, LocalDate.now(), 0.00, typeAccount);
        client.addAccount(account);
        accountService.saveAccount(account);

        mensaje= "New account created";
        return new ResponseEntity<>(mensaje, HttpStatus.CREATED);
    }

    int min = 00000000;
    int max = 99999999;

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public String getStringRandomNumber() {
        int randomNumber = getRandomNumber(min, max);
        return String.valueOf(randomNumber);
    }
}

