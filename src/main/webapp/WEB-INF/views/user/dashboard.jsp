<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="My Dashboard" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<h3><i class="bi bi-person-circle"></i> My Dashboard</h3>
<p>Welcome, <strong><c:out value="${loggedUser.email}"/></strong>!</p>

<div class="row g-4 mt-2">
  <div class="col-md-4">
    <div class="card shadow-sm h-100"><div class="card-body">
      <h5><i class="bi bi-ticket-perforated"></i> My Bookings</h5>
      <a href="${ctx}/user/bookings" class="btn btn-primary"><i class="bi bi-list-ul"></i> View My Bookings</a>
    </div></div>
  </div>
  <div class="col-md-4">
    <div class="card shadow-sm h-100"><div class="card-body">
      <h5><i class="bi bi-search"></i> Search Flights</h5>
      <a href="${ctx}/" class="btn btn-success"><i class="bi bi-search"></i> Find Flights</a>
    </div></div>
  </div>
</div>
  <div class="col-md-4">
    <div class="card shadow-sm h-100"><div class="card-body">
      <h5>All Flights</h5>
      <p class="text-muted small">Browse every flight in the system, including ones with no bookings yet.</p>
      <a href="${ctx}/admin/flights/all" class="btn btn-dark"><i class="bi bi-list-ul"></i> All Flights</a>
    </div></div>
  </div>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>