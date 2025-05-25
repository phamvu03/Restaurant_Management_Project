package com.restaurant.restaurant_management_project.dao;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.restaurant.restaurant_management_project.database.DatabaseConnection.getConnection;
import static com.restaurant.restaurant_management_project.database.DatabaseConnection.releaseConnection;

public class ReportDAO {
    public BigDecimal getBenefitByDate(LocalDate ngay) {
        BigDecimal tongDoanhThu = new BigDecimal(0);
        String sql = "SELECT SUM(ctdh.SoLuong * ma.Gia) AS TongDoanhThu " +
                "FROM DonHang dh " +
                "JOIN ChiTietDonHang ctdh ON dh.MaDonHang = ctdh.MaDonHang " +
                "JOIN MonAn ma ON ctdh.MaMon = ma.id " +
                "WHERE CAST(dh.ThoiGianThanhToan AS DATE) = ? AND dh.ThoiGianThanhToan IS NOT NULL;";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(ngay));
            rs = ps.executeQuery();

            if (rs.next()) {
                // Xử lý trường hợp SUM trả về NULL
                BigDecimal result = rs.getBigDecimal("TongDoanhThu");
                if (result != null) {
                    tongDoanhThu = result;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Nên log lỗi chi tiết hơn
            System.out.println("Lỗi SQL: " + e.getMessage());
        }
        finally {
            // Đóng tài nguyên theo thứ tự ngược lại
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (ps != null) {
                try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            releaseConnection(conn);
        }
        return tongDoanhThu;
    }
    public int getOderByDate(LocalDate ngay) {
        int orderNum = 0;
        String sql = "SELECT COUNT(MaDonHang) AS SoLuongDonHang " +
                "FROM DonHang " +
                "WHERE CAST(ThoiGianTao AS DATE) = ? " ;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, java.sql.Date.valueOf(ngay));
            rs = ps.executeQuery();

            if (rs.next()) {
                // Xử lý trường hợp SUM trả về NULL
                orderNum = rs.getInt("SoLuongDonHang");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Nên log lỗi chi tiết hơn
            System.out.println("Lỗi SQL: " + e.getMessage());
        }
        finally {
            // Đóng tài nguyên theo thứ tự ngược lại
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (ps != null) {
                try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            releaseConnection(conn);
        }
        return orderNum;
    }
    public int geTotalTable()
    {
        int tableNum = 0;
        String sql = "SELECT COUNT(MaBan) AS SoLuongBan " +
                "FROM Ban ";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                // Xử lý trường hợp SUM trả về NULL
                tableNum = rs.getInt("SoLuongBan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Nên log lỗi chi tiết hơn
            System.out.println("Lỗi SQL: " + e.getMessage());
        }
        finally {
            // Đóng tài nguyên theo thứ tự ngược lại
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (ps != null) {
                try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            releaseConnection(conn);
        }
        return tableNum;
    }
    public int getTableInUse(LocalDateTime time)
    {
        int tableNum = 0;
        String sql = "SELECT DISTINCT MaBan FROM DatBan " +
                "WHERE ? BETWEEN ThoiGianHen AND DATEADD(HOUR, 2, ThoiGianHen)" ;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(time));
            rs = ps.executeQuery();

            if (rs.next()) {
                // Xử lý trường hợp SUM trả về NULL
                tableNum = rs.getInt("SoLuongBan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Nên log lỗi chi tiết hơn
            System.out.println("Lỗi SQL: " + e.getMessage());
        }
        finally {
            // Đóng tài nguyên theo thứ tự ngược lại
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (ps != null) {
                try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            releaseConnection(conn);
        }
        return tableNum;
    }

}
