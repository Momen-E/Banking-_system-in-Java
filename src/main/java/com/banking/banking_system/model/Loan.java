package com.banking.banking_system.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;
    private double originalAmount;
    private double remainingAmount;
    private double monthlyInterestRate;
    private LocalDate dateTaken;
    private LocalDate dueDate;
    private boolean paidOff = false;

    public Loan() {}

    public Loan(Long accountId, double amount, double monthlyInterestRate, LocalDate dateTaken, LocalDate dueDate) {
        this.accountId = accountId;
        this.originalAmount = amount;
        this.monthlyInterestRate = monthlyInterestRate;
        this.dateTaken = dateTaken;
        this.dueDate = dueDate;
        this.paidOff = false;
        long months = ChronoUnit.MONTHS.between(dateTaken, dueDate);
        this.remainingAmount = amount + (amount * (monthlyInterestRate / 100) * months);
    }

    public Long getId() { return id; }
    public Long getAccountId() { return accountId; }
    public double getOriginalAmount() { return originalAmount; }
    public double getRemainingAmount() { return remainingAmount; }
    public double getMonthlyInterestRate() { return monthlyInterestRate; }
    public LocalDate getDateTaken() { return dateTaken; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isPaidOff() { return paidOff; }

    public void setRemainingAmount(double remainingAmount) { this.remainingAmount = remainingAmount; }
    public void setPaidOff(boolean paidOff) { this.paidOff = paidOff; }

    public double getCurrentRemaining() {
        if (paidOff) return 0;
        LocalDate today = LocalDate.now();
        if (today.isAfter(dueDate)) {
            long extraMonths = ChronoUnit.MONTHS.between(dueDate, today) + 1;
            double penalty = remainingAmount * (monthlyInterestRate / 100) * extraMonths;
            return remainingAmount + penalty;
        }
        return remainingAmount;
    }

    public boolean isOverdue() {
        return !paidOff && LocalDate.now().isAfter(dueDate);
    }
}