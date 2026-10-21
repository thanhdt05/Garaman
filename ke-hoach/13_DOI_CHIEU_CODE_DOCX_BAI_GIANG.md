# Rà soát mã nguồn với DOCX gốc và bài giảng — 21/10/2026

> Đây là kết quả trước sửa. Trạng thái sau yêu cầu sửa code và phạm vi loại trừ bảo mật của chủ bài được ghi tại [14_KET_QUA_SUA_KHOP_THIET_KE.md](14_KET_QUA_SUA_KHOP_THIET_KE.md).

**Kết luận: chưa đủ căn cứ chốt “đã khớp đầy đủ và sẵn sàng dùng nguyên trạng để viết thiết kế chi tiết”.** Khung triển khai hai module phù hợp; các quan hệ thực thể cốt lõi và ánh xạ SQL nhìn chung đúng. Còn lỗi giao diện thanh toán, sai lệch tài liệu/sơ đồ so với code, lời văn DOCX chưa đồng nhất với quyết định triển khai, và thiếu nghiệm thu JSP/MySQL thực tế.

Đây là kết quả **kiểm tra**, không phải biên bản đã sửa hết lỗi. Lần này chỉ bổ sung tài liệu rà soát; không sửa mã nghiệp vụ, schema, DOCX/PDF gốc hay bản sao Word do AI tạo.

## 1. Nguồn và giới hạn kết luận

- Đọc `README`, `00`, `01`, `07`, `11`, `12` trong `ke-hoach`; đối chiếu mã Java, JSP/JSPF, JS, SQL, POM, các tài liệu/sơ đồ và test hiện có của `app/`.
- Đọc trực tiếp nội dung trích xuất từ `26_B23DCCN764-full.docx`, xem các ảnh nhúng: Hình 1.2/1.3 (UC), 1.4 (tổng quan thực thể), 1.5/1.6 (lớp phân tích), 1.7/1.10 (trạng thái), 1.8/1.11 (giao tiếp), 2.1 (thiết kế thực thể), 2.2 (CSDL).
- Đọc bài giảng mục 4.3.1, trang in 96–103, tương ứng trang PDF 97–104; xem hình mẫu 4.3/4.4 tại trang PDF 99/100. Mẫu giao diện có form/bảng/nút và mũi tên giữa màn hình; mẫu lớp có tầng giao diện, DAO, thực thể và chữ ký hàm.
- Kiểm tra SHA-256 của cả bốn nguồn gốc: đều trùng manifest dùng để trích xuất trong `.review/manifest.json`. DOCX chính: `d29fe3d0d462ff6a868dc951ec039389ee0da1213097ed4f3b9e6ee09dcb984f`; PDF bài giảng: `416f0401a70297300419d3abe6a72c4d3d816635e4f58c4b6cc5cd52a865d389`.
- Mã `[Pxxxx]` dưới đây là số đoạn XML từ công cụ trích nguồn, **không phải số trang Word**. Có thể tìm bằng tiêu đề hình hoặc câu trích trong bản gốc.
- Phạm vi code vẫn là **hai module, 10 model, 9 bảng** theo `00`. Không thiếu chức năng chỉ vì app chưa cài đặt toàn bộ mô hình tổng quan.

## 2. Kết quả theo từng nội dung người dùng yêu cầu

