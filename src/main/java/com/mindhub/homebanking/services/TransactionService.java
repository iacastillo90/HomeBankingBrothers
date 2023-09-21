package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.LoanType;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    void saveTransaction(Transaction transaction);
    List<Transaction> findByType(LoanType transactionType);
    List<Transaction> findByDateBetweenAndAccountNumber(LocalDateTime startDate, LocalDateTime endDate, String accountNumber);
}
