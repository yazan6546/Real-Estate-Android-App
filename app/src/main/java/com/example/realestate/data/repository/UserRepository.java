package com.example.realestate.data.repository;

import com.example.realestate.data.db.dao.UserDao;
import com.example.realestate.data.db.entity.UserEntity;

public class UserRepository {

    UserDao userDao;
    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public void insertUser(UserEntity user) {
        userDao.insertUser(user);
    }

    public UserEntity getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    public void updateUser(UserEntity user) {
        userDao.updateUser(user);
    }

    public void deleteUser(UserEntity user) {
        userDao.deleteUser(user);
    }

    public UserEntity getUserByEmailAndPassword(String email, String password) {
        return userDao.getUserByEmailAndPassword(email, password);
    }
}
