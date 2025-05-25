package com.restaurant.restaurant_management_project.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=RM_Db;integratedSecurity=true;encrypt=false;trustServerCertificate=true;";
    private static final int INITIAL_POOL_SIZE = 5; // Số lượng kết nối ban đầu
    private static final int MAX_POOL_SIZE = 10;   // Số lượng kết nối tối đa
    private static final long CONNECTION_TIMEOUT_MS = 5000; // Thời gian chờ lấy kết nối (5 giây)

    private final BlockingQueue<Connection> availableConnections;
    private final List<Connection> usedConnections; // Để theo dõi các kết nối đang được sử dụng
    private volatile boolean isShutdown = false; // Cờ để kiểm soát việc shutdown

    private static ConnectionPool instance; // Singleton instance

    // Private constructor để triển khai Singleton pattern
    private ConnectionPool() throws SQLException {
        availableConnections = new ArrayBlockingQueue<>(MAX_POOL_SIZE);
        usedConnections = new ArrayList<>();
        initializeConnectionPool();
    }

    // Phương thức để lấy instance của ConnectionPool (Singleton)
    public static synchronized ConnectionPool getInstance() throws SQLException {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    private void initializeConnectionPool() throws SQLException {
        System.out.println("Initializing connection pool...");
        try {
            // Đăng ký JDBC Driver (tự động từ Java 6 trở đi, nhưng không hại khi thêm)
            // Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
                Connection connection = createNewConnection();
                if (connection != null) {
                    availableConnections.offer(connection); // Thêm vào hàng đợi các kết nối khả dụng
                    System.out.println("Created new connection and added to pool: " + connection);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error initializing connection pool: " + e.getMessage());
            throw e; // Ném ngoại lệ để biết việc khởi tạo thất bại
        }
    }

    private Connection createNewConnection() throws SQLException {
        System.out.println("Attempting to create a new database connection...");
        try {
            Connection connection = DriverManager.getConnection(DB_URL);
            System.out.println("Successfully created a new connection: " + connection);
            return connection;
        } catch (SQLException e) {
            System.err.println("Failed to create new connection: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Lấy một kết nối từ pool.
     * Nếu pool trống, nó sẽ cố gắng tạo kết nối mới nếu chưa đạt MAX_POOL_SIZE.
     * Nếu pool đã đạt MAX_POOL_SIZE và không có kết nối nào khả dụng, nó sẽ chờ.
     *
     * @return Một đối tượng Connection.
     * @throws SQLException Nếu không thể lấy kết nối hoặc pool đã shutdown.
     */
    public synchronized Connection getConnection() throws SQLException {
        if (isShutdown) {
            throw new SQLException("Connection pool has been shut down.");
        }

        Connection connection = null;
        try {
            // Cố gắng lấy một kết nối từ pool các kết nối khả dụng
            connection = availableConnections.poll(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            if (connection == null) {
                // Nếu không có kết nối nào trong pool ngay lập tức
                if (usedConnections.size() + availableConnections.size() < MAX_POOL_SIZE) {
                    // Nếu tổng số kết nối (đã sử dụng + khả dụng) chưa đạt MAX_POOL_SIZE, tạo kết nối mới
                    System.out.println("Pool empty, creating new connection (current size: " + (usedConnections.size() + availableConnections.size()) + ")");
                    connection = createNewConnection();
                } else {
                    // Nếu pool đã đầy, chờ cho một kết nối được giải phóng
                    System.out.println("Pool is full, waiting for an available connection (current size: " + (usedConnections.size() + availableConnections.size()) + ")");
                    connection = availableConnections.take(); // Chờ vô thời hạn cho một kết nối
                }
            }

            if (connection != null) {
                // Kiểm tra xem kết nối có còn hợp lệ không (ví dụ: chưa bị đóng bởi server)
                // Đây là một kiểm tra đơn giản, có thể cần một kiểm tra phức tạp hơn trong thực tế.
                if (!connection.isValid(2)) { // Kiểm tra trong 2 giây
                    System.out.println("Invalid connection detected, creating a new one.");
                    try {
                        connection.close(); // Đóng kết nối không hợp lệ
                    } catch (SQLException e) {
                        System.err.println("Error closing invalid connection: " + e.getMessage());
                    }
                    connection = createNewConnection(); // Tạo một kết nối mới
                }
                usedConnections.add(connection);
                System.out.println("Connection acquired: " + connection + ". Used: " + usedConnections.size() + ", Available: " + availableConnections.size());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for a connection.", e);
        } catch (SQLException e) {
            System.err.println("Error getting connection from pool: " + e.getMessage());
            throw e;
        }
        return connection;
    }

    /**
     * Giải phóng một kết nối trở lại pool.
     *
     * @param connection Đối tượng Connection cần giải phóng.
     */
    public synchronized void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }

        if (usedConnections.remove(connection)) {
            try {
                // Kiểm tra nếu pool chưa shutdown thì mới đưa về
                if (!isShutdown) {
                    availableConnections.put(connection); // Đưa kết nối trở lại pool
                    System.out.println("Connection released: " + connection + ". Used: " + usedConnections.size() + ", Available: " + availableConnections.size());
                } else {
                    // Nếu pool đã shutdown, đóng kết nối ngay lập tức
                    connection.close();
                    System.out.println("Connection " + connection + " closed during shutdown.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while releasing connection: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Error closing released connection during shutdown: " + e.getMessage());
            }
        } else {
            System.err.println("Attempted to release an unknown or already released connection: " + connection);
            try {
                connection.close(); // Đóng kết nối nếu nó không thuộc pool này
            } catch (SQLException e) {
                System.err.println("Error closing unknown connection: " + e.getMessage());
            }
        }
    }

    /**
     * Đóng tất cả các kết nối trong pool và giải phóng tài nguyên.
     */
    public synchronized void shutdown() {
        if (isShutdown) {
            System.out.println("Connection pool is already shut down.");
            return;
        }
        isShutdown = true;
        System.out.println("Shutting down connection pool...");

        // Đóng tất cả các kết nối đang khả dụng
        while (!availableConnections.isEmpty()) {
            Connection connection = availableConnections.poll();
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("Closed available connection: " + connection);
                } catch (SQLException e) {
                    System.err.println("Error closing available connection during shutdown: " + e.getMessage());
                }
            }
        }

        // Đóng tất cả các kết nối đang được sử dụng (nếu có, nhưng không khuyến khích trong thực tế)
        // Trong một pool thực tế, bạn có thể cần cơ chế để chờ hoặc buộc đóng các kết nối đang sử dụng.
        // Ở đây chỉ đóng ngay lập tức nếu chúng tồn tại.
        for (Connection connection : new ArrayList<>(usedConnections)) { // Copy để tránh ConcurrentModificationException
            try {
                connection.close();
                System.out.println("Closed used connection during shutdown: " + connection);
            } catch (SQLException e) {
                System.err.println("Error closing used connection during shutdown: " + e.getMessage());
            }
        }
        usedConnections.clear();
        System.out.println("Connection pool shut down successfully.");
        instance = null; // Reset instance
    }

    public int getAvailableConnectionsCount() {
        return availableConnections.size();
    }

    public int getUsedConnectionsCount() {
        return usedConnections.size();
    }

    public int getTotalConnectionsCount() {
        return availableConnections.size() + usedConnections.size();
    }
}