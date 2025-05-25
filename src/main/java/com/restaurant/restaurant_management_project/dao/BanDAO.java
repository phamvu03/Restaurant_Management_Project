package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.model.Ban;
import com.restaurant.restaurant_management_project.database.ConnectionPool;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BanDAO {
    // Lấy danh sách tất cả bàn
    public List<Ban> getDSBan() {
        List<Ban> dsBan = new ArrayList<>();
        String sql = "SELECT * FROM Ban ORDER BY maBan";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                dsBan.add(mapResultSetToBan(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách bàn: " + e.getMessage());
        }
        return dsBan;
    }

    // Thêm bàn mới
    public boolean themBan(Ban ban) {
        // Thêm maBan vào câu lệnh SQL
        String sql = "INSERT INTO Ban (maBan, tenBan, viTri, trangThai, soChoNgoi) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ban.getMaBan());
            stmt.setString(2, ban.getTenBan());
            stmt.setString(3, ban.getViTri());
            stmt.setString(4, ban.getTrangThai());
            stmt.setInt(5, ban.getSoChoNgoi());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm bàn: " + e.getMessage());
            return false;
        }
    }
    // Cập nhật thông tin bàn
    public boolean suaBan(Ban ban) {
        String sql = "UPDATE Ban SET tenBan = ?, viTri = ?, trangThai = ?, soChoNgoi = ? WHERE maBan = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ban.getTenBan());
            stmt.setString(2, ban.getViTri());
            stmt.setString(3, ban.getTrangThai());
            stmt.setInt(4, ban.getSoChoNgoi());
            stmt.setInt(5, ban.getMaBan());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật bàn: " + e.getMessage());
        }
        return false;
    }

    // Xóa bàn
    public boolean xoaBan(int maBan) {
        String sql = "DELETE FROM Ban WHERE maBan = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa bàn: " + e.getMessage());
        }
        return false;
    }

    // Cập nhật trạng thái bàn
    public boolean capNhatTrangThai(int maBan, String trangThai) {
        String sql = "UPDATE Ban SET trangThai = ? WHERE maBan = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trangThai);
            stmt.setInt(2, maBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái bàn: " + e.getMessage());
        }
        return false;
    }

    // Lấy bàn theo mã
    public Ban getBanById(int maBan) {
        String sql = "SELECT * FROM Ban WHERE maBan = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maBan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBan(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy bàn theo mã: " + e.getMessage());
        }
        return null;
    }

    // Chuyển ResultSet thành đối tượng Ban
    private Ban mapResultSetToBan(ResultSet rs) throws SQLException {
        return new Ban(
                rs.getInt("maBan"),
                rs.getString("tenBan"),
                rs.getString("viTri"),
                rs.getString("trangThai"),
                rs.getInt("soChoNgoi")
        );
    }
}
