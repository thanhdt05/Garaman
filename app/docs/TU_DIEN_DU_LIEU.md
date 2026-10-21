# Từ Điển Dữ Liệu CSDL GaraMan (Đề 26)

**Hệ quản trị CSDL:** MySQL 8.4 / 8.0 (InnoDB)  
**Bảng mã:** `utf8mb4` | **Collation:** `utf8mb4_0900_ai_ci`  
**Quy ước:** Chuẩn hóa đúng 9 bảng vật lý phục vụ hai module đề 26 (Khách hàng tra cứu dịch vụ/phụ tùng; Nhân viên kho nhập phụ tùng); tên bảng và tên cột giữ nguyên camelCase kèm hậu tố 4 theo đúng thiết kế CSDL và tài liệu kế hoạch `00_PHAM_VI.md`, `03_HOP_DONG.md`.

---

## 1. Danh mục 9 bảng CSDL trong phạm vi đề 26

| STT | Tên bảng | Ý nghĩa nghiệp vụ | Ghi chú cấu trúc |
|---|---|---|---|
| 1 | `tblMember4` | Thành viên hệ thống (tài khoản, thông tin chung) | Bảng cha của Employee, Customer |
| 2 | `tblEmployee4` | Nhân viên gara (Thủ kho) | Kế thừa tblMember4, PK là FK cha |
| 3 | `tblCustomer4` | Khách hàng của gara | Kế thừa tblMember4, PK là FK cha |
| 4 | `tblSupplier4` | Nhà cung cấp phụ tùng | Đang hoạt động / Ngừng hoạt động |
| 5 | `tblSparePartCategory4` | Nhóm phụ tùng | Tên UNIQUE, Đang sử dụng / Ngừng sử dụng |
| 6 | `tblService4` | Danh mục dịch vụ bảo dưỡng / sửa chữa | Đang cung cấp / Ngừng cung cấp |
| 7 | `tblSparePart4` | Danh mục phụ tùng | Lưu cột `stockQuantity DECIMAL(12,3)` trực tiếp |
| 8 | `tblPurchaseInvoice4` | Hóa đơn nhập phụ tùng từ nhà cung cấp | 1 người lập kiêm trả tiền, requestCode UUID |
| 9 | `tblPurchaseInvoiceDetail4` | Chi tiết từng dòng phụ tùng nhập | Chốt tên/đơn vị áp dụng, số lượng, đơn giá |

---

## 2. Chi tiết cấu trúc từng bảng

### 2.1. `tblMember4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã định danh thành viên |
| `fullName` | `VARCHAR(255)` | NOT NULL | Họ và tên đầy đủ |
| `username` | `VARCHAR(100)` | NULL, UNIQUE | Tên đăng nhập (Customer vãng lai có thể NULL) |
| `password` | `VARCHAR(255)` | NULL | Chuỗi hash PBKDF2WithHmacSHA256 |
| `dateOfBirth` | `DATE` | NULL | Ngày sinh |
| `address` | `TEXT` | NULL | Địa chỉ liên hệ |
| `email` | `VARCHAR(255)` | NULL | Thư điện tử |
| `phoneNumber` | `VARCHAR(20)` | NOT NULL | Số điện thoại liên hệ (8-15 ký tự) |
| `role` | `VARCHAR(30)` | NOT NULL, CHECK | 'Customer', 'WarehouseStaff' |
| `accountStatus`| `VARCHAR(30)` | NULL, CHECK | 'Hoạt động', 'Đã khóa' |

*Ràng buộc bổ sung:*
- `chk_member_account`: `(username IS NULL AND password IS NULL AND accountStatus IS NULL AND role = 'Customer') OR (username IS NOT NULL AND password IS NOT NULL AND accountStatus IS NOT NULL)`

### 2.2. `tblEmployee4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `tblMember4id` | `INT` | PK, FK → tblMember4(id) | Khóa chính đồng thời là khóa ngoại kế thừa |
| `position` | `VARCHAR(30)` | NOT NULL, CHECK | 'Kho' |
| `employmentStatus`| `VARCHAR(30)` | NOT NULL, CHECK | 'Đang làm việc', 'Ngừng làm việc' |

### 2.3. `tblCustomer4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `tblMember4id` | `INT` | PK, FK → tblMember4(id) | Khóa chính đồng thời là khóa ngoại kế thừa (Customer4) |

### 2.4. `tblSupplier4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã nhà cung cấp |
| `name` | `VARCHAR(255)` | NOT NULL | Tên nhà cung cấp |
| `address` | `TEXT` | NOT NULL | Địa chỉ trụ sở / kho NCC |
| `phoneNumber` | `VARCHAR(20)` | NOT NULL | Số điện thoại liên hệ |
| `email` | `VARCHAR(255)` | NULL | Thư điện tử |
| `status` | `VARCHAR(30)` | NOT NULL, CHECK | 'Đang hoạt động', 'Ngừng hoạt động' (Default: Đang hoạt động) |

### 2.5. `tblSparePartCategory4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã nhóm phụ tùng |
| `name` | `VARCHAR(255)` | NOT NULL, UNIQUE | Tên nhóm phụ tùng |
| `description` | `TEXT` | NULL | Mô tả chi tiết nhóm |
| `status` | `VARCHAR(30)` | NOT NULL, CHECK | 'Đang sử dụng', 'Ngừng sử dụng' (Default: Đang sử dụng) |

