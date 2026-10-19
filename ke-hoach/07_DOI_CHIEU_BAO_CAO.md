# Hướng dẫn viết và đối chiếu báo cáo theo bài giảng

## 1. Căn cứ và nguyên tắc sử dụng

Đây là hướng dẫn cho Gemini viết **4.3.1** bằng giao diện/code thật và chuẩn bị dữ liệu đối chiếu cho phần sau. Phần 4.3.2 trở đi được hướng dẫn để người dùng tiếp tục, chưa phải yêu cầu triển khai thêm chức năng hoặc tự viết hết báo cáo trong đợt này.

| Nguồn | Phần dùng | Áp dụng |
|---|---|---|
| DOCX mới của người dùng | Kịch bản hai module; hình lớp phân tích đang là 1.5/1.6; giao tiếp; lớp thực thể và CSDL | Đầu vào nghiệp vụ và mô hình, giữ hậu tố 4 và luồng người dùng đã làm |
| Ngân hàng đề, ĐỀ SỐ 26, trang PDF 27 | Tra cứu và nhập phụ tùng; yêu cầu giao tiếp/lớp thiết kế/CSDL/code/gói/triển khai | Chốt hai module, đề GaraMan khác chỉ cung cấp ngữ cảnh |
| Bài giảng, 4.3.1, trang in 96–103 (PDF 97–104) | Bốn bước ở trang in 96–97; Hình 4.3–4.8, riêng Hình 4.8 ở trang in 103 | Trình bày mỗi module với thiết kế giao diện và biểu đồ lớp thiết kế, không chỉ chụp web/dán code |
| Bài giảng, 4.3.2, trang in 103–112 | Hoạt động, kịch bản phiên bản 3, tuần tự thiết kế | Dùng đúng các hàm đã chốt trong biểu đồ lớp và code |
| Bài giảng, 4.3.3, trang in 113–114 | Gói và triển khai | Vẽ package thực và các node chạy hệ thống |
| Lab1 | JSP + Servlet + JDBC + MySQL + DAO; JavaBean/UserDAO/UserServlet và các JSP | JSP giao diện, Servlet nhận request, DAO truy cập DB, model chứa dữ liệu |

Bài giảng dùng các trang JSP và DAO trong ví dụ. Lab1 bổ sung Servlet điều phối; vì vậy thiết kế của bài này thể hiện Servlet thực tế, không mô tả JSP gọi DAO trực tiếp nếu code không làm vậy. Giống mẫu về phương pháp/bốn bước/tầng và cách xác định input–output; không chép tên lớp đăng kí học, màu sắc hoặc các thao tác của ví dụ sang GaraMan.

Số hình/tiêu đề hiện có của DOCX là số nguồn để tìm. Khi đổi đánh số theo Chương 3–4, lập bảng số nguồn → số cuối và cập nhật lời dẫn/caption đồng bộ. Không mặc định đã có Hình 3.5 nếu DOCX chưa đổi số. Trong bản nháp có thể dùng mã hình TK-TC-UI, TK-TC-CLASS, TK-NH-UI, TK-NH-CLASS, sau đó gán số cuối.

## 2. Đầu ra bắt buộc của 4.3.1

Trong `app/docs/4.3.1_THIET_KE_TINH.md`, và chèn vào bản sao DOCX:

```text
4.3 THIẾT KẾ CHI TIẾT CHO MODUL
4.3.1 Thiết kế tĩnh
  Đầu vào và nền tảng lựa chọn
  a. Thiết kế tĩnh modul khách hàng tìm kiếm thông tin dịch vụ/phụ tùng
     Bước 1. Thiết kế giao diện
     Bước 2. Đề xuất lớp giao diện và dữ liệu trao đổi
     Bước 3. Đề xuất lớp truy xuất dữ liệu
     Bước 4. Bổ sung thực thể và biểu đồ lớp thiết kế
  b. Thiết kế tĩnh modul nhân viên kho nhập phụ tùng
     Bước 1. Thiết kế giao diện
     Bước 2. Đề xuất lớp giao diện và dữ liệu trao đổi
     Bước 3. Đề xuất lớp truy xuất dữ liệu
     Bước 4. Bổ sung thực thể và biểu đồ lớp thiết kế
```

