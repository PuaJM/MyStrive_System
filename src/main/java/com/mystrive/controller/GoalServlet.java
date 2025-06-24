package com.mystrive.controller;

import com.mystrive.dao.GoalDAO;
import com.mystrive.dao.CategoryDAO;
import com.mystrive.dao.MilestoneDAO;
import com.mystrive.model.Goal;
import com.mystrive.model.Category;
import com.mystrive.model.Milestone;
import com.mystrive.model.User;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet for handling all Goal Management operations (CRUD).
 * This updated version also supports filtering goals by category.
 */
public class GoalServlet extends HttpServlet {

    // --- Added: Serial Version UID ---
    // This is recommended for all Servlets (classes that implement Serializable)
    // to ensure version compatibility during serialization/deserialization.
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GoalServlet.class.getName());

    private GoalDAO goalDAO;
    private CategoryDAO categoryDAO;
    private MilestoneDAO milestoneDAO;

    /**
     * Initializes the servlet.
     * This method is called by the servlet container when the servlet is first loaded.
     */
    @Override
    public void init() throws ServletException { // Added throws ServletException
        super.init(); // --- Added: Call to super.init() ---
        // It's good practice to call the parent class's init() method.
        goalDAO = new GoalDAO();
        categoryDAO = new CategoryDAO();
        milestoneDAO = new MilestoneDAO();
        LOGGER.log(Level.INFO, "GoalServlet initialized. DAOs instances created.");
    }

    /**
     * Handles HTTP GET requests.
     * This method is responsible for:
     * 1. Displaying the list of all goals (dashboard.jsp - default action), now with category filtering.
     * 2. Displaying the "Add New Goal" form (addEditGoal.jsp).
     * 3. Displaying the "Edit Goal" form, pre-populating fields (addEditGoal.jsp).
     * 4. Handling goal deletion requests.
     * 5. Displaying "Goal Details" with associated milestones (goalDetails.jsp).
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.log(Level.INFO, "Received GET request for /goals.");

        HttpSession session = request.getSession(false);
        // --- Updated: Check for "currentUser" attribute ---
        // Consistency with LoginServlet where "currentUser" is set in session.
        if (session == null || session.getAttribute("currentUser") == null) {
            LOGGER.log(Level.WARNING, "Unauthorized access to GoalServlet (GET). Redirecting to login.jsp.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // --- Updated: Retrieve "currentUser" attribute from session ---
        User currentUser = (User) session.getAttribute("currentUser");
        int userId = currentUser.getUserId();

        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }

        RequestDispatcher dispatcher;

        try {
            switch (action) {
                case "list":
                    // --- Start of Modified "list" action logic ---
                    LOGGER.log(Level.INFO, "Action: list goals for user ID: {0}", userId);

                    List<Goal> goals; // Declare goals list.
                    // Retrieve a list of ALL categories for the current user.
                    // This list will be passed to dashboard.jsp to populate the category filter sidebar.
                    List<Category> categoriesForSidebar = categoryDAO.getAllCategoriesByUserId(userId);
                    request.setAttribute("categoriesForSidebar", categoriesForSidebar);

                    // Check if a category ID filter is present in the request.
                    String filterCategoryIdParam = request.getParameter("categoryId");
                    int filterCategoryId = 0; // Default to 0, indicating no specific category filter.

                    if (filterCategoryIdParam != null && !filterCategoryIdParam.isEmpty()) {
                        try {
                            filterCategoryId = Integer.parseInt(filterCategoryIdParam);
                            // Important: Verify if this category actually belongs to the user.
                            // If it doesn't, treat it as an invalid filter.
                            Category selectedCategory = categoryDAO.getCategoryById(filterCategoryId);
                            if (selectedCategory == null || selectedCategory.getUserId() != userId) {
                                LOGGER.log(Level.WARNING, "User {0} attempted to filter by unauthorized or non-existent category ID {1}.", new Object[]{userId, filterCategoryId});
                                // Optionally, set an error message, but proceed to show all goals.
                                request.setAttribute("errorMessage", "Invalid category selected for filtering. Showing all goals.");
                                filterCategoryId = 0; // Reset to show all goals.
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.WARNING, "Invalid categoryId filter format: {0}. Showing all goals.", filterCategoryIdParam);
                            request.setAttribute("errorMessage", "Invalid category filter format. Showing all goals.");
                            filterCategoryId = 0; // Reset to show all goals.
                        }
                    }

                    if (filterCategoryId > 0) {
                        // If a valid category ID is provided, fetch goals specific to that category.
                        LOGGER.log(Level.INFO, "Fetching goals for user {0} filtered by category ID {1}.", new Object[]{userId, filterCategoryId});
                        goals = goalDAO.getAllGoalsByUserIdAndCategoryId(userId, filterCategoryId);
                        // Also, pass the selected category ID to the JSP to highlight it in the sidebar.
                        request.setAttribute("selectedCategoryId", filterCategoryId);
                    } else {
                        // If no category ID is provided, or it's invalid, fetch all goals for the user.
                        LOGGER.log(Level.INFO, "Fetching all goals for user {0}.", userId);
                        goals = goalDAO.getAllGoalsByUserId(userId);
                        request.setAttribute("selectedCategoryId", 0); // Indicate no category is specifically selected.
                    }

                    // Set the list of goals as a request attribute.
                    request.setAttribute("goals", goals);

                    // Check for success/error messages from session (Post-Redirect-Get pattern).
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

                    dispatcher = request.getRequestDispatcher("/dashboard.jsp");
                    dispatcher.forward(request, response);
                    // --- End of Modified "list" action logic ---
                    break;

                case "add":
                    LOGGER.log(Level.INFO, "Action: display add goal form for user ID: {0}", userId);
                    List<Category> categoriesForAdd = categoryDAO.getAllCategoriesByUserId(userId);
                    request.setAttribute("categories", categoriesForAdd);
                    request.setAttribute("goal", new Goal());
                    request.setAttribute("formTitle", "Add New Goal");
                    dispatcher = request.getRequestDispatcher("/addEditGoal.jsp");
                    dispatcher.forward(request, response);
                    break;

                case "edit":
                    LOGGER.log(Level.INFO, "Action: display edit goal form.");
                    String goalIdParamEdit = request.getParameter("goalId");
                    if (goalIdParamEdit == null || goalIdParamEdit.isEmpty()) {
                        LOGGER.log(Level.WARNING, "Missing goalId parameter for edit action.");
                        // --- Updated: Error message handling using session attribute and redirect to main goals ---
                        session.setAttribute("errorMessage", "Goal ID is required for editing.");
                        response.sendRedirect(request.getContextPath() + "/goals");
                        return;
                    }
                    int goalIdToEdit = Integer.parseInt(goalIdParamEdit);

                    Goal existingGoal = goalDAO.getGoalById(goalIdToEdit);

                    if (existingGoal != null && existingGoal.getUserId() == userId) {
                        LOGGER.log(Level.INFO, "Retrieving goal ID {0} for editing by user ID {1}.", new Object[]{goalIdToEdit, userId});
                        List<Category> categoriesForEdit = categoryDAO.getAllCategoriesByUserId(userId);
                        request.setAttribute("categories", categoriesForEdit);
                        request.setAttribute("goal", existingGoal);
                        request.setAttribute("formTitle", "Edit Goal");
                        dispatcher = request.getRequestDispatcher("/addEditGoal.jsp");
                        dispatcher.forward(request, response);
                    } else {
                        LOGGER.log(Level.WARNING, "Goal ID {0} not found or not owned by user {1} for editing.", new Object[]{goalIdToEdit, userId});
                        // --- Updated: Error message handling using session attribute and redirect to main goals ---
                        session.setAttribute("errorMessage", "Goal not found or unauthorized access for editing.");
                        response.sendRedirect(request.getContextPath() + "/goals");
                    }
                    break;

                case "delete":
                    LOGGER.log(Level.INFO, "Action: delete goal.");
                    String goalIdParamDelete = request.getParameter("goalId");
                    if (goalIdParamDelete == null || goalIdParamDelete.isEmpty()) {
                        LOGGER.log(Level.WARNING, "Missing goalId parameter for delete action.");
                        session.setAttribute("errorMessage", "Goal ID is required for deletion.");
                        response.sendRedirect(request.getContextPath() + "/goals");
                        return;
                    }
                    int goalIdToDelete = Integer.parseInt(goalIdParamDelete);

                    LOGGER.log(Level.INFO, "Attempting to delete goal ID: {0} for user ID: {1}", new Object[]{goalIdToDelete, userId});
                    boolean deleted = goalDAO.deleteGoal(goalIdToDelete, userId);
                    if (deleted) {
                        LOGGER.log(Level.INFO, "Goal ID {0} deleted successfully.", goalIdToDelete);
                        session.setAttribute("successMessage", "Goal successfully deleted!");
                    } else {
                        LOGGER.log(Level.WARNING, "Failed to delete goal ID {0} or unauthorized.", goalIdToDelete);
                        session.setAttribute("errorMessage", "Failed to delete goal or unauthorized.");
                    }
                    response.sendRedirect(request.getContextPath() + "/goals");
                    break;

                case "view":
                    LOGGER.log(Level.INFO, "Action: view goal details.");
                    String goalIdParamView = request.getParameter("goalId");
                    if (goalIdParamView == null || goalIdParamView.isEmpty()) {
                        LOGGER.log(Level.WARNING, "Missing goalId parameter for view action.");
                        session.setAttribute("errorMessage", "Goal ID is required to view details.");
                        response.sendRedirect(request.getContextPath() + "/goals");
                        return;
                    }
                    int goalIdToView = Integer.parseInt(goalIdParamView);

                    Goal goalToView = goalDAO.getGoalById(goalIdToView);

                    if (goalToView != null && goalToView.getUserId() == userId) {
                        LOGGER.log(Level.INFO, "Retrieving details for goal ID {0} by user ID {1}.", new Object[]{goalIdToView, userId});
                        List<Milestone> milestonesForGoal = milestoneDAO.getAllMilestonesByGoalId(goalIdToView);
                        request.setAttribute("goal", goalToView);
                        request.setAttribute("milestones", milestonesForGoal);

                        // --- Added: Session message handling for MilestoneServlet messages ---
                        // Transfer success/error messages from session to request scope for display on goalDetails.jsp
                        String successMilestoneMsg = (String) session.getAttribute("successMessage");
                        if (successMilestoneMsg != null) {
                            request.setAttribute("successMessage", successMilestoneMsg);
                            session.removeAttribute("successMessage");
                        }
                        String errorMilestoneMsg = (String) session.getAttribute("errorMessage");
                        if (errorMilestoneMsg != null) {
                            request.setAttribute("errorMessage", errorMilestoneMsg);
                            session.removeAttribute("errorMessage");
                        }
                        // --- End Added ---

                        dispatcher = request.getRequestDispatcher("/goalDetails.jsp");
                        dispatcher.forward(request, response);
                    } else {
                        LOGGER.log(Level.WARNING, "Goal ID {0} not found or not owned by user {1} for viewing details.", new Object[]{goalIdToView, userId});
                        // --- Updated: Error message handling using session attribute and redirect to main goals ---
                        session.setAttribute("errorMessage", "Goal details not found or unauthorized access.");
                        response.sendRedirect(request.getContextPath() + "/goals");
                    }
                    break;

                default:
                    LOGGER.log(Level.WARNING, "Unknown action '{0}' requested in GoalServlet (GET). Redirecting to list.", action);
                    // --- Updated: Error message handling using session attribute and redirect to main goals ---
                    session.setAttribute("errorMessage", "Invalid action requested.");
                    response.sendRedirect(request.getContextPath() + "/goals");
                    break;
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid ID parameter format in GoalServlet (GET): " + e.getMessage(), e);
            // --- Updated: Error message handling using session attribute and redirect to main goals ---
            session.setAttribute("errorMessage", "Invalid ID format provided.");
            response.sendRedirect(request.getContextPath() + "/goals");
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
        LOGGER.log(Level.INFO, "Received POST request for /goals. Processing goal data.");

        HttpSession session = request.getSession(false);
        // --- Updated: Check for "currentUser" attribute ---
        if (session == null || session.getAttribute("currentUser") == null) {
            LOGGER.log(Level.WARNING, "Unauthorized access to GoalServlet (POST). Redirecting to login.jsp.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // --- Updated: Retrieve "currentUser" attribute from session ---
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
        Date targetDate = null;

        // Server-side validation
        if (goalDescription == null || goalDescription.trim().isEmpty()) { // --- Minor edit: Combined conditions for clarity ---
            errorMessage = "Goal description is required.";
        } else if (targetDateStr == null || targetDateStr.trim().isEmpty()) {
            errorMessage = "Target Date is required.";
        } else if (status == null || status.trim().isEmpty()) {
            errorMessage = "Status is required.";
        }

        if (errorMessage == null && categoryIdStr != null && !categoryIdStr.isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryIdStr);
                // Optional: Validate if categoryId belongs to the current user
                Category selectedCategory = categoryDAO.getCategoryById(categoryId);
                if (selectedCategory == null || selectedCategory.getUserId() != userId) {
                    errorMessage = "Invalid category selected or unauthorized.";
                    categoryId = null; // Invalidate categoryId if it doesn't belong to the user
                    LOGGER.log(Level.WARNING, "User {0} tried to use unauthorized category ID {1}.", new Object[]{userId, categoryIdStr}); // --- Added logging ---
                }
            } catch (NumberFormatException e) {
                errorMessage = "Invalid category ID format.";
                LOGGER.log(Level.WARNING, "Invalid categoryId format: {0}", categoryIdStr); // --- Added logging ---
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "An unexpected error occurred during goal operation (GET): " + e.getMessage(), e);
                request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
                dispatcher.forward(request, response);
            }
            
        }
        // If categoryIdStr is empty, it means "No Category" is selected, so categoryId remains null.

        // --- Added: Target Date validation including past date check ---
        if (errorMessage == null && targetDateStr != null && !targetDateStr.isEmpty()) {
            try {
                targetDate = Date.valueOf(targetDateStr);
                java.util.Date today = new java.util.Date(); // Current date (java.util.Date)
                java.util.Date target = new java.util.Date(targetDate.getTime()); // Convert java.sql.Date to java.util.Date

                // Clear time components for accurate date-only comparison
                today = new Date(today.getTime()); // Truncate to day
                target = new Date(target.getTime()); // Truncate to day

                if (target.before(today)) { // Check if target date is before today
                    errorMessage = "Target Date cannot be in the past.";
                }
            } catch (IllegalArgumentException e) {
                errorMessage = "Invalid target date format. Please use ISO (YYYY-MM-DD)."; // --- Specific format instruction ---
                LOGGER.log(Level.WARNING, "Invalid targetDate format: {0}", targetDateStr); // --- Added logging ---
            }
        }
        // --- End Added ---


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
            List<Category> categoriesForForm = categoryDAO.getAllCategoriesByUserId(userId); // --- Renamed for clarity ---
            request.setAttribute("categories", categoriesForForm);
            request.setAttribute("formTitle", (goalIdStr != null && !goalIdStr.isEmpty()) ? "Edit Goal" : "Add New Goal");

            RequestDispatcher dispatcher = request.getRequestDispatcher("/addEditGoal.jsp");
            dispatcher.forward(request, response);
            LOGGER.log(Level.WARNING, "Goal POST failed due to validation errors for user {0}: {1}", new Object[]{userId, errorMessage}); // --- Added logging parameters ---
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
                    session.setAttribute("errorMessage", "Failed to update goal. Goal not found or unauthorized."); // --- Improved error message ---
                }
            } else {
                LOGGER.log(Level.WARNING, "Unknown action '{0}' requested in GoalServlet (POST).", action);
                session.setAttribute("errorMessage", "Invalid action for goal operation.");
            }

            // Redirect back to the goals list (dashboard) using Post-Redirect-Get pattern
            response.sendRedirect(request.getContextPath() + "/goals");

        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid goalId parameter in GoalServlet (POST) for update: " + e.getMessage(), e);
            session.setAttribute("errorMessage", "Invalid goal ID format for update."); // --- Consistency in error message ---
            response.sendRedirect(request.getContextPath() + "/goals"); // --- Redirect to safe page ---
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred during goal operation (POST): " + e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            dispatcher.forward(request, response);
        }
    }
}