package controller;

import dao.PurchaseInvoiceDAO4;
import dao.SparePartDAO4;
import dao.SupplierDAO4;
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

    @Override
    public void init() throws ServletException {
        purchaseInvoiceDAO = new PurchaseInvoiceDAO4();
        sparePartDAO = new SparePartDAO4();
        supplierDAO = new SupplierDAO4();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/warehouse/purchases/draft":
                hienThiBanNhap(request, response);
                break;
            case "/warehouse/purchases/view":
                xemHoaDon(request, response);
                break;
            case "/warehouse/purchases/print":
                inHoaDon(request, response);
                break;
            case "/warehouse/purchases/unpaid":
                hienThiHoaDonChuaThanhToan(request, response);
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
                batDauNhap(request, response);
                break;
            case "/warehouse/purchases/search-part":
                timPhuTung(request, response);
                break;
            case "/warehouse/purchases/add-line":
                themDongNhap(request, response);
                break;
            case "/warehouse/purchases/update":
                capNhatDongNhap(request, response);
                break;
            case "/warehouse/purchases/remove-line":
                xoaDongNhap(request, response);
                break;
            case "/warehouse/purchases/cancel":
                huyBanNhap(request, response);
                break;
            case "/warehouse/purchases/confirm":
                xacNhanHoaDon(request, response);
                break;
            case "/warehouse/purchases/pay":
                ghiNhanThanhToan(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Hành động POST không hợp lệ");
                break;
        }
    }

    public void batDauNhap(HttpServletRequest request, HttpServletResponse response)
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
            supplierId = Validation4.idDuong(supplierIdStr, "supplierId");
            Supplier4 supplier = supplierDAO.layThongTinNhaCungCap(supplierId);
            if (supplier == null) {
                WebUtil4.datFlash(request, "Không tìm thấy nhà cung cấp hoặc nhà cung cấp đã ngừng hoạt động.");
                response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
                return;
            }

            WarehouseStaff4 staff = new WarehouseStaff4();
            staff.setId(currentUser.getId());
            staff.setHoTen(currentUser.getHoTen());

            PurchaseInvoice4 newDraft = DraftStore4.taoBanNhap(session, supplier, staff);
            String maYeuCau = newDraft.getMaYeuCau();
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau);

        } catch (ValidationException4 ve) {
            WebUtil4.datFlash(request, ve.getMessage());
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
        } catch (SQLException e) {
            WebUtil4.datFlash(request, "Lỗi kết nối CSDL khi tải thông tin nhà cung cấp.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
        }
    }

    public void hienThiBanNhap(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String maYeuCau = request.getParameter("maYeuCau");
        HttpSession session = request.getSession(false);

        SessionUser4 currentUser = session != null ? (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER) : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (maYeuCau == null || maYeuCau.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }

        // Kiểm tra xem mã yêu cầu này đã được commit vào DB chưa (hỗ trợ refresh sau xác nhận)
        try {
            Integer committedId = purchaseInvoiceDAO.timIdTheoMaYeuCau(maYeuCau, currentUser.getId());
            if (committedId != null) {
                response.sendRedirect(request.getContextPath() + "/warehouse/purchases/view?id=" + committedId);
                return;
            }
        } catch (SQLException ignored) {
        }

        PurchaseInvoice4 draft = null;
        long draftVersion = 1L;
        try {
            draft = DraftStore4.layBanNhap(session, maYeuCau);
            draftVersion = DraftStore4.layPhienBan(session, maYeuCau);
        } catch (ValidationException4 e) {
            WebUtil4.datFlash(request, "Bản nháp hóa đơn không hợp lệ hoặc đã hết hạn.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }

        if (draft == null) {
            WebUtil4.datFlash(request, "Bản nháp hóa đơn không tồn tại hoặc phiên làm việc đã hết hạn.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }

        String tuKhoa = request.getParameter("tuKhoa");
        String selectedPartId = request.getParameter("selectedPartId");
        String focusPartId = request.getParameter("focusPartId");

        List<SparePart4> dsPhuTungTimKiem = new ArrayList<>();
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            try {
                dsPhuTungTimKiem = sparePartDAO.timPhuTungTheoTen(tuKhoa.trim(), false);
            } catch (SQLException ignored) {
            }
        }

        request.setAttribute("hoaDon", draft);
        request.setAttribute("maYeuCau", maYeuCau);
        request.setAttribute("draftVersion", draftVersion);
        request.setAttribute("tuKhoa", tuKhoa != null ? tuKhoa.trim() : "");
        request.setAttribute("selectedPartId", selectedPartId);
        request.setAttribute("focusPartId", focusPartId);
        request.setAttribute("dsPhuTungTimKiem", dsPhuTungTimKiem);
        request.setAttribute("draftRawInputs", DraftStore4.layDuLieuFormNhap(session, maYeuCau));
        request.setAttribute("buocNhap", 2);

        request.getRequestDispatcher("/WEB-INF/view/warehouse/gdLapHoaDonNhap.jsp").forward(request, response);
    }

    public void timPhuTung(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String maYeuCau = request.getParameter("maYeuCau");
        HttpSession session = request.getSession(false);
        kiemTraVersionVaDongThoi(request, session, maYeuCau, response);
        if (response.isCommitted()) return;

        // Lưu toàn bộ dữ liệu dòng đang sửa
        luuDuLieuDongVaoDraft(request, session, maYeuCau);

        String tuKhoa = request.getParameter("tuKhoa");
        String encoded = tuKhoa != null ? URLEncoder.encode(tuKhoa.trim(), StandardCharsets.UTF_8) : "";
        response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau + "&tuKhoa=" + encoded);
    }

    public void themDongNhap(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String maYeuCau = request.getParameter("maYeuCau");
        HttpSession session = request.getSession(false);
        kiemTraVersionVaDongThoi(request, session, maYeuCau, response);
        if (response.isCommitted()) return;

        luuDuLieuDongVaoDraft(request, session, maYeuCau);

        String selectedPartIdStr = request.getParameter("selectedPartId");
        try {
            int partId = Validation4.idDuong(selectedPartIdStr, "selectedPartId");
            PurchaseInvoice4 draft = DraftStore4.layBanNhap(session, maYeuCau);

            // Kiểm tra xem part đã có trong draft chưa
            boolean daCo = false;
            for (PurchaseInvoiceDetail4 d : draft.getDsChiTietNhap()) {
                if (d.getPhuTung() != null && d.getPhuTung().getId() == partId) {
                    daCo = true;
                    break;
                }
            }

            if (daCo) {
                // Đã có: không thêm dòng mới, chỉ focus vào dòng cũ
                response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau +
                        "&focusPartId=" + partId);
                return;
            }

            SparePart4 part = sparePartDAO.layChiTietPhuTung(partId, false);
            if (part != null) {
                PurchaseInvoiceDetail4 detail = new PurchaseInvoiceDetail4();
                detail.setPhuTung(part);
                detail.setTenPhuTungApDung(part.getTen());
                detail.setDonViApDung(part.getDonVi());
                detail.setSoLuong(BigDecimal.ZERO);
                detail.setDonGiaNhap(BigDecimal.ZERO);
                draft.getDsChiTietNhap().add(detail);
                DraftStore4.tangPhienBan(session, maYeuCau);
            }

            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau +
                    "&focusPartId=" + partId);

        } catch (ValidationException4 | SQLException e) {
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau);
        }
    }

    public void capNhatDongNhap(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String maYeuCau = request.getParameter("maYeuCau");
        HttpSession session = request.getSession(false);
        kiemTraVersionVaDongThoi(request, session, maYeuCau, response);
        if (response.isCommitted()) return;

        luuDuLieuDongVaoDraft(request, session, maYeuCau);

        String dichDen = request.getParameter("dichDen");
        if ("new-part".equalsIgnoreCase(dichDen)) {
            response.sendRedirect(request.getContextPath() + "/warehouse/parts/new?maYeuCau=" + maYeuCau);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau);
    }

    public void xoaDongNhap(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String maYeuCau = request.getParameter("maYeuCau");
        HttpSession session = request.getSession(false);
        kiemTraVersionVaDongThoi(request, session, maYeuCau, response);
        if (response.isCommitted()) return;

        luuDuLieuDongVaoDraft(request, session, maYeuCau);

        String removePartIdStr = request.getParameter("removePartId");
        try {
            int removeId = Validation4.idDuong(removePartIdStr, "removePartId");
            PurchaseInvoice4 draft = DraftStore4.layBanNhap(session, maYeuCau);
            draft.getDsChiTietNhap().removeIf(d -> d.getPhuTung() != null && d.getPhuTung().getId() == removeId);
            DraftStore4.xoaLoiCuaPhuTung(session, maYeuCau, removeId);
            DraftStore4.tangPhienBan(session, maYeuCau);
        } catch (ValidationException4 ignored) {
        }

        response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau);
    }

    public void huyBanNhap(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String maYeuCau = request.getParameter("maYeuCau");
        HttpSession session = request.getSession(false);
        if (session != null && maYeuCau != null) {
            try {
                DraftStore4.xoaBanNhap(session, maYeuCau);
            } catch (ValidationException4 ignored) {
            }
        }
        WebUtil4.datFlash(request, "Đã hủy bản nháp hóa đơn nhập.");
        response.sendRedirect(request.getContextPath() + "/home");
    }

    public void xacNhanHoaDon(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String maYeuCau = request.getParameter("maYeuCau");
        HttpSession session = request.getSession(false);
        SessionUser4 currentUser = session != null ? (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER) : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Kiểm tra idempotent trước
        try {
            Integer existingId = purchaseInvoiceDAO.timIdTheoMaYeuCau(maYeuCau, currentUser.getId());
            if (existingId != null) {
                if (session != null) {
                    try {
                        DraftStore4.xoaBanNhap(session, maYeuCau);
                    } catch (ValidationException4 ignored) {
                    }
                }
                response.sendRedirect(request.getContextPath() + "/warehouse/purchases/view?id=" + existingId);
                return;
            }
        } catch (SQLException ignored) {
        }

        kiemTraVersionVaDongThoi(request, session, maYeuCau, response);
        if (response.isCommitted()) return;

        try {
            DraftStore4.batDauXacNhan(session, maYeuCau);
        } catch (ValidationException4 e) {
            response.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
            return;
        }

        try {
            luuDuLieuDongVaoDraft(request, session, maYeuCau);

            // Kiểm tra xem draft có dòng nào bị parse lỗi không
            Map<String, String> rawInputs = DraftStore4.layDuLieuFormNhap(session, maYeuCau);
            for (Map.Entry<String, String> entry : rawInputs.entrySet()) {
                if (entry.getKey().startsWith("error_")) {
                    WebUtil4.datFlash(request, "Hóa đơn còn dữ liệu chưa hợp lệ. Vui lòng kiểm tra lại các ô báo đỏ.");
                    response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau);
                    return;
                }
            }

            PurchaseInvoice4 draft = DraftStore4.layBanNhap(session, maYeuCau);
            if (draft.getDsChiTietNhap() == null || draft.getDsChiTietNhap().isEmpty()) {
                WebUtil4.datFlash(request, "Hóa đơn nhập phải có ít nhất một dòng phụ tùng.");
                response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau);
                return;
            }

            // Kiểm tra từng dòng: lượng > 0, giá > 0
            for (PurchaseInvoiceDetail4 d : draft.getDsChiTietNhap()) {
                if (d.getSoLuong() == null || d.getSoLuong().compareTo(BigDecimal.ZERO) <= 0) {
                    WebUtil4.datFlash(request, "Phụ tùng '" + d.getPhuTung().getTen() + "' chưa nhập số lượng hợp lệ (> 0).");
                    response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau +
                            "&focusPartId=" + d.getPhuTung().getId());
                    return;
                }
                if (d.getDonGiaNhap() == null || d.getDonGiaNhap().compareTo(BigDecimal.ZERO) <= 0) {
                    WebUtil4.datFlash(request, "Phụ tùng '" + d.getPhuTung().getTen() + "' chưa nhập đơn giá nhập hợp lệ (> 0).");
                    response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau +
                            "&focusPartId=" + d.getPhuTung().getId());
                    return;
                }
            }

            // Lưu chính thức vào CSDL
            int invoiceId = purchaseInvoiceDAO.luuHoaDonNhap(draft);
            DraftStore4.xoaBanNhap(session, maYeuCau);

            WebUtil4.datFlash(request, "Hóa đơn nhập HN" + invoiceId + " đã được xác nhận và nhập kho thành công.");
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/view?id=" + invoiceId);

        } catch (ValidationException4 ve) {
            WebUtil4.datFlash(request, "Lỗi xác thực: " + ve.getMessage());
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau);
        } catch (SQLException e) {
            WebUtil4.datFlash(request, "Lỗi cơ sở dữ liệu khi xác nhận hóa đơn: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau);
        } finally {
            DraftStore4.ketThucXacNhan(session, maYeuCau);
        }
    }

    public void xemHoaDon(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        int id;
        try {
            id = Validation4.idDuong(idStr, "id");
            PurchaseInvoice4 invoice = purchaseInvoiceDAO.layThongTinHoaDonNhap(id);
            if (invoice == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hóa đơn nhập số: " + idStr);
                return;
            }

            request.setAttribute("hoaDon", invoice);
            request.setAttribute("buocNhap", 3);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdHoaDonNhapDaXacNhan.jsp").forward(request, response);

        } catch (ValidationException4 ve) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã hóa đơn không hợp lệ");
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi CSDL khi tải hóa đơn");
        }
    }

    public void inHoaDon(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        int id;
        try {
            id = Validation4.idDuong(idStr, "id");
            PurchaseInvoice4 invoice = purchaseInvoiceDAO.layThongTinHoaDonNhap(id);
            if (invoice == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hóa đơn nhập số: " + idStr);
                return;
            }

            request.setAttribute("hoaDon", invoice);
            request.setAttribute("buocNhap", 4);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdInHoaDonNhap.jsp").forward(request, response);

        } catch (ValidationException4 ve) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mã hóa đơn không hợp lệ");
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi CSDL khi tải hóa đơn");
        }
    }

    public void ghiNhanThanhToan(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        SessionUser4 currentUser = session != null ? (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER) : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idStr = request.getParameter("id");
        String soTienStr = request.getParameter("soTienDaTra");

        int id;
        BigDecimal soTien;
        try {
            id = Validation4.idDuong(idStr, "id");
            soTien = Validation4.soThapPhan(soTienStr, "soTienDaTra", 18, 2, false);
        } catch (ValidationException4 ve) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ve.getMessage());
            return;
        }

        try {
            boolean thanhCong = purchaseInvoiceDAO.ghiNhanThanhToan(id, soTien, currentUser.getId());
            if (thanhCong) {
                WebUtil4.datFlash(request, "Đã ghi nhận thanh toán thành công cho hóa đơn HN" + id);
            } else {
                WebUtil4.datFlash(request, "Hóa đơn HN" + id + " đã được thanh toán trước đó.");
            }
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/view?id=" + id);

        } catch (ValidationException4 ve) {
            if ("quyen".equals(ve.getField())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, ve.getMessage());
            } else if ("id".equals(ve.getField())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, ve.getMessage());
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ve.getMessage());
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi CSDL khi thanh toán");
        }
    }

    public void hienThiHoaDonChuaThanhToan(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        SessionUser4 currentUser = session != null ? (SessionUser4) session.getAttribute(AppConstants4.SESSION_USER) : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<PurchaseInvoice4> ds = purchaseInvoiceDAO.layHoaDonChuaThanhToan(currentUser.getId());
            request.setAttribute("dsHoaDonChuaTra", ds);
            request.setAttribute("activeNav", "unpaid");
            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdChinhKho.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi CSDL khi tải danh sách hóa đơn chưa thanh toán.");
            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdChinhKho.jsp").forward(request, response);
        }
    }

    private void kiemTraVersionVaDongThoi(HttpServletRequest req, HttpSession session, String maYeuCau, HttpServletResponse res)
            throws IOException {
        if (session == null || maYeuCau == null) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu tham số phiên làm việc");
            return;
        }
        String clientVerStr = req.getParameter("draftVersion");
        if (clientVerStr != null && !clientVerStr.trim().isEmpty()) {
            try {
                long clientVer = Long.parseLong(clientVerStr.trim());
                long serverVer = DraftStore4.layPhienBan(session, maYeuCau);
                if (clientVer != serverVer) {
                    res.sendError(HttpServletResponse.SC_CONFLICT, "Dữ liệu hóa đơn nháp đã bị thay đổi ở tab khác. Vui lòng tải lại trang.");
                }
            } catch (NumberFormatException | ValidationException4 e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Phiên bản draft không hợp lệ");
            }
        }
    }

    private void luuDuLieuDongVaoDraft(HttpServletRequest req, HttpSession session, String maYeuCau) {
        if (session == null || maYeuCau == null) return;
        try {
            PurchaseInvoice4 draft = DraftStore4.layBanNhap(session, maYeuCau);
            if (draft == null) return;

            String[] partIds = req.getParameterValues("partId");
            if (partIds == null) return;

            Map<String, String> rawValues = new HashMap<>();

            for (String pidStr : partIds) {
                if (pidStr == null || pidStr.trim().isEmpty()) continue;
                int pid;
                try {
                    pid = Integer.parseInt(pidStr.trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                String qtyStr = req.getParameter("soLuong_" + pid);
                String priceStr = req.getParameter("donGiaNhap_" + pid);

                rawValues.put("soLuong_" + pid, qtyStr != null ? qtyStr : "");
                rawValues.put("donGiaNhap_" + pid, priceStr != null ? priceStr : "");

                // Tìm detail trong draft
                PurchaseInvoiceDetail4 target = null;
                for (PurchaseInvoiceDetail4 d : draft.getDsChiTietNhap()) {
                    if (d.getPhuTung() != null && d.getPhuTung().getId() == pid) {
                        target = d;
                        break;
                    }
                }
                if (target == null) continue;

                // Parse số lượng
                if (qtyStr != null && !qtyStr.trim().isEmpty()) {
                    try {
                        BigDecimal q = Validation4.soThapPhan(qtyStr, "soLuong", 12, 3, true);
                        Validation4.kiemTraSoLuong(q, target.getPhuTung().getDonVi());
                        target.setSoLuong(q);
                        rawValues.remove("error_soLuong_" + pid);
                    } catch (ValidationException4 e) {
                        rawValues.put("error_soLuong_" + pid, e.getMessage());
                    }
                }

                // Parse đơn giá nhập
                if (priceStr != null && !priceStr.trim().isEmpty()) {
                    try {
                        BigDecimal p = Validation4.soThapPhan(priceStr, "donGiaNhap", 18, 2, true);
                        target.setDonGiaNhap(p);
                        rawValues.remove("error_donGiaNhap_" + pid);
                    } catch (ValidationException4 e) {
                        rawValues.put("error_donGiaNhap_" + pid, e.getMessage());
                    }
                }
            }

            DraftStore4.capNhatDuLieuFormNhap(session, maYeuCau, rawValues);
            DraftStore4.tangPhienBan(session, maYeuCau);

        } catch (ValidationException4 ignored) {
        }
    }
}
