<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%--
    dashboard.jsp
    This is the main dashboard page of the MyStrive application, displaying a list of goals
    for the currently logged-in user. It now features enhanced sidebar navigation
    to filter goals by specific categories.
--%>

<jsp:include page="header.jsp" />

<div class="dashboard-layout">
    <div class="sidebar">
        <h3>Navigation</h3>
        <ul>

            <li <c:if test="${requestScope.selectedCategoryId == 0}">class="active-filter"</c:if>>
                <a href="${pageContext.request.contextPath}/goals"><i class="fas fa-bullseye"></i> All Goals</a>
            </li>
            <%-- Line separator for visual distinction. --%>
            <li style="border-bottom: 1px solid #ddd; margin-bottom: 10px; padding-bottom: 5px;"></li>

            <h3>Categories</h3>

            <c:choose>
                <c:when test="${not empty requestScope.categoriesForSidebar}">
                    <c:forEach var="category" items="${requestScope.categoriesForSidebar}">
                        <%-- Add 'active-filter' class if this category is currently selected for filtering. --%>
                        <li <c:if test="${requestScope.selectedCategoryId == category.categoryId}">class="active-filter"</c:if>>
                            <%-- The link points back to the GoalServlet, explicitly requesting a 'list' action
                                and passing the 'categoryId' as a parameter. --%>
                            <a href="${pageContext.request.contextPath}/goals?action=list&categoryId=<c:out value="${category.categoryId}"/>">
                                <i class="fas fa-tag"></i> <c:out value="${category.categoryName}"/>
                            </a>
                        </li>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <li><p style="padding: 10px 15px; color: #777;">No categories found.</p></li>
                </c:otherwise>
            </c:choose>

            <li style="border-top: 1px solid #ddd; margin-top: 10px; padding-top: 5px;"></li>
            <li><a href="${pageContext.request.contextPath}/categories"><i class="fas fa-tags"></i> Manage Categories</a></li>
        </ul>
    </div>

    <div class="content-area">
        <h2 style="color: var(--secondary-color); margin-bottom: 25px;">My Goals</h2>

        <c:if test="${not empty sessionScope.successMessage}">
            <div class="message success-message">
                <p><c:out value="${sessionScope.successMessage}"/></p>
            </div>
            <c:remove var="successMessage" scope="session"/>
        </c:if>

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
                <div class="data-card">
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
                                    <td data-label="Description">
                                        <a href="${pageContext.request.contextPath}/milestones?goalId=<c:out value="${goal.goalId}"/>" class="goal-description-link">
                                            <c:out value="${goal.goalDescription}"/>
                                        </a>
                                    </td>
                                    <td data-label="Category"><c:out value="${goal.categoryName != null ? goal.categoryName : 'N/A'}"/></td>
                                    <td data-label="Target Date"><fmt:formatDate value="${goal.targetDate}" pattern="yyyy-MM-dd"/></td>
                                    <td data-label="Status"><c:out value="${goal.status}"/></td>
                                    <td data-label="Actions" class="actions">
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
                </div>
            </c:when>
            <c:otherwise>
                <div class="message">
                    <p>No goals found <c:if test="${requestScope.selectedCategoryId > 0}">for this category</c:if>. Start by adding a new goal!</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="footer.jsp" />
