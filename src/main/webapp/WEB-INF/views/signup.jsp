<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="Sign Up" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center">
  <div class="col-md-6 col-lg-5">
    <h4><i class="bi bi-person-plus"></i> Sign Up</h4>

    <c:if test="${not empty successMessage}">
      <div class="alert alert-success"><c:out value="${successMessage}"/></div>
    </c:if>

    <div class="card shadow-sm"><div class="card-body">
      <form:form action="${ctx}/signup" method="post" modelAttribute="signupForm">
        <div class="mb-3">
          <label for="email" class="form-label">Email</label>
          <form:input path="email" cssClass="form-control" cssErrorClass="form-control is-invalid"
                      placeholder="Enter your email"/>
          <form:errors path="email" cssClass="invalid-feedback d-block"/>
        </div>
        <div class="mb-3">
          <label for="password" class="form-label">Password</label>
          <form:password path="password" cssClass="form-control" cssErrorClass="form-control is-invalid"
                         placeholder="Enter your password"/>
          <form:errors path="password" cssClass="invalid-feedback d-block"/>
        </div>
        <div class="mb-3">
          <label for="role" class="form-label">Role</label>
          <form:select path="role" cssClass="form-select" cssErrorClass="form-select is-invalid">
            <form:option value="" label="-- Select Role --"/>
            <form:option value="User" label="User"/>
            <form:option value="Admin" label="Admin"/>
          </form:select>
          <form:errors path="role" cssClass="invalid-feedback d-block"/>
        </div>
        <button type="submit" class="btn btn-primary w-100"><i class="bi bi-person-plus"></i> Sign Up</button>
      </form:form>
      <p class="text-center mt-3 mb-0">Already have an account? <a href="${ctx}/login">Login here</a></p>
    </div></div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>