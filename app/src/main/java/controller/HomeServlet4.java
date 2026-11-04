package controller;

import model.Member4;

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
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        javax.servlet.http.HttpSession entrySession = request.getSession(true);
        Member4 entryUser = (Member4) entrySession.getAttribute("currentUser4");

        if (entryUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
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

        showHome(request, response);
    }

    public void showHome(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Member4 currentUser = session != null ? (Member4) session.getAttribute("currentUser4") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = currentUser.getRole();
        if ("Customer".equals(role)) {
            request.getRequestDispatcher("/WEB-INF/view/customer/customerHome.jsp").forward(request, response);
        } else if ("WarehouseStaff".equals(role)) {
            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/warehouseHome.jsp").forward(request, response);
        } else {
            // Đối với Manager hoặc SalesStaff chưa có module chạy trong phạm vi đề 26
            request.setAttribute("thongBaoPhamVi", "Tài khoản hiện tại có vai trò '" + role +
            "'. Theo phạm vi đồ án Đề 26 (PTIT), hệ thống hiện chỉ hỗ trợ các nghiệp vụ cho Khách hàng (Tra cứu dịch vụ/phụ tùng) và Nhân viên kho (Nhập phụ tùng).");
            request.getRequestDispatcher("/WEB-INF/view/common/error.jsp").forward(request, response);
        }
    }
}
