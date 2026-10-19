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

    public static String batBuoc(String value, String field, int maxLength) throws ValidationException4 {
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

    public static String tuyChon(String value, int maxLength) throws ValidationException4 {
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

    public static int idDuong(String value, String field) throws ValidationException4 {
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

    public static BigDecimal soThapPhan(String value, String field, int precision, int scale, boolean choPhepKhong) throws ValidationException4 {
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

        if (choPhepKhong) {
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

    public static void kiemTraSoLuong(BigDecimal qty, String donVi) throws ValidationException4 {
        if (qty == null) {
            throw new ValidationException4("soLuong", "Số lượng không được để trống");
        }
        if (qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException4("soLuong", "Số lượng phải lớn hơn 0");
        }

        if (AppConstants4.DON_VI_CAI.equals(donVi) || AppConstants4.DON_VI_CHIEC.equals(donVi) || AppConstants4.DON_VI_BO.equals(donVi)) {
            if (qty.stripTrailingZeros().scale() > 0) {
                throw new ValidationException4("soLuong", "Đơn vị " + donVi + " phải có số lượng nguyên");
            }
        } else if (AppConstants4.DON_VI_LIT.equals(donVi)) {
            if (qty.scale() > 3) {
                throw new ValidationException4("soLuong", "Đơn vị Lít chỉ cho phép tối đa 3 chữ số thập phân");
            }
        }
    }

    public static void kiemTraEmail(String email) throws ValidationException4 {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        String trimmed = email.trim();
        if (trimmed.length() > 255 || !EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException4("email", "Địa chỉ email không đúng định dạng");
        }
    }

    public static void kiemTraDienThoai(String phone) throws ValidationException4 {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException4("dienThoai", "Số điện thoại không được để trống");
        }
        String cleaned = phone.trim().replaceAll("[\\s-]", "");
        if (!PHONE_PATTERN.matcher(cleaned).matches()) {
            throw new ValidationException4("dienThoai", "Số điện thoại phải từ 8 đến 15 chữ số, có thể bắt đầu bằng dấu +");
        }
    }

    public static void kiemTraNhaCungCap(Supplier4 supplier) throws ValidationException4 {
        if (supplier == null) {
            throw new ValidationException4("nhaCungCap", "Thông tin nhà cung cấp không được null");
        }
        batBuoc(supplier.getTen(), "ten", 255);
        batBuoc(supplier.getDiaChi(), "diaChi", 5000);
        kiemTraDienThoai(supplier.getDienThoai());
        if (supplier.getEmail() != null) {
            kiemTraEmail(supplier.getEmail());
        }
    }

    public static void kiemTraPhuTung(SparePart4 part) throws ValidationException4 {
        if (part == null) {
            throw new ValidationException4("phuTung", "Thông tin phụ tùng không được null");
        }
        batBuoc(part.getTen(), "ten", 255);
        batBuoc(part.getHangSanXuat(), "hangSanXuat", 255);

        String donVi = part.getDonVi();
        if (donVi == null || (!AppConstants4.DON_VI_CAI.equals(donVi) &&
                              !AppConstants4.DON_VI_CHIEC.equals(donVi) &&
                              !AppConstants4.DON_VI_BO.equals(donVi) &&
                              !AppConstants4.DON_VI_LIT.equals(donVi))) {
            throw new ValidationException4("donVi", "Đơn vị tính không hợp lệ (chỉ chấp nhận Cái, Chiếc, Bộ, Lít)");
        }

        if (part.getNhom() == null || part.getNhom().getId() <= 0) {
            throw new ValidationException4("nhomId", "Phải chọn nhóm phụ tùng hợp lệ đang sử dụng");
        }

        String trangThai = part.getTrangThai();
        if (trangThai == null || (!AppConstants4.TRANG_THAI_PT_CHUA_CONG_BO.equals(trangThai) &&
                                  !AppConstants4.TRANG_THAI_PT_DANG_KINH_DOANH.equals(trangThai))) {
            throw new ValidationException4("trangThai", "Trạng thái phụ tùng mới chỉ được chọn 'Chưa công bố' hoặc 'Đang kinh doanh'");
        }

        if (AppConstants4.TRANG_THAI_PT_DANG_KINH_DOANH.equals(trangThai)) {
            if (part.getGiaBan() == null || part.getGiaBan().compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException4("giaBan", "Phụ tùng đang kinh doanh bắt buộc phải có giá bán >= 0");
            }
            if (part.getMoTa() == null || part.getMoTa().trim().isEmpty()) {
                throw new ValidationException4("moTa", "Phụ tùng đang kinh doanh bắt buộc phải có mô tả");
            }
        }

        if (part.getHinhAnh() != null && !part.getHinhAnh().trim().isEmpty()) {
            String img = part.getHinhAnh().trim();
            if (!AppConstants4.ASSET_IMG_PART.equals(img) &&
                !AppConstants4.ASSET_IMG_BRAKE.equals(img) &&
                !AppConstants4.ASSET_IMG_PLACEHOLDER.equals(img)) {
                throw new ValidationException4("hinhAnh", "Đường dẫn ảnh minh họa không nằm trong danh sách tài nguyên cục bộ hợp lệ");
            }
        }
    }

    public static void kiemTraHoaDonNhap(PurchaseInvoice4 hoaDon) throws ValidationException4 {
        if (hoaDon == null) {
            throw new ValidationException4("hoaDon", "Hóa đơn nhập không được null");
        }
        if (hoaDon.getNhaCungCap() == null || hoaDon.getNhaCungCap().getId() <= 0) {
            throw new ValidationException4("nhaCungCap", "Hóa đơn phải gắn với nhà cung cấp hợp lệ");
        }
        if (hoaDon.getNhanVienKho() == null || hoaDon.getNhanVienKho().getId() <= 0) {
            throw new ValidationException4("nhanVienKho", "Hóa đơn phải có thông tin nhân viên kho lập hợp lệ");
        }
        if (hoaDon.getDsChiTietNhap() == null || hoaDon.getDsChiTietNhap().isEmpty()) {
            throw new ValidationException4("chiTiet", "Hóa đơn nhập phải có ít nhất một dòng phụ tùng");
        }

        Set<Integer> partIds = new HashSet<>();
        for (PurchaseInvoiceDetail4 ct : hoaDon.getDsChiTietNhap()) {
            if (ct.getPhuTung() == null || ct.getPhuTung().getId() <= 0) {
                throw new ValidationException4("partId", "Chi tiết dòng nhập thiếu thông tin phụ tùng");
            }
            int pid = ct.getPhuTung().getId();
            if (partIds.contains(pid)) {
                throw new ValidationException4("partId", "Phụ tùng bị trùng lặp trong hóa đơn: " + pid);
            }
            partIds.add(pid);

            if (ct.getSoLuong() == null || ct.getSoLuong().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException4("soLuong_" + pid, "Số lượng nhập phải lớn hơn 0");
            }
            if (ct.getDonGiaNhap() == null || ct.getDonGiaNhap().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException4("donGiaNhap_" + pid, "Đơn giá nhập phải lớn hơn 0");
            }

            kiemTraSoLuong(ct.getSoLuong(), ct.getPhuTung().getDonVi());
        }
    }
}
