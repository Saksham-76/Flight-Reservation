<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="Login" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center">
  <div class="col-md-6 col-lg-5">
    <h4><i class="bi bi-box-arrow-in-right"></i> Login</h4>

    <c:if test="${not empty successMessage}">
      <div class="alert alert-success"><c:out value="${successMessage}"/></div>
    </c:if>
    <c:if test="${not empty loginError}">
      <div class="alert alert-danger"><c:out value="${loginError}"/></div>
    </c:if>

    <div class="card shadow-sm"><div class="card-body">
      <form:form action="${ctx}/login" method="post" modelAttribute="loginForm">
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
        <button type="submit" class="btn btn-primary w-100"><i class="bi bi-box-arrow-in-right"></i> Login</button>
      </form:form>
      <p class="text-center mt-3 mb-0">Don't have an account? <a href="${ctx}/signup">Sign up here</a></p>
    </div></div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>