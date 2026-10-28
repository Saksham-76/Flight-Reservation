<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="Book Flight" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center">
  <div class="col-md-7 col-lg-5">
    <h4>Book Flight</h4>

    <div class="alert alert-info">
      <strong>Flight Number:</strong> <c:out value="${flight.flightNumber}"/><br>
      <c:out value="${flight.source}"/> &rarr; <c:out value="${flight.destination}"/>
      &middot; Dep <c:out value="${flight.departureTime}"/>
      &middot; &#8377;<c:out value="${flight.pricePerSeat}"/> per seat
    </div>

    <c:if test="${not empty seatError}">
      <div class="alert alert-danger"><c:out value="${seatError}"/></div>
    </c:if>

    <div class="card shadow-sm"><div class="card-body">
      <form:form action="${ctx}/book/${flight.flightNumber}" method="post" modelAttribute="bookingForm">
        <div class="mb-3">
          <label for="journeyDate" class="form-label">Journey Date *</label>
          <form:input type="date" path="journeyDate" min="${today}"
                      cssClass="form-control" cssErrorClass="form-control is-invalid"/>
          <form:errors path="journeyDate" cssClass="invalid-feedback d-block"/>
        </div>
        <div class="mb-3">
          <label for="numberOfPassengers" class="form-label">Number of Passengers *</label>
          <form:input type="number" path="numberOfPassengers"
                      cssClass="form-control" cssErrorClass="form-control is-invalid"/>
          <div class="form-text">Maximum 10 passengers allowed</div>
          <form:errors path="numberOfPassengers" cssClass="invalid-feedback d-block"/>
        </div>
        <button type="submit" class="btn btn-primary w-100 mb-2">Book Now</button>
        <a href="${ctx}/user/dashboard" class="btn btn-secondary w-100">Back to Dashboard</a>
      </form:form>
    </div></div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>