Mở đầu nói ngắn: thiết kế từ hai biểu đồ lớp phân tích và mô hình thực thể đã có; triển khai web JSP/Servlet/JDBC/MySQL theo Lab1. Không đưa phiên bản Maven, token hoặc SQL vào mô tả nghiệp vụ của khách/người kho.

Cần **4 hình chính**: giao diện/luồng tra cứu, lớp thiết kế tra cứu, giao diện/luồng nhập, lớp thiết kế nhập. Hình giao diện phải nhìn được form/bảng/nút và mũi tên chuyển màn hình như mẫu, có thể ghép ảnh thật và chia hình con để chữ đọc được. Không dùng một sơ đồ các hình chữ nhật tên màn hình thay cho toàn bộ thiết kế giao diện. Ảnh chi tiết màn hình thêm ở phụ lục khi cần.

Lưu `.puml` có thể sửa và `.svg`/`.png` đã xuất trong `app/docs/diagrams/`. PNG chèn DOCX phải đủ đọc chữ. Nếu công cụ không export được, ghi rõ và bàn giao nguồn; không đặt file ảnh rỗng hoặc báo đã có ảnh.

## 3. Module tra cứu — triển khai bốn bước

### Bước 1. Thiết kế giao diện

Trang chính khách → trang tìm chưa có kết quả → kết quả theo tên → chi tiết dịch vụ/phụ tùng → quay về đúng từ khóa. Mô tả hai trạng thái trước/sau tìm là một trang, không tự tạo hai module.

Dùng dữ liệu DOCX: từ khóa “lọc”; DV1 “Thay lọc dầu động cơ” giá tiền công 120.000 đồng; PT1 “Lọc dầu động cơ AF-01” giá bán 150.000 đồng. Chi tiết part có nhóm, đơn vị, hãng AutoFilter, thông số 76 mm/85 mm, bảo hành 6 tháng, điều kiện theo kịch bản, dòng xe “Chưa cập nhật”. Không tự ghép tiền công với tiền part.

Chụp giao diện ứng dụng đang chạy. Từ khóa rỗng báo lỗi, không có kết quả có thông báo riêng, quay lại giữ từ khóa. Chi tiết phân biệt loại service/part dù id bằng nhau.

### Bước 2. Ánh xạ biên sang giao diện thật

| Biên phân tích | JSP/Servlet | Dữ liệu chính |
|---|---|---|
| CustomerHomeUI4 | gdChinhKH.jsp / HomeServlet4 | Tên từ session; liên kết mở chức năng |
| CatalogSearchUI4 | gdTimThongTin.jsp / CatalogServlet4 | input tuKhoa; nút Tìm kiếm; output dsDichVu/dsPhuTung; mỗi kết quả có id/loai |
| ItemDetailsUI4 | gdChiTietThongTin.jsp / CatalogServlet4 | output dichVu hoặc phuTung; id/loai/tuKhoa qua query; Quay lại |

Sau bảng, liệt kê thuộc tính ẩn/truyền kèm: id đối tượng, loai, tuKhoa; thông tin tài khoản từ session, không cho người dùng nhập lại. Query param và hidden field là cách trao đổi, không tự thêm cột CSDL.

Tên request attributes, route và kiểu theo file 03. Không tạo Java class trùng tên lớp biên để thay JSP. Không thêm lớp CatalogItem4 hoặc bảng Product chung vì có thể render hai list thực thể sẵn có.

### Bước 3. DAO với tham số và kết quả

