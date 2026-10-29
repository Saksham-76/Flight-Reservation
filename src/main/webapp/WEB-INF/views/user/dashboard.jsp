<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="My Dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="mb-4">
  <h2 class="mb-1"><i class="bi bi-person-circle text-danger"></i> My Dashboard</h2>
  <p class="text-muted mb-0">Welcome back, <strong><c:out value="${loggedUser.email}"/></strong></p>
</div>

<div class="row g-4">
  <div class="col-md-6">
    <div class="card hover-card hc-teal h-100"><div class="card-body p-4 text-center">
      <div class="icon-circle"><i class="bi bi-ticket-perforated-fill"></i></div>
      <h4>My Bookings</h4>
      <p class="text-muted">See every booking you have made, view tickets or cancel.</p>
      <a href="${ctx}/user/bookings" class="btn btn-primary"><i class="bi bi-list-ul"></i> View My Bookings</a>
    </div></div>
  </div>
  <div class="col-md-6">
    <div class="card hover-card hc-coral h-100"><div class="card-body p-4 text-center">
      <div class="icon-circle"><i class="bi bi-search-heart"></i></div>
      <h4>Search Flights</h4>
      <p class="text-muted">Find your next trip and book it in a few clicks.</p>
      <a href="${ctx}/" class="btn btn-primary"><i class="bi bi-search"></i> Find Flights</a>
    </div></div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>