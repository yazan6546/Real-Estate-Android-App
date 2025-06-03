package com.example.realestate.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.realestate.data.db.dao.UserDao;
import com.example.realestate.data.db.entity.UserEntity;
import com.example.realestate.data.db.result.GenderCount;
import com.example.realestate.domain.mapper.UserMapper;
import com.example.realestate.domain.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserRepository {

    UserDao userDao;

    public interface UserCallback {
        void onSuccess(User user);
        void onError(Exception e);
    }
    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public void insertUser(UserEntity user) {

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                userDao.insertUser(user);
            } catch (Exception e) {
                Log.e("UserRepository", "Error inserting user", e);
            }
        });
    }


    public void updateUser(UserEntity user) {
        userDao.updateUser(user);
    }

    public void deleteUser(UserEntity user) {
        userDao.deleteUser(user);
    }

    public void getUserByEmail(String email, UserCallback callback) {
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

    public UserEntity getUserByEmailAndPassword(String email, String password) {
        return userDao.getUserByEmailAndPassword(email, password);
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
