package com.example.realestate.data.repository;

public interface RepositoryCallback<T> {
    default void onSuccess(T result) {}
    default void onSuccess() {}
    void onError(Throwable throwable);
}
