<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Book Flight</title>
</head>
<body>

<h2>Book Your Flight</h2>

<c:if test="${not empty errorMessage}">
    <p style="color: red;"><c:out value="${errorMessage}"/></p>
</c:if>

<h3>Flight Details</h3>

<p>Flight Number: <c:out value="${flight.flightNumber}"/></p>
<p>From: <c:out value="${flight.source}"/></p>
<p>To: <c:out value="${flight.destination}"/></p>
<p>Departure: <c:out value="${flight.departureTime}"/></p>
<p>Arrival: <c:out value="${flight.arrivalTime}"/></p>
<p>Price per Seat: ₹<c:out value="${flight.pricePerSeat}"/></p>

<hr>

<form:form method="post"
           action="${pageContext.request.contextPath}/booking"
           modelAttribute="bookingForm">

    <input type="hidden" name="flightId" value="${flight.flightId}"/>

    <div>
        <label for="journeyDate">Journey Date:</label>
        <form:input path="journeyDate"
                    id="journeyDate"
                    type="date"
                    required="required"/>
        <form:errors path="journeyDate" cssStyle="color:red"/>
    </div>

    <br>

    <div>
        <label for="passengerCount">Number of Passengers:</label>
        <form:select path="passengerCount" id="passengerCount">
            <form:option value="1" label="1"/>
            <form:option value="2" label="2"/>
            <form:option value="3" label="3"/>
            <form:option value="4" label="4"/>
            <form:option value="5" label="5"/>
            <form:option value="6" label="6"/>
            <form:option value="7" label="7"/>
            <form:option value="8" label="8"/>
            <form:option value="9" label="9"/>
            <form:option value="10" label="10"/>
        </form:select>
        <form:errors path="passengerCount" cssStyle="color:red"/>
    </div>

    <br>

    <button type="submit">Continue to Passenger Details</button>

</form:form>

<br>

<a href="${pageContext.request.contextPath}/">Back to Home</a>

</body>
</html>