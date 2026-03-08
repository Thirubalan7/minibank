package com.minibankproject.project.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    @KafkaListener(topics = "bank-transactions", groupId = "bank-group")
    public void consume(String message)
    {

        System.out.println("Kafka transaction event received: " + message);
    }
}