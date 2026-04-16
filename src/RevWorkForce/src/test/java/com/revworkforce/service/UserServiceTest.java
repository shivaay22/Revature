package com.revworkforce.service;

import com.revworkforce.dao.UserDAO;
import com.revworkforce.model.User;
import com.revworkforce.utils.PasswordUtil;

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
        user.setEmail("test@mail.com");
        user.setPasswordHash("hashed");
    }


    // LOGIN


    @Test
    void testLoginSuccess() throws SQLException {
        when(userDAO.authenticate("EMP001", "pass")).thenReturn(user);

        User result = userService.login("EMP001", "pass");

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
                    .thenReturn("random123");

            mocked.when(() -> PasswordUtil.hashPassword("random123"))
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
        when(userDAO.updateUser(user)).thenReturn(true);

        assertTrue(userService.updateUser(user));
    }

    @Test
    void testUpdateProfile() throws SQLException {
        when(userDAO.updateProfile(1, "123", "addr", "999"))
                .thenReturn(true);

        assertTrue(userService.updateProfile(1, "123", "addr", "999"));
    }


    // CHANGE PASSWORD


    @Test
    void testChangePasswordSuccess() throws SQLException {
        try (MockedStatic<PasswordUtil> mocked = mockStatic(PasswordUtil.class)) {

            when(userDAO.getUserById(1)).thenReturn(user);

            mocked.when(() -> PasswordUtil.verifyPassword("old", "hashed"))
                    .thenReturn(true);

            mocked.when(() -> PasswordUtil.hashPassword("new"))
                    .thenReturn("newHash");

            when(userDAO.changePassword(1, "newHash")).thenReturn(true);

            assertTrue(userService.changePassword(1, "old", "new"));
        }
    }

    @Test
    void testChangePasswordFailWrongOldPassword() throws SQLException {
        try (MockedStatic<PasswordUtil> mocked = mockStatic(PasswordUtil.class)) {

            when(userDAO.getUserById(1)).thenReturn(user);

            mocked.when(() -> PasswordUtil.verifyPassword("old", "hashed"))
                    .thenReturn(false);

            assertFalse(userService.changePassword(1, "old", "new"));
        }
    }

    @Test
    void testChangePasswordUserNotFound() throws SQLException {
        when(userDAO.getUserById(1)).thenReturn(null);

        assertFalse(userService.changePassword(1, "old", "new"));
    }


    // RESET PASSWORD


    @Test
    void testResetPassword() throws SQLException {
        try (MockedStatic<PasswordUtil> mocked = mockStatic(PasswordUtil.class)) {

            mocked.when(() -> PasswordUtil.hashPassword("new"))
                    .thenReturn("hashed");

            when(userDAO.changePassword(1, "hashed")).thenReturn(true);

            assertTrue(userService.resetPassword(1, "new"));
        }
    }


    // ACTIVATE / DEACTIVATE


    @Test
    void testDeactivateUser() throws SQLException {
        when(userDAO.deactivateUser(1)).thenReturn(true);

        assertTrue(userService.deactivateUser(1));
    }

    @Test
    void testActivateUser() throws SQLException {
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

        assertNull(userService.getReportingManager(1));
    }

    @Test
    void testGetReportingManagerUserNotFound() throws SQLException {
        when(userDAO.getUserById(1)).thenReturn(null);

        assertNull(userService.getReportingManager(1));
    }
}