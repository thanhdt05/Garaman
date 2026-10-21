# Danh sách tên tiếng Anh thống nhất — hai biểu đồ lớp thiết kế

Ngày chốt: 21/10/2026. **Dùng bộ tên mới dưới đây để vẽ hai biểu đồ trong Visual Paradigm.** Hai SVG/PlantUML chính đã đổi tên theo danh sách này. Lượt này chưa đổi tên file Java/JSP thật, method/field trong code, bảng/cột SQL, project VPP hoặc nội dung DOCX gốc. Các tên tiếng Việt trong tài liệu cài đặt cũ là tên hiện tại của code; không lấy chúng ghi lại vào hai hình mới.

## 1. Quy tắc đặt tên đã chốt

- Lớp dùng tiếng Anh và `PascalCase`; giữ hậu tố `4` của các lớp Java/lớp biên đã có. Các lớp như `Member4`, `SparePart4`, `PurchaseInvoice4` đã đúng nên giữ nguyên.
- Thuộc tính, phương thức và tham số dùng tiếng Anh, `lowerCamelCase`. Không dùng tên không dấu tiếng Việt hoặc ghép như `btnTimKiem`, `dsPhuTung`, `tblChiTietNhap`.
- Thành phần giao diện ghi theo mẫu bài giảng: `keyword: TextField`, `resultsTable: Table`, `searchButton: submit`, `backLink: link`. Dùng hậu tố `Button`, `Link`, `Table` thay các tiền tố `btn`, `tbl`; danh sách dữ liệu dùng danh từ số nhiều như `services`, `spareParts`, `suppliers`.
- JSP dùng tên tiếng Anh `lowerCamelCase.jsp`, không giữ tiền tố `gd` hoặc viết tắt KH/NCC. JSP trong hai hình vốn không có hậu tố `4`, nên không tự thêm hậu tố mới.
- Tiêu đề hình, chú giải, nhãn giao diện người dùng và dữ liệu mẫu vẫn được viết tiếng Việt. Chỉ thống nhất ngôn ngữ của các định danh lớp/thuộc tính/hàm/tham số.
- Giữ nguyên kiểu dữ liệu, phạm vi truy cập, kế thừa, quan hệ và bội số. Không thêm trường dữ liệu hay chức năng để thực hiện đổi tên.

**Cách tra trong Paradigm:** tìm tên lớp/trang trước → mở Specification → Attributes hoặc Operations → đổi theo hàng tương ứng. Tên cũ có thể xuất hiện trong nhiều lớp; kiểm tra cột lớp sở hữu. Không Replace All mù: `themNhaCungCap` là `addSupplierLink` khi là liên kết giao diện, nhưng là `addSupplier(...)` khi là phương thức DAO.

Bản tra dạng Excel/CSV: [TEN_CU_TEN_MOI.csv](TEN_CU_TEN_MOI.csv). Bản ánh xạ để đối chiếu tự động: [NAMING_ENGLISH.json](NAMING_ENGLISH.json). CSV có cột loại, lớp/trang cũ, lớp/trang mới, tên cũ, tên mới và kiểu dữ liệu; có cả các tên giữ nguyên để kiểm tra đủ thuộc tính.

## 2. Tên lớp/trang giao diện cần đổi

| Tên cũ | Tên mới thống nhất |
|---|---|
| `gdChinhKH.jsp` | `customerHome.jsp` |
| `gdTimThongTin.jsp` | `catalogSearch.jsp` |
| `gdChiTietThongTin.jsp` | `itemDetails.jsp` |
| `gdChinhKho.jsp` | `warehouseHome.jsp` |
| `gdChonNhaCungCap.jsp` | `selectSupplier.jsp` |
| `gdThemNhaCungCap.jsp` | `newSupplier.jsp` |
| `gdLapHoaDonNhap.jsp` | `purchaseInvoice.jsp` |
| `gdThemPhuTung.jsp` | `newSparePart.jsp` |
| `gdHoaDonNhapDaXacNhan.jsp` | `confirmedInvoice.jsp` |
| `gdInHoaDonNhap.jsp` | `printInvoice.jsp` |

