package util;

import exception.ValidationException4;
import model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử quy tắc xác thực dữ liệu (Validation4)")
class Validation4Test {

    @Test
    @DisplayName("Kiểm tra chuỗi bắt buộc (batBuoc)")
    void testBatBuoc() throws ValidationException4 {
        assertEquals("Hà Nội", Validation4.batBuoc("  Hà Nội  ", "diaChi", 50));

        assertThrows(ValidationException4.class, () -> Validation4.batBuoc(null, "ten", 50));
        assertThrows(ValidationException4.class, () -> Validation4.batBuoc("   ", "ten", 50));
        assertThrows(ValidationException4.class, () -> Validation4.batBuoc("Chuỗi quá dài so với quy định", "ten", 10));
    }

    @Test
    @DisplayName("Kiểm tra ID số nguyên dương (idDuong)")
    void testIdDuong() throws ValidationException4 {
        assertEquals(1, Validation4.idDuong("1", "id"));
        assertEquals(999, Validation4.idDuong(" 999 ", "id"));

        assertThrows(ValidationException4.class, () -> Validation4.idDuong(null, "id"));
        assertThrows(ValidationException4.class, () -> Validation4.idDuong("0", "id"));
        assertThrows(ValidationException4.class, () -> Validation4.idDuong("-5", "id"));
        assertThrows(ValidationException4.class, () -> Validation4.idDuong("abc", "id"));
    }

    @Test
    @DisplayName("Kiểm tra định dạng và độ chính xác số thập phân (soThapPhan)")
    void testSoThapPhan() throws ValidationException4 {
        // DECIMAL(12,3)
        BigDecimal val1 = Validation4.soThapPhan("123.456", "soLuong", 12, 3, false);
        assertEquals(new BigDecimal("123.456"), val1);

        // Vượt quá scale cho phép
        assertThrows(ValidationException4.class, () ->
                Validation4.soThapPhan("123.4567", "soLuong", 12, 3, false));

        // Ký tự chữ hoặc đặc biệt
        assertThrows(ValidationException4.class, () ->
                Validation4.soThapPhan("12a.5", "soLuong", 12, 3, false));

        // Giá trị âm
        assertThrows(ValidationException4.class, () ->
                Validation4.soThapPhan("-10", "soLuong", 12, 3, false));

        // Giá trị 0 khi không cho phép 0
        assertThrows(ValidationException4.class, () ->
                Validation4.soThapPhan("0", "soLuong", 12, 3, false));

        // Giá trị 0 khi cho phép 0
        BigDecimal valZero = Validation4.soThapPhan("0", "giaBan", 15, 2, true);
        assertEquals(BigDecimal.ZERO, valZero);
    }

    @Test
    @DisplayName("Kiểm tra quy tắc số lượng theo đơn vị tính (Cái/Chiếc/Bộ vs Lít)")
    void testKiemTraSoLuongTheoDonVi() throws ValidationException4 {
        // Đơn vị Cái/Chiếc/Bộ: bắt buộc số nguyên
        assertDoesNotThrow(() -> Validation4.kiemTraSoLuong(new BigDecimal("5"), AppConstants4.DON_VI_CAI));
        assertDoesNotThrow(() -> Validation4.kiemTraSoLuong(new BigDecimal("5.000"), AppConstants4.DON_VI_CHIEC));
        assertDoesNotThrow(() -> Validation4.kiemTraSoLuong(new BigDecimal("12"), AppConstants4.DON_VI_BO));

        // Lẻ đối với Cái/Chiếc/Bộ -> Ngoại lệ
        assertThrows(ValidationException4.class, () ->
                Validation4.kiemTraSoLuong(new BigDecimal("2.5"), AppConstants4.DON_VI_CAI));
        assertThrows(ValidationException4.class, () ->
                Validation4.kiemTraSoLuong(new BigDecimal("1.25"), AppConstants4.DON_VI_CHIEC));

        // Đơn vị Lít: cho phép thập phân tối đa 3 chữ số
        assertDoesNotThrow(() -> Validation4.kiemTraSoLuong(new BigDecimal("2.5"), AppConstants4.DON_VI_LIT));
        assertDoesNotThrow(() -> Validation4.kiemTraSoLuong(new BigDecimal("2.500"), AppConstants4.DON_VI_LIT));
        assertDoesNotThrow(() -> Validation4.kiemTraSoLuong(new BigDecimal("0.75"), AppConstants4.DON_VI_LIT));

        // Lít vượt quá 3 chữ số thập phân -> Ngoại lệ
        assertThrows(ValidationException4.class, () ->
                Validation4.kiemTraSoLuong(new BigDecimal("2.1234"), AppConstants4.DON_VI_LIT));

        // Số lượng <= 0 -> Ngoại lệ
        assertThrows(ValidationException4.class, () ->
                Validation4.kiemTraSoLuong(BigDecimal.ZERO, AppConstants4.DON_VI_LIT));
        assertThrows(ValidationException4.class, () ->
                Validation4.kiemTraSoLuong(new BigDecimal("-1"), AppConstants4.DON_VI_CAI));
    }

