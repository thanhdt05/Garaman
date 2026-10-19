-- =====================================================================
-- CSDL GaraMan (Đề 26 - PTIT)
-- Schema toàn hệ thống gồm 16 bảng chuẩn hóa, bảo toàn mô hình đối tượng
-- Động cơ lưu trữ: InnoDB, Bảng mã: utf8mb4, Collation: utf8mb4_0900_ai_ci
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `garaman4`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `garaman4`;

-- 1. Bảng thành viên chung (Member)
CREATE TABLE IF NOT EXISTS `tblMember4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `hoTen` VARCHAR(255) NOT NULL,
    `tenDangNhap` VARCHAR(100) NULL UNIQUE,
    `matKhau` VARCHAR(255) NULL,
    `ngaySinh` DATE NULL,
    `diaChi` TEXT NULL,
    `email` VARCHAR(255) NULL,
    `dienThoai` VARCHAR(20) NOT NULL,
    `vaiTro` VARCHAR(30) NOT NULL,
    `trangThaiTaiKhoan` VARCHAR(30) NULL,
    CONSTRAINT `chk_member_vaiTro` CHECK (`vaiTro` IN ('Customer', 'Manager', 'WarehouseStaff', 'SalesStaff')),
    CONSTRAINT `chk_member_trangThai` CHECK (`trangThaiTaiKhoan` IS NULL OR `trangThaiTaiKhoan` IN ('Hoạt động', 'Đã khóa')),
    CONSTRAINT `chk_member_account` CHECK (
        (`tenDangNhap` IS NULL AND `matKhau` IS NULL AND `trangThaiTaiKhoan` IS NULL AND `vaiTro` = 'Customer')
        OR (`tenDangNhap` IS NOT NULL AND `matKhau` IS NOT NULL AND `trangThaiTaiKhoan` IS NOT NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. Bảng nhân viên (Employee kế thừa Member)
CREATE TABLE IF NOT EXISTS `tblEmployee4` (
    `tblMember4id` INT PRIMARY KEY,
    `viTri` VARCHAR(30) NOT NULL,
    `trangThaiLamViec` VARCHAR(30) NOT NULL,
    CONSTRAINT `chk_employee_viTri` CHECK (`viTri` IN ('Quản lí', 'Kho', 'Bán hàng')),
    CONSTRAINT `chk_employee_trangThai` CHECK (`trangThaiLamViec` IN ('Đang làm việc', 'Ngừng làm việc')),
    CONSTRAINT `fk_employee_member` FOREIGN KEY (`tblMember4id`) REFERENCES `tblMember4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Bảng khách hàng (Customer kế thừa Member)
