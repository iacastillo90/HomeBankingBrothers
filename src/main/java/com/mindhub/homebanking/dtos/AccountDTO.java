package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.TypeAccounts;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AccountDTO {
    private Long id;
    private String number;
    private LocalDate creationDate;
    private Double balance;
    private List<TransactionDTO> transactions;
    private Boolean isActive ;
    private TypeAccounts typeAccounts;

    public AccountDTO(Account account) {
        id = account.getId();
        number = account.getNumber();
        creationDate = account.getCreationDate();
        balance = account.getBalance();
        transactions = account.getTransactions().stream().map( transaction -> new TransactionDTO(transaction)).collect(Collectors.toList());
        typeAccounts = account.getTypeAccounts();
        isActive = account.getIsActive();

    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Double getBalance() {
        return balance;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }
    public TypeAccounts getTypeAccounts() {
        return typeAccounts;
    }

    public Boolean getIsActive() {
        return isActive;
    }


}