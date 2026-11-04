package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseInvoice4 implements Serializable {
    private static final long serialVersionUID = 2L;

    private int id;
    private String requestCode;
    private LocalDateTime purchaseDate;
    private LocalDateTime confirmedAt;
    private String paymentStatus = "Chưa thanh toán";
    private LocalDateTime paidAt;
    private Supplier4 supplier;
    private WarehouseStaff4 warehouseStaff;
    private List<PurchaseInvoiceDetail4> invoiceDetails = new ArrayList<>();
    private String supplierNameSnapshot;
    private String warehouseStaffNameSnapshot;

    public PurchaseInvoice4() {
    }

    public BigDecimal calculateTotalAmount() {
        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (invoiceDetails != null) {
            for (PurchaseInvoiceDetail4 detail : invoiceDetails) {
                total = total.add(detail.calculateLineAmount());
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotalAmount() {
        return calculateTotalAmount();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(String requestCode) {
        this.requestCode = requestCode;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public Supplier4 getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier4 supplier) {
        this.supplier = supplier;
    }

    public WarehouseStaff4 getWarehouseStaff() {
        return warehouseStaff;
    }

    public void setWarehouseStaff(WarehouseStaff4 warehouseStaff) {
        this.warehouseStaff = warehouseStaff;
    }

    public List<PurchaseInvoiceDetail4> getInvoiceDetails() {
        return invoiceDetails;
    }

    public void setInvoiceDetails(List<PurchaseInvoiceDetail4> invoiceDetails) {
        this.invoiceDetails = invoiceDetails != null ? invoiceDetails : new ArrayList<>();
    }

    public String getSupplierNameSnapshot() {
        return supplierNameSnapshot;
    }

    public void setSupplierNameSnapshot(String supplierNameSnapshot) {
        this.supplierNameSnapshot = supplierNameSnapshot;
    }

    public String getWarehouseStaffNameSnapshot() {
        return warehouseStaffNameSnapshot;
    }

    public void setWarehouseStaffNameSnapshot(String warehouseStaffNameSnapshot) {
        this.warehouseStaffNameSnapshot = warehouseStaffNameSnapshot;
    }
}
