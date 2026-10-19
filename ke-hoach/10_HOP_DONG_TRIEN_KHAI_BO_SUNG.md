# Hợp đồng bổ sung: các điểm AI không được tự đoán

Áp dụng cùng 02/03/09. File này đóng các khoảng trống kỹ thuật phát hiện trong lần rà soát 19/10/2026, không thêm module nghiệp vụ. Khi chạm các mục dưới đây, hợp đồng cụ thể ở đây thay câu mô tả khái quát trong 02/03; sửa đổi tiếp theo phải cập nhật đồng thời các tài liệu liên quan.

## 1. Các file bổ sung vào cây 02

```text
app/src/main/webapp/WEB-INF/view/common/steps.jspf
app/src/main/webapp/WEB-INF/view/warehouse/parts-picker.jspf
app/src/main/webapp/WEB-INF/view/warehouse/invoice-lines.jspf
```

Đây là fragment tách từ nguồn 09; không thêm controller, model, bảng hoặc endpoint chỉ để render fragment. Cây 02 đã cập nhật đủ ba file. Tên Java ở bảng 02 là filename có `.java` đúng package tương ứng. Các method tự bổ sung để phân rã là private và ghi vào thiết kế nếu được đưa lên sơ đồ.

`pom.xml`: groupId `vn.edu.ptit`, artifactId `garaman`, version `1.0.0`, packaging `war`, build.finalName `garaman`, compiler.release `17`, encoding UTF-8. Các dependency/plugin đúng 02. README ghi patch Tomcat 9 thực sự đã chạy và output Java/Maven/MySQL thực tế; không coi đọc được POM là đã build thành công.

`app/docs/METHOD_MAP.md` ghi đầy đủ method thực tế theo bảng 02; cả hàm bổ sung ở file này phải có mặt. Đây là đầu vào kiểm tra hình của 4.3.1 và tuần tự về sau, không phải danh sách được phép bịa thêm API.

## 2. Cấu hình DB, web.xml và lỗi

### DB

`src/main/resources/db.properties.example` gồm đúng `db.url`, `db.user`, `db.password`. File local `db.properties` cùng thư mục, bị gitignore; không commit và không bàn giao WAR có mật khẩu thật. Mỗi khóa lấy env tương ứng `GARAMAN_DB_URL/USER/PASSWORD` nếu được đặt, nếu không mới dùng properties; thiếu url/user hoặc không có cấu hình password thì báo lỗi cấu hình, không tự dùng tài khoản root. Password có thể là chuỗi rỗng được cấu hình tường minh, không log.

URL mẫu: `jdbc:mysql://localhost:3306/garaman4?connectionTimeZone=Asia/Ho_Chi_Minh&forceConnectionTimeZoneToSession=true&characterEncoding=UTF-8`. Không tự bật allowPublicKeyRetrieval hay đổi SSL để lấp lỗi kết nối; hướng dẫn cấu hình DB local nếu driver yêu cầu. Trước khi bàn giao, xác minh URL và múi giờ với Connector/J thực chạy. Các mốc DATETIME dùng `LocalDateTime.now(AppConstants4.APP_ZONE).withNano(0)`; ghi/đọc JDBC bằng setObject/getObject(...,LocalDateTime.class), DATE bằng LocalDate. Với một lần xác nhận, ngayNhap và thoiDiemXacNhan dùng **cùng một biến thời điểm**. Không sinh hai thời điểm ở hai tầng khác nhau.

### Mapping

web.xml dùng namespace `http://xmlns.jcp.org/xml/ns/javaee`, schema `web-app_4_0.xsd`, version 4.0. Mapping Servlet bằng các path chính xác trong bảng route 03; không đồng thời dùng @WebServlet. Filter mapping theo thứ tự:

