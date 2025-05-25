package com.restaurant.restaurant_management_project.model;

import com.restaurant.restaurant_management_project.database.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;



public class NhanVien {
    private static int dem = 0; // Dùng để sinh mã tự động

    private String maNV;
    private String tenNV;
    private LocalDate ngaySinh;
    private String sdt;
    private String email;
    private String chucVu;
    private BigDecimal luong;

    // Constructor không tham số - tự sinh mã
    public NhanVien() {
        this.maNV = sinhMaNhanVien();
    }

    // Constructor đầy đủ
    public NhanVien(String maNV, String tenNV, LocalDate ngaySinh, String sdt, String email, String chucVu, BigDecimal luong) {
        if (maNV == null || maNV.trim().isEmpty()) {
            this.maNV = sinhMaNhanVien();
        } else {
            this.maNV = maNV;
        }
        this.tenNV = tenNV;
        this.ngaySinh = ngaySinh;
        this.sdt = sdt;
        this.email = email;
        this.chucVu = chucVu;
        this.luong = luong;
    }

    // Cập nhật biến đếm từ CSDL
    public static void capNhatBienDemTuDatabase() {
        String sql = "SELECT MAX(MaNV) FROM NhanVien";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String maxMa = rs.getString(1); // VD: NV015
                if (maxMa != null && maxMa.matches("NV\\d{3}")) {
                    int so = Integer.parseInt(maxMa.substring(2));
                    dem = so;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sinh mã mới
    private static String sinhMaNhanVien() {
        dem++;
        return String.format("NV%03d", dem);
    }

    // Getters & Setters
    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public String getTenNV() { return tenNV; }
    public void setTenNV(String tenNV) { this.tenNV = tenNV; }
    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }
    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }
    public BigDecimal getLuong() { return luong; }
    public void setLuong(BigDecimal luong) { this.luong = luong; }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNV='" + maNV + '\'' +
                ", tenNV='" + tenNV + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", sdt='" + sdt + '\'' +
                ", email='" + email + '\'' +
                ", chucVu='" + chucVu + '\'' +
                ", luong=" + luong +
                '}';
    }
}
