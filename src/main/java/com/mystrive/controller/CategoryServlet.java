package com.mystrive.controller;

import com.mystrive.model.Category;
import com.mystrive.model.User;
import com.mystrive.dao.CategoryDAO;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;


public class CategoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CategoryServlet.class.getName());
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        categoryDAO = new CategoryDAO();
    }

    /**
     * Handles HTTP GET requests.
     * This method is responsible for:
     * 1. Displaying the list of all categories for the current user (manageCategories.jsp).
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // Do not create a new session if one doesn't exist
        if (session == null || session.getAttribute("currentUser") == null) {
            // User not logged in, redirect to login page
            LOGGER.log(Level.WARNING, "Unauthorized access to CategoryServlet (GET). Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        int userId = currentUser.getUserId();
        String action = request.getParameter("action");
        RequestDispatcher dispatcher;

        try {
            if (action == null || action.isEmpty() || "list".equals(action)) {
                // Action: List all categories for the current user (Manage Categories Page)
                LOGGER.log(Level.INFO, "Retrieving all categories for user ID: {0}", userId);
                List<Category> categories = categoryDAO.getAllCategoriesByUserId(userId);
                request.setAttribute("categories", categories);

                // Check for success/error messages from session (from doPost redirect)
                String successMessage = (String) session.getAttribute("successMessage");
                if (successMessage != null) {
                    request.setAttribute("successMessage", successMessage);
                    session.removeAttribute("successMessage");
                }
                String errorMessage = (String) session.getAttribute("errorMessage");
                if (errorMessage != null) {
                    request.setAttribute("errorMessage", errorMessage);
                    session.removeAttribute("errorMessage");
                }

                dispatcher = request.getRequestDispatcher("/manageCategories.jsp");
                dispatcher.forward(request, response);
            } else if ("edit".equals(action)) {
                // Action: Display form to edit an existing category
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                LOGGER.log(Level.INFO, "Displaying edit category form for category ID: {0}, user ID: {1}", new Object[]{categoryId, userId});
                Category existingCategory = categoryDAO.getCategoryById(categoryId);

                if (existingCategory != null && existingCategory.getUserId() == userId) {
                    request.setAttribute("category", existingCategory);
                    request.setAttribute("formTitle", "Edit Category");
                    dispatcher = request.getRequestDispatcher("/manageCategories.jsp"); // Forward to the same page, but pre-populate form
                    dispatcher.forward(request, response);
                } else {
                    LOGGER.log(Level.WARNING, "Category ID {0} not found or not owned by user {1} for editing.", new Object[]{categoryId, userId});
                    session.setAttribute("errorMessage", "Category not found or unauthorized access.");
                    response.sendRedirect(request.getContextPath() + "/categories");
                }
            } else if ("delete".equals(action)) {
                // Action: Delete a category
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                LOGGER.log(Level.INFO, "Attempting to delete category ID: {0} for user ID: {1}", new Object[]{categoryId, userId});

                boolean deleted = categoryDAO.deleteCategory(categoryId, userId);
                if (deleted) {
                    LOGGER.log(Level.INFO, "Category ID {0} deleted successfully.", categoryId);
                    session.setAttribute("successMessage", "Category successfully deleted!");
                } else {
                    LOGGER.log(Level.WARNING, "Failed to delete category ID {0} or unauthorized.", categoryId);
                    session.setAttribute("errorMessage", "Failed to delete category or unauthorized. Ensure no goals are linked to it if it exists.");
                }
                response.sendRedirect(request.getContextPath() + "/categories"); // Redirect back to list page (PRG pattern)
            } else {
                // Unknown action, redirect to list page with error
                LOGGER.log(Level.WARNING, "Unknown action '{0}' requested in CategoryServlet (GET).", action);
                session.setAttribute("errorMessage", "Invalid action for category operation.");
                response.sendRedirect(request.getContextPath() + "/categories");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid categoryId parameter in CategoryServlet (GET): " + e.getMessage(), e);
            session.setAttribute("errorMessage", "Invalid category ID format.");
            response.sendRedirect(request.getContextPath() + "/categories");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred in CategoryServlet (GET): " + e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            dispatcher = request.getRequestDispatcher("/error.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Handles HTTP POST requests.
     * This method is responsible for:
     * 1. Adding a new category.
     * 2. Updating an existing category.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            LOGGER.log(Level.WARNING, "Unauthorized access to CategoryServlet (POST). Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        int userId = currentUser.getUserId();

        String action = request.getParameter("action");
        String categoryName = request.getParameter("categoryName");
        String categoryIdStr = request.getParameter("categoryId"); // Present for "update" action

        String errorMessage = null;

        // Server-side validation
        if (categoryName == null || categoryName.trim().isEmpty()) {
            errorMessage = "Category name is required.";
        }

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            // Re-populate data for the form if validation fails
            Category currentCategory = new Category();
            currentCategory.setCategoryName(categoryName);
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                try {
                    currentCategory.setCategoryId(Integer.parseInt(categoryIdStr));
                } catch (NumberFormatException e) { /* ignore */ }
            }
            request.setAttribute("category", currentCategory); // So the form can display what was entered
            request.setAttribute("formTitle", (categoryIdStr != null && !categoryIdStr.isEmpty() && !categoryIdStr.equals("0")) ? "Edit Category" : "Add New Category");

            List<Category> categories = categoryDAO.getAllCategoriesByUserId(userId); // Re-fetch all categories for list display
            request.setAttribute("categories", categories);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/manageCategories.jsp");
            dispatcher.forward(request, response);
            LOGGER.log(Level.WARNING, "Category POST failed due to validation errors for user {0}: {1}", new Object[]{userId, errorMessage});
            return;
        }

        Category category = new Category();
        category.setUserId(userId);
        category.setCategoryName(categoryName);

        boolean operationSuccess = false;
        try {
            if ("add".equals(action)) {
                // Add new category
                LOGGER.log(Level.INFO, "Adding new category '{0}' for user ID: {1}", new Object[]{categoryName, userId});
                operationSuccess = categoryDAO.addCategory(category);
                if (operationSuccess) {
                    session.setAttribute("successMessage", "Category successfully added!");
                } else {
                    session.setAttribute("errorMessage", "Failed to add new category. Category name might already exist.");
                }
            } else if ("update".equals(action)) {
                // Update existing category
                int categoryId = Integer.parseInt(categoryIdStr);
                category.setCategoryId(categoryId); // Set the category ID for update
                LOGGER.log(Level.INFO, "Updating category ID: {0} to '{1}' for user ID: {2}", new Object[]{categoryId, categoryName, userId});
                operationSuccess = categoryDAO.updateCategory(category);
                if (operationSuccess) {
                    session.setAttribute("successMessage", "Category successfully updated!");
                } else {
                    session.setAttribute("errorMessage", "Failed to update category. Category not found or unauthorized.");
                }
            } else {
                LOGGER.log(Level.WARNING, "Unknown action '{0}' requested in CategoryServlet (POST).", action);
                session.setAttribute("errorMessage", "Invalid action for category operation.");
            }

            // Redirect back to the categories list using Post-Redirect-Get pattern
            response.sendRedirect(request.getContextPath() + "/categories");

        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid categoryId parameter in CategoryServlet (POST) for update: " + e.getMessage(), e);
            session.setAttribute("errorMessage", "Invalid category ID format for update.");
            response.sendRedirect(request.getContextPath() + "/categories"); // Redirect to handle error
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred during category operation (POST): " + e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            dispatcher.forward(request, response);
        }
    }
}
