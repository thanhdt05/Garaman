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

    public Integer timIdTheoMaYeuCau(String maYeuCau, int idNhanVienKho) throws SQLException {
        if (maYeuCau == null || maYeuCau.trim().isEmpty() || idNhanVienKho <= 0) {
            return null;
        }

        String sql = "SELECT id FROM tblPurchaseInvoice4 WHERE maYeuCau = ? AND tblEmployee4id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maYeuCau.trim());
            ps.setInt(2, idNhanVienKho);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }

    public int luuHoaDonNhap(PurchaseInvoice4 hoaDon) throws SQLException, ValidationException4 {
        if (hoaDon == null) {
            throw new ValidationException4("hoaDon", "Hóa đơn nhập không được null");
        }

        // 1. Kiểm tra id cũ trước nếu đã từng lưu mã này
        if (hoaDon.getMaYeuCau() != null && hoaDon.getNhanVienKho() != null) {
            Integer existingId = timIdTheoMaYeuCau(hoaDon.getMaYeuCau(), hoaDon.getNhanVienKho().getId());
            if (existingId != null) {
                return existingId;
            }
        }

        // 2. Validate dữ liệu cơ bản
        Validation4.kiemTraHoaDonNhap(hoaDon);

        // 3. Mở Connection và thực hiện Transaction với khóa thứ tự:
        // Supplier -> Employee -> SparePart (part id tăng dần)
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            int supplierId = hoaDon.getNhaCungCap().getId();
            int employeeId = hoaDon.getNhanVienKho().getId();

            // Khóa Nhà cung cấp
            String lockSupplierSql = "SELECT id, ten, trangThai FROM tblSupplier4 WHERE id = ? FOR UPDATE";
            String lockedSupplierName;
            try (PreparedStatement ps = con.prepareStatement(lockSupplierSql)) {
                ps.setInt(1, supplierId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ValidationException4("nhaCungCap", "Nhà cung cấp không tồn tại");
                    }
                    if (!AppConstants4.TRANG_THAI_NCC_DANG_HOAT_DONG.equals(rs.getString("trangThai"))) {
                        throw new ValidationException4("nhaCungCap", "Nhà cung cấp đã ngừng hoạt động");
                    }
                    lockedSupplierName = rs.getString("ten");
                }
            }

            // Khóa Nhân viên
            String lockEmpSql = "SELECT m.id, m.hoTen, m.trangThaiTaiKhoan, e.viTri, e.trangThaiLamViec " +
                                "FROM tblMember4 m " +
                                "JOIN tblEmployee4 e ON m.id = e.tblMember4id " +
                                "WHERE m.id = ? FOR UPDATE";
            String lockedEmployeeName;
            try (PreparedStatement ps = con.prepareStatement(lockEmpSql)) {
                ps.setInt(1, employeeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ValidationException4("nhanVienKho", "Nhân viên không tồn tại");
                    }
                    if (!AppConstants4.TRANG_THAI_TK_HOAT_DONG.equals(rs.getString("trangThaiTaiKhoan")) ||
                        !AppConstants4.TRANG_THAI_LV_DANG_LAM.equals(rs.getString("trangThaiLamViec"))) {
                        throw new ValidationException4("nhanVienKho", "Tài khoản nhân viên không hoạt động hoặc đã ngừng làm việc");
                    }
                    lockedEmployeeName = rs.getString("hoTen");
                }
            }

            // Sắp xếp các dòng theo Part ID tăng dần để tránh Deadlock
            List<PurchaseInvoiceDetail4> sortedDetails = new ArrayList<>(hoaDon.getDsChiTietNhap());
            sortedDetails.sort(Comparator.comparingInt(d -> d.getPhuTung().getId()));

            // Khóa các Phụ tùng theo thứ tự ID tăng dần
            String lockPartSql = "SELECT id, ten, donVi, trangThai FROM tblSparePart4 WHERE id = ? FOR UPDATE";
            for (PurchaseInvoiceDetail4 detail : sortedDetails) {
                int pid = detail.getPhuTung().getId();
                try (PreparedStatement ps = con.prepareStatement(lockPartSql)) {
                    ps.setInt(1, pid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new ValidationException4("phuTung", "Phụ tùng ID " + pid + " không tồn tại");
                        }
                        String trangThai = rs.getString("trangThai");
                        if (AppConstants4.TRANG_THAI_PT_NGUNG_KINH_DOANH.equals(trangThai)) {
                            throw new ValidationException4("phuTung", "Phụ tùng '" + rs.getString("ten") + "' đã ngừng kinh doanh");
                        }
                        // Chốt thông tin áp dụng từ DB
                        detail.setTenPhuTungApDung(rs.getString("ten"));
                        detail.setDonViApDung(rs.getString("donVi"));
                    }
                }
            }

            // Chốt thông tin Master áp dụng
            hoaDon.setNhaCungCapTenApDung(lockedSupplierName);
            hoaDon.setNhanVienTenApDung(lockedEmployeeName);

            // Gán thời điểm server thống nhất
            LocalDateTime now = LocalDateTime.now(AppConstants4.APP_ZONE);
            hoaDon.setNgayNhap(now);
            hoaDon.setThoiDiemXacNhan(now);
            hoaDon.setTinhTrang(AppConstants4.TINH_TRANG_HD_CHUA_THANH_TOAN);
            hoaDon.setThoiDiemThanhToan(null);

            // INSERT Header
            int invoiceId = chenHoaDon(con, hoaDon);
            hoaDon.setId(invoiceId);

            // INSERT Chi tiết
            chenChiTiet(con, invoiceId, sortedDetails);

            // Tăng tồn kho cho từng phụ tùng
            for (PurchaseInvoiceDetail4 detail : sortedDetails) {
                sparePartDAO.tangTonSauNhap(con, detail.getPhuTung().getId(), detail.getSoLuong());
            }

            // Hoàn tất transaction
            con.commit();
            return invoiceId;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {
                }
            }
            // Xử lý va chạm UNIQUE của maYeuCau (MySQL 1062)
            if (e.getErrorCode() == 1062 && hoaDon.getMaYeuCau() != null) {
                Integer oldId = timIdTheoMaYeuCau(hoaDon.getMaYeuCau(), hoaDon.getNhanVienKho().getId());
                if (oldId != null) {
                    return oldId;
                }
            }
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    public PurchaseInvoice4 layThongTinHoaDonNhap(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT id, maYeuCau, ngayNhap, thoiDiemXacNhan, tinhTrang, thoiDiemThanhToan, " +
                     "       tblSupplier4id, tblEmployee4id, nhaCungCapTenApDung, nhanVienTenApDung " +
                     "FROM tblPurchaseInvoice4 WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PurchaseInvoice4 invoice = mapInvoice(rs);
                    List<PurchaseInvoiceDetail4> details = layChiTiet(con, id);
                    invoice.setDsChiTietNhap(details);
                    return invoice;
                }
            }
        }
        return null;
    }

    public List<PurchaseInvoice4> layHoaDonChuaThanhToan(int idNhanVienKho) throws SQLException {
        List<PurchaseInvoice4> list = new ArrayList<>();
        if (idNhanVienKho <= 0) {
            return list;
        }

        String sql = "SELECT id, maYeuCau, ngayNhap, thoiDiemXacNhan, tinhTrang, thoiDiemThanhToan, " +
                     "       tblSupplier4id, tblEmployee4id, nhaCungCapTenApDung, nhanVienTenApDung " +
                     "FROM tblPurchaseInvoice4 " +
                     "WHERE tblEmployee4id = ? AND tinhTrang = ? " +
                     "ORDER BY thoiDiemXacNhan DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idNhanVienKho);
            ps.setString(2, AppConstants4.TINH_TRANG_HD_CHUA_THANH_TOAN);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseInvoice4 inv = mapInvoice(rs);
                    inv.setDsChiTietNhap(layChiTiet(con, inv.getId()));
                    list.add(inv);
                }
            }
        }
        return list;
    }

    public boolean ghiNhanThanhToan(int idHoaDon, BigDecimal soTienDaTra, int idNhanVienHienTai)
            throws SQLException, ValidationException4 {
        if (idHoaDon <= 0) {
            throw new ValidationException4("id", "Mã hóa đơn không hợp lệ");
        }
        if (soTienDaTra == null || soTienDaTra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException4("soTienDaTra", "Số tiền thanh toán phải lớn hơn 0");
        }

        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            String lockSql = "SELECT id, tinhTrang, tblEmployee4id, thoiDiemThanhToan " +
                             "FROM tblPurchaseInvoice4 WHERE id = ? FOR UPDATE";

            String currentStatus;
            int creatorId;
            try (PreparedStatement ps = con.prepareStatement(lockSql)) {
                ps.setInt(1, idHoaDon);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ValidationException4("id", "Không tìm thấy hóa đơn nhập số: " + idHoaDon);
                    }
                    currentStatus = rs.getString("tinhTrang");
                    creatorId = rs.getInt("tblEmployee4id");
                }
            }

            // Quyền: chỉ người lập hóa đơn mới được thanh toán
            if (creatorId != idNhanVienHienTai) {
                throw new ValidationException4("quyen", "Chỉ nhân viên đã lập hóa đơn mới có quyền thực hiện thanh toán");
            }

            // Nếu đã thanh toán: idempotent trả false, giữ nguyên thời điểm
            if (AppConstants4.TINH_TRANG_HD_DA_THANH_TOAN.equals(currentStatus)) {
                con.commit();
                return false;
            }

            // Tính tổng tiền từ chi tiết đã chốt
            List<PurchaseInvoiceDetail4> details = layChiTiet(con, idHoaDon);
            BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            for (PurchaseInvoiceDetail4 d : details) {
                total = total.add(d.tinhThanhTien());
            }

            if (soTienDaTra.setScale(2, RoundingMode.HALF_UP).compareTo(total) != 0) {
                throw new ValidationException4("soTienDaTra", "Số tiền thanh toán (" + soTienDaTra +
                        " đ) không khớp với tổng tiền hóa đơn (" + total + " đ)");
            }

            // Cập nhật trạng thái thanh toán
            LocalDateTime now = LocalDateTime.now(AppConstants4.APP_ZONE);
            String updateSql = "UPDATE tblPurchaseInvoice4 SET tinhTrang = ?, thoiDiemThanhToan = ? WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                ps.setString(1, AppConstants4.TINH_TRANG_HD_DA_THANH_TOAN);
                ps.setTimestamp(2, Timestamp.valueOf(now));
                ps.setInt(3, idHoaDon);
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException | ValidationException4 e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {
                }
            }
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private int chenHoaDon(Connection con, PurchaseInvoice4 hoaDon) throws SQLException {
        String sql = "INSERT INTO tblPurchaseInvoice4 " +
                     " (maYeuCau, ngayNhap, thoiDiemXacNhan, tinhTrang, thoiDiemThanhToan, " +
                     "  tblSupplier4id, tblEmployee4id, nhaCungCapTenApDung, nhanVienTenApDung) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, hoaDon.getMaYeuCau());
            ps.setTimestamp(2, Timestamp.valueOf(hoaDon.getNgayNhap()));
            ps.setTimestamp(3, Timestamp.valueOf(hoaDon.getThoiDiemXacNhan()));
            ps.setString(4, hoaDon.getTinhTrang());
            ps.setNull(5, Types.TIMESTAMP);
            ps.setInt(6, hoaDon.getNhaCungCap().getId());
            ps.setInt(7, hoaDon.getNhanVienKho().getId());
            ps.setString(8, hoaDon.getNhaCungCapTenApDung());
            ps.setString(9, hoaDon.getNhanVienTenApDung());

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

    private void chenChiTiet(Connection con, int idHoaDon, List<PurchaseInvoiceDetail4> chiTiet)
            throws SQLException {
        String sql = "INSERT INTO tblPurchaseInvoiceDetail4 " +
                     " (soLuong, donGiaNhap, tblPurchaseInvoice4id, tblSparePart4id, tenPhuTungApDung, donViApDung) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (PurchaseInvoiceDetail4 ct : chiTiet) {
                ps.setBigDecimal(1, ct.getSoLuong());
                ps.setBigDecimal(2, ct.getDonGiaNhap());
                ps.setInt(3, idHoaDon);
                ps.setInt(4, ct.getPhuTung().getId());
                ps.setString(5, ct.getTenPhuTungApDung());
                ps.setString(6, ct.getDonViApDung());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<PurchaseInvoiceDetail4> layChiTiet(Connection con, int idHoaDon) throws SQLException {
        List<PurchaseInvoiceDetail4> list = new ArrayList<>();
        String sql = "SELECT id, soLuong, donGiaNhap, tblSparePart4id, tenPhuTungApDung, donViApDung " +
                     "FROM tblPurchaseInvoiceDetail4 " +
                     "WHERE tblPurchaseInvoice4id = ? " +
                     "ORDER BY id ASC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idHoaDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseInvoiceDetail4 ct = new PurchaseInvoiceDetail4();
                    ct.setId(rs.getInt("id"));
                    ct.setSoLuong(rs.getBigDecimal("soLuong"));
                    ct.setDonGiaNhap(rs.getBigDecimal("donGiaNhap"));
                    ct.setTenPhuTungApDung(rs.getString("tenPhuTungApDung"));
                    ct.setDonViApDung(rs.getString("donViApDung"));

                    SparePart4 pt = new SparePart4();
                    pt.setId(rs.getInt("tblSparePart4id"));
                    pt.setTen(ct.getTenPhuTungApDung());
                    pt.setDonVi(ct.getDonViApDung());
                    ct.setPhuTung(pt);

                    list.add(ct);
                }
            }
        }
        return list;
    }

    private PurchaseInvoice4 mapInvoice(ResultSet rs) throws SQLException {
        PurchaseInvoice4 inv = new PurchaseInvoice4();
        inv.setId(rs.getInt("id"));
        inv.setMaYeuCau(rs.getString("maYeuCau"));

        Timestamp nn = rs.getTimestamp("ngayNhap");
        if (nn != null) {
            inv.setNgayNhap(nn.toLocalDateTime());
        }

        Timestamp xn = rs.getTimestamp("thoiDiemXacNhan");
        if (xn != null) {
            inv.setThoiDiemXacNhan(xn.toLocalDateTime());
        }

        inv.setTinhTrang(rs.getString("tinhTrang"));

        Timestamp tt = rs.getTimestamp("thoiDiemThanhToan");
        if (tt != null) {
            inv.setThoiDiemThanhToan(tt.toLocalDateTime());
        }

        inv.setNhaCungCapTenApDung(rs.getString("nhaCungCapTenApDung"));
        inv.setNhanVienTenApDung(rs.getString("nhanVienTenApDung"));

        Supplier4 sup = new Supplier4();
        sup.setId(rs.getInt("tblSupplier4id"));
        sup.setTen(inv.getNhaCungCapTenApDung());
        inv.setNhaCungCap(sup);

        WarehouseStaff4 ws = new WarehouseStaff4();
        ws.setId(rs.getInt("tblEmployee4id"));
        ws.setHoTen(inv.getNhanVienTenApDung());
        inv.setNhanVienKho(ws);

        return inv;
    }
}
