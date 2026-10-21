# Đồng bộ tên tiếng Anh trong code — 21/10/2026

Đã đồng bộ mã nguồn ứng dụng `app/` theo `NAMING_ENGLISH.json` và bộ tên đã dùng trong báo cáo. Giữ nguyên nghiệp vụ hai module, 10 model, 9 bảng, kiểu dữ liệu, URL nghiệp vụ và lời văn giao diện tiếng Việt.

## Đã sửa

- 64 thuộc tính thực thể và 15 hàm nghiệp vụ trên hai biểu đồ đã được đối chiếu với Java. Getter/setter, Servlet, util, DTO, test, EL trong JSP và JavaScript dùng tên tiếng Anh tương ứng.
- Đổi 11 file JSP; toàn bộ `RequestDispatcher`, include, đường dẫn và biểu thức EL được cập nhật cùng nhau. Các lớp Java vốn đã có tên tiếng Anh nên giữ tên và hậu tố `4`. `SessionUser4` vẫn là DTO, không tạo bảng mới.
- Các tham số/form key dùng tên mới, ví dụ `keyword`, `itemTypeFilter`, `requestCode`, `quantity_<id>`, `purchaseUnitPrice_<id>`, `amountPaid`.
- Schema/seed/verify SQL mới dùng 50 tên cột tiếng Anh; không đổi tên 9 bảng và khóa ngoại `tbl…4id`. Có migration và rollback riêng cho CSDL cũ, không cần xóa/tạo lại bảng hay nạp lại seed.
- Đổi `serialVersionUID` của model/DTO bị ảnh hưởng để không đọc nhầm session cũ sau khi tên field thay đổi. Sau khi triển khai cần đăng nhập lại; hóa đơn đã lưu nằm trong CSDL.
- Các tài liệu hợp đồng triển khai, bảng field/hàm và PlantUML liên quan trong `app/docs` đã được đồng bộ. Prototype `giao-dien-tham-chieu/` là bản tham chiếu độc lập, không đổi tên trong lượt này.

[CSV tên cũ → mới](CODE_ENGLISH_RENAME.csv) chứa các định danh thực sự có trong code cũ. [Danh sách hàm hiện tại](METHOD_MAP.md) được lấy từ khai báo Java cuối cùng.

## Kiểm tra đã chạy

- Maven WAR: build thành công; 30 unit/controller test PASS.
- MySQL trên CSDL riêng `garaman4_english_test`: 2 integration test PASS, không bỏ qua, kiểm tra lưu hóa đơn, snapshot, tăng tồn một lần và thanh toán lặp.
- Tomcat 9.0.86 + Edge headless, CSDL test riêng: tra cứu ban đầu trống, kết quả dịch vụ/phụ tùng, chi tiết đúng loại, quay lại giữ từ khóa; tìm/chọn phụ tùng bằng bảng; thêm phụ tùng, giữ lượng/giá khi quay về nháp, xóa dòng; xác nhận/retry không cộng tồn lần hai; in; từ chối thiếu/thừa tiền và ghi nhận trả đủ; thêm nhà cung cấp. Không có lỗi JavaScript trong lượt chạy hoàn tất.
- Migration: thử trên bản sao schema cũ có seed, chạy đổi tên → rollback → đổi lại; fingerprint dữ liệu và số dòng của cả 9 bảng giữ nguyên qua cả ba bước.
- Kiểm tra tĩnh: 11 JSP mới tồn tại, mọi dispatcher/include trỏ tới file có thật; các field/hàm nghiệp vụ khớp bộ tên đã chốt.

Kết quả chi tiết cục bộ: `.review/english-code/audit.json`, `browser-check.json`, `integration-final.log`. Bản kiểm tra riêng đang chạy tại `http://localhost:18099/garaman/`; tài khoản seed như trước. Chỉ dùng CSDL test cho các thao tác thử.

## Sao lưu

- `backups/app-before-english-code-20261021.zip`: source/config/SQL trước khi đổi tên.
- `backups/docs-before-english-code-20261021.zip`: các tài liệu kỹ thuật trước khi đồng bộ.
- `backups/garaman4-before-english-code-20261021.sql`: bản dump CSDL hiện tại trước chuyển đổi. Đã tạo bằng `mysqldump --single-transaction`; checksum tại `.review/english-code/db-backup.sha256`.

## Phần chưa áp dụng vào môi trường đang dùng

**Chưa đổi cột trên CSDL `garaman4`, chưa thay WAR/chưa dừng Tomcat tại cổng 8080.** Bộ duyệt tự động từ chối bước này vì yêu cầu sửa code chưa xác nhận việc chuyển đổi CSDL đang dùng. Trang đăng nhập cũ tại 8080 vẫn trả HTTP 200.

Các file đã sẵn sàng để duyệt:

- `app/target/garaman.war`: WAR mới đã build.
- `app/database/04_migrate_english_names.sql`: đổi 50 cột sang tiếng Anh.
- `app/database/05_rollback_english_names.sql`: đổi ngược tên cột, giữ dữ liệu.
- `.review/english-code/upgrade-live.ps1`: kiểm tra đúng Tomcat 8080, sao lưu WAR, yêu cầu backup CSDL có sẵn trước khi dừng, chuyển đổi, so sánh fingerprint 9 bảng rồi triển khai và khởi động lại. Chỉ chạy sau khi chủ bài đồng ý.

MySQL không cho đổi tên cột đang được CHECK constraint sử dụng trực tiếp. Migration vì vậy DROP CHECK, đổi tên cột và ADD lại cùng điều kiện đã đổi định danh **trong một ALTER TABLE cho mỗi bảng**; không bỏ mất kiểm tra/ràng buộc nghiệp vụ. Migration gồm nhiều bảng nên toàn bộ script không phải một transaction; luôn giữ backup trước khi áp dụng và kiểm tra kết quả từng bước.

Không chạy WAR mới vào CSDL cũ khi chưa chuyển đổi tên cột. Không chạy `02_seed.sql` lên dữ liệu đang có. Tạo CSDL demo mới thì dùng `01_schema.sql` và `02_seed.sql`; cập nhật CSDL cũ thì dùng migration một lần.
