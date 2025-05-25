package dao;

import db.ConnectionPool;
import db.DBConnection;
import model.NhanVien;
import model.TaiKhoan;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class TaiKhoanDAO {
  

    // 1. Lấy toàn bộ tài khoản
    public List<TaiKhoan> getAllAccounts() throws SQLException {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

               while (rs.next()) {
                   list.add(mapRow(rs));
               }
        }
        return list;
    }

    // 5. Tìm kiếm theo tên hoặc chức vụ (có thể mở rộng thêm)
    public List<TaiKhoan> timKiemTaiKhoan(String tukhoa) throws SQLException {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan WHERE username LIKE ? OR role LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String k = "%" + tukhoa + "%";
            ps.setString(1, k);
            ps.setString(2, k);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    // 3. Thêm tài khoản mới
    public boolean addAccount(TaiKhoan tk) throws SQLException {
        String sql = "INSERT INTO TaiKhoan (username, password, role, MaNV) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

               setParams(ps, tk);
               return ps.executeUpdate() > 0;
           }
    }

    // 4. Cập nhật tai khoan
    public boolean updateTaiKhoan(TaiKhoan tk) throws SQLException {
        String sql = "UPDATE TaiKhoan SET password = ?, username =?, role=?,MaNV=? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

        	ps.setString(1, tk.getPassword());
            ps.setString(2, tk.getUsername());
            ps.setString(3, tk.getRole());
            ps.setString(4, tk.getMaNV());
            ps.setString(5, tk.getUsername());
               return ps.executeUpdate() > 0;
           }
    }

    // 5. Xóa tài khoản
    public boolean deleteAccount(String username) throws SQLException {
        String sql = "DELETE FROM TaiKhoan WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        }
    }

    // 6. Kiểm tra đăng nhập
    public boolean checkLogin(String username, String password) throws SQLException {
        String sql = "SELECT 1 FROM TaiKhoan WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    // Helper: chuyển 1 dòng trong ResultSet thành TaiKhoan
    private TaiKhoan mapRow(ResultSet rs) throws SQLException {
        return new TaiKhoan(
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("role"),
            rs.getString("MaNV")
        );
    }
    // Helper: set các tham số PreparedStatement
    private void setParams(PreparedStatement ps, TaiKhoan tk) throws SQLException {
        ps.setString(1, tk.getUsername());
        ps.setString(2, tk.getPassword());
        ps.setString(3, tk.getRole());
        ps.setString(4, tk.getMaNV());
    }
}
