<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<c:set var="pageTitle" value="Hóa đơn nhập HN${purchaseInvoice.id} · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Hóa đơn HN${purchaseInvoice.id}" scope="request" />
<c:set var="activeNav" value="purchase" scope="request" />
<c:set var="purchaseStep" value="3" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">NHẬP PHỤ TÙNG / HÓA ĐƠN ĐÃ XÁC NHẬN</div>
    <h1>Hóa đơn nhập đã xác nhận</h1>
    <p>Đối chiếu chứng từ, in hóa đơn và ghi nhận thanh toán sau khi trả đủ tiền.</p>
  </div>
  <c:choose>
    <c:when test="${purchaseInvoice.paymentStatus == 'Đã thanh toán'}">
      <span class="badge" data-payment-badge>Đã thanh toán</span>
    </c:when>
    <c:otherwise>
      <span class="badge pending" data-payment-badge>Chưa thanh toán</span>
    </c:otherwise>
  </c:choose>
</div>

<%@ include file="/WEB-INF/view/common/steps.jspf" %>

<div class="notice success" id="invoice-notice">
  Hóa đơn HN<c:out value="${purchaseInvoice.id}"/> đã được xác nhận. Có thể in chứng từ và ghi nhận thanh toán.
</div>

<section class="panel">
  <div class="panel-head">
    <h2>Thông tin hóa đơn</h2>
    <span class="code">HN<c:out value="${purchaseInvoice.id}"/></span>
  </div>
  <div class="compact-info">
    <div>
      <small>NHÀ CUNG CẤP</small>
      <strong data-supplier-name><c:out value="${purchaseInvoice.supplierNameSnapshot}"/></strong>
    </div>
    <div>
      <small>NGÀY NHẬP</small>
      <strong data-invoice-date><c:out value="${purchaseDateDisplay}"/></strong>
    </div>
    <div>
      <small>NHÂN VIÊN KHO</small>
      <strong><c:out value="${purchaseInvoice.warehouseStaffNameSnapshot}"/> · NVK<fmt:formatNumber value="${purchaseInvoice.warehouseStaff.id}" pattern="000"/></strong>
    </div>
    <div>
      <small>THỜI ĐIỂM XÁC NHẬN</small>
      <strong data-confirmation-time><c:out value="${confirmedAtDisplay}"/></strong>
    </div>
  </div>
</section>

<section class="panel">
  <div class="panel-head">
    <h2>Chi tiết phụ tùng nhập</h2>
    <span class="summary-label">Các dòng đã xác nhận</span>
  </div>
  <%@ include file="/WEB-INF/view/warehouse/invoice-lines.jspf" %>
</section>

<!-- Khung thanh toán cho nhà cung cấp -->
<section class="panel">
  <div class="panel-head">
    <h2>Thanh toán cho nhà cung cấp</h2>
    <c:choose>
      <c:when test="${purchaseInvoice.paymentStatus == 'Đã thanh toán'}">
        <span class="badge" data-payment-badge>Đã thanh toán</span>
      </c:when>
      <c:otherwise>
        <span class="badge pending" data-payment-badge>Chưa thanh toán</span>
      </c:otherwise>
    </c:choose>
  </div>

  <div class="panel-body">
    <c:choose>
      <c:when test="${purchaseInvoice.paymentStatus == 'Đã thanh toán'}">
        <p class="page-subtitle" id="payment-time-row">
          Thời điểm thanh toán: <strong data-payment-time><c:out value="${paidAtDisplay}"/></strong> ·
          Đã trả: <strong data-paid-amount><fmt:formatNumber value="${purchaseInvoice.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="2"/></strong>
        </p>
      </c:when>

      <c:otherwise>
        <c:choose>
          <c:when test="${canRecordPayment}">
            <p class="article">Sau khi in chứng từ, đối chiếu với nhà cung cấp và thực tế trả đủ tiền, nhập số tiền đã trả để ghi nhận thanh toán.</p>
            <form id="payment-form" class="payment-form" action="<c:url value='/warehouse/purchases/pay'/>" method="post" novalidate>
              <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
              <input type="hidden" name="id" value="<c:out value='${purchaseInvoice.id}'/>">

              <div class="field">
                <label for="amountPaid">Số tiền đã trả (VNĐ)</label>
                <input id="amountPaid" name="amountPaid" type="number" min="0" max="999999999999999" step="0.01"
                       value="<c:out value='${formValues.amountPaid != null ? formValues.amountPaid : "0"}'/>"
                       required aria-describedby="payment-error payment-help">
                <small id="payment-help">Thanh toán đủ một lần theo tổng tiền hóa đơn.</small>
              </div>
              <button class="btn btn-primary" id="pay-invoice" type="submit">Đã thanh toán</button>
            </form>
            <c:if test="${not empty errorMessage}">
              <p class="error" id="payment-error" role="alert"><c:out value="${errorMessage}"/></p>
            </c:if>
          </c:when>

          <c:otherwise>
            <p class="page-subtitle">
              Chỉ nhân viên đã lập hóa đơn (<strong><c:out value="${purchaseInvoice.warehouseStaffNameSnapshot}"/></strong>) mới có quyền ghi nhận thanh toán cho chứng từ này.
            </p>
          </c:otherwise>
        </c:choose>
      </c:otherwise>
    </c:choose>
  </div>
  <div class="panel-note">
    Ghi nhận thanh toán sau khi đã đối chiếu chứng từ và thực tế trả đủ tiền cho nhà cung cấp.
  </div>
</section>

<div class="actions">
  <a class="btn left" href="<c:url value='/home'/>">← Về trang chính</a>
  <a class="btn btn-primary" id="open-print" href="<c:url value='/warehouse/purchases/print'><c:param name='id' value='${purchaseInvoice.id}'/></c:url>">
    In hóa đơn nhập →
  </a>
</div>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