1. EncodingFilter4: `/*`, dispatcher REQUEST và ERROR; set request UTF-8 trước getParameter và response UTF-8.
2. AuthFilter4: `/*`, chỉ REQUEST. Cho GET `/assets/*`, GET `/login` công khai; POST `/login` công khai về quyền nhưng bắt buộc CSRF. `/logout`, `/home`, `/customer/*`, `/warehouse/*` được bảo vệ. GET `/` được welcome chuyển tới home; thiếu login redirect. Không redirect vòng khi ERROR forward sang JSP lỗi.

Các HTTP method khác GET/POST trả 405 (HEAD có thể theo HttpServlet GET chuẩn, không viết nghiệp vụ khác); endpoint sai method trả 405, path lạ trả 404. Mọi POST hợp lệ đều qua kiểm tra CSRF trước khi sửa dữ liệu. Assets chỉ phục vụ nội dung tĩnh, không có POST nghiệp vụ. Không công khai file cấu hình/SQL/docs qua webroot.

`currentUser4` ở session; `currentUser` ở request do Filter đặt từ bản đọc mới nhất; `csrfToken4` session → request `csrfToken`. Filter đọc trạng thái tài khoản/Employee mỗi request bảo vệ; hết quyền thì invalidate và chuyển login, sai vai trò trả 403. Đọc hóa đơn lịch sử không lọc theo tình trạng làm việc của **người lập cũ**, nhưng người đang truy cập phải hoạt động.

### Error và ValidationException4

File `ValidationException4` giữ hai constructor/getField ở 02. Quy ước field: `quyen`→403; `khongTonTai`→404; `xungDot`→409; `heThong` chỉ dùng lỗi cấu hình do ứng dụng phát hiện→500; các field input như `soLuong_<id>`, `donGiaNhap_<id>`, `maYeuCau`, `soTienDaTra`→lỗi form 400. Input id sai cú pháp trả400, đúng cú pháp nhưng không có bản ghi trả404. SQL exception luôn→500 có log, không gán thành lỗi không có kết quả.

Entry point doGet/doPost xử lý exception một lần: ValidationException→status tương ứng, đặt fieldErrors/errorMessage và dữ liệu form rồi forward đúng view; SQLException→log có mã sự kiện, gửi 500; ServletException/IOException theo API. Lỗi không có form phù hợp dùng common/error.jsp. Tuyệt đối không trả stacktrace/SQL/hash/password cho trình duyệt. web.xml có error-page 400/403/404/409/500; thông điệp escape HTML.

Các helper Servlet tên ở 02 là `private void tenHam(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException, ValidationException4`. Override init/doGet/doPost tuân thủ throws của Servlet API; không thêm SQLException vào override. DAO không đổi SQL lỗi thành false/null.

## 3. Chữ ký util/DTO cần thực hiện đúng

Giữ các chữ ký ở 02. Bổ sung/khóa phần chưa cụ thể:

