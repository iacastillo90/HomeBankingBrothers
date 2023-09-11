package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.models.LoanType;
import com.mindhub.homebanking.repositories.ClientLoanRepository;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class LoanServiceImplement implements LoanService {
    @Autowired
    private LoanRepository loanRepository;

    @Override
    public List<LoanDTO> getLoanApplicationDTO() {
        return loanRepository.findAll()
                .stream().map(loan -> new LoanDTO(loan))
                .collect(Collectors.toList());
    }
    @Override
    public void saveLoan(Loan Loan) {
        loanRepository.save(Loan);
    }
    @Override
    public Loan findByName(LoanType name) {
        return loanRepository.findByName(name);
    }
}
