<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Thêm phụ tùng mới · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Nhập phụ tùng / Thêm phụ tùng" scope="request" />
<c:set var="activeNav" value="purchase" scope="request" />
<c:set var="purchaseStep" value="2" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<div class="page-heading">
  <div>
    <div class="eyebrow">NHẬP PHỤ TÙNG / PHỤ TÙNG MỚI</div>
    <h1>Thêm phụ tùng mới</h1>
    <p>Khai báo thông tin phụ tùng trước khi đưa vào hóa đơn nhập hàng.</p>
  </div>
</div>

<%@ include file="/WEB-INF/view/common/steps.jspf" %>

<section class="panel form-panel">
  <div class="panel-head">
    <h2>Thông tin phụ tùng</h2>
    <span class="summary-label"><span class="required">*</span> Thông tin bắt buộc</span>
  </div>
  <div class="panel-body">
    <form action="<c:url value='/warehouse/parts/create'/>" method="post" id="part-form">
      <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">
      <input type="hidden" name="requestCode" value="<c:out value='${requestCode}'/>">

      <input type="hidden" name="draftVersion" value="<c:out value='${draftVersion}'/>">

      <div class="form-grid">
        <div class="field full-width">
          <label for="name">Tên phụ tùng <span class="required">*</span></label>
          <input id="name" name="name" required maxlength="255"
                 class="form-control ${not empty fieldErrors['name'] ? 'is-invalid' : ''}"
                 placeholder="Nhập tên phụ tùng chuẩn xác"
                 value="<c:out value='${formValues.name}'/>" autofocus>
          <c:if test="${not empty fieldErrors['name']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['name']}"/></p>
          </c:if>
        </div>

        <div class="field">
          <label for="categoryId">Nhóm phụ tùng <span class="required">*</span></label>
          <select id="categoryId" name="categoryId" required class="form-control ${not empty fieldErrors['categoryId'] ? 'is-invalid' : ''}">
            <option value="">-- Chọn nhóm phụ tùng --</option>
            <c:forEach var="category" items="${categories}">
              <option value="${category.id}" ${formValues['categoryId'] == category.id ? 'selected' : ''}>
                <c:out value="${category.name}"/>
              </option>
            </c:forEach>
          </select>
          <c:if test="${not empty fieldErrors['categoryId']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['categoryId']}"/></p>
          </c:if>
        </div>

        <div class="field">
          <label for="unit">Đơn vị tính <span class="required">*</span></label>
          <select id="unit" name="unit" required class="form-control ${not empty fieldErrors['unit'] ? 'is-invalid' : ''}">
            <option value="">-- Chọn đơn vị tính --</option>
            <option value="Cái" ${formValues['unit'] == 'Cái' ? 'selected' : ''}>Cái</option>
            <option value="Chiếc" ${formValues['unit'] == 'Chiếc' ? 'selected' : ''}>Chiếc</option>
            <option value="Bộ" ${formValues['unit'] == 'Bộ' ? 'selected' : ''}>Bộ</option>
            <option value="Lít" ${formValues['unit'] == 'Lít' ? 'selected' : ''}>Lít</option>
          </select>
          <c:if test="${not empty fieldErrors['unit']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['unit']}"/></p>
          </c:if>
        </div>

        <div class="field">
          <label for="manufacturer">Hãng sản xuất <span class="required">*</span></label>
          <input id="manufacturer" name="manufacturer" required maxlength="255"
                 class="form-control ${not empty fieldErrors['manufacturer'] ? 'is-invalid' : ''}"
                 placeholder="Nhà sản xuất (ví dụ: Denso, Bosch, Toyota...)"
                 value="<c:out value='${formValues.manufacturer}'/>">
          <c:if test="${not empty fieldErrors['manufacturer']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['manufacturer']}"/></p>
          </c:if>
        </div>

        <div class="field">
          <label for="status">Trạng thái phát hành</label>
          <select id="status" name="status" class="form-control">
            <option value="Chưa công bố" ${formValues['status'] == 'Chưa công bố' ? 'selected' : ''}>Chưa công bố</option>
            <option value="Đang kinh doanh" ${formValues['status'] == 'Đang kinh doanh' ? 'selected' : ''}>Đang kinh doanh</option>
          </select>
          <small style="color: var(--muted); display: block; margin-top: 4px;">Chỉ công bố kinh doanh khi đã có đầy đủ giá bán và mô tả.</small>
        </div>

        <div class="field">
          <label for="sellingPrice">Giá bán niêm yết (VNĐ)</label>
          <input id="sellingPrice" name="sellingPrice" type="number" min="0" step="0.01" max="999999999999"
                 class="form-control ${not empty fieldErrors['sellingPrice'] ? 'is-invalid' : ''}"
                 placeholder="Để trống nếu chưa công bố"
                 value="<c:out value='${formValues.sellingPrice}'/>">
          <small style="color: var(--muted); display: block; margin-top: 4px;">Khác với đơn giá nhập trên hóa đơn; bắt buộc khi công bố.</small>
          <c:if test="${not empty fieldErrors['sellingPrice']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['sellingPrice']}"/></p>
          </c:if>
        </div>

        <div class="field">
          <label for="specifications">Thông số kỹ thuật</label>
          <input id="specifications" name="specifications" maxlength="255" class="form-control"
                 placeholder="Mã tham chiếu, kích thước, tiêu chuẩn..."
                 value="<c:out value='${formValues.specifications}'/>">
        </div>

        <div class="field">
          <label for="compatibleVehicleModels">Dòng xe phù hợp</label>
          <input id="compatibleVehicleModels" name="compatibleVehicleModels" maxlength="255" class="form-control"
                 placeholder="Hãng xe, dòng xe, đời xe tương thích"
                 value="<c:out value='${formValues.compatibleVehicleModels}'/>">
        </div>

        <div class="field">
          <label for="warrantyPeriod">Thời hạn bảo hành</label>
          <input id="warrantyPeriod" name="warrantyPeriod" maxlength="100" class="form-control"
                 placeholder="Ví dụ: 06 tháng, 12 tháng..."
                 value="<c:out value='${formValues.warrantyPeriod}'/>">
        </div>

        <div class="field">
          <label for="stockQuantity">Tồn kho ban đầu</label>
          <input id="stockQuantity" type="text" value="0.000" readonly class="form-control" style="background: var(--bg-alt); color: var(--muted);">
          <small style="color: var(--muted); display: block; margin-top: 4px;">Tồn kho chỉ tăng tự động sau khi xác nhận hóa đơn nhập.</small>
        </div>

        <div class="field full-width">
          <label for="warrantyTerms">Điều kiện bảo hành</label>
          <textarea id="warrantyTerms" name="warrantyTerms" rows="2" class="form-control"
                    placeholder="Phạm vi và điều kiện áp dụng bảo hành"><c:out value="${formValues['warrantyTerms']}"/></textarea>
        </div>

        <div class="field full-width">
          <label for="description">Mô tả phụ tùng</label>
          <textarea id="description" name="description" rows="3" class="form-control ${not empty fieldErrors['description'] ? 'is-invalid' : ''}"
                    placeholder="Mô tả công dụng, đặc điểm cấu tạo... (Bắt buộc khi chọn Đang kinh doanh)"><c:out value="${formValues['description']}"/></textarea>
          <c:if test="${not empty fieldErrors['description']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['description']}"/></p>
          </c:if>
        </div>

        <div class="field full-width">
          <label for="imagePath">Ảnh minh họa</label>
          <select id="imagePath" name="imagePath" class="form-control">
            <option value="" ${empty formValues['imagePath'] ? 'selected' : ''}>-- Chọn ảnh minh họa có sẵn --</option>
            <option value="assets/images/part.svg" ${formValues['imagePath'] == 'assets/images/part.svg' ? 'selected' : ''}>Phụ tùng ô tô chung (part.svg)</option>
            <option value="assets/images/brake.svg" ${formValues['imagePath'] == 'assets/images/brake.svg' ? 'selected' : ''}>Hệ thống phanh (brake.svg)</option>
            <option value="assets/images/placeholder.svg" ${formValues['imagePath'] == 'assets/images/placeholder.svg' ? 'selected' : ''}>Khác (placeholder.svg)</option>
          </select>
        </div>
      </div>

      <div class="actions">
        <a class="btn left" href="<c:url value='/warehouse/purchases/draft'><c:param name='requestCode' value='${requestCode}'/></c:url>">← Quay lại hóa đơn</a>
        <button class="btn btn-primary" type="submit" id="btn-save-part">Lưu phụ tùng →</button>
      </div>
    </form>
  </div>
  <div class="panel-note">
    Lưu xong, hệ thống sẽ quay lại màn hình lập hóa đơn với phụ tùng vừa tạo để bạn nhập số lượng và đơn giá.
  </div>
</section>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
