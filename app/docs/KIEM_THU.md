# Kết quả kiểm tra hai module — 21/10/2026

## 1. Java và WAR

Maven 3.9.11 `-o -f app/pom.xml package`: **30 test đạt, 0 failure/error/skipped**, tạo `target/garaman.war`. JVM thực chạy JDK 25.0.2, POM target release 17.

| Bộ test | Số test | Phạm vi |
|---|---:|---|
| PurchaseInvoiceServlet4Test | 15 | Nháp và năm ca mới: lấy NCC–nhân viên, thiếu nhân viên, lỗi tìm part, số tiền sai/đúng |
| PurchaseInvoice4Test | 5 | Công thức tiền trong model |
| Validation4Test | 7 | Dữ liệu đầu vào |
| PasswordUtil4Test | 3 | Test sẵn có; không mở rộng tài khoản |

## 2. MySQL và trình duyệt thực tế

MySQL kiểm tra riêng tại `.review/mysql-design-test`, cổng **18098**, DB `garaman4_test`; không dùng dữ liệu/dịch vụ MySQL của người dùng. Schema/seed từ source, chỉ đổi tên DB trong bản SQL kiểm tra.

DAO trong IT dùng thống nhất `GARAMAN_TEST_DB_URL`, `GARAMAN_TEST_DB_USER`, `GARAMAN_TEST_DB_PASSWORD`. Tên DB phải kết thúc `_test`. Chọn trực tiếp IT bằng Surefire:

```powershell
mvn -o -f app/pom.xml -Dtest=dao.PurchaseInvoiceDAO4IT test
```

**2 IT đạt, 0 skipped** trên MySQL **8.0.43**: khởi tạo DAO; lưu/đọc hóa đơn với snapshot, tồn tăng 10, retry cùng ID không cộng lại, thanh toán đủ và gửi lặp không đổi tồn. Chưa chạy profile Failsafe; không suy ra rollback/đồng thời từ kết quả này.

Tomcat **9.0.98** + Edge: **19 trạng thái JSP fixture đạt**, gồm các trang khách/kho/form phụ, hóa đơn ban đầu/lỗi/đã trả, lỗi tìm part và trang lỗi chung. Fixture dùng model mẫu, không có DAO. Kết quả: `.review/jsp-render-results.json`.

Sau đó chạy **ứng dụng thật** `/garaman` với Servlet/DAO/JSP và MySQL riêng. Script `.review/check-backend-flow.cjs` đạt:

- Tra cứu ban đầu không có bảng; “lọc” trả DV1/PT1 chung bảng; chi tiết đúng loại, PT1 đúng thông số/điều kiện bảo hành DOCX; DV1 chưa có bảo hành; quay lại giữ từ khóa.
- Chọn Hòa Phát; lấy tên nhân viên qua DAO thay vì cache; nháp rỗng và dòng mới 0/0.
- Đi form thêm part giữ 10 × 80000; part mới tồn 0; lưu danh mục không tự thêm dòng; chọn rồi xóa dòng mới vẫn giữ PT1.
- Xác nhận 10 × 80000 = 800000; tồn 10 → 20 vì IT đã nhập 10 trước đó. Retry mở lại ID 102, không cộng tồn; giá bán vẫn 150000.00.
- Ô trả tiền ban đầu 0; 799999/800001 giữ input và Chưa thanh toán; in/quay lại không đổi tồn; 800000 ghi nhận Đã thanh toán.
- Đổi tên NCC/part trong DB sau xác nhận rồi tải lại: chứng từ vẫn giữ Hòa Phát và Lọc dầu động cơ AF-01.

Kết quả DB: `.review/backend-flow-results.json`. 18 ảnh prototype trong `screenshots/` không làm bằng chứng runtime. In E2E giả lập hộp thoại/sự kiện quay lại; chưa kiểm tra máy in vật lý.

## 3. Trạng thái các ca trong ma trận

| Mã | Kết quả hiện tại |
|---|---|
| C01/C02 | Đã chạy tra cứu ban đầu và “lọc” trả DV1/PT1 |
| C03 | Không dấu/hoa: chưa chạy lại E2E |
| C04/C05 | Đã chạy chi tiết dịch vụ/part với seed đã sửa |
| C06 | Kiểm tra source; chưa chạy lại E2E từ khóa rỗng |
| C07 | Đã chạy quay lại giữ từ khóa; chưa chạy lại nhánh không có kết quả |
| P01 | Đã tìm/chọn NCC; chưa chạy lại thêm NCC |
| P02/P03 | Đã thêm part tồn 0/chủ động chọn; dòng 0/0 và 10 × 80000, giữ giá bán |
| P04 | Java nháp đạt; E2E giữ dòng qua form phụ và thêm/xóa part; chưa mọi tổ hợp |
| P05 | Java validation/controller đạt; chưa mọi input sai trên browser |
| P06 | Rollback lỗi SQL: chưa chạy |
| P07 | Retry tuần tự đã chạy Java/DB/E2E; đồng thời DB chưa chạy |
| P08 | Hai mã nhập cùng part lần lượt đã chạy IT rồi E2E; đồng thời chưa chạy |
| P09/P10 | Có test Java liên quan; chưa chạy lại E2E hủy/đa tab đầy đủ |
| P11 | Đã mở bản in/quay lại, tồn không đổi; chưa kiểm tra in A4/máy in vật lý |
| P12 | Đã chạy thiếu/thừa/đủ tiền và giữ form; IT kiểm tra trả tiền lặp |
| P14 | Đã đổi tên NCC/part và đọc lại lịch sử |
| P13/P15/P16 | Phân quyền/bảo mật ngoài trọng tâm lượt sửa theo yêu cầu |

