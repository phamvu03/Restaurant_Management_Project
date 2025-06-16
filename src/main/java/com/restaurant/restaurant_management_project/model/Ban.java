package com.restaurant.restaurant_management_project.model;

public class Ban {
    private String maBan;
    private String viTri;
    private int soGhe;
    private String ghiChu;

    // Constructor mặc định
    public Ban() {}

    public Ban(String maBan, String viTri, int soGhe, String ghiChu) {
        this.maBan = maBan;
        this.viTri = viTri;
        this.soGhe = soGhe;
        this.ghiChu = ghiChu;
    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public int getSoGhe() {
        return soGhe;
    }

    public void setSoGhe(int soGhe) {
        this.soGhe = soGhe;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return "Ban{" +
                "maBan='" + maBan + '\'' +
                ", viTri='" + viTri + '\'' +
                ", soGhe=" + soGhe +
                ", ghiChu='" + ghiChu + '\'' +
                '}';
    }
}