| Hàm thiết kế | Input | Output |
|---|---|---|
| ServiceDAO4.timDichVuTheoTen(String tuKhoa) | Tên chứa từ khóa, trim hợp lệ | List<Service4> đang cung cấp |
| SparePartDAO4.timPhuTungTheoTen(String tuKhoa, boolean chiCongBo) | Từ khóa; true cho khách | List<SparePart4> đang kinh doanh |
| ServiceDAO4.layChiTietDichVu(int id) | id dịch vụ | Service4 hoặc null nếu không được xem |
| SparePartDAO4.layChiTietPhuTung(int id, boolean chiCongBo) | id phụ tùng, true | SparePart4 có nhóm/tồn từ DB hoặc null |

Các DAO kế thừa DAO trừu tượng để dùng cơ chế tạo Connection chung; không dùng một Connection static mở suốt ứng dụng. Không ghi tìm không thấy nếu thật ra SQL lỗi. Các hàm trả về và exceptions phải khớp file 02/code, không tự đổi List thành boolean trong hình.

### Bước 4. Thực thể và lớp thiết kế

Giữ Member4/Customer4, Service4, SparePart4 và SparePartCategory4, quan hệ/kế thừa theo mô hình gốc. Nhóm phụ tùng là object của SparePart4; tonKho là BigDecimal ánh xạ cột đã lưu.

Biểu đồ `catalog-design.puml` thể hiện JSP/Servlet phía trên, DAO giữa, thực thể phía dưới. JSP gửi request tới Servlet; Servlet gọi DAO và forward JSP; DAO truy xuất/map model; JSP đọc model được chuyển qua request. Vẽ các quan hệ này bằng dependency với nhãn ý nghĩa, không vẽ kết tập giữa Servlet và bảng DB.

Phải có tên method Servlet dùng thật: hienThiTimKiem, timKiem, xemChiTiet. Thể hiện các dao method đủ tham số/kiểu trả về. Entity giữ thuộc tính private và quan hệ, có thể lược getter/setter cho hình gọn.

## 4. Module nhập — triển khai bốn bước

### Bước 1. Thiết kế giao diện

Luồng bắt buộc:

```text
Trang chủ kho → Tìm/chọn NCC → Lập hóa đơn nháp
                  ↕ thêm NCC       ↕ tìm/chọn part, sửa lượng/giá
                                    ↕ thêm part mới
                                    → Hủy trước xác nhận
                                    → Xác nhận → Hóa đơn chưa trả ↔ Bản in
                                                           → Đã trả
Trang chủ kho → Hóa đơn chưa trả của tôi → Mở lại → In/ghi nhận trả tiền
```

Nhánh thêm mới quay về đúng bước chọn; không tự chọn/thêm dòng mà kịch bản vẫn nói người dùng phải chọn. Tồn part mới bằng 0. Dòng mới 0/0 có thể hiển thị trong nháp nhưng chặn xác nhận nếu chưa nhập hợp lệ. Chọn part trùng chỉ focus dòng cũ; vòng lặp giữ những dòng đã sửa.

Ví dụ chính thống nhất DOCX: Hòa Phát, PT1, nhập 10 cái × 80.000 đồng = 800.000 đồng; tồn tăng 10 khi xác nhận. Giá bán danh mục 150.000 không đổi. Sau xác nhận in rồi ghi nhận trả đủ; không coi in là bằng chứng đã thanh toán.

### Bước 2. Biên, JSP/Servlet và dữ liệu ẩn

Giữ đủ 7 biên của pha phân tích, ánh xạ theo bảng file 03:

