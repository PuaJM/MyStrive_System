<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MyStrive - Personal Goal Management</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <header class="header">
        <div class="container header-content">
            <a href="${pageContext.request.contextPath}/goals" class="logo">My<span>Strive</span></a>
            <nav class="navbar">
                <ul>
                    <li><a href="${pageContext.request.contextPath}/goals"><i class="fas fa-bullseye"></i> Goals</a></li>
                    <li><a href="${pageContext.request.contextPath}/categories"><i class="fas fa-tags"></i> Categories</a></li>
                </ul>
                <c:if test="${sessionScope.currentUser != null}">
                    <div class="user-info">
                        <i class="fas fa-user-circle"></i> Hello, <c:out value="${sessionScope.currentUser.username}"/>
                        <a href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Logout</a>
                    </div>
                </c:if>
                <c:if test="${sessionScope.currentUser == null}">
                    <div class="user-info">
                        <a href="${pageContext.request.contextPath}/login.jsp"><i class="fas fa-sign-in-alt"></i> Login</a>
                        <a href="${pageContext.request.contextPath}/register.jsp"><i class="fas fa-user-plus"></i> Register</a>
                    </div>
                </c:if>
            </nav>
        </div>
    </header>
    <main class="main-content">
        <div class="container">
            <%-- This is where page-specific content will be inserted --%>
