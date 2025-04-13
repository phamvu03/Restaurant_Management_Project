package com.restaurant.restaurant_management_project.model;

import java.sql.Date;

/**
 *
 * @author admin
 */
public class Reservation {
    private String MaDatBan;
    private String MaBan;
    private String TenKhachDaiDien;
    private int SoKhach;
    private String SDTKhach;
    private String Email;
    private Date ThoiGianHen;
    private String TrangThai;
    private String GhiChu;

    public Reservation() {
    }

    public Reservation(String MaDatBan, String MaBan, String TenKhachDaiDien, int SoKhach, String SDTKhach, String Email, Date ThoiGianHen, String TrangThai, String GhiChu) {
        this.MaDatBan = MaDatBan;
        this.MaBan = MaBan;
        this.TenKhachDaiDien = TenKhachDaiDien;
        this.SoKhach = SoKhach;
        this.SDTKhach = SDTKhach;
        this.Email = Email;
        this.ThoiGianHen = ThoiGianHen;
        this.TrangThai = TrangThai;
        this.GhiChu = GhiChu;
    }

    public String getMaDatBan() {
        return MaDatBan;
    }

    public void setMaDatBan(String MaDatBan) {
        this.MaDatBan = MaDatBan;
    }

    public String getTenKhachDaiDien() {
        return TenKhachDaiDien;
    }

    public void setTenKhachDaiDien(String TenKhachDaiDien) {
        this.TenKhachDaiDien = TenKhachDaiDien;
    }

    public int getSoKhach() {
        return SoKhach;
    }

    public void setSoKhach(int SoKhach) {
        this.SoKhach = SoKhach;
    }

    public String getSDTKhach() {
        return SDTKhach;
    }

    public void setSDTKhach(String SDTKhach) {
        this.SDTKhach = SDTKhach;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public Date getThoiGianHen() {
        return ThoiGianHen;
    }

    public void setThoiGianHen(Date ThoiGianHen) {
        this.ThoiGianHen = ThoiGianHen;
    }

    public String getTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(String TrangThai) {
        this.TrangThai = TrangThai;
    }

    public String getGhiChu() {
        return GhiChu;
    }

    public void setGhiChu(String GhiChu) {
        this.GhiChu = GhiChu;
    }

    public String getMaBan() {
        return MaBan;
    }

    public void setMaBan(String MaBan) {
        this.MaBan = MaBan;
    }

    public void setThoiGianDen(Date date) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
