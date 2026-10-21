package filter;

import dto.SessionUser4;
import util.AppConstants4;
import util.WebUtil4;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthFilter4 implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();
        String method = req.getMethod();

        // Tài nguyên tĩnh công khai
        if (path.startsWith("/assets/") || path.startsWith("/favicon.ico")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(true);
        String token = WebUtil4.createCsrfToken(session);
        req.setAttribute(AppConstants4.ATTR_CSRF_TOKEN, token);

        SessionUser4 currentUser = (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER);
        if (currentUser != null) {
            req.setAttribute(AppConstants4.ATTR_CURRENT_USER, currentUser);
        }

        String flash = WebUtil4.getFlash(req);
        if (flash != null) {
            req.setAttribute(AppConstants4.ATTR_FLASH_MESSAGE, flash);
        }

        // Trang đăng nhập
        if ("/login".equals(path)) {
            if ("POST".equalsIgnoreCase(method)) {
                if (!WebUtil4.isValidCsrfToken(req)) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token không hợp lệ hoặc đã hết hạn");
                    return;
                }
            }
            chain.doFilter(request, response);
            return;
        }

        // Các đường dẫn cần bảo vệ
        boolean isProtected = "/home".equals(path) || "/logout".equals(path) ||
                              path.startsWith("/customer/") || path.startsWith("/warehouse/");

        if (isProtected) {
            if (currentUser == null) {
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            // Kiểm tra quyền theo vai trò
            if (path.startsWith("/customer/")) {
                if (!AppConstants4.ROLE_CUSTOMER.equals(currentUser.getRole())) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập khu vực khách hàng");
                    return;
                }
            } else if (path.startsWith("/warehouse/")) {
                if (!AppConstants4.ROLE_WAREHOUSE_STAFF.equals(currentUser.getRole())) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập khu vực kho phụ tùng");
                    return;
                }
            }

            // Kiểm tra CSRF cho mọi POST được bảo vệ
            if ("POST".equalsIgnoreCase(method)) {
                if (!WebUtil4.isValidCsrfToken(req)) {
                    res.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token không hợp lệ hoặc đã hết hạn");
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup
    }
}
