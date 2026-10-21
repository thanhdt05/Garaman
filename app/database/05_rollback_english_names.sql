-- Reverse English column names without deleting data.

USE `garaman4`;

ALTER TABLE `tblMember4`
  DROP CHECK `chk_member_role`,
  DROP CHECK `chk_member_status`,
  DROP CHECK `chk_member_account`,
  RENAME COLUMN `fullName` TO `hoTen`,
  RENAME COLUMN `username` TO `tenDangNhap`,
  RENAME COLUMN `password` TO `matKhau`,
  RENAME COLUMN `dateOfBirth` TO `ngaySinh`,
  RENAME COLUMN `address` TO `diaChi`,
  RENAME COLUMN `phoneNumber` TO `dienThoai`,
  RENAME COLUMN `role` TO `vaiTro`,
  RENAME COLUMN `accountStatus` TO `trangThaiTaiKhoan`,
  ADD CONSTRAINT `chk_member_vaiTro` CHECK (`vaiTro` IN ('Customer', 'WarehouseStaff')),
  ADD CONSTRAINT `chk_member_trangThai` CHECK (`trangThaiTaiKhoan` IS NULL OR `trangThaiTaiKhoan` IN ('Hoạt động', 'Đã khóa')),
  ADD CONSTRAINT `chk_member_account` CHECK (
        (`tenDangNhap` IS NULL AND `matKhau` IS NULL AND `trangThaiTaiKhoan` IS NULL AND `vaiTro` = 'Customer')
        OR (`tenDangNhap` IS NOT NULL AND `matKhau` IS NOT NULL AND `trangThaiTaiKhoan` IS NOT NULL)
    );

ALTER TABLE `tblEmployee4`
  DROP CHECK `chk_employee_position`,
  DROP CHECK `chk_employee_status`,
  RENAME COLUMN `position` TO `viTri`,
  RENAME COLUMN `employmentStatus` TO `trangThaiLamViec`,
  ADD CONSTRAINT `chk_employee_viTri` CHECK (`viTri` IN ('Kho')),
  ADD CONSTRAINT `chk_employee_trangThai` CHECK (`trangThaiLamViec` IN ('Đang làm việc', 'Ngừng làm việc'));

ALTER TABLE `tblSupplier4`
  DROP CHECK `chk_supplier_status`,
  RENAME COLUMN `name` TO `ten`,
  RENAME COLUMN `address` TO `diaChi`,
  RENAME COLUMN `phoneNumber` TO `dienThoai`,
  RENAME COLUMN `status` TO `trangThai`,
  ADD CONSTRAINT `chk_supplier_trangThai` CHECK (`trangThai` IN ('Đang hoạt động', 'Ngừng hoạt động'));

ALTER TABLE `tblSparePartCategory4`
  DROP CHECK `chk_category_status`,
  RENAME COLUMN `name` TO `ten`,
  RENAME COLUMN `description` TO `moTa`,
  RENAME COLUMN `status` TO `trangThai`,
  ADD CONSTRAINT `chk_category_trangThai` CHECK (`trangThai` IN ('Đang sử dụng', 'Ngừng sử dụng'));

ALTER TABLE `tblService4`
  DROP CHECK `chk_service_unitPrice`,
  DROP CHECK `chk_service_thoiGian`,
  DROP CHECK `chk_service_status`,
  RENAME COLUMN `name` TO `ten`,
  RENAME COLUMN `category` TO `nhom`,
  RENAME COLUMN `description` TO `moTa`,
  RENAME COLUMN `content` TO `noiDung`,
  RENAME COLUMN `imagePath` TO `hinhAnh`,
  RENAME COLUMN `unitPrice` TO `donGia`,
  RENAME COLUMN `estimatedDuration` TO `thoiGianDuKien`,
  RENAME COLUMN `warrantyPeriod` TO `hanBaoHanh`,
  RENAME COLUMN `warrantyTerms` TO `dieuKienBaoHanh`,
  RENAME COLUMN `status` TO `trangThai`,
  ADD CONSTRAINT `chk_service_donGia` CHECK (`donGia` >= 0),
  ADD CONSTRAINT `chk_service_thoiGian` CHECK (`thoiGianDuKien` > 0),
  ADD CONSTRAINT `chk_service_trangThai` CHECK (`trangThai` IN ('Đang cung cấp', 'Ngừng cung cấp'));

