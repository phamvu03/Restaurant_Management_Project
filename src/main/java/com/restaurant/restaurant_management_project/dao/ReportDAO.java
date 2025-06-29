package com.restaurant.restaurant_management_project.dao;



import com.restaurant.restaurant_management_project.database.ConnectionPool;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;


public class ReportDAO {
    private ConnectionPool connectionPool;

    public ReportDAO() {
        try {
            connectionPool = ConnectionPool.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<String, BigDecimal> getDoanhThuTheoNgay(LocalDate fromDate, LocalDate toDate) {
        Map<String, BigDecimal> result = new LinkedHashMap<>();

        // Điền dữ liệu mặc định cho tất cả các ngày trong khoảng thời gian
        LocalDate currentDate = fromDate;
        while (!currentDate.isAfter(toDate)) {
            String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            result.put(formattedDate, BigDecimal.ZERO);
            currentDate = currentDate.plusDays(1);
        }

        // SQL truy vấn doanh thu theo ngày từ DonHang, ChiTietDonHang và MonAn
        String sql = "SELECT " +
                "    CAST(dh.ThoiGianThanhToan AS DATE) as Ngay, " +
                "    SUM(ma.Gia * ctdh.SoLuong) as DoanhThu " +
                "FROM DonHang dh " +
                "JOIN ChiTietDonHang ctdh ON dh.MaDonHang = ctdh.MaDonHang " +
                "JOIN MonAn ma ON ctdh.MaMon = ma.MaMon " +
                "WHERE dh.ThoiGianThanhToan IS NOT NULL " +
                "AND dh.ThoiGianThanhToan BETWEEN ? AND ? " +
                "GROUP BY CAST(dh.ThoiGianThanhToan AS DATE) " +
                "ORDER BY CAST(dh.ThoiGianThanhToan AS DATE)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = connectionPool.getConnection();
            stmt = conn.prepareStatement(sql);

            // Đặt tham số cho khoảng thời gian
            LocalDateTime fromDateTime = fromDate.atStartOfDay();
            LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);
            stmt.setTimestamp(1, Timestamp.valueOf(fromDateTime));
            stmt.setTimestamp(2, Timestamp.valueOf(toDateTime));

            rs = stmt.executeQuery();

            // Cập nhật doanh thu cho các ngày có dữ liệu
            while (rs.next()) {
                LocalDate date = rs.getDate("Ngay").toLocalDate();
                String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                BigDecimal revenue = rs.getBigDecimal("DoanhThu");

                // Kiểm tra và xử lý giá trị null
                if (revenue != null) {
                    result.put(formattedDate, revenue);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi truy vấn doanh thu theo ngày: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            // Đóng tài nguyên theo thứ tự ngược lại
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            connectionPool.releaseConnection(conn);
        }
        return result;
    }
    public BigDecimal getBenefitByDate(LocalDateTime ngay) {
        BigDecimal tongDoanhThu = new BigDecimal(0);
        String sql = "SELECT SUM(ctdh.SoLuong * ma.Gia) AS TongDoanhThu " +
                "FROM DonHang dh " +
                "JOIN ChiTietDonHang ctdh ON dh.MaDonHang = ctdh.MaDonHang " +
                "JOIN MonAn ma ON ctdh.MaMon = ma.MaMon " +
                "WHERE CAST(dh.ThoiGianThanhToan AS DATE) = ? AND dh.ThoiGianThanhToan IS NOT NULL;";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionPool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(ngay.toLocalDate()));
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
            connectionPool.releaseConnection(conn);
        }
        return tongDoanhThu;
    }
    public int getOderByDate(LocalDateTime ngay) {
        int orderNum = 0;
        String sql = "SELECT COUNT(MaDonHang) AS SoLuongDonHang " +
                "FROM DonHang " +
                "WHERE CAST(ThoiGianTao AS DATE) = ? " ;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionPool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(ngay.toLocalDate()));
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
            connectionPool.releaseConnection(conn);
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
            conn = connectionPool.getConnection();
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
            connectionPool.releaseConnection(conn);
        }
        return tableNum;
    }
    public int getTableInUse(LocalDateTime time)
    {
        int tableNum = 0;
        String sql = "SELECT COUNT (DISTINCT MaBan) FROM DatBan " +
                "WHERE ? BETWEEN ThoiGianDat AND DATEADD(HOUR, 2, ThoiGianDat)" ;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionPool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(time));
            rs = ps.executeQuery();

            if (rs.next()) {
                tableNum = rs.getInt(1);
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
            connectionPool.releaseConnection(conn);
        }
        return tableNum;
    }
    public int getOrderedTable(LocalDateTime start,LocalDateTime end)
    {
        int tableNum = 0;
        String sql = "SELECT COUNT(*) FROM DatBan " +
                "WHERE ThoiGianDat >= ? AND ThoiGianDat <= ?" ;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = connectionPool.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            rs = ps.executeQuery();

            if (rs.next()) {
                tableNum = rs.getInt(1);
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
            connectionPool.releaseConnection(conn);
        }
        return tableNum;
    }
    public Map<String, BigDecimal> getDoanhThuTheoThu(LocalDateTime fromDate, LocalDateTime toDate) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, BigDecimal> result = new LinkedHashMap<>();

        try {
            conn = connectionPool.getConnection();

            String sql = "SELECT " +
                    "    DATEPART(WEEKDAY, dh.ThoiGianThanhToan) AS Thu, " +
                    "    SUM(ma.Gia * ctdh.SoLuong) AS DoanhThu " +
                    "FROM DonHang dh " +
                    "JOIN ChiTietDonHang ctdh ON dh.MaDonHang = ctdh.MaDonHang " +
                    "JOIN MonAn ma ON ctdh.MaMon = ma.MaMon " +
                    "WHERE dh.ThoiGianThanhToan IS NOT NULL ";

            if (fromDate != null) {
                sql += "AND dh.ThoiGianThanhToan >= ? ";
            }

            if (toDate != null) {
                sql += "AND dh.ThoiGianThanhToan <= ? ";
            }

            sql += "GROUP BY DATEPART(WEEKDAY, dh.ThoiGianThanhToan) " +
                    "ORDER BY Thu";

            ps = conn.prepareStatement(sql);

            int paramIndex = 1;
            if (fromDate != null) {
                ps.setTimestamp(paramIndex++, java.sql.Timestamp.valueOf(fromDate));
            }

            if (toDate != null) {
                ps.setTimestamp(paramIndex++, java.sql.Timestamp.valueOf(toDate));
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                int thu = rs.getInt("Thu");
                BigDecimal doanhThu = rs.getBigDecimal("DoanhThu");

                String tenThu = getTenThu(thu);
                result.put(tenThu, doanhThu);
            }
        } finally {
            connectionPool.releaseConnection(conn);
        }
        return result;
    }

    public Map<String, BigDecimal> getDoanhThuTheoTuanTrongThang(int month, int year) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, BigDecimal> result = new LinkedHashMap<>();

        try {
            conn = connectionPool.getConnection();

            String sql = "WITH ThongTinTuan AS ( " +
                    "    SELECT " +
                    "        DATEPART(DAY, dh.ThoiGianThanhToan) AS Ngay, " +
                    "        DATEDIFF(WEEK, DATEADD(MONTH, DATEDIFF(MONTH, 0, dh.ThoiGianThanhToan), 0), dh.ThoiGianThanhToan) + 1 AS TuanTrongThang, " +
                    "        dh.ThoiGianThanhToan, " +
                    "        ma.Gia, " +
                    "        ctdh.SoLuong " +
                    "    FROM DonHang dh " +
                    "    JOIN ChiTietDonHang ctdh ON dh.MaDonHang = ctdh.MaDonHang " +
                    "JOIN MonAn ma ON ctdh.MaMon = ma.MaMon "+
                    "    WHERE dh.ThoiGianThanhToan IS NOT NULL " +
                    "    AND YEAR(dh.ThoiGianThanhToan) = ? " +
                    "    AND MONTH(dh.ThoiGianThanhToan) = ? " +
                    ") " +
                    "SELECT " +
                    "    TuanTrongThang, " +
                    "    MIN(CONVERT(DATE, ThoiGianThanhToan)) AS NgayBatDau, " +
                    "    MAX(CONVERT(DATE, ThoiGianThanhToan)) AS NgayKetThuc, " +
                    "    SUM(Gia * SoLuong) AS DoanhThu " +
                    "FROM ThongTinTuan " +
                    "GROUP BY TuanTrongThang " +
                    "ORDER BY TuanTrongThang";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, year);
            ps.setInt(2, month);

            rs = ps.executeQuery();

            while (rs.next()) {
                int tuanTrongThang = rs.getInt("TuanTrongThang");
                BigDecimal doanhThu = rs.getBigDecimal("DoanhThu");

                // Format: "Tuần 1
                String thongTinTuan = "Tuần " + tuanTrongThang ;

                result.put(thongTinTuan, doanhThu);
            }
        } finally {
            connectionPool.releaseConnection(conn);
        }

        return result;
    }
    public Map<String, BigDecimal> getDoanhThuTheoThang(int year) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, BigDecimal> result = new LinkedHashMap<>();

        try {
            conn = connectionPool.getConnection();

            String sql = "SELECT " +
                    "    MONTH(dh.ThoiGianThanhToan) AS Thang, " +
                    "    SUM(ma.Gia * ctdh.SoLuong) AS DoanhThu " +
                    "FROM DonHang dh " +
                    "JOIN ChiTietDonHang ctdh ON dh.MaDonHang = ctdh.MaDonHang " +
                    "JOIN MonAn ma ON ctdh.MaMon = ma.MaMon "+
                    "WHERE dh.ThoiGianThanhToan IS NOT NULL ";

            if (year > 0) {
                sql += "AND YEAR(dh.ThoiGianThanhToan) = ? ";
            }

            sql += "GROUP BY MONTH(dh.ThoiGianThanhToan) " +
                    "ORDER BY Thang ASC";

            ps = conn.prepareStatement(sql);

            if (year > 0) {
                ps.setInt(1, year);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                int thang = rs.getInt("Thang");
                BigDecimal doanhThu = rs.getBigDecimal("DoanhThu");

                String tenThang = getTenThang(thang);
                result.put(tenThang, doanhThu);
            }
        } finally {
            connectionPool.releaseConnection(conn);
        }

        return result;
    }

    public Map<String, Integer> getSalesByCategory() {
        Map<String, Integer> salesMap = new HashMap<>();

        String sql = "SELECT ma.Nhom AS CategoryName, SUM(ctdh.SoLuong) AS TotalQuantitySold " +
                "FROM MonAn ma " +
                "JOIN ChiTietDonHang ctdh ON ma.MaMon = ctdh.MaMon " +
                "GROUP BY ma.Nhom";
        Connection conn = null;
        try{
            conn = connectionPool.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String categoryName = rs.getString("CategoryName");
                int quantitySold = rs.getInt("TotalQuantitySold");

                salesMap.put(categoryName, quantitySold);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            connectionPool.releaseConnection(conn);
        }

        return salesMap;
    }
    public Map<Integer, Integer> getThongKeKhachTheoGio(LocalDateTime fromDate, LocalDateTime toDate) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        // Sử dụng TreeMap để tự động sắp xếp theo giờ
        Map<Integer, Integer> result = new TreeMap<>();

        try {
            conn =connectionPool.getConnection();

            String sql = "SELECT " +
                    "    DATEPART(HOUR, db.ThoiGianDat) AS Gio, " +
                    "    SUM(db.SoKhach) AS TongKhach " +
                    "FROM DatBan db " +
                    "WHERE db.ThoiGianDat BETWEEN ? AND ? " +
                    "GROUP BY DATEPART(HOUR, db.ThoiGianDat) " +
                    "ORDER BY Gio";

            ps = conn.prepareStatement(sql);
            ps.setTimestamp( 1, java.sql.Timestamp.valueOf(fromDate));
            ps.setTimestamp( 2, java.sql.Timestamp.valueOf(toDate));

            rs = ps.executeQuery();

            // Khởi tạo với 0 khách cho tất cả các giờ từ 0-23
            for (int i = 0; i < 24; i++) {
                result.put(i, 0);
            }

            // Cập nhật số khách cho các giờ có dữ liệu
            while (rs.next()) {
                int gio = rs.getInt("Gio");
                int tongKhach = rs.getInt("TongKhach");
                result.put(gio, tongKhach);
            }

        } finally {
            connectionPool.releaseConnection(conn);
        }
        return result;
    }

    public Map<String, Integer> getThongKeKhachTheoThu(LocalDate fromDate, LocalDate toDate) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Integer> result = new LinkedHashMap<>();

        try {
            conn = connectionPool.getConnection();

            String sql = "SELECT " +
                    "    DATEPART(WEEKDAY, db.ThoiGianDat) AS Thu, " +
                    "    SUM(db.SoKhach) AS TongKhach " +
                    "FROM DatBan db " +
                    "WHERE db.ThoiGianDat BETWEEN ? AND ? " +
                    "GROUP BY DATEPART(WEEKDAY, db.ThoiGianDat) " +
                    "ORDER BY Thu";

            ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));

            rs = ps.executeQuery();

            // Khởi tạo với 0 khách cho tất cả các thứ
            for (int i = 1; i <= 7; i++) {
                result.put(getTenThu(i), 0);
            }

            // Cập nhật số khách cho các thứ có dữ liệu
            while (rs.next()) {
                int thu = rs.getInt("Thu");
                int tongKhach = rs.getInt("TongKhach");
                result.put(getTenThu(thu), tongKhach);
            }

        } finally {
            connectionPool.releaseConnection(conn);
        }
        return result;
    }
    public Map<Integer, Integer> getThongKeKhachTheoNgayTrongThang(int month, int year) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<Integer, Integer> result = new TreeMap<>();

        try {
            conn = connectionPool.getConnection();

            String sql = "SELECT " +
                    "    DAY(db.ThoiGianDat) AS Ngay, " +
                    "    SUM(db.SoKhach) AS TongKhach " +
                    "FROM DatBan db " +
                    "WHERE MONTH(db.ThoiGianDat) = ? AND YEAR(db.ThoiGianDat) = ? " +
                    "GROUP BY DAY(db.ThoiGianDat) " +
                    "ORDER BY Ngay";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, month);
            ps.setInt(2, year);

            rs = ps.executeQuery();

            // Khởi tạo với 0 khách cho tất cả các ngày trong tháng
            int daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth();
            for (int i = 1; i <= daysInMonth; i++) {
                result.put(i, 0);
            }

            // Cập nhật số khách cho các ngày có dữ liệu
            while (rs.next()) {
                int ngay = rs.getInt("Ngay");
                int tongKhach = rs.getInt("TongKhach");
                result.put(ngay, tongKhach);
            }

        } finally {
            connectionPool.releaseConnection(conn);
        }

        return result;
    }
    private String getTenThu(int thu) {
        switch (thu) {
            case 1: return "Chủ nhật";
            case 2: return "Thứ hai";
            case 3: return "Thứ ba";
            case 4: return "Thứ tư";
            case 5: return "Thứ năm";
            case 6: return "Thứ sáu";
            case 7: return "Thứ bảy";
            default: return "Không xác định";
        }
    }

    /**
     * Lấy tên tháng từ số tháng
     */
    private String getTenThang(int thang) {
        switch (thang) {
            case 1: return "Tháng 1";
            case 2: return "Tháng 2";
            case 3: return "Tháng 3";
            case 4: return "Tháng 4";
            case 5: return "Tháng 5";
            case 6: return "Tháng 6";
            case 7: return "Tháng 7";
            case 8: return "Tháng 8";
            case 9: return "Tháng 9";
            case 10: return "Tháng 10";
            case 11: return "Tháng 11";
            case 12: return "Tháng 12";
            default: return "Không xác định";
        }
    }

}
