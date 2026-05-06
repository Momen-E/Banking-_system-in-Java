package com.banking.banking_system.service;

import com.banking.banking_system.model.BankAccount;
import com.banking.banking_system.repository.BankAccountRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import com.banking.banking_system.model.Loan;
import com.banking.banking_system.repository.LoanRepository;
import java.time.LocalDate;

@Service
public class BankService {

    private final BankAccountRepository repo;
    private final LoanRepository loanRepo;

    public BankService(BankAccountRepository repo,LoanRepository loanRepo) {
        this.repo = repo;
        this.loanRepo=loanRepo;
    }

    public BankAccount createAccount(String name, double balance, String currency, int pin) {
        BankAccount acc = new BankAccount(name, balance, currency, pin);
        return repo.save(acc);
    }

    public Optional<BankAccount> findById(Long id) {
        return repo.findById(id);
    }

    public List<BankAccount> findByName(String name) {
        return repo.findByOwnerNameContainingIgnoreCase(name);
    }

    public List<BankAccount> getAllAccounts() {
        return repo.findAll();
    }

    public void deleteAccount(Long id) {
        repo.deleteById(id);
    }

    public BankAccount deposit(Long id, double amount) {
        BankAccount acc = repo.findById(id).orElseThrow();
        if (acc.isFrozen()) throw new RuntimeException("Account is frozen.");
        acc.setBalance(acc.getBalance() + amount);
        acc.getHistory().add("Deposited " + amount + " " + acc.getCurrency());
        return repo.save(acc);
    }

    public BankAccount withdraw(Long id, double amount) {
        BankAccount acc = repo.findById(id).orElseThrow();
        if (acc.isFrozen()) throw new RuntimeException("Account is frozen.");
        if (amount > acc.getBalance()) throw new RuntimeException("Not enough balance.");
        acc.setBalance(acc.getBalance() - amount);
        acc.getHistory().add("Withdrew " + amount + " " + acc.getCurrency());
        return repo.save(acc);
    }

    public BankAccount freezeAccount(Long id) {
        BankAccount acc = repo.findById(id).orElseThrow();
        acc.setFrozen(true);
        acc.getHistory().add("Account frozen.");
        return repo.save(acc);
    }

    public BankAccount unfreezeAccount(Long id) {
        BankAccount acc = repo.findById(id).orElseThrow();
        acc.setFrozen(false);
        acc.getHistory().add("Account unfrozen.");
        return repo.save(acc);
    }
    public BankAccount transfer(Long fromId, Long toId, double amount) {
        BankAccount from = repo.findById(fromId).orElseThrow();
        BankAccount to = repo.findById(toId).orElseThrow();
        if (from.isFrozen()) throw new RuntimeException("Account is frozen.");
        if (amount > from.getBalance()) throw new RuntimeException("Not enough balance.");

        double amountToAdd = amount;
        if (!from.getCurrency().equals(to.getCurrency())) {
            amountToAdd = convert(amount, from.getCurrency(), to.getCurrency());
        }

        from.setBalance(from.getBalance() - amount);
        from.getHistory().add("Sent " + amount + " " + from.getCurrency() + " to account #" + toId);
        to.setBalance(to.getBalance() + amountToAdd);
        to.getHistory().add("Received " + amountToAdd + " " + to.getCurrency() + " from account #" + fromId);

        repo.save(to);
        return repo.save(from);
    }

    public static double convert(double amount, String from, String to) {
        if (from.equals("USD") && to.equals("EGP")) return amount * 50.10;
        if (from.equals("EGP") && to.equals("USD")) return amount / 50.10;
        if (from.equals("EUR") && to.equals("EGP")) return amount * 58.20;
        if (from.equals("EGP") && to.equals("EUR")) return amount / 58.20;
        if (from.equals("USD") && to.equals("EUR")) return amount * 0.86;
        if (from.equals("EUR") && to.equals("USD")) return amount / 0.86;
        return amount;
    }

    public Loan takeLoan(Long accountId, double amount, int months) {
        BankAccount acc = repo.findById(accountId).orElseThrow();
        if (acc.isFrozen()) throw new RuntimeException("Account is frozen.");
        LocalDate dateTaken = LocalDate.now();
        LocalDate dueDate = dateTaken.plusMonths(months);
        Loan loan = new Loan(accountId, amount, 5.0, dateTaken, dueDate);
        acc.setBalance(acc.getBalance() + amount);
        acc.getHistory().add("Took a loan of " + amount + " " + acc.getCurrency() + " due on " + dueDate);
        repo.save(acc);
        return loanRepo.save(loan);
    }

    public Loan payLoan(Long loanId, double amount) {
        Loan loan = loanRepo.findById(loanId).orElseThrow();
        BankAccount acc = repo.findById(loan.getAccountId()).orElseThrow();
        if (acc.isFrozen()) throw new RuntimeException("Account is frozen.");
        if (amount > acc.getBalance()) throw new RuntimeException("Not enough balance.");
        double current = loan.getCurrentRemaining();
        double actualPayment = Math.min(amount, current);
        acc.setBalance(acc.getBalance() - actualPayment);
        acc.getHistory().add("Paid " + actualPayment + " towards loan #" + loanId);
        if (actualPayment >= current) {
            loan.setRemainingAmount(0);
            loan.setPaidOff(true);
        } else {
            loan.setRemainingAmount(loan.getRemainingAmount() - actualPayment);
        }
        repo.save(acc);
        return loanRepo.save(loan);
    }

    public List<Loan> getAccountLoans(Long accountId) {
        return loanRepo.findByAccountId(accountId);
    }

    public List<Loan> getAllActiveLoans() {
        return loanRepo.findByPaidOffFalse();
    }
}
