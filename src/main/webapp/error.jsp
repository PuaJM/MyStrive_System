<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Include the common header fragment --%>
<jsp:include page="header.jsp" />

<div class="form-card text-center">
    <h2 style="color: #d32f2f; margin-bottom: 25px;">Error!</h2>

    <div class="message error-message">
        <p>An unexpected error occurred. Please try again later.</p>
        <%-- Display specific error message if provided by a servlet --%>
        <c:if test="${not empty requestScope.errorMessage}">
            <p><strong>Details:</strong> <c:out value="${requestScope.errorMessage}"/></p>
        </c:if>
    </div>

    <div style="margin-top: 30px;">
        <a href="javascript:history.back()" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Back to Previous Page
        </a>
        <a href="${pageContext.request.contextPath}/goals" class="btn btn-primary" style="margin-left: 15px;">
            <i class="fas fa-bullseye"></i> Go to Goals
        </a>
    </div>
</div>

<%-- Include the common footer fragment --%>
<jsp:include page="footer.jsp" />