CREATE TABLE IF NOT EXISTS `tblCustomer4` (
    `tblMember4id` INT PRIMARY KEY,
    CONSTRAINT `fk_customer_member` FOREIGN KEY (`tblMember4id`) REFERENCES `tblMember4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. Bảng nhân viên kỹ thuật (Technician - hồ sơ độc lập)
CREATE TABLE IF NOT EXISTS `tblTechnician4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `hoTen` VARCHAR(255) NOT NULL,
    `ngaySinh` DATE NULL,
    `diaChi` TEXT NULL,
    `email` VARCHAR(255) NULL,
    `dienThoai` VARCHAR(20) NOT NULL,
    `chuyenMon` VARCHAR(255) NOT NULL,
    `trangThai` VARCHAR(30) NOT NULL DEFAULT 'Đang làm việc',
    CONSTRAINT `chk_technician_trangThai` CHECK (`trangThai` IN ('Đang làm việc', 'Ngừng làm việc'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. Bảng nhà cung cấp (Supplier)
CREATE TABLE IF NOT EXISTS `tblSupplier4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `ten` VARCHAR(255) NOT NULL,
    `diaChi` TEXT NOT NULL,
    `dienThoai` VARCHAR(20) NOT NULL,
    `email` VARCHAR(255) NULL,
    `trangThai` VARCHAR(30) NOT NULL DEFAULT 'Đang hoạt động',
    CONSTRAINT `chk_supplier_trangThai` CHECK (`trangThai` IN ('Đang hoạt động', 'Ngừng hoạt động'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6. Bảng xe ô tô (Car)
CREATE TABLE IF NOT EXISTS `tblCar4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `bienSo` VARCHAR(20) NOT NULL UNIQUE,
    `hangXe` VARCHAR(255) NOT NULL,
    `dongXe` VARCHAR(255) NOT NULL,
    `namSanXuat` INT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 7. Bảng nhóm phụ tùng (SparePartCategory)
CREATE TABLE IF NOT EXISTS `tblSparePartCategory4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `ten` VARCHAR(255) NOT NULL UNIQUE,
    `moTa` TEXT NULL,
    `trangThai` VARCHAR(30) NOT NULL DEFAULT 'Đang sử dụng',
    CONSTRAINT `chk_category_trangThai` CHECK (`trangThai` IN ('Đang sử dụng', 'Ngừng sử dụng'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 8. Bảng dịch vụ (Service)
CREATE TABLE IF NOT EXISTS `tblService4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `ten` VARCHAR(255) NOT NULL,
    `nhom` VARCHAR(255) NOT NULL,
    `moTa` TEXT NOT NULL,
    `noiDung` TEXT NOT NULL,
    `hinhAnh` VARCHAR(255) NULL,
    `donGia` DECIMAL(18,2) NOT NULL,
    `thoiGianDuKien` INT NOT NULL,
    `hanBaoHanh` VARCHAR(255) NULL,
    `dieuKienBaoHanh` TEXT NULL,
    `trangThai` VARCHAR(30) NOT NULL DEFAULT 'Đang cung cấp',
    CONSTRAINT `chk_service_donGia` CHECK (`donGia` >= 0),
    CONSTRAINT `chk_service_thoiGian` CHECK (`thoiGianDuKien` > 0),
    CONSTRAINT `chk_service_trangThai` CHECK (`trangThai` IN ('Đang cung cấp', 'Ngừng cung cấp'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 9. Bảng phụ tùng (SparePart - lưu trữ tonKho trực tiếp)
CREATE TABLE IF NOT EXISTS `tblSparePart4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `ten` VARCHAR(255) NOT NULL,
    `moTa` TEXT NULL,
    `hinhAnh` VARCHAR(255) NULL,
    `donVi` VARCHAR(20) NOT NULL,
    `hangSanXuat` VARCHAR(255) NOT NULL,
    `thongSo` TEXT NULL,
    `dongXePhuHop` TEXT NULL,
    `giaBan` DECIMAL(18,2) NULL,
    `hanBaoHanh` VARCHAR(255) NULL,
    `dieuKienBaoHanh` TEXT NULL,
    `trangThai` VARCHAR(30) NOT NULL DEFAULT 'Chưa công bố',
    `tonKho` DECIMAL(12,3) NOT NULL DEFAULT 0,
    `tblSparePartCategory4id` INT NOT NULL,
    CONSTRAINT `chk_sparepart_donVi` CHECK (`donVi` IN ('Cái', 'Chiếc', 'Bộ', 'Lít')),
    CONSTRAINT `chk_sparepart_trangThai` CHECK (`trangThai` IN ('Chưa công bố', 'Đang kinh doanh', 'Ngừng kinh doanh')),
    CONSTRAINT `chk_sparepart_giaBan` CHECK (`giaBan` IS NULL OR `giaBan` >= 0),
    CONSTRAINT `chk_sparepart_tonKho` CHECK (`tonKho` >= 0),
    CONSTRAINT `chk_sparepart_kinhDoanh` CHECK (
        `trangThai` <> 'Đang kinh doanh'
        OR (`giaBan` IS NOT NULL AND `moTa` IS NOT NULL AND CHAR_LENGTH(TRIM(`moTa`)) > 0)
    ),
    CONSTRAINT `fk_sparepart_category` FOREIGN KEY (`tblSparePartCategory4id`) REFERENCES `tblSparePartCategory4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 10. Bảng lịch hẹn (Appointment)
CREATE TABLE IF NOT EXISTS `tblAppointment4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `ngayGioHen` DATETIME NOT NULL,
    `thoiDiemDat` DATETIME NOT NULL,
    `yeuCau` TEXT NULL,
    `ghiChu` TEXT NULL,
    `trangThai` VARCHAR(30) NOT NULL DEFAULT 'Đã đặt',
    `tblCustomer4id` INT NOT NULL,
    `tblCar4id` INT NOT NULL,
    `hoTenLienHe` VARCHAR(255) NOT NULL,
    `dienThoaiLienHe` VARCHAR(20) NOT NULL,
    CONSTRAINT `chk_appointment_trangThai` CHECK (`trangThai` IN ('Đã đặt', 'Đã tiếp nhận', 'Đã hủy')),
    CONSTRAINT `fk_appointment_customer` FOREIGN KEY (`tblCustomer4id`) REFERENCES `tblCustomer4` (`tblMember4id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_appointment_car` FOREIGN KEY (`tblCar4id`) REFERENCES `tblCar4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 11. Bảng phiếu tiếp nhận (RepairIntake)
CREATE TABLE IF NOT EXISTS `tblRepairIntake4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `thoiDiemTiepNhan` DATETIME NOT NULL,
    `soKm` DECIMAL(12,1) NULL,
    `tinhTrangXe` TEXT NULL,
    `trangThai` VARCHAR(30) NOT NULL DEFAULT 'Đã tiếp nhận',
    `ghiChu` TEXT NULL,
    `tblCustomer4id` INT NOT NULL,
    `tblCar4id` INT NOT NULL,
    `tblEmployee4id` INT NOT NULL,
    `tblAppointment4id` INT NULL UNIQUE,
    CONSTRAINT `chk_intake_soKm` CHECK (`soKm` IS NULL OR `soKm` >= 0),
    CONSTRAINT `chk_intake_trangThai` CHECK (`trangThai` IN ('Đã tiếp nhận', 'Đang xử lí', 'Hoàn thành', 'Đã hủy')),
    CONSTRAINT `fk_intake_customer` FOREIGN KEY (`tblCustomer4id`) REFERENCES `tblCustomer4` (`tblMember4id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_intake_car` FOREIGN KEY (`tblCar4id`) REFERENCES `tblCar4` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_intake_employee` FOREIGN KEY (`tblEmployee4id`) REFERENCES `tblEmployee4` (`tblMember4id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_intake_appointment` FOREIGN KEY (`tblAppointment4id`) REFERENCES `tblAppointment4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 12. Bảng dịch vụ của phiếu tiếp nhận (RepairService)
CREATE TABLE IF NOT EXISTS `tblRepairService4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `donGiaApDung` DECIMAL(18,2) NOT NULL,
    `hanBaoHanhApDung` VARCHAR(255) NULL,
    `dieuKienBaoHanhApDung` TEXT NULL,
    `ghiChu` TEXT NULL,
    `tenDichVuApDung` VARCHAR(255) NOT NULL,
    `tblRepairIntake4id` INT NOT NULL,
    `tblService4id` INT NOT NULL,
    `tblTechnician4id` INT NOT NULL,
    CONSTRAINT `chk_repair_service_donGia` CHECK (`donGiaApDung` >= 0),
    CONSTRAINT `uk_repair_service` UNIQUE (`tblRepairIntake4id`, `tblService4id`),
    CONSTRAINT `fk_repair_service_intake` FOREIGN KEY (`tblRepairIntake4id`) REFERENCES `tblRepairIntake4` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_repair_service_service` FOREIGN KEY (`tblService4id`) REFERENCES `tblService4` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_repair_service_tech` FOREIGN KEY (`tblTechnician4id`) REFERENCES `tblTechnician4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 13. Bảng phụ tùng của phiếu tiếp nhận (RepairPart)
CREATE TABLE IF NOT EXISTS `tblRepairPart4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `soLuongYeuCau` DECIMAL(12,3) NOT NULL,
    `soLuongThucTe` DECIMAL(12,3) NOT NULL DEFAULT 0,
    `donGiaApDung` DECIMAL(18,2) NOT NULL,
    `hanBaoHanhApDung` VARCHAR(255) NULL,
    `dieuKienBaoHanhApDung` TEXT NULL,
    `ghiChu` TEXT NULL,
    `tenPhuTungApDung` VARCHAR(255) NOT NULL,
    `donViApDung` VARCHAR(20) NOT NULL,
    `tblRepairIntake4id` INT NOT NULL,
    `tblSparePart4id` INT NOT NULL,
    CONSTRAINT `chk_repair_part_sl_yeucau` CHECK (`soLuongYeuCau` > 0),
    CONSTRAINT `chk_repair_part_sl_thucte` CHECK (`soLuongThucTe` >= 0),
    CONSTRAINT `chk_repair_part_donGia` CHECK (`donGiaApDung` >= 0),
    CONSTRAINT `uk_repair_part` UNIQUE (`tblRepairIntake4id`, `tblSparePart4id`),
    CONSTRAINT `fk_repair_part_intake` FOREIGN KEY (`tblRepairIntake4id`) REFERENCES `tblRepairIntake4` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_repair_part_part` FOREIGN KEY (`tblSparePart4id`) REFERENCES `tblSparePart4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 14. Bảng hóa đơn thanh toán khách hàng (CustomerInvoice)
CREATE TABLE IF NOT EXISTS `tblCustomerInvoice4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `thoiDiemLap` DATETIME NOT NULL,
    `thoiDiemXacNhan` DATETIME NOT NULL,
    `tinhTrang` VARCHAR(30) NOT NULL DEFAULT 'Chưa thanh toán',
    `thoiDiemThanhToan` DATETIME NULL,
    `tblRepairIntake4id` INT NOT NULL UNIQUE,
    `tblEmployee4id` INT NOT NULL,
    `hoTenKhachHangApDung` VARCHAR(255) NOT NULL,
    `bienSoApDung` VARCHAR(20) NOT NULL,
    `nhanVienTenApDung` VARCHAR(255) NOT NULL,
    CONSTRAINT `chk_customer_invoice_tinhTrang` CHECK (`tinhTrang` IN ('Chưa thanh toán', 'Đã thanh toán')),
    CONSTRAINT `chk_customer_invoice_thanhToan` CHECK (
        (`tinhTrang` = 'Chưa thanh toán' AND `thoiDiemThanhToan` IS NULL)
        OR (`tinhTrang` = 'Đã thanh toán' AND `thoiDiemThanhToan` IS NOT NULL)
    ),
    CONSTRAINT `fk_cust_inv_intake` FOREIGN KEY (`tblRepairIntake4id`) REFERENCES `tblRepairIntake4` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_cust_inv_employee` FOREIGN KEY (`tblEmployee4id`) REFERENCES `tblEmployee4` (`tblMember4id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 15. Bảng hóa đơn nhập phụ tùng (PurchaseInvoice)
CREATE TABLE IF NOT EXISTS `tblPurchaseInvoice4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `ngayNhap` DATETIME NOT NULL,
    `thoiDiemXacNhan` DATETIME NOT NULL,
    `tinhTrang` VARCHAR(30) NOT NULL DEFAULT 'Chưa thanh toán',
    `thoiDiemThanhToan` DATETIME NULL,
    `tblSupplier4id` INT NOT NULL,
    `tblEmployee4id` INT NOT NULL,
    `maYeuCau` CHAR(36) NOT NULL,
    `nhaCungCapTenApDung` VARCHAR(255) NOT NULL,
    `nhanVienTenApDung` VARCHAR(255) NOT NULL,
    CONSTRAINT `uk_purchase_request` UNIQUE (`maYeuCau`),
    CONSTRAINT `chk_purchase_invoice_tinhTrang` CHECK (`tinhTrang` IN ('Chưa thanh toán', 'Đã thanh toán')),
    CONSTRAINT `chk_purchase_invoice_thanhToan` CHECK (
        (`tinhTrang` = 'Chưa thanh toán' AND `thoiDiemThanhToan` IS NULL)
        OR (`tinhTrang` = 'Đã thanh toán' AND `thoiDiemThanhToan` IS NOT NULL)
    ),
    CONSTRAINT `fk_purchase_inv_supplier` FOREIGN KEY (`tblSupplier4id`) REFERENCES `tblSupplier4` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_purchase_inv_employee` FOREIGN KEY (`tblEmployee4id`) REFERENCES `tblEmployee4` (`tblMember4id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 16. Bảng chi tiết hóa đơn nhập (PurchaseInvoiceDetail)
CREATE TABLE IF NOT EXISTS `tblPurchaseInvoiceDetail4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `soLuong` DECIMAL(12,3) NOT NULL,
    `donGiaNhap` DECIMAL(18,2) NOT NULL,
    `tblPurchaseInvoice4id` INT NOT NULL,
    `tblSparePart4id` INT NOT NULL,
    `tenPhuTungApDung` VARCHAR(255) NOT NULL,
    `donViApDung` VARCHAR(20) NOT NULL,
    CONSTRAINT `chk_purchase_detail_soLuong` CHECK (`soLuong` > 0),
    CONSTRAINT `chk_purchase_detail_donGia` CHECK (`donGiaNhap` > 0),
    CONSTRAINT `uk_purchase_part` UNIQUE (`tblPurchaseInvoice4id`, `tblSparePart4id`),
    CONSTRAINT `fk_purchase_detail_inv` FOREIGN KEY (`tblPurchaseInvoice4id`) REFERENCES `tblPurchaseInvoice4` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_purchase_detail_part` FOREIGN KEY (`tblSparePart4id`) REFERENCES `tblSparePart4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
