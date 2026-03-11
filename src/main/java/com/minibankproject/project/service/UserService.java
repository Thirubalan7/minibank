package com.minibankproject.project.service;

import com.minibankproject.project.entity.UserEntity;

import java.util.List;

public interface UserService {

    UserEntity createUser(UserEntity user);

    UserEntity createEmployee(UserEntity user);

    List<UserEntity> getEmployees();


    List<UserEntity> getUsers();

}