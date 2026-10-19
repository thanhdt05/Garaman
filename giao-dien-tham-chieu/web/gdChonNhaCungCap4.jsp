<%@ page pageEncoding="UTF-8" %>
<%@ include file="WEB-INF/jspf/header.jspf" %>
<div class="page-heading"><div><div class="eyebrow">NHÂN VIÊN KHO / NHẬP PHỤ TÙNG</div><h1>Chọn nhà cung cấp</h1><p>Tìm theo tên để chọn nhà cung cấp cho lần nhập.</p></div><a class="btn" href="gdThemNhaCungCap4.jsp">+ Thêm nhà cung cấp</a></div>
<%@ include file="WEB-INF/jspf/steps.jspf" %>
<section class="panel"><div class="panel-body"><form id="supplier-search" class="inline-search" novalidate><div class="field"><label for="tenNCC">Tên nhà cung cấp</label><input id="tenNCC" name="ten" placeholder="Ví dụ: Hòa Phát" required maxlength="150" aria-describedby="supplier-search-error"></div><button class="btn btn-primary" type="submit">Tìm kiếm</button></form><p class="error" id="supplier-search-error" role="alert" hidden></p></div></section>
<section class="panel"><div class="panel-head"><h2>Danh sách nhà cung cấp</h2><span class="result-count" id="result-count">Chưa tìm kiếm</span></div>
<div class="empty" id="search-initial"><strong>Tìm nhà cung cấp cho lần nhập</strong>Nhập tên và chọn Tìm kiếm; thêm mới nếu chưa có nhà cung cấp cần chọn.</div>
<div class="table-wrap" id="search-results" hidden><table id="supplier-table"><thead><tr><th>Mã</th><th>Nhà cung cấp</th><th>Liên hệ</th><th>Trạng thái</th><th></th></tr></thead><tbody>
<tr data-key="Hòa Phát" data-id="NCC1" data-name="Hòa Phát" data-address="Hà Nội" data-phone="0900000001" data-email="kho@hoaphat.example"><td class="code">NCC1</td><td><strong>Hòa Phát</strong><small>Hà Nội</small></td><td>0900000001<small>kho@hoaphat.example</small></td><td><span class="badge">Đang hợp tác</span></td><td class="number"><button class="btn btn-small select-supplier" type="button">Chọn →</button></td></tr>
</tbody></table></div><div class="empty" id="search-empty" hidden><strong>Không tìm thấy nhà cung cấp</strong>Thử tên khác hoặc chọn “Thêm nhà cung cấp”.</div><div class="panel-note">Chọn nhà cung cấp để chuyển sang màn hình lập hóa đơn nhập.</div></section><a class="btn btn-quiet" href="gdChinhNVKho4.jsp">← Về trang chính</a>
<%@ include file="WEB-INF/jspf/footer.jspf" %>
