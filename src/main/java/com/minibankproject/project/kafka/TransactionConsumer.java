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
    public void consume(String message) {

        System.out.println("Kafka transaction event received: " + message);

        try {

            String[] data = message.split(",");

            Long accountNumber = Long.parseLong(data[0]);
            Double amount = Double.parseDouble(data[1]);
            String type = data[2];

            AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            //save transaction

            TransactionEntity tx = new TransactionEntity();
            tx.setAccount(account);
            tx.setAmount(amount);
            tx.setType(type);
            tx.setTransactionDate(LocalDateTime.now());

            transactionRepository.save(tx);

            System.out.println("Transaction stored successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}