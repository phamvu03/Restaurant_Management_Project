package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.database.DatabaseConnection;
import com.restaurant.restaurant_management_project.model.OrderDetail;
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
public class OrderDetailDAO {
    public List<OrderDetail> GetAllEquipment(){
        List<OrderDetail> orderDetails = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietDonHang";
        
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                OrderDetail detail = new OrderDetail();
                detail.setMaDonHang(rs.getString("MaDonHang"));
                detail.setMaMon(rs.getString("MaMon"));
                detail.setSoLuong(rs.getInt("SoLuong"));
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy danh sách chi tiết đơn hàng: " + ex.getMessage());
        }
        return orderDetails;
    }
    public boolean addOrderDetail(OrderDetail detail){
        String sql = "INSERT INTO ChiTietDonHang (MaDonHang, MaMon, SoLuong) "
                + "VALUES (?,?,?)";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, detail.getMaDonHang());
            stmt.setString(2, detail.getMaMon());
            stmt.setInt(3, detail.getSoLuong());
            
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm đơn hàng: " + ex.getMessage());
            return false;        
        }
    }
    public boolean updateOrderDetail(OrderDetail detail){
        String sql = "UPDATE ChiTietDonHang "
                    + "SET SoLuong = ?) "
                    + "WHERE MaDonHang = ? AND MaMon = ?";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, detail.getSoLuong());
            stmt.setString(2, detail.getMaDonHang());
            stmt.setString(3, detail.getMaMon());
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException ex) {
            System.err.println("Lỗi khi sửa món trong đơn hàng: " + ex.getMessage());
            return false;        
        }
    }
    public boolean deleteOrderDetail(String orderId, String itemId){
        String sql = "DELETE FROM ChiTietDonHang "
                    + "WHERE MaDonHang = ? AND  MaMon = ?";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, orderId);
            stmt.setString(2, itemId);
            
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa món trong đơn hàng: " + ex.getMessage());
            return false;
        }
    }
}
