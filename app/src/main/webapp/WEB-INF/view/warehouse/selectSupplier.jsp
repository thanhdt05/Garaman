<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Chọn nhà cung cấp · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Nhập phụ tùng / Chọn NCC" scope="request" />
<c:set var="activeNav" value="purchase" scope="request" />
<c:set var="purchaseStep" value="1" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">NHÂN VIÊN KHO / NHẬP PHỤ TÙNG</div>
    <h1>Chọn nhà cung cấp</h1>
    <p>Tìm theo tên để chọn nhà cung cấp cho lần nhập.</p>
  </div>
  <a class="btn" href="<c:url value='/warehouse/suppliers/new'/>">+ Thêm nhà cung cấp</a>
</div>

<%@ include file="/WEB-INF/view/common/steps.jspf" %>

<section class="panel">
  <div class="panel-body">
    <form id="supplier-search" class="inline-search" action="<c:url value='/warehouse/suppliers'/>" method="get" novalidate>
      <div class="field">
        <label for="supplierKeyword">Tên nhà cung cấp</label>
        <input id="supplierKeyword" name="keyword" placeholder="Ví dụ: Hòa Phát" required maxlength="150"
               value="<c:out value='${keyword}'/>" aria-describedby="supplier-search-error" autofocus>
      </div>
      <button class="btn btn-primary" type="submit" id="btn-search-supplier">Tìm kiếm</button>
    </form>
    <c:if test="${not empty errorMessage}">
      <p class="error" id="supplier-search-error" role="alert"><c:out value="${errorMessage}"/></p>
    </c:if>
  </div>
</section>

<section class="panel">
  <c:choose>
    <c:when test="${not daTim}">
      <div class="panel-head">
        <h2>Danh sách nhà cung cấp</h2>
        <span class="result-count" id="result-count">Chưa tìm kiếm</span>
      </div>
      <div class="empty" id="search-initial">
        <strong>Tìm nhà cung cấp cho lần nhập</strong>
        Nhập tên và chọn Tìm kiếm; thêm mới nếu chưa có nhà cung cấp cần chọn.
      </div>
    </c:when>

    <c:when test="${empty suppliers}">
      <div class="panel-head">
        <h2>Danh sách nhà cung cấp</h2>
        <span class="result-count" id="result-count">0 nhà cung cấp</span>
      </div>
      <div class="empty" id="search-empty">
        <strong>Không tìm thấy nhà cung cấp</strong>
        Thử tên khác hoặc chọn “+ Thêm nhà cung cấp”.
      </div>
    </c:when>

    <c:otherwise>
      <div class="panel-head">
        <h2>Danh sách nhà cung cấp</h2>
        <span class="result-count" id="result-count"><c:out value="${suppliers.size()}"/> nhà cung cấp</span>
      </div>
      <div class="table-wrap" id="search-results">
        <table id="supplier-table">
          <thead>
            <tr>
              <th>Mã</th>
              <th>Nhà cung cấp</th>
              <th>Liên hệ</th>
              <th>Trạng thái</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="supplier" items="${suppliers}">
              <tr class="${selectedId == supplier.id ? 'highlight' : ''}">
                <td class="code">NCC<c:out value="${supplier.id}"/></td>
                <td>
                  <strong><c:out value="${supplier.name}"/></strong>
                  <small><c:out value="${supplier.address}"/></small>
                </td>
                <td>
                  <c:out value="${supplier.phoneNumber}"/>
                  <c:if test="${not empty supplier.email}">
                    <small><c:out value="${supplier.email}"/></small>
                  </c:if>
                </td>
                <td><span class="badge"><c:out value="${supplier.status}"/></span></td>
                <td class="number">
                  <form action="<c:url value='/warehouse/purchases/start'/>" method="post" style="margin:0;">
                    <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                    <input type="hidden" name="supplierId" value="<c:out value='${supplier.id}'/>">
                    <button class="btn btn-small select-supplier" type="submit">
                      Chọn →
                    </button>
                  </form>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
      <div class="panel-note">
        Chọn nhà cung cấp để chuyển sang màn hình lập hóa đơn nhập.
      </div>
    </c:otherwise>
  </c:choose>
</section>

<a class="btn btn-quiet" href="<c:url value='/home'/>">← Về trang chính</a>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
