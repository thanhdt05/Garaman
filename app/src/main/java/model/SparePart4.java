package model;

import java.io.Serializable;
import java.math.BigDecimal;
import util.AppConstants4;

public class SparePart4 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String ten;
    private String moTa;
    private String hinhAnh;
    private String donVi;
    private String hangSanXuat;
    private String thongSo;
    private String dongXePhuHop;
    private BigDecimal giaBan;
    private String hanBaoHanh;
    private String dieuKienBaoHanh;
    private String trangThai = AppConstants4.TRANG_THAI_PT_CHUA_CONG_BO;
    private BigDecimal tonKho = BigDecimal.ZERO;
    private SparePartCategory4 nhom;

    public SparePart4() {
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

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }

    public String getHangSanXuat() {
        return hangSanXuat;
    }

    public void setHangSanXuat(String hangSanXuat) {
        this.hangSanXuat = hangSanXuat;
    }

    public String getThongSo() {
        return thongSo;
    }

    public void setThongSo(String thongSo) {
        this.thongSo = thongSo;
    }

    public String getDongXePhuHop() {
        return dongXePhuHop;
    }

    public void setDongXePhuHop(String dongXePhuHop) {
        this.dongXePhuHop = dongXePhuHop;
    }

    public BigDecimal getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(BigDecimal giaBan) {
        this.giaBan = giaBan;
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

    public BigDecimal getTonKho() {
        return tonKho;
    }

    public void setTonKho(BigDecimal tonKho) {
        this.tonKho = tonKho != null ? tonKho : BigDecimal.ZERO;
    }

    public SparePartCategory4 getNhom() {
        return nhom;
    }

    public void setNhom(SparePartCategory4 nhom) {
        this.nhom = nhom;
    }
}
