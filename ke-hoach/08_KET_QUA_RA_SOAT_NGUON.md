# Kết quả kiểm tra trực tiếp tài liệu và kế hoạch

Đây là ghi nhận lần đối chiếu nguồn ban đầu. App hiện đã có source; trạng thái và lỗi được kiểm tra mới tại [12](12_RA_SOAT_APP_HIEN_TAI.md), tiêu chuẩn đúng mẫu/luồng/UI tại [11](11_CHUAN_MAU_LUONG_GIAO_DIEN.md). Không dùng câu chưa có backend dưới đây như mô tả trạng thái hiện tại.

**Phạm vi đã sửa theo yêu cầu người dùng: chỉ hai module. Áp dụng [00_PHAM_VI.md](00_PHAM_VI.md); mô hình gốc trong bảng nguồn chỉ là tài liệu tham chiếu. Kế hoạch app hiện chỉ có 9 bảng và 10 model.**

Ngày rà soát: 19/10/2026. Phạm vi đợt này: kiểm tra và hoàn thiện **kế hoạch bàn giao AI**, sửa giao diện tham chiếu. Chưa triển khai backend và chưa viết hoàn chỉnh 4.3.1 vào Word.

## 1. Kết luận

Bộ 01–07 có nền tảng tốt: đúng hai module, có cây file, DAO/Servlet, schema, transaction và truy vết. **Bản trước rà soát chưa đủ kín để giao AI code mà không phải đoán**, vì chưa đề cập nguồn giao diện Garaman1, còn thiếu hợp đồng một số helper, đồng thời bản nháp, xử lý lỗi và ánh xạ kiểu khi render JSP chưa chốt hết. Các bổ sung bắt buộc nằm trong 09–10; 01–07 đã được cập nhật các điểm liên quan.

Không có kế hoạch Gemini riêng khác trong thư mục lúc kiểm tra. Đối tượng được rà soát là `ke-hoach/README.md` cùng 01–07. Các câu “người dùng đã chốt” trong bộ cũ được hiểu là quyết định được ghi lại trong bộ tài liệu có sẵn; không dùng chúng để suy diễn thêm nghiệp vụ.

## 2. Căn cứ đã đọc

| Nguồn | Vị trí kiểm tra | Kết quả |
|---|---|---|
| Ngân hàng câu hỏi | Đề 26, **trang PDF 27** | Hai chức năng chi tiết là tra cứu dịch vụ/phụ tùng và nhập phụ tùng; sau đăng nhập. Đề 27, PDF 28 chỉ tham khảo bối cảnh lịch hẹn/tiếp nhận |
| Bài giảng | **Trang in 96–97, PDF 97–98** | 4.3.1 có bốn bước: giao diện, lớp giao diện/thuộc tính ẩn, DAO với đầu vào/đầu ra, thực thể và quan hệ |
| Bài giảng | Trang in 98–103, PDF 99–104, Hình 4.3–4.8 | Cần cả sơ đồ giao diện và biểu đồ lớp thiết kế. Hình 4.8 nằm ở PDF 104, bổ sung cho dải PDF ghi thiếu ở bản 07 cũ |
| LAB1 | Exercise 1; đoạn Tools and technologies; JavaBean/UserDAO/UserServlet | Dùng JSP + Servlet + JDBC + MySQL + DAO. Bản mẫu nêu Tomcat 8.5, Java 8+, `javax.servlet`; Tomcat 9/Java 17/Maven là lựa chọn triển khai tương thích, không phải phiên bản nguyên văn LAB1 |
| `26_B23DCCN764-full.docx` | Kịch bản 1.2.1; Hình 1.5/1.6; Hình 2.1/2.2; cuối file | 4.3.1 mới có tiêu đề a/b; chỉ dùng phần hai module và quan hệ/thuộc tính liên quan. Hình 2.2 **đã là `tonKho decimal(12,3)`**, không còn là integer |
| Giao diện Garaman1 | `web/*.jsp`, `assets/app.js`, JSPF, README, checks | Luồng chính đúng: tìm rồi xem; chọn NCC → dropdown part trên hóa đơn → xác nhận trực tiếp → in/pay. Đây là prototype sessionStorage, chưa có backend |

