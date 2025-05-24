package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.model.DatBan;
import com.restaurant.restaurant_management_project.model.ThongKe;
import com.restaurant.restaurant_management_project.database.ConnectionPool;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatBanDAO {
    public boolean themDatBan(DatBan datBan) {
        String sql = "INSERT INTO DatBan (maBan, tenKhachHang, soDienThoai, thoiGianDat, thoiGianDen, soLuongNguoi, ghiChu) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, datBan.getMaBan());
            stmt.setString(2, datBan.getTenKhachHang());
            stmt.setString(3, datBan.getSoDienThoai());
            stmt.setTimestamp(4, Timestamp.valueOf(datBan.getThoiGianDat()));
            stmt.setTimestamp(5, Timestamp.valueOf(datBan.getThoiGianDen()));
            stmt.setInt(6, datBan.getSoLuongNguoi());
            stmt.setString(7, datBan.getGhiChu());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    datBan.setMaDatBan(generatedKeys.getInt(1));
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
        String sql = "SELECT * FROM DatBan ORDER BY thoiGianDat DESC";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                DatBan datBan = new DatBan();
                datBan.setMaDatBan(rs.getInt("maDatBan"));
                datBan.setMaBan(rs.getInt("maBan"));
                datBan.setTenKhachHang(rs.getString("tenKhachHang"));
                datBan.setSoDienThoai(rs.getString("soDienThoai"));
                datBan.setThoiGianDat(rs.getTimestamp("thoiGianDat").toLocalDateTime());
                datBan.setThoiGianDen(rs.getTimestamp("thoiGianDen").toLocalDateTime());
                datBan.setSoLuongNguoi(rs.getInt("soLuongNguoi"));
                datBan.setGhiChu(rs.getString("ghiChu"));

                ds.add(datBan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean huyDatBan(int maDatBan) {
        String sql = "DELETE FROM DatBan WHERE maDatBan = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, maDatBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi hủy đặt bàn: " + e.getMessage());
            return false;
        }
    }

    public List<ThongKe> thongKeDatBan(LocalDate from, LocalDate to) {
        List<ThongKe> ds = new ArrayList<>();
        String sql = "SELECT DATE(thoiGianDat) as ngay, " +
                "COUNT(*) as soLuot, " +
                "SUM(soLuongNguoi) as tongKhach " +
                "FROM DatBan " +
                "WHERE DATE(thoiGianDat) BETWEEN ? AND ? " +
                "GROUP BY DATE(thoiGianDat) " +
                "ORDER BY ngay";

        try (Connection conn = ConnectionPool.getConnection();
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
