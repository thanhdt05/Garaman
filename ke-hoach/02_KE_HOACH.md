# Kế hoạch triển khai hai module đề 26

**Phạm vi cao nhất: [00_PHAM_VI.md](00_PHAM_VI.md). Chỉ tạo 10 model và 9 bảng liên quan hai module; không tạo các lớp/bảng còn lại trong báo cáo.**

**Mẫu/luồng/UI bắt buộc theo [11](11_CHUAN_MAU_LUONG_GIAO_DIEN.md).** Hai list tra cứu render trong một bảng; chọn part bằng nút Chọn ở từng dòng bảng kết quả ngay trên hóa đơn (yêu cầu chủ bài 21/10/2026); giữ ngày nhập, tổng0, nhãn và bố cục nguồn. File12 ghi những lệch đã thấy ở app hiện có; không coi các tài liệu “hoàn thành100%” do AI khác tạo là bằng chứng nghiệm thu.

**Bổ sung sau kiểm tra nguồn:** đọc 08 về điểm còn lệch; nhập giao diện đã sửa trong `giao-dien-tham-chieu/` theo ánh xạ từng file ở 09; thực hiện hợp đồng helper/exception/filter/version nháp/409/format ngày ở 10. Các chữ ký dưới đây là yêu cầu triển khai, chưa có backend tại thời điểm rà soát. Không chép sessionStorage/web.xml/WAR Tomcat10 của prototype sang app.

**Phương án:** triển khai đúng luồng hai module đã làm; lưu `stockQuantity` trực tiếp; hóa đơn nhập có một người lập và chính người đó ghi nhận trả tiền. File 03 khóa schema/route/dữ liệu của hai module; 07 hướng dẫn 4.3.1 của hai module.

## 1. Đầu ra và giới hạn công việc

Đọc hai kịch bản và hình lớp phân tích liên quan trong DOCX. File 01 chỉ liệt kê các điểm cần thống nhất trong hai module. Không sửa lại toàn bộ báo cáo và không tạo bản DOCX chỉnh sửa như điều kiện trước khi code. Mọi hình/chữ ký hàm/nội dung 4.3.1 mới truy về code của hai module theo 07. Giữ nguyên tài liệu gốc.

Với hình CSDL vẽ bằng Paradigm không có kiểu datetime trong danh sách đang dùng, giữ nhãn date và chèn ghi chú ánh xạ theo file 01. Từ điển dữ liệu/SQL vẫn dùng DATETIME cho thời điểm, DATE cho ngày sinh. Kiểm tra DDL thực tế khi sinh từ công cụ; không bắt người dùng đổi công cụ hoặc đổi nhãn không được hỗ trợ.

Đầu ra cần có:

1. Project Java Web build thành WAR, có JSP, Servlet, JDBC, DAO và MySQL.
2. Đăng nhập/đăng xuất cho hai vai trò cần demo: Customer và WarehouseStaff.
3. Module tra cứu dịch vụ/phụ tùng: mở trang trống → tìm theo tên → xem chi tiết đúng loại → quay lại kết quả.
4. Module nhập: chọn/thêm nhà cung cấp → chọn/thêm phụ tùng → nhập nhiều dòng → xác nhận một lần → xem/in hóa đơn → ghi nhận trả đủ tiền.
5. Schema tập con **9 bảng** theo 00/03, seed và **10 model** phục vụ hai module cùng đăng nhập hỗ trợ; giữ stockQuantity, không lưu lineAmount/totalAmount.
6. Nội dung mục 4.3.1 theo bốn bước bài giảng, hai sơ đồ luồng màn hình, hai biểu đồ lớp thiết kế có chữ ký thật, ảnh giao diện thật và ma trận truy vết.
7. Hướng dẫn build/chạy/kiểm tra có kết quả thực tế.

Chỉ tạo các chức năng/lớp/bảng trong 00. Không tạo cả POJO, schema, khung chức năng hoặc test cho module ngoài phạm vi. Trang chủ hai vai trò chỉ chứa đường vào hai module tương ứng và đăng xuất. Mô hình tổng thể của báo cáo giữ làm nguồn tham chiếu, không phải đầu ra phải cài đặt.

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
├── giao-dien-tham-chieu/              # Prototype đã sửa, nhập theo file 09
└── app/
    ├── pom.xml
    ├── README.md
    ├── .gitignore
    ├── database/
    │   ├── 01_schema.sql
    │   ├── 02_seed.sql
    │   └── 03_verify.sql
    ├── docs/
    │   ├── TIEN_DO.md
    │   ├── THAY_DOI_SO_VOI_BAO_CAO.md
    │   ├── TU_DIEN_DU_LIEU.md
    │   ├── MA_TRAN_TRUY_VET.md
    │   ├── KIEM_THU.md
    │   ├── 4.3.1_THIET_KE_TINH.md
    │   ├── MODEL_FIELD_MAP.md
    │   ├── METHOD_MAP.md
    │   ├── diagrams/
    │   │   ├── catalog-ui-flow.puml
    │   │   ├── catalog-design.puml
    │   │   ├── purchase-ui-flow.puml
    │   │   ├── purchase-design.puml
    │   │   └── database.puml             # Chỉ 9 bảng của hai module
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
        │   │   │   ├── WarehouseStaff4.java
        │   │   │   ├── Customer4.java
        │   │   │   ├── Supplier4.java
        │   │   │   ├── Service4.java
        │   │   │   ├── SparePart4.java
        │   │   │   ├── SparePartCategory4.java
        │   │   │   ├── PurchaseInvoice4.java
        │   │   │   └── PurchaseInvoiceDetail4.java
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
        │       │   ├── css/styles.css
        │       │   ├── css/forms.css
        │       │   ├── css/print.css
        │       │   ├── js/purchase.js
        │       │   ├── js/print.js
        │       │   └── images/
        │       │       ├── placeholder.svg
        │       │       ├── part.svg
        │       │       ├── brake.svg
        │       │       └── garage.svg
        │       └── WEB-INF/
        │           ├── web.xml
        │           └── view/
        │               ├── common/
        │               │   ├── header.jspf
        │               │   ├── footer.jspf
        │               │   ├── messages.jspf
        │               │   ├── steps.jspf
        │               │   └── error.jsp
        │               ├── member/login.jsp
        │               ├── customer/
        │               │   ├── customerHome.jsp
        │               │   ├── catalogSearch.jsp
        │               │   └── itemDetails.jsp
        │               └── warehouse/
        │                   ├── parts-picker.jspf
        │                   ├── invoice-lines.jspf
        │                   ├── warehouseHome.jsp
        │                   ├── selectSupplier.jsp
        │                   ├── newSupplier.jsp
        │                   ├── purchaseInvoice.jsp
        │                   ├── newSparePart.jsp
        │                   ├── confirmedInvoice.jsp
        │                   └── printInvoice.jsp
        └── test/java/
            ├── model/PurchaseInvoice4Test.java
            ├── util/Validation4Test.java
            └── dao/PurchaseInvoiceDAO4IT.java