| Nội dung | Căn cứ trong DOCX gốc | Đối chiếu mã thực tế | Kết luận |
|---|---|---|---|
| Use case tra cứu | Hình 1.2; P0410–P0420; kịch bản P0458–P0518 | Đăng nhập hỗ trợ; `/customer/catalog` tìm theo tên; `/detail` phân biệt `service`/`part`; có xem chi tiết và quay lại | Khớp chức năng chính ở mức source. Tìm rỗng được chặn trước DAO; chỉ đọc danh mục đang công bố. Chưa nghiệm thu E2E |
| Use case nhập | Hình 1.3; P0425–P0437; P0520–P0634 | Tìm/chọn/thêm NCC; tìm/chọn/thêm part trên nháp; xác nhận; in; ghi nhận trả đủ tiền | Đủ các thao tác chính ở mức source. Nhà cung cấp là actor bên ngoài, không cần tạo tài khoản/Servlet cho nhà cung cấp |
| Kịch bản và ngoại lệ | Mục 1.2.1.a/b | Trang tìm ban đầu trống; thêm mới quay về danh sách; dòng mới 0/0; chọn trùng focus; hủy nháp; chống xác nhận trùng; in chỉ đọc | Phần lớn đã có xử lý. Còn R01/R03/R07 và các khác biệt tại mục 3; chưa được coi là đã chạy đúng |
| Sơ đồ lớp thực thể toàn hệ thống — tổng quan | Hình 1.4, P0708 | Các thực thể trong phạm vi đều có: `Member4`, `Employee4`, `WarehouseStaff4`, `Customer4`, `Service4`, `SparePartCategory4`, `SparePart4`, `Supplier4`, `PurchaseInvoice4`, `PurchaseInvoiceDetail4` | Khớp tập con liên quan. Không cần tạo xe/lịch hẹn/tiếp nhận/hóa đơn khách/thống kê để “đủ hình” |
| Lớp phân tích hai module | Hình 1.5/1.6, P0742/P0842 | Ba biên tra cứu và bảy biên nhập đều có JSP tương ứng; thao tác dữ liệu chuyển sang DAO; tính tiền nằm trong model | Cách chuyển pha phân tích → thiết kế đúng hướng bài giảng. Riêng `layThongTinNhanVienKho` có lớp/hàm nhưng chưa được gọi trong luồng hiện tại, xem R05 |
| Chuyển trạng thái | Hình 1.7/1.10, P0851/P0895 | Tra cứu: chính → tìm → chi tiết → tìm. Nhập: chính → NCC ↔ thêm NCC → nháp ↔ thêm part → đã xác nhận ↔ in → ghi nhận trả tiền | Luồng màn hình chính khớp ở mức source. Không có màn duyệt trước xác nhận phát sinh. Chưa xác minh luồng lỗi bằng Tomcat |
| Giao tiếp | Hình 1.8/1.11, P0884/P0962 | Tên thao tác tìm/lấy/lưu/tăng tồn/ghi nhận thanh toán đều có ở DAO/model tương ứng | Khớp phần lớn ý nghĩa. Việc chuyển hàm truy vấn từ entity phân tích sang DAO là hợp lệ; không cần nhét JDBC vào entity. Chưa thể dùng sơ đồ thiết kế bổ sung nguyên trạng vì R04/R05 |
| Thiết kế lớp thực thể | Hình 2.1, P0975 | Kế thừa đúng; hóa đơn giữ `Supplier4`, `WarehouseStaff4`, `List<PurchaseInvoiceDetail4>`; dòng giữ `SparePart4`; part giữ `SparePartCategory4`; có các trường chốt lịch sử | Quan hệ và dữ liệu chính phù hợp. `float` → `BigDecimal`, `Date` → `LocalDate`/`LocalDateTime`, mảng → `List` là tinh chỉnh triển khai đã có bảng ánh xạ, không phải lỗi nghiệp vụ |
| Thiết kế CSDL | Mục 2.2, Hình 2.2, P0976–P1092 | Đúng 9 bảng, tên camelCase/hậu tố 4; kế thừa bằng PK/FK; FK nhân viên về `tblEmployee4.tblMember4id`; không lưu `tongTien`/`thanhTien`; có snapshot, UNIQUE UUID và cặp hóa đơn–part | Schema và SQL DAO nhất quán ở mức source. Lời văn bỏ `tonKho` mâu thuẫn ngay với hình gốc; xem mục 3. Chưa thực thi schema/transaction trên MySQL 8.4 |
| Thiết kế chi tiết theo bài giảng | Mục 4.3.1 bài giảng; Hình 4.3–4.8 | Có bản Markdown bốn bước, có nguồn PlantUML và tổ chức JSP–Servlet–DAO–model | **Chưa đạt bản bàn giao hoàn chỉnh**: tài liệu chứa lớp/hàm/cột không có thật, sơ đồ chưa đồng bộ, thiếu bốn hình xuất sẵn chèn, ảnh hiện có là ảnh prototype |

Không thể suy ra “đạt toàn bộ” chỉ từ việc có đủ tên lớp hay WAR build được.

## 3. Những chỗ DOCX gốc khác quyết định triển khai đã chốt

Các điểm này đã được ghi trong `ke-hoach/01_DANH_GIA.md`. Không tự đảo quyết định cũ, thêm người thanh toán, hoặc sửa nguồn gốc trong lần rà soát này.

