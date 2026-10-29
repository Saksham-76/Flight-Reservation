<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="My Tickets" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<h4 class="mb-0"><i class="bi bi-ticket-detailed"></i> My Tickets</h4>
<p class="text-muted">
  Flight <strong><c:out value="${booking.flightNumber}"/></strong>:
  <c:out value="${booking.source}"/> &rarr; <c:out value="${booking.destination}"/>
</p>

<c:if test="${not empty successMessage}">
  <div class="alert alert-success"><c:out value="${successMessage}"/></div>
</c:if>
<c:if test="${not empty errorMessage}">
  <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
</c:if>

<div class="row g-4">
  <c:forEach var="t" items="${tickets}">
    <div class="col-md-6">
      <div class="card hover-card hc-orange h-100 ${t.status == 'CANCELLED' ? 'opacity-75' : ''}">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
          <span><i class="bi bi-airplane"></i> Flight Ticket</span>
          <span class="badge bg-light text-dark">PNR: <c:out value="${t.pnrNo}"/></span>
        </div>
        <div class="card-body">
          <h6 class="text-primary"><i class="bi bi-person"></i> Passenger Information</h6>
          <div class="mb-2"><small class="text-muted d-block">Name</small><strong><c:out value="${t.passengerName}"/></strong></div>
          <div class="row mb-3">
            <div class="col"><small class="text-muted d-block">Gender</small><c:out value="${t.gender}"/></div>
            <div class="col"><small class="text-muted d-block">Age</small><c:out value="${t.age}"/> years</div>
          </div>

          <h6 class="text-primary"><i class="bi bi-calendar-event"></i> Travel Information</h6>
          <div class="row mb-3">
            <div class="col"><small class="text-muted d-block">Journey Date</small><strong><c:out value="${t.journeyDate}"/></strong></div>
            <div class="col"><small class="text-muted d-block">Seat Number</small><span class="badge bg-info text-dark"><c:out value="${t.seatNo}"/></span></div>
          </div>

          <h6 class="text-primary"><i class="bi bi-bookmark"></i> Booking Details</h6>
          <div class="row">
            <div class="col"><small class="text-muted d-block">Booking ID</small>#<c:out value="${t.bookingId}"/></div>
            <div class="col">
              <small class="text-muted d-block">Status</small>
              <span class="badge ${t.status == 'BOOKED' ? 'bg-success' : 'bg-danger'}"><c:out value="${t.status}"/></span>
            </div>
          </div>
        </div>
        <c:if test="${t.cancellable}">
          <div class="card-footer bg-transparent text-end">
            <form action="${ctx}/user/bookings/${t.bookingId}/tickets/${t.pnrNo}/cancel" method="post"
                  onsubmit="return confirm('Cancel ticket PNR ${t.pnrNo}? The seat will be released.');">
              <button type="submit" class="btn btn-outline-danger btn-sm"><i class="bi bi-x-circle"></i> Cancel Ticket</button>
            </form>
          </div>
        </c:if>
      </div>
    </div>
  </c:forEach>
</div>

<div class="mt-4">
  <a href="${ctx}/user/bookings" class="btn btn-secondary"><i class="bi bi-arrow-left"></i> Back to My Bookings</a>
  <button type="button" class="btn btn-outline-primary" onclick="window.print()"><i class="bi bi-printer"></i> Print Tickets</button>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>