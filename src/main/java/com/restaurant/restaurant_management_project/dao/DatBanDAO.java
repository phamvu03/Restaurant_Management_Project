package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.database.ConnectionPool;
import com.restaurant.restaurant_management_project.database.DatabaseConnection;
import com.restaurant.restaurant_management_project.model.DatBan;
import com.restaurant.restaurant_management_project.model.ThongKe;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatBanDAO {
    public boolean themDatBan(DatBan datBan) {
        String sql = "INSERT INTO DatBan (MaBan, TenKhach, SoKhach, SDTKhach, Email, ThoiGianDat, TrangThai, GhiChu) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, datBan.getMaBan());
            stmt.setString(2, datBan.getTenKhachHang());
            stmt.setInt(3, datBan.getSoKhach());
            stmt.setString(4, datBan.getSoDienThoai());
            stmt.setString(5, datBan.getEmail());
            stmt.setTimestamp(6, Timestamp.valueOf(datBan.getThoiGianDat()));
            stmt.setString(7, datBan.getTrangThai());
            stmt.setString(8, datBan.getGhiChu());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    datBan.setMaDatBan(generatedKeys.getString(1));
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<DatBan> getDSDatBan() {
        List<DatBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM DatBan ORDER BY ThoiGianDat DESC";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DatBan datBan = new DatBan();
                datBan.setMaDatBan(rs.getString("MaDatBan"));
                datBan.setMaBan(rs.getString("MaBan"));
                datBan.setTenKhachHang(rs.getString("TenKhach"));
                datBan.setSoKhach(rs.getInt("SoKhach"));
                datBan.setSoDienThoai(rs.getString("SDTKhach"));
                datBan.setEmail(rs.getString("Email"));
                datBan.setThoiGianDat(rs.getTimestamp("ThoiGianDat").toLocalDateTime());
                datBan.setTrangThai(rs.getString("TrangThai"));
                datBan.setGhiChu(rs.getString("GhiChu"));

                ds.add(datBan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean huyDatBan(String maDatBan) {
        String sql = "DELETE FROM DatBan WHERE MaDatBan = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maDatBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi hủy đặt bàn: " + e.getMessage());
            return false;
        }
    }

    public boolean capNhatTrangThai(String maDatBan, String trangThai) {
        String sql = "UPDATE DatBan SET TrangThai = ? WHERE MaDatBan = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trangThai);
            stmt.setString(2, maDatBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái: " + e.getMessage());
            return false;
        }
    }

    public DatBan getDatBanById(String maDatBan) {
        String sql = "SELECT * FROM DatBan WHERE MaDatBan = ?";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maDatBan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DatBan datBan = new DatBan();
                    datBan.setMaDatBan(rs.getString("MaDatBan"));
                    datBan.setMaBan(rs.getString("MaBan"));
                    datBan.setTenKhachHang(rs.getString("TenKhach"));
                    datBan.setSoKhach(rs.getInt("SoKhach"));
                    datBan.setSoDienThoai(rs.getString("SDTKhach"));
                    datBan.setEmail(rs.getString("Email"));
                    datBan.setThoiGianDat(rs.getTimestamp("ThoiGianDat").toLocalDateTime());
                    datBan.setTrangThai(rs.getString("TrangThai"));
                    datBan.setGhiChu(rs.getString("GhiChu"));
                    return datBan;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ThongKe> thongKeDatBan(LocalDate from, LocalDate to) {
        List<ThongKe> ds = new ArrayList<>();
        String sql = "SELECT CAST(ThoiGianDat AS DATE) as ngay, " +
                "COUNT(*) as soLuot, " +
                "SUM(SoKhach) as tongKhach " +
                "FROM DatBan " +
                "WHERE CAST(ThoiGianDat AS DATE) BETWEEN ? AND ? " +
                "GROUP BY CAST(ThoiGianDat AS DATE) " +
                "ORDER BY ngay";

        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(from));
            stmt.setDate(2, Date.valueOf(to));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ThongKe tk = new ThongKe(
                            rs.getDate("ngay").toLocalDate(),
                            rs.getInt("soLuot"),
                            rs.getInt("tongKhach")
                    );
                    ds.add(tk);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
