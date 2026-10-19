# BẢNG ÁNH XẠ PHƯƠNG THỨC HỆ THỐNG (METHOD_MAP.md)

Tài liệu này chuẩn hóa và liệt kê đầy đủ toàn bộ các phương thức (method) trong các lớp Controller (Servlet), DAO, Filter, Util và Model nghiệp vụ đã triển khai trong mã nguồn dự án GaraMan đề 26.
Tất cả các phương thức xuất hiện trên biểu đồ lớp thiết kế (Class Diagram) và biểu đồ tuần tự (Sequence Diagram) tại mục 4.3.1 và 4.3.2 đều phải ánh xạ chính xác 1-1 với bảng này.

---

## 1. Tầng Controller (Servlets) & Filter

### 1.1. `com.garaman.controller.CatalogServlet4`
- **Tập tin:** `app/src/main/java/com/garaman/controller/CatalogServlet4.java`
- **Route:** `/customer/catalog`, `/customer/catalog/detail`
- **Kế thừa:** `HttpServlet`

| STT | Tên phương thức & Chữ ký đầy đủ | Phạm vi | Kiểu gọi | Nơi gọi / Kích hoạt | Route & Nhánh xử lý | Nguồn hợp đồng | Ghi chú / Vị trí code |
|---|---|---|---|---|---|---|---|
| 1 | `doGet(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `protected` | Instance | Tomcat container khi nhận HTTP GET | Phân luồng `/customer/catalog` và `/customer/catalog/detail` | 02, 03 | Dòng 35 |
| 2 | `doPost(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `protected` | Instance | Tomcat container khi nhận HTTP POST | Chuyển tiếp sang `doGet` để tìm kiếm an toàn | 02, 03 | Dòng 45 |
| 3 | `hienThiTimKiem(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | `doGet` khi `tuKhoa == null` | GET `/customer/catalog` (trạng thái chưa tìm) | 02, 07 | Forward `gdTimThongTin.jsp`, kết quả rỗng |
| 4 | `timKiem(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | `doGet` khi có param `tuKhoa` | GET/POST `/customer/catalog?tuKhoa=...` | 02, 03, 07 | Gọi `ServiceDAO4.timDichVuTheoTen` và `SparePartDAO4.timPhuTungTheoTen(..., true)`, forward `gdTimThongTin.jsp` |
| 5 | `xemChiTiet(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | `doGet` tại route `/customer/catalog/detail` | GET `/customer/catalog/detail?id=...&loai=...` | 02, 03, 07 | Gọi `layChiTietDichVu` hoặc `layChiTietPhuTung`, forward `gdChiTietThongTin.jsp` |

---

### 1.2. `com.garaman.controller.SupplierServlet4`
- **Tập tin:** `app/src/main/java/com/garaman/controller/SupplierServlet4.java`
- **Route:** `/warehouse/suppliers`, `/warehouse/suppliers/new`
- **Kế thừa:** `HttpServlet`

| STT | Tên phương thức & Chữ ký đầy đủ | Phạm vi | Kiểu gọi | Nơi gọi / Kích hoạt | Route & Nhánh xử lý | Nguồn hợp đồng | Ghi chú / Vị trí code |
|---|---|---|---|---|---|---|---|
| 1 | `doGet(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `protected` | Instance | Tomcat container khi nhận HTTP GET | Phân luồng tìm kiếm và hiển thị form thêm mới | 02, 03 | Dòng 30 |
| 2 | `doPost(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `protected` | Instance | Tomcat container khi nhận HTTP POST | Xử lý submit thêm mới nhà cung cấp | 02, 03 | Dòng 40 |
| 3 | `danhSachNhaCungCap(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | `doGet` tại `/warehouse/suppliers` | GET `/warehouse/suppliers?tuKhoa=...` | 02, 03, 09 | Gọi `SupplierDAO4.timNhaCungCapTheoTen`, forward `gdChonNhaCungCap.jsp` |
| 4 | `hienThiThemNhaCungCap(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | `doGet` tại `/warehouse/suppliers/new` | GET `/warehouse/suppliers/new` | 02, 03, 09 | Forward `gdThemNhaCungCap.jsp` |
| 5 | `xuLyThemNhaCungCap(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | `doPost` tại `/warehouse/suppliers/new` | POST `/warehouse/suppliers/new` | 02, 03, 10 | Kiểm tra CSRF, validate tên/địa chỉ/SĐT, gọi `SupplierDAO4.themNhaCungCap`, redirect về chọn NCC |

---

### 1.3. `com.garaman.controller.SparePartServlet4`
- **Tập tin:** `app/src/main/java/com/garaman/controller/SparePartServlet4.java`
- **Route:** `/warehouse/parts/new`
- **Kế thừa:** `HttpServlet`

| STT | Tên phương thức & Chữ ký đầy đủ | Phạm vi | Kiểu gọi | Nơi gọi / Kích hoạt | Route & Nhánh xử lý | Nguồn hợp đồng | Ghi chú / Vị trí code |
|---|---|---|---|---|---|---|---|
| 1 | `doGet(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `protected` | Instance | Tomcat container khi nhận HTTP GET | Hiển thị form thêm phụ tùng mới | 02, 03 | Dòng 30 |
| 2 | `doPost(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `protected` | Instance | Tomcat container khi nhận HTTP POST | Xử lý thêm phụ tùng mới | 02, 03 | Dòng 45 |
| 3 | `hienThiThemPhuTung(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | `doGet` tại `/warehouse/parts/new` | GET `/warehouse/parts/new` | 02, 03, 09 | Đọc danh mục nhóm đang dùng, forward `gdThemPhuTung.jsp` |
| 4 | `xuLyThemPhuTung(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | `doPost` tại `/warehouse/parts/new` | POST `/warehouse/parts/new` | 02, 03, 10 | Validate tên, nhóm, đơn vị, giá bán; gọi `SparePartDAO4.themPhuTung`, quay lại draft hóa đơn |

---

### 1.4. `com.garaman.controller.PurchaseInvoiceServlet4`
- **Tập tin:** `app/src/main/java/com/garaman/controller/PurchaseInvoiceServlet4.java`
- **Route:** `/warehouse/invoices`, `/warehouse/invoices/view`, `/warehouse/invoices/print`, `/warehouse/invoices/pay`, `/warehouse/invoices/unpaid`
- **Kế thừa:** `HttpServlet`

| STT | Tên phương thức & Chữ ký đầy đủ | Phạm vi | Kiểu gọi | Nơi gọi / Kích hoạt | Route & Nhánh xử lý | Nguồn hợp đồng | Ghi chú / Vị trí code |
|---|---|---|---|---|---|---|---|
| 1 | `doGet(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `protected` | Instance | Tomcat GET | Phân luồng hiển thị nháp, xem, in, danh sách chưa thanh toán | 02, 03 | Dòng 40 |
| 2 | `doPost(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `protected` | Instance | Tomcat POST | Xử lý các action thao tác nháp, xác nhận, thanh toán | 02, 03 | Dòng 65 |
| 3 | `khoiTaoHoaDon(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | Action `start` trong `doGet` | GET `/warehouse/invoices?action=start&supplierId=...` | 02, 03, 10 | Lấy NCC từ `SupplierDAO4`, tạo `DraftStore4`, gán `maYeuCau` UUID, redirect draft |
| 4 | `hienThiLapHoaDon(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | Action `draft` trong `doGet` | GET `/warehouse/invoices?draftId=...` | 02, 03, 09 | Đọc nháp từ session, forward `gdLapHoaDonNhap.jsp` |
| 5 | `timPhuTung(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | Action `search-part` trong `doPost` | POST `/warehouse/invoices?action=search-part` | 02, 03, 09 | Cập nhật số lượng/giá đang nhập vào nháp, tìm phụ tùng từ `SparePartDAO4 (chiCongBo=false)` |
| 6 | `themDongPhuTung(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | Action `add-line` trong `doPost` | POST `/warehouse/invoices?action=add-line` | 02, 03, 10 | Thêm phụ tùng vào nháp (focus nếu trùng), giữ các dòng khác |
| 7 | `capNhatDong(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | Action `update` trong `doPost` | POST `/warehouse/invoices?action=update` | 02, 03, 10 | Lưu số lượng và đơn giá nhập vào nháp, lưu raw input nếu có lỗi |
| 8 | `xoaDong(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | Action `remove-line` trong `doPost` | POST `/warehouse/invoices?action=remove-line` | 02, 03, 09 | Xóa 1 phụ tùng khỏi nháp theo `removePartId` |
| 9 | `huyHoaDon(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | Action `cancel` trong `doPost` | POST `/warehouse/invoices?action=cancel` | 02, 03, 07 | Hủy bản nháp trong session, redirect về chọn nhà cung cấp |
| 10 | `xacNhanHoaDon(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | Action `confirm` trong `doPost` | POST `/warehouse/invoices?action=confirm` | 02, 03, 10 | Đọc/validate dòng, kiểm tra draft version, gọi `PurchaseInvoiceDAO4.luuHoaDonNhap`, xóa nháp, redirect `view` |
| 11 | `xemHoaDon(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | Route `/warehouse/invoices/view` | GET `/warehouse/invoices/view?id=...` | 02, 03, 09 | Gọi `PurchaseInvoiceDAO4.layThongTinHoaDonNhap`, forward `gdHoaDonNhapDaXacNhan.jsp` |
| 12 | `inHoaDon(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | Route `/warehouse/invoices/print` | GET `/warehouse/invoices/print?id=...` | 02, 03, 09 | Gọi `layThongTinHoaDonNhap`, forward `gdInHoaDonNhap.jsp` |
| 13 | `thanhToanHoaDon(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | Route `/warehouse/invoices/pay` | POST `/warehouse/invoices/pay` | 02, 03, 10 | Kiểm tra người lập, đúng tổng tiền, gọi `PurchaseInvoiceDAO4.ghiNhanThanhToan`, redirect `view` |
| 14 | `danhSachChuaThanhToan(HttpServletRequest req, HttpServletResponse resp): void throws ServletException, IOException` | `private` | Instance | Route `/warehouse/invoices/unpaid` | GET `/warehouse/invoices/unpaid` | 02, 03, 07 | Gọi `PurchaseInvoiceDAO4.layDanhSachChuaThanhToanTheoNguoiLap`, forward `gdChinhKho.jsp` |

---

### 1.5. `com.garaman.controller.AuthServlet4` & `HomeServlet4`
- **Tập tin:** `AuthServlet4.java`, `HomeServlet4.java`
- **Route:** `/auth/login`, `/auth/logout`, `/customer/home`, `/warehouse/home`

| STT | Lớp & Phương thức | Phạm vi | Chức năng & Nơi gọi | Route & Luồng | Nguồn |
|---|---|---|---|---|---|
| 1 | `AuthServlet4.doGet(...)` | `protected` | Hiển thị form đăng nhập | GET `/auth/login` → `gdDangNhap.jsp` | 02, 03 |
| 2 | `AuthServlet4.doPost(...)` | `protected` | Xác thực đăng nhập qua `MemberDAO4`, bảo vệ session fixation, cấp CSRF token | POST `/auth/login` → điều hướng theo vai trò | 02, 03 |
| 3 | `HomeServlet4.doGet(...)` | `protected` | Điều hướng trang chủ Khách hàng (`/customer/home`) hoặc Nhân viên kho (`/warehouse/home`) | GET `/customer/home`, GET `/warehouse/home` | 02, 03 |

---

### 1.6. Bộ lọc hệ thống (Filters)
- **Tập tin:** `EncodingFilter4.java`, `AuthFilter4.java`

| STT | Lớp & Phương thức | Phạm vi | Chức năng |
|---|---|---|---|
| 1 | `EncodingFilter4.doFilter(...)` | `public` | Ép mã hóa `UTF-8` cho request và response |
| 2 | `AuthFilter4.doFilter(...)` | `public` | Chặn truy cập trái phép, phân quyền vai trò `Customer` và `WarehouseStaff` |

---

## 2. Tầng Truy xuất Dữ liệu (DAO)

### 2.1. `com.garaman.dao.DAO` (Lớp trừu tượng cơ sở)
- **Tập tin:** `app/src/main/java/com/garaman/dao/DAO.java`

| STT | Tên phương thức & Chữ ký đầy đủ | Phạm vi | Kiểu gọi | Chức năng | Throws |
|---|---|---|---|---|---|
| 1 | `DAO()` | `protected` | Constructor | Khởi tạo DAO cơ sở | - |
| 2 | `getConnection(): Connection` | `protected` | Instance | Mở kết nối JDBC mới từ cấu hình DataSource / DriverManager qua `DBUtil4` | `SQLException` |
| 3 | `closeQuietly(AutoCloseable... closeables): void` | `protected` | Instance | Đóng an toàn ResultSet, Statement, Connection | - |

---

### 2.2. `com.garaman.dao.MemberDAO4`
- **Tập tin:** `app/src/main/java/com/garaman/dao/MemberDAO4.java`
- **Kế thừa:** `DAO`

| STT | Tên phương thức & Chữ ký đầy đủ | Phạm vi | Kiểu gọi | Chức năng | Throws |
|---|---|---|---|---|---|
| 1 | `kiemTraDangNhap(String tenDangNhap, String matKhau): Member4` | `public` | Instance | Xác thực đăng nhập, kiểm tra hash PBKDF2 bằng `PasswordUtil4` | `SQLException` |
| 2 | `layThongTinThanhVien(int id): Member4` | `public` | Instance | Đọc thông tin thành viên theo ID | `SQLException` |
| 3 | `mapMember(ResultSet rs): Member4` | `private` | Instance | Ánh xạ dòng dữ liệu SQL sang đối tượng `Member4` | `SQLException` |

---

### 2.3. `com.garaman.dao.WarehouseStaffDAO4`
- **Tập tin:** `app/src/main/java/com/garaman/dao/WarehouseStaffDAO4.java`
- **Kế thừa:** `DAO`

| STT | Tên phương thức & Chữ ký đầy đủ | Phạm vi | Kiểu gọi | Chức năng | Throws |
|---|---|---|---|---|---|
| 1 | `layThongTinNhanVienKho(int memberId): WarehouseStaff4` | `public` | Instance | Lấy thông tin nhân viên kho đang hoạt động theo `memberId` | `SQLException` |

---

### 2.4. `com.garaman.dao.ServiceDAO4`
- **Tập tin:** `app/src/main/java/com/garaman/dao/ServiceDAO4.java`
- **Kế thừa:** `DAO`

| STT | Tên phương thức & Chữ ký đầy đủ | Phạm vi | Kiểu gọi | Chức năng | Throws |
|---|---|---|---|---|---|
| 1 | `timDichVuTheoTen(String tuKhoa): List<Service4>` | `public` | Instance | Tìm danh sách dịch vụ đang cung cấp theo từ khóa | `SQLException` |
| 2 | `layChiTietDichVu(int id): Service4` | `public` | Instance | Lấy thông tin chi tiết dịch vụ theo ID | `SQLException` |
| 3 | `mapService(ResultSet rs): Service4` | `private` | Instance | Ánh xạ dòng dữ liệu sang đối tượng `Service4` | `SQLException` |

---

### 2.5. `com.garaman.dao.SparePartDAO4`
- **Tập tin:** `app/src/main/java/com/garaman/dao/SparePartDAO4.java`
- **Kế thừa:** `DAO`

| STT | Tên phương thức & Chữ ký đầy đủ | Phạm vi | Kiểu gọi | Chức năng | Throws |
|---|---|---|---|---|---|
| 1 | `timPhuTungTheoTen(String tuKhoa, boolean chiCongBo): List<SparePart4>` | `public` | Instance | Tìm phụ tùng theo tên; `chiCongBo=true` cho khách, `false` cho kho | `SQLException` |
| 2 | `layChiTietPhuTung(int id, boolean chiCongBo): SparePart4` | `public` | Instance | Lấy thông tin chi tiết phụ tùng theo ID và trạng thái | `SQLException` |
| 3 | `themPhuTung(SparePart4 phuTung): int` | `public` | Instance | Thêm phụ tùng mới (tồn ban đầu 0, trạng thái Chưa công bố) | `SQLException` |
| 4 | `tangTonSauNhap(Connection conn, int phuTungId, BigDecimal soLuongTang): void` | `package-private` (`~`) | Instance | Cộng dồn số lượng tồn kho trong transaction nhập hàng | `SQLException` |
| 5 | `mapSparePart(ResultSet rs): SparePart4` | `private` | Instance | Ánh xạ dòng dữ liệu sang `SparePart4` kèm `SparePartCategory4` | `SQLException` |

---

### 2.6. `com.garaman.dao.SparePartCategoryDAO4`
- **Tập tin:** `app/src/main/java/com/garaman/dao/SparePartCategoryDAO4.java`
- **Kế thừa:** `DAO`

| STT | Tên phương thức & Chữ ký đầy đủ | Phạm vi | Kiểu gọi | Chức năng | Throws |
|---|---|---|---|---|---|
| 1 | `layDanhSachNhom(): List<SparePartCategory4>` | `public` | Instance | Lấy danh sách nhóm phụ tùng đang hoạt động (`trangThai = 'DANG_DUNG'`) | `SQLException` |
| 2 | `layNhomTheoId(int id): SparePartCategory4` | `public` | Instance | Đọc thông tin nhóm phụ tùng theo ID | `SQLException` |

---

### 2.7. `com.garaman.dao.SupplierDAO4`
- **Tập tin:** `app/src/main/java/com/garaman/dao/SupplierDAO4.java`
- **Kế thừa:** `DAO`

| STT | Tên phương thức & Chữ ký đầy đủ | Phạm vi | Kiểu gọi | Chức năng | Throws |
|---|---|---|---|---|---|
| 1 | `timNhaCungCapTheoTen(String tuKhoa): List<Supplier4>` | `public` | Instance | Tìm nhà cung cấp đang hợp tác theo tên chứa từ khóa | `SQLException` |
| 2 | `layThongTinNhaCungCap(int id): Supplier4` | `public` | Instance | Lấy thông tin nhà cung cấp theo ID | `SQLException` |
| 3 | `themNhaCungCap(Supplier4 ncc): int` | `public` | Instance | Thêm mới nhà cung cấp, trả về ID vừa sinh | `SQLException` |
| 4 | `mapSupplier(ResultSet rs): Supplier4` | `private` | Instance | Ánh xạ dòng dữ liệu sang đối tượng `Supplier4` | `SQLException` |

---

### 2.8. `com.garaman.dao.PurchaseInvoiceDAO4`
- **Tập tin:** `app/src/main/java/com/garaman/dao/PurchaseInvoiceDAO4.java`
- **Kế thừa:** `DAO`

| STT | Tên phương thức & Chữ ký đầy đủ | Phạm vi | Kiểu gọi | Chức năng & Luồng Transaction | Throws |
|---|---|---|---|---|---|
| 1 | `luuHoaDonNhap(PurchaseInvoice4 hoaDon): int` | `public` | Instance | Lưu hóa đơn nhập trong transaction: Khóa theo thứ tự NCC -> Nhân viên -> Phụ tùng (ID tăng dần), insert header, insert chi tiết (lưu snapshot tên/đơn vị), gọi `SparePartDAO4.tangTonSauNhap`, xử lý idempotent mã yêu cầu trùng | `SQLException` |
| 2 | `layThongTinHoaDonNhap(int id): PurchaseInvoice4` | `public` | Instance | Đọc đầy đủ thông tin hóa đơn, nhà cung cấp, người lập và danh sách chi tiết | `SQLException` |
| 3 | `ghiNhanThanhToan(int hoaDonId, BigDecimal soTienThanhToan, int nhanVienHienTaiId): boolean` | `public` | Instance | Xác nhận thanh toán: Kiểm tra đúng người lập, trạng thái CHUA_THANH_TOAN, đúng tổng tiền, cập nhật trạng thái DA_THANH_TOAN | `SQLException` |
| 4 | `layDanhSachChuaThanhToanTheoNguoiLap(int nguoiLapId): List<PurchaseInvoice4>` | `public` | Instance | Lấy danh sách hóa đơn nhập chưa thanh toán của nhân viên kho hiện tại | `SQLException` |
| 5 | `kiemTraMaYeuCauDaLuu(String maYeuCau): Integer` | `private` | Instance | Kiểm tra mã yêu cầu UUID đã được lưu trước đó chưa (phục vụ retry an toàn) | `SQLException` |
| 6 | `chenHoaDon(Connection conn, PurchaseInvoice4 hoaDon): int` | `private` | Instance | Thực thi câu lệnh `INSERT INTO tblPurchaseInvoice4` | `SQLException` |
| 7 | `chenChiTiet(Connection conn, int hoaDonId, PurchaseInvoiceDetail4 ct): void` | `private` | Instance | Thực thi câu lệnh `INSERT INTO tblPurchaseInvoiceDetail4` | `SQLException` |
| 8 | `khoaDuLieu(Connection conn, int nccId, int nvId, List<Integer> partIds): void` | `private` | Instance | Thực hiện `SELECT ... FOR UPDATE` theo thứ tự chặt chẽ chống Deadlock | `SQLException` |

---

## 3. Tầng Nghiệp vụ Model (Business Calculations)

| STT | Lớp & Phương thức | Phạm vi | Kiểu | Mô tả tính toán | Nguồn hợp đồng |
|---|---|---|---|---|---|
| 1 | `PurchaseInvoiceDetail4.getThanhTien(): BigDecimal` | `public` | Instance | Tính thành tiền của dòng: `soLuong * donGiaNhap` | 02, 03 |
| 2 | `PurchaseInvoice4.tinhTongTien(): BigDecimal` | `public` | Instance | Tính tổng tiền hóa đơn bằng cách cộng dồn `getThanhTien()` của tất cả các dòng | 02, 03 |

---

## 4. Tầng Tiện ích Kỹ thuật (Utilities)

| STT | Lớp & Phương thức | Phạm vi | Kiểu | Chức năng |
|---|---|---|---|---|
| 1 | `DBUtil4.getConnection(): Connection` | `public` | Static | Khởi tạo kết nối JDBC từ `db.properties` hoặc biến môi trường |
| 2 | `PasswordUtil4.hashPassword(String raw): String` | `public` | Static | Băm mật khẩu bằng thuật toán PBKDF2WithHmacSHA256 (khớp seed data) |
| 3 | `PasswordUtil4.verifyPassword(String raw, String hashed): boolean` | `public` | Static | Kiểm tra mật khẩu thô với chuỗi hash đã lưu |
| 4 | `CsrfUtil4.generateToken(HttpSession session): String` | `public` | Static | Sinh hoặc lấy CSRF token trong session người dùng |
| 5 | `CsrfUtil4.validateToken(HttpServletRequest req): boolean` | `public` | Static | Kiểm tra CSRF token hợp lệ cho các request POST |
| 6 | `ValidationUtil4.validateSoLuong(BigDecimal sl, String donVi): String` | `public` | Static | Kiểm tra tính hợp lệ của số lượng nhập (không âm, không rỗng, đơn vị Cái/Chiếc/Bình là số nguyên) |
| 7 | `ValidationUtil4.validateDonGiaNhap(BigDecimal gia): String` | `public` | Static | Kiểm tra đơn giá nhập (lớn hơn 0, tối đa 12 chữ số nguyên, 2 số thập phân) |
| 8 | `DraftStore4.getDraft(HttpSession session, String draftId): PurchaseInvoiceDraftDTO4` | `public` | Static | Lấy bản nháp hóa đơn theo UUID và kiểm tra session lock |
| 9 | `DraftStore4.saveDraft(HttpSession session, PurchaseInvoiceDraftDTO4 draft): void` | `public` | Static | Lưu cập nhật bản nháp vào session, tăng version |
| 10 | `DraftStore4.removeDraft(HttpSession session, String draftId): void` | `public` | Static | Xóa bản nháp khỏi session khi xác nhận hoặc hủy |
