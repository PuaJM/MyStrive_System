package com.mystrive.controller;

import com.mystrive.model.User;
import com.mystrive.dao.UserDAO;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;

public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private UserDAO userDAO;

    // Initialize DAO when servlet is created
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }

    /**
     * Handles HTTP POST requests for user login.
     * Authenticates user credentials via UserDAO, sets up session if successful,
     * and redirects to dashboard or back to login with error.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Retrieve form parameters
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        LOGGER.log(Level.INFO, "Attempting login for user: {0}", username);

        String errorMessage = null;

        // Basic server-side validation
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            errorMessage = "Username and password are required.";
        }

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
            dispatcher.forward(request, response);
            LOGGER.log(Level.WARNING, "Login failed due to validation errors for user {0}: {1}", new Object[]{username, errorMessage});
            return;
        }

        User authenticatedUser = null;
        try {
            authenticatedUser = userDAO.loginUser(username, password); // In a real app, compare hashed passwords!

            if (authenticatedUser != null) {
                // Login successful, set user in session
                HttpSession session = request.getSession();
                session.setAttribute("currentUser", authenticatedUser);
                session.setMaxInactiveInterval(30 * 60); // Session timeout in seconds (30 minutes)
                LOGGER.log(Level.INFO, "User {0} successfully logged in and session created.", username);

                // Redirect to the dashboard (Goal List Page)
                response.sendRedirect(request.getContextPath() + "/goals"); // Will be handled by GoalServlet doGet
            } else {
                // Authentication failed
                request.setAttribute("errorMessage", "Invalid username or password.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
                dispatcher.forward(request, response);
                LOGGER.log(Level.WARNING, "Login failed for user {0}: Invalid credentials.", username);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred during login for user " + username + ": " + e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp"); // Generic error page
            dispatcher.forward(request, response);
        }
    }

    /**
     * Handles HTTP GET requests.
     * Typically, a GET request to /login would just display the login form.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If a success message exists from registration, display it.
        String successMessage = (String) request.getSession().getAttribute("successMessage");
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
            request.getSession().removeAttribute("successMessage"); // Consume the message
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
        dispatcher.forward(request, response);
        LOGGER.log(Level.INFO, "Displaying login page.");
    }
}
