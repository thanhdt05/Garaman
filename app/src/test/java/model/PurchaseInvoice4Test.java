package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Kiểm thử tính toán tổng tiền hóa đơn nhập (PurchaseInvoice4)")
class PurchaseInvoice4Test {

    @Test
    @DisplayName("Hóa đơn không có chi tiết hoặc danh sách rỗng thì tổng tiền bằng 0.00")
    void testTinhTongTien_EmptyList() {
        PurchaseInvoice4 invoice = new PurchaseInvoice4();
        invoice.setDsChiTietNhap(null);
        assertEquals(new BigDecimal("0.00"), invoice.tinhTongTien());
        assertEquals(new BigDecimal("0.00"), invoice.getTongTien());

        invoice.setDsChiTietNhap(new ArrayList<>());
        assertEquals(new BigDecimal("0.00"), invoice.tinhTongTien());
    }

    @Test
    @DisplayName("Hóa đơn 1 dòng chi tiết tính đúng thành tiền và tổng tiền")
    void testTinhTongTien_SingleDetail() {
        PurchaseInvoice4 invoice = new PurchaseInvoice4();
        PurchaseInvoiceDetail4 detail = new PurchaseInvoiceDetail4();
        detail.setSoLuong(new BigDecimal("5.000"));
        detail.setDonGiaNhap(new BigDecimal("120000.00"));

        List<PurchaseInvoiceDetail4> list = new ArrayList<>();
        list.add(detail);
        invoice.setDsChiTietNhap(list);

        assertEquals(new BigDecimal("600000.00"), detail.tinhThanhTien());
        assertEquals(new BigDecimal("600000.00"), invoice.tinhTongTien());
    }

    @Test
    @DisplayName("Hóa đơn nhiều dòng chi tiết tính tổng chính xác")
    void testTinhTongTien_MultipleDetails() {
        PurchaseInvoice4 invoice = new PurchaseInvoice4();

        // Dòng 1: 10 * 50,000 = 500,000.00
        PurchaseInvoiceDetail4 d1 = new PurchaseInvoiceDetail4();
        d1.setSoLuong(new BigDecimal("10.000"));
        d1.setDonGiaNhap(new BigDecimal("50000.00"));

        // Dòng 2: 2.5 * 180,000 = 450,000.00
        PurchaseInvoiceDetail4 d2 = new PurchaseInvoiceDetail4();
        d2.setSoLuong(new BigDecimal("2.500"));
        d2.setDonGiaNhap(new BigDecimal("180000.00"));

        // Dòng 3: 4 * 325,500.50 = 1,302,002.00
        PurchaseInvoiceDetail4 d3 = new PurchaseInvoiceDetail4();
        d3.setSoLuong(new BigDecimal("4.000"));
        d3.setDonGiaNhap(new BigDecimal("325500.50"));

        List<PurchaseInvoiceDetail4> list = new ArrayList<>();
        list.add(d1);
        list.add(d2);
        list.add(d3);
        invoice.setDsChiTietNhap(list);

        BigDecimal expectedTotal = new BigDecimal("500000.00")
                .add(new BigDecimal("450000.00"))
                .add(new BigDecimal("1302002.00"))
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals(expectedTotal, invoice.tinhTongTien());
        assertEquals(expectedTotal, invoice.getTongTien());
    }

    @Test
    @DisplayName("Kiểm tra làm tròn HALF_UP khi nhân số lượng lẻ với đơn giá lẻ")
    void testTinhTongTien_DecimalPrecisionAndHalfUp() {
        PurchaseInvoiceDetail4 detail1 = new PurchaseInvoiceDetail4();
        // 1 * 10.005 -> 10.01 (HALF_UP làm tròn lên)
        detail1.setSoLuong(new BigDecimal("1.000"));
        detail1.setDonGiaNhap(new BigDecimal("10.005"));
        assertEquals(new BigDecimal("10.01"), detail1.tinhThanhTien());

        PurchaseInvoiceDetail4 detail2 = new PurchaseInvoiceDetail4();
        // 1 * 10.004 -> 10.00 (HALF_UP làm tròn xuống)
        detail2.setSoLuong(new BigDecimal("1.000"));
        detail2.setDonGiaNhap(new BigDecimal("10.004"));
        assertEquals(new BigDecimal("10.00"), detail2.tinhThanhTien());

        // Test hóa đơn gồm cả 2 dòng trên: 10.01 + 10.00 = 20.01
        PurchaseInvoice4 invoice = new PurchaseInvoice4();
        List<PurchaseInvoiceDetail4> list = new ArrayList<>();
        list.add(detail1);
        list.add(detail2);
        invoice.setDsChiTietNhap(list);

        assertEquals(new BigDecimal("20.01"), invoice.tinhTongTien());
    }

    @Test
    @DisplayName("Xử lý an toàn khi số lượng hoặc đơn giá null/0")
    void testTinhTongTien_NullOrZeroValues() {
        PurchaseInvoiceDetail4 dNull = new PurchaseInvoiceDetail4();
        dNull.setSoLuong(null);
        dNull.setDonGiaNhap(null);
        assertEquals(new BigDecimal("0.00"), dNull.tinhThanhTien());

        PurchaseInvoiceDetail4 dZero = new PurchaseInvoiceDetail4();
        dZero.setSoLuong(BigDecimal.ZERO);
        dZero.setDonGiaNhap(new BigDecimal("100000.00"));
        assertEquals(new BigDecimal("0.00"), dZero.tinhThanhTien());
    }
}
