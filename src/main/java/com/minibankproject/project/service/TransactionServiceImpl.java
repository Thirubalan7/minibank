package com.minibankproject.project.service;

import com.minibankproject.project.dto.TransferRequest;
import com.minibankproject.project.entity.AccountEntity;
import com.minibankproject.project.entity.TransactionEntity;
import com.minibankproject.project.kafka.TransactionProducer;
import com.minibankproject.project.repository.AccountRepository;
import com.minibankproject.project.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionProducer producer;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public void deposit(Long accountId,Double amount){
        requirePositiveAmount(amount);
        String email = currentUserEmail();

        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        assertOwner(account, email);

        account.setBalance(safe(account.getBalance()) + amount);
        accountRepository.save(account);

        TransactionEntity tx = new TransactionEntity();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType("DEPOSIT");
        tx.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(tx);

        producer.sendTransaction(accountId, amount, "DEPOSIT");

    }

    @Override
    @Transactional
    public void withdraw(Long accountId,Double amount){
        requirePositiveAmount(amount);
        String email = currentUserEmail();

        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        assertOwner(account, email);

        double newBalance = safe(account.getBalance()) - amount;
        if (newBalance < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        account.setBalance(newBalance);
        accountRepository.save(account);

        TransactionEntity tx = new TransactionEntity();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType("WITHDRAW");
        tx.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(tx);

        producer.sendTransaction(accountId, amount, "WITHDRAW");

    }


    @Override
    @Transactional
    public void transfer(TransferRequest request){
        if (request == null) {
            throw new IllegalArgumentException("Request is required");
        }
        requirePositiveAmount(request.getAmount());
        if (request.getFromAccountId() == null || request.getToAccountId() == null) {
            throw new IllegalArgumentException("fromAccountId and toAccountId are required");
        }
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new IllegalArgumentException("fromAccountId and toAccountId must be different");
        }

        String email = currentUserEmail();

        AccountEntity from = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("From account not found"));
        AccountEntity to = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new RuntimeException("To account not found"));

        assertOwner(from, email);

        double newFromBalance = safe(from.getBalance()) - request.getAmount();
        if (newFromBalance < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        from.setBalance(newFromBalance);
        to.setBalance(safe(to.getBalance()) + request.getAmount());
        accountRepository.save(from);
        accountRepository.save(to);

        LocalDateTime now = LocalDateTime.now();

        TransactionEntity outTx = new TransactionEntity();
        outTx.setAccount(from);
        outTx.setAmount(request.getAmount());
        outTx.setType("TRANSFER_OUT");
        outTx.setTransactionDate(now);
        transactionRepository.save(outTx);

        TransactionEntity inTx = new TransactionEntity();
        inTx.setAccount(to);
        inTx.setAmount(request.getAmount());
        inTx.setType("TRANSFER_IN");
        inTx.setTransactionDate(now);
        transactionRepository.save(inTx);

        producer.sendTransaction(request.getFromAccountId(), request.getAmount(), "TRANSFER");

    }

    @Override
    public List<TransactionEntity> getTransactions(Long accountId){

        return transactionRepository.findByAccountId(accountId);

    }

    @Override
    public List<TransactionEntity> getTransactionsByUser(Long userId){

        return transactionRepository.findByAccountUserId(userId);

    }

    private static void requirePositiveAmount(Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }
    }

    private static double safe(Double balance) {
        return balance == null ? 0.0 : balance;
    }

    private static String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new AccessDeniedException("Unauthorized");
        }
        return auth.getName();
    }

    private static void assertOwner(AccountEntity account, String email) {
        if (account.getUser() == null || account.getUser().getEmail() == null) {
            throw new AccessDeniedException("Access denied");
        }
        if (!account.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
