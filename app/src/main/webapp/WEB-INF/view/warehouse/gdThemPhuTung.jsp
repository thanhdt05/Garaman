<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Thêm phụ tùng mới · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Nhập phụ tùng / Thêm phụ tùng" scope="request" />
<c:set var="activeNav" value="purchase" scope="request" />
<c:set var="buocNhap" value="2" scope="request" />
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
      <input type="hidden" name="maYeuCau" value="<c:out value='${maYeuCau}'/>">

      <div class="form-grid">
        <div class="field full-width">
          <label for="ten">Tên phụ tùng <span class="required">*</span></label>
          <input id="ten" name="ten" required maxlength="255"
                 class="form-control ${not empty fieldErrors['ten'] ? 'is-invalid' : ''}"
                 placeholder="Nhập tên phụ tùng chuẩn xác"
                 value="<c:out value='${formValues[\"ten\"]}'/>" autofocus>
          <c:if test="${not empty fieldErrors['ten']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['ten']}"/></p>
          </c:if>
        </div>

        <div class="field">
          <label for="nhomId">Nhóm phụ tùng <span class="required">*</span></label>
          <select id="nhomId" name="nhomId" required class="form-control ${not empty fieldErrors['nhomId'] ? 'is-invalid' : ''}">
            <option value="">-- Chọn nhóm phụ tùng --</option>
            <c:forEach var="nhom" items="${dsNhom}">
              <option value="${nhom.id}" ${formValues['nhomId'] == nhom.id ? 'selected' : ''}>
                <c:out value="${nhom.ten}"/>
              </option>
            </c:forEach>
          </select>
          <c:if test="${not empty fieldErrors['nhomId']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['nhomId']}"/></p>
          </c:if>
        </div>

        <div class="field">
          <label for="donVi">Đơn vị tính <span class="required">*</span></label>
          <select id="donVi" name="donVi" required class="form-control ${not empty fieldErrors['donVi'] ? 'is-invalid' : ''}">
            <option value="">-- Chọn đơn vị tính --</option>
            <option value="Cái" ${formValues['donVi'] == 'Cái' ? 'selected' : ''}>Cái</option>
            <option value="Chiếc" ${formValues['donVi'] == 'Chiếc' ? 'selected' : ''}>Chiếc</option>
            <option value="Bộ" ${formValues['donVi'] == 'Bộ' ? 'selected' : ''}>Bộ</option>
            <option value="Lít" ${formValues['donVi'] == 'Lít' ? 'selected' : ''}>Lít</option>
          </select>
          <c:if test="${not empty fieldErrors['donVi']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['donVi']}"/></p>
          </c:if>
        </div>

        <div class="field">
          <label for="hangSanXuat">Hãng sản xuất <span class="required">*</span></label>
          <input id="hangSanXuat" name="hangSanXuat" required maxlength="255"
                 class="form-control ${not empty fieldErrors['hangSanXuat'] ? 'is-invalid' : ''}"
                 placeholder="Nhà sản xuất (ví dụ: Denso, Bosch, Toyota...)"
                 value="<c:out value='${formValues[\"hangSanXuat\"]}'/>">
          <c:if test="${not empty fieldErrors['hangSanXuat']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['hangSanXuat']}"/></p>
          </c:if>
        </div>

        <div class="field">
          <label for="trangThai">Trạng thái phát hành</label>
          <select id="trangThai" name="trangThai" class="form-control">
            <option value="Chưa công bố" ${formValues['trangThai'] == 'Chưa công bố' ? 'selected' : ''}>Chưa công bố</option>
            <option value="Đang kinh doanh" ${formValues['trangThai'] == 'Đang kinh doanh' ? 'selected' : ''}>Đang kinh doanh</option>
          </select>
          <small style="color: var(--muted); display: block; margin-top: 4px;">Chỉ công bố kinh doanh khi đã có đầy đủ giá bán và mô tả.</small>
        </div>

        <div class="field">
          <label for="giaBan">Giá bán niêm yết (VNĐ)</label>
          <input id="giaBan" name="giaBan" type="number" min="0" step="0.01" max="999999999999"
                 class="form-control ${not empty fieldErrors['giaBan'] ? 'is-invalid' : ''}"
                 placeholder="Để trống nếu chưa công bố"
                 value="<c:out value='${formValues[\"giaBan\"]}'/>">
          <small style="color: var(--muted); display: block; margin-top: 4px;">Khác với đơn giá nhập trên hóa đơn; bắt buộc khi công bố.</small>
          <c:if test="${not empty fieldErrors['giaBan']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['giaBan']}"/></p>
          </c:if>
        </div>

        <div class="field">
          <label for="thongSo">Thông số kỹ thuật</label>
          <input id="thongSo" name="thongSo" maxlength="255" class="form-control"
                 placeholder="Mã tham chiếu, kích thước, tiêu chuẩn..."
                 value="<c:out value='${formValues[\"thongSo\"]}'/>">
        </div>

        <div class="field">
          <label for="dongXePhuHop">Dòng xe phù hợp</label>
          <input id="dongXePhuHop" name="dongXePhuHop" maxlength="255" class="form-control"
                 placeholder="Hãng xe, dòng xe, đời xe tương thích"
                 value="<c:out value='${formValues[\"dongXePhuHop\"]}'/>">
        </div>

        <div class="field">
          <label for="hanBaoHanh">Thời hạn bảo hành</label>
          <input id="hanBaoHanh" name="hanBaoHanh" maxlength="100" class="form-control"
                 placeholder="Ví dụ: 06 tháng, 12 tháng..."
                 value="<c:out value='${formValues[\"hanBaoHanh\"]}'/>">
        </div>

        <div class="field">
          <label for="tonKho">Tồn kho ban đầu</label>
          <input id="tonKho" type="text" value="0.000" readonly class="form-control" style="background: var(--bg-alt); color: var(--muted);">
          <small style="color: var(--muted); display: block; margin-top: 4px;">Tồn kho chỉ tăng tự động sau khi xác nhận hóa đơn nhập.</small>
        </div>

        <div class="field full-width">
          <label for="dieuKienBaoHanh">Điều kiện bảo hành</label>
          <textarea id="dieuKienBaoHanh" name="dieuKienBaoHanh" rows="2" class="form-control"
                    placeholder="Phạm vi và điều kiện áp dụng bảo hành"><c:out value="${formValues['dieuKienBaoHanh']}"/></textarea>
        </div>

        <div class="field full-width">
          <label for="moTa">Mô tả phụ tùng</label>
          <textarea id="moTa" name="moTa" rows="3" class="form-control ${not empty fieldErrors['moTa'] ? 'is-invalid' : ''}"
                    placeholder="Mô tả công dụng, đặc điểm cấu tạo... (Bắt buộc khi chọn Đang kinh doanh)"><c:out value="${formValues['moTa']}"/></textarea>
          <c:if test="${not empty fieldErrors['moTa']}">
            <p class="field-error" style="color: var(--danger); font-size: 12px; margin-top: 4px;"><c:out value="${fieldErrors['moTa']}"/></p>
          </c:if>
        </div>

        <div class="field full-width">
          <label for="hinhAnh">Ảnh minh họa</label>
          <select id="hinhAnh" name="hinhAnh" class="form-control">
            <option value="" ${empty formValues['hinhAnh'] ? 'selected' : ''}>-- Chọn ảnh minh họa có sẵn --</option>
            <option value="assets/images/part.svg" ${formValues['hinhAnh'] == 'assets/images/part.svg' ? 'selected' : ''}>Phụ tùng ô tô chung (part.svg)</option>
            <option value="assets/images/brake.svg" ${formValues['hinhAnh'] == 'assets/images/brake.svg' ? 'selected' : ''}>Hệ thống phanh (brake.svg)</option>
            <option value="assets/images/placeholder.svg" ${formValues['hinhAnh'] == 'assets/images/placeholder.svg' ? 'selected' : ''}>Khác (placeholder.svg)</option>
          </select>
        </div>
      </div>

      <div class="actions" style="margin-top: 24px; display: flex; justify-content: space-between; align-items: center;">
        <a class="btn" href="<c:url value='/warehouse/purchases/draft'><c:param name='maYeuCau' value='${maYeuCau}'/></c:url>">← Quay lại hóa đơn</a>
        <button class="btn btn-primary" type="submit" id="btn-save-part">Lưu phụ tùng →</button>
      </div>
    </form>
  </div>
  <div class="panel-note">
    Lưu xong, hệ thống sẽ quay lại màn hình lập hóa đơn với phụ tùng vừa tạo để bạn nhập số lượng và đơn giá.
  </div>
</section>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
