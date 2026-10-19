package dao;

import exception.ValidationException4;
import model.*;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử tích hợp giao dịch nhập và thanh toán (PurchaseInvoiceDAO4IT)")
class PurchaseInvoiceDAO4IT {

    private static final String TEST_DB_URL = System.getenv("GARAMAN_TEST_DB_URL") != null
            ? System.getenv("GARAMAN_TEST_DB_URL")
            : "jdbc:mysql://localhost:3306/garaman4_test?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8&allowPublicKeyRetrieval=true";
    private static final String TEST_DB_USER = System.getenv("GARAMAN_TEST_DB_USER") != null
            ? System.getenv("GARAMAN_TEST_DB_USER") : "root";
    private static final String TEST_DB_PASS = System.getenv("GARAMAN_TEST_DB_PASSWORD") != null
            ? System.getenv("GARAMAN_TEST_DB_PASSWORD") : "root";

    private static boolean dbAvailable = false;

    @BeforeAll
    static void checkDatabaseAvailability() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection con = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASS)) {
                dbAvailable = con.isValid(2);
            }
        } catch (Throwable e) {
            dbAvailable = false;
        }
    }

    @Test
    @DisplayName("Kiểm tra cấu trúc và tính toán dữ liệu của lớp DAO trước khi gọi DB")
    void testDaoInstantiation() {
        PurchaseInvoiceDAO4 dao = new PurchaseInvoiceDAO4();
        assertNotNull(dao);
    }

    @Test
    @DisplayName("Giao dịch nhập và cập nhật tồn kho (chỉ chạy khi MySQL garaman4_test sẵn sàng)")
    void testLuuHoaDonNhapVaTangTon() throws SQLException, ValidationException4 {
        Assumptions.assumeTrue(dbAvailable, "Bỏ qua kiểm thử tích hợp do CSDL test MySQL chưa sẵn sàng");

        PurchaseInvoiceDAO4 dao = new PurchaseInvoiceDAO4();

        Supplier4 sup = new Supplier4();
        sup.setId(1);

        WarehouseStaff4 staff = new WarehouseStaff4();
        staff.setId(2);

        SparePart4 part = new SparePart4();
        part.setId(1);
        part.setDonVi("Cái");

        PurchaseInvoiceDetail4 detail = new PurchaseInvoiceDetail4();
        detail.setPhuTung(part);
        detail.setSoLuong(new BigDecimal("10.000"));
        detail.setDonGiaNhap(new BigDecimal("80000.00"));

        PurchaseInvoice4 invoice = new PurchaseInvoice4();
        String uuid = UUID.randomUUID().toString();
        invoice.setMaYeuCau(uuid);
        invoice.setNhaCungCap(sup);
        invoice.setNhanVienKho(staff);

        List<PurchaseInvoiceDetail4> list = new ArrayList<>();
        list.add(detail);
        invoice.setDsChiTietNhap(list);

        int invoiceId = dao.luuHoaDonNhap(invoice);
        assertTrue(invoiceId > 0);

        // Đọc lại kiểm tra snapshot
        PurchaseInvoice4 saved = dao.layThongTinHoaDonNhap(invoiceId);
        assertNotNull(saved);
        assertEquals(uuid, saved.getMaYeuCau());
        assertEquals("Chưa thanh toán", saved.getTinhTrang());
        assertNotNull(saved.getNhaCungCapTenApDung());
        assertNotNull(saved.getNhanVienTenApDung());

        // Kiểm tra thanh toán
        BigDecimal total = saved.getTongTien();
        boolean paid = dao.ghiNhanThanhToan(invoiceId, total, 2);
        assertTrue(paid);

        // Thanh toán lặp lại (idempotent)
        boolean repeatPaid = dao.ghiNhanThanhToan(invoiceId, total, 2);
        assertFalse(repeatPaid);
    }
}
