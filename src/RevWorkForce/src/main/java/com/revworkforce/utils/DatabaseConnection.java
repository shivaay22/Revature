package com.revworkforce.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/revworkforce";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "2022"; // Change this to your MySQL password

    private static Connection connection = null;
    private static Connection connectionProxy = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                
                connectionProxy = (Connection) java.lang.reflect.Proxy.newProxyInstance(
                    DatabaseConnection.class.getClassLoader(),
                    new Class<?>[]{Connection.class},
                    (proxy, method, args1) -> {
                        if ("close".equals(method.getName())) {
                            // Suppress close call so try-with-resources doesn't terminate it
                            return null;
                        }
                        try {
                            return method.invoke(connection, args1);
                        } catch (java.lang.reflect.InvocationTargetException e) {
                            throw e.getCause();
                        }
                    }
                );
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connectionProxy;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                connectionProxy = null;
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}