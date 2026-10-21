package controller;

import dao.MemberDAO4;
import dto.SessionUser4;
import model.Employee4;
import model.Member4;
import util.AppConstants4;
import util.PasswordUtil4;
import util.WebUtil4;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthServlet4 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private MemberDAO4 memberDAO;

    @Override
    public void init() throws ServletException {
        memberDAO = new MemberDAO4();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/logout".equals(path)) {
            logout(request, response);
            return;
        }
        showLogin(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/logout".equals(path)) {
            logout(request, response);
            return;
        }
        login(request, response);
    }

    public void showLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(AppConstants4.SESSION_USER) != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        String msg = request.getParameter("msg");
        if ("logged_out".equals(msg)) {
            request.setAttribute("infoMessage", "Bạn đã đăng xuất khỏi hệ thống thành công.");
        }

        request.getRequestDispatcher("/WEB-INF/view/member/login.jsp").forward(request, response);
    }

    public void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            request.setAttribute("username", username != null ? username.trim() : "");
            request.getRequestDispatcher("/WEB-INF/view/member/login.jsp").forward(request, response);
            return;
        }

        try {
            Member4 member = memberDAO.findByUsername(username.trim());
            if (member == null) {
                request.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác.");
                request.setAttribute("username", username.trim());
                request.getRequestDispatcher("/WEB-INF/view/member/login.jsp").forward(request, response);
                return;
            }

            if (!AppConstants4.ACCOUNT_STATUS_ACTIVE.equals(member.getAccountStatus())) {
                request.setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa hoặc chưa được kích hoạt.");
                request.setAttribute("username", username.trim());
                request.getRequestDispatcher("/WEB-INF/view/member/login.jsp").forward(request, response);
                return;
            }

            if (member instanceof Employee4) {
                Employee4 emp = (Employee4) member;
                if (!AppConstants4.EMPLOYMENT_STATUS_ACTIVE.equals(emp.getEmploymentStatus())) {
                    request.setAttribute("errorMessage", "Nhân viên đã ngừng làm việc tại Gara.");
                    request.setAttribute("username", username.trim());
                    request.getRequestDispatcher("/WEB-INF/view/member/login.jsp").forward(request, response);
                    return;
                }
            }

            if (!PasswordUtil4.verifyPassword(password, member.getPassword())) {
                request.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác.");
                request.setAttribute("username", username.trim());
                request.getRequestDispatcher("/WEB-INF/view/member/login.jsp").forward(request, response);
                return;
            }

            // Đăng nhập hợp lệ: làm mới session để chống Session Fixation
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession = request.getSession(true);
            WebUtil4.createCsrfToken(newSession);

            SessionUser4 sessionUser = new SessionUser4(
                    member.getId(),
                    member.getFullName(),
                    member.getRole(),
                    member.getUsername()
            );
            newSession.setAttribute(AppConstants4.SESSION_USER, sessionUser);

            response.sendRedirect(request.getContextPath() + "/home");

        } catch (SQLException e) {
            // Không lộ chi tiết SQL ra client
            request.setAttribute("errorMessage", "Hệ thống đang gặp sự cố kết nối CSDL. Vui lòng thử lại sau.");
            request.setAttribute("username", username.trim());
            request.getRequestDispatcher("/WEB-INF/view/member/login.jsp").forward(request, response);
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login?msg=logged_out");
    }
}
