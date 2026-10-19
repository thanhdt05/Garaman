package model;

import java.math.BigDecimal;

public class CustomerStatistics4 extends Customer4 {
    private static final long serialVersionUID = 1L;

    private int soLanSuaXe;
    private BigDecimal doanhThu = BigDecimal.ZERO;

    public CustomerStatistics4() {
        super();
    }

    public int getSoLanSuaXe() {
        return soLanSuaXe;
    }

    public void setSoLanSuaXe(int soLanSuaXe) {
        this.soLanSuaXe = soLanSuaXe;
    }

    public BigDecimal getDoanhThu() {
        return doanhThu;
    }

    public void setDoanhThu(BigDecimal doanhThu) {
        this.doanhThu = doanhThu != null ? doanhThu : BigDecimal.ZERO;
    }
}
