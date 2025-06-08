package com.example.realestate.domain.service;

import com.example.realestate.data.repository.RepositoryCallback;

public class CallbackUtils {

    public static <T> RepositoryCallback<T> createCallback(RepositoryCallback<T> callback) {
        return new RepositoryCallback<>() {
            @Override
            public void onSuccess(T result) {
                callback.onSuccess(result);
            }
            @Override
            public void onError(Throwable t) {
                callback.onError(t);
            }
        };
    }

    public static <T> RepositoryCallback<T> emptyCallback() {
        return new RepositoryCallback<>() {
        };
    }
}
