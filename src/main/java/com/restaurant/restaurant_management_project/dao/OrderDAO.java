package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.database.ConnectionPool;
import com.restaurant.restaurant_management_project.model.Order;
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
public class OrderDAO {
    public List<Order> getAllOrder(){
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM DonHang";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    Order order = new Order();
                    order.setMaDonHang(rs.getString("MaDonHang"));
                    order.setMaDatBan(rs.getString("MaDatBan"));
                    order.setMaNV(rs.getString("MaNV"));
                    order.setThoiGianTao(rs.getDate("ThoiGianTao"));
                    order.setThoiGianThanhToan(rs.getDate("ThoiGianThanhToan"));

                    orders.add(order);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy danh sách đơn hàng: " + ex.getMessage());
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return orders;
    }
    public boolean addOrder(Order order){
        String sql = "INSERT INTO DonHang (MaDonHang, MaDatBan, MaNV, ThoiGianTao, "
                + "ThoiGianThanhToan) VALUES (?,?,?,?,?)";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql)){
                stmt.setString(1, order.getMaDonHang());
                stmt.setString(2, order.getMaDatBan());
                stmt.setString(3, order.getMaNV());
                stmt.setDate(4, order.getThoiGianTao());
                stmt.setDate(5, order.getThoiGianTao());

                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;
            } 
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm đơn hàng: " + ex.getMessage());
            return false;        
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public boolean deleteOrder(String orderId){
        String sql = "DELETE FROM DonHang WHERE MaDonHang = ?";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            
            try(PreparedStatement stmt = connection.prepareStatement(sql)){
                stmt.setString(1, orderId);

                int rowsDeleted = stmt.executeUpdate();
                return rowsDeleted > 0;
            } 
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa đơn hàng: " + ex.getMessage());
            return false;
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public boolean updateOrderPaidAt(String orderId, String orderPaidAt){
        String sql = "UPDATE DonHang SET ThoiGianThanhToan = ? WHERE MaDonHang = ?";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();

            try(PreparedStatement stmt = connection.prepareStatement(sql)){
                stmt.setString(1, orderPaidAt);
                stmt.setString(2, orderId);

                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa đơn hàng: " + ex.getMessage());
            return false;
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
