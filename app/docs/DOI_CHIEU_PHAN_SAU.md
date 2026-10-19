# TÀI LIỆU ĐỐI CHIẾU VÀ BÀN GIAO THIẾT KẾ PHẦN SAU (DOI_CHIEU_PHAN_SAU.md)

Tài liệu này cung cấp toàn bộ dữ liệu kỹ thuật thực tế từ mã nguồn đã triển khai (chữ ký hàm, route, biến input/output, điều kiện rẽ nhánh, danh sách actor/lifelines) để người dùng tiếp tục hoàn thiện mục 4.3.2 (Thiết kế động), mục 4.3.3 (Thiết kế triển khai), Chương 5 (Cài đặt) và Chương 6 (Rà soát & Kiểm thử) trong Báo cáo Đề 26.

---

## 1. Dữ liệu chuẩn bị cho Mục 4.3.2 (Thiết kế động)

### 1.1. Danh sách Lifeline (Tác nhân & Thành phần hệ thống)

#### Modul Khách hàng tra cứu:
1. `Actor`: `Khách hàng` (Customer)
2. `Boundary (View)`: `gdChinhKH.jsp`, `gdTimThongTin.jsp`, `gdChiTietThongTin.jsp`
3. `Controller`: `com.garaman.controller.CatalogServlet4`
4. `DAO`: `com.garaman.dao.ServiceDAO4`, `com.garaman.dao.SparePartDAO4`
5. `Entity`: `com.garaman.model.Service4`, `com.garaman.model.SparePart4`, `com.garaman.model.SparePartCategory4`

#### Modul Nhân viên kho nhập phụ tùng:
1. `Actor`: `Nhân viên kho` (WarehouseStaff)
2. `Boundary (View)`: `gdChinhKho.jsp`, `gdChonNhaCungCap.jsp`, `gdThemNhaCungCap.jsp`, `gdLapHoaDonNhap.jsp` (kèm `parts-picker.jspf`, `invoice-lines.jspf`), `gdThemPhuTung.jsp`, `gdHoaDonNhapDaXacNhan.jsp`, `gdInHoaDonNhap.jsp`
3. `Controller`: `com.garaman.controller.SupplierServlet4`, `com.garaman.controller.SparePartServlet4`, `com.garaman.controller.PurchaseInvoiceServlet4`
4. `Technical Utilities`: `com.garaman.util.DraftStore4`, `com.garaman.util.ValidationUtil4`, `com.garaman.util.CsrfUtil4`
5. `DAO`: `com.garaman.dao.SupplierDAO4`, `com.garaman.dao.WarehouseStaffDAO4`, `com.garaman.dao.SparePartCategoryDAO4`, `com.garaman.dao.SparePartDAO4`, `com.garaman.dao.PurchaseInvoiceDAO4`
6. `Database / Transaction`: `java.sql.Connection` (MySQL InnoDB Transaction)
7. `Entity`: `com.garaman.model.Supplier4`, `com.garaman.model.WarehouseStaff4`, `com.garaman.model.SparePart4`, `com.garaman.model.PurchaseInvoice4`, `com.garaman.model.PurchaseInvoiceDetail4`

---

### 1.2. Hướng dẫn viết Kịch bản Phiên bản 3 (Design Scenarios)

Khi viết kịch bản phiên bản 3 cho mục 4.3.2, người dùng tuân thủ cấu trúc:
**Actor -> Giao diện / Form -> Servlet -> DAO / Model -> CSDL -> Servlet Forward / Redirect -> View hiển thị**.