Các tên trên là tên JSP đề xuất trên hình thiết kế. Chưa đổi tên file trong `app/src/main/webapp/`; vì vậy ứng dụng đang chạy vẫn dùng các file cũ. Khi đổi code sau này, phải cập nhật đồng thời các đường dẫn forward/include theo bảng, không chỉ đổi tên file.

## 3. Các lớp đã dùng tiếng Anh — giữ nguyên

| Nhóm | Tên giữ nguyên |
|---|---|
| Thực thể | `Member4`, `Employee4`, `WarehouseStaff4`, `Customer4`, `Service4`, `SparePart4`, `SparePartCategory4`, `Supplier4`, `PurchaseInvoice4`, `PurchaseInvoiceDetail4` |
| DAO | `DAO`, `ServiceDAO4`, `SparePartDAO4`, `SupplierDAO4`, `WarehouseStaffDAO4`, `SparePartCategoryDAO4`, `PurchaseInvoiceDAO4` |
| Servlet điều phối | `CatalogServlet4`, `SupplierServlet4`, `SparePartServlet4`, `PurchaseInvoiceServlet4` |

Các lớp biên trong phần phân tích như `CustomerHomeUI4`, `CatalogSearchUI4`, `ItemDetailsUI4`, `WarehouseHomeUI4`, `SelectSupplierUI4`, `NewSupplierUI4`, `PurchaseInvoiceUI4`, `NewSparePartUI4`, `ConfirmedInvoiceUI4`, `PrintInvoiceUI4` vốn đã là tiếng Anh. Không cần đổi chúng để đồng nhất ngôn ngữ; các hình phân tích đó chưa được sửa trong lượt này.

`CustomerHomeView`, `CatalogSearchView`… trong mã PlantUML chỉ là alias phục vụ nối đường vẽ, không phải các lớp Java/thực thể cần tạo thêm trong Paradigm.

## 4. Thuộc tính lớp thực thể

Những hàng tên cũ bằng tên mới là giữ nguyên. Thuộc tính kế thừa không khai báo lại trong lớp con.

### Member4

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `id` | `id` | `int` |
| `hoTen` | `fullName` | `String` |
| `tenDangNhap` | `username` | `String` |
| `matKhau` | `password` | `String` |
| `ngaySinh` | `dateOfBirth` | `LocalDate` |
| `diaChi` | `address` | `String` |
| `email` | `email` | `String` |
| `dienThoai` | `phoneNumber` | `String` |
| `vaiTro` | `role` | `String` |
| `trangThaiTaiKhoan` | `accountStatus` | `String` |
### Employee4

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `viTri` | `position` | `String` |
| `trangThaiLamViec` | `employmentStatus` | `String` |
### WarehouseStaff4

Không có thuộc tính khai báo riêng trong hai hình; kế thừa từ `Employee4 → Member4`.

### Customer4

Không có thuộc tính khai báo riêng trong hai hình; kế thừa từ `Member4`.

