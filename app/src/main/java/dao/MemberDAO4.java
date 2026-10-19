package dao;

import model.*;
import util.AppConstants4;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDAO4 extends DAO {

    public Member4 timTheoTenDangNhap(String tenDangNhap) throws SQLException {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT m.id, m.hoTen, m.tenDangNhap, m.matKhau, m.ngaySinh, m.diaChi, " +
                     "       m.email, m.dienThoai, m.vaiTro, m.trangThaiTaiKhoan, " +
                     "       e.viTri, e.trangThaiLamViec " +
                     "FROM tblMember4 m " +
                     "LEFT JOIN tblEmployee4 e ON m.id = e.tblMember4id " +
                     "WHERE m.tenDangNhap = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMember(rs);
                }
            }
        }
        return null;
    }

    public Member4 layThanhVienDangHoatDong(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT m.id, m.hoTen, m.tenDangNhap, m.matKhau, m.ngaySinh, m.diaChi, " +
                     "       m.email, m.dienThoai, m.vaiTro, m.trangThaiTaiKhoan, " +
                     "       e.viTri, e.trangThaiLamViec " +
                     "FROM tblMember4 m " +
                     "LEFT JOIN tblEmployee4 e ON m.id = e.tblMember4id " +
                     "WHERE m.id = ? AND m.trangThaiTaiKhoan = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, AppConstants4.TRANG_THAI_TK_HOAT_DONG);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Member4 member = mapMember(rs);
                    if (member instanceof Employee4) {
                        Employee4 emp = (Employee4) member;
                        if (!AppConstants4.TRANG_THAI_LV_DANG_LAM.equals(emp.getTrangThaiLamViec())) {
                            return null;
                        }
                    }
                    return member;
                }
            }
        }
        return null;
    }

    private Member4 mapMember(ResultSet rs) throws SQLException {
        String vaiTro = rs.getString("vaiTro");
        Member4 m;

        if (AppConstants4.ROLE_CUSTOMER.equalsIgnoreCase(vaiTro)) {
            m = new Customer4();
        } else if (AppConstants4.ROLE_WAREHOUSE_STAFF.equalsIgnoreCase(vaiTro)) {
            WarehouseStaff4 ws = new WarehouseStaff4();
            ws.setViTri(rs.getString("viTri"));
            ws.setTrangThaiLamViec(rs.getString("trangThaiLamViec"));
            m = ws;
        } else if (AppConstants4.ROLE_MANAGER.equalsIgnoreCase(vaiTro)) {
            Manager4 mgr = new Manager4();
            mgr.setViTri(rs.getString("viTri"));
            mgr.setTrangThaiLamViec(rs.getString("trangThaiLamViec"));
            m = mgr;
        } else if (AppConstants4.ROLE_SALES_STAFF.equalsIgnoreCase(vaiTro)) {
            SalesStaff4 ss = new SalesStaff4();
            ss.setViTri(rs.getString("viTri"));
            ss.setTrangThaiLamViec(rs.getString("trangThaiLamViec"));
            m = ss;
        } else {
            // Mặc định tạo Customer nếu vai trò không xác định
            m = new Customer4();
        }

        m.setId(rs.getInt("id"));
        m.setHoTen(rs.getString("hoTen"));
        m.setTenDangNhap(rs.getString("tenDangNhap"));
        m.setMatKhau(rs.getString("matKhau"));

        Date ns = rs.getDate("ngaySinh");
        if (ns != null) {
            m.setNgaySinh(ns.toLocalDate());
        }

        m.setDiaChi(rs.getString("diaChi"));
        m.setEmail(rs.getString("email"));
        m.setDienThoai(rs.getString("dienThoai"));
        m.setVaiTro(vaiTro);
        m.setTrangThaiTaiKhoan(rs.getString("trangThaiTaiKhoan"));

        return m;
    }
}
