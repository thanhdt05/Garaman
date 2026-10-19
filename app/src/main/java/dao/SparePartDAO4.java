package dao;

import exception.ValidationException4;
import model.SparePart4;
import model.SparePartCategory4;
import util.AppConstants4;
import util.Validation4;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SparePartDAO4 extends DAO {

    public List<SparePart4> timPhuTungTheoTen(String tuKhoa, boolean chiCongBo) throws SQLException {
        List<SparePart4> list = new ArrayList<>();
        String sql;
        if (chiCongBo) {
            sql = "SELECT p.id, p.ten, p.tblSparePartCategory4id, p.moTa, p.hinhAnh, p.donVi, " +
                  "       p.hangSanXuat, p.thongSo, p.dongXePhuHop, p.giaBan, p.hanBaoHanh, " +
                  "       p.dieuKienBaoHanh, p.trangThai, p.tonKho, " +
                  "       c.ten AS tenNhom, c.moTa AS moTaNhom, c.trangThai AS trangThaiNhom " +
                  "FROM tblSparePart4 p " +
                  "JOIN tblSparePartCategory4 c ON p.tblSparePartCategory4id = c.id " +
                  "WHERE p.trangThai = ? AND p.ten LIKE ? " +
                  "ORDER BY p.ten ASC";
        } else {
            sql = "SELECT p.id, p.ten, p.tblSparePartCategory4id, p.moTa, p.hinhAnh, p.donVi, " +
                  "       p.hangSanXuat, p.thongSo, p.dongXePhuHop, p.giaBan, p.hanBaoHanh, " +
                  "       p.dieuKienBaoHanh, p.trangThai, p.tonKho, " +
                  "       c.ten AS tenNhom, c.moTa AS moTaNhom, c.trangThai AS trangThaiNhom " +
                  "FROM tblSparePart4 p " +
                  "JOIN tblSparePartCategory4 c ON p.tblSparePartCategory4id = c.id " +
                  "WHERE p.trangThai IN (?, ?) AND p.ten LIKE ? " +
                  "ORDER BY p.ten ASC";
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (chiCongBo) {
                ps.setString(1, AppConstants4.TRANG_THAI_PT_DANG_KINH_DOANH);
                ps.setString(2, taoMauTimKiem(tuKhoa));
            } else {
                ps.setString(1, AppConstants4.TRANG_THAI_PT_CHUA_CONG_BO);
                ps.setString(2, AppConstants4.TRANG_THAI_PT_DANG_KINH_DOANH);
                ps.setString(3, taoMauTimKiem(tuKhoa));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSparePart(rs));
                }
            }
        }
        return list;
    }

    public SparePart4 layChiTietPhuTung(int id, boolean chiCongBo) throws SQLException {
        if (id <= 0) {
            return null;
        }

        String sql;
        if (chiCongBo) {
            sql = "SELECT p.id, p.ten, p.tblSparePartCategory4id, p.moTa, p.hinhAnh, p.donVi, " +
                  "       p.hangSanXuat, p.thongSo, p.dongXePhuHop, p.giaBan, p.hanBaoHanh, " +
                  "       p.dieuKienBaoHanh, p.trangThai, p.tonKho, " +
                  "       c.ten AS tenNhom, c.moTa AS moTaNhom, c.trangThai AS trangThaiNhom " +
                  "FROM tblSparePart4 p " +
                  "JOIN tblSparePartCategory4 c ON p.tblSparePartCategory4id = c.id " +
                  "WHERE p.id = ? AND p.trangThai = ?";
        } else {
            sql = "SELECT p.id, p.ten, p.tblSparePartCategory4id, p.moTa, p.hinhAnh, p.donVi, " +
                  "       p.hangSanXuat, p.thongSo, p.dongXePhuHop, p.giaBan, p.hanBaoHanh, " +
                  "       p.dieuKienBaoHanh, p.trangThai, p.tonKho, " +
                  "       c.ten AS tenNhom, c.moTa AS moTaNhom, c.trangThai AS trangThaiNhom " +
                  "FROM tblSparePart4 p " +
                  "JOIN tblSparePartCategory4 c ON p.tblSparePartCategory4id = c.id " +
                  "WHERE p.id = ? AND p.trangThai IN (?, ?)";
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (chiCongBo) {
                ps.setString(2, AppConstants4.TRANG_THAI_PT_DANG_KINH_DOANH);
            } else {
                ps.setString(2, AppConstants4.TRANG_THAI_PT_CHUA_CONG_BO);
                ps.setString(3, AppConstants4.TRANG_THAI_PT_DANG_KINH_DOANH);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSparePart(rs);
                }
            }
        }
        return null;
    }

    public BigDecimal layTonKho(int id) throws SQLException {
        if (id <= 0) {
            return BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
        }

        String sql = "SELECT tonKho FROM tblSparePart4 WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal ton = rs.getBigDecimal("tonKho");
                    return ton != null ? ton.setScale(3, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
                }
            }
        }
        return BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
    }

    /**
     * Tăng số lượng tồn kho của phụ tùng trong phạm vi transaction của hóa đơn nhập.
     * Phương thức có phạm vi package-private, nhận connection ngoài và KHÔNG tự commit hay đóng.
     */
    void tangTonSauNhap(Connection con, int idPhuTung, BigDecimal soLuong)
            throws SQLException, ValidationException4 {
        if (soLuong == null || soLuong.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException4("soLuong", "Số lượng cộng tồn phải lớn hơn 0");
        }

        String sql = "UPDATE tblSparePart4 SET tonKho = tonKho + ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, soLuong);
            ps.setInt(2, idPhuTung);
            int rows = ps.executeUpdate();
            if (rows != 1) {
                throw new SQLException("Không thể cập nhật tồn kho cho phụ tùng ID: " + idPhuTung);
            }
        }
    }

    public int themPhuTung(SparePart4 part) throws SQLException, ValidationException4 {
        Validation4.kiemTraPhuTung(part);

        String sql = "INSERT INTO tblSparePart4 " +
                     " (ten, tblSparePartCategory4id, moTa, hinhAnh, donVi, hangSanXuat, " +
                     "  thongSo, dongXePhuHop, giaBan, hanBaoHanh, dieuKienBaoHanh, trangThai, tonKho) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, part.getTen().trim());
            ps.setInt(2, part.getNhom().getId());
            ps.setString(3, part.getMoTa());
            ps.setString(4, part.getHinhAnh());
            ps.setString(5, part.getDonVi());
            ps.setString(6, part.getHangSanXuat().trim());
            ps.setString(7, part.getThongSo());
            ps.setString(8, part.getDongXePhuHop());

            if (part.getGiaBan() != null) {
                ps.setBigDecimal(9, part.getGiaBan());
            } else {
                ps.setNull(9, java.sql.Types.DECIMAL);
            }

            ps.setString(10, part.getHanBaoHanh());
            ps.setString(11, part.getDieuKienBaoHanh());
            ps.setString(12, part.getTrangThai());
            ps.setBigDecimal(13, BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP));

            int affected = ps.executeUpdate();
            if (affected != 1) {
                throw new SQLException("Thêm phụ tùng thất bại");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    part.setId(id);
                    return id;
                } else {
                    throw new SQLException("Thêm phụ tùng thành công nhưng không lấy được ID tự sinh");
                }
            }
        }
    }

    private SparePart4 mapSparePart(ResultSet rs) throws SQLException {
        SparePart4 p = new SparePart4();
        p.setId(rs.getInt("id"));
        p.setTen(rs.getString("ten"));
        p.setMoTa(rs.getString("moTa"));
        p.setHinhAnh(rs.getString("hinhAnh"));
        p.setDonVi(rs.getString("donVi"));
        p.setHangSanXuat(rs.getString("hangSanXuat"));
        p.setThongSo(rs.getString("thongSo"));
        p.setDongXePhuHop(rs.getString("dongXePhuHop"));

        BigDecimal giaBan = rs.getBigDecimal("giaBan");
        p.setGiaBan(giaBan);

        p.setHanBaoHanh(rs.getString("hanBaoHanh"));
        p.setDieuKienBaoHanh(rs.getString("dieuKienBaoHanh"));
        p.setTrangThai(rs.getString("trangThai"));

        BigDecimal tonKho = rs.getBigDecimal("tonKho");
        p.setTonKho(tonKho != null ? tonKho.setScale(3, RoundingMode.HALF_UP) : BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP));

        SparePartCategory4 cat = new SparePartCategory4();
        cat.setId(rs.getInt("tblSparePartCategory4id"));
        cat.setTen(rs.getString("tenNhom"));
        cat.setMoTa(rs.getString("moTaNhom"));
        cat.setTrangThai(rs.getString("trangThaiNhom"));
        p.setNhom(cat);

        return p;
    }
}
