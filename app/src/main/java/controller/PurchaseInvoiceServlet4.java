package controller;

import dao.PurchaseInvoiceDAO4;
import dao.SparePartDAO4;
import dao.SupplierDAO4;
import dao.WarehouseStaffDAO4;
import dto.SessionUser4;
import exception.ValidationException4;
import model.PurchaseInvoice4;
import model.PurchaseInvoiceDetail4;
import model.SparePart4;
import model.Supplier4;
import model.WarehouseStaff4;
import util.AppConstants4;
import util.DraftStore4;
import util.Validation4;
import util.WebUtil4;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PurchaseInvoiceServlet4 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private PurchaseInvoiceDAO4 purchaseInvoiceDAO;
    private SparePartDAO4 sparePartDAO;
    private SupplierDAO4 supplierDAO;
    private WarehouseStaffDAO4 warehouseStaffDAO;

    @Override
    public void init() throws ServletException {
        purchaseInvoiceDAO = new PurchaseInvoiceDAO4();
        sparePartDAO = new SparePartDAO4();
        supplierDAO = new SupplierDAO4();
        warehouseStaffDAO = new WarehouseStaffDAO4();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/warehouse/purchases/draft":
            showDraft(request, response);
            break;
            case "/warehouse/purchases/view":
            viewInvoice(request, response);
            break;
            case "/warehouse/purchases/print":
            printInvoice(request, response);
            break;
            case "/warehouse/purchases/unpaid":
            showUnpaidInvoices(request, response);
            break;
            default:
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/warehouse/purchases/start":
            startPurchase(request, response);
            break;
            case "/warehouse/purchases/search-part":
            searchSpareParts(request, response);
            break;
            case "/warehouse/purchases/add-line":
            addInvoiceLine(request, response);
            break;
            case "/warehouse/purchases/update":
            updateInvoiceLines(request, response);
            break;
            case "/warehouse/purchases/remove-line":
            removeInvoiceLine(request, response);
            break;
            case "/warehouse/purchases/cancel":
            cancelDraft(request, response);
            break;
            case "/warehouse/purchases/confirm":
            confirmInvoice(request, response);
            break;
            case "/warehouse/purchases/pay":
            recordPayment(request, response);
            break;
            default:
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Hành động POST không hợp lệ");
            break;
        }
    }

    public void startPurchase(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        SessionUser4 currentUser = (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String supplierIdStr = request.getParameter("supplierId");
        int supplierId;
        try {
            supplierId = Validation4.positiveId(supplierIdStr, "supplierId");
            Supplier4 supplier = supplierDAO.getSupplierDetails(supplierId);
            if (supplier == null) {
                WebUtil4.setFlash(request, "Không tìm thấy nhà cung cấp hoặc nhà cung cấp đã ngừng hoạt động.");
                response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
                return;
            }

            WarehouseStaff4 staff = warehouseStaffDAO.getWarehouseStaffDetails(currentUser.getId());
            if (staff == null) {
                WebUtil4.setFlash(request, "Không tìm thấy thông tin nhân viên kho để lập hóa đơn.");
                response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
                return;
            }

            PurchaseInvoice4 newDraft = DraftStore4.createDraft(session, supplier, staff);
            String requestCode = newDraft.getRequestCode();
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);

        } catch (ValidationException4 ve) {
            WebUtil4.setFlash(request, ve.getMessage());
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
        } catch (SQLException e) {
            WebUtil4.setFlash(request, "Không thể tải thông tin nhà cung cấp hoặc nhân viên kho. Vui lòng thử lại.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
        }
    }

    public void showDraft(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String requestCode = request.getParameter("requestCode");
        HttpSession session = request.getSession(false);

        SessionUser4 currentUser = session != null ? (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER) : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (requestCode == null || requestCode.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }

        // Kiểm tra xem mã yêu cầu này đã được commit vào DB chưa (hỗ trợ refresh sau xác nhận)
        try {
            Integer committedId = purchaseInvoiceDAO.findInvoiceIdByRequestCode(requestCode, currentUser.getId());
            if (committedId != null) {
                response.sendRedirect(request.getContextPath() + "/warehouse/purchases/view?id=" + committedId);
                return;
            }
        } catch (SQLException ignored) {
        }

        PurchaseInvoice4 draft = null;
        long draftVersion = 1L;
        try {
            synchronized (session) {
                draft = DraftStore4.getDraft(session, requestCode);
                draftVersion = DraftStore4.getDraftVersion(session, requestCode);
                request.setAttribute("formValues", DraftStore4.getFormValues(session, requestCode));
            }
        } catch (ValidationException4 e) {
            WebUtil4.setFlash(request, "Bản nháp hóa đơn không hợp lệ hoặc đã hết hạn.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }

        if (draft == null) {
            WebUtil4.setFlash(request, "Bản nháp hóa đơn không tồn tại hoặc phiên làm việc đã hết hạn.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }

        String keyword = request.getParameter("keyword");
        String selectedPartId = request.getParameter("selectedPartId");
        String focusPartId = request.getParameter("focusPartId");

        List<SparePart4> spareParts = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            try {
                spareParts = sparePartDAO.findSparePartsByName(keyword.trim(), false);
            } catch (SQLException e) {
                request.setAttribute("partSearchError", "Không thể tìm phụ tùng do lỗi truy vấn dữ liệu. Vui lòng thử lại.");
            }
        }

        request.setAttribute("purchaseDateDisplay", WebUtil4.formatDateTime(draft.getPurchaseDate()));
        request.setAttribute("purchaseInvoice", draft);
        request.setAttribute("requestCode", requestCode);
        request.setAttribute("draftVersion", draftVersion);
        request.setAttribute("keyword", keyword != null ? keyword.trim() : "");
        request.setAttribute("selectedPartId", selectedPartId);
        request.setAttribute("focusPartId", focusPartId);
        request.setAttribute("spareParts", spareParts);
        request.setAttribute("purchaseStep", 2);

        request.getRequestDispatcher("/WEB-INF/view/warehouse/purchaseInvoice.jsp").forward(request, response);
    }

    public void searchSpareParts(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String requestCode = request.getParameter("requestCode");
        HttpSession session = request.getSession(false);
        if (session == null) { response.sendError(400); return; }
        synchronized (session) {
            if (!validateVersionAndConcurrency(request, session, requestCode, response)) return;

            // Lưu toàn bộ dữ liệu dòng đang sửa
            saveLineInputsToDraft(request, session, requestCode);

            String keyword = request.getParameter("keyword");
            String encoded = keyword != null ? URLEncoder.encode(keyword.trim(), StandardCharsets.UTF_8) : "";
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode + "&keyword=" + encoded);
        }
    }

    public void addInvoiceLine(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String requestCode = request.getParameter("requestCode");
        HttpSession session = request.getSession(false);
        if (session == null) { response.sendError(400); return; }
        String selectedPartIdStr = request.getParameter("selectedPartId");
        try {
            int partId = Validation4.positiveId(selectedPartIdStr, "selectedPartId");
            SparePart4 sparePart = sparePartDAO.getSparePartDetails(partId, false);
            synchronized (session) {
                if (!validateVersionAndConcurrency(request, session, requestCode, response)) return;
                saveLineInputsToDraft(request, session, requestCode);
                PurchaseInvoice4 draft = DraftStore4.getDraft(session, requestCode);

                // Kiểm tra xem part đã có trong draft chưa
                boolean alreadyExists = false;
                for (PurchaseInvoiceDetail4 d : draft.getInvoiceDetails()) {
                    if (d.getSparePart() != null && d.getSparePart().getId() == partId) {
                        alreadyExists = true;
                        break;
                    }
                }

                if (alreadyExists) {
                    // Đã có: không thêm dòng mới, chỉ focus vào dòng cũ
                    response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode +
                        "&focusPartId=" + partId);
                    return;
                }

                if (sparePart != null) {
                    PurchaseInvoiceDetail4 detail = new PurchaseInvoiceDetail4();
                    detail.setSparePart(sparePart);
                    detail.setSparePartNameSnapshot(sparePart.getName());
                    detail.setUnitSnapshot(sparePart.getUnit());
                    detail.setQuantity(BigDecimal.ZERO);
                    detail.setPurchaseUnitPrice(BigDecimal.ZERO);
                    draft.getInvoiceDetails().add(detail);
                    DraftStore4.saveDraft(session, draft);
                    DraftStore4.incrementDraftVersion(session, requestCode);
                }

                response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode +
                    "&focusPartId=" + partId);

            }
        } catch (ValidationException4 | SQLException e) {
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);
        }
    }

    public void updateInvoiceLines(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String requestCode = request.getParameter("requestCode");
        HttpSession session = request.getSession(false);
        if (session == null) { response.sendError(400); return; }
        synchronized (session) {
            if (!validateVersionAndConcurrency(request, session, requestCode, response)) return;

            saveLineInputsToDraft(request, session, requestCode);

            String destination = request.getParameter("destination");
            if ("new-part".equalsIgnoreCase(destination)) {
                response.sendRedirect(request.getContextPath() + "/warehouse/parts/new?requestCode=" + requestCode);
                return;
            }

            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);
        }
    }

    public void removeInvoiceLine(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String requestCode = request.getParameter("requestCode");
        HttpSession session = request.getSession(false);
        if (session == null) { response.sendError(400); return; }
        synchronized (session) {
            if (!validateVersionAndConcurrency(request, session, requestCode, response)) return;

            saveLineInputsToDraft(request, session, requestCode);

            String removePartIdStr = request.getParameter("removePartId");
            try {
                int removeId = Validation4.positiveId(removePartIdStr, "removePartId");
                PurchaseInvoice4 draft = DraftStore4.getDraft(session, requestCode);
                draft.getInvoiceDetails().removeIf(d -> d.getSparePart() != null && d.getSparePart().getId() == removeId);
                DraftStore4.saveDraft(session, draft);
                DraftStore4.clearSparePartErrors(session, requestCode, removeId);
                DraftStore4.incrementDraftVersion(session, requestCode);
            } catch (ValidationException4 ignored) {
            }

            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);
        }
    }

    public void cancelDraft(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String requestCode = request.getParameter("requestCode");
        HttpSession session = request.getSession(false);
        if (session == null) { response.sendError(400); return; }
        synchronized (session) {
            if (!validateVersionAndConcurrency(request, session, requestCode, response)) return;
            try {
                DraftStore4.removeDraft(session, requestCode);
            } catch (ValidationException4 e) {
                response.sendError(400, e.getMessage());
                return;
            }
        }
        WebUtil4.setFlash(request, "Đã hủy bản nháp hóa đơn nhập.");
        response.sendRedirect(request.getContextPath() + "/home");
    }

    public void confirmInvoice(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String requestCode = request.getParameter("requestCode");
        HttpSession session = request.getSession(false);
        SessionUser4 currentUser = session != null ? (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER) : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Kiểm tra idempotent trước
        try {
            Integer existingId = purchaseInvoiceDAO.findInvoiceIdByRequestCode(requestCode, currentUser.getId());
            if (existingId != null) {
                if (session != null) {
                    try {
                        DraftStore4.removeDraft(session, requestCode);
                    } catch (ValidationException4 ignored) {
                    }
                }
                response.sendRedirect(request.getContextPath() + "/warehouse/purchases/view?id=" + existingId);
                return;
            }
        } catch (SQLException ignored) {
        }

        boolean confirming = false;
        try {
            PurchaseInvoice4 draft;
            synchronized (session) {
                if (!validateVersionAndConcurrency(request, session, requestCode, response)) return;
                saveLineInputsToDraft(request, session, requestCode);
                draft = DraftStore4.beginConfirmation(session, requestCode,
                DraftStore4.getDraftVersion(session, requestCode));
                confirming = true;
            }

            // Kiểm tra xem draft có dòng nào bị parse lỗi không
            Map<String, String> rawInputs = DraftStore4.getFormValues(session, requestCode);
            for (Map.Entry<String, String> entry : rawInputs.entrySet()) {
                if (entry.getKey().startsWith("error_")) {
                    WebUtil4.setFlash(request, "Hóa đơn còn dữ liệu chưa hợp lệ. Vui lòng kiểm tra lại các ô báo đỏ.");
                    response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);
                    return;
                }
            }

            if (draft.getInvoiceDetails() == null || draft.getInvoiceDetails().isEmpty()) {
                WebUtil4.setFlash(request, "Hóa đơn nhập phải có ít nhất một dòng phụ tùng.");
                response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);
                return;
            }

            // Kiểm tra từng dòng: lượng > 0, giá > 0
            for (PurchaseInvoiceDetail4 d : draft.getInvoiceDetails()) {
                if (d.getQuantity() == null || d.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                    WebUtil4.setFlash(request, "Phụ tùng '" + d.getSparePart().getName() + "' chưa nhập số lượng hợp lệ (> 0).");
                    response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode +
                        "&focusPartId=" + d.getSparePart().getId());
                    return;
                }
                if (d.getPurchaseUnitPrice() == null || d.getPurchaseUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    WebUtil4.setFlash(request, "Phụ tùng '" + d.getSparePart().getName() + "' chưa nhập đơn giá nhập hợp lệ (> 0).");
                    response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode +
                        "&focusPartId=" + d.getSparePart().getId());
                    return;
                }
            }

            // Lưu chính thức vào CSDL
            int invoiceId = purchaseInvoiceDAO.savePurchaseInvoice(draft);
            DraftStore4.removeDraft(session, requestCode);

            WebUtil4.setFlash(request, "Hóa đơn nhập HN" + invoiceId + " đã được xác nhận và nhập kho thành công.");
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/view?id=" + invoiceId);

        } catch (ValidationException4 ve) {
            WebUtil4.setFlash(request, "Lỗi xác thực: " + ve.getMessage());
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);
        } catch (SQLException e) {
            WebUtil4.setFlash(request, "Lỗi cơ sở dữ liệu khi xác nhận hóa đơn. Vui lòng thử lại.");
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);
        } finally {
            if (confirming) DraftStore4.endConfirmation(session, requestCode);
        }
    }

    public void viewInvoice(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String idStr = request.getParameter("id");
        int id;
        try {
            id = Validation4.positiveId(idStr, "id");
            PurchaseInvoice4 invoice = purchaseInvoiceDAO.getPurchaseInvoiceDetails(id);
            if (invoice == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hóa đơn nhập số: " + idStr);
                return;
            }

            HttpSession session = request.getSession(false);
            SessionUser4 currentUser = session != null ? (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER) : null;
            boolean canRecordPayment = currentUser != null && invoice.getWarehouseStaff() != null && currentUser.getId() == invoice.getWarehouseStaff().getId();

            request.setAttribute("purchaseDateDisplay", WebUtil4.formatDateTime(invoice.getPurchaseDate()));
            request.setAttribute("confirmedAtDisplay", WebUtil4.formatDateTime(invoice.getConfirmedAt()));
            request.setAttribute("paidAtDisplay", WebUtil4.formatDateTime(invoice.getPaidAt()));
            request.setAttribute("purchaseInvoice", invoice);
            request.setAttribute("canRecordPayment", canRecordPayment);
            request.setAttribute("purchaseStep", 3);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/confirmedInvoice.jsp").forward(request, response);

        } catch (ValidationException4 ve) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã hóa đơn không hợp lệ");
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi CSDL khi tải hóa đơn");
        }
    }

    public void printInvoice(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String idStr = request.getParameter("id");
        int id;
        try {
            id = Validation4.positiveId(idStr, "id");
            PurchaseInvoice4 invoice = purchaseInvoiceDAO.getPurchaseInvoiceDetails(id);
            if (invoice == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hóa đơn nhập số: " + idStr);
                return;
            }

            request.setAttribute("purchaseDateDisplay", WebUtil4.formatDateTime(invoice.getPurchaseDate()));
            request.setAttribute("confirmedAtDisplay", WebUtil4.formatDateTime(invoice.getConfirmedAt()));
            request.setAttribute("paidAtDisplay", WebUtil4.formatDateTime(invoice.getPaidAt()));
            request.setAttribute("purchaseInvoice", invoice);
            request.setAttribute("purchaseStep", 4);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/printInvoice.jsp").forward(request, response);

        } catch (ValidationException4 ve) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã hóa đơn không hợp lệ");
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi CSDL khi tải hóa đơn");
        }
    }

    public void recordPayment(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        SessionUser4 currentUser = session != null ? (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER) : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idStr = request.getParameter("id");
        String amountText = request.getParameter("amountPaid");

        int id;
        try {
            id = Validation4.positiveId(idStr, "id");
        } catch (ValidationException4 ve) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ve.getMessage());
            return;
        }

        try {
            BigDecimal amount = Validation4.parseDecimal(amountText, "amountPaid", 18, 2, false);
            boolean success = purchaseInvoiceDAO.recordPayment(id, amount, currentUser.getId());
            if (success) {
                WebUtil4.setFlash(request, "Đã ghi nhận thanh toán thành công cho hóa đơn HN" + id);
            } else {
                WebUtil4.setFlash(request, "Hóa đơn HN" + id + " đã được thanh toán trước đó.");
            }
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/view?id=" + id);

        } catch (ValidationException4 ve) {
            if ("permission".equals(ve.getField())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, ve.getMessage());
            } else if ("id".equals(ve.getField())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, ve.getMessage());
            } else {
                Map<String, String> formValues = new HashMap<>();
                formValues.put("amountPaid", amountText != null ? amountText : "");
                request.setAttribute("formValues", formValues);
                request.setAttribute("errorMessage", ve.getMessage());
                viewInvoice(request, response);
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi CSDL khi thanh toán");
        }
    }

    public void showUnpaidInvoices(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        SessionUser4 currentUser = session != null ? (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER) : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<PurchaseInvoice4> items = purchaseInvoiceDAO.getUnpaidInvoices(currentUser.getId());
            request.setAttribute("unpaidInvoices", items);
            request.setAttribute("activeNav", "unpaid");
            request.getRequestDispatcher("/WEB-INF/view/warehouse/warehouseHome.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi CSDL khi tải danh sách hóa đơn chưa thanh toán.");
            request.getRequestDispatcher("/WEB-INF/view/warehouse/warehouseHome.jsp").forward(request, response);
        }
    }

    private boolean validateVersionAndConcurrency(HttpServletRequest req, HttpSession session, String requestCode, HttpServletResponse res)
        throws IOException {
        if (session == null || requestCode == null) {
            res.sendError(400, "Thiếu tham số phiên làm việc");
            return false;
        }
        try {
            String submitted = req.getParameter("draftVersion");
            if (submitted == null || submitted.trim().isEmpty()) throw new NumberFormatException();
            DraftStore4.validateDraftVersion(session, requestCode, Long.parseLong(submitted.trim()));
            SessionUser4 user = (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER);
            PurchaseInvoice4 draft = DraftStore4.getDraft(session, requestCode);
            if (user == null || draft.getWarehouseStaff().getId() != user.getId()) {
                res.sendError(403, "Bạn không có quyền sửa bản nháp này");
                return false;
            }
            Object confirming = session.getAttribute(AppConstants4.SESSION_DRAFT_CONFIRMING);
            if (confirming instanceof java.util.Set && ((java.util.Set<?>) confirming).contains(requestCode)) {
                res.sendError(409, "Bản nháp đang được xác nhận. Vui lòng đợi và tải lại trang.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            res.sendError(400, "Thiếu hoặc sai phiên bản bản nháp");
        } catch (ValidationException4 e) {
            res.sendError("conflict".equals(e.getField()) ? 409 : 400, e.getMessage());
        }
        return false;
    }

    private void saveLineInputsToDraft(HttpServletRequest req, HttpSession session, String requestCode) {
        if (session == null || requestCode == null) return;
        synchronized (session) {
            try {
                PurchaseInvoice4 draft = DraftStore4.getDraft(session, requestCode);
                Map<String, String> rawValues = new HashMap<>();
                // Iterate trusted stored rows: omitted/blank fields must not reuse old valid values.
                for (PurchaseInvoiceDetail4 target : draft.getInvoiceDetails()) {
                    int pid = target.getSparePart().getId();
                    String qtyStr = req.getParameter("quantity_" + pid);
                    String priceStr = req.getParameter("purchaseUnitPrice_" + pid);
                    rawValues.put("quantity_" + pid, qtyStr == null ? "" : qtyStr);
                    rawValues.put("purchaseUnitPrice_" + pid, priceStr == null ? "" : priceStr);
                    try {
                        BigDecimal qty = Validation4.parseDecimal(qtyStr, "quantity", 12, 3, true);
                        if (qty.signum() > 0) Validation4.validateQuantity(qty, target.getSparePart().getUnit());
                        target.setQuantity(qty);
                    } catch (ValidationException4 e) {
                        target.setQuantity(BigDecimal.ZERO);
                        rawValues.put("error_quantity_" + pid, e.getMessage());
                    }
                    try {
                        target.setPurchaseUnitPrice(Validation4.parseDecimal(priceStr, "purchaseUnitPrice", 18, 2, true));
                    } catch (ValidationException4 e) {
                        target.setPurchaseUnitPrice(BigDecimal.ZERO);
                        rawValues.put("error_purchaseUnitPrice_" + pid, e.getMessage());
                    }
                }
                DraftStore4.saveDraft(session, draft);
                DraftStore4.updateFormValues(session, requestCode, rawValues);
                DraftStore4.incrementDraftVersion(session, requestCode);
            } catch (ValidationException4 e) {
                throw new IllegalStateException("Bản nháp không còn hợp lệ sau khi kiểm tra phiên bản", e);
            }
        }
    }
}