### Service4

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `id` | `id` | `int` |
| `ten` | `name` | `String` |
| `nhom` | `category` | `String` |
| `moTa` | `description` | `String` |
| `noiDung` | `content` | `String` |
| `hinhAnh` | `imagePath` | `String` |
| `donGia` | `unitPrice` | `BigDecimal` |
| `thoiGianDuKien` | `estimatedDuration` | `int` |
| `hanBaoHanh` | `warrantyPeriod` | `String` |
| `dieuKienBaoHanh` | `warrantyTerms` | `String` |
| `trangThai` | `status` | `String` |
### Supplier4

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `id` | `id` | `int` |
| `ten` | `name` | `String` |
| `diaChi` | `address` | `String` |
| `dienThoai` | `phoneNumber` | `String` |
| `email` | `email` | `String` |
| `trangThai` | `status` | `String` |
### SparePartCategory4

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `id` | `id` | `int` |
| `ten` | `name` | `String` |
| `moTa` | `description` | `String` |
| `trangThai` | `status` | `String` |
### SparePart4

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `id` | `id` | `int` |
| `ten` | `name` | `String` |
| `moTa` | `description` | `String` |
| `hinhAnh` | `imagePath` | `String` |
| `donVi` | `unit` | `String` |
| `hangSanXuat` | `manufacturer` | `String` |
| `thongSo` | `specifications` | `String` |
| `dongXePhuHop` | `compatibleVehicleModels` | `String` |
| `giaBan` | `sellingPrice` | `BigDecimal` |
| `hanBaoHanh` | `warrantyPeriod` | `String` |
| `dieuKienBaoHanh` | `warrantyTerms` | `String` |
| `trangThai` | `status` | `String` |
| `tonKho` | `stockQuantity` | `BigDecimal` |
| `nhom` | `category` | `SparePartCategory4` |
### PurchaseInvoice4

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `id` | `id` | `int` |
| `maYeuCau` | `requestCode` | `String` |
| `ngayNhap` | `purchaseDate` | `LocalDateTime` |
| `thoiDiemXacNhan` | `confirmedAt` | `LocalDateTime` |
| `tinhTrang` | `paymentStatus` | `String` |
| `thoiDiemThanhToan` | `paidAt` | `LocalDateTime` |
| `nhaCungCap` | `supplier` | `Supplier4` |
| `nhanVienKho` | `warehouseStaff` | `WarehouseStaff4` |
| `dsChiTietNhap` | `invoiceDetails` | `List<PurchaseInvoiceDetail4>` |
| `nhaCungCapTenApDung` | `supplierNameSnapshot` | `String` |
| `nhanVienTenApDung` | `warehouseStaffNameSnapshot` | `String` |
### PurchaseInvoiceDetail4

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `id` | `id` | `int` |
| `soLuong` | `quantity` | `BigDecimal` |
| `donGiaNhap` | `purchaseUnitPrice` | `BigDecimal` |
| `tenPhuTungApDung` | `sparePartNameSnapshot` | `String` |
| `donViApDung` | `unitSnapshot` | `String` |
| `phuTung` | `sparePart` | `SparePart4` |

Một số cách dùng cần giữ thống nhất:

- `unitPrice` là đơn giá dịch vụ; `sellingPrice` là giá bán phụ tùng; `purchaseUnitPrice` là đơn giá nhập. Đây là ba khái niệm khác nhau, không đổi tất cả thành `price`.
- `status` là trạng thái danh mục, `accountStatus` là trạng thái tài khoản, `employmentStatus` là trạng thái làm việc và `paymentStatus` là tình trạng thanh toán hóa đơn.
- `estimatedDuration` của dịch vụ giữ kiểu `int` và ý nghĩa thời lượng dự kiến (phút); `warrantyPeriod` giữ kiểu `String`, không đổi thành ngày hết hạn.
- `imagePath` giữ kiểu `String`; `compatibleVehicleModels` giữ kiểu `String` mô tả dòng xe phù hợp, không sinh lớp/bảng xe mới.
- `category` của `Service4` là `String`, còn của `SparePart4` là `SparePartCategory4`; chỉ đổi tên, không đổi mô hình dữ liệu.
- Hậu tố `Snapshot` chỉ các tên/đơn vị đã chốt trên hóa đơn: `supplierNameSnapshot`, `warehouseStaffNameSnapshot`, `sparePartNameSnapshot`, `unitSnapshot`. Không thay chúng bằng tên hồ sơ hiện tại.
- Nếu cần ghi tổng/thành tiền dẫn xuất khi sửa các hình tiếp theo, thống nhất `tongTien → totalAmount`, `thanhTien → lineAmount`. Hai hình hiện tại không thêm hai thuộc tính lưu mới hoặc cột SQL cho các giá trị này.

## 5. Thuộc tính và control của từng trang JSP

