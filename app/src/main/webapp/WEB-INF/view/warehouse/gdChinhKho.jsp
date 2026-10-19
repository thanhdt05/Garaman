<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<c:set var="pageTitle" value="Trang chính nhân viên kho · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Kho phụ tùng" scope="request" />
<c:set var="activeNav" value="${activeNav != null ? activeNav : 'warehouse-home'}" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">KHÔNG GIAN NHÂN VIÊN KHO</div>
    <h1>Xin chào, <c:out value="${currentUser.hoTen != null ? currentUser.hoTen : 'Thủ kho'}" /></h1>
    <p>Tiếp nhận phụ tùng và lập hóa đơn nhập từ nhà cung cấp.</p>
  </div>
  <span class="badge neutral">Nhân viên kho</span>
</div>

<section class="hero">
  <div>
    <span class="mini-label">NGHIỆP VỤ NHẬP PHỤ TÙNG</span>
    <h2>Mỗi lần nhập hàng.<br>Đầy đủ từng chi tiết.</h2>
    <p>Bắt đầu bằng việc chọn nhà cung cấp, thêm các phụ tùng cần nhập rồi kiểm tra và xác nhận hóa đơn chính xác vào kho.</p>
    <a class="btn btn-primary" id="start-purchase" href="<c:url value='/warehouse/suppliers'/>">
      Bắt đầu nhập phụ tùng <span aria-hidden="true">→</span>
    </a>
  </div>
  <img src="<c:url value='/assets/images/part.svg'/>" alt="Minh họa phụ tùng ô tô">
</section>

<c:if test="${not empty dsHoaDonChuaTra}">
  <div class="section-title" style="margin-top: 28px;">
    <h2>Hóa đơn chưa thanh toán của bạn</h2>
    <span class="summary-label"><c:out value="${dsHoaDonChuaTra.size()}" /> hóa đơn chờ xử lý</span>
  </div>

  <section class="card" style="padding: 0; overflow: hidden; margin-bottom: 24px;">
    <table class="data-table">
      <thead>
        <tr>
          <th>Mã HĐ</th>
          <th>Nhà cung cấp</th>
          <th>Thời điểm lập</th>
          <th style="text-align: right;">Tổng tiền</th>
          <th>Tình trạng</th>
          <th style="text-align: center;">Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <c:forEach var="hd" items="${dsHoaDonChuaTra}">
          <tr>
            <td><strong>HN<c:out value="${hd.id}" /></strong></td>
            <td><c:out value="${hd.nhaCungCapTenApDung}" /></td>
            <td>
              <fmt:parseDate value="${hd.thoiDiemXacNhan}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" type="both" />
              <c:choose>
                <c:when test="${parsedDate != null}">
                  <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy HH:mm" />
                </c:when>
                <c:otherwise>
                  <c:out value="${hd.thoiDiemXacNhan}" />
                </c:otherwise>
              </c:choose>
            </td>
            <td style="text-align: right; font-weight: 600;">
              <fmt:formatNumber value="${hd.tongTien}" type="currency" currencySymbol="₫" maxFractionDigits="0" />
            </td>
            <td>
              <span class="badge warning"><c:out value="${hd.tinhTrang}" /></span>
            </td>
            <td style="text-align: center;">
              <a class="btn btn-small" href="<c:url value='/warehouse/purchases/view?id=${hd.id}'/>">
                Mở hóa đơn →
              </a>
            </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>
  </section>
</c:if>

<div class="section-title">
  <h2>Quy trình nhập phụ tùng</h2>
  <span class="summary-label">04 bước thực hiện</span>
</div>

<section class="panel">
  <div class="list-item">
    <div>
      <h3>01. Chọn nhà cung cấp</h3>
      <p>Tìm theo tên hoặc thêm mới nhà cung cấp đang hợp tác.</p>
    </div>
    <span class="code">NHÀ CUNG CẤP</span>
  </div>
  <div class="list-item">
    <div>
      <h3>02. Lập danh sách phụ tùng</h3>
      <p>Tìm phụ tùng, thêm mới nếu chưa có, nhập số lượng và đơn giá nhập cho từng dòng.</p>
    </div>
    <span class="code">CHI TIẾT NHẬP</span>
  </div>
  <div class="list-item">
    <div>
      <h3>03. Kiểm tra và xác nhận</h3>
      <p>Đối chiếu nhà cung cấp, thông tin phụ tùng, số lượng, đơn giá và tổng tiền hóa đơn.</p>
    </div>
    <span class="code">XÁC NHẬN</span>
  </div>
  <div class="list-item">
    <div>
      <h3>04. In hóa đơn và thanh toán</h3>
      <p>In hóa đơn nhập lưu kho và thực hiện ghi nhận thanh toán tiền hàng cho nhà cung cấp.</p>
    </div>
    <span class="code">HOÀN TẤT</span>
  </div>
</section>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
