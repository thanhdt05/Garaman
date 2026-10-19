package dao;

import model.Service4;
import util.AppConstants4;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO4 extends DAO {

    public List<Service4> timDichVuTheoTen(String tuKhoa) throws SQLException {
        List<Service4> list = new ArrayList<>();
        String sql = "SELECT id, ten, nhom, moTa, noiDung, hinhAnh, donGia, " +
                     "       thoiGianDuKien, hanBaoHanh, dieuKienBaoHanh, trangThai " +
                     "FROM tblService4 " +
                     "WHERE trangThai = ? AND ten LIKE ? " +
                     "ORDER BY ten ASC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, AppConstants4.TRANG_THAI_DV_DANG_CUNG_CAP);
            ps.setString(2, taoMauTimKiem(tuKhoa));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapService(rs));
                }
            }
        }
        return list;
    }

    public Service4 layChiTietDichVu(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT id, ten, nhom, moTa, noiDung, hinhAnh, donGia, " +
                     "       thoiGianDuKien, hanBaoHanh, dieuKienBaoHanh, trangThai " +
                     "FROM tblService4 " +
                     "WHERE id = ? AND trangThai = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, AppConstants4.TRANG_THAI_DV_DANG_CUNG_CAP);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapService(rs);
                }
            }
        }
        return null;
    }

    private Service4 mapService(ResultSet rs) throws SQLException {
        Service4 s = new Service4();
        s.setId(rs.getInt("id"));
        s.setTen(rs.getString("ten"));
        s.setNhom(rs.getString("nhom"));
        s.setMoTa(rs.getString("moTa"));
        s.setNoiDung(rs.getString("noiDung"));
        s.setHinhAnh(rs.getString("hinhAnh"));

        BigDecimal donGia = rs.getBigDecimal("donGia");
        s.setDonGia(donGia != null ? donGia : BigDecimal.ZERO);

        s.setThoiGianDuKien(rs.getInt("thoiGianDuKien"));
        s.setHanBaoHanh(rs.getString("hanBaoHanh"));
        s.setDieuKienBaoHanh(rs.getString("dieuKienBaoHanh"));
        s.setTrangThai(rs.getString("trangThai"));
        return s;
    }
}
