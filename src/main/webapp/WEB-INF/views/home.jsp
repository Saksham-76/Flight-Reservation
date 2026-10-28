<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="Home" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row g-4">
  <!-- Search Flights -->
  <div class="col-md-6">
    <h4><i class="bi bi-search"></i> Search Flights</h4>
    <div class="card shadow-sm"><div class="card-body">
      <form:form action="${ctx}/search" method="post" modelAttribute="searchForm">
        <div class="mb-3">
          <label for="source" class="form-label">From</label>
          <form:input path="source" cssClass="form-control" cssErrorClass="form-control is-invalid"
                      placeholder="Enter departure city"/>
          <form:errors path="source" cssClass="invalid-feedback d-block"/>
        </div>
        <div class="mb-3">
          <label for="destination" class="form-label">To</label>
          <form:input path="destination" cssClass="form-control" cssErrorClass="form-control is-invalid"
                      placeholder="Enter destination city"/>
          <form:errors path="destination" cssClass="invalid-feedback d-block"/>
        </div>
        <button type="submit" class="btn btn-primary"><i class="bi bi-search"></i> Search Flights</button>
      </form:form>
    </div></div>
  </div>

  <!-- User Actions: hidden once the user is logged in -->
  <c:if test="${empty sessionScope.loggedInUser}">
    <div class="col-md-6">
      <h4><i class="bi bi-person"></i> User Actions</h4>
      <div class="card shadow-sm"><div class="card-body">
        <p>Please login to book flights and access your account.</p>
        <a href="${ctx}/login" class="btn btn-primary w-100 mb-2"><i class="bi bi-box-arrow-in-right"></i> Login</a>
        <a href="${ctx}/signup" class="btn btn-outline-secondary w-100"><i class="bi bi-person-plus"></i> Sign Up</a>
      </div></div>
    </div>
  </c:if>
</div>

<!-- Search results -->
<c:if test="${searched}">
  <h4 class="mt-5"><i class="bi bi-airplane"></i> Available Flights</h4>
  <c:choose>
    <c:when test="${empty flights}">
      <div class="alert alert-warning">No flights found for this route.</div>
    </c:when>
    <c:otherwise>
      <div class="table-responsive">
        <table class="table table-hover bg-white shadow-sm align-middle">
          <thead class="table-light">
            <tr><th>Flight</th><th>Route</th><th>Schedule</th><th>Price</th><th>Action</th></tr>
          </thead>
          <tbody>
            <c:forEach var="f" items="${flights}">
              <tr>
                <td><c:out value="${f.flightNumber}"/></td>
                <td><c:out value="${f.source}"/> &rarr; <c:out value="${f.destination}"/></td>
                <td>Dep: <c:out value="${f.departureTime}"/><br>Arr: <c:out value="${f.arrivalTime}"/></td>
                <td>&#8377;<c:out value="${f.pricePerSeat}"/></td>
                <td><a href="${ctx}/book/${f.flightNumber}" class="btn btn-primary btn-sm"><i class="bi bi-ticket"></i> Book</a></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:otherwise>
  </c:choose>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>