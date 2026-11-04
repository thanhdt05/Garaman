package dao;

import model.*;
import org.junit.jupiter.api.*;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/** Real MySQL transactions: shift handover, audit identity, rollback and concurrent payment. */
class PurchasePaymentFlowIT {
    private static final String URL = System.getenv("GARAMAN_TEST_DB_URL");
    private static final String USER = System.getenv("GARAMAN_TEST_DB_USER");
    private static final String PASSWORD = System.getenv("GARAMAN_TEST_DB_PASSWORD");
    private PurchaseInvoiceDAO4 dao;
    private int invoiceId, otherStaffId;
    private String otherStaffName, creatorName;
    private BigDecimal stockBeforePayment;
    private final BigDecimal total = new BigDecimal("800000.00");

    @BeforeAll static void requireIsolatedDatabase() {
        Assumptions.assumeTrue(URL != null, "Cần GARAMAN_TEST_DB_URL để chạy MySQL IT");
        String path = URI.create(URL.substring("jdbc:".length())).getPath();
        assertTrue(path != null && path.matches("/[A-Za-z0-9_]+_test"), "Chỉ chạy trên CSDL có hậu tố _test");
    }

    private static Connection open() throws SQLException { return DriverManager.getConnection(URL, USER, PASSWORD); }

    @BeforeEach void setup() throws Exception {
        dao = new PurchaseInvoiceDAO4() {
            @Override protected Connection getConnection() throws SQLException { return open(); }
        };
        otherStaffName = "Nhân viên ca sau " + UUID.randomUUID();
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(
                "INSERT INTO tblMember4 (fullName, username, password, phoneNumber, role, accountStatus) "
                + "SELECT ?, ?, password, phoneNumber, 'WarehouseStaff', 'Hoạt động' FROM tblMember4 WHERE id = 2",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, otherStaffName); ps.setString(2, "flow_" + UUID.randomUUID());
            assertEquals(1, ps.executeUpdate());
            try (ResultSet rs = ps.getGeneratedKeys()) { assertTrue(rs.next()); otherStaffId = rs.getInt(1); }
            try (PreparedStatement emp = c.prepareStatement(
                    "INSERT INTO tblEmployee4 (tblMember4id, position, employmentStatus) VALUES (?, 'Kho', 'Đang làm việc')")) {
                emp.setInt(1, otherStaffId); emp.executeUpdate();
            }
        }
        Supplier4 supplier = new Supplier4(); supplier.setId(1);
        WarehouseStaff4 creator = new WarehouseStaff4(); creator.setId(2);
        SparePart4 part = new SparePart4(); part.setId(1); part.setUnit("Cái");
        PurchaseInvoiceDetail4 line = new PurchaseInvoiceDetail4(); line.setSparePart(part);
        line.setQuantity(BigDecimal.TEN); line.setPurchaseUnitPrice(new BigDecimal("80000.00"));
        PurchaseInvoice4 invoice = new PurchaseInvoice4(); invoice.setRequestCode(UUID.randomUUID().toString());
        invoice.setSupplier(supplier); invoice.setWarehouseStaff(creator); invoice.setInvoiceDetails(List.of(line));
        invoiceId = dao.savePurchaseInvoice(invoice);
        creatorName = dao.getPurchaseInvoiceDetails(invoiceId).getWarehouseStaffNameSnapshot();
        stockBeforePayment = stock();
    }

    private BigDecimal stock() throws SQLException {
        try (Connection c = open(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(
                "SELECT stockQuantity FROM tblSparePart4 WHERE id = 1")) { assertTrue(rs.next()); return rs.getBigDecimal(1); }
    }

    private Map<String,Object> stored() throws SQLException {
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(
                "SELECT paymentStatus, paidAt, paidByEmployee4id, paidByNameSnapshot, tblEmployee4id, "
                + "warehouseStaffNameSnapshot FROM tblPurchaseInvoice4 WHERE id = ?")) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next()); Map<String,Object> row = new HashMap<>();
                for (String key : List.of("paymentStatus", "paidAt", "paidByEmployee4id", "paidByNameSnapshot",
                        "tblEmployee4id", "warehouseStaffNameSnapshot")) row.put(key, rs.getObject(key));
                return row;
            }
        }
    }

    private void assertUnpaid() throws SQLException {
        Map<String,Object> row = stored();
        assertEquals("Chưa thanh toán", row.get("paymentStatus"));
        assertNull(row.get("paidAt")); assertNull(row.get("paidByEmployee4id")); assertNull(row.get("paidByNameSnapshot"));
        assertEquals(0, stockBeforePayment.compareTo(stock()));
    }

    @Test void nextShiftCanSeeAndPayAnotherCreatorsInvoiceWithoutChangingCreator() throws Exception {
        assertTrue(dao.getUnpaidInvoices(otherStaffId).stream().anyMatch(i -> i.getId() == invoiceId));
        assertTrue(dao.recordPayment(invoiceId, total, otherStaffId));
        Map<String,Object> paid = stored();
        assertEquals("Đã thanh toán", paid.get("paymentStatus")); assertNotNull(paid.get("paidAt"));
        assertEquals(otherStaffId, ((Number)paid.get("paidByEmployee4id")).intValue());
        assertEquals(otherStaffName, paid.get("paidByNameSnapshot"));
        assertEquals(2, ((Number)paid.get("tblEmployee4id")).intValue());
        assertEquals(creatorName, paid.get("warehouseStaffNameSnapshot"));
        assertFalse(dao.getUnpaidInvoices(otherStaffId).stream().anyMatch(i -> i.getId() == invoiceId));
        assertFalse(dao.recordPayment(invoiceId, total, 2));
        assertEquals(paid, stored(), "Gửi lại từ ca khác không được đổi người trả hoặc thời điểm trả");
        assertEquals(0, stockBeforePayment.compareTo(stock()));
    }

    @Test void incompleteOrExcessPaymentCannotChangeStatusOrStock() throws Exception {
        for (BigDecimal amount : List.of(BigDecimal.ZERO, new BigDecimal("799999"),
                new BigDecimal("800001"), new BigDecimal("800000.001"))) {
            assertThrows(IllegalArgumentException.class, () -> dao.recordPayment(invoiceId, amount, otherStaffId));
            assertUnpaid();
        }
    }

    @Test void customerLockedAccountAndStoppedEmployeeCannotPayOrReadUnpaidList() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> dao.recordPayment(invoiceId, total, 1));
        assertTrue(dao.getUnpaidInvoices(1).isEmpty()); assertUnpaid();
        for (String sql : List.of(
                "UPDATE tblMember4 SET accountStatus = 'Đã khóa' WHERE id = ?",
                "UPDATE tblMember4 SET accountStatus = 'Hoạt động', role = 'Customer' WHERE id = ?",
                "UPDATE tblMember4 SET role = 'WarehouseStaff' WHERE id = ?",
                "UPDATE tblEmployee4 SET employmentStatus = 'Ngừng làm việc' WHERE tblMember4id = ?")) {
            try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, otherStaffId); ps.executeUpdate();
            }
            if (sql.contains("SET role = 'WarehouseStaff'")) continue;
            assertThrows(IllegalArgumentException.class, () -> dao.recordPayment(invoiceId, total, otherStaffId));
            assertTrue(dao.getUnpaidInvoices(otherStaffId).isEmpty()); assertUnpaid();
            PurchaseInvoice4 blocked = dao.getPurchaseInvoiceDetails(invoiceId);
            blocked.setRequestCode(UUID.randomUUID().toString());
            blocked.getWarehouseStaff().setId(otherStaffId);
            assertThrows(IllegalArgumentException.class, () -> dao.savePurchaseInvoice(blocked));
            assertEquals(0, stockBeforePayment.compareTo(stock()));
        }
    }

    @Test void concurrentStaffRecordExactlyOnePaymentAndKeepTheFirstPayer() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2); CountDownLatch ready = new CountDownLatch(2), go = new CountDownLatch(1);
        try {
            List<Future<Integer>> jobs = new ArrayList<>();
            for (int staffId : List.of(2, otherStaffId)) jobs.add(pool.submit(() -> {
                ready.countDown(); assertTrue(go.await(5, TimeUnit.SECONDS));
                return dao.recordPayment(invoiceId, total, staffId) ? staffId : 0;
            }));
            assertTrue(ready.await(5, TimeUnit.SECONDS)); go.countDown();
            int first = jobs.get(0).get(10, TimeUnit.SECONDS), second = jobs.get(1).get(10, TimeUnit.SECONDS);
            assertTrue((first == 0) != (second == 0), "Chỉ một transaction được chốt thanh toán");
            assertEquals(first + second, ((Number)stored().get("paidByEmployee4id")).intValue());
            assertEquals(0, stockBeforePayment.compareTo(stock()));
        } finally { pool.shutdownNow(); }
    }

    @Test void databaseFailureRollsBackPaymentAndAllowsRetry() throws Exception {
        String trigger = "flow_payment_" + invoiceId;
        try (Connection c = open(); Statement st = c.createStatement()) {
            st.execute("CREATE TRIGGER " + trigger + " BEFORE UPDATE ON tblPurchaseInvoice4 FOR EACH ROW "
                    + "BEGIN IF NEW.id = " + invoiceId + " THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'test rollback'; END IF; END");
        }
        try {
            assertThrows(SQLException.class, () -> dao.recordPayment(invoiceId, total, otherStaffId));
            assertUnpaid();
        } finally {
            try (Connection c = open(); Statement st = c.createStatement()) { st.execute("DROP TRIGGER " + trigger); }
        }
        assertTrue(dao.recordPayment(invoiceId, total, otherStaffId));
        assertEquals(otherStaffId, ((Number)stored().get("paidByEmployee4id")).intValue());
    }
}
