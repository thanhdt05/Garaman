package dao;

import model.Service4;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO4 extends DAO {

    public List<Service4> findServicesByName(String keyword) throws SQLException {
        List<Service4> list = new ArrayList<>();
        String sql = "SELECT id, name, category, description, content, imagePath, unitPrice,        estimatedDuration, warrantyPeriod, warrantyTerms, status FROM tblService4 WHERE status = ? AND name LIKE ? ORDER BY name ASC";

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "Đang cung cấp");
            ps.setString(2, createSearchPattern(keyword));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapService(rs));
                }
            }
        }
        return list;
    }

    public Service4 getServiceDetails(int serviceId) throws SQLException {
        if (serviceId <= 0) {
            return null;
        }

        String sql = "SELECT id, name, category, description, content, imagePath, unitPrice,        estimatedDuration, warrantyPeriod, warrantyTerms, status FROM tblService4 WHERE id = ? AND status = ?";

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            ps.setString(2, "Đang cung cấp");

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
        s.setName(rs.getString("name"));
        s.setCategory(rs.getString("category"));
        s.setDescription(rs.getString("description"));
        s.setContent(rs.getString("content"));
        s.setImagePath(rs.getString("imagePath"));

        BigDecimal unitPrice = rs.getBigDecimal("unitPrice");
        s.setUnitPrice(unitPrice != null ? unitPrice : BigDecimal.ZERO);

        s.setEstimatedDuration(rs.getInt("estimatedDuration"));
        s.setWarrantyPeriod(rs.getString("warrantyPeriod"));
        s.setWarrantyTerms(rs.getString("warrantyTerms"));
        s.setStatus(rs.getString("status"));
        return s;
    }
}
