package controller;

import model.Member4;

import dao.SupplierDAO4;
import model.Supplier4;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SupplierServlet4 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private SupplierDAO4 supplierDAO;

    @Override
    public void init() throws ServletException {
        supplierDAO = new SupplierDAO4();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        javax.servlet.http.HttpSession entrySession = request.getSession(true);
        Member4 entryUser = (Member4) entrySession.getAttribute("currentUser4");

        if (entryUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!"WarehouseStaff".equals(entryUser.getRole())) {
            response.sendError(403, "Bạn không có quyền truy cập chức năng này");
            return;
        }

        String csrfToken = (String) entrySession.getAttribute("csrfToken4");
        if (csrfToken == null) {
            byte[] tokenBytes = new byte[32];
            new java.security.SecureRandom().nextBytes(tokenBytes);
            csrfToken = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
            entrySession.setAttribute("csrfToken4", csrfToken);
        }
        request.setAttribute("csrfToken", csrfToken);
        request.setAttribute("currentUser", entryUser);
        Object flashMessage = entrySession.getAttribute("flashMessage4");
        entrySession.removeAttribute("flashMessage4");
        if (flashMessage != null) request.setAttribute("flashMessage", flashMessage);

        String path = request.getServletPath();
        if ("/warehouse/suppliers/new".equals(path)) {
            showNewSupplier(request, response);
            return;
        }

        String keyword = request.getParameter("keyword");
        if (keyword == null) {
            showSupplierSelection(request, response);
        } else {
            searchSuppliers(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        javax.servlet.http.HttpSession entrySession = request.getSession(true);
        Member4 entryUser = (Member4) entrySession.getAttribute("currentUser4");

        if (entryUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!"WarehouseStaff".equals(entryUser.getRole())) {
            response.sendError(403, "Bạn không có quyền truy cập chức năng này");
            return;
        }

        String csrfToken = (String) entrySession.getAttribute("csrfToken4");
        if (csrfToken == null) {
            byte[] tokenBytes = new byte[32];
            new java.security.SecureRandom().nextBytes(tokenBytes);
            csrfToken = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
            entrySession.setAttribute("csrfToken4", csrfToken);
        }
        request.setAttribute("csrfToken", csrfToken);
        request.setAttribute("currentUser", entryUser);
        Object flashMessage = entrySession.getAttribute("flashMessage4");
        entrySession.removeAttribute("flashMessage4");
        if (flashMessage != null) request.setAttribute("flashMessage", flashMessage);

        String submittedToken = request.getParameter("csrfToken");
        if (submittedToken == null || !java.security.MessageDigest.isEqual(
        csrfToken.getBytes(java.nio.charset.StandardCharsets.UTF_8),
        submittedToken.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
            response.sendError(403, "CSRF token không hợp lệ hoặc đã hết hạn");
            return;
        }

        String path = request.getServletPath();
        if ("/warehouse/suppliers/create".equals(path)) {
            addSupplier(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Yêu cầu không được hỗ trợ");
    }

    public void showSupplierSelection(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String selectedId = request.getParameter("selectedId");
        request.setAttribute("selectedId", selectedId);
        request.setAttribute("daTim", false);
        request.setAttribute("purchaseStep", 1);
        request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/selectSupplier.jsp").forward(request, response);
    }

    public void searchSuppliers(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String trimmed = keyword != null ? keyword.trim() : "";
        String selectedId = request.getParameter("selectedId");

        try {
            List<Supplier4> suppliers = supplierDAO.findSuppliersByName(trimmed);
            request.setAttribute("keyword", trimmed);
            request.setAttribute("selectedId", selectedId);
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("daTim", true);
            request.setAttribute("purchaseStep", 1);
            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/selectSupplier.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi truy vấn cơ sở dữ liệu khi tìm kiếm nhà cung cấp.");
            request.setAttribute("keyword", trimmed);
            request.setAttribute("daTim", true);
            request.setAttribute("purchaseStep", 1);
            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/selectSupplier.jsp").forward(request, response);
        }
    }

    public void showNewSupplier(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        request.setAttribute("keyword", keyword != null ? keyword.trim() : "");
        request.setAttribute("purchaseStep", 1);
        request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/newSupplier.jsp").forward(request, response);
    }

    public void addSupplier(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String phoneNumber = request.getParameter("phoneNumber");
        String email = request.getParameter("email");

        Map<String, String> formValues = new HashMap<>();
        formValues.put("name", name != null ? name : "");
        formValues.put("address", address != null ? address : "");
        formValues.put("phoneNumber", phoneNumber != null ? phoneNumber : "");
        formValues.put("email", email != null ? email : "");
        request.setAttribute("formValues", formValues);

        Supplier4 supplier = new Supplier4();
        supplier.setName(name);
        supplier.setAddress(address);
        supplier.setPhoneNumber(phoneNumber);
        supplier.setEmail(email);

        try {
            int newId = supplierDAO.addSupplier(supplier);
            request.getSession().setAttribute("flashMessage4", "Đã thêm nhà cung cấp mới thành công: " + supplier.getName());

            String encodedName = URLEncoder.encode(supplier.getName(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers?keyword=" + encodedName + "&selectedId=" + newId);

        } catch (IllegalArgumentException ve) {
            String validationMessage = ve.getMessage();
            String validationField = null;
            int separator = validationMessage == null ? -1 : validationMessage.indexOf(": ");
            if (separator > 0) {
                validationField = validationMessage.substring(0, separator);
                validationMessage = validationMessage.substring(separator + 2);
            }
            request.setAttribute("errorMessage", validationMessage);
            if (validationField != null) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put(validationField, validationMessage);
                request.setAttribute("fieldErrors", fieldErrors);
            }
            request.setAttribute("purchaseStep", 1);
            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/newSupplier.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi CSDL khi lưu nhà cung cấp. Vui lòng kiểm tra lại thông tin.");
            request.setAttribute("purchaseStep", 1);
            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/newSupplier.jsp").forward(request, response);
        }
    }
}