## 4. Giới hạn

Mục tiêu vẫn MySQL 8.4/Java 17; lần chạy dùng MySQL 8.0.43/JDK 25.0.2 có sẵn. Chưa nghiệm thu môi trường mục tiêu hoặc mọi ngoại lệ. XML thật tại `target/surefire-reports/`; fixture/E2E tại `.review/` là dữ liệu sinh tự động, gitignored. Seed sửa chỉ áp dụng khi nạp mới, không tự thay đổi DB đang có.

## 5. Sửa kết nối CSDL trên web đang chạy ngày 21/10/2026

Địa chỉ thực tế: `http://localhost:8080/garaman/login`, Tomcat 9.0.86 ở `E:\java\apache-tomcat-9.0.86`, MySQL 8.0.43 cổng 3306, database `garaman4`.

- Tái hiện bằng đúng Connector/J 8.4.0 và cấu hình đã deploy: SQLState `HY000`, mã 1298, `Unknown or incorrect time zone: 'Asia/Ho_Chi_Minh'`.
- Đổi `connectionTimeZone=Asia/Ho_Chi_Minh` thành `connectionTimeZone=%2B07:00`, giữ `forceConnectionTimeZoneToSession=true`. Múi giờ phiên thực tế sau sửa là `+07:00`. Offset không yêu cầu bảng múi giờ có tên trên máy chủ; xem [tài liệu Connector/J](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-datetime-types-processing.html).
- Đã cập nhật cấu hình nguồn, file mẫu, bản build và duy nhất entry `WEB-INF/classes/db.properties` của WAR đang deploy. Đối chiếu hash từng entry xác nhận các entry khác không đổi; Tomcat tự nạp lại context `/garaman`.
- JDBC đọc thành công: 2 thành viên, 3 dịch vụ, 3 NCC, 5 phụ tùng. Không thay đổi schema hoặc dữ liệu nghiệp vụ trong lượt sửa này.
- Microsoft Edge/Playwright trên chính cổng 8080: đăng nhập `khach26` và `kho26` đều thành công; tìm “lọc” trả 2 kết quả; tìm NCC “Hòa Phát” trả 1 kết quả. Không có lỗi JavaScript trong hai phiên kiểm tra.

Bản sao WAR trước sửa, kết quả kiểm tra và ảnh nằm ở `.review/sql-connection-fix/` (gitignored). Lượt này xác nhận kết nối/đăng nhập/tra cứu; không chạy lại toàn bộ nghiệp vụ nhập/thanh toán.

## 6. Bảng kết quả chọn phụ tùng — yêu cầu ngày 21/10/2026

Theo yêu cầu chủ bài, đã thay dropdown bằng bảng kết quả 5 cột Mã/Tên phụ tùng/ĐVT/Hãng sản xuất/Chọn. Nút từng dòng gửi `selectedPartId` bằng POST add-line trong form hóa đơn hiện có; bỏ JavaScript tự submit khi đổi lựa chọn dropdown. Không đổi Servlet, DAO, schema hoặc quy tắc xác nhận/tăng tồn.

Đã cập nhật hai tài nguyên JSPF/JavaScript vào WAR build và WAR trên Tomcat 9.0.86 ở `localhost:8080/garaman`; đối chiếu hash xác nhận mọi entry khác trong WAR không đổi. Jasper đã biên dịch JSP thật khi kiểm tra bằng Microsoft Edge/Playwright:

- Cả bật và tắt JavaScript: bảng đúng 5 cột, mỗi nút gửi ID của dòng tương ứng; tìm kiếm và gợi ý từ `selectedPartId` không tự thêm dòng.
- Chọn PT1 tạo dòng lượng/giá 0/0. Nhập 10 × 80000, tìm tiếp và chọn thêm vẫn giữ dữ liệu; chọn lại PT1 chỉ có một dòng và focus dòng cũ khi bật JavaScript.
- Chọn một phụ tùng khác từ bảng nhiều kết quả tạo đúng dòng mới, giữ dữ liệu PT1. Tìm không có kết quả báo rỗng và giữ các dòng đang sửa.
- Viewport 1440×1000 và 390×844: đã kiểm tra/chụp kết quả; bảng cuộn trong vùng chứa, không làm tràn ngang toàn trang. Không có lỗi JavaScript.

Các thao tác chỉ dùng bản nháp trong phiên kiểm tra, không xác nhận hóa đơn/thêm danh mục/thay đổi dữ liệu MySQL. Bản sao WAR, script, ảnh và kết quả nằm ở `.review/part-table/` (gitignored). Không chạy lại toàn bộ luồng xác nhận/in/thanh toán cho thay đổi này.

Hai PlantUML lớp/giao diện nhập đã đồng bộ sang bảng và xuất lại SVG/PNG. Các câu cần sửa trong báo cáo, bảng ví dụ và vị trí hình được ghi ở [SUA_DOCX_CHON_PHU_TUNG_DANG_BANG.md](SUA_DOCX_CHON_PHU_TUNG_DANG_BANG.md); sáu đoạn trích cũ đã đối chiếu nguyên văn với DOCX gốc, file gốc không bị sửa.