```

Ảnh SVG chỉ là minh họa cục bộ đơn giản, phải ghi “Ảnh minh họa” và không giả là ảnh sản phẩm của nhà sản xuất. Không cần sinh ảnh AI, upload ảnh, thư viện gallery hay truy cập CDN. Trường `imagePath` trong form chỉ chọn đường dẫn asset cục bộ hợp lệ.

## 4. Nội dung và hàm của từng nhóm file

Các chữ ký dưới đây là hợp đồng. Có thể thêm hàm private nhỏ để tránh lặp, nhưng không đổi public method/endpoint/dữ liệu mà không cập nhật tài liệu và truy vết. `List<T>` mặc định dùng `ArrayList<T>` khi cần implementation. Không tạo phương thức CRUD chưa được dùng.

### 4.1. Model — giữ mô hình báo cáo

Mọi thuộc tính `private`, có constructor không tham số, getter/setter kiểu JavaBean; list khởi tạo rỗng. Với mỗi thuộc tính `x:T` trong hợp đồng có `T getX()` và `void setX(T value)`; không bắt Gemini tự đoán thuộc tính. Hai lớp trừu tượng là `Member4`, `Employee4`; lớp con rỗng chỉ có constructor cần thiết, không lặp id/thuộc tính cha.

| File/lớp | Thuộc tính và phương thức cần có |
|---|---|
| `Member4` | id, fullName, username, password, dateOfBirth, address, email, phoneNumber, role, accountStatus theo hợp đồng |
| `Employee4 extends Member4` | position, employmentStatus |
| `WarehouseStaff4 extends Employee4` | Vai trò kho dùng cho module nhập, không có id mới hay SQL |
| `Customer4 extends Member4` | Không có thuộc tính riêng hiện tại |
| `Supplier4`, `Service4`, `SparePartCategory4` | Thuộc tính đúng từ điển dữ liệu của hai module |
| `SparePart4` | Các cột danh mục + `category:SparePartCategory4`, `stockQuantity:BigDecimal` lưu trực tiếp; thao tác SQL tăng tồn chuyển sang `SparePartDAO4.increaseStockAfterPurchase(...)` theo bước 3 của bài giảng |
| `PurchaseInvoiceDetail4` | id, sparePart, quantity, purchaseUnitPrice, sparePartNameSnapshot:String, unitSnapshot:String, lineAmount dẫn xuất; `BigDecimal calculateLineAmount()` |
| `PurchaseInvoice4` | id, requestCode, purchaseDate, confirmedAt, paymentStatus, paidAt, supplier, warehouseStaff, invoiceDetails, supplierNameSnapshot:String, warehouseStaffNameSnapshot:String, totalAmount dẫn xuất; `BigDecimal calculateTotalAmount()` |

Chỉ tạo đúng 10 model ở 00; không tạo POJO để lấp các lớp còn lại trong mô hình báo cáo. PurchaseInvoice4 giữ List<PurchaseInvoiceDetail4>; dòng không cần object hóa đơn cha ngược lại, tránh object graph vòng khi copy nháp.

Giá trị getter thành tiền/tổng tiền nên tính lại từ dữ liệu gốc hoặc gọi hàm tính; không cho request bind giá trị tiền tổng. Không dùng `new BigDecimal(double)`.

### 4.2. DAO

Quy ước chung: truy vấn danh sách không có kết quả trả list rỗng; truy vấn theo id không tồn tại trả `null`; lỗi SQL ném `SQLException`, không trả list rỗng/false giả thành không có dữ liệu. Mutating methods có thể ném `ValidationException4`. ResultSet/statement/connection dùng try-with-resources theo quyền sở hữu kết nối.

| File | Chữ ký bắt buộc và trách nhiệm |
|---|---|
| `DAO.java` | `protected Connection getConnection() throws SQLException`; `protected String createSearchPattern(String keyword)` escape ký tự LIKE; đọc biến `GARAMAN_DB_URL`, `GARAMAN_DB_USER`, `GARAMAN_DB_PASSWORD`, hoặc `db.properties` local; chỉ tạo factory cấu hình chung, không giữ static connection |
| `MemberDAO4.java` | `public Member4 findByUsername(String username) throws SQLException`; `public Member4 getActiveMember(int id) throws SQLException`; `private Member4 mapMember(ResultSet rs) throws SQLException`. JOIN Employee khi cần và tạo đúng subclass theo vai trò. Chưa có DAO đăng kí tài khoản |
| `WarehouseStaffDAO4.java` | `public WarehouseStaff4 getWarehouseStaffDetails(int id) throws SQLException`; chỉ trả nhân viên đang làm có vị trí kho và tài khoản hợp lệ |
| `ServiceDAO4.java` | `public List<Service4> findServicesByName(String keyword) throws SQLException`; `public Service4 getServiceDetails(int id) throws SQLException`; cả hai chỉ trả dịch vụ đang cung cấp; `private Service4 mapService(ResultSet rs)` |
| `SparePartCategoryDAO4.java` | `public List<SparePartCategory4> getActiveCategories() throws SQLException`; chỉ nhóm đang dùng; `public SparePartCategory4 getActiveCategory(int id) throws SQLException`; `private SparePartCategory4 mapCategory(ResultSet rs)` |
| `SupplierDAO4.java` | `public List<Supplier4> findSuppliersByName(String keyword) throws SQLException`; `public Supplier4 getSupplierDetails(int id) throws SQLException`; `public int addSupplier(Supplier4 supplier) throws SQLException, ValidationException4`; tìm/chọn chỉ nhà cung cấp đang hoạt động; `private Supplier4 mapSupplier(ResultSet rs)` |
| `SparePartDAO4.java` | `public List<SparePart4> findSparePartsByName(String keyword, boolean publishedOnly) throws SQLException`; `public SparePart4 getSparePartDetails(int id, boolean publishedOnly) throws SQLException`; `public int addSparePart(SparePart4 part) throws SQLException, ValidationException4`; `public BigDecimal getStockQuantity(int id) throws SQLException`; `void increaseStockAfterPurchase(Connection connection, int sparePartId, BigDecimal quantity) throws SQLException, ValidationException4`; `private SparePart4 mapSparePart(ResultSet rs) throws SQLException`. Hàm tăng tồn có phạm vi package, nhận connection của transaction; không tự mở/đóng/commit connection. `getStockQuantity` đọc cột đã lưu, không SUM lịch sử. `publishedOnly=true` cho khách; `false` cho kho, vẫn loại ngừng kinh doanh. Với hóa đơn cũ phải đọc snapshot, không dùng bộ lọc này để làm biến mất dòng lịch sử |
| `PurchaseInvoiceDAO4.java` | Các chữ ký và transaction ở phần dưới |

`PurchaseInvoiceDAO4`:

```java
public int savePurchaseInvoice(PurchaseInvoice4 purchaseInvoice)
    throws SQLException, ValidationException4;
