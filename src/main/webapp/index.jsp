<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>MyStrive</title>
    <%-- Redirect to the login page --%>
    <meta http-equiv="Refresh" content="0; URL=${pageContext.request.contextPath}/login.jsp">
</head>
<body>
    <p>Redirecting to <a href="${pageContext.request.contextPath}/login.jsp">Login Page</a>...</p>
</body>
</html>
