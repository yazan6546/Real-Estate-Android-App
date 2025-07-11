package com.example.realestate.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.realestate.data.db.dao.FavoriteDao;
import com.example.realestate.data.db.entity.FavoriteEntity;
import com.example.realestate.domain.mapper.FavoriteMapper;
import com.example.realestate.domain.model.Favorite;
import com.example.realestate.domain.service.CallbackUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class FavoriteRepository {
    private final FavoriteDao favoriteDao;

    public FavoriteRepository(FavoriteDao favoriteDao) {
        this.favoriteDao = favoriteDao;
    }


    public void insertAll(List<Favorite> favorites, RepositoryCallback<Void> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<FavoriteEntity> entities = FavoriteMapper.fromDomainList(favorites);
                favoriteDao.insertAll(entities);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e);
                Log.e("FavoriteRepository", "Error inserting favorites", e);
            }
        });
    }

    public void insertAll(List<Favorite> favorites) {
        insertAll(favorites, CallbackUtils.emptyCallback());
    }

    public LiveData<List<Favorite>> getFavoritesByEmail(String email) {
        return Transformations.map(favoriteDao.getFavoriteByEmail(email),
                FavoriteMapper::toDomainList);
    }

    public LiveData<List<Favorite>> getFavoritesByAddedDate(Date addedDate) {
        return Transformations.map(favoriteDao.getFavoriteByAddedDate(addedDate),
                FavoriteMapper::toDomainList);
    }

    public void addFavorite(Favorite favorite, RepositoryCallback<Favorite> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                FavoriteEntity entity = FavoriteMapper.fromDomain(favorite);
                favoriteDao.insertFavorite(entity);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void updateFavorite(Favorite favorite, RepositoryCallback<Favorite> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                FavoriteEntity entity = FavoriteMapper.fromDomain(favorite);
                favoriteDao.updateFavorite(entity);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    public void deleteFavorite(Favorite favorite, RepositoryCallback<Favorite> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                FavoriteEntity entity = FavoriteMapper.fromDomain(favorite);
                favoriteDao.deleteFavorite(entity);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    // Check if a property is in user's favorites
    public void isFavorite(String email, int propertyId, RepositoryCallback<Boolean> callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                boolean exists = favoriteDao.isFavorite(email, propertyId);
                callback.onSuccess(exists);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }
}
