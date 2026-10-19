# HỆ THỐNG QUẢN LÝ GARA Ô TÔ GARAMAN (ĐỀ SỐ 26)

Ứng dụng Java Web doanh nghiệp phát triển theo kiến trúc MVC / DAO / JDBC chuẩn công nghiệp, phục vụ quản lý và vận hành gara ô tô.
Dự án được xây dựng hoàn chỉnh trong thư mục `app/` theo bộ kế hoạch và hợp đồng kỹ thuật nghiêm ngặt (`ke-hoach/01` đến `ke-hoach/10`), đồng bộ 100% giữa mã nguồn thực thi, cấu trúc CSDL và báo cáo thiết kế (`app/docs/BAO_CAO_DA_SUA.docx`).

---

## 1. Phạm vi Triển khai

### 1.1. Các chức năng hoàn chỉnh
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
  - Chống xác nhận trùng lặp (Double Submit / Retry) qua ràng buộc duy nhất `UNIQUE (maYeuCau)`.
  - In hóa đơn nhập theo định dạng chuẩn A4 (việc in không làm thay đổi trạng thái thanh toán hoặc tồn kho).
  - Ghi nhận thanh toán: Chỉ người lập mới có quyền thanh toán, kiểm tra đúng tổng tiền, chuyển trạng thái ĐÃ THANH TOÁN.
  - Cho phép mở lại danh sách hóa đơn nhập chưa thanh toán của riêng nhân viên kho để thanh toán sau.
- **Module phụ trợ: Đăng nhập & Điều hướng theo vai trò**
  - Xác thực người dùng bằng thuật toán băm mật khẩu chuẩn công nghiệp PBKDF2WithHmacSHA256 kèm muối ngẫu nhiên (Salt).
  - Chống Session Fixation (tái tạo session ID mới ngay sau khi đăng nhập thành công).
  - Bộ lọc bảo vệ (`AuthFilter4`) phân quyền chặt chẽ giữa vai trò Khách hàng (`Customer`) và Nhân viên kho (`WarehouseStaff`).
  - Bảo vệ chống tấn công CSRF (Cross-Site Request Forgery) bằng token đồng bộ trên toàn bộ các biểu mẫu POST.
  - Bộ lọc mã hóa tiếng Việt (`EncodingFilter4`) ép kiểu `UTF-8` cho toàn bộ Request và Response.

### 1.2. Giới hạn phạm vi đề 26
- Hệ thống xây dựng đầy đủ 16 bảng CSDL InnoDB và 23 lớp thực thể Java POJO của toàn bộ hệ thống GaraMan để đảm bảo tính toàn vẹn quan hệ và khả năng mở rộng.
- Các module ngoài Đề 26 (như Tiếp nhận khách hàng, Lập báo giá, Sửa chữa, Thống kê...) chỉ được chuẩn bị sẵn ở tầng mô hình lớp POJO và CSDL; không mở rộng Controller và View ngoài phạm vi để tuân thủ quy định bảo vệ đồ án.

---

## 2. Yêu cầu Môi trường

- **Hệ điều hành:** Windows, Linux hoặc macOS.
- **Java Development Kit (JDK):** Phiên bản **Java 17 LTS**.
- **Apache Maven:** Phiên bản **3.8+** (hoặc dùng Maven wrapper có sẵn).
- **Web Container:** **Apache Tomcat 9.0.x** (Mã nguồn sử dụng chuẩn `javax.servlet.*`, Servlet 4.0 và JSP 2.3).
  *(Lưu ý: Không dùng Tomcat 10+ vì Tomcat 10 chuyển sang gói `jakarta.*`, không tương thích với Servlet 4.0).*
- **Cơ sở dữ liệu:** **MySQL Server 8.0** hoặc **8.4 LTS** (Engine InnoDB, bảng mã `utf8mb4_unicode_ci`).

---

## 3. Cài đặt Cơ sở Dữ liệu

Mở công cụ dòng lệnh MySQL Client hoặc MySQL Workbench, kết nối với quyền root và thực thi tuần tự 3 tập tin SQL trong thư mục `app/database/`:

```bash
# 1. Khởi tạo cơ sở dữ liệu và 16 bảng với đầy đủ ràng buộc toàn vẹn
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
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/garaman4?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true&useSSL=false
db.user=root
db.password=root123
db.initialSize=5
db.maxTotal=20
```

