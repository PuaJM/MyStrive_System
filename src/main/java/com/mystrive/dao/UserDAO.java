/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mystrive.dao;

import com.mystrive.model.User;
import com.mystrive.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    public boolean registerUser(User user) {
        String SQL_INSERT = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_INSERT);
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword()); // Remember to hash passwords in a real app!
                preparedStatement.setString(3, user.getEmail());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    success = true;
                    LOGGER.log(Level.INFO, "User {0} registered successfully.", user.getUsername());
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering user: " + e.getMessage(), e);
            // Handle specific errors like duplicate username/email if needed
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after user registration.", e);
            }
        }
        return success;
    }


    public User loginUser(String username, String password) {
        String SQL_SELECT = "SELECT user_id, username, password, email, created_at FROM users WHERE username = ? AND password = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_SELECT);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password); // Again, use hashed passwords in a real app!

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    user = new User();
                    user.setUserId(resultSet.getInt("user_id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEmail(resultSet.getString("email"));
                    user.setCreatedAt(resultSet.getTimestamp("created_at"));
                    LOGGER.log(Level.INFO, "User {0} logged in successfully.", username);
                } else {
                    LOGGER.log(Level.WARNING, "Login failed for user: {0}. Invalid credentials.", username);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during user login: " + e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after user login.", e);
            }
        }
        return user;
    }


    public User getUserById(int userId) {
        String SQL_SELECT = "SELECT user_id, username, password, email, created_at FROM users WHERE user_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_SELECT);
                preparedStatement.setInt(1, userId);

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    user = new User();
                    user.setUserId(resultSet.getInt("user_id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEmail(resultSet.getString("email"));
                    user.setCreatedAt(resultSet.getTimestamp("created_at"));
                    LOGGER.log(Level.INFO, "User with ID {0} retrieved successfully.", userId);
                } else {
                    LOGGER.log(Level.WARNING, "User with ID {0} not found.", userId);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving user by ID: " + e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after retrieving user by ID.", e);
            }
        }
        return user;
    }
}
