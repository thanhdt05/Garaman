# Từ Điển Dữ Liệu CSDL GaraMan (Đề 26)

**Hệ quản trị CSDL:** MySQL 8.4 / 8.0 (InnoDB)  
**Bảng mã:** `utf8mb4` | **Collation:** `utf8mb4_0900_ai_ci`  
**Quy ước:** 16 bảng vật lý; tên bảng và tên cột giữ nguyên camelCase kèm hậu tố 4 theo đúng thiết kế CSDL.

---

## 1. Danh mục 16 bảng

| STT | Tên bảng | Ý nghĩa nghiệp vụ | Ghi chú cấu trúc |
|---|---|---|---|
| 1 | `tblMember4` | Thành viên toàn hệ thống (tài khoản, thông tin chung) | Bảng cha của Employee, Customer |
| 2 | `tblEmployee4` | Nhân viên gara ô tô | Kế thừa tblMember4, PK là FK cha |
| 3 | `tblCustomer4` | Khách hàng của gara | Kế thừa tblMember4, PK là FK cha |
| 4 | `tblTechnician4` | Kỹ thuật viên sửa chữa | Độc lập, không có tài khoản login |
| 5 | `tblSupplier4` | Nhà cung cấp phụ tùng | Đang hoạt động / Ngừng hoạt động |
| 6 | `tblCar4` | Xe ô tô khách mang đến sửa chữa/bảo dưỡng | Biển số UNIQUE |
| 7 | `tblSparePartCategory4` | Nhóm phụ tùng | Tên UNIQUE, Đang sử dụng / Ngừng sử dụng |
| 8 | `tblService4` | Danh mục dịch vụ sửa chữa / bảo dưỡng | Đang cung cấp / Ngừng cung cấp |
| 9 | `tblSparePart4` | Danh mục phụ tùng | Lưu cột `tonKho DECIMAL(12,3)` trực tiếp |
| 10 | `tblAppointment4` | Lịch hẹn đặt trực tuyến của khách | Có 2 trường liên hệ bổ sung |
| 11 | `tblRepairIntake4` | Phiếu tiếp nhận xe sửa chữa tại xưởng | Liên kết xe, khách, người tiếp nhận |
| 12 | `tblRepairService4` | Dịch vụ được chỉ định trong phiếu tiếp nhận | Chốt tên/giá/bảo hành áp dụng |
| 13 | `tblRepairPart4` | Phụ tùng sử dụng trong phiếu tiếp nhận | Chốt tên/đơn vị/giá áp dụng, lượng thực tế |
| 14 | `tblCustomerInvoice4` | Hóa đơn thanh toán của khách hàng | 1 người lập kiêm thu tiền, chốt lịch sử |
| 15 | `tblPurchaseInvoice4` | Hóa đơn nhập phụ tùng từ nhà cung cấp | 1 người lập kiêm trả tiền, maYeuCau UUID |
| 16 | `tblPurchaseInvoiceDetail4` | Chi tiết từng dòng phụ tùng nhập | Chốt tên/đơn vị áp dụng, số lượng, đơn giá |

---

## 2. Chi tiết cấu trúc từng bảng

### 2.1. `tblMember4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã định danh thành viên |
| `hoTen` | `VARCHAR(255)` | NOT NULL | Họ và tên đầy đủ |
| `tenDangNhap` | `VARCHAR(100)` | NULL, UNIQUE | Tên đăng nhập (Customer vãng lai có thể NULL) |
| `matKhau` | `VARCHAR(255)` | NULL | Chuỗi hash PBKDF2WithHmacSHA256 |
| `ngaySinh` | `DATE` | NULL | Ngày sinh |
| `diaChi` | `TEXT` | NULL | Địa chỉ liên hệ |
| `email` | `VARCHAR(255)` | NULL | Thư điện tử |
| `dienThoai` | `VARCHAR(20)` | NOT NULL | Số điện thoại liên hệ (8-15 ký tự) |
| `vaiTro` | `VARCHAR(30)` | NOT NULL, CHECK | 'Customer', 'Manager', 'WarehouseStaff', 'SalesStaff' |
| `trangThaiTaiKhoan`| `VARCHAR(30)` | NULL, CHECK | 'Hoạt động', 'Đã khóa' |

### 2.2. `tblEmployee4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `tblMember4id` | `INT` | PK, FK → tblMember4(id) | Khóa chính đồng thời là khóa ngoại kế thừa |
| `viTri` | `VARCHAR(30)` | NOT NULL, CHECK | 'Quản lí', 'Kho', 'Bán hàng' |
| `trangThaiLamViec`| `VARCHAR(30)` | NOT NULL, CHECK | 'Đang làm việc', 'Ngừng làm việc' |

