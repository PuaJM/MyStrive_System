<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Include the common header fragment --%>
<jsp:include page="header.jsp" />

<div class="container">
    <%-- Display server-side success message if present --%>
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="message success-message">
            <p><c:out value="${sessionScope.successMessage}"/></p>
        </div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>

    <%-- Display server-side error message if present --%>
    <c:if test="${not empty requestScope.errorMessage}">
        <div class="message error-message">
            <p><c:out value="${requestScope.errorMessage}"/></p>
        </div>
    </c:if>

    <c:if test="${requestScope.goal != null}">
        <div class="goal-details-card">
            <h2 style="color: var(--secondary-color); margin-bottom: 20px;">Goal Details - <c:out value="${requestScope.goal.goalDescription}"/></h2>
            
            <p><strong>Category:</strong> <c:out value="${requestScope.goal.categoryName != null ? requestScope.goal.categoryName : 'N/A'}"/></p>
            <p><strong>Target Date:</strong> <fmt:formatDate value="${requestScope.goal.targetDate}" pattern="yyyy-MM-dd"/></p>
            <p><strong>Status:</strong> <c:out value="${requestScope.goal.status}"/></p>
            <p><strong>Created On:</strong> <fmt:formatDate value="${requestScope.goal.createdAt}" pattern="yyyy-MM-dd HH:mm"/></p>
            <p><strong>Last Updated:</strong> <fmt:formatDate value="${requestScope.goal.updatedAt}" pattern="yyyy-MM-dd HH:mm"/></p>

            <div class="form-actions" style="justify-content: flex-start; margin-top: 25px;">
                <a href="${pageContext.request.contextPath}/goals?action=edit&goalId=<c:out value="${requestScope.goal.goalId}"/>" class="btn btn-secondary">
                    <i class="fas fa-edit"></i> Edit Goal
                </a>
                <a href="${pageContext.request.contextPath}/goals" class="btn btn-cancel">
                    <i class="fas fa-arrow-left"></i> Back to Goals
                </a>
            </div>
        </div>

        <div class="milestones-section">
            <h3 style="color: var(--primary-color);">Milestones</h3>

            <%-- Form for Add/Edit Milestone --%>
            <div class="form-card" style="margin-top: 20px; margin-bottom: 30px;">
                <h4 style="color: var(--secondary-color); margin-bottom: 20px;">
                    <c:out value="${requestScope.formTitle != null ? requestScope.formTitle : 'Add New Milestone'}"/>
                </h4>
                <form action="${pageContext.request.contextPath}/milestones" method="post" id="milestoneForm">
                    <input type="hidden" name="goalId" value="${requestScope.goal.goalId}">
                    <c:if test="${requestScope.milestone != null && requestScope.milestone.milestoneId != 0}">
                        <input type="hidden" name="milestoneId" value="${requestScope.milestone.milestoneId}">
                        <input type="hidden" name="action" value="update">
                    </c:if>
                    <c:if test="${requestScope.milestone == null || requestScope.milestone.milestoneId == 0}">
                        <input type="hidden" name="action" value="add">
                    </c:if>

                    <div class="form-group">
                        <label for="milestoneDescription">Milestone Description:</label>
                        <textarea id="milestoneDescription" name="milestoneDescription" required><c:out value="${requestScope.milestone.milestoneDescription}"/></textarea>
                        <small class="error-text" id="milestoneDescriptionError"></small>
                    </div>

                    <div class="form-group">
                        <label for="dueDate">Due Date:</label>
                        <input type="date" id="dueDate" name="dueDate"
                               value="<fmt:formatDate value="${requestScope.milestone.dueDate}" pattern="yyyy-MM-dd"/>" required>
                        <small class="error-text" id="dueDateError"></small>
                    </div>

                    <div class="form-group">
                        <label for="milestoneStatus">Status:</label>
                        <select id="milestoneStatus" name="status" required>
                            <option value="">-- Select Status --</option>
                            <option value="Not Started" <c:if test="${requestScope.milestone.status eq 'Not Started'}">selected</c:if>>Not Started</option>
                            <option value="In Progress" <c:if test="${requestScope.milestone.status eq 'In Progress'}">selected</c:if>>In Progress</option>
                            <option value="Completed" <c:if test="${requestScope.milestone.status eq 'Completed'}">selected</c:if>>Completed</option>
                            <option value="On Hold" <c:if test="${requestScope.milestone.status eq 'On Hold'}">selected</c:if>>On Hold</option>
                            <option value="Cancelled" <c:if test="${requestScope.milestone.status eq 'Cancelled'}">selected</c:if>>Cancelled</option>
                        </select>
                        <small class="error-text" id="milestoneStatusError"></small>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Save Milestone</button>
                        <a href="${pageContext.request.contextPath}/milestones?goalId=<c:out value="${requestScope.goal.goalId}"/>" class="btn btn-cancel">Cancel</a>
                    </div>
                </form>
            </div>

            <%-- List of Milestones --%>
            <c:choose>
                <c:when test="${not empty requestScope.milestones}">
                    <div class="data-card">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Description</th>
                                    <th>Due Date</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="milestone" items="${requestScope.milestones}">
                                    <tr>
                                        <td><c:out value="${milestone.milestoneDescription}"/></td>
                                        <td><fmt:formatDate value="${milestone.dueDate}" pattern="yyyy-MM-dd"/></td>
                                        <td><c:out value="${milestone.status}"/></td>
                                        <td class="actions">
                                            <a href="${pageContext.request.contextPath}/milestones?action=editForm&goalId=<c:out value="${requestScope.goal.goalId}"/>&milestoneId=<c:out value="${milestone.milestoneId}"/>" class="btn btn-secondary btn-sm">
                                                <i class="fas fa-edit"></i> Edit
                                            </a>
                                            <a href="${pageContext.request.contextPath}/milestones?action=delete&goalId=<c:out value="${requestScope.goal.goalId}"/>&milestoneId=<c:out value="${milestone.milestoneId}"/>" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this milestone?');">
                                                <i class="fas fa-trash-alt"></i> Delete
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="message">
                        <p>No milestones found for this goal. Add one using the form above!</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </c:if>
    <c:if test="${requestScope.goal == null}">
        <div class="message error-message">
            <p>Goal not found or unauthorized access.</p>
            <p><a href="${pageContext.request.contextPath}/goals" class="btn btn-secondary" style="display: inline-block; margin-top: 15px;">Back to Goals</a></p>
        </div>
    </c:if>
</div>

<%-- Link to client-side JavaScript for validation --%>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
<script>
    // Initialize validation for the milestone form
    document.addEventListener('DOMContentLoaded', function() {
        const milestoneForm = document.getElementById('milestoneForm');
        if (milestoneForm) {
            milestoneForm.addEventListener('submit', function(event) {
                if (!validateMilestoneForm()) {
                    event.preventDefault(); // Prevent form submission if validation fails
                }
            });
        }
    });
</script>

<%-- Include the common footer fragment --%>
<jsp:include page="footer.jsp" />
