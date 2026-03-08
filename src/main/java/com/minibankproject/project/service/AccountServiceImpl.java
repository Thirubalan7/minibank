package com.minibankproject.project.service;

import com.minibankproject.project.entity.AccountEntity;
import com.minibankproject.project.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService{

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public AccountEntity createAccount(AccountEntity account){

        return accountRepository.save(account);

    }


    @Override
    public List<AccountEntity> getAccountsByUser(Long userId) {
        return accountRepository.findByUser_Id(userId);
    }

}
