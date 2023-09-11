package com.mindhub.homebanking.dtos;

import java.util.List;

public class LoanApplicationDTO {
    private String typeName;
    private double amount;
    private List<Integer> payments;
    private String numberAccountDestination;

    public LoanApplicationDTO(String typeName, double amount, List<Integer> payments, String numberAccountDestination) {
        this.typeName = typeName;
        this.amount = amount;
        this.payments = payments;
        this.numberAccountDestination = numberAccountDestination;
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
}
