package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PurchaseInvoiceDetail4 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private BigDecimal soLuong = BigDecimal.ZERO;
    private BigDecimal donGiaNhap = BigDecimal.ZERO;
    private String tenPhuTungApDung;
    private String donViApDung;
    private SparePart4 phuTung;

    public PurchaseInvoiceDetail4() {
    }

    public BigDecimal tinhThanhTien() {
        if (soLuong == null || donGiaNhap == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return soLuong.multiply(donGiaNhap).setScale(2, RoundingMode.HALF_UP);
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

    public BigDecimal getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(BigDecimal soLuong) {
        this.soLuong = soLuong != null ? soLuong : BigDecimal.ZERO;
    }

    public BigDecimal getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(BigDecimal donGiaNhap) {
        this.donGiaNhap = donGiaNhap != null ? donGiaNhap : BigDecimal.ZERO;
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
