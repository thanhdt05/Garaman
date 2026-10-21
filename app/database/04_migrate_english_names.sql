-- Rename columns only; keep tables, data, types and constraints.

USE `garaman4`;

ALTER TABLE `tblMember4`
  DROP CHECK `chk_member_vaiTro`,
  DROP CHECK `chk_member_trangThai`,
  DROP CHECK `chk_member_account`,
  RENAME COLUMN `hoTen` TO `fullName`,
  RENAME COLUMN `tenDangNhap` TO `username`,
  RENAME COLUMN `matKhau` TO `password`,
  RENAME COLUMN `ngaySinh` TO `dateOfBirth`,
  RENAME COLUMN `diaChi` TO `address`,
  RENAME COLUMN `dienThoai` TO `phoneNumber`,
  RENAME COLUMN `vaiTro` TO `role`,
  RENAME COLUMN `trangThaiTaiKhoan` TO `accountStatus`,
  ADD CONSTRAINT `chk_member_role` CHECK (`role` IN ('Customer', 'WarehouseStaff')),
  ADD CONSTRAINT `chk_member_status` CHECK (`accountStatus` IS NULL OR `accountStatus` IN ('Hoạt động', 'Đã khóa')),
  ADD CONSTRAINT `chk_member_account` CHECK (
        (`username` IS NULL AND `password` IS NULL AND `accountStatus` IS NULL AND `role` = 'Customer')
        OR (`username` IS NOT NULL AND `password` IS NOT NULL AND `accountStatus` IS NOT NULL)
    );

ALTER TABLE `tblEmployee4`
  DROP CHECK `chk_employee_viTri`,
  DROP CHECK `chk_employee_trangThai`,
  RENAME COLUMN `viTri` TO `position`,
  RENAME COLUMN `trangThaiLamViec` TO `employmentStatus`,
  ADD CONSTRAINT `chk_employee_position` CHECK (`position` IN ('Kho')),
  ADD CONSTRAINT `chk_employee_status` CHECK (`employmentStatus` IN ('Đang làm việc', 'Ngừng làm việc'));

ALTER TABLE `tblSupplier4`
  DROP CHECK `chk_supplier_trangThai`,
  RENAME COLUMN `ten` TO `name`,
  RENAME COLUMN `diaChi` TO `address`,
  RENAME COLUMN `dienThoai` TO `phoneNumber`,
  RENAME COLUMN `trangThai` TO `status`,
  ADD CONSTRAINT `chk_supplier_status` CHECK (`status` IN ('Đang hoạt động', 'Ngừng hoạt động'));

ALTER TABLE `tblSparePartCategory4`
  DROP CHECK `chk_category_trangThai`,
  RENAME COLUMN `ten` TO `name`,
  RENAME COLUMN `moTa` TO `description`,
  RENAME COLUMN `trangThai` TO `status`,
  ADD CONSTRAINT `chk_category_status` CHECK (`status` IN ('Đang sử dụng', 'Ngừng sử dụng'));

ALTER TABLE `tblService4`
  DROP CHECK `chk_service_donGia`,
  DROP CHECK `chk_service_thoiGian`,
  DROP CHECK `chk_service_trangThai`,
  RENAME COLUMN `ten` TO `name`,
  RENAME COLUMN `nhom` TO `category`,
  RENAME COLUMN `moTa` TO `description`,
  RENAME COLUMN `noiDung` TO `content`,
  RENAME COLUMN `hinhAnh` TO `imagePath`,
  RENAME COLUMN `donGia` TO `unitPrice`,
  RENAME COLUMN `thoiGianDuKien` TO `estimatedDuration`,
  RENAME COLUMN `hanBaoHanh` TO `warrantyPeriod`,
  RENAME COLUMN `dieuKienBaoHanh` TO `warrantyTerms`,
  RENAME COLUMN `trangThai` TO `status`,
  ADD CONSTRAINT `chk_service_unitPrice` CHECK (`unitPrice` >= 0),
  ADD CONSTRAINT `chk_service_thoiGian` CHECK (`estimatedDuration` > 0),
  ADD CONSTRAINT `chk_service_status` CHECK (`status` IN ('Đang cung cấp', 'Ngừng cung cấp'));

