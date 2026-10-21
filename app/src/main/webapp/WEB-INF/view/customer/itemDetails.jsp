<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<c:set var="pageTitle" value="Chi tiết thông tin · GaraMan" scope="request" />
<c:set var="breadcrumb" value="Chi tiết thông tin" scope="request" />
<c:set var="activeNav" value="search" scope="request" />
<%@ include file="/WEB-INF/view/common/header.jspf" %>

<c:choose>
  <%-- NHÁNH 1: CHI TIẾT DỊCH VỤ --%>
  <c:when test="${itemType == 'service'}">
    <div class="page-heading">
      <div>
        <div class="eyebrow">TRA CỨU / CHI TIẾT DỊCH VỤ</div>
        <h1 id="detail-name"><c:out value="${service.name}"/></h1>
        <p><span id="detail-code">DV<c:out value="${service.id}"/></span> · <span id="detail-group"><c:out value="${service.category}"/></span></p>
      </div>
      <a class="btn" href="<c:url value='/customer/catalog'><c:param name='keyword' value='${keyword}'/><c:param name='itemTypeFilter' value='${itemTypeFilter}'/></c:url>">← Quay lại tìm kiếm</a>
    </div>

    <div class="two-col">
      <div>
        <section class="panel">
          <div class="panel-head">
            <h2>Thông tin dịch vụ</h2>
            <span class="badge"><c:out value="${service.status}"/></span>
          </div>
          <div class="panel-body">
            <p class="article" id="detail-description"><c:out value="${service.description}"/></p>
            <div class="detail-grid">
              <dl class="detail-item">
                <dt>Đơn giá dịch vụ</dt>
                <dd><strong id="detail-price"><fmt:formatNumber value="${service.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="2"/></strong></dd>
              </dl>
              <dl class="detail-item">
                <dt>Thời gian dự kiến</dt>
                <dd id="detail-time"><c:out value="${service.estimatedDuration}"/> phút</dd>
              </dl>
            </div>
          </div>
          <div class="panel-note">Đơn giá trên là chi phí nhân công dịch vụ. Dầu nhớt và vật tư phụ tùng thay thế được tính riêng theo thực tế.</div>
        </section>

        <section class="panel">
          <div class="panel-head"><h2>Nội dung thực hiện</h2></div>
          <div class="panel-body article" id="detail-content" style="white-space: pre-line;">
            <c:out value="${service.content}"/>
          </div>
        </section>
      </div>

      <aside class="panel">
        <img class="illustration" src="<c:url value='/${not empty service.imagePath ? service.imagePath : "assets/images/garage.svg"}'/>" alt="Hình minh họa dịch vụ tại gara">
        <div class="panel-body">
          <h2>Chính sách bảo hành</h2>
          <dl class="detail-item">
            <dt>Thời hạn bảo hành</dt>
            <dd id="detail-warranty"><c:out value="${not empty service.warrantyPeriod ? service.warrantyPeriod : 'Chưa cập nhật thông tin bảo hành'}"/></dd>
          </dl>
          <div class="aside-note">
            <h3>Điều kiện bảo hành</h3>
            <p id="detail-conditions"><c:out value="${not empty service.warrantyTerms ? service.warrantyTerms : 'Chưa cập nhật thông tin bảo hành'}"/></p>
          </div>
        </div>
      </aside>
    </div>
  </c:when>

  <%-- NHÁNH 2: CHI TIẾT PHỤ TÙNG --%>
  <c:when test="${itemType == 'part'}">
    <div class="page-heading">
      <div>
        <div class="eyebrow">TRA CỨU / CHI TIẾT PHỤ TÙNG</div>
        <h1 id="detail-name"><c:out value="${sparePart.name}"/></h1>
        <p><span id="detail-code">PT<c:out value="${sparePart.id}"/></span> · <span id="detail-group"><c:out value="${sparePart.category != null ? sparePart.category.name : 'Chưa phân nhóm'}"/></span></p>
      </div>
      <a class="btn" href="<c:url value='/customer/catalog'><c:param name='keyword' value='${keyword}'/><c:param name='itemTypeFilter' value='${itemTypeFilter}'/></c:url>">← Quay lại tìm kiếm</a>
    </div>

    <div class="two-col">
      <div>
        <section class="panel">
          <div class="panel-head">
            <h2>Thông tin phụ tùng</h2>
            <span class="badge"><c:out value="${sparePart.status}"/></span>
          </div>
          <div class="panel-body">
            <p class="article" id="detail-description"><c:out value="${sparePart.description}"/></p>
            <div class="detail-grid">
              <dl class="detail-item">
                <dt>Giá bán niêm yết</dt>
                <dd>
                  <strong id="detail-price">
                    <c:choose>
                      <c:when test="${sparePart.sellingPrice != null}">
                        <fmt:formatNumber value="${sparePart.sellingPrice}" type="currency" currencySymbol="₫" maxFractionDigits="2"/>
                      </c:when>
                      <c:otherwise>Liên hệ trực tiếp</c:otherwise>
                    </c:choose>
                  </strong>
                </dd>
              </dl>
              <dl class="detail-item">
                <dt>Đơn vị tính</dt>
                <dd id="detail-unit"><c:out value="${sparePart.unit}"/></dd>
              </dl>
              <dl class="detail-item">
                <dt>Hãng sản xuất</dt>
                <dd id="detail-brand"><c:out value="${sparePart.manufacturer}"/></dd>
              </dl>
              <dl class="detail-item">
                <dt>Thông số kỹ thuật</dt>
                <dd id="detail-spec"><c:out value="${not empty sparePart.specifications ? sparePart.specifications : 'Chưa cập nhật'}"/></dd>
              </dl>
              <dl class="detail-item full-width">
                <dt>Dòng xe phù hợp</dt>
                <dd id="detail-cars"><c:out value="${not empty sparePart.compatibleVehicleModels ? sparePart.compatibleVehicleModels : 'Chưa cập nhật'}"/></dd>
              </dl>
            </div>
          </div>
        </section>

        <section class="panel">
          <div class="panel-head"><h2>Lưu ý khi lựa chọn</h2></div>
          <div class="panel-body article">
            Kiểm tra kỹ dòng xe, đời xe và mã phụ tùng trước khi lắp đặt. Kỹ thuật viên của GaraMan luôn sẵn sàng hỗ trợ kiểm tra tính tương thích cho xe của bạn.
          </div>
        </section>
      </div>

      <aside class="panel">
        <img class="illustration" id="detail-image" src="<c:url value='/${not empty sparePart.imagePath ? sparePart.imagePath : "assets/images/part.svg"}'/>" alt="Hình minh họa phụ tùng ô tô">
        <div class="panel-body">
          <h2>Chính sách bảo hành</h2>
          <dl class="detail-item">
            <dt>Thời hạn bảo hành</dt>
            <dd id="detail-warranty"><c:out value="${not empty sparePart.warrantyPeriod ? sparePart.warrantyPeriod : 'Chưa cập nhật thông tin bảo hành'}"/></dd>
          </dl>
          <div class="aside-note">
            <h3>Điều kiện bảo hành</h3>
            <p id="detail-conditions"><c:out value="${not empty sparePart.warrantyTerms ? sparePart.warrantyTerms : 'Chưa cập nhật thông tin bảo hành'}"/></p>
          </div>
        </div>
      </aside>
    </div>
  </c:when>
</c:choose>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
