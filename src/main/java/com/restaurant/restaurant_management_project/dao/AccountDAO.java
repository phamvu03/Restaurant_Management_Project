package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.database.ConnectionPool;
import com.restaurant.restaurant_management_project.database.DatabaseConnection;
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
// 1. Lấy toàn bộ tài khoản
public class AccountDAO {
    public List<Account> getAllAccounts(){
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
                    accounts.add(detail);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy danh sách tài khoản: " + ex.getMessage());
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return accounts;
    }
    // 5. Tìm kiếm theo tên
    public List<Account> timKiemTaiKhoan(String keyword) {
        List<Account> result = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan WHERE TenTK LIKE ? OR MaNV LIKE ?";
        Connection connection = null;

        try {
            connection = ConnectionPool.getInstance().getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                String wildcard = "%" + keyword + "%";
                stmt.setString(1, wildcard);
                stmt.setString(2, wildcard);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        result.add(new Account(
                                rs.getString("MaNV"),
                                rs.getString("TenTK"),
                                rs.getString("MatKhau")
                        ));
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi tìm kiếm tài khoản: " + ex.getMessage());
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }



    public boolean addAccount(Account acc){
        if (isUsernameExists(acc.getTenTK())) {
            System.err.println("Tên tài khoản đã tồn tại!");
            return false;
        }

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
            System.err.println("Lỗi khi thêm tài khoản: " + ex.getMessage());
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

        String sql = "UPDATE TaiKhoan SET TenTK = ?, MatKhau = ? WHERE MaNV = ? ";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql)){

                stmt.setString(1, acc.getTenTK());
                stmt.setString(2, acc.getMatKhau());
                stmt.setString(3, acc.getMaNV());


                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi sửa mật khẩu: " + ex.getMessage());
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
        String sql = "DELETE FROM TaiKhoan WHERE MaNV = ?";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql)){

                stmt.setString(1, employId);

                int rowsDeleted = stmt.executeUpdate();
                return rowsDeleted > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa nhân viên: " + ex.getMessage());
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
        String sql = "SELECT * FROM TaiKhoan WHERE TenTK = ? AND MatKhau = ?";
        System.out.println("Bat dau");
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
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
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(AccountDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public boolean isUsernameExists(String tenTK) {
        String sql = "SELECT 1 FROM TaiKhoan WHERE TenTK = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenTK);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi kiểm tra tên tài khoản: " + ex.getMessage());
            return false;
        }
    }

}
