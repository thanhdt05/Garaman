<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<c:set var="pageTitle" value="Lập hóa đơn nhập · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Nhập phụ tùng / Lập hóa đơn" scope="request" />
<c:set var="activeNav" value="purchase" scope="request" />
<c:set var="buocNhap" value="2" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">NHÂN VIÊN KHO / NHẬP PHỤ TÙNG</div>
    <h1>Lập hóa đơn nhập</h1>
    <p>Tìm phụ tùng, chọn và nhập số lượng cùng đơn giá cho từng dòng hàng.</p>
  </div>
  <button type="button" class="btn" onclick="document.getElementById('parts-picker').scrollIntoView({behavior: 'smooth'});">
    + Thêm phụ tùng
  </button>
</div>

<%@ include file="/WEB-INF/view/common/steps.jspf" %>

<!-- FORM DUY NHẤT BAO TOÀN BỘ TRANG ĐỂ BẢO TOÀN DỮ LIỆU ĐANG NHẬP -->
<form id="purchase-form" method="post" action="<c:url value='/warehouse/purchases/update'/>">
  <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
  <input type="hidden" name="maYeuCau" value="<c:out value='${maYeuCau}'/>">
  <input type="hidden" name="draftVersion" value="<c:out value='${draftVersion}'/>">

  <!-- Thông tin chung hóa đơn nhập -->
  <section class="panel" style="margin-bottom: 24px;">
    <div class="panel-head" style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 12px;">
      <h2 style="margin: 0; font-size: 18px;">Thông tin nhập hàng</h2>
      <a class="text-link" href="<c:url value='/warehouse/suppliers'/>" onclick="return confirm('Đổi nhà cung cấp sẽ chuyển sang phiên nhập mới. Bạn có muốn tiếp tục?');">
        Đổi nhà cung cấp
      </a>
    </div>
    <div class="compact-info" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; margin-top: 16px;">
      <div style="background: var(--bg-alt); padding: 12px 16px; border-radius: 6px;">
        <small style="color: var(--muted); display: block; font-size: 11px; text-transform: uppercase;">NHÀ CUNG CẤP</small>
        <strong style="font-size: 15px;"><c:out value="${hoaDon.nhaCungCap.ten}"/></strong>
      </div>
      <div style="background: var(--bg-alt); padding: 12px 16px; border-radius: 6px;">
        <small style="color: var(--muted); display: block; font-size: 11px; text-transform: uppercase;">NHÂN VIÊN LẬP</small>
        <strong style="font-size: 15px;"><c:out value="${hoaDon.nhanVienKho.hoTen}"/></strong>
      </div>
      <div style="background: var(--bg-alt); padding: 12px 16px; border-radius: 6px;">
        <small style="color: var(--muted); display: block; font-size: 11px; text-transform: uppercase;">ĐỊA CHỈ NHÀ CUNG CẤP</small>
        <strong style="font-size: 14px;"><c:out value="${hoaDon.nhaCungCap.diaChi}"/></strong>
      </div>
      <div style="background: var(--bg-alt); padding: 12px 16px; border-radius: 6px;">
        <small style="color: var(--muted); display: block; font-size: 11px; text-transform: uppercase;">MÃ YÊU CẦU NHÁP</small>
        <code style="font-size: 12px;"><c:out value="${maYeuCau.substring(0, 8)}"/>...</code>
      </div>
    </div>
  </section>

  <!-- Khung tìm kiếm và chọn phụ tùng -->
  <%@ include file="/WEB-INF/view/warehouse/parts-picker.jspf" %>

  <!-- Danh sách chi tiết các dòng nhập -->
  <section class="panel" style="margin-bottom: 24px;">
    <div class="panel-head" style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 12px;">
      <h2 style="margin: 0; font-size: 18px;">Các dòng phụ tùng nhập</h2>
      <span class="result-count" id="cart-count">
        <c:out value="${hoaDon.dsChiTietNhap != null ? hoaDon.dsChiTietNhap.size() : 0}"/> mặt hàng
      </span>
    </div>

    <c:choose>
      <c:when test="${empty hoaDon.dsChiTietNhap}">
        <div id="cart-empty" class="empty" style="margin-top: 16px;">
          <strong>Chưa có phụ tùng nào trong hóa đơn</strong>
          <p>Sử dụng khung "Tìm và chọn phụ tùng" ở phía trên để thêm các mặt hàng cần nhập kho.</p>
        </div>
      </c:when>

      <c:otherwise>
        <div class="table-wrap" style="margin-top: 16px;">
          <table class="data-table" id="cart-table">
            <thead>
              <tr>
                <th style="width: 50px;">STT</th>
                <th>Phụ tùng</th>
                <th>Hãng</th>
                <th style="width: 80px;">ĐVT</th>
                <th style="width: 140px; text-align: right;">Số lượng <span class="required">*</span></th>
                <th style="width: 170px; text-align: right;">Đơn giá nhập (₫) <span class="required">*</span></th>
                <th style="width: 150px; text-align: right;">Thành tiền (₫)</th>
                <th style="width: 80px; text-align: center;">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="ct" items="${hoaDon.dsChiTietNhap}" varStatus="status">
                <c:set var="pid" value="${ct.phuTung.id}"/>
                <tr class="${focusPartId == pid ? 'highlight' : ''}">
                  <td class="code" style="text-align: center;">
                    <input type="hidden" name="partId" value="${pid}">
                    <fmt:formatNumber value="${status.index + 1}" pattern="00"/>
                  </td>
                  <td>
                    <strong><c:out value="${ct.phuTung.ten}"/></strong>
                    <br><small style="color: var(--muted);">Mã: PT<c:out value="${pid}"/></small>
                  </td>
                  <td><c:out value="${ct.phuTung.hangSanXuat}"/></td>
                  <td><c:out value="${ct.phuTung.donVi}"/></td>
                  <td style="text-align: right;">
                    <c:set var="qtyKey" value="soLuong_${pid}"/>
                    <c:set var="qtyVal" value="${draftRawInputs[qtyKey] != null ? draftRawInputs[qtyKey] : ct.soLuong}"/>
                    <c:set var="qtyErr" value="${draftRawInputs['error_soLuong_' + pid]}"/>
                    <input type="text" id="soLuong_${pid}" name="soLuong_${pid}"
                           class="form-control ${not empty qtyErr ? 'is-invalid' : ''}"
                           value="<c:out value='${qtyVal}'/>"
                           style="text-align: right; width: 110px; display: inline-block;">
                    <c:if test="${not empty qtyErr}">
                      <p class="field-error" style="color: var(--danger); font-size: 11px; margin: 4px 0 0 0; text-align: right;"><c:out value="${qtyErr}"/></p>
                    </c:if>
                  </td>
                  <td style="text-align: right;">
                    <c:set var="priceKey" value="donGiaNhap_${pid}"/>
                    <c:set var="priceVal" value="${draftRawInputs[priceKey] != null ? draftRawInputs[priceKey] : ct.donGiaNhap}"/>
                    <c:set var="priceErr" value="${draftRawInputs['error_donGiaNhap_' + pid]}"/>
                    <input type="text" id="donGiaNhap_${pid}" name="donGiaNhap_${pid}"
                           class="form-control ${not empty priceErr ? 'is-invalid' : ''}"
                           value="<c:out value='${priceVal}'/>"
                           style="text-align: right; width: 140px; display: inline-block;">
                    <c:if test="${not empty priceErr}">
                      <p class="field-error" style="color: var(--danger); font-size: 11px; margin: 4px 0 0 0; text-align: right;"><c:out value="${priceErr}"/></p>
                    </c:if>
                  </td>
                  <td style="text-align: right; font-weight: 600;">
                    <fmt:formatNumber value="${ct.thanhTien}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                  </td>
                  <td style="text-align: center;">
                    <button type="submit" class="btn btn-small btn-quiet"
                            formaction="<c:url value='/warehouse/purchases/remove-line'/>"
                            name="removePartId" value="${pid}"
                            title="Xóa phụ tùng này khỏi hóa đơn"
                            style="color: var(--danger); padding: 4px 8px;">
                      ✕ Xóa
                    </button>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>

        <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 16px;">
          <button type="submit" class="btn"
                  formaction="<c:url value='/warehouse/purchases/update'/>"
                  style="font-size: 13px;">
            ↻ Cập nhật &amp; tính lại tiền
          </button>
          <div class="total-row" style="display: flex; gap: 16px; align-items: center; background: var(--bg-alt); padding: 12px 20px; border-radius: 6px; border: 1px solid var(--border);">
            <span style="font-weight: 600;">Tổng tiền hóa đơn:</span>
            <strong style="font-size: 20px; color: var(--primary);">
              <fmt:formatNumber value="${hoaDon.tongTien}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
            </strong>
          </div>
        </div>
      </c:otherwise>
    </c:choose>
  </section>

  <!-- Thanh thao tác chính -->
  <div class="actions" style="display: flex; justify-content: space-between; align-items: center; margin-top: 24px;">
    <button class="btn left btn-quiet" type="submit"
            formaction="<c:url value='/warehouse/purchases/cancel'/>"
            onclick="return confirm('Bạn có chắc chắn muốn hủy bản nháp này? Mọi dữ liệu đang nhập sẽ bị xóa.');">
      Hủy hóa đơn
    </button>

    <div style="display: flex; gap: 12px;">
      <a class="btn" href="#parts-picker">Chọn tiếp phụ tùng ↑</a>
      <button class="btn btn-primary" id="confirm-purchase" type="submit"
              formaction="<c:url value='/warehouse/purchases/confirm'/>"
              ${empty hoaDon.dsChiTietNhap ? 'disabled' : ''}>
        Xác nhận hóa đơn nhập →
      </button>
    </div>
  </div>
</form>

<c:if test="${not empty focusPartId}">
  <script>
    document.addEventListener("DOMContentLoaded", function() {
      var el = document.getElementById("soLuong_${focusPartId}");
      if (el) {
        el.focus();
        el.select();
      }
    });
  </script>
</c:if>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