public PurchaseInvoice4 getPurchaseInvoiceDetails(int id)
    throws SQLException;
public Integer findInvoiceIdByRequestCode(String requestCode, int warehouseStaffId)
    throws SQLException;
public List<PurchaseInvoice4> getUnpaidInvoices(int warehouseStaffId)
    throws SQLException;
public boolean recordPayment(int invoiceId, BigDecimal amountPaid,
                                  int currentWarehouseStaffId)
    throws SQLException, ValidationException4;

private int insertInvoice(Connection connection, PurchaseInvoice4 purchaseInvoice)
    throws SQLException;
private void insertInvoiceDetails(Connection connection, int invoiceId,
                        List<PurchaseInvoiceDetail4> invoiceDetail)
    throws SQLException;
private List<PurchaseInvoiceDetail4> getInvoiceDetails(Connection connection, int invoiceId)
    throws SQLException;
private PurchaseInvoice4 mapInvoice(ResultSet rs) throws SQLException;
```

`recordPayment()` trả true khi chuyển trạng thái, false khi chính người lập retry hóa đơn đã trả, giữ nguyên thời điểm. Không có hóa đơn→404; sai người→403 kể cả đã trả; số tiền parse sai hoặc chưa trả nhưng khác tổng→400. Thứ tự xử lý chính xác ở 10. Kho có thể mở/in hóa đơn đã xác nhận, nhưng chỉ người lập được pay; danh sách chưa trả lọc theo người hiện tại. DAO nhận id từ Servlet/session, không lưu FK người trả tiền riêng. Không lọc mất người lập cũ trên chứng từ lịch sử chỉ vì đã ngừng làm việc.

Không cần `PurchaseInvoiceDetailDAO4` riêng: chi tiết thuộc aggregate hóa đơn, chỉ được ghi trong transaction của hóa đơn. Không tự dùng DAO con mở connection mới trong transaction.

#### Trình tự lưu hóa đơn

1. Kiểm tra mã yêu cầu hợp lệ; nếu đã có hóa đơn của nhân viên này với mã đó thì trả id cũ ngay.
2. Mở connection, bắt đầu transaction. Kiểm tra lại nhân viên kho, nhà cung cấp và phụ tùng; nhóm của part phải tồn tại nhưng part cũ vẫn được nhập khi nhóm ngừng sử dụng. Chỉ yêu cầu nhóm đang sử dụng khi tạo part mới; không tin object đã giữ lâu trong session.
3. Kiểm tra có ít nhất một dòng, không trùng phụ tùng, lượng/giá dương, scale/đơn vị đúng. Chốt tên NCC, tên nhân viên, tên và đơn vị phụ tùng từ DB vào đúng các trường áp dụng trong file 03; giá nhập lấy từ input đã validate. Không lưu thêm hãng, địa chỉ hay điện thoại vào hóa đơn.
4. Khóa bản ghi theo thứ tự Supplier → Employee → SparePart (part id tăng dần), đọc lại trạng thái/đơn vị/tồn rồi chốt thông tin. Các bước kiểm tra/chụp dữ liệu ở bước 2–3 phải thực hiện hoặc kiểm tra lại sau khi khóa; không chụp master từ dữ liệu cũ trước khóa. Các giao dịch nhập dùng cùng thứ tự khóa.
5. Gán thời điểm xác nhận ở server, trạng thái `Chưa thanh toán`; INSERT header với `requestCode UNIQUE`, lấy generated key; INSERT mọi dòng bằng cùng connection.
6. Sau khi INSERT chi tiết, gọi `SparePartDAO4.increaseStockAfterPurchase(con,id,quantity)` cho từng dòng, dùng chính Connection đang mở. UPDATE cộng dồn tồn theo file 03. Commit khi tất cả thành công; lỗi chi tiết hoặc cập nhật tồn rollback cả header, mọi dòng và mọi thay đổi tồn. Không đặt commit bên trong DAO con.
7. Nếu va chạm UNIQUE của `requestCode` do request đồng thời: rollback connection hiện tại, truy vấn lại hóa đơn đã commit bằng connection mới, xác minh chủ thể rồi trả cùng id. Chỉ xử lí riêng đúng lỗi unique mã yêu cầu; lỗi khác vẫn báo lỗi.
8. Trả id để Servlet redirect GET hóa đơn đã lưu. Nếu response bị mất, retry cùng mã vẫn tìm được hóa đơn.

`SELECT ... FOR UPDATE` nằm trong transaction và giữ khóa tới commit/rollback; xem [MySQL — Locking Reads](https://dev.mysql.com/doc/refman/8.4/en/innodb-locking-reads.html). Chỉ dùng cho transaction nhập hàng của module này.

#### Trình tự ghi nhận thanh toán

Mở transaction → khóa header → kiểm tra người hiện tại là nhân viên kho đang hoạt động và chính người lập → đọc trạng thái/tổng tiền từ dòng lịch sử → nếu đã thanh toán trả false, giữ nguyên thời điểm → nếu chưa trả, số tiền phải đúng tổng → UPDATE trạng thái/thời điểm đúng một lần → commit. Không đổi người lập, không thêm FK thu/chi, không cập nhật tồn, không tạo bảng Payment hoặc khoản trả từng phần. Người khác cố gọi pay bị từ chối quyền. `recordPayment` báo lỗi quyền bằng `ValidationException4` với field `permission`, Servlet chuyển thành HTTP 403.

### 4.3. Servlet

Mọi Servlet dùng `init()` tạo DAO stateless, `doGet()` phân phối các action đọc, `doPost()` phân phối action thay đổi; URL đăng ký trong `web.xml`, không đồng thời đăng ký trùng bằng annotation. Helpers nhận `(HttpServletRequest request, HttpServletResponse response)`; kiểu trả về `void`, khai báo `ServletException, IOException`, bổ sung `SQLException, ValidationException4` nếu gọi DAO và để entry point xử lí.

| File | Hàm bắt buộc ngoài init/doGet/doPost |
|---|---|
| `AuthServlet4` | `showLogin(...)`, `login(...)`, `logout(...)`; xác thực hash, tạo session mới sau login, tạo SessionUser4 và token; login sai trả thông báo chung, không lộ SQL/hash |
| `HomeServlet4` | `showHome(...)`; chọn đúng JSP theo vai trò; chỉ hỗ trợ Customer/WarehouseStaff theo 00; vai trò ngoài phạm vi bị từ chối, không tạo lớp/trang cho vai trò khác |
| `CatalogServlet4` | `showSearch(...)`, `searchCatalog(...)`, `viewDetails(...)`; list dịch vụ và phụ tùng lấy độc lập; phân biệt bằng `itemType=service|part`, không dùng id để đoán loại |
| `SupplierServlet4` | `showSupplierSelection(...)`, `searchSuppliers(...)`, `showNewSupplier(...)`, `addSupplier(...)`; sau lưu redirect kết quả có bản ghi mới, nhân viên chủ động chọn như kịch bản |
| `SparePartServlet4` | `showNewSparePart(...)`, `addSparePart(...)`; load nhóm đang dùng, kiểm tra requestCode bản nháp còn hợp lệ; khi lưu xong quay về hóa đơn và hiện phụ tùng mới để nhân viên chọn, chưa tự cộng dòng/chưa tăng tồn |
| `PurchaseInvoiceServlet4` | `startPurchase(...)`, `showDraft(...)`, `searchSpareParts(...)`, `addInvoiceLine(...)`, `updateInvoiceLines(...)`, `removeInvoiceLine(...)`, `cancelDraft(...)`, `confirmInvoice(...)`, `viewInvoice(...)`, `printInvoice(...)`, `recordPayment(...)`, `showUnpaidInvoices(...)` |

Quy tắc quan trọng của `PurchaseInvoiceServlet4`:

- `startPurchase` nhận id nhà cung cấp từ POST, lấy user từ session, sinh UUID và tạo draft. Ngày nhập hiển thị dự kiến; mốc chính thức được gán lúc xác nhận, không tin hidden input nhân viên/ngày/tổng tiền.
- `searchSpareParts` nhận cả toàn bộ ô dòng đang sửa để giữ dữ liệu trước khi tìm; xử lí POST cập nhật nháp rồi redirect GET cùng từ khóa.
- `addInvoiceLine` cũng nhận toàn bộ ô dòng để giữ các sửa chưa bấm cập nhật; nếu part đã có thì chỉ focus, không tự cộng số lượng.
- `updateInvoiceLines` giữ input lỗi để người dùng sửa và chặn xác nhận; không âm thầm chuyển lỗi parse thành 0. Việc thêm part tạo dòng 0 là trường hợp rõ ràng, khác với parse lỗi.
- Trước khi sang form thêm phụ tùng, submit lưu các dòng đang sửa vào nháp bằng action `update` kèm `destination=new-part`; không dùng link GET làm mất input chưa gửi.
- `confirmInvoice` tra mã đã xác nhận trước khi yêu cầu draft còn trong session. Nhận lại dữ liệu dòng từ form, tính/validate phía server, gọi DAO. Sau commit xóa draft, giữ khả năng retry nhờ mã đã lưu trong DB.
- `removeInvoiceLine` chỉ sửa nháp; `cancelDraft` chỉ xóa đúng draft theo mã, không xóa hồ sơ phụ tùng/nhà cung cấp vừa tạo.
- `showUnpaidInvoices` chỉ lấy hóa đơn chưa thanh toán do người đang đăng nhập lập, phục vụ mở lại sau khi dừng ở bước xác nhận/in. Chưa có chức năng bàn giao người thanh toán; ngoại lệ 18b phải khớp file 01.

### 4.4. Filter, DTO, exception và tiện ích

| File | Hợp đồng |
|---|---|
| `EncodingFilter4` | `init(FilterConfig)`, `doFilter(ServletRequest, ServletResponse, FilterChain)`, `destroy()`; đặt UTF-8 trước khi đọc param và response content charset |
| `AuthFilter4` | Các hàm Filter chuẩn; đọc lại thành viên còn hoạt động ở mỗi request bảo vệ; kiểm tra phiên cả `/home` và prefix `/customer/*`, `/warehouse/*`; tất cả POST kiểm tra CSRF kể cả login, logout; cho GET `/login` và `/assets/*` công khai. Giữ thứ tự filter sau Encoding |
| `SessionUser4` | `id:int`, `fullName:String`, `role:String`, `position:String`; constructor/getter, `static SessionUser4 fromMember(Member4 member)`; không chứa password/hash |
| `ValidationException4` | Kế thừa `Exception`, constructors `(String message)` và `(String field, String message)`, getter `getField()` |
| `AppConstants4` | Hằng vai trò/trạng thái/đơn vị/session keys; `APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh")`; constructor private, không có SQL |
| `Validation4` | `String requireText(String value, String field, int maxLength)`; `String optionalText(String value, int maxLength)`; `int positiveId(String value, String field)`; `BigDecimal parseDecimal(String value, String field, int precision, int scale, boolean allowZero)`; `void validateQuantity(BigDecimal qty, String unit)`; `void validateEmail(String email)`; `void validatePhoneNumber(String phone)`; `void validateSupplier(Supplier4 value)`; `void validateSparePart(SparePart4 value)`; `void validatePurchaseInvoice(PurchaseInvoice4 value)`; validate lỗi ném ValidationException4 |
| `PasswordUtil4` | `String hashPassword(char[] password)`; `boolean validatePassword(char[] password, String storedHash)`; `main(String[] args)` tạo hash cho seed local. Chọn PBKDF2WithHmacSHA256 với salt ngẫu nhiên, định dạng tự chứa thuật toán/iteration/salt/hash, so sánh constant-time; không tự hash một vòng SHA rồi coi như đủ |
| `WebUtil4` | `void forward(req,res,String view)`; `void redirect(req,res,String path)`; `SessionUser4 requireUser(req)`; `String createCsrfToken(HttpSession session)`; `boolean isValidCsrfToken(req)`; `void flash(req,String message)`; `String getFlash(req)`; URL nội bộ gắn contextPath và encode query đúng, không redirect sang giá trị URL tùy ý từ input |
| `DraftStore4` | `PurchaseInvoice4 createDraft(HttpSession session, Supplier4 supplier, WarehouseStaff4 staff)`; `PurchaseInvoice4 getDraft(HttpSession session, String requestCode)`; `void saveDraft(HttpSession session, PurchaseInvoice4 draft)`; `void removeDraft(HttpSession session, String requestCode)`; `PurchaseInvoice4 copyDraft(PurchaseInvoice4 draft)`; `void validateDraftLimit(HttpSession session)`. Map theo UUID, mỗi session tối đa 10 draft, đầy thì báo người dùng hủy draft cũ, không tự xóa draft đang làm |

Thao tác đọc–kiểm tra version–sửa–ghi draft phải nằm trong cùng `synchronized(session)` ngắn ở Servlet. Dùng draftVersions4/draftConfirming4 và helper ở 10 để chặn form cũ hoặc sửa/hủy khi confirm; trả409. Lấy deep-copy trước ra ngoài khóa; không giữ khóa session khi gọi DB. Hai UUID không trộn; hai form cùng UUID/version không âm thầm ghi đè. Không lưu draft vào field static/Servlet.

`DraftStore4` quản lí input thô: `Map<String,String> getFormValue(HttpSession session,String requestCode)` và `void saveFormValue(HttpSession session,String requestCode,Map<String,String> values)`, public static, throws ValidationException4. Xóa draft phải xóa raw/version/cờ tương ứng. Các util/throws chưa ghi đầy đủ ở bảng trên phải theo chữ ký chính xác ở 10; requireUser thiếu phiên báo quyền, không trả user giả.

### 4.5. File cấu hình, SQL và tài liệu

| File | Nội dung bắt buộc |
|---|---|
| `pom.xml` | WAR, Java17, dependencies đã chốt, UTF-8; JUnit; profile `integration-test` chạy `*IT` bằng Failsafe, mặc định unit test không cần DB |
| `.gitignore` | target/, log, cấu hình DB local, file IDE/cache; không ignore seed/schema/tài liệu |
| `db.properties.example` | Mẫu URL/user/password rỗng hoặc placeholder; copy thành `db.properties` local; biến môi trường ưu tiên; không hardcode root/password thật |
| `web.xml` | Mapping Filter theo thứ tự, mapping Servlet trong hợp đồng, session timeout 30 phút, welcome vào `/home`, error page; cookie HttpOnly; cấu hình Secure khi chạy HTTPS |
| `01_schema.sql` | Tạo database/tables/index/check theo hợp đồng, có cột tồn lưu trực tiếp; không DROP database mặc định, không tự nạp dữ liệu |
| `02_seed.sql` | Tài khoản demo có hash thật, nhóm, dịch vụ/phụ tùng/nhà cung cấp mẫu; tồn ban đầu qua hóa đơn nhập seed; có chú thích dữ liệu demo |
| `03_verify.sql` | SELECT đối chiếu tồn với tổng nhập đã xác nhận trên DB riêng hai module (tồn ban đầu 0), tổng tiền, trùng mã/dòng, hóa đơn thiếu dòng và trạng thái/thời điểm; không JOIN bảng ngoài 9 bảng; chỉ đọc |
| `THAY_DOI_SO_VOI_BAO_CAO.md` | Chỉ đối chiếu hai module: vị trí nguồn, ánh xạ thiết kế/code, điều chỉnh kỹ thuật có lý do. Không sửa hoặc mở rộng mô hình tổng thể |
| `TU_DIEN_DU_LIEU.md` | Schema thực tế, kiểu, NULL/default/PK/FK/UNIQUE/CHECK; đánh dấu dữ liệu lịch sử và dẫn xuất |
| `MA_TRAN_TRUY_VET.md` | Quy tắc/kịch bản → UI → endpoint → Servlet method → DAO method → bảng/cột → test → hình trong 4.3.1; mẫu và mã theo file 07 |
| `MODEL_FIELD_MAP.md` | Mỗi dòng là một thuộc tính: lớp/field/kiểu trong DOCX → kiểu Java → cột SQL hoặc thuộc tính dẫn xuất/đối tượng; không tự thêm field |
| `METHOD_MAP.md` | Package/file/class, phạm vi, static/instance, chữ ký đầy đủ (param/return/throws), nơi gọi, route/nhánh, nguồn hợp đồng, vị trí code. Liệt kê hàm nghiệp vụ và helper private thực sự có trong code; getter/setter có thể nhóm theo quy tắc JavaBean. Không đưa method dự kiến chưa triển khai lên sơ đồ |
| `KIEM_THU.md` | Các ca ở phần 8, môi trường, lệnh, kết quả, ảnh/log; chưa chạy thì ghi chưa chạy |
| `TIEN_DO.md` | Tiến độ theo từng giai đoạn hai module; chỉ ghi file đã làm, việc còn thiếu và kết quả kiểm chứng thực tế; không mở thêm giai đoạn cho module khác |
| `README.md` | Yêu cầu môi trường, import schema/seed, cấu hình DB, build WAR, deploy Tomcat9, URL, tài khoản demo, chạy test và giới hạn module |

## 5. JSP và bố cục màn hình

Giữ bố cục, màu sắc, sidebar/topbar, form/bảng/dropdown/nút và mẫu in của `giao-dien-tham-chieu/web` theo09/11. Bài giảng làm mẫu phương pháp trình bày, không thay diện mạo GaraMan. Chỉ đổi dữ liệu mẫu thành dữ liệu server và các chi tiết kỹ thuật cần cho form; không tự thiết kế lại. Bản in A4 giữ bố cục nguồn.

| JSP | Trường và thao tác bắt buộc |
|---|---|
| `login.jsp` | Username, password, token, nút đăng nhập, thông báo lỗi; không xây đăng kí tài khoản trong đợt này |
| `customerHome.jsp` | Tên khách, nút Tìm thông tin dịch vụ/phụ tùng, form đăng xuất |
| `catalogSearch.jsp` | Giữ ô tên và select Loại thông tin như nguồn, itemTypeFilter mặc định all theo11; Tìm kiếm/Về trang chính; chưa tìm không có bảng dữ liệu; kết quả trong một bảng Mã/Tên/Loại/Đơn giá/Chọn; không tách bảng theo loại; rỗng có thông báo riêng |
| `itemDetails.jsp` | Một JSP với hai nhánh theo loại: dịch vụ đầy đủ nội dung/giá công/thời gian/bảo hành; phụ tùng đầy đủ nhóm/đơn vị/hãng/thông số/dòng xe/giá bán/bảo hành; ảnh placeholder khi thiếu; Quay lại giữ từ khóa |
| `warehouseHome.jsp` | Tên nhân viên, Nhập phụ tùng, Hóa đơn nhập chưa thanh toán của tôi, đăng xuất. Danh sách chưa thanh toán của người hiện tại render ngay dưới trang chủ khi mở chức năng đó |
| `selectSupplier.jsp` | Tên nhà cung cấp, tìm, thêm mới, bảng mã/tên/địa chỉ/điện thoại/Chọn; chọn bằng POST bắt đầu draft |
| `newSupplier.jsp` | Tên/địa chỉ/điện thoại bắt buộc, email tùy chọn, Lưu và Quay lại; lỗi hiển thị đúng trường và giữ dữ liệu nhập |
| `purchaseInvoice.jsp` | Header NCC/nhân viên/ngày nhập dự kiến như nguồn; tìm tên part + Tìm kiếm + bảng Mã/Tên phụ tùng/ĐVT/Hãng sản xuất/Chọn + Thêm mới. Bấm Chọn ở từng dòng gửi POST add-line giữ các ô dòng theo11. Bảng dòng nhập STT/Phụ tùng/Hãng/ĐVT/SL/Đơn giá/Thành tiền/Xóa; tổng luôn thấy kể cả0; Xác nhận/Hủy. Token/UUID/version ẩn. Dòng mới0/0, trùng focus dòng cũ |
| `newSparePart.jsp` | Tên, nhóm có sẵn, đơn vị, hãng, mô tả, ảnh cục bộ, thông số, dòng xe, giá bán, hạn/điều kiện bảo hành, trạng thái; Lưu và Quay lại draft; hiển thị rõ giá nhập được nhập ở dòng hóa đơn |
| `confirmedInvoice.jsp` | Mã/thời điểm/nhà cung cấp/nhân viên từ dữ liệu đã lưu, bảng dòng, tổng, trạng thái; In hóa đơn; ô Số tiền đã trả và Đã thanh toán khi chưa trả và người hiện tại là người lập; quay về trang chính; không sửa dòng đã xác nhận |
| `printInvoice.jsp` | Nội dung chứng từ lấy cùng dữ liệu lịch sử; In và Quay lại; không tự tạo giao dịch khi mở trang/in; không có sidebar/form nhập ở phần in |
| `header.jspf`, `footer.jspf`, `messages.jspf` | Khung chung, thông tin user, lỗi/flash an toàn HTML; footer có chú thích demo ngắn |
| `error.jsp` | Tiêu đề theo 400/403/404/500, thông điệp dễ hiểu, đường quay lại phù hợp; không in stack trace/SQL/password |

JSP render bằng JSTL/EL và `<c:out>` khi phù hợp; URL dùng `<c:url>` và `<c:param>`; không ghép từ khóa thô vào HTML/URL. Form POST chứa CSRF. Màn hình không lộ tên bảng, UUID kỹ thuật, Connection, SQL hoặc nội dung hướng dẫn lập trình cho người sử dụng.

### Các hàm JavaScript

- `purchase.js`: `recalculateEstimatedTotal()`, `focusExistingLine(partId)`, `xacNhanHuy()`. Tính trước chỉ để phản hồi giao diện, không gửi tổng tiền như căn cứ chốt. Không tự thêm/xóa hàng đã lưu bằng localStorage.
- `print.js`: `printInvoice()` gọi `window.print()`, `returnToInvoice()` dùng URL GET id hóa đơn đã có. Có thể dùng `afterprint` hỗ trợ quay lại, nhưng sự kiện này không chứng minh in thành công; luôn có nút Quay lại, không đổi trạng thái thanh toán theo sự kiện in.
- Không dùng AJAX trong baseline. Việc tìm part bằng POST rồi redirect và select kết quả HTML đã đáp ứng mẫu kịch bản. Không tạo JSON endpoint không cần thiết.

Khi dùng purchase.js phải nạp script trên trang draft và thống nhất selector với các ô lượng/giá, thành tiền và tổng trong JSP. Không coi có file JavaScript là đã có tương tác. Nhãn giao diện không hiển thị UUID/snapshot/field SQL; ngày/giờ render qua thuộc tính hiển thị ở10.

## 6. Viết mục 4.3.1 theo đúng bài giảng

Chỉ chuẩn bị nội dung **4.3.1 của hai module** theo 07 ở file Markdown và hình sẵn chèn. Giữ số nguồn để đối chiếu, không đánh số lại các chương hoặc sửa toàn bộ DOCX. Không copy tên module đăng kí học/nhập điểm từ mẫu.

### Phần mở đầu

Nêu đầu vào là hai hình lớp phân tích module hiện có (đang là Hình 1.5/1.6) và mô hình thực thể thiết kế đã hiệu chỉnh; dùng số sau cùng theo bảng ánh xạ hình, không hardcode số chưa tồn tại; nền tảng web JSP/Servlet/JDBC/MySQL. Giải thích Servlet điều phối theo Lab1, DAO giữ vai trò truy xuất như bài giảng.

### a. Thiết kế tĩnh module tìm kiếm thông tin dịch vụ/phụ tùng

1. **Bước 1 — Thiết kế giao diện:** hình bố cục các màn hình nối bằng mũi tên tương tự Hình 4.3 của bài giảng. Gồm trang chính → trang tìm trống/kết quả → chi tiết dịch vụ hoặc phụ tùng → quay lại. Ảnh chụp thật là minh chứng bổ sung, không thay thế sơ đồ luồng màn hình.
2. **Bước 2 — Lớp giao diện:** ánh xạ CustomerHomeUI4 → `customerHome.jsp`; CatalogSearchUI4 → `catalogSearch.jsp`; ItemDetailsUI4 → `itemDetails.jsp`. Liệt kê input/output/submit, dữ liệu request và dữ liệu ẩn `id`, `itemType`, `keyword`; Servlet Home/Catalog tương ứng. Dữ liệu người dùng lấy từ session, không tin hidden role/userId.
3. **Bước 3 — DAO:** DAO, ServiceDAO4, SparePartDAO4; trình bày chữ ký/tham số/kết quả/phạm vi lọc trạng thái theo code.
4. **Bước 4 — Thực thể:** Member4/Customer4, Service4, SparePart4/SparePartCategory4; giữ quan hệ và kế thừa như mô hình. Vẽ `catalog-design.puml`: JSP/Servlet ở trên, DAO giữa, thực thể dưới; mũi tên JSP gửi request cho Servlet, Servlet phụ thuộc DAO và forward JSP, DAO tạo/map thực thể.

### b. Thiết kế tĩnh module nhập phụ tùng

1. **Bước 1:** trang chính kho → chọn nhà cung cấp; nhánh thêm nhà cung cấp quay về lựa chọn; → lập hóa đơn; nhánh thêm phụ tùng quay về draft; → xác nhận → bản in → hóa đơn → ghi nhận đã trả. Thể hiện vòng lặp tìm/chọn/sửa dòng và nhánh hủy trước xác nhận.
2. **Bước 2:** bảng ánh xạ 7 lớp biên trong pha phân tích với 7 JSP kho; thêm Servlet Supplier/SparePart/PurchaseInvoice. Liệt kê thuộc tính ẩn và session dùng để trao đổi, nhất là mã bản nháp/CSRF; số lượng/giá nhập là input, thành tiền/tổng/nhân viên/thời điểm là output.
3. **Bước 3:** DAO, SupplierDAO4, WarehouseStaffDAO4, SparePartCategoryDAO4, SparePartDAO4, PurchaseInvoiceDAO4. Ghi rõ `calculateTotalAmount()` nằm ở model; persistence ở DAO; chuyển `increaseStockAfterPurchase()` sang `SparePartDAO4`, có Connection và tham số lượng/id, được DAO hóa đơn gọi trong cùng transaction. Thể hiện phụ thuộc PurchaseInvoiceDAO4 → SparePartDAO4; không sửa hình giao tiếp phân tích đã giữ.
4. **Bước 4:** Member4/Employee4/WarehouseStaff4, Supplier4, SparePartCategory4/SparePart4, PurchaseInvoice4/PurchaseInvoiceDetail4. Vẽ composition hóa đơn–dòng, association tới nhà cung cấp/nhân viên/danh mục, đúng thuộc tính object và kiểu Java.

Hai biểu đồ class phải có phạm vi `+/-/#`, kiểu thuộc tính, tham số/kiểu trả về DAO; hiển thị `<<JSP>>` và `<<Servlet>>` rõ ràng. JSP là thành phần giao diện được mô hình hóa theo mẫu, không giả nó là một JavaBean được tự viết thêm. Không bắt vẽ mọi getter/setter trên hình nếu quá rối, nhưng code phải có.

`database.puml` chỉ thể hiện tập con 9 bảng app sử dụng, không thay ERD tổng thể. Bốn hình chính chỉ cho hai module. Không yêu cầu vẽ lại mô hình tổng thể, viết phần hoạt động/triển khai hay các chương tiếp theo.

## 7. Trình tự triển khai cho Gemini

| Giai đoạn | Việc cần làm | Điều kiện được chuyển bước |
|---|---|---|
| 0. Khóa căn cứ | Đọc 00, hai kịch bản/hình phân tích của bài làm, mẫu bài giảng/LAB1 và hợp đồng; ghi đối chiếu liên quan theo 01 | Phạm vi đúng hai module, 10 model và 9 bảng; không tạo trước DOCX chỉnh sửa |
| 1. Bộ khung | Maven WAR, package, web.xml, Filter, error JSP, CSS; build | WAR build được, không trộn javax/jakarta |
| 2. CSDL và model | Chỉ 9 bảng và 10 model trong 00, seed cho hai module, công thức tính và verify SQL | Import DB riêng trống được; không phụ thuộc bảng/model ngoài phạm vi |
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
- P07: Hai request đồng thời cùng `requestCode` → đúng 1 header/nhóm dòng; retry sau khi xóa draft hoặc mất response vẫn trả id cũ.
- P08: Hai mã khác nhau cùng nhập một part → lưu cả hai hóa đơn hợp lệ, lượng tồn tăng bằng tổng hai lần; không đánh đồng hai lần nhập chỉ vì cùng NCC/part.
- P09: Hủy trước xác nhận chỉ bỏ nháp; các NCC/part mới vẫn tồn tại; không có giao dịch nhập mới.
- P10: Hai tab hai draft không trộn NCC/dòng; token/mã giả hoặc của session khác không dùng để thao tác draft.
- P11: In và in lại không thay đổi số hóa đơn, dòng, trạng thái thanh toán hoặc tồn. Hủy hộp thoại in cũng không tự ghi nhận đã trả.
- P12: Người lập nhập đúng tổng → ghi trạng thái/thời điểm; hai request cùng người lập đồng thời chỉ một request thay đổi trạng thái, lần sau giữ thời điểm. Tồn không đổi.
- P13: A lập hóa đơn rồi đăng xuất; B thuộc kho có thể đọc/in theo quyền nhưng không thấy hóa đơn A trong danh sách chưa trả của mình và không gọi pay được (403). A đăng nhập lại thấy và thanh toán được; người lập không đổi.
- P14: Sửa tên phụ tùng/NCC/nhân viên sau khi xác nhận → bản in cũ vẫn giữ các tên và đơn vị áp dụng đã lưu. Hãng/địa chỉ/điện thoại không được đưa lên bản in. Không đổi đơn vị phụ tùng đã có giao dịch; dữ liệu kiểm thử sửa master chỉ dùng DB test.
- P15: Client giả tổng, ngày, id người lập/người thực hiện, trạng thái hoặc giá bán → server bỏ qua các trường không thuộc input được phép; thiếu/sai CSRF bị từ chối.

- P16: Người lập ngừng làm việc → không đăng nhập/thanh toán được; nhân viên kho đang hoạt động vẫn tra cứu/in chứng từ lịch sử, tên người lập không mất. Chưa có chức năng bàn giao để trả thay.

`PurchaseInvoice4Test`: tổng nhiều dòng, số thập phân và quy tắc làm tròn. `Validation4Test`: ranh giới lượng/giá, đơn vị, scale, trạng thái công bố. `PurchaseInvoiceDAO4IT`: rollback, xác nhận trùng đồng thời, thanh toán lặp và bảo toàn snapshot. Bổ sung kiểm thử tồn: (a) nhập 2,5 Lít (input 2.500) cộng chính xác 2,5 Lít; (b) giao dịch nhiều part, cập nhật part đầu thành công nhưng part sau vượt giới hạn tồn → rollback cả hai tồn cùng chứng từ; (c) hai mã khác nhau đồng thời cùng part không mất lượt cộng; (d) pay/in/retry không cộng lại. Integration test chỉ chạy trên database tên kết thúc `_test`, không xóa dữ liệu DB demo hoặc DB người dùng.

## 9. Điều kiện hoàn thành

- Có đủ file và chức năng được giao; không còn TODO trong luồng chính/ngoại lệ bắt buộc.
- CSDL, model, DAO, controller, JSP và sơ đồ dùng cùng thuật ngữ/tên/chữ ký.
- Build WAR được; test nào chạy có kết quả thật, test chưa chạy có lý do.
- Mục 4.3.1 chứa đủ thiết kế giao diện, ánh xạ biên→JSP/Servlet, thuộc tính ẩn, DAO với input/output và thực thể liên quan.
- Code/test/demo hoàn thành đúng hai module; 4.3.1 và hình chỉ mô tả hai module. Đối chiếu các điểm liên quan trong 01, giữ cột tồn và một người lập/trả tiền; tài liệu gốc nguyên vẹn. Không yêu cầu bản DOCX sửa toàn bộ.
- Các kết quả chưa làm thuộc module khác được ghi trong phạm vi README, không quảng bá thành tính năng đã có.

## 10. Khóa cách ánh xạ model và thiết kế

`MODEL_FIELD_MAP.md` chỉ liệt kê field trong **9 bảng và 10 model của hai module** theo 00; không liệt kê hoặc tạo code cho các lớp còn lại trong báo cáo. Với cột SQL không phải FK: INT NN → int; INT N → Integer; VARCHAR/TEXT/CHAR → String; DECIMAL → BigDecimal; DATE → LocalDate; DATETIME → LocalDateTime. FK chuyển thành thuộc tính object được chỉ rõ trong file 03, không tự thêm cặp object/id trùng ý nghĩa. id lớp con kế thừa từ cha.

Các field liên quan: stockQuantity ánh xạ cột đã lưu; lineAmount của dòng nhập và totalAmount của hóa đơn nhập là getter tính lại; danh sách dòng dùng List thay mảng có ánh xạ, quan hệ không đổi. Không thêm nhanVienThanhToan hoặc model ngoài danh sách 00.

Phạm vi truy cập của các phương thức util trong bảng là public static, trừ constructor private và những method được ghi riêng; DAO là instance, kế thừa DAO trừu tượng. Các method map ResultSet đều private và throws SQLException. Chữ ký Servlet helper dùng hai tham số request/response và void; kiểu đầy đủ xem mục 4.3. Mỗi field model có getter/setter theo JavaBean, trừ giá trị dẫn xuất chỉ cần getter và hàm tính. Không thêm public CRUD cho module ngoài phạm vi.

Trong SQL, Employee/Customer dùng tblMember4id làm PK và FK tới tblMember4.id; FK người lập hóa đơn nhập trỏ tblEmployee4.tblMember4id. Chi tiết nhập giữ id PK và UNIQUE(tblPurchaseInvoice4id,tblSparePart4id). Chỉ các FK thuộc 9 bảng trong 03 được triển khai; không tạo bảng khác để hoàn thiện quan hệ ngoài phạm vi.

Không tạo DAO con cho chi tiết nhập chỉ để bám một lớp–một DAO: chi tiết được quản lí trong transaction của PurchaseInvoiceDAO4. Tầng thực thể không thực hiện SQL; cách chuyển phương thức từ thực thể phân tích sang DAO là đúng bước 3 bài giảng.
