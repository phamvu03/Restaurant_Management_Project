package com.restaurant.restaurant_management_project.dao;
import com.restaurant.restaurant_management_project.database.DatabaseConnection;
import com.restaurant.restaurant_management_project.model.Equipment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class EquipmentDAO {
    public List<Equipment> GetAllEquipment(){
        List<Equipment> equipments = new ArrayList<>();
        String sql = "SELECT * FROM DungCu";
        
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                Equipment equip = new Equipment();
                equip.setMaDungCu(rs.getString("MaDungCu"));
                equip.setTenDungCu(rs.getString("TenDungCu"));
                equip.setLoai(rs.getString("Loai"));
                equip.setSoLuong(rs.getInt("SoLuong"));
                equip.setTinhTrang(rs.getString("TinhTrang"));
                equip.setNgayThongKe(rs.getDate("NgayThongKe"));

                equipments.add(equip);
            }
            
        }catch(SQLException ex){
            System.err.println("Lỗi khi lấy danh sách người dùng: " + ex.getMessage());
        }
        return equipments; 
    }
    public boolean addEquipment(Equipment equip){
        String sql = "INSERT INTO DungCu (MaDungCu, TenDungCu, Loai, SoLuong, TinhTrang,"
                + "NgayThongKe) VALUES (?,?,?,?,?,?)";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, equip.getMaDungCu());
            stmt.setString(2, equip.getTenDungCu());
            stmt.setString(3, equip.getLoai());
            stmt.setInt(4, equip.getSoLuong());
            stmt.setString(5, equip.getTinhTrang());
            stmt.setDate(6, equip.getNgayThongKe());
            
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm dụng cụ: " + ex.getMessage());
            return false;
        }
    }
    public boolean updateEquipment(Equipment equip){
        String sql = "UPDATE DungCu SET TenDungCu = ?, Loai = ?, SoLuong = ?,"
                + "TinhTrang = ?, NgayThongKe = ? WHERE MaDungCu = ?";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, equip.getTenDungCu());
            stmt.setString(2, equip.getLoai());
            stmt.setInt(3, equip.getSoLuong());
            stmt.setString(4, equip.getTinhTrang());
            stmt.setDate(5, equip.getNgayThongKe());
            stmt.setString(6, equip.getMaDungCu());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật thông tin: " + ex.getMessage());
            return false;
        }
    }   
    public boolean deleteEquipment(String equipId){
        String sql = "DELETE FROM DungCu WHERE MaDungCu = ?";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            
            stmt.setString(1, equipId);
            
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa dụng cụ: " + ex.getMessage());
            return false;
        }
    }
}
