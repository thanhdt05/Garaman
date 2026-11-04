-- =====================================================================
-- CSDL GaraMan (Đề 26 - PTIT)
-- Schema tập con chuẩn hóa 9 bảng phục vụ đúng 2 module đề 26:
--   1. Khách hàng tra cứu dịch vụ / phụ tùng
--   2. Nhân viên kho nhập phụ tùng
-- Động cơ lưu trữ: InnoDB, Bảng mã: utf8mb4, Collation: utf8mb4_0900_ai_ci
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `garaman4`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `garaman4`;

-- 1. Bảng thành viên chung (Member)
CREATE TABLE IF NOT EXISTS `tblMember4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `fullName` VARCHAR(255) NOT NULL,
    `username` VARCHAR(100) NULL UNIQUE,
    `password` VARCHAR(255) NULL,
    `dateOfBirth` DATE NULL,
    `address` TEXT NULL,
    `email` VARCHAR(255) NULL,
    `phoneNumber` VARCHAR(20) NOT NULL,
    `role` VARCHAR(30) NOT NULL,
    `accountStatus` VARCHAR(30) NULL,
    CONSTRAINT `chk_member_role` CHECK (`role` IN ('Customer', 'WarehouseStaff')),
    CONSTRAINT `chk_member_status` CHECK (`accountStatus` IS NULL OR `accountStatus` IN ('Hoạt động', 'Đã khóa')),
    CONSTRAINT `chk_member_account` CHECK (
        (`username` IS NULL AND `password` IS NULL AND `accountStatus` IS NULL AND `role` = 'Customer')
        OR (`username` IS NOT NULL AND `password` IS NOT NULL AND `accountStatus` IS NOT NULL)
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. Bảng nhân viên (Employee kế thừa Member)
CREATE TABLE IF NOT EXISTS `tblEmployee4` (
    `tblMember4id` INT PRIMARY KEY,
    `position` VARCHAR(30) NOT NULL,
    `employmentStatus` VARCHAR(30) NOT NULL,
    CONSTRAINT `chk_employee_position` CHECK (`position` IN ('Kho')),
    CONSTRAINT `chk_employee_status` CHECK (`employmentStatus` IN ('Đang làm việc', 'Ngừng làm việc')),
    CONSTRAINT `fk_employee_member` FOREIGN KEY (`tblMember4id`) REFERENCES `tblMember4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Bảng khách hàng (Customer kế thừa Member)