### 2.3. `tblCustomer4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `tblMember4id` | `INT` | PK, FK → tblMember4(id) | Khóa chính đồng thời là khóa ngoại kế thừa |

### 2.4. `tblTechnician4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã định danh kỹ thuật viên |
| `hoTen` | `VARCHAR(255)` | NOT NULL | Họ tên kỹ thuật viên |
| `ngaySinh` | `DATE` | NULL | Ngày sinh |
| `diaChi` | `TEXT` | NULL | Địa chỉ |
| `email` | `VARCHAR(255)` | NULL | Email |
| `dienThoai` | `VARCHAR(20)` | NOT NULL | Số điện thoại |
| `chuyenMon` | `VARCHAR(255)` | NOT NULL | Lĩnh vực chuyên môn (điện, gầm, động cơ...) |
| `trangThai` | `VARCHAR(30)` | NOT NULL, CHECK | 'Đang làm việc', 'Ngừng làm việc' |

### 2.5. `tblSupplier4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã nhà cung cấp |
| `ten` | `VARCHAR(255)` | NOT NULL | Tên nhà cung cấp |
| `diaChi` | `TEXT` | NOT NULL | Địa chỉ giao dịch/kho |
| `dienThoai` | `VARCHAR(20)` | NOT NULL | Số điện thoại liên hệ |
| `email` | `VARCHAR(255)` | NULL | Email |
| `trangThai` | `VARCHAR(30)` | NOT NULL, DEFAULT, CHECK | 'Đang hoạt động', 'Ngừng hoạt động' |

### 2.6. `tblCar4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã xe |
| `bienSo` | `VARCHAR(20)` | NOT NULL, UNIQUE | Biển kiểm soát |
| `hangXe` | `VARCHAR(255)` | NOT NULL | Hãng sản xuất (Toyota, Ford...) |
| `dongXe` | `VARCHAR(255)` | NOT NULL | Dòng xe (Vios, Ranger...) |
| `namSanXuat` | `INT` | NULL | Năm sản xuất |

### 2.7. `tblSparePartCategory4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã nhóm phụ tùng |
| `ten` | `VARCHAR(255)` | NOT NULL, UNIQUE | Tên nhóm |
| `moTa` | `TEXT` | NULL | Mô tả phân loại |
| `trangThai` | `VARCHAR(30)` | NOT NULL, DEFAULT, CHECK | 'Đang sử dụng', 'Ngừng sử dụng' |

### 2.8. `tblService4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã dịch vụ |
| `ten` | `VARCHAR(255)` | NOT NULL | Tên dịch vụ |
| `nhom` | `VARCHAR(255)` | NOT NULL | Nhóm phân loại dịch vụ |
| `moTa` | `TEXT` | NOT NULL | Mô tả tóm tắt tiền công |
| `noiDung` | `TEXT` | NOT NULL | Quy trình kỹ thuật thực hiện |
| `hinhAnh` | `VARCHAR(255)` | NULL | Đường dẫn ảnh minh họa cục bộ |
| `donGia` | `DECIMAL(18,2)` | NOT NULL, CHECK >= 0 | Tiền công dịch vụ (chưa kèm phụ tùng) |
| `thoiGianDuKien` | `INT` | NOT NULL, CHECK > 0 | Thời gian thực hiện dự kiến (phút) |
| `hanBaoHanh` | `VARCHAR(255)` | NULL | Ví dụ: "6 tháng" |
| `dieuKienBaoHanh`| `TEXT` | NULL | Điều kiện áp dụng bảo hành |
| `trangThai` | `VARCHAR(30)` | NOT NULL, CHECK | 'Đang cung cấp', 'Ngừng cung cấp' |

