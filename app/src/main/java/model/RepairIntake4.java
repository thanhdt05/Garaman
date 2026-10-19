package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepairIntake4 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private LocalDateTime thoiDiemTiepNhan;
    private BigDecimal soKm;
    private String tinhTrangXe;
    private String trangThai;
    private String ghiChu;
    private Customer4 khachHang;
    private Car4 oTo;
    private SalesStaff4 nhanVienTiepNhan;
    private Appointment4 lichHen;
    private List<RepairService4> dsDichVu = new ArrayList<>();
    private List<RepairPart4> dsPhuTung = new ArrayList<>();

    public RepairIntake4() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getThoiDiemTiepNhan() {
        return thoiDiemTiepNhan;
    }

    public void setThoiDiemTiepNhan(LocalDateTime thoiDiemTiepNhan) {
        this.thoiDiemTiepNhan = thoiDiemTiepNhan;
    }

    public BigDecimal getSoKm() {
        return soKm;
    }

    public void setSoKm(BigDecimal soKm) {
        this.soKm = soKm;
    }

    public String getTinhTrangXe() {
        return tinhTrangXe;
    }

    public void setTinhTrangXe(String tinhTrangXe) {
        this.tinhTrangXe = tinhTrangXe;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
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

    public SalesStaff4 getNhanVienTiepNhan() {
        return nhanVienTiepNhan;
    }

    public void setNhanVienTiepNhan(SalesStaff4 nhanVienTiepNhan) {
        this.nhanVienTiepNhan = nhanVienTiepNhan;
    }

    public Appointment4 getLichHen() {
        return lichHen;
    }

    public void setLichHen(Appointment4 lichHen) {
        this.lichHen = lichHen;
    }

    public List<RepairService4> getDsDichVu() {
        return dsDichVu;
    }

    public void setDsDichVu(List<RepairService4> dsDichVu) {
        this.dsDichVu = dsDichVu != null ? dsDichVu : new ArrayList<>();
    }

    public List<RepairPart4> getDsPhuTung() {
        return dsPhuTung;
    }

    public void setDsPhuTung(List<RepairPart4> dsPhuTung) {
        this.dsPhuTung = dsPhuTung != null ? dsPhuTung : new ArrayList<>();
    }
}
