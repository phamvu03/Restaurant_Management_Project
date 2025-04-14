package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.database.DatabaseConnection;
import com.restaurant.restaurant_management_project.model.Reservation;
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
public class ReservationDAO {
    public List<Reservation> GetAllReservation(){
        String sql = "SELECT * FROM DatBan";
        List<Reservation> reservatrions = new ArrayList<>();
        
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                Reservation reserv = new Reservation();
                reserv.setMaDatBan(rs.getString("MaDatBan"));
                reserv.setMaBan(rs.getString("MaBan"));
                reserv.setTenKhachDaiDien(rs.getString("TenKhachDaiDien"));
                reserv.setSoKhach(rs.getInt("SoKhach"));
                reserv.setSDTKhach(rs.getString("SDTKhach"));
                reserv.setEmail(rs.getString("Email"));
                reserv.setThoiGianHen(rs.getDate("ThoiGianHen"));
                reserv.setTrangThai(rs.getString("TrangThai"));
                reserv.setGhiChu(rs.getString("GhiChu"));

                reservatrions.add(reserv);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy danh sách đặt bàn: " + ex.getMessage());           
        }
        return  reservatrions;
    }   
    public boolean addReservation(Reservation reserv){
        String sql = "INSERT INTO DatBan (MaDatBan, MaBan, TenKhachDaiDien, SoKhach, "
                + "SDTKhach, Email, ThoiGianHen, TrangThai, GhiChu) VALUES (?,?,?,?,?,?,?,?,?)";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, reserv.getMaDatBan());
            stmt.setString(2, reserv.getMaBan());
            stmt.setString(3, reserv.getTenKhachDaiDien());
            stmt.setInt(4, reserv.getSoKhach());
            stmt.setString(5, reserv.getSDTKhach());
            stmt.setString(6, reserv.getEmail());
            stmt.setDate(7, reserv.getThoiGianHen());
            stmt.setString(8, reserv.getTrangThai());
            stmt.setString(9, reserv.getGhiChu());
            
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException ex) {
            System.err.println("Loi " + ex.getMessage());
            return false;
        }
    }
    public boolean updateReservation(Reservation reserv){
        String sql = "UPDATE DatBan SET TenKhachDaiDien = ?, SoKhach = ?, SDTKhach = ?,"
                + "Email = ?, ThoiGianHen = ?, TrangThai = ?, GhiChu = ? WHERE MaDatBan = ?";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, reserv.getTenKhachDaiDien());
            stmt.setInt(2, reserv.getSoKhach());
            stmt.setString(3, reserv.getSDTKhach());
            stmt.setString(4, reserv.getEmail());
            stmt.setDate(5, reserv.getThoiGianHen());
            stmt.setString(6, reserv.getTrangThai());
            stmt.setString(7, reserv.getGhiChu());
            stmt.setString(8, reserv.getMaDatBan());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật thông tin: " + ex.getMessage());
            return false;
        }
    }
    public boolean deleteReservation(String reservId){
        String sql = "DELETE FROM DatBan WHERE MaDatBan = ?";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, reservId);
            
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa ban: " + ex.getMessage());
            return false;        
        }
    }
}
