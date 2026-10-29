<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Admin Dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="mb-4">
  <h2 class="mb-1"><i class="bi bi-shield-check text-danger"></i> Admin Dashboard</h2>
  <p class="text-muted mb-0">Welcome, <strong><c:out value="${loggedUser.email}"/></strong></p>
</div>

<h5 class="mb-3"><i class="bi bi-airplane"></i> Flight Management</h5>
<div class="row g-4 text-center">
  <div class="col-md-4">
    <div class="card hover-card hc-teal h-100"><div class="card-body p-4">
      <div class="icon-circle"><i class="bi bi-plus-circle-fill"></i></div>
      <h5>Add New Flight</h5>
      <p class="text-muted">Create new flight routes and schedules for your airline.</p>
      <a href="${ctx}/admin/flights/add" class="btn btn-primary"><i class="bi bi-plus-circle"></i> Add Flight</a>
    </div></div>
  </div>
  <div class="col-md-4">
    <div class="card hover-card hc-coral h-100"><div class="card-body p-4">
      <div class="icon-circle"><i class="bi bi-graph-up-arrow"></i></div>
      <h5>View Flights</h5>
      <p class="text-muted">Upcoming and completed journeys with passengers and revenue.</p>
      <a href="${ctx}/admin/flights" class="btn btn-primary"><i class="bi bi-list-ul"></i> View Flights</a>
    </div></div>
  </div>
  <div class="col-md-4">
    <div class="card hover-card hc-orange h-100"><div class="card-body p-4">
      <div class="icon-circle"><i class="bi bi-collection-fill"></i></div>
      <h5>All Flights</h5>
      <p class="text-muted">Browse every flight in the system, even ones with no bookings yet.</p>
      <a href="${ctx}/admin/flights/all" class="btn btn-primary"><i class="bi bi-list-ul"></i> All Flights</a>
    </div></div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>