package model;

import java.io.Serializable;
import java.math.BigDecimal;
import util.AppConstants4;

public class Service4 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String ten;
    private String nhom;
    private String moTa;
    private String noiDung;
    private String hinhAnh;
    private BigDecimal donGia;
    private int thoiGianDuKien;
    private String hanBaoHanh;
    private String dieuKienBaoHanh;
    private String trangThai = AppConstants4.TRANG_THAI_DV_DANG_CUNG_CAP;

    public Service4() {
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

    public String getNhom() {
        return nhom;
    }

    public void setNhom(String nhom) {
        this.nhom = nhom;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public int getThoiGianDuKien() {
        return thoiGianDuKien;
    }

    public void setThoiGianDuKien(int thoiGianDuKien) {
        this.thoiGianDuKien = thoiGianDuKien;
    }

    public String getHanBaoHanh() {
        return hanBaoHanh;
    }

    public void setHanBaoHanh(String hanBaoHanh) {
        this.hanBaoHanh = hanBaoHanh;
    }

    public String getDieuKienBaoHanh() {
        return dieuKienBaoHanh;
    }

    public void setDieuKienBaoHanh(String dieuKienBaoHanh) {
        this.dieuKienBaoHanh = dieuKienBaoHanh;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
