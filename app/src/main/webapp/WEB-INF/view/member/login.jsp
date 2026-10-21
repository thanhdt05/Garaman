<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Đăng nhập · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Đăng nhập hệ thống" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">XÁC THỰC NGƯỜI DÙNG</div>
    <h1>Đăng nhập GaraMan</h1>
    <p>Truy cập vào các chức năng hệ thống theo phân quyền của bạn.</p>
  </div>
  <span class="badge neutral">Hệ thống</span>
</div>

<div class="grid grid-2" style="max-width: 900px; margin-top: 24px; align-items: start;">
  <section class="card form-container">
    <div class="card-header">
      <h2 style="font-size: 18px; margin: 0 0 4px 0;">Thông tin tài khoản</h2>
      <p style="color: var(--muted); font-size: 13px; margin: 0;">Vui lòng nhập tên đăng nhập và mật khẩu để tiếp tục.</p>
    </div>

    <form action="<c:url value='/login'/>" method="post" style="margin-top: 16px;">
      <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">

      <div class="form-group">
        <label for="username">Tên đăng nhập <span class="required">*</span></label>
        <input type="text" id="username" name="username" class="form-control"
               value="<c:out value='${username}'/>" required autofocus placeholder="Ví dụ: khach26 hoặc kho26">
      </div>

      <div class="form-group" style="margin-top: 14px;">
        <label for="password">Mật khẩu <span class="required">*</span></label>
        <input type="password" id="password" name="password" class="form-control"
               required placeholder="Nhập mật khẩu của bạn">
      </div>

      <div style="margin-top: 24px; display: flex; gap: 12px; align-items: center;">
        <button type="submit" class="btn btn-primary" id="btn-login" style="padding: 10px 24px;">
          Đăng nhập <span aria-hidden="true">→</span>
        </button>
      </div>
    </form>
  </section>

  <section class="card" style="background: var(--bg-alt); border-left: 4px solid var(--primary);">
    <h3 style="font-size: 16px; margin-top: 0;">Tài khoản demo kiểm thử (Đề 26)</h3>
    <p style="font-size: 13px; color: var(--muted); line-height: 1.5;">
      Hệ thống đã chuẩn bị sẵn tài khoản demo với mật khẩu chung là <code>123456</code>:
    </p>

    <div style="margin-top: 12px; font-size: 13px;">
      <div style="padding: 8px 12px; background: #fff; border-radius: 6px; margin-bottom: 8px; border: 1px solid var(--border);">
        <strong>Khách hàng:</strong> <code>khach26</code> (Mật khẩu: <code>123456</code>)<br>
        <span style="color: var(--muted); font-size: 12px;">Đàm Tiến Thành — Module tra cứu dịch vụ/phụ tùng</span>
      </div>

      <div style="padding: 8px 12px; background: #fff; border-radius: 6px; border: 1px solid var(--border);">
        <strong>Nhân viên kho:</strong> <code>kho26</code> (Mật khẩu: <code>123456</code>)<br>
        <span style="color: var(--muted); font-size: 12px;">Trần Văn Kho — Module nhập phụ tùng</span>
      </div>
    </div>
  </section>
</div>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
