package dao;

import model.WarehouseStaff4;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WarehouseStaffDAO4 extends DAO {

    public WarehouseStaff4 getWarehouseStaffDetails(int warehouseStaffId) throws SQLException {
        if (warehouseStaffId <= 0) {
            return null;
        }

        String sql = "SELECT m.id, m.fullName, m.username, m.password, m.dateOfBirth, m.address,        m.email, m.phoneNumber, m.role, m.accountStatus,        e.position, e.employmentStatus FROM tblMember4 m JOIN tblEmployee4 e ON m.id = e.tblMember4id WHERE m.id = ?   AND m.role = ?   AND m.accountStatus = ?   AND e.employmentStatus = ?";

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, warehouseStaffId);
            ps.setString(2, "WarehouseStaff");
            ps.setString(3, "Hoạt động");
            ps.setString(4, "Đang làm việc");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    WarehouseStaff4 ws = new WarehouseStaff4();
                    ws.setId(rs.getInt("id"));
                    ws.setFullName(rs.getString("fullName"));
                    ws.setUsername(rs.getString("username"));
                    ws.setPassword(rs.getString("password"));

                    Date ns = rs.getDate("dateOfBirth");
                    if (ns != null) {
                        ws.setDateOfBirth(ns.toLocalDate());
                    }

                    ws.setAddress(rs.getString("address"));
                    ws.setEmail(rs.getString("email"));
                    ws.setPhoneNumber(rs.getString("phoneNumber"));
                    ws.setRole(rs.getString("role"));
                    ws.setAccountStatus(rs.getString("accountStatus"));
                    ws.setPosition(rs.getString("position"));
                    ws.setEmploymentStatus(rs.getString("employmentStatus"));
                    return ws;
                }
            }
        }
        return null;
    }
}
