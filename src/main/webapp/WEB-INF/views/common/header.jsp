<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="loggedUser" value="${sessionScope.loggedInUser}" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><c:out value="${pageTitle}"/> - Flight Reservation</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
  <link href="${ctx}/resources/css/style.css?v=1" rel="stylesheet">
</head>
<body class="d-flex flex-column min-vh-100">
<nav class="navbar navbar-expand navbar-dark navbar-flite sticky-top flex-wrap px-3">
  <a class="navbar-brand d-flex align-items-center gap-2" href="${ctx}/">
    <span class="brand-icon"><i class="bi bi-airplane-fill"></i></span> Flight Reservation
  </a>
  <ul class="navbar-nav me-auto">
    <li class="nav-item"><a class="nav-link" href="${ctx}/"><i class="bi bi-house"></i> Home</a></li>
    <c:if test="${not empty loggedUser}">
      <c:choose>
        <c:when test="${loggedUser.role == 'Admin'}">
          <li class="nav-item"><a class="nav-link" href="${ctx}/admin/dashboard"><i class="bi bi-speedometer2"></i> Dashboard</a></li>
          <li class="nav-item"><a class="nav-link" href="${ctx}/admin/flights/add"><i class="bi bi-plus-circle"></i> Add Flight</a></li>
          <li class="nav-item"><a class="nav-link" href="${ctx}/admin/flights/all"><i class="bi bi-list-ul"></i> All Flights</a></li>
        </c:when>
        <c:otherwise>
          <li class="nav-item"><a class="nav-link" href="${ctx}/user/dashboard"><i class="bi bi-speedometer2"></i> Dashboard</a></li>
          <li class="nav-item"><a class="nav-link" href="${ctx}/user/bookings"><i class="bi bi-ticket-perforated"></i> My Bookings</a></li>
        </c:otherwise>
      </c:choose>
    </c:if>
  </ul>
  <ul class="navbar-nav">
    <c:choose>
      <c:when test="${empty loggedUser}">
        <li class="nav-item"><a class="nav-link" href="${ctx}/login"><i class="bi bi-box-arrow-in-right"></i> Login</a></li>
        <li class="nav-item"><a class="nav-link" href="${ctx}/signup"><i class="bi bi-person-plus"></i> Sign Up</a></li>
      </c:when>
      <c:otherwise>
        <li class="nav-item">
          <span class="nav-link">
            <i class="bi bi-person-circle"></i> <c:out value="${loggedUser.email}"/>
            <c:if test="${loggedUser.role == 'Admin'}"><span class="badge bg-warning text-dark">Admin</span></c:if>
          </span>
        </li>
        <li class="nav-item"><a class="nav-link" href="${ctx}/logout"><i class="bi bi-box-arrow-right"></i> Logout</a></li>
      </c:otherwise>
    </c:choose>
  </ul>
</nav>
<main class="flex-grow-1 container py-4">