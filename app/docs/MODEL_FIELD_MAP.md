# Bảng Ánh Xạ Thuộc Tính: Thiết Kế → Java Model → CSDL (MODEL_FIELD_MAP)

Tài liệu này đối chiếu từng thuộc tính từ mô hình lớp phân tích/thiết kế sang mã nguồn Java (`model.*`) và schema CSDL vật lý (`tbl*4`).

---

## 1. Bảng ánh xạ các lớp nghiệp vụ và thống kê

| Tên lớp Java | Thuộc tính Java | Kiểu Java | Cột SQL / Nguồn | Kiểu SQL | Ý nghĩa & Ghi chú |
|---|---|---|---|---|---|
| **Member4** | `id` | `int` | `tblMember4.id` | `INT PK AI` | Mã định danh |
| Member4 | `hoTen` | `String` | `tblMember4.hoTen` | `VARCHAR(255)` | Họ tên |
| Member4 | `tenDangNhap` | `String` | `tblMember4.tenDangNhap` | `VARCHAR(100)` | Tên đăng nhập |
| Member4 | `matKhau` | `String` | `tblMember4.matKhau` | `VARCHAR(255)` | Chuỗi hash PBKDF2 |
| Member4 | `ngaySinh` | `LocalDate` | `tblMember4.ngaySinh` | `DATE` | Ngày sinh |
| Member4 | `diaChi` | `String` | `tblMember4.diaChi` | `TEXT` | Địa chỉ |
| Member4 | `email` | `String` | `tblMember4.email` | `VARCHAR(255)` | Email |
| Member4 | `dienThoai` | `String` | `tblMember4.dienThoai` | `VARCHAR(20)` | Số điện thoại |
| Member4 | `vaiTro` | `String` | `tblMember4.vaiTro` | `VARCHAR(30)` | Vai trò |
| Member4 | `trangThaiTaiKhoan`| `String` | `tblMember4.trangThaiTaiKhoan` | `VARCHAR(30)` | Trạng thái tài khoản |
| **Employee4** | *(kế thừa Member4)*| — | — | — | Kế thừa toàn bộ thuộc tính Member4 |
| Employee4 | `viTri` | `String` | `tblEmployee4.viTri` | `VARCHAR(30)` | Vị trí làm việc |
| Employee4 | `trangThaiLamViec` | `String` | `tblEmployee4.trangThaiLamViec` | `VARCHAR(30)` | Trạng thái làm việc |
| **Customer4** | *(kế thừa Member4)*| — | `tblCustomer4.tblMember4id` | `INT PK` | Khóa chính tham chiếu Member4 |
| **Manager4** | *(kế thừa Employee4)*| — | — | — | Lớp Java vai trò Quản lí |
| **WarehouseStaff4**| *(kế thừa Employee4)*| — | — | — | Lớp Java vai trò Kho |
| **SalesStaff4** | *(kế thừa Employee4)*| — | — | — | Lớp Java vai trò Bán hàng |
| **Technician4** | `id` | `int` | `tblTechnician4.id` | `INT PK AI` | Mã định danh kỹ thuật viên |
| Technician4 | `hoTen` | `String` | `tblTechnician4.hoTen` | `VARCHAR(255)` | Họ tên |
| Technician4 | `ngaySinh` | `LocalDate` | `tblTechnician4.ngaySinh` | `DATE` | Ngày sinh |
| Technician4 | `diaChi` | `String` | `tblTechnician4.diaChi` | `TEXT` | Địa chỉ |
| Technician4 | `email` | `String` | `tblTechnician4.email` | `VARCHAR(255)` | Email |
| Technician4 | `dienThoai` | `String` | `tblTechnician4.dienThoai` | `VARCHAR(20)` | Điện thoại |
| Technician4 | `chuyenMon` | `String` | `tblTechnician4.chuyenMon` | `VARCHAR(255)` | Chuyên môn kỹ thuật |
| Technician4 | `trangThai` | `String` | `tblTechnician4.trangThai` | `VARCHAR(30)` | Trạng thái công việc |
| **Supplier4** | `id` | `int` | `tblSupplier4.id` | `INT PK AI` | Mã nhà cung cấp |
| Supplier4 | `ten` | `String` | `tblSupplier4.ten` | `VARCHAR(255)` | Tên nhà cung cấp |
| Supplier4 | `diaChi` | `String` | `tblSupplier4.diaChi` | `TEXT` | Địa chỉ |
| Supplier4 | `dienThoai` | `String` | `tblSupplier4.dienThoai` | `VARCHAR(20)` | Điện thoại |
| Supplier4 | `email` | `String` | `tblSupplier4.email` | `VARCHAR(255)` | Email |
| Supplier4 | `trangThai` | `String` | `tblSupplier4.trangThai` | `VARCHAR(30)` | Trạng thái hoạt động |
| **Car4** | `id` | `int` | `tblCar4.id` | `INT PK AI` | Mã xe ô tô |
| Car4 | `bienSo` | `String` | `tblCar4.bienSo` | `VARCHAR(20)` | Biển số xe (UNIQUE) |
| Car4 | `hangXe` | `String` | `tblCar4.hangXe` | `VARCHAR(255)` | Hãng xe |
| Car4 | `dongXe` | `String` | `tblCar4.dongXe` | `VARCHAR(255)` | Dòng xe |
| Car4 | `namSanXuat` | `Integer` | `tblCar4.namSanXuat` | `INT` | Năm sản xuất (nullable) |
| **SparePartCategory4**| `id`| `int` | `tblSparePartCategory4.id` | `INT PK AI` | Mã nhóm phụ tùng |
| SparePartCategory4 | `ten` | `String` | `tblSparePartCategory4.ten` | `VARCHAR(255)` | Tên nhóm (UNIQUE) |
| SparePartCategory4 | `moTa` | `String` | `tblSparePartCategory4.moTa` | `TEXT` | Mô tả nhóm |
| SparePartCategory4 | `trangThai` | `String` | `tblSparePartCategory4.trangThai` | `VARCHAR(30)` | Trạng thái nhóm |
| **Service4** | `id` | `int` | `tblService4.id` | `INT PK AI` | Mã dịch vụ |
| Service4 | `ten` | `String` | `tblService4.ten` | `VARCHAR(255)` | Tên dịch vụ |
| Service4 | `nhom` | `String` | `tblService4.nhom` | `VARCHAR(255)` | Tên nhóm dịch vụ |
| Service4 | `moTa` | `String` | `tblService4.moTa` | `TEXT` | Mô tả tiền công |
| Service4 | `noiDung` | `String` | `tblService4.noiDung` | `TEXT` | Quy trình kỹ thuật |
| Service4 | `hinhAnh` | `String` | `tblService4.hinhAnh` | `VARCHAR(255)` | Đường dẫn ảnh asset |
| Service4 | `donGia` | `BigDecimal` | `tblService4.donGia` | `DECIMAL(18,2)` | Tiền công |
| Service4 | `thoiGianDuKien` | `int` | `tblService4.thoiGianDuKien` | `INT` | Thời gian thực hiện (phút) |
| Service4 | `hanBaoHanh` | `String` | `tblService4.hanBaoHanh` | `VARCHAR(255)` | Hạn bảo hành |
| Service4 | `dieuKienBaoHanh`| `String` | `tblService4.dieuKienBaoHanh` | `TEXT` | Điều kiện bảo hành |
| Service4 | `trangThai` | `String` | `tblService4.trangThai` | `VARCHAR(30)` | Trạng thái dịch vụ |
| **SparePart4** | `id` | `int` | `tblSparePart4.id` | `INT PK AI` | Mã phụ tùng |
| SparePart4 | `ten` | `String` | `tblSparePart4.ten` | `VARCHAR(255)` | Tên phụ tùng |
| SparePart4 | `moTa` | `String` | `tblSparePart4.moTa` | `TEXT` | Mô tả phụ tùng |
| SparePart4 | `hinhAnh` | `String` | `tblSparePart4.hinhAnh` | `VARCHAR(255)` | Đường dẫn asset ảnh |
| SparePart4 | `donVi` | `String` | `tblSparePart4.donVi` | `VARCHAR(20)` | Đơn vị tính |
| SparePart4 | `hangSanXuat` | `String` | `tblSparePart4.hangSanXuat` | `VARCHAR(255)` | Hãng sản xuất |
| SparePart4 | `thongSo` | `String` | `tblSparePart4.thongSo` | `TEXT` | Thông số kỹ thuật |
| SparePart4 | `dongXePhuHop` | `String` | `tblSparePart4.dongXePhuHop` | `TEXT` | Dòng xe tương thích |
| SparePart4 | `giaBan` | `BigDecimal` | `tblSparePart4.giaBan` | `DECIMAL(18,2)` | Giá bán niêm yết |
| SparePart4 | `hanBaoHanh` | `String` | `tblSparePart4.hanBaoHanh` | `VARCHAR(255)` | Hạn bảo hành |
| SparePart4 | `dieuKienBaoHanh`| `String` | `tblSparePart4.dieuKienBaoHanh` | `TEXT` | Điều kiện bảo hành |
| SparePart4 | `trangThai` | `String` | `tblSparePart4.trangThai` | `VARCHAR(30)` | Trạng thái kinh doanh |
| SparePart4 | `tonKho` | `BigDecimal` | `tblSparePart4.tonKho` | `DECIMAL(12,3)` | Số lượng tồn kho lưu trữ |
| SparePart4 | `nhom` | `SparePartCategory4` | `tblSparePartCategory4id` | `INT FK` | Nhóm phụ tùng |
| **Appointment4** | `id` | `int` | `tblAppointment4.id` | `INT PK AI` | Mã lịch hẹn |
| Appointment4 | `ngayGioHen` | `LocalDateTime` | `tblAppointment4.ngayGioHen` | `DATETIME` | Ngày giờ hẹn |
| Appointment4 | `thoiDiemDat` | `LocalDateTime` | `tblAppointment4.thoiDiemDat` | `DATETIME` | Thời điểm đặt |
| Appointment4 | `yeuCau` | `String` | `tblAppointment4.yeuCau` | `TEXT` | Yêu cầu khách |
| Appointment4 | `ghiChu` | `String` | `tblAppointment4.ghiChu` | `TEXT` | Ghi chú |
| Appointment4 | `trangThai` | `String` | `tblAppointment4.trangThai` | `VARCHAR(30)` | Trạng thái lịch |
| Appointment4 | `hoTenLienHe` | `String` | `tblAppointment4.hoTenLienHe` | `VARCHAR(255)` | Tên liên hệ |
| Appointment4 | `dienThoaiLienHe`| `String` | `tblAppointment4.dienThoaiLienHe` | `VARCHAR(20)` | SĐT liên hệ |
| Appointment4 | `khachHang` | `Customer4` | `tblCustomer4id` | `INT FK` | Khách hàng |
| Appointment4 | `oTo` | `Car4` | `tblCar4id` | `INT FK` | Xe ô tô |
| **RepairIntake4** | `id` | `int` | `tblRepairIntake4.id` | `INT PK AI` | Mã phiếu tiếp nhận |
| RepairIntake4 | `thoiDiemTiepNhan`| `LocalDateTime` | `tblRepairIntake4.thoiDiemTiepNhan` | `DATETIME` | Thời điểm tiếp nhận |
| RepairIntake4 | `soKm` | `BigDecimal` | `tblRepairIntake4.soKm` | `DECIMAL(12,1)` | Số km |
| RepairIntake4 | `tinhTrangXe` | `String` | `tblRepairIntake4.tinhTrangXe` | `TEXT` | Tình trạng xe |
| RepairIntake4 | `trangThai` | `String` | `tblRepairIntake4.trangThai` | `VARCHAR(30)` | Trạng thái phiếu |
| RepairIntake4 | `ghiChu` | `String` | `tblRepairIntake4.ghiChu` | `TEXT` | Ghi chú |
| RepairIntake4 | `khachHang` | `Customer4` | `tblCustomer4id` | `INT FK` | Khách hàng |
| RepairIntake4 | `oTo` | `Car4` | `tblCar4id` | `INT FK` | Xe ô tô |
| RepairIntake4 | `nhanVienTiepNhan`| `SalesStaff4` | `tblEmployee4id` | `INT FK` | Nhân viên tiếp nhận |
| RepairIntake4 | `lichHen` | `Appointment4` | `tblAppointment4id` | `INT FK UNIQUE` | Lịch hẹn tương ứng |
| RepairIntake4 | `dsDichVu` | `List<RepairService4>` | *(1-n qua bảng dòng)* | — | Danh sách dịch vụ |
| RepairIntake4 | `dsPhuTung` | `List<RepairPart4>` | *(1-n qua bảng dòng)* | — | Danh sách phụ tùng |
| **RepairService4** | `id` | `int` | `tblRepairService4.id` | `INT PK AI` | Mã dòng dịch vụ tiếp nhận |
| RepairService4 | `donGiaApDung` | `BigDecimal` | `tblRepairService4.donGiaApDung` | `DECIMAL(18,2)` | Đơn giá tiền công chốt |
| RepairService4 | `hanBaoHanhApDung`| `String` | `tblRepairService4.hanBaoHanhApDung` | `VARCHAR(255)` | Bảo hành áp dụng |
| RepairService4 | `dieuKienBaoHanhApDung`| `String` | `tblRepairService4.dieuKienBaoHanhApDung` | `TEXT` | ĐK bảo hành áp dụng |
| RepairService4 | `ghiChu` | `String` | `tblRepairService4.ghiChu` | `TEXT` | Ghi chú |
| RepairService4 | `tenDichVuApDung`| `String` | `tblRepairService4.tenDichVuApDung` | `VARCHAR(255)` | Tên dịch vụ chốt |
| RepairService4 | `dichVu` | `Service4` | `tblService4id` | `INT FK` | Dịch vụ gốc |
| RepairService4 | `nhanVienKyThuat`| `Technician4` | `tblTechnician4id` | `INT FK` | Kỹ thuật viên |
| **RepairPart4** | `id` | `int` | `tblRepairPart4.id` | `INT PK AI` | Mã dòng phụ tùng tiếp nhận |
| RepairPart4 | `soLuongYeuCau`| `BigDecimal` | `tblRepairPart4.soLuongYeuCau` | `DECIMAL(12,3)` | Lượng yêu cầu |
| RepairPart4 | `soLuongThucTe`| `BigDecimal` | `tblRepairPart4.soLuongThucTe` | `DECIMAL(12,3)` | Lượng thực tế đã dùng |
| RepairPart4 | `donGiaApDung` | `BigDecimal` | `tblRepairPart4.donGiaApDung` | `DECIMAL(18,2)` | Đơn giá xuất chốt |
| RepairPart4 | `hanBaoHanhApDung`| `String` | `tblRepairPart4.hanBaoHanhApDung` | `VARCHAR(255)` | Bảo hành áp dụng |
| RepairPart4 | `dieuKienBaoHanhApDung`| `String` | `tblRepairPart4.dieuKienBaoHanhApDung` | `TEXT` | ĐK bảo hành áp dụng |
| RepairPart4 | `ghiChu` | `String` | `tblRepairPart4.ghiChu` | `TEXT` | Ghi chú |
| RepairPart4 | `tenPhuTungApDung`| `String` | `tblRepairPart4.tenPhuTungApDung` | `VARCHAR(255)` | Tên phụ tùng chốt |
| RepairPart4 | `donViApDung` | `String` | `tblRepairPart4.donViApDung` | `VARCHAR(20)` | Đơn vị tính chốt |
| RepairPart4 | `phuTung` | `SparePart4` | `tblSparePart4id` | `INT FK` | Phụ tùng gốc |
| RepairPart4 | `thanhTien` | `BigDecimal` | *(Dẫn xuất)* | — | `tinhThanhTien()` = SL thực tế * đơn giá |
| **CustomerInvoice4**| `id` | `int` | `tblCustomerInvoice4.id` | `INT PK AI` | Mã hóa đơn khách |
| CustomerInvoice4 | `thoiDiemLap` | `LocalDateTime` | `tblCustomerInvoice4.thoiDiemLap` | `DATETIME` | Thời điểm lập hóa đơn |
| CustomerInvoice4 | `thoiDiemXacNhan`| `LocalDateTime` | `tblCustomerInvoice4.thoiDiemXacNhan` | `DATETIME` | Thời điểm xác nhận |
| CustomerInvoice4 | `tinhTrang` | `String` | `tblCustomerInvoice4.tinhTrang` | `VARCHAR(30)` | Tình trạng thanh toán |
| CustomerInvoice4 | `thoiDiemThanhToan`| `LocalDateTime` | `tblCustomerInvoice4.thoiDiemThanhToan` | `DATETIME` | Thời điểm thu tiền |
| CustomerInvoice4 | `phieuTiepNhan` | `RepairIntake4` | `tblRepairIntake4id` | `INT FK UNIQUE` | Phiếu tiếp nhận tương ứng |
| CustomerInvoice4 | `nhanVienLap` | `SalesStaff4` | `tblEmployee4id` | `INT FK` | Người lập kiêm thu tiền |
| CustomerInvoice4 | `hoTenKhachHangApDung`| `String`| `tblCustomerInvoice4.hoTenKhachHangApDung` | `VARCHAR(255)`| Snapshot tên khách |
| CustomerInvoice4 | `bienSoApDung` | `String` | `tblCustomerInvoice4.bienSoApDung` | `VARCHAR(20)` | Snapshot biển số |
| CustomerInvoice4 | `nhanVienTenApDung`| `String` | `tblCustomerInvoice4.nhanVienTenApDung` | `VARCHAR(255)`| Snapshot tên người lập |
| CustomerInvoice4 | `tongTien` | `BigDecimal` | *(Dẫn xuất)* | — | `tinhTongTien()` tổng dịch vụ + phụ tùng |
| **PurchaseInvoice4**| `id` | `int` | `tblPurchaseInvoice4.id` | `INT PK AI` | Mã hóa đơn nhập |
| PurchaseInvoice4 | `ngayNhap` | `LocalDateTime` | `tblPurchaseInvoice4.ngayNhap` | `DATETIME` | Ngày nhập |
| PurchaseInvoice4 | `thoiDiemXacNhan`| `LocalDateTime` | `tblPurchaseInvoice4.thoiDiemXacNhan` | `DATETIME` | Thời điểm xác nhận nhập |
| PurchaseInvoice4 | `tinhTrang` | `String` | `tblPurchaseInvoice4.tinhTrang` | `VARCHAR(30)` | Tình trạng thanh toán |
| PurchaseInvoice4 | `thoiDiemThanhToan`| `LocalDateTime` | `tblPurchaseInvoice4.thoiDiemThanhToan` | `DATETIME` | Thời điểm thanh toán |
| PurchaseInvoice4 | `nhaCungCap` | `Supplier4` | `tblSupplier4id` | `INT FK` | Nhà cung cấp |
| PurchaseInvoice4 | `nhanVienKho` | `WarehouseStaff4`| `tblEmployee4id` | `INT FK` | Nhân viên kho lập |
| PurchaseInvoice4 | `maYeuCau` | `String` | `tblPurchaseInvoice4.maYeuCau` | `CHAR(36) UNIQUE` | UUID chống trùng lặp |
| PurchaseInvoice4 | `nhaCungCapTenApDung`| `String` | `tblPurchaseInvoice4.nhaCungCapTenApDung` | `VARCHAR(255)` | Snapshot tên NCC |
| PurchaseInvoice4 | `nhanVienTenApDung`| `String` | `tblPurchaseInvoice4.nhanVienTenApDung` | `VARCHAR(255)` | Snapshot tên nhân viên kho |
| PurchaseInvoice4 | `dsChiTietNhap` | `List<PurchaseInvoiceDetail4>` | *(1-n qua bảng dòng)* | — | Danh sách chi tiết nhập |
| PurchaseInvoice4 | `tongTien` | `BigDecimal` | *(Dẫn xuất)* | — | `tinhTongTien()` tổng tiền các dòng |
| **PurchaseInvoiceDetail4**| `id` | `int` | `tblPurchaseInvoiceDetail4.id` | `INT PK AI` | Mã chi tiết nhập |
| PurchaseInvoiceDetail4 | `soLuong` | `BigDecimal` | `tblPurchaseInvoiceDetail4.soLuong` | `DECIMAL(12,3)` | Số lượng nhập |
| PurchaseInvoiceDetail4 | `donGiaNhap` | `BigDecimal` | `tblPurchaseInvoiceDetail4.donGiaNhap` | `DECIMAL(18,2)` | Đơn giá nhập |
| PurchaseInvoiceDetail4 | `phuTung` | `SparePart4` | `tblSparePart4id` | `INT FK` | Phụ tùng nhập |
| PurchaseInvoiceDetail4 | `tenPhuTungApDung`| `String` | `tblPurchaseInvoiceDetail4.tenPhuTungApDung`| `VARCHAR(255)`| Snapshot tên phụ tùng |
| PurchaseInvoiceDetail4 | `donViApDung` | `String` | `tblPurchaseInvoiceDetail4.donViApDung` | `VARCHAR(20)` | Snapshot đơn vị tính |
| PurchaseInvoiceDetail4 | `thanhTien` | `BigDecimal` | *(Dẫn xuất)* | — | `tinhThanhTien()` = SL * đơn giá nhập |
| **ServiceStatistics4** | `soLuotSuDung`| `int` | *(Thống kê POJO)* | — | Số lượt sử dụng dịch vụ |
| ServiceStatistics4 | `doanhThu` | `BigDecimal` | *(Thống kê POJO)* | — | Doanh thu dịch vụ |
| **SparePartStatistics4**| `soLuongSuDung`| `BigDecimal`| *(Thống kê POJO)* | — | Tổng lượng phụ tùng sử dụng |
| SparePartStatistics4 | `doanhThu` | `BigDecimal` | *(Thống kê POJO)* | — | Doanh thu phụ tùng |
| **CustomerStatistics4** | `soLanSuaXe` | `int` | *(Thống kê POJO)* | — | Số lượt khách sửa xe |
| CustomerStatistics4 | `doanhThu` | `BigDecimal` | *(Thống kê POJO)* | — | Doanh thu từ khách hàng |
| **SupplierStatistics4** | `luongNhap` | `BigDecimal` | *(Thống kê POJO)* | — | Tổng lượng nhập theo đơn vị |
| SupplierStatistics4 | `donVi` | `String` | *(Thống kê POJO)* | — | Đơn vị tính tương ứng |
