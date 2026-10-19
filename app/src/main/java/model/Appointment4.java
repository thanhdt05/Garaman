package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Appointment4 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private LocalDateTime ngayGioHen;
    private LocalDateTime thoiDiemDat;
    private String yeuCau;
    private String ghiChu;
    private String trangThai;
    private String hoTenLienHe;
    private String dienThoaiLienHe;
    private Customer4 khachHang;
    private Car4 oTo;

    public Appointment4() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getNgayGioHen() {
        return ngayGioHen;
    }

    public void setNgayGioHen(LocalDateTime ngayGioHen) {
        this.ngayGioHen = ngayGioHen;
    }

    public LocalDateTime getThoiDiemDat() {
        return thoiDiemDat;
    }

    public void setThoiDiemDat(LocalDateTime thoiDiemDat) {
        this.thoiDiemDat = thoiDiemDat;
    }

    public String getYeuCau() {
        return yeuCau;
    }

    public void setYeuCau(String yeuCau) {
        this.yeuCau = yeuCau;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getHoTenLienHe() {
        return hoTenLienHe;
    }

    public void setHoTenLienHe(String hoTenLienHe) {
        this.hoTenLienHe = hoTenLienHe;
    }

    public String getDienThoaiLienHe() {
        return dienThoaiLienHe;
    }

    public void setDienThoaiLienHe(String dienThoaiLienHe) {
        this.dienThoaiLienHe = dienThoaiLienHe;
    }

    public Customer4 getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(Customer4 khachHang) {
        this.khachHang = khachHang;
    }

    public Car4 getOTo() {
        return oTo;
    }

    public void setOTo(Car4 oTo) {
        this.oTo = oTo;
    }
}
