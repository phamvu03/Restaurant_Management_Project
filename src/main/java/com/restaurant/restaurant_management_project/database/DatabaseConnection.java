package com.restaurant.restaurant_management_project.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://localhost;databaseName=RM_Db;encrypt=true;trustServerCertificate=true";

    private static final int MAX_POOL_SIZE = 20; 
    private static final int TIMEOUT_SECONDS = 5;

    private static final LinkedList<Connection> pool = new LinkedList<>();

    static {
        try {
            for (int i = 0; i < MAX_POOL_SIZE; i++) {
                Connection conn = DriverManager.getConnection(URL,"nhahang_admin","abc123!@#");
                pool.add(conn);
            }
            System.out.println("Connection Pool initialized with " + pool.size() 
                    + " connections. (max: " + MAX_POOL_SIZE +  ")");
        } catch (SQLException e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
        }
    }

    public synchronized static Connection getConnection() throws SQLException {
        if (pool.isEmpty()) {
            throw new SQLException("No available connections in the pool.");
        }
        return pool.removeFirst();
    }

    public synchronized static void releaseConnection(Connection conn) {
        if (conn != null) {
            pool.addLast(conn);
        }
    }

    public synchronized static void shutdown() {
        try {
            for (Connection conn : pool) {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            }
            pool.clear();
            System.out.println("Connection pool has been shutdown.");
        } catch (SQLException e) {
            System.err.println("Error shutting down connection pool: " + e.getMessage());
        }
    }
}
