package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Set;

import static com.mindhub.homebanking.models.TransactionType.*;

@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @Transactional
    @RequestMapping (path = "/transactions",method = RequestMethod.POST)
    public ResponseEntity<Object> createdTransaction(Authentication authentication,
                                                     @RequestParam Double amount,
                                                     @RequestParam String description,
                                                     @RequestParam String numberDestiny,
                                                     @RequestParam String numberOrigin){

        Client client = clientService.findByEmail(authentication.getName());
        Account accountDestiny=accountService.findByNumber(numberDestiny);
        Account accountOrigin=accountService.findByNumber(numberOrigin);
        Set<Account> setNumberOrigin= client.getAccounts();
        String mensaje = " ";

        if ((amount == null) || (description == null) || (numberDestiny == null) || (numberOrigin == null)){
            mensaje = "403 Empty parameters";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }
        if (accountOrigin==null){
            mensaje = "403 The account does not exist";
            return new ResponseEntity<>(mensaje,HttpStatus.FORBIDDEN);
        }
        if (accountDestiny==null){
            mensaje = "Destination account does not exist";
            return new ResponseEntity<>(mensaje,HttpStatus.FORBIDDEN);
        }
        if (numberOrigin.equals(numberDestiny)){
            mensaje = "403 the accounts are the same";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }
        if (setNumberOrigin.contains(numberOrigin)) {
            mensaje = "403 UNAUTHENTICATED ACCOUNT";
            return new ResponseEntity<>(mensaje, HttpStatus.FORBIDDEN);
        }
        if (accountOrigin.getBalance()< amount || amount <= 0){
            mensaje = "You do not have enough balance to make the transaction";
            return new ResponseEntity<>(mensaje,HttpStatus.FORBIDDEN);
        }
        Transaction transactionsOrigin=new Transaction(CREDIT,amount,accountOrigin.getNumber()+description, LocalDateTime.now());
        Transaction transactionsDestiny=new Transaction(DEBIT,amount,accountDestiny.getNumber()+description,LocalDateTime.now());
        transactionService.saveTransaction(transactionsOrigin);
        transactionService.saveTransaction(transactionsDestiny);

        Double auxOrigin=accountOrigin.getBalance()-amount;
        Double auxDestiny=accountDestiny.getBalance()+amount;

        accountOrigin.setBalance(auxOrigin);
        accountDestiny.setBalance(auxDestiny);
        accountService.saveAccount(accountDestiny);
        accountService.saveAccount(accountOrigin);

        mensaje = "201 Successful transfer";
        return new ResponseEntity<>(mensaje,HttpStatus.CREATED);
    }
}
