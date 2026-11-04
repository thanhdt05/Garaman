# HỆ THỐNG QUẢN LÝ GARA Ô TÔ GARAMAN (ĐỀ SỐ 26)

Ứng dụng Java Web JSP / Servlet / DAO / JDBC cho hai module đề 26: khách tra cứu dịch vụ/phụ tùng và nhân viên kho nhập phụ tùng.

**Sửa luồng theo báo cáo 30/10/2026:** nhân viên kho ca hiện tại được ghi nhận thanh toán hóa đơn do người khác lập; lưu riêng người thực sự trả và giữ người lập. Danh sách hóa đơn chưa thanh toán dùng chung cho gara. CSDL tên tiếng Anh đang có cần migration [06_migrate_purchase_payment.sql](database/06_migrate_purchase_payment.sql) trước khi dùng WAR mới; CSDL tạo mới đã có cột trong schema 01. Xem [luồng đã sửa và kiểm chứng](docs/SUA_LUONG_THEO_BAO_CAO.md).

**Cập nhật cấu trúc 30/10/2026:** bỏ các gói Java `exception`, `util`, `filter`, `dto`; chuyển xử lí cần thiết vào các hàm Servlet/DAO đang có, không thêm hàm ứng dụng. JSP nhân viên kho nằm trong `WEB-INF/view/warehouseStaff`. Xem [thay đổi và kiểm chứng](docs/RUT_GON_CODE_THEO_GOI.md), [phần package cho báo cáo](docs/THIET_KE_GOI.md) và [hướng dẫn vẽ đủ lớp/JSP](docs/VE_PACKAGE_VISUAL_PARADIGM.md).
Phạm vi theo `ke-hoach/00_PHAM_VI.md`, luồng/UI theo `11`. Source và tài liệu hai module đã được sửa theo rà soát `13`; xem kết quả kiểm tra thực tế tại `docs/KIEM_THU.md`. Bản sao Word cũ chưa được cập nhật theo lần sửa này.

**Cập nhật tên tiếng Anh 21/10/2026:** source Java/JSP/JavaScript, tên file JSP và schema SQL đã đồng bộ theo bộ tên trong báo cáo. Xem [hướng dẫn chuyển đổi](docs/CODE_ENGLISH_MIGRATION.md) và [CSV tên cũ → mới](docs/CODE_ENGLISH_RENAME.csv). Nếu dùng CSDL cũ, cần chạy migration `database/04_migrate_english_names.sql` sau khi sao lưu và dừng app; không chạy lại seed lên dữ liệu đang có. CSDL/Tomcat tại cổng 8080 chưa được chuyển đổi vì đang chờ chủ bài đồng ý. Bản kiểm tra riêng chạy tại `http://localhost:18099/garaman/` với CSDL test tách biệt.

---

## 1. Phạm vi Triển khai

### 1.1. Chức năng có trong source
- **Module 1 (Đề 26): Khách hàng tìm kiếm thông tin dịch vụ / phụ tùng**
  - Tra cứu đồng thời hai danh mục Dịch vụ và Phụ tùng theo từ khóa không phân biệt hoa thường và dấu tiếng Việt.
  - Xem chi tiết thông số kỹ thuật, bảo hành của từng dịch vụ và phụ tùng.
  - Bảo mật giá bán niêm yết, tuyệt đối không làm lộ giá vốn và số lượng tồn kho cho khách hàng.
  - Giữ nguyên từ khóa và trạng thái tìm kiếm khi quay lại.
