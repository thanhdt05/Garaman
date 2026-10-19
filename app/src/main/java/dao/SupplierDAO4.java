package dao;

import exception.ValidationException4;
import model.Supplier4;
import util.AppConstants4;
import util.Validation4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO4 extends DAO {

    public List<Supplier4> timNhaCungCapTheoTen(String tuKhoa) throws SQLException {
        List<Supplier4> list = new ArrayList<>();
        String sql = "SELECT id, ten, diaChi, dienThoai, email, trangThai " +
                     "FROM tblSupplier4 " +
                     "WHERE trangThai = ? AND ten LIKE ? " +
                     "ORDER BY ten ASC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, AppConstants4.TRANG_THAI_NCC_DANG_HOAT_DONG);
            ps.setString(2, taoMauTimKiem(tuKhoa));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSupplier(rs));
                }
            }
        }
        return list;
    }

    public Supplier4 layThongTinNhaCungCap(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT id, ten, diaChi, dienThoai, email, trangThai " +
                     "FROM tblSupplier4 " +
                     "WHERE id = ? AND trangThai = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, AppConstants4.TRANG_THAI_NCC_DANG_HOAT_DONG);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSupplier(rs);
                }
            }
        }
        return null;
    }

    public int themNhaCungCap(Supplier4 supplier) throws SQLException, ValidationException4 {
        Validation4.kiemTraNhaCungCap(supplier);

        String sql = "INSERT INTO tblSupplier4 (ten, diaChi, dienThoai, email, trangThai) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, supplier.getTen().trim());
            ps.setString(2, supplier.getDiaChi().trim());
            ps.setString(3, supplier.getDienThoai().trim());

            if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
                ps.setString(4, supplier.getEmail().trim());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }

            ps.setString(5, AppConstants4.TRANG_THAI_NCC_DANG_HOAT_DONG);

            int affected = ps.executeUpdate();
            if (affected != 1) {
                throw new SQLException("Thêm nhà cung cấp thất bại");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    supplier.setId(id);
                    return id;
                } else {
                    throw new SQLException("Thêm nhà cung cấp thành công nhưng không lấy được ID");
                }
            }
        }
    }

    private Supplier4 mapSupplier(ResultSet rs) throws SQLException {
        Supplier4 s = new Supplier4();
        s.setId(rs.getInt("id"));
        s.setTen(rs.getString("ten"));
        s.setDiaChi(rs.getString("diaChi"));
        s.setDienThoai(rs.getString("dienThoai"));
        s.setEmail(rs.getString("email"));
        s.setTrangThai(rs.getString("trangThai"));
        return s;
    }
}
