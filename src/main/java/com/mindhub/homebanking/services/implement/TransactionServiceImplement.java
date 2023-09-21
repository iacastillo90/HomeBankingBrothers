package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.models.LoanType;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImplement implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Override
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> findByType(LoanType transactionType) {
        return transactionRepository.findByType(transactionType);
    }

    @Override
    public List<Transaction> findByDateBetweenAndAccountNumber(LocalDateTime startDate, LocalDateTime endDate, String accountNumber) {
        return transactionRepository.findByDateBetweenAndAccountNumber(startDate, endDate, accountNumber);
    }
}
