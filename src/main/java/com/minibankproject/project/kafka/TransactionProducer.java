package com.minibankproject.project.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    @Autowired
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    private static final String TOPIC = "bank-transactions";

    public void sendTransaction(Long accountNumber, Double amount, String type)
    {

        TransactionEvent event = new TransactionEvent();
        event.setAccountNumber(accountNumber);
        event.setAmount(amount);
        event.setType(type);

        kafkaTemplate.send(TOPIC, event);

        System.out.println("Transaction event sent to Kafka: " + event);
    }
}