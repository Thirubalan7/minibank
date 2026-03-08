package com.minibankproject.project.controller;

import com.minibankproject.project.entity.AccountEntity;
import com.minibankproject.project.entity.TransactionEntity;
import com.minibankproject.project.entity.UserEntity;
import com.minibankproject.project.repository.UserRepository;
import com.minibankproject.project.service.AccountService;
import com.minibankproject.project.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/accounts/me")
    public List<AccountEntity> myAccounts(Authentication authentication){
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return accountService.getAccountsByUser(user.getId());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/transactions/me")
    public List<TransactionEntity> myTransactions(Authentication authentication){
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return transactionService.getTransactionsByUser(user.getId());
    }
}