- **Module 2 (Đề 26): Nhân viên kho nhập phụ tùng**
  - Tìm kiếm và chọn nhà cung cấp; hỗ trợ thêm mới nhà cung cấp ngay trong luồng nghiệp vụ.
  - Lập hóa đơn nhập nhiều dòng phụ tùng; hỗ trợ thêm mới phụ tùng chưa có trong danh mục (tồn khởi tạo = 0, trạng thái Chưa công bố).
  - Tự động kiểm tra trùng lặp phụ tùng trong bảng (chỉ focus dòng cũ, không tạo dòng thừa).
  - Quản lý bản nháp an toàn theo phiên người dùng (`draftId` UUID, kiểm soát xung đột version, hỗ trợ mở nhiều tab độc lập).
  - Thực thi giao dịch CSDL (ACID Transaction) toàn vẹn: Khóa thứ tự chống Deadlock, lưu hóa đơn, chốt snapshot tên/đơn vị áp dụng lịch sử, tự động cộng dồn tồn kho.
  - Chống xác nhận trùng lặp (Double Submit / Retry) qua ràng buộc duy nhất `UNIQUE (requestCode)`.
  - In hóa đơn nhập theo định dạng chuẩn A4 (việc in không làm thay đổi trạng thái thanh toán hoặc tồn kho).
  - Nhân viên kho đang có quyền được ghi nhận thanh toán đủ tổng tiền; lưu người thực sự trả, thời điểm và trạng thái, giữ nguyên người lập.
  - Mở lại danh sách hóa đơn nhập chưa thanh toán của gara, gồm cả hóa đơn do nhân viên ca trước lập.
- **Module phụ trợ: Đăng nhập & Điều hướng theo vai trò**
  - Xác thực người dùng bằng thuật toán băm mật khẩu chuẩn công nghiệp PBKDF2WithHmacSHA256 kèm muối ngẫu nhiên (Salt).
  - Chống Session Fixation (tái tạo session ID mới ngay sau khi đăng nhập thành công).
  - Các Servlet kiểm tra đăng nhập và phân quyền giữa vai trò Khách hàng (`Customer`) và Nhân viên kho (`WarehouseStaff`) tại `doGet()`/`doPost()`.
  - Bảo vệ chống tấn công CSRF (Cross-Site Request Forgery) bằng token đồng bộ trên toàn bộ các biểu mẫu POST.
  - Servlet thiết lập `UTF-8` cho Request và Response; JSP khai báo UTF-8.

### 1.2. Giới hạn phạm vi đề 26
- Hệ thống xây dựng chuẩn hóa đúng **9 bảng CSDL InnoDB** và **10 lớp thực thể Java POJO** phục vụ đúng hai module của đề 26 theo `ke-hoach/00_PHAM_VI.md` (các POJO ngoài phạm vi đã được tách riêng vào `app/archive/extra_models/` để bảo toàn mã nguồn mà không làm phình dự án).
- Đúng 9 bảng: `tblMember4, tblEmployee4, tblCustomer4, tblService4, tblSparePartCategory4, tblSparePart4, tblSupplier4, tblPurchaseInvoice4, tblPurchaseInvoiceDetail4`.
- Đúng 10 model: `Member4, Employee4, WarehouseStaff4, Customer4, Service4, SparePartCategory4, SparePart4, Supplier4, PurchaseInvoice4, PurchaseInvoiceDetail4`.
- Không tạo thành phần của các module khác (xe, lịch hẹn, tiếp nhận, hóa đơn khách, kỹ thuật viên, thống kê...).

---

## 2. Yêu cầu Môi trường

- **Hệ điều hành:** Windows, Linux hoặc macOS.
- **Java Development Kit (JDK):** Phiên bản **Java 17 LTS**.
- **Apache Maven:** Phiên bản **3.8+** (hoặc dùng Maven wrapper có sẵn).
- **Web Container:** **Apache Tomcat 9.0.x** (Mã nguồn sử dụng chuẩn `javax.servlet.*`, Servlet 4.0 và JSP 2.3).
  *(Lưu ý: Không dùng Tomcat 10+ vì Tomcat 10 chuyển sang gói `jakarta.*`, không tương thích với Servlet 4.0).*
- **Cơ sở dữ liệu:** **MySQL Server 8.4 LTS** (Engine InnoDB, bảng mã `utf8mb4_0900_ai_ci`).

---

## 3. Cài đặt Cơ sở Dữ liệu

Mở công cụ dòng lệnh MySQL Client hoặc MySQL Workbench, kết nối với quyền root và thực thi tuần tự 3 tập tin SQL trong thư mục `app/database/`:

```bash
# 1. Khởi tạo cơ sở dữ liệu và 9 bảng với đầy đủ ràng buộc toàn vẹn
mysql -u root -p < app/database/01_schema.sql

# 2. Nạp dữ liệu mẫu ban đầu (tài khoản demo, nhà cung cấp Hòa Phát, phụ tùng AF-01, nhóm danh mục)
mysql -u root -p < app/database/02_seed.sql

# 3. Chạy kiểm tra tính toàn vẹn dữ liệu
mysql -u root -p < app/database/03_verify.sql
```

