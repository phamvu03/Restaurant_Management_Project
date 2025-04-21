package com.restaurant.restaurant_management_project.model;

/**
 *
 * @author admin
 */
public class Account {
    private String MaNV;
    private String TenTK;
    private String MatKhau;

    public Account() {
    }

    public Account(String MaNV, String TenTK, String MatKhau) {
        this.MaNV = MaNV;
        this.TenTK = TenTK;
        this.MatKhau = MatKhau;
    }

    public String getMaNV() {
        return MaNV;
    }

    public void setMaNV(String MaNV) {
        this.MaNV = MaNV;
    }

    public String getTenTK() {
        return TenTK;
    }

    public void setTenTK(String TenTK) {
        this.TenTK = TenTK;
    }

    public String getMatKhau() {
        return MatKhau;
    }

    public void setMatKhau(String MatKhau) {
        this.MatKhau = MatKhau;
    }
    
    
}
