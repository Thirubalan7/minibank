package com.minibankproject.project.repository;

import com.minibankproject.project.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {

    List<TransactionEntity> findByAccountId(Long accountId);

    List<TransactionEntity> findByAccountUserId(Long userId);
}
