package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.models.LoanType;

import java.util.List;

public interface LoanService {
    List<LoanDTO> getLoanApplicationDTO();
    void saveLoan(Loan Loan);
    Loan findByName(LoanType name);

}
