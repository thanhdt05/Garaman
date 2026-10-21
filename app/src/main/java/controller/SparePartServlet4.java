package controller;

import dao.SparePartCategoryDAO4;
import dao.SparePartDAO4;
import exception.ValidationException4;
import model.PurchaseInvoice4;
import model.SparePart4;
import model.SparePartCategory4;
import util.AppConstants4;
import util.DraftStore4;
import util.Validation4;
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
        if (session != null && requestCode != null) {
            try {
                draft = DraftStore4.getDraft(session, requestCode);
                draftVersion = DraftStore4.getDraftVersion(session, requestCode);
            } catch (ValidationException4 ignored) {
                draft = null;
            }
        }

        if (draft == null) {
            WebUtil4.setFlash(request, "Phiên lập hóa đơn không tồn tại hoặc đã hết hạn. Vui lòng chọn lại nhà cung cấp.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }

        try {
            List<SparePartCategory4> categories = categoryDAO.getActiveCategories();
            request.setAttribute("categories", categories);
            request.setAttribute("requestCode", requestCode);
            request.setAttribute("draftVersion", draftVersion);
            request.setAttribute("purchaseStep", 2);

            request.getRequestDispatcher("/WEB-INF/view/warehouse/newSparePart.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi CSDL khi tải danh mục nhóm phụ tùng.");
            request.setAttribute("purchaseStep", 2);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/newSparePart.jsp").forward(request, response);
        }
    }

    public void addSparePart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestCode = request.getParameter("requestCode");
        HttpSession session = request.getSession(false);

        PurchaseInvoice4 draft = null;
        long draftVersion = 1L;
        if (session != null && requestCode != null) {
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
                    DraftStore4.validateDraftVersion(session, requestCode, draftVersion);
                    Object confirming = session.getAttribute(AppConstants4.SESSION_DRAFT_CONFIRMING);
                    if (confirming instanceof java.util.Set && ((java.util.Set<?>) confirming).contains(requestCode)) {
                        response.sendError(409, "Bản nháp đang được xác nhận");
                        return;
                    }
                    draft = DraftStore4.getDraft(session, requestCode);
                }
            } catch (ValidationException4 e) {
                response.sendError("conflict".equals(e.getField()) ? 409 : 400, e.getMessage());
                return;
            }
        }

        if (draft == null) {
            WebUtil4.setFlash(request, "Phiên lập hóa đơn không tồn tại hoặc đã hết hạn.");
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
        formValues.put("status", status != null ? status : AppConstants4.PART_STATUS_UNPUBLISHED);
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
        sparePart.setStatus(status != null && !status.trim().isEmpty() ? status.trim() : AppConstants4.PART_STATUS_UNPUBLISHED);

        try {
            int nId = Validation4.positiveId(categoryId, "categoryId");
            SparePartCategory4 category = categoryDAO.getActiveCategory(nId);
            if (category == null) {
                throw new ValidationException4("categoryId", "Nhóm phụ tùng đã chọn không hợp lệ hoặc đã ngừng sử dụng");
            }
            sparePart.setCategory(category);

            if (sellingPrice != null && !sellingPrice.trim().isEmpty()) {
                sparePart.setSellingPrice(Validation4.parseDecimal(sellingPrice, "sellingPrice", 15, 2, true));
            } else {
                sparePart.setSellingPrice(null);
            }

            Validation4.validateSparePart(sparePart);
            synchronized (session) {
                DraftStore4.validateDraftVersion(session, requestCode, draftVersion);
                Object confirming = session.getAttribute(AppConstants4.SESSION_DRAFT_CONFIRMING);
                if (confirming instanceof java.util.Set && ((java.util.Set<?>) confirming).contains(requestCode)) {
                    response.sendError(409, "Bản nháp đang được xác nhận");
                    return;
                }
                draftVersion = DraftStore4.incrementDraftVersion(session, requestCode);
                request.setAttribute("draftVersion", draftVersion);
            }
            int newPartId = sparePartDAO.addSparePart(sparePart);
            WebUtil4.setFlash(request, "Đã thêm phụ tùng mới thành công: " + sparePart.getName());

            String encodedName = URLEncoder.encode(sparePart.getName(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode +
                    "&selectedPartId=" + newPartId + "&keyword=" + encodedName);

        } catch (ValidationException4 ve) {
            if ("conflict".equals(ve.getField())) {
                response.sendError(409, ve.getMessage());
                return;
            }
            request.setAttribute("errorMessage", ve.getMessage());
            if (ve.getField() != null) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put(ve.getField(), ve.getMessage());
                request.setAttribute("fieldErrors", fieldErrors);
            }
            try {
                request.setAttribute("categories", categoryDAO.getActiveCategories());
            } catch (SQLException ignored) {
            }
            request.getRequestDispatcher("/WEB-INF/view/warehouse/newSparePart.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi CSDL khi lưu thông tin phụ tùng. Vui lòng kiểm tra lại.");
            try {
                request.setAttribute("categories", categoryDAO.getActiveCategories());
            } catch (SQLException ignored) {
            }
            request.getRequestDispatcher("/WEB-INF/view/warehouse/newSparePart.jsp").forward(request, response);
        }
    }
}
