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
    <p>Nhập tên hoặc một phần tên để tìm thông tin bạn cần.</p>
  </div>
</div>

<section class="panel">
  <div class="panel-body">
    <form id="catalog-search" class="search-form" action="<c:url value='/customer/catalog'/>" method="get" novalidate>
      <div class="field">
        <label for="itemTypeFilter">Loại thông tin</label>
        <select id="itemTypeFilter" name="itemTypeFilter">
          <option value="all" ${itemTypeFilter == 'all' || empty itemTypeFilter ? 'selected' : ''}>Dịch vụ &amp; phụ tùng</option>
          <option value="service" ${itemTypeFilter == 'service' ? 'selected' : ''}>Dịch vụ</option>
          <option value="part" ${itemTypeFilter == 'part' ? 'selected' : ''}>Phụ tùng</option>
        </select>
      </div>
      <div class="field">
        <label for="keyword">Tên dịch vụ / phụ tùng</label>
        <input id="keyword" name="keyword" placeholder="Ví dụ: lọc" maxlength="150" required
               value="<c:out value='${keyword}'/>" aria-describedby="catalog-search-error" autofocus>
      </div>
      <button class="btn btn-primary" type="submit" id="btn-search">Tìm kiếm</button>
    </form>
    <c:if test="${not empty errorMessage}">
      <p class="error" id="catalog-search-error" role="alert"><c:out value="${errorMessage}"/></p>
    </c:if>
  </div>
</section>

<section class="panel">
  <c:choose>
    <c:when test="${not hasSearched}">
      <div class="panel-head">
        <h2>Kết quả tìm kiếm</h2>
        <span class="result-count" id="result-count">Chưa tìm kiếm</span>
      </div>
      <div class="empty" id="search-initial">
        <strong>Bạn muốn tìm thông tin gì?</strong>
        Nhập tên dịch vụ hoặc phụ tùng và chọn Tìm kiếm.
      </div>
    </c:when>

    <c:otherwise>
      <c:set var="hienThiDV" value="${itemTypeFilter != 'part'}" />
      <c:set var="hienThiPT" value="${itemTypeFilter != 'service'}" />
      <c:set var="soDV" value="${(hienThiDV and not empty services) ? services.size() : 0}" />
      <c:set var="soPT" value="${(hienThiPT and not empty spareParts) ? spareParts.size() : 0}" />
      <c:set var="totalCount" value="${soDV + soPT}" />

      <div class="panel-head">
        <h2>Kết quả tìm kiếm</h2>
        <span class="result-count" id="result-count"><c:out value="${totalCount}"/> kết quả</span>
      </div>

      <c:choose>
        <c:when test="${totalCount == 0}">
          <div class="empty" id="search-empty">
            <strong>Không tìm thấy kết quả</strong>
            Thử tên khác hoặc chọn lại loại thông tin.
          </div>
        </c:when>

        <c:otherwise>
          <div class="table-wrap" id="search-results">
            <table id="catalog-table">
              <thead>
                <tr>
                  <th>Mã</th>
                  <th>Tên dịch vụ / phụ tùng</th>
                  <th>Loại</th>
                  <th class="number">Đơn giá / Giá bán</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <c:if test="${hienThiDV}">
                  <c:forEach var="service" items="${services}">
                    <tr data-type="service" data-key="<c:out value='${service.name}'/>">
                      <td class="code">DV<c:out value="${service.id}"/></td>
                      <td>
                        <strong><c:out value="${service.name}"/></strong>
                        <small><c:out value="${service.category}"/></small>
                      </td>
                      <td><span class="badge neutral">Dịch vụ</span></td>
                      <td class="number">
                        <fmt:formatNumber value="${service.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="2"/>
                      </td>
                      <td class="number">
                        <a class="text-link" href="<c:url value='/customer/catalog/detail'><c:param name='id' value='${service.id}'/><c:param name='itemType' value='service'/><c:param name='keyword' value='${keyword}'/><c:param name='itemTypeFilter' value='${itemTypeFilter}'/></c:url>">
                          Xem chi tiết →
                        </a>
                      </td>
                    </tr>
                  </c:forEach>
                </c:if>

                <c:if test="${hienThiPT}">
                  <c:forEach var="sparePart" items="${spareParts}">
                    <tr data-type="part" data-key="<c:out value='${sparePart.name}'/>">
                      <td class="code">PT<c:out value="${sparePart.id}"/></td>
                      <td>
                        <strong><c:out value="${sparePart.name}"/></strong>
                        <small><c:out value="${sparePart.manufacturer}"/> · Đơn vị: <c:out value="${sparePart.unit}"/></small>
                      </td>
                      <td><span class="badge">Phụ tùng</span></td>
                      <td class="number">
                        <c:choose>
                          <c:when test="${sparePart.sellingPrice != null}">
                            <fmt:formatNumber value="${sparePart.sellingPrice}" type="currency" currencySymbol="₫" maxFractionDigits="2"/>
                          </c:when>
                          <c:otherwise>Liên hệ</c:otherwise>
                        </c:choose>
                      </td>
                      <td class="number">
                        <a class="text-link" href="<c:url value='/customer/catalog/detail'><c:param name='id' value='${sparePart.id}'/><c:param name='itemType' value='part'/><c:param name='keyword' value='${keyword}'/><c:param name='itemTypeFilter' value='${itemTypeFilter}'/></c:url>">
                          Xem chi tiết →
                        </a>
                      </td>
                    </tr>
                  </c:forEach>
                </c:if>
              </tbody>
            </table>
          </div>
        </c:otherwise>
      </c:choose>

      <div class="panel-note">Chọn “Xem chi tiết” để xem thông tin và chính sách bảo hành của từng mục.</div>
    </c:otherwise>
  </c:choose>
</section>

<a class="btn btn-quiet" href="<c:url value='/home'/>">← Về trang chính</a>

<%@ include file="/WEB-INF/view/common/footer.jspf" %>