### gdChinhKH.jsp → customerHome.jsp

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `hoTen` | `fullName` | `String` |
| `timThongTin` | `searchCatalogLink` | `link` |
### gdTimThongTin.jsp → catalogSearch.jsp

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `tuKhoa` | `keyword` | `TextField` |
| `boLoc` | `itemTypeFilter` | `Select` |
| `btnTimKiem` | `searchButton` | `submit` |
| `dsDichVu` | `services` | `List<Service4>` |
| `dsPhuTung` | `spareParts` | `List<SparePart4>` |
| `tblKetQua` | `resultsTable` | `Table` |
| `chonThongTin` | `viewDetailsLink` | `link` |
### gdChiTietThongTin.jsp → itemDetails.jsp

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `dichVu` | `service` | `Service4` |
| `phuTung` | `sparePart` | `SparePart4` |
| `quayLai` | `backLink` | `link` |
### gdChinhKho.jsp → warehouseHome.jsp

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `hoTen` | `fullName` | `String` |
| `nhapPhuTung` | `startPurchaseLink` | `link` |
### gdChonNhaCungCap.jsp → selectSupplier.jsp

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `tuKhoa` | `keyword` | `TextField` |
| `btnTimKiem` | `searchButton` | `submit` |
| `dsNhaCungCap` | `suppliers` | `List<Supplier4>` |
| `tblNhaCungCap` | `suppliersTable` | `Table` |
| `btnChonNhaCungCap` | `selectSupplierButton` | `submit` |
| `themNhaCungCap` | `addSupplierLink` | `link` |
### gdThemNhaCungCap.jsp → newSupplier.jsp

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `nhaCungCap` | `supplier` | `Supplier4` |
| `ten` | `name` | `TextField` |
| `diaChi` | `address` | `TextField` |
| `dienThoai` | `phoneNumber` | `TextField` |
| `email` | `email` | `TextField` |
| `btnLuu` | `saveButton` | `submit` |
| `quayLai` | `backLink` | `link` |
### gdLapHoaDonNhap.jsp → purchaseInvoice.jsp

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `hoaDonNhap` | `purchaseInvoice` | `PurchaseInvoice4` |
| `nhaCungCap` | `supplier` | `Supplier4` |
| `nhanVienKho` | `warehouseStaff` | `WarehouseStaff4` |
| `tuKhoa` | `keyword` | `TextField` |
| `btnTimKiem` | `searchButton` | `submit` |
| `dsPhuTung` | `spareParts` | `List<SparePart4>` |
| `tblKetQuaPhuTung` | `sparePartsTable` | `Table` |
| `btnChonPhuTung` | `selectSparePartButton` | `submit` |
| `tblChiTietNhap` | `invoiceDetailsTable` | `Table` |
| `themPhuTung` | `addSparePartButton` | `submit` |
| `btnCapNhat` | `updateButton` | `submit` |
| `btnXacNhan` | `confirmButton` | `submit` |
| `btnHuy` | `cancelButton` | `submit` |
### gdThemPhuTung.jsp → newSparePart.jsp

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `phuTung` | `sparePart` | `SparePart4` |
| `ten` | `name` | `TextField` |
| `dsNhom` | `categories` | `List<SparePartCategory4>` |
| `nhom` | `category` | `Select` |
| `donVi` | `unit` | `Select` |
| `hangSanXuat` | `manufacturer` | `TextField` |
| `moTa` | `description` | `TextArea` |
| `thongSo` | `specifications` | `TextField` |
| `dongXePhuHop` | `compatibleVehicleModels` | `TextField` |
| `hinhAnh` | `imagePath` | `Select` |
| `giaBan` | `sellingPrice` | `TextField` |
| `hanBaoHanh` | `warrantyPeriod` | `TextField` |
| `dieuKienBaoHanh` | `warrantyTerms` | `TextArea` |
| `trangThai` | `status` | `Select` |
| `btnLuu` | `saveButton` | `submit` |
| `quayLai` | `backLink` | `link` |
### gdHoaDonNhapDaXacNhan.jsp → confirmedInvoice.jsp

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `hoaDonNhap` | `purchaseInvoice` | `PurchaseInvoice4` |
| `tblChiTietNhap` | `invoiceDetailsTable` | `Table` |
| `soTienDaTra` | `amountPaid` | `TextField` |
| `btnDaThanhToan` | `markAsPaidButton` | `submit` |
| `inHoaDon` | `printInvoiceLink` | `link` |
### gdInHoaDonNhap.jsp → printInvoice.jsp

| Tên cũ | Tên mới | Kiểu giữ nguyên |
|---|---|---|
| `hoaDonNhap` | `purchaseInvoice` | `PurchaseInvoice4` |
| `tblChiTietNhap` | `invoiceDetailsTable` | `Table` |
| `btnIn` | `printButton` | `button` |
| `quayLai` | `backLink` | `link` |

