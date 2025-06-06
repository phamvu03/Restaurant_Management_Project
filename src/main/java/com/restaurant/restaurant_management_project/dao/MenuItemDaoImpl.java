package com.restaurant.restaurant_management_project.dao;

import com.restaurant.restaurant_management_project.database.ConnectionPool;
import com.restaurant.restaurant_management_project.model.RMenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuItemDaoImpl implements MenuItemDao {
    private ConnectionPool connectionPool;
    public MenuItemDaoImpl() {
        try {
            connectionPool = ConnectionPool.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getNextSequenceValue(String sequenceName) throws SQLException {
        String sql = "SELECT NEXT VALUE FOR " + sequenceName;
        Connection conn = null;

        try {
            conn = connectionPool.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Could not retrieve next value from sequence: " + sequenceName);
            }
        }finally {
            connectionPool.releaseConnection(conn);
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
        Connection conn = null;
        try{
            conn = connectionPool.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, offset);
                stmt.setInt(2, limit);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        RMenuItem item = extractMenuItemFromResultSet(rs);
                        menuItems.add(item);
                    }
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            connectionPool.releaseConnection(conn);
        }
        return menuItems;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM MonAn";
        Connection conn = null;
        try{
            conn = connectionPool.getConnection();
            try (PreparedStatement preparedStatement = conn.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                   return resultSet.getInt(1);
                }
            } 
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm số lượng món ăn: " + e.getMessage(), e);
        } finally {

                connectionPool.releaseConnection(conn);
        }
        return 0;
    }

    @Override
    public boolean add(RMenuItem item) {
        String sql = "INSERT INTO MonAn (MaMon,TenMon,TrangThai,Gia,Nhom,HinhAnh,DonVi,MonAnKem)" +
                "VALUES (?, ?, ?,?,?,?,?,?);";
        int result;
        Connection conn = null;
        ResultSet resultSet;
        try{
            conn = connectionPool.getConnection();
            try(PreparedStatement preparedStatement = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
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
            connectionPool.releaseConnection(conn);
        }
        return result > 0;
    }

    @Override
    public boolean update(RMenuItem item) {
        String sql = "UPDATE MonAn " +
                "SET TenMon = ?, TrangThai = ?, Gia = ?, Nhom = ?, HinhAnh = ?, DonVi = ?, MonAnKem = ? " +
                "WHERE Id = ?;";
        Connection conn = null;
        try{
            conn = ConnectionPool.getInstance().getConnection();
            try(PreparedStatement preparedStatement = conn.prepareStatement(sql);) {
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

                return exeUpdate(preparedStatement,conn);
            }
        }
         catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
                connectionPool.releaseConnection(conn);
        }
    }

    @Override
public boolean delete(RMenuItem item) {
    String sqlDelete = "DELETE FROM MonAn WHERE Id=?";
    String sqlUpdate = "UPDATE dbo.MonAn SET MonAnKem = NULL WHERE MonAnKem = ?";
        Connection conn = null;

    try {
        conn = ConnectionPool.getInstance().getConnection();

        // 1. Update các bản ghi liên quan
        PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate);
        updateStmt.setInt(1, item.getId());
        exeUpdate(updateStmt, conn);

        // 2. Xóa món ăn chính
        PreparedStatement deleteStmt = conn.prepareStatement(sqlDelete);
        deleteStmt.setInt(1, item.getId());
        return exeUpdate(deleteStmt, conn);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } finally {
            connectionPool.releaseConnection(conn);
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
    public List<RMenuItem> getPopularMenuItems(int limit, java.util.Date startDate, java.util.Date endDate) {
        List<RMenuItem> menuItems = new ArrayList<>();
        Connection conn = null;
        String sql = "SELECT TOP(?)ma.id, ma.MaMon, ma.TenMon, ma.TrangThai, ma.Gia, ma.Nhom, ma.HinhAnh, ma.DonVi, ma.MonAnKem, " +
                "SUM(ctdh.SoLuong) AS TotalSold " +
                "FROM MonAn ma " +
                "JOIN ChiTietDonHang ctdh ON ma.id = ctdh.MaMon " +
                "JOIN DonHang dh ON ctdh.MaDonHang = dh.MaDonHang " +
                "WHERE dh.ThoiGianThanhToan BETWEEN ? AND ? " +
                "GROUP BY ma.id, ma.MaMon, ma.TenMon, ma.TrangThai, ma.Gia, ma.Nhom, ma.HinhAnh, ma.DonVi, ma.MonAnKem " +
                "ORDER BY SUM(ctdh.SoLuong) DESC";

        try {
            conn = connectionPool.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Set date range parameters
            java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
            pstmt.setInt(1, limit);
            pstmt.setDate(2, sqlStartDate);
            pstmt.setDate(3, sqlEndDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RMenuItem item = extractMenuItemFromResultSet(rs);
                    menuItems.add(item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            connectionPool.releaseConnection(conn);
        }

        return menuItems;
    }
    public int getFoodSalesQuantity(int foodId, Date startDate, Date endDate) {
        int totalSold = 0;

        // SQL Server query to get sales quantity of a specific food item
        String sql = "SELECT SUM(ctdh.SoLuong) AS TotalSold " +
                "FROM ChiTietDonHang ctdh " +
                "JOIN DonHang dh ON ctdh.MaDonHang = dh.MaDonHang " +
                "WHERE ctdh.MaMon = ? AND dh.ThoiGianThanhToan BETWEEN ? AND ?";
        Connection conn = null;;
        try
        {
            conn = connectionPool.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, foodId);
            pstmt.setDate(2, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(3, new java.sql.Date(endDate.getTime()));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalSold = rs.getInt("TotalSold");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            connectionPool.releaseConnection(conn);
        }

        return totalSold;
    }
    private RMenuItem extractMenuItemFromResultSet(ResultSet rs) throws SQLException {

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

        return menuItem;
    }
}