---

## 4. Cấu hình Kết nối CSDL

Tạo tập tin cấu hình kết nối `db.properties` trong thư mục `app/src/main/resources/` (hoặc sao chép từ `db.properties.example`):

```properties
db.url=jdbc:mysql://localhost:3306/garaman4?connectionTimeZone=%2B07:00&forceConnectionTimeZoneToSession=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false
db.user=root
db.password=root123
```

`%2B07:00` là múi giờ `+07:00` đã mã hóa dấu cộng trong URL. Cách này đặt đúng giờ Việt Nam cho phiên JDBC mà không cần bảng múi giờ MySQL trên Windows. Nếu ép `Asia/Ho_Chi_Minh` khi máy chủ chưa nạp bảng múi giờ, MySQL sẽ báo lỗi 1298 `Unknown or incorrect time zone`.

Sau khi đổi cấu hình, cần cập nhật WAR và nạp lại ứng dụng Tomcat để cấu hình mới có hiệu lực.

*Lưu ý:* Hệ thống cũng hỗ trợ ghi đè cấu hình qua các biến môi trường hệ thống:
- `GARAMAN_DB_URL`
- `GARAMAN_DB_USER`
- `GARAMAN_DB_PASSWORD`

---

## 5. Biên dịch và Đóng gói Ứng dụng

Chuyển vào thư mục `app/` và chạy lệnh Maven để biên dịch, chạy các bài kiểm thử tự động và đóng gói thành tập tin WAR:

```bash
# Di chuyển vào thư mục app
cd app

# Biên dịch, chạy kiểm thử và đóng gói WAR
mvn clean package
```

Kết quả đóng gói sẽ tạo ra tập tin: `app/target/garaman.war`.

---

## 6. Triển khai lên Apache Tomcat 9

1. Sao chép tập tin `app/target/garaman.war` vào thư mục `webapps/` của Apache Tomcat 9.0.x:
   ```bash
   cp app/target/garaman.war /path/to/apache-tomcat-9.0.x/webapps/
   ```
2. Khởi động máy chủ Tomcat:
   - Trên Windows: Chạy file `bin/startup.bat` (hoặc `bin/catalina.bat run`)
   - Trên Linux/macOS: Chạy file `bin/startup.sh`
3. Truy cập ứng dụng qua trình duyệt web tại địa chỉ:
   ```text
   http://localhost:8080/garaman/
   ```

---

## 7. Tài khoản Đăng nhập Demo

Hệ thống đã nạp sẵn 2 tài khoản demo chuẩn đề 26 với mật khẩu đã được băm an toàn qua PBKDF2:

| Vai trò | Tên đăng nhập | Mật khẩu | Trang chuyển hướng sau khi đăng nhập | Quyền hạn truy cập |
|---|---|---|---|---|
| **Khách hàng** | `khach26` | `123456` | `/garaman/home` | Tra cứu danh mục dịch vụ, phụ tùng, xem chi tiết giá bán và thông số. Bị chặn truy cập các trang kho. |
| **Nhân viên kho** | `kho26` | `123456` | `/garaman/home` | Chọn/thêm nhà cung cấp, thêm phụ tùng, lập hóa đơn nhập, in ấn và thanh toán. Bị chặn truy cập trang khách hàng. |

---

## 8. Danh mục Route & Kịch bản Kiểm thử Nghiệp vụ

