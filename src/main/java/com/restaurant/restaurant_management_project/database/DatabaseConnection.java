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
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=RM_Db;encrypt=true;trustServerCertificate=true";
    private static Connection connection = null;
    
    public static Connection GetConnection() throws SQLException{
            try{
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection(URL,"nhahang_admin","abc123!@#");
                System.out.println("Successfully access to SQL Server! :D");
            } catch (ClassNotFoundException ex) {
                System.err.println("Cannot found SQL Server JDBC Driver:: " + ex.getMessage());
                throw new SQLException("Cannot found SQL Server JDBC Driver:: ", ex);
            } catch(SQLException e){
                System.err.println("Connection error to SQL Server:: " + e.getMessage());
                throw e;
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
