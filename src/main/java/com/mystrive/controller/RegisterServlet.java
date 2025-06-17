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
import javax.servlet.RequestDispatcher; 

public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());
    private UserDAO userDAO;

    // Initialize DAO when servlet is created
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }

    /**
     * Handles HTTP POST requests for user registration.
     * Receives user input, performs server-side validation,
     * registers the user via UserDAO, and redirects accordingly.
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
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");

        LOGGER.log(Level.INFO, "Attempting to register user: {0} with email: {1}", new Object[]{username, email});

        String errorMessage = null;

        // Basic server-side validation (client-side JS validation is also assumed)
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            errorMessage = "All fields are required.";
        } else if (!password.equals(confirmPassword)) {
            errorMessage = "Password and Confirm Password do not match.";
        } else if (password.length() < 6) { // Example: minimum password length
            errorMessage = "Password must be at least 6 characters long.";
        }


        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/register.jsp");
            dispatcher.forward(request, response);
            LOGGER.log(Level.WARNING, "Registration failed due to validation errors: {0}", errorMessage);
            return;
        }

        // Create a User object
        User newUser = new User(username, password, email); // In a real app, hash the password before storing!

        // Attempt to register the user using DAO
        try {
            boolean isRegistered = userDAO.registerUser(newUser);

            if (isRegistered) {
                // Registration successful, redirect to login page with a success message
                request.getSession().setAttribute("successMessage", "Registration successful! Please log in.");
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                LOGGER.log(Level.INFO, "User {0} successfully registered and redirected to login.", username);
            } else {
                // Registration failed (e.g., username/email already exists, DB error)
                request.setAttribute("errorMessage", "Registration failed. Username or Email might already be taken.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/register.jsp");
                dispatcher.forward(request, response);
                LOGGER.log(Level.WARNING, "Registration failed for user {0}: possible duplicate username/email.", username);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An unexpected error occurred during registration for user " + username + ": " + e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/error.jsp"); // Generic error page
            dispatcher.forward(request, response);
        }
    }

    /**
     * Handles HTTP GET requests.
     * Typically, a GET request to /register would just display the registration form.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/register.jsp");
        dispatcher.forward(request, response);
        LOGGER.log(Level.INFO, "Displaying registration page.");
    }
}
