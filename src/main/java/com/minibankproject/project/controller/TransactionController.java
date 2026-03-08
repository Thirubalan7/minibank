package com.minibankproject.project.controller;

import com.minibankproject.project.dto.TransferRequest;
import com.minibankproject.project.entity.TransactionEntity;
import com.minibankproject.project.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/deposit")
    public String deposit(@RequestParam Long accountId,
                          @RequestParam Double amount,
                          Authentication authentication){

        transactionService.deposit(accountId, amount);
        return "Deposit successful";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/withdraw")
    public String withdraw(@RequestParam Long accountId,
                           @RequestParam Double amount,
                           Authentication authentication){

        transactionService.withdraw(accountId, amount);
        return "Withdraw successful";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transfer")
    public String transfer(@RequestBody TransferRequest request, Authentication authentication){

        transactionService.transfer(request);
        return "Transfer successful";
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER')")
    @GetMapping("/account/{accountId}")
    public List<TransactionEntity> getTransactions(@PathVariable Long accountId){
        return transactionService.getTransactions(accountId);
    }
}