Mô hình tổng thể trong DOCX chỉ dùng tra cứu quan hệ/thuộc tính liên quan; không cài đặt những phần ngoài hai module. Đã đọc XML và xem trực tiếp hình lớp thiết kế, ERD nhúng trong DOCX; không chỉ đối chiếu phần chữ. Công cụ đọc lại nguồn: `tools/extract_sources.py` (Python 3 + PySide6, chỉ đọc nguồn, ghi kết quả vào `.review/`). Định danh đoạn `[Pnnnn]` là thứ tự đoạn XML do công cụ này tạo, **không phải số trang Word**.

| File nguồn trong Garaman | SHA-256 lúc kiểm tra |
|---|---|
| 26_B23DCCN764-full.docx | `d29fe3d0d462ff6a868dc951ec039389ee0da1213097ed4f3b9e6ee09dcb984f` |
| BG HP TTTN 2 CNPM 2020 final.pdf | `416f0401a70297300419d3abe6a72c4d3d816635e4f58c4b6cc5cd52a865d389` |
| LAB1-JavaProgrammingBasic.docx | `0ff9249bc697ab867386b7512b37ea3f4e5d755ffac32a8f9f7d1319f603f1bf` |
| NganHangCauHoi-PhanTichThietKe-Capnhat.2019.pdf | `d8bbceb913e7cee15c01d33d7d1550ad1198d0564ed1458761248d97a0f56833` |

Nếu hash khác, đọc lại phần thay đổi; không áp máy móc kết quả trên bản mới. Báo cáo ở **Garaman** là nguồn chính, không tự chọn `full 1.docx` ở Garaman1 chỉ vì tên tương tự hoặc thời gian mới hơn.

## 3. Các thiếu sót và cách đóng

| Mã | Vấn đề trước rà soát | Cách xử lý |
|---|---|---|
| R01 | Kế hoạch chưa nhắc Garaman1, có nguy cơ AI tự dựng lại màn hình | File 09 ánh xạ từng JSP/JSPF/asset; bản giao diện sửa trong `../giao-dien-tham-chieu/` |
| R02 | Garaman1 có hai JSP chi tiết; kế hoạch có một JSP chi tiết | Gộp HTML hai trang thành hai nhánh `loai` ở `gdChiTietThongTin.jsp`; giữ đủ trường từng loại |
| R03 | Garaman1 dùng Tomcat 10.1/web.xml Jakarta; app chọn Tomcat 9/javax | Chỉ lấy bố cục/HTML/CSS, viết web.xml 4.0 mới; không chép WAR/runtime/web.xml cũ |
| R04 | Hóa đơn đã xác nhận/in còn địa chỉ, điện thoại, email NCC và hãng | Đã sửa trong bản tham chiếu; giữ các thông tin đối chiếu trước xác nhận; HTML fallback và JS render cùng bỏ cột hãng |
| R05 | Form part thiếu Chưa công bố, cho tạo Ngừng kinh doanh | Mặc định Chưa công bố; chỉ hai lựa chọn khi tạo; chặn công bố thiếu giá/mô tả |
| R06 | Hãng tùy chọn ở UI nhưng NN trong ERD/03; đơn vị nhập tự do có Hộp | Hãng bắt buộc theo hợp đồng đã có; select Cái/Chiếc/Bộ/Lít; Lít step 0.001; không thêm Hộp vào schema |
| R07 | Upload ảnh trong UI dù kế hoạch chỉ dùng asset cục bộ | Chọn ảnh SVG có sẵn; không phát sinh upload Servlet/thư mục/thư viện mới |
| R08 | 01 yêu cầu “sửa tồn integer” dù ERD đã decimal | Đổi thành kiểm tra đã đạt, chỉ còn mâu thuẫn chữ “loại tồn dẫn xuất” |
| R09 | Chưa nói cách xử lý stale form/cùng draft trong khi confirm | File 10 quy định version + cờ đang xác nhận, HTTP 409, không khóa cả session khi gọi JDBC |
| R10 | Chưa khóa precedence lỗi pay/retry, throws, cấu hình DB, thời điểm JSP | File 10 chốt hành vi, helper, mapping filter và cấu hình; cập nhật 02/03 |
| R11 | Schema SQL dự kiến có TEXT/độ dài khác ERD VARCHAR(255) | Ghi khác biệt trong từ điển và sơ đồ tập con 9 bảng, không yêu cầu sửa ERD tổng thể |
| R12 | Bước 5 lớp thực thể còn nói người trả tiền riêng; kịch bản 19/18b | Giữ danh sách TÌM→THAY file 01, bổ sung input kiểm tra người lập ở pha phân tích và test quyền |
| R13 | Kế hoạch cũ yêu cầu model/schema cả hệ thống dù chỉ code hai chức năng | Đã bỏ khỏi toàn bộ kế hoạch; 00 khóa 9 bảng/10 model, không tạo cả POJO khung ngoài phạm vi; 04 đã cập nhật cho OpenCode/DeepSeek |

