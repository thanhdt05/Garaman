# Prompt cho OpenCode/DeepSeek hoặc AI khác — chỉ hai module

Tên file được giữ để các liên kết đang có vẫn dùng được. Copy khối bên dưới khi mở AI tại thư mục Garaman.

---

Hãy triển khai đúng **HAI MODULE đề 26** của bài làm hiện có:

1. Khách hàng tra cứu thông tin dịch vụ/phụ tùng.
2. Nhân viên kho nhập phụ tùng từ nhà cung cấp, gồm các nhánh chọn/thêm NCC, chọn/thêm phụ tùng, lập nháp, xác nhận, in và ghi nhận trả đủ tiền.

Đăng nhập/đăng xuất và hai trang chính chỉ hỗ trợ hai module, không phát triển quản lý tài khoản.

**Đọc ke-hoach/00_PHAM_VI.md trước. File này thay thế chỉ dẫn cũ yêu cầu cài đặt cả mô hình hệ thống. Chỉ tạo 10 model và 9 bảng trong danh sách; không tạo POJO, SQL, DAO, Servlet, JSP hoặc test cho module khác, kể cả khung trống.**

Đọc tiếp:

- ke-hoach/README.md và các file 01–10 theo vai trò đã ghi; mọi file đều chịu giới hạn của 00.
- 26_B23DCCN764-full.docx: hai kịch bản ở 1.2.1, các lớp biên/phân tích/giao tiếp của hai module, các thuộc tính/quan hệ cần thiết trong hình thiết kế. Phần mô hình tổng thể chỉ để tra cứu, không phải danh sách cài đặt.
- Ngân hàng đề: ĐỀ SỐ 26, trang PDF 27, đúng hai chức năng trên.
- Bài giảng: 4.3.1 và các hình ví dụ giao diện/lớp thiết kế, để trình bày hai module theo bốn bước.
- LAB1: cách dùng JSP + Servlet + JDBC + MySQL + DAO.
- giao-dien-tham-chieu/README.md và mã web/; tái sử dụng theo ánh xạ từng file ở 09.
- ke-hoach/11_CHUAN_MAU_LUONG_GIAO_DIEN.md: tiêu chuẩn bắt buộc giữ mẫu/luồng/UI; ke-hoach/12_RA_SOAT_APP_HIEN_TAI.md: các lỗi cụ thể ở app đã có cần đối chiếu/sửa trước nghiệm thu. Không chấp nhận câu “hoàn thành100%” trong tài liệu cũ nếu chưa có bằng chứng.

Thực hiện trong app/ theo 02/03/09/10:

- Chỉ 10 model: Member4, Employee4, WarehouseStaff4, Customer4, Service4, SparePartCategory4, SparePart4, Supplier4, PurchaseInvoice4, PurchaseInvoiceDetail4.
- Chỉ 9 bảng: tblMember4, tblEmployee4, tblCustomer4, tblService4, tblSparePartCategory4, tblSparePart4, tblSupplier4, tblPurchaseInvoice4, tblPurchaseInvoiceDetail4.
- Giữ tên, field, kế thừa và quan hệ liên quan của bài làm. App là tập con để chạy hai module, không thay thế mô hình tổng thể trong Word.
- Java17/Maven WAR/Tomcat9/javax.servlet/JSP/JSTL/JDBC/MySQL8.4 theo phiên bản ở 02. Giữ bố cục giao diện đã sửa, thay dữ liệu mô phỏng bằng server; không nhập sessionStorage/HTML hóa đơn fallback sang app.
- JSP render; Servlet điều phối; DAO truy xuất và transaction; model dữ liệu/tính tiền. Tiền/lượng dùng BigDecimal/DECIMAL.
- Xác nhận nhập lưu header, dòng và tăng tonKho trong cùng transaction; lỗi rollback; cùng maYeuCau không tạo/cộng hai lần. Giá nhập không thay giá bán.
- In/pay không tăng tồn. Chỉ người lập ghi nhận trả đủ tiền; request lặp giữ mốc thanh toán. Chứng từ dùng các tên/đơn vị đã chốt theo báo cáo.
- Giữ đầy đủ luồng chính/ngoại lệ của hai module, kiểm tra input/quyền và hợp đồng nháp ở 10; không thêm nghiệp vụ mới từ các đề GaraMan khác.
- Giữ giao diện nguồn theo11, áp dụng điều chỉnh chủ bài 21/10/2026: tra cứu một bảng chung có cột Loại và select bộ lọc UI; nhập chọn part bằng bảng kết quả có nút Chọn từng dòng ngay trên hóa đơn; ngày nhập/tổng0 đúng vị trí; không lộ UUID/snapshot; không tự thêm màn duyệt trước xác nhận. Không chỉ sao chép CSS rồi thay layout. Lọc UI boLoc theo03/11, không thay loai chi tiết.