    @Test
    @DisplayName("Kiểm tra định dạng email và điện thoại")
    void testKiemTraEmailVaDienThoai() throws ValidationException4 {
        // Điện thoại
        assertDoesNotThrow(() -> Validation4.kiemTraDienThoai("0912345678"));
        assertDoesNotThrow(() -> Validation4.kiemTraDienThoai("+84912345678"));
        assertDoesNotThrow(() -> Validation4.kiemTraDienThoai("024-38388388"));

        assertThrows(ValidationException4.class, () -> Validation4.kiemTraDienThoai("12345")); // quá ngắn
        assertThrows(ValidationException4.class, () -> Validation4.kiemTraDienThoai("0912abc789"));
        assertThrows(ValidationException4.class, () -> Validation4.kiemTraDienThoai(null));

        // Email
        assertDoesNotThrow(() -> Validation4.kiemTraEmail("user@example.com"));
        assertDoesNotThrow(() -> Validation4.kiemTraEmail("")); // rỗng cho qua vì email là tùy chọn
        assertDoesNotThrow(() -> Validation4.kiemTraEmail(null));

        assertThrows(ValidationException4.class, () -> Validation4.kiemTraEmail("invalid-email"));
        assertThrows(ValidationException4.class, () -> Validation4.kiemTraEmail("user@"));
    }

    @Test
    @DisplayName("Kiểm tra phụ tùng: Chưa công bố vs Đang kinh doanh")
    void testKiemTraPhuTung() {
        SparePartCategory4 category = new SparePartCategory4();
        category.setId(1);
        category.setTen("Phụ tùng bảo dưỡng");

        // Hợp lệ: Chưa công bố (không cần giá bán, không cần mô tả)
        SparePart4 unpub = new SparePart4();
        unpub.setTen("Dầu phanh DOT4");
        unpub.setHangSanXuat("Bosch");
        unpub.setDonVi(AppConstants4.DON_VI_LIT);
        unpub.setNhom(category);
        unpub.setTrangThai(AppConstants4.TRANG_THAI_PT_CHUA_CONG_BO);
        assertDoesNotThrow(() -> Validation4.kiemTraPhuTung(unpub));

        // Đang kinh doanh nhưng thiếu giá bán hoặc mô tả -> Ngoại lệ
        SparePart4 activeInvalid = new SparePart4();
        activeInvalid.setTen("Lọc dầu");
        activeInvalid.setHangSanXuat("Toyota");
        activeInvalid.setDonVi(AppConstants4.DON_VI_CAI);
        activeInvalid.setNhom(category);
        activeInvalid.setTrangThai(AppConstants4.TRANG_THAI_PT_DANG_KINH_DOANH);
        assertThrows(ValidationException4.class, () -> Validation4.kiemTraPhuTung(activeInvalid));

        // Đang kinh doanh hợp lệ
        activeInvalid.setGiaBan(new BigDecimal("150000.00"));
        activeInvalid.setMoTa("Lọc dầu chính hãng");
        activeInvalid.setHinhAnh(AppConstants4.ASSET_IMG_PART);
        assertDoesNotThrow(() -> Validation4.kiemTraPhuTung(activeInvalid));

        // Đơn vị tính không hợp lệ -> Ngoại lệ
        activeInvalid.setDonVi("Thùng");
        assertThrows(ValidationException4.class, () -> Validation4.kiemTraPhuTung(activeInvalid));
    }

    @Test
    @DisplayName("Kiểm tra hợp lệ hóa đơn nhập và phát hiện trùng lặp phụ tùng")
    void testKiemTraHoaDonNhap() {
        Supplier4 supplier = new Supplier4();
        supplier.setId(1);

        WarehouseStaff4 staff = new WarehouseStaff4();
        staff.setId(2);

        SparePart4 p1 = new SparePart4();
        p1.setId(10);
        p1.setDonVi(AppConstants4.DON_VI_CAI);

        PurchaseInvoiceDetail4 d1 = new PurchaseInvoiceDetail4();
        d1.setPhuTung(p1);
        d1.setSoLuong(new BigDecimal("5.000"));
        d1.setDonGiaNhap(new BigDecimal("200000.00"));

        PurchaseInvoice4 invoice = new PurchaseInvoice4();
        invoice.setNhaCungCap(supplier);
        invoice.setNhanVienKho(staff);

        List<PurchaseInvoiceDetail4> list = new ArrayList<>();
        list.add(d1);
        invoice.setDsChiTietNhap(list);

        // Hợp lệ 1 dòng
        assertDoesNotThrow(() -> Validation4.kiemTraHoaDonNhap(invoice));

        // Thêm 1 dòng trùng partId 10 -> Ngoại lệ trùng lặp
        PurchaseInvoiceDetail4 d2 = new PurchaseInvoiceDetail4();
        d2.setPhuTung(p1);
        d2.setSoLuong(new BigDecimal("2.000"));
        d2.setDonGiaNhap(new BigDecimal("210000.00"));
        list.add(d2);

        assertThrows(ValidationException4.class, () -> Validation4.kiemTraHoaDonNhap(invoice));
    }
}
