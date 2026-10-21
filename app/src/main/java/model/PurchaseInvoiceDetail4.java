package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PurchaseInvoiceDetail4 implements Serializable {
    private static final long serialVersionUID = 2L;

    private int id;
    private BigDecimal quantity = BigDecimal.ZERO;
    private BigDecimal purchaseUnitPrice = BigDecimal.ZERO;
    private String sparePartNameSnapshot;
    private String unitSnapshot;
    private SparePart4 sparePart;

    public PurchaseInvoiceDetail4() {
    }

    public BigDecimal calculateLineAmount() {
        if (quantity == null || purchaseUnitPrice == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return quantity.multiply(purchaseUnitPrice).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getLineAmount() {
        return calculateLineAmount();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity != null ? quantity : BigDecimal.ZERO;
    }

    public BigDecimal getPurchaseUnitPrice() {
        return purchaseUnitPrice;
    }

    public void setPurchaseUnitPrice(BigDecimal purchaseUnitPrice) {
        this.purchaseUnitPrice = purchaseUnitPrice != null ? purchaseUnitPrice : BigDecimal.ZERO;
    }

    public String getSparePartNameSnapshot() {
        return sparePartNameSnapshot;
    }

    public void setSparePartNameSnapshot(String sparePartNameSnapshot) {
        this.sparePartNameSnapshot = sparePartNameSnapshot;
    }

    public String getUnitSnapshot() {
        return unitSnapshot;
    }

    public void setUnitSnapshot(String unitSnapshot) {
        this.unitSnapshot = unitSnapshot;
    }

    public SparePart4 getSparePart() {
        return sparePart;
    }

    public void setSparePart(SparePart4 sparePart) {
        this.sparePart = sparePart;
    }
}
