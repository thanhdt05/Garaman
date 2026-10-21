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
            viewDetails(request, response);
            return;
        }

        String keyword = request.getParameter("keyword");
        if (keyword == null) {
            showSearch(request, response);
        } else {
            searchCatalog(request, response);
        }
    }

    public void showSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String itemTypeFilter = request.getParameter("itemTypeFilter");
        if (itemTypeFilter == null || itemTypeFilter.trim().isEmpty()) {
            itemTypeFilter = "all";
        } else if (!"all".equals(itemTypeFilter) && !"service".equals(itemTypeFilter) && !"part".equals(itemTypeFilter)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bộ lọc không hợp lệ: " + itemTypeFilter);
            return;
        }

        request.setAttribute("hasSearched", false);
        request.setAttribute("itemTypeFilter", itemTypeFilter);
        request.setAttribute("keyword", "");
        request.getRequestDispatcher("/WEB-INF/view/customer/catalogSearch.jsp").forward(request, response);
    }

    public void searchCatalog(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String itemTypeFilter = request.getParameter("itemTypeFilter");
        if (itemTypeFilter == null || itemTypeFilter.trim().isEmpty()) {
            itemTypeFilter = "all";
        } else if (!"all".equals(itemTypeFilter) && !"service".equals(itemTypeFilter) && !"part".equals(itemTypeFilter)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bộ lọc không hợp lệ: " + itemTypeFilter);
            return;
        }

        String keyword = request.getParameter("keyword");
        String trimmed = keyword != null ? keyword.trim() : "";

        // Ngoại lệ 3a: từ khóa rỗng hoặc toàn khoảng trắng -> báo lỗi trước query
        if (trimmed.isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập tên dịch vụ hoặc phụ tùng cần tìm.");
            request.setAttribute("hasSearched", false);
            request.setAttribute("keyword", "");
            request.setAttribute("itemTypeFilter", itemTypeFilter);
            request.getRequestDispatcher("/WEB-INF/view/customer/catalogSearch.jsp").forward(request, response);
            return;
        }

        if (trimmed.length() > 255) {
            request.setAttribute("errorMessage", "Từ khóa tìm kiếm không được vượt quá 255 ký tự.");
            request.setAttribute("hasSearched", false);
            request.setAttribute("keyword", trimmed);
            request.setAttribute("itemTypeFilter", itemTypeFilter);
            request.getRequestDispatcher("/WEB-INF/view/customer/catalogSearch.jsp").forward(request, response);
            return;
        }

        try {
            List<Service4> services = serviceDAO.findServicesByName(trimmed);
            List<SparePart4> spareParts = sparePartDAO.findSparePartsByName(trimmed, true);

            request.setAttribute("keyword", trimmed);
            request.setAttribute("itemTypeFilter", itemTypeFilter);
            request.setAttribute("services", services);
            request.setAttribute("spareParts", spareParts);
            request.setAttribute("hasSearched", true);

            request.getRequestDispatcher("/WEB-INF/view/customer/catalogSearch.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Hệ thống đang gặp sự cố khi tra cứu dữ liệu. Vui lòng thử lại sau.");
            request.setAttribute("keyword", trimmed);
            request.setAttribute("itemTypeFilter", itemTypeFilter);
            request.setAttribute("hasSearched", false);
            request.getRequestDispatcher("/WEB-INF/view/customer/catalogSearch.jsp").forward(request, response);
        }
    }

    public void viewDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String itemType = request.getParameter("itemType");
        String keyword = request.getParameter("keyword");
        String itemTypeFilter = request.getParameter("itemTypeFilter");
        if (itemTypeFilter == null || itemTypeFilter.trim().isEmpty()) {
            itemTypeFilter = "all";
        } else if (!"all".equals(itemTypeFilter) && !"service".equals(itemTypeFilter) && !"part".equals(itemTypeFilter)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bộ lọc không hợp lệ: " + itemTypeFilter);
            return;
        }

        request.setAttribute("keyword", keyword != null ? keyword.trim() : "");
        request.setAttribute("itemTypeFilter", itemTypeFilter);

        int id;
        try {
            id = Validation4.positiveId(idStr, "id");
        } catch (ValidationException4 e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã định danh không hợp lệ: " + idStr);
            return;
        }

        if ("service".equalsIgnoreCase(itemType)) {
            try {
                Service4 service = serviceDAO.getServiceDetails(id);
                if (service == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy dịch vụ hoặc dịch vụ đã ngừng cung cấp");
                    return;
                }
                request.setAttribute("service", service);
                request.setAttribute("itemType", "service");
                request.getRequestDispatcher("/WEB-INF/view/customer/itemDetails.jsp").forward(request, response);
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi truy vấn CSDL khi tải chi tiết dịch vụ");
            }
        } else if ("part".equalsIgnoreCase(itemType)) {
            try {
                SparePart4 sparePart = sparePartDAO.getSparePartDetails(id, true);
                if (sparePart == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy phụ tùng hoặc phụ tùng chưa công bố/ngừng kinh doanh");
                    return;
                }
                request.setAttribute("sparePart", sparePart);
                request.setAttribute("itemType", "part");
                request.getRequestDispatcher("/WEB-INF/view/customer/itemDetails.jsp").forward(request, response);
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi truy vấn CSDL khi tải chi tiết phụ tùng");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Loại thông tin tra cứu không hợp lệ (chỉ chấp nhận 'service' hoặc 'part')");
        }
    }
}
