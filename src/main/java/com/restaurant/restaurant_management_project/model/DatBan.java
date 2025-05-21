package com.restaurant.restaurant_management_project.model;

import java.time.LocalDateTime;

public class DatBan {
    private int maDatBan;
    private int maBan;
    private String tenKhachHang;
    private String soDienThoai;
    private LocalDateTime thoiGianDat;
    private LocalDateTime thoiGianDen;
    private int soLuongNguoi;
    private String ghiChu;

    public DatBan() {}

    public DatBan(int maBan, String tenKhachHang, String soDienThoai,
                  LocalDateTime thoiGianDat, LocalDateTime thoiGianDen,
                  int soLuongNguoi, String ghiChu) {
        this.maBan = maBan;
        this.tenKhachHang = tenKhachHang;
        this.soDienThoai = soDienThoai;
        this.thoiGianDat = thoiGianDat;
        this.thoiGianDen = thoiGianDen;
        this.soLuongNguoi = soLuongNguoi;
        this.ghiChu = ghiChu;
    }

    // Getters and Setters
    public int getMaDatBan() {
        return maDatBan;
    }

    public void setMaDatBan(int maDatBan) {
        this.maDatBan = maDatBan;
    }

    public int getMaBan() {
        return maBan;
    }

    public void setMaBan(int maBan) {
        this.maBan = maBan;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public LocalDateTime getThoiGianDat() {
        return thoiGianDat;
    }

    public void setThoiGianDat(LocalDateTime thoiGianDat) {
        this.thoiGianDat = thoiGianDat;
    }

    public LocalDateTime getThoiGianDen() {
        return thoiGianDen;
    }

    public void setThoiGianDen(LocalDateTime thoiGianDen) {
        this.thoiGianDen = thoiGianDen;
    }

    public int getSoLuongNguoi() {
        return soLuongNguoi;
    }

    public void setSoLuongNguoi(int soLuongNguoi) {
        this.soLuongNguoi = soLuongNguoi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