| Biên | JSP | Servlet |
|---|---|---|
| WarehouseHomeUI4 | gdChinhKho.jsp | HomeServlet4/PurchaseInvoiceServlet4 (danh sách chưa trả của tôi) |
| SelectSupplierUI4 | gdChonNhaCungCap.jsp | SupplierServlet4 |
| NewSupplierUI4 | gdThemNhaCungCap.jsp | SupplierServlet4 |
| PurchaseInvoiceUI4 | gdLapHoaDonNhap.jsp | PurchaseInvoiceServlet4 |
| NewSparePartUI4 | gdThemPhuTung.jsp | SparePartServlet4 |
| ConfirmedInvoiceUI4 | gdHoaDonNhapDaXacNhan.jsp | PurchaseInvoiceServlet4 |
| PrintInvoiceUI4 | gdInHoaDonNhap.jsp | PurchaseInvoiceServlet4 |

Mỗi JSP cần bảng input/output/submit/ẩn cụ thể. Dùng danh mục trường ở phần 5 file 02 và request/session file 03; không bỏ form thêm mới vì chỉ muốn làm một trang demo.

Dữ liệu trao đổi cần giải thích tại thiết kế:

- supplierId trước tạo nháp; maYeuCau do server sinh, giữ nháp độc lập giữa hai tab.
- selectedPartId, removePartId và mảng partId phân biệt rõ; soLuong_<id>, donGiaNhap_<id> chứa giá trị dòng.
- csrfToken là trường kỹ thuật cho POST, không thay mã chống xác nhận trùng.
- currentUser lấy từ session, không lấy id nhân viên/quyền/tổng/ngày/trạng thái từ input để quyết định lưu.
- Khi thêm part, giữ mã nháp để quay về; khi lỗi, giữ input thô để sửa, không silently đổi thành 0.
- Thành tiền/tổng là output và được tính lại server; tên/đơn vị áp dụng chốt từ DB khi xác nhận.

### Bước 3. DAO và transaction

Có DAO, SupplierDAO4, WarehouseStaffDAO4, SparePartCategoryDAO4, SparePartDAO4, PurchaseInvoiceDAO4. Chi tiết nhập được quản lí trong DAO hóa đơn, không tự tách DAO chi tiết mở transaction riêng.

| Phương thức từ phân tích | Thiết kế cuối | Ghi chú |
|---|---|---|
| timNhaCungCapTheoTen/themNhaCungCap/layThongTinNhaCungCap | SupplierDAO4 với tham số file 02 | Tìm/chọn/thêm đúng kịch bản |
| layThongTinNhanVienKho | WarehouseStaffDAO4.layThongTinNhanVienKho(int) | Nhân viên hiện tại còn hoạt động |
| layDanhSachNhom | SparePartCategoryDAO4.layDanhSachNhom() | Nhóm đang dùng cho form thêm part |
| timPhuTungTheoTen/layChiTietPhuTung/themPhuTung | SparePartDAO4, chiCongBo=false khi kho tìm | Không sửa giá bán bằng giá nhập |
| tinhTongTien | PurchaseInvoice4.tinhTongTien(): BigDecimal | Tính trong model, không phải truy vấn SQL |
| luuHoaDonNhap | PurchaseInvoiceDAO4.luuHoaDonNhap(PurchaseInvoice4): int | Trả id chứng từ mới hoặc id đã có cùng mã yêu cầu |
| tangTonSauNhap | SparePartDAO4.tangTonSauNhap(Connection,int,BigDecimal): void | Phạm vi package, nhận connection từ DAO hóa đơn |
| layThongTinHoaDonNhap | PurchaseInvoiceDAO4.layThongTinHoaDonNhap(int): PurchaseInvoice4 | Đọc snapshot và dòng lịch sử |
| ghiNhanThanhToan | PurchaseInvoiceDAO4.ghiNhanThanhToan(int,BigDecimal,int): boolean | id hóa đơn, số tiền đã trả, id người hiện tại; phải bằng người lập |

Giải thích ngắn transaction: khóa/kiểm tra dữ liệu → INSERT header và dòng → tăng tồn → commit; lỗi rollback toàn bộ; UNIQUE maYeuCau bảo đảm request lặp không tạo/cộng hai lần. Không đưa toàn bộ SQL và chi tiết xử lí lỗi vào đoạn mô tả thao tác người dùng.