| Lớp | Chữ ký và hành vi |
|---|---|
| WebUtil4 | `public static void forward(HttpServletRequest req,HttpServletResponse res,String view) throws ServletException,IOException`; view là đường `/WEB-INF/view/...` do code chọn |
| WebUtil4 | `public static void redirect(HttpServletRequest req,HttpServletResponse res,String path) throws IOException`; path bắt đầu `/`, chỉ route nội bộ do code tạo, res.sendRedirect(req.getContextPath()+path); query đã URL-encode UTF-8 khi ghép giá trị |
| WebUtil4 | `public static SessionUser4 requireUser(HttpServletRequest req) throws ValidationException4`; thiếu session→field quyen, không tạo user giả |
| WebUtil4 | `public static String taoCsrfToken(HttpSession session)`; 32 byte SecureRandom, Base64 URL-safe không padding, đặt csrfToken4; trả token hiện có nếu hợp lệ |
| WebUtil4 | `public static boolean hopLeCsrf(HttpServletRequest req)`; getSession(false), thiếu/sai→false; so sánh token constant-time |
| WebUtil4 | `public static void flash(HttpServletRequest req,String message)`; `public static String layFlash(HttpServletRequest req)` đọc rồi xóa flashMessage4 |
| WebUtil4 | `public static String dinhDangThoiDiem(LocalDateTime value)`; NULL→`Chưa ghi nhận`, khác NULL→`dd/MM/yyyy HH:mm:ss`; không thay timezone trên LocalDateTime |
| SessionUser4 | `public SessionUser4(int id,String hoTen,String vaiTro,String viTri)`; field private final; getters; `public static SessionUser4 fromMember(Member4 member)`; Customer viTri=NULL, không matKhau |
| PasswordUtil4 | Các hàm public static ở 02 không checked exception; lỗi thuật toán platform→IllegalStateException. PBKDF2WithHmacSHA256, iteration 600000, salt 16 byte, derived key 32 byte; lưu `pbkdf2_sha256$600000$<salt-base64>$<key-base64>`; mỗi hash salt riêng. Hash sai định dạng/trường vượt giới hạn→verify false, không crash login. Chưa có tính năng đổi password/đăng ký |
| Validation4 | Các hàm public static ở 02 đều `throws ValidationException4` khi input sai; `tuyChon` trống→NULL; `batBuoc` trống→lỗi field, không trả chuỗi rỗng. `kiemTraDienThoai` chỉ kiểm tra String đã chuẩn hóa; Servlet bỏ khoảng trắng và `-` trước gọi; lưu String, không parse số |
| DraftStore4 | Các hàm quản lý ở 02 là public static; thao tác UUID thiếu/không thuộc session→field khongTonTai; lỗi UUID cú pháp→maYeuCau; hết 10 draft→xungDot. `saoChep(PurchaseInvoice4)` deep-copy mọi object/list mutable, không chia sẻ dòng/master với bản đang sửa |

`DraftStore4` thêm các hàm public static, đều ném ValidationException4 trừ hàm kết thúc đã ghi:

```java
long layPhienBan(HttpSession session, String maYeuCau)
void kiemTraPhienBan(HttpSession session, String maYeuCau, long phienBan)
long tangPhienBan(HttpSession session, String maYeuCau)
PurchaseInvoice4 batDauXacNhan(HttpSession session, String maYeuCau, long phienBan)
void ketThucXacNhan(HttpSession session, String maYeuCau) // không checked exception
```

Các hàm DraftStore đồng bộ ngắn theo session; Servlet vẫn giữ **toàn bộ** đọc→kiểm tra version→sửa→ghi→tăng version trong cùng synchronized(session), không chỉ từng get/set. `batDauXacNhan` kiểm tra version/trạng thái, đặt cờ đang xác nhận và trả deep-copy. `ketThucXacNhan` chỉ bỏ cờ, an toàn nếu draft đã bị xóa hoặc session đã invalidate; không phục hồi draft. Không đưa DAO, Connection hoặc HttpServletRequest vào model.

Quy tắc model: không có field lưu `tongTien`/`thanhTien`; giữ `getTongTien()/tinhTongTien()` và `getThanhTien()/tinhThanhTien()` tính lại. Các object FK được nêu trong 03; **không tạo** field invoice cha trên dòng con hoặc intake cha trên RepairService/RepairPart vì cha đã chứa List. MODEL_FIELD_MAP ghi đó là FK vật lý được truyền khi INSERT, không cố tạo cả object/id cha cho “đủ mọi cột”. Mọi cột còn lại đều có field hoặc ánh xạ id kế thừa. DTO không dùng làm thực thể CSDL.

## 4. Bản nháp: version và đồng thời

Thêm session keys kỹ thuật, không thêm cột DB/model:

| Key | Kiểu | Khởi tạo/xóa |
|---|---|---|
| draftVersions4 | Map<String,Long> | Draft mới version 0; tăng sau mỗi POST sửa draft; xóa cùng draft |
| draftConfirming4 | Set<String> | UUID đang commit; thêm/bỏ trong vùng khóa ngắn |