*Lưu ý:* Hệ thống cũng hỗ trợ ghi đè cấu hình qua các biến môi trường hệ thống:
- `GARAMAN_DB_URL` (hoặc `DB_URL`)
- `GARAMAN_DB_USER` (hoặc `DB_USER`)
- `GARAMAN_DB_PASSWORD` (hoặc `DB_PASSWORD`)

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
| **Khách hàng** | `khach26` | `123456` | `/garaman/customer/home` | Tra cứu danh mục dịch vụ, phụ tùng, xem chi tiết giá bán và thông số. Bị chặn truy cập các trang kho. |
| **Nhân viên kho** | `kho26` | `123456` | `/garaman/warehouse/home` | Chọn/thêm nhà cung cấp, thêm phụ tùng, lập hóa đơn nhập, in ấn và thanh toán. Bị chặn truy cập trang khách hàng. |

---

## 8. Danh mục Route & Kịch bản Kiểm thử Nghiệp vụ

| Route / URL | Phương thức | Mô tả chức năng | Vai trò được phép |
|---|---|---|---|
| `/auth/login` | GET, POST | Giao diện và xử lý đăng nhập | Mọi người dùng |
| `/auth/logout` | GET, POST | Đăng xuất, hủy session | Mọi người dùng |
| `/customer/home` | GET | Trang chủ của Khách hàng | `Customer` |
| `/customer/catalog` | GET | Tìm kiếm dịch vụ và phụ tùng theo từ khóa (`?tuKhoa=...`) | `Customer` |
| `/customer/catalog/detail` | GET | Xem chi tiết thông số kỹ thuật dịch vụ / phụ tùng | `Customer` |
| `/warehouse/home` | GET | Trang chủ Nhân viên kho, danh sách hóa đơn chưa thanh toán | `WarehouseStaff` |
| `/warehouse/suppliers` | GET | Tìm kiếm và chọn nhà cung cấp | `WarehouseStaff` |
| `/warehouse/suppliers/new` | GET, POST | Thêm mới nhà cung cấp | `WarehouseStaff` |
| `/warehouse/parts/new` | GET, POST | Thêm mới phụ tùng vào danh mục | `WarehouseStaff` |
| `/warehouse/invoices` | GET, POST | Quản lý bản nháp, thêm dòng, sửa số lượng/đơn giá, xác nhận nhập | `WarehouseStaff` |
| `/warehouse/invoices/view` | GET | Xem hóa đơn nhập đã xác nhận (chi tiết, người lập, trạng thái) | `WarehouseStaff` |
| `/warehouse/invoices/print` | GET | Xem và in chứng từ hóa đơn nhập khổ A4 | `WarehouseStaff` |
| `/warehouse/invoices/pay` | POST | Ghi nhận thanh toán cho hóa đơn nhập | `WarehouseStaff` (chính người lập) |
| `/warehouse/invoices/unpaid` | GET | Xem danh sách các hóa đơn nhập chưa thanh toán của bản thân | `WarehouseStaff` |

---

## 9. Cấu trúc Tài liệu Dự án

Tất cả các tài liệu kỹ thuật và thiết kế tĩnh đồng bộ nằm trong thư mục `app/docs/`:
- `BAO_CAO_DA_SUA.docx`: Bản sao báo cáo tốt nghiệp đã đồng bộ toàn bộ 10 điểm chỉnh sửa, bổ sung ghi chú kiểu dữ liệu và chèn chi tiết mục 4.3.1 Thiết kế tĩnh.
- `4.3.1_THIET_KE_TINH.md`: Bản thiết kế tĩnh chi tiết 4 bước của cả 2 module theo đúng bài giảng.
- `METHOD_MAP.md`: Bảng ánh xạ phương thức toàn diện giữa Controller, DAO, Model, Util và kịch bản.
- `MA_TRAN_TRUY_VET.md`: Ma trận truy vết hai chiều từ yêu cầu đề 26 đến code và test case.
- `DOI_CHIEU_PHAN_SAU.md`: Bàn giao chi tiết cho mục 4.3.2 (kịch bản v3, tuần tự), 4.3.3 và các chương tiếp theo.
- `KIEM_THU.md`: Báo cáo kết quả kiểm thử tự động và bảng kiểm thử kịch bản C01-C07, P01-P16.
- `TIEN_DO.md`: Bảng theo dõi tiến độ hoàn thành các giai đoạn dự án.
- `diagrams/`: Chứa các tập tin PlantUML thiết kế lớp và biểu đồ tuần tự.
- `screenshots/`: Chứa 13 ảnh chụp màn hình ứng dụng thực tế.
