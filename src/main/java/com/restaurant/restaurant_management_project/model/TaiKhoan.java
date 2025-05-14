package com.restaurant.restaurant_management_project.model;

public class TaiKhoan {
	private String username;
	private String password;
	private String role;
	private String MaNV;

	// Constructor không tham số
	public TaiKhoan() {
		// TODO Auto-generated constructor stub
	}

	public TaiKhoan(String username, String password, String role, String MaNV) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.MaNV = MaNV;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getMaNV() {
		return MaNV;
	}

	public void setMaNV(String MaNV) {
		this.MaNV = MaNV;
	}
}