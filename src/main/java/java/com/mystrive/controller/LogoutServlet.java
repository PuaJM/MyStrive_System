package java.com.mystrive.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());

    /**
     * Handles HTTP GET requests for user logout.
     * Invalidates the user's session and redirects them to the login page.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); // Get existing session, don't create a new one
        if (session != null) {
            String username = null;
            if (session.getAttribute("currentUser") != null) {
                username = ((java.com.mystrive.model.User) session.getAttribute("currentUser")).getUsername();
            }
            session.invalidate(); // Invalidate the session
            LOGGER.log(Level.INFO, "User {0} logged out and session invalidated.", username != null ? username : "unknown");
        } else {
            LOGGER.log(Level.INFO, "Attempted logout but no active session found.");
        }

        // Redirect to the login page after logout
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    /**
     * Handles HTTP POST requests.
     * For logout, a GET request is typically sufficient and simpler.
     * If POST is used, it would perform the same logic as doGet.
     *
     * @param request The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response); // Delegate POST to GET for simplicity in logout
    }
}
