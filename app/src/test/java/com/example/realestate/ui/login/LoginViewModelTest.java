package com.example.realestate.ui.login;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.realestate.data.repository.RepositoryCallback;
import com.example.realestate.data.repository.UserRepository;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.service.Hashing;
import com.example.realestate.domain.service.SharedPrefManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

    // Change from generic @Mock to specific mock creation
    private Observer authStateObserver;

    private Observer errorMessageObserver;

    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        // Create the mocks explicitly with the correct type information
        authStateObserver = mock(Observer.class);
        errorMessageObserver = mock(Observer.class);

        viewModel = new LoginViewModel(userRepository, sharedPrefManager);
        viewModel.authState.observeForever(authStateObserver);
        viewModel.errorMessage.observeForever(errorMessageObserver);
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
        String password = "password123";
        String hashedPassword = "hashedPassword";
        boolean isRememberMe = true;

        User user = new User("John", "Doe", email, hashedPassword, "1234567890", "USA",
                "Los Angeles", false);
        // Mock the static Hashing class
        try (MockedStatic<Hashing> hashingMockedStatic = mockStatic(Hashing.class)) {
            hashingMockedStatic.when(() -> Hashing.verifyPassword(password, hashedPassword)).thenReturn(true);

            // Setup the callback mechanism to trigger onSuccess with our mocked user
            doAnswer(invocation -> {
                RepositoryCallback<User> callback = invocation.getArgument(1);
                callback.onSuccess(user);
                return null;
            }).when(userRepository).getUserByEmail(eq(email), any(RepositoryCallback.class));

            // Act
            viewModel.login(email, password, isRememberMe);

            // Assert
            verify(authStateObserver).onChanged(LoginViewModel.AuthState.LOADING);
            verify(authStateObserver).onChanged(LoginViewModel.AuthState.SUCCESS);
            verify(sharedPrefManager).writeObject(eq("user_session"), any());
        }
    }

    @Test
    public void login_validCredentialsWithIncorrectPassword_returnsError() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongPassword";
        String hashedPassword = "hashedPassword";

        User user = new User("John", "Doe", email, hashedPassword, "1234567890", "USA",
                "Los Angeles", false);
        // Mock the static Hashing class
        try (MockedStatic<Hashing> hashingMockedStatic = mockStatic(Hashing.class)) {
            hashingMockedStatic.when(() -> Hashing.verifyPassword(password, hashedPassword)).thenReturn(false);

            // Setup the callback mechanism to trigger onSuccess with our mocked user
            doAnswer(invocation -> {
                RepositoryCallback<User> callback = invocation.getArgument(1);
                callback.onSuccess(user);
                return null;
            }).when(userRepository).getUserByEmail(eq(email), any(RepositoryCallback.class));

            // Act
            viewModel.login(email, password, false);

            // Assert
            verify(authStateObserver).onChanged(LoginViewModel.AuthState.LOADING);
            verify(authStateObserver).onChanged(LoginViewModel.AuthState.ERROR);
            verify(errorMessageObserver).onChanged("Invalid email or password");
        }
    }

    @Test
    public void login_withRememberMeUnchecked_clearsSharedPreferences() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String hashedPassword = "hashedPassword";
        boolean isRememberMe = false;

        User user = new User("John", "Doe", email, hashedPassword, "1234567890", "USA",
                "Los Angeles", false);

        // Mock the static Hashing class
        try (MockedStatic<Hashing> hashingMockedStatic = mockStatic(Hashing.class)) {
            hashingMockedStatic.when(() -> Hashing.verifyPassword(password, hashedPassword)).thenReturn(true);

            // Setup the callback mechanism to trigger onSuccess with our mocked user
            doAnswer(invocation -> {
                RepositoryCallback<User> callback = invocation.getArgument(1);
                callback.onSuccess(user);
                return null;
            }).when(userRepository).getUserByEmail(eq(email), any(RepositoryCallback.class));

            // Act
            viewModel.login(email, password, isRememberMe);

            // Assert
            verify(authStateObserver).onChanged(LoginViewModel.AuthState.LOADING);
            verify(authStateObserver).onChanged(LoginViewModel.AuthState.SUCCESS);
            verify(sharedPrefManager).clear();
        }
    }
}
