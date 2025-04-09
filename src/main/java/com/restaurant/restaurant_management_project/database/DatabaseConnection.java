/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.restaurant.restaurant_management_project.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=RM_Db;username=sa;password=123456;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
    private static Connection connection = null;
    
    public static Connection GetConnection() throws SQLException{
        if(connection == null || connection.isClosed()){
            try{
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL);
                System.out.println("Kết nối thành công tới SQL Server!");
            } catch (ClassNotFoundException ex) {
                System.err.println("Không tìm thấy SQL Server JDBC Driver" + ex.getMessage());
                throw new SQLException("Không tìm thấy SQL Server JDBC Driver", ex);
            } catch(SQLException e){
                System.err.println("Lỗi kết nối SQL Server: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    public static void closeConnection(){
        if(connection == null){
            try {
                connection.close();
                System.out.println("Đã đóng kết nối");
            } catch (SQLException ex) {
                System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
            }
        } 
    }
}
