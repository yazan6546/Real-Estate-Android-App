package com.example.realestate.data.repository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.realestate.data.db.dao.UserDao;
import com.example.realestate.data.db.entity.UserEntity;
import com.example.realestate.domain.mapper.UserMapper;
import com.example.realestate.domain.model.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.Executor;

@RunWith(MockitoJUnitRunner.class)
public class UserRepositoryTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserRepository.UserCallback callback;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Exception> exceptionCaptor;

    private UserRepository userRepository;

    @Before
    public void setUp() {
        userRepository = new UserRepository(userDao);
    }

    @Test
    public void insertUser_callsDaoInsert() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");

        // Act
        userRepository.insertUser(userEntity);

        // Assert
        verify(userDao).insertUser(userEntity);
    }

    @Test
    public void updateUser_callsDaoUpdate() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");

        // Act
        userRepository.updateUser(userEntity);

        // Assert
        verify(userDao).updateUser(userEntity);
    }

    @Test
    public void deleteUser_callsDaoDelete() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");

        // Act
        userRepository.deleteUser(userEntity);

        // Assert
        verify(userDao).deleteUser(userEntity);
    }

    @Test
    public void getUserByEmail_userExists_callsCallbackSuccess() {
        // Arrange
        String email = "test@example.com";
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setFirstName("John");
        userEntity.setLastName("Doe");

        // Need to use doAnswer to handle the async execution
        doAnswer(invocation -> {
            // Simulate async execution by directly returning the user
            return userEntity;
        }).when(userDao).getUserByEmail(email);

        // Act
        userRepository.getUserByEmail(email, callback);

        // Ideally would use a CountDownLatch or similar to wait for async operation
        // But for simplicity in unit tests, we'll use a small delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }

        // Assert
        // Note: This test may be flaky due to async nature
        // In real implementation, consider using a test executor or CountDownLatch
        verify(callback, timeout(1000)).onSuccess(any(User.class));
    }

    @Test
    public void getUserByEmail_userDoesNotExist_callsCallbackError() {
        // Arrange
        String email = "nonexistent@example.com";

        // Return null to simulate user not found
        doReturn(null).when(userDao).getUserByEmail(email);

        // Act
        userRepository.getUserByEmail(email, callback);

        // Give time for async operation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }

        // Assert
        verify(callback, timeout(1000)).onError(any(Exception.class));
    }

    @Test
    public void getUserByEmail_daoThrowsException_callsCallbackError() {
        // Arrange
        String email = "test@example.com";
        RuntimeException expectedException = new RuntimeException("Database error");

        doThrow(expectedException).when(userDao).getUserByEmail(email);

        // Act
        userRepository.getUserByEmail(email, callback);

        // Give time for async operation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            fail("Test interrupted");
        }

        // Assert
        verify(callback, timeout(1000)).onError(any(Exception.class));
    }

    @Test
    public void getUserByEmailAndPassword_callsDaoMethod() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        UserEntity expectedUser = new UserEntity();

        when(userDao.getUserByEmailAndPassword(email, password)).thenReturn(expectedUser);

        // Act
        UserEntity result = userRepository.getUserByEmailAndPassword(email, password);

        // Assert
        assertEquals(expectedUser, result);
        verify(userDao).getUserByEmailAndPassword(email, password);
    }
}
