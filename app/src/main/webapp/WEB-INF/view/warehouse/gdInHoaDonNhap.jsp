<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<c:set var="pageTitle" value="Bản in hóa đơn HN${hoaDon.id} · GaraMan" scope="request" />
<c:set var="breadcrumb" value="In hóa đơn HN${hoaDon.id}" scope="request" />
<c:set var="activeNav" value="purchase" scope="request" />
<c:set var="buocNhap" value="4" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">NHẬP PHỤ TÙNG / BẢN IN HN<c:out value="${hoaDon.id}"/></div>
    <h1>Bản in hóa đơn nhập</h1>
    <p>In chứng từ lưu kho để đối chiếu và xác nhận bàn giao phụ tùng với nhà cung cấp.</p>
  </div>
  <a class="btn" href="<c:url value='/warehouse/purchases/view'><c:param name='id' value='${hoaDon.id}'/></c:url>">
    ← Quay lại hóa đơn
  </a>
</div>

<%@ include file="/WEB-INF/view/common/steps.jspf" %>

<article class="invoice" style="background: #fff; padding: 32px; border-radius: 8px; border: 1px solid var(--border); box-shadow: var(--shadow-sm); margin-bottom: 24px;">
  <div class="invoice-header" style="display: flex; justify-content: space-between; border-bottom: 2px solid var(--border); padding-bottom: 20px; margin-bottom: 24px;">
    <div>
      <h2 style="margin: 0; font-size: 24px; font-weight: 800; color: var(--primary);">GaraMan<span>.</span></h2>
      <p style="margin: 4px 0 0 0; color: var(--muted); font-size: 12px; letter-spacing: 1px;">HỆ THỐNG QUẢN LÝ GARA Ô TÔ</p>
    </div>
    <div style="text-align: right;">
      <strong class="code" style="font-size: 14px; color: var(--primary);">CHỨNG TỪ NHẬP PHỤ TÙNG</strong>
      <p style="margin: 4px 0 0 0; font-size: 13px;">Số chứng từ: <strong>HN<c:out value="${hoaDon.id}"/></strong></p>
      <p style="margin: 2px 0 0 0; font-size: 12px; color: var(--muted);">Thời điểm xác nhận: <c:out value="${hoaDon.thoiDiemXacNhan}"/></p>
    </div>
  </div>

  <div class="invoice-title" style="text-align: center; margin-bottom: 28px;">
    <h2 style="margin: 0; font-size: 20px; text-transform: uppercase; letter-spacing: 0.5px;">Hóa đơn nhập phụ tùng</h2>
    <p style="margin: 4px 0 0 0; color: var(--muted); font-size: 13px;">Thông tin giao nhận phụ tùng từ nhà cung cấp vào kho gara</p>
  </div>

  <div class="invoice-meta" style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px; margin-bottom: 24px; background: var(--bg-alt); padding: 16px 20px; border-radius: 6px;">
    <div>
      <small style="color: var(--muted); font-size: 11px; text-transform: uppercase; font-weight: 600;">ĐƠN VỊ CUNG CẤP HÀNG</small>
      <p style="margin: 4px 0 0 0; font-size: 15px;"><strong><c:out value="${hoaDon.nhaCungCapTenApDung}"/></strong></p>
      <p style="margin: 2px 0 0 0; font-size: 12px; color: var(--muted);">Mã nhà cung cấp: NCC<c:out value="${hoaDon.nhaCungCap.id}"/></p>
    </div>
    <div>
      <small style="color: var(--muted); font-size: 11px; text-transform: uppercase; font-weight: 600;">THÔNG TIN TIẾP NHẬN KHO</small>
      <p style="margin: 4px 0 0 0; font-size: 15px;">Thủ kho tiếp nhận: <strong><c:out value="${hoaDon.nhanVienTenApDung}"/></strong></p>
      <p style="margin: 2px 0 0 0; font-size: 13px;">Tình trạng: <strong><c:out value="${hoaDon.tinhTrang}"/></strong></p>
      <c:if test="${hoaDon.tinhTrang == 'Đã thanh toán'}">
        <p style="margin: 2px 0 0 0; font-size: 12px; color: #166534;">Thời điểm chi tiền: <c:out value="${hoaDon.thoiDiemThanhToan}"/></p>
      </c:if>
    </div>
  </div>

  <%@ include file="/WEB-INF/view/warehouse/invoice-lines.jspf" %>

  <div class="signatures" style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; text-align: center; margin-top: 48px; padding-top: 24px;">
    <div>
      <strong style="display: block; font-size: 13px;">Người lập hóa đơn</strong>
      <small style="color: var(--muted); display: block; margin-top: 4px;">(Ký, ghi rõ họ tên)</small>
      <div style="height: 60px;"></div>
      <p style="margin: 0; font-size: 13px; font-weight: 600;"><c:out value="${hoaDon.nhanVienTenApDung}"/></p>
    </div>
    <div>
      <strong style="display: block; font-size: 13px;">Thủ kho nhận hàng</strong>
      <small style="color: var(--muted); display: block; margin-top: 4px;">(Ký, ghi rõ họ tên)</small>
      <div style="height: 60px;"></div>
      <p style="margin: 0; font-size: 13px; font-weight: 600;"><c:out value="${hoaDon.nhanVienTenApDung}"/></p>
    </div>
    <div>
      <strong style="display: block; font-size: 13px;">Đại diện nhà cung cấp</strong>
      <small style="color: var(--muted); display: block; margin-top: 4px;">(Ký, ghi rõ họ tên)</small>
      <div style="height: 60px;"></div>
      <p style="margin: 0; font-size: 13px; font-weight: 600;"><c:out value="${hoaDon.nhaCungCapTenApDung}"/></p>
    </div>
  </div>

  <div class="panel-note" style="margin-top: 32px; text-align: center; font-size: 12px; color: var(--muted);">
    Chứng từ được tạo và quản lý bởi Hệ thống phần mềm GaraMan · Kho phụ tùng ô tô
  </div>
</article>

<div class="invoice-actions" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
  <a class="btn" href="<c:url value='/warehouse/purchases/view'><c:param name='id' value='${hoaDon.id}'/></c:url>">
    ← Quay lại hóa đơn
  </a>
  <button class="btn btn-primary" id="print-invoice" type="button" onclick="window.print();" style="padding: 10px 24px;">
    🖨 In hóa đơn
  </button>
</div>

<script src="<c:url value='/assets/js/print.js'/>"></script>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
