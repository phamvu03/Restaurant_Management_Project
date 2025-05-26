package com.restaurant.restaurant_management_project.dao; // Đảm bảo package này khớp với cấu trúc thư mục của bạn

import com.restaurant.restaurant_management_project.database.ConnectionPool; // Điều chỉnh import này nếu ConnectionPool nằm ở package khác
import com.restaurant.restaurant_management_project.model.Equipment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO (Data Access Object) để tương tác với bảng DungCu trong cơ sở dữ liệu.
 */
public class EquipmentDAO {

    /**
     * Lấy tất cả các dụng cụ từ cơ sở dữ liệu.
     *
     * @return Danh sách các đối tượng Equipment, trả về danh sách rỗng nếu có lỗi hoặc không có dữ liệu.
     */
    public List<Equipment> getAllEquipment() {
        List<Equipment> equipments = new ArrayList<>();
        String sql = "SELECT MaDungCu, TenDungCu, Loai, SoLuong, TinhTrang, NgayThongKe FROM DungCu";
        Connection connection = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Equipment equip = new Equipment();
                    equip.setMaDungCu(rs.getString("MaDungCu"));
                    equip.setTenDungCu(rs.getString("TenDungCu"));
                    equip.setLoai(rs.getString("Loai"));
                    equip.setSoLuong(rs.getInt("SoLuong"));
                    equip.setTinhTrang(rs.getString("TinhTrang"));
                    equip.setNgayThongKe(rs.getDate("NgayThongKe")); // Sử dụng java.sql.Date

                    equipments.add(equip);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy danh sách dụng cụ: " + ex.getMessage());
            // Có thể ném lại ngoại lệ cho lớp trên xử lý nếu cần
        } finally {
            // Đảm bảo kết nối được giải phóng, ngay cả khi có lỗi xảy ra
            if (connection != null) {
                try {
                    ConnectionPool.getInstance().releaseConnection(connection);
                } catch (SQLException e) {
                    System.err.println("Lỗi khi giải phóng kết nối: " + e.getMessage());
                }
            }
        }
        return equipments;
    }

    /**
     * Thêm một dụng cụ mới vào cơ sở dữ liệu.
     * MaDungCu sẽ được tự động sinh bởi database.
     *
     * @param equip Đối tượng Equipment chứa thông tin dụng cụ cần thêm.
     * @return true nếu thêm thành công, false nếu có lỗi.
     */
    public boolean addEquipment(Equipment equip) {
        // Loại bỏ MaDungCu khỏi câu lệnh INSERT vì nó được tự động sinh bởi database
        String sql = "INSERT INTO DungCu (TenDungCu, Loai, SoLuong, TinhTrang, NgayThongKe) VALUES (?,?,?,?,?)";
        Connection connection = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, equip.getTenDungCu());
                stmt.setString(2, equip.getLoai());
                stmt.setInt(3, equip.getSoLuong());
                stmt.setString(4, equip.getTinhTrang());
                stmt.setDate(5, equip.getNgayThongKe());

                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm dụng cụ: " + ex.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    ConnectionPool.getInstance().releaseConnection(connection);
                } catch (SQLException e) {
                    System.err.println("Lỗi khi giải phóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Cập nhật thông tin của một dụng cụ trong cơ sở dữ liệu.
     *
     * @param equip Đối tượng Equipment chứa thông tin dụng cụ cần cập nhật (dựa vào MaDungCu).
     * @return true nếu cập nhật thành công, false nếu có lỗi hoặc không tìm thấy dụng cụ.
     */
    public boolean updateEquipment(Equipment equip) {
        String sql = "UPDATE DungCu SET TenDungCu = ?, Loai = ?, SoLuong = ?, TinhTrang = ?, NgayThongKe = ? WHERE MaDungCu = ?";
        Connection connection = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, equip.getTenDungCu());
                stmt.setString(2, equip.getLoai());
                stmt.setInt(3, equip.getSoLuong());
                stmt.setString(4, equip.getTinhTrang());
                stmt.setDate(5, equip.getNgayThongKe());
                stmt.setString(6, equip.getMaDungCu()); // Điều kiện WHERE

                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật thông tin dụng cụ: " + ex.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    ConnectionPool.getInstance().releaseConnection(connection);
                } catch (SQLException e) {
                    System.err.println("Lỗi khi giải phóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Xóa một dụng cụ khỏi cơ sở dữ liệu dựa trên mã dụng cụ.
     *
     * @param equipId Mã dụng cụ cần xóa.
     * @return true nếu xóa thành công, false nếu có lỗi hoặc không tìm thấy dụng cụ.
     */
    public boolean deleteEquipment(String equipId) {
        String sql = "DELETE FROM DungCu WHERE MaDungCu = ?";
        Connection connection = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, equipId);

                int rowsDeleted = stmt.executeUpdate();
                return rowsDeleted > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa dụng cụ: " + ex.getMessage());
            return false;
        } finally {
            if (connection != null) {
                try {
                    ConnectionPool.getInstance().releaseConnection(connection);
                } catch (SQLException e) {
                    System.err.println("Lỗi khi giải phóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Tìm kiếm dụng cụ theo tên dụng cụ (sử dụng LIKE).
     *
     * @param keyword Từ khóa tìm kiếm (tên dụng cụ).
     * @return Danh sách các đối tượng Equipment khớp với từ khóa, trả về danh sách rỗng nếu không tìm thấy.
     * @throws SQLException Nếu có lỗi trong quá trình truy vấn database (được ném lại để lớp gọi xử lý).
     */
    public List<Equipment> searchEquipmentByName(String keyword) throws SQLException {
        List<Equipment> equipments = new ArrayList<>();
        String sql = "SELECT MaDungCu, TenDungCu, Loai, SoLuong, TinhTrang, NgayThongKe FROM DungCu WHERE TenDungCu LIKE ?";
        Connection connection = null;
        try {
            connection = ConnectionPool.getInstance().getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, "%" + keyword + "%"); // Đặt tham số cho LIKE
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Equipment equip = new Equipment();
                        equip.setMaDungCu(rs.getString("MaDungCu"));
                        equip.setTenDungCu(rs.getString("TenDungCu"));
                        equip.setLoai(rs.getString("Loai"));
                        equip.setSoLuong(rs.getInt("SoLuong"));
                        equip.setTinhTrang(rs.getString("TinhTrang"));
                        equip.setNgayThongKe(rs.getDate("NgayThongKe"));
                        equipments.add(equip);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi tìm kiếm dụng cụ: " + ex.getMessage());
            throw ex; // Ném lại ngoại lệ để lớp gọi xử lý
        } finally {
            if (connection != null) {
                try {
                    ConnectionPool.getInstance().releaseConnection(connection);
                } catch (SQLException e) {
                    System.err.println("Lỗi khi giải phóng kết nối: " + e.getMessage());
                }
            }
        }
        return equipments;
    }
}