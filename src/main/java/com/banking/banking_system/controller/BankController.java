package com.banking.banking_system.controller;

import com.banking.banking_system.model.BankAccount;
import com.banking.banking_system.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.banking.banking_system.model.Loan;


@RestController
@RequestMapping("/api/accounts")
public class BankController {

    private final BankService service;

    public BankController(BankService service) {
        this.service = service;
    }

    @GetMapping
    public List<BankAccount> getAllAccounts() {
        return service.getAllAccounts();
    }

    @PostMapping
    public BankAccount createAccount(@RequestBody Map<String, String> body) {
        return service.createAccount(
                body.get("ownerName"),
                Double.parseDouble(body.get("balance")),
                body.get("currency"),
                Integer.parseInt(body.get("pin"))
        );
    }

    @GetMapping("/search")
    public List<BankAccount> search(@RequestParam String name) {
        return service.findByName(name);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteAccount(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<?> deposit(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(service.deposit(id, Double.parseDouble(body.get("amount"))));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(service.withdraw(id, Double.parseDouble(body.get("amount"))));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/freeze")
    public BankAccount freeze(@PathVariable Long id) {
        return service.freezeAccount(id);
    }

    @PostMapping("/{id}/unfreeze")
    public BankAccount unfreeze(@PathVariable Long id) {
        return service.unfreezeAccount(id);
    }
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(service.transfer(
                    Long.parseLong(body.get("fromId")),
                    Long.parseLong(body.get("toId")),
                    Double.parseDouble(body.get("amount"))
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/{id}/loan")
    public ResponseEntity<?> takeLoan(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(service.takeLoan(
                    id,
                    Double.parseDouble(body.get("amount")),
                    Integer.parseInt(body.get("months"))
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/loans/{loanId}/pay")
    public ResponseEntity<?> payLoan(@PathVariable Long loanId, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(service.payLoan(
                    loanId,
                    Double.parseDouble(body.get("amount"))
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/loans")
    public List<Loan> getAccountLoans(@PathVariable Long id) {
        return service.getAccountLoans(id);
    }

    @GetMapping("/loans/active")
    public List<Loan> getAllActiveLoans() {
        return service.getAllActiveLoans();
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}