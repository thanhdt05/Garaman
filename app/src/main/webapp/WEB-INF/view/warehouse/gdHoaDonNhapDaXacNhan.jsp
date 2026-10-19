<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<c:set var="pageTitle" value="Hóa đơn nhập HN${hoaDon.id} · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Hóa đơn HN${hoaDon.id}" scope="request" />
<c:set var="activeNav" value="purchase" scope="request" />
<c:set var="buocNhap" value="3" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">NHẬP PHỤ TÙNG / HÓA ĐƠN ĐÃ XÁC NHẬN</div>
    <h1>Hóa đơn nhập đã xác nhận</h1>
    <p>Đối chiếu chứng từ, in hóa đơn và ghi nhận thanh toán sau khi trả đủ tiền.</p>
  </div>
  <c:choose>
    <c:when test="${hoaDon.tinhTrang == 'Đã thanh toán'}">
      <span class="badge neutral" style="background: var(--success, #16a34a); color: #fff;">Đã thanh toán</span>
    </c:when>
    <c:otherwise>
      <span class="badge warning">Chưa thanh toán</span>
    </c:otherwise>
  </c:choose>
</div>

<%@ include file="/WEB-INF/view/common/steps.jspf" %>

<div class="notice success" id="invoice-notice" style="background: #f0fdf4; border: 1px solid #bbf7d0; color: #166534; padding: 12px 16px; border-radius: 6px; margin-bottom: 20px;">
  Hóa đơn <strong>HN<c:out value="${hoaDon.id}"/></strong> đã được xác nhận và ghi nhận tăng tồn kho vào hệ thống.
</div>

<section class="panel" style="margin-bottom: 24px;">
  <div class="panel-head" style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 12px;">
    <h2 style="margin: 0; font-size: 18px;">Thông tin hóa đơn</h2>
    <span class="code" style="font-size: 14px; font-weight: 700;">HN<c:out value="${hoaDon.id}"/></span>
  </div>
  <div class="compact-info" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-top: 16px;">
    <div style="background: var(--bg-alt); padding: 12px 16px; border-radius: 6px;">
      <small style="color: var(--muted); display: block; font-size: 11px; text-transform: uppercase;">NHÀ CUNG CẤP ÁP DỤNG</small>
      <strong style="font-size: 15px;"><c:out value="${hoaDon.nhaCungCapTenApDung}"/></strong>
    </div>
    <div style="background: var(--bg-alt); padding: 12px 16px; border-radius: 6px;">
      <small style="color: var(--muted); display: block; font-size: 11px; text-transform: uppercase;">NHÂN VIÊN LẬP ÁP DỤNG</small>
      <strong style="font-size: 15px;"><c:out value="${hoaDon.nhanVienTenApDung}"/></strong>
    </div>
    <div style="background: var(--bg-alt); padding: 12px 16px; border-radius: 6px;">
      <small style="color: var(--muted); display: block; font-size: 11px; text-transform: uppercase;">THỜI ĐIỂM XÁC NHẬN</small>
      <strong style="font-size: 14px;">
        <c:out value="${hoaDon.thoiDiemXacNhan}"/>
      </strong>
    </div>
    <div style="background: var(--bg-alt); padding: 12px 16px; border-radius: 6px;">
      <small style="color: var(--muted); display: block; font-size: 11px; text-transform: uppercase;">TÌNH TRẠNG THANH TOÁN</small>
      <strong style="font-size: 14px;"><c:out value="${hoaDon.tinhTrang}"/></strong>
    </div>
  </div>
</section>

<section class="panel" style="margin-bottom: 24px;">
  <div class="panel-head" style="border-bottom: 1px solid var(--border); padding-bottom: 12px; margin-bottom: 16px;">
    <h2 style="margin: 0; font-size: 18px;">Chi tiết phụ tùng nhập</h2>
    <span class="summary-label" style="color: var(--muted); font-size: 13px;">Dữ liệu chứng từ đã chốt (snapshot)</span>
  </div>
  <%@ include file="/WEB-INF/view/warehouse/invoice-lines.jspf" %>
</section>

