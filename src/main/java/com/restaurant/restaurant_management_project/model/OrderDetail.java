package com.restaurant.restaurant_management_project.model;

/**
 *
 * @author admin
 */
public class OrderDetail {
    private String MaDonHang;
    private String MaMon;
    private int SoLuong;

    public OrderDetail() {
    }

    public OrderDetail(String MaDonHang, String MaMon, int SoLuong) {
        this.MaDonHang = MaDonHang;
        this.MaMon = MaMon;
        this.SoLuong = SoLuong;
    }

    public String getMaDonHang() {
        return MaDonHang;
    }

    public void setMaDonHang(String MaDonHang) {
        this.MaDonHang = MaDonHang;
    }

    public String getMaMon() {
        return MaMon;
    }

    public void setMaMon(String MaMon) {
        this.MaMon = MaMon;
    }

    public int getSoLuong() {
        return SoLuong;
    }

    public void setSoLuong(int SoLuong) {
        this.SoLuong = SoLuong;
    }

    @Override
    public String toString() {
        return "OrderDetail{" + "MaDonHang=" + MaDonHang + ", MaMon=" + MaMon + ", SoLuong=" + SoLuong + '}';
    }

}
