<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Include the common header fragment --%>
<jsp:include page="header.jsp" />

<div class="dashboard-layout">
    <div class="sidebar">
        <h3>Navigation</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/goals"><i class="fas fa-bullseye"></i> All Goals</a></li>
            <li><a href="${pageContext.request.contextPath}/categories"><i class="fas fa-tags"></i> Manage Categories</a></li>
                
        </ul>
    </div>

    <div class="content-area">
        <h2 style="color: var(--secondary-color); margin-bottom: 25px;">My Goals</h2>

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

        <div style="margin-bottom: 20px; text-align: right;">
            <a href="${pageContext.request.contextPath}/goals?action=add" class="btn btn-primary">
                <i class="fas fa-plus"></i> Add New Goal
            </a>
        </div>

        <c:choose>
            <c:when test="${not empty requestScope.goals}">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Description</th>
                                <th>Category</th>
                                <th>Target Date</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="goal" items="${requestScope.goals}">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/milestones?goalId=<c:out value="${goal.goalId}"/>" class="goal-description-link">
                                            <c:out value="${goal.goalDescription}"/>
                                        </a>
                                    </td>
                                    <td><c:out value="${goal.categoryName != null ? goal.categoryName : 'N/A'}"/></td>
                                    <td><fmt:formatDate value="${goal.targetDate}" pattern="yyyy-MM-dd"/></td>
                                    <td><c:out value="${goal.status}"/></td>
                                    <td class="actions">
                                        <a href="${pageContext.request.contextPath}/goals?action=edit&goalId=<c:out value="${goal.goalId}"/>" class="btn btn-secondary btn-sm">
                                            <i class="fas fa-edit"></i> Edit
                                        </a>
                                        <a href="${pageContext.request.contextPath}/goals?action=delete&goalId=<c:out value="${goal.goalId}"/>" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this goal and all its milestones?');">
                                            <i class="fas fa-trash-alt"></i> Delete
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
            </c:when>
            <c:otherwise>
                <div class="message">
                    <p>No goals found. Start by adding a new goal!</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%-- Include the common footer fragment --%>
<jsp:include page="footer.jsp" />
