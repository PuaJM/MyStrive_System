<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Include the common header fragment --%>
<jsp:include page="header.jsp" />

<div class="form-card">
    <h2 class="text-center" style="color: var(--secondary-color); margin-bottom: 25px;">Register for MyStrive</h2>

    <%-- Display server-side error message if present --%>
    <c:if test="${not empty requestScope.errorMessage}">
        <div class="message error-message">
            <p><c:out value="${requestScope.errorMessage}"/></p>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/register" method="post" id="registerForm">
        <div class="form-group">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" value="${param.username}" required>
            <small class="error-text" id="usernameError"></small>
        </div>

        <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="${param.email}" required>
            <small class="error-text" id="emailError"></small>
        </div>

        <div class="form-group">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
            <small class="error-text" id="passwordError"></small>
        </div>

        <div class="form-group">
            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
            <small class="error-text" id="confirmPasswordError"></small>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Register</button>
        </div>
    </form>

    <p class="text-center" style="margin-top: 20px;">
        Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Login here</a>
    </p>
</div>

<%-- Link to client-side JavaScript for validation --%>
<script src="${pageContext.request.contextPath}/validation.js"></script>
<script>
    // Initialize validation for the registration form
    document.addEventListener('DOMContentLoaded', function() {
        const registerForm = document.getElementById('registerForm');
        if (registerForm) {
            registerForm.addEventListener('submit', function(event) {
                if (!validateRegisterForm()) {
                    event.preventDefault(); // Prevent form submission if validation fails
                }
            });
        }
    });
</script>

<%-- Include the common footer fragment --%>
<jsp:include page="footer.jsp" />
