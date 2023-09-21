package com.mindhub.homebanking.dtos;

import java.util.List;

public class LoanApplicationDTO {
    private String typeName;
    private double amount;
    private List<Integer> payments;
    private String numberAccountDestination;
    private double interest;

    public LoanApplicationDTO(String typeName, double amount, List<Integer> payments, String numberAccountDestination, double interest) {
        this.typeName = typeName;
        this.amount = amount;
        this.payments = payments;
        this.numberAccountDestination = numberAccountDestination;
        this.interest = 0;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public void setPayments(List<Integer> payments) {
        this.payments = payments;
    }
    public String getNumberAccountDestination() {
        return numberAccountDestination;
    }
    public void setNumberAccountDestination(String numberAccountDestination) {
        this.numberAccountDestination = numberAccountDestination;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }
}
