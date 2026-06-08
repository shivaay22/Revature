package com.revworkforce.dao;

import com.revworkforce.model.User;
import com.revworkforce.utils.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDAOTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private Statement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    @Mock
    private ResultSet mockGeneratedKeys;

    private UserDAO userDAO;
    private User testUser;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        testUser = createTestUser();
    }

    private User createTestUser() {
        User user = new User();
        user.setUserId(1);
        user.setEmployeeId("EMP001");
        user.setFullName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPasswordHash("password123");
        user.setPhone("1234567890");
        user.setAddress("123 Main St");
        user.setEmergencyContact("Jane Doe: 0987654321");
        user.setDateOfBirth(new Date());
        user.setJoiningDate(new Date());
        user.setDepartment("Engineering");
        user.setDesignation("Software Engineer");
        user.setRole(User.UserRole.EMPLOYEE);
        user.setManagerId(2);
        user.setSalary(75000.0);
        user.setActive(true);
        return user;
    }

    @Test
    void authenticate_Success() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            mockResultSetBehavior(mockResultSet);

            User result = userDAO.authenticate("EMP001", "password123");

            assertNotNull(result);
            assertEquals("EMP001", result.getEmployeeId());
            assertEquals("John Doe", result.getFullName());
            verify(mockPreparedStatement).setString(1, "EMP001");
        }
    }

    @Test
    void authenticate_UserNotFound() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            User result = userDAO.authenticate("EMP001", "wrongpassword");

            assertNull(result);
        }
    }

    @Test
    void authenticate_WrongPassword() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getString("password_hash")).thenReturn("differentpassword");

            User result = userDAO.authenticate("EMP001", "wrongpassword");

            assertNull(result);
        }
    }

    @Test
    void getUserById_Success() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            mockResultSetBehavior(mockResultSet);

            User result = userDAO.getUserById(1);

            assertNotNull(result);
            assertEquals(1, result.getUserId());
            assertEquals("EMP001", result.getEmployeeId());
            verify(mockPreparedStatement).setInt(1, 1);
        }
    }

    @Test
    void getUserById_NotFound() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            User result = userDAO.getUserById(999);

            assertNull(result);
        }
    }

    @Test
    void getUserByEmployeeId_Success() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            mockResultSetBehavior(mockResultSet);

            User result = userDAO.getUserByEmployeeId("EMP001");

            assertNotNull(result);
            assertEquals("EMP001", result.getEmployeeId());
            verify(mockPreparedStatement).setString(1, "EMP001");
        }
    }

    @Test
    void getEmployeesByManager_Success() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, false);
            mockResultSetBehavior(mockResultSet);

            List<User> result = userDAO.getEmployeesByManager(2);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(mockPreparedStatement).setInt(1, 2);
        }
    }

    @Test
    void createUser_Success() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                    .thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);
            when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
            when(mockGeneratedKeys.next()).thenReturn(true);
            when(mockGeneratedKeys.getInt(1)).thenReturn(1);

            boolean result = userDAO.createUser(testUser);

            assertTrue(result);
            assertEquals(1, testUser.getUserId());
            verify(mockPreparedStatement).setString(1, testUser.getEmployeeId());
            verify(mockPreparedStatement).setString(2, testUser.getFullName());
            verify(mockPreparedStatement).setBoolean(15, testUser.isActive());
        }
    }

    @Test
    void createUser_Failure() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                    .thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            boolean result = userDAO.createUser(testUser);

            assertFalse(result);
        }
    }

    @Test
    void updateUser_Success() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            boolean result = userDAO.updateUser(testUser);

            assertTrue(result);
            verify(mockPreparedStatement).setString(1, testUser.getFullName());
            verify(mockPreparedStatement).setInt(10, testUser.getUserId());
        }
    }

    @Test
    void updateUser_Failure() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0);

            boolean result = userDAO.updateUser(testUser);

            assertFalse(result);
        }
    }

    @Test
    void updateProfile_Success() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            boolean result = userDAO.updateProfile(1, "9876543210", "456 New St", "Emergency Contact");

            assertTrue(result);
            verify(mockPreparedStatement).setString(1, "9876543210");
            verify(mockPreparedStatement).setInt(4, 1);
        }
    }

    @Test
    void changePassword_Success() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            boolean result = userDAO.changePassword(1, "newhashedpassword");

            assertTrue(result);
            verify(mockPreparedStatement).setString(1, "newhashedpassword");
            verify(mockPreparedStatement).setInt(2, 1);
        }
    }

    @Test
    void deactivateUser_Success() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            boolean result = userDAO.deactivateUser(1);

            assertTrue(result);
            verify(mockPreparedStatement).setInt(1, 1);
        }
    }

    @Test
    void activateUser_Success() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            boolean result = userDAO.activateUser(1);

            assertTrue(result);
            verify(mockPreparedStatement).setInt(1, 1);
        }
    }

    @Test
    void searchEmployees_Success() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true, false);
            mockResultSetBehavior(mockResultSet);

            List<User> result = userDAO.searchEmployees("John");

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(mockPreparedStatement).setString(1, "%John%");
            verify(mockPreparedStatement).setString(2, "%John%");
            verify(mockPreparedStatement).setString(3, "%John%");
            verify(mockPreparedStatement).setString(4, "%John%");
        }
    }

    @Test
    void authenticate_DatabaseError_ThrowsSQLException() throws SQLException {
        try (MockedStatic<DatabaseConnection> mockedStatic = mockStatic(DatabaseConnection.class)) {
            mockedStatic.when(DatabaseConnection::getConnection).thenThrow(new SQLException("Database error"));

            assertThrows(SQLException.class, () -> {
                userDAO.authenticate("EMP001", "password123");
            });
        }
    }

    private void mockResultSetBehavior(ResultSet rs) throws SQLException {
        when(rs.getInt("user_id")).thenReturn(1);
        when(rs.getString("employee_id")).thenReturn("EMP001");
        when(rs.getString("full_name")).thenReturn("John Doe");
        when(rs.getString("email")).thenReturn("john.doe@example.com");
        when(rs.getString("password_hash")).thenReturn("ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f");
        when(rs.getString("phone")).thenReturn("1234567890");
        when(rs.getString("address")).thenReturn("123 Main St");
        when(rs.getString("emergency_contact")).thenReturn("Jane Doe: 0987654321");
        when(rs.getDate("date_of_birth")).thenReturn(new java.sql.Date(new Date().getTime()));
        when(rs.getDate("joining_date")).thenReturn(new java.sql.Date(new Date().getTime()));
        when(rs.getString("department")).thenReturn("Engineering");
        when(rs.getString("designation")).thenReturn("Software Engineer");
        when(rs.getString("role")).thenReturn("EMPLOYEE");
        when(rs.getInt("manager_id")).thenReturn(2);
        when(rs.getDouble("salary")).thenReturn(75000.0);
        when(rs.getBoolean("is_active")).thenReturn(true);
        when(rs.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(rs.getTimestamp("updated_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
    }

    private void mockResultSetBehaviorForSecondUser(ResultSet rs) throws SQLException {
        when(rs.getInt("user_id")).thenReturn(2);
        when(rs.getString("employee_id")).thenReturn("EMP002");
        when(rs.getString("full_name")).thenReturn("Jane Smith");
        when(rs.getString("email")).thenReturn("jane.smith@example.com");
        when(rs.getString("password_hash")).thenReturn("password456");
        when(rs.getString("phone")).thenReturn("0987654321");
        when(rs.getString("address")).thenReturn("456 Oak Ave");
        when(rs.getString("emergency_contact")).thenReturn("John Smith: 1234567890");
        when(rs.getDate("date_of_birth")).thenReturn(new java.sql.Date(new Date().getTime()));
        when(rs.getDate("joining_date")).thenReturn(new java.sql.Date(new Date().getTime()));
        when(rs.getString("department")).thenReturn("Marketing");
        when(rs.getString("designation")).thenReturn("Marketing Specialist");
        when(rs.getString("role")).thenReturn("EMPLOYEE");
        when(rs.getInt("manager_id")).thenReturn(2);
        when(rs.getDouble("salary")).thenReturn(65000.0);
        when(rs.getBoolean("is_active")).thenReturn(true);
        when(rs.getTimestamp("created_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(rs.getTimestamp("updated_at")).thenReturn(new Timestamp(System.currentTimeMillis()));
    }
}