| Điểm | DOCX gốc | App/quyết định đã chốt | Cách trình bày khi làm tiếp |
|---|---|---|---|
| Tồn kho | P0595/P0813 mô tả tồn tính từ giao dịch; P1087 liệt kê `tonKho` trong thuộc tính dẫn xuất bị loại. Nhưng Hình 2.2 **vẫn có** `tonKho decimal(12,3)`; Hình 1.6/1.11 vẫn có `tangTonSauNhap()` | Lưu `tonKho`, cộng tồn trong transaction xác nhận | Nêu rõ chủ đích giữ tồn để tra cứu nhanh; tổng tiền/thành tiền vẫn dẫn xuất. Không nói bài giảng bắt buộc lưu tồn, cũng không nói nguyên văn gốc đã hoàn toàn thống nhất |
| Người trả tiền | P0624 nói lưu nhân viên thực sự trả tiền, giữ người lập; P0634 cho nhân viên có quyền ở ca hiện tại xử lý hóa đơn chưa trả của gara. Các hình thực thể/CSDL chỉ có một quan hệ nhân viên lập | Chỉ người lập ghi nhận trả đủ; danh sách chưa trả chỉ của mình; không có FK người trả riêng | Dùng đoạn thay thế đã chốt tại `01`, ghi rõ khác biệt trong nội dung thiết kế hai module. Không âm thầm coi bản Word AI sửa là bản gốc |
| Kiểu dữ liệu | Hình 2.1 dùng `float`, `Date`, mảng; Hình 2.2 dùng nhãn `date` | `BigDecimal`/`DECIMAL`, `LocalDateTime`/`DATETIME`, `LocalDate`/`DATE`, `List` | Thêm bảng ánh xạ/ghi chú tinh chỉnh kỹ thuật. Không đổi quan hệ nghiệp vụ vì đổi kiểu |
| Bảng vai trò | P1089 bỏ bảng vai trò; bước FK trước đó còn tên bảng vai trò | Không tạo bảng `tblWarehouseStaff4`; FK hóa đơn trỏ bảng nhân viên | Giải thích việc chuyển FK sau tối ưu. Hình 2.2 đã thể hiện FK nhân viên |
| Bảng dòng sau xác nhận | Bảng ví dụ bước 13 trong gốc còn cột Hãng; P0620 loại Hãng khỏi bản in | `11` đã khóa cả chứng từ đã xác nhận/bản in dùng sáu cột từ dữ liệu đã chốt; chỉ nháp có Hãng | Nếu mô tả phiên bản triển khai, dùng đúng sáu cột đã chốt và ghi rõ sự thống nhất này; không thêm snapshot hãng/bảng mới |

## 4. Các vấn đề cần sửa trước khi chốt thiết kế

### R01 — Ô số tiền trả dùng định dạng không hợp lệ cho HTML number

**Ưu tiên cao, lỗi source và thành phần HTML đã kiểm chứng riêng.**

- `app/src/main/webapp/WEB-INF/view/warehouse/gdHoaDonNhapDaXacNhan.jsp:4` đặt locale `vi_VN`; dòng 99–100 ghi `<fmt:formatNumber ... pattern='0.00'/>` vào `value` của `input type="number"`.
- Với tổng 800000, định dạng theo locale này là `800000,00`. Kiểm tra độc lập bằng Java `DecimalFormat` cho ra đúng chuỗi đó; Microsoft Edge bỏ giá trị này thành chuỗi rỗng trong HTML number. `800000.00` được giữ nguyên.
- Hệ quả: số tiền mặc định không hiện đúng; bấm thanh toán có thể gửi số rỗng và nhận 400. Người dùng vẫn có thể tự nhập lại số hợp lệ; đây không phải khẳng định mọi lần thanh toán đều thất bại.
- Sửa ngay trong JSP hiện có: giá trị máy đọc dùng số có dấu chấm; định dạng tiền tiếng Việt chỉ dùng cho phần hiển thị. Giữ ô nhập và nút hiện có. Trạng thái ban đầu của ô cũng cần theo mẫu đã khóa (prototype dùng 0), không tự suy từ tổng rằng tiền đã trả.
- Kiểm tra đã chạy **không phải** compile/render JSP bằng Tomcat; cần kiểm tra lại sau triển khai thật.

### R02 — Tài liệu 4.3.1 và ma trận truy vết chứa tên không tồn tại

**Ưu tiên cao đối với báo cáo.** Nếu chép nguyên tài liệu này, phần thiết kế chi tiết sẽ không truy được về code/CSDL.

