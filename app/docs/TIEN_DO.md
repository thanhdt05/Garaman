# Tiến độ hai module — 21/10/2026

Giữ 10 model, 9 bảng và các màn/route/thao tác đã chốt. Lượt sửa này tập trung nghiệp vụ, không mở rộng bảo mật hoặc tài khoản.

| Phần | Trạng thái có bằng chứng |
|---|---|
| Nháp, lấy nhân viên, lỗi tìm part, số tiền sai/đúng | 15 test controller giả lập đạt |
| Model/validation/password hiện có | 15 test Java đạt |
| WAR | Package thành công target/garaman.war |
| JSP trên Tomcat 9.0.98 | 19 trạng thái fixture đạt bằng Edge, gồm trang lỗi |
| DAO lưu/đọc/retry/thanh toán/tồn | 2 IT đạt trên MySQL 8.0.43 riêng |
| Hai luồng thực tế | Tra cứu và nhập/xác nhận/in/trả tiền chạy bằng Edge + DAO thật |
| MySQL 8.4 / runtime Java 17 | Chưa nghiệm thu; JVM thực chạy 25.0.2, target compile 17 |
| Đồng thời/rollback DB và mọi ngoại lệ | Chưa kiểm tra đầy đủ |
| Thiết kế chi tiết | Markdown/ma trận/METHOD_MAP/PlantUML đã đồng bộ; chưa xuất đủ bốn hình cuối |
| Word và ảnh mẫu | Không sửa Word; 18 PNG có sẵn vẫn là prototype |

Xem [kết quả sửa](../../ke-hoach/14_KET_QUA_SUA_KHOP_THIET_KE.md) và [kiểm thử](KIEM_THU.md). Rà soát 13 là trạng thái trước sửa.
