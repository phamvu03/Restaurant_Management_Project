package com.restaurant.restaurant_management_project.database;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("Kết nối thành công đến cơ sở dữ liệu!");
        } catch (SQLException ex) {
            System.err.println("Kết nối thất bại: " + ex.getMessage());
        }
    }
}