ALTER TABLE `tblSparePart4`
  DROP CHECK `chk_sparepart_donVi`,
  DROP CHECK `chk_sparepart_trangThai`,
  DROP CHECK `chk_sparepart_giaBan`,
  DROP CHECK `chk_sparepart_tonKho`,
  DROP CHECK `chk_sparepart_kinhDoanh`,
  RENAME COLUMN `ten` TO `name`,
  RENAME COLUMN `moTa` TO `description`,
  RENAME COLUMN `hinhAnh` TO `imagePath`,
  RENAME COLUMN `donVi` TO `unit`,
  RENAME COLUMN `hangSanXuat` TO `manufacturer`,
  RENAME COLUMN `thongSo` TO `specifications`,
  RENAME COLUMN `dongXePhuHop` TO `compatibleVehicleModels`,
  RENAME COLUMN `giaBan` TO `sellingPrice`,
  RENAME COLUMN `hanBaoHanh` TO `warrantyPeriod`,
  RENAME COLUMN `dieuKienBaoHanh` TO `warrantyTerms`,
  RENAME COLUMN `trangThai` TO `status`,
  RENAME COLUMN `tonKho` TO `stockQuantity`,
  ADD CONSTRAINT `chk_sparepart_unit` CHECK (`unit` IN ('Cái', 'Chiếc', 'Bộ', 'Lít')),
  ADD CONSTRAINT `chk_sparepart_status` CHECK (`status` IN ('Chưa công bố', 'Đang kinh doanh', 'Ngừng kinh doanh')),
  ADD CONSTRAINT `chk_sparepart_sellingPrice` CHECK (`sellingPrice` IS NULL OR `sellingPrice` >= 0),
  ADD CONSTRAINT `chk_sparepart_stockQuantity` CHECK (`stockQuantity` >= 0),
  ADD CONSTRAINT `chk_sparepart_kinhDoanh` CHECK (
        `status` <> 'Đang kinh doanh'
        OR (`sellingPrice` IS NOT NULL AND `description` IS NOT NULL AND CHAR_LENGTH(TRIM(`description`)) > 0)
    );

ALTER TABLE `tblPurchaseInvoice4`
  DROP CHECK `chk_purchase_invoice_tinhTrang`,
  DROP CHECK `chk_purchase_invoice_thanhToan`,
  RENAME COLUMN `ngayNhap` TO `purchaseDate`,
  RENAME COLUMN `thoiDiemXacNhan` TO `confirmedAt`,
  RENAME COLUMN `tinhTrang` TO `paymentStatus`,
  RENAME COLUMN `thoiDiemThanhToan` TO `paidAt`,
  RENAME COLUMN `maYeuCau` TO `requestCode`,
  RENAME COLUMN `nhaCungCapTenApDung` TO `supplierNameSnapshot`,
  RENAME COLUMN `nhanVienTenApDung` TO `warehouseStaffNameSnapshot`,
  ADD CONSTRAINT `chk_purchase_invoice_paymentStatus` CHECK (`paymentStatus` IN ('Chưa thanh toán', 'Đã thanh toán')),
  ADD CONSTRAINT `chk_purchase_invoice_thanhToan` CHECK (
        (`paymentStatus` = 'Chưa thanh toán' AND `paidAt` IS NULL)
        OR (`paymentStatus` = 'Đã thanh toán' AND `paidAt` IS NOT NULL)
    );

ALTER TABLE `tblPurchaseInvoiceDetail4`
  DROP CHECK `chk_purchase_detail_soLuong`,
  DROP CHECK `chk_purchase_detail_donGia`,
  RENAME COLUMN `soLuong` TO `quantity`,
  RENAME COLUMN `donGiaNhap` TO `purchaseUnitPrice`,
  RENAME COLUMN `tenPhuTungApDung` TO `sparePartNameSnapshot`,
  RENAME COLUMN `donViApDung` TO `unitSnapshot`,
  ADD CONSTRAINT `chk_purchase_detail_quantity` CHECK (`quantity` > 0),
  ADD CONSTRAINT `chk_purchase_detail_unitPrice` CHECK (`purchaseUnitPrice` > 0);
