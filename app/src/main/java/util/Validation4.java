package util;

import exception.ValidationException4;
import model.PurchaseInvoice4;
import model.PurchaseInvoiceDetail4;
import model.SparePart4;
import model.Supplier4;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public final class Validation4 {

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{8,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Validation4() {
        // Private constructor
    }

    public static String requireText(String value, String field, int maxLength) throws ValidationException4 {
        if (value == null) {
            throw new ValidationException4(field, "Trường này là bắt buộc");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException4(field, "Trường này không được để trống");
        }
        if (trimmed.length() > maxLength) {
            throw new ValidationException4(field, "Độ dài không được vượt quá " + maxLength + " ký tự");
        }
        return trimmed;
    }

    public static String optionalText(String value, int maxLength) throws ValidationException4 {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() > maxLength) {
            throw new ValidationException4(null, "Độ dài vượt quá giới hạn cho phép (" + maxLength + " ký tự)");
        }
        return trimmed;
    }

    public static int positiveId(String value, String field) throws ValidationException4 {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException4(field, "Mã định danh không được để trống");
        }
        try {
            int id = Integer.parseInt(value.trim());
            if (id <= 0) {
                throw new ValidationException4(field, "Mã định danh phải là số nguyên dương");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new ValidationException4(field, "Mã định danh không hợp lệ: " + value);
        }
    }

    public static BigDecimal parseDecimal(String value, String field, int precision, int scale, boolean allowZero) throws ValidationException4 {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException4(field, "Giá trị số không được để trống");
        }
        String trimmed = value.trim();
        if (!NUMERIC_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException4(field, "Giá trị phải là số dương hợp lệ, không chứa dấu phẩy hay ký tự đặc biệt");
        }

        BigDecimal num;
        try {
            num = new BigDecimal(trimmed);
        } catch (NumberFormatException e) {
            throw new ValidationException4(field, "Số không hợp lệ");
        }

        if (num.scale() > scale) {
            throw new ValidationException4(field, "Số phần thập phân không được vượt quá " + scale + " chữ số");
        }

        int integerDigits = num.precision() - num.scale();
        int maxIntegerDigits = precision - scale;
        if (integerDigits > maxIntegerDigits) {
            throw new ValidationException4(field, "Giá trị số vượt quá giới hạn tối đa cho phép");
        }

        if (allowZero) {
            if (num.compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException4(field, "Giá trị không được âm");
            }
        } else {
            if (num.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException4(field, "Giá trị phải lớn hơn 0");
            }
        }

        return num;
    }

    public static void validateQuantity(BigDecimal qty, String unit) throws ValidationException4 {
        if (qty == null) {
            throw new ValidationException4("quantity", "Số lượng không được để trống");
        }
        if (qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException4("quantity", "Số lượng phải lớn hơn 0");
        }

        if (AppConstants4.UNIT_PIECE.equals(unit) || AppConstants4.UNIT_ITEM.equals(unit) || AppConstants4.UNIT_SET.equals(unit)) {
            if (qty.stripTrailingZeros().scale() > 0) {
                throw new ValidationException4("quantity", "Đơn vị " + unit + " phải có số lượng nguyên");
            }
        } else if (AppConstants4.UNIT_LITER.equals(unit)) {
            if (qty.scale() > 3) {
                throw new ValidationException4("quantity", "Đơn vị Lít chỉ cho phép tối đa 3 chữ số thập phân");
            }
        }
    }

    public static void validateEmail(String email) throws ValidationException4 {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        String trimmed = email.trim();
        if (trimmed.length() > 255 || !EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException4("email", "Địa chỉ email không đúng định dạng");
        }
    }

    public static void validatePhoneNumber(String phone) throws ValidationException4 {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException4("phoneNumber", "Số điện thoại không được để trống");
        }
        String cleaned = phone.trim().replaceAll("[\\s-]", "");
        if (!PHONE_PATTERN.matcher(cleaned).matches()) {
            throw new ValidationException4("phoneNumber", "Số điện thoại phải từ 8 đến 15 chữ số, có thể bắt đầu bằng dấu +");
        }
    }

    public static void validateSupplier(Supplier4 supplier) throws ValidationException4 {
        if (supplier == null) {
            throw new ValidationException4("supplier", "Thông tin nhà cung cấp không được null");
        }
        requireText(supplier.getName(), "name", 255);
        requireText(supplier.getAddress(), "address", 5000);
        validatePhoneNumber(supplier.getPhoneNumber());
        if (supplier.getEmail() != null) {
            validateEmail(supplier.getEmail());
        }
    }

    public static void validateSparePart(SparePart4 sparePart) throws ValidationException4 {
        if (sparePart == null) {
            throw new ValidationException4("sparePart", "Thông tin phụ tùng không được null");
        }
        requireText(sparePart.getName(), "name", 255);
        requireText(sparePart.getManufacturer(), "manufacturer", 255);

        String unit = sparePart.getUnit();
        if (unit == null || (!AppConstants4.UNIT_PIECE.equals(unit) &&
                              !AppConstants4.UNIT_ITEM.equals(unit) &&
                              !AppConstants4.UNIT_SET.equals(unit) &&
                              !AppConstants4.UNIT_LITER.equals(unit))) {
            throw new ValidationException4("unit", "Đơn vị tính không hợp lệ (chỉ chấp nhận Cái, Chiếc, Bộ, Lít)");
        }

        if (sparePart.getCategory() == null || sparePart.getCategory().getId() <= 0) {
            throw new ValidationException4("categoryId", "Phải chọn nhóm phụ tùng hợp lệ đang sử dụng");
        }

        String status = sparePart.getStatus();
        if (status == null || (!AppConstants4.PART_STATUS_UNPUBLISHED.equals(status) &&
                                  !AppConstants4.PART_STATUS_ACTIVE.equals(status))) {
            throw new ValidationException4("status", "Trạng thái phụ tùng mới chỉ được chọn 'Chưa công bố' hoặc 'Đang kinh doanh'");
        }

        if (AppConstants4.PART_STATUS_ACTIVE.equals(status)) {
            if (sparePart.getSellingPrice() == null || sparePart.getSellingPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException4("sellingPrice", "Phụ tùng đang kinh doanh bắt buộc phải có giá bán >= 0");
            }
            if (sparePart.getDescription() == null || sparePart.getDescription().trim().isEmpty()) {
                throw new ValidationException4("description", "Phụ tùng đang kinh doanh bắt buộc phải có mô tả");
            }
        }

        if (sparePart.getImagePath() != null && !sparePart.getImagePath().trim().isEmpty()) {
            String img = sparePart.getImagePath().trim();
            if (!AppConstants4.ASSET_IMG_PART.equals(img) &&
                !AppConstants4.ASSET_IMG_BRAKE.equals(img) &&
                !AppConstants4.ASSET_IMG_PLACEHOLDER.equals(img)) {
                throw new ValidationException4("imagePath", "Đường dẫn ảnh minh họa không nằm trong danh sách tài nguyên cục bộ hợp lệ");
            }
        }
    }

    public static void validatePurchaseInvoice(PurchaseInvoice4 purchaseInvoice) throws ValidationException4 {
        if (purchaseInvoice == null) {
            throw new ValidationException4("purchaseInvoice", "Hóa đơn nhập không được null");
        }
        if (purchaseInvoice.getSupplier() == null || purchaseInvoice.getSupplier().getId() <= 0) {
            throw new ValidationException4("supplier", "Hóa đơn phải gắn với nhà cung cấp hợp lệ");
        }
        if (purchaseInvoice.getWarehouseStaff() == null || purchaseInvoice.getWarehouseStaff().getId() <= 0) {
            throw new ValidationException4("warehouseStaff", "Hóa đơn phải có thông tin nhân viên kho lập hợp lệ");
        }
        if (purchaseInvoice.getInvoiceDetails() == null || purchaseInvoice.getInvoiceDetails().isEmpty()) {
            throw new ValidationException4("invoiceDetail", "Hóa đơn nhập phải có ít nhất một dòng phụ tùng");
        }

        Set<Integer> partIds = new HashSet<>();
        for (PurchaseInvoiceDetail4 invoiceDetail : purchaseInvoice.getInvoiceDetails()) {
            if (invoiceDetail.getSparePart() == null || invoiceDetail.getSparePart().getId() <= 0) {
                throw new ValidationException4("partId", "Chi tiết dòng nhập thiếu thông tin phụ tùng");
            }
            int pid = invoiceDetail.getSparePart().getId();
            if (partIds.contains(pid)) {
                throw new ValidationException4("partId", "Phụ tùng bị trùng lặp trong hóa đơn: " + pid);
            }
            partIds.add(pid);

            if (invoiceDetail.getQuantity() == null || invoiceDetail.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException4("quantity_" + pid, "Số lượng nhập phải lớn hơn 0");
            }
            if (invoiceDetail.getPurchaseUnitPrice() == null || invoiceDetail.getPurchaseUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException4("purchaseUnitPrice_" + pid, "Đơn giá nhập phải lớn hơn 0");
            }

            validateQuantity(invoiceDetail.getQuantity(), invoiceDetail.getSparePart().getUnit());
        }
    }
}
