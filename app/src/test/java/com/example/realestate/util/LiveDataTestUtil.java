package com.example.realestate.util;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for testing LiveData objects.
 * This class provides a method to get the value from a LiveData object synchronously.
 */
public class LiveDataTestUtil {

    /**
     * Gets the value from a LiveData object synchronously.
     *
     * @param liveData The LiveData object to observe.
     * @param <T>      The type of data held by the LiveData.
     * @return The value of the LiveData.
     * @throws InterruptedException If the awaitValue operation is interrupted.
     */
    public static <T> T getValue(final LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T t) {
                data[0] = t;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        latch.await(2, TimeUnit.SECONDS);

        return (T) data[0];
    }
}
