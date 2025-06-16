package com.restaurant.restaurant_management_project.model;

import java.time.LocalDateTime;

public class DatBan {
    private String maDatBan;
    private String maBan;
    private String tenKhachHang;
    private int soKhach;
    private String soDienThoai;
    private String email;
    private LocalDateTime thoiGianDat;
    private String trangThai;
    private String ghiChu;

    public DatBan() {}

    // Constructor phù hợp với DatBanController
    public DatBan(String maBan, String tenKhachHang, String soDienThoai, 
                  LocalDateTime thoiGianDat, int soKhach, String ghiChu) {
        this.maBan = maBan;
        this.tenKhachHang = tenKhachHang;
        this.soDienThoai = soDienThoai;
        this.thoiGianDat = thoiGianDat;
        this.soKhach = soKhach;
        this.ghiChu = ghiChu;
        this.trangThai = "Đã đặt"; // Mặc định khi đặt bàn
    }

    // Getters and setters...
    public String getMaDatBan() { return maDatBan; }
    public void setMaDatBan(String maDatBan) { this.maDatBan = maDatBan; }
    
    public String getMaBan() { return maBan; }
    public void setMaBan(String maBan) { this.maBan = maBan; }
    
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    
    public int getSoKhach() { return soKhach; }
    public void setSoKhach(int soKhach) { this.soKhach = soKhach; }
    
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDateTime getThoiGianDat() { return thoiGianDat; }
    public void setThoiGianDat(LocalDateTime thoiGianDat) { this.thoiGianDat = thoiGianDat; }
    
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
