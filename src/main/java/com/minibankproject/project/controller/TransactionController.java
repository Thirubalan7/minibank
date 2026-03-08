package com.minibankproject.project.controller;

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

    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER')")
    @GetMapping("/account/{accountId}")
    public List<TransactionEntity> getTransactions(@PathVariable Long accountId){
        return transactionService.getTransactions(accountId);
    }


}