### 2.9. `tblSparePart4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã phụ tùng |
| `ten` | `VARCHAR(255)` | NOT NULL | Tên phụ tùng |
| `moTa` | `TEXT` | NULL | Mô tả chi tiết |
| `hinhAnh` | `VARCHAR(255)` | NULL | Đường dẫn asset cục bộ |
| `donVi` | `VARCHAR(20)` | NOT NULL, CHECK | 'Cái', 'Chiếc', 'Bộ', 'Lít' |
| `hangSanXuat` | `VARCHAR(255)` | NOT NULL | Hãng sản xuất phụ tùng |
| `thongSo` | `TEXT` | NULL | Thông số kỹ thuật |
| `dongXePhuHop` | `TEXT` | NULL | Dòng xe tương thích |
| `giaBan` | `DECIMAL(18,2)` | NULL, CHECK >= 0 | Giá niêm yết bán lẻ |
| `hanBaoHanh` | `VARCHAR(255)` | NULL | Thời hạn bảo hành |
| `dieuKienBaoHanh`| `TEXT` | NULL | Điều kiện bảo hành |
| `trangThai` | `VARCHAR(30)` | NOT NULL, DEFAULT, CHECK | 'Chưa công bố', 'Đang kinh doanh', 'Ngừng kinh doanh' |
| `tonKho` | `DECIMAL(12,3)` | NOT NULL, DEFAULT 0, CHECK >= 0 | **Lưu trữ trực tiếp**, cập nhật sau giao dịch |
| `tblSparePartCategory4id` | `INT` | NOT NULL, FK → tblSparePartCategory4 | Nhóm phụ tùng |

### 2.10. `tblAppointment4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã lịch hẹn |
| `ngayGioHen` | `DATETIME` | NOT NULL | Thời điểm khách hẹn đến xưởng |
| `thoiDiemDat` | `DATETIME` | NOT NULL | Thời điểm tạo lịch hẹn trực tuyến |
| `yeuCau` | `TEXT` | NULL | Nội dung yêu cầu |
| `ghiChu` | `TEXT` | NULL | Ghi chú thêm |
| `trangThai` | `VARCHAR(30)` | NOT NULL, CHECK | 'Đã đặt', 'Đã tiếp nhận', 'Đã hủy' |
| `tblCustomer4id` | `INT` | NOT NULL, FK → tblCustomer4 | Khách hàng đặt lịch |
| `tblCar4id` | `INT` | NOT NULL, FK → tblCar4 | Xe cần bảo dưỡng |
| `hoTenLienHe` | `VARCHAR(255)` | NOT NULL | **[Bổ sung]** Họ tên liên hệ lúc đặt |
| `dienThoaiLienHe`| `VARCHAR(20)` | NOT NULL | **[Bổ sung]** SĐT liên hệ lúc đặt |

### 2.11. `tblRepairIntake4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã phiếu tiếp nhận |
| `thoiDiemTiepNhan`| `DATETIME` | NOT NULL | Thời điểm tiếp nhận xe |
| `soKm` | `DECIMAL(12,1)` | NULL, CHECK >= 0 | Số km đồng hồ xe |
| `tinhTrangXe` | `TEXT` | NULL | Hiện trạng xe khi nhận |
| `trangThai` | `VARCHAR(30)` | NOT NULL, CHECK | 'Đã tiếp nhận', 'Đang xử lí', 'Hoàn thành', 'Đã hủy' |
| `ghiChu` | `TEXT` | NULL | Ghi chú xưởng |
| `tblCustomer4id` | `INT` | NOT NULL, FK → tblCustomer4 | Khách hàng |
| `tblCar4id` | `INT` | NOT NULL, FK → tblCar4 | Xe |
| `tblEmployee4id` | `INT` | NOT NULL, FK → tblEmployee4 | Nhân viên bán hàng tiếp nhận xe |
| `tblAppointment4id`| `INT` | NULL, UNIQUE, FK → tblAppointment4 | Lịch hẹn tương ứng (0..1) |

### 2.12. `tblRepairService4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã chi tiết dịch vụ tiếp nhận |
| `donGiaApDung` | `DECIMAL(18,2)` | NOT NULL, CHECK >= 0 | Đơn giá tiền công chốt |
| `hanBaoHanhApDung`| `VARCHAR(255)` | NULL | Bảo hành chốt |
| `dieuKienBaoHanhApDung`| `TEXT` | NULL | Điều kiện bảo hành chốt |
| `ghiChu` | `TEXT` | NULL | Ghi chú |
| `tenDichVuApDung`| `VARCHAR(255)` | NOT NULL | **[Bổ sung]** Snapshot tên dịch vụ |
| `tblRepairIntake4id`| `INT` | NOT NULL, FK, UNIQUE (intake, service) | Phiếu tiếp nhận |
| `tblService4id` | `INT` | NOT NULL, FK → tblService4 | Dịch vụ gốc |
| `tblTechnician4id`| `INT` | NOT NULL, FK → tblTechnician4 | Kỹ thuật viên phụ trách |