#### Kịch bản Tra cứu dịch vụ / phụ tùng (Phiên bản 3):
- **Tiền điều kiện:** Người dùng truy cập hệ thống ở vai trò khách hàng.
- **Luồng chính:**
  1. Khách hàng bấm liên kết tra cứu trên `gdChinhKH.jsp`.
  2. Trình duyệt gửi `GET /customer/catalog` tới `CatalogServlet4`.
  3. `CatalogServlet4.hienThiTimKiem` forward sang `gdTimThongTin.jsp` với trạng thái ban đầu (chưa tìm).
  4. Khách hàng nhập từ khóa `"lọc"` vào ô tìm kiếm và nhấn nút `"Tìm kiếm"`.
  5. Form gửi `GET /customer/catalog?tuKhoa=lọc` tới `CatalogServlet4`.
  6. `CatalogServlet4.timKiem` cắt khoảng trắng (`trim`), kiểm tra từ khóa không rỗng.
  7. Servlet gọi `ServiceDAO4.timDichVuTheoTen("lọc")` nhận về `List<Service4>`.
  8. Servlet gọi `SparePartDAO4.timPhuTungTheoTen("lọc", true)` nhận về `List<SparePart4>`.
  9. Servlet gán các danh sách vào request attributes (`dsDichVu`, `dsPhuTung`, `tuKhoa`), forward sang `gdTimThongTin.jsp`.
  10. `gdTimThongTin.jsp` render bảng Dịch vụ (có DV1) và bảng Phụ tùng (có PT1).
  11. Khách hàng nhấn liên kết xem chi tiết của PT1.
  12. Trình duyệt gửi `GET /customer/catalog/detail?id=1&loai=spare-part&tuKhoa=lọc` tới `CatalogServlet4`.
  13. `CatalogServlet4.xemChiTiet` gọi `SparePartDAO4.layChiTietPhuTung(1, true)` đọc dữ liệu phụ tùng kèm nhóm `SparePartCategory4`.
  14. Servlet gán `phuTung` và `tuKhoa` vào request attribute, forward sang `gdChiTietThongTin.jsp`.
  15. Khách hàng bấm nút *"Quay lại kết quả tìm kiếm"*, trình duyệt gửi `GET /customer/catalog?tuKhoa=lọc`, quay lại bước 5.
- **Ngoại lệ:**
  - `3a. Từ khóa rỗng:` Khách hàng để trống từ khóa và bấm tìm. `CatalogServlet4.timKiem` phát hiện từ khóa rỗng, set `errorMessage = "Vui lòng nhập từ khóa tìm kiếm hợp lệ."`, forward về `gdTimThongTin.jsp`.
  - `4a. Không tìm thấy kết quả:` Hai DAO trả về danh sách rỗng. `gdTimThongTin.jsp` hiển thị thông điệp *"Không tìm thấy kết quả phù hợp với từ khóa đã nhập."*

#### Kịch bản Nhập phụ tùng (Phiên bản 3):
- **Tiền điều kiện:** Nhân viên kho đã đăng nhập thành công vào hệ thống.
- **Luồng chính:**
  1. Nhân viên nhấn *"Nhập phụ tùng"* trên `gdChinhKho.jsp`.
  2. Trình duyệt gửi `GET /warehouse/suppliers` tới `SupplierServlet4.danhSachNhaCungCap`. Servlet gọi `SupplierDAO4.timNhaCungCapTheoTen("")` và forward sang `gdChonNhaCungCap.jsp`.
  3. Nhân viên chọn nhà cung cấp (ID: 1 - Hòa Phát), bấm nút *"Chọn"*.
  4. Trình duyệt gửi `GET /warehouse/invoices?action=start&supplierId=1` tới `PurchaseInvoiceServlet4.khoiTaoHoaDon`.
  5. Servlet lấy thông tin NCC từ `SupplierDAO4`, tạo bản nháp `PurchaseInvoiceDraftDTO4` mới với mã `maYeuCau` UUID, lưu vào session qua `DraftStore4.saveDraft`, sau đó redirect tới `GET /warehouse/invoices?draftId=...`.
  6. `PurchaseInvoiceServlet4.hienThiLapHoaDon` đọc nháp từ session, forward sang `gdLapHoaDonNhap.jsp`.
  7. Nhân viên nhập từ khóa tìm phụ tùng trong ô tìm kiếm nhúng, bấm nút *"Tìm phụ tùng"*.
  8. Trình duyệt gửi `POST /warehouse/invoices?action=search-part`. Servlet cập nhật số lượng và đơn giá của các dòng đang nhập trên form vào bản nháp, gọi `SparePartDAO4.timPhuTungTheoTen(tuKhoa, false)`, gán kết quả tìm kiếm vào request và forward lại `gdLapHoaDonNhap.jsp`.
  9. Nhân viên bấm nút *"Thêm vào hóa đơn"* tại dòng phụ tùng PT1.
  10. Trình duyệt gửi `POST /warehouse/invoices?action=add-line&selectedPartId=1`. Servlet kiểm tra nếu PT1 đã có trong bảng thì chỉ focus; nếu chưa có thì thêm một dòng mới với số lượng `0`, đơn giá `0`, lưu nháp và forward lại trang.
  11. Nhân viên nhập số lượng `10` Cái, đơn giá nhập `80.000 ₫`, bấm *"Xác nhận hóa đơn"*.
  12. Trình duyệt gửi `POST /warehouse/invoices?action=confirm` kèm CSRF token, `draftId`, `version` và các tham số dòng.
  13. `PurchaseInvoiceServlet4.xacNhanHoaDon` kiểm tra version chống xung đột, validate dữ liệu từng dòng qua `ValidationUtil4`.
  14. Servlet lấy thông tin nhân viên lập từ session (`currentUser`), dựng đối tượng `PurchaseInvoice4`.
  15. Servlet gọi `PurchaseInvoiceDAO4.luuHoaDonNhap(hoaDon)`.
  16. `PurchaseInvoiceDAO4` mở giao dịch CSDL (`conn.setAutoCommit(false)`), thực thi khóa thứ tự chống Deadlock (`khoaDuLieu`), ghi header (`chenHoaDon`), ghi chi tiết với snapshot tên/đơn vị (`chenChiTiet`), gọi `SparePartDAO4.tangTonSauNhap(conn, 1, 10)` để cộng dồn tồn kho, sau đó gọi `conn.commit()`. Hàm trả về mã ID hóa đơn vừa sinh (ví dụ: `101`).
  17. Servlet gọi `DraftStore4.removeDraft` xóa bản nháp khỏi session, gửi redirect `GET /warehouse/invoices/view?id=101`.
  18. `PurchaseInvoiceServlet4.xemHoaDon` gọi `PurchaseInvoiceDAO4.layThongTinHoaDonNhap(101)`, forward sang `gdHoaDonNhapDaXacNhan.jsp` hiển thị hóa đơn trạng thái `CHƯA THANH TOÁN`.
  19. Nhân viên bấm *"In hóa đơn"*, trình duyệt gửi `GET /warehouse/invoices/print?id=101`, forward sang `gdInHoaDonNhap.jsp` hiển thị mẫu in A4 (tồn kho không đổi).
  20. Nhân viên quay lại trang xem hóa đơn, nhập số tiền thanh toán `800.000 ₫`, bấm *"Xác nhận thanh toán"*.
  21. Trình duyệt gửi `POST /warehouse/invoices/pay` kèm `id=101`, `soTienThanhToan=800000`, `csrfToken`.
  22. `PurchaseInvoiceServlet4.thanhToanHoaDon` kiểm tra đúng người lập, gọi `PurchaseInvoiceDAO4.ghiNhanThanhToan(101, 800000, currentUserId)`.
  23. `PurchaseInvoiceDAO4` kiểm tra điều kiện trạng thái `CHUA_THANH_TOAN` và số tiền khớp đúng tổng tiền, thực thi `UPDATE tblPurchaseInvoice4 SET trangThaiThanhToan='DA_THANH_TOAN', ngayThanhToan=NOW()`.
  24. Servlet redirect về `GET /warehouse/invoices/view?id=101`. Hóa đơn hiển thị trạng thái `ĐÃ THANH TOÁN`.
