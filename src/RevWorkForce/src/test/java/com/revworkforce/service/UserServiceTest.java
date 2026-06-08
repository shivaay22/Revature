package com.revworkforce.service;

import com.revworkforce.dao.UserDAO;
import com.revworkforce.model.User;
import com.revworkforce.utils.PasswordUtil;
import com.revworkforce.exceptions.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUserId(1);
        user.setEmployeeId("EMP001");
        user.setFullName("John Doe");
        user.setEmail("test@mail.com");
        user.setPasswordHash("SecurePass123@");
        user.setDepartment("Engineering");
        user.setDesignation("Software Engineer");
        user.setActive(true);
    }

    // LOGIN

    @Test
    void testLoginSuccess() throws SQLException {
        when(userDAO.authenticate("EMP001", "SecurePass123@")).thenReturn(user);

        User result = userService.login("EMP001", "SecurePass123@");

        assertNotNull(result);
        assertEquals("EMP001", result.getEmployeeId());
    }

    // GET USER

    @Test
    void testGetUserById() throws SQLException {
        when(userDAO.getUserById(1)).thenReturn(user);

        assertEquals(user, userService.getUserById(1));
    }

    @Test
    void testGetUserByEmployeeId() throws SQLException {
        when(userDAO.getUserByEmployeeId("EMP001")).thenReturn(user);

        assertEquals(user, userService.getUserByEmployeeId("EMP001"));
    }

    // GET EMPLOYEES

    @Test
    void testGetAllEmployees() throws SQLException {
        when(userDAO.getAllEmployees()).thenReturn(List.of(user));

        assertEquals(1, userService.getAllEmployees().size());
    }

    @Test
    void testGetEmployeesByManager() throws SQLException {
        User manager = new User();
        manager.setUserId(10);
        manager.setActive(true);

        when(userDAO.getUserById(10)).thenReturn(manager);
        when(userDAO.getEmployeesByManager(10)).thenReturn(List.of(user));

        assertEquals(1, userService.getEmployeesByManager(10).size());
    }

    // CREATE USER

    @Test
    void testCreateUserWithExistingPassword() throws SQLException {
        when(userDAO.createUser(user)).thenReturn(true);

        boolean result = userService.createUser(user);

        assertTrue(result);
        verify(userDAO).createUser(user);
    }

    @Test
    void testCreateUserGeneratePassword() throws SQLException {
        user.setPasswordHash(null);

        try (MockedStatic<PasswordUtil> mocked = mockStatic(PasswordUtil.class)) {

            mocked.when(PasswordUtil::generateRandomPassword)
                    .thenReturn("RandomPass123@");

            mocked.when(() -> PasswordUtil.hashPassword("RandomPass123@"))
                    .thenReturn("hashed123");

            when(userDAO.createUser(user)).thenReturn(true);

            boolean result = userService.createUser(user);

            assertTrue(result);
            assertEquals("hashed123", user.getPasswordHash());
        }
    }

    // UPDATE USER

    @Test
    void testUpdateUser() throws SQLException {
        when(userDAO.getUserById(1)).thenReturn(user);
        when(userDAO.updateUser(user)).thenReturn(true);

        assertTrue(userService.updateUser(user));
    }

    @Test
    void testUpdateProfile() throws SQLException {
        when(userDAO.getUserById(1)).thenReturn(user);
        when(userDAO.updateProfile(1, "1234567890", "addr", "0987654321"))
                .thenReturn(true);

        assertTrue(userService.updateProfile(1, "1234567890", "addr", "0987654321"));
    }

    // CHANGE PASSWORD

    @Test
    void testChangePasswordSuccess() throws SQLException {
        try (MockedStatic<PasswordUtil> mocked = mockStatic(PasswordUtil.class)) {

            when(userDAO.getUserById(1)).thenReturn(user);

            mocked.when(() -> PasswordUtil.verifyPassword("SecureOldPass123@", "SecurePass123@"))
                    .thenReturn(true);

            mocked.when(() -> PasswordUtil.hashPassword("SecureNewPass123@"))
                    .thenReturn("newHash");

            when(userDAO.changePassword(1, "newHash")).thenReturn(true);

            assertTrue(userService.changePassword(1, "SecureOldPass123@", "SecureNewPass123@"));
        }
    }

    @Test
    void testChangePasswordFailWrongOldPassword() throws SQLException {
        try (MockedStatic<PasswordUtil> mocked = mockStatic(PasswordUtil.class)) {

            when(userDAO.getUserById(1)).thenReturn(user);

            mocked.when(() -> PasswordUtil.verifyPassword("SecureWrongOldPass123@", "SecurePass123@"))
                    .thenReturn(false);

            assertThrows(ValidationException.class, () -> {
                userService.changePassword(1, "SecureWrongOldPass123@", "SecureNewPass123@");
            });
        }
    }

    @Test
    void testChangePasswordUserNotFound() throws SQLException {
        when(userDAO.getUserById(1)).thenReturn(null);

        assertThrows(ValidationException.class, () -> {
            userService.changePassword(1, "SecureOldPass123@", "SecureNewPass123@");
        });
    }

    // RESET PASSWORD

    @Test
    void testResetPassword() throws SQLException {
        try (MockedStatic<PasswordUtil> mocked = mockStatic(PasswordUtil.class)) {

            when(userDAO.getUserById(1)).thenReturn(user);
            mocked.when(() -> PasswordUtil.hashPassword("SecureNewPass123@"))
                    .thenReturn("hashed");

            when(userDAO.changePassword(1, "hashed")).thenReturn(true);

            assertTrue(userService.resetPassword(1, "SecureNewPass123@"));
        }
    }

    // ACTIVATE / DEACTIVATE

    @Test
    void testDeactivateUser() throws SQLException {
        when(userDAO.getUserById(1)).thenReturn(user);
        when(userDAO.deactivateUser(1)).thenReturn(true);

        assertTrue(userService.deactivateUser(1));
    }

    @Test
    void testActivateUser() throws SQLException {
        user.setActive(false);
        when(userDAO.getUserById(1)).thenReturn(user);
        when(userDAO.activateUser(1)).thenReturn(true);

        assertTrue(userService.activateUser(1));
    }

    // SEARCH

    @Test
    void testSearchEmployees() throws SQLException {
        when(userDAO.searchEmployees("shivam")).thenReturn(List.of(user));

        assertEquals(1, userService.searchEmployees("shivam").size());
    }

    // REPORTING MANAGER

    @Test
    void testGetReportingManagerSuccess() throws SQLException {
        User manager = new User();
        manager.setUserId(2);
        manager.setActive(true);

        user.setManagerId(2);

        when(userDAO.getUserById(1)).thenReturn(user);
        when(userDAO.getUserById(2)).thenReturn(manager);

        User result = userService.getReportingManager(1);

        assertNotNull(result);
        assertEquals(2, result.getUserId());
    }

    @Test
    void testGetReportingManagerNoManager() throws SQLException {
        user.setManagerId(null);

        when(userDAO.getUserById(1)).thenReturn(user);

        assertThrows(ValidationException.class, () -> {
            userService.getReportingManager(1);
        });
    }

    @Test
    void testGetReportingManagerUserNotFound() throws SQLException {
        when(userDAO.getUserById(1)).thenReturn(null);

        assertThrows(ValidationException.class, () -> {
            userService.getReportingManager(1);
        });
    }
}