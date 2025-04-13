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
    
}
