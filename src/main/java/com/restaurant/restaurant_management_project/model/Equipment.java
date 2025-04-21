package com.restaurant.restaurant_management_project.model;

import java.sql.Date;

/**
 *
 * @author admin
 */
public class Equipment {
    String MaDungCu;
    String TenDungCu;
    String Loai;
    int SoLuong;
    String TinhTrang;
    Date NgayThongKe;

    public Equipment() {
    }

    public Equipment(String MaDungCu, String TenDungCu, String Loai, int SoLuong, String TinhTrang, Date NgayThongKe) {
        this.MaDungCu = MaDungCu;
        this.TenDungCu = TenDungCu;
        this.Loai = Loai;
        this.SoLuong = SoLuong;
        this.TinhTrang = TinhTrang;
        this.NgayThongKe = NgayThongKe;
    }

    public String getMaDungCu() {
        return MaDungCu;
    }

    public void setMaDungCu(String MaDungCu) {
        this.MaDungCu = MaDungCu;
    }

    public String getTenDungCu() {
        return TenDungCu;
    }

    public void setTenDungCu(String TenDungCu) {
        this.TenDungCu = TenDungCu;
    }

    public String getLoai() {
        return Loai;
    }

    public void setLoai(String Loai) {
        this.Loai = Loai;
    }

    public int getSoLuong() {
        return SoLuong;
    }

    public void setSoLuong(int SoLuong) {
        this.SoLuong = SoLuong;
    }

    public String getTinhTrang() {
        return TinhTrang;
    }

    public void setTinhTrang(String TinhTrang) {
        this.TinhTrang = TinhTrang;
    }

    public Date getNgayThongKe() {
        return NgayThongKe;
    }

    public void setNgayThongKe(Date NgayThongKe) {
        this.NgayThongKe = NgayThongKe;
    }

    @Override
    public String toString() {
        return "equipment{" + "MaDungCu=" + MaDungCu + ", TenDungCu=" + TenDungCu + ", Loai=" + Loai + ", SoLuong=" + SoLuong + ", TinhTrang=" + TinhTrang + ", NgayThongKe=" + NgayThongKe + '}';
    }

}
