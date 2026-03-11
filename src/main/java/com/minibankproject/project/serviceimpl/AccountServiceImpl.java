package com.minibankproject.project.serviceimpl;

import com.minibankproject.project.entity.AccountEntity;
import com.minibankproject.project.entity.UserEntity;
import com.minibankproject.project.repository.AccountRepository;
import com.minibankproject.project.repository.UserRepository;
import com.minibankproject.project.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public AccountEntity createAccount(AccountEntity account){

        Optional<AccountEntity> existing =
                accountRepository.findByAccountNumber(account.getAccountNumber());

        if(existing.isPresent()){
            throw new RuntimeException("Account number already exists");
        }

        Long userId = account.getUser().getId();

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        account.setUser(user);

        return accountRepository.save(account);

    }


    @Override
    public List<AccountEntity> getAccountsByUser(Long userId) {
        return accountRepository.findByUser_Id(userId);
    }

}