| Chỗ ghi hiện nay | Vị trí | Đúng theo source |
|---|---|---|
| `PurchaseInvoiceDraftDTO4` | `app/docs/4.3.1_THIET_KE_TINH.md:82` | Nháp là `PurchaseInvoice4`, lưu bởi `DraftStore4`; không có DTO này |
| `khoaDuLieu` | Cùng file, dòng 131 | Các câu `SELECT ... FOR UPDATE` nằm trong `PurchaseInvoiceDAO4.luuHoaDonNhap`; không có hàm tên này |
| `layDanhSachChuaThanhToanTheoNguoiLap` | Cùng file, dòng 134; `MA_TRAN_TRUY_VET.md:24` | `PurchaseInvoiceDAO4.layHoaDonChuaThanhToan(int)` |
| `giaTienCong` | `MA_TRAN_TRUY_VET.md:12` | `tblService4.donGia` |
| `maSoThue`, `sdt` | Cùng file, dòng 15–16 | Không có `maSoThue`; số điện thoại là `dienThoai` |
| `maPhuTung`, `donViTinh` | Cùng file, dòng 17 | ID là `id`; đơn vị là `donVi` |
| `maHoaDon`, `tongTien` là cột hóa đơn | Cùng file, dòng 20 | ID là `id`; `tongTien` tính từ chi tiết, không lưu cột |
| `tenApDung` | Cùng file, dòng 21 | `tenPhuTungApDung` |
| `trangThaiThanhToan`, `ngayThanhToan` | Cùng file, dòng 22/24 | `tinhTrang`, `thoiDiemThanhToan` |
| `tblWarehouseStaff4tblMember4id` | Cùng file, dòng 24 | `tblEmployee4id` tham chiếu `tblEmployee4.tblMember4id` |
| Mục kịch bản `1.3.1`/`1.3.2` | Ma trận dòng 11–24 | DOCX hiện tại là `1.2.1.a/b` |
| `DANG_HOAT_DONG`, `DANG_BAN` là giá trị lưu | Bản 4.3.1 dòng 56–57 | `Đang cung cấp`, `Đang kinh doanh` theo `AppConstants4` và SQL |

Nội dung còn mô tả hai bảng kết quả thay vì một bảng chung (dòng 24–25), tìm phụ tùng theo mã thay vì chỉ tên (dòng 84), thêm ren M20x1.5 không có trong ví dụ gốc (dòng 27), hủy quay về chọn NCC trong khi source quay về `/home` (dòng 88). Bảng form thêm part dòng 112 còn thiếu `draftVersion` và nhiều trường thực tế.

Sửa tài liệu theo mã/hợp đồng hiện có; **không thêm lớp, hàm, cột hoặc nghiệp vụ để làm cho các tên sai thành đúng**.

### R03 — Khóa tài khoản sau đăng nhập chưa chặn được phiên thanh toán đang mở

**Ưu tiên cao trước nghiệm thu quyền thanh toán.**

- `AuthFilter4.java:44` đọc `SessionUser4` đã cache; dòng 70–98 chỉ kiểm tra session, vai trò, CSRF, không đọc trạng thái tài khoản hiện tại.
- `MemberDAO4.layThanhVienDangHoatDong` có sẵn nhưng không có chỗ gọi trong `src/main/java`. `WarehouseStaffDAO4.layThongTinNhanVienKho` cũng chưa được gọi.
- `PurchaseInvoiceDAO4.ghiNhanThanhToan` tại dòng 239 kiểm tra ID người lập, trạng thái hóa đơn và tổng tiền; không kiểm tra tài khoản/người lao động còn hoạt động. Vì vậy người đã đăng nhập trước khi bị khóa/ngừng làm việc vẫn có đường ghi nhận trả tiền qua phiên cũ.
- Tạo phiên cache và gọi filter giả lập đã xác nhận filter cho POST pay đi tiếp mà không có bước đọc DB. Chưa chạy tình huống khóa tài khoản trên MySQL thật.
- Khi sửa, dùng các lớp/phương thức hiện có để kiểm tra trạng thái còn hiệu lực; không thêm use case hoặc route quản trị tài khoản. Cần chứng minh cả tài khoản bị khóa trước đăng nhập lẫn bị khóa khi phiên đang mở.

### R04 — Sơ đồ bổ sung chưa phản ánh đúng mã nên chưa thể dùng làm mẫu thiết kế chi tiết

