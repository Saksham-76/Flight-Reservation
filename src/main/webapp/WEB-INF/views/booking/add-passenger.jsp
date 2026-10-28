<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="Add Passenger" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center">
  <div class="col-md-7 col-lg-5">
    <div class="card shadow-sm"><div class="card-body">
      <h4 class="mb-0">Add Passenger <c:out value="${currentPassenger}"/></h4>
      <small class="text-muted">Remaining passengers: <c:out value="${remaining}"/></small>
      <hr>

      <form:form action="${ctx}/book/passenger" method="post" modelAttribute="passengerForm">
        <div class="mb-3">
          <label for="passengerName" class="form-label">Passenger Name *</label>
          <form:input path="passengerName" cssClass="form-control" cssErrorClass="form-control is-invalid"
                      placeholder="Enter full name"/>
          <form:errors path="passengerName" cssClass="invalid-feedback d-block"/>
        </div>
        <div class="mb-3">
          <label for="age" class="form-label">Age *</label>
          <form:input type="number" path="age" cssClass="form-control" cssErrorClass="form-control is-invalid"/>
          <form:errors path="age" cssClass="invalid-feedback d-block"/>
        </div>
        <div class="mb-3">
          <label for="gender" class="form-label">Gender *</label>
          <form:select path="gender" cssClass="form-select" cssErrorClass="form-select is-invalid">
            <form:option value="" label="Select Gender"/>
            <form:option value="Male" label="Male"/>
            <form:option value="Female" label="Female"/>
            <form:option value="Other" label="Other"/>
          </form:select>
          <form:errors path="gender" cssClass="invalid-feedback d-block"/>
        </div>
        <button type="submit" class="btn btn-primary w-100">
          <c:choose>
            <c:when test="${lastPassenger}">Complete Booking</c:when>
            <c:otherwise>Add Passenger &amp; Continue</c:otherwise>
          </c:choose>
        </button>
      </form:form>

      <div class="progress mt-4" style="height: 8px;">
        <div class="progress-bar" role="progressbar" style="width: ${progress}%"></div>
      </div>
      <small class="text-muted">Passenger <c:out value="${currentPassenger}"/> of <c:out value="${totalPassengers}"/></small>
    </div></div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>