package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.database.DatabaseConnection;
import com.restaurant.restaurant_management_project.model.Table;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class TableDAO {
    public List<Table> GetAllTable(){
        List<Table> tables = new ArrayList<>();
        String sql = "SELECT * FROM Ban";
        Connection connection = null;
        try{
            connection = DatabaseConnection.getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()){
                    while(rs.next()){
                        Table table = new Table();
                        table.setMaBan(rs.getString("MaBan"));
                        table.setSoGhe(rs.getInt("SoGhe"));
                        table.setGhiChu(rs.getString("GhiChu"));

                        tables.add(table);
                    }
            }
        }catch (SQLException ex) {
            System.err.println("Lỗi khi lấy danh sách người dùng: " + ex.getMessage());
        }finally {
            DatabaseConnection.releaseConnection(connection);
        }
        return tables;
    }
    public boolean addTable(Table table){
        String sql = "INSERT INTO Ban (MaBan, SoGhe, GhiChu) VALUES (?,?,?)";
        Connection connection = null;
        try{
            connection = DatabaseConnection.getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql)){
                stmt.setString(1, table.getMaBan());
                stmt.setInt(2, table.getSoGhe());
                stmt.setString(3, table.getGhiChu());

                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm bàn: " + ex.getMessage());
            return false;
        }finally {
            DatabaseConnection.releaseConnection(connection);
        }
    }
    public boolean updateTable(Table table){
        String sql = "UPDATE Ban SET SoGhe = ?, GhiChu = ? WHERE MaBan = ?";
        Connection connection = null;
        try{
            connection = DatabaseConnection.getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql)){
                stmt.setInt(1, table.getSoGhe());
                stmt.setString(2, table.getGhiChu());
                stmt.setString(3, table.getMaBan());

                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;
            }
        }catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật thông tin: " + ex.getMessage());
            return false;
        }finally {
            DatabaseConnection.releaseConnection(connection);
        }
    }
    public boolean deleteTable(String tableId){
        String sql = "DELETE FROM Ban WHERE MaBan = ?";
        Connection connection = null;
        try{
            connection = DatabaseConnection.getConnection();
            try(PreparedStatement stmt = connection.prepareStatement(sql)){

                stmt.setString(1, tableId);

                int rowsDeleted = stmt.executeUpdate();
                return rowsDeleted > 0;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa bàn: " + ex.getMessage());
            return false;
        } finally {
            DatabaseConnection.releaseConnection(connection);
        }
    }
}
