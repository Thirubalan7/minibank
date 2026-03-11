package com.minibankproject.project.serviceimpl;

import com.minibankproject.project.entity.AccountEntity;
import com.minibankproject.project.entity.RoleEntity;
import com.minibankproject.project.entity.UserEntity;
import com.minibankproject.project.enums.RoleType;
import com.minibankproject.project.repository.AccountRepository;
import com.minibankproject.project.repository.RoleRepository;
import com.minibankproject.project.repository.UserRepository;
import com.minibankproject.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity getUserByAccountNumber(Long accountNumber) {

        AccountEntity account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return account.getUser();
    }
    @Override
    public UserEntity createUser(UserEntity user) {


        RoleEntity role = roleRepository
                .findByRoleName(RoleType.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(role);


        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public UserEntity createEmployee(UserEntity user) {

        RoleEntity role = roleRepository
                .findByRoleName(RoleType.ROLE_EMPLOYEE)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(role);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public List<UserEntity> getEmployees() {

        return userRepository.findByRole_RoleName(RoleType.ROLE_EMPLOYEE);

    }

    @Override
    public List<UserEntity> getUsers() {

        return userRepository.findByRole_RoleName(RoleType.ROLE_USER);

    }
}