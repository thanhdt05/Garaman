<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<c:set var="pageTitle" value="Lập hóa đơn nhập · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Nhập phụ tùng / Lập hóa đơn" scope="request" />
<c:set var="activeNav" value="purchase" scope="request" />
<c:set var="purchaseStep" value="2" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">NHÂN VIÊN KHO / NHẬP PHỤ TÙNG</div>
    <h1>Lập hóa đơn nhập</h1>
    <p>Tìm phụ tùng, chọn và nhập số lượng cùng đơn giá trên từng dòng.</p>
  </div>
  <button class="btn" type="submit" form="purchase-form"
          formaction="<c:url value='/warehouse/purchases/update'/>" name="destination" value="new-part">
    + Thêm phụ tùng mới
  </button>
</div>

<%@ include file="/WEB-INF/view/common/steps.jspf" %>

<!-- Thông tin chung hóa đơn nhập -->
<section class="panel">
  <div class="panel-head">
    <h2>Thông tin nhập hàng</h2>
    <a class="text-link" href="<c:url value='/warehouse/suppliers'/>" onclick="return confirm('Đổi nhà cung cấp sẽ bắt đầu lại phiên nhập. Bạn có chắc chắn?');">
      Đổi nhà cung cấp
    </a>
  </div>
  <div class="compact-info">
    <div>
      <small>NHÀ CUNG CẤP</small>
      <strong data-supplier-name><c:out value="${purchaseInvoice.supplier.name}"/></strong>
    </div>
    <div>
      <small>NHÂN VIÊN KHO</small>
      <strong><c:out value="${purchaseInvoice.warehouseStaff.fullName}"/> · NVK<fmt:formatNumber value="${purchaseInvoice.warehouseStaff.id}" pattern="000"/></strong>
    </div>
    <div>
      <small>ĐỊA CHỈ</small>
      <strong data-supplier-address><c:out value="${purchaseInvoice.supplier.address}"/></strong>
    </div>
    <div>
      <small>NGÀY NHẬP</small>
      <strong data-invoice-date>
        <c:out value="${purchaseDateDisplay}"/>
      </strong>
    </div>
  </div>
</section>

