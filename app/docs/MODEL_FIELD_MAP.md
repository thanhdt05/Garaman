# Bảng Ánh Xạ Thuộc Tính: Thiết Kế → Java Model → CSDL (MODEL_FIELD_MAP)

Tài liệu này đối chiếu từng thuộc tính từ mô hình lớp phân tích/thiết kế sang mã nguồn Java (`model.*`) và schema CSDL vật lý (`tbl*4`) cho đúng 10 model và 9 bảng trong phạm vi 2 module của đề 26 (`ke-hoach/00_PHAM_VI.md`).

Các cột tiếng Anh dưới đây là schema mới trong source. CSDL đang dùng tại cổng 8080 chưa chuyển đổi; xem [trạng thái migration](CODE_ENGLISH_MIGRATION.md) trước khi triển khai WAR mới.

---

## 1. Bảng ánh xạ 10 lớp model nghiệp vụ

| Tên lớp Java | Thuộc tính Java | Kiểu Java | Cột SQL / Nguồn | Kiểu SQL | Ý nghĩa & Ghi chú |
|---|---|---|---|---|---|
| **Member4** | `id` | `int` | `tblMember4.id` | `INT PK AI` | Mã định danh thành viên |
| Member4 | `fullName` | `String` | `tblMember4.fullName` | `VARCHAR(255)` | Họ tên đầy đủ |
| Member4 | `username` | `String` | `tblMember4.username` | `VARCHAR(100)` | Tên đăng nhập |
| Member4 | `password` | `String` | `tblMember4.password` | `VARCHAR(255)` | Chuỗi hash PBKDF2WithHmacSHA256 |
| Member4 | `dateOfBirth` | `LocalDate` | `tblMember4.dateOfBirth` | `DATE` | Ngày sinh |
| Member4 | `address` | `String` | `tblMember4.address` | `TEXT` | Địa chỉ |
| Member4 | `email` | `String` | `tblMember4.email` | `VARCHAR(255)` | Email |
| Member4 | `phoneNumber` | `String` | `tblMember4.phoneNumber` | `VARCHAR(20)` | Số điện thoại |
| Member4 | `role` | `String` | `tblMember4.role` | `VARCHAR(30)` | Vai trò ('Customer', 'WarehouseStaff') |
| Member4 | `accountStatus`| `String` | `tblMember4.accountStatus` | `VARCHAR(30)` | Trạng thái tài khoản |
| **Employee4** | *(kế thừa Member4)*| — | — | — | Kế thừa toàn bộ thuộc tính Member4 |
| Employee4 | `position` | `String` | `tblEmployee4.position` | `VARCHAR(30)` | Vị trí làm việc ('Kho') |
| Employee4 | `employmentStatus` | `String` | `tblEmployee4.employmentStatus` | `VARCHAR(30)` | Trạng thái làm việc ('Đang làm việc', 'Ngừng làm việc') |
| **WarehouseStaff4**| *(kế thừa Employee4)*| — | — | — | Lớp Java vai trò Thủ kho, dùng cho module Nhập hàng |
| **Customer4** | *(kế thừa Member4)*| — | `tblCustomer4.tblMember4id` | `INT PK` | Khóa chính tham chiếu Member4 |
| **Supplier4** | `id` | `int` | `tblSupplier4.id` | `INT PK AI` | Mã nhà cung cấp |
| Supplier4 | `name` | `String` | `tblSupplier4.name` | `VARCHAR(255)` | Tên nhà cung cấp |
| Supplier4 | `address` | `String` | `tblSupplier4.address` | `TEXT` | Địa chỉ trụ sở/kho |
| Supplier4 | `phoneNumber` | `String` | `tblSupplier4.phoneNumber` | `VARCHAR(20)` | Điện thoại liên hệ |
| Supplier4 | `email` | `String` | `tblSupplier4.email` | `VARCHAR(255)` | Email liên hệ |
| Supplier4 | `status` | `String` | `tblSupplier4.status` | `VARCHAR(30)` | Trạng thái hoạt động |
| **SparePartCategory4**| `id`| `int` | `tblSparePartCategory4.id` | `INT PK AI` | Mã nhóm phụ tùng |
| SparePartCategory4 | `name` | `String` | `tblSparePartCategory4.name` | `VARCHAR(255)` | Tên nhóm phụ tùng (UNIQUE) |
| SparePartCategory4 | `description` | `String` | `tblSparePartCategory4.description` | `TEXT` | Mô tả nhóm |
| SparePartCategory4 | `status` | `String` | `tblSparePartCategory4.status` | `VARCHAR(30)` | Trạng thái nhóm phụ tùng |
| **Service4** | `id` | `int` | `tblService4.id` | `INT PK AI` | Mã dịch vụ |
| Service4 | `name` | `String` | `tblService4.name` | `VARCHAR(255)` | Tên dịch vụ |
| Service4 | `category` | `String` | `tblService4.category` | `VARCHAR(255)` | Tên nhóm dịch vụ |
| Service4 | `description` | `String` | `tblService4.description` | `TEXT` | Mô tả tiền công |
| Service4 | `content` | `String` | `tblService4.content` | `TEXT` | Quy trình kỹ thuật thực hiện |
| Service4 | `imagePath` | `String` | `tblService4.imagePath` | `VARCHAR(255)` | Đường dẫn ảnh asset minh họa |
| Service4 | `unitPrice` | `BigDecimal` | `tblService4.unitPrice` | `DECIMAL(18,2)` | Tiền công dịch vụ |
| Service4 | `estimatedDuration` | `int` | `tblService4.estimatedDuration` | `INT` | Thời gian dự kiến (phút) |
| Service4 | `warrantyPeriod` | `String` | `tblService4.warrantyPeriod` | `VARCHAR(255)` | Hạn bảo hành dịch vụ |
| Service4 | `warrantyTerms`| `String` | `tblService4.warrantyTerms` | `TEXT` | Điều kiện bảo hành |
| Service4 | `status` | `String` | `tblService4.status` | `VARCHAR(30)` | Trạng thái dịch vụ |
| **SparePart4** | `id` | `int` | `tblSparePart4.id` | `INT PK AI` | Mã phụ tùng |
| SparePart4 | `name` | `String` | `tblSparePart4.name` | `VARCHAR(255)` | Tên phụ tùng |
| SparePart4 | `description` | `String` | `tblSparePart4.description` | `TEXT` | Mô tả phụ tùng |
| SparePart4 | `imagePath` | `String` | `tblSparePart4.imagePath` | `VARCHAR(255)` | Đường dẫn ảnh asset minh họa |
| SparePart4 | `unit` | `String` | `tblSparePart4.unit` | `VARCHAR(20)` | Đơn vị tính ('Cái', 'Chiếc', 'Bộ', 'Lít') |
| SparePart4 | `manufacturer` | `String` | `tblSparePart4.manufacturer` | `VARCHAR(255)` | Hãng sản xuất |
| SparePart4 | `specifications` | `String` | `tblSparePart4.specifications` | `TEXT` | Thông số kỹ thuật |
| SparePart4 | `compatibleVehicleModels` | `String` | `tblSparePart4.compatibleVehicleModels` | `TEXT` | Dòng xe phù hợp |
| SparePart4 | `sellingPrice` | `BigDecimal` | `tblSparePart4.sellingPrice` | `DECIMAL(18,2)` | Đơn giá bán (nullable nếu Chưa công bố) |
| SparePart4 | `warrantyPeriod` | `String` | `tblSparePart4.warrantyPeriod` | `VARCHAR(255)` | Hạn bảo hành |
| SparePart4 | `warrantyTerms`| `String` | `tblSparePart4.warrantyTerms` | `TEXT` | Điều kiện bảo hành |
| SparePart4 | `status` | `String` | `tblSparePart4.status` | `VARCHAR(30)` | Trạng thái kinh doanh |
| SparePart4 | `stockQuantity` | `BigDecimal` | `tblSparePart4.stockQuantity` | `DECIMAL(12,3)` | Số lượng tồn kho thực tế |
| SparePart4 | `category` | `SparePartCategory4` | `tblSparePartCategory4id` | `INT FK` | Nhóm phụ tùng |
| **PurchaseInvoice4**| `id`| `int` | `tblPurchaseInvoice4.id` | `INT PK AI` | Mã hóa đơn nhập |
| PurchaseInvoice4 | `purchaseDate` | `LocalDateTime`| `tblPurchaseInvoice4.purchaseDate` | `DATETIME` | Thời điểm lập hóa đơn |
| PurchaseInvoice4 | `confirmedAt`| `LocalDateTime`| `tblPurchaseInvoice4.confirmedAt`| `DATETIME` | Thời điểm xác nhận nhập hàng |
| PurchaseInvoice4 | `paymentStatus` | `String` | `tblPurchaseInvoice4.paymentStatus` | `VARCHAR(30)` | 'Chưa thanh toán' / 'Đã thanh toán' |
| PurchaseInvoice4 | `paidAt`| `LocalDateTime`| `tblPurchaseInvoice4.paidAt`| `DATETIME` | Thời điểm ghi nhận trả tiền (nullable) |
| PurchaseInvoice4 | `supplier` | `Supplier4` | `tblSupplier4id` | `INT FK` | Nhà cung cấp |
| PurchaseInvoice4 | `warehouseStaff` | `WarehouseStaff4`| `tblEmployee4id` | `INT FK` | Nhân viên kho lập & trả tiền |
| PurchaseInvoice4 | `requestCode` | `String` | `tblPurchaseInvoice4.requestCode` | `CHAR(36) UQ` | UUID sinh ở server chống lặp |
| PurchaseInvoice4 | `supplierNameSnapshot`| `String` | `tblPurchaseInvoice4.supplierNameSnapshot`| `VARCHAR(255)`| Snapshot tên NCC chốt lịch sử |
| PurchaseInvoice4 | `warehouseStaffNameSnapshot` | `String` | `tblPurchaseInvoice4.warehouseStaffNameSnapshot` | `VARCHAR(255)`| Snapshot tên thủ kho chốt lịch sử |
| PurchaseInvoice4 | `invoiceDetails`| `List<PurchaseInvoiceDetail4>`| *(quan hệ 1-N)* | — | Danh sách các dòng chi tiết nhập |
| PurchaseInvoice4 | `totalAmount` | `BigDecimal` | *(dẫn xuất qua method)* | — | `calculateTotalAmount()`: Tổng tiền hóa đơn |
| **PurchaseInvoiceDetail4**| `id`| `int` | `tblPurchaseInvoiceDetail4.id` | `INT PK AI` | Mã dòng chi tiết |
| PurchaseInvoiceDetail4 | `quantity` | `BigDecimal` | `tblPurchaseInvoiceDetail4.quantity` | `DECIMAL(12,3)` | Số lượng nhập |
| PurchaseInvoiceDetail4 | `purchaseUnitPrice` | `BigDecimal` | `tblPurchaseInvoiceDetail4.purchaseUnitPrice` | `DECIMAL(18,2)` | Đơn giá nhập |
| PurchaseInvoiceDetail4 | `sparePart` | `SparePart4` | `tblSparePart4id` | `INT FK` | Phụ tùng được nhập |
| PurchaseInvoiceDetail4 | `sparePartNameSnapshot`| `String` | `tblPurchaseInvoiceDetail4.sparePartNameSnapshot`| `VARCHAR(255)`| Snapshot tên phụ tùng |
| PurchaseInvoiceDetail4 | `unitSnapshot`| `String` | `tblPurchaseInvoiceDetail4.unitSnapshot`| `VARCHAR(20)` | Snapshot đơn vị tính |
| PurchaseInvoiceDetail4 | `lineAmount` | `BigDecimal` | *(dẫn xuất qua method)* | — | `calculateLineAmount()`: quantity * purchaseUnitPrice |
