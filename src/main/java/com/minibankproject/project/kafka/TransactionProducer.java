package com.minibankproject.project.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "bank-transactions";

    public void sendTransaction(Long accountId, Double amount, String type)
    {
        String message = accountId + "," + amount + "," + type;

        kafkaTemplate.send(TOPIC, message);

        System.out.println("Transaction event sent to Kafka: " + message);
    }
}