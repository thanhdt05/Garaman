package controller;

import dao.MemberDAO4;
import model.Employee4;
import model.Member4;

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
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        javax.servlet.http.HttpSession entrySession = request.getSession(true);
        Member4 entryUser = (Member4) entrySession.getAttribute("currentUser4");

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
        if ("/logout".equals(path)) {
            logout(request, response);
            return;
        }
        showLogin(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        javax.servlet.http.HttpSession entrySession = request.getSession(true);
        Member4 entryUser = (Member4) entrySession.getAttribute("currentUser4");

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
        if ("/logout".equals(path)) {
            logout(request, response);
            return;
        }
        login(request, response);
    }

    public void showLogin(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser4") != null) {
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

            if (!"Hoạt động".equals(member.getAccountStatus())) {
                request.setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa hoặc chưa được kích hoạt.");
                request.setAttribute("username", username.trim());
                request.getRequestDispatcher("/WEB-INF/view/member/login.jsp").forward(request, response);
                return;
            }

            if (member instanceof Employee4) {
                Employee4 emp = (Employee4) member;
                if (!"Đang làm việc".equals(emp.getEmploymentStatus())) {
                    request.setAttribute("errorMessage", "Nhân viên đã ngừng làm việc tại Gara.");
                    request.setAttribute("username", username.trim());
                    request.getRequestDispatcher("/WEB-INF/view/member/login.jsp").forward(request, response);
                    return;
                }
            }

            boolean validPassword = false;
            String storedHash = member.getPassword();
            try {
                String[] hashParts = storedHash == null ? new String[0] : storedHash.split("\\$");
                if (hashParts.length == 4 && "pbkdf2_sha256".equals(hashParts[0])) {
                    int iterations = Integer.parseInt(hashParts[1]);
                    byte[] salt = java.util.Base64.getDecoder().decode(hashParts[2]);
                    byte[] expected = java.util.Base64.getDecoder().decode(hashParts[3]);
                    if (iterations > 0 && expected.length > 0) {
                        javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                        password.toCharArray(), salt, iterations, expected.length * 8);
                        byte[] actual = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                        .generateSecret(spec).getEncoded();
                        validPassword = java.security.MessageDigest.isEqual(expected, actual);
                        spec.clearPassword();
                    }
                }
            } catch (IllegalArgumentException | java.security.GeneralSecurityException invalidHash) {
                validPassword = false;
            }
            if (!validPassword) {
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
            byte[] tokenBytes = new byte[32];
            new java.security.SecureRandom().nextBytes(tokenBytes);
            newSession.setAttribute("csrfToken4", java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes));

            member.setPassword(null);
            newSession.setAttribute("currentUser4", member);

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
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.setHeader("Allow", "POST");
            response.sendError(405);
            return;
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login?msg=logged_out");
    }
}
