# Rút gọn code và bổ sung package diagram — 30/10/2026

Thực hiện theo yêu cầu: bỏ các gói Java hỗ trợ, giữ các lớp cơ bản để chạy hai modul; không thêm hàm ứng dụng. Model trên hình gồm toàn bộ thực thể của báo cáo; DAO/Servlet/JSP trên hình lấy đúng tên đã có trong thiết kế hai modul.

## Các file đã bỏ

| Gói | Lớp đã xóa khỏi source chính |
|---|---|
| exception | `ValidationException4` |
| dto | `SessionUser4` |
| filter | `AuthFilter4`, `EncodingFilter4` |
| util | `AppConstants4`, `DraftStore4`, `PasswordUtil4`, `Validation4`, `WebUtil4` |

Đã bỏ khai báo và mapping của hai filter trong `web.xml`. WAR không chứa các lớp Java của bốn gói này. Các ngoại lệ chuẩn của Java/JDBC như `IllegalArgumentException`, `SQLException`, `IOException`, `ServletException` vẫn được dùng trong các hàm đang có.

## Xử lí được giữ trong các hàm đang có

| Nội dung | Nơi xử lí sau khi rút gọn |
|---|---|
| UTF-8, đăng nhập, vai trò, CSRF, thông báo sau redirect | `doGet()`/`doPost()` của các Servlet đang có; JSP giữ khai báo UTF-8. |
| Kiểm tra mật khẩu PBKDF2, đổi session sau đăng nhập, đăng xuất | Các hàm `login()`/`logout()` của `AuthServlet4`; dùng API chuẩn JDK. |
| Thông tin thành viên trong session | Dùng `Member4` và kiểu thực thể kế thừa đã có; xóa mật khẩu khỏi đối tượng trước khi đưa vào session. |
| Nháp hóa đơn, phiên bản, trạng thái đang xác nhận, dữ liệu ô nhập | Các hàm đang có trong `PurchaseInvoiceServlet4` và `SparePartServlet4`; lưu bằng Map/Set trong session. |
| Kiểm tra dữ liệu đầu vào và thông báo tại trường nhập | Các hàm Servlet/DAO đang có; dùng `IllegalArgumentException`, không tạo lớp ngoại lệ riêng. |
| Trạng thái, vai trò, đơn vị, định dạng ngày | Dùng trực tiếp tại lớp đang xử lí, không tạo gói hằng số hoặc hàm định dạng mới. |
| Lưu hóa đơn, chốt thông tin, tăng tồn, chống lưu trùng, thanh toán | Giữ các hàm DAO đang có và transaction JDBC. |

Đã đổi thư mục `WEB-INF/view/warehouse` thành `WEB-INF/view/warehouseStaff`; sửa forward, JSP include và kiểm thử tương ứng. Các URL `/warehouse/...`, tên form, tên trường và các hàm nghiệp vụ giữ nguyên.

Source chính còn **24 lớp Java: 10 model, 8 DAO, 6 Servlet**. Số này gồm ba lớp hỗ trợ đăng nhập/điều hướng vốn đã có: `MemberDAO4`, `AuthServlet4`, `HomeServlet4`. Hình báo cáo chỉ thể hiện các tên đã được nêu ở phần thiết kế hai modul: 7 DAO, 4 Servlet, 10 JSP; xem [hướng dẫn đối chiếu](VE_PACKAGE_VISUAL_PARADIGM.md).

## Kiểm chứng

- Đối chiếu từng lớp còn giữ lại với bản source sao lưu trước khi sửa: **không thêm hoặc xóa phương thức**, giữ tên và tham số; thay loại ngoại lệ tự định nghĩa bằng ngoại lệ chuẩn Java. [Kết quả](diagrams/basic-packages-code-check.json).
- `mvn -o -f app/pom.xml test`: **27 kiểm thử đạt**, bao gồm đăng nhập bằng hash seed, mật khẩu sai, CSRF, phân quyền; dữ liệu DAO không hợp lệ; tính tiền; lưu nháp, xung đột phiên bản, xác nhận và gửi lại yêu cầu.
- Chạy trực tiếp `dao.PurchaseInvoiceDAO4IT` trên CSDL `garaman4_basic_test`: **2 kiểm thử đạt, 0 bỏ qua**. Kiểm tra lưu hóa đơn, tồn kho, retry và thanh toán.
- Đóng gói Maven WAR đạt. Kiểm tra trên Edge với Tomcat riêng tại cổng 18099: tìm kiếm/xem chi tiết dịch vụ và phụ tùng; tìm/chọn nhà cung cấp; tìm/chọn/thêm phụ tùng; giữ dữ liệu nháp; xóa dòng; xác nhận; gửi lại không tăng tồn lần hai; mở bản in; thanh toán thiếu/thừa/đúng; thêm nhà cung cấp. Không có lỗi JavaScript hoặc lỗi mã hóa tiếng Việt trong luồng kiểm tra.
- Kiểm tra package: đủ **23 model, 7 DAO, 4 Servlet, 10 JSP**, 6 Dependency; PDF một trang. Cập nhật deployment chỉ còn bốn gói trong WAR.

[Kết quả chạy cuối và mã SHA-256 của WAR](diagrams/basic-packages-runtime-check.json).

Môi trường thực chạy: Tomcat **9.0.86**, MySQL **8.0.43**, JDK **25.0.2** với biên dịch `--release 17`. Thiết kế mục tiêu vẫn Java 17/MySQL 8.4; lần kiểm tra này chưa chứng minh chạy trên đúng hai phiên bản mục tiêu đó. Dữ liệu kiểm thử tách riêng; không triển khai WAR lên môi trường sử dụng và không sửa các file DOCX/PDF nguồn.

## Cập nhật sửa luồng sau lần rút gọn

Ở thời điểm chỉ rút gọn, code còn giới hạn thanh toán/danh sách theo người lập và chưa lưu riêng người trả. Theo yêu cầu tiếp theo của chủ bài, các điểm này đã được sửa trong hàm hiện có, không thêm getter/setter/model. Kết quả chạy ở phần trên là bằng chứng của lần rút gọn; xem [bản sửa luồng và kiểm chứng mới](SUA_LUONG_THEO_BAO_CAO.md) cho trạng thái code sau khi sửa nghiệp vụ.
