package dao;

import model.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDAO4 extends DAO {

    public Member4 findByUsername(String username) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT m.id, m.fullName, m.username, m.password, m.dateOfBirth, m.address,        m.email, m.phoneNumber, m.role, m.accountStatus,        e.position, e.employmentStatus FROM tblMember4 m LEFT JOIN tblEmployee4 e ON m.id = e.tblMember4id WHERE m.username = ?";

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMember(rs);
                }
            }
        }
        return null;
    }

    public Member4 getActiveMember(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }

        String sql = "SELECT m.id, m.fullName, m.username, m.password, m.dateOfBirth, m.address,        m.email, m.phoneNumber, m.role, m.accountStatus,        e.position, e.employmentStatus FROM tblMember4 m LEFT JOIN tblEmployee4 e ON m.id = e.tblMember4id WHERE m.id = ? AND m.accountStatus = ?";

        try (Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, "Hoạt động");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Member4 member = mapMember(rs);
                    if (member instanceof Employee4) {
                        Employee4 emp = (Employee4) member;
                        if (!"Đang làm việc".equals(emp.getEmploymentStatus())) {
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
        String role = rs.getString("role");
        Member4 m;

        if ("WarehouseStaff".equalsIgnoreCase(role)) {
            WarehouseStaff4 ws = new WarehouseStaff4();
            ws.setPosition(rs.getString("position"));
            ws.setEmploymentStatus(rs.getString("employmentStatus"));
            m = ws;
        } else {
            // Khách hàng hoặc vai trò mặc định trong phạm vi 10 model
            m = new Customer4();
        }

        m.setId(rs.getInt("id"));
        m.setFullName(rs.getString("fullName"));
        m.setUsername(rs.getString("username"));
        m.setPassword(rs.getString("password"));

        Date ns = rs.getDate("dateOfBirth");
        if (ns != null) {
            m.setDateOfBirth(ns.toLocalDate());
        }

        m.setAddress(rs.getString("address"));
        m.setEmail(rs.getString("email"));
        m.setPhoneNumber(rs.getString("phoneNumber"));
        m.setRole(role);
        m.setAccountStatus(rs.getString("accountStatus"));

        return m;
    }
}
