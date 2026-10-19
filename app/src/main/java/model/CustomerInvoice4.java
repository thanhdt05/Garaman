package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import util.AppConstants4;

public class CustomerInvoice4 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private LocalDateTime thoiDiemLap;
    private LocalDateTime thoiDiemXacNhan;
    private String tinhTrang = AppConstants4.TINH_TRANG_HD_CHUA_THANH_TOAN;
    private LocalDateTime thoiDiemThanhToan;
    private String hoTenKhachHangApDung;
    private String bienSoApDung;
    private String nhanVienTenApDung;
    private RepairIntake4 phieuTiepNhan;
    private SalesStaff4 nhanVienLap;

    public CustomerInvoice4() {
    }

    public BigDecimal tinhTongTien() {
        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (phieuTiepNhan != null) {
            if (phieuTiepNhan.getDsDichVu() != null) {
                for (RepairService4 dv : phieuTiepNhan.getDsDichVu()) {
                    if (dv.getDonGiaApDung() != null) {
                        total = total.add(dv.getDonGiaApDung().setScale(2, RoundingMode.HALF_UP));
                    }
                }
            }
            if (phieuTiepNhan.getDsPhuTung() != null) {
                for (RepairPart4 pt : phieuTiepNhan.getDsPhuTung()) {
                    total = total.add(pt.tinhThanhTien());
                }
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

    public LocalDateTime getThoiDiemLap() {
        return thoiDiemLap;
    }

    public void setThoiDiemLap(LocalDateTime thoiDiemLap) {
        this.thoiDiemLap = thoiDiemLap;
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

    public String getHoTenKhachHangApDung() {
        return hoTenKhachHangApDung;
    }

    public void setHoTenKhachHangApDung(String hoTenKhachHangApDung) {
        this.hoTenKhachHangApDung = hoTenKhachHangApDung;
    }

    public String getBienSoApDung() {
        return bienSoApDung;
    }

    public void setBienSoApDung(String bienSoApDung) {
        this.bienSoApDung = bienSoApDung;
    }

    public String getNhanVienTenApDung() {
        return nhanVienTenApDung;
    }

    public void setNhanVienTenApDung(String nhanVienTenApDung) {
        this.nhanVienTenApDung = nhanVienTenApDung;
    }

    public RepairIntake4 getPhieuTiepNhan() {
        return phieuTiepNhan;
    }

    public void setPhieuTiepNhan(RepairIntake4 phieuTiepNhan) {
        this.phieuTiepNhan = phieuTiepNhan;
    }

    public SalesStaff4 getNhanVienLap() {
        return nhanVienLap;
    }

    public void setNhanVienLap(SalesStaff4 nhanVienLap) {
        this.nhanVienLap = nhanVienLap;
    }
}
