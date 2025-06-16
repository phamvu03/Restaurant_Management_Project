package com.restaurant.restaurant_management_project.model;

import java.math.BigDecimal;
import java.sql.Date;

public class OrderView {

    private String maDonHang;
    private String maDatBan;
    private String maNV;
    private String tenNV;
    private Date thoiGianTao;
    private Date thoiGianThanhToan;
    private BigDecimal revenue;

    public OrderView(Order order, BigDecimal revenue, String tenNV) {
        this.maDonHang = order.getMaDonHang();
        this.maDatBan = order.getMaDatBan();
        this.maNV = order.getMaNV();
        this.tenNV = tenNV;
        this.thoiGianTao = order.getThoiGianTao();
        this.thoiGianThanhToan = order.getThoiGianThanhToan();
        this.revenue = revenue;
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public String getMaDatBan() {
        return maDatBan;
    }

    public String getMaNV() {
        return maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public Date getThoiGianTao() {
        return thoiGianTao;
    }

    public Date getThoiGianThanhToan() {
        return thoiGianThanhToan;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }
}