`selectSparePartButton` nằm ở mỗi dòng `sparePartsTable`; khi cài đặt, nút gửi mã của dòng được chọn. `invoiceDetailsTable` là bảng các dòng nhập cho phép sửa lượng/giá, khác với bảng kết quả tìm kiếm. Giữ cách biểu diễn `TextField`, `TextArea`, `Select`, `Table`, `link`, `submit`, `button` của thiết kế, không đưa ID HTML/query vào hình chính.

## 6. Tên phương thức và chữ ký mới

Các phương thức tìm/lấy/thêm/lưu/ghi nhận giữ đúng lớp sở hữu trong hai hình. Riêng `calculateTotalAmount` thuộc thực thể `PurchaseInvoice4`, không chuyển sang DAO.

| Lớp | Chữ ký cũ | Chữ ký mới thống nhất |
|---|---|---|
| `ServiceDAO4` | `timDichVuTheoTen(tuKhoa: String): List<Service4>` | `findServicesByName(keyword: String): List<Service4>` |
| `ServiceDAO4` | `layChiTietDichVu(id: int): Service4` | `getServiceDetails(serviceId: int): Service4` |
| `SparePartDAO4` | `timPhuTungTheoTen(tuKhoa: String, chiCongBo: boolean): List<SparePart4>` | `findSparePartsByName(keyword: String, publishedOnly: boolean): List<SparePart4>` |
| `SparePartDAO4` | `layChiTietPhuTung(id: int, chiCongBo: boolean): SparePart4` | `getSparePartDetails(sparePartId: int, publishedOnly: boolean): SparePart4` |
| `SupplierDAO4` | `timNhaCungCapTheoTen(tuKhoa: String): List<Supplier4>` | `findSuppliersByName(keyword: String): List<Supplier4>` |
| `SupplierDAO4` | `layThongTinNhaCungCap(id: int): Supplier4` | `getSupplierDetails(supplierId: int): Supplier4` |
| `SupplierDAO4` | `themNhaCungCap(supplier: Supplier4): int` | `addSupplier(supplier: Supplier4): int` |
| `WarehouseStaffDAO4` | `layThongTinNhanVienKho(id: int): WarehouseStaff4` | `getWarehouseStaffDetails(warehouseStaffId: int): WarehouseStaff4` |
| `SparePartCategoryDAO4` | `layDanhSachNhom(): List<SparePartCategory4>` | `getActiveCategories(): List<SparePartCategory4>` |
| `SparePartDAO4` | `themPhuTung(part: SparePart4): int` | `addSparePart(sparePart: SparePart4): int` |
| `SparePartDAO4` | `tangTonSauNhap(con: Connection, idPhuTung: int, soLuong: BigDecimal): void` | `increaseStockAfterPurchase(connection: Connection, sparePartId: int, quantity: BigDecimal): void` |
| `PurchaseInvoiceDAO4` | `luuHoaDonNhap(hoaDon: PurchaseInvoice4): int` | `savePurchaseInvoice(purchaseInvoice: PurchaseInvoice4): int` |
| `PurchaseInvoiceDAO4` | `layThongTinHoaDonNhap(id: int): PurchaseInvoice4` | `getPurchaseInvoiceDetails(invoiceId: int): PurchaseInvoice4` |
| `PurchaseInvoiceDAO4` | `ghiNhanThanhToan(idHoaDon: int, soTienDaTra: BigDecimal, idNhanVienHienTai: int): boolean` | `recordPayment(invoiceId: int, amountPaid: BigDecimal, currentWarehouseStaffId: int): boolean` |
| `PurchaseInvoice4` | `tinhTongTien(): BigDecimal` | `calculateTotalAmount(): BigDecimal` |

`+` vẫn là public; `~ increaseStockAfterPurchase(...)` vẫn là package-private và dùng cùng `Connection` với lưu hóa đơn. Chỉ đổi tên, không đổi cơ chế transaction.

## 7. Tham số cần đổi (tra theo phương thức)

