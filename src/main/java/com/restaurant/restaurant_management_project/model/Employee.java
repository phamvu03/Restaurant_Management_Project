package com.restaurant.restaurant_management_project.model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 *
 * @author admin
 */
public class Employee {
    private String MaNV;
    private String TenNV;
    private Date NgaySinh;
    private String SDT;
    private String Email;
    private String ChucVu;
    private BigDecimal Luong;

    public Employee() {
    }

    public Employee(String MaNV, String TenNV, Date NgaySinh, String SDT, String Email, String ChucVu, BigDecimal Luong) {
        this.MaNV = MaNV;
        this.TenNV = TenNV;
        this.NgaySinh = NgaySinh;
        this.SDT = SDT;
        this.Email = Email;
        this.ChucVu = ChucVu;
        this.Luong = Luong;
    }

    public String getMaNV() {
        return MaNV;
    }

    public void setMaNV(String MaNV) {
        this.MaNV = MaNV;
    }

    public String getTenNV() {
        return TenNV;
    }

    public void setTenNV(String TenNV) {
        this.TenNV = TenNV;
    }

    public Date getNgaySinh() {
        return NgaySinh;
    }

    public void setNgaySinh(Date NgaySinh) {
        this.NgaySinh = NgaySinh;
    }

    public String getSDT() {
        return SDT;
    }

    public void setSDT(String SDT) {
        this.SDT = SDT;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getChucVu() {
        return ChucVu;
    }

    public void setChucVu(String ChucVu) {
        this.ChucVu = ChucVu;
    }

    public BigDecimal getLuong() {
        return Luong;
    }

    public void setLuong(BigDecimal Luong) {
        this.Luong = Luong;
    }
    
    
}
