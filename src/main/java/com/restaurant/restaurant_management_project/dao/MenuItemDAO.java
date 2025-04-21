package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.database.DatabaseConnection;
import com.restaurant.restaurant_management_project.model.MenuItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;

/**
 *
 * @author admin
 */
public class MenuItemDAO {
    public List<MenuItem> GetAllMenuItem(){
        List<MenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT * FROM MonAn";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                MenuItem item = new MenuItem();
                item.setCode(rs.getString("MaMon"));
                item.setName(rs.getString("TenMon"));
                byte[] imageBytes = rs.getBytes("HinhAnh");
                if(imageBytes != null){
                    Image image = convertBytesToImage(imageBytes);
                    item.setImage(image);
                }
                item.setPrice(rs.getBigDecimal("Gia"));
                item.setVat(rs.getFloat("VAT"));
                item.setUnit(rs.getString("DonVi"));
                item.setCategory(rs.getString("Loai"));
                
                String sideDishCode = rs.getString("side_dish_code");
                if (sideDishCode != null && !rs.wasNull()) {
                    MenuItem sideDish = new MenuItem();
                    sideDish.setCode(sideDishCode);
                    item.setSideDish(sideDish);
                }
                item.setStatus(rs.getBoolean("TrangThai"));
                
                
                menuItems.add(item);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy danh sách món ăn: " + ex.getMessage());
        }
        
        return menuItems;
    }
    public boolean addItem(MenuItem item){
        String sql = "INSERT INTO MonAn (MaMon, TenMon, HinhAnh, Gia, VAT, DonVi, "
                + "Loai, MonAnKem, TrangThai) VALUES (?,?,?,?,?,?,?,?,?)";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            
            stmt.setString(1, item.getCode());
            stmt.setString(2, item.getName());
            byte[] imageBytes = convertImageToBytes(item.getImage());
            if(imageBytes != null){
                stmt.setBytes(3, imageBytes);
            } else{
                stmt.setNull(3, Types.BLOB);
            }
            
            stmt.setBigDecimal(4, item.getPrice());
            stmt.setFloat(5, item.getVat());
            stmt.setString(6, item.getUnit());
            stmt.setString(7, item.getCategory());
            if(item.getSideDish() != null){
                stmt.setString(8, item.getSideDish().getCode());
            } else{
                stmt.setNull(8, Types.VARCHAR);
            }
            stmt.setBoolean(9, item.isStatus());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm món ăn: " + ex.getMessage());
            return false;
        }
    }
    
    public boolean updateItem(MenuItem item){
        String sql = "UPDATE MonAn SET TenMon = ?, HinhAnh = ?, Gia = ?, VAT = ?,"
                + "DonVi = ?, Loai = ?, MonAnKem = ?, TinhTrang = ? WHERE MaMon = ?";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            
            stmt.setString(1, item.getName());
            byte[] imageBytes = convertImageToBytes(item.getImage());
            if(imageBytes != null){
                stmt.setBytes(2, imageBytes);
            } else{
                stmt.setNull(2, Types.BLOB);
            }
            
            stmt.setBigDecimal(3, item.getPrice());
            stmt.setFloat(4, item.getVat());
            stmt.setString(5, item.getUnit());
            stmt.setString(6, item.getCategory());
            if(item.getSideDish() != null){
                stmt.setString(7, item.getSideDish().getCode());
            } else{
                stmt.setNull(7, Types.VARCHAR);
            }
            stmt.setBoolean(8, item.isStatus());
            stmt.setString(9, item.getCode());


            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException ex) {
            System.err.println("Lỗi khi sửa món ăn: " + ex.getMessage());
            return false;
        }
    }
    public boolean deleteItem(String itemId){
        String sql = "DELETE FROM MonAn WHERE MaMon = ?";
        try(Connection connection = DatabaseConnection.GetConnection();
            PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, itemId);
            
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa dụng cụ: " + ex.getMessage());
            return false;        
        }
    }
        
    private byte[] convertImageToBytes(Image image) {
        if (image == null) {
            return null;
        }
        // Lấy URL của image để đọc dữ liệu
        String url = image.getUrl();
        if (url == null) {
            return null;
        }
        try {
            java.net.URL imageUrl = new java.net.URL(url);
            try (InputStream in = imageUrl.openStream()) {
                return in.readAllBytes();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private Image convertBytesToImage(byte[] imageBytes) {
        if (imageBytes == null) {
            return null;
        }
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            return new Image(bis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
