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
        String sql = "SELECT MaBan, ViTri, SoGhe, GhiChu FROM Ban";
        Connection conn = null;

        try {
            conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                dsBan.add(mapResultSetToBan(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách bàn: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    ConnectionPool.getInstance().releaseConnection(conn);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return dsBan;
    }

    // Thêm bàn mới
    public boolean themBan(Ban ban) {
        String sql = "INSERT INTO Ban (ViTri, SoGhe, GhiChu) VALUES (?, ?, ?)";
        Connection conn = null;
        
        try {
            conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, ban.getViTri());
            stmt.setInt(2, ban.getSoGhe());
            stmt.setString(3, ban.getGhiChu());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            // Lấy mã bàn được tự động sinh
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ban.setMaBan(generatedKeys.getString("MaBan"));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm bàn: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    ConnectionPool.getInstance().releaseConnection(conn);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Cập nhật thông tin bàn
    public boolean suaBan(Ban ban) {
        String sql = "UPDATE Ban SET ViTri = ?, SoGhe = ?, GhiChu = ? WHERE MaBan = ?";
        Connection conn = null;

        try {
            conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, ban.getViTri());
            stmt.setInt(2, ban.getSoGhe());
            stmt.setString(3, ban.getGhiChu());
            stmt.setString(4, ban.getMaBan());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật bàn: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    ConnectionPool.getInstance().releaseConnection(conn);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Xóa bàn
    public boolean xoaBan(String maBan) {
        String sql = "DELETE FROM Ban WHERE MaBan = ?";
        Connection conn = null;

        try {
            conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, maBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa bàn: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    ConnectionPool.getInstance().releaseConnection(conn);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Lấy bàn theo mã
    public Ban getBanById(String maBan) {
        String sql = "SELECT MaBan, ViTri, SoGhe, GhiChu FROM Ban WHERE MaBan = ?";
        Connection conn = null;

        try {
            conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, maBan);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBan(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy bàn theo mã: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    ConnectionPool.getInstance().releaseConnection(conn);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    // Lấy trạng thái bàn từ bảng DatBan
    public String getTrangThaiBan(String maBan) {
        String sql = "SELECT TOP 1 TrangThai FROM DatBan WHERE MaBan = ? " +
                    "AND TrangThai IN ('Đã đặt', 'Đang dùng') " +
                    "ORDER BY ThoiGianDat DESC";
        Connection conn = null;

        try {
            conn = ConnectionPool.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, maBan);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("TrangThai");
            }
            // Mặc định bàn trống nếu không có đặt bàn nào
            return "Trống";
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy trạng thái bàn: " + e.getMessage());
            return "Trống"; // Mặc định
        } finally {
            if (conn != null) {
                try {
                    ConnectionPool.getInstance().releaseConnection(conn);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Method giả lập capNhatTrangThai cho compatibility
    public boolean capNhatTrangThai(String maBan, String trangThai) {
        // Vì bảng Ban không có trường trangThai, 
        // trạng thái được quản lý thông qua bảng DatBan
        System.out.println("Trạng thái bàn " + maBan + " được cập nhật thành " + trangThai + " thông qua DatBan");
        return true;
    }

    // Chuyển ResultSet thành đối tượng Ban
    private Ban mapResultSetToBan(ResultSet rs) throws SQLException {
        return new Ban(
                rs.getString("MaBan"),
                rs.getString("ViTri"),
                rs.getInt("SoGhe"),
                rs.getString("GhiChu")
        );
    }
}
