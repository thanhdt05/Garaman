package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RepairPart4 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private BigDecimal soLuongYeuCau = BigDecimal.ZERO;
    private BigDecimal soLuongThucTe = BigDecimal.ZERO;
    private BigDecimal donGiaApDung = BigDecimal.ZERO;
    private String hanBaoHanhApDung;
    private String dieuKienBaoHanhApDung;
    private String ghiChu;
    private String tenPhuTungApDung;
    private String donViApDung;
    private SparePart4 phuTung;

    public RepairPart4() {
    }

    public BigDecimal tinhThanhTien() {
        if (soLuongThucTe == null || donGiaApDung == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return soLuongThucTe.multiply(donGiaApDung).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getThanhTien() {
        return tinhThanhTien();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getSoLuongYeuCau() {
        return soLuongYeuCau;
    }

    public void setSoLuongYeuCau(BigDecimal soLuongYeuCau) {
        this.soLuongYeuCau = soLuongYeuCau != null ? soLuongYeuCau : BigDecimal.ZERO;
    }

    public BigDecimal getSoLuongThucTe() {
        return soLuongThucTe;
    }

    public void setSoLuongThucTe(BigDecimal soLuongThucTe) {
        this.soLuongThucTe = soLuongThucTe != null ? soLuongThucTe : BigDecimal.ZERO;
    }

    public BigDecimal getDonGiaApDung() {
        return donGiaApDung;
    }

    public void setDonGiaApDung(BigDecimal donGiaApDung) {
        this.donGiaApDung = donGiaApDung != null ? donGiaApDung : BigDecimal.ZERO;
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

    public String getTenPhuTungApDung() {
        return tenPhuTungApDung;
    }

    public void setTenPhuTungApDung(String tenPhuTungApDung) {
        this.tenPhuTungApDung = tenPhuTungApDung;
    }

    public String getDonViApDung() {
        return donViApDung;
    }

    public void setDonViApDung(String donViApDung) {
        this.donViApDung = donViApDung;
    }

    public SparePart4 getPhuTung() {
        return phuTung;
    }

    public void setPhuTung(SparePart4 phuTung) {
        this.phuTung = phuTung;
    }
}