Thêm phụ thuộc PurchaseInvoiceDAO4 → SparePartDAO4 cho hàm tăng tồn; không có vòng JSP → DB hoặc model tự mở Connection. Trên UML thiết kế hàm này dùng phạm vi `~` (package) theo code. Các ký hiệu `+`, `-`, `#` và `~` phải có ý nghĩa đúng, không ghi toàn bộ method public nếu code là private/package.

### Bước 4. Thực thể và biểu đồ lớp thiết kế

Có Member4, Employee4, WarehouseStaff4; Supplier4; SparePartCategory4, SparePart4; PurchaseInvoice4, PurchaseInvoiceDetail4. Giữ kế thừa, object và list như mô hình đã làm. Một hóa đơn chứa nhiều dòng; mỗi dòng tham chiếu một part. Không phát sinh CustomerInvoice hoặc người thu/chi riêng chỉ để làm luồng nhập.

Trên `purchase-design.puml` vẽ JSP/Servlet → DAO → entity như mẫu, thể hiện đầy đủ các phương thức dùng trong luồng. Tiện ích token/draft có thể đưa sơ đồ kỹ thuật phụ để hình chính đọc được; nếu chúng xuất hiện ở tuần tự chi tiết sau này thì thêm tham chiếu ở bảng thiết kế tương ứng, không bịa lớp mới.

## 5. Ma trận truy vết phải tạo cùng code

`app/docs/MA_TRAN_TRUY_VET.md` có cột:

```text
Mã yêu cầu | Vị trí trong DOCX | Bước/ngoại lệ | UI/JSP | HTTP/route |
Servlet.method | DAO/model.method | Bảng/cột | Test ID |
Hình 4.3.1 | Mã nhánh cho 4.3.2
```

Mỗi dòng phải chứa tên thật; không dùng dấu “...” để che thiếu đường xử lí. Những hàng tối thiểu:

| Mã | Căn cứ | Điểm nối phải truy được |
|---|---|---|
| TC-01 | Tra cứu bước 1–2 | Trang chính → GET catalog → trạng thái chưa tìm |
| TC-02 | Tra cứu bước 3–4 | Catalog.timKiem → hai DAO tìm tên → bảng kết quả |
| TC-03 | Tra cứu bước 5–6, ngoại lệ 5a | loai/id → DAO chi tiết đúng loại → JSP chi tiết |
| TC-04 | Tra cứu 3a/4a, quay lại | Validate từ khóa/không kết quả/giữ tuKhoa |
| NH-01 | Nhập bước 1–6 | Tìm/chọn NCC → start → tạo nháp theo session |
| NH-02 | Nhập 4a | Thêm NCC → trở về danh sách, chủ động chọn |
| NH-03 | Nhập bước 7–11 | Tìm/chọn/sửa dòng; không trùng part |
| NH-04 | Nhập 8a | Tạo part nhóm có sẵn, tồn 0 → quay về đúng nháp |
| NH-05 | Nhập 9a/11a/12b | Dòng trùng/input sai/nháp trống bị xử lí đúng |
| NH-06 | Nhập bước 12–13, 12c | Confirm → INSERT header/dòng + tăng tồn, chống lặp |
| NH-07 | Nhập bước 14–17 | Đọc hóa đơn đã lưu → print, tồn không đổi |
| NH-08 | Nhập bước 18–19, 18a | Người lập pay đúng tổng, trạng thái/thời điểm, không cộng tồn |
| NH-09 | Nhập 12a | Hủy nháp, giữ danh mục đã tạo |
| NH-10 | Nhập 18b đã sửa theo file 01 | Chính người lập mở lại chưa trả và tiếp tục |
| KT-01 | Bổ sung kỹ thuật theo code | Quyền/CSRF/transaction/snapshot/multi-tab, không mô tả thành UC mới |

