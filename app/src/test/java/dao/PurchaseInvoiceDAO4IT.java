package dao;

import model.*;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
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
        String databasePath = URI.create(TEST_DB_URL.substring("jdbc:".length())).getPath();
        assertTrue(databasePath != null && databasePath.matches("/[A-Za-z0-9_]+_test"),
                "Integration test chỉ được kết nối database có tên kết thúc _test");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASS)) {
                dbAvailable = connection.isValid(2);
            }
        } catch (Throwable e) {
            dbAvailable = false;
        }
    }

    @Test
    @DisplayName("Kiểm tra cấu trúc và tính toán dữ liệu của lớp DAO trước khi gọi DB")
    void testDaoInstantiation() {
        PurchaseInvoiceDAO4 dao = testDAO();
        assertNotNull(dao);
    }

    @Test
    @DisplayName("Giao dịch nhập và cập nhật tồn kho (chỉ chạy khi MySQL garaman4_test sẵn sàng)")
    void testSavePurchaseInvoiceAndIncreaseStock() throws SQLException, IllegalArgumentException {
        Assumptions.assumeTrue(dbAvailable, "Bỏ qua kiểm thử tích hợp do CSDL test MySQL chưa sẵn sàng");

        PurchaseInvoiceDAO4 dao = testDAO();
        SparePartDAO4 partDAO = new SparePartDAO4() {
            @Override protected Connection getConnection() throws SQLException {
                return DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASS);
            }
        };
        BigDecimal stockBefore = partDAO.getStockQuantity(1);

        Supplier4 sup = new Supplier4();
        sup.setId(1);

        WarehouseStaff4 staff = new WarehouseStaff4();
        staff.setId(2);

        SparePart4 sparePart = new SparePart4();
        sparePart.setId(1);
        sparePart.setUnit("Cái");

        PurchaseInvoiceDetail4 detail = new PurchaseInvoiceDetail4();
        detail.setSparePart(sparePart);
        detail.setQuantity(new BigDecimal("10.000"));
        detail.setPurchaseUnitPrice(new BigDecimal("80000.00"));

        PurchaseInvoice4 invoice = new PurchaseInvoice4();
        String uuid = UUID.randomUUID().toString();
        invoice.setRequestCode(uuid);
        invoice.setSupplier(sup);
        invoice.setWarehouseStaff(staff);

        List<PurchaseInvoiceDetail4> list = new ArrayList<>();
        list.add(detail);
        invoice.setInvoiceDetails(list);

        int invoiceId = dao.savePurchaseInvoice(invoice);
        assertTrue(invoiceId > 0);
        assertEquals(0, stockBefore.add(new BigDecimal("10")).compareTo(partDAO.getStockQuantity(1)));
        assertEquals(invoiceId, dao.savePurchaseInvoice(invoice), "Retry phải trả lại cùng hóa đơn");
        assertEquals(0, stockBefore.add(new BigDecimal("10")).compareTo(partDAO.getStockQuantity(1)),
                "Retry không được tăng tồn thêm lần nữa");

        // Đọc lại kiểm tra snapshot
        PurchaseInvoice4 saved = dao.getPurchaseInvoiceDetails(invoiceId);
        assertNotNull(saved);
        assertEquals(uuid, saved.getRequestCode());
        assertEquals("Chưa thanh toán", saved.getPaymentStatus());
        assertNotNull(saved.getSupplierNameSnapshot());
        assertNotNull(saved.getWarehouseStaffNameSnapshot());

        // Kiểm tra thanh toán
        BigDecimal total = saved.getTotalAmount();
        boolean paid = dao.recordPayment(invoiceId, total, 2);
        assertTrue(paid);

        // Thanh toán lặp lại (idempotent)
        boolean repeatPaid = dao.recordPayment(invoiceId, total, 2);
        assertFalse(repeatPaid);
        assertEquals(0, stockBefore.add(new BigDecimal("10")).compareTo(partDAO.getStockQuantity(1)),
                "Thanh toán không được làm thay đổi tồn");
    }

    private static PurchaseInvoiceDAO4 testDAO() {
        return new PurchaseInvoiceDAO4() {
            @Override protected Connection getConnection() throws SQLException {
                return DriverManager.getConnection(TEST_DB_URL, TEST_DB_USER, TEST_DB_PASS);
            }
        };
    }
}
