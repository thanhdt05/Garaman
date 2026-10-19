<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<c:set var="pageTitle" value="Tìm kiếm dịch vụ &amp; phụ tùng · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Tra cứu thông tin" scope="request" />
<c:set var="activeNav" value="search" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">KHÁCH HÀNG / TRA CỨU</div>
    <h1>Tìm dịch vụ &amp; phụ tùng</h1>
    <p>Nhập tên hoặc một phần tên để tìm dịch vụ sửa chữa hoặc phụ tùng bạn cần.</p>
  </div>
</div>

<section class="panel">
  <div class="panel-body">
    <form action="<c:url value='/customer/catalog'/>" method="get" class="search-form" style="display: flex; gap: 12px; align-items: flex-end;">
      <div class="field" style="flex: 1; margin: 0;">
        <label for="tuKhoa" style="display: block; font-weight: 500; margin-bottom: 6px;">Tên dịch vụ / phụ tùng</label>
        <input type="text" id="tuKhoa" name="tuKhoa" class="form-control"
               placeholder="Ví dụ: lọc dầu, phanh, bảo dưỡng..."
               value="<c:out value='${tuKhoa}'/>" maxlength="150" autofocus>
      </div>
      <button class="btn btn-primary" type="submit" id="btn-search" style="padding: 10px 24px;">Tìm kiếm</button>
      <c:if test="${not empty tuKhoa}">
        <a class="btn" href="<c:url value='/customer/catalog'/>" style="padding: 10px 16px;">Xóa tìm</a>
      </c:if>
    </form>
  </div>
</section>

<c:choose>
  <c:when test="${not daTim}">
    <section class="panel">
      <div class="empty">
        <strong>Bạn muốn tìm thông tin gì?</strong>
        <p>Nhập từ khóa tên dịch vụ hoặc phụ tùng ở khung tìm kiếm phía trên rồi chọn <strong>Tìm kiếm</strong>.</p>
      </div>
    </section>
  </c:when>

  <c:otherwise>
    <c:set var="totalCount" value="${(dsDichVu != null ? dsDichVu.size() : 0) + (dsPhuTung != null ? dsPhuTung.size() : 0)}" />

    <section class="panel">
      <div class="panel-head" style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 12px;">
        <h2 style="margin: 0; font-size: 18px;">Kết quả tìm kiếm</h2>
        <span class="badge neutral" id="result-count">
          Tìm thấy <c:out value="${totalCount}"/> kết quả cho từ khóa "<c:out value="${tuKhoa}"/>"
        </span>
      </div>

      <c:if test="${totalCount == 0}">
        <div class="empty" style="margin-top: 16px;">
          <strong>Không tìm thấy kết quả phù hợp</strong>
          <p>Không có dịch vụ hoặc phụ tùng nào khớp với từ khóa "<strong><c:out value='${tuKhoa}'/></strong>". Thử tìm kiếm với từ khóa khác ngắn hơn.</p>
        </div>
      </c:if>

      <c:if test="${not empty dsDichVu}">
        <div style="margin-top: 20px;">
          <h3 style="font-size: 15px; margin-bottom: 10px; color: var(--primary);">
            Dịch vụ chăm sóc &amp; sửa chữa (<c:out value="${dsDichVu.size()}"/>)
          </h3>
          <div class="table-wrap">
            <table class="data-table">
              <thead>
                <tr>
                  <th style="width: 80px;">Mã</th>
                  <th>Tên dịch vụ</th>
                  <th>Nhóm dịch vụ</th>
                  <th style="text-align: right; width: 140px;">Đơn giá</th>
                  <th style="width: 140px;">Thời gian dự kiến</th>
                  <th style="width: 120px; text-align: center;">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="dv" items="${dsDichVu}">
                  <tr>
                    <td class="code">DV<c:out value="${dv.id}"/></td>
                    <td><strong><c:out value="${dv.ten}"/></strong></td>
                    <td><c:out value="${dv.nhom}"/></td>
                    <td style="text-align: right; font-weight: 600;">
                      <fmt:formatNumber value="${dv.donGia}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                    </td>
                    <td><c:out value="${dv.thoiGianDuKien}"/> phút</td>
                    <td style="text-align: center;">
                      <a class="text-link" href="<c:url value='/customer/catalog/detail'><c:param name='id' value='${dv.id}'/><c:param name='loai' value='service'/><c:param name='tuKhoa' value='${tuKhoa}'/></c:url>">
                        Xem chi tiết →
                      </a>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </div>
      </c:if>

      <c:if test="${not empty dsPhuTung}">
        <div style="margin-top: 28px;">
          <h3 style="font-size: 15px; margin-bottom: 10px; color: var(--primary);">
            Phụ tùng ô tô chính hãng (<c:out value="${dsPhuTung.size()}"/>)
          </h3>
          <div class="table-wrap">
            <table class="data-table">
              <thead>
                <tr>
                  <th style="width: 80px;">Mã</th>
                  <th>Tên phụ tùng</th>
                  <th>Hãng sản xuất</th>
                  <th>Đơn vị tính</th>
                  <th style="text-align: right; width: 140px;">Giá bán niêm yết</th>
                  <th style="width: 120px; text-align: center;">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="pt" items="${dsPhuTung}">
                  <tr>
                    <td class="code">PT<c:out value="${pt.id}"/></td>
                    <td>
                      <strong><c:out value="${pt.ten}"/></strong>
                      <c:if test="${pt.nhom != null}">
                        <br><small style="color: var(--muted);"><c:out value="${pt.nhom.ten}"/></small>
                      </c:if>
                    </td>
                    <td><c:out value="${pt.hangSanXuat}"/></td>
                    <td><c:out value="${pt.donVi}"/></td>
                    <td style="text-align: right; font-weight: 600;">
                      <c:choose>
                        <c:when test="${pt.giaBan != null}">
                          <fmt:formatNumber value="${pt.giaBan}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                        </c:when>
                        <c:otherwise>Liên hệ</c:otherwise>
                      </c:choose>
                    </td>
                    <td style="text-align: center;">
                      <a class="text-link" href="<c:url value='/customer/catalog/detail'><c:param name='id' value='${pt.id}'/><c:param name='loai' value='part'/><c:param name='tuKhoa' value='${tuKhoa}'/></c:url>">
                        Xem chi tiết →
                      </a>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </div>
      </c:if>

      <div class="panel-note" style="margin-top: 16px;">
        Chọn "Xem chi tiết" để xem quy trình thực hiện, thông số kỹ thuật và chính sách bảo hành tương ứng.
      </div>
    </section>
  </c:otherwise>
</c:choose>

<div style="margin-top: 20px;">
  <a class="btn btn-quiet" href="<c:url value='/home'/>">← Về trang chính</a>
</div>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
