package com.example.realestate.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.realestate.data.db.dao.UserDao;
import com.example.realestate.data.db.entity.UserEntity;
import com.example.realestate.data.db.result.GenderCount;
import com.example.realestate.domain.mapper.UserMapper;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.service.CallbackUtils;

import java.util.List;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public void insertUser(User user, RepositoryCallback<User> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                userDao.insertUser(UserMapper.fromDomain(user));
                callback.onSuccess();
            } catch (Exception e) {
                Log.e("UserRepository", "Error inserting user", e);
                callback.onError(e);
            }
        });
    }

    public void insertAll(List<User> users, RepositoryCallback<Void> callback) {

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                userDao.insertAll(UserMapper.fromDomainList(users));
                callback.onSuccess();
            } catch (Exception e) {
                Log.e("UserRepository", "Error inserting initial users", e);
                callback.onError(e);
            }
        });
    }

    public void insertAll(List<User> users) {
        insertAll(users, CallbackUtils.emptyCallback());
    }

    public void insertUser(User user) {
        insertUser(user, CallbackUtils.emptyCallback());
    }


    public void updateUser(User user, RepositoryCallback<User> callback) {

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                userDao.updateUser(UserMapper.fromDomain(user));
                callback.onSuccess();
            } catch (Exception e) {
                Log.e("UserRepository", "Error updating user", e);
                callback.onError(e);
            }
        });
    }

    public void updateUser(User user, boolean hash, RepositoryCallback<User> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                userDao.updateUser(UserMapper.fromDomain(user, hash));
                callback.onSuccess();
            } catch (Exception e) {
                Log.e("UserRepository", "Error updating user without hashing password", e);
                callback.onError(e);
            }
        });
    }

    public void updateUser(User user) {
        updateUser(user, CallbackUtils.emptyCallback());
    }

    public void deleteUser(User user, RepositoryCallback<User> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                userDao.deleteUser(UserMapper.fromDomain(user));
                callback.onSuccess();
            } catch (Exception e) {
                Log.e("UserRepository", "Error deleting user", e);
                callback.onError(e);
            }
        });
    }

    public LiveData<List<User>> getAllUsers() {
        return Transformations.map(userDao.getAllUsers(), UserMapper::toDomainList);
    }

    public LiveData<List<User>> getAllNormalUsers() {
        return Transformations.map(userDao.getAllNormalUsers(), UserMapper::toDomainList);
    }


    public void getUserByEmail(String email, RepositoryCallback<User> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserEntity user = userDao.getUserByEmail(email);
                if (user != null) {
                    callback.onSuccess(UserMapper.toDomain(user));
                } else {
                    callback.onError(new Exception("User not found"));
                }
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public LiveData<User> getUserByEmailLive(String email) {
        return Transformations.map(userDao.getUserByEmailLive(email), UserMapper::toDomain);
    }

    public LiveData<GenderCount> getGenderDistribution() {
        return userDao.getGenderCounts();
    }
    public LiveData<Integer> getUserCount() {
        return userDao.getUserCount();
    }
}
