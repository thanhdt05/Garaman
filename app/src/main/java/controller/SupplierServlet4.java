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
            hienThiThemNhaCungCap(request, response);
            return;
        }

        String tuKhoa = request.getParameter("tuKhoa");
        if (tuKhoa == null) {
            hienThiChonNhaCungCap(request, response);
        } else {
            timNhaCungCap(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/warehouse/suppliers/create".equals(path)) {
            themNhaCungCap(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Yêu cầu không được hỗ trợ");
    }

    public void hienThiChonNhaCungCap(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String selectedId = request.getParameter("selectedId");
        request.setAttribute("selectedId", selectedId);
        request.setAttribute("daTim", false);
        request.setAttribute("buocNhap", 1);
        request.getRequestDispatcher("/WEB-INF/view/warehouse/gdChonNhaCungCap.jsp").forward(request, response);
    }

    public void timNhaCungCap(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tuKhoa = request.getParameter("tuKhoa");
        String trimmed = tuKhoa != null ? tuKhoa.trim() : "";
        String selectedId = request.getParameter("selectedId");

        try {
            List<Supplier4> dsNhaCungCap = supplierDAO.timNhaCungCapTheoTen(trimmed);
            request.setAttribute("tuKhoa", trimmed);
            request.setAttribute("selectedId", selectedId);
            request.setAttribute("dsNhaCungCap", dsNhaCungCap);
            request.setAttribute("daTim", true);
            request.setAttribute("buocNhap", 1);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdChonNhaCungCap.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi truy vấn cơ sở dữ liệu khi tìm kiếm nhà cung cấp.");
            request.setAttribute("tuKhoa", trimmed);
            request.setAttribute("daTim", true);
            request.setAttribute("buocNhap", 1);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdChonNhaCungCap.jsp").forward(request, response);
        }
    }

    public void hienThiThemNhaCungCap(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tuKhoa = request.getParameter("tuKhoa");
        request.setAttribute("tuKhoa", tuKhoa != null ? tuKhoa.trim() : "");
        request.setAttribute("buocNhap", 1);
        request.getRequestDispatcher("/WEB-INF/view/warehouse/gdThemNhaCungCap.jsp").forward(request, response);
    }

    public void themNhaCungCap(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String ten = request.getParameter("ten");
        String diaChi = request.getParameter("diaChi");
        String dienThoai = request.getParameter("dienThoai");
        String email = request.getParameter("email");

        Map<String, String> formValues = new HashMap<>();
        formValues.put("ten", ten != null ? ten : "");
        formValues.put("diaChi", diaChi != null ? diaChi : "");
        formValues.put("dienThoai", dienThoai != null ? dienThoai : "");
        formValues.put("email", email != null ? email : "");
        request.setAttribute("formValues", formValues);

        Supplier4 supplier = new Supplier4();
        supplier.setTen(ten);
        supplier.setDiaChi(diaChi);
        supplier.setDienThoai(dienThoai);
        supplier.setEmail(email);

        try {
            int newId = supplierDAO.themNhaCungCap(supplier);
            WebUtil4.datFlash(request, "Đã thêm nhà cung cấp mới thành công: " + supplier.getTen());

            String encodedName = URLEncoder.encode(supplier.getTen(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers?tuKhoa=" + encodedName + "&selectedId=" + newId);

        } catch (ValidationException4 ve) {
            request.setAttribute("errorMessage", ve.getMessage());
            if (ve.getField() != null) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put(ve.getField(), ve.getMessage());
                request.setAttribute("fieldErrors", fieldErrors);
            }
            request.setAttribute("buocNhap", 1);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdThemNhaCungCap.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi CSDL khi lưu nhà cung cấp. Vui lòng kiểm tra lại thông tin.");
            request.setAttribute("buocNhap", 1);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdThemNhaCungCap.jsp").forward(request, response);
        }
    }
}
