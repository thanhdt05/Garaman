package controller;

import dao.SupplierDAO4;
import exception.ValidationException4;
import model.Supplier4;
import util.WebUtil4;

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
        request.getRequestDispatcher("/WEB-INF/view/warehouse/selectSupplier.jsp").forward(request, response);
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
            request.getRequestDispatcher("/WEB-INF/view/warehouse/selectSupplier.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi truy vấn cơ sở dữ liệu khi tìm kiếm nhà cung cấp.");
            request.setAttribute("keyword", trimmed);
            request.setAttribute("daTim", true);
            request.setAttribute("purchaseStep", 1);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/selectSupplier.jsp").forward(request, response);
        }
    }

    public void showNewSupplier(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        request.setAttribute("keyword", keyword != null ? keyword.trim() : "");
        request.setAttribute("purchaseStep", 1);
        request.getRequestDispatcher("/WEB-INF/view/warehouse/newSupplier.jsp").forward(request, response);
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
            WebUtil4.setFlash(request, "Đã thêm nhà cung cấp mới thành công: " + supplier.getName());

            String encodedName = URLEncoder.encode(supplier.getName(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers?keyword=" + encodedName + "&selectedId=" + newId);

        } catch (ValidationException4 ve) {
            request.setAttribute("errorMessage", ve.getMessage());
            if (ve.getField() != null) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put(ve.getField(), ve.getMessage());
                request.setAttribute("fieldErrors", fieldErrors);
            }
            request.setAttribute("purchaseStep", 1);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/newSupplier.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi CSDL khi lưu nhà cung cấp. Vui lòng kiểm tra lại thông tin.");
            request.setAttribute("purchaseStep", 1);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/newSupplier.jsp").forward(request, response);
        }
    }
}
