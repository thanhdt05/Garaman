package controller;

import dao.ServiceDAO4;
import dao.SparePartDAO4;
import exception.ValidationException4;
import model.Service4;
import model.SparePart4;
import util.Validation4;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CatalogServlet4 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ServiceDAO4 serviceDAO;
    private SparePartDAO4 sparePartDAO;

    @Override
    public void init() throws ServletException {
        serviceDAO = new ServiceDAO4();
        sparePartDAO = new SparePartDAO4();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/customer/catalog/detail".equals(path)) {
            xemChiTiet(request, response);
            return;
        }

        String tuKhoa = request.getParameter("tuKhoa");
        if (tuKhoa == null) {
            hienThiTimKiem(request, response);
        } else {
            timKiem(request, response);
        }
    }

    public void hienThiTimKiem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("daTim", false);
        request.getRequestDispatcher("/WEB-INF/view/customer/gdTimThongTin.jsp").forward(request, response);
    }

    public void timKiem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tuKhoa = request.getParameter("tuKhoa");
        String trimmed = tuKhoa != null ? tuKhoa.trim() : "";

        try {
            List<Service4> dsDichVu = serviceDAO.timDichVuTheoTen(trimmed);
            List<SparePart4> dsPhuTung = sparePartDAO.timPhuTungTheoTen(trimmed, true);

            request.setAttribute("tuKhoa", trimmed);
            request.setAttribute("dsDichVu", dsDichVu);
            request.setAttribute("dsPhuTung", dsPhuTung);
            request.setAttribute("daTim", true);

            request.getRequestDispatcher("/WEB-INF/view/customer/gdTimThongTin.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Hệ thống đang gặp sự cố khi tra cứu dữ liệu. Vui lòng thử lại sau.");
            request.setAttribute("tuKhoa", trimmed);
            request.setAttribute("daTim", true);
            request.getRequestDispatcher("/WEB-INF/view/customer/gdTimThongTin.jsp").forward(request, response);
        }
    }

    public void xemChiTiet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String loai = request.getParameter("loai");
        String tuKhoa = request.getParameter("tuKhoa");
        request.setAttribute("tuKhoa", tuKhoa != null ? tuKhoa.trim() : "");

        int id;
        try {
            id = Validation4.idDuong(idStr, "id");
        } catch (ValidationException4 e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã định danh không hợp lệ: " + idStr);
            return;
        }

        if ("service".equalsIgnoreCase(loai)) {
            try {
                Service4 dv = serviceDAO.layChiTietDichVu(id);
                if (dv == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy dịch vụ hoặc dịch vụ đã ngừng cung cấp");
                    return;
                }
                request.setAttribute("dichVu", dv);
                request.setAttribute("loai", "service");
                request.getRequestDispatcher("/WEB-INF/view/customer/gdChiTietThongTin.jsp").forward(request, response);
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi truy vấn CSDL khi tải chi tiết dịch vụ");
            }
        } else if ("part".equalsIgnoreCase(loai)) {
            try {
                SparePart4 pt = sparePartDAO.layChiTietPhuTung(id, true);
                if (pt == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy phụ tùng hoặc phụ tùng chưa công bố/ngừng kinh doanh");
                    return;
                }
                request.setAttribute("phuTung", pt);
                request.setAttribute("loai", "part");
                request.getRequestDispatcher("/WEB-INF/view/customer/gdChiTietThongTin.jsp").forward(request, response);
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi truy vấn CSDL khi tải chi tiết phụ tùng");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Loại thông tin tra cứu không hợp lệ (chỉ chấp nhận 'service' hoặc 'part')");
        }
    }
}
