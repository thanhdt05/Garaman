<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Thêm nhà cung cấp · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Nhập phụ tùng / Thêm NCC" scope="request" />
<c:set var="activeNav" value="purchase" scope="request" />
<c:set var="buocNhap" value="1" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">NHẬP PHỤ TÙNG / NHÀ CUNG CẤP MỚI</div>
    <h1>Thêm nhà cung cấp</h1>
    <p>Khai báo thông tin nhà cung cấp đối tác chưa có trong hệ thống.</p>
  </div>
</div>

<%@ include file="/WEB-INF/view/common/steps.jspf" %>

<section class="panel form-panel">
  <div class="panel-head">
    <h2>Thông tin nhà cung cấp</h2>
    <span class="summary-label"><span class="required">*</span> Thông tin bắt buộc</span>
  </div>
  <div class="panel-body">
    <form action="<c:url value='/warehouse/suppliers/create'/>" method="post" id="supplier-form">
      <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">

      <div class="form-grid">
        <div class="field full-width">
          <label for="ten">Tên nhà cung cấp <span class="required">*</span></label>
          <input id="ten" name="ten" required maxlength="255"
                 class="form-control ${not empty fieldErrors['ten'] ? 'is-invalid' : ''}"
                 placeholder="Nhập tên công ty / nhà cung cấp"
                 value="<c:out value='${formValues[\"ten\"]}'/>" autofocus>
          <c:if test="${not empty fieldErrors['ten']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['ten']}"/></p>
          </c:if>
        </div>

        <div class="field full-width">
          <label for="diaChi">Địa chỉ trụ sở / kho hàng <span class="required">*</span></label>
          <input id="diaChi" name="diaChi" required maxlength="255"
                 class="form-control ${not empty fieldErrors['diaChi'] ? 'is-invalid' : ''}"
                 placeholder="Số nhà, đường, quận / huyện, tỉnh / thành phố"
                 value="<c:out value='${formValues[\"diaChi\"]}'/>">
          <c:if test="${not empty fieldErrors['diaChi']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['diaChi']}"/></p>
          </c:if>
        </div>

        <div class="field">
          <label for="dienThoai">Điện thoại liên hệ <span class="required">*</span></label>
          <input id="dienThoai" name="dienThoai" type="tel" required maxlength="20"
                 class="form-control ${not empty fieldErrors['dienThoai'] ? 'is-invalid' : ''}"
                 placeholder="Số điện thoại liên hệ"
                 value="<c:out value='${formValues[\"dienThoai\"]}'/>">
          <c:if test="${not empty fieldErrors['dienThoai']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['dienThoai']}"/></p>
          </c:if>
        </div>

        <div class="field">
          <label for="email">Địa chỉ Email</label>
          <input id="email" name="email" type="email" maxlength="255"
                 class="form-control ${not empty fieldErrors['email'] ? 'is-invalid' : ''}"
                 placeholder="Địa chỉ email liên hệ giao dịch"
                 value="<c:out value='${formValues[\"email\"]}'/>">
          <c:if test="${not empty fieldErrors['email']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['email']}"/></p>
          </c:if>
        </div>
      </div>

      <div class="actions" style="margin-top: 24px; display: flex; justify-content: space-between; align-items: center;">
        <a class="btn" href="<c:url value='/warehouse/suppliers'/>">← Quay lại</a>
        <button class="btn btn-primary" type="submit" id="btn-save-supplier">Lưu nhà cung cấp →</button>
      </div>
    </form>
  </div>
  <div class="panel-note">
    Sau khi lưu thành công, hệ thống sẽ tự động chuyển về danh sách và chọn nhà cung cấp vừa tạo.
  </div>
</section>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
