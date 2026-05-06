package com.banking.banking_system.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ownerName;
    private double balance;
    private String currency;
    private int pin;
    private boolean frozen = false;

    @ElementCollection
    @CollectionTable(name = "account_history", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "entry")
    private List<String> history = new ArrayList<>();

    public BankAccount() {}

    public BankAccount(String ownerName, double balance, String currency, int pin) {
        this.ownerName = ownerName;
        this.balance = balance;
        this.currency = currency;
        this.pin = pin;
    }

    public Long getId() { return id; }
    public String getOwnerName() { return ownerName; }
    public double getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public int getPin() { return pin; }
    public boolean isFrozen() { return frozen; }
    public List<String> getHistory() { return history; }

    public void setBalance(double balance) { this.balance = balance; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setPin(int pin) { this.pin = pin; }
    public void setFrozen(boolean frozen) { this.frozen = frozen; }
}