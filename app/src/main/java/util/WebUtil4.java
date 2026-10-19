package util;

import dto.SessionUser4;
import exception.ValidationException4;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public final class WebUtil4 {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private WebUtil4() {
        // Private constructor
    }

    public static void forward(HttpServletRequest req, HttpServletResponse res, String view) throws ServletException, IOException {
        req.getRequestDispatcher(view).forward(req, res);
    }

    public static void redirect(HttpServletRequest req, HttpServletResponse res, String path) throws IOException {
        if (path == null || !path.startsWith("/")) {
            throw new IllegalArgumentException("Đường dẫn redirect phải bắt đầu bằng /: " + path);
        }
        res.sendRedirect(req.getContextPath() + path);
    }

    public static SessionUser4 requireUser(HttpServletRequest req) throws ValidationException4 {
        HttpSession session = req.getSession(false);
        if (session == null) {
            throw new ValidationException4("quyen", "Phiên làm việc đã hết hạn hoặc chưa đăng nhập");
        }
        SessionUser4 user = (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER);
        if (user == null) {
            throw new ValidationException4("quyen", "Bạn cần đăng nhập để thực hiện chức năng này");
        }
        return user;
    }

    public static String taoCsrfToken(HttpSession session) {
        if (session == null) {
            throw new IllegalArgumentException("Session không được null khi tạo CSRF token");
        }
        String token = (String) session.getAttribute(AppConstants4.SESSION_CSRF);
        if (token == null || token.trim().isEmpty()) {
            byte[] bytes = new byte[32];
            SECURE_RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(AppConstants4.SESSION_CSRF, token);
        }
        return token;
    }

    public static boolean hopLeCsrf(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }
        String sessionToken = (String) session.getAttribute(AppConstants4.SESSION_CSRF);
        if (sessionToken == null || sessionToken.isEmpty()) {
            return false;
        }

        String reqToken = req.getParameter("csrfToken");
        if (reqToken == null || reqToken.isEmpty()) {
            return false;
        }

        return MessageDigest.isEqual(sessionToken.getBytes(), reqToken.getBytes());
    }

    public static void flash(HttpServletRequest req, String message) {
        HttpSession session = req.getSession();
        session.setAttribute(AppConstants4.SESSION_FLASH, message);
    }

    public static void datFlash(HttpServletRequest req, String message) {
        flash(req, message);
    }

    public static String layFlash(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        String message = (String) session.getAttribute(AppConstants4.SESSION_FLASH);
        if (message != null) {
            session.removeAttribute(AppConstants4.SESSION_FLASH);
        }
        return message;
    }

    public static String dinhDangThoiDiem(LocalDateTime value) {
        if (value == null) {
            return "Chưa ghi nhận";
        }
        return value.format(DATE_TIME_FORMATTER);
    }
}
