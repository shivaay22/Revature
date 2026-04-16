-- Create Database
CREATE DATABASE IF NOT EXISTS revworkforce;
USE revworkforce;

-- Users Table
CREATE TABLE users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       employee_id VARCHAR(20) UNIQUE NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       phone VARCHAR(15),
                       address TEXT,
                       emergency_contact VARCHAR(15),
                       date_of_birth DATE,
                       joining_date DATE,
                       department VARCHAR(50),
                       designation VARCHAR(50),
                       role ENUM('EMPLOYEE', 'MANAGER', 'ADMIN') DEFAULT 'EMPLOYEE',
                       manager_id INT,
                       salary DECIMAL(10,2),
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       FOREIGN KEY (manager_id) REFERENCES users(user_id)
);

-- Leave Balances Table
CREATE TABLE leave_balances (
                                balance_id INT PRIMARY KEY AUTO_INCREMENT,
                                user_id INT NOT NULL,
                                leave_type ENUM('CASUAL', 'SICK', 'PAID', 'PRIVILEGE') NOT NULL,
                                total_days INT DEFAULT 0,
                                used_days INT DEFAULT 0,
                                year INT NOT NULL,
                                FOREIGN KEY (user_id) REFERENCES users(user_id),
                                UNIQUE KEY unique_user_leave_year (user_id, leave_type, year)
);

-- Leave Requests Table
CREATE TABLE leave_requests (
                                request_id INT PRIMARY KEY AUTO_INCREMENT,
                                user_id INT NOT NULL,
                                leave_type ENUM('CASUAL', 'SICK', 'PAID', 'PRIVILEGE') NOT NULL,
                                start_date DATE NOT NULL,
                                end_date DATE NOT NULL,
                                reason TEXT,
                                status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
                                manager_comments TEXT,
                                applied_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                reviewed_date TIMESTAMP NULL,
                                FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Performance Reviews Table
CREATE TABLE performance_reviews (
                                     review_id INT PRIMARY KEY AUTO_INCREMENT,
                                     user_id INT NOT NULL,
                                     review_year INT NOT NULL,
                                     key_deliverables TEXT,
                                     major_accomplishments TEXT,
                                     areas_improvement TEXT,
                                     self_rating DECIMAL(2,1),
                                     manager_rating DECIMAL(2,1),
                                     manager_feedback TEXT,
                                     status ENUM('DRAFT', 'SUBMITTED', 'REVIEWED') DEFAULT 'DRAFT',
                                     submitted_date DATE,
                                     reviewed_date DATE,
                                     FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Goals Table
CREATE TABLE goals (
                       goal_id INT PRIMARY KEY AUTO_INCREMENT,
                       user_id INT NOT NULL,
                       goal_description TEXT NOT NULL,
                       deadline DATE,
                       priority ENUM('HIGH', 'MEDIUM', 'LOW') DEFAULT 'MEDIUM',
                       success_metrics TEXT,
                       progress_percentage INT DEFAULT 0,
                       status ENUM('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'NOT_STARTED',
                       manager_feedback TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Notifications Table
CREATE TABLE notifications (
                               notification_id INT PRIMARY KEY AUTO_INCREMENT,
                               user_id INT NOT NULL,
                               title VARCHAR(200) NOT NULL,
                               message TEXT NOT NULL,
                               type ENUM('LEAVE', 'PERFORMANCE', 'BIRTHDAY', 'ANNIVERSARY', 'ANNOUNCEMENT', 'SYSTEM') NOT NULL,
                               is_read BOOLEAN DEFAULT FALSE,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Holidays Table
CREATE TABLE holidays (
                          holiday_id INT PRIMARY KEY AUTO_INCREMENT,
                          holiday_name VARCHAR(100) NOT NULL,
                          holiday_date DATE NOT NULL,
                          year INT NOT NULL,
                          description TEXT
);

-- Announcements Table
CREATE TABLE announcements (
                               announcement_id INT PRIMARY KEY AUTO_INCREMENT,
                               title VARCHAR(200) NOT NULL,
                               content TEXT NOT NULL,
                               created_by INT,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               expiry_date DATE,
                               FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- System Logs Table
CREATE TABLE system_logs (
                             log_id INT PRIMARY KEY AUTO_INCREMENT,
                             user_id INT,
                             action VARCHAR(100),
                             details TEXT,
                             ip_address VARCHAR(45),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Insert Sample Data
INSERT INTO users (employee_id, full_name, email, password_hash, phone, role, is_active) VALUES
    ('ADMIN001', 'System Admin', 'admin@revworkforce.com', '$2a$10$YourHashedPasswordHere', '9999999999', 'ADMIN', TRUE);

INSERT INTO users (employee_id, full_name, email, password_hash, phone, department, designation, role, is_active) VALUES
                                                                                                                      ('EMP001', 'John Doe', 'john.doe@revworkforce.com', '$2a$10$YourHashedPasswordHere', '9876543210', 'Engineering', 'Software Engineer', 'EMPLOYEE', TRUE),
                                                                                                                      ('EMP002', 'Jane Smith', 'jane.smith@revworkforce.com', '$2a$10$YourHashedPasswordHere', '9876543211', 'Engineering', 'Senior Engineer', 'EMPLOYEE', TRUE),
                                                                                                                      ('MGR001', 'Mike Johnson', 'mike.johnson@revworkforce.com', '$2a$10$YourHashedPasswordHere', '9876543212', 'Engineering', 'Engineering Manager', 'MANAGER', TRUE);

-- Update manager assignments
UPDATE users SET manager_id = (SELECT user_id FROM users WHERE employee_id = 'MGR001') WHERE employee_id IN ('EMP001', 'EMP002');

-- Insert leave balances for current year
INSERT INTO leave_balances (user_id, leave_type, total_days, used_days, year)
SELECT user_id, 'CASUAL', 12, 0, YEAR(CURDATE()) FROM users WHERE role != 'ADMIN';

INSERT INTO leave_balances (user_id, leave_type, total_days, used_days, year)
SELECT user_id, 'SICK', 10, 0, YEAR(CURDATE()) FROM users WHERE role != 'ADMIN';

INSERT INTO leave_balances (user_id, leave_type, total_days, used_days, year)
SELECT user_id, 'PAID', 20, 0, YEAR(CURDATE()) FROM users WHERE role != 'ADMIN';

-- Insert holidays
INSERT INTO holidays (holiday_name, holiday_date, year) VALUES
    ('New Year\'s Day', '2024-01-01', 2024),
('Republic Day', '2024-01-26', 2024),
('Independence Day', '2024-08-15', 2024),
('Gandhi Jayanti', '2024-10-02', 2024),
('Christmas Day', '2024-12-25', 2024);