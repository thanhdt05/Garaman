package dao;

import model.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** Replaces utility-only tests with calls at the actual persistence boundary. */
class InputValidationTest {
    @Test void missingSupplierFieldsAndInvalidContactAreRejectedBeforeJdbc() {
        SupplierDAO4 dao=new SupplierDAO4();
        assertThrows(IllegalArgumentException.class,()->dao.addSupplier(null));
        Supplier4 supplier=new Supplier4(); supplier.setName("NCC"); supplier.setAddress("Hà Nội");
        supplier.setPhoneNumber("0912abc789"); supplier.setStatus("Đang hoạt động");
        assertThrows(IllegalArgumentException.class,()->dao.addSupplier(supplier));
        supplier.setPhoneNumber("0912345678"); supplier.setEmail("user@");
        assertThrows(IllegalArgumentException.class,()->dao.addSupplier(supplier));
    }
    @Test void activePartNeedsPublishedInformationAndValidUnit() {
        SparePartDAO4 dao=new SparePartDAO4();
        assertThrows(IllegalArgumentException.class,()->dao.addSparePart(null));
        SparePart4 part=new SparePart4(); part.setName("Lọc dầu"); part.setManufacturer("Hãng A");
        SparePartCategory4 category=new SparePartCategory4(); category.setId(1); part.setCategory(category);
        part.setUnit("Cái"); part.setStatus("Đang kinh doanh");
        assertThrows(IllegalArgumentException.class,()->dao.addSparePart(part));
        part.setSellingPrice(BigDecimal.TEN); part.setDescription("Mô tả"); part.setUnit("Kg");
        assertThrows(IllegalArgumentException.class,()->dao.addSparePart(part));
    }
    @Test void invoiceRejectsFractionalPiecesAndDuplicatePartsBeforeJdbc() {
        PurchaseInvoice4 invoice=new PurchaseInvoice4();
        Supplier4 supplier=new Supplier4(); supplier.setId(1); invoice.setSupplier(supplier);
        WarehouseStaff4 staff=new WarehouseStaff4(); staff.setId(2); invoice.setWarehouseStaff(staff);
        SparePart4 part=new SparePart4(); part.setId(1); part.setUnit("Cái");
        PurchaseInvoiceDetail4 line=new PurchaseInvoiceDetail4(); line.setSparePart(part);
        line.setQuantity(new BigDecimal("1.5")); line.setPurchaseUnitPrice(BigDecimal.TEN);
        invoice.setInvoiceDetails(List.of(line));
        PurchaseInvoiceDAO4 dao=new PurchaseInvoiceDAO4();
        assertThrows(IllegalArgumentException.class,()->dao.savePurchaseInvoice(invoice));
        line.setQuantity(BigDecimal.ONE); invoice.setInvoiceDetails(List.of(line,line));
        assertThrows(IllegalArgumentException.class,()->dao.savePurchaseInvoice(invoice));
    }
}
