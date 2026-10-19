package util;

import exception.ValidationException4;
import model.PurchaseInvoice4;
import model.PurchaseInvoiceDetail4;
import model.SparePart4;
import model.SparePartCategory4;
import model.Supplier4;
import model.WarehouseStaff4;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpSession;

public final class DraftStore4 {

    private static final int MAX_DRAFTS_PER_SESSION = 10;

    private DraftStore4() {
        // Private constructor
    }

    private static void kiemTraCuPhapMaYeuCau(String maYeuCau) throws ValidationException4 {
        if (maYeuCau == null || maYeuCau.trim().isEmpty()) {
            throw new ValidationException4("maYeuCau", "Mã yêu cầu bản nháp không được để trống");
        }
        try {
            UUID.fromString(maYeuCau.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidationException4("maYeuCau", "Mã yêu cầu bản nháp không đúng định dạng UUID: " + maYeuCau);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, PurchaseInvoice4> getDraftMap(HttpSession session) {
        synchronized (session) {
            Map<String, PurchaseInvoice4> map = (Map<String, PurchaseInvoice4>) session.getAttribute(AppConstants4.SESSION_PURCHASE_DRAFTS);
            if (map == null) {
                map = new HashMap<>();
                session.setAttribute(AppConstants4.SESSION_PURCHASE_DRAFTS, map);
            }
            return map;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Long> getVersionMap(HttpSession session) {
        synchronized (session) {
            Map<String, Long> map = (Map<String, Long>) session.getAttribute(AppConstants4.SESSION_DRAFT_VERSIONS);
            if (map == null) {
                map = new HashMap<>();
                session.setAttribute(AppConstants4.SESSION_DRAFT_VERSIONS, map);
            }
            return map;
        }
    }

    @SuppressWarnings("unchecked")
    private static Set<String> getConfirmingSet(HttpSession session) {
        synchronized (session) {
            Set<String> set = (Set<String>) session.getAttribute(AppConstants4.SESSION_DRAFT_CONFIRMING);
            if (set == null) {
                set = new HashSet<>();
                session.setAttribute(AppConstants4.SESSION_DRAFT_CONFIRMING, set);
            }
            return set;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, String>> getFormValuesMap(HttpSession session) {
        synchronized (session) {
            Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) session.getAttribute(AppConstants4.SESSION_DRAFT_FORM_VALUES);
            if (map == null) {
                map = new HashMap<>();
                session.setAttribute(AppConstants4.SESSION_DRAFT_FORM_VALUES, map);
            }
            return map;
        }
    }

    public static void kiemTraGioiHan(HttpSession session) throws ValidationException4 {
        synchronized (session) {
            Map<String, PurchaseInvoice4> drafts = getDraftMap(session);
            if (drafts.size() >= MAX_DRAFTS_PER_SESSION) {
                throw new ValidationException4("xungDot",
                        "Số lượng bản nháp đang mở vượt quá giới hạn (tối đa " + MAX_DRAFTS_PER_SESSION + " bản nháp). Vui lòng hủy các bản nháp không dùng.");
            }
        }
    }

    public static PurchaseInvoice4 taoBanNhap(HttpSession session, Supplier4 supplier, WarehouseStaff4 staff) throws ValidationException4 {
        if (supplier == null || supplier.getId() <= 0) {
            throw new ValidationException4("supplierId", "Nhà cung cấp không hợp lệ");
        }
        if (staff == null || staff.getId() <= 0) {
            throw new ValidationException4("quyen", "Nhân viên kho không hợp lệ");
        }

        synchronized (session) {
            kiemTraGioiHan(session);

            String maYeuCau = UUID.randomUUID().toString();
            PurchaseInvoice4 draft = new PurchaseInvoice4();
            draft.setMaYeuCau(maYeuCau);
            draft.setNhaCungCap(supplier);
            draft.setNhanVienKho(staff);
            draft.setNgayNhap(LocalDateTime.now(AppConstants4.APP_ZONE).withNano(0));
            draft.setTinhTrang(AppConstants4.TINH_TRANG_HD_CHUA_THANH_TOAN);
            draft.setDsChiTietNhap(new ArrayList<>());

            getDraftMap(session).put(maYeuCau, saoChep(draft));
            getVersionMap(session).put(maYeuCau, 0L);
            getFormValuesMap(session).put(maYeuCau, new HashMap<>());

            return saoChep(draft);
        }
    }

    public static PurchaseInvoice4 layBanNhap(HttpSession session, String maYeuCau) throws ValidationException4 {
        kiemTraCuPhapMaYeuCau(maYeuCau);

        synchronized (session) {
            PurchaseInvoice4 draft = getDraftMap(session).get(maYeuCau);
            if (draft == null) {
                throw new ValidationException4("khongTonTai", "Bản nháp hóa đơn nhập không tồn tại hoặc đã hết hạn: " + maYeuCau);
            }
            return saoChep(draft);
        }
    }

    public static void luuBanNhap(HttpSession session, PurchaseInvoice4 draft) throws ValidationException4 {
        if (draft == null) {
            throw new ValidationException4("draft", "Bản nháp không được null");
        }
        String maYeuCau = draft.getMaYeuCau();
        kiemTraCuPhapMaYeuCau(maYeuCau);

        synchronized (session) {
            if (!getDraftMap(session).containsKey(maYeuCau)) {
                throw new ValidationException4("khongTonTai", "Không tìm thấy bản nháp để lưu: " + maYeuCau);
            }
            getDraftMap(session).put(maYeuCau, saoChep(draft));
        }
    }

    public static void xoaBanNhap(HttpSession session, String maYeuCau) throws ValidationException4 {
        if (maYeuCau == null || maYeuCau.trim().isEmpty()) {
            return;
        }
        kiemTraCuPhapMaYeuCau(maYeuCau);

        synchronized (session) {
            getDraftMap(session).remove(maYeuCau);
            getVersionMap(session).remove(maYeuCau);
            getFormValuesMap(session).remove(maYeuCau);
            getConfirmingSet(session).remove(maYeuCau);
        }
    }

    public static long layPhienBan(HttpSession session, String maYeuCau) throws ValidationException4 {
        kiemTraCuPhapMaYeuCau(maYeuCau);
        synchronized (session) {
            Long ver = getVersionMap(session).get(maYeuCau);
            if (ver == null) {
                throw new ValidationException4("khongTonTai", "Bản nháp không tồn tại để kiểm tra phiên bản: " + maYeuCau);
            }
            return ver;
        }
    }

    public static void kiemTraPhienBan(HttpSession session, String maYeuCau, long phienBan) throws ValidationException4 {
        long current = layPhienBan(session, maYeuCau);
        if (current != phienBan) {
            throw new ValidationException4("xungDot",
                    "Dữ liệu bản nháp đã được cập nhật từ thao tác khác (phiên bản form " + phienBan + " != hiện tại " + current + "). Vui lòng tải lại trang.");
        }
    }

    public static long tangPhienBan(HttpSession session, String maYeuCau) throws ValidationException4 {
        kiemTraCuPhapMaYeuCau(maYeuCau);
        synchronized (session) {
            long current = layPhienBan(session, maYeuCau);
            long next = current + 1;
            getVersionMap(session).put(maYeuCau, next);
            return next;
        }
    }

    public static PurchaseInvoice4 batDauXacNhan(HttpSession session, String maYeuCau) throws ValidationException4 {
        long phienBan = layPhienBan(session, maYeuCau);
        return batDauXacNhan(session, maYeuCau, phienBan);
    }

    public static PurchaseInvoice4 batDauXacNhan(HttpSession session, String maYeuCau, long phienBan) throws ValidationException4 {
        kiemTraCuPhapMaYeuCau(maYeuCau);
        synchronized (session) {
            kiemTraPhienBan(session, maYeuCau, phienBan);
            Set<String> confirming = getConfirmingSet(session);
            if (confirming.contains(maYeuCau)) {
                throw new ValidationException4("xungDot",
                        "Bản nháp đang trong tiến trình xác nhận hóa đơn. Vui lòng đợi hoặc tải lại trang.");
            }
            confirming.add(maYeuCau);
            return layBanNhap(session, maYeuCau);
        }
    }

    public static void ketThucXacNhan(HttpSession session, String maYeuCau) {
        if (maYeuCau == null || maYeuCau.trim().isEmpty()) {
            return;
        }
        try {
            synchronized (session) {
                getConfirmingSet(session).remove(maYeuCau);
            }
        } catch (Exception e) {
            // Safe ignore if session invalidated
        }
    }

    public static Map<String, String> layGiaTriForm(HttpSession session, String maYeuCau) throws ValidationException4 {
        kiemTraCuPhapMaYeuCau(maYeuCau);
        synchronized (session) {
            Map<String, String> values = getFormValuesMap(session).get(maYeuCau);
            if (values == null) {
                return new HashMap<>();
            }
            return new HashMap<>(values);
        }
    }

    public static Map<String, String> layDuLieuFormNhap(HttpSession session, String maYeuCau) {
        try {
            return layGiaTriForm(session, maYeuCau);
        } catch (ValidationException4 e) {
            return new HashMap<>();
        }
    }

    public static void luuGiaTriForm(HttpSession session, String maYeuCau, Map<String, String> values) throws ValidationException4 {
        kiemTraCuPhapMaYeuCau(maYeuCau);
        synchronized (session) {
            if (values == null) {
                getFormValuesMap(session).remove(maYeuCau);
            } else {
                getFormValuesMap(session).put(maYeuCau, new HashMap<>(values));
            }
        }
    }

    public static void capNhatDuLieuFormNhap(HttpSession session, String maYeuCau, Map<String, String> values) throws ValidationException4 {
        luuGiaTriForm(session, maYeuCau, values);
    }

    public static void xoaLoiCuaPhuTung(HttpSession session, String maYeuCau, int partId) throws ValidationException4 {
        kiemTraCuPhapMaYeuCau(maYeuCau);
        synchronized (session) {
            Map<String, String> values = getFormValuesMap(session).get(maYeuCau);
            if (values != null) {
                values.remove("soLuong_" + partId);
                values.remove("donGiaNhap_" + partId);
                values.remove("error_soLuong_" + partId);
                values.remove("error_donGiaNhap_" + partId);
            }
        }
    }

    public static PurchaseInvoice4 saoChep(PurchaseInvoice4 draft) {
        if (draft == null) {
            return null;
        }
        PurchaseInvoice4 copy = new PurchaseInvoice4();
        copy.setId(draft.getId());
        copy.setMaYeuCau(draft.getMaYeuCau());
        copy.setNgayNhap(draft.getNgayNhap());
        copy.setThoiDiemXacNhan(draft.getThoiDiemXacNhan());
        copy.setTinhTrang(draft.getTinhTrang());
        copy.setThoiDiemThanhToan(draft.getThoiDiemThanhToan());
        copy.setNhaCungCapTenApDung(draft.getNhaCungCapTenApDung());
        copy.setNhanVienTenApDung(draft.getNhanVienTenApDung());

        if (draft.getNhaCungCap() != null) {
            Supplier4 s = new Supplier4();
            s.setId(draft.getNhaCungCap().getId());
            s.setTen(draft.getNhaCungCap().getTen());
            s.setDiaChi(draft.getNhaCungCap().getDiaChi());
            s.setDienThoai(draft.getNhaCungCap().getDienThoai());
            s.setEmail(draft.getNhaCungCap().getEmail());
            s.setTrangThai(draft.getNhaCungCap().getTrangThai());
            copy.setNhaCungCap(s);
        }

        if (draft.getNhanVienKho() != null) {
            WarehouseStaff4 w = new WarehouseStaff4();
            w.setId(draft.getNhanVienKho().getId());
            w.setHoTen(draft.getNhanVienKho().getHoTen());
            w.setTenDangNhap(draft.getNhanVienKho().getTenDangNhap());
            w.setVaiTro(draft.getNhanVienKho().getVaiTro());
            w.setViTri(draft.getNhanVienKho().getViTri());
            w.setTrangThaiLamViec(draft.getNhanVienKho().getTrangThaiLamViec());
            copy.setNhanVienKho(w);
        }

        List<PurchaseInvoiceDetail4> listCopy = new ArrayList<>();
        if (draft.getDsChiTietNhap() != null) {
            for (PurchaseInvoiceDetail4 d : draft.getDsChiTietNhap()) {
                PurchaseInvoiceDetail4 dc = new PurchaseInvoiceDetail4();
                dc.setId(d.getId());
                dc.setSoLuong(d.getSoLuong());
                dc.setDonGiaNhap(d.getDonGiaNhap());
                dc.setTenPhuTungApDung(d.getTenPhuTungApDung());
                dc.setDonViApDung(d.getDonViApDung());

                if (d.getPhuTung() != null) {
                    SparePart4 p = new SparePart4();
                    p.setId(d.getPhuTung().getId());
                    p.setTen(d.getPhuTung().getTen());
                    p.setMoTa(d.getPhuTung().getMoTa());
                    p.setHinhAnh(d.getPhuTung().getHinhAnh());
                    p.setDonVi(d.getPhuTung().getDonVi());
                    p.setHangSanXuat(d.getPhuTung().getHangSanXuat());
                    p.setThongSo(d.getPhuTung().getThongSo());
                    p.setDongXePhuHop(d.getPhuTung().getDongXePhuHop());
                    p.setGiaBan(d.getPhuTung().getGiaBan());
                    p.setHanBaoHanh(d.getPhuTung().getHanBaoHanh());
                    p.setDieuKienBaoHanh(d.getPhuTung().getDieuKienBaoHanh());
                    p.setTrangThai(d.getPhuTung().getTrangThai());
                    p.setTonKho(d.getPhuTung().getTonKho());

                    if (d.getPhuTung().getNhom() != null) {
                        SparePartCategory4 cat = new SparePartCategory4();
                        cat.setId(d.getPhuTung().getNhom().getId());
                        cat.setTen(d.getPhuTung().getNhom().getTen());
                        cat.setMoTa(d.getPhuTung().getNhom().getMoTa());
                        cat.setTrangThai(d.getPhuTung().getNhom().getTrangThai());
                        p.setNhom(cat);
                    }
                    dc.setPhuTung(p);
                }
                listCopy.add(dc);
            }
        }
        copy.setDsChiTietNhap(listCopy);
        return copy;
    }
}
