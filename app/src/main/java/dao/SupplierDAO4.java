package dao;

import model.Supplier4;

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
        String sql = "SELECT id, name, address, phoneNumber, email, status FROM tblSupplier4 WHERE status = ? AND name LIKE ? ORDER BY name ASC";

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "Đang hoạt động");
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

        String sql = "SELECT id, name, address, phoneNumber, email, status FROM tblSupplier4 WHERE id = ? AND status = ?";

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            ps.setString(2, "Đang hoạt động");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSupplier(rs);
                }
            }
        }
        return null;
    }

    public int addSupplier(Supplier4 supplier) throws SQLException, IllegalArgumentException {
        {
            if (supplier == null) {
                throw new IllegalArgumentException("supplier: Thông tin nhà cung cấp không được null");
            }
            {
                if (supplier.getName() == null) {
                    throw new IllegalArgumentException("name: Trường này là bắt buộc");
                }
                String trimmed = supplier.getName().trim();
                if (trimmed.isEmpty()) {
                    throw new IllegalArgumentException("name: Trường này không được để trống");
                }
                if (trimmed.length() > 255) {
                    throw new IllegalArgumentException("name: Độ dài không được vượt quá " + 255 + " ký tự");
                }
            }
            {
                if (supplier.getAddress() == null) {
                    throw new IllegalArgumentException("address: Trường này là bắt buộc");
                }
                String trimmed = supplier.getAddress().trim();
                if (trimmed.isEmpty()) {
                    throw new IllegalArgumentException("address: Trường này không được để trống");
                }
                if (trimmed.length() > 5000) {
                    throw new IllegalArgumentException("address: Độ dài không được vượt quá " + 5000 + " ký tự");
                }
            }
            {
                if (supplier.getPhoneNumber() == null || supplier.getPhoneNumber().trim().isEmpty()) {
                    throw new IllegalArgumentException("phoneNumber: Số điện thoại không được để trống");
                }
                String cleaned = supplier.getPhoneNumber().trim().replaceAll("[\\s-]", "");
                if (!java.util.regex.Pattern.compile("^\\+?[0-9]{8,15}$").matcher(cleaned).matches()) {
                    throw new IllegalArgumentException("phoneNumber: Số điện thoại phải từ 8 đến 15 chữ số, có thể bắt đầu bằng dấu +");
                }
            }
            if (supplier.getEmail() != null) {
                {
                    if (supplier.getEmail() != null && !supplier.getEmail().trim().isEmpty()) {

                        String trimmed = supplier.getEmail().trim();
                        if (trimmed.length() > 255 || !java.util.regex.Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matcher(trimmed).matches()) {
                            throw new IllegalArgumentException("email: Địa chỉ email không đúng định dạng");
                        }
                    }
                }
            }
        }

        String sql = "INSERT INTO tblSupplier4 (name, address, phoneNumber, email, status) VALUES (?, ?, ?, ?, ?)";

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

            ps.setString(5, "Đang hoạt động");

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
