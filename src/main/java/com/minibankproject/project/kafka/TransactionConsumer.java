package com.minibankproject.project.kafka;

import com.minibankproject.project.entity.AccountEntity;
import com.minibankproject.project.entity.TransactionEntity;
import com.minibankproject.project.repository.AccountRepository;
import com.minibankproject.project.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionConsumer {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @KafkaListener(topics = "bank-transactions", groupId = "bank-group")
    public void consume(TransactionEvent event) {

        System.out.println("Kafka transaction event received: " + event);

        AccountEntity account = accountRepository.findByAccountNumber(event.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        TransactionEntity tx = new TransactionEntity();
        tx.setAccount(account);
        tx.setAmount(event.getAmount());
        tx.setType(event.getType());
        tx.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(tx);

        System.out.println("Transaction stored successfully");
    }
}