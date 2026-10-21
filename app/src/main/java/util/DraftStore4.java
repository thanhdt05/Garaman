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

    private static void validateRequestCode(String requestCode) throws ValidationException4 {
        if (requestCode == null || requestCode.trim().isEmpty()) {
            throw new ValidationException4("requestCode", "Mã yêu cầu bản nháp không được để trống");
        }
        try {
            UUID.fromString(requestCode.trim());
        } catch (IllegalArgumentException e) {
            throw new ValidationException4("requestCode", "Mã yêu cầu bản nháp không đúng định dạng UUID: " + requestCode);
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

    public static void validateDraftLimit(HttpSession session) throws ValidationException4 {
        synchronized (session) {
            Map<String, PurchaseInvoice4> drafts = getDraftMap(session);
            if (drafts.size() >= MAX_DRAFTS_PER_SESSION) {
                throw new ValidationException4("conflict",
                        "Số lượng bản nháp đang mở vượt quá giới hạn (tối đa " + MAX_DRAFTS_PER_SESSION + " bản nháp). Vui lòng hủy các bản nháp không dùng.");
            }
        }
    }

    public static PurchaseInvoice4 createDraft(HttpSession session, Supplier4 supplier, WarehouseStaff4 staff) throws ValidationException4 {
        if (supplier == null || supplier.getId() <= 0) {
            throw new ValidationException4("supplierId", "Nhà cung cấp không hợp lệ");
        }
        if (staff == null || staff.getId() <= 0) {
            throw new ValidationException4("permission", "Nhân viên kho không hợp lệ");
        }

        synchronized (session) {
            validateDraftLimit(session);

            String requestCode = UUID.randomUUID().toString();
            PurchaseInvoice4 draft = new PurchaseInvoice4();
            draft.setRequestCode(requestCode);
            draft.setSupplier(supplier);
            draft.setWarehouseStaff(staff);
            draft.setPurchaseDate(LocalDateTime.now(AppConstants4.APP_ZONE).withNano(0));
            draft.setPaymentStatus(AppConstants4.INVOICE_STATUS_UNPAID);
            draft.setInvoiceDetails(new ArrayList<>());

            getDraftMap(session).put(requestCode, copyDraft(draft));
            getVersionMap(session).put(requestCode, 0L);
            getFormValuesMap(session).put(requestCode, new HashMap<>());

            return copyDraft(draft);
        }
    }

    public static PurchaseInvoice4 getDraft(HttpSession session, String requestCode) throws ValidationException4 {
        validateRequestCode(requestCode);

        synchronized (session) {
            PurchaseInvoice4 draft = getDraftMap(session).get(requestCode);
            if (draft == null) {
                throw new ValidationException4("khongTonTai", "Bản nháp hóa đơn nhập không tồn tại hoặc đã hết hạn: " + requestCode);
            }
            return copyDraft(draft);
        }
    }

    public static void saveDraft(HttpSession session, PurchaseInvoice4 draft) throws ValidationException4 {
        if (draft == null) {
            throw new ValidationException4("draft", "Bản nháp không được null");
        }
        String requestCode = draft.getRequestCode();
        validateRequestCode(requestCode);

        synchronized (session) {
            if (!getDraftMap(session).containsKey(requestCode)) {
                throw new ValidationException4("khongTonTai", "Không tìm thấy bản nháp để lưu: " + requestCode);
            }
            getDraftMap(session).put(requestCode, copyDraft(draft));
        }
    }

    public static void removeDraft(HttpSession session, String requestCode) throws ValidationException4 {
        if (requestCode == null || requestCode.trim().isEmpty()) {
            return;
        }
        validateRequestCode(requestCode);

        synchronized (session) {
            getDraftMap(session).remove(requestCode);
            getVersionMap(session).remove(requestCode);
            getFormValuesMap(session).remove(requestCode);
            getConfirmingSet(session).remove(requestCode);
        }
    }

    public static long getDraftVersion(HttpSession session, String requestCode) throws ValidationException4 {
        validateRequestCode(requestCode);
        synchronized (session) {
            Long ver = getVersionMap(session).get(requestCode);
            if (ver == null) {
                throw new ValidationException4("khongTonTai", "Bản nháp không tồn tại để kiểm tra phiên bản: " + requestCode);
            }
            return ver;
        }
    }

    public static void validateDraftVersion(HttpSession session, String requestCode, long version) throws ValidationException4 {
        long current = getDraftVersion(session, requestCode);
        if (current != version) {
            throw new ValidationException4("conflict",
                    "Dữ liệu bản nháp đã được cập nhật từ thao tác khác (phiên bản form " + version + " != hiện tại " + current + "). Vui lòng tải lại trang.");
        }
    }

    public static long incrementDraftVersion(HttpSession session, String requestCode) throws ValidationException4 {
        validateRequestCode(requestCode);
        synchronized (session) {
            long current = getDraftVersion(session, requestCode);
            long next = current + 1;
            getVersionMap(session).put(requestCode, next);
            return next;
        }
    }

    public static PurchaseInvoice4 beginConfirmation(HttpSession session, String requestCode) throws ValidationException4 {
        long version = getDraftVersion(session, requestCode);
        return beginConfirmation(session, requestCode, version);
    }

    public static PurchaseInvoice4 beginConfirmation(HttpSession session, String requestCode, long version) throws ValidationException4 {
        validateRequestCode(requestCode);
        synchronized (session) {
            validateDraftVersion(session, requestCode, version);
            Set<String> confirming = getConfirmingSet(session);
            if (confirming.contains(requestCode)) {
                throw new ValidationException4("conflict",
                        "Bản nháp đang trong tiến trình xác nhận hóa đơn. Vui lòng đợi hoặc tải lại trang.");
            }
            confirming.add(requestCode);
            return getDraft(session, requestCode);
        }
    }

    public static void endConfirmation(HttpSession session, String requestCode) {
        if (requestCode == null || requestCode.trim().isEmpty()) {
            return;
        }
        try {
            synchronized (session) {
                getConfirmingSet(session).remove(requestCode);
            }
        } catch (Exception e) {
            // Safe ignore if session invalidated
        }
    }

    public static Map<String, String> getFormValue(HttpSession session, String requestCode) throws ValidationException4 {
        validateRequestCode(requestCode);
        synchronized (session) {
            Map<String, String> values = getFormValuesMap(session).get(requestCode);
            if (values == null) {
                return new HashMap<>();
            }
            return new HashMap<>(values);
        }
    }

    public static Map<String, String> getFormValues(HttpSession session, String requestCode) {
        try {
            return getFormValue(session, requestCode);
        } catch (ValidationException4 e) {
            return new HashMap<>();
        }
    }

    public static void saveFormValue(HttpSession session, String requestCode, Map<String, String> values) throws ValidationException4 {
        validateRequestCode(requestCode);
        synchronized (session) {
            if (values == null) {
                getFormValuesMap(session).remove(requestCode);
            } else {
                getFormValuesMap(session).put(requestCode, new HashMap<>(values));
            }
        }
    }

    public static void updateFormValues(HttpSession session, String requestCode, Map<String, String> values) throws ValidationException4 {
        saveFormValue(session, requestCode, values);
    }

    public static void clearSparePartErrors(HttpSession session, String requestCode, int partId) throws ValidationException4 {
        validateRequestCode(requestCode);
        synchronized (session) {
            Map<String, String> values = getFormValuesMap(session).get(requestCode);
            if (values != null) {
                values.remove("quantity_" + partId);
                values.remove("purchaseUnitPrice_" + partId);
                values.remove("error_quantity_" + partId);
                values.remove("error_purchaseUnitPrice_" + partId);
            }
        }
    }

    public static PurchaseInvoice4 copyDraft(PurchaseInvoice4 draft) {
        if (draft == null) {
            return null;
        }
        PurchaseInvoice4 copy = new PurchaseInvoice4();
        copy.setId(draft.getId());
        copy.setRequestCode(draft.getRequestCode());
        copy.setPurchaseDate(draft.getPurchaseDate());
        copy.setConfirmedAt(draft.getConfirmedAt());
        copy.setPaymentStatus(draft.getPaymentStatus());
        copy.setPaidAt(draft.getPaidAt());
        copy.setSupplierNameSnapshot(draft.getSupplierNameSnapshot());
        copy.setWarehouseStaffNameSnapshot(draft.getWarehouseStaffNameSnapshot());

        if (draft.getSupplier() != null) {
            Supplier4 s = new Supplier4();
            s.setId(draft.getSupplier().getId());
            s.setName(draft.getSupplier().getName());
            s.setAddress(draft.getSupplier().getAddress());
            s.setPhoneNumber(draft.getSupplier().getPhoneNumber());
            s.setEmail(draft.getSupplier().getEmail());
            s.setStatus(draft.getSupplier().getStatus());
            copy.setSupplier(s);
        }

        if (draft.getWarehouseStaff() != null) {
            WarehouseStaff4 w = new WarehouseStaff4();
            w.setId(draft.getWarehouseStaff().getId());
            w.setFullName(draft.getWarehouseStaff().getFullName());
            w.setUsername(draft.getWarehouseStaff().getUsername());
            w.setRole(draft.getWarehouseStaff().getRole());
            w.setPosition(draft.getWarehouseStaff().getPosition());
            w.setEmploymentStatus(draft.getWarehouseStaff().getEmploymentStatus());
            copy.setWarehouseStaff(w);
        }

        List<PurchaseInvoiceDetail4> listCopy = new ArrayList<>();
        if (draft.getInvoiceDetails() != null) {
            for (PurchaseInvoiceDetail4 d : draft.getInvoiceDetails()) {
                PurchaseInvoiceDetail4 dc = new PurchaseInvoiceDetail4();
                dc.setId(d.getId());
                dc.setQuantity(d.getQuantity());
                dc.setPurchaseUnitPrice(d.getPurchaseUnitPrice());
                dc.setSparePartNameSnapshot(d.getSparePartNameSnapshot());
                dc.setUnitSnapshot(d.getUnitSnapshot());

                if (d.getSparePart() != null) {
                    SparePart4 p = new SparePart4();
                    p.setId(d.getSparePart().getId());
                    p.setName(d.getSparePart().getName());
                    p.setDescription(d.getSparePart().getDescription());
                    p.setImagePath(d.getSparePart().getImagePath());
                    p.setUnit(d.getSparePart().getUnit());
                    p.setManufacturer(d.getSparePart().getManufacturer());
                    p.setSpecifications(d.getSparePart().getSpecifications());
                    p.setCompatibleVehicleModels(d.getSparePart().getCompatibleVehicleModels());
                    p.setSellingPrice(d.getSparePart().getSellingPrice());
                    p.setWarrantyPeriod(d.getSparePart().getWarrantyPeriod());
                    p.setWarrantyTerms(d.getSparePart().getWarrantyTerms());
                    p.setStatus(d.getSparePart().getStatus());
                    p.setStockQuantity(d.getSparePart().getStockQuantity());

                    if (d.getSparePart().getCategory() != null) {
                        SparePartCategory4 cat = new SparePartCategory4();
                        cat.setId(d.getSparePart().getCategory().getId());
                        cat.setName(d.getSparePart().getCategory().getName());
                        cat.setDescription(d.getSparePart().getCategory().getDescription());
                        cat.setStatus(d.getSparePart().getCategory().getStatus());
                        p.setCategory(cat);
                    }
                    dc.setSparePart(p);
                }
                listCopy.add(dc);
            }
        }
        copy.setInvoiceDetails(listCopy);
        return copy;
    }
}
