# Kế hoạch chi tiết cho Gemini — GaraMan đề 26

**Phương án cuối:** giữ DOCX mới và hai module đề 26; lưu `tonKho` trực tiếp; mỗi hóa đơn có đúng một người lập và người đó thực hiện thu/chi; người tiếp nhận lưu riêng trên phiếu. Không thêm quan hệ nhân viên thanh toán riêng. File này là kế hoạch thực hiện chính; file 03 khóa schema/route/dữ liệu; file 07 khóa cách viết và đối chiếu báo cáo.

## 1. Đầu ra và giới hạn công việc

Đọc bản DOCX mới nhất, không áp lại danh sách sửa cũ lên những hình người dùng đã sửa. Trước khi code, tạo bản sao báo cáo và chỉ sửa các điểm còn lệch trong file 01. Không thêm 14 trường lần nữa: đó là danh mục cuối để kiểm tra trường đã có. Mọi hình mới, chữ ký hàm và đoạn báo cáo phải truy về code thực tế theo file 07. Giữ nguyên file gốc để đối chiếu.

Với hình CSDL vẽ bằng Paradigm không có kiểu datetime trong danh sách đang dùng, giữ nhãn date và chèn ghi chú ánh xạ theo file 01. Từ điển dữ liệu/SQL vẫn dùng DATETIME cho thời điểm, DATE cho ngày sinh. Kiểm tra DDL thực tế khi sinh từ công cụ; không bắt người dùng đổi công cụ hoặc đổi nhãn không được hỗ trợ.

Đầu ra cần có:

1. Project Java Web build thành WAR, có JSP, Servlet, JDBC, DAO và MySQL.
2. Đăng nhập/đăng xuất cho hai vai trò cần demo: Customer và WarehouseStaff.
3. Module tra cứu dịch vụ/phụ tùng: mở trang trống → tìm theo tên → xem chi tiết đúng loại → quay lại kết quả.
4. Module nhập: chọn/thêm nhà cung cấp → chọn/thêm phụ tùng → nhập nhiều dòng → xác nhận một lần → xem/in hóa đơn → ghi nhận trả đủ tiền.
5. Schema toàn hệ thống từ mô hình hiện có; seed phục vụ hai module; không có bảng thống kê hoặc bảng vai trò con đã loại; giữ cột tonKho, không lưu thanhTien/tongTien.
6. Nội dung mục 4.3.1 theo bốn bước bài giảng, hai sơ đồ luồng màn hình, hai biểu đồ lớp thiết kế có chữ ký thật, ảnh giao diện thật và ma trận truy vết.
7. Hướng dẫn build/chạy/kiểm tra có kết quả thực tế.

Các màn hình quản lí, tiếp nhận sửa xe, thanh toán khách, đặt lịch và thống kê chưa triển khai trong đợt này. CSDL và thực thể vẫn biểu diễn các đối tượng đó. Trang chủ hai vai trò chỉ bật chức năng thuộc đợt này; không tạo nút dẫn tới trang lỗi rồi gọi là chức năng hoàn chỉnh.

## 2. Kiến trúc và bộ công nghệ chốt

```text
Trình duyệt → Filter → Servlet → DAO → MySQL
                          ↓        ↓
                          └── Model ┘
                          ↓
                    JSP + JSTL/EL → HTML
```

- Java 17; Maven, đóng gói `war`, tên artifact/context mặc định `garaman`.
- Tomcat 9.0.x, Servlet 4.0, JSP 2.3, namespace `javax.servlet.*`, JSTL 1.2.
- MySQL 8.4, InnoDB, `utf8mb4`; JDBC Connector/J thuộc nhánh tương thích MySQL 8.4 và Java 17.
- JSP + HTML/CSS cục bộ + JavaScript thuần. Không cần framework giao diện hoặc CDN để demo.
- JUnit 5 cho phần tính/validate quan trọng; kiểm tra transaction DAO bằng MySQL test riêng và kiểm tra giao diện bằng trình duyệt.