CREATE TABLE IF NOT EXISTS `tblCustomer4` (
    `tblMember4id` INT PRIMARY KEY,
    CONSTRAINT `fk_customer_member` FOREIGN KEY (`tblMember4id`) REFERENCES `tblMember4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. Bảng nhà cung cấp (Supplier)
CREATE TABLE IF NOT EXISTS `tblSupplier4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `address` TEXT NOT NULL,
    `phoneNumber` VARCHAR(20) NOT NULL,
    `email` VARCHAR(255) NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'Đang hoạt động',
    CONSTRAINT `chk_supplier_status` CHECK (`status` IN ('Đang hoạt động', 'Ngừng hoạt động'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. Bảng nhóm phụ tùng (SparePartCategory)
CREATE TABLE IF NOT EXISTS `tblSparePartCategory4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL UNIQUE,
    `description` TEXT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'Đang sử dụng',
    CONSTRAINT `chk_category_status` CHECK (`status` IN ('Đang sử dụng', 'Ngừng sử dụng'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6. Bảng dịch vụ (Service)
CREATE TABLE IF NOT EXISTS `tblService4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `category` VARCHAR(255) NOT NULL,
    `description` TEXT NOT NULL,
    `content` TEXT NOT NULL,
    `imagePath` VARCHAR(255) NULL,
    `unitPrice` DECIMAL(18,2) NOT NULL,
    `estimatedDuration` INT NOT NULL,
    `warrantyPeriod` VARCHAR(255) NULL,
    `warrantyTerms` TEXT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'Đang cung cấp',
    CONSTRAINT `chk_service_unitPrice` CHECK (`unitPrice` >= 0),
    CONSTRAINT `chk_service_thoiGian` CHECK (`estimatedDuration` > 0),
    CONSTRAINT `chk_service_status` CHECK (`status` IN ('Đang cung cấp', 'Ngừng cung cấp'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 7. Bảng phụ tùng (SparePart - lưu trữ tonKho trực tiếp)
CREATE TABLE IF NOT EXISTS `tblSparePart4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT NULL,
    `imagePath` VARCHAR(255) NULL,
    `unit` VARCHAR(20) NOT NULL,
    `manufacturer` VARCHAR(255) NOT NULL,
    `specifications` TEXT NULL,
    `compatibleVehicleModels` TEXT NULL,
    `sellingPrice` DECIMAL(18,2) NULL,
    `warrantyPeriod` VARCHAR(255) NULL,
    `warrantyTerms` TEXT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'Chưa công bố',
    `stockQuantity` DECIMAL(12,3) NOT NULL DEFAULT 0,
    `tblSparePartCategory4id` INT NOT NULL,
    CONSTRAINT `chk_sparepart_donVi` CHECK (`unit` IN ('Cái', 'Chiếc', 'Bộ', 'Lít')),
    CONSTRAINT `chk_sparepart_trangThai` CHECK (`status` IN ('Chưa công bố', 'Đang kinh doanh', 'Ngừng kinh doanh')),
    CONSTRAINT `chk_sparepart_giaBan` CHECK (`sellingPrice` IS NULL OR `sellingPrice` >= 0),
    CONSTRAINT `chk_sparepart_tonKho` CHECK (`stockQuantity` >= 0),
    CONSTRAINT `chk_sparepart_kinhDoanh` CHECK (
        `status` <> 'Đang kinh doanh'
        OR (`sellingPrice` IS NOT NULL AND `description` IS NOT NULL AND CHAR_LENGTH(TRIM(`description`)) > 0)
    ),
    CONSTRAINT `fk_sparepart_category` FOREIGN KEY (`tblSparePartCategory4id`) REFERENCES `tblSparePartCategory4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 8. Bảng hóa đơn nhập phụ tùng (PurchaseInvoice)
CREATE TABLE IF NOT EXISTS `tblPurchaseInvoice4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `purchaseDate` DATETIME NOT NULL,
    `confirmedAt` DATETIME NOT NULL,
    `paymentStatus` VARCHAR(30) NOT NULL DEFAULT 'Chưa thanh toán',
    `paidAt` DATETIME NULL,
    `paidByEmployee4id` INT NULL,
    `paidByNameSnapshot` VARCHAR(255) NULL,
    `tblSupplier4id` INT NOT NULL,
    `tblEmployee4id` INT NOT NULL,
    `requestCode` CHAR(36) NOT NULL,
    `supplierNameSnapshot` VARCHAR(255) NOT NULL,
    `warehouseStaffNameSnapshot` VARCHAR(255) NOT NULL,
    CONSTRAINT `uk_purchase_request` UNIQUE (`requestCode`),
    CONSTRAINT `chk_purchase_invoice_tinhTrang` CHECK (`paymentStatus` IN ('Chưa thanh toán', 'Đã thanh toán')),
    CONSTRAINT `chk_purchase_invoice_thanhToan` CHECK (
        (`paymentStatus` = 'Chưa thanh toán' AND `paidAt` IS NULL)
        OR (`paymentStatus` = 'Đã thanh toán' AND `paidAt` IS NOT NULL)
    ),
    -- Cho phép hồ sơ cũ đã trả nhưng không biết người trả để NULL cả hai cột.
    -- recordPayment luôn ghi đủ mã và tên người thực sự trả cho giao dịch mới.
    CONSTRAINT `chk_purchase_invoice_paidBy` CHECK (
        (`paidByEmployee4id` IS NULL AND `paidByNameSnapshot` IS NULL)
        OR (`paymentStatus` = 'Đã thanh toán' AND `paidByEmployee4id` IS NOT NULL
            AND `paidByNameSnapshot` IS NOT NULL AND CHAR_LENGTH(TRIM(`paidByNameSnapshot`)) > 0)
    ),
    CONSTRAINT `fk_purchase_inv_supplier` FOREIGN KEY (`tblSupplier4id`) REFERENCES `tblSupplier4` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_purchase_inv_employee` FOREIGN KEY (`tblEmployee4id`) REFERENCES `tblEmployee4` (`tblMember4id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_purchase_inv_paid_by` FOREIGN KEY (`paidByEmployee4id`) REFERENCES `tblEmployee4` (`tblMember4id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 9. Bảng chi tiết hóa đơn nhập (PurchaseInvoiceDetail)
CREATE TABLE IF NOT EXISTS `tblPurchaseInvoiceDetail4` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `quantity` DECIMAL(12,3) NOT NULL,
    `purchaseUnitPrice` DECIMAL(18,2) NOT NULL,
    `tblPurchaseInvoice4id` INT NOT NULL,
    `tblSparePart4id` INT NOT NULL,
    `sparePartNameSnapshot` VARCHAR(255) NOT NULL,
    `unitSnapshot` VARCHAR(20) NOT NULL,
    CONSTRAINT `chk_purchase_detail_soLuong` CHECK (`quantity` > 0),
    CONSTRAINT `chk_purchase_detail_donGia` CHECK (`purchaseUnitPrice` > 0),
    CONSTRAINT `uk_purchase_part` UNIQUE (`tblPurchaseInvoice4id`, `tblSparePart4id`),
    CONSTRAINT `fk_purchase_detail_inv` FOREIGN KEY (`tblPurchaseInvoice4id`) REFERENCES `tblPurchaseInvoice4` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_purchase_detail_part` FOREIGN KEY (`tblSparePart4id`) REFERENCES `tblSparePart4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