- **Ngoại lệ chính:**
  - `4a. Thêm NCC mới:` Bấm thêm mới -> `GET /warehouse/suppliers/new` -> submit form -> `POST /warehouse/suppliers/new` -> `SupplierDAO4.themNhaCungCap` -> redirect về chọn NCC.
  - `8a. Thêm phụ tùng mới:` Bấm thêm mới -> `GET /warehouse/parts/new?draftId=...` -> submit form -> `POST /warehouse/parts/new` -> `SparePartDAO4.themPhuTung` (tồn 0, Chưa công bố) -> quay về đúng draft.
  - `12a. Hủy lập hóa đơn:` Bấm *"Hủy hóa đơn"* -> `POST /warehouse/invoices?action=cancel` -> `DraftStore4.removeDraft` -> redirect về chọn NCC (các NCC và phụ tùng đã tạo vẫn giữ nguyên).
  - `12b. Dữ liệu dòng không hợp lệ:` Trống dòng, số lượng <= 0 hoặc đơn giá <= 0 -> giữ nguyên các giá trị thô trên form, hiển thị thông báo lỗi chi tiết, không ghi CSDL.
  - `12c. Submit trùng lặp / Network Retry:` Nhấn xác nhận hai lần hoặc gửi lại request với cùng `maYeuCau` UUID -> CSDL kích hoạt bắt lỗi trùng mã hoặc trả về hóa đơn ID đã lưu trước đó mà không cộng tồn lần hai (idempotent).
  - `18b. Thanh toán sau:` Nhân viên đăng xuất hoặc chuyển trang mà chưa thanh toán -> Truy cập Trang chủ Kho -> Mục *"Hóa đơn chưa thanh toán của tôi"* (`GET /warehouse/invoices/unpaid`) -> Click mở lại hóa đơn và thanh toán.

---

## 2. Dữ liệu chuẩn bị cho Mục 4.3.3 (Thiết kế triển khai)

