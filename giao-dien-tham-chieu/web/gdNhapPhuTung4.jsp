<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/jspf/header.jspf" %>
<div class="page-heading">
  <div><div class="eyebrow">NHÂN VIÊN KHO / NHẬP PHỤ TÙNG</div><h1>Lập hóa đơn nhập</h1><p>Tìm phụ tùng, chọn và nhập số lượng cùng đơn giá trên từng dòng.</p></div>
  <a class="btn" href="gdThemPhuTung4.jsp">+ Thêm phụ tùng mới</a>
</div>
<%@ include file="WEB-INF/jspf/steps.jspf" %>
<section class="panel">
  <div class="panel-head"><h2>Thông tin nhập hàng</h2><a class="text-link" href="gdChonNhaCungCap4.jsp">Đổi nhà cung cấp</a></div>
  <div class="compact-info">
    <div><small>NHÀ CUNG CẤP</small><strong data-supplier-name>Hòa Phát</strong></div>
    <div><small>NHÂN VIÊN KHO</small><strong>Đàm Tiến Thành · NVK001</strong></div>
    <div><small>ĐỊA CHỈ</small><strong data-supplier-address>Hà Nội</strong></div>
    <div><small>NGÀY NHẬP</small><strong data-invoice-date>17/10/2026</strong></div>
  </div>
</section>
<%@ include file="WEB-INF/jspf/parts-picker.jspf" %>
<form id="purchase-form">
  <section class="panel">
    <div class="panel-head"><h2>Các dòng phụ tùng nhập</h2><span class="result-count" id="cart-count">0 mặt hàng</span></div>
    <div class="table-wrap"><table id="cart-table">
      <thead><tr><th>STT</th><th>Phụ tùng</th><th>Hãng</th><th>ĐVT</th><th class="number">Số lượng</th><th class="number">Đơn giá nhập (₫)</th><th class="number">Thành tiền (₫)</th><th></th></tr></thead>
      <tbody></tbody>
    </table></div>
    <div id="cart-empty" class="empty"><strong>Chưa có phụ tùng trong hóa đơn</strong>Tìm theo tên ở phía trên và chọn phụ tùng cần nhập.</div>
    <div class="total-row"><span>Tổng tiền hóa đơn</span><strong data-invoice-total>0 ₫</strong></div>
  </section>
  <p class="error" id="cart-error" role="alert" hidden></p>
  <div class="actions"><button class="btn left" id="cancel-purchase" type="button">Hủy hóa đơn</button><a class="btn" href="#parts-picker">Chọn tiếp phụ tùng ↑</a><button class="btn btn-primary" id="confirm-purchase" type="submit" disabled>Xác nhận hóa đơn →</button></div>
</form>
<%@ include file="WEB-INF/jspf/footer.jspf" %>
