package dao;

import model.*;

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

    public int savePurchaseInvoice(PurchaseInvoice4 purchaseInvoice) throws SQLException, IllegalArgumentException {
        if (purchaseInvoice == null) {
            throw new IllegalArgumentException("purchaseInvoice: Hóa đơn nhập không được null");
        }

        // 1. Kiểm tra id cũ trước nếu đã từng lưu mã này
        if (purchaseInvoice.getRequestCode() != null && purchaseInvoice.getWarehouseStaff() != null) {
            Integer existingId = findInvoiceIdByRequestCode(purchaseInvoice.getRequestCode(), purchaseInvoice.getWarehouseStaff().getId());
            if (existingId != null) {
                return existingId;
            }
        }

        // 2. Validate dữ liệu cơ bản
        {
            if (purchaseInvoice == null) {
                throw new IllegalArgumentException("purchaseInvoice: Hóa đơn nhập không được null");
            }
            if (purchaseInvoice.getSupplier() == null || purchaseInvoice.getSupplier().getId() <= 0) {
                throw new IllegalArgumentException("supplier: Hóa đơn phải gắn với nhà cung cấp hợp lệ");
            }
            if (purchaseInvoice.getWarehouseStaff() == null || purchaseInvoice.getWarehouseStaff().getId() <= 0) {
                throw new IllegalArgumentException("warehouseStaff: Hóa đơn phải có thông tin nhân viên kho lập hợp lệ");
            }
            if (purchaseInvoice.getInvoiceDetails() == null || purchaseInvoice.getInvoiceDetails().isEmpty()) {
                throw new IllegalArgumentException("invoiceDetail: Hóa đơn nhập phải có ít nhất một dòng phụ tùng");
            }

            java.util.Set<Integer> partIds = new java.util.HashSet<>();
            for (PurchaseInvoiceDetail4 invoiceDetail : purchaseInvoice.getInvoiceDetails()) {
                if (invoiceDetail.getSparePart() == null || invoiceDetail.getSparePart().getId() <= 0) {
                    throw new IllegalArgumentException("partId: Chi tiết dòng nhập thiếu thông tin phụ tùng");
                }
                int pid = invoiceDetail.getSparePart().getId();
                if (partIds.contains(pid)) {
                    throw new IllegalArgumentException("partId: Phụ tùng bị trùng lặp trong hóa đơn: " + pid);
                }
                partIds.add(pid);

                if (invoiceDetail.getQuantity() == null || invoiceDetail.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("quantity_" + pid + ": Số lượng nhập phải lớn hơn 0");
                }
                if (invoiceDetail.getPurchaseUnitPrice() == null || invoiceDetail.getPurchaseUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("purchaseUnitPrice_" + pid + ": Đơn giá nhập phải lớn hơn 0");
                }

                {
                    if (invoiceDetail.getQuantity() == null) {
                        throw new IllegalArgumentException("quantity: Số lượng không được để trống");
                    }
                    if (invoiceDetail.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("quantity: Số lượng phải lớn hơn 0");
                    }

                    if ("Cái".equals((invoiceDetail.getSparePart().getUnit())) || "Chiếc".equals((invoiceDetail.getSparePart().getUnit())) || "Bộ".equals((invoiceDetail.getSparePart().getUnit()))) {
                        if (invoiceDetail.getQuantity().stripTrailingZeros().scale() > 0) {
                            throw new IllegalArgumentException("quantity: Đơn vị " + (invoiceDetail.getSparePart().getUnit()) + " phải có số lượng nguyên");
                        }
                    } else if ("Lít".equals((invoiceDetail.getSparePart().getUnit()))) {
                        if (invoiceDetail.getQuantity().scale() > 3) {
                            throw new IllegalArgumentException("quantity: Đơn vị Lít chỉ cho phép tối đa 3 chữ số thập phân");
                        }
                    }
                }
            }
        }

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
                        throw new IllegalArgumentException("supplier: Nhà cung cấp không tồn tại");
                    }
                    if (!"Đang hoạt động".equals(rs.getString("status"))) {
                        throw new IllegalArgumentException("supplier: Nhà cung cấp đã ngừng hoạt động");
                    }
                    lockedSupplierName = rs.getString("name");
                }
            }

            // Khóa Nhân viên
            String lockEmpSql = "SELECT m.id, m.fullName, m.role, m.accountStatus, e.position, e.employmentStatus FROM tblMember4 m JOIN tblEmployee4 e ON m.id = e.tblMember4id WHERE m.id = ? FOR UPDATE";
            String lockedEmployeeName;
            try (PreparedStatement ps = connection.prepareStatement(lockEmpSql)) {
                ps.setInt(1, employeeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("warehouseStaff: Nhân viên không tồn tại");
                    }
                    if (!"WarehouseStaff".equals(rs.getString("role"))
                            || !"Hoạt động".equals(rs.getString("accountStatus"))
                            || !"Đang làm việc".equals(rs.getString("employmentStatus"))) {
                        throw new IllegalArgumentException("warehouseStaff: Tài khoản hiện tại không có quyền xác nhận nhập phụ tùng");
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
                            throw new IllegalArgumentException("sparePart: Phụ tùng ID " + pid + " không tồn tại");
                        }
                        String status = rs.getString("status");
                        if ("Ngừng kinh doanh".equals(status)) {
                            throw new IllegalArgumentException("sparePart: Phụ tùng '" + rs.getString("name") + "' đã ngừng kinh doanh");
                        }
                        // Chốt thông tin áp dụng từ DB
                        detail.setSparePartNameSnapshot(rs.getString("name"));
                        detail.setUnitSnapshot(rs.getString("unit"));
                        {
                            if (detail.getQuantity() == null) {
                                throw new IllegalArgumentException("quantity: Số lượng không được để trống");
                            }
                            if (detail.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                                throw new IllegalArgumentException("quantity: Số lượng phải lớn hơn 0");
                            }

                            if ("Cái".equals((detail.getUnitSnapshot())) || "Chiếc".equals((detail.getUnitSnapshot())) || "Bộ".equals((detail.getUnitSnapshot()))) {
                                if (detail.getQuantity().stripTrailingZeros().scale() > 0) {
                                    throw new IllegalArgumentException("quantity: Đơn vị " + (detail.getUnitSnapshot()) + " phải có số lượng nguyên");
                                }
                            } else if ("Lít".equals((detail.getUnitSnapshot()))) {
                                if (detail.getQuantity().scale() > 3) {
                                    throw new IllegalArgumentException("quantity: Đơn vị Lít chỉ cho phép tối đa 3 chữ số thập phân");
                                }
                            }
                        }
                    }
                }
            }

            // Chốt thông tin Master áp dụng
            purchaseInvoice.setSupplierNameSnapshot(lockedSupplierName);
            purchaseInvoice.setWarehouseStaffNameSnapshot(lockedEmployeeName);

            // Gán thời điểm server thống nhất
            LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
            purchaseInvoice.setPurchaseDate(now);
            purchaseInvoice.setConfirmedAt(now);
            purchaseInvoice.setPaymentStatus("Chưa thanh toán");
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

        } catch (IllegalArgumentException e) {
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

        String sql = "SELECT id, requestCode, purchaseDate, confirmedAt, paymentStatus, paidAt,        tblSupplier4id, tblEmployee4id, supplierNameSnapshot, warehouseStaffNameSnapshot FROM tblPurchaseInvoice4 WHERE id = ?";

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

        // Hóa đơn chưa trả là công việc chung của gara, không lọc theo người lập.
        // Tham số giữ nguyên, dùng để kiểm tra quyền của nhân viên kho đang xem.
        String sql = "SELECT id, requestCode, purchaseDate, confirmedAt, paymentStatus, paidAt, "
                + "tblSupplier4id, tblEmployee4id, supplierNameSnapshot, warehouseStaffNameSnapshot "
                + "FROM tblPurchaseInvoice4 WHERE paymentStatus = ? AND EXISTS ("
                + "SELECT 1 FROM tblMember4 m JOIN tblEmployee4 e ON e.tblMember4id = m.id "
                + "WHERE m.id = ? AND m.role = ? AND m.accountStatus = ? AND e.employmentStatus = ?) "
                + "ORDER BY confirmedAt DESC, id DESC";

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "Chưa thanh toán");
            ps.setInt(2, warehouseStaffId);
            ps.setString(3, "WarehouseStaff");
            ps.setString(4, "Hoạt động");
            ps.setString(5, "Đang làm việc");

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
    throws SQLException, IllegalArgumentException {
        if (invoiceId <= 0) {
            throw new IllegalArgumentException("id: Mã hóa đơn không hợp lệ");
        }
        if (amountPaid == null || amountPaid.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amountPaid: Số tiền thanh toán phải lớn hơn 0");
        }
        if (currentWarehouseStaffId <= 0) {
            throw new IllegalArgumentException("permission: Nhân viên kho hiện tại không hợp lệ");
        }

        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            // Lấy người thực sự trả tiền từ tài khoản trong CSDL, không từ dữ liệu form.
            // Kiểm tra và khóa nhân viên trước hóa đơn trong cùng transaction.
            String staffSql = "SELECT m.fullName, m.role, m.accountStatus, e.employmentStatus "
                    + "FROM tblMember4 m JOIN tblEmployee4 e ON e.tblMember4id = m.id "
                    + "WHERE m.id = ? FOR UPDATE";
            String paidByName;
            try (PreparedStatement ps = connection.prepareStatement(staffSql)) {
                ps.setInt(1, currentWarehouseStaffId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || !"WarehouseStaff".equals(rs.getString("role"))
                            || !"Hoạt động".equals(rs.getString("accountStatus"))
                            || !"Đang làm việc".equals(rs.getString("employmentStatus"))) {
                        throw new IllegalArgumentException("permission: Tài khoản hiện tại không có quyền ghi nhận thanh toán");
                    }
                    paidByName = rs.getString("fullName");
                }
            }

            String lockSql = "SELECT paymentStatus FROM tblPurchaseInvoice4 WHERE id = ? FOR UPDATE";

            String currentStatus;
            try (PreparedStatement ps = connection.prepareStatement(lockSql)) {
                ps.setInt(1, invoiceId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("id: Không tìm thấy hóa đơn nhập số: " + invoiceId);
                    }
                    currentStatus = rs.getString("paymentStatus");
                }
            }

            // Nếu đã thanh toán: idempotent trả false, giữ nguyên thời điểm
            if ("Đã thanh toán".equals(currentStatus)) {
                connection.commit();
                return false;
            }

            // Tính tổng tiền từ chi tiết đã chốt
            List<PurchaseInvoiceDetail4> details = getInvoiceDetails(connection, invoiceId);
            BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            for (PurchaseInvoiceDetail4 d : details) {
                total = total.add(d.calculateLineAmount());
            }

            if (amountPaid.compareTo(total) != 0) {
                throw new IllegalArgumentException("amountPaid: Số tiền thanh toán (" + amountPaid +
                " đ) không khớp với tổng tiền hóa đơn (" + total + " đ)");
            }

            // Cập nhật trạng thái thanh toán
            LocalDateTime now = LocalDateTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).withNano(0);
            String updateSql = "UPDATE tblPurchaseInvoice4 SET paymentStatus = ?, paidAt = ?, "
                    + "paidByEmployee4id = ?, paidByNameSnapshot = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setString(1, "Đã thanh toán");
                ps.setTimestamp(2, Timestamp.valueOf(now));
                ps.setInt(3, currentWarehouseStaffId);
                ps.setString(4, paidByName);
                ps.setInt(5, invoiceId);
                ps.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException | IllegalArgumentException e) {
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
        String sql = "INSERT INTO tblPurchaseInvoice4  (requestCode, purchaseDate, confirmedAt, paymentStatus, paidAt,   tblSupplier4id, tblEmployee4id, supplierNameSnapshot, warehouseStaffNameSnapshot) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
        String sql = "INSERT INTO tblPurchaseInvoiceDetail4  (quantity, purchaseUnitPrice, tblPurchaseInvoice4id, tblSparePart4id, sparePartNameSnapshot, unitSnapshot) VALUES (?, ?, ?, ?, ?, ?)";

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
        String sql = "SELECT id, quantity, purchaseUnitPrice, tblSparePart4id, sparePartNameSnapshot, unitSnapshot FROM tblPurchaseInvoiceDetail4 WHERE tblPurchaseInvoice4id = ? ORDER BY id ASC";

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
