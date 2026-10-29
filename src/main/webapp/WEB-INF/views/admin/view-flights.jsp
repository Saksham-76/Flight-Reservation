<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Flight Journey Status" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h4 class="mb-0"><i class="bi bi-airplane"></i> Flight Journey Status</h4>
  <div>
    <a href="${ctx}/admin/flights/all" class="btn btn-outline-primary btn-sm"><i class="bi bi-list-ul"></i> All Flights</a>
    <a href="${ctx}/admin/dashboard" class="btn btn-dark btn-sm"><i class="bi bi-arrow-left"></i> Back to Dashboard</a>
  </div>
</div>

<div class="alert alert-info">
  <i class="bi bi-calendar-event"></i> Current Date: <strong><c:out value="${today}"/></strong>
</div>

<ul class="nav nav-tabs">
  <li class="nav-item">
    <a class="nav-link ${tab == 'upcoming' ? 'active' : ''}" href="${ctx}/admin/flights?tab=upcoming">
      <i class="bi bi-calendar-check"></i> Upcoming Journeys
      <span class="badge bg-primary"><c:out value="${upcomingCount}"/></span>
    </a>
  </li>
  <li class="nav-item">
    <a class="nav-link ${tab == 'completed' ? 'active' : ''}" href="${ctx}/admin/flights?tab=completed">
      <i class="bi bi-calendar2-check"></i> Completed Journeys
      <span class="badge bg-success"><c:out value="${completedCount}"/></span>
    </a>
  </li>
</ul>

<div class="card border-top-0 rounded-top-0 shadow-sm"><div class="card-body">
  <h6 class="mb-3">
    <c:choose>
      <c:when test="${tab == 'completed'}">Completed Journeys</c:when>
      <c:otherwise>Upcoming Journeys</c:otherwise>
    </c:choose>
  </h6>

  <c:choose>
    <c:when test="${empty journeys}">
      <div class="alert alert-secondary mb-0">No journeys with bookings yet.</div>
    </c:when>
    <c:otherwise>
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th>Flight Number</th><th>Journey Date</th><th>Total Passengers</th>
              <th>Total Bookings</th><th>Total Revenue</th>
              <c:if test="${tab == 'upcoming'}"><th>Days Until Journey</th></c:if>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="j" items="${journeys}">
              <tr>
                <td><span class="badge bg-info text-dark"><c:out value="${j.flightNumber}"/></span></td>
                <td><c:out value="${j.journeyDate}"/></td>
                <td><i class="bi bi-people"></i> <span class="badge bg-primary"><c:out value="${j.totalPassengers}"/></span></td>
                <td><span class="badge bg-dark"><c:out value="${j.totalBookings}"/></span></td>
                <td class="text-success fw-bold">&#8377;<c:out value="${j.totalRevenue}"/></td>
                <c:if test="${tab == 'upcoming'}">
                  <td>
                    <c:choose>
                      <c:when test="${j.daysUntilJourney == 0}">Today</c:when>
                      <c:when test="${j.daysUntilJourney == 1}">1 day</c:when>
                      <c:otherwise><c:out value="${j.daysUntilJourney}"/> days</c:otherwise>
                    </c:choose>
                  </td>
                </c:if>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:otherwise>
  </c:choose>
</div></div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>