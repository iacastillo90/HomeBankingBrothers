package com.mindhub.homebanking.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentsDTO {
    private String number;
    private double amount;
    private int cvv;
    private String Description;

    private LocalDate thruDate;
    private String name;

    public PaymentsDTO() {
    }

    public PaymentsDTO(String number, double amount, int cvv, String description, LocalDate thruDate, String name) {
        this.number = number;
        this.amount = amount;
        this.cvv = cvv;
        Description = description;
        this.thruDate = thruDate;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public LocalDate getThruDate() {
        return thruDate;
    }

    public void setThruDate(LocalDate thruDate) {
        this.thruDate = thruDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
