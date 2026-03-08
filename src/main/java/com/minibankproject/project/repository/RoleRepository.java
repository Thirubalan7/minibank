package com.minibankproject.project.repository;

import com.minibankproject.project.entity.RoleEntity;
import com.minibankproject.project.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByRoleName(RoleType roleName);

}