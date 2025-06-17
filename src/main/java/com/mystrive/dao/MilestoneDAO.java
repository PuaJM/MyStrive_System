/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mystrive.dao;

import com.mystrive.model.Milestone;
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


public class MilestoneDAO {

    private static final Logger LOGGER = Logger.getLogger(MilestoneDAO.class.getName());

    public boolean addMilestone(Milestone milestone) {
        String SQL_INSERT = "INSERT INTO milestones (goal_id, milestone_description, due_date, status) VALUES (?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, milestone.getGoalId());
                preparedStatement.setString(2, milestone.getMilestoneDescription());
                preparedStatement.setDate(3, milestone.getDueDate());
                preparedStatement.setString(4, milestone.getStatus());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    success = true;
                    ResultSet rs = preparedStatement.getGeneratedKeys();
                    if (rs.next()) {
                        milestone.setMilestoneId(rs.getInt(1));
                    }
                    LOGGER.log(Level.INFO, "Milestone '{0}' added successfully for goal ID {1}.",
                            new Object[]{milestone.getMilestoneDescription(), milestone.getGoalId()});
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding milestone: " + e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after adding milestone.", e);
            }
        }
        return success;
    }

    public List<Milestone> getAllMilestonesByGoalId(int goalId) {
        List<Milestone> milestones = new ArrayList<>();
        String SQL_SELECT = "SELECT milestone_id, goal_id, milestone_description, due_date, status, created_at, updated_at FROM milestones WHERE goal_id = ? ORDER BY due_date ASC, milestone_id ASC";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_SELECT);
                preparedStatement.setInt(1, goalId);

                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    Milestone milestone = new Milestone();
                    milestone.setMilestoneId(resultSet.getInt("milestone_id"));
                    milestone.setGoalId(resultSet.getInt("goal_id"));
                    milestone.setMilestoneDescription(resultSet.getString("milestone_description"));
                    milestone.setDueDate(resultSet.getDate("due_date"));
                    milestone.setStatus(resultSet.getString("status"));
                    milestone.setCreatedAt(resultSet.getTimestamp("created_at"));
                    milestone.setUpdatedAt(resultSet.getTimestamp("updated_at"));
                    milestones.add(milestone);
                }
                LOGGER.log(Level.INFO, "{0} milestones retrieved for goal ID {1}.",
                        new Object[]{milestones.size(), goalId});
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving milestones for goal ID " + goalId + ": " + e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after retrieving milestones.", e);
            }
        }
        return milestones;
    }

    public Milestone getMilestoneById(int milestoneId) {
        String SQL_SELECT = "SELECT milestone_id, goal_id, milestone_description, due_date, status, created_at, updated_at FROM milestones WHERE milestone_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Milestone milestone = null;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_SELECT);
                preparedStatement.setInt(1, milestoneId);

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    milestone = new Milestone();
                    milestone.setMilestoneId(resultSet.getInt("milestone_id"));
                    milestone.setGoalId(resultSet.getInt("goal_id"));
                    milestone.setMilestoneDescription(resultSet.getString("milestone_description"));
                    milestone.setDueDate(resultSet.getDate("due_date"));
                    milestone.setStatus(resultSet.getString("status"));
                    milestone.setCreatedAt(resultSet.getTimestamp("created_at"));
                    milestone.setUpdatedAt(resultSet.getTimestamp("updated_at"));
                    LOGGER.log(Level.INFO, "Milestone with ID {0} retrieved successfully.", milestoneId);
                } else {
                    LOGGER.log(Level.WARNING, "Milestone with ID {0} not found.", milestoneId);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving milestone by ID " + milestoneId + ": " + e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after retrieving milestone by ID.", e);
            }
        }
        return milestone;
    }

    public boolean updateMilestone(Milestone milestone) {
        String SQL_UPDATE = "UPDATE milestones SET milestone_description = ?, due_date = ?, status = ? WHERE milestone_id = ? AND goal_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_UPDATE);
                preparedStatement.setString(1, milestone.getMilestoneDescription());
                preparedStatement.setDate(2, milestone.getDueDate());
                preparedStatement.setString(3, milestone.getStatus());
                preparedStatement.setInt(4, milestone.getMilestoneId());
                preparedStatement.setInt(5, milestone.getGoalId()); 

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    success = true;
                    LOGGER.log(Level.INFO, "Milestone ID {0} updated successfully.", milestone.getMilestoneId());
                } else {
                    LOGGER.log(Level.WARNING, "Milestone ID {0} not found or not belonging to goal {1} for update.",
                            new Object[]{milestone.getMilestoneId(), milestone.getGoalId()});
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating milestone ID " + milestone.getMilestoneId() + ": " + e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after updating milestone.", e);
            }
        }
        return success;
    }


    public boolean deleteMilestone(int milestoneId, int goalId) {
        String SQL_DELETE = "DELETE FROM milestones WHERE milestone_id = ? AND goal_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_DELETE);
                preparedStatement.setInt(1, milestoneId);
                preparedStatement.setInt(2, goalId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    success = true;
                    LOGGER.log(Level.INFO, "Milestone ID {0} deleted successfully for goal ID {1}.",
                            new Object[]{milestoneId, goalId});
                } else {
                    LOGGER.log(Level.WARNING, "Milestone ID {0} not found or not belonging to goal {1} for deletion.",
                            new Object[]{milestoneId, goalId});
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting milestone ID " + milestoneId + ": " + e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after deleting milestone.", e);
            }
        }
        return success;
    }
}

