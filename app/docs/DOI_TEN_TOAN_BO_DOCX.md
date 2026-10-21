# Đồng bộ tên tiếng Anh trong DOCX

Đã cập nhật trực tiếp `26_B23DCCN764-full.docx` ngày 21/10/2026, kiểm tra mở bằng Microsoft Word và xuất PDF 63 trang. Giữ nguyên hình thiết kế tra cứu chủ bài vừa thêm trong lúc sửa (image13.png).

- Bản sao lưu mới nhất trước khi đổi tên: `backups/26_B23DCCN764-full.before-english-latest-20261021.docx`.
- Bản có đánh dấu đổi tên trong văn bản: `backups/26_B23DCCN764-full.english-name-changes-review.docx`. Hình cũ đầy đủ nằm trong bản sao lưu; bản đánh dấu dùng hình mới.
- [CSV để tìm trong Visual Paradigm](TEN_CU_TEN_MOI_TOAN_BO_DOCX.csv); các dòng có Owner cụ thể được ưu tiên so với dòng `*`.
- Các hình đã đổi tên có bản SVG/PNG tại `diagrams/report-english/`. Dựng từ mô hình và tọa độ XML xuất từ dự án gốc; không dùng ảnh xuất có watermark của bản Visual Paradigm dùng thử.
- Đã kiểm tra: 1.102 đoạn, 10 bảng OOXML, 13 hình; chỉ thay `word/document.xml` và 7 ảnh. Khi bỏ toàn bộ thay đổi tên, văn bản trùng từng đoạn với bản gốc mới nhất. Khóa, kiểu dữ liệu, ràng buộc U/N và số quan hệ được lấy từ mô hình gốc.

Đổi tên định danh trong báo cáo và 7 hình có thuộc tính/phương thức; giữ lời văn nghiệp vụ tiếng Việt, kiểu dữ liệu và quan hệ gốc.
Bảng tên của hai module vẫn theo `NAMING_ENGLISH.json`. Các tên chỉ có trong mô hình tổng thể được bổ sung bên dưới.

- Số lần đổi trong văn bản Word: 124.
- `CustomerInvoice4.nhanVienTenApDung` → `salesStaffNameSnapshot`; `PurchaseInvoice4.nhanVienTenApDung` → `warehouseStaffNameSnapshot`.
- `RepairIntake4.dsDichVu` → `repairServices`; `RepairIntake4.dsPhuTung` → `repairParts`.
- Khóa chính `id`, tên lớp tiếng Anh và tên khóa ngoại `tbl…4id` được giữ nguyên.
- Đây là cập nhật tài liệu; tên trong mã Java/JSP/SQL đang chạy chưa được đổi theo.

## Bảng tra cứu tên trong toàn báo cáo

