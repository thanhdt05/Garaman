<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    if (statusCode == null) {
        statusCode = response.getStatus();
    }
    if (statusCode == 200) {
        statusCode = 500;
    }
    request.setAttribute("statusCode", statusCode);

    String errorTitle = "Lỗi hệ thống";
    String errorDesc = "Đã xảy ra sự cố trong quá trình xử lý yêu cầu.";

    if (statusCode == 400) {
        errorTitle = "Yêu cầu không hợp lệ (400)";
        errorDesc = "Dữ liệu gửi lên không đúng định dạng hoặc thiếu thông tin bắt buộc.";
    } else if (statusCode == 403) {
        errorTitle = "Từ chối truy cập (403)";
        errorDesc = "Bạn không có quyền thực hiện thao tác này hoặc phiên làm việc đã hết hạn.";
    } else if (statusCode == 404) {
        errorTitle = "Không tìm thấy trang hoặc dữ liệu (404)";
        errorDesc = "Tài nguyên hoặc dữ liệu bạn yêu cầu không tồn tại trên hệ thống.";
    } else if (statusCode == 405) {
        errorTitle = "Phương thức không được hỗ trợ (405)";
        errorDesc = "Phương thức HTTP này không được phép trên đường dẫn yêu cầu.";
    } else if (statusCode == 409) {
        errorTitle = "Xung đột dữ liệu (409)";
        errorDesc = "Yêu cầu xung đột với trạng thái hiện tại (phiên bản dữ liệu cũ hoặc đang được xử lý đồng thời). Vui lòng tải lại trang.";
    } else if (statusCode == 500) {
        errorTitle = "Lỗi máy chủ nội bộ (500)";
        errorDesc = "Hệ thống gặp sự cố xử lý. Vui lòng thử lại sau hoặc liên hệ quản trị viên.";
    }
    request.setAttribute("errorTitle", errorTitle);
    request.setAttribute("errorDesc", errorDesc);
%>
<jsp:include page="/WEB-INF/view/common/header.jspf">
  <jsp:param name="pageTitle" value="${errorTitle}" />
  <jsp:param name="breadcrumb" value="Thông báo sự cố" />
</jsp:include>

<div class="panel form-panel" style="margin-top: 20px;">
  <div class="panel-head">
    <div>
      <h2 style="color: #a45348;"><c:out value="${errorTitle}" /></h2>
      <p><c:out value="${errorDesc}" /></p>
    </div>
  </div>
  <div class="panel-body">
    <c:if test="${not empty errorMessage}">
      <div class="notice" style="background: #fdf2f2; color: #991b1b; border-color: #f87171;">
        <c:out value="${errorMessage}" />
      </div>
    </c:if>

    <div style="margin-top: 25px; display: flex; gap: 15px;">
      <a href="<c:url value='/home'/>" class="btn btn-primary">Về trang chủ</a>
      <a href="javascript:history.back()" class="btn">Quay lại trang trước</a>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/view/common/footer.jspf" />
