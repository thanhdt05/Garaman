<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/jspf/header.jspf" %>
<div class="page-heading"><div><div class="eyebrow">KHÁCH HÀNG / TRA CỨU</div><h1>Tìm dịch vụ &amp; phụ tùng</h1><p>Nhập tên hoặc một phần tên để tìm thông tin bạn cần.</p></div></div>
<section class="panel"><div class="panel-body">
  <form id="catalog-search" class="search-form" novalidate>
    <div class="field"><label for="loai">Loại thông tin</label><select id="loai" name="loai"><option value="all">Dịch vụ &amp; phụ tùng</option><option value="dichvu">Dịch vụ</option><option value="phutung">Phụ tùng</option></select></div>
    <div class="field"><label for="tuKhoa">Tên dịch vụ / phụ tùng</label><input id="tuKhoa" name="tuKhoa" placeholder="Ví dụ: lọc" maxlength="150" required aria-describedby="catalog-search-error"></div>
    <button class="btn btn-primary" type="submit">Tìm kiếm</button>
  </form><p class="error" id="catalog-search-error" role="alert" hidden></p>
</div></section>
<section class="panel">
  <div class="panel-head"><h2>Kết quả tìm kiếm</h2><span class="result-count" id="result-count">Chưa tìm kiếm</span></div>
  <div class="empty" id="search-initial"><strong>Bạn muốn tìm thông tin gì?</strong>Nhập tên dịch vụ hoặc phụ tùng và chọn Tìm kiếm.</div>
  <div class="table-wrap" id="search-results" hidden><table id="catalog-table">
    <thead><tr><th>Mã</th><th>Tên dịch vụ / phụ tùng</th><th>Loại</th><th class="number">Đơn giá / Giá bán</th><th></th></tr></thead>
    <tbody>
      <tr data-type="dichvu" data-key="Thay lọc dầu động cơ"><td class="code">DV1</td><td><strong>Thay lọc dầu động cơ</strong><small>Bảo dưỡng định kỳ</small></td><td><span class="badge neutral">Dịch vụ</span></td><td class="number">120.000 ₫</td><td class="number"><a class="text-link" href="gdChiTietDichVu4.jsp">Xem chi tiết →</a></td></tr>
      <tr data-type="phutung" data-key="Lọc dầu động cơ AF-01"><td class="code">PT1</td><td><strong>Lọc dầu động cơ AF-01</strong><small>AutoFilter · Đơn vị: cái</small></td><td><span class="badge">Phụ tùng</span></td><td class="number">150.000 ₫</td><td class="number"><a class="text-link" href="gdChiTietPhuTung4.jsp">Xem chi tiết →</a></td></tr>
    </tbody>
  </table></div>
  <div class="empty" id="search-empty" hidden><strong>Không tìm thấy kết quả</strong>Thử tên khác hoặc chọn lại loại thông tin.</div>
  <div class="panel-note">Chọn “Xem chi tiết” để xem thông tin và chính sách bảo hành của từng mục.</div>
</section>
<a class="btn btn-quiet" href="gdChinhKH4.jsp">← Về trang chính</a>
<%@ include file="WEB-INF/jspf/footer.jspf" %>
