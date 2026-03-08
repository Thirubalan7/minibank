package com.minibankproject.project.service;

import com.minibankproject.project.entity.AccountEntity;

import java.util.List;

public interface AccountService {

    AccountEntity createAccount(AccountEntity account);

    List<AccountEntity> getAccountsByUser(Long userId);

}
