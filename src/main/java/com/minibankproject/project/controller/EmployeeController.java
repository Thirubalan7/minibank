package com.minibankproject.project.controller;


import com.minibankproject.project.entity.AccountEntity;
import com.minibankproject.project.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private AccountService accountService;

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/account")
    public ResponseEntity<AccountEntity> createAccount(@RequestBody AccountEntity account)
    {
        AccountEntity accountEntity=accountService.createAccount(account);

        return ResponseEntity.status(HttpStatus.CREATED).body(accountEntity);
    }

}