- `app/docs/diagrams/tuan_tu_tra_cuu.puml:55` gửi `loai=spare-part`; `CatalogServlet4` chỉ nhận `service` hoặc `part`. Gọi trực tiếp hàm `xemChiTiet` với URL trong sơ đồ đã xác nhận trả **400**. Sửa nhãn tham số thành `part`, bảo toàn `boLoc` khi đi/quay lại.
- `purchase-design.puml:157` và bản `lop_thiet_ke_nhap_phu_tung.puml` ghi các field `Member4` là protected (`#`), trong khi Java là private (`-`).
- Hai hình lớp nhập bỏ `tinhTongTien()`/`tinhThanhTien()` đang được code gọi, chỉ ghi getter. Cần hiện các phương thức tính nghiệp vụ làm căn cứ cho hoạt động/tuần tự; getter là cách JSP đọc thuộc tính dẫn xuất.
- `lop_thiet_ke_tong_the.puml:73` đặt bội số phía `PurchaseInvoice4` là `*` trong quan hệ sở hữu dòng; mỗi dòng thuộc một hóa đơn nên phía hóa đơn phải là `1`. Nháp có thể 0 dòng; chứng từ đã xác nhận phải có ít nhất 1 dòng, nên ghi rõ phạm vi hoặc ràng buộc trạng thái.
- `lop_thiet_ke_tong_the.puml:81/90` vẽ `HomeServlet4 → MemberDAO4`, `PurchaseInvoiceServlet4 → WarehouseStaffDAO4`; code hiện chưa có các lời gọi này.
- Hình lớp nhập thiếu dependency thực tế `PurchaseInvoiceDAO4 → SparePartDAO4` cho `tangTonSauNhap`, trong khi hình tổng thể có. `METHOD_MAP.md` cũng bỏ sót hàm package-private này.
- `tuan_tu_nhap_phu_tung.puml:50` đặt `chenChiTiet(..., List)` trong loop từng dòng; code gọi **một lần** để batch tất cả chi tiết, sau đó mới loop cộng tồn. Cần mô tả đúng lần gọi nếu dùng làm thiết kế hoạt động.

Không yêu cầu sửa các hình pha phân tích thành hình Servlet/DAO: hai pha có mức chi tiết khác nhau. Các nhận xét trên áp dụng cho **sơ đồ thiết kế bổ sung trong app**, nơi đã tự nhận mô tả code.

### R05 — Thao tác lấy nhân viên kho trong phân tích/giao tiếp chưa có lời gọi thực thi

- DOCX P0778–P0782 và thông điệp 14–16 Hình 1.11: lấy thông tin nhân viên kho đang đăng nhập.
- Có `WarehouseStaffDAO4.layThongTinNhanVienKho(int)` nhưng không được dùng. `PurchaseInvoiceServlet4.batDauNhap` dòng 125–127 chỉ dựng `WarehouseStaff4` từ ID/tên cache của session; không nạp trạng thái nhân viên hiện tại bằng DAO như tài liệu 4.3.1 mô tả.
- `luuHoaDonNhap` có kiểm tra lại trạng thái tài khoản/làm việc khi xác nhận, nên không thể kết luận bước lưu hoàn toàn bỏ kiểm tra nhân viên. Tuy nhiên sơ đồ giao tiếp → bảng phương thức → lời gọi code hiện chưa xuyên suốt.
- Cần nối lời gọi hiện có vào đúng bước lấy nhân viên và kiểm tra kết quả; ghi input/output đúng như phân tích. Không tạo DAO nghiệp vụ mới.

### R06 — Ảnh hiện có không phải bằng chứng giao diện backend

**Ưu tiên cao đối với bằng chứng và hình báo cáo.**

- So SHA-256 từng file: **18/18 PNG** trong `app/docs/screenshots/` giống hệt file cùng tên ở `giao-dien-tham-chieu/docs/anh-giao-dien/`.
- `app/README.md:172` lại gọi chúng là ảnh ứng dụng thực tế; dòng 4 khẳng định hoàn chỉnh/đồng bộ 100%. Các khẳng định này không phù hợp bằng chứng đang có.
- Thư mục `app/docs/diagrams/` chỉ có nguồn `.puml`, không có PNG/SVG xuất sẵn.
- Kiểm tra XML Word: DOCX gốc và `app/docs/BAO_CAO_DA_SUA.docx` đều có 12 tham chiếu hình; **không có hình nhúng nào sau tiêu đề 4.3**. Bản sao có thêm chữ nhưng chưa có bốn hình thiết kế của hai module.
- Ảnh prototype vẫn có giá trị làm **mẫu thiết kế** nếu ghi nhãn đúng. Không gọi đó là kết quả chạy backend; cần hình thiết kế giao diện có form/bảng/nút và đường chuyển màn theo bài giảng, cùng hai hình lớp đã đồng bộ và xuất đủ đọc.

