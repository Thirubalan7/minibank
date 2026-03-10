package com.minibankproject.project.controller;

import com.minibankproject.project.entity.AccountEntity;
import com.minibankproject.project.entity.UserEntity;
import com.minibankproject.project.repository.UserRepository;
import com.minibankproject.project.service.AccountService;
import com.minibankproject.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/employees")
    public UserEntity createEmployee(@RequestBody UserEntity user)
    {
        return userService.createEmployee(user);
    }

    @PreAuthorize("hasAnyRole('USER','MANAGER')")
    @GetMapping("/users")
    public List<AccountEntity> myAccounts(Authentication authentication){
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return accountService.getAccountsByUser(user.getId());
    }
   @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/employees")
    public ResponseEntity<List<UserEntity>> getEmployees()
    {

       return ResponseEntity.ok(userService.getEmployees());
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/users")
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity user)
    {
        UserEntity userEntity= userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userEntity);
    }
}