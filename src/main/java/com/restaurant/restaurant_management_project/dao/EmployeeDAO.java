package com.restaurant.restaurant_management_project.dao;
import com.restaurant.restaurant_management_project.database.ConnectionPool;
import com.restaurant.restaurant_management_project.model.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class EmployeeDAO {
    public List<Employee> getAllEmployee(){
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM NHANVIEN";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(
                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    Employee employ = new Employee();
                    employ.setMaNV(rs.getString("MaNV"));
                    employ.setTenNV(rs.getString("TenNV"));
                    employ.setNgaySinh(rs.getDate("NgaySinh"));
                    employ.setSDT(rs.getString("SDT"));
                    employ.setEmail(rs.getString("Email"));
                    employ.setChucVu(rs.getString("ChucVu"));
                    employ.setLuong(rs.getBigDecimal("Luong"));

                    employees.add(employ);
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
    
    public boolean addEmployee(Employee employ){
        String sql = "INSERT INTO NhanVien (MaNV, TenNV, NgaySinh, SDT, Email, ChucVu, Luong) "
                + "VALUES (?,?,?,?,?,?,?)";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql)){
                stmt.setString(1, employ.getMaNV());
                stmt.setString(2, employ.getTenNV());
                stmt.setDate(3, employ.getNgaySinh());
                stmt.setString(4, employ.getSDT());
                stmt.setString(5, employ.getEmail());
                stmt.setString(6, employ.getChucVu());
                stmt.setBigDecimal(7, employ.getLuong());

                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;
            } 
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm nhân viên: " + ex.getMessage());
            return false;
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public boolean updateEmployee(Employee employ){
        String sql = "UPDATE NhanVien SET TenNV = ?, NgaySinh = ?, SDT = ?, Email = ?, "
                + "ChucVu = ?, Luong = ? WHERE MaNV = ?";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql)){
                stmt.setString(1, employ.getTenNV());
                stmt.setDate(2, employ.getNgaySinh());
                stmt.setString(3, employ.getSDT());
                stmt.setString(4, employ.getEmail());
                stmt.setString(5, employ.getChucVu());
                stmt.setBigDecimal(6, employ.getLuong());
                stmt.setString(7, employ.getMaNV());
            
                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật thông tin: " + ex.getMessage());
            return false;
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public boolean deleteEmployee(String employId){
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql)){
                stmt.setString(1, employId);

                int rowsDeleted = stmt.executeUpdate();
                return rowsDeleted > 0;
            } 
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa dụng cụ: " + ex.getMessage());
            return false;
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(EmployeeDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
