<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="My Bookings" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<h4><i class="bi bi-ticket-perforated"></i> My Bookings</h4>

<c:if test="${not empty successMessage}">
  <div class="alert alert-success"><c:out value="${successMessage}"/></div>
</c:if>
<c:if test="${not empty errorMessage}">
  <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
</c:if>

<c:choose>
  <c:when test="${empty bookings}">
    <div class="alert alert-info">
      You have no bookings yet. <a href="${ctx}/" class="alert-link">Find flights</a>
    </div>
  </c:when>
  <c:otherwise>
    <div class="table-responsive">
      <table class="table table-hover bg-white shadow-sm align-middle">
        <thead class="table-light">
          <tr>
            <th>Booking ID</th><th>Flight Number</th><th>Journey Date</th>
            <th>Status</th><th>Action</th><th>Cancel</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="b" items="${bookings}">
            <tr>
              <td><c:out value="${b.bookingId}"/></td>
              <td><c:out value="${b.flightNumber}"/></td>
              <td><c:out value="${b.journeyDate}"/></td>
              <td>
                <span class="badge ${b.status == 'BOOKED' ? 'bg-success' : 'bg-danger'}">
                  <c:out value="${b.status}"/>
                </span>
              </td>
              <td>
                <a href="${ctx}/user/bookings/${b.bookingId}/tickets" class="btn btn-info btn-sm text-white">
                  <i class="bi bi-eye"></i> View Tickets
                </a>
              </td>
              <td>
                <c:choose>
                  <c:when test="${b.cancellable}">
                    <form action="${ctx}/user/bookings/${b.bookingId}/cancel" method="post" class="d-inline"
                          onsubmit="return confirm('Cancel booking #${b.bookingId}? All its seats will be released.');">
                      <button type="submit" class="btn btn-danger btn-sm"><i class="bi bi-x-circle"></i> Cancel</button>
                    </form>
                  </c:when>
                  <c:otherwise>
                    <button type="button" class="btn btn-secondary btn-sm" disabled><i class="bi bi-x-circle"></i> Cancel</button>
                  </c:otherwise>
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