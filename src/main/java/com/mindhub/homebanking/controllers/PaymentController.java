package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.PaymentsDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CardService cardService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @Transactional
    @PostMapping("/payments")
    public ResponseEntity<Object> paymentDTOList(@RequestBody PaymentsDTO paymentsDTO, Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());
        Card card = cardService.findByNumber(paymentsDTO.getNumber());
        Set<Account> accountList = client.getAccounts();
        List<Account> accountBalance = accountList.stream()
                .filter(account -> account.getBalance() >= paymentsDTO.getAmount())
                .collect(Collectors.toList());

        Account account = accountBalance.stream().findFirst().orElse(null);
        String mensaje = "";

        if (paymentsDTO == null) {
            mensaje = "Data is missing.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        if (paymentsDTO.getNumber().isEmpty() || paymentsDTO.getCvv() != card.getCvv() || paymentsDTO.getAmount() <= 0 || paymentsDTO.getDescription().isEmpty()) {
            mensaje = "Payment data is incorrect.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        if (!paymentsDTO.getName().equals(client.getFirstName() + " " + client.getLastName())) {
            mensaje = "Name doesn't match.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        String cardThruDate = card.toString().substring(5, 7) + '/' + card.getThruDate().toString().substring(0, 4);
        String paymentThruDate = paymentsDTO.getThruDate().toString();

        System.out.println(cardThruDate.equals(paymentThruDate));



        if (account == null) {
            mensaje = "The account does not exist.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        if (account.getBalance() < paymentsDTO.getAmount()) {
            mensaje= "Insufficient funds.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        if (!card.getIsActive() || !account.getIsActive()) {
            mensaje= "The card or account can no longer be used.";
            return new ResponseEntity<>(mensaje, HttpStatus.BAD_REQUEST);
        }

        Double newBalance = account.getBalance() - paymentsDTO.getAmount();
        account.setBalance(newBalance);

        // Registrar la transacción
        Transaction transaction = new Transaction(TransactionType.DEBIT, paymentsDTO.getAmount(), paymentsDTO.getDescription(), LocalDateTime.now());

        account.addTransaction( transaction);
        Double auxDestiny=account.getBalance()+paymentsDTO.getAmount();
        account.setBalance(auxDestiny);
        accountService.saveAccount(account);

        transaction.setBalance(newBalance);
        transactionService.saveTransaction(transaction);


        mensaje = "Transaction successfully completed.";
        return new ResponseEntity<>(mensaje, HttpStatus.OK);
    }
}
