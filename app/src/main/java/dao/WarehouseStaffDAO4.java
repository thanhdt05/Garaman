package dao;

import model.WarehouseStaff4;
import util.AppConstants4;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WarehouseStaffDAO4 extends DAO {

    public WarehouseStaff4 layThongTinNhanVienKho(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT m.id, m.hoTen, m.tenDangNhap, m.matKhau, m.ngaySinh, m.diaChi, " +
                     "       m.email, m.dienThoai, m.vaiTro, m.trangThaiTaiKhoan, " +
                     "       e.viTri, e.trangThaiLamViec " +
                     "FROM tblMember4 m " +
                     "JOIN tblEmployee4 e ON m.id = e.tblMember4id " +
                     "WHERE m.id = ? " +
                     "  AND m.vaiTro = ? " +
                     "  AND m.trangThaiTaiKhoan = ? " +
                     "  AND e.trangThaiLamViec = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, AppConstants4.ROLE_WAREHOUSE_STAFF);
            ps.setString(3, AppConstants4.TRANG_THAI_TK_HOAT_DONG);
            ps.setString(4, AppConstants4.TRANG_THAI_LV_DANG_LAM);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    WarehouseStaff4 ws = new WarehouseStaff4();
                    ws.setId(rs.getInt("id"));
                    ws.setHoTen(rs.getString("hoTen"));
                    ws.setTenDangNhap(rs.getString("tenDangNhap"));
                    ws.setMatKhau(rs.getString("matKhau"));

                    Date ns = rs.getDate("ngaySinh");
                    if (ns != null) {
                        ws.setNgaySinh(ns.toLocalDate());
                    }

                    ws.setDiaChi(rs.getString("diaChi"));
                    ws.setEmail(rs.getString("email"));
                    ws.setDienThoai(rs.getString("dienThoai"));
                    ws.setVaiTro(rs.getString("vaiTro"));
                    ws.setTrangThaiTaiKhoan(rs.getString("trangThaiTaiKhoan"));
                    ws.setViTri(rs.getString("viTri"));
                    ws.setTrangThaiLamViec(rs.getString("trangThaiLamViec"));
                    return ws;
                }
            }
        }
        return null;
    }
}
