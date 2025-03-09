package com.project0.service;

import com.projecto_0.dao.UserDao;
import com.projecto_0.models.UserModel;
import com.projecto_0.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserDao userDaoMock;
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Create a mock instance of UserDao
        userDaoMock = Mockito.mock(UserDao.class);
        // Inject the mock into the UserService
        userService = new UserService(userDaoMock);
    }

    @Test
    void registerUser_ShouldReturnNewUser_WhenEmailNotExists() {
        // Arrange
        String name = "Carlo";
        String lastName = "Yañez";
        String email = "cy@mail.com";
        String password = "1234";
        int roleId = 2;

        when(userDaoMock.emailExists(email)).thenReturn(false);
        when(userDaoMock.registerUser(any(UserModel.class))).thenReturn(true);

        // Act
        UserModel result = userService.registerUser(name, lastName, email, password, roleId);

        // Assert
        assertNotNull(result, "Should return a new user when email does not exist.");
        assertEquals(name, result.getName(), "Name should match.");
        assertEquals(lastName, result.getLastName(), "Last name should match.");
        assertEquals(email, result.getEmail(), "Email should match.");
        assertEquals(roleId, result.getRoleId(), "Role ID should match.");
        verify(userDaoMock).emailExists(email);
        verify(userDaoMock).registerUser(any(UserModel.class));
    }

    @Test
    void registerUser_ShouldReturnNull_WhenEmailAlreadyExists() {
        // Arrange
        String email = "an@mail.com";
        when(userDaoMock.emailExists(email)).thenReturn(true);

        // Act
        UserModel result = userService.registerUser("Anahi", "Rosas", email, "a@mail.com", 1);

        // Assert
        assertNull(result, "Should return null when email already exists.");
        verify(userDaoMock).emailExists(email);
        verify(userDaoMock, never()).registerUser(any(UserModel.class));
    }

    @Test
    void loginUser_ShouldReturnTrue_WhenCredentialsMatch() {
        // Arrange
        String email = "a@mail.com";
        String password = "1234";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        UserModel user = new UserModel();
        user.setEmail(email);
        user.setPassword(hashedPassword);

        when(userDaoMock.getUserByEmail(email)).thenReturn(user);

        // Act
        boolean result = userService.loginUser(email, password);

        // Assert
        assertTrue(result, "Should return true when credentials match.");
        verify(userDaoMock).getUserByEmail(email);
    }

    @Test
    void loginUser_ShouldReturnFalse_WhenUserNotFound() {
        // Arrange
        String email = "nonexistent@mail.com";
        when(userDaoMock.getUserByEmail(email)).thenReturn(null);

        // Act
        boolean result = userService.loginUser(email, "password123");

        // Assert
        assertFalse(result, "Should return false when user is not found.");
        verify(userDaoMock).getUserByEmail(email);
    }

    @Test
    void loginUser_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
        // Arrange
        String email = "a@mail.com";
        String password = "wrongPassword";
        String hashedPassword = BCrypt.hashpw("1234", BCrypt.gensalt());

        UserModel user = new UserModel();
        user.setEmail(email);
        user.setPassword(hashedPassword);

        when(userDaoMock.getUserByEmail(email)).thenReturn(user);

        // Act
        boolean result = userService.loginUser(email, password);

        // Assert
        assertFalse(result, "Should return false when password does not match.");
        verify(userDaoMock).getUserByEmail(email);
    }

    @Test
    void getUserInfo_ShouldReturnEmptyList_WhenUserIsNotAdmin() {
        // Arrange
        int userId = 2; // Non-admin user
        UserModel user = new UserModel();
        user.setId(userId);
        user.setRoleId(2); // role_id = 2 (normal user)

        when(userDaoMock.getUserById(userId)).thenReturn(user);

        // Act
        List<Map<String, Object>> result = userService.getUserInfo(userId);

        // Assert
        assertTrue(result.isEmpty(), "Should return an empty list when user is not an admin.");
        verify(userDaoMock).getUserById(userId);
    }
}