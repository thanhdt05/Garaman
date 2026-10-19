<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/jspf/header.jspf" %>
<div class="page-heading">
  <div><div class="eyebrow">NHẬP PHỤ TÙNG / HÓA ĐƠN ĐÃ XÁC NHẬN</div><h1>Hóa đơn nhập đã xác nhận</h1><p>Đối chiếu chứng từ, in hóa đơn và ghi nhận thanh toán sau khi trả đủ tiền.</p></div>
  <span class="badge pending" data-payment-badge>Chưa thanh toán</span>
</div>
<%@ include file="WEB-INF/jspf/steps.jspf" %>
<div class="notice success" id="invoice-notice">Hóa đơn HN1 đã được xác nhận. Có thể in chứng từ và ghi nhận thanh toán.</div>
<section class="panel">
  <div class="panel-head"><h2>Thông tin hóa đơn</h2><span class="code">HN1</span></div>
  <div class="compact-info">
    <div><small>NHÀ CUNG CẤP</small><strong data-supplier-name>Hòa Phát</strong></div>
    <div><small>NGÀY NHẬP</small><strong data-invoice-date>17/10/2026</strong></div>
    <div><small>NHÂN VIÊN KHO</small><strong>Đàm Tiến Thành · NVK001</strong></div>
    <div><small>THỜI ĐIỂM XÁC NHẬN</small><strong data-confirmation-time>17/10/2026</strong></div>
  </div>
</section>
<section class="panel"><div class="panel-head"><h2>Chi tiết phụ tùng nhập</h2><span class="summary-label">Các dòng đã xác nhận</span></div>
  <%@ include file="WEB-INF/jspf/invoice-lines.jspf" %>
</section>
<section class="panel">
  <div class="panel-head"><h2>Thanh toán cho nhà cung cấp</h2><span class="badge pending" data-payment-badge>Chưa thanh toán</span></div>
  <div class="panel-body">
    <p class="article">Sau khi in chứng từ, đối chiếu với nhà cung cấp và thực tế trả đủ tiền, nhập số tiền đã trả để ghi nhận thanh toán.</p>
    <form id="payment-form" class="payment-form" novalidate>
      <div class="field"><label for="soTienDaTra">Số tiền đã trả (VNĐ)</label><input id="soTienDaTra" name="soTienDaTra" type="number" min="0" max="999999999999999" step="0.01" value="0" required aria-describedby="payment-error payment-help"><small id="payment-help">Thanh toán đủ một lần theo tổng tiền hóa đơn.</small></div>
      <button class="btn btn-primary" id="pay-invoice" type="submit">Đã thanh toán</button>
    </form>
    <p class="error" id="payment-error" role="alert" hidden></p>
    <p class="page-subtitle" id="payment-time-row" hidden>Thời điểm thanh toán: <strong data-payment-time></strong> · Đã trả: <strong data-paid-amount></strong></p>
  </div>
  <div class="panel-note">Ghi nhận thanh toán sau khi đã đối chiếu chứng từ và thực tế trả đủ tiền cho nhà cung cấp.</div>
</section>
<div class="actions"><a class="btn left" href="gdChinhNVKho4.jsp">← Về trang chính</a><a class="btn btn-primary" id="open-print" href="gdHoaDonNhap4.jsp">In hóa đơn nhập →</a></div>
<%@ include file="WEB-INF/jspf/footer.jspf" %>
