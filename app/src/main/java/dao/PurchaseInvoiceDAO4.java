package dao;

import exception.ValidationException4;
import model.*;
import util.AppConstants4;
import util.Validation4;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PurchaseInvoiceDAO4 extends DAO {

    private final SparePartDAO4 sparePartDAO = new SparePartDAO4();

    public Integer findInvoiceIdByRequestCode(String requestCode, int warehouseStaffId) throws SQLException {
        if (requestCode == null || requestCode.trim().isEmpty() || warehouseStaffId <= 0) {
            return null;
        }

        String sql = "SELECT id FROM tblPurchaseInvoice4 WHERE requestCode = ? AND tblEmployee4id = ?";
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, requestCode.trim());
            ps.setInt(2, warehouseStaffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }

    public int savePurchaseInvoice(PurchaseInvoice4 purchaseInvoice) throws SQLException, ValidationException4 {
        if (purchaseInvoice == null) {
            throw new ValidationException4("purchaseInvoice", "Hóa đơn nhập không được null");
        }

        // 1. Kiểm tra id cũ trước nếu đã từng lưu mã này
        if (purchaseInvoice.getRequestCode() != null && purchaseInvoice.getWarehouseStaff() != null) {
            Integer existingId = findInvoiceIdByRequestCode(purchaseInvoice.getRequestCode(), purchaseInvoice.getWarehouseStaff().getId());
            if (existingId != null) {
                return existingId;
            }
        }

        // 2. Validate dữ liệu cơ bản
        Validation4.validatePurchaseInvoice(purchaseInvoice);

        // 3. Mở Connection và thực hiện Transaction với khóa thứ tự:
        // Supplier -> Employee -> SparePart (part id tăng dần)
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            int supplierId = purchaseInvoice.getSupplier().getId();
            int employeeId = purchaseInvoice.getWarehouseStaff().getId();

            // Khóa Nhà cung cấp
            String lockSupplierSql = "SELECT id, name, status FROM tblSupplier4 WHERE id = ? FOR UPDATE";
            String lockedSupplierName;
            try (PreparedStatement ps = connection.prepareStatement(lockSupplierSql)) {
                ps.setInt(1, supplierId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ValidationException4("supplier", "Nhà cung cấp không tồn tại");
                    }
                    if (!AppConstants4.SUPPLIER_STATUS_ACTIVE.equals(rs.getString("status"))) {
                        throw new ValidationException4("supplier", "Nhà cung cấp đã ngừng hoạt động");
                    }
                    lockedSupplierName = rs.getString("name");
                }
            }

            // Khóa Nhân viên
            String lockEmpSql = "SELECT m.id, m.fullName, m.accountStatus, e.position, e.employmentStatus " +
                                "FROM tblMember4 m " +
                                "JOIN tblEmployee4 e ON m.id = e.tblMember4id " +
                                "WHERE m.id = ? FOR UPDATE";
            String lockedEmployeeName;
            try (PreparedStatement ps = connection.prepareStatement(lockEmpSql)) {
                ps.setInt(1, employeeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ValidationException4("warehouseStaff", "Nhân viên không tồn tại");
                    }
                    if (!AppConstants4.ACCOUNT_STATUS_ACTIVE.equals(rs.getString("accountStatus")) ||
                        !AppConstants4.EMPLOYMENT_STATUS_ACTIVE.equals(rs.getString("employmentStatus"))) {
                        throw new ValidationException4("warehouseStaff", "Tài khoản nhân viên không hoạt động hoặc đã ngừng làm việc");
                    }
                    lockedEmployeeName = rs.getString("fullName");
                }
            }

            // Sắp xếp các dòng theo Part ID tăng dần để tránh Deadlock
            List<PurchaseInvoiceDetail4> sortedDetails = new ArrayList<>(purchaseInvoice.getInvoiceDetails());
            sortedDetails.sort(Comparator.comparingInt(d -> d.getSparePart().getId()));

            // Khóa các Phụ tùng theo thứ tự ID tăng dần
            String lockPartSql = "SELECT id, name, unit, status FROM tblSparePart4 WHERE id = ? FOR UPDATE";
            for (PurchaseInvoiceDetail4 detail : sortedDetails) {
                int pid = detail.getSparePart().getId();
                try (PreparedStatement ps = connection.prepareStatement(lockPartSql)) {
                    ps.setInt(1, pid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new ValidationException4("sparePart", "Phụ tùng ID " + pid + " không tồn tại");
                        }
                        String status = rs.getString("status");
                        if (AppConstants4.PART_STATUS_DISCONTINUED.equals(status)) {
                            throw new ValidationException4("sparePart", "Phụ tùng '" + rs.getString("name") + "' đã ngừng kinh doanh");
                        }
                        // Chốt thông tin áp dụng từ DB
                        detail.setSparePartNameSnapshot(rs.getString("name"));
                        detail.setUnitSnapshot(rs.getString("unit"));
                        Validation4.validateQuantity(detail.getQuantity(), detail.getUnitSnapshot());
                    }
                }
            }

            // Chốt thông tin Master áp dụng
            purchaseInvoice.setSupplierNameSnapshot(lockedSupplierName);
            purchaseInvoice.setWarehouseStaffNameSnapshot(lockedEmployeeName);

            // Gán thời điểm server thống nhất
            LocalDateTime now = LocalDateTime.now(AppConstants4.APP_ZONE);
            purchaseInvoice.setPurchaseDate(now);
            purchaseInvoice.setConfirmedAt(now);
            purchaseInvoice.setPaymentStatus(AppConstants4.INVOICE_STATUS_UNPAID);
            purchaseInvoice.setPaidAt(null);

            // INSERT Header
            int invoiceId = insertInvoice(connection, purchaseInvoice);
            purchaseInvoice.setId(invoiceId);

            // INSERT Chi tiết
            insertInvoiceDetails(connection, invoiceId, sortedDetails);

            // Tăng tồn kho cho từng phụ tùng
            for (PurchaseInvoiceDetail4 detail : sortedDetails) {
                sparePartDAO.increaseStockAfterPurchase(connection, detail.getSparePart().getId(), detail.getQuantity());
            }

            // Hoàn tất transaction
            connection.commit();
            return invoiceId;

        } catch (ValidationException4 e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ignored) { }
            }
            throw e;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {
                }
            }
            // Xử lý va chạm UNIQUE của requestCode (MySQL 1062)
            if (e.getErrorCode() == 1062 && purchaseInvoice.getRequestCode() != null) {
                Integer oldId = findInvoiceIdByRequestCode(purchaseInvoice.getRequestCode(), purchaseInvoice.getWarehouseStaff().getId());
                if (oldId != null) {
                    return oldId;
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public PurchaseInvoice4 getPurchaseInvoiceDetails(int invoiceId) throws SQLException {
        if (invoiceId <= 0) {
            return null;
        }

        String sql = "SELECT id, requestCode, purchaseDate, confirmedAt, paymentStatus, paidAt, " +
                     "       tblSupplier4id, tblEmployee4id, supplierNameSnapshot, warehouseStaffNameSnapshot " +
                     "FROM tblPurchaseInvoice4 WHERE id = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PurchaseInvoice4 invoice = mapInvoice(rs);
                    List<PurchaseInvoiceDetail4> details = getInvoiceDetails(connection, invoiceId);
                    invoice.setInvoiceDetails(details);
                    return invoice;
                }
            }
        }
        return null;
    }

    public List<PurchaseInvoice4> getUnpaidInvoices(int warehouseStaffId) throws SQLException {
        List<PurchaseInvoice4> list = new ArrayList<>();
        if (warehouseStaffId <= 0) {
            return list;
        }

        String sql = "SELECT id, requestCode, purchaseDate, confirmedAt, paymentStatus, paidAt, " +
                     "       tblSupplier4id, tblEmployee4id, supplierNameSnapshot, warehouseStaffNameSnapshot " +
                     "FROM tblPurchaseInvoice4 " +
                     "WHERE tblEmployee4id = ? AND paymentStatus = ? " +
                     "ORDER BY confirmedAt DESC";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, warehouseStaffId);
            ps.setString(2, AppConstants4.INVOICE_STATUS_UNPAID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseInvoice4 inv = mapInvoice(rs);
                    inv.setInvoiceDetails(getInvoiceDetails(connection, inv.getId()));
                    list.add(inv);
                }
            }
        }
        return list;
    }

    public boolean recordPayment(int invoiceId, BigDecimal amountPaid, int currentWarehouseStaffId)
            throws SQLException, ValidationException4 {
        if (invoiceId <= 0) {
            throw new ValidationException4("id", "Mã hóa đơn không hợp lệ");
        }
        if (amountPaid == null || amountPaid.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException4("amountPaid", "Số tiền thanh toán phải lớn hơn 0");
        }

        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            String lockSql = "SELECT id, paymentStatus, tblEmployee4id, paidAt " +
                             "FROM tblPurchaseInvoice4 WHERE id = ? FOR UPDATE";

            String currentStatus;
            int creatorId;
            try (PreparedStatement ps = connection.prepareStatement(lockSql)) {
                ps.setInt(1, invoiceId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ValidationException4("id", "Không tìm thấy hóa đơn nhập số: " + invoiceId);
                    }
                    currentStatus = rs.getString("paymentStatus");
                    creatorId = rs.getInt("tblEmployee4id");
                }
            }

            // Quyền: chỉ người lập hóa đơn mới được thanh toán
            if (creatorId != currentWarehouseStaffId) {
                throw new ValidationException4("permission", "Chỉ nhân viên đã lập hóa đơn mới có quyền thực hiện thanh toán");
            }

            // Nếu đã thanh toán: idempotent trả false, giữ nguyên thời điểm
            if (AppConstants4.INVOICE_STATUS_PAID.equals(currentStatus)) {
                connection.commit();
                return false;
            }

            // Tính tổng tiền từ chi tiết đã chốt
            List<PurchaseInvoiceDetail4> details = getInvoiceDetails(connection, invoiceId);
            BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            for (PurchaseInvoiceDetail4 d : details) {
                total = total.add(d.calculateLineAmount());
            }

            if (amountPaid.setScale(2, RoundingMode.HALF_UP).compareTo(total) != 0) {
                throw new ValidationException4("amountPaid", "Số tiền thanh toán (" + amountPaid +
                        " đ) không khớp với tổng tiền hóa đơn (" + total + " đ)");
            }

            // Cập nhật trạng thái thanh toán
            LocalDateTime now = LocalDateTime.now(AppConstants4.APP_ZONE);
            String updateSql = "UPDATE tblPurchaseInvoice4 SET paymentStatus = ?, paidAt = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setString(1, AppConstants4.INVOICE_STATUS_PAID);
                ps.setTimestamp(2, Timestamp.valueOf(now));
                ps.setInt(3, invoiceId);
                ps.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException | ValidationException4 e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private int insertInvoice(Connection connection, PurchaseInvoice4 purchaseInvoice) throws SQLException {
        String sql = "INSERT INTO tblPurchaseInvoice4 " +
                     " (requestCode, purchaseDate, confirmedAt, paymentStatus, paidAt, " +
                     "  tblSupplier4id, tblEmployee4id, supplierNameSnapshot, warehouseStaffNameSnapshot) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, purchaseInvoice.getRequestCode());
            ps.setTimestamp(2, Timestamp.valueOf(purchaseInvoice.getPurchaseDate()));
            ps.setTimestamp(3, Timestamp.valueOf(purchaseInvoice.getConfirmedAt()));
            ps.setString(4, purchaseInvoice.getPaymentStatus());
            ps.setNull(5, Types.TIMESTAMP);
            ps.setInt(6, purchaseInvoice.getSupplier().getId());
            ps.setInt(7, purchaseInvoice.getWarehouseStaff().getId());
            ps.setString(8, purchaseInvoice.getSupplierNameSnapshot());
            ps.setString(9, purchaseInvoice.getWarehouseStaffNameSnapshot());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Thêm hóa đơn thành công nhưng không lấy được ID tự sinh");
                }
            }
        }
    }

    private void insertInvoiceDetails(Connection connection, int invoiceId, List<PurchaseInvoiceDetail4> invoiceDetails)
            throws SQLException {
        String sql = "INSERT INTO tblPurchaseInvoiceDetail4 " +
                     " (quantity, purchaseUnitPrice, tblPurchaseInvoice4id, tblSparePart4id, sparePartNameSnapshot, unitSnapshot) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (PurchaseInvoiceDetail4 invoiceDetail : invoiceDetails) {
                ps.setBigDecimal(1, invoiceDetail.getQuantity());
                ps.setBigDecimal(2, invoiceDetail.getPurchaseUnitPrice());
                ps.setInt(3, invoiceId);
                ps.setInt(4, invoiceDetail.getSparePart().getId());
                ps.setString(5, invoiceDetail.getSparePartNameSnapshot());
                ps.setString(6, invoiceDetail.getUnitSnapshot());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<PurchaseInvoiceDetail4> getInvoiceDetails(Connection connection, int invoiceId) throws SQLException {
        List<PurchaseInvoiceDetail4> list = new ArrayList<>();
        String sql = "SELECT id, quantity, purchaseUnitPrice, tblSparePart4id, sparePartNameSnapshot, unitSnapshot " +
                     "FROM tblPurchaseInvoiceDetail4 " +
                     "WHERE tblPurchaseInvoice4id = ? " +
                     "ORDER BY id ASC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseInvoiceDetail4 invoiceDetail = new PurchaseInvoiceDetail4();
                    invoiceDetail.setId(rs.getInt("id"));
                    invoiceDetail.setQuantity(rs.getBigDecimal("quantity"));
                    invoiceDetail.setPurchaseUnitPrice(rs.getBigDecimal("purchaseUnitPrice"));
                    invoiceDetail.setSparePartNameSnapshot(rs.getString("sparePartNameSnapshot"));
                    invoiceDetail.setUnitSnapshot(rs.getString("unitSnapshot"));

                    SparePart4 sparePart = new SparePart4();
                    sparePart.setId(rs.getInt("tblSparePart4id"));
                    sparePart.setName(invoiceDetail.getSparePartNameSnapshot());
                    sparePart.setUnit(invoiceDetail.getUnitSnapshot());
                    invoiceDetail.setSparePart(sparePart);

                    list.add(invoiceDetail);
                }
            }
        }
        return list;
    }

    private PurchaseInvoice4 mapInvoice(ResultSet rs) throws SQLException {
        PurchaseInvoice4 inv = new PurchaseInvoice4();
        inv.setId(rs.getInt("id"));
        inv.setRequestCode(rs.getString("requestCode"));

        Timestamp nn = rs.getTimestamp("purchaseDate");
        if (nn != null) {
            inv.setPurchaseDate(nn.toLocalDateTime());
        }

        Timestamp xn = rs.getTimestamp("confirmedAt");
        if (xn != null) {
            inv.setConfirmedAt(xn.toLocalDateTime());
        }

        inv.setPaymentStatus(rs.getString("paymentStatus"));

        Timestamp tt = rs.getTimestamp("paidAt");
        if (tt != null) {
            inv.setPaidAt(tt.toLocalDateTime());
        }

        inv.setSupplierNameSnapshot(rs.getString("supplierNameSnapshot"));
        inv.setWarehouseStaffNameSnapshot(rs.getString("warehouseStaffNameSnapshot"));

        Supplier4 sup = new Supplier4();
        sup.setId(rs.getInt("tblSupplier4id"));
        sup.setName(inv.getSupplierNameSnapshot());
        inv.setSupplier(sup);

        WarehouseStaff4 ws = new WarehouseStaff4();
        ws.setId(rs.getInt("tblEmployee4id"));
        ws.setFullName(inv.getWarehouseStaffNameSnapshot());
        inv.setWarehouseStaff(ws);

        return inv;
    }
}
