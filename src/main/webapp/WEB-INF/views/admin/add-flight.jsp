<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="Add Flight" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center">
  <div class="col-lg-9">
    <h4><i class="bi bi-airplane"></i> Add Flight</h4>

    <c:if test="${not empty successMessage}">
      <div class="alert alert-success"><c:out value="${successMessage}"/></div>
    </c:if>

    <div class="card shadow-sm"><div class="card-body">
      <form:form action="${ctx}/admin/flights/add" method="post" modelAttribute="addFlightForm">
        <div class="row g-3">
          <div class="col-md-6">
            <label for="flightNumber" class="form-label">Flight Number</label>
            <form:input path="flightNumber" cssClass="form-control" cssErrorClass="form-control is-invalid"
                        placeholder="Enter flight number"/>
            <form:errors path="flightNumber" cssClass="invalid-feedback d-block"/>
          </div>
          <div class="col-md-6">
            <label for="capacity" class="form-label">Capacity</label>
            <form:input type="number" path="capacity" cssClass="form-control" cssErrorClass="form-control is-invalid"/>
            <form:errors path="capacity" cssClass="invalid-feedback d-block"/>
          </div>

          <div class="col-md-6">
            <label for="source" class="form-label">Source</label>
            <form:input path="source" cssClass="form-control" cssErrorClass="form-control is-invalid"
                        placeholder="Enter source city"/>
            <form:errors path="source" cssClass="invalid-feedback d-block"/>
          </div>
          <div class="col-md-6">
            <label for="destination" class="form-label">Destination</label>
            <form:input path="destination" cssClass="form-control" cssErrorClass="form-control is-invalid"
                        placeholder="Enter destination city"/>
            <form:errors path="destination" cssClass="invalid-feedback d-block"/>
          </div>

          <div class="col-md-6">
            <label for="departureTime" class="form-label">Departure Time</label>
            <form:input type="time" path="departureTime" cssClass="form-control" cssErrorClass="form-control is-invalid"/>
            <form:errors path="departureTime" cssClass="invalid-feedback d-block"/>
          </div>
          <div class="col-md-6">
            <label for="arrivalTime" class="form-label">Arrival Time</label>
            <form:input type="time" path="arrivalTime" cssClass="form-control" cssErrorClass="form-control is-invalid"/>
            <form:errors path="arrivalTime" cssClass="invalid-feedback d-block"/>
          </div>

          <div class="col-md-6">
            <label for="pricePerSeat" class="form-label">Price per Seat</label>
            <div class="input-group">
              <span class="input-group-text">&#8377;</span>
              <form:input type="number" step="0.01" path="pricePerSeat" cssClass="form-control"
                          cssErrorClass="form-control is-invalid" placeholder="0.00"/>
            </div>
            <form:errors path="pricePerSeat" cssClass="text-danger small d-block"/>
          </div>
        </div>
        <button type="submit" class="btn btn-success w-100 mt-4"><i class="bi bi-plus-circle"></i> Add Flight</button>
      </form:form>
    </div></div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>