Request `draftVersion` là số nguyên không âm, bắt buộc cho search-part/add-line/update/remove-line/cancel/confirm và parts/create. GET draft/parts/new trả request attribute `draftVersion:long`. Hidden version là dữ liệu trao đổi kỹ thuật; server đối chiếu phiên bản thật, không tin giá trị này tự thân.

1. POST thường: trong khóa session kiểm tra UUID, owner, version và chưa confirming; áp input hiện tại, giữ raw input lỗi; tăng version, lưu rồi lấy copy. Gọi DB tìm danh mục ngoài khóa; redirect GET dùng bản mới. Hai form cùng version chỉ một được ghi; form còn lại 409, giữ input request để người dùng xem và tải lại, không tự đè dữ liệu mới.
2. Xác nhận: trước hết tìm id theo maYeuCau + user hiện tại trong DB. Có rồi→redirect id cũ, không đòi draft/version/input cũ hợp lệ. Chưa có→trong khóa session kiểm tra version, cập nhật toàn bộ input dòng; input sai lưu raw, tăng version và trả lỗi; input đúng gọi batDauXacNhan lấy bản copy và đặt cờ. Ra khỏi khóa rồi gọi DAO transaction. Commit thành công→xóa draft/raw/version/cờ, redirect view. Lỗi→bỏ cờ, giữ draft/input cho sửa; lỗi DB vẫn báo500, không tự đổi sang thành công.
3. Khi đang confirming: sửa/xóa/hủy/thêm part bị409; confirm đồng thời nếu DB chưa có kết quả cũng409 “đang xử lý, tải lại/thử lại”. Sau commit, retry cùng UUID nhận id cũ. Có thể trả 1 redirect thành công + 1 phản hồi409 trong lần gửi đồng thời, nhưng số chứng từ/tồn đúng một lần. P07 phải kiểm tra tiếp retry, không đòi cả hai response đều tạo mới.
4. Create part: kiểm tra draft/version trước khi gọi DAO. Trong lúc DB tạo danh mục ngoài khóa, confirm có thể hoàn tất; nếu về sau không còn draft, vẫn giữ part mới đã tạo, thông báo đã tạo danh mục và cho trở về home/chọn lại, không tái tạo draft hay thêm dòng vào hóa đơn đã commit. Không rollback hồ sơ danh mục vì draft bị hủy sau đó. Khi draft còn sống, redirect về nó và chỉ highlight part mới, không tự thêm dòng.
5. Đăng xuất trong lúc confirm: chứng từ có thể đã commit; sau đó không tái tạo session/draft. User đăng nhập lại mở danh sách chưa trả. Client phải đăng nhập mới retry được; không coi session mất là rollback một transaction đã commit.

Kiểm tra draft thô: mỗi id có trong draft phải xuất hiện **đúng một** `partId` trong form cập nhật/xác nhận. Thiếu dòng/duplicate id/input name nhiều giá trị→400, không âm thầm bỏ dòng hoặc chọn giá trị đầu. Id mới không được lén chèn qua mảng; add-line đọc part từ DAO. Remove cho phép bỏ dòng lỗi đích nhưng giữ input/lỗi các dòng khác. Cancel không đọc/validate ô tiền/lượng.

PRG giữ raw theo UUID. Request `fieldErrors` được **tính lại từ raw** mỗi GET, không cần lưu riêng một map lỗi nữa. Dữ liệu hợp lệ một dòng mới 0/0 được giữ ở draft nhưng chưa được confirm. Khi tìm/chọn/đi form mới, 0/0 và input lỗi không cản điều hướng; confirm luôn validate toàn bộ.

## 5. Transaction, quyền và retry

`luuHoaDonNhap` chỉ nhận UUID chuẩn do server sinh. Query id theo mã phải kiểm tra owner; mã đã thuộc người khác→quyen403, không mở chứng từ hay tạo mới. `timIdTheoMaYeuCau` phải phân biệt không có mã với có mã nhưng khác người lập; không chỉ `WHERE maYeuCau=? AND employee=?` rồi coi khác chủ là không có.

