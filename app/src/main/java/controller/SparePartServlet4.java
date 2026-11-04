package controller;

import model.Member4;
import java.math.BigDecimal;

import dao.SparePartCategoryDAO4;
import dao.SparePartDAO4;
import model.PurchaseInvoice4;
import model.SparePart4;
import model.SparePartCategory4;

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
import javax.servlet.http.HttpSession;

public class SparePartServlet4 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private SparePartCategoryDAO4 categoryDAO;
    private SparePartDAO4 sparePartDAO;

    @Override
    public void init() throws ServletException {
        categoryDAO = new SparePartCategoryDAO4();
        sparePartDAO = new SparePartDAO4();
    }

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

        if (!"WarehouseStaff".equals(entryUser.getRole())) {
            response.sendError(403, "Bạn không có quyền truy cập chức năng này");
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

        String path = request.getServletPath();
        if ("/warehouse/parts/new".equals(path)) {
            showNewSparePart(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        javax.servlet.http.HttpSession entrySession = request.getSession(true);
        Member4 entryUser = (Member4) entrySession.getAttribute("currentUser4");

        if (entryUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!"WarehouseStaff".equals(entryUser.getRole())) {
            response.sendError(403, "Bạn không có quyền truy cập chức năng này");
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

        String submittedToken = request.getParameter("csrfToken");
        if (submittedToken == null || !java.security.MessageDigest.isEqual(
        csrfToken.getBytes(java.nio.charset.StandardCharsets.UTF_8),
        submittedToken.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
            response.sendError(403, "CSRF token không hợp lệ hoặc đã hết hạn");
            return;
        }

        String path = request.getServletPath();
        if ("/warehouse/parts/create".equals(path)) {
            addSparePart(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Yêu cầu không được hỗ trợ");
    }

    public void showNewSparePart(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String requestCode = request.getParameter("requestCode");
        HttpSession session = request.getSession(false);

        PurchaseInvoice4 draft = null;
        long draftVersion = 1L;
        if (session != null && requestCode != null && session.getAttribute("purchaseDrafts4") instanceof Map
        && ((Map<?, ?>) session.getAttribute("purchaseDrafts4")).containsKey(requestCode)) {
            try {
                draft = ((Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).get(requestCode);
                draftVersion = ((Map<String, Long>) session.getAttribute("draftVersions4")).get(requestCode);
            } catch (IllegalArgumentException ignored) {
                draft = null;
            }
        }

        if (draft == null) {
            request.getSession().setAttribute("flashMessage4", "Phiên lập hóa đơn không tồn tại hoặc đã hết hạn. Vui lòng chọn lại nhà cung cấp.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }

        try {
            List<SparePartCategory4> categories = categoryDAO.getActiveCategories();
            request.setAttribute("categories", categories);
            request.setAttribute("requestCode", requestCode);
            request.setAttribute("draftVersion", draftVersion);
            request.setAttribute("purchaseStep", 2);

            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/newSparePart.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi CSDL khi tải danh mục nhóm phụ tùng.");
            request.setAttribute("purchaseStep", 2);
            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/newSparePart.jsp").forward(request, response);
        }
    }

    public void addSparePart(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String requestCode = request.getParameter("requestCode");
        HttpSession session = request.getSession(false);

        PurchaseInvoice4 draft = null;
        long draftVersion = 1L;
        if (session != null && requestCode != null && session.getAttribute("purchaseDrafts4") instanceof Map
        && ((Map<?, ?>) session.getAttribute("purchaseDrafts4")).containsKey(requestCode)) {
            try {
                synchronized (session) {
                    String submitted = request.getParameter("draftVersion");
                    if (submitted == null || !submitted.matches("[0-9]+")) {
                        response.sendError(400, "Thiếu hoặc sai phiên bản bản nháp");
                        return;
                    }
                    try {
                        draftVersion = Long.parseLong(submitted);
                    } catch (NumberFormatException e) {
                        response.sendError(400, "Phiên bản bản nháp không hợp lệ");
                        return;
                    }
                    {
                        Map<String, Long> storedVersions = (Map<String, Long>) session.getAttribute("draftVersions4");
                        if (storedVersions == null || !storedVersions.containsKey(requestCode))
                        throw new IllegalArgumentException("khongTonTai: Bản nháp không tồn tại hoặc đã hết hạn");
                        if (storedVersions.get(requestCode).longValue() != draftVersion)
                        throw new IllegalArgumentException("conflict: Dữ liệu bản nháp đã được cập nhật từ thao tác khác. Vui lòng tải lại trang.");
                    }
                    Object confirming = session.getAttribute("draftConfirming4");
                    if (confirming instanceof java.util.Set && ((java.util.Set<?>) confirming).contains(requestCode)) {
                        response.sendError(409, "Bản nháp đang được xác nhận");
                        return;
                    }
                    draft = ((Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).get(requestCode);
                }
            } catch (IllegalArgumentException e) {
                String validationMessage = e.getMessage();
                String validationField = null;
                int separator = validationMessage == null ? -1 : validationMessage.indexOf(": ");
                if (separator > 0) {
                    validationField = validationMessage.substring(0, separator);
                    validationMessage = validationMessage.substring(separator + 2);
                }
                response.sendError("conflict".equals(validationField) ? 409 : 400, validationMessage);
                return;
            }
        }

        if (draft == null) {
            request.getSession().setAttribute("flashMessage4", "Phiên lập hóa đơn không tồn tại hoặc đã hết hạn.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }

        String name = request.getParameter("name");
        String categoryId = request.getParameter("categoryId");
        String unit = request.getParameter("unit");
        String manufacturer = request.getParameter("manufacturer");
        String sellingPrice = request.getParameter("sellingPrice");
        String specifications = request.getParameter("specifications");
        String compatibleVehicleModels = request.getParameter("compatibleVehicleModels");
        String warrantyPeriod = request.getParameter("warrantyPeriod");
        String status = request.getParameter("status");
        String warrantyTerms = request.getParameter("warrantyTerms");
        String description = request.getParameter("description");
        String imagePath = request.getParameter("imagePath");

        Map<String, String> formValues = new HashMap<>();
        formValues.put("name", name != null ? name : "");
        formValues.put("categoryId", categoryId != null ? categoryId : "");
        formValues.put("unit", unit != null ? unit : "");
        formValues.put("manufacturer", manufacturer != null ? manufacturer : "");
        formValues.put("sellingPrice", sellingPrice != null ? sellingPrice : "");
        formValues.put("specifications", specifications != null ? specifications : "");
        formValues.put("compatibleVehicleModels", compatibleVehicleModels != null ? compatibleVehicleModels : "");
        formValues.put("warrantyPeriod", warrantyPeriod != null ? warrantyPeriod : "");
        formValues.put("status", status != null ? status : "Chưa công bố");
        formValues.put("warrantyTerms", warrantyTerms != null ? warrantyTerms : "");
        formValues.put("description", description != null ? description : "");
        formValues.put("imagePath", imagePath != null ? imagePath : "");
        request.setAttribute("formValues", formValues);
        request.setAttribute("requestCode", requestCode);
        request.setAttribute("draftVersion", draftVersion);
        request.setAttribute("purchaseStep", 2);

        SparePart4 sparePart = new SparePart4();
        sparePart.setName(name);
        sparePart.setManufacturer(manufacturer);
        sparePart.setUnit(unit);
        sparePart.setDescription(description);
        sparePart.setImagePath(imagePath != null && !imagePath.trim().isEmpty() ? imagePath.trim() : null);
        sparePart.setSpecifications(specifications);
        sparePart.setCompatibleVehicleModels(compatibleVehicleModels);
        sparePart.setWarrantyPeriod(warrantyPeriod);
        sparePart.setWarrantyTerms(warrantyTerms);
        sparePart.setStatus(status != null && !status.trim().isEmpty() ? status.trim() : "Chưa công bố");

        try {
            int nId = Integer.parseInt(categoryId == null ? "" : categoryId.trim());
            if (nId <= 0) throw new IllegalArgumentException("categoryId: Mã định danh phải là số nguyên dương");
            SparePartCategory4 category = categoryDAO.getActiveCategory(nId);
            if (category == null) {
                throw new IllegalArgumentException("categoryId: Nhóm phụ tùng đã chọn không hợp lệ hoặc đã ngừng sử dụng");
            }
            sparePart.setCategory(category);

            if (sellingPrice != null && !sellingPrice.trim().isEmpty()) {
                String amountText1 = sellingPrice == null ? "" : sellingPrice.trim();
                if (!amountText1.matches("[0-9]+(\\.[0-9]+)?"))
                throw new IllegalArgumentException("sellingPrice: Giá trị phải là số dương hợp lệ, không chứa dấu phẩy hay ký tự đặc biệt");
                BigDecimal parsedAmount1 = new BigDecimal(amountText1);
                if (parsedAmount1.scale() > 2 || parsedAmount1.precision() - parsedAmount1.scale() > 15 - 2)
                throw new IllegalArgumentException("sellingPrice: Giá trị số vượt quá độ chính xác cho phép");
                if (parsedAmount1.signum() < 0)
                throw new IllegalArgumentException("sellingPrice: Giá trị phải lớn hơn 0");
                sparePart.setSellingPrice(parsedAmount1);
            } else {
                sparePart.setSellingPrice(null);
            }

            {
                if (sparePart == null) {
                    throw new IllegalArgumentException("sparePart: Thông tin phụ tùng không được null");
                }
                {
                    if (sparePart.getName() == null) {
                        throw new IllegalArgumentException("name: Trường này là bắt buộc");
                    }
                    String trimmed = sparePart.getName().trim();
                    if (trimmed.isEmpty()) {
                        throw new IllegalArgumentException("name: Trường này không được để trống");
                    }
                    if (trimmed.length() > 255) {
                        throw new IllegalArgumentException("name: Độ dài không được vượt quá " + 255 + " ký tự");
                    }
                }
                {
                    if (sparePart.getManufacturer() == null) {
                        throw new IllegalArgumentException("manufacturer: Trường này là bắt buộc");
                    }
                    String trimmed = sparePart.getManufacturer().trim();
                    if (trimmed.isEmpty()) {
                        throw new IllegalArgumentException("manufacturer: Trường này không được để trống");
                    }
                    if (trimmed.length() > 255) {
                        throw new IllegalArgumentException("manufacturer: Độ dài không được vượt quá " + 255 + " ký tự");
                    }
                }

                String validatedUnit = sparePart.getUnit();
                if (validatedUnit == null || (!"Cái".equals(validatedUnit) &&
                !"Chiếc".equals(validatedUnit) &&
                !"Bộ".equals(validatedUnit) &&
                !"Lít".equals(validatedUnit))) {
                    throw new IllegalArgumentException("unit: Đơn vị tính không hợp lệ (chỉ chấp nhận Cái, Chiếc, Bộ, Lít)");
                }

                if (sparePart.getCategory() == null || sparePart.getCategory().getId() <= 0) {
                    throw new IllegalArgumentException("categoryId: Phải chọn nhóm phụ tùng hợp lệ đang sử dụng");
                }

                String validatedStatus = sparePart.getStatus();
                if (validatedStatus == null || (!"Chưa công bố".equals(validatedStatus) &&
                !"Đang kinh doanh".equals(validatedStatus))) {
                    throw new IllegalArgumentException("status: Trạng thái phụ tùng mới chỉ được chọn 'Chưa công bố' hoặc 'Đang kinh doanh'");
                }

                if ("Đang kinh doanh".equals(validatedStatus)) {
                    if (sparePart.getSellingPrice() == null || sparePart.getSellingPrice().compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("sellingPrice: Phụ tùng đang kinh doanh bắt buộc phải có giá bán >= 0");
                    }
                    if (sparePart.getDescription() == null || sparePart.getDescription().trim().isEmpty()) {
                        throw new IllegalArgumentException("description: Phụ tùng đang kinh doanh bắt buộc phải có mô tả");
                    }
                }

                if (sparePart.getImagePath() != null && !sparePart.getImagePath().trim().isEmpty()) {
                    String img = sparePart.getImagePath().trim();
                    if (!"assets/images/part.svg".equals(img) &&
                    !"assets/images/brake.svg".equals(img) &&
                    !"assets/images/placeholder.svg".equals(img)) {
                        throw new IllegalArgumentException("imagePath: Đường dẫn ảnh minh họa không nằm trong danh sách tài nguyên cục bộ hợp lệ");
                    }
                }
            }
            synchronized (session) {
                {
                    Map<String, Long> storedVersions = (Map<String, Long>) session.getAttribute("draftVersions4");
                    if (storedVersions == null || !storedVersions.containsKey(requestCode))
                    throw new IllegalArgumentException("khongTonTai: Bản nháp không tồn tại hoặc đã hết hạn");
                    if (storedVersions.get(requestCode).longValue() != draftVersion)
                    throw new IllegalArgumentException("conflict: Dữ liệu bản nháp đã được cập nhật từ thao tác khác. Vui lòng tải lại trang.");
                }
                Object confirming = session.getAttribute("draftConfirming4");
                if (confirming instanceof java.util.Set && ((java.util.Set<?>) confirming).contains(requestCode)) {
                    response.sendError(409, "Bản nháp đang được xác nhận");
                    return;
                }
                draftVersion = ((Map<String, Long>) session.getAttribute("draftVersions4")).merge(requestCode, 1L, Long::sum);
                request.setAttribute("draftVersion", draftVersion);
            }
            int newPartId = sparePartDAO.addSparePart(sparePart);
            request.getSession().setAttribute("flashMessage4", "Đã thêm phụ tùng mới thành công: " + sparePart.getName());

            String encodedName = URLEncoder.encode(sparePart.getName(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode +
            "&selectedPartId=" + newPartId + "&keyword=" + encodedName);

        } catch (IllegalArgumentException ve) {
            String validationMessage = ve.getMessage();
            String validationField = null;
            int separator = validationMessage == null ? -1 : validationMessage.indexOf(": ");
            if (separator > 0) {
                validationField = validationMessage.substring(0, separator);
                validationMessage = validationMessage.substring(separator + 2);
            }
            if ("conflict".equals(validationField)) {
                response.sendError(409, validationMessage);
                return;
            }
            request.setAttribute("errorMessage", validationMessage);
            if (validationField != null) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put(validationField, validationMessage);
                request.setAttribute("fieldErrors", fieldErrors);
            }
            try {
                request.setAttribute("categories", categoryDAO.getActiveCategories());
            } catch (SQLException ignored) {
            }
            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/newSparePart.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi CSDL khi lưu thông tin phụ tùng. Vui lòng kiểm tra lại.");
            try {
                request.setAttribute("categories", categoryDAO.getActiveCategories());
            } catch (SQLException ignored) {
            }
            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/newSparePart.jsp").forward(request, response);
        }
    }
}
