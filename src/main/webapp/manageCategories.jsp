<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Include the common header fragment --%>
<jsp:include page="header.jsp" />

<div class="dashboard-layout">
    <div class="sidebar">
        <h3>Navigation</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/goals"><i class="fas fa-bullseye"></i> All Goals</a></li>
            <li><a href="${pageContext.request.contextPath}/categories"><i class="fas fa-tags"></i> Manage Categories</a></li>
            <%-- Add more sidebar links here if needed --%>
        </ul>
    </div>

    <div class="content-area">
        <h2 style="color: var(--secondary-color); margin-bottom: 25px;">Manage Categories</h2>

        <%-- Display server-side success message if present --%>
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

        <%-- Form for Add/Edit Category --%>
        <div class="form-card" style="margin-bottom: 30px;">
            <h4 style="color: var(--secondary-color); margin-bottom: 20px;">
                <c:choose>
                    <c:when test="${requestScope.category != null && requestScope.category.categoryId != 0}">
                        Edit Category
                    </c:when>
                    <c:otherwise>
                        Add New Category
                    </c:otherwise>
                </c:choose>
            </h4>
            <form action="${pageContext.request.contextPath}/categories" method="post" id="categoryForm">
                <c:choose>
                    <c:when test="${requestScope.category != null && requestScope.category.categoryId != 0}">
                        <%-- Hidden field for category ID for update operation --%>
                        <input type="hidden" name="categoryId" value="${requestScope.category.categoryId}">
                        <input type="hidden" name="action" value="update">
                    </c:when>
                    <c:otherwise>
                        <%-- Hidden field for add operation --%>
                        <input type="hidden" name="action" value="add">
                    </c:otherwise>
                </c:choose>

                <div class="form-group">
                    <label for="categoryName">Category Name:</label>
                    <input type="text" id="categoryName" name="categoryName"
                           value="<c:out value="${requestScope.category != null ? requestScope.category.categoryName : ''}"/>" required>
                    <small class="error-text" id="categoryNameError"></small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Save Category</button>
                    <%-- If editing, show cancel to clear form; if adding, show cancel to go back to list (which is this page) --%>
                    <c:if test="${requestScope.category != null && requestScope.category.categoryId != 0}">
                         <a href="${pageContext.request.contextPath}/categories" class="btn btn-cancel">Cancel Edit</a>
                    </c:if>
                    <c:if test="${requestScope.category == null || requestScope.category.categoryId == 0}">
                         <button type="reset" class="btn btn-cancel">Clear Form</button> <%-- For clearing add form --%>
                    </c:if>
                </div>
            </form>
        </div>

        <%-- List of Categories --%>
        <c:choose>
            <c:when test="${not empty requestScope.categories}">
                <div class="data-card">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Category Name</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="category" items="${requestScope.categories}">
                                <tr>
                                    <td><c:out value="${category.categoryName}"/></td>
                                    <td class="actions">
                                        <a href="${pageContext.request.contextPath}/categories?action=edit&categoryId=<c:out value="${category.categoryId}"/>" class="btn btn-secondary btn-sm">
                                            <i class="fas fa-edit"></i> Edit
                                        </a>
                                        <a href="${pageContext.request.contextPath}/categories?action=delete&categoryId=<c:out value="${category.categoryId}"/>" class="btn btn-danger btn-sm" onclick="return confirm('Are you sure you want to delete this category? This will also remove the category from any associated goals.');">
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
                    <p>No categories found. Add one using the form above!</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%-- Link to client-side JavaScript for validation --%>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
<script>
    // Initialize validation for the category form
    document.addEventListener('DOMContentLoaded', function() {
        const categoryForm = document.getElementById('categoryForm');
        if (categoryForm) {
            categoryForm.addEventListener('submit', function(event) {
                if (!validateCategoryForm()) {
                    event.preventDefault(); // Prevent form submission if validation fails
                }
            });
        }
    });
</script>

<%-- Include the common footer fragment --%>
<jsp:include page="footer.jsp" />