Đặt tên UNIQUE mã yêu cầu `uk_purchase_request`; UNIQUE dòng nhập `uk_purchase_part`. Chỉ nhánh lỗi MySQL 1062 của uk_purchase_request mới rollback rồi mở connection mới tìm lại mã/owner và trả id cũ. 1062 ở constraint khác, deadlock, timeout, mất kết nối và CHECK violation không được báo “đã lưu”. Deadlock/timeout rollback và cho retry cùng UUID; không tự sinh mã nhập mới. Nếu commit trả lỗi không xác định đã thành công hay chưa, báo chưa xác định và cho retry cùng mã; DB sẽ phân giải, không cộng bù tồn.

Trong transaction, khóa theo 02/03, đồng thời đọc Member liên quan để kiểm tra tài khoản người đang thao tác, không chỉ Employee. Chốt snapshot **sau** đọc dữ liệu đã khóa. Part đọc theo thứ tự id tăng dần; map dòng hiển thị vẫn theo thứ tự người nhập, không tự đổi thứ tự UI vì thứ tự khóa. Tạo part mới: kiểm tra nhóm đang dùng trong cùng transaction với INSERT, khóa nhóm trước đọc/ghi; không gọi DAO nhóm mở connection khác rồi tưởng đã bảo đảm nhất quán. Supplier mới luôn trạng thái đang hoạt động server gán.

Không dùng Inventory DAO ngoài transaction để lấy tồn rồi set lại một giá trị tuyệt đối. Tăng tồn bằng `tonKho=tonKho+?`, kiểm tra kết quả và phạm vi trước commit. Trong DAO không lấy Connection từ field static.

Pay có thứ tự kết quả cố định: không có hóa đơn→404; người gọi không phải người lập/không hoạt động→403; đúng người và đã thanh toán→false, giữ thời điểm **dù amount retry khác nhưng vẫn là số hợp lệ**; đúng người/chưa trả→kiểm tra amount bằng tổng, sai400; đúng→UPDATE và true. Input amount không parse được vẫn400 tại Servlet trước DAO. Không cập nhật lại thời điểm khi trả false. Không yêu cầu browser chứng minh đã in thành công mới pay; in là bước nghiệp vụ của người dùng, window.afterprint không thể xác minh giấy thực tế.

View/print hóa đơn cũ nạp người lập và part kể cả khi master ngừng hoạt động. Các field tên/đơn vị áp dụng dùng snapshot; mọi JOIN liên kết không được lọc mất header/dòng. Không gửi matKhau vào request; nếu object Member map có hash thì không gắn object đó nguyên vẹn lên view.

## 6. Số, thời gian và JSP

| Dữ liệu | Miền chính thức server |
|---|---|
| Lượng | 0 < qty <= 999999999.999, tối đa scale3; Cái/Chiếc/Bộ có phần thập phân bằng0, Lít được lẻ |
| Giá nhập | 0 < price <= 9999999999999999.99, scale2 |
| Giá bán/dịch vụ | >=0 trong cùng miền tiền; giá bán NULL chỉ khi chưa công bố |
| Thành tiền/tổng | Tính mỗi dòng HALF_UP scale2 rồi cộng, tổng <=9999999999999999.99; vượt→lỗi, không làm tràn DB |
| Chuỗi số | Trim; regex `[0-9]+(?:\.[0-9]+)?`; không exponent/dấu phẩy/phân nhóm/+/-; quá scale bị lỗi, kể cả chữ số dư là0 |
| Dòng mới | qty=0 và price=0 trong nháp để nhập tiếp; lưu chính thức vẫn phải >0 |

`BigDecimal` khởi tạo từ String. Khi xác minh đơn vị đếm dùng stripTrailingZeros().scale()<=0 sau parse; `10.000` hợp lệ vì scale không quá3 và giá trị nguyên. So sánh tiền dùng compareTo, không dùng equals để so scale.

