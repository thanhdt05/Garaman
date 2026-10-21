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
    @DisplayName("Kiểm tra chuỗi bắt buộc (requireText)")
    void testRequireText() throws ValidationException4 {
        assertEquals("Hà Nội", Validation4.requireText("  Hà Nội  ", "address", 50));

        assertThrows(ValidationException4.class, () -> Validation4.requireText(null, "name", 50));
        assertThrows(ValidationException4.class, () -> Validation4.requireText("   ", "name", 50));
        assertThrows(ValidationException4.class, () -> Validation4.requireText("Chuỗi quá dài so với quy định", "name", 10));
    }

    @Test
    @DisplayName("Kiểm tra ID số nguyên dương (positiveId)")
    void testPositiveId() throws ValidationException4 {
        assertEquals(1, Validation4.positiveId("1", "id"));
        assertEquals(999, Validation4.positiveId(" 999 ", "id"));

        assertThrows(ValidationException4.class, () -> Validation4.positiveId(null, "id"));
        assertThrows(ValidationException4.class, () -> Validation4.positiveId("0", "id"));
        assertThrows(ValidationException4.class, () -> Validation4.positiveId("-5", "id"));
        assertThrows(ValidationException4.class, () -> Validation4.positiveId("abc", "id"));
    }

    @Test
    @DisplayName("Kiểm tra định dạng và độ chính xác số thập phân (parseDecimal)")
    void testParseDecimal() throws ValidationException4 {
        // DECIMAL(12,3)
        BigDecimal val1 = Validation4.parseDecimal("123.456", "quantity", 12, 3, false);
        assertEquals(new BigDecimal("123.456"), val1);

        // Vượt quá scale cho phép
        assertThrows(ValidationException4.class, () ->
                Validation4.parseDecimal("123.4567", "quantity", 12, 3, false));

        // Ký tự chữ hoặc đặc biệt
        assertThrows(ValidationException4.class, () ->
                Validation4.parseDecimal("12a.5", "quantity", 12, 3, false));

        // Giá trị âm
        assertThrows(ValidationException4.class, () ->
                Validation4.parseDecimal("-10", "quantity", 12, 3, false));

        // Giá trị 0 khi không cho phép 0
        assertThrows(ValidationException4.class, () ->
                Validation4.parseDecimal("0", "quantity", 12, 3, false));

        // Giá trị 0 khi cho phép 0
        BigDecimal valZero = Validation4.parseDecimal("0", "sellingPrice", 15, 2, true);
        assertEquals(BigDecimal.ZERO, valZero);
    }

    @Test
    @DisplayName("Kiểm tra quy tắc số lượng theo đơn vị tính (Cái/Chiếc/Bộ vs Lít)")
    void testQuantityValidationByUnit() throws ValidationException4 {
        // Đơn vị Cái/Chiếc/Bộ: bắt buộc số nguyên
        assertDoesNotThrow(() -> Validation4.validateQuantity(new BigDecimal("5"), AppConstants4.UNIT_PIECE));
        assertDoesNotThrow(() -> Validation4.validateQuantity(new BigDecimal("5.000"), AppConstants4.UNIT_ITEM));
        assertDoesNotThrow(() -> Validation4.validateQuantity(new BigDecimal("12"), AppConstants4.UNIT_SET));

        // Lẻ đối với Cái/Chiếc/Bộ -> Ngoại lệ
        assertThrows(ValidationException4.class, () ->
                Validation4.validateQuantity(new BigDecimal("2.5"), AppConstants4.UNIT_PIECE));
        assertThrows(ValidationException4.class, () ->
                Validation4.validateQuantity(new BigDecimal("1.25"), AppConstants4.UNIT_ITEM));

        // Đơn vị Lít: cho phép thập phân tối đa 3 chữ số
        assertDoesNotThrow(() -> Validation4.validateQuantity(new BigDecimal("2.5"), AppConstants4.UNIT_LITER));
        assertDoesNotThrow(() -> Validation4.validateQuantity(new BigDecimal("2.500"), AppConstants4.UNIT_LITER));
        assertDoesNotThrow(() -> Validation4.validateQuantity(new BigDecimal("0.75"), AppConstants4.UNIT_LITER));

        // Lít vượt quá 3 chữ số thập phân -> Ngoại lệ
        assertThrows(ValidationException4.class, () ->
                Validation4.validateQuantity(new BigDecimal("2.1234"), AppConstants4.UNIT_LITER));

        // Số lượng <= 0 -> Ngoại lệ
        assertThrows(ValidationException4.class, () ->
                Validation4.validateQuantity(BigDecimal.ZERO, AppConstants4.UNIT_LITER));
        assertThrows(ValidationException4.class, () ->
                Validation4.validateQuantity(new BigDecimal("-1"), AppConstants4.UNIT_PIECE));
    }

    @Test
    @DisplayName("Kiểm tra định dạng email và điện thoại")
    void testEmailAndPhoneNumberValidation() throws ValidationException4 {
        // Điện thoại
        assertDoesNotThrow(() -> Validation4.validatePhoneNumber("0912345678"));
        assertDoesNotThrow(() -> Validation4.validatePhoneNumber("+84912345678"));
        assertDoesNotThrow(() -> Validation4.validatePhoneNumber("024-38388388"));

        assertThrows(ValidationException4.class, () -> Validation4.validatePhoneNumber("12345")); // quá ngắn
        assertThrows(ValidationException4.class, () -> Validation4.validatePhoneNumber("0912abc789"));
        assertThrows(ValidationException4.class, () -> Validation4.validatePhoneNumber(null));

        // Email
        assertDoesNotThrow(() -> Validation4.validateEmail("user@example.com"));
        assertDoesNotThrow(() -> Validation4.validateEmail("")); // rỗng cho qua vì email là tùy chọn
        assertDoesNotThrow(() -> Validation4.validateEmail(null));

        assertThrows(ValidationException4.class, () -> Validation4.validateEmail("invalid-email"));
        assertThrows(ValidationException4.class, () -> Validation4.validateEmail("user@"));
    }

    @Test
    @DisplayName("Kiểm tra phụ tùng: Chưa công bố vs Đang kinh doanh")
    void testValidateSparePart() {
        SparePartCategory4 category = new SparePartCategory4();
        category.setId(1);
        category.setName("Phụ tùng bảo dưỡng");

        // Hợp lệ: Chưa công bố (không cần giá bán, không cần mô tả)
        SparePart4 unpub = new SparePart4();
        unpub.setName("Dầu phanh DOT4");
        unpub.setManufacturer("Bosch");
        unpub.setUnit(AppConstants4.UNIT_LITER);
        unpub.setCategory(category);
        unpub.setStatus(AppConstants4.PART_STATUS_UNPUBLISHED);
        assertDoesNotThrow(() -> Validation4.validateSparePart(unpub));

        // Đang kinh doanh nhưng thiếu giá bán hoặc mô tả -> Ngoại lệ
        SparePart4 activeInvalid = new SparePart4();
        activeInvalid.setName("Lọc dầu");
        activeInvalid.setManufacturer("Toyota");
        activeInvalid.setUnit(AppConstants4.UNIT_PIECE);
        activeInvalid.setCategory(category);
        activeInvalid.setStatus(AppConstants4.PART_STATUS_ACTIVE);
        assertThrows(ValidationException4.class, () -> Validation4.validateSparePart(activeInvalid));

        // Đang kinh doanh hợp lệ
        activeInvalid.setSellingPrice(new BigDecimal("150000.00"));
        activeInvalid.setDescription("Lọc dầu chính hãng");
        activeInvalid.setImagePath(AppConstants4.ASSET_IMG_PART);
        assertDoesNotThrow(() -> Validation4.validateSparePart(activeInvalid));

        // Đơn vị tính không hợp lệ -> Ngoại lệ
        activeInvalid.setUnit("Thùng");
        assertThrows(ValidationException4.class, () -> Validation4.validateSparePart(activeInvalid));
    }

    @Test
    @DisplayName("Kiểm tra hợp lệ hóa đơn nhập và phát hiện trùng lặp phụ tùng")
    void testValidatePurchaseInvoice() {
        Supplier4 supplier = new Supplier4();
        supplier.setId(1);

        WarehouseStaff4 staff = new WarehouseStaff4();
        staff.setId(2);

        SparePart4 p1 = new SparePart4();
        p1.setId(10);
        p1.setUnit(AppConstants4.UNIT_PIECE);

        PurchaseInvoiceDetail4 d1 = new PurchaseInvoiceDetail4();
        d1.setSparePart(p1);
        d1.setQuantity(new BigDecimal("5.000"));
        d1.setPurchaseUnitPrice(new BigDecimal("200000.00"));

        PurchaseInvoice4 invoice = new PurchaseInvoice4();
        invoice.setSupplier(supplier);
        invoice.setWarehouseStaff(staff);

        List<PurchaseInvoiceDetail4> list = new ArrayList<>();
        list.add(d1);
        invoice.setInvoiceDetails(list);

        // Hợp lệ 1 dòng
        assertDoesNotThrow(() -> Validation4.validatePurchaseInvoice(invoice));

        // Thêm 1 dòng trùng partId 10 -> Ngoại lệ trùng lặp
        PurchaseInvoiceDetail4 d2 = new PurchaseInvoiceDetail4();
        d2.setSparePart(p1);
        d2.setQuantity(new BigDecimal("2.000"));
        d2.setPurchaseUnitPrice(new BigDecimal("210000.00"));
        list.add(d2);

        assertThrows(ValidationException4.class, () -> Validation4.validatePurchaseInvoice(invoice));
    }
}
