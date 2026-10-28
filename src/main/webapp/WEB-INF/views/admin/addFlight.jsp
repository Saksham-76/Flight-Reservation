<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Flight</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
</head>
<body>

<jsp:include page="../common/header.jsp" />

<div class="container mt-5 mb-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-7">

            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h3 class="mb-0">Add New Flight</h3>
                </div>

                <div class="card-body">

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger">
                            <c:out value="${errorMessage}" />
                        </div>
                    </c:if>

                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success">
                            <c:out value="${successMessage}" />
                        </div>
                    </c:if>

                    <form:form method="post"
                               action="${pageContext.request.contextPath}/admin/flights/add"
                               modelAttribute="flight">

                        <div class="mb-3">
                            <form:label path="flightNumber" cssClass="form-label">
                                Flight Number
                            </form:label>
                            <form:input path="flightNumber"
                                        cssClass="form-control"
                                        placeholder="e.g. AI101"
                                        required="required"
                                        maxlength="50" />
                            <form:errors path="flightNumber"
                                         cssClass="text-danger" />
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <form:label path="source" cssClass="form-label">
                                    Source
                                </form:label>
                                <form:input path="source"
                                            cssClass="form-control"
                                            placeholder="e.g. Kolkata"
                                            required="required"
                                            maxlength="50" />
                                <form:errors path="source"
                                             cssClass="text-danger" />
                            </div>

                            <div class="col-md-6 mb-3">
                                <form:label path="destination" cssClass="form-label">
                                    Destination
                                </form:label>
                                <form:input path="destination"
                                            cssClass="form-control"
                                            placeholder="e.g. Delhi"
                                            required="required"
                                            maxlength="50" />
                                <form:errors path="destination"
                                             cssClass="text-danger" />
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <form:label path="departureTime" cssClass="form-label">
                                    Departure Time
                                </form:label>
                                <form:input path="departureTime"
                                            type="datetime-local"
                                            cssClass="form-control"
                                            required="required" />
                                <form:errors path="departureTime"
                                             cssClass="text-danger" />
                            </div>

                            <div class="col-md-6 mb-3">
                                <form:label path="arrivalTime" cssClass="form-label">
                                    Arrival Time
                                </form:label>
                                <form:input path="arrivalTime"
                                            type="datetime-local"
                                            cssClass="form-control"
                                            required="required" />
                                <form:errors path="arrivalTime"
                                             cssClass="text-danger" />
                            </div>
                        </div>

                        <div class="mb-3">
                            <form:label path="pricePerSeat" cssClass="form-label">
                                Price Per Seat (₹)
                            </form:label>
                            <form:input path="pricePerSeat"
                                        type="number"
                                        step="0.01"
                                        min="0.01"
                                        cssClass="form-control"
                                        required="required" />
                            <form:errors path="pricePerSeat"
                                         cssClass="text-danger" />
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary">
                                Add Flight
                            </button>

                            <a href="${pageContext.request.contextPath}/admin/dashboard"
                               class="btn btn-secondary">
                                Cancel
                            </a>
                        </div>

                    </form:form>

                </div>
            </div>

        </div>
    </div>
</div>

</body>
</html>