package com.mindhub.homebanking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Loan {
    @Id
    @GenericGenerator(name= "native", strategy = "native")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double maxAccount;
    @Enumerated(EnumType.STRING)
    private LoanType name;
    private Double interest;

    @ElementCollection
    @CollectionTable(name = "loan_payments", joinColumns = @JoinColumn(name = "loan_id"))
    @Column(name = "payment")
    private List<Integer> payments = new ArrayList<>();

    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER)
    private Set<ClientLoan> clientLoans = new HashSet<>();

    public Loan() {
    }

    public Loan(LoanType name, Double maxAccount, List<Integer> payments, Double interest) {
        this.name = name;
        this.maxAccount = maxAccount;
        this.payments = payments;
        this.interest = interest;
    }

    public Set<Client> getClients() {
        return clientLoans.stream().map(ClientLoan::getClient).collect(Collectors.toSet());
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

    public List<Integer> getPayments() {
        return payments;
    }

    public LoanType getName() {
        return name;
    }
    public void setLoanType(LoanType name) {
        this.name = name;
    }
    public Set<ClientLoan> getClientLoans() {
        return clientLoans;
    }
    public void setClientLoans(Set<ClientLoan> clientLoans) {
        this.clientLoans = clientLoans;
    }
    public double getInterest() {
        return interest;
    }
    public void setInterest(double interest) {
        this.interest = interest;
    }
}
