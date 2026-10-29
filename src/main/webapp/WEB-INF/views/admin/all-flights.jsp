<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="All Flights" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h4 class="mb-0"><i class="bi bi-list-ul"></i> All Flights</h4>
  <div>
    <a href="${ctx}/admin/flights/add" class="btn btn-success btn-sm"><i class="bi bi-plus-circle"></i> Add Flight</a>
    <a href="${ctx}/admin/flights" class="btn btn-outline-primary btn-sm"><i class="bi bi-graph-up"></i> Journey Status</a>
    <a href="${ctx}/admin/dashboard" class="btn btn-dark btn-sm"><i class="bi bi-arrow-left"></i> Back to Dashboard</a>
  </div>
</div>

<div class="alert alert-info d-flex justify-content-between mb-3">
  <span><i class="bi bi-calendar-event"></i> Current Date: <strong><c:out value="${today}"/></strong></span>
  <span>Flights found: <strong><c:out value="${flights.size()}"/></strong></span>
</div>

<form action="${ctx}/admin/flights/all" method="get" class="row g-2 mb-3">
  <div class="col-md-6 col-lg-4">
    <input type="text" name="q" value="<c:out value='${q}'/>" class="form-control"
           placeholder="Search by flight number, source or destination">
  </div>
  <div class="col-auto">
    <button type="submit" class="btn btn-primary"><i class="bi bi-search"></i> Search</button>
    <c:if test="${not empty q}">
      <a href="${ctx}/admin/flights/all" class="btn btn-outline-secondary">Clear</a>
    </c:if>
  </div>
</form>

<c:choose>
  <c:when test="${empty flights}">
    <div class="alert alert-warning">No flights found.</div>
  </c:when>
  <c:otherwise>
    <div class="table-responsive">
      <table class="table table-hover bg-white shadow-sm align-middle">
        <thead class="table-light">
          <tr>
            <th>Flight Number</th>
            <th>Route</th>
            <th>Departure</th>
            <th>Arrival</th>
            <th>Capacity</th>
            <th>Price / Seat</th>
            <th>Upcoming Journeys</th>
            <th>Completed Journeys</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="f" items="${flights}">
            <tr>
              <td><span class="badge bg-info text-dark"><c:out value="${f.flightNumber}"/></span></td>
              <td><c:out value="${f.source}"/> &rarr; <c:out value="${f.destination}"/></td>
              <td><c:out value="${f.departureTime}"/></td>
              <td><c:out value="${f.arrivalTime}"/></td>
              <td><c:out value="${f.capacity}"/></td>
              <td>&#8377;<c:out value="${f.pricePerSeat}"/></td>
              <td>
                <c:choose>
                  <c:when test="${f.upcomingJourneys > 0}">
                    <span class="badge bg-primary"><c:out value="${f.upcomingJourneys}"/> journey(s)</span>
                    <small class="text-muted"><c:out value="${f.upcomingPassengers}"/> passenger(s)</small>
                  </c:when>
                  <c:otherwise><span class="text-muted">No bookings</span></c:otherwise>
                </c:choose>
              </td>
              <td>
                <c:choose>
                  <c:when test="${f.completedJourneys > 0}">
                    <span class="badge bg-success"><c:out value="${f.completedJourneys}"/> journey(s)</span>
                    <small class="text-muted"><c:out value="${f.completedPassengers}"/> passenger(s)</small>
                  </c:when>
                  <c:otherwise><span class="text-muted">None</span></c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>