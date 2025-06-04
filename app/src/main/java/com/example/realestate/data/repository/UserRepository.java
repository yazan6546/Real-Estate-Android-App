package com.example.realestate.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.realestate.data.db.dao.UserDao;
import com.example.realestate.data.db.entity.UserEntity;
import com.example.realestate.data.db.result.GenderCount;
import com.example.realestate.domain.mapper.UserMapper;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.service.CallbackUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public void insertUser(User user, RepositoryCallback<User> callback) {

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                userDao.insertUser(UserMapper.toEntity(user));
                callback.onSuccess();
            } catch (Exception e) {
                Log.e("UserRepository", "Error inserting user", e);
                callback.onError(e);
            }
        });
    }

    public void insertUser(User user) {
        insertUser(user, CallbackUtils.emptyCallback());
    }


    public void updateUser(User user, RepositoryCallback<User> callback) {

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                userDao.updateUser(UserMapper.toEntity(user));
                callback.onSuccess(user);
            } catch (Exception e) {
                Log.e("UserRepository", "Error updating user", e);
                callback.onError(e);
            }
        });
    }

    public void deleteUser(User user) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                userDao.deleteUser(UserMapper.toEntity(user));
            } catch (Exception e) {
                Log.e("UserRepository", "Error deleting user", e);
            }
        });
    }

    public void getUserByEmail(String email, RepositoryCallback<User> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                UserEntity user = userDao.getUserByEmail(email);
                if (user != null) {
                    callback.onSuccess(UserMapper.fromEntity(user));
                } else {
                    callback.onError(new Exception("User not found"));
                }
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public Map<String, Integer> getGenderDistribution() {
        List<GenderCount> counts = userDao.getGenderCounts();
        Map<String, Integer> genderMap = new HashMap<>();

        for (GenderCount count : counts) {
            genderMap.put(count.gender, count.count);
        }
        return genderMap;
    }
}