Liên kết các mã trên tới C01–C07, P01–P16 và các biến thể tồn/rollback trong file 02. Một yêu cầu có thể nhiều test. Các điều kiện kỹ thuật được ghi là lựa chọn thiết kế, không gán giả một bước UC chưa có.

## 6. Dữ liệu bàn giao để làm 4.3.2 đúng mẫu

Đầu ra đợt này có `DOI_CHIEU_PHAN_SAU.md`, ghi danh sách actor/lifeline, chữ ký hàm đã code, route, điều kiện và biến đầu vào/đầu ra cho từng nhánh. Chưa khẳng định đã viết xong 4.3.2.

Khi người dùng làm tiếp 4.3.2:

1. Vẽ biểu đồ hoạt động mỗi module. Các hành động xử lí hệ thống gắn đúng method đã thiết kế; thao tác con người như nhập từ khóa, giao tiền là hành động actor, không bịa thành method DAO.
2. Viết kịch bản phiên bản 3: actor → JSP/form → Servlet → DAO/model → trả kết quả → forward/redirect tới JSP. Không lấy nguyên kịch bản phân tích trong đó entity tự tìm DB thay cho bản thiết kế.
3. Vẽ tuần tự từ chính kịch bản phiên bản 3, tên thông điệp đúng signature code, có tham số/giá trị trả về và activation. Dùng alt/loop cho tìm rỗng, lặp dòng, dữ liệu sai và request lặp; không thay các nhánh thành module mới.

Luồng tra cứu cần đối chiếu:

```text
Customer → gdTimThongTin.jsp → CatalogServlet4.timKiem(req,res)
  → ServiceDAO4.timDichVuTheoTen(tuKhoa): List<Service4>
  → SparePartDAO4.timPhuTungTheoTen(tuKhoa,true): List<SparePart4>
  → request attributes → forward gdTimThongTin.jsp
Chọn kết quả → CatalogServlet4.xemChiTiet(req,res)
  → DAO chi tiết đúng loai → forward gdChiTietThongTin.jsp
```

Luồng xác nhận nhập cần đối chiếu:

```text
WarehouseStaff → gdLapHoaDonNhap.jsp → PurchaseInvoiceServlet4.xacNhanHoaDon(req,res)
  → đọc/validate input và nháp, kiểm tra maYeuCau đã lưu
  → PurchaseInvoiceDAO4.luuHoaDonNhap(hoaDon)
     → transaction/khóa/đọc lại dữ liệu → INSERT header/dòng
     → SparePartDAO4.tangTonSauNhap(con,id,qty) trong loop
     → commit → id
  → redirect GET view → PurchaseInvoiceServlet4.xemHoaDon(req,res)
     → PurchaseInvoiceDAO4.layThongTinHoaDonNhap(id)
     → forward gdHoaDonNhapDaXacNhan.jsp
```

Trong hình tuần tự chi tiết, chỉ ghi `chenHoaDon`, `chenChiTiet` và method private đúng chữ ký khi code thật có gọi; commit/rollback thuộc JDBC Connection. Hai HTTP request quanh redirect là hai lần xử lí riêng, không vẽ redirect như một forward trong cùng request. In và pay là nhánh/request riêng; không đặt pay bên trong `luuHoaDonNhap`.

Luồng pay: Servlet lấy user từ session → DAO.ghiNhanThanhToan(id,tien,userId) → kiểm tra người lập/trạng thái/đúng tổng → UPDATE trạng thái/thời điểm → redirect view. Không gọi tăng tồn, không thêm người thanh toán vào model.

Giữ hình giao tiếp pha phân tích hiện có; hình tuần tự thiết kế có DAO/Servlet rõ hơn là mức chi tiết mới, không phải lý do thay lại hình phân tích đã chốt.

## 7. Dùng tiếp cho 4.3.3, Chương 5 và Chương 6

