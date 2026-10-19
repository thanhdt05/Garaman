package model;

import java.math.BigDecimal;

public class ServiceStatistics4 extends Service4 {
    private static final long serialVersionUID = 1L;

    private int soLuotSuDung;
    private BigDecimal doanhThu = BigDecimal.ZERO;

    public ServiceStatistics4() {
        super();
    }

    public int getSoLuotSuDung() {
        return soLuotSuDung;
    }

    public void setSoLuotSuDung(int soLuotSuDung) {
        this.soLuotSuDung = soLuotSuDung;
    }

    public BigDecimal getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(BigDecimal doanhThu) {
        this.doanhThu = doanhThu != null ? doanhThu : BigDecimal.ZERO;
    }
}