### 2.13. `tblRepairPart4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã chi tiết phụ tùng tiếp nhận |
| `soLuongYeuCau` | `DECIMAL(12,3)` | NOT NULL, CHECK > 0 | Lượng dự kiến yêu cầu |
| `soLuongThucTe` | `DECIMAL(12,3)` | NOT NULL, DEFAULT 0, CHECK >= 0 | Lượng thực tế đã dùng để sửa xe |
| `donGiaApDung` | `DECIMAL(18,2)` | NOT NULL, CHECK >= 0 | Đơn giá xuất chốt |
| `hanBaoHanhApDung`| `VARCHAR(255)` | NULL | Bảo hành chốt |
| `dieuKienBaoHanhApDung`| `TEXT` | NULL | Điều kiện bảo hành chốt |
| `ghiChu` | `TEXT` | NULL | Ghi chú |
| `tenPhuTungApDung`| `VARCHAR(255)` | NOT NULL | **[Bổ sung]** Snapshot tên phụ tùng |
| `donViApDung` | `VARCHAR(20)` | NOT NULL | **[Bổ sung]** Snapshot đơn vị tính |
| `tblRepairIntake4id`| `INT` | NOT NULL, FK, UNIQUE (intake, part) | Phiếu tiếp nhận |
| `tblSparePart4id`| `INT` | NOT NULL, FK → tblSparePart4 | Phụ tùng gốc |

### 2.14. `tblCustomerInvoice4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã hóa đơn khách hàng |
| `thoiDiemLap` | `DATETIME` | NOT NULL | Thời điểm lập hóa đơn |
| `thoiDiemXacNhan`| `DATETIME` | NOT NULL | Thời điểm xác nhận hoàn thành |
| `tinhTrang` | `VARCHAR(30)` | NOT NULL, DEFAULT, CHECK | 'Chưa thanh toán', 'Đã thanh toán' |
| `thoiDiemThanhToan`| `DATETIME` | NULL | Thời điểm thu tiền |
| `tblRepairIntake4id`| `INT` | NOT NULL, UNIQUE, FK → tblRepairIntake4 | Phiếu tiếp nhận (1 - 1) |
| `tblEmployee4id` | `INT` | NOT NULL, FK → tblEmployee4 | **[Bổ sung]** Nhân viên lập và thu tiền |
| `hoTenKhachHangApDung`| `VARCHAR(255)` | NOT NULL | **[Bổ sung]** Snapshot tên khách |
| `bienSoApDung` | `VARCHAR(20)` | NOT NULL | **[Bổ sung]** Snapshot biển số xe |
| `nhanVienTenApDung`| `VARCHAR(255)` | NOT NULL | **[Bổ sung]** Snapshot tên người lập |

### 2.15. `tblPurchaseInvoice4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã hóa đơn nhập |
| `ngayNhap` | `DATETIME` | NOT NULL | Ngày nhập (bằng thoiDiemXacNhan) |
| `thoiDiemXacNhan`| `DATETIME` | NOT NULL | Thời điểm xác nhận nhập hàng |
| `tinhTrang` | `VARCHAR(30)` | NOT NULL, DEFAULT, CHECK | 'Chưa thanh toán', 'Đã thanh toán' |
| `thoiDiemThanhToan`| `DATETIME` | NULL | Thời điểm trả tiền nhà cung cấp |
| `tblSupplier4id` | `INT` | NOT NULL, FK → tblSupplier4 | Nhà cung cấp |
| `tblEmployee4id` | `INT` | NOT NULL, FK → tblEmployee4 | Nhân viên kho lập và trả tiền |
| `maYeuCau` | `CHAR(36)` | NOT NULL, UNIQUE | **[Bổ sung]** UUID chống trùng lặp |
| `nhaCungCapTenApDung`| `VARCHAR(255)` | NOT NULL | **[Bổ sung]** Snapshot tên nhà cung cấp |
| `nhanVienTenApDung`| `VARCHAR(255)` | NOT NULL | **[Bổ sung]** Snapshot tên người lập |

### 2.16. `tblPurchaseInvoiceDetail4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã chi tiết nhập |
| `soLuong` | `DECIMAL(12,3)` | NOT NULL, CHECK > 0 | Số lượng nhập |
| `donGiaNhap` | `DECIMAL(18,2)` | NOT NULL, CHECK > 0 | Đơn giá nhập |
| `tblPurchaseInvoice4id`| `INT` | NOT NULL, FK, UNIQUE (inv, part) | Hóa đơn nhập |
| `tblSparePart4id`| `INT` | NOT NULL, FK → tblSparePart4 | Phụ tùng nhập |
| `tenPhuTungApDung`| `VARCHAR(255)` | NOT NULL | **[Bổ sung]** Snapshot tên phụ tùng |
| `donViApDung` | `VARCHAR(20)` | NOT NULL | **[Bổ sung]** Snapshot đơn vị tính |
