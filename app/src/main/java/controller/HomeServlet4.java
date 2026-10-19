package controller;

import dto.SessionUser4;
import util.AppConstants4;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HomeServlet4 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        hienThiTrangChu(request, response);
    }

    public void hienThiTrangChu(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        SessionUser4 currentUser = session != null ? (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER) : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String vaiTro = currentUser.getVaiTro();
        if (AppConstants4.ROLE_CUSTOMER.equals(vaiTro)) {
            request.getRequestDispatcher("/WEB-INF/view/customer/gdChinhKH.jsp").forward(request, response);
        } else if (AppConstants4.ROLE_WAREHOUSE_STAFF.equals(vaiTro)) {
            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdChinhKho.jsp").forward(request, response);
        } else {
            // Đối với Manager hoặc SalesStaff chưa có module chạy trong phạm vi đề 26
            request.setAttribute("thongBaoPhamVi", "Tài khoản hiện tại có vai trò '" + vaiTro +
                    "'. Theo phạm vi đồ án Đề 26 (PTIT), hệ thống hiện chỉ hỗ trợ các nghiệp vụ cho Khách hàng (Tra cứu dịch vụ/phụ tùng) và Nhân viên kho (Nhập phụ tùng).");
            request.getRequestDispatcher("/WEB-INF/view/common/error.jsp").forward(request, response);
        }
    }
}
