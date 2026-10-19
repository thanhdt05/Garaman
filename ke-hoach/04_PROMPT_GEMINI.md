# Prompt giao việc cho Gemini — bản cuối

Copy toàn bộ khối bên dưới, cung cấp `ke-hoach/`, `giao-dien-tham-chieu/` và bốn tài liệu gốc. File 02 là kế hoạch chính; 03 khóa dữ liệu/route; 07 khóa báo cáo; 08 ghi kết quả rà soát nguồn; 09 ánh xạ từng file giao diện; 10 đóng các hợp đồng kỹ thuật còn thiếu. Đây là một phương án thực hiện, không yêu cầu lập lại phương án để chọn.

---

Thực hiện GaraMan đề 26 cho Đàm Tiến Thành, B23DCCN764. Mục tiêu là code giao diện/backend/CSDL chạy được làm căn cứ viết mục 4.3.1 Thiết kế tĩnh đúng mẫu bài giảng, bám sát báo cáo đã có, và chuẩn bị dữ liệu đối chiếu cho các phần sau.

Đọc bốn tài liệu trong thư mục:

1. `26_B23DCCN764-full.docx`: dùng bản mới nhất, là phần người dùng đã làm, không khởi tạo lại mô hình khác.
2. `BG HP TTTN 2 CNPM 2020 final.pdf`: đặc biệt 4.3.1 với bốn bước, ví dụ Hình 4.3–4.8; đọc 4.3.2–4.3.3 để chuẩn bị đối chiếu.
3. `NganHangCauHoi-PhanTichThietKe-Capnhat.2019.pdf`: ĐỀ SỐ 26; đề GaraMan khác chỉ để hiểu ngữ cảnh, không tăng phạm vi code.
4. `LAB1-JavaProgrammingBasic.docx`: cách tổ chức JSP/Servlet/JDBC/MySQL/DAO.

Đọc toàn bộ các file hiện tại trong `ke-hoach/`, bắt đầu README. Tuân thủ 01 về những sửa cuối trong báo cáo; 02 về cấu trúc/file/hàm/trình tự/test; 03 về schema và giao tiếp; 05 về thuộc tính; 07 về bố cục và ma trận báo cáo. Không sử dụng những yêu cầu đã bị thay thế trong lịch sử trò chuyện.

Đọc bắt buộc 08/09/10. Báo cáo nguồn chính ở Garaman có hash trong 08; không lấy DOCX khác trong Garaman1 thay thế. Tái sử dụng bố cục HTML/CSS/SVG của `giao-dien-tham-chieu/web` theo từng hàng 09, không tự thiết kế lại hoặc nhập JS sessionStorage/HTML hóa đơn fallback sang app thật. Các sửa UI đã có không chứng minh backend đã hoạt động. Chữ ký/throws, filter/config, draftVersion/409, giới hạn số, retry/pay và render LocalDateTime theo 10.

Tạo app/docs/METHOD_MAP.md cùng MODEL_FIELD_MAP.md; mọi hàm xuất hiện ở class/sequence diagram phải tìm được đúng chữ ký và nơi gọi trong code. Helper private được phép thêm để phân rã nhưng phải liệt kê nếu xuất hiện trên hình; không tự đặt tên một method trên sơ đồ rồi bỏ trống code. Mọi mục ngoài phạm vi chỉ ghi chuẩn bị/chưa thực hiện.

Các quyết định cố định:

- Chỉ code hai module tra cứu dịch vụ/phụ tùng và nhập phụ tùng cùng đăng nhập/đăng xuất. Mô hình/16 bảng vẫn toàn hệ thống. Các module đặt lịch/tiếp nhận/thanh toán khách/thống kê chưa code giao diện/backend.
- Giữ hậu tố 4, tên lớp/bảng/field/route, quan hệ và thông tin áp dụng trong bản đã làm. Danh mục 14 trường file 05 phần lớn đã có trong DOCX, không thêm lại hoặc tự tăng lên 16 trường. Không thêm các bản lịch sử hồ sơ đã bị loại.
- Lưu `tblSparePart4.tonKho DECIMAL(12,3) NOT NULL DEFAULT 0`, CHECK không âm. `tangTonSauNhap` trong thiết kế chuyển sang SparePartDAO4 với Connection/id/qty; được gọi trong transaction hóa đơn. Không bỏ hình giao tiếp phân tích tăng tồn, không thay tồn lưu bằng view tính tổng giao dịch. Không lưu thanhTien/tongTien thành cột.
- Mỗi hóa đơn có một người lập và chính người đó thực hiện thu/chi; không thêm object hay FK nhân viên thanh toán riêng. Phiếu tiếp nhận có nhân viên tiếp nhận riêng, có thể khác người lập hóa đơn khách.
- Hóa đơn nhập chưa trả được mở lại bởi người lập; danh sách lọc theo người đang đăng nhập. Người khác thuộc kho có thể đọc/in theo quyền nhưng không pay thay. Kiểm tra phía server/DAO, không chỉ ẩn nút.
- Giữ hai quan hệ tùy chọn: Appointment–RepairIntake là 0..1 ở hai đầu; RepairIntake–CustomerInvoice là 1 ở đầu phiếu và 0..1 ở đầu hóa đơn; FK intake trong invoice NOT NULL UNIQUE, FK appointment trong intake NULL UNIQUE.
- Chọn Java17/Maven WAR/Tomcat9/javax.servlet/JSP/JSTL/JDBC/MySQL8.4 với dependency khóa trong file 02; không đổi sang Spring/React/JPA, không trộn jakarta/javax. JSP render, Servlet điều phối, DAO SQL/transaction, model chứa dữ liệu/tính toán.
- Tiền/lượng dùng BigDecimal/DECIMAL; thời điểm Java LocalDateTime/MySQL DATETIME, ngày sinh LocalDate/DATE. Hình ERD Paradigm giữ nhãn date được, có ghi chú ánh xạ; không ép đổi công cụ.

