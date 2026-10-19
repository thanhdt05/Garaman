package model;

import java.io.Serializable;
import util.AppConstants4;

public class SparePartCategory4 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String ten;
    private String moTa;
    private String trangThai = AppConstants4.TRANG_THAI_NHOM_DANG_SU_DUNG;

    public SparePartCategory4() {
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

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