<!-- FORM DUY NHẤT BAO TOÀN BỘ PHẦN CHỌN VÀ DÒNG NHẬP ĐỂ BẢO TOÀN DỮ LIỆU ĐANG NHẬP -->
<form id="purchase-form" method="post" action="<c:url value='/warehouse/purchases/update'/>">
  <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
  <input type="hidden" name="requestCode" value="<c:out value='${requestCode}'/>">
  <input type="hidden" name="draftVersion" value="<c:out value='${draftVersion}'/>">
  <c:if test="${not empty focusPartId}">
    <input type="hidden" id="focusPartId" value="${focusPartId}">
  </c:if>

  <!-- Khung tìm kiếm và chọn phụ tùng -->
  <%@ include file="/WEB-INF/view/warehouse/parts-picker.jspf" %>

  <!-- Danh sách chi tiết các dòng nhập -->
  <section class="panel">
    <div class="panel-head">
      <h2>Các dòng phụ tùng nhập</h2>
      <span class="result-count" id="cart-count">
        <c:out value="${purchaseInvoice.invoiceDetails != null ? purchaseInvoice.invoiceDetails.size() : 0}"/> mặt hàng
      </span>
    </div>

    <div class="table-wrap">
      <table id="cart-table">
        <thead>
          <tr>
            <th>STT</th>
            <th>Phụ tùng</th>
            <th>Hãng</th>
            <th>ĐVT</th>
            <th class="number">Số lượng</th>
            <th class="number">Đơn giá nhập (₫)</th>
            <th class="number">Thành tiền (₫)</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="invoiceDetail" items="${purchaseInvoice.invoiceDetails}" varStatus="status">
            <c:set var="pid" value="${invoiceDetail.sparePart.id}"/>
            <tr id="row-part-${pid}" class="${focusPartId == pid ? 'highlight' : ''}">
              <td class="code">
                <input type="hidden" name="partId" value="${pid}">
                <fmt:formatNumber value="${status.index + 1}" pattern="00"/>
              </td>
              <td>
                <strong><c:out value="${invoiceDetail.sparePart.name}"/></strong>
                <small>PT<c:out value="${pid}"/></small>
              </td>
              <td><c:out value="${invoiceDetail.sparePart.manufacturer}"/></td>
              <td><c:out value="${invoiceDetail.sparePart.unit}"/></td>
              <td class="number">
                <c:set var="qtyKey" value="quantity_${pid}"/>
                <c:set var="qtyVal" value="${formValues[qtyKey] != null ? formValues[qtyKey] : invoiceDetail.quantity}"/>
                <c:set var="qtyErrorKey" value="error_quantity_${pid}"/>
                <c:set var="qtyErr" value="${formValues[qtyErrorKey]}"/>
                <input type="text" id="quantity_${pid}" name="quantity_${pid}"
                       class="table-input qty-input ${not empty qtyErr ? 'is-invalid' : ''}"
                       value="<c:out value='${qtyVal}'/>" maxlength="15">
                <c:if test="${not empty qtyErr}">
                  <p class="error" role="alert"><c:out value="${qtyErr}"/></p>
                </c:if>
              </td>
              <td class="number">
                <c:set var="priceKey" value="purchaseUnitPrice_${pid}"/>
                <c:set var="priceVal" value="${formValues[priceKey] != null ? formValues[priceKey] : invoiceDetail.purchaseUnitPrice}"/>
                <c:set var="priceErrorKey" value="error_purchaseUnitPrice_${pid}"/>
                <c:set var="priceErr" value="${formValues[priceErrorKey]}"/>
                <input type="text" id="purchaseUnitPrice_${pid}" name="purchaseUnitPrice_${pid}"
                       class="table-input price price-input ${not empty priceErr ? 'is-invalid' : ''}"
                       value="<c:out value='${priceVal}'/>" maxlength="18">
                <c:if test="${not empty priceErr}">
                  <p class="error" role="alert"><c:out value="${priceErr}"/></p>
                </c:if>
              </td>
              <td class="number line-total">
                <fmt:formatNumber value="${invoiceDetail.lineAmount}" type="currency" currencySymbol="₫" maxFractionDigits="2"/>
              </td>
              <td class="number">
                <button type="submit" class="icon-button"
                        formaction="<c:url value='/warehouse/purchases/remove-line'/>"
                        name="removePartId" value="${pid}"
                        title="Xóa phụ tùng này khỏi hóa đơn">
                  ✕
                </button>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>

    <c:if test="${empty purchaseInvoice.invoiceDetails}">
      <div id="cart-empty" class="empty">
        <strong>Chưa có phụ tùng trong hóa đơn</strong>
        Tìm theo tên ở phía trên và chọn phụ tùng cần nhập.
      </div>
    </c:if>

    <div class="total-row">
      <span>Tổng tiền hóa đơn</span>
      <strong id="grand-total" data-invoice-total>
        <fmt:formatNumber value="${purchaseInvoice.totalAmount != null ? purchaseInvoice.totalAmount : 0}" type="currency" currencySymbol="₫" maxFractionDigits="2"/>
      </strong>
    </div>
  </section>

  <c:if test="${not empty errorMessage}">
    <p class="error" id="cart-error" role="alert"><c:out value="${errorMessage}"/></p>
  </c:if>

  <!-- Thanh thao tác chính -->
  <div class="actions">
    <button class="btn left btn-quiet" id="cancel-purchase" type="submit"
            formaction="<c:url value='/warehouse/purchases/cancel'/>"
            onclick="return confirm('Bạn có chắc chắn muốn hủy bản nháp này? Mọi thông tin các dòng chưa xác nhận sẽ bị xóa.');">
      Hủy hóa đơn
    </button>
    <button type="submit" class="btn"
            formaction="<c:url value='/warehouse/purchases/update'/>">
      ↻ Cập nhật &amp; tính lại tiền
    </button>
    <a class="btn" href="#parts-picker">Chọn tiếp phụ tùng ↑</a>
    <button class="btn btn-primary" id="confirm-purchase" type="submit"
            formaction="<c:url value='/warehouse/purchases/confirm'/>"
            ${empty purchaseInvoice.invoiceDetails ? 'disabled' : ''}>
      Xác nhận hóa đơn →
    </button>
  </div>
</form>

<script src="<c:url value='/assets/js/purchase.js'/>"></script>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
