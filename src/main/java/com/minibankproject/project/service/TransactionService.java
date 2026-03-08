package com.minibankproject.project.service;

import com.minibankproject.project.dto.TransferRequest;
import com.minibankproject.project.entity.TransactionEntity;

import java.util.List;

public interface TransactionService {

    void deposit(Long accountId,Double amount);

    void withdraw(Long accountId,Double amount);

    void transfer(TransferRequest request);

    List<TransactionEntity> getTransactions(Long accountId);

    List<TransactionEntity> getTransactionsByUser(Long userId);

}