| Route / URL | Phương thức | Mô tả chức năng | Vai trò được phép |
|---|---|---|---|
| `/login` | GET, POST | Giao diện và xử lý đăng nhập | Mọi người dùng |
| `/logout` | POST | Đăng xuất, hủy session | Mọi người dùng |
| `/home` | GET | Trang chủ điều hướng theo vai trò (Khách hàng hoặc Nhân viên kho) | Người dùng đã đăng nhập |
| `/customer/catalog` | GET | Tìm kiếm dịch vụ và phụ tùng theo từ khóa (`?keyword=...&itemTypeFilter=...`) | `Customer` |
| `/customer/catalog/detail` | GET | Xem chi tiết thông số kỹ thuật dịch vụ / phụ tùng (`?id=...&itemType=service\|part`) | `Customer` |
| `/warehouse/suppliers` | GET | Tìm kiếm và chọn nhà cung cấp | `WarehouseStaff` |
| `/warehouse/suppliers/new` | GET | Mở form thêm mới nhà cung cấp | `WarehouseStaff` |
| `/warehouse/suppliers/create` | POST | Lưu nhà cung cấp mới vào CSDL | `WarehouseStaff` |
| `/warehouse/parts/new` | GET | Mở form thêm mới phụ tùng vào danh mục | `WarehouseStaff` |
| `/warehouse/parts/create` | POST | Lưu phụ tùng mới vào CSDL | `WarehouseStaff` |
| `/warehouse/purchases/start` | POST | Khởi tạo phiên nhập nháp với nhà cung cấp | `WarehouseStaff` |
| `/warehouse/purchases/draft` | GET | Xem và chỉnh sửa bản nháp hóa đơn nhập | `WarehouseStaff` |
| `/warehouse/purchases/search-part` | POST | Tìm phụ tùng trên hóa đơn và bảo toàn các dòng nhập | `WarehouseStaff` |
| `/warehouse/purchases/add-line` | POST | Thêm dòng phụ tùng vào hóa đơn nháp (focus nếu trùng) | `WarehouseStaff` |
| `/warehouse/purchases/update` | POST | Cập nhật số lượng, đơn giá và tính lại tiền | `WarehouseStaff` |
| `/warehouse/purchases/remove-line` | POST | Xóa một dòng phụ tùng khỏi hóa đơn nháp | `WarehouseStaff` |
| `/warehouse/purchases/cancel` | POST | Hủy bản nháp hóa đơn trong session | `WarehouseStaff` |
| `/warehouse/purchases/confirm` | POST | Xác nhận hóa đơn nhập, ghi nhận DB và tăng tồn kho | `WarehouseStaff` |
| `/warehouse/purchases/view` | GET | Xem hóa đơn nhập đã xác nhận (chi tiết snapshot, người lập, trạng thái) | `WarehouseStaff` |
| `/warehouse/purchases/print` | GET | Xem và in chứng từ hóa đơn nhập khổ A4 | `WarehouseStaff` |
| `/warehouse/purchases/pay` | POST | Ghi nhận thanh toán; lưu nhân viên thực sự trả tiền | `WarehouseStaff` đang có quyền |
| `/warehouse/purchases/unpaid` | GET | Xem danh sách hóa đơn nhập chưa thanh toán của gara | `WarehouseStaff` đang có quyền |

---

## 9. Cấu trúc Tài liệu Dự án

Tất cả các tài liệu kỹ thuật và thiết kế tĩnh đồng bộ nằm trong thư mục `app/docs/`:
- `BAO_CAO_DA_SUA.docx`: Bản sao Word từ lần tạo trước; chưa đồng bộ với lần sửa source/tài liệu hiện tại, không dùng làm bằng chứng đã nghiệm thu.
- `4.3.1_THIET_KE_TINH.md`: Bản thiết kế tĩnh chi tiết 4 bước của cả 2 module theo đúng bài giảng.
- `METHOD_MAP.md`: Bảng ánh xạ phương thức toàn diện giữa Controller, DAO, Model, Util và kịch bản.
- `MA_TRAN_TRUY_VET.md`: Ma trận truy vết hai chiều từ yêu cầu đề 26 đến code và test case.
- `DOI_CHIEU_PHAN_SAU.md`: Bàn giao chi tiết cho mục 4.3.2 (kịch bản v3, tuần tự), 4.3.3 và các chương tiếp theo.
- `KIEM_THU.md`: Báo cáo kết quả kiểm thử tự động và bảng kiểm thử kịch bản C01-C07, P01-P16.
- `TIEN_DO.md`: Bảng theo dõi tiến độ hoàn thành các giai đoạn dự án.
- `diagrams/`: Chứa các tập tin PlantUML thiết kế lớp và biểu đồ tuần tự.
- `screenshots/`: 18 ảnh mẫu từ prototype, dùng tham chiếu thiết kế; chưa phải ảnh backend chạy thật.
