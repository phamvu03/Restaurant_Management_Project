package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.database.ConnectionPool;
import com.restaurant.restaurant_management_project.model.Account;
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
public class AccountDAO {
    public List<Account> GetAllEquipment(){
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    Account detail = new Account();
                    detail.setMaNV(rs.getString("MaNV"));
                    detail.setTenTK(rs.getString("TenTK"));
                    detail.setMatKhau(rs.getString("MatKhau"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy danh sách tai khoan: " + ex.getMessage());
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return accounts;
    }
    public boolean addAccount(Account acc){
        String sql = "INSERT INTO TaiKhoan (MaNV, TenTK, MatKhau) "
                + "VALUES (?,?,?)";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql)){

                stmt.setString(1, acc.getMaNV());
                stmt.setString(2, acc.getTenTK());
                stmt.setString(3, acc.getMatKhau());

                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm tai khoan: " + ex.getMessage());
            return false;        
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public boolean updateAccount(Account acc){
        String sql = "UPDATE TaiKhoan SET MatKhau = ?"
                    + "WHERE MaNV = ? AND TenTK = ?";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql)){

                stmt.setString(1, acc.getMatKhau());
                stmt.setString(2, acc.getMaNV());
                stmt.setString(3, acc.getTenTK());

                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi sửa mat khau: " + ex.getMessage());
            return false;
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public boolean deleteAccount(String employId, String accName){
        String sql = "DELTE FROM TaiKhoan WHERE MaNV = ?";
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
                Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public boolean checkAccount(String userName,String passWord)
    {
        String sql = "SELECT * " +
                "FROM TaiKhoan" +
                " WHERE TenTK = ? AND MatKhau = ?";
        System.out.println("Bat dau");
        Connection connection = null;
        try{
            connection = DatabaseConnection.getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql)){

                stmt.setString(1, userName);
                stmt.setString(2, passWord);

                ResultSet resultSet = stmt.executeQuery();
                return resultSet.next();
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi khi kiểm tra tài khoản! " + ex.getMessage());
            return false;
        } finally {
            DatabaseConnection.releaseConnection(connection);
        }
    }
}
