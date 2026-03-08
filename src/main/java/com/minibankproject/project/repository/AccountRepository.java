package com.minibankproject.project.repository;

import com.minibankproject.project.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity,Long> {

    List<AccountEntity> findByUser_Id(Long userId);


}