JSP dùng `<fmt:setLocale value="vi_VN"/>` và `<fmt:formatNumber>` cho BigDecimal. `fmt:formatDate` **không nhận trực tiếp LocalDateTime/LocalDate**. Controller đặt String request attributes qua WebUtil4.dinhDangThoiDiem: `ngayNhapHienThi`, `thoiDiemXacNhanHienThi`, `thoiDiemThanhToanHienThi` cho draft/view/print; JSP escape các String này. List unpaid đặt map `ngayNhapHienThiTheoId:Map<Integer,String>` để render từng dòng; không thêm field SQL hoặc String trùng ngày vào model.

`buocNhap` request: chọn/thêm NCC=1; draft/thêm part=2; confirmed=3; print=4. `dinhDangThoiDiem` có thể áp cho ngày nhập dự kiến; ngày chính thức đọc DB, không tự Date.now khi load JSP.

Các chuỗi plain text dùng c:out trong text và attribute. Ảnh chỉ allowlist 2 đường ở 09 và NULL→placeholder. Không nạp URL ảnh trực tiếp từ input hoặc bịa bảo hành/hãng/dòng xe. Bỏ bộ lọc loại phụ trong prototype theo 09 để app chỉ nhận params đã chốt trong 03.

## 7. SQL so với hình và điều kiện nhận bàn giao

Hình 2.2 dùng VARCHAR(255) ở nhiều mô tả/địa chỉ; 03 chọn TEXT (vẫn giới hạn input 5000), phone20, username100, trạng thái30, đơn vị20 và thoiGian DATETIME. Đây là **điều chỉnh vật lý có chủ đích**, không được nói “SQL giống nguyên từng kiểu trong hình”. Khi code: bảng ánh xạ liệt kê từng khác biệt → cập nhật ERD bản sao hoặc ghi chú đầy đủ dưới hình/từ điển; tên field/NULL/FK/ý nghĩa không đổi. Hình gốc giữ nhãn date được theo 01. tonKho trong hình đã decimal(12,3), không sửa lại thành integer.

DEFAULT/CHECK/UNIQUE theo 03 là ràng buộc triển khai; NN không kiểm tra được chuỗi rỗng nên server vẫn validate. Các trạng thái và vaiTro phải dùng đúng chữ hoa/dấu; AppConstants4 tập trung hằng, DAO dùng PreparedStatement. Không tự suy ra các bảng thêm từ một CHECK liên bảng không thực hiện được.

Bổ sung nghiệm thu vào nhóm kỹ thuật hiện có, không cần tạo thêm test class chỉ để đủ danh sách:

- P07: 2 confirm cùng UUID, retry sau409/mất response/mất draft→cùng id, đúng một lần tăng tồn; khác owner→403.
- P10: hai UUID độc lập; hai tab **cùng UUID cùng version**→một bản cập nhật, một409; sửa/hủy khi đang confirm→409; input lỗi không bị thay bằng0/giá trị cũ.
- P12: người khác đã biết id không pay được kể cả hóa đơn đã trả; người lập retry giữ thời điểm; số tiền sai khi chưa trả vẫn lỗi.
- P02/P15: không công bố part thiếu mô tả/giá; hãng và enum đơn vị đúng; nhóm bị ngừng giữa mở form và lưu không được tạo mới; ảnh ngoài allowlist bị chặn.
- UI: không có hãng/liên hệ NCC sau xác nhận; draft vẫn có thông tin đối chiếu; ngày LocalDateTime render được; không còn HTML HN1/sample fallback hoặc sessionStorage điều khiển app.

Chưa code/test backend thì ghi chưa chạy. Không dùng kết quả test prototype để chứng minh transaction/quyền/CSRF/tồn đúng. Tham chiếu kỹ thuật đã kiểm tra: [Apache Tomcat versions](https://tomcat.apache.org/whichversion.html), [migration javax→jakarta](https://tomcat.apache.org/migration-10.html), [MySQL locking reads](https://dev.mysql.com/doc/refman/8.4/en/innodb-locking-reads.html), [Java LocalDateTime](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/time/LocalDateTime.html).
