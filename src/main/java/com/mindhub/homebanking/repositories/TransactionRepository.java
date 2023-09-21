package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.LoanType;
import com.mindhub.homebanking.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.time.LocalDateTime;
import java.util.List;

@RepositoryRestResource
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByType(LoanType type);
    List<Transaction> findByDateBetweenAndAccountNumber(LocalDateTime startDate, LocalDateTime endDate, String accountNumber);
}