### R07 — Một số nhánh lỗi không giữ đúng ngữ cảnh để sửa/tiếp tục

- `PurchaseInvoiceServlet4.hienThiBanNhap:195–196` nuốt `SQLException` khi tìm part, sau đó JSP có thể báo “Không tìm thấy phụ tùng”. Lỗi truy vấn không đồng nghĩa danh mục không có part; có thể dẫn người dùng sang thêm mới sai lý do.
- `ghiNhanThanhToan:507/526` trả `sendError(400, ...)` khi số trả rỗng/sai tổng thay vì trở lại chứng từ với lỗi/input. Trong source, ô `payment-error` trên JSP vì vậy không nhận được lỗi từ các nhánh này; trang lỗi chung không cho biết cách sửa số trả ngay tại hóa đơn.
- Cần giữ thông báo lỗi và ngữ cảnh chứng từ/form hiện có, trạng thái vẫn Chưa thanh toán; không thêm màn hay route.

### R08 — Integration test có thể kiểm tra một DB nhưng ghi vào DB khác

**Ưu tiên cao trước khi chạy kiểm thử tích hợp. Không chạy test này trong lần rà soát.**

- `PurchaseInvoiceDAO4IT.java:24–38` chỉ dùng `GARAMAN_TEST_DB_*` để kiểm tra kết nối `garaman4_test` có hoạt động.
- Dòng 58/85 tạo DAO thật và ghi chứng từ; `DAO.java:33–40` chỉ đọc `GARAMAN_DB_*` hoặc `db.properties`, không đọc cấu hình `GARAMAN_TEST_DB_*`.
- Nếu chỉ cấu hình DB test theo tên biến trong IT, DAO có thể ghi vào DB app. Phải bảo đảm cấu hình DAO và DB kiểm tra là cùng một CSDL riêng trước khi bật profile integration.
- Ca tên “lưu hóa đơn và tăng tồn” hiện chưa assert tồn trước/sau; không có các ca thật cho rollback, xác nhận đồng thời, hai hóa đơn cùng part, thay danh mục rồi đọc snapshot. Hai test trong file IT không đủ chứng minh các bất biến này.

### R09 — Dữ liệu mẫu chưa khớp nội dung ví dụ gốc

- DOCX P0497 mô tả “Lọc dầu dạng vặn; đường kính 76 mm; chiều cao 85 mm”; `02_seed.sql` đổi thành “Đường kính ren 76 mm, chiều cao 85 mm”.
- P0507 quy định bảo hành lỗi sản xuất, loại trừ lắp/sử dụng sai; seed lại thêm điều kiện “nguyên tem niêm phong và hóa đơn mua hàng”.
- Seed còn tự đặt bảo hành dịch vụ; DOCX ví dụ chỉ yêu cầu hiển thị nếu có thông tin. Tài liệu 4.3.1 lại thêm ren M20x1.5.
- Giá chính 120000/150000 và ví dụ nhập 10 × 80000 vẫn phù hợp, nhưng không được nói dữ liệu ví dụ khớp nguyên văn. Cần đưa mẫu hai module về đúng nội dung nguồn, phần chưa biết hiển thị Chưa cập nhật theo `11`.

### R10 — Tham số focus được ghi vào thuộc tính HTML chưa escape

- `PurchaseInvoiceServlet4.hienThiBanNhap` nhận `focusPartId` trực tiếp từ request; `gdLapHoaDonNhap.jsp:61` chèn `${focusPartId}` vào `value` không qua `c:out` hay kiểm tra ID số.
- Dữ liệu query có dấu nháy có thể thoát khỏi thuộc tính HTML. Cần kiểm tra là ID hợp lệ và escape đầu ra ngay trong luồng hiện có; không coi “hidden input” là lý do bỏ escape.
- Đây là phát hiện ở source; chưa kiểm thử trang JSP triển khai hoặc payload trên hệ thống chạy thật.

### R11 — DTO phiên được gán sai trường vị trí

- `AuthServlet4.java:122–127` truyền `member.getTenDangNhap()` vào tham số thứ tư của `SessionUser4`, nhưng constructor lưu tham số này vào `viTri`.
- `SessionUser4.fromMember(member)` đã có ánh xạ đúng vị trí nhân viên. Hiện các màn chính chủ yếu dùng vai trò/tên nên chưa thấy tác động trực tiếp tới hai luồng, nhưng dữ liệu DTO không đúng ý nghĩa khi dùng tiếp.

## 5. Ánh xạ phương thức để làm tiếp thiết kế tĩnh

