package com.banking.banking_system.repository;

import com.banking.banking_system.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByOwnerNameContainingIgnoreCase(String name);
}