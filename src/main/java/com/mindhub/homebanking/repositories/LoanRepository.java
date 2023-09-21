package com.mindhub.homebanking.repositories;

import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.models.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface LoanRepository extends JpaRepository <Loan, Long> {
    Loan findByName(LoanType name);


}
