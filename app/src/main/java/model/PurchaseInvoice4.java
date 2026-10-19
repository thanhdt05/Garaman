package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import util.AppConstants4;

public class PurchaseInvoice4 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String maYeuCau;
    private LocalDateTime ngayNhap;
    private LocalDateTime thoiDiemXacNhan;
    private String tinhTrang = AppConstants4.TINH_TRANG_HD_CHUA_THANH_TOAN;
    private LocalDateTime thoiDiemThanhToan;
    private Supplier4 nhaCungCap;
    private WarehouseStaff4 nhanVienKho;
    private List<PurchaseInvoiceDetail4> dsChiTietNhap = new ArrayList<>();
    private String nhaCungCapTenApDung;
    private String nhanVienTenApDung;

    public PurchaseInvoice4() {
    }

    public BigDecimal tinhTongTien() {
        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (dsChiTietNhap != null) {
            for (PurchaseInvoiceDetail4 detail : dsChiTietNhap) {
                total = total.add(detail.tinhThanhTien());
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTongTien() {
        return tinhTongTien();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaYeuCau() {
        return maYeuCau;
    }

    public void setMaYeuCau(String maYeuCau) {
        this.maYeuCau = maYeuCau;
    }

    public LocalDateTime getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDateTime ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public LocalDateTime getThoiDiemXacNhan() {
        return thoiDiemXacNhan;
    }

    public void setThoiDiemXacNhan(LocalDateTime thoiDiemXacNhan) {
        this.thoiDiemXacNhan = thoiDiemXacNhan;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public LocalDateTime getThoiDiemThanhToan() {
        return thoiDiemThanhToan;
    }

    public void setThoiDiemThanhToan(LocalDateTime thoiDiemThanhToan) {
        this.thoiDiemThanhToan = thoiDiemThanhToan;
    }

    public Supplier4 getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(Supplier4 nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public WarehouseStaff4 getNhanVienKho() {
        return nhanVienKho;
    }

    public void setNhanVienKho(WarehouseStaff4 nhanVienKho) {
        this.nhanVienKho = nhanVienKho;
    }

    public List<PurchaseInvoiceDetail4> getDsChiTietNhap() {
        return dsChiTietNhap;
    }

    public void setDsChiTietNhap(List<PurchaseInvoiceDetail4> dsChiTietNhap) {
        this.dsChiTietNhap = dsChiTietNhap != null ? dsChiTietNhap : new ArrayList<>();
    }

    public String getNhaCungCapTenApDung() {
        return nhaCungCapTenApDung;
    }

    public void setNhaCungCapTenApDung(String nhaCungCapTenApDung) {
        this.nhaCungCapTenApDung = nhaCungCapTenApDung;
    }

    public String getNhanVienTenApDung() {
        return nhanVienTenApDung;
    }

    public void setNhanVienTenApDung(String nhanVienTenApDung) {
        this.nhanVienTenApDung = nhanVienTenApDung;
    }
}
