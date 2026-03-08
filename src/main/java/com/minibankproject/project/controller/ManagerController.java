package com.minibankproject.project.controller;

import com.minibankproject.project.entity.UserEntity;
import com.minibankproject.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/employees")
    public UserEntity createEmployee(@RequestBody UserEntity user)
    {
        return userService.createEmployee(user);
    }

   @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/employees")
    public List<UserEntity> getEmployees()
    {
        return userService.getEmployees();
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/users")
    public UserEntity createUser(@RequestBody UserEntity user)
    {
        return userService.createUser(user);
    }
}