## 4. Những điểm liên quan hai module cần ghi đối chiếu

Các đoạn liên quan hai module được ghi đối chiếu ở 01: bước 19 `[P0624]`, ngoại lệ 18b `[P0634]`, quan hệ đối tượng hóa đơn nhập `[P0702]`, loại tồn `[P1087]`. Các cụm “tồn tính từ các giao dịch” ở `[P0334]`, `[P0607]` và `[P0813]` cần tìm theo chữ vì số đoạn có thể thay đổi. Chỉ bàn giao ghi chú và nội dung 4.3.1 hai module; không tự sửa Word gốc.

Ở mô tả phương thức ghi nhận thanh toán `[P0836–P0837]`, bổ sung kiểm tra nhân viên hiện tại là người lập vào điều kiện và input; không thêm thuộc tính nhân viên trả tiền. Ở UC và lời dẫn có tham chiếu Hình 3.2/3.3 trong khi caption hiện là 1.2/1.3: lập bảng số hình rồi sửa đúng tham chiếu, không đổi số trong tên lớp. Phần đầu đánh Chương I/II trong khi cuối dùng 4.3: cần thống nhất hệ thống số ở ghi chú đối chiếu; không yêu cầu sửa hoặc đánh số lại toàn bộ báo cáo trong lần code hai module.

Không yêu cầu vẽ lại toàn bộ phân tích vì thay `float` bằng `BigDecimal` hoặc thêm Servlet. Bước chuyển từ phân tích sang thiết kế chính là chỗ giải thích các thay đổi này. Các hình nguyên gốc giữ để đối chiếu; phiên bản thiết kế/code phải cùng chữ ký và quan hệ. Chưa có code thì các chữ ký trong kế hoạch là **hợp đồng cần thực hiện**, chưa phải bằng chứng “hàm thật đã chạy”.

## 5. Mức bảo đảm

Không thể bảo đảm một AI sẽ không tự bịa chỉ bằng độ dài prompt. Cơ chế kiểm soát là: nguồn có vị trí/hash → 02/03/09/10 khóa hợp đồng → mỗi thay đổi có truy vết → kiểm tra code/build/test thực tế → cập nhật hình theo code. Khi gặp thiếu yêu cầu nghiệp vụ, ghi vị trí và hỏi; không mở thêm module từ các đề GaraMan khác. Helper private được bổ sung nhưng phải có trong code nếu đưa lên hình thiết kế.

Kết quả chạy giao diện ghi riêng tại `../giao-dien-tham-chieu/docs/KET_QUA_KIEM_TRA.md`. Các ca backend C01/P06/P07/P12/P13 và integration test hiện là **kế hoạch**, chưa được chạy trong đợt rà soát.
