# Kết quả sửa mã bám thiết kế hai module — 21/10/2026

Theo yêu cầu chủ bài: tập trung hai module đã phân tích, không mở rộng/sửa bảo mật, phân quyền hoặc đăng nhập/đăng ký. Giữ nguyên màn hình, route, thao tác và 10 model/9 bảng; không thêm hàm nghiệp vụ hay DAO nghiệp vụ. DOCX/PDF gốc và bản sao Word cũ giữ nguyên.

## Thay đổi đã thực hiện

| Mục từ rà soát 13 | Thay đổi | Kiểm chứng |
|---|---|---|
| R01 — Số tiền đã trả | JSP dùng số thông thường, ban đầu 0 theo mẫu; khi sai giữ số đã nhập, không định dạng tiền tiếng Việt vào HTML number | Tomcat 9 + Edge: ban đầu 0; lỗi 799999/800001 giữ input |
| R05 — Lấy nhân viên kho | `batDauNhap` gọi `SupplierDAO4.layThongTinNhaCungCap` rồi `WarehouseStaffDAO4.layThongTinNhanVienKho` trước khi tạo nháp | Test Java kiểm tra thứ tự; E2E đổi tên nhân viên trong DB sau khi tạo phiên, hóa đơn lấy đúng tên mới từ DAO |
| R07 — Lỗi tìm part | Truyền `partSearchError`, picker báo lỗi truy vấn thay vì không có part | Test controller lỗi SQL và JSP fixture |
| R07 — Số tiền sai | `ghiNhanThanhToan` dùng lại `xemHoaDon` để báo lỗi trên chứng từ, giữ input; không đưa số tiền sai sang trang lỗi chung | Test rỗng/0/thiếu/thừa; E2E thiếu/thừa vẫn Chưa thanh toán, trả đủ thành công |
| Lỗi phát hiện khi chạy JSP | Trang lỗi chung đổi dynamic include `.jspf` sang static include, đặt tiêu đề bằng request attribute | Trước sửa mã fragment xuất nguyên ra trang; sau sửa đạt |
| R09 — Seed | NCC1 Hòa Phát/Hà Nội; PT1 đúng mô tả/thông số/điều kiện bảo hành DOCX; bỏ bảo hành DV1 tự suy diễn | Import SQL vào DB riêng và đọc chi tiết qua browser |
| R08 — DB của IT | DAO dùng đúng `GARAMAN_TEST_DB_*`, kiểm tra tên DB kết thúc `_test`; bổ sung assert tồn, retry, thanh toán | 2 IT đạt, 0 skipped trên DB riêng |
| R02/R04 — Tài liệu/sơ đồ | Đồng bộ 4.3.1, ma trận cột/hàm, METHOD_MAP, URL `loai=part`, field private, hàm tính tiền, bội số, lời gọi DAO và batch chi tiết | Các bản `.puml` tên tiếng Việt cùng nội dung với bản canonical |
| R06 — Bằng chứng | README ghi đúng 18 ảnh có sẵn là prototype; bỏ khẳng định đồng bộ 100% và Word đã hoàn tất | Kết quả runtime thật lưu riêng, không ghi đè ảnh mẫu để giả làm ảnh backend |

R03/R10/R11 về bảo mật/phiên đăng nhập không xử lý trong lượt này theo yêu cầu. Quy tắc người lập/trả tiền hiện có được giữ. Các quyết định tại `01` tiếp tục áp dụng: tồn lưu trực tiếp; tổng tiền/thành tiền dẫn xuất; chứng từ sau xác nhận có sáu cột. Không đảo lại mô hình vì các đoạn lời văn gốc đang mâu thuẫn.

## Kiểm tra đã chạy

- Maven 3.9.11 offline `-f app/pom.xml package`: **30 test Java đạt**, 0 failure/error/skipped; tạo `app/target/garaman.war`. JVM là JDK 25.0.2, target biên dịch release 17.
- Chọn IT trực tiếp bằng `-Dtest=dao.PurchaseInvoiceDAO4IT test`: **2 test đạt**, 0 skipped, trên MySQL **8.0.43**, `127.0.0.1:18098/garaman4_test`. Không chạy profile Failsafe.
- Tomcat **9.0.98** (gói Apache đã xác minh SHA-512) + Edge: **19 trạng thái JSP fixture đạt**, gồm trang lỗi, hóa đơn ban đầu/lỗi/đã trả và lỗi tìm part. Fixture không truy cập DAO/MySQL.
- Edge chạy **ứng dụng thật** `/garaman`, Servlet/DAO/JSP với schema/seed trên MySQL riêng: tra cứu; chọn NCC; lấy nhân viên; thêm part tồn 0 rồi chủ động chọn; giữ lượng/giá khi đi form phụ; xác nhận; retry; in/quay lại; thanh toán thiếu/thừa/đủ; kiểm tra lịch sử sau đổi tên danh mục.
- Chứng từ E2E ID 102: PT1 tồn **10 → 20** vì IT trước đó đã nhập 10; riêng E2E tăng đúng 10. Giá bán vẫn 150000.00; retry/in/thanh toán không cộng lại. Các tên chốt trên chứng từ không đổi khi master đổi.

Bằng chứng: `app/target/surefire-reports/`, `.review/jsp-render-results.json`, `.review/backend-flow-results.json`. Script: `.review/check-jsp-render.cjs`, `.review/check-backend-flow.cjs`, `.review/ReviewRenderServlet.java`. Các đầu ra sinh tự động là gitignored.

## Giới hạn

- Mục tiêu vẫn **MySQL 8.4 / Java 17**; lần chạy thực dùng MySQL 8.0.43/JDK 25.0.2 có sẵn. Không tuyên bố đã nghiệm thu môi trường mục tiêu.
- Chưa chạy lỗi SQL để chứng minh rollback toàn bộ, các ca DB đồng thời và mọi ngoại lệ trong kế hoạch. In E2E giả lập hộp thoại/sự kiện quay lại, không kiểm tra máy in vật lý.
- Markdown/nguồn sơ đồ đã sửa; chưa xuất/ghép đủ bốn hình cuối hoặc cập nhật bản sao Word.
- Seed sửa chỉ tác động lần nạp mới. Không tự cập nhật/xóa/nạp lại CSDL đang có. MySQL kiểm tra khởi tạo riêng trong `.review/mysql-design-test`; không dùng datadir hay khởi động dịch vụ MySQL80 của người dùng.

Mã hiện bám thao tác và dữ liệu đã chốt của hai module. Dùng Markdown/nguồn sơ đồ đã cập nhật để làm tiếp báo cáo, ghi kết quả kiểm chứng đúng theo phạm vi trên.
