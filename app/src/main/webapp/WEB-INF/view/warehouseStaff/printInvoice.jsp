<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<c:set var="pageTitle" value="Bản in hóa đơn HN${purchaseInvoice.id} · GaraMan" scope="request" />
<c:set var="breadcrumb" value="In hóa đơn HN${purchaseInvoice.id}" scope="request" />
<c:set var="activeNav" value="purchase" scope="request" />
<c:set var="purchaseStep" value="4" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">NHẬP PHỤ TÙNG / BẢN IN HN<c:out value="${purchaseInvoice.id}"/></div>
    <h1>Bản in hóa đơn nhập</h1>
    <p>In chứng từ để đối chiếu và giao cho nhà cung cấp.</p>
  </div>
  <a class="btn" href="<c:url value='/warehouse/purchases/view'><c:param name='id' value='${purchaseInvoice.id}'/></c:url>">
    ← Quay lại hóa đơn
  </a>
</div>

<%@ include file="/WEB-INF/view/common/steps.jspf" %>

<article class="invoice">
  <div class="invoice-header">
    <div>
      <h2>GaraMan<span style="color:#829bb5">.</span></h2>
      <p>HỆ THỐNG QUẢN LÝ GARA Ô TÔ</p>
    </div>
    <div style="text-align:right">
      <strong class="code">CHỨNG TỪ NHẬP PHỤ TÙNG</strong>
      <p>Số hóa đơn: <strong>HN<c:out value="${purchaseInvoice.id}"/></strong></p>
      <p>Ngày nhập: <span data-invoice-date><c:out value="${purchaseDateDisplay}"/></span></p>
    </div>
  </div>

  <div class="invoice-title">
    <h2>Hóa đơn nhập phụ tùng</h2>
    <p>Thông tin nhập hàng từ nhà cung cấp</p>
  </div>

  <div class="invoice-meta">
    <div>
      <small>NHÀ CUNG CẤP</small>
      <p><strong data-supplier-name><c:out value="${purchaseInvoice.supplierNameSnapshot}"/></strong></p>
    </div>
    <div>
      <small>THÔNG TIN NHẬP KHO</small>
      <p>Nhân viên: <strong><c:out value="${purchaseInvoice.warehouseStaffNameSnapshot}"/></strong></p>
      <p>Mã nhân viên: NVK<fmt:formatNumber value="${purchaseInvoice.warehouseStaff.id}" pattern="000"/></p>
      <p>Xác nhận: <span data-confirmation-time><c:out value="${confirmedAtDisplay}"/></span></p>
      <p>Thanh toán: <strong data-payment-status><c:out value="${purchaseInvoice.paymentStatus}"/></strong></p>
      <c:if test="${purchaseInvoice.paymentStatus == 'Đã thanh toán'}">
        <p data-payment-details>
          Đã trả: <span><fmt:formatNumber value="${purchaseInvoice.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="2"/></span><br>
          Thời điểm: <span><c:out value="${paidAtDisplay}"/></span>
        </p>
      </c:if>
    </div>
  </div>

  <%@ include file="/WEB-INF/view/warehouseStaff/invoice-lines.jspf" %>

  <div class="signatures">
    <div>
      <strong>Người lập hóa đơn</strong>
      <small>(Ký, ghi rõ họ tên)</small>
    </div>
    <div>
      <strong>Nhân viên kho</strong>
      <small>(Ký, ghi rõ họ tên)</small>
    </div>
    <div>
      <strong>Nhà cung cấp</strong>
      <small>(Ký, ghi rõ họ tên)</small>
    </div>
  </div>

  <div class="panel-note">
    Chứng từ được tạo và quản lý bởi Hệ thống phần mềm GaraMan · Kho phụ tùng ô tô
  </div>
</article>

<div class="invoice-actions">
  <a class="btn" href="<c:url value='/warehouse/purchases/view'><c:param name='id' value='${purchaseInvoice.id}'/></c:url>">
    ← Quay lại hóa đơn
  </a>
  <button class="btn btn-primary" id="print-invoice" type="button" onclick="printInvoice();">
    In
  </button>
</div>

<script src="<c:url value='/assets/js/print.js'/>"></script>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
