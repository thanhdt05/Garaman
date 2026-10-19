<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Trang chính khách hàng · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Khách hàng" scope="request" />
<c:set var="activeNav" value="customer-home" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">KHÔNG GIAN KHÁCH HÀNG</div>
    <h1>Xin chào, <c:out value="${currentUser.hoTen != null ? currentUser.hoTen : 'Quý khách'}" /></h1>
    <p>Thông tin dịch vụ và phụ tùng dành cho chiếc xe của bạn.</p>
  </div>
  <span class="badge neutral">Khách hàng</span>
</div>

<section class="hero">
  <div>
    <span class="mini-label">DỊCH VỤ &amp; PHỤ TÙNG</span>
    <h2>Hiểu rõ trước mỗi<br>lần chăm sóc xe.</h2>
    <p>Tìm dịch vụ và phụ tùng theo tên. Xem chi tiết nội dung, đơn giá và điều kiện bảo hành minh bạch.</p>
    <a class="btn btn-primary" id="btn-search-catalog" href="<c:url value='/customer/catalog'/>">
      Tìm dịch vụ / phụ tùng <span aria-hidden="true">→</span>
    </a>
  </div>
  <img src="<c:url value='/assets/images/garage.svg'/>" alt="Minh họa khu vực bảo dưỡng ô tô">
</section>

<div class="section-title">
  <h2>Bạn đang cần tìm gì?</h2>
  <span class="summary-label">Tra cứu thông tin tại gara</span>
</div>

<section class="panel">
  <div class="list-item">
    <div>
      <h3>Dịch vụ chăm sóc và sửa chữa</h3>
      <p>Nội dung thực hiện, đơn giá niêm yết, thời gian dự kiến và chính sách bảo hành.</p>
    </div>
    <a class="btn btn-small" href="<c:url value='/customer/catalog'/>">Tìm dịch vụ →</a>
  </div>
  <div class="list-item">
    <div>
      <h3>Phụ tùng ô tô chính hãng</h3>
      <p>Hãng sản xuất, quy cách thông số, dòng xe tương thích và giá bán công khai.</p>
    </div>
    <a class="btn btn-small" href="<c:url value='/customer/catalog'/>">Tìm phụ tùng →</a>
  </div>
</section>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