ALTER TABLE `tblSparePart4`
  DROP CHECK `chk_sparepart_unit`,
  DROP CHECK `chk_sparepart_status`,
  DROP CHECK `chk_sparepart_sellingPrice`,
  DROP CHECK `chk_sparepart_stockQuantity`,
  DROP CHECK `chk_sparepart_kinhDoanh`,
  RENAME COLUMN `name` TO `ten`,
  RENAME COLUMN `description` TO `moTa`,
  RENAME COLUMN `imagePath` TO `hinhAnh`,
  RENAME COLUMN `unit` TO `donVi`,
  RENAME COLUMN `manufacturer` TO `hangSanXuat`,
  RENAME COLUMN `specifications` TO `thongSo`,
  RENAME COLUMN `compatibleVehicleModels` TO `dongXePhuHop`,
  RENAME COLUMN `sellingPrice` TO `giaBan`,
  RENAME COLUMN `warrantyPeriod` TO `hanBaoHanh`,
  RENAME COLUMN `warrantyTerms` TO `dieuKienBaoHanh`,
  RENAME COLUMN `status` TO `trangThai`,
  RENAME COLUMN `stockQuantity` TO `tonKho`,
  ADD CONSTRAINT `chk_sparepart_donVi` CHECK (`donVi` IN ('Cái', 'Chiếc', 'Bộ', 'Lít')),
  ADD CONSTRAINT `chk_sparepart_trangThai` CHECK (`trangThai` IN ('Chưa công bố', 'Đang kinh doanh', 'Ngừng kinh doanh')),
  ADD CONSTRAINT `chk_sparepart_giaBan` CHECK (`giaBan` IS NULL OR `giaBan` >= 0),
  ADD CONSTRAINT `chk_sparepart_tonKho` CHECK (`tonKho` >= 0),
  ADD CONSTRAINT `chk_sparepart_kinhDoanh` CHECK (
        `trangThai` <> 'Đang kinh doanh'
        OR (`giaBan` IS NOT NULL AND `moTa` IS NOT NULL AND CHAR_LENGTH(TRIM(`moTa`)) > 0)
    );

ALTER TABLE `tblPurchaseInvoice4`
  DROP CHECK `chk_purchase_invoice_paymentStatus`,
  DROP CHECK `chk_purchase_invoice_thanhToan`,
  RENAME COLUMN `purchaseDate` TO `ngayNhap`,
  RENAME COLUMN `confirmedAt` TO `thoiDiemXacNhan`,
  RENAME COLUMN `paymentStatus` TO `tinhTrang`,
  RENAME COLUMN `paidAt` TO `thoiDiemThanhToan`,
  RENAME COLUMN `requestCode` TO `maYeuCau`,
  RENAME COLUMN `supplierNameSnapshot` TO `nhaCungCapTenApDung`,
  RENAME COLUMN `warehouseStaffNameSnapshot` TO `nhanVienTenApDung`,
  ADD CONSTRAINT `chk_purchase_invoice_tinhTrang` CHECK (`tinhTrang` IN ('Chưa thanh toán', 'Đã thanh toán')),
  ADD CONSTRAINT `chk_purchase_invoice_thanhToan` CHECK (
        (`tinhTrang` = 'Chưa thanh toán' AND `thoiDiemThanhToan` IS NULL)
        OR (`tinhTrang` = 'Đã thanh toán' AND `thoiDiemThanhToan` IS NOT NULL)
    );

ALTER TABLE `tblPurchaseInvoiceDetail4`
  DROP CHECK `chk_purchase_detail_quantity`,
  DROP CHECK `chk_purchase_detail_unitPrice`,
  RENAME COLUMN `quantity` TO `soLuong`,
  RENAME COLUMN `purchaseUnitPrice` TO `donGiaNhap`,
  RENAME COLUMN `sparePartNameSnapshot` TO `tenPhuTungApDung`,
  RENAME COLUMN `unitSnapshot` TO `donViApDung`,
  ADD CONSTRAINT `chk_purchase_detail_soLuong` CHECK (`soLuong` > 0),
  ADD CONSTRAINT `chk_purchase_detail_donGia` CHECK (`donGiaNhap` > 0);
