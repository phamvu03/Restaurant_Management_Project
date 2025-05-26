package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.database.ConnectionPool;
import com.restaurant.restaurant_management_project.model.RMenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuItemDaoImpl implements MenuItemDao {

    public MenuItemDaoImpl() {
    }

    public int getNextSequenceValue(String sequenceName) throws SQLException {
        String sql = "SELECT NEXT VALUE FOR " + sequenceName;
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Could not retrieve next value from sequence: " + sequenceName);
            }
        }
    }

    @Override
    public List<RMenuItem> getAll() {
        return getAll(0, Integer.MAX_VALUE);
    }

    @Override
    public List<RMenuItem> getAll(int offset, int limit) {
        List<RMenuItem> menuItems = new ArrayList<>();
        String sql = "SELECT id, MaMon, TenMon, HinhAnh, Gia, DonVi, Nhom, MonAnKem, TrangThai FROM MonAn ORDER BY Id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, offset);
                stmt.setInt(2, limit);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        RMenuItem menuItem = new RMenuItem(
                            rs.getString("TenMon"),
                            rs.getBoolean("TrangThai"),
                            rs.getBigDecimal("Gia"),
                            rs.getString("Nhom"),
                            rs.getBytes("HinhAnh"),
                            rs.getString("DonVi"),
                            rs.getInt("MonAnKem") != 0 ? rs.getInt("MonAnKem") : null
                        );
                        menuItem.setId(rs.getInt("id"));
                        menuItem.setItemId(rs.getString("MaMon"));
                        menuItems.add(menuItem);
                    }
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(MenuItemDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return menuItems;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM MonAn";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                   return resultSet.getInt(1);
                }
            } 
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm số lượng món ăn: " + e.getMessage(), e);
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(MenuItemDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return 0;
    }

    @Override
    public boolean add(RMenuItem item) {
        String sql = "INSERT INTO MonAn (MaMon,TenMon,TrangThai,Gia,Nhom,HinhAnh,DonVi,MonAnKem)" +
                "VALUES (?, ?, ?,?,?,?,?,?);";
        int result;
        Connection connection = null;
        ResultSet resultSet;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
                String itemId = "MA" + this.getNextSequenceValue("MID_seq");
                preparedStatement.setString(1,itemId);
                preparedStatement.setString(2, item.getItemName());
                preparedStatement.setBoolean(3, item.isItemStatus());
                preparedStatement.setBigDecimal(4, item.getItemPrice());
                preparedStatement.setString(5, item.getItemCategory());
                preparedStatement.setBytes(6, item.getItemImage());
                preparedStatement.setString(7, item.getItemUnit());
                if (item.getSideItem() == null)
                {
                    preparedStatement.setNull(8, Types.INTEGER);
                }
                else preparedStatement.setInt(8, item.getSideItem());
                result = preparedStatement.executeUpdate();
                if(result>0)
                {
                    resultSet = preparedStatement.getGeneratedKeys();
                    if(resultSet.next())
                    {
                        int id = resultSet.getInt(1);
                        item.setItemId(itemId);
                        item.setId(id);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(MenuItemDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result > 0;
    }

    @Override
    public boolean update(RMenuItem item) {
        String sql = "UPDATE MonAn " +
                "SET TenMon = ?, TrangThai = ?, Gia = ?, Nhom = ?, HinhAnh = ?, DonVi = ?, MonAnKem = ? " +
                "WHERE Id = ?;";
        Connection connection = null;
        try{
            connection = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
                preparedStatement.setString(1, item.getItemName());
                preparedStatement.setBoolean(2, item.isItemStatus());
                preparedStatement.setBigDecimal(3, item.getItemPrice());
                preparedStatement.setString(4, item.getItemCategory());
                preparedStatement.setBytes(5, item.getItemImage());
                preparedStatement.setString(6, item.getItemUnit());
                if (item.getSideItem() == null)
                {
                    preparedStatement.setNull(7, Types.INTEGER);
                }
                else preparedStatement.setInt(7, item.getSideItem());
                preparedStatement.setInt(8, item.getId());

                return exeUpdate(preparedStatement,connection);
            }
        }
         catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ConnectionPool.getInstance().releaseConnection(connection);
            } catch (SQLException ex) {
                Logger.getLogger(MenuItemDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
public boolean delete(RMenuItem item) {
    String sqlDelete = "DELETE FROM MonAn WHERE Id=?";
    String sqlUpdate = "UPDATE dbo.MonAn SET MonAnKem = NULL WHERE MonAnKem = ?";
    Connection connection = null;

    try {
        connection = ConnectionPool.getInstance().getConnection();

        // 1. Update các bản ghi liên quan
        PreparedStatement updateStmt = connection.prepareStatement(sqlUpdate);
        updateStmt.setInt(1, item.getId());
        exeUpdate(updateStmt, connection);

        // 2. Xóa món ăn chính
        PreparedStatement deleteStmt = connection.prepareStatement(sqlDelete);
        deleteStmt.setInt(1, item.getId());
        return exeUpdate(deleteStmt, connection);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } finally {
        try {
            ConnectionPool.getInstance().releaseConnection(connection);
        } catch (SQLException ex) {
            Logger.getLogger(MenuItemDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

    private boolean exeUpdate(PreparedStatement pre,Connection connection)
    {
        try {
            connection.setAutoCommit(false);
            int result = pre.executeUpdate();
            if(result>0)
            {
                connection.commit();
                return true;
            }else {
                connection.rollback();
                return false;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }


}