| Lớp.phương thức cũ | Tham số cũ | Tham số mới | Kiểu giữ nguyên |
|---|---|---|---|
| `ServiceDAO4.timDichVuTheoTen` | `tuKhoa` | `keyword` | `String` |
| `ServiceDAO4.layChiTietDichVu` | `id` | `serviceId` | `int` |
| `SparePartDAO4.timPhuTungTheoTen` | `tuKhoa` | `keyword` | `String` |
| `SparePartDAO4.timPhuTungTheoTen` | `chiCongBo` | `publishedOnly` | `boolean` |
| `SparePartDAO4.layChiTietPhuTung` | `id` | `sparePartId` | `int` |
| `SparePartDAO4.layChiTietPhuTung` | `chiCongBo` | `publishedOnly` | `boolean` |
| `SupplierDAO4.timNhaCungCapTheoTen` | `tuKhoa` | `keyword` | `String` |
| `SupplierDAO4.layThongTinNhaCungCap` | `id` | `supplierId` | `int` |
| `SupplierDAO4.themNhaCungCap` | `supplier` | `supplier` | `Supplier4` |
| `WarehouseStaffDAO4.layThongTinNhanVienKho` | `id` | `warehouseStaffId` | `int` |
| `SparePartDAO4.themPhuTung` | `part` | `sparePart` | `SparePart4` |
| `SparePartDAO4.tangTonSauNhap` | `con` | `connection` | `Connection` |
| `SparePartDAO4.tangTonSauNhap` | `idPhuTung` | `sparePartId` | `int` |
| `SparePartDAO4.tangTonSauNhap` | `soLuong` | `quantity` | `BigDecimal` |
| `PurchaseInvoiceDAO4.luuHoaDonNhap` | `hoaDon` | `purchaseInvoice` | `PurchaseInvoice4` |
| `PurchaseInvoiceDAO4.layThongTinHoaDonNhap` | `id` | `invoiceId` | `int` |
| `PurchaseInvoiceDAO4.ghiNhanThanhToan` | `idHoaDon` | `invoiceId` | `int` |
| `PurchaseInvoiceDAO4.ghiNhanThanhToan` | `soTienDaTra` | `amountPaid` | `BigDecimal` |
| `PurchaseInvoiceDAO4.ghiNhanThanhToan` | `idNhanVienHienTai` | `currentWarehouseStaffId` | `int` |

`publishedOnly` là cờ lọc danh mục công bố cho khách; không phải một quyền hoặc use case mới. Những tham số `id` của hàm lấy chi tiết được ghi rõ thành `serviceId`, `sparePartId`, `supplierId`, `warehouseStaffId`, `invoiceId` theo đối tượng cần lấy.

## 8. Nhãn quan hệ trên hình

Dùng đúng tên thuộc tính tiếng Anh ở đầu quan hệ: `category`, `supplier`, `warehouseStaff`, `invoiceDetails`, `sparePart`. Giữ nguyên đường nối, chiều tham chiếu, composition và bội số. Những tên này trùng với thuộc tính tương ứng, không tạo thêm một quan hệ khác.

## 9. Thứ tự sửa trong Visual Paradigm

1. Dùng hai SVG mới làm mẫu, đổi 10 tên JSP theo mục 2. Các lớp đã là tiếng Anh ở mục 3 giữ nguyên.
2. Sửa thuộc tính thực thể theo mục 4. Đối tượng dùng chung ở hai hình như `Member4`, `SparePart4`, `SparePartCategory4` phải cùng dùng một bộ tên.
3. Sửa thuộc tính giao diện theo mục 5, vẫn dùng ký pháp control của bài giảng.
4. Sửa Operations và tham số theo mục 6–7; không thêm/xóa phương thức so với hai hình mới.
5. Sửa nhãn quan hệ theo mục 8. Dùng CSV để rà lần cuối theo lớp/trang.

Chỉ khi đã vẽ xong hai hình mới tiếp tục dùng **chính bộ tên này** để đồng bộ các hình phân tích/giao tiếp/thực thể, nội dung DOCX và code theo yêu cầu tiếp theo. Các bảng cài đặt trong tài liệu cũ vẫn mô tả code tiếng Việt hiện tại; đây là khác biệt tạm thời đã được ghi nhận, không phải hai cách đặt tên được phép dùng lẫn nhau trên hình mới.

Hai hình mới: [tra cứu](diagrams/catalog-design.svg), [nhập phụ tùng](diagrams/purchase-design.svg). Mã sửa được: [tra cứu .puml](diagrams/catalog-design.puml), [nhập .puml](diagrams/purchase-design.puml).

