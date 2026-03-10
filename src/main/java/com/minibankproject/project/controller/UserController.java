package com.minibankproject.project.controller;

import com.minibankproject.project.dto.TransferRequest;
import com.minibankproject.project.entity.AccountEntity;
import com.minibankproject.project.entity.TransactionEntity;
import com.minibankproject.project.entity.UserEntity;
import com.minibankproject.project.repository.UserRepository;
import com.minibankproject.project.service.AccountService;
import com.minibankproject.project.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private TransactionService transactionService;
 
    @Autowired
    private UserRepository userRepository;



    @PreAuthorize("hasRole('USER')")
    @GetMapping("/transactions/me")
    public List<TransactionEntity> myTransactions(Authentication authentication){
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return transactionService.getTransactionsByUser(user.getId());
    }

   @PreAuthorize("hasRole('USER')")
    @PostMapping("/deposit")
    public ResponseEntity<String>  deposit(@RequestParam Long accountId, @RequestParam Double amount) {
        transactionService.deposit(accountId, amount);

       return ResponseEntity.ok("Deposit successful");
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestParam Long accountId, @RequestParam Double amount) {
        transactionService.withdraw(accountId, amount);

        return ResponseEntity.ok("Withdraw successful");
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        transactionService.transfer(request);

        return ResponseEntity.ok("Deposit successful");
    }
}
