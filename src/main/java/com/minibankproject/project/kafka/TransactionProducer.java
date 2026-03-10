package com.minibankproject.project.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "bank-transactions";

    public void sendTransaction(Long accountNumber, Double amount, String type)
    {
        String message = accountNumber + "," + amount + "," + type;

        kafkaTemplate.send(TOPIC, message);

        System.out.println("Transaction event sent to Kafka: " + message);
    }
}