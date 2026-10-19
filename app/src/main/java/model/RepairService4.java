package model;

import java.io.Serializable;
import java.math.BigDecimal;

public class RepairService4 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private BigDecimal donGiaApDung;
    private String hanBaoHanhApDung;
    private String dieuKienBaoHanhApDung;
    private String ghiChu;
    private String tenDichVuApDung;
    private Service4 dichVu;
    private Technician4 nhanVienKyThuat;

    public RepairService4() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getDonGiaApDung() {
        return donGiaApDung;
    }

    public void setDonGiaApDung(BigDecimal donGiaApDung) {
        this.donGiaApDung = donGiaApDung;
    }

    public String getHanBaoHanhApDung() {
        return hanBaoHanhApDung;
    }

    public void setHanBaoHanhApDung(String hanBaoHanhApDung) {
        this.hanBaoHanhApDung = hanBaoHanhApDung;
    }

    public String getDieuKienBaoHanhApDung() {
        return dieuKienBaoHanhApDung;
    }

    public void setDieuKienBaoHanhApDung(String dieuKienBaoHanhApDung) {
        this.dieuKienBaoHanhApDung = dieuKienBaoHanhApDung;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTenDichVuApDung() {
        return tenDichVuApDung;
    }

    public void setTenDichVuApDung(String tenDichVuApDung) {
        this.tenDichVuApDung = tenDichVuApDung;
    }

    public Service4 getDichVu() {
        return dichVu;
    }

    public void setDichVu(Service4 dichVu) {
        this.dichVu = dichVu;
    }

    public Technician4 getNhanVienKyThuat() {
        return nhanVienKyThuat;
    }

    public void setNhanVienKyThuat(Technician4 nhanVienKyThuat) {
        this.nhanVienKyThuat = nhanVienKyThuat;
    }
}
