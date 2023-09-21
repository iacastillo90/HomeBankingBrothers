package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.LoanType;

public class ClientLoanDTO {

    private Long id;
    private Long loanId;
    private LoanType name;
    private Double amount;
    private Integer payments;


    public ClientLoanDTO(ClientLoan clientLoan) {
        id = clientLoan.getId();
        loanId = clientLoan.getLoan().getId();
        name = clientLoan.getLoan().getName();
        amount = clientLoan.getAmount();
        payments = clientLoan.getPayments();

    }

    public Long getId() {
        return id;
    }
    public Long getLoanId() {
        return loanId;
    }

    public LoanType getName() {
        return name;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }

}
