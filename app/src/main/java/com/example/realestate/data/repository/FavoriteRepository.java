package com.example.realestate.data.repository;

import com.example.realestate.data.db.dao.FavoriteDao;

public class FavoriteRepository {
    private final FavoriteDao favoriteDao;


    public interface FavoriteCallback {
        void onSuccess();
        void onFailure();
    }
    public FavoriteRepository(FavoriteDao favoriteDao) {
        this.favoriteDao = favoriteDao;
    }
}
