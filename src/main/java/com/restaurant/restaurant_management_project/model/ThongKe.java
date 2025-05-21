package com.restaurant.restaurant_management_project.model;

import java.time.LocalDate;

public class ThongKe {
    private LocalDate ngay;
    private int soLuotDat;
    private int tongKhach;

    public ThongKe(LocalDate ngay, int soLuotDat,int tongKhach) {
        this.ngay = ngay;
        this.soLuotDat = soLuotDat;
        this.tongKhach= tongKhach;
    }

    // Getters and Setters
    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        this.ngay = ngay;
    }

    public int getSoLuotDat() {
        return soLuotDat;
    }

    public void setSoLuotDat(int soLuotDat) {
        this.soLuotDat = soLuotDat;
    }

    public int getTongKhach() {
        return tongKhach;
    }

    public void setTongKhach(int tongKhach) {
        this.tongKhach = tongKhach;
    }
}