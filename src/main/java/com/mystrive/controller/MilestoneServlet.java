package com.mystrive.controller;

import com.mystrive.model.Milestone;
import com.mystrive.model.Goal;
import com.mystrive.model.User;
import com.mystrive.dao.MilestoneDAO;
import com.mystrive.dao.GoalDAO; // Needed to verify goal ownership and retrieve goal details

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

public class MilestoneServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(MilestoneServlet.class.getName());
    private MilestoneDAO milestoneDAO;
    private GoalDAO goalDAO; // To check goal ownership

    @Override
    public void init() throws ServletException {
        super.init();
        milestoneDAO = new MilestoneDAO();
        goalDAO = new GoalDAO();
    }

    /**
     * Handles HTTP GET requests.
     * This method is responsible for:
     * 1. Displaying the list of milestones for a specific goal (redirects to goalDetails.jsp).
     * 2. Displaying the Add/Edit Milestone form (within goalDetails.jsp or a specific form).
     * 3. Handling milestone deletion requests.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            LOGGER.log(Level.WARNING, "Unauthorized access to MilestoneServlet (GET). Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        int userId = currentUser.getUserId();
        String action = request.getParameter("action");
        String goalIdParam = request.getParameter("goalId");
        int goalId;

        // Ensure goalId is present and valid for all milestone operations
        if (goalIdParam == null || goalIdParam.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Missing goalId parameter for milestone operation (GET).");
            session.setAttribute("errorMessage", "Goal ID is required to manage milestones.");
            response.sendRedirect(request.getContextPath() + "/goals"); // Redirect to dashboard
            return;
        }

        try {
            goalId = Integer.parseInt(goalIdParam);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid goalId parameter format: " + goalIdParam, e);
            session.setAttribute("errorMessage", "Invalid goal ID format.");
            response.sendRedirect(request.getContextPath() + "/goals"); // Redirect to dashboard
            return;
        }

        // Verify that the goal belongs to the current user
        Goal parentGoal = goalDAO.getGoalById(goalId);
        if (parentGoal == null || parentGoal.getUserId() != userId) {
            LOGGER.log(Level.WARNING, "Unauthorized access to goal ID {0} for user {1} to manage milestones.", new Object[]{goalId, userId});
            session.setAttribute("errorMessage", "Goal not found or unauthorized access.");
            response.sendRedirect(request.getContextPath() + "/goals");
            return;
        }

        // Pass the parent goal to the JSP for display context
        request.setAttribute("goal", parentGoal);

        RequestDispatcher dispatcher;
        try {
            if ("addForm".equals(action)) {
                // Action: Display form to add a new milestone
                LOGGER.log(Level.INFO, "Displaying add milestone form for goal ID: {0}", goalId);
                request.setAttribute("milestone", new Milestone()); // Empty milestone object for the form
                request.setAttribute("formTitle", "Add New Milestone");
                
                // Re-fetch milestones for the list part of goalDetails.jsp
                List<Milestone> milestones = milestoneDAO.getAllMilestonesByGoalId(goalId);
                request.setAttribute("milestones", milestones);
                
                dispatcher = request.getRequestDispatcher("/goalDetails.jsp"); // Forward to goal details page
                dispatcher.forward(request, response);
            } else if ("editForm".equals(action)) {
                // Action: Display form to edit an existing milestone
                String milestoneIdParam = request.getParameter("milestoneId");
                if (milestoneIdParam == null || milestoneIdParam.trim().isEmpty()) {
                    LOGGER.log(Level.WARNING, "Missing milestoneId parameter for editForm action.");
                    session.setAttribute("errorMessage", "Milestone ID is required for editing.");
                    response.sendRedirect(request.getContextPath() + "/milestones?action=list&goalId=" + goalId);
                    return;
                }
                int milestoneId = Integer.parseInt(milestoneIdParam);
                LOGGER.log(Level.INFO, "Displaying edit milestone form for ID: {0}, goal ID: {1}", new Object[]{milestoneId, goalId});

                Milestone existingMilestone = milestoneDAO.getMilestoneById(milestoneId);

                if (existingMilestone != null && existingMilestone.getGoalId() == goalId) {
                    request.setAttribute("milestone", existingMilestone);
                    request.setAttribute("formTitle", "Edit Milestone");

                    // Re-fetch milestones for the list part of goalDetails.jsp
                    List<Milestone> milestones = milestoneDAO.getAllMilestonesByGoalId(goalId);
                    request.setAttribute("milestones", milestones);

                    dispatcher = request.getRequestDispatcher("/goalDetails.jsp"); // Forward to goal details page
                    dispatcher.forward(request, response);
                } else {
                    LOGGER.log(Level.WARNING, "Milestone ID {0} not found or not belonging to goal {1} for editing.", new Object[]{milestoneId, goalId});
                    session.setAttribute("errorMessage", "Milestone not found or unauthorized access.");
                    response.sendRedirect(request.getContextPath() + "/milestones?action=list&goalId=" + goalId);
                }
            } else if ("delete".equals(action)) {
                // Action: Delete a milestone
                String milestoneIdParam = request.getParameter("milestoneId");
                if (milestoneIdParam == null || milestoneIdParam.trim().isEmpty()) {
                    LOGGER.log(Level.WARNING, "Missing milestoneId parameter for delete action.");
                    session.setAttribute("errorMessage", "Milestone ID is required for deletion.");
                    response.sendRedirect(request.getContextPath() + "/milestones?action=list&goalId=" + goalId);
                    return;
                }
                int milestoneId = Integer.parseInt(milestoneIdParam);
                LOGGER.log(Level.INFO, "Attempting to delete milestone ID: {0} for goal ID: {1}", new Object[]{milestoneId, goalId});

                boolean deleted = milestoneDAO.deleteMilestone(milestoneId, goalId);
                if (deleted) {
                    LOGGER.log(Level.INFO, "Milestone ID {0} deleted successfully.", milestoneId);
                    session.setAttribute("successMessage", "Milestone successfully deleted!");
                } else {
                    LOGGER.log(Level.WARNING, "Failed to delete milestone ID {0} or unauthorized (not belonging to goal {1}).", new Object[]{milestoneId, goalId});
                    session.setAttribute("errorMessage", "Failed to delete milestone or unauthorized.");
                }
                response.sendRedirect(request.getContextPath() + "/milestones?action=list&goalId=" + goalId); // Redirect back to goal details
            } else {
                // Default action: List all milestones for the given goal (Goal Details Page)
                LOGGER.log(Level.INFO, "Retrieving all milestones for goal ID: {0}", goalId);
                List<Milestone> milestones = milestoneDAO.getAllMilestonesByGoalId(goalId);
                request.setAttribute("milestones", milestones);

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
                
                dispatcher = request.getRequestDispatcher("/goalDetails.jsp");
                dispatcher.forward(request, response);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid milestoneId parameter in MilestoneServlet (GET): " + e.getMessage(), e);
            session.setAttribute("errorMessage", "Invalid milestone ID format.");
            response.sendRedirect(request.getContextPath() + "/milestones?action=list&goalId=" + goalId); // Redirect back to goal details
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred in MilestoneServlet (GET): " + e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            dispatcher = request.getRequestDispatcher("/error.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Handles HTTP POST requests.
     * This method is responsible for:
     * 1. Adding a new milestone.
     * 2. Updating an existing milestone.
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
            LOGGER.log(Level.WARNING, "Unauthorized access to MilestoneServlet (POST). Redirecting to login.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        int userId = currentUser.getUserId();

        String action = request.getParameter("action");
        String goalIdParam = request.getParameter("goalId");
        String milestoneDescription = request.getParameter("milestoneDescription");
        String dueDateStr = request.getParameter("dueDate");
        String status = request.getParameter("status");
        String milestoneIdStr = request.getParameter("milestoneId"); // Present for "update" action

        int goalId;
        try {
            goalId = Integer.parseInt(goalIdParam);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid goalId parameter in MilestoneServlet (POST): " + goalIdParam, e);
            session.setAttribute("errorMessage", "Invalid goal ID format.");
            response.sendRedirect(request.getContextPath() + "/goals");
            return;
        }

        // Verify that the goal belongs to the current user before processing milestone operations
        Goal parentGoal = goalDAO.getGoalById(goalId);
        if (parentGoal == null || parentGoal.getUserId() != userId) {
            LOGGER.log(Level.WARNING, "Unauthorized attempt to manage milestones for goal ID {0} by user {1}.", new Object[]{goalId, userId});
            session.setAttribute("errorMessage", "Goal not found or unauthorized access.");
            response.sendRedirect(request.getContextPath() + "/goals");
            return;
        }
        request.setAttribute("goal", parentGoal); // Ensure parent goal is available if forwarding back to JSP

        String errorMessage = null;

        // Server-side validation
        if (milestoneDescription == null || milestoneDescription.trim().isEmpty() ||
            dueDateStr == null || dueDateStr.trim().isEmpty() ||
            status == null || status.trim().isEmpty()) {
            errorMessage = "Milestone description, due date, and status are required.";
        }

        Date dueDate = null;
        if (dueDateStr != null && !dueDateStr.isEmpty()) {
            try {
                dueDate = Date.valueOf(dueDateStr);
            } catch (IllegalArgumentException e) {
                errorMessage = "Invalid due date format. Please use YYYY-MM-DD.";
            }
        }

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            // Re-populate data for the form if validation fails
            Milestone currentMilestone = new Milestone();
            currentMilestone.setGoalId(goalId);
            currentMilestone.setMilestoneDescription(milestoneDescription);
            currentMilestone.setDueDate(dueDate);
            currentMilestone.setStatus(status);
            if (milestoneIdStr != null && !milestoneIdStr.isEmpty()) {
                try {
                    currentMilestone.setMilestoneId(Integer.parseInt(milestoneIdStr));
                } catch (NumberFormatException e) { /* ignore */ }
            }
            request.setAttribute("milestone", currentMilestone); // So the form can display what was entered
            request.setAttribute("formTitle", (milestoneIdStr != null && !milestoneIdStr.isEmpty()) ? "Edit Milestone" : "Add New Milestone");

            // Re-fetch existing milestones to display the list part of goalDetails.jsp
            List<Milestone> milestones = milestoneDAO.getAllMilestonesByGoalId(goalId);
            request.setAttribute("milestones", milestones);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/goalDetails.jsp"); // Forward back to goal details page
            dispatcher.forward(request, response);
            LOGGER.log(Level.WARNING, "Milestone POST failed due to validation errors for goal ID {0}, user {1}: {2}", new Object[]{goalId, userId, errorMessage});
            return;
        }

        Milestone milestone = new Milestone();
        milestone.setGoalId(goalId);
        milestone.setMilestoneDescription(milestoneDescription);
        milestone.setDueDate(dueDate);
        milestone.setStatus(status);

        boolean operationSuccess = false;
        try {
            if ("add".equals(action)) {
                // Add new milestone
                LOGGER.log(Level.INFO, "Adding new milestone for goal ID: {0}", goalId);
                operationSuccess = milestoneDAO.addMilestone(milestone);
                if (operationSuccess) {
                    session.setAttribute("successMessage", "Milestone successfully added!");
                } else {
                    session.setAttribute("errorMessage", "Failed to add new milestone. Please try again.");
                }
            } else if ("update".equals(action)) {
                // Update existing milestone
                int milestoneId = Integer.parseInt(milestoneIdStr);
                milestone.setMilestoneId(milestoneId); // Set the milestone ID for update
                LOGGER.log(Level.INFO, "Updating milestone ID: {0} for goal ID: {1}", new Object[]{milestoneId, goalId});
                operationSuccess = milestoneDAO.updateMilestone(milestone);
                if (operationSuccess) {
                    session.setAttribute("successMessage", "Milestone successfully updated!");
                } else {
                    session.setAttribute("errorMessage", "Failed to update milestone. Milestone not found or unauthorized.");
                }
            } else {
                LOGGER.log(Level.WARNING, "Unknown action '{0}' requested in MilestoneServlet (POST).", action);
                session.setAttribute("errorMessage", "Invalid action for milestone operation.");
            }

            // Redirect back to the goal details page using Post-Redirect-Get pattern
            response.sendRedirect(request.getContextPath() + "/milestones?action=list&goalId=" + goalId);

        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Invalid milestoneId parameter in MilestoneServlet (POST) for update: " + e.getMessage(), e);
            session.setAttribute("errorMessage", "Invalid milestone ID format for update.");
            // To ensure the page displays correctly, redirect back to goal details or generic error
            response.sendRedirect(request.getContextPath() + "/milestones?action=list&goalId=" + goalId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred during milestone operation (POST): " + e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp");
            dispatcher.forward(request, response);
        }
    }
}