Làm theo thứ tự:

1. Tạo `app/docs/BAO_CAO_DA_SUA.docx` từ bản sao DOCX mới. Áp đúng các sửa còn tồn tại trong file 01, không lặp những gì người dùng đã sửa. Không sửa file gốc. Ghi `THAY_DOI_SO_VOI_BAO_CAO.md` theo vị trí/câu/hình, không dùng nhật ký thay bản sửa.
2. Tạo schema/model, `TU_DIEN_DU_LIEU.md` và `MODEL_FIELD_MAP.md`. Ghi rõ field từ hình → Java → SQL hoặc object/dẫn xuất. Khóa 16 bảng và các FK/NULL/UNIQUE theo file 03.
3. Thực hiện các giai đoạn 1–7 file 02 tại `app/`: bộ khung, DB/model, đăng nhập, tra cứu, thêm/chọn NCC/part, nháp, xác nhận/in/pay. Mọi file/hàm được giao phải có triển khai thực, không dừng ở TODO.
4. Build, chạy unit/integration test thích hợp, deploy và chạy các ca C01–C07/P01–P16 cùng biến thể tồn/rollback. Integration test dùng DB `_test` riêng, không xóa DB demo/người dùng.
5. Hoàn thiện 4.3.1 theo file 07: từng module có đủ bốn bước, bốn hình chính, ảnh thật và chữ ký hàm thật. Xuất sơ đồ nguồn sang SVG/PNG, chèn nội dung/hình vào bản sao báo cáo với caption/tham chiếu đúng số.
6. Tạo ma trận truy vết và `DOI_CHIEU_PHAN_SAU.md` để làm tiếp 4.3.2/4.3.3/Chương5–6. Chỉ chuẩn bị đầu vào/hướng dẫn cho các mục này; không gọi chúng đã hoàn thành, không viết thêm module ngoài đề.
7. Bàn giao README chạy từ schema/seed, cấu hình, build/deploy, URL/tài khoản demo, kết quả test thật và giới hạn thực tế.

Các điểm bắt buộc kiểm chứng:

- Tìm theo tên chứa từ khóa, trang đầu chưa có kết quả; phân biệt service/part khi id trùng; quay về giữ từ khóa; chỉ công bố mục hợp lệ cho khách.
- Nháp theo UUID trong session, hai tab không trộn; tìm/chuyển form thêm part không mất input; part trùng focus dòng cũ; dòng 0/0 không được xác nhận.
- INSERT header, INSERT chi tiết và UPDATE cộng tồn cùng Connection/transaction. Một lỗi rollback mọi thay đổi. Hai request cùng maYeuCau chỉ tạo/cộng một lần; hai mã khác nhau nhập cùng part đều cộng, không mất cập nhật.
- Retry sau mất response hoặc sau xóa nháp vẫn tìm id đã lưu. Chống lặp bằng UNIQUE và transaction, không chỉ disable nút.
- In và pay không đổi tồn. Pay đúng tổng, chỉ người lập, request lặp giữ mốc cũ. Không lưu trả góp/đặt cọc hoặc tạo bảng Payment.
- Bản in dùng tên NCC/nhân viên/part/đơn vị đã chốt; đổi master không đổi bản in cũ. Không in thêm địa chỉ/điện thoại NCC hay hãng part ngoài phạm vi đã thống nhất.
- Input tổng/người lập/trạng thái/thời điểm từ client không làm căn cứ lưu; PreparedStatement, HTML escape, CSRF và kiểm tra quyền theo file 03.
- Ma trận TC/NH file 07 nối từng bước/ngoại lệ DOCX tới JSP → route → Servlet → DAO/model → bảng/cột → test → hình. Hình thiết kế có Servlet thật theo Lab1; không vẽ JSP gọi DAO trực tiếp nếu code không làm vậy.

Nếu thiếu MySQL/Tomcat/công cụ xuất hình/chỉnh DOCX, vẫn hoàn thiện phần độc lập; ghi chính xác phần chưa chạy/chưa xuất/chưa chèn và bàn giao nguồn/vị trí sẵn dùng. Không báo test pass, ảnh thật hoặc DOCX hoàn tất khi chưa kiểm chứng. Không đổi công nghệ/nghiệp vụ để che thiếu môi trường.

Không hỏi lại các quyết định đã chốt. Có thể thêm helper private nhỏ phục vụ triển khai; không âm thầm thêm module, public API, bảng/cột hoặc quan hệ ngoài hợp đồng. Nếu phát hiện mâu thuẫn thực sự, ghi vị trí và đối chiếu quyết định cuối; không tự sáng tác quy tắc mới.
