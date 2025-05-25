package com.restaurant.restaurant_management_project.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NhanVien {
    private String maNV;
    private String tenNV;
    private LocalDate ngaySinh;
    private String sdt;
    private String email;
    private String chucVu;
    private BigDecimal luong;

    // Constructor không tham số
    public NhanVien() {
    }

    // Constructor đầy đủ
    public NhanVien(String maNV, String tenNV, LocalDate ngaySinh, String sdt, String email, String chucVu, BigDecimal luong) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.ngaySinh = ngaySinh;
        this.sdt = sdt;
        this.email = email;
        this.chucVu = chucVu;
        this.luong = luong;
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
