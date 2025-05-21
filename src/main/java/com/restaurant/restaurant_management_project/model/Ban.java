package com.restaurant.restaurant_management_project.model;

public class Ban {
    private int maBan;
    private String tenBan;
    private String viTri;
    private String trangThai;
    private int soChoNgoi;

    // Constructor mặc định
    public Ban() {}

    // Constructor đầy đủ
    public Ban(int maBan, String tenBan, String viTri, String trangThai, int soChoNgoi) {
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.viTri = viTri;
        this.trangThai = trangThai;
        this.soChoNgoi = soChoNgoi;
    }

    // Getter và Setter cho maBan
    public int getMaBan() {
        return maBan;
    }

    public void setMaBan(int maBan) {
        this.maBan = maBan;
    }

    // Getter và Setter cho tenBan
    public String getTenBan() {
        return tenBan;
    }

    public void setTenBan(String tenBan) {
        this.tenBan = tenBan;
    }

    // Getter và Setter cho viTri
    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    // Getter và Setter cho trangThai
    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // Getter và Setter cho soChoNgoi
    public int getSoChoNgoi() {
        return soChoNgoi;
    }

    public void setSoChoNgoi(int soChoNgoi) {
        this.soChoNgoi = soChoNgoi;
    }

    // (Tùy chọn) Ghi đè toString() để in thông tin bàn
    @Override
    public String toString() {
        return "Ban{" +
                "maBan=" + maBan +
                ", tenBan='" + tenBan + '\'' +
                ", viTri='" + viTri + '\'' +
                ", trangThai='" + trangThai + '\'' +
                ", soChoNgoi=" + soChoNgoi +
                '}';
    }
}
