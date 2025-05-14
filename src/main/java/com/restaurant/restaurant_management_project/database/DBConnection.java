package com.restaurant.restaurant_management_project.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * DBConnection: lớp quản lý cấu hình và tạo kết nối JDBC đến SQL Server
 * Cấu hình lấy từ file db.properties nằm trong resources
 */
public class DBConnection {
    private static final String PROPERTIES_FILE = "db.properties";
    private static String url;
    private static String user;
    private static String password;
    private static String driverClass;

    static {
        // Load cấu hình từ file db.properties
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            Properties props = new Properties();
            if (in == null) {
                throw new RuntimeException("Không tìm thấy file " + PROPERTIES_FILE);
            }
            props.load(in);
            driverClass = props.getProperty("jdbc.driverClass");
            url         = props.getProperty("jdbc.url");
            user        = props.getProperty("jdbc.user");
            password    = props.getProperty("jdbc.password");

            // Load driver
            Class.forName(driverClass);
            System.out.println("JDBC Driver loaded: " + driverClass);
        } catch (IOException | ClassNotFoundException ex) {
            throw new ExceptionInInitializerError("Lỗi khởi tạo DBConnection: " + ex.getMessage());
        }
    }

    /**
     * Trả về Connection mới. Caller nhớ gọi close() khi xong.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
