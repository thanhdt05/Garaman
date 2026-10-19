package dao;

import model.SparePartCategory4;
import util.AppConstants4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SparePartCategoryDAO4 extends DAO {

    public List<SparePartCategory4> layDanhSachNhom() throws SQLException {
        List<SparePartCategory4> list = new ArrayList<>();
        String sql = "SELECT id, ten, moTa, trangThai " +
                     "FROM tblSparePartCategory4 " +
                     "WHERE trangThai = ? " +
                     "ORDER BY ten ASC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, AppConstants4.TRANG_THAI_NHOM_DANG_SU_DUNG);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCategory(rs));
                }
            }
        }
        return list;
    }

    public SparePartCategory4 layNhomDangSuDung(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT id, ten, moTa, trangThai " +
                     "FROM tblSparePartCategory4 " +
                     "WHERE id = ? AND trangThai = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, AppConstants4.TRANG_THAI_NHOM_DANG_SU_DUNG);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCategory(rs);
                }
            }
        }
        return null;
    }

    private SparePartCategory4 mapCategory(ResultSet rs) throws SQLException {
        SparePartCategory4 c = new SparePartCategory4();
        c.setId(rs.getInt("id"));
        c.setTen(rs.getString("ten"));
        c.setMoTa(rs.getString("moTa"));
        c.setTrangThai(rs.getString("trangThai"));
        return c;
    }
}
