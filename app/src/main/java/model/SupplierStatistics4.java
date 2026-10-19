package model;

import java.math.BigDecimal;

public class SupplierStatistics4 extends Supplier4 {
    private static final long serialVersionUID = 1L;

    private BigDecimal luongNhap = BigDecimal.ZERO;
    private String donVi;

    public SupplierStatistics4() {
        super();
    }

    public BigDecimal getLuongNhap() {
        return luongNhap;
    }

    public void setLuongNhap(BigDecimal luongNhap) {
        this.luongNhap = luongNhap != null ? luongNhap : BigDecimal.ZERO;
    }

    public String getDonVi() {
        return donVi;
    }

    public void setDonVi(String donVi) {
        this.donVi = donVi;
    }
}
