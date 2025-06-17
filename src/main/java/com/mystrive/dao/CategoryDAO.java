/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mystrive.dao;

import com.mystrive.model.Category;
import com.mystrive.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryDAO {

    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());

    public boolean addCategory(Category category) {
        String SQL_INSERT = "INSERT INTO categories (user_id, category_name) VALUES (?, ?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, category.getUserId());
                preparedStatement.setString(2, category.getCategoryName());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    success = true;
                    // Retrieve auto-generated categoryId
                    ResultSet rs = preparedStatement.getGeneratedKeys();
                    if (rs.next()) {
                        category.setCategoryId(rs.getInt(1));
                    }
                    LOGGER.log(Level.INFO, "Category '{0}' added successfully for user ID {1}.",
                            new Object[]{category.getCategoryName(), category.getUserId()});
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding category: " + e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after adding category.", e);
            }
        }
        return success;
    }

    public List<Category> getAllCategoriesByUserId(int userId) {
        List<Category> categories = new ArrayList<>();
        String SQL_SELECT = "SELECT category_id, user_id, category_name, created_at FROM categories WHERE user_id = ? ORDER BY category_name ASC";
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
                    Category category = new Category();
                    category.setCategoryId(resultSet.getInt("category_id"));
                    category.setUserId(resultSet.getInt("user_id"));
                    category.setCategoryName(resultSet.getString("category_name"));
                    category.setCreatedAt(resultSet.getTimestamp("created_at"));
                    categories.add(category);
                }
                LOGGER.log(Level.INFO, "{0} categories retrieved for user ID {1}.",
                        new Object[]{categories.size(), userId});
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving categories for user ID " + userId + ": " + e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after retrieving categories.", e);
            }
        }
        return categories;
    }

    public Category getCategoryById(int categoryId) {
        String SQL_SELECT = "SELECT category_id, user_id, category_name, created_at FROM categories WHERE category_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Category category = null;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_SELECT);
                preparedStatement.setInt(1, categoryId);

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    category = new Category();
                    category.setCategoryId(resultSet.getInt("category_id"));
                    category.setUserId(resultSet.getInt("user_id"));
                    category.setCategoryName(resultSet.getString("category_name"));
                    category.setCreatedAt(resultSet.getTimestamp("created_at"));
                    LOGGER.log(Level.INFO, "Category with ID {0} retrieved successfully.", categoryId);
                } else {
                    LOGGER.log(Level.WARNING, "Category with ID {0} not found.", categoryId);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving category by ID " + categoryId + ": " + e.getMessage(), e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after retrieving category by ID.", e);
            }
        }
        return category;
    }

    public boolean updateCategory(Category category) {
        String SQL_UPDATE = "UPDATE categories SET category_name = ? WHERE category_id = ? AND user_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_UPDATE);
                preparedStatement.setString(1, category.getCategoryName());
                preparedStatement.setInt(2, category.getCategoryId());
                preparedStatement.setInt(3, category.getUserId()); // Ensure user owns the category

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    success = true;
                    LOGGER.log(Level.INFO, "Category ID {0} updated successfully.", category.getCategoryId());
                } else {
                    LOGGER.log(Level.WARNING, "Category ID {0} not found or not owned by user {1} for update.",
                            new Object[]{category.getCategoryId(), category.getUserId()});
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating category ID " + category.getCategoryId() + ": " + e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after updating category.", e);
            }
        }
        return success;
    }

    public boolean deleteCategory(int categoryId, int userId) {
        String SQL_DELETE = "DELETE FROM categories WHERE category_id = ? AND user_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            connection = DBConnection.getConnection();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(SQL_DELETE);
                preparedStatement.setInt(1, categoryId);
                preparedStatement.setInt(2, userId);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    success = true;
                    LOGGER.log(Level.INFO, "Category ID {0} deleted successfully for user ID {1}.",
                            new Object[]{categoryId, userId});
                } else {
                    LOGGER.log(Level.WARNING, "Category ID {0} not found or not owned by user {1} for deletion.",
                            new Object[]{categoryId, userId});
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting category ID " + categoryId + ": " + e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                DBConnection.closeConnection(connection);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources after deleting category.", e);
            }
        }
        return success;
    }
}
