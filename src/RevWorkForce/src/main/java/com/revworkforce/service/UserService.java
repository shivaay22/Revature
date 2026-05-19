package com.revworkforce.service;

import com.revworkforce.dao.UserDAO;
import com.revworkforce.model.User;
import com.revworkforce.utils.PasswordUtil;
import com.revworkforce.exceptions.ValidationException;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class UserService {
    private UserDAO userDAO;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9]{10,15}$"
    );
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );
    private static final Pattern EMPLOYEE_ID_PATTERN = Pattern.compile(
            "^[A-Za-z0-9]{4,20}$"
    );

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User login(String employeeId, String password) throws SQLException {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID cannot be null or empty");
        }

        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password cannot be null or empty");
        }

        if (employeeId.length() > 50) {
            throw new ValidationException("Employee ID exceeds maximum length of 50 characters");
        }

        User user = userDAO.authenticate(employeeId, password);

        if (user == null) {
            throw new ValidationException("Invalid employee ID or password");
        }

        if (!user.isActive()) {
            throw new ValidationException("Account is deactivated. Please contact administrator");
        }

        return user;
    }

    public User getUserById(int userId) throws SQLException {
        if (userId <= 0) {
            throw new ValidationException("User ID must be a positive integer");
        }

        User user = userDAO.getUserById(userId);

        if (user == null) {
            throw new ValidationException("User not found with ID: " + userId);
        }

        return user;
    }

    public User getUserByEmployeeId(String employeeId) throws SQLException {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID cannot be null or empty");
        }

        if (employeeId.length() > 50) {
            throw new ValidationException("Employee ID exceeds maximum length of 50 characters");
        }

        User user = userDAO.getUserByEmployeeId(employeeId);

        if (user == null) {
            throw new ValidationException("User not found with Employee ID: " + employeeId);
        }

        return user;
    }

    public List<User> getAllEmployees() throws SQLException {
        List<User> employees = userDAO.getAllEmployees();

        if (employees == null) {
            throw new ValidationException("Failed to retrieve employees list");
        }

        return employees;
    }

    public List<User> getEmployeesByManager(int managerId) throws SQLException {
        if (managerId <= 0) {
            throw new ValidationException("Manager ID must be a positive integer");
        }

        User manager = userDAO.getUserById(managerId);
        if (manager == null) {
            throw new ValidationException("Manager not found with ID: " + managerId);
        }


        List<User> employees = userDAO.getEmployeesByManager(managerId);

        if (employees == null) {
            throw new ValidationException("Failed to retrieve employees for manager ID: " + managerId);
        }

        return employees;
    }

    public boolean createUser(User user) throws SQLException {
        if (user == null) {
            throw new ValidationException("User object cannot be null");
        }

        if (user.getEmployeeId() == null || user.getEmployeeId().trim().isEmpty()) {
            throw new ValidationException("Employee ID cannot be null or empty");
        }

        if (!EMPLOYEE_ID_PATTERN.matcher(user.getEmployeeId()).matches()) {
            throw new ValidationException("Employee ID must be 4-20 alphanumeric characters");
        }

        User existingUser = userDAO.getUserByEmployeeId(user.getEmployeeId());
        if (existingUser != null) {
            throw new ValidationException("Employee ID already exists: " + user.getEmployeeId());
        }

        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new ValidationException("Full name cannot be null or empty");
        }

        if (user.getFullName().length() < 2 || user.getFullName().length() > 100) {
            throw new ValidationException("Full name must be between 2 and 100 characters");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email cannot be null or empty");
        }

        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new ValidationException("Invalid email format");
        }
        // Validate department
        if (user.getDepartment() == null || user.getDepartment().trim().isEmpty()) {
            throw new ValidationException("Department cannot be null or empty");
        }

        if (user.getDepartment().length() > 50) {
            throw new ValidationException("Department name exceeds maximum length of 50 characters");
        }

        if (user.getDesignation() == null || user.getDesignation().trim().isEmpty()) {
            throw new ValidationException("Designation cannot be null or empty");
        }

        if (user.getDesignation().length() > 50) {
            throw new ValidationException("Designation exceeds maximum length of 50 characters");
        }

        if (user.getPasswordHash() == null) {
            String randomPassword = PasswordUtil.generateRandomPassword();

            if (!PASSWORD_PATTERN.matcher(randomPassword).matches()) {
                throw new ValidationException("Generated password does not meet security requirements");
            }

            user.setPasswordHash(PasswordUtil.hashPassword(randomPassword));
            System.out.println("Generated password for " + user.getEmail() + ": " + randomPassword);
        } else {
            if (!PASSWORD_PATTERN.matcher(user.getPasswordHash()).matches()) {
                throw new ValidationException("Password must be at least 8 characters and contain uppercase, lowercase, number, and special character");
            }
        }

        boolean result = userDAO.createUser(user);

        if (!result) {
            throw new ValidationException("Failed to create user. Please try again");
        }

        return result;
    }

    public boolean updateUser(User user) throws SQLException {
        if (user == null) {
            throw new ValidationException("User object cannot be null");
        }

        if (user.getUserId() <= 0) {
            throw new ValidationException("Invalid user ID: " + user.getUserId());
        }


        User existingUser = userDAO.getUserById(user.getUserId());
        if (existingUser == null) {
            throw new ValidationException("User not found with ID: " + user.getUserId());
        }

        if (user.getFullName() != null) {
            if (user.getFullName().trim().isEmpty()) {
                throw new ValidationException("Full name cannot be empty");
            }
            if (user.getFullName().length() < 2 || user.getFullName().length() > 100) {
                throw new ValidationException("Full name must be between 2 and 100 characters");
            }
        }
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
                throw new ValidationException("Invalid email format");
            }
        }
        return userDAO.updateUser(user);
    }

    public boolean updateProfile(int userId, String phone, String address, String emergencyContact) throws SQLException {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID: " + userId);
        }

        User existingUser = userDAO.getUserById(userId);
        if (existingUser == null) {
            throw new ValidationException("User not found with ID: " + userId);
        }

        if (phone != null && !phone.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new ValidationException("Invalid phone number format. Must be 10-15 digits, optionally starting with +");
            }
        }

        if (address != null && address.length() > 500) {
            throw new ValidationException("Address exceeds maximum length of 500 characters");
        }

        if (emergencyContact != null && !emergencyContact.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(emergencyContact).matches()) {
                throw new ValidationException("Invalid emergency contact number format");
            }
        }

        return userDAO.updateProfile(userId, phone, address, emergencyContact);
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) throws SQLException {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID: " + userId);
        }

        if (oldPassword == null || oldPassword.isEmpty()) {
            throw new ValidationException("Old password cannot be empty");
        }

        if (newPassword == null || newPassword.isEmpty()) {
            throw new ValidationException("New password cannot be empty");
        }

        if (oldPassword.equals(newPassword)) {
            throw new ValidationException("New password must be different from old password");
        }

        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            throw new ValidationException("Password must be at least 8 characters and contain uppercase, lowercase, number, and special character");
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ValidationException("User not found with ID: " + userId);
        }

        if (!user.isActive()) {
            throw new ValidationException("Cannot change password for deactivated account");
        }

        if (!PasswordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
            throw new ValidationException("Old password is incorrect");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        return userDAO.changePassword(userId, newHash);
    }

    public boolean resetPassword(int userId, String newPassword) throws SQLException {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID: " + userId);
        }

        if (newPassword == null || newPassword.isEmpty()) {
            throw new ValidationException("New password cannot be empty");
        }

        // Validate new password strength
        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            throw new ValidationException("Password must be at least 8 characters and contain uppercase, lowercase, number, and special character");
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ValidationException("User not found with ID: " + userId);
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        return userDAO.changePassword(userId, newHash);
    }

    public boolean deactivateUser(int userId) throws SQLException {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID: " + userId);
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ValidationException("User not found with ID: " + userId);
        }

        if (!user.isActive()) {
            throw new ValidationException("User is already deactivated");
        }

        return userDAO.deactivateUser(userId);
    }

    public boolean activateUser(int userId) throws SQLException {
        if (userId <= 0) {
            throw new ValidationException("Invalid user ID: " + userId);
        }

        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new ValidationException("User not found with ID: " + userId);
        }

        if (user.isActive()) {
            throw new ValidationException("User is already active");
        }

        return userDAO.activateUser(userId);
    }

    public List<User> searchEmployees(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new ValidationException("Search keyword cannot be null or empty");
        }

        if (keyword.length() < 2) {
            throw new ValidationException("Search keyword must be at least 2 characters");
        }

        if (keyword.length() > 100) {
            throw new ValidationException("Search keyword exceeds maximum length of 100 characters");
        }

        List<User> results = userDAO.searchEmployees(keyword);

        if (results == null) {
            throw new ValidationException("Failed to perform search");
        }

        return results;
    }

    public User getReportingManager(int employeeId) throws SQLException {
        if (employeeId <= 0) {
            throw new ValidationException("Invalid employee ID: " + employeeId);
        }

        User employee = userDAO.getUserById(employeeId);

        if (employee == null) {
            throw new ValidationException("Employee not found with ID: " + employeeId);
        }

        if (!employee.isActive()) {
            throw new ValidationException("User with ID " + employeeId + " is not an employee");
        }

        if (employee.getManagerId() != null && employee.getManagerId() > 0) {
            User manager = userDAO.getUserById(employee.getManagerId());
            if (manager == null) {
                throw new ValidationException("Reporting manager not found for employee ID: " + employeeId);
            }
            return manager;
        }

        throw new ValidationException("No reporting manager assigned for employee ID: " + employeeId);
    }
}