<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="Home" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<!-- ===== Hero ===== -->
<section class="hero">
  <div class="row align-items-center g-4">
    <div class="col-lg-6">
      <span class="hero-pill"><i class="bi bi-stars"></i> Fly smarter, book faster</span>
      <h1 class="display-5 mt-3">Where to next?</h1>
      <p class="lead mb-0">Search flights, book in minutes and manage every trip in one place.</p>
    </div>
    <div class="col-lg-6 text-center">
      <svg class="hero-route" viewBox="0 0 400 200" aria-hidden="true">
        <path id="routePath" class="route-line" d="M30 160 Q 200 -20 370 140"
              fill="none" stroke="#F4A261" stroke-width="3" stroke-dasharray="8 10" stroke-linecap="round"/>
        <circle class="pulse-dot" cx="30" cy="160" r="8" fill="#F4A261"/>
        <circle class="pulse-dot delay" cx="370" cy="140" r="8" fill="#E76F51"/>
        <g>
          <path d="M14 0 L-12 -10 L-6 0 L-12 10 Z" fill="#FFFFFF"/>
          <animateMotion dur="6s" repeatCount="indefinite" rotate="auto">
            <mpath href="#routePath"/>
          </animateMotion>
        </g>
      </svg>
    </div>
  </div>
</section>

<!-- ===== Search + user actions (overlap the hero) ===== -->
<div class="row g-4 search-wrap">
  <div class="${empty loggedUser ? 'col-lg-7' : 'col-lg-8 mx-auto'}">
    <div class="card search-card"><div class="card-body p-4">
      <h4 class="mb-3"><i class="bi bi-search text-danger"></i> Search Flights</h4>
      <form:form action="${ctx}/search" method="post" modelAttribute="searchForm">
        <div class="row g-3">
          <div class="col-md-6">
            <label for="source" class="form-label">From</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-geo-alt-fill"></i></span>
              <form:input path="source" cssClass="form-control" cssErrorClass="form-control is-invalid"
                          placeholder="Enter departure city"/>
            </div>
            <form:errors path="source" cssClass="invalid-feedback d-block"/>
          </div>
          <div class="col-md-6">
            <label for="destination" class="form-label">To</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-flag-fill"></i></span>
              <form:input path="destination" cssClass="form-control" cssErrorClass="form-control is-invalid"
                          placeholder="Enter destination city"/>
            </div>
            <form:errors path="destination" cssClass="invalid-feedback d-block"/>
          </div>
          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100 py-2"><i class="bi bi-search"></i> Search Flights</button>
          </div>
        </div>
      </form:form>
    </div></div>
  </div>

  <c:if test="${empty loggedUser}">
    <div class="col-lg-5">
      <div class="card hover-card hc-teal h-100"><div class="card-body p-4 text-center d-flex flex-column justify-content-center">
        <div class="icon-circle"><i class="bi bi-person-heart"></i></div>
        <h4>User Actions</h4>
        <p class="text-muted">Please login to book flights and access your account.</p>
        <a href="${ctx}/login" class="btn btn-primary w-100 mb-2"><i class="bi bi-box-arrow-in-right"></i> Login</a>
        <a href="${ctx}/signup" class="btn btn-secondary w-100"><i class="bi bi-person-plus"></i> Sign Up</a>
      </div></div>
    </div>
  </c:if>
</div>

<!-- ===== Search results ===== -->
<c:if test="${searched}">
  <h4 class="mt-5 mb-3"><i class="bi bi-airplane text-danger"></i> Available Flights</h4>
  <c:choose>
    <c:when test="${empty flights}">
      <div class="alert alert-warning">No flights found for this route.</div>
    </c:when>
    <c:otherwise>
      <div class="table-responsive">
        <table class="table table-hover bg-white align-middle mb-0">
          <thead class="table-light">
            <tr><th>Flight</th><th>Route</th><th>Schedule</th><th>Price</th><th>Action</th></tr>
          </thead>
          <tbody>
            <c:forEach var="f" items="${flights}">
              <tr>
                <td><span class="badge bg-info text-dark"><c:out value="${f.flightNumber}"/></span></td>
                <td><c:out value="${f.source}"/> &rarr; <c:out value="${f.destination}"/></td>
                <td>Dep: <c:out value="${f.departureTime}"/><br>Arr: <c:out value="${f.arrivalTime}"/></td>
                <td class="fw-bold">&#8377;<c:out value="${f.pricePerSeat}"/></td>
                <td><a href="${ctx}/book/${f.flightNumber}" class="btn btn-primary btn-sm"><i class="bi bi-ticket"></i> Book</a></td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:otherwise>
  </c:choose>
</c:if>

<!-- ===== Feature cards (landing view only) ===== -->
<c:if test="${not searched}">
  <div class="row g-4 mt-4 text-center">
    <div class="col-md-4">
      <div class="card hover-card hc-teal h-100"><div class="card-body p-4">
        <div class="icon-circle"><i class="bi bi-lightning-charge-fill"></i></div>
        <h5>Instant Booking</h5>
        <p class="text-muted mb-0">Pick a date, add your passengers and get a booking ID in moments.</p>
      </div></div>
    </div>
    <div class="col-md-4">
      <div class="card hover-card hc-coral h-100"><div class="card-body p-4">
        <div class="icon-circle"><i class="bi bi-arrow-repeat"></i></div>
        <h5>Flexible Cancellation</h5>
        <p class="text-muted mb-0">Cancel a whole booking or a single ticket. Seats are released right away.</p>
      </div></div>
    </div>
    <div class="col-md-4">
      <div class="card hover-card hc-orange h-100"><div class="card-body p-4">
        <div class="icon-circle"><i class="bi bi-shield-lock-fill"></i></div>
        <h5>Secure Accounts</h5>
        <p class="text-muted mb-0">Passwords are hashed and every booking stays private to its owner.</p>
      </div></div>
    </div>
  </div>
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>