package com.restaurant.restaurant_management_project.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/*
 ConnectionPool: quản lý tái sử dụng các kết nối JDBC.
 */
public class ConnectionPool {
	private final BlockingQueue<Connection> pool;
	private final int maxSize;
	private final long timeout; // milliseconds

	// Khởi tạo pool với kích thước ban đầu và tối đa, thời gian chờ
	public ConnectionPool(int initialSize, int maxSize, long timeoutMillis) throws SQLException {
		if (initialSize < 1 || maxSize < initialSize) {
			throw new IllegalArgumentException("initialSize >= 1 và maxSize >= initialSize");
		}
		this.pool = new ArrayBlockingQueue<>(maxSize);
		this.maxSize = maxSize;
		this.timeout = timeoutMillis;

		// Khởi tạo kết nối ban đầu
		for (int i = 0; i < initialSize; i++) {
			pool.offer(createNewConnection());
		}
	}

	// Tạo một kết nối mới thông qua DBConnection
	private Connection createNewConnection() throws SQLException {
		return DBConnection.getConnection();
	}

	/**
	 * Lấy một Connection từ pool. Nếu pool không có sẵn nhưng tổng số connections <
	 * maxSize, sẽ tạo mới. Ngược lại chờ tối đa timeout rồi trả về null hoặc ném
	 * SQLException.
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = pool.poll();
		if (conn != null && conn.isValid(2)) {
			return conn;
		}
		// Nếu pool đã rỗng nhưng chưa đạt maxSize, tạo bổ sung
		synchronized (this) {
			int currentTotal = maxSize - pool.remainingCapacity();
			if (currentTotal < maxSize) {
				return createNewConnection();
			}
		}
		try {
			// Chờ timeout
			conn = pool.poll(timeout, TimeUnit.MILLISECONDS);
			if (conn == null) {
				throw new SQLException("Không lấy được kết nối sau " + timeout + "ms");
			}
			if (!conn.isValid(2)) {
				// Nếu kết nối cũ invalid, tạo lại
				return createNewConnection();
			}
			return conn;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new SQLException("Bị interrupt khi lấy connection", e);
		}
	}

	
	public void releaseConnection(Connection conn) {
		if (conn == null)
			return;
		try {
			if (!conn.isClosed()) {
				if (!pool.offer(conn)) {
					// Pool đã đầy, đóng luôn
					conn.close();
				}
			}
		} catch (SQLException e) {
			// ignore
		}
	}

	
	public void shutdown() {
		for (Connection conn : pool) {
			try {
				if (!conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				// ignore
			}
		}
		pool.clear();
	}
}