| Tên cũ | Tên mới mặc định |
|---|---|
| `bienSo` | `licensePlate` |
| `bienSoApDung` | `licensePlateSnapshot` |
| `boLoc` | `itemTypeFilter` |
| `btnCapNhat` | `updateButton` |
| `btnChonNhaCungCap` | `selectSupplierButton` |
| `btnChonPhuTung` | `selectSparePartButton` |
| `btnDaThanhToan` | `markAsPaidButton` |
| `btnHuy` | `cancelButton` |
| `btnIn` | `printButton` |
| `btnLuu` | `saveButton` |
| `btnTimKiem` | `searchButton` |
| `btnXacNhan` | `confirmButton` |
| `chiCongBo` | `publishedOnly` |
| `chonThongTin` | `viewDetailsLink` |
| `chuyenMon` | `specialty` |
| `diaChi` | `address` |
| `dichVu` | `service` |
| `dienThoai` | `phoneNumber` |
| `dienThoaiLienHe` | `contactPhoneNumber` |
| `dieuKienBaoHanh` | `warrantyTerms` |
| `dieuKienBaoHanhApDung` | `warrantyTermsSnapshot` |
| `doanhThu` | `revenue` |
| `donGia` | `unitPrice` |
| `donGiaApDung` | `unitPriceSnapshot` |
| `donGiaNhap` | `purchaseUnitPrice` |
| `donVi` | `unit` |
| `donViApDung` | `unitSnapshot` |
| `dongXe` | `model` |
| `dongXePhuHop` | `compatibleVehicleModels` |
| `dsChiTietNhap` | `invoiceDetails` |
| `dsDichVu` | `services` |
| `dsNhaCungCap` | `suppliers` |
| `dsNhom` | `categories` |
| `dsPhuTung` | `spareParts` |
| `gdChiTietThongTin.jsp` | `itemDetails.jsp` |
| `gdChinhKH.jsp` | `customerHome.jsp` |
| `gdChinhKho.jsp` | `warehouseHome.jsp` |
| `gdChonNhaCungCap.jsp` | `selectSupplier.jsp` |
| `gdHoaDonNhapDaXacNhan.jsp` | `confirmedInvoice.jsp` |
| `gdInHoaDonNhap.jsp` | `printInvoice.jsp` |
| `gdLapHoaDonNhap.jsp` | `purchaseInvoice.jsp` |
| `gdThemNhaCungCap.jsp` | `newSupplier.jsp` |
| `gdThemPhuTung.jsp` | `newSparePart.jsp` |
| `gdTimThongTin.jsp` | `catalogSearch.jsp` |
| `ghiChu` | `notes` |
| `ghiNhanThanhToan` | `recordPayment` |
| `ghiNhanThanhToan(idHoaDon: int, soTienDaTra: BigDecimal, idNhanVienHienTai: int): boolean` | `recordPayment(invoiceId: int, amountPaid: BigDecimal, currentWarehouseStaffId: int): boolean` |
| `giaBan` | `sellingPrice` |
| `hanBaoHanh` | `warrantyPeriod` |
| `hanBaoHanhApDung` | `warrantyPeriodSnapshot` |
| `hangSanXuat` | `manufacturer` |
| `hangXe` | `manufacturer` |
| `hinhAnh` | `imagePath` |
| `hoTen` | `fullName` |
| `hoTenKhachHangApDung` | `customerNameSnapshot` |
| `hoTenLienHe` | `contactName` |
| `hoaDon` | `purchaseInvoice` |
| `hoaDonNhap` | `purchaseInvoice` |
| `idHoaDon` | `invoiceId` |
| `idNhanVienHienTai` | `currentWarehouseStaffId` |
| `idPhuTung` | `sparePartId` |
| `inDiaChi` | `address` |
| `inDienThoai` | `phoneNumber` |
| `inDieuKienBaoHanh` | `warrantyTerms` |
| `inDonVi` | `unit` |
| `inDongXePhuHop` | `compatibleVehicleModels` |
| `inEmail` | `email` |
| `inGiaBan` | `sellingPrice` |
| `inHanBaoHanh` | `warrantyPeriod` |
| `inHangSanXuat` | `manufacturer` |
| `inHinhAnh` | `imagePath` |
| `inHoaDon` | `printInvoiceLink` |
| `inMota` | `description` |
| `inSoTienDaTra` | `amountPaid` |
| `inTen` | `name` |
| `inTenCanTim` | `keyword` |
| `inTenNhaCungCap` | `keyword` |
| `inTenPhuTung` | `keyword` |
| `inThongSo` | `specifications` |
| `inTrangThai` | `status` |
| `inoutDSChiTietNhap` | `invoiceDetails` |
| `inoutNhom` | `category` |
| `khachHang` | `customer` |
| `layChiTietDichVu` | `getServiceDetails` |
| `layChiTietDichVu(id: int): Service4` | `getServiceDetails(serviceId: int): Service4` |
| `layChiTietPhuTung` | `getSparePartDetails` |
| `layChiTietPhuTung(id: int, chiCongBo: boolean): SparePart4` | `getSparePartDetails(sparePartId: int, publishedOnly: boolean): SparePart4` |
| `layDanhSachNhom` | `getActiveCategories` |
| `layDanhSachNhom(): List<SparePartCategory4>` | `getActiveCategories(): List<SparePartCategory4>` |
| `layThongTinHoaDonNhap` | `getPurchaseInvoiceDetails` |
| `layThongTinHoaDonNhap(id: int): PurchaseInvoice4` | `getPurchaseInvoiceDetails(invoiceId: int): PurchaseInvoice4` |
| `layThongTinNhaCungCap` | `getSupplierDetails` |
| `layThongTinNhaCungCap(id: int): Supplier4` | `getSupplierDetails(supplierId: int): Supplier4` |
| `layThongTinNhanVienKho` | `getWarehouseStaffDetails` |
| `layThongTinNhanVienKho(id: int): WarehouseStaff4` | `getWarehouseStaffDetails(warehouseStaffId: int): WarehouseStaff4` |
| `lichHen` | `appointment` |
| `luongNhap` | `receivedQuantity` |
| `luuHoaDonNhap` | `savePurchaseInvoice` |
| `luuHoaDonNhap(hoaDon: PurchaseInvoice4): int` | `savePurchaseInvoice(purchaseInvoice: PurchaseInvoice4): int` |
| `maYeuCau` | `requestCode` |
| `makh` | `customerCode` |
| `matKhau` | `password` |
| `moTa` | `description` |
| `mota` | `description` |
| `namSanXuat` | `manufactureYear` |
| `ngayGioHen` | `appointmentAt` |
| `ngayNhap` | `purchaseDate` |
| `ngaySinh` | `dateOfBirth` |
| `nhaCungCap` | `supplier` |
| `nhaCungCapTenApDung` | `supplierNameSnapshot` |
| `nhanVienKho` | `warehouseStaff` |
| `nhanVienKyThuat` | `technician` |
| `nhanVienLap` | `createdBy` |
| `nhanVienTenApDung` | `warehouseStaffNameSnapshot` |
| `nhanVienTiepNhan` | `receivingStaff` |
| `nhapPhuTung` | `startPurchaseLink` |
| `nhom` | `category` |
| `noiDung` | `content` |
| `oTo` | `car` |
| `outDSChiTietNhap` | `invoiceDetails` |
| `outDSKetQua` | `searchResults` |
| `outDSNhaCungCap` | `suppliers` |
| `outNgayNhap` | `purchaseDate` |
| `outNhaCungCap` | `supplier` |
| `outNhanVienKho` | `warehouseStaff` |
| `outNoiDungChungTu` | `purchaseInvoice` |
| `outThongTinChiTiet` | `itemDetails` |
| `outThongTinHoaDon` | `purchaseInvoice` |
| `outTinhTrangThanhToan` | `paymentStatus` |
| `outTongTien` | `totalAmount` |
| `outsubDSPhuTung` | `spareParts` |
| `phieuTiepNhan` | `repairIntake` |
| `phuTung` | `sparePart` |
| `quayLai` | `backLink` |
| `soKm` | `odometerReading` |
| `soLanSuaXe` | `repairCount` |
| `soLuong` | `quantity` |
| `soLuongSuDung` | `usedQuantity` |
| `soLuongThucTe` | `actualQuantity` |
| `soLuongYeuCau` | `requestedQuantity` |
| `soLuotSuDung` | `usageCount` |
| `soTienDaTra` | `amountPaid` |
| `subChon` | `selectButton` |
| `subHuy` | `cancelButton` |
| `subIn` | `printButton` |
| `subInHoaDon` | `printInvoiceLink` |
| `subLuu` | `saveButton` |
| `subNhapPhuTung` | `startPurchaseLink` |
| `subQuayLai` | `backLink` |
| `subThemNhaCungCapMoi` | `addSupplierLink` |
| `subThemPhuTungMoi` | `addSparePartButton` |
| `subTimKiem` | `searchButton` |
| `subTimThongTin` | `searchCatalogLink` |
| `subXacNhan` | `confirmButton` |
| `subXacNhanDaThanhToan` | `markAsPaidButton` |
| `tangTonSauNhap` | `increaseStockAfterPurchase` |
| `tangTonSauNhap(con: Connection, idPhuTung: int, soLuong: BigDecimal): void` | `increaseStockAfterPurchase(connection: Connection, sparePartId: int, quantity: BigDecimal): void` |
| `tblChiTietNhap` | `invoiceDetailsTable` |
| `tblKetQua` | `resultsTable` |
| `tblKetQuaPhuTung` | `sparePartsTable` |
| `tblNhaCungCap` | `suppliersTable` |
| `ten` | `name` |
| `tenDangNhap` | `username` |
| `tenDichVuApDung` | `serviceNameSnapshot` |
| `tenPhuTungApDung` | `sparePartNameSnapshot` |
| `thanhTien` | `lineAmount` |
| `themNhaCungCap` | `addSupplier` |
| `themNhaCungCap(supplier: Supplier4): int` | `addSupplier(supplier: Supplier4): int` |
| `themPhuTung` | `addSparePart` |
| `themPhuTung(part: SparePart4): int` | `addSparePart(sparePart: SparePart4): int` |
| `thoiDiemDat` | `bookedAt` |
| `thoiDiemLap` | `createdAt` |
| `thoiDiemThanhToan` | `paidAt` |
| `thoiDiemTiepNhan` | `receivedAt` |
| `thoiDiemXacNhan` | `confirmedAt` |
| `thoiDienTiepNhan` | `receivedAt` |
| `thoiGianDuKien` | `estimatedDuration` |
| `thongSo` | `specifications` |
| `timDichVuTheoTen` | `findServicesByName` |
| `timDichVuTheoTen(tuKhoa: String): List<Service4>` | `findServicesByName(keyword: String): List<Service4>` |
| `timNhaCungCapTheoTen` | `findSuppliersByName` |
| `timNhaCungCapTheoTen(tuKhoa: String): List<Supplier4>` | `findSuppliersByName(keyword: String): List<Supplier4>` |
| `timPhuTungTheoTen` | `findSparePartsByName` |
| `timPhuTungTheoTen(tuKhoa: String, chiCongBo: boolean): List<SparePart4>` | `findSparePartsByName(keyword: String, publishedOnly: boolean): List<SparePart4>` |
| `timThongTin` | `searchCatalogLink` |
| `tinhTongTien` | `calculateTotalAmount` |
| `tinhTongTien(): BigDecimal` | `calculateTotalAmount(): BigDecimal` |
| `tinhTrang` | `paymentStatus` |
| `tinhTrangLamViec` | `employmentStatus` |
| `tinhTrangXe` | `vehicleCondition` |
| `tonKho` | `stockQuantity` |
| `tongTien` | `totalAmount` |
| `tonkho` | `stockQuantity` |
| `trangThai` | `status` |
| `trangThaiLamViec` | `employmentStatus` |
| `trangThaiTaiKhoan` | `accountStatus` |
| `tuKhoa` | `keyword` |
| `vaiTro` | `role` |
| `viTri` | `position` |
| `yeuCau` | `requestDetails` |

