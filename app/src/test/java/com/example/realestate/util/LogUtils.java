package com.example.realestate.util;

import android.util.Log;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Utility class to handle Log mocking in tests.
 */
public class LogUtils {

    /**
     * Creates a mocked static instance of the Android Log class for testing.
     *
     * @return A MockedStatic instance that should be closed after use
     */
    public static MockedStatic<Log> mockLog() {
        MockedStatic<Log> mockedLog = Mockito.mockStatic(Log.class);

        mockedLog.when(() -> Log.v(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(0);
        mockedLog.when(() -> Log.v(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class)))
                .thenReturn(0);

        mockedLog.when(() -> Log.d(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(0);
        mockedLog.when(() -> Log.d(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class)))
                .thenReturn(0);

        mockedLog.when(() -> Log.i(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(0);
        mockedLog.when(() -> Log.i(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class)))
                .thenReturn(0);

        mockedLog.when(() -> Log.w(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(0);
        mockedLog.when(() -> Log.w(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class)))
                .thenReturn(0);

        mockedLog.when(() -> Log.e(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(0);
        mockedLog.when(() -> Log.e(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class)))
                .thenReturn(0);

        return mockedLog;
    }
}