Đây là các tên **đã tồn tại** trong source, không phải đề xuất mở rộng chức năng.

| Phương thức pha phân tích | Vị trí thiết kế/code | Kết quả chính |
|---|---|---|
| `timDichVuTheoTen` | `ServiceDAO4.timDichVuTheoTen(String)` | `List<Service4>` |
| `layChiTietDichVu` | `ServiceDAO4.layChiTietDichVu(int)` | `Service4` hoặc null |
| `timPhuTungTheoTen` | `SparePartDAO4.timPhuTungTheoTen(String, boolean)` | `List<SparePart4>`; khách true, kho false |
| `layChiTietPhuTung` | `SparePartDAO4.layChiTietPhuTung(int, boolean)` | `SparePart4` hoặc null |
| `timNhaCungCapTheoTen` | `SupplierDAO4.timNhaCungCapTheoTen(String)` | `List<Supplier4>` |
| `layThongTinNhaCungCap` | `SupplierDAO4.layThongTinNhaCungCap(int)` | `Supplier4` hoặc null |
| `themNhaCungCap` | `SupplierDAO4.themNhaCungCap(Supplier4)` | int ID |
| `layThongTinNhanVienKho` | `WarehouseStaffDAO4.layThongTinNhanVienKho(int)` | `WarehouseStaff4` hoặc null; còn thiếu lời gọi theo R05 |
| `layDanhSachNhom` | `SparePartCategoryDAO4.layDanhSachNhom()` | `List<SparePartCategory4>` |
| `themPhuTung` | `SparePartDAO4.themPhuTung(SparePart4)` | int ID, tồn ban đầu 0 |
| `tinhTongTien` | `PurchaseInvoice4.tinhTongTien()` | `BigDecimal`; không phải truy vấn DB |
| Thành tiền dòng | `PurchaseInvoiceDetail4.tinhThanhTien()` | `BigDecimal`, làm tròn 2 chữ số HALF_UP |
| `luuHoaDonNhap` | `PurchaseInvoiceDAO4.luuHoaDonNhap(PurchaseInvoice4)` | int ID; quản lý transaction |
| `tangTonSauNhap` | `SparePartDAO4.tangTonSauNhap(Connection,int,BigDecimal)` | void; package-private `~`; dùng connection của hóa đơn |
| `layThongTinHoaDonNhap` | `PurchaseInvoiceDAO4.layThongTinHoaDonNhap(int)` | `PurchaseInvoice4` gồm snapshot và chi tiết |
| `ghiNhanThanhToan` | `PurchaseInvoiceDAO4.ghiNhanThanhToan(int,BigDecimal,int)` | boolean; ID hóa đơn, số trả, ID người hiện tại |
| Mở lại chưa trả | `PurchaseInvoiceDAO4.layHoaDonChuaThanhToan(int)` | `List<PurchaseInvoice4>` |

Mười biên phân tích đã ánh xạ đủ sang JSP: `CustomerHomeUI4` → `gdChinhKH.jsp`; `CatalogSearchUI4` → `gdTimThongTin.jsp`; `ItemDetailsUI4` → `gdChiTietThongTin.jsp`; `WarehouseHomeUI4` → `gdChinhKho.jsp`; `SelectSupplierUI4` → `gdChonNhaCungCap.jsp`; `NewSupplierUI4` → `gdThemNhaCungCap.jsp`; `PurchaseInvoiceUI4` → `gdLapHoaDonNhap.jsp`; `NewSparePartUI4` → `gdThemPhuTung.jsp`; `ConfirmedInvoiceUI4` → `gdHoaDonNhapDaXacNhan.jsp`; `PrintInvoiceUI4` → `gdInHoaDonNhap.jsp`.

Servlet chịu trách nhiệm nhận request/điều phối, JSP hiển thị dữ liệu, DAO dùng JDBC, model giữ dữ liệu và phép tính. Đây là cách tinh chỉnh hợp lệ theo bài giảng/Lab1; không bắt buộc tạo Java class mang tên mỗi biên hoặc để JSP tự gọi DAO.

## 6. Kiểm tra thực sự đã chạy trong lần này

