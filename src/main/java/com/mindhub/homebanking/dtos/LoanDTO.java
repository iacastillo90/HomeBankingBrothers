package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.models.LoanType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LoanDTO {

    private Long id;
    private Double maxAccount;
    private LoanType name;
    private List<Integer> payments;
    private List<ClientDTO> clients;
    private double interest;

    public LoanDTO() {
    }

    public LoanDTO(Loan loan) {
        this.id = loan.getId();
        this.name = loan.getName();
        this.maxAccount = loan.getMaxAccount();
        this.payments = loan.getPayments();
        this.interest = loan.getInterest();
        this.clients = loan.getClientLoans().stream().map(clientLoan -> new ClientDTO(clientLoan.getClient()))
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMaxAccount() {
        return maxAccount;
    }

    public void setMaxAccount(Double maxAccount) {
        this.maxAccount = maxAccount;
    }

    public LoanType getName() {
        return name;
    }

    public void setName(LoanType name) {
        this.name = name;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public void setPayments(List<Integer> payments) {
        this.payments = payments;
    }

    public List<ClientDTO> getClients() {
        return clients;
    }

    public void setClients(List<ClientDTO> clients) {
        this.clients = clients;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }
}
