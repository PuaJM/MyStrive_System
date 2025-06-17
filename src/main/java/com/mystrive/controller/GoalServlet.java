package com.mystrive.controller;

import com.mystrive.model.Goal;
import com.mystrive.model.User;
import com.mystrive.model.Category;
import com.mystrive.model.Milestone; 
import com.mystrive.dao.GoalDAO;
import com.mystrive.dao.CategoryDAO;
import com.mystrive.dao.MilestoneDAO; 

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;

public class GoalServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GoalServlet.class.getName());
    private GoalDAO goalDAO;
    private CategoryDAO categoryDAO;
    private MilestoneDAO milestoneDAO; // Initialize for milestone retrieval

    @Override
    public void init() throws ServletException {
        super.init();
        goalDAO = new GoalDAO();
        categoryDAO = new CategoryDAO();
        milestoneDAO = new MilestoneDAO();
    }

    /**
     * Handles HTTP GET requests.
     * This method is responsible for:
     * 1. Displaying the list of all goals (dashboard.jsp).
     * 2. Displaying the Add/Edit Goal form (addEditGoal.jsp).
     * 3. Displaying Goal Details with Milestones (goalDetails.jsp).
     * 4. Handling goal deletion requests.
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
            LOGGER.log(Level.WARNING, "Unauthorized access to GoalServlet (GET). Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        int userId = currentUser.getUserId();
        String action = request.getParameter("action");
        RequestDispatcher dispatcher;

        try {
            if (action == null || action.isEmpty() || "list".equals(action)) {
                // Action: List all goals for the current user (Dashboard)
                LOGGER.log(Level.INFO, "Retrieving all goals for user ID: {0}", userId);
                List<Goal> goals = goalDAO.getAllGoalsByUserId(userId);
                request.setAttribute("goals", goals);
                dispatcher = request.getRequestDispatcher("/dashboard.jsp");
                dispatcher.forward(request, response);
            } else if ("add".equals(action)) {
                // Action: Display form to add a new goal
                LOGGER.log(Level.INFO, "Displaying add goal form for user ID: {0}", userId);
                List<Category> categories = categoryDAO.getAllCategoriesByUserId(userId);
                request.setAttribute("categories", categories);
                request.setAttribute("goal", new Goal()); // Provide an empty goal object for the form
                request.setAttribute("formTitle", "Add New Goal");
                dispatcher = request.getRequestDispatcher("/addEditGoal.jsp");
                dispatcher.forward(request, response);
            } else if ("edit".equals(action)) {
                // Action: Display form to edit an existing goal
                int goalId = Integer.parseInt(request.getParameter("goalId"));
                LOGGER.log(Level.INFO, "Displaying edit goal form for goal ID: {0}, user ID: {1}", new Object[]{goalId, userId});
                Goal existingGoal = goalDAO.getGoalById(goalId);

                if (existingGoal != null && existingGoal.getUserId() == userId) {
                    List<Category> categories = categoryDAO.getAllCategoriesByUserId(userId);
                    request.setAttribute("categories", categories);
                    request.setAttribute("goal", existingGoal);
                    request.setAttribute("formTitle", "Edit Goal");
                    dispatcher = request.getRequestDispatcher("/addEditGoal.jsp");
                    dispatcher.forward(request, response);
                } else {
                    LOGGER.log(Level.WARNING, "Goal ID {0} not found or not owned by user {1} for editing.", new Object[]{goalId, userId});
                    response.sendRedirect(request.getContextPath() + "/goals?error=Goal not found or unauthorized access.");
                }
            } else if ("delete".equals(action)) {
                // Action: Delete a goal
                int goalId = Integer.parseInt(request.getParameter("goalId"));
                LOGGER.log(Level.INFO, "Attempting to delete goal ID: {0} for user ID: {1}", new Object[]{goalId, userId});

                boolean deleted = goalDAO.deleteGoal(goalId, userId);
                if (deleted) {
                    LOGGER.log(Level.INFO, "Goal ID {0} deleted successfully.", goalId);
                    session.setAttribute("successMessage", "Goal successfully deleted!");
                } else {
                    LOGGER.log(Level.WARNING, "Failed to delete goal ID {0} or unauthorized.", goalId);
                    session.setAttribute("errorMessage", "Failed to delete goal or unauthorized.");
                }
                response.sendRedirect(request.getContextPath() + "/goals"); // Redirect back to list page (PRG pattern)
            } else if ("view".equals(action)) {
                // Action: View goal details and associated milestones
                int goalId = Integer.parseInt(request.getParameter("goalId"));
                LOGGER.log(Level.INFO, "Viewing details for goal ID: {0}, user ID: {1}", new Object[]{goalId, userId});
                Goal goal = goalDAO.getGoalById(goalId);

                if (goal != null && goal.getUserId() == userId) {
                    List<Milestone> milestones = milestoneDAO.getAllMilestonesByGoalId(goalId);
                    request.setAttribute("goal", goal);
                    request.setAttribute("milestones", milestones);
                    dispatcher = request.getRequestDispatcher("/goalDetails.jsp");
                    dispatcher.forward(request, response);
                } else {
                    LOGGER.log(Level.WARNING, "Goal ID {0} not found or not owned by user {1} for viewing details.", new Object[]{goalId, userId});
                    response.sendRedirect(request.getContextPath() + "/goals?error=Goal details not found or unauthorized access.");
                }
            } else {
                // Unknown action, redirect to dashboard with error
                LOGGER.log(Level.WARNING, "Unknown action '{0}' requested in GoalServlet (GET).", action);
                response.sendRedirect(request.getContextPath() + "/goals?error=Invalid action.");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid goalId parameter in GoalServlet (GET): " + e.getMessage(), e);
            response.sendRedirect(request.getContextPath() + "/goals?error=Invalid goal ID format.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred in GoalServlet (GET): " + e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            dispatcher = request.getRequestDispatcher("/error.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Handles HTTP POST requests.
     * This method is responsible for:
     * 1. Adding a new goal.
     * 2. Updating an existing goal.
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
            LOGGER.log(Level.WARNING, "Unauthorized access to GoalServlet (POST). Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        int userId = currentUser.getUserId();

        String action = request.getParameter("action");
        String goalDescription = request.getParameter("goalDescription");
        String categoryIdStr = request.getParameter("categoryId");
        String targetDateStr = request.getParameter("targetDate");
        String status = request.getParameter("status");
        String goalIdStr = request.getParameter("goalId"); // Present for "edit" action

        String errorMessage = null;
        Integer categoryId = null;

        // Server-side validation
        if (goalDescription == null || goalDescription.trim().isEmpty() ||
            targetDateStr == null || targetDateStr.trim().isEmpty() ||
            status == null || status.trim().isEmpty()) {
            errorMessage = "Goal description, target date, and status are required.";
        } else if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryIdStr);
                // Optional: Validate if categoryId belongs to the current user
                Category selectedCategory = categoryDAO.getCategoryById(categoryId);
                if (selectedCategory == null || selectedCategory.getUserId() != userId) {
                    errorMessage = "Invalid category selected.";
                    categoryId = null; // Invalidate categoryId if it doesn't belong to the user
                }
            } catch (NumberFormatException e) {
                errorMessage = "Invalid category ID format.";
            }
        }
        // If categoryIdStr is empty, it means "No Category" is selected, so categoryId remains null.

        Date targetDate = null;
        if (targetDateStr != null && !targetDateStr.isEmpty()) {
            try {
                targetDate = Date.valueOf(targetDateStr);
            } catch (IllegalArgumentException e) {
                errorMessage = "Invalid target date format. Please use YYYY-MM-DD.";
            }
        }

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            // Re-populate data for the form if validation fails
            Goal currentGoal = new Goal();
            currentGoal.setGoalDescription(goalDescription);
            currentGoal.setCategoryId(categoryId);
            currentGoal.setTargetDate(targetDate);
            currentGoal.setStatus(status);
            if (goalIdStr != null && !goalIdStr.isEmpty()) {
                try {
                    currentGoal.setGoalId(Integer.parseInt(goalIdStr));
                } catch (NumberFormatException e) { /* ignore */ }
            }
            request.setAttribute("goal", currentGoal);
            List<Category> categories = categoryDAO.getAllCategoriesByUserId(userId);
            request.setAttribute("categories", categories);
            request.setAttribute("formTitle", (goalIdStr != null && !goalIdStr.isEmpty()) ? "Edit Goal" : "Add New Goal");

            RequestDispatcher dispatcher = request.getRequestDispatcher("/addEditGoal.jsp");
            dispatcher.forward(request, response);
            LOGGER.log(Level.WARNING, "Goal POST failed due to validation errors for user {0}: {1}", new Object[]{userId, errorMessage});
            return;
        }

        Goal goal = new Goal();
        goal.setUserId(userId);
        goal.setGoalDescription(goalDescription);
        goal.setCategoryId(categoryId); // Can be null if no category is selected
        goal.setTargetDate(targetDate);
        goal.setStatus(status);

        boolean operationSuccess = false;
        try {
            if ("add".equals(action)) {
                // Add new goal
                LOGGER.log(Level.INFO, "Adding new goal for user ID: {0}", userId);
                operationSuccess = goalDAO.addGoal(goal);
                if (operationSuccess) {
                    session.setAttribute("successMessage", "Goal successfully added!");
                } else {
                    session.setAttribute("errorMessage", "Failed to add new goal. Please try again.");
                }
            } else if ("update".equals(action)) {
                // Update existing goal
                int goalId = Integer.parseInt(goalIdStr);
                goal.setGoalId(goalId); // Set the goal ID for update
                LOGGER.log(Level.INFO, "Updating goal ID: {0} for user ID: {1}", new Object[]{goalId, userId});
                operationSuccess = goalDAO.updateGoal(goal);
                if (operationSuccess) {
                    session.setAttribute("successMessage", "Goal successfully updated!");
                } else {
                    session.setAttribute("errorMessage", "Failed to update goal. Please try again.");
                }
            } else {
                LOGGER.log(Level.WARNING, "Unknown action '{0}' requested in GoalServlet (POST).", action);
                session.setAttribute("errorMessage", "Invalid action for goal operation.");
            }

            // Redirect back to the goals list (dashboard) using Post-Redirect-Get pattern
            response.sendRedirect(request.getContextPath() + "/goals");

        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid goalId parameter in GoalServlet (POST) for update: " + e.getMessage(), e);
            request.setAttribute("errorMessage", "Invalid goal ID format for update.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred during goal operation (POST): " + e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            dispatcher.forward(request, response);
        }
    }
}