| Kiểm tra | Kết quả | Phạm vi chứng minh |
|---|---|---|
| Maven 3.9.11 `-o -f app/pom.xml package` | BUILD SUCCESS; 25 test, 0 failure/error/skipped; WAR tạo được | Java/model/validation/password/controller giả lập. JVM thực chạy là JDK 25.0.2, POM target `release 17`; không phải nghiệm thu runtime Java 17 |
| Kiểm tra hash bốn nguồn | Đều trùng manifest | Bản trích xuất/hình đã xem ứng với các file gốc hiện tại |
| So hash ảnh | 18/18 ảnh app giống prototype | Ảnh app hiện có không cung cấp bằng chứng backend độc lập |
| Kiểm tra ZIP/XML hai Word | Không có hình nào sau mục 4.3; không có PNG/SVG thiết kế xuất sẵn | Chưa bàn giao bốn hình thiết kế chèn báo cáo |
| Java probe gọi `CatalogServlet4.xemChiTiet` với `loai=spare-part` | 400 | URL của sơ đồ tuần tự hiện sai với controller; không cần DB để thấy lỗi này |
| Java probe định dạng `0.00`, locale vi-VN | `800000,00` | Chuỗi định dạng dùng trong phân tích R01 |
| Microsoft Edge 155, HTML number độc lập | `800000,00` → value rỗng; `800000.00` → giữ giá trị | Hành vi input HTML; **không phải** trang backend đã triển khai |
| Filter với session/CSRF giả lập | POST kho được chuyển tiếp từ vai trò cache | Filter hiện không đọc trạng thái tài khoản DB; không thay thế ca khóa tài khoản trên MySQL |
| JSP/Jasper trên Tomcat 9 | Chưa chạy | Chưa khẳng định mọi JSP/JSPF compile/render đúng |
| MySQL 8.4 schema/DAO/transaction/đồng thời | Chưa chạy | Chưa khẳng định rollback/cộng tồn/snapshot bền vững/quyền thanh toán đạt |
| Hai luồng backend bằng trình duyệt | Chưa chạy | Chưa có ảnh/backend E2E để nghiệm thu |

Không khởi động hay thay đổi MySQL đang có của người dùng. Dịch vụ tìm thấy là MySQL80 ở trạng thái dừng; chưa xác minh được môi trường mục tiêu Tomcat 9 + MySQL 8.4 đang hoạt động.

Bằng chứng kiểm tra bổ sung nằm trong `.review/source-audit-evidence.json`, `.review/ReviewSourceProbe.java`, `.review/review-browser.cjs`, `.review/review-browser-output.json`; `.review/` là thư mục sinh tự động, gitignored. Kết quả unit thật nằm ở `app/target/surefire-reports/`.

## 7. Điều kiện để chốt phần thiết kế chi tiết của hai module

1. Thống nhất cách ghi những khác biệt **đã chốt** tại mục 3 trong phần hai module; giữ nguồn gốc read-only. Không dùng tuyên bố “đồng bộ 100%” thay cho bảng đối chiếu.
2. Sửa các lỗi code trong hàm/JSP hiện có, đặc biệt số tiền, quyền khi phiên đã mở, lấy thông tin nhân viên, xử lý lỗi và escape. Giữ các màn/route/thao tác theo `00`/`11`.
3. Đồng bộ 4.3.1/ma trận/phương thức/sơ đồ theo tên source và SQL thật; bỏ lớp/hàm/cột tưởng tượng. Xác định một bản nguồn cho mỗi hình để tránh hai bản `.puml` lệch nhau.
4. Hoàn thiện **bốn hình chính**: giao diện/luồng tra cứu; lớp thiết kế tra cứu; giao diện/luồng nhập; lớp thiết kế nhập. Hình giao diện phải nhìn được form/bảng/nút và mũi tên như mẫu; xuất PNG/SVG đủ đọc. Phân biệt rõ ảnh mẫu prototype với ảnh app chạy thật.
5. Kiểm tra Jasper/Tomcat 9 và MySQL 8.4 trên CSDL riêng: tra cứu “lọc”/hai loại/giữ từ khóa; nhánh thêm mới; nhiều dòng/dòng trùng/input sai; 10 × 80000; rollback lỗi SQL; xác nhận cùng UUID; hai phiếu cùng part; snapshot sau sửa danh mục; in không cập nhật tồn; thiếu/đủ/thừa tiền; khóa tài khoản; mở lại hóa đơn chưa trả. Sửa định tuyến DB test trước khi chạy IT.

**Mức sẵn sàng:** có thể tiếp tục xây dựng bản nháp thiết kế chi tiết dựa trên khung và bảng ánh xạ ở đây. Chưa nên chép nguyên `app/docs/4.3.1_THIET_KE_TINH.md` hoặc dùng các sơ đồ/ảnh hiện có làm bản cuối, và chưa thể xác nhận backend đã đúng hoàn toàn.