### 2.1. Biểu đồ gói (Package Diagram)
Cấu trúc gói thực tế của dự án trong `app/src/main/java/`:
- `com.garaman.model`: Chứa 23 lớp thực thể POJO nghiệp vụ (`Member4`, `Employee4`, `WarehouseStaff4`, `Customer4`, `Service4`, `SparePart4`, `SparePartCategory4`, `Supplier4`, `PurchaseInvoice4`, `PurchaseInvoiceDetail4`, ...).
- `com.garaman.dao`: Chứa lớp trừu tượng `DAO` và các lớp truy xuất dữ liệu (`MemberDAO4`, `WarehouseStaffDAO4`, `ServiceDAO4`, `SparePartDAO4`, `SparePartCategoryDAO4`, `SupplierDAO4`, `PurchaseInvoiceDAO4`).
- `com.garaman.controller`: Chứa các Servlet điều khiển (`AuthServlet4`, `HomeServlet4`, `CatalogServlet4`, `SupplierServlet4`, `SparePartServlet4`, `PurchaseInvoiceServlet4`).
- `com.garaman.filter`: Chứa các bộ lọc chặn truy cập và mã hóa (`EncodingFilter4`, `AuthFilter4`).
- `com.garaman.util`: Chứa các tiện ích hỗ trợ kỹ thuật (`DBUtil4`, `PasswordUtil4`, `CsrfUtil4`, `ValidationUtil4`, `DraftStore4`).
- `com.garaman.dto`: Chứa các đối tượng truyền tải dữ liệu bản nháp (`PurchaseInvoiceDraftDTO4`, `DraftLineDTO4`).
- `com.garaman.exception`: Chứa các ngoại lệ tùy biến hệ thống (`ValidationException4`, `ConcurrencyException4`, `NotFoundException4`).
- `Web Views (webapp/)`:
  - `member/`: `gdDangNhap.jsp`
  - `customer/`: `gdChinhKH.jsp`, `gdTimThongTin.jsp`, `gdChiTietThongTin.jsp`
  - `warehouse/`: `gdChinhKho.jsp`, `gdChonNhaCungCap.jsp`, `gdThemNhaCungCap.jsp`, `gdLapHoaDonNhap.jsp`, `gdThemPhuTung.jsp`, `gdHoaDonNhapDaXacNhan.jsp`, `gdInHoaDonNhap.jsp`
  - `WEB-INF/jspf/`: `header.jspf`, `footer.jspf`, `messages.jspf`, `steps.jspf`, `parts-picker.jspf`, `invoice-lines.jspf`

### 2.2. Biểu đồ triển khai (Deployment Diagram)
- **Node 1: Thiết bị người dùng (Client Machine / Browser):**
  - Microsoft Edge / Google Chrome chạy trên Windows / macOS / Linux.
  - Giao tiếp qua giao thức HTTP / HTTPS cổng 8080 (hoặc 8096 khi demo).
- **Node 2: Máy chủ ứng dụng (Web / Application Server):**
  - Môi trường thực thi: Java Runtime Environment (JRE / JDK 17).
  - Web Container: Apache Tomcat 9.0.x (hỗ trợ Servlet 4.0 `javax.servlet.*` và JSP 2.3).
  - Thành phần triển khai: Tập tin nén `garaman.war` triển khai tại đường dẫn context `/garaman`.
- **Node 3: Máy chủ cơ sở dữ liệu (Database Server):**
  - RDBMS: MySQL Server 8.0 hoặc 8.4 LTS.
  - Cơ sở dữ liệu: `garaman4` (InnoDB, bảng mã `utf8mb4`).
  - Giao tiếp với App Server qua JDBC Connection Pool trên cổng chuẩn 3306.

---

## 3. Dữ liệu chuẩn bị cho Chương 5 (Cài đặt) và Chương 6 (Kiểm thử)

- **Cấu hình môi trường và triển khai:** Được ghi đầy đủ trong `app/README.md`.
- **Kịch bản kiểm thử chức năng:**
  - 7 ca kiểm thử tra cứu: `C01` đến `C07`.
  - 16 ca kiểm thử nhập phụ tùng: `P01` đến `P16`.
- **Kiểm thử đơn vị (Unit Tests):**
  - `PurchaseInvoice4Test`: Kiểm tra tính tổng tiền hóa đơn, thành tiền dòng, làm tròn số thập phân.
  - `Validation4Test`: Kiểm tra ranh giới số lượng, đơn giá, đơn vị tính Cái vs Lít.
  - `PasswordUtil4Test`: Kiểm tra thuật toán băm mật khẩu PBKDF2 khớp seed data.
- **Kiểm thử tích hợp (Integration Tests):**
  - `PurchaseInvoiceDAO4IT`: Kiểm tra transaction rollback khi lỗi dòng, chống xác nhận lặp lại, bảo toàn snapshot lịch sử tên và đơn vị áp dụng.
