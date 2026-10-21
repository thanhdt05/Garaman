package model;

import java.io.Serializable;
import java.math.BigDecimal;
import util.AppConstants4;

public class SparePart4 implements Serializable {
    private static final long serialVersionUID = 2L;

    private int id;
    private String name;
    private String description;
    private String imagePath;
    private String unit;
    private String manufacturer;
    private String specifications;
    private String compatibleVehicleModels;
    private BigDecimal sellingPrice;
    private String warrantyPeriod;
    private String warrantyTerms;
    private String status = AppConstants4.PART_STATUS_UNPUBLISHED;
    private BigDecimal stockQuantity = BigDecimal.ZERO;
    private SparePartCategory4 category;

    public SparePart4() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getCompatibleVehicleModels() {
        return compatibleVehicleModels;
    }

    public void setCompatibleVehicleModels(String compatibleVehicleModels) {
        this.compatibleVehicleModels = compatibleVehicleModels;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getWarrantyPeriod() {
        return warrantyPeriod;
    }

    public void setWarrantyPeriod(String warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }

    public String getWarrantyTerms() {
        return warrantyTerms;
    }

    public void setWarrantyTerms(String warrantyTerms) {
        this.warrantyTerms = warrantyTerms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(BigDecimal stockQuantity) {
        this.stockQuantity = stockQuantity != null ? stockQuantity : BigDecimal.ZERO;
    }

    public SparePartCategory4 getCategory() {
        return category;
    }

    public void setCategory(SparePartCategory4 category) {
        this.category = category;
    }
}
