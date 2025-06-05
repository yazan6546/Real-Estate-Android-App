package com.example.realestate.ui.login;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.service.Hashing;
import com.example.realestate.domain.service.SharedPrefManager;
import com.example.realestate.domain.service.UserSession;
import com.example.realestate.util.LogUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoginViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private UserRepository userRepository;

    @Mock
    private SharedPrefManager sharedPrefManager;

    private Observer<LoginViewModel.AuthState> authStateObserver;
    private Observer<String> errorMessageObserver;

    private LoginViewModel viewModel;

    private MockedStatic<Log> logMock;

    @Before
    public void setUp() {
        // Mock Android Log class
        logMock = LogUtils.mockLog();

        // Create the mocks explicitly with the correct type information
        authStateObserver = mock(Observer.class);
        errorMessageObserver = mock(Observer.class);

        viewModel = new LoginViewModel(userRepository, sharedPrefManager);
        viewModel.authState.observeForever(authStateObserver);
        viewModel.errorMessage.observeForever(errorMessageObserver);
    }

    @After
    public void tearDown() {
        // Close the mocked static instance to prevent memory leaks
        if (logMock != null) {
            logMock.close();
        }
    }

    @Test
    public void login_emptyCredentials_returnsError() {
        // Arrange
        String emptyEmail = "";
        String emptyPassword = "";

        // Act
        viewModel.login(emptyEmail, emptyPassword, false);

        // Assert
        verify(authStateObserver).onChanged(LoginViewModel.AuthState.ERROR);
        verify(errorMessageObserver).onChanged("Email and password cannot be empty");
    }

    @Test
    public void login_emptyEmail_returnsError() {
        // Arrange
        String emptyEmail = "";
        String password = "password123";

        // Act
        viewModel.login(emptyEmail, password, false);

        // Assert
        verify(authStateObserver).onChanged(LoginViewModel.AuthState.ERROR);
        verify(errorMessageObserver).onChanged("Email and password cannot be empty");
    }

    @Test
    public void login_emptyPassword_returnsError() {
        // Arrange
        String email = "test@example.com";
        String emptyPassword = "";

        // Act
        viewModel.login(email, emptyPassword, false);

        // Assert
        verify(authStateObserver).onChanged(LoginViewModel.AuthState.ERROR);
        verify(errorMessageObserver).onChanged("Email and password cannot be empty");
    }

    @Test
    public void login_validCredentialsButUserNotFound_returnsError() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        // Setup the callback mechanism to trigger onError
        doAnswer(invocation -> {
            RepositoryCallback<User> callback = invocation.getArgument(1);
            callback.onError(new Exception("User not found"));
            return null;
        }).when(userRepository).getUserByEmail(eq(email), any(RepositoryCallback.class));

        // Act
        viewModel.login(email, password, false);

        // Assert
        verify(authStateObserver).onChanged(LoginViewModel.AuthState.LOADING);
        verify(authStateObserver).onChanged(LoginViewModel.AuthState.ERROR);
        verify(errorMessageObserver).onChanged(contains("Login failed"));
    }

    @Test
    public void login_validCredentialsWithCorrectPassword_returnsSuccess() {
        // Arrange
        String email = "test@example.com";
        String password = "password123@A";
        String hashedPassword = "hashedPassword12!A";
        boolean isRememberMe = true;

        // Create a user with the hashed password, not the plain password
        User user = new User("John", "Doe", email, hashedPassword, "1234567890", "USA",
                "Los Angeles", User.Gender.MALE, false);

        // Mock the static Hashing class
        try (MockedStatic<Hashing> hashingMockedStatic = mockStatic(Hashing.class)) {
            // Make sure this mock matches exactly how it will be called in the ViewModel
            hashingMockedStatic.when(() -> Hashing.verifyPassword(anyString(), anyString())).thenReturn(true);

            // Setup the callback mechanism to trigger onSuccess with our mocked user
            doAnswer(invocation -> {
                RepositoryCallback<User> callback = invocation.getArgument(1);
                callback.onSuccess(user);
                return null;
            }).when(userRepository).getUserByEmail(eq(email), any(RepositoryCallback.class));

            // Act
            viewModel.login(email, password, isRememberMe);

            // Assert - verify correct state transitions in order
            InOrder inOrder = inOrder(authStateObserver);
            inOrder.verify(authStateObserver).onChanged(LoginViewModel.AuthState.LOADING);
            inOrder.verify(authStateObserver).onChanged(LoginViewModel.AuthState.SUCCESS);

            // Verify SharedPreferences is called
            verify(sharedPrefManager).writeObject(eq("user_session"), any(UserSession.class));
        }
    }

    @Test
    public void login_validCredentialsWithIncorrectPassword_returnsError() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongPassword";
        String hashedPassword = "hashedPassword@123";

        User user = new User("John", "Doe", email, hashedPassword, "1234567890", "USA",
                "Los Angeles", User.Gender.MALE, false);

        // Mock the static Hashing class
        try (MockedStatic<Hashing> hashingMockedStatic = mockStatic(Hashing.class)) {
            hashingMockedStatic.when(() -> Hashing.verifyPassword(anyString(), anyString())).thenReturn(false);

            // Setup the callback mechanism to trigger onSuccess with our mocked user
            doAnswer(invocation -> {
                RepositoryCallback<User> callback = invocation.getArgument(1);
                callback.onSuccess(user);
                return null;
            }).when(userRepository).getUserByEmail(eq(email), any(RepositoryCallback.class));

            // Act
            viewModel.login(email, password, false);

            // Assert
            InOrder inOrder = inOrder(authStateObserver);
            inOrder.verify(authStateObserver).onChanged(LoginViewModel.AuthState.LOADING);
            inOrder.verify(authStateObserver).onChanged(LoginViewModel.AuthState.ERROR);
            verify(errorMessageObserver).onChanged("Invalid email or password");
        }
    }
}
