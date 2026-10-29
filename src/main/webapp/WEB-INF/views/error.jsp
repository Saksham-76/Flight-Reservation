<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Error ${errorStatus}" scope="request"/>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="row justify-content-center">
  <div class="col-md-8 col-lg-6">
    <div class="card shadow-sm text-center">
      <div class="card-body py-5">
        <i class="bi bi-exclamation-triangle text-danger display-3"></i>
        <h1 class="display-4 fw-bold mt-3"><c:out value="${errorStatus}"/></h1>
        <h4><c:out value="${errorTitle}"/></h4>
        <p class="text-muted mt-3"><c:out value="${errorMessage}"/></p>

        <c:if test="${not empty errorId}">
          <p class="small text-muted">
            Reference: <code><c:out value="${errorId}"/></code>
            <br>Quote this if you contact support.
          </p>
        </c:if>

        <div class="mt-4">
          <a href="${ctx}/" class="btn btn-primary"><i class="bi bi-house"></i> Home</a>
          <c:if test="${not empty loggedUser}">
            <a href="${ctx}/${loggedUser.role == 'Admin' ? 'admin' : 'user'}/dashboard"
               class="btn btn-outline-primary"><i class="bi bi-speedometer2"></i> Dashboard</a>
          </c:if>
          <a href="javascript:history.back()" class="btn btn-outline-secondary"><i class="bi bi-arrow-left"></i> Go Back</a>
        </div>
      </div>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>