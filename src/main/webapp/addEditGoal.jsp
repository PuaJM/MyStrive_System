<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Include the common header fragment --%>
<jsp:include page="header.jsp" />

<div class="form-card">
    <h2 class="text-center" style="color: var(--secondary-color); margin-bottom: 25px;">
        <c:out value="${requestScope.formTitle != null ? requestScope.formTitle : 'Manage Goal'}"/>
    </h2>

    <%-- Display server-side error message if present --%>
    <c:if test="${not empty requestScope.errorMessage}">
        <div class="message error-message">
            <p><c:out value="${requestScope.errorMessage}"/></p>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/goals" method="post" id="goalForm">
        <c:if test="${requestScope.goal.goalId != 0}">
            <%-- Hidden field for goal ID for update operation --%>
            <input type="hidden" name="goalId" value="${requestScope.goal.goalId}">
            <input type="hidden" name="action" value="update">
        </c:if>
        <c:if test="${requestScope.goal.goalId == 0}">
            <%-- Hidden field for add operation --%>
            <input type="hidden" name="action" value="add">
        </c:if>

        <div class="form-group">
            <label for="goalDescription">Goal Description:</label>
            <textarea id="goalDescription" name="goalDescription" required><c:out value="${requestScope.goal.goalDescription}"/></textarea>
            <small class="error-text" id="goalDescriptionError"></small>
        </div>

        <div class="form-group">
            <label for="categoryId">Category:</label>
            <select id="categoryId" name="categoryId">
                <option value="">-- Select Category --</option>
                <c:forEach var="category" items="${requestScope.categories}">
                    <option value="${category.categoryId}"
                        <c:if test="${category.categoryId == requestScope.goal.categoryId}">selected</c:if>>
                        <c:out value="${category.categoryName}"/>
                    </option>
                </c:forEach>
            </select>
            <small class="error-text" id="categoryIdError"></small>
        </div>

        <div class="form-group">
            <label for="targetDate">Target Date:</label>
            <input type="date" id="targetDate" name="targetDate"
                   value="<fmt:formatDate value="${requestScope.goal.targetDate}" pattern="yyyy-MM-dd"/>" required>
            <small class="error-text" id="targetDateError"></small>
        </div>

        <div class="form-group">
            <label for="status">Status:</label>
            <select id="status" name="status" required>
                <option value="">-- Select Status --</option>
                <option value="Not Started" <c:if test="${requestScope.goal.status eq 'Not Started'}">selected</c:if>>Not Started</option>
                <option value="In Progress" <c:if test="${requestScope.goal.status eq 'In Progress'}">selected</c:if>>In Progress</option>
                <option value="Completed" <c:if test="${requestScope.goal.status eq 'Completed'}">selected</c:if>>Completed</option>
                <option value="On Hold" <c:if test="${requestScope.goal.status eq 'On Hold'}">selected</c:if>>On Hold</option>
                <option value="Cancelled" <c:if test="${requestScope.goal.status eq 'Cancelled'}">selected</c:if>>Cancelled</option>
            </select>
            <small class="error-text" id="statusError"></small>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Save Goal</button>
            <a href="${pageContext.request.contextPath}/goals" class="btn btn-cancel">Cancel</a>
        </div>
    </form>
</div>

<%-- Link to client-side JavaScript for validation --%>
<script src="${pageContext.request.contextPath}/validation.js"></script>
<script>
    // Initialize validation for the goal form
    document.addEventListener('DOMContentLoaded', function() {
        const goalForm = document.getElementById('goalForm');
        if (goalForm) {
            goalForm.addEventListener('submit', function(event) {
                if (!validateGoalForm()) {
                    event.preventDefault(); // Prevent form submission if validation fails
                }
            });
        }
    });
</script>

<%-- Include the common footer fragment --%>
<jsp:include page="footer.jsp" />