### 2.6. `tblService4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã dịch vụ |
| `name` | `VARCHAR(255)` | NOT NULL | Tên dịch vụ |
| `category` | `VARCHAR(255)` | NOT NULL | Nhóm dịch vụ (Bảo dưỡng, Sửa chữa, Chăm sóc xe...) |
| `description` | `TEXT` | NOT NULL | Mô tả tóm tắt dịch vụ |
| `content` | `TEXT` | NOT NULL | Chi tiết quy trình thực hiện công việc |
| `imagePath` | `VARCHAR(255)` | NULL | Đường dẫn file ảnh minh họa asset |
| `unitPrice` | `DECIMAL(18,2)`| NOT NULL, CHECK >= 0 | Tiền công dịch vụ (VNĐ) |
| `estimatedDuration`| `INT` | NOT NULL, CHECK > 0 | Thời gian thực hiện dự kiến (phút) |
| `warrantyPeriod` | `VARCHAR(255)` | NULL | Thời hạn bảo hành (VD: '6 tháng') |
| `warrantyTerms`| `TEXT` | NULL | Điều kiện áp dụng bảo hành |
| `status` | `VARCHAR(30)` | NOT NULL, CHECK | 'Đang cung cấp', 'Ngừng cung cấp' |

### 2.7. `tblSparePart4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã phụ tùng |
| `name` | `VARCHAR(255)` | NOT NULL | Tên phụ tùng |
| `description` | `TEXT` | NULL | Mô tả chi tiết phụ tùng |
| `imagePath` | `VARCHAR(255)` | NULL | Đường dẫn file ảnh asset minh họa |
| `unit` | `VARCHAR(20)` | NOT NULL, CHECK | 'Cái', 'Chiếc', 'Bộ', 'Lít' |
| `manufacturer` | `VARCHAR(255)` | NOT NULL | Hãng sản xuất phụ tùng |
| `specifications` | `TEXT` | NULL | Thông số kỹ thuật chi tiết |
| `compatibleVehicleModels` | `TEXT` | NULL | Dòng xe ô tô tương thích |
| `sellingPrice` | `DECIMAL(18,2)`| NULL, CHECK >= 0 | Đơn giá bán niêm yết (NULL nếu Chưa công bố) |
| `warrantyPeriod` | `VARCHAR(255)` | NULL | Thời hạn bảo hành phụ tùng |
| `warrantyTerms`| `TEXT` | NULL | Điều kiện bảo hành của hãng/gara |
| `status` | `VARCHAR(30)` | NOT NULL, CHECK | 'Chưa công bố', 'Đang kinh doanh', 'Ngừng kinh doanh' |
| `stockQuantity` | `DECIMAL(12,3)`| NOT NULL, DEFAULT 0 | Số lượng tồn kho thực tế, lưu trực tiếp |
| `tblSparePartCategory4id` | `INT` | NOT NULL, FK → Category | Nhóm phụ tùng tương ứng |

*Ràng buộc bổ sung:*
- `chk_sparepart_kinhDoanh`: Khi `status = 'Đang kinh doanh'` thì bắt buộc `sellingPrice IS NOT NULL` và `description` không được rỗng.

### 2.8. `tblPurchaseInvoice4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã hóa đơn nhập |
| `purchaseDate` | `DATETIME` | NOT NULL | Thời điểm lập hóa đơn nhập |
| `confirmedAt`| `DATETIME` | NOT NULL | Thời điểm xác nhận nhập hàng |
| `paymentStatus` | `VARCHAR(30)` | NOT NULL, CHECK | 'Chưa thanh toán', 'Đã thanh toán' (Default: Chưa thanh toán) |
| `paidAt`| `DATETIME` | NULL | Thời điểm ghi nhận trả tiền cho NCC |
| `tblSupplier4id` | `INT` | NOT NULL, FK → Supplier | Nhà cung cấp xuất hàng |
| `tblEmployee4id` | `INT` | NOT NULL, FK → Employee | Nhân viên kho thực hiện lập và thanh toán |
| `requestCode` | `CHAR(36)` | NOT NULL, UNIQUE | UUID sinh ở server chống trùng lặp nhập/tiền |
| `supplierNameSnapshot` | `VARCHAR(255)` | NOT NULL | Snapshot tên nhà cung cấp tại thời điểm xác nhận |
| `warehouseStaffNameSnapshot` | `VARCHAR(255)` | NOT NULL | Snapshot tên nhân viên kho tại thời điểm xác nhận |

*Ràng buộc bổ sung:*
- `chk_purchase_invoice_thanhToan`: `(paymentStatus = 'Chưa thanh toán' AND paidAt IS NULL) OR (paymentStatus = 'Đã thanh toán' AND paidAt IS NOT NULL)`

### 2.9. `tblPurchaseInvoiceDetail4`
| Cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa / Ghi chú |
|---|---|---|---|
| `id` | `INT` | PK, AUTO_INCREMENT | Mã dòng chi tiết hóa đơn nhập |
| `quantity` | `DECIMAL(12,3)`| NOT NULL, CHECK > 0 | Số lượng nhập |
| `purchaseUnitPrice` | `DECIMAL(18,2)`| NOT NULL, CHECK > 0 | Đơn giá nhập thỏa thuận với NCC (VNĐ) |
| `tblPurchaseInvoice4id` | `INT` | NOT NULL, FK → PurchaseInvoice | Hóa đơn nhập cha |
| `tblSparePart4id` | `INT` | NOT NULL, FK → SparePart | Phụ tùng được nhập |
| `sparePartNameSnapshot` | `VARCHAR(255)`| NOT NULL | Snapshot tên phụ tùng tại thời điểm xác nhận |
| `unitSnapshot` | `VARCHAR(20)` | NOT NULL | Snapshot đơn vị tính tại thời điểm xác nhận |

*Ràng buộc bổ sung:*
- `uk_purchase_part`: UNIQUE (`tblPurchaseInvoice4id`, `tblSparePart4id`) - Mỗi phụ tùng chỉ xuất hiện tối đa 1 dòng trên hóa đơn nhập.
