# Kết quả sửa app trong phạm vi hàm hiện có — 19/10/2026

Theo yêu cầu chủ bài: giữ hai module, màn hình/thao tác/route và hàm nghiệp vụ hiện có. Không sửa báo cáo Word gốc, không thêm lớp nghiệp vụ hay bảng. Sửa nội dung các hàm và tham số ẩn kỹ thuật; các tên hàm trên sơ đồ phải ánh xạ code theo `app/docs/METHOD_MAP.md`.

| Mã lỗi trước sửa | Thay đổi hiện tại | Bằng chứng/giới hạn |
|---|---|---|
| B01 — Thêm/sửa/xóa mất dữ liệu vì sửa deep-copy | Các nhánh gọi lại luuBanNhap; vẫn giữ deep-copy | Test controller thêm/sửa/xóa đạt; 10 × 80000 = 800000 |
| B02 — EL dùng cộng số để nối key | Dùng c:set key chuỗi; bỏ escape dấu nháy thừa trong form mới/ảnh chi tiết | Đã sửa source JSP; chưa chạy Jasper/Tomcat 9 |
| B03 — Nút đầu trang sang form mới mất input | Giữ vị trí/nhãn nút, gửi POST update với dichDen=new-part bằng form hiện có | Test điều hướng giữ lượng/giá đạt bằng request giả lập |
| B04 — Bỏ qua version/cờ đang xác nhận | Bắt buộc version; khóa session đọc/kiểm tra/sửa/lưu; chặn sửa/hủy lúc xác nhận; form thêm part gửi/kiểm tra version | Test thiếu/cũ/đang xác nhận/hai request đồng thời đạt; JDBC ở ngoài khóa |
| B05 — Báo cáo PASS thiếu bằng chứng | KIEM_THU/TIEN_DO ghi 25 test Java, các ca DB/E2E Chưa chạy | Có báo cáo Surefire thật; không khẳng định hoàn thành 100% |
| B06 — Sơ đồ/hợp đồng ghi hàm không tồn tại | Sửa chữ ký/tên sai trong hai bản sơ đồ nhập; METHOD_MAP trích đúng chữ ký source; tuần tự bỏ khoaDuLieu không tồn tại | Không tạo thêm hàm để khớp tài liệu sai; ảnh xuất chưa nghiệm thu |
| B07 — Tự suy diễn bảo hành/thông số | Giá trị thiếu ghi Chưa cập nhật, giữ trường/bố cục | Đã sửa JSP, chưa đối chiếu ảnh app chạy thật |
| B08 — Ngày giờ/mất phần lẻ tiền | Dùng WebUtil4.dinhDangThoiDiem hiện có; tiền tối đa 2 chữ số lẻ, lượng tối đa 3 | Đã sửa source, cần kiểm tra trình duyệt |

Đồng thời sửa rollback khi ValidationException4 và kiểm tra số lượng theo đơn vị vừa khóa từ DB trong luuHoaDonNhap. Trang in gọi inHoaDon hiện có và quay về chứng từ sau hộp thoại in bằng quayLaiHoaDon hiện có. Không thêm route hoặc nút.

## Kiểm tra đã chạy

Maven offline package: 25 test, 0 failure/error/skipped; WAR tạo thành công. 10 test controller mới dùng session/request/DAO giả lập, 15 test cũ kiểm tra model/validation/password. Không chạy PurchaseInvoiceDAO4IT, MySQL thật, JSP trên Tomcat 9 hoặc E2E trình duyệt. Không kết luận giao diện giống hoàn toàn hay luồng DB đã đạt chỉ từ unit test.

## Trước khi nghiệm thu

Chạy Tomcat 9 + MySQL 8.4 với dữ liệu riêng; kiểm tra JSP từng màn, luồng nhập thực, lỗi SQL rollback/tồn không đổi, hai request xác nhận cùng UUID, đúng người lập trả đủ tiền và in không tăng tồn. Đối chiếu ảnh màn hình với giao diện nguồn và bài làm. Chưa sửa các chương/phần khác hoặc DOCX theo suy diễn.