### 4.3.3 Thiết kế triển khai

- Biểu đồ gói: model, dao, controller, filter, util, dto, exception; view/member, view/customer, view/warehouse, common; assets/resources. Mũi tên phụ thuộc theo import/call thực tế.
- Biểu đồ triển khai: trình duyệt → HTTP → Tomcat chứa WAR/JSP/Servlet/DAO/model → JDBC → MySQL chứa garaman4. Nếu demo cùng máy vẫn vẽ các môi trường chạy logic đúng, không bịa ba máy vật lí bắt buộc.
- Ghi artifact WAR/context, phiên bản thực chạy và cấu hình kết nối không chứa mật khẩu thật. Ba tầng trình bày/xử lí/truy cập dữ liệu không đồng nghĩa phải có ba máy.
- Không thêm server Spring/React, dịch vụ thanh toán hoặc Redis không có trong code.

### Chương 5 Cài đặt

Dùng cấu trúc môi trường → thư mục/package → cài đặt từng module → kiểm thử đơn vị theo bài giảng. Trích đúng file/hàm, tập trung điểm quan trọng: tìm đúng loại, giữ nháp, transaction lưu/cộng tồn, chống lặp, in/pay tách riêng. Không dán toàn bộ source thay lời giải thích; source nằm trong project.

Ghi công nghệ/version thật, lệnh build/deploy, script/schema/seed, tài khoản demo và URL trong README. Unit test cho tổng tiền/validate; integration test cho transaction/quyền người lập/cộng tồn đồng thời. Báo kết quả thật, tách lỗi môi trường khỏi lỗi nghiệp vụ. Nếu chưa chạy trình duyệt thì không gọi ảnh mockup là ảnh thật.

### Chương 6 Rà soát và kiểm thử

Theo các nhóm của bài giảng: đặc tả → phân tích → thiết kế → code → test chức năng. Dùng checklist có câu hỏi cụ thể, kết quả và vị trí lỗi/sửa, thay vì đánh tất cả đạt chung chung.

Các test chức năng có: mã yêu cầu từ ma trận, tiền điều kiện, dữ liệu, các bước, kết quả mong đợi, thực tế, pass/fail/chưa chạy và ảnh/log. Tái hiện ví dụ DOCX, ca lỗi và ca rollback/confirm lặp/pay lặp. Không gọi các module đặt lịch/tiếp nhận/thống kê chưa code là đã kiểm thử.

## 8. Kiểm tra chéo trước khi bàn giao 4.3.1

- Mọi biên phân tích đều có JSP/đường đi hoặc giải thích gộp/tách rõ; 3 JSP khách và 7 JSP kho đúng bảng ánh xạ, đăng nhập là hỗ trợ.
- Mọi public/package method dùng trên class diagram có trong code với cùng tham số/kiểu trả về/phạm vi. Method phân tích cần SQL đã được chuyển sang DAO, công thức tiền vẫn ở model.
- Model–SQL giữ đúng object/FK và kiểu theo MODEL_FIELD_MAP; 16 bảng, đúng quan hệ tùy chọn, không có bảng nhân viên vai trò đã bỏ hoặc FK thu/chi riêng.
- Hàm tăng tồn ở DAO nhận Connection; transaction hóa đơn gọi nó; hình thể hiện phụ thuộc này, test chứng minh rollback và không tăng lặp.
- UI không lộ UUID/SQL/tên bảng; tổng và người lập lấy phía server; hóa đơn/bản in dùng các trường áp dụng lịch sử.
- Bốn hình chính đọc được, có nguồn chỉnh sửa và ảnh từ ứng dụng chạy; caption/lời dẫn đúng số cuối trong DOCX.
- Các chữ “chưa triển khai/chưa chạy/chưa chèn” phản ánh đúng thực tế; không gán trạng thái hoàn thành cho phần 4.3.2 trở đi chỉ vì đã chuẩn bị ma trận.
