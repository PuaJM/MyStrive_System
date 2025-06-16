<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Include the common header fragment --%>
<jsp:include page="header.jsp" />

<div class="form-card text-center">
    <h2 style="color: var(--primary-color); margin-bottom: 25px;">Success!</h2>

    <div class="message success-message">
        <p>Operation completed successfully.</p>
        <%-- Display specific success message if provided by a servlet --%>
        <c:if test="${not empty requestScope.successMessage}">
            <p><strong>Details:</strong> <c:out value="${requestScope.successMessage}"/></p>
        </c:if>
    </div>

    <div style="margin-top: 30px;">
        <a href="${pageContext.request.contextPath}/goals" class="btn btn-primary">
            <i class="fas fa-bullseye"></i> Go to Goals
        </a>
    </div>
</div>

<%-- Include the common footer fragment --%>
<jsp:include page="footer.jsp" />