## Các thay đổi trong phần chữ của Word

| Đoạn OOXML | Tên cũ | Tên mới |
|---|---|---|
| 723 | `timDichVuTheoTen` | `findServicesByName` |
| 728 | `timPhuTungTheoTen` | `findSparePartsByName` |
| 736 | `layChiTietDichVu` | `getServiceDetails` |
| 741 | `layChiTietPhuTung` | `getSparePartDetails` |
| 757 | `timNhaCungCapTheoTen` | `findSuppliersByName` |
| 765 | `themNhaCungCap` | `addSupplier` |
| 778 | `layThongTinNhaCungCap` | `getSupplierDetails` |
| 783 | `layThongTinNhanVienKho` | `getWarehouseStaffDetails` |
| 788 | `timPhuTungTheoTen` | `findSparePartsByName` |
| 797 | `layDanhSachNhom` | `getActiveCategories` |
| 802 | `themPhuTung` | `addSparePart` |
| 807 | `layChiTietPhuTung` | `getSparePartDetails` |
| 812 | `tinhTongTien` | `calculateTotalAmount` |
| 817 | `luuHoaDonNhap` | `savePurchaseInvoice` |
| 817 | `tangTonSauNhap` | `increaseStockAfterPurchase` |
| 827 | `layThongTinHoaDonNhap` | `getPurchaseInvoiceDetails` |
| 835 | `layThongTinHoaDonNhap` | `getPurchaseInvoiceDetails` |
| 840 | `ghiNhanThanhToan` | `recordPayment` |
| 1005 | `hoTen` | `fullName` |
| 1005 | `tenDangNhap` | `username` |
| 1005 | `matKhau` | `password` |
| 1005 | `ngaySinh` | `dateOfBirth` |
| 1005 | `diaChi` | `address` |
| 1005 | `dienThoai` | `phoneNumber` |
| 1005 | `vaiTro` | `role` |
| 1005 | `trangThaiTaiKhoan` | `accountStatus` |
| 1006 | `viTri` | `position` |
| 1006 | `trangThaiLamViec` | `employmentStatus` |
| 1011 | `hoTen` | `fullName` |
| 1011 | `ngaySinh` | `dateOfBirth` |
| 1011 | `diaChi` | `address` |
| 1011 | `dienThoai` | `phoneNumber` |
| 1011 | `chuyenMon` | `specialty` |
| 1011 | `trangThai` | `status` |
| 1012 | `ten` | `name` |
| 1012 | `diaChi` | `address` |
| 1012 | `dienThoai` | `phoneNumber` |
| 1012 | `trangThai` | `status` |
| 1013 | `bienSo` | `licensePlate` |
| 1013 | `hangXe` | `manufacturer` |
| 1013 | `dongXe` | `model` |
| 1013 | `namSanXuat` | `manufactureYear` |
| 1014 | `ngayGioHen` | `appointmentAt` |
| 1014 | `thoiDiemDat` | `bookedAt` |
| 1014 | `hoTenLienHe` | `contactName` |
| 1014 | `dienThoaiLienHe` | `contactPhoneNumber` |
| 1014 | `yeuCau` | `requestDetails` |
| 1014 | `ghiChu` | `notes` |
| 1014 | `trangThai` | `status` |
| 1015 | `thoiDiemTiepNhan` | `receivedAt` |
| 1015 | `soKm` | `odometerReading` |
| 1015 | `tinhTrangXe` | `vehicleCondition` |
| 1015 | `trangThai` | `status` |
| 1015 | `ghiChu` | `notes` |
| 1016 | `thoiDiemLap` | `createdAt` |
| 1016 | `thoiDiemXacNhan` | `confirmedAt` |
| 1016 | `hoTenKhachHangApDung` | `customerNameSnapshot` |
| 1016 | `bienSoApDung` | `licensePlateSnapshot` |
| 1016 | `nhanVienTenApDung` | `salesStaffNameSnapshot` |
| 1016 | `tongTien` | `totalAmount` |
| 1016 | `tinhTrang` | `paymentStatus` |
| 1016 | `thoiDiemThanhToan` | `paidAt` |
| 1017 | `ten` | `name` |
| 1017 | `nhom` | `category` |
| 1017 | `moTa` | `description` |
| 1017 | `noiDung` | `content` |
| 1017 | `hinhAnh` | `imagePath` |
| 1017 | `donGia` | `unitPrice` |
| 1017 | `thoiGianDuKien` | `estimatedDuration` |
| 1017 | `hanBaoHanh` | `warrantyPeriod` |
| 1017 | `dieuKienBaoHanh` | `warrantyTerms` |
| 1017 | `trangThai` | `status` |
| 1018 | `ten` | `name` |
| 1018 | `moTa` | `description` |
| 1018 | `hinhAnh` | `imagePath` |
| 1018 | `donVi` | `unit` |
| 1018 | `hangSanXuat` | `manufacturer` |
| 1018 | `thongSo` | `specifications` |
| 1018 | `dongXePhuHop` | `compatibleVehicleModels` |
| 1018 | `giaBan` | `sellingPrice` |
| 1018 | `hanBaoHanh` | `warrantyPeriod` |
| 1018 | `dieuKienBaoHanh` | `warrantyTerms` |
| 1018 | `trangThai` | `status` |
| 1018 | `tonKho` | `stockQuantity` |
| 1019 | `ten` | `name` |
| 1019 | `moTa` | `description` |
| 1019 | `trangThai` | `status` |
| 1020 | `tenDichVuApDung` | `serviceNameSnapshot` |
| 1020 | `donGiaApDung` | `unitPriceSnapshot` |
| 1020 | `hanBaoHanhApDung` | `warrantyPeriodSnapshot` |
| 1020 | `dieuKienBaoHanhApDung` | `warrantyTermsSnapshot` |
| 1020 | `ghiChu` | `notes` |
| 1021 | `tenPhuTungApDung` | `sparePartNameSnapshot` |
| 1021 | `donViApDung` | `unitSnapshot` |
| 1021 | `soLuongYeuCau` | `requestedQuantity` |
| 1021 | `soLuongThucTe` | `actualQuantity` |
| 1021 | `donGiaApDung` | `unitPriceSnapshot` |
| 1021 | `hanBaoHanhApDung` | `warrantyPeriodSnapshot` |
| 1021 | `dieuKienBaoHanhApDung` | `warrantyTermsSnapshot` |
| 1021 | `ghiChu` | `notes` |
| 1022 | `maYeuCau` | `requestCode` |
| 1022 | `ngayNhap` | `purchaseDate` |
| 1022 | `thoiDiemXacNhan` | `confirmedAt` |
| 1022 | `nhaCungCapTenApDung` | `supplierNameSnapshot` |
| 1022 | `nhanVienTenApDung` | `warehouseStaffNameSnapshot` |
| 1022 | `tongTien` | `totalAmount` |
| 1022 | `tinhTrang` | `paymentStatus` |
| 1022 | `thoiDiemThanhToan` | `paidAt` |
| 1023 | `tenPhuTungApDung` | `sparePartNameSnapshot` |
| 1023 | `donViApDung` | `unitSnapshot` |
| 1023 | `soLuong` | `quantity` |
| 1023 | `donGiaNhap` | `purchaseUnitPrice` |
| 1023 | `thanhTien` | `lineAmount` |
| 1024 | `doanhThu` | `revenue` |
| 1024 | `soLanSuaXe` | `repairCount` |
| 1025 | `soLuotSuDung` | `usageCount` |
| 1025 | `doanhThu` | `revenue` |
| 1026 | `soLuongSuDung` | `usedQuantity` |
| 1026 | `doanhThu` | `revenue` |
| 1027 | `luongNhap` | `receivedQuantity` |
| 1027 | `donVi` | `unit` |
| 1086 | `tongTien` | `totalAmount` |
| 1087 | `thanhTien` | `lineAmount` |
| 1088 | `tonKho` | `stockQuantity` |
