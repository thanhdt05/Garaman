package dao;

import model.SparePartCategory4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SparePartCategoryDAO4 extends DAO {

    public List<SparePartCategory4> getActiveCategories() throws SQLException {
        List<SparePartCategory4> list = new ArrayList<>();
        String sql = "SELECT id, name, description, status FROM tblSparePartCategory4 WHERE status = ? ORDER BY name ASC";

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "Đang sử dụng");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCategory(rs));
                }
            }
        }
        return list;
    }

    public SparePartCategory4 getActiveCategory(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT id, name, description, status FROM tblSparePartCategory4 WHERE id = ? AND status = ?";

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, "Đang sử dụng");
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
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setStatus(rs.getString("status"));
        return c;
    }
}
