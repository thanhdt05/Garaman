<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Chọn nhà cung cấp · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Nhập phụ tùng / Chọn NCC" scope="request" />
<c:set var="activeNav" value="purchase" scope="request" />
<c:set var="buocNhap" value="1" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">NHÂN VIÊN KHO / NHẬP PHỤ TÙNG</div>
    <h1>Chọn nhà cung cấp</h1>
    <p>Tìm theo tên để chọn nhà cung cấp đối tác cho lần nhập phụ tùng này.</p>
  </div>
  <a class="btn" href="<c:url value='/warehouse/suppliers/new'/>">+ Thêm nhà cung cấp</a>
</div>

<%@ include file="/WEB-INF/view/common/steps.jspf" %>

<section class="panel">
  <div class="panel-body">
    <form action="<c:url value='/warehouse/suppliers'/>" method="get" class="inline-search" style="display: flex; gap: 12px; align-items: flex-end;">
      <div class="field" style="flex: 1; margin: 0;">
        <label for="tenNCC" style="display: block; font-weight: 500; margin-bottom: 6px;">Tên nhà cung cấp</label>
        <input type="text" id="tenNCC" name="tuKhoa" class="form-control"
               placeholder="Ví dụ: Hòa Phát, Phụ tùng Miền Nam..."
               value="<c:out value='${tuKhoa}'/>" maxlength="150" autofocus>
      </div>
      <button class="btn btn-primary" type="submit" id="btn-search-supplier" style="padding: 10px 24px;">Tìm kiếm</button>
      <c:if test="${not empty tuKhoa}">
        <a class="btn" href="<c:url value='/warehouse/suppliers'/>" style="padding: 10px 16px;">Xóa tìm</a>
      </c:if>
    </form>
  </div>
</section>

<section class="panel">
  <div class="panel-head" style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 12px;">
    <h2 style="margin: 0; font-size: 18px;">Danh sách nhà cung cấp</h2>
    <span class="result-count" id="result-count">
      <c:choose>
        <c:when test="${not daTim}">Chưa tìm kiếm</c:when>
        <c:otherwise>Tìm thấy <c:out value="${dsNhaCungCap != null ? dsNhaCungCap.size() : 0}" /> nhà cung cấp</c:otherwise>
      </c:choose>
    </span>
  </div>

  <c:choose>
    <c:when test="${not daTim}">
      <div class="empty">
        <strong>Tìm nhà cung cấp cho lần nhập</strong>
        <p>Nhập tên nhà cung cấp và chọn <strong>Tìm kiếm</strong>; hoặc chọn "<strong>+ Thêm nhà cung cấp</strong>" nếu chưa có trong danh mục.</p>
      </div>
    </c:when>

    <c:when test="${empty dsNhaCungCap}">
      <div class="empty">
        <strong>Không tìm thấy nhà cung cấp phù hợp</strong>
        <p>Thử tìm với tên khác hoặc chọn "<a href="<c:url value='/warehouse/suppliers/new'/>">Thêm nhà cung cấp</a>" để tạo mới đối tác.</p>
      </div>
    </c:when>

    <c:otherwise>
      <div class="table-wrap" style="margin-top: 16px;">
        <table class="data-table" id="supplier-table">
          <thead>
            <tr>
              <th style="width: 80px;">Mã</th>
              <th>Nhà cung cấp</th>
              <th>Địa chỉ</th>
              <th>Điện thoại / Email</th>
              <th>Trạng thái</th>
              <th style="width: 100px; text-align: center;">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="ncc" items="${dsNhaCungCap}">
              <tr class="${selectedId == ncc.id ? 'highlight' : ''}">
                <td class="code">NCC<c:out value="${ncc.id}"/></td>
                <td><strong><c:out value="${ncc.ten}"/></strong></td>
                <td><c:out value="${ncc.diaChi}"/></td>
                <td>
                  <c:out value="${ncc.dienThoai}"/>
                  <c:if test="${not empty ncc.email}">
                    <br><small style="color: var(--muted);"><c:out value="${ncc.email}"/></small>
                  </c:if>
                </td>
                <td><span class="badge"><c:out value="${ncc.trangThai}"/></span></td>
                <td style="text-align: center;">
                  <form action="<c:url value='/warehouse/purchases/start'/>" method="post" style="margin: 0;">
                    <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
                    <input type="hidden" name="supplierId" value="<c:out value='${ncc.id}'/>">
                    <button class="btn btn-small select-supplier" type="submit">
                      Chọn →
                    </button>
                  </form>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
      <div class="panel-note" style="margin-top: 16px;">
        Chọn nhà cung cấp để chuyển sang màn hình lập danh sách phụ tùng cần nhập.
      </div>
    </c:otherwise>
  </c:choose>
</section>

<div style="margin-top: 20px;">
  <a class="btn btn-quiet" href="<c:url value='/home'/>">← Về trang chính</a>
</div>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
