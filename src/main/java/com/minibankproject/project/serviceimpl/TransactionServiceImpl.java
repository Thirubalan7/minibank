package com.minibankproject.project.serviceimpl;

import com.minibankproject.project.dto.TransferRequest;
import com.minibankproject.project.entity.AccountEntity;
import com.minibankproject.project.entity.TransactionEntity;
import com.minibankproject.project.kafka.TransactionProducer;
import com.minibankproject.project.repository.AccountRepository;
import com.minibankproject.project.repository.TransactionRepository;
import com.minibankproject.project.service.TransactionService;
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
    public void deposit(Long accountId,Double amount){


        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance() + amount);

        accountRepository.save(account);


        producer.sendTransaction(accountId, amount, "DEPOSIT");

    }

    @Override
    @Transactional
    public void withdraw(Long accountId,Double amount){



        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if(account.getBalance() < amount){
            throw new RuntimeException("Insufficient balance");
        }


        account.setBalance(account.getBalance() - amount);

        accountRepository.save(account);

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



        AccountEntity from = accountRepository.findById(request.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("From account not found"));
        AccountEntity to = accountRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new RuntimeException("To account not found"));



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

        return transactionRepository.findByAccount_User_Id(userId);

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