<!-- Khung thanh toán cho nhà cung cấp -->
<section class="panel" style="margin-bottom: 24px;">
  <div class="panel-head" style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 12px;">
    <h2 style="margin: 0; font-size: 18px;">Thanh toán cho nhà cung cấp</h2>
    <c:choose>
      <c:when test="${hoaDon.tinhTrang == 'Đã thanh toán'}">
        <span class="badge neutral" style="background: var(--success, #16a34a); color: #fff;">Đã thanh toán</span>
      </c:when>
      <c:otherwise>
        <span class="badge warning">Chưa thanh toán</span>
      </c:otherwise>
    </c:choose>
  </div>

  <div class="panel-body" style="padding-top: 16px;">
    <c:choose>
      <c:when test="${hoaDon.tinhTrang == 'Đã thanh toán'}">
        <div style="padding: 16px; background: #f0fdf4; border-radius: 6px; border: 1px solid #bbf7d0;">
          <h3 style="margin: 0 0 8px 0; color: #166534; font-size: 15px;">✓ Hóa đơn đã hoàn tất thanh toán</h3>
          <p style="margin: 0; font-size: 14px; color: #166534;">
            Thời điểm thanh toán: <strong><c:out value="${hoaDon.thoiDiemThanhToan}"/></strong> ·
            Số tiền đã chi: <strong><fmt:formatNumber value="${hoaDon.tongTien}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></strong>
          </p>
        </div>
      </c:when>

      <c:otherwise>
        <c:choose>
          <c:when test="${currentUser.id == hoaDon.nhanVienKho.id}">
            <p class="article" style="color: var(--muted); margin-bottom: 16px;">
              Sau khi in chứng từ, đối chiếu với nhà cung cấp và thực tế trả đủ tiền, hãy xác nhận thanh toán đủ một lần theo tổng tiền hóa đơn.
            </p>
            <form action="<c:url value='/warehouse/purchases/pay'/>" method="post" class="payment-form" style="max-width: 480px;">
              <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
              <input type="hidden" name="id" value="<c:out value='${hoaDon.id}'/>">

              <div class="field">
                <label for="soTienDaTra" style="font-weight: 500; margin-bottom: 6px; display: block;">Số tiền đã trả (VNĐ)</label>
                <div style="display: flex; gap: 12px; align-items: center;">
                  <input id="soTienDaTra" name="soTienDaTra" type="text"
                         class="form-control"
                         value="<fmt:formatNumber value='${hoaDon.tongTien}' pattern='0.00'/>"
                         required style="font-size: 16px; font-weight: 600;">
                  <button class="btn btn-primary" id="pay-invoice" type="submit" style="white-space: nowrap; padding: 10px 20px;">
                    Ghi nhận đã thanh toán
                  </button>
                </div>
                <small style="color: var(--muted); display: block; margin-top: 6px;">Thanh toán đủ một lần đúng bằng tổng tiền hóa đơn.</small>
              </div>
            </form>
          </c:when>

          <c:otherwise>
            <div style="padding: 12px 16px; background: var(--bg-alt); border-radius: 6px; border: 1px solid var(--border);">
              <p style="margin: 0; color: var(--muted); font-size: 13px;">
                Chỉ nhân viên đã lập hóa đơn (<strong><c:out value="${hoaDon.nhanVienTenApDung}"/></strong>) mới có quyền thực hiện ghi nhận thanh toán cho chứng từ này.
              </p>
            </div>
          </c:otherwise>
        </c:choose>
      </c:otherwise>
    </c:choose>
  </div>
  <div class="panel-note" style="margin-top: 16px;">
    Ghi nhận thanh toán sau khi đã đối chiếu chứng từ và thực tế trả đủ tiền cho nhà cung cấp.
  </div>
</section>

<div class="actions" style="display: flex; justify-content: space-between; align-items: center; margin-top: 24px;">
  <a class="btn left" href="<c:url value='/home'/>">← Về trang chính</a>
  <a class="btn btn-primary" id="open-print" href="<c:url value='/warehouse/purchases/print'><c:param name='id' value='${hoaDon.id}'/></c:url>">
    In hóa đơn nhập →
  </a>
</div>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
