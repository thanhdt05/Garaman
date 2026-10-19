<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/jspf/header.jspf" %>
<div class="page-heading"><div><div class="eyebrow">NHẬP PHỤ TÙNG / BẢN IN HN1</div><h1>Bản in hóa đơn nhập</h1><p>In chứng từ để đối chiếu và giao cho nhà cung cấp.</p></div><a class="btn" href="gdXacNhanHoaDon4.jsp">← Quay lại hóa đơn</a></div>
<%@ include file="WEB-INF/jspf/steps.jspf" %>
<article class="invoice">
  <div class="invoice-header"><div><h2>GaraMan<span style="color:#829bb5">.</span></h2><p>HỆ THỐNG QUẢN LÝ GARA Ô TÔ</p></div><div style="text-align:right"><strong class="code">CHỨNG TỪ NHẬP PHỤ TÙNG</strong><p>Số hóa đơn: <strong>HN1</strong></p><p>Ngày nhập: <span data-invoice-date>17/10/2026</span></p></div></div>
  <div class="invoice-title"><h2>Hóa đơn nhập phụ tùng</h2><p>Thông tin nhập hàng từ nhà cung cấp</p></div>
  <div class="invoice-meta">
    <div><small>NHÀ CUNG CẤP</small><p><strong data-supplier-name>Hòa Phát</strong></p></div>
    <div><small>THÔNG TIN NHẬP KHO</small><p>Nhân viên: <strong>Đàm Tiến Thành</strong></p><p>Mã nhân viên: NVK001</p><p>Xác nhận: <span data-confirmation-time>17/10/2026</span></p><p>Thanh toán: <strong data-payment-status>Chưa thanh toán</strong></p><p data-payment-details hidden>Đã trả: <span data-paid-amount></span><br>Thời điểm: <span data-payment-time></span></p></div>
  </div>
  <%@ include file="WEB-INF/jspf/invoice-lines.jspf" %>
  <div class="signatures"><div><strong>Người lập hóa đơn</strong><small>(Ký, ghi rõ họ tên)</small></div><div><strong>Nhân viên kho</strong><small>(Ký, ghi rõ họ tên)</small></div><div><strong>Nhà cung cấp</strong><small>(Ký, ghi rõ họ tên)</small></div></div>
  <div class="panel-note">Giao diện minh họa · Chưa phát sinh chứng từ hoặc giao dịch thực tế.</div>
</article>
<div class="invoice-actions"><a class="btn" href="gdXacNhanHoaDon4.jsp">← Quay lại hóa đơn</a><button class="btn btn-primary" id="print-invoice" type="button">In</button></div>
<%@ include file="WEB-INF/jspf/footer.jspf" %>