Chọn Tomcat 9 để theo namespace `javax.servlet` trong Lab1. Tomcat 9 hỗ trợ Servlet 4.0/JSP 2.3; Tomcat 10 chuyển sang `jakarta.*`, nên không trộn hai bộ API. Căn cứ: [bảng phiên bản Tomcat](https://tomcat.apache.org/whichversion.html), [hướng dẫn migration Tomcat 10](https://tomcat.apache.org/migration-10.html).

Maven chỉ chuẩn hóa build và thư viện; không làm thay đổi mô hình bài giảng. Bộ dependency đã chốt: `javax.servlet:javax.servlet-api:4.0.1` (provided), `javax.servlet:jstl:1.2`, `com.mysql:mysql-connector-j:8.4.0`, `org.junit.jupiter:junit-jupiter:5.10.2` (test). Plugin đã chốt: compiler `3.13.0`, war `3.4.0`, surefire và failsafe `3.2.5`. Đây là bộ phiên bản cố định để tái hiện, không tuyên bố là bản mới nhất. Gemini phải xác minh resolve/build trong môi trường chạy; nếu cần đổi patch/version thì ghi lý do vào nhật ký trước khi sửa. Không dùng `LATEST` hoặc dấu `+`. Không đóng gói `servlet-api`/`jsp-api` của container trong WAR; Servlet API có scope `provided`.

Không dùng Spring, Spring Boot, Hibernate/JPA, React, REST API riêng, microservice, Redis hoặc module thanh toán trực tuyến. Không có yêu cầu nào trong hai module cần chúng.

### Trách nhiệm từng phần

| Thành phần | Được làm | Giới hạn |
|---|---|---|
| JSP | Render request attributes, form, bảng, trạng thái lỗi và liên kết | Không chứa JDBC/SQL, không gọi DAO, không thực hiện transaction |
| Servlet | Parse input, validate, lấy user từ session, thao tác nháp, gọi DAO, forward/redirect | Không ghép SQL; không giữ dữ liệu riêng người dùng trong field của Servlet |
| DAO | SQL, map ResultSet, truy xuất dữ liệu và transaction hóa đơn | Không truy cập request/session, không render HTML |
| Model | Thuộc tính, getter/setter, tính thành tiền/tổng tiền | Không tự mở Connection |
| Filter | Encoding, kiểm tra phiên/quyền, token POST | Không thực hiện nghiệp vụ nhập hàng |
| JavaScript | Xem trước tiền, focus dòng đã có, print, xác nhận hủy | Kết quả tính phía server mới là kết quả chốt |

Không thêm service/repository/interface cho từng lớp chỉ để tăng số tầng. Dùng một `abstract DAO` kế thừa chung theo bài giảng. Dùng hàm mở kết nối chung nhưng **không dùng một static Connection đang mở cho mọi request**. Mỗi thao tác thường có connection riêng; một lần lưu hóa đơn dùng đúng một connection trong toàn transaction.

## 3. Cây thư mục cần tạo

Tạo project trong `app/`, không di chuyển bốn tài liệu gốc. Tên package ngắn `model`, `dao`, `controller`, `filter`, `util`, `dto`, `exception` để dễ đối chiếu bài giảng. Mọi tên lớp nghiệp vụ và hỗ trợ của project dùng hậu tố `4`, ngoại trừ lớp cơ sở `DAO` theo mẫu.

```text
Garaman/
├── [4 tài liệu gốc, giữ nguyên]
├── ke-hoach/                         # Bộ chỉ dẫn hiện tại
└── app/
    ├── pom.xml
    ├── README.md
    ├── .gitignore
    ├── database/
    │   ├── 01_schema.sql
    │   ├── 02_seed.sql
    │   └── 03_verify.sql
    ├── docs/
    │   ├── BAO_CAO_DA_SUA.docx
    │   ├── THAY_DOI_SO_VOI_BAO_CAO.md
    │   ├── TU_DIEN_DU_LIEU.md
    │   ├── MA_TRAN_TRUY_VET.md
    │   ├── KIEM_THU.md
    │   ├── 4.3.1_THIET_KE_TINH.md
    │   ├── DOI_CHIEU_PHAN_SAU.md
    │   ├── MODEL_FIELD_MAP.md
    │   ├── diagrams/
    │   │   ├── catalog-ui-flow.puml
    │   │   ├── catalog-design.puml
    │   │   ├── purchase-ui-flow.puml
    │   │   ├── purchase-design.puml
    │   │   ├── entity-design.puml
    │   │   └── database.puml
    │   └── screenshots/
    │       ├── 01-login.png
    │       ├── 02-customer-home.png
    │       ├── 03-search-empty.png
    │       ├── 04-search-result.png
    │       ├── 05-service-detail.png
    │       ├── 06-part-detail.png
    │       ├── 07-warehouse-home.png
    │       ├── 08-select-supplier.png
    │       ├── 09-new-supplier.png
    │       ├── 10-purchase-draft.png
    │       ├── 11-new-part.png
    │       ├── 12-confirmed-unpaid.png
    │       ├── 13-print-invoice.png
    │       └── 14-confirmed-paid.png
    └── src/
        ├── main/
        │   ├── java/
        │   │   ├── model/
        │   │   │   ├── Member4.java
        │   │   │   ├── Employee4.java
        │   │   │   ├── Manager4.java
        │   │   │   ├── WarehouseStaff4.java
        │   │   │   ├── SalesStaff4.java
        │   │   │   ├── Customer4.java
        │   │   │   ├── Technician4.java
        │   │   │   ├── Supplier4.java
        │   │   │   ├── Car4.java
        │   │   │   ├── Appointment4.java
        │   │   │   ├── RepairIntake4.java
        │   │   │   ├── CustomerInvoice4.java
        │   │   │   ├── Service4.java
        │   │   │   ├── SparePart4.java
        │   │   │   ├── SparePartCategory4.java
        │   │   │   ├── RepairService4.java
        │   │   │   ├── RepairPart4.java
        │   │   │   ├── PurchaseInvoice4.java
        │   │   │   ├── PurchaseInvoiceDetail4.java
        │   │   │   ├── ServiceStatistics4.java
        │   │   │   ├── SparePartStatistics4.java
        │   │   │   ├── CustomerStatistics4.java
        │   │   │   └── SupplierStatistics4.java
        │   │   ├── dao/
        │   │   │   ├── DAO.java
        │   │   │   ├── MemberDAO4.java
        │   │   │   ├── WarehouseStaffDAO4.java
        │   │   │   ├── ServiceDAO4.java
        │   │   │   ├── SparePartDAO4.java
        │   │   │   ├── SparePartCategoryDAO4.java
        │   │   │   ├── SupplierDAO4.java
        │   │   │   └── PurchaseInvoiceDAO4.java
        │   │   ├── controller/
        │   │   │   ├── AuthServlet4.java
        │   │   │   ├── HomeServlet4.java
        │   │   │   ├── CatalogServlet4.java
        │   │   │   ├── SupplierServlet4.java
        │   │   │   ├── SparePartServlet4.java
        │   │   │   └── PurchaseInvoiceServlet4.java
        │   │   ├── filter/
        │   │   │   ├── EncodingFilter4.java
        │   │   │   └── AuthFilter4.java
        │   │   ├── dto/SessionUser4.java
        │   │   ├── exception/ValidationException4.java
        │   │   └── util/
        │   │       ├── AppConstants4.java
        │   │       ├── Validation4.java
        │   │       ├── PasswordUtil4.java
        │   │       ├── WebUtil4.java
        │   │       └── DraftStore4.java
        │   ├── resources/db.properties.example
        │   └── webapp/
        │       ├── assets/
        │       │   ├── css/app.css
        │       │   ├── css/print.css
        │       │   ├── js/purchase.js
        │       │   ├── js/print.js
        │       │   └── images/
        │       │       ├── placeholder.svg
        │       │       ├── dich-vu-loc-dau.svg
        │       │       └── phu-tung-loc-dau.svg
        │       └── WEB-INF/
        │           ├── web.xml
        │           └── view/
        │               ├── common/
        │               │   ├── header.jspf
        │               │   ├── footer.jspf
        │               │   ├── messages.jspf
        │               │   └── error.jsp
        │               ├── member/gdDangNhap.jsp
        │               ├── customer/
        │               │   ├── gdChinhKH.jsp
        │               │   ├── gdTimThongTin.jsp
        │               │   └── gdChiTietThongTin.jsp
        │               └── warehouse/
        │                   ├── gdChinhKho.jsp
        │                   ├── gdChonNhaCungCap.jsp
        │                   ├── gdThemNhaCungCap.jsp
        │                   ├── gdLapHoaDonNhap.jsp
        │                   ├── gdThemPhuTung.jsp
        │                   ├── gdHoaDonNhapDaXacNhan.jsp
        │                   └── gdInHoaDonNhap.jsp
        └── test/java/
            ├── model/PurchaseInvoice4Test.java
            ├── util/Validation4Test.java
            └── dao/PurchaseInvoiceDAO4IT.java
```

Ảnh SVG chỉ là minh họa cục bộ đơn giản, phải ghi “Ảnh minh họa” và không giả là ảnh sản phẩm của nhà sản xuất. Không cần sinh ảnh AI, upload ảnh, thư viện gallery hay truy cập CDN. Trường `hinhAnh` trong form chỉ chọn đường dẫn asset cục bộ hợp lệ.

## 4. Nội dung và hàm của từng nhóm file

Các chữ ký dưới đây là hợp đồng. Có thể thêm hàm private nhỏ để tránh lặp, nhưng không đổi public method/endpoint/dữ liệu mà không cập nhật tài liệu và truy vết. `List<T>` mặc định dùng `ArrayList<T>` khi cần implementation. Không tạo phương thức CRUD chưa được dùng.

### 4.1. Model — giữ mô hình báo cáo

Mọi thuộc tính `private`, có constructor không tham số, getter/setter kiểu JavaBean; list khởi tạo rỗng. Với mỗi thuộc tính `x:T` trong hợp đồng có `T getX()` và `void setX(T value)`; không bắt Gemini tự đoán thuộc tính. Hai lớp trừu tượng là `Member4`, `Employee4`; lớp con rỗng chỉ có constructor cần thiết, không lặp id/thuộc tính cha.

| File/lớp | Thuộc tính và phương thức cần có |
|---|---|
| `Member4` | id, hoTen, tenDangNhap, matKhau, ngaySinh, diaChi, email, dienThoai, vaiTro, trangThaiTaiKhoan theo hợp đồng |
| `Employee4 extends Member4` | viTri, trangThaiLamViec |
| `Manager4`, `WarehouseStaff4`, `SalesStaff4` | Kế thừa Employee4; không có id mới hay SQL |
| `Customer4 extends Member4` | Không có thuộc tính riêng hiện tại |
| `Technician4` | Hồ sơ kỹ thuật viên độc lập như mô hình, không kế thừa tài khoản |
| `Supplier4`, `Car4`, `Service4`, `SparePartCategory4` | Thuộc tính đúng từ điển dữ liệu |
| `SparePart4` | Các cột danh mục + `nhom:SparePartCategory4`, `tonKho:BigDecimal` lưu trực tiếp; thao tác SQL tăng tồn chuyển sang `SparePartDAO4.tangTonSauNhap(...)` theo bước 3 của bài giảng |
| `Appointment4` | Các trường cơ bản, hoTenLienHe:String, dienThoaiLienHe:String + `khachHang:Customer4`, `oTo:Car4`; không có email/địa chỉ liên hệ riêng |
| `RepairIntake4` | Các trường cơ bản + khachHang, oTo, nhanVienTiepNhan, lichHen nullable, dsDichVu, dsPhuTung |
| `RepairService4` | Giá/bảo hành/tên áp dụng, ghiChu + dichVu:Service4, nhanVienKyThuat:Technician4 |
| `RepairPart4` | Lượng yêu cầu/thực tế, giá/bảo hành/tên/đơn vị áp dụng, ghiChu + phuTung:SparePart4 |
| `CustomerInvoice4` | Mốc thời gian, tinhTrang, hoTenKhachHangApDung:String, bienSoApDung:String, nhanVienTenApDung:String + phieuTiepNhan:RepairIntake4, nhanVienLap:SalesStaff4, tongTien dẫn xuất; `BigDecimal tinhTongTien()` cộng dịch vụ và phụ tùng thực tế |
| `PurchaseInvoiceDetail4` | id, phuTung, soLuong, donGiaNhap, tenPhuTungApDung:String, donViApDung:String, thanhTien dẫn xuất; `BigDecimal tinhThanhTien()` |
| `PurchaseInvoice4` | id, maYeuCau, ngayNhap, thoiDiemXacNhan, tinhTrang, thoiDiemThanhToan, nhaCungCap, nhanVienKho, dsChiTietNhap, nhaCungCapTenApDung:String, nhanVienTenApDung:String, tongTien dẫn xuất; `BigDecimal tinhTongTien()` |
| `ServiceStatistics4 extends Service4` | soLuotSuDung:int, doanhThu:BigDecimal |
| `SparePartStatistics4 extends SparePart4` | soLuongSuDung:BigDecimal, doanhThu:BigDecimal |
| `CustomerStatistics4 extends Customer4` | soLanSuaXe:int, doanhThu:BigDecimal |
| `SupplierStatistics4 extends Supplier4` | luongNhap:BigDecimal, donVi:String; mỗi object ứng với một nhà cung cấp + một đơn vị, không cộng lẫn đơn vị |

Các lớp thuộc tiếp nhận/lịch hẹn/thống kê được tạo POJO để khớp thiết kế toàn hệ thống; chưa cần DAO/Servlet/JSP cho chúng. Không tạo bảng cho bốn lớp thống kê. Trong quan hệ cha chứa list con, con không cần giữ object cha ngược lại; tránh object graph vòng khi copy bản nháp.

Giá trị getter thành tiền/tổng tiền nên tính lại từ dữ liệu gốc hoặc gọi hàm tính; không cho request bind giá trị tiền tổng. Không dùng `new BigDecimal(double)`.

### 4.2. DAO

Quy ước chung: truy vấn danh sách không có kết quả trả list rỗng; truy vấn theo id không tồn tại trả `null`; lỗi SQL ném `SQLException`, không trả list rỗng/false giả thành không có dữ liệu. Mutating methods có thể ném `ValidationException4`. ResultSet/statement/connection dùng try-with-resources theo quyền sở hữu kết nối.

| File | Chữ ký bắt buộc và trách nhiệm |
|---|---|
| `DAO.java` | `protected Connection getConnection() throws SQLException`; `protected String taoMauTimKiem(String tuKhoa)` escape ký tự LIKE; đọc biến `GARAMAN_DB_URL`, `GARAMAN_DB_USER`, `GARAMAN_DB_PASSWORD`, hoặc `db.properties` local; chỉ tạo factory cấu hình chung, không giữ static connection |
| `MemberDAO4.java` | `public Member4 timTheoTenDangNhap(String tenDangNhap) throws SQLException`; `public Member4 layThanhVienDangHoatDong(int id) throws SQLException`; `private Member4 mapMember(ResultSet rs) throws SQLException`. JOIN Employee khi cần và tạo đúng subclass theo vai trò. Chưa có DAO đăng kí tài khoản |
| `WarehouseStaffDAO4.java` | `public WarehouseStaff4 layThongTinNhanVienKho(int id) throws SQLException`; chỉ trả nhân viên đang làm có vị trí kho và tài khoản hợp lệ |
| `ServiceDAO4.java` | `public List<Service4> timDichVuTheoTen(String tuKhoa) throws SQLException`; `public Service4 layChiTietDichVu(int id) throws SQLException`; cả hai chỉ trả dịch vụ đang cung cấp; `private Service4 mapService(ResultSet rs)` |
| `SparePartCategoryDAO4.java` | `public List<SparePartCategory4> layDanhSachNhom() throws SQLException`; chỉ nhóm đang dùng; `public SparePartCategory4 layNhomDangSuDung(int id) throws SQLException`; `private SparePartCategory4 mapCategory(ResultSet rs)` |
| `SupplierDAO4.java` | `public List<Supplier4> timNhaCungCapTheoTen(String tuKhoa) throws SQLException`; `public Supplier4 layThongTinNhaCungCap(int id) throws SQLException`; `public int themNhaCungCap(Supplier4 supplier) throws SQLException, ValidationException4`; tìm/chọn chỉ nhà cung cấp đang hoạt động; `private Supplier4 mapSupplier(ResultSet rs)` |
| `SparePartDAO4.java` | `public List<SparePart4> timPhuTungTheoTen(String tuKhoa, boolean chiCongBo) throws SQLException`; `public SparePart4 layChiTietPhuTung(int id, boolean chiCongBo) throws SQLException`; `public int themPhuTung(SparePart4 part) throws SQLException, ValidationException4`; `public BigDecimal layTonKho(int id) throws SQLException`; `void tangTonSauNhap(Connection con, int idPhuTung, BigDecimal soLuong) throws SQLException, ValidationException4`; `private SparePart4 mapSparePart(ResultSet rs) throws SQLException`. Hàm tăng tồn có phạm vi package, nhận connection của transaction; không tự mở/đóng/commit connection. `layTonKho` đọc cột đã lưu, không SUM lịch sử. `chiCongBo=true` cho khách; `false` cho kho, vẫn loại ngừng kinh doanh. Với hóa đơn cũ phải đọc snapshot, không dùng bộ lọc này để làm biến mất dòng lịch sử |
| `PurchaseInvoiceDAO4.java` | Các chữ ký và transaction ở phần dưới |

`PurchaseInvoiceDAO4`:

```java
public int luuHoaDonNhap(PurchaseInvoice4 hoaDon)
    throws SQLException, ValidationException4;
public PurchaseInvoice4 layThongTinHoaDonNhap(int id)
    throws SQLException;
public Integer timIdTheoMaYeuCau(String maYeuCau, int idNhanVienKho)
    throws SQLException;
public List<PurchaseInvoice4> layHoaDonChuaThanhToan(int idNhanVienKho)
    throws SQLException;
public boolean ghiNhanThanhToan(int idHoaDon, BigDecimal soTienDaTra,
                                  int idNhanVienHienTai)
    throws SQLException, ValidationException4;

private int chenHoaDon(Connection con, PurchaseInvoice4 hoaDon)
    throws SQLException;
private void chenChiTiet(Connection con, int idHoaDon,
                        List<PurchaseInvoiceDetail4> chiTiet)
    throws SQLException;
private List<PurchaseInvoiceDetail4> layChiTiet(Connection con, int idHoaDon)
    throws SQLException;
private PurchaseInvoice4 mapInvoice(ResultSet rs) throws SQLException;
```

`ghiNhanThanhToan()` trả true nếu request này chuyển trạng thái, false nếu hóa đơn đã thanh toán bởi chính người lập. Trường hợp không tồn tại hoặc số tiền không bằng tổng phải báo lỗi rõ. Quyền kho được Filter kiểm tra; khóa request chống lặp của lần nhập phải kiểm tra nhân viên sở hữu. Nhân viên kho có thể mở/in hóa đơn đã xác nhận theo id; chỉ chính người lập được ghi nhận trả tiền. Danh sách chưa trả lọc theo id người đang đăng nhập. DAO lấy/kiểm tra id người thực hiện từ tham số do Servlet lấy từ session, phải bằng người lập, không lưu thêm FK. Không dùng WarehouseStaffDAO4 chỉ lọc nhân viên còn hoạt động để làm mất người lập cũ trên chứng từ lịch sử.

Không cần `PurchaseInvoiceDetailDAO4` riêng: chi tiết thuộc aggregate hóa đơn, chỉ được ghi trong transaction của hóa đơn. Không tự dùng DAO con mở connection mới trong transaction.

#### Trình tự lưu hóa đơn

1. Kiểm tra mã yêu cầu hợp lệ; nếu đã có hóa đơn của nhân viên này với mã đó thì trả id cũ ngay.
2. Mở connection, bắt đầu transaction. Kiểm tra lại nhân viên kho, nhà cung cấp và phụ tùng; nhóm của part phải tồn tại nhưng part cũ vẫn được nhập khi nhóm ngừng sử dụng. Chỉ yêu cầu nhóm đang sử dụng khi tạo part mới; không tin object đã giữ lâu trong session.
3. Kiểm tra có ít nhất một dòng, không trùng phụ tùng, lượng/giá dương, scale/đơn vị đúng. Chốt tên NCC, tên nhân viên, tên và đơn vị phụ tùng từ DB vào đúng các trường áp dụng trong file 03; giá nhập lấy từ input đã validate. Không lưu thêm hãng, địa chỉ hay điện thoại vào hóa đơn.
4. Khóa bản ghi theo thứ tự Supplier → Employee → SparePart (part id tăng dần), đọc lại trạng thái/đơn vị/tồn rồi chốt thông tin. Các bước kiểm tra/chụp dữ liệu ở bước 2–3 phải thực hiện hoặc kiểm tra lại sau khi khóa; không chụp master từ dữ liệu cũ trước khóa. Mọi thao tác sửa/xuất tương lai dùng cùng thứ tự khóa.
5. Gán thời điểm xác nhận ở server, trạng thái `Chưa thanh toán`; INSERT header với `maYeuCau UNIQUE`, lấy generated key; INSERT mọi dòng bằng cùng connection.
6. Sau khi INSERT chi tiết, gọi `SparePartDAO4.tangTonSauNhap(con,id,soLuong)` cho từng dòng, dùng chính Connection đang mở. UPDATE cộng dồn tồn theo file 03. Commit khi tất cả thành công; lỗi chi tiết hoặc cập nhật tồn rollback cả header, mọi dòng và mọi thay đổi tồn. Không đặt commit bên trong DAO con.
7. Nếu va chạm UNIQUE của `maYeuCau` do request đồng thời: rollback connection hiện tại, truy vấn lại hóa đơn đã commit bằng connection mới, xác minh chủ thể rồi trả cùng id. Chỉ xử lí riêng đúng lỗi unique mã yêu cầu; lỗi khác vẫn báo lỗi.
8. Trả id để Servlet redirect GET hóa đơn đã lưu. Nếu response bị mất, retry cùng mã vẫn tìm được hóa đơn.

`SELECT ... FOR UPDATE` cần nằm trong transaction và giữ khóa tới commit/rollback; xem [MySQL — Locking Reads](https://dev.mysql.com/doc/refman/8.4/en/innodb-locking-reads.html). Khi mở rộng module xuất/giữ hàng, phải dùng cùng thứ tự khóa bản ghi phụ tùng và kiểm tra lượng còn cấp trong transaction; chưa triển khai phần đó trong đợt này.

#### Trình tự ghi nhận thanh toán

Mở transaction → khóa header → kiểm tra người hiện tại là nhân viên kho đang hoạt động và chính người lập → đọc trạng thái/tổng tiền từ dòng lịch sử → nếu đã thanh toán trả false, giữ nguyên thời điểm → nếu chưa trả, số tiền phải đúng tổng → UPDATE trạng thái/thời điểm đúng một lần → commit. Không đổi người lập, không thêm FK thu/chi, không cập nhật tồn, không tạo bảng Payment hoặc khoản trả từng phần. Người khác cố gọi pay bị từ chối quyền. `ghiNhanThanhToan` báo lỗi quyền bằng `ValidationException4` với field `quyen`, Servlet chuyển thành HTTP 403.

### 4.3. Servlet

Mọi Servlet dùng `init()` tạo DAO stateless, `doGet()` phân phối các action đọc, `doPost()` phân phối action thay đổi; URL đăng ký trong `web.xml`, không đồng thời đăng ký trùng bằng annotation. Helpers nhận `(HttpServletRequest request, HttpServletResponse response)`; kiểu trả về `void`, khai báo `ServletException, IOException`, bổ sung `SQLException, ValidationException4` nếu gọi DAO và để entry point xử lí.

| File | Hàm bắt buộc ngoài init/doGet/doPost |
|---|---|
| `AuthServlet4` | `hienThiDangNhap(...)`, `dangNhap(...)`, `dangXuat(...)`; xác thực hash, tạo session mới sau login, tạo SessionUser4 và token; login sai trả thông báo chung, không lộ SQL/hash |
| `HomeServlet4` | `hienThiTrangChu(...)`; chọn đúng JSP theo vai trò; đối tượng Manager/SalesStaff chưa có module chạy thì trả thông báo phạm vi hỗ trợ rõ, không cho truy cập kho |
| `CatalogServlet4` | `hienThiTimKiem(...)`, `timKiem(...)`, `xemChiTiet(...)`; list dịch vụ và phụ tùng lấy độc lập; phân biệt bằng `loai=service|part`, không dùng id để đoán loại |
| `SupplierServlet4` | `hienThiChonNhaCungCap(...)`, `timNhaCungCap(...)`, `hienThiThemNhaCungCap(...)`, `themNhaCungCap(...)`; sau lưu redirect kết quả có bản ghi mới, nhân viên chủ động chọn như kịch bản |
| `SparePartServlet4` | `hienThiThemPhuTung(...)`, `themPhuTung(...)`; load nhóm đang dùng, kiểm tra maYeuCau bản nháp còn hợp lệ; khi lưu xong quay về hóa đơn và hiện phụ tùng mới để nhân viên chọn, chưa tự cộng dòng/chưa tăng tồn |
| `PurchaseInvoiceServlet4` | `batDauNhap(...)`, `hienThiBanNhap(...)`, `timPhuTung(...)`, `themDongNhap(...)`, `capNhatDongNhap(...)`, `xoaDongNhap(...)`, `huyBanNhap(...)`, `xacNhanHoaDon(...)`, `xemHoaDon(...)`, `inHoaDon(...)`, `ghiNhanThanhToan(...)`, `hienThiHoaDonChuaThanhToan(...)` |

Quy tắc quan trọng của `PurchaseInvoiceServlet4`:

- `batDauNhap` nhận id nhà cung cấp từ POST, lấy user từ session, sinh UUID và tạo draft. Ngày nhập hiển thị dự kiến; mốc chính thức được gán lúc xác nhận, không tin hidden input nhân viên/ngày/tổng tiền.
- `timPhuTung` nhận cả toàn bộ ô dòng đang sửa để giữ dữ liệu trước khi tìm; xử lí POST cập nhật nháp rồi redirect GET cùng từ khóa.
- `themDongNhap` cũng nhận toàn bộ ô dòng để giữ các sửa chưa bấm cập nhật; nếu part đã có thì chỉ focus, không tự cộng số lượng.
- `capNhatDongNhap` giữ input lỗi để người dùng sửa và chặn xác nhận; không âm thầm chuyển lỗi parse thành 0. Việc thêm part tạo dòng 0 là trường hợp rõ ràng, khác với parse lỗi.
- Trước khi sang form thêm phụ tùng, submit lưu các dòng đang sửa vào nháp bằng action `update` kèm `dichDen=new-part`; không dùng link GET làm mất input chưa gửi.
- `xacNhanHoaDon` tra mã đã xác nhận trước khi yêu cầu draft còn trong session. Nhận lại dữ liệu dòng từ form, tính/validate phía server, gọi DAO. Sau commit xóa draft, giữ khả năng retry nhờ mã đã lưu trong DB.
- `xoaDongNhap` chỉ sửa nháp; `huyBanNhap` chỉ xóa đúng draft theo mã, không xóa hồ sơ phụ tùng/nhà cung cấp vừa tạo.
- `hienThiHoaDonChuaThanhToan` chỉ lấy hóa đơn chưa thanh toán do người đang đăng nhập lập, phục vụ mở lại sau khi dừng ở bước xác nhận/in. Chưa có chức năng bàn giao người thanh toán; ngoại lệ 18b phải khớp file 01.

### 4.4. Filter, DTO, exception và tiện ích

| File | Hợp đồng |
|---|---|
| `EncodingFilter4` | `init(FilterConfig)`, `doFilter(ServletRequest, ServletResponse, FilterChain)`, `destroy()`; đặt UTF-8 trước khi đọc param và response content charset |
| `AuthFilter4` | Các hàm Filter chuẩn; đọc lại thành viên còn hoạt động ở mỗi request bảo vệ; kiểm tra phiên cả `/home` và prefix `/customer/*`, `/warehouse/*`; tất cả POST kiểm tra CSRF kể cả login, logout; cho GET `/login` và `/assets/*` công khai. Giữ thứ tự filter sau Encoding |
| `SessionUser4` | `id:int`, `hoTen:String`, `vaiTro:String`, `viTri:String`; constructor/getter, `static SessionUser4 fromMember(Member4 member)`; không chứa matKhau/hash |
| `ValidationException4` | Kế thừa `Exception`, constructors `(String message)` và `(String field, String message)`, getter `getField()` |
| `AppConstants4` | Hằng vai trò/trạng thái/đơn vị/session keys; `APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh")`; constructor private, không có SQL |
| `Validation4` | `String batBuoc(String value, String field, int maxLength)`; `String tuyChon(String value, int maxLength)`; `int idDuong(String value, String field)`; `BigDecimal soThapPhan(String value, String field, int precision, int scale, boolean choPhepKhong)`; `void kiemTraSoLuong(BigDecimal qty, String donVi)`; `void kiemTraEmail(String email)`; `void kiemTraDienThoai(String phone)`; `void kiemTraNhaCungCap(Supplier4 value)`; `void kiemTraPhuTung(SparePart4 value)`; `void kiemTraHoaDonNhap(PurchaseInvoice4 value)`; validate lỗi ném ValidationException4 |
| `PasswordUtil4` | `String bamMatKhau(char[] matKhau)`; `boolean kiemTraMatKhau(char[] matKhau, String hashDaLuu)`; `main(String[] args)` tạo hash cho seed local. Chọn PBKDF2WithHmacSHA256 với salt ngẫu nhiên, định dạng tự chứa thuật toán/iteration/salt/hash, so sánh constant-time; không tự hash một vòng SHA rồi coi như đủ |
| `WebUtil4` | `void forward(req,res,String view)`; `void redirect(req,res,String path)`; `SessionUser4 requireUser(req)`; `String taoCsrfToken(HttpSession session)`; `boolean hopLeCsrf(req)`; `void flash(req,String message)`; `String layFlash(req)`; URL nội bộ gắn contextPath và encode query đúng, không redirect sang giá trị URL tùy ý từ input |
| `DraftStore4` | `PurchaseInvoice4 taoBanNhap(HttpSession session, Supplier4 supplier, WarehouseStaff4 staff)`; `PurchaseInvoice4 layBanNhap(HttpSession session, String maYeuCau)`; `void luuBanNhap(HttpSession session, PurchaseInvoice4 draft)`; `void xoaBanNhap(HttpSession session, String maYeuCau)`; `PurchaseInvoice4 saoChep(PurchaseInvoice4 draft)`; `void kiemTraGioiHan(HttpSession session)`. Map theo UUID, mỗi session tối đa 10 draft, đầy thì báo người dùng hủy draft cũ, không tự xóa draft đang làm |

Thao tác đọc–sửa–ghi draft phải nằm trong cùng `synchronized(session)` ngắn ở Servlet, vì chỉ đồng bộ từng hàm get/put vẫn có thể mất cập nhật. Sao chép draft trước khi đưa ra ngoài vùng khóa; không giữ khóa session trong lúc chờ in hoặc gọi DB lâu. Hai tab với hai UUID không được trộn hóa đơn. Không lưu draft vào field static hay field của Servlet.

`DraftStore4` quản lí thêm input thô khi dòng nhập có lỗi: `Map<String,String> layGiaTriForm(HttpSession session, String maYeuCau)` và `void luuGiaTriForm(HttpSession session, String maYeuCau, Map<String,String> values)`. Dùng `draftFormValues4` theo hợp đồng; xóa draft phải xóa cả input thô tương ứng. Các public method tiện ích có thể ném `ValidationException4` nếu đầu vào/session không hợp lệ; `requireUser` xử lí thiếu phiên bằng lỗi quyền rõ ràng, không trả object giả.

### 4.5. File cấu hình, SQL và tài liệu

| File | Nội dung bắt buộc |
|---|---|
| `pom.xml` | WAR, Java17, dependencies đã chốt, UTF-8; JUnit; profile `integration-test` chạy `*IT` bằng Failsafe, mặc định unit test không cần DB |
| `.gitignore` | target/, log, cấu hình DB local, file IDE/cache; không ignore seed/schema/tài liệu |
| `db.properties.example` | Mẫu URL/user/password rỗng hoặc placeholder; copy thành `db.properties` local; biến môi trường ưu tiên; không hardcode root/password thật |
| `web.xml` | Mapping Filter theo thứ tự, mapping Servlet trong hợp đồng, session timeout 30 phút, welcome vào `/home`, error page; cookie HttpOnly; cấu hình Secure khi chạy HTTPS |
| `01_schema.sql` | Tạo database/tables/index/check theo hợp đồng, có cột tồn lưu trực tiếp; không DROP database mặc định, không tự nạp dữ liệu |
| `02_seed.sql` | Tài khoản demo có hash thật, nhóm, dịch vụ/phụ tùng/nhà cung cấp mẫu; tồn ban đầu qua hóa đơn nhập seed; có chú thích dữ liệu demo |
| `03_verify.sql` | SELECT đối chiếu tồn lưu với nhập trừ xuất, tổng, trùng maYeuCau, trùng dòng, hóa đơn không có chi tiết, trạng thái/thời điểm thanh toán; script chỉ đọc |
| `BAO_CAO_DA_SUA.docx` | Bản báo cáo sao chép đã sửa lời văn, thuộc tính, quan hệ, hình nhúng và đánh số theo file 01; không thay thế bằng một phụ lục liệt kê thay đổi |
| `THAY_DOI_SO_VOI_BAO_CAO.md` | Thay đổi cuối theo file 01/07, trước/sau, bảng/lớp/hình/phần bị tác động, lý do; không đưa thay đổi ngoài baseline vào âm thầm |
| `TU_DIEN_DU_LIEU.md` | Schema thực tế, kiểu, NULL/default/PK/FK/UNIQUE/CHECK; đánh dấu dữ liệu lịch sử và dẫn xuất |
| `MA_TRAN_TRUY_VET.md` | Quy tắc/kịch bản → UI → endpoint → Servlet method → DAO method → bảng/cột → test → hình trong 4.3.1; mẫu và mã theo file 07 |
| `MODEL_FIELD_MAP.md` | Mỗi dòng là một thuộc tính: lớp/field/kiểu trong DOCX → kiểu Java → cột SQL hoặc thuộc tính dẫn xuất/đối tượng; không tự thêm field |
| `DOI_CHIEU_PHAN_SAU.md` | Bàn giao tên/hàm và dữ liệu cho 4.3.2, 4.3.3, Chương 5–6 theo file 07; không ghi những mục này đã hoàn thành |
| `KIEM_THU.md` | Các ca ở phần 8, môi trường, lệnh, kết quả, ảnh/log; chưa chạy thì ghi chưa chạy |
| `README.md` | Yêu cầu môi trường, import schema/seed, cấu hình DB, build WAR, deploy Tomcat9, URL, tài khoản demo, chạy test và giới hạn module |

## 5. JSP và bố cục màn hình

Không cần bắt chước màu sắc mẫu cũ; cần giống cách trình bày **mỗi bước có giao diện, trường nhập/xuất và thao tác rõ**. Giao diện gọn để đưa vào báo cáo: nền sáng, tiêu đề, khu vực người đăng nhập, form phía trên, bảng phía dưới, nút chính/phụ có nhãn tiếng Việt. Bản in riêng khổ A4, chữ đen/nền trắng.

| JSP | Trường và thao tác bắt buộc |
|---|---|
| `gdDangNhap.jsp` | Username, password, token, nút đăng nhập, thông báo lỗi; không xây đăng kí tài khoản trong đợt này |
| `gdChinhKH.jsp` | Tên khách, nút Tìm thông tin dịch vụ/phụ tùng, form đăng xuất |
| `gdTimThongTin.jsp` | Ô tên, Tìm kiếm, Về trang chính; khi chưa tìm không có bảng; khi có kết quả hiển thị Mã/Tên/Loại/Đơn giá/Chọn; tìm xong rỗng thì thông báo không có kết quả |
| `gdChiTietThongTin.jsp` | Một JSP với hai nhánh theo loại: dịch vụ đầy đủ nội dung/giá công/thời gian/bảo hành; phụ tùng đầy đủ nhóm/đơn vị/hãng/thông số/dòng xe/giá bán/bảo hành; ảnh placeholder khi thiếu; Quay lại giữ từ khóa |
| `gdChinhKho.jsp` | Tên nhân viên, Nhập phụ tùng, Hóa đơn nhập chưa thanh toán của tôi, đăng xuất. Danh sách chưa thanh toán của người hiện tại render ngay dưới trang chủ khi mở chức năng đó |
| `gdChonNhaCungCap.jsp` | Tên nhà cung cấp, tìm, thêm mới, bảng mã/tên/địa chỉ/điện thoại/Chọn; chọn bằng POST bắt đầu draft |
| `gdThemNhaCungCap.jsp` | Tên/địa chỉ/điện thoại bắt buộc, email tùy chọn, Lưu và Quay lại; lỗi hiển thị đúng trường và giữ dữ liệu nhập |
| `gdLapHoaDonNhap.jsp` | Header nhà cung cấp/nhân viên/ngày dự kiến; ô tìm tên phụ tùng + nút tìm + select kết quả + nút chọn + thêm mới; bảng STT/Mã/Tên/Đơn vị/Hãng/SL/Đơn giá/Thành tiền/Xóa; tổng; Cập nhật/Xác nhận/Hủy; token và mã draft. Dòng mới 0/0; chọn trùng focus dòng cũ |
| `gdThemPhuTung.jsp` | Tên, nhóm có sẵn, đơn vị, hãng, mô tả, ảnh cục bộ, thông số, dòng xe, giá bán, hạn/điều kiện bảo hành, trạng thái; Lưu và Quay lại draft; hiển thị rõ giá nhập được nhập ở dòng hóa đơn |
| `gdHoaDonNhapDaXacNhan.jsp` | Mã/thời điểm/nhà cung cấp/nhân viên từ dữ liệu đã lưu, bảng dòng, tổng, trạng thái; In hóa đơn; ô Số tiền đã trả và Đã thanh toán khi chưa trả và người hiện tại là người lập; quay về trang chính; không sửa dòng đã xác nhận |
| `gdInHoaDonNhap.jsp` | Nội dung chứng từ lấy cùng dữ liệu lịch sử; In và Quay lại; không tự tạo giao dịch khi mở trang/in; không có sidebar/form nhập ở phần in |
| `header.jspf`, `footer.jspf`, `messages.jspf` | Khung chung, thông tin user, lỗi/flash an toàn HTML; footer có chú thích demo ngắn |
| `error.jsp` | Tiêu đề theo 400/403/404/500, thông điệp dễ hiểu, đường quay lại phù hợp; không in stack trace/SQL/password |

JSP render bằng JSTL/EL và `<c:out>` khi phù hợp; URL dùng `<c:url>` và `<c:param>`; không ghép từ khóa thô vào HTML/URL. Form POST chứa CSRF. Màn hình không lộ tên bảng, UUID kỹ thuật, Connection, SQL hoặc nội dung hướng dẫn lập trình cho người sử dụng.

### Các hàm JavaScript

- `purchase.js`: `tinhLaiTienDuKien()`, `focusDongDaCo(partId)`, `xacNhanHuy()`. Tính trước chỉ để phản hồi giao diện, không gửi tổng tiền như căn cứ chốt. Không tự thêm/xóa hàng đã lưu bằng localStorage.
- `print.js`: `inHoaDon()` gọi `window.print()`, `quayLaiHoaDon()` dùng URL GET id hóa đơn đã có. Có thể dùng `afterprint` hỗ trợ quay lại, nhưng sự kiện này không chứng minh in thành công; luôn có nút Quay lại, không đổi trạng thái thanh toán theo sự kiện in.
- Không dùng AJAX trong baseline. Việc tìm part bằng POST rồi redirect và select kết quả HTML đã đáp ứng mẫu kịch bản. Không tạo JSON endpoint không cần thiết.

## 6. Viết mục 4.3.1 theo đúng bài giảng

Giữ tên **4.3.1** trong file đầu ra theo yêu cầu người dùng. Bốn bước, hình và ma trận đối chiếu bắt buộc nằm ở file 07. Giữ cấu trúc phần trước của DOCX; lập bảng số cũ → mới khi cần đánh số Chương 3–4, không tìm-thay toàn văn tùy tiện. Không copy tên module đăng kí học/nhập điểm từ mẫu.

### Phần mở đầu

Nêu đầu vào là hai hình lớp phân tích module hiện có (đang là Hình 1.5/1.6) và mô hình thực thể thiết kế đã hiệu chỉnh; dùng số sau cùng theo bảng ánh xạ hình, không hardcode số chưa tồn tại; nền tảng web JSP/Servlet/JDBC/MySQL. Giải thích Servlet điều phối theo Lab1, DAO giữ vai trò truy xuất như bài giảng.

### a. Thiết kế tĩnh module tìm kiếm thông tin dịch vụ/phụ tùng

1. **Bước 1 — Thiết kế giao diện:** hình bố cục các màn hình nối bằng mũi tên tương tự Hình 4.3 của bài giảng. Gồm trang chính → trang tìm trống/kết quả → chi tiết dịch vụ hoặc phụ tùng → quay lại. Ảnh chụp thật là minh chứng bổ sung, không thay thế sơ đồ luồng màn hình.
2. **Bước 2 — Lớp giao diện:** ánh xạ CustomerHomeUI4 → `gdChinhKH.jsp`; CatalogSearchUI4 → `gdTimThongTin.jsp`; ItemDetailsUI4 → `gdChiTietThongTin.jsp`. Liệt kê input/output/submit, dữ liệu request và dữ liệu ẩn `id`, `loai`, `tuKhoa`; Servlet Home/Catalog tương ứng. Dữ liệu người dùng lấy từ session, không tin hidden role/userId.
3. **Bước 3 — DAO:** DAO, ServiceDAO4, SparePartDAO4; trình bày chữ ký/tham số/kết quả/phạm vi lọc trạng thái theo code.
4. **Bước 4 — Thực thể:** Member4/Customer4, Service4, SparePart4/SparePartCategory4; giữ quan hệ và kế thừa như mô hình. Vẽ `catalog-design.puml`: JSP/Servlet ở trên, DAO giữa, thực thể dưới; mũi tên JSP gửi request cho Servlet, Servlet phụ thuộc DAO và forward JSP, DAO tạo/map thực thể.

### b. Thiết kế tĩnh module nhập phụ tùng

1. **Bước 1:** trang chính kho → chọn nhà cung cấp; nhánh thêm nhà cung cấp quay về lựa chọn; → lập hóa đơn; nhánh thêm phụ tùng quay về draft; → xác nhận → bản in → hóa đơn → ghi nhận đã trả. Thể hiện vòng lặp tìm/chọn/sửa dòng và nhánh hủy trước xác nhận.
2. **Bước 2:** bảng ánh xạ 7 lớp biên trong pha phân tích với 7 JSP kho; thêm Servlet Supplier/SparePart/PurchaseInvoice. Liệt kê thuộc tính ẩn và session dùng để trao đổi, nhất là mã bản nháp/CSRF; số lượng/giá nhập là input, thành tiền/tổng/nhân viên/thời điểm là output.
3. **Bước 3:** DAO, SupplierDAO4, WarehouseStaffDAO4, SparePartCategoryDAO4, SparePartDAO4, PurchaseInvoiceDAO4. Ghi rõ `tinhTongTien()` nằm ở model; persistence ở DAO; chuyển `tangTonSauNhap()` sang `SparePartDAO4`, có Connection và tham số lượng/id, được DAO hóa đơn gọi trong cùng transaction. Thể hiện phụ thuộc PurchaseInvoiceDAO4 → SparePartDAO4; không sửa hình giao tiếp phân tích đã giữ.
4. **Bước 4:** Member4/Employee4/WarehouseStaff4, Supplier4, SparePartCategory4/SparePart4, PurchaseInvoice4/PurchaseInvoiceDetail4. Vẽ composition hóa đơn–dòng, association tới nhà cung cấp/nhân viên/danh mục, đúng thuộc tính object và kiểu Java.

Hai biểu đồ class phải có phạm vi `+/-/#`, kiểu thuộc tính, tham số/kiểu trả về DAO; hiển thị `<<JSP>>` và `<<Servlet>>` rõ ràng. JSP là thành phần giao diện được mô hình hóa theo mẫu, không giả nó là một JavaBean được tự viết thêm. Không bắt vẽ mọi getter/setter trên hình nếu quá rối, nhưng code phải có.

`entity-design.puml` và `database.puml` phục vụ cập nhật mô hình trước đó; export SVG/PNG để chèn báo cáo. Không thay communication diagram pha phân tích bằng sơ đồ pha thiết kế rồi giữ nguyên tiêu đề. Mục 4.3.2 về hoạt động và 4.3.3 về triển khai chưa phải nội dung hoàn thành trong đợt này; tên hàm đã chốt giúp thực hiện tiếp hai mục đó.

## 7. Trình tự triển khai cho Gemini

| Giai đoạn | Việc cần làm | Điều kiện được chuyển bước |
|---|---|---|
| 0. Khóa căn cứ | Đọc 4 tài liệu và bộ kế hoạch; áp dụng file 01, cập nhật bản báo cáo sao chép đúng các chỗ còn lệch; đối chiếu 14 trường đã có/được giữ, ma trận UI–DAO và phiên bản | Không còn lựa chọn song song về tồn, ngày giờ, tên bảng/cột hoặc công nghệ |
| 1. Bộ khung | Maven WAR, package, web.xml, Filter, error JSP, CSS; build | WAR build được, không trộn javax/jakarta |
| 2. CSDL và model | 16 bảng có cột tồn lưu, seed, toàn bộ POJO, công thức tính, verify SQL | Import trên DB trống được, dữ liệu/constraint/tính tiền đúng; không mất lịch sử |
| 3. Đăng nhập | MemberDAO, WarehouseStaffDAO, PasswordUtil, Auth/Home; token/session/role | Customer vào trang khách, kho vào trang kho, không vượt quyền bằng URL |
| 4. Tra cứu | Service/SparePart DAO, CatalogServlet, 3 JSP khách, dữ liệu mẫu | Tìm được ví dụ “lọc”, phân biệt 2 id trùng số, giữ kết quả khi quay lại |
| 5. Danh mục hỗ trợ nhập | Supplier/Category DAO, thêm NCC/part | Thêm xong quay về đúng bước, part mới có tồn 0 và không tự công bố khi thiếu thông tin |
| 6. Nhập nháp | DraftStore và toàn bộ thao tác dòng, giữ dữ liệu qua form phụ | Không trộn hai tab, dòng mới 0, chọn trùng không thêm dòng, hủy không đụng DB giao dịch |
| 7. Chốt/in/thanh toán | Transaction DAO, mã chống trùng, reopen unpaid, print/pay | Rollback đúng, confirm đồng thời 1 hóa đơn, thanh toán lặp không đổi mốc cũ, in không đổi tồn |
| 8. Hoàn thiện 4.3.1 | Chụp UI, xuất sơ đồ, ghi chữ ký thực, rà soát truy vết | Mọi tên/hàm/UI/bảng trong báo cáo đều khớp code; không dùng hình giả |
| 9. Bàn giao | Build/test lại sau thay đổi cuối, README, kết quả và giới hạn | Chạy lại từ schema/seed theo README được; lỗi môi trường ghi chính xác |

Không cần hỏi lại từng bước đã được giao. Nếu môi trường không có MySQL/Tomcat thì vẫn hoàn thiện mã, script, build và ghi phần chưa kiểm chứng; không ghi “đã chạy” dựa trên đọc code.

## 8. Nghiệm thu cần thực hiện

### Nhóm tra cứu

- C01: Chưa login → vào URL khách bị đưa về login; kho không được vào chức năng khách.
- C02: Mới mở trang không có bảng kết quả; gửi từ khóa rỗng/toàn khoảng trắng báo lỗi.
- C03: “lọc” có cả DV1 `Thay lọc dầu động cơ`, PT1 `Lọc dầu động cơ AF-01`, giá 120.000 và 150.000.
- C04: Từ khóa không có kết quả có thông báo; `%`, `_`, `!` được tìm như ký tự bình thường, không biến thành wildcard đặc biệt ngoài ý muốn.
- C05: Dịch vụ/phụ tùng có cùng id số vẫn mở đúng chi tiết nhờ tham số loại.
- C06: `Chưa công bố`/ngừng kinh doanh không xuất hiện cho khách và không mở được bằng id trực tiếp.
- C07: Quay lại giữ từ khóa; dữ liệu chứa ký tự HTML không được thực thi như script.

### Nhóm nhập

- P01: Chọn NCC có sẵn hoặc thêm NCC bắt buộc tên/địa chỉ/điện thoại, email tùy chọn.
- P02: Tạo part mới chọn nhóm đang dùng; chưa có dòng nhập nên tồn lưu là 0. Nhóm ngừng dùng/id giả không được chấp nhận.
- P03: Chọn PT1 tạo dòng 0/0; nhập 10 và 80.000 → 800.000; giá bán 150.000 không tự đổi thành 80.000.
- P04: Nhiều dòng đúng tổng; chọn lại PT1 chỉ focus; đổi từ khóa/thêm part/đi tới form thêm mới không mất các lượng/giá đang sửa.
- P05: Trống dòng, lượng âm/0, giá âm/0, lượng lẻ cho đơn vị Cái, quá precision/scale, nhập ký tự sai → không xác nhận.
- P06: Lỗi ghi một dòng bất kỳ → không còn header hay dòng dở dang; tồn không tăng.
- P07: Hai request đồng thời cùng `maYeuCau` → đúng 1 header/nhóm dòng; retry sau khi xóa draft hoặc mất response vẫn trả id cũ.
- P08: Hai mã khác nhau cùng nhập một part → lưu cả hai hóa đơn hợp lệ, lượng tồn tăng bằng tổng hai lần; không đánh đồng hai lần nhập chỉ vì cùng NCC/part.
- P09: Hủy trước xác nhận chỉ bỏ nháp; các NCC/part mới vẫn tồn tại; không có giao dịch nhập mới.
- P10: Hai tab hai draft không trộn NCC/dòng; token/mã giả hoặc của session khác không dùng để thao tác draft.
- P11: In và in lại không thay đổi số hóa đơn, dòng, trạng thái thanh toán hoặc tồn. Hủy hộp thoại in cũng không tự ghi nhận đã trả.
- P12: Người lập nhập đúng tổng → ghi trạng thái/thời điểm; hai request cùng người lập đồng thời chỉ một request thay đổi trạng thái, lần sau giữ thời điểm. Tồn không đổi.
- P13: A lập hóa đơn rồi đăng xuất; B thuộc kho có thể đọc/in theo quyền nhưng không thấy hóa đơn A trong danh sách chưa trả của mình và không gọi pay được (403). A đăng nhập lại thấy và thanh toán được; người lập không đổi.
- P14: Sửa tên phụ tùng/NCC/nhân viên sau khi xác nhận → bản in cũ vẫn giữ các tên và đơn vị áp dụng đã lưu. Hãng/địa chỉ/điện thoại không được đưa lên bản in. Không đổi đơn vị phụ tùng đã có giao dịch; dữ liệu kiểm thử sửa master chỉ dùng DB test.
- P15: Client giả tổng, ngày, id người lập/người thực hiện, trạng thái hoặc giá bán → server bỏ qua các trường không thuộc input được phép; thiếu/sai CSRF bị từ chối.

- P16: Người lập ngừng làm việc → không đăng nhập/thanh toán được; nhân viên kho đang hoạt động vẫn tra cứu/in chứng từ lịch sử, tên người lập không mất. Chưa có chức năng bàn giao để trả thay.

Các quy tắc giữ giá qua ngày, hủy phiếu trước khi làm, khớp lịch/khách/xe và không tạo hai phiếu mở cho cùng xe ở mục 9 file 03 là điều kiện cho module tiếp nhận/thanh toán khách tương lai; ghi ca kiểm tra trong tài liệu nhưng không báo đã chạy khi module chưa triển khai.

`PurchaseInvoice4Test`: tổng nhiều dòng, số thập phân và quy tắc làm tròn. `Validation4Test`: ranh giới lượng/giá, đơn vị, scale, trạng thái công bố. `PurchaseInvoiceDAO4IT`: rollback, xác nhận trùng đồng thời, thanh toán lặp và bảo toàn snapshot. Bổ sung kiểm thử tồn: (a) nhập 2,5 Lít (input 2.500) cộng chính xác 2,5 Lít; (b) giao dịch nhiều part, cập nhật part đầu thành công nhưng part sau vượt giới hạn tồn → rollback cả hai tồn cùng chứng từ; (c) hai mã khác nhau đồng thời cùng part không mất lượt cộng; (d) pay/in/retry không cộng lại. Integration test chỉ chạy trên database tên kết thúc `_test`, không xóa dữ liệu DB demo hoặc DB người dùng.

## 9. Điều kiện hoàn thành

- Có đủ file và chức năng được giao; không còn TODO trong luồng chính/ngoại lệ bắt buộc.
- CSDL, model, DAO, controller, JSP và sơ đồ dùng cùng thuật ngữ/tên/chữ ký.
- Build WAR được; test nào chạy có kết quả thật, test chưa chạy có lý do.
- Mục 4.3.1 chứa đủ thiết kế giao diện, ánh xạ biên→JSP/Servlet, thuộc tính ẩn, DAO với input/output và thực thể liên quan.
- Có bản báo cáo sao chép đã cập nhật đủ lời văn và hình theo file 01; đúng danh mục 14 trường giữ, cột tồn trực tiếp và không có hai FK thu/chi riêng; file gốc vẫn nguyên vẹn.
- Các kết quả chưa làm thuộc module khác được ghi trong phạm vi README, không quảng bá thành tính năng đã có.

## 10. Khóa cách ánh xạ model và thiết kế

`MODEL_FIELD_MAP.md` phải liệt kê đầy đủ mọi field trong 16 bảng và 23 lớp nghiệp vụ/thống kê của cây thư mục. Với cột SQL không phải FK: INT NN → int; INT N → Integer; VARCHAR/TEXT/CHAR → String; DECIMAL → BigDecimal; DATE → LocalDate; DATETIME → LocalDateTime. FK chuyển thành thuộc tính object được chỉ rõ trong file 03, không tự thêm cặp object/id trùng ý nghĩa. id lớp con kế thừa từ cha.

Các field còn có trong biểu đồ lớp: `tonKho` ánh xạ cột đã lưu; `thanhTien` của dòng nhập và `tongTien` của hai hóa đơn là getter tính lại; danh sách dòng dùng List thay mảng có ghi rõ ánh xạ, quan hệ không đổi. Không thêm field `nhanVienThanhToan`. Các lớp thống kê giữ các thuộc tính theo bảng model, chỉ POJO chưa có DAO thống kê.

Phạm vi truy cập của các phương thức util trong bảng là public static, trừ constructor private và những method được ghi riêng; DAO là instance, kế thừa DAO trừu tượng. Các method map ResultSet đều private và throws SQLException. Chữ ký Servlet helper dùng hai tham số request/response và void; kiểu đầy đủ xem mục 4.3. Mỗi field model có getter/setter theo JavaBean, trừ giá trị dẫn xuất chỉ cần getter và hàm tính. Không thêm public CRUD cho module ngoài phạm vi.

Trong SQL, FK phải ghi đích chính xác: Employee/Customer tới PK `tblMember4id`, các bảng khác tới `id`. Các quan hệ 0..1 đã chốt không được đổi NOT NULL/UNIQUE ngược nghĩa. UNIQUE theo cặp ở ba bảng dòng giữ id PK, thể hiện trong SQL dù hình Paradigm không tiện đánh dấu.

Không tạo DAO con cho chi tiết nhập chỉ để bám một lớp–một DAO: chi tiết được quản lí trong transaction của PurchaseInvoiceDAO4. Tầng thực thể không thực hiện SQL; cách chuyển phương thức từ thực thể phân tích sang DAO là đúng bước 3 bài giảng.
