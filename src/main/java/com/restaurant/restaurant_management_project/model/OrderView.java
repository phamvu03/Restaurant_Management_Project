package com.restaurant.restaurant_management_project.model;

import java.math.BigDecimal;
import java.sql.Date;

public class OrderView {

    private String maDonHang;
    private String maDatBan;
    private String maNV;
    private Date thoiGianTao;
    private Date thoiGianThanhToan;
    private BigDecimal revenue;

    public OrderView(Order order, BigDecimal revenue) {
        this.maDonHang = order.getMaDonHang();
        this.maDatBan = order.getMaDatBan();
        this.maNV = order.getMaNV();
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