Không làm đặt lịch, tiếp nhận sửa xe, xe, phân công kỹ thuật, hóa đơn khách, thống kê hoặc quản trị danh mục đầy đủ. Không tạo lớp/bảng dự phòng cho chúng. Không yêu cầu chuẩn bị các phần/chương sau của báo cáo.

Trình tự:

1. Đọc nguồn và tóm tắt ngắn phạm vi đúng hai module, rồi bắt đầu code theo 02; không dừng ở lập lại kế hoạch.
2. Tạo bộ khung, 9 bảng/10 model, seed tài khoản/danh mục, đăng nhập hỗ trợ.
3. Hoàn thành module tra cứu và module nhập với các nhánh đã giao; dùng UI tham chiếu.
4. Build/deploy và kiểm tra hai module; kết quả chưa chạy ghi rõ. Dùng DB test riêng, không xóa dữ liệu người dùng.
5. Chuẩn bị app/docs/4.3.1_THIET_KE_TINH.md, bốn hình giao diện/lớp thiết kế, ảnh chạy thật và ma trận truy vết chỉ của hai module theo 07. Không chỉnh toàn bộ DOCX; giữ nguyên nguồn.
6. Bàn giao README chạy/demo, schema/seed, kết quả test và các file ánh xạ thật của hai module. Dừng khi các đầu ra này hoàn tất, không mở thêm module.

Nếu lần chạy trước đã tạo code ngoài phạm vi, ghi nhận và tách khỏi build/demo nếu cần; không tự xóa file hoặc DB. Nếu thiếu công cụ đọc PDF/DOCX, tìm cách trích xuất phần cần đọc, không đoán nội dung hoặc bỏ qua các hình liên quan. Nếu môi trường thiếu runtime/DB, hoàn thiện phần độc lập và ghi chính xác phần chưa kiểm chứng.

Ghi tiến độ vào app/docs/TIEN_DO.md, chỉ theo các giai đoạn hai module. Mọi hàm đưa lên hình phải khớp code và METHOD_MAP.md. Chỉ hỏi khi có mâu thuẫn thực sự chưa được bộ tài liệu giải quyết.

---

Nếu OpenCode đang làm theo prompt cũ, gửi:

```text
Dừng mở rộng phạm vi theo yêu cầu cũ. Đọc lại ke-hoach/00_PHAM_VI.md và 04_PROMPT_GEMINI.md đã sửa. Chỉ làm hai module của tôi với 9 bảng/10 model trong danh sách. Không tạo tiếp model/schema/khung chức năng cho các phần khác. Kiểm tra tiến độ hiện có, giữ file/dữ liệu đã có và tiếp tục hai module theo kế hoạch mới.
```


## Ràng buộc sửa chữa theo yêu cầu chủ bài

Giữ nguyên các màn hình, nút/thao tác và luồng gọi trong thiết kế hai module của `26_B23DCCN764-full.docx` và giao diện tham chiếu. Sửa lỗi bên trong các hàm nghiệp vụ hiện có; không tự thêm màn, route, action, DAO nghiệp vụ hoặc hàm nghiệp vụ mới để xử lý lỗi. Dùng lại POST cập nhật khi rời hóa đơn sang form thêm phụ tùng để giữ dữ liệu. UUID/version/CSRF là tham số ẩn hỗ trợ kỹ thuật, không thành chức năng mới trên giao diện. Các hàm kỹ thuật hỗ trợ đã có không phải use case mới; không vẽ chúng thành nút hoặc luồng nghiệp vụ riêng. Nếu thiết kế gốc chưa đủ để quyết định một thay đổi nghiệp vụ, nêu đúng điểm thiếu để chủ bài quyết định.
