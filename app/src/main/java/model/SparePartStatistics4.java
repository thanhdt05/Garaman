package model;

import java.math.BigDecimal;

public class SparePartStatistics4 extends SparePart4 {
    private static final long serialVersionUID = 1L;

    private BigDecimal soLuongSuDung = BigDecimal.ZERO;
    private BigDecimal doanhThu = BigDecimal.ZERO;

    public SparePartStatistics4() {
        super();
    }

    public BigDecimal getSoLuongSuDung() {
        return soLuongSuDung;
    }

    public void setSoLuongSuDung(BigDecimal soLuongSuDung) {
        this.soLuongSuDung = soLuongSuDung != null ? soLuongSuDung : BigDecimal.ZERO;
    }

    public BigDecimal getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(BigDecimal doanhThu) {
        this.doanhThu = doanhThu != null ? doanhThu : BigDecimal.ZERO;
    }
}
