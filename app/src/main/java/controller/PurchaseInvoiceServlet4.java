package controller;

import model.Member4;

import dao.PurchaseInvoiceDAO4;
import dao.SparePartDAO4;
import dao.SupplierDAO4;
import dao.WarehouseStaffDAO4;
import model.PurchaseInvoice4;
import model.PurchaseInvoiceDetail4;
import model.SparePart4;
import model.Supplier4;
import model.WarehouseStaff4;

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
        Member4 currentUser = (Member4) session.getAttribute("currentUser4");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String supplierIdStr = request.getParameter("supplierId");
        int supplierId;
        try {
            supplierId = Integer.parseInt(supplierIdStr == null ? "" : supplierIdStr.trim());
            if (supplierId <= 0) throw new IllegalArgumentException("supplierId: Mã định danh phải là số nguyên dương");
            Supplier4 supplier = supplierDAO.getSupplierDetails(supplierId);
            if (supplier == null) {
                request.getSession().setAttribute("flashMessage4", "Không tìm thấy nhà cung cấp hoặc nhà cung cấp đã ngừng hoạt động.");
                response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
                return;
            }

            WarehouseStaff4 staff = warehouseStaffDAO.getWarehouseStaffDetails(currentUser.getId());
            if (staff == null) {
                request.getSession().setAttribute("flashMessage4", "Không tìm thấy thông tin nhân viên kho để lập hóa đơn.");
                response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
                return;
            }

            PurchaseInvoice4 newDraft = new PurchaseInvoice4();
            synchronized (session) {
                Map<String, PurchaseInvoice4> drafts = (Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4");
                if (drafts == null) {
                    drafts = new HashMap<>();
                    session.setAttribute("purchaseDrafts4", drafts);
                    session.setAttribute("draftVersions4", new HashMap<String, Long>());
                    session.setAttribute("draftFormValues4", new HashMap<String, Map<String, String>>());
                    session.setAttribute("draftConfirming4", new java.util.HashSet<String>());
                }
                if (drafts.size() >= 5) throw new IllegalArgumentException("Số bản nháp đang mở tối đa là 5. Vui lòng hủy bản không dùng.");
                String code = java.util.UUID.randomUUID().toString();
                newDraft.setRequestCode(code);
                newDraft.setSupplier(supplier);
                newDraft.setWarehouseStaff(staff);
                newDraft.setPurchaseDate(java.time.LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).withNano(0));
                newDraft.setPaymentStatus("Chưa thanh toán");
                newDraft.setInvoiceDetails(new ArrayList<>());
                drafts.put(code, newDraft);
                ((Map<String, Long>) session.getAttribute("draftVersions4")).put(code, 0L);
                ((Map<String, Map<String, String>>) session.getAttribute("draftFormValues4")).put(code, new HashMap<>());
            }
            String requestCode = newDraft.getRequestCode();
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);

        } catch (IllegalArgumentException ve) {
            request.getSession().setAttribute("flashMessage4", ve.getMessage());
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
        } catch (SQLException e) {
            request.getSession().setAttribute("flashMessage4", "Không thể tải thông tin nhà cung cấp hoặc nhân viên kho. Vui lòng thử lại.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
        }
    }

    public void showDraft(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String requestCode = request.getParameter("requestCode");
        HttpSession session = request.getSession(false);

        Member4 currentUser = session != null ? (Member4) session.getAttribute("currentUser4") : null;
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

        Map<String, PurchaseInvoice4> availableDrafts = (Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4");
        if (availableDrafts == null || !availableDrafts.containsKey(requestCode)) {
            request.getSession().setAttribute("flashMessage4", "Bản nháp hóa đơn không tồn tại hoặc đã hết hạn.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }
        PurchaseInvoice4 draft = null;
        long draftVersion = 1L;
        try {
            synchronized (session) {
                draft = ((Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).get(requestCode);
                draftVersion = ((Map<String, Long>) session.getAttribute("draftVersions4")).get(requestCode);
                request.setAttribute("formValues", new HashMap<>(((Map<String, Map<String, String>>) session.getAttribute("draftFormValues4")).getOrDefault(requestCode, java.util.Collections.emptyMap())));
            }
        } catch (IllegalArgumentException e) {
            request.getSession().setAttribute("flashMessage4", "Bản nháp hóa đơn không hợp lệ hoặc đã hết hạn.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }

        if (draft == null) {
            request.getSession().setAttribute("flashMessage4", "Bản nháp hóa đơn không tồn tại hoặc phiên làm việc đã hết hạn.");
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

        request.setAttribute("purchaseDateDisplay", (draft.getPurchaseDate() == null ? "Chưa ghi nhận" : draft.getPurchaseDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        request.setAttribute("purchaseInvoice", draft);
        request.setAttribute("requestCode", requestCode);
        request.setAttribute("draftVersion", draftVersion);
        request.setAttribute("keyword", keyword != null ? keyword.trim() : "");
        request.setAttribute("selectedPartId", selectedPartId);
        request.setAttribute("focusPartId", focusPartId);
        request.setAttribute("spareParts", spareParts);
        request.setAttribute("purchaseStep", 2);

        request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/purchaseInvoice.jsp").forward(request, response);
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
            int partId = Integer.parseInt(selectedPartIdStr == null ? "" : selectedPartIdStr.trim());
            if (partId <= 0) throw new IllegalArgumentException("selectedPartId: Mã định danh phải là số nguyên dương");
            SparePart4 sparePart = sparePartDAO.getSparePartDetails(partId, false);
            synchronized (session) {
                if (!validateVersionAndConcurrency(request, session, requestCode, response)) return;
                saveLineInputsToDraft(request, session, requestCode);
                PurchaseInvoice4 draft = ((Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).get(requestCode);

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
                    ((Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).put(draft.getRequestCode(), draft);
                    ((Map<String, Long>) session.getAttribute("draftVersions4")).merge(requestCode, 1L, Long::sum);
                }

                response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode +
                "&focusPartId=" + partId);

            }
        } catch (IllegalArgumentException | SQLException e) {
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
                int removeId = Integer.parseInt(removePartIdStr == null ? "" : removePartIdStr.trim());
                if (removeId <= 0) throw new IllegalArgumentException("removePartId: Mã định danh phải là số nguyên dương");
                PurchaseInvoice4 draft = ((Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).get(requestCode);
                draft.getInvoiceDetails().removeIf(d -> d.getSparePart() != null && d.getSparePart().getId() == removeId);
                ((Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).put(draft.getRequestCode(), draft);
                {
                    Map<String, String> previousValues = ((Map<String, Map<String, String>>) session.getAttribute("draftFormValues4")).get(requestCode);
                    if (previousValues != null) {
                        for (String prefix : new String[]{"quantity_", "purchaseUnitPrice_", "error_quantity_", "error_purchaseUnitPrice_"})
                        previousValues.remove(prefix + removeId);
                    }
                }
                ((Map<String, Long>) session.getAttribute("draftVersions4")).merge(requestCode, 1L, Long::sum);
            } catch (IllegalArgumentException ignored) {
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
                synchronized (session) {
                    for (String stateKey : new String[]{"purchaseDrafts4", "draftVersions4", "draftFormValues4", "draftConfirming4"}) {
                        Object state = session.getAttribute(stateKey);
                        if (state instanceof Map) ((Map<?, ?>) state).remove(requestCode);
                        if (state instanceof java.util.Set) ((java.util.Set<?>) state).remove(requestCode);
                    }
                }
            } catch (IllegalArgumentException e) {
                response.sendError(400, e.getMessage());
                return;
            }
        }
        request.getSession().setAttribute("flashMessage4", "Đã hủy bản nháp hóa đơn nhập.");
        response.sendRedirect(request.getContextPath() + "/home");
    }

    public void confirmInvoice(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String requestCode = request.getParameter("requestCode");
        HttpSession session = request.getSession(false);
        Member4 currentUser = session != null ? (Member4) session.getAttribute("currentUser4") : null;

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
                        synchronized (session) {
                            for (String stateKey : new String[]{"purchaseDrafts4", "draftVersions4", "draftFormValues4", "draftConfirming4"}) {
                                Object state = session.getAttribute(stateKey);
                                if (state instanceof Map) ((Map<?, ?>) state).remove(requestCode);
                                if (state instanceof java.util.Set) ((java.util.Set<?>) state).remove(requestCode);
                            }
                        }
                    } catch (IllegalArgumentException ignored) {
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
                PurchaseInvoice4 storedDraft = ((Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).get(requestCode);
                ((java.util.Set<String>) session.getAttribute("draftConfirming4")).add(requestCode);
                draft = new PurchaseInvoice4();
                draft.setRequestCode(storedDraft.getRequestCode());
                draft.setSupplier(storedDraft.getSupplier());
                draft.setWarehouseStaff(storedDraft.getWarehouseStaff());
                draft.setPurchaseDate(storedDraft.getPurchaseDate());
                draft.setPaymentStatus(storedDraft.getPaymentStatus());
                List<PurchaseInvoiceDetail4> frozenLines = new ArrayList<>();
                for (PurchaseInvoiceDetail4 line : storedDraft.getInvoiceDetails()) {
                    PurchaseInvoiceDetail4 copy = new PurchaseInvoiceDetail4();
                    copy.setSparePart(line.getSparePart());
                    copy.setQuantity(line.getQuantity());
                    copy.setPurchaseUnitPrice(line.getPurchaseUnitPrice());
                    copy.setSparePartNameSnapshot(line.getSparePartNameSnapshot());
                    copy.setUnitSnapshot(line.getUnitSnapshot());
                    frozenLines.add(copy);
                }
                draft.setInvoiceDetails(frozenLines);
                confirming = true;
            }

            // Kiểm tra xem draft có dòng nào bị parse lỗi không
            Map<String, String> rawInputs = new HashMap<>(((Map<String, Map<String, String>>) session.getAttribute("draftFormValues4")).getOrDefault(requestCode, java.util.Collections.emptyMap()));
            for (Map.Entry<String, String> entry : rawInputs.entrySet()) {
                if (entry.getKey().startsWith("error_")) {
                    request.getSession().setAttribute("flashMessage4", "Hóa đơn còn dữ liệu chưa hợp lệ. Vui lòng kiểm tra lại các ô báo đỏ.");
                    response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);
                    return;
                }
            }

            if (draft.getInvoiceDetails() == null || draft.getInvoiceDetails().isEmpty()) {
                request.getSession().setAttribute("flashMessage4", "Hóa đơn nhập phải có ít nhất một dòng phụ tùng.");
                response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);
                return;
            }

            // Kiểm tra từng dòng: lượng > 0, giá > 0
            for (PurchaseInvoiceDetail4 d : draft.getInvoiceDetails()) {
                if (d.getQuantity() == null || d.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                    request.getSession().setAttribute("flashMessage4", "Phụ tùng '" + d.getSparePart().getName() + "' chưa nhập số lượng hợp lệ (> 0).");
                    response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode +
                    "&focusPartId=" + d.getSparePart().getId());
                    return;
                }
                if (d.getPurchaseUnitPrice() == null || d.getPurchaseUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    request.getSession().setAttribute("flashMessage4", "Phụ tùng '" + d.getSparePart().getName() + "' chưa nhập đơn giá nhập hợp lệ (> 0).");
                    response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode +
                    "&focusPartId=" + d.getSparePart().getId());
                    return;
                }
            }

            // Lưu chính thức vào CSDL
            int invoiceId = purchaseInvoiceDAO.savePurchaseInvoice(draft);
            synchronized (session) {
                for (String stateKey : new String[]{"purchaseDrafts4", "draftVersions4", "draftFormValues4", "draftConfirming4"}) {
                    Object state = session.getAttribute(stateKey);
                    if (state instanceof Map) ((Map<?, ?>) state).remove(requestCode);
                    if (state instanceof java.util.Set) ((java.util.Set<?>) state).remove(requestCode);
                }
            }

            request.getSession().setAttribute("flashMessage4", "Hóa đơn nhập HN" + invoiceId + " đã được xác nhận và nhập kho thành công.");
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/view?id=" + invoiceId);

        } catch (IllegalArgumentException ve) {
            request.getSession().setAttribute("flashMessage4", "Lỗi xác thực: " + ve.getMessage());
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);
        } catch (SQLException e) {
            request.getSession().setAttribute("flashMessage4", "Lỗi cơ sở dữ liệu khi xác nhận hóa đơn. Vui lòng thử lại.");
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?requestCode=" + requestCode);
        } finally {
            if (confirming) {
                synchronized (session) {
                    ((java.util.Set<?>) session.getAttribute("draftConfirming4")).remove(requestCode);
                }
            }
        }
    }

    public void viewInvoice(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String idStr = request.getParameter("id");
        int id;
        try {
            id = Integer.parseInt(idStr == null ? "" : idStr.trim());
            if (id <= 0) throw new IllegalArgumentException("id: Mã định danh phải là số nguyên dương");
            PurchaseInvoice4 invoice = purchaseInvoiceDAO.getPurchaseInvoiceDetails(id);
            if (invoice == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hóa đơn nhập số: " + idStr);
                return;
            }

            HttpSession session = request.getSession(false);
            Member4 currentUser = session != null ? (Member4) session.getAttribute("currentUser4") : null;
            boolean canRecordPayment = currentUser != null && "WarehouseStaff".equals(currentUser.getRole())
                    && "Chưa thanh toán".equals(invoice.getPaymentStatus());

            request.setAttribute("purchaseDateDisplay", (invoice.getPurchaseDate() == null ? "Chưa ghi nhận" : invoice.getPurchaseDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            request.setAttribute("confirmedAtDisplay", (invoice.getConfirmedAt() == null ? "Chưa ghi nhận" : invoice.getConfirmedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            request.setAttribute("paidAtDisplay", (invoice.getPaidAt() == null ? "Chưa ghi nhận" : invoice.getPaidAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            request.setAttribute("purchaseInvoice", invoice);
            request.setAttribute("canRecordPayment", canRecordPayment);
            request.setAttribute("purchaseStep", 3);
            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/confirmedInvoice.jsp").forward(request, response);

        } catch (IllegalArgumentException ve) {
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
            id = Integer.parseInt(idStr == null ? "" : idStr.trim());
            if (id <= 0) throw new IllegalArgumentException("id: Mã định danh phải là số nguyên dương");
            PurchaseInvoice4 invoice = purchaseInvoiceDAO.getPurchaseInvoiceDetails(id);
            if (invoice == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hóa đơn nhập số: " + idStr);
                return;
            }

            request.setAttribute("purchaseDateDisplay", (invoice.getPurchaseDate() == null ? "Chưa ghi nhận" : invoice.getPurchaseDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            request.setAttribute("confirmedAtDisplay", (invoice.getConfirmedAt() == null ? "Chưa ghi nhận" : invoice.getConfirmedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            request.setAttribute("paidAtDisplay", (invoice.getPaidAt() == null ? "Chưa ghi nhận" : invoice.getPaidAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            request.setAttribute("purchaseInvoice", invoice);
            request.setAttribute("purchaseStep", 4);
            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/printInvoice.jsp").forward(request, response);

        } catch (IllegalArgumentException ve) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã hóa đơn không hợp lệ");
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi CSDL khi tải hóa đơn");
        }
    }

    public void recordPayment(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Member4 currentUser = session != null ? (Member4) session.getAttribute("currentUser4") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idStr = request.getParameter("id");
        String amountText = request.getParameter("amountPaid");

        int id;
        try {
            id = Integer.parseInt(idStr == null ? "" : idStr.trim());
            if (id <= 0) throw new IllegalArgumentException("id: Mã định danh phải là số nguyên dương");
        } catch (IllegalArgumentException ve) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ve.getMessage());
            return;
        }

        try {
            String amountText1 = amountText == null ? "" : amountText.trim();
            if (!amountText1.matches("[0-9]+(\\.[0-9]+)?"))
            throw new IllegalArgumentException("amountPaid: Giá trị phải là số dương hợp lệ, không chứa dấu phẩy hay ký tự đặc biệt");
            BigDecimal parsedAmount1 = new BigDecimal(amountText1);
            if (parsedAmount1.scale() > 2 || parsedAmount1.precision() - parsedAmount1.scale() > 18 - 2)
            throw new IllegalArgumentException("amountPaid: Giá trị số vượt quá độ chính xác cho phép");
            if (parsedAmount1.signum() < 0 || (!false && parsedAmount1.signum() == 0))
            throw new IllegalArgumentException("amountPaid: Giá trị phải lớn hơn 0");
            BigDecimal amount = parsedAmount1;
            boolean success = purchaseInvoiceDAO.recordPayment(id, amount, currentUser.getId());
            if (success) {
                request.getSession().setAttribute("flashMessage4", "Đã ghi nhận thanh toán thành công cho hóa đơn HN" + id);
            } else {
                request.getSession().setAttribute("flashMessage4", "Hóa đơn HN" + id + " đã được thanh toán trước đó.");
            }
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/view?id=" + id);

        } catch (IllegalArgumentException ve) {
            String validationMessage = ve.getMessage();
            String validationField = null;
            int separator = validationMessage == null ? -1 : validationMessage.indexOf(": ");
            if (separator > 0) {
                validationField = validationMessage.substring(0, separator);
                validationMessage = validationMessage.substring(separator + 2);
            }
            if ("permission".equals(validationField)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, validationMessage);
            } else if ("id".equals(validationField)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, validationMessage);
            } else {
                Map<String, String> formValues = new HashMap<>();
                formValues.put("amountPaid", amountText != null ? amountText : "");
                request.setAttribute("formValues", formValues);
                request.setAttribute("errorMessage", validationMessage);
                viewInvoice(request, response);
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi CSDL khi thanh toán");
        }
    }

    public void showUnpaidInvoices(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Member4 currentUser = session != null ? (Member4) session.getAttribute("currentUser4") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<PurchaseInvoice4> items = purchaseInvoiceDAO.getUnpaidInvoices(currentUser.getId());
            request.setAttribute("unpaidInvoices", items);
            request.setAttribute("activeNav", "unpaid");
            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/warehouseHome.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi CSDL khi tải danh sách hóa đơn chưa thanh toán.");
            request.getRequestDispatcher("/WEB-INF/view/warehouseStaff/warehouseHome.jsp").forward(request, response);
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
            {
                Map<String, Long> storedVersions = (Map<String, Long>) session.getAttribute("draftVersions4");
                if (storedVersions == null || !storedVersions.containsKey(requestCode))
                throw new IllegalArgumentException("khongTonTai: Bản nháp không tồn tại hoặc đã hết hạn");
                if (storedVersions.get(requestCode).longValue() != Long.parseLong(submitted.trim()))
                throw new IllegalArgumentException("conflict: Dữ liệu bản nháp đã được cập nhật từ thao tác khác. Vui lòng tải lại trang.");
            }
            Member4 user = (Member4) session.getAttribute("currentUser4");
            PurchaseInvoice4 draft = ((Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).get(requestCode);
            if (user == null || draft.getWarehouseStaff().getId() != user.getId()) {
                res.sendError(403, "Bạn không có quyền sửa bản nháp này");
                return false;
            }
            Object confirming = session.getAttribute("draftConfirming4");
            if (confirming instanceof java.util.Set && ((java.util.Set<?>) confirming).contains(requestCode)) {
                res.sendError(409, "Bản nháp đang được xác nhận. Vui lòng đợi và tải lại trang.");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            res.sendError(400, "Thiếu hoặc sai phiên bản bản nháp");
        } catch (IllegalArgumentException e) {
            String validationMessage = e.getMessage();
            String validationField = null;
            int separator = validationMessage == null ? -1 : validationMessage.indexOf(": ");
            if (separator > 0) {
                validationField = validationMessage.substring(0, separator);
                validationMessage = validationMessage.substring(separator + 2);
            }
            res.sendError("conflict".equals(validationField) ? 409 : 400, validationMessage);
        }
        return false;
    }

    private void saveLineInputsToDraft(HttpServletRequest req, HttpSession session, String requestCode) {
        if (session == null || requestCode == null) return;
        synchronized (session) {
            try {
                PurchaseInvoice4 draft = ((Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).get(requestCode);
                Map<String, String> rawValues = new HashMap<>();
                // Iterate trusted stored rows: omitted/blank fields must not reuse old valid values.
                for (PurchaseInvoiceDetail4 target : draft.getInvoiceDetails()) {
                    int pid = target.getSparePart().getId();
                    String qtyStr = req.getParameter("quantity_" + pid);
                    String priceStr = req.getParameter("purchaseUnitPrice_" + pid);
                    rawValues.put("quantity_" + pid, qtyStr == null ? "" : qtyStr);
                    rawValues.put("purchaseUnitPrice_" + pid, priceStr == null ? "" : priceStr);
                    try {
                        String amountText2 = qtyStr == null ? "" : qtyStr.trim();
                        if (!amountText2.matches("[0-9]+(\\.[0-9]+)?"))
                        throw new IllegalArgumentException("quantity: Giá trị phải là số dương hợp lệ, không chứa dấu phẩy hay ký tự đặc biệt");
                        BigDecimal parsedAmount2 = new BigDecimal(amountText2);
                        if (parsedAmount2.scale() > 3 || parsedAmount2.precision() - parsedAmount2.scale() > 12 - 3)
                        throw new IllegalArgumentException("quantity: Giá trị số vượt quá độ chính xác cho phép");
                        if (parsedAmount2.signum() < 0)
                        throw new IllegalArgumentException("quantity: Giá trị phải lớn hơn 0");
                        BigDecimal qty = parsedAmount2;
                        if (qty.signum() > 0) {
                            if (qty == null) {
                                throw new IllegalArgumentException("quantity: Số lượng không được để trống");
                            }
                            if (qty.compareTo(BigDecimal.ZERO) <= 0) {
                                throw new IllegalArgumentException("quantity: Số lượng phải lớn hơn 0");
                            }

                            if ("Cái".equals((target.getSparePart().getUnit())) || "Chiếc".equals((target.getSparePart().getUnit())) || "Bộ".equals((target.getSparePart().getUnit()))) {
                                if (qty.stripTrailingZeros().scale() > 0) {
                                    throw new IllegalArgumentException("quantity: Đơn vị " + (target.getSparePart().getUnit()) + " phải có số lượng nguyên");
                                }
                            } else if ("Lít".equals((target.getSparePart().getUnit()))) {
                                if (qty.scale() > 3) {
                                    throw new IllegalArgumentException("quantity: Đơn vị Lít chỉ cho phép tối đa 3 chữ số thập phân");
                                }
                            }
                        }
                        target.setQuantity(qty);
                    } catch (IllegalArgumentException e) {
                        target.setQuantity(BigDecimal.ZERO);
                        rawValues.put("error_quantity_" + pid, e.getMessage().replaceFirst("^[A-Za-z_][A-Za-z0-9_]*: ", ""));
                    }
                    try {
                        String amountText3 = priceStr == null ? "" : priceStr.trim();
                        if (!amountText3.matches("[0-9]+(\\.[0-9]+)?"))
                        throw new IllegalArgumentException("purchaseUnitPrice: Giá trị phải là số dương hợp lệ, không chứa dấu phẩy hay ký tự đặc biệt");
                        BigDecimal parsedAmount3 = new BigDecimal(amountText3);
                        if (parsedAmount3.scale() > 2 || parsedAmount3.precision() - parsedAmount3.scale() > 18 - 2)
                        throw new IllegalArgumentException("purchaseUnitPrice: Giá trị số vượt quá độ chính xác cho phép");
                        if (parsedAmount3.signum() < 0)
                        throw new IllegalArgumentException("purchaseUnitPrice: Giá trị phải lớn hơn 0");
                        target.setPurchaseUnitPrice(parsedAmount3);
                    } catch (IllegalArgumentException e) {
                        target.setPurchaseUnitPrice(BigDecimal.ZERO);
                        rawValues.put("error_purchaseUnitPrice_" + pid, e.getMessage().replaceFirst("^[A-Za-z_][A-Za-z0-9_]*: ", ""));
                    }
                }
                ((Map<String, PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).put(draft.getRequestCode(), draft);
                ((Map<String, Map<String, String>>) session.getAttribute("draftFormValues4")).put(requestCode, new HashMap<>(rawValues));
                ((Map<String, Long>) session.getAttribute("draftVersions4")).merge(requestCode, 1L, Long::sum);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Bản nháp không còn hợp lệ sau khi kiểm tra phiên bản", e);
            }
        }
    }
}
