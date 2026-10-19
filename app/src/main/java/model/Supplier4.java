package model;

import java.io.Serializable;
import util.AppConstants4;

public class Supplier4 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String ten;
    private String diaChi;
    private String dienThoai;
    private String email;
    private String trangThai = AppConstants4.TRANG_THAI_NCC_DANG_HOAT_DONG;

    public Supplier4() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
