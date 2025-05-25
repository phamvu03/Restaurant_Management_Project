package dao;

import model.NhanVien;
import db.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class NhanVienDAO {

    // 1. Lấy toàn bộ danh sách nhân viên
    public List<NhanVien> getAllNhanVien() throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }
    
    //kiểm tra mã nhân viên có tồn tại chưa
    public boolean maNVTonTai(String maNV) throws SQLException {
        String sql = "SELECT 1 FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // Trả về true nếu tìm thấy
        }
    }

    // 2. Thêm nhân viên mới
    public boolean addNhanVien(NhanVien nv) throws SQLException {
    	

        String sql = "INSERT INTO NhanVien (MaNV, TenNV, NgaySinh, SDT, Email, ChucVu, Luong) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            setParams(ps, nv);
            return ps.executeUpdate() > 0;
        }
    }

    // 3. Cập nhật nhân viên
    public boolean updateNhanVien(NhanVien nv) throws SQLException {
        String sql = "UPDATE NhanVien SET TenNV=?, NgaySinh=?, SDT=?, Email=?, ChucVu=?, Luong=? WHERE MaNV=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getTenNV());
            ps.setDate(2, Date.valueOf(nv.getNgaySinh()));
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getEmail());
            ps.setString(5, nv.getChucVu());
            ps.setBigDecimal(6, nv.getLuong());
            ps.setString(7, nv.getMaNV());

            return ps.executeUpdate() > 0;
        }
    }

    // 4. Xóa nhân viên theo mã
    public boolean deleteNhanVien(String maNV) throws SQLException {
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        }
    }

    // 5. Tìm kiếm theo tên hoặc chức vụ (có thể mở rộng thêm)
    public List<NhanVien> searchNhanVien(String keyword) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE TenNV LIKE ? OR ChucVu LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String k = "%" + keyword + "%";
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

    // 6. Thống kê tổng lương theo chức vụ
    public List<Object[]> thongKeNhanVienTheoChucVu() throws SQLException {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT ChucVu, COUNT(*) AS SoLuong FROM NhanVien GROUP BY ChucVu";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String chucVu = rs.getString("ChucVu");
                int soLuong = rs.getInt("SoLuong");
                result.add(new Object[]{chucVu, soLuong});
            }
        }
        return result;
    }


    // Helper: chuyển 1 dòng trong ResultSet thành NhanVien
    private NhanVien mapRow(ResultSet rs) throws SQLException {
        return new NhanVien(
            rs.getString("MaNV"),
            rs.getString("TenNV"),
            rs.getDate("NgaySinh").toLocalDate(),
            rs.getString("SDT"),
            rs.getString("Email"),
            rs.getString("ChucVu"),
            rs.getBigDecimal("Luong")
        );
    }

    // Helper: set các tham số PreparedStatement
    private void setParams(PreparedStatement ps, NhanVien nv) throws SQLException {
        ps.setString(1, nv.getMaNV());
        ps.setString(2, nv.getTenNV());
        ps.setDate(3, Date.valueOf(nv.getNgaySinh()));
        ps.setString(4, nv.getSdt());
        ps.setString(5, nv.getEmail());
        ps.setString(6, nv.getChucVu());
        ps.setBigDecimal(7, nv.getLuong());
    }
}
