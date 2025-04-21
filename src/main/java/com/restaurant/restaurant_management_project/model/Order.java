package com.restaurant.restaurant_management_project.model;

import java.sql.Date;

/**
 *
 * @author admin
 */
public class Order {
    private String MaDonHang;
    private String MaDatBan;
    private String MaNV;
    private Date ThoiGianTao;
    private Date ThoiGianThanhToan;

    public Order() {
    }

    public Order(String MaDonHang, String MaDatBan, String MaNV, Date ThoiGianTao, Date ThoiGianThanhToan) {
        this.MaDonHang = MaDonHang;
        this.MaDatBan = MaDatBan;
        this.MaNV = MaNV;
        this.ThoiGianTao = ThoiGianTao;
        this.ThoiGianThanhToan = ThoiGianThanhToan;
    }

    public String getMaDonHang() {
        return MaDonHang;
    }

    public void setMaDonHang(String MaDonHang) {
        this.MaDonHang = MaDonHang;
    }

    public String getMaDatBan() {
        return MaDatBan;
    }

    public void setMaDatBan(String MaDatBan) {
        this.MaDatBan = MaDatBan;
    }

    public String getMaNV() {
        return MaNV;
    }

    public void setMaNV(String MaNV) {
        this.MaNV = MaNV;
    }

    public Date getThoiGianTao() {
        return ThoiGianTao;
    }

    public void setThoiGianTao(Date ThoiGianTao) {
        this.ThoiGianTao = ThoiGianTao;
    }

    public Date getThoiGianThanhToan() {
        return ThoiGianThanhToan;
    }

    public void setThoiGianThanhToan(Date ThoiGianThanhToan) {
        this.ThoiGianThanhToan = ThoiGianThanhToan;
    }
}
