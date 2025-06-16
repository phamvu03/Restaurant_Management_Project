package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.database.ConnectionPool;
import com.restaurant.restaurant_management_project.model.Employee;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmployeeDAO {

    // Lấy tất cả nhân viên
    public List<Employee> getAllEmployee() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";
        Connection connection = null;

        try {
            connection = ConnectionPool.getInstance().getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee emp = new Employee();
                    emp.setMaNV(rs.getString("MaNV"));
                    emp.setTenNV(rs.getString("TenNV"));
                    emp.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                    emp.setSDT(rs.getString("SDT"));
                    emp.setEmail(rs.getString("Email"));
                    emp.setChucVu(rs.getString("ChucVu"));
                    emp.setLuong(rs.getBigDecimal("Luong"));
                    employees.add(emp);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy danh sách nhân viên: " + ex.getMessage());
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return employees;
    }

    // Thêm nhân viên
    public boolean addEmployee(Employee emp) {
        String sql = "INSERT INTO NhanVien ( TenNV, NgaySinh, SDT, Email, ChucVu, Luong) " +
                "OUTPUT INSERTED.idNV, INSERTED.MaNV "+
                "VALUES (?, ?, ?, ?, ?, ?)";
        ResultSet resultSet ;
        Connection connection = null;
        int result =0;

        try {
            connection = ConnectionPool.getInstance().getConnection();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, emp.getTenNV());
                stmt.setDate(2, Date.valueOf(emp.getNgaySinh()));
                stmt.setString(3, emp.getSDT());
                stmt.setString(4, emp.getEmail());
                stmt.setString(5, emp.getChucVu());
                stmt.setBigDecimal(6, emp.getLuong());

                resultSet = stmt.executeQuery();
                if (resultSet.next()) {
                    String maNV = resultSet.getString("MaNV");
                   emp.setMaNV(maNV);
                   result = 1;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm nhân viên: " + e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result > 0;
    }

    // Cập nhật nhân viên
    public boolean updateEmployee(Employee emp) {
        String sql = "UPDATE NhanVien SET TenNV = ?, NgaySinh = ?, SDT = ?, Email = ?, ChucVu = ?, Luong = ? WHERE MaNV = ?";
        Connection connection = null;

        try {
            connection = ConnectionPool.getInstance().getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, emp.getTenNV());
                stmt.setDate(2, Date.valueOf(emp.getNgaySinh()));
                stmt.setString(3, emp.getSDT());
                stmt.setString(4, emp.getEmail());
                stmt.setString(5, emp.getChucVu());
                stmt.setBigDecimal(6, emp.getLuong());
                stmt.setString(7, emp.getMaNV());

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật nhân viên: " + e.getMessage());
            return false;
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // Xóa nhân viên
    public boolean deleteEmployee(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";
        Connection connection = null;

        try {
            connection = ConnectionPool.getInstance().getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, maNV);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa nhân viên: " + e.getMessage());
            return false;
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // Tìm kiếm theo mã
    public List<Employee> searchNhanVien(String tuKhoa) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien WHERE MaNV LIKE ? OR TenNV LIKE ? OR ChucVu LIKE ?";
        Connection connection = null;

        try {
            connection = ConnectionPool.getInstance().getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                String wildcardKeyword = "%" + tuKhoa + "%";
                stmt.setString(1, wildcardKeyword);
                stmt.setString(2, wildcardKeyword);
                stmt.setString(3, wildcardKeyword);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Employee emp = new Employee();
                        emp.setMaNV(rs.getString("MaNV"));
                        emp.setTenNV(rs.getString("TenNV"));
                        emp.setNgaySinh(rs.getDate("NgaySinh").toLocalDate());
                        emp.setSDT(rs.getString("SDT"));
                        emp.setEmail(rs.getString("Email"));
                        emp.setChucVu(rs.getString("ChucVu"));
                        emp.setLuong(rs.getBigDecimal("Luong"));
                        list.add(emp);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm: " + e.getMessage());
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return list;
    }


    // Lấy giá trị sequence kế tiếp (VD: NV001, NV002)
    public String getNextSequenceValue(String sequenceName) {
        String sql = "SELECT NEXT VALUE FOR " + sequenceName + " AS nextVal";
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int nextVal = rs.getInt("nextVal");
                return String.format("NV%03d", nextVal);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy sequence: " + e.getMessage(), e);
        }
        return "NV001";
    }

    // Kiểm tra mã nhân viên đã tồn tại
    public boolean isEmployeeExists(String maNV) {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE MaNV = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maNV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra mã NV tồn tại: " + e.getMessage());
        }
        return false;
    }
}
