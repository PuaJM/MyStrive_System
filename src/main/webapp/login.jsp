<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Include the common header fragment --%>
<jsp:include page="header.jsp" />

<div class="form-card">
    <h2 class="text-center" style="color: var(--secondary-color); margin-bottom: 25px;">Login to MyStrive</h2>

    <%-- Display server-side success message if present (e.g., after successful registration) --%>
    <c:if test="${not empty requestScope.successMessage}">
        <div class="message success-message">
            <p><c:out value="${requestScope.successMessage}"/></p>
        </div>
    </c:if>

    <%-- Display server-side error message if present --%>
    <c:if test="${not empty requestScope.errorMessage}">
        <div class="message error-message">
            <p><c:out value="${requestScope.errorMessage}"/></p>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post" id="loginForm">
        <div class="form-group">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" value="${param.username}" required>
            <small class="error-text" id="usernameError"></small>
        </div>

        <div class="form-group">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
            <small class="error-text" id="passwordError"></small>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Login</button>
        </div>
    </form>

    <p class="text-center" style="margin-top: 20px;">
        <a href="#">Forgot Password?</a> | <a href="${pageContext.request.contextPath}/register.jsp">Register for an account</a>
    </p>
</div>

<%-- Link to client-side JavaScript for validation --%>
<script src="${pageContext.request.contextPath}/validation.js"></script>
<script>
    // Initialize validation for the login form
    document.addEventListener('DOMContentLoaded', function() {
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', function(event) {
                if (!validateLoginForm()) {
                    event.preventDefault(); // Prevent form submission if validation fails
                }
            });
        }
    });
</script>

<%-- Include the common footer fragment --%>
<jsp:include page="footer.jsp" />
