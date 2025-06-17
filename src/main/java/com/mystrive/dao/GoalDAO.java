/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mystrive.dao;


import com.mystrive.model.Goal;
import com.mystrive.model.Category; 
import com.mystrive.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GoalDAO {

    private static final Logger LOGGER = Logger.getLogger(GoalDAO.class.getName());

    public boolean addGoal(Goal goal) {
        String SQL_INSERT = "INSERT INTO goals (user_id, category_id, goal_description, target_date, status) VALUES (?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, goal.getUserId());
                if (goal.getCategoryId() != null) {
                    preparedStatement.setInt(2, goal.getCategoryId());
                } else {
                    preparedStatement.setNull(2, java.sql.Types.INTEGER);
                }
                preparedStatement.setString(3, goal.getGoalDescription());
                preparedStatement.setDate(4, goal.getTargetDate());
                preparedStatement.setString(5, goal.getStatus());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    success = true;
                    ResultSet rs = preparedStatement.getGeneratedKeys();
                    if (rs.next()) {
                        goal.setGoalId(rs.getInt(1));
                    }
                    LOGGER.log(Level.INFO, "Goal '{0}' added successfully for user ID {1}.",
                            new Object[]{goal.getGoalDescription(), goal.getUserId()});
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding goal: " + e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after adding goal.", e);
            }
        }
        return success;
    }

    public List<Goal> getAllGoalsByUserId(int userId) {
        List<Goal> goals = new ArrayList<>();
        // Join with categories table to get category name
        String SQL_SELECT = "SELECT g.goal_id, g.user_id, g.category_id, g.goal_description, g.target_date, g.status, g.created_at, g.updated_at, c.category_name " +
                            "FROM goals g LEFT JOIN categories c ON g.category_id = c.category_id " +
                            "WHERE g.user_id = ? ORDER BY g.target_date ASC, g.goal_id DESC"; // Order by target date and then ID
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_SELECT);
                preparedStatement.setInt(1, userId);

                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    Goal goal = new Goal();
                    goal.setGoalId(resultSet.getInt("goal_id"));
                    goal.setUserId(resultSet.getInt("user_id"));
                    // Check for null category_id from database
                    if (resultSet.getObject("category_id") != null) {
                        goal.setCategoryId(resultSet.getInt("category_id"));
                    } else {
                        goal.setCategoryId(null);
                    }
                    goal.setGoalDescription(resultSet.getString("goal_description"));
                    goal.setTargetDate(resultSet.getDate("target_date"));
                    goal.setStatus(resultSet.getString("status"));
                    goal.setCreatedAt(resultSet.getTimestamp("created_at"));
                    goal.setUpdatedAt(resultSet.getTimestamp("updated_at"));
                    goal.setCategoryName(resultSet.getString("category_name")); // Set category name from join
                    goals.add(goal);
                }
                LOGGER.log(Level.INFO, "{0} goals retrieved for user ID {1}.",
                        new Object[]{goals.size(), userId});
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving goals for user ID " + userId + ": " + e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after retrieving goals.", e);
            }
        }
        return goals;
    }

    public Goal getGoalById(int goalId) {
        String SQL_SELECT = "SELECT g.goal_id, g.user_id, g.category_id, g.goal_description, g.target_date, g.status, g.created_at, g.updated_at, c.category_name " +
                            "FROM goals g LEFT JOIN categories c ON g.category_id = c.category_id " +
                            "WHERE g.goal_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Goal goal = null;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_SELECT);
                preparedStatement.setInt(1, goalId);

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    goal = new Goal();
                    goal.setGoalId(resultSet.getInt("goal_id"));
                    goal.setUserId(resultSet.getInt("user_id"));
                    if (resultSet.getObject("category_id") != null) {
                        goal.setCategoryId(resultSet.getInt("category_id"));
                    } else {
                        goal.setCategoryId(null);
                    }
                    goal.setGoalDescription(resultSet.getString("goal_description"));
                    goal.setTargetDate(resultSet.getDate("target_date"));
                    goal.setStatus(resultSet.getString("status"));
                    goal.setCreatedAt(resultSet.getTimestamp("created_at"));
                    goal.setUpdatedAt(resultSet.getTimestamp("updated_at"));
                    goal.setCategoryName(resultSet.getString("category_name"));
                    LOGGER.log(Level.INFO, "Goal with ID {0} retrieved successfully.", goalId);
                } else {
                    LOGGER.log(Level.WARNING, "Goal with ID {0} not found.", goalId);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving goal by ID " + goalId + ": " + e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after retrieving goal by ID.", e);
            }
        }
        return goal;
    }

    public boolean updateGoal(Goal goal) {
        String SQL_UPDATE = "UPDATE goals SET category_id = ?, goal_description = ?, target_date = ?, status = ? WHERE goal_id = ? AND user_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_UPDATE);
                if (goal.getCategoryId() != null) {
                    preparedStatement.setInt(1, goal.getCategoryId());
                } else {
                    preparedStatement.setNull(1, java.sql.Types.INTEGER);
                }
                preparedStatement.setString(2, goal.getGoalDescription());
                preparedStatement.setDate(3, goal.getTargetDate());
                preparedStatement.setString(4, goal.getStatus());
                preparedStatement.setInt(5, goal.getGoalId());
                preparedStatement.setInt(6, goal.getUserId()); // Ensure user owns the goal

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    success = true;
                    LOGGER.log(Level.INFO, "Goal ID {0} updated successfully.", goal.getGoalId());
                } else {
                    LOGGER.log(Level.WARNING, "Goal ID {0} not found or not owned by user {1} for update.",
                            new Object[]{goal.getGoalId(), goal.getUserId()});
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating goal ID " + goal.getGoalId() + ": " + e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after updating goal.", e);
            }
        }
        return success;
    }

    public boolean deleteGoal(int goalId, int userId) {
        String SQL_DELETE = "DELETE FROM goals WHERE goal_id = ? AND user_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_DELETE);
                preparedStatement.setInt(1, goalId);
                preparedStatement.setInt(2, userId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    success = true;
                    LOGGER.log(Level.INFO, "Goal ID {0} deleted successfully for user ID {1}.",
                            new Object[]{goalId, userId});
                } else {
                    LOGGER.log(Level.WARNING, "Goal ID {0} not found or not owned by user {1} for deletion.",
                            new Object[]{goalId, userId});
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting goal ID " + goalId + ": " + e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after deleting goal.", e);
            }
        }
        return success;
    }
}

