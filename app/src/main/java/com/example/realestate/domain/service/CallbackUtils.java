package com.example.realestate.domain.service;

public class CallbackUtils {

    public interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }


    public static <T> RepositoryCallback<T> createCallback(RepositoryCallback<T> callback) {
        return new RepositoryCallback<T>() {
            @Override
            public void onSuccess(T result) {
                callback.onSuccess(result);
            }
            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        };
    }

    public static <T> RepositoryCallback<T> emptyCallback() {
        return new RepositoryCallback<T>() {
            @Override
            public void onSuccess(T result) {
                // No operation
            }
            @Override
            public void onError(Exception e) {
                // No operation
            }
        };
    }
}
