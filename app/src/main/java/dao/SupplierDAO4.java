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

    public List<Supplier4> findSuppliersByName(String keyword) throws SQLException {
        List<Supplier4> list = new ArrayList<>();
        String sql = "SELECT id, name, address, phoneNumber, email, status " +
                     "FROM tblSupplier4 " +
                     "WHERE status = ? AND name LIKE ? " +
                     "ORDER BY name ASC";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, AppConstants4.SUPPLIER_STATUS_ACTIVE);
            ps.setString(2, createSearchPattern(keyword));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapSupplier(rs));
                }
            }
        }
        return list;
    }

    public Supplier4 getSupplierDetails(int supplierId) throws SQLException {
        if (supplierId <= 0) {
            return null;
        }

        String sql = "SELECT id, name, address, phoneNumber, email, status " +
                     "FROM tblSupplier4 " +
                     "WHERE id = ? AND status = ?";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            ps.setString(2, AppConstants4.SUPPLIER_STATUS_ACTIVE);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSupplier(rs);
                }
            }
        }
        return null;
    }

    public int addSupplier(Supplier4 supplier) throws SQLException, ValidationException4 {
        Validation4.validateSupplier(supplier);

        String sql = "INSERT INTO tblSupplier4 (name, address, phoneNumber, email, status) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, supplier.getName().trim());
            ps.setString(2, supplier.getAddress().trim());
            ps.setString(3, supplier.getPhoneNumber().trim());

            if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {
                ps.setString(4, supplier.getEmail().trim());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }

            ps.setString(5, AppConstants4.SUPPLIER_STATUS_ACTIVE);

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
        s.setName(rs.getString("name"));
        s.setAddress(rs.getString("address"));
        s.setPhoneNumber(rs.getString("phoneNumber"));
        s.setEmail(rs.getString("email"));
        s.setStatus(rs.getString("status"));
        return s;
    }
}
