# Phạm vi bắt buộc — chỉ hai module đề 26

**Chỉ dẫn này thay thế yêu cầu cũ tạo model/schema cho cả hệ thống.** Mục tiêu là code đúng hai module người dùng đã phân tích, làm căn cứ cho 4.3.1 của chính hai module đó.

Giao diện/luồng phải giữ theo [11_CHUAN_MAU_LUONG_GIAO_DIEN.md](11_CHUAN_MAU_LUONG_GIAO_DIEN.md): nguồn nghiệp vụ là DOCX, nguồn bố cục là giao diện tham chiếu đã sửa; bài giảng/LAB1 khóa cách thiết kế và tổ chức code. Không sửa ngược yêu cầu để hợp thức hóa giao diện do AI tự đổi. Kết quả rà soát app hiện tại ở12, chưa được nghiệm thu chỉ vì đã có code hoặc unit test PASS.

## Chức năng được triển khai

1. **Khách hàng tra cứu dịch vụ/phụ tùng:** trang tìm ban đầu trống → nhập tên → danh sách kết quả → chi tiết đúng loại → quay lại giữ từ khóa.
2. **Nhân viên kho nhập phụ tùng:** tìm/chọn NCC, thêm NCC khi chưa có → tìm/chọn phụ tùng ngay trên hóa đơn, thêm phụ tùng khi chưa có → nhập nhiều dòng lượng/giá → xác nhận → in → ghi nhận trả đủ tiền; xử lý nháp, hủy, lỗi và mở lại hóa đơn chưa trả theo kịch bản đã có.

Đăng nhập/đăng xuất và hai trang chính chỉ hỗ trợ tiền điều kiện hai module. Dùng tài khoản seed; không xây đăng ký, cấp tài khoản, quản trị nhân viên hoặc hồ sơ cá nhân.

## Danh sách được tạo trong app

**10 lớp model nghiệp vụ/hỗ trợ tài khoản**, đúng tên và kế thừa trong báo cáo:

```text
Member4, Employee4, WarehouseStaff4, Customer4,
Service4, SparePartCategory4, SparePart4, Supplier4,
PurchaseInvoice4, PurchaseInvoiceDetail4
```

**9 bảng SQL**, trích phần liên quan từ mô hình gốc:

```text
tblMember4, tblEmployee4, tblCustomer4,
tblService4, tblSparePartCategory4, tblSparePart4, tblSupplier4,
tblPurchaseInvoice4, tblPurchaseInvoiceDetail4
```

WarehouseStaff4 kế thừa Employee4 nhưng không có bảng vai trò riêng. Các lớp DAO/Servlet/Filter/util/DTO/exception chỉ phục vụ hai module và phần đăng nhập hỗ trợ, tên/chữ ký theo 02/03/09/10. Không thêm POJO ngoài danh sách chỉ để “khớp toàn bộ báo cáo”.

Không triển khai đặt lịch, xe, tiếp nhận sửa xe, phân công kỹ thuật, hóa đơn khách, thu tiền khách, thống kê, quản trị danh mục đầy đủ hoặc CRUD ngoài các nhánh thêm mới cần cho nhập hàng. Không tạo model/bảng/DAO/Servlet/JSP/test cho chúng, kể cả dạng khung trống.

## Bài làm gốc và đầu ra

- Mô hình tổng thể trong DOCX là **nguồn tham chiếu**, không phải danh sách phải code hết. Giữ nguyên file gốc và các phần ngoài hai module.
- Chỉ đối chiếu luồng, thuộc tính và quan hệ liên quan đến hai module. Schema app là tập con 9 bảng, không tuyên bố thay thế ERD tổng thể của báo cáo.
- Đầu ra: project chạy được hai module, README chạy/demo, test thật của hai module, nội dung 4.3.1 và bốn hình giao diện/lớp thiết kế tương ứng. Các tài liệu ánh xạ chỉ ghi những file/hàm/bảng thực sự được dùng trong phạm vi này.
- Không yêu cầu viết/sửa lại toàn bộ Word, vẽ lại toàn bộ mô hình hoặc chuẩn bị các chương/phần sau. Nội dung 4.3.1 bàn giao dạng Markdown và hình sẵn chèn; chỉ chỉnh bản sao DOCX nếu người dùng yêu cầu riêng.
- Giữ giao diện tham chiếu đã sửa và luồng của bài làm. Chống xác nhận trùng, transaction, kiểm tra input/quyền là chi tiết để hai module hoạt động đúng, không mở rộng thành chức năng mới.

Nếu gặp chỉ dẫn cũ trái phạm vi này, áp dụng file 00 và ghi rõ chỗ lệch; không làm thêm để hoàn thành danh sách cũ. Nếu đã có code ngoài phạm vi do lần chạy trước, ghi nhận và tách khỏi luồng build/demo nếu cần; không tự xóa file hoặc dữ liệu người dùng.


## Ràng buộc sửa chữa theo yêu cầu chủ bài

Giữ nguyên các màn hình, nút/thao tác và luồng gọi trong thiết kế hai module của `26_B23DCCN764-full.docx` và giao diện tham chiếu. Sửa lỗi bên trong các hàm nghiệp vụ hiện có; không tự thêm màn, route, action, DAO nghiệp vụ hoặc hàm nghiệp vụ mới để xử lý lỗi. Dùng lại POST cập nhật khi rời hóa đơn sang form thêm phụ tùng để giữ dữ liệu. UUID/version/CSRF là tham số ẩn hỗ trợ kỹ thuật, không thành chức năng mới trên giao diện. Các hàm kỹ thuật hỗ trợ đã có không phải use case mới; không vẽ chúng thành nút hoặc luồng nghiệp vụ riêng. Nếu thiết kế gốc chưa đủ để quyết định một thay đổi nghiệp vụ, nêu đúng điểm thiếu để chủ bài quyết định.
