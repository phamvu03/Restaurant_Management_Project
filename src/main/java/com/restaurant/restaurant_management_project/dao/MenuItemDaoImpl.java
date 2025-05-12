package com.restaurant.restaurant_management_project.dao;



import com.restaurant.restaurant_management_project.model.RMenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.restaurant.restaurant_management_project.database.DatabaseConnection.GetConnection;
import static com.restaurant.restaurant_management_project.database.DatabaseConnection.closeConnection;

public class MenuItemDaoImpl implements MenuItemDao {

    public MenuItemDaoImpl() {
    }

    public int getNextSequenceValue(String sequenceName) throws SQLException {
        String sql = "SELECT NEXT VALUE FOR " + sequenceName;
        try (Connection connection = GetConnection();
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
        String sql = "SELECT Id, Item_id, Item_name, Item_status, Item_price, Item_category, Item_unit, Side_item, Item_image FROM MenuItem ORDER BY Id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try (Connection conn = GetConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, offset);
            stmt.setInt(2, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RMenuItem menuItem = new RMenuItem(
                        rs.getString("Item_name"),
                        rs.getBoolean("Item_status"),
                        rs.getBigDecimal("Item_price"),
                        rs.getString("Item_category"),
                        rs.getBytes("Item_image"),
                        rs.getString("Item_unit"),
                        rs.getInt("Side_item") != 0 ? rs.getInt("Side_item") : null
                    );
                    menuItem.setId(rs.getInt("Id"));
                    menuItem.setItemId(rs.getString("Item_id"));
                    menuItems.add(menuItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM MenuItem";
        try (Connection connection = GetConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm số lượng món ăn: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public boolean add(RMenuItem item) {
        String sql = "INSERT INTO menuItem (Item_id,Item_name,Item_status,Item_price,Item_category,Item_image,Item_unit,Side_item,Item_quantity_sold)" +
                "VALUES (?, ?, ?,?,?,?,?,?,?);";
        int result;
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            connection = GetConnection();
            preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
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
            closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result > 0;
    }

    @Override
    public boolean update(RMenuItem item) {
        String sql = "UPDATE menuItem " +
                "SET Item_name = ?, Item_status = ?, Item_price = ?, Item_category = ?, Item_image = ?, Item_unit = ?, Side_item = ?, Item_quantity_sold = ? " +
                "WHERE Id = ?;";
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            connection = GetConnection();
            preparedStatement = connection.prepareStatement(sql);
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
            preparedStatement.setInt(9, item.getId());

            return exeUpdate(preparedStatement,connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(RMenuItem item) {
        String sql = "DELETE FROM menuItem WHERE Id=?";
        String sqlUpdate = "UPDATE dbo.menuItem SET Side_item = NULL WHERE Side_item = ?";
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            //Thay đổi các bản ghi có khóa phụ tương ứng với bản ghi bị xóa
            connection = GetConnection();
            preparedStatement = connection.prepareStatement(sqlUpdate);
            preparedStatement.setInt(1,item.getId());
            exeUpdate(preparedStatement,connection);
            //xóa và trả về kết quả
            connection = GetConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,item.getId());
            return exeUpdate(preparedStatement,connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        finally {
            closeConnection();
        }
    }


}
