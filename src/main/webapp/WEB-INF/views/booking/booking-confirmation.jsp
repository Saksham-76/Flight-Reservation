<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Booking Confirmed" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card border-success shadow-sm mx-auto" style="max-width: 720px;">
  <div class="card-header bg-success text-white">
    <h5 class="mb-0"><i class="bi bi-check-circle-fill"></i> Booking Confirmed!</h5>
  </div>
  <div class="card-body text-center">
    <i class="bi bi-airplane-fill text-success display-4"></i>
    <h5 class="mt-3">Congratulations! Your flight is booked!</h5>

    <div class="alert alert-primary fw-bold mt-3">
      <i class="bi bi-ticket-perforated"></i> Booking ID: <c:out value="${booking.bookingId}"/>
    </div>

    <table class="table table-sm text-start w-auto mx-auto">
      <tr><th>Flight</th><td><c:out value="${booking.flightNumber}"/></td></tr>
      <tr><th>Route</th><td><c:out value="${booking.source}"/> &rarr; <c:out value="${booking.destination}"/></td></tr>
      <tr><th>Journey Date</th><td><c:out value="${booking.journeyDate}"/></td></tr>
      <tr><th>Passengers</th><td><c:out value="${booking.seats}"/></td></tr>
      <tr><th>Total</th><td>&#8377;<c:out value="${booking.totalPrice}"/></td></tr>
    </table>

    <div class="row my-4">
      <div class="col"><i class="bi bi-shield-check fs-3"></i><div class="fw-bold">Secured</div><small class="text-muted">Your booking is protected</small></div>
      <div class="col"><i class="bi bi-envelope-check fs-3"></i><div class="fw-bold">Email Sent</div><small class="text-muted">Confirmation details sent</small></div>
      <div class="col"><i class="bi bi-calendar-check fs-3"></i><div class="fw-bold">Ready</div><small class="text-muted">Check-in 24hrs before</small></div>
    </div>

    <div class="bg-light rounded p-3 text-start mb-4">
      <strong><i class="bi bi-info-circle"></i> Next Steps:</strong>
      <ul class="mb-0 mt-2">
        <li>Save your booking ID for future reference</li>
        <li>Check your email for detailed itinerary</li>
        <li>Arrive at airport 2 hours before departure</li>
        <li>Complete online check-in 24 hours before flight</li>
      </ul>
    </div>

    <a href="${ctx}/user/dashboard" class="btn btn-success"><i class="bi bi-house"></i> Go to Dashboard</a>
    <button type="button" class="btn btn-outline-secondary" onclick="window.print()">
      <i class="bi bi-printer"></i> Print Confirmation
    </button>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>