# Kế hoạch cuối cho Gemini — GaraMan đề 26

**Đã chốt một phương án:** giữ hai module đề 26 và mô hình bạn đã làm; lưu `tonKho` trực tiếp; mỗi hóa đơn có một nhân viên lập, chính người đó thực hiện thu/chi. Nhân viên tiếp nhận lưu riêng trên phiếu. Không thêm người thanh toán hoặc FK thu/chi riêng.

File kế hoạch chính là [02_KE_HOACH.md](02_KE_HOACH.md), gồm cây thư mục, từng file/hàm, trình tự triển khai và kiểm thử. Các file còn lại bổ trợ cùng một phương án:

| File | Đọc để làm gì |
|---|---|
| [01_DANH_GIA.md](01_DANH_GIA.md) | Những chỗ cuối cần đồng bộ trong bản DOCX mới, có TÌM → THAY; không áp lại danh sách sửa của các lần trước |
| [02_KE_HOACH.md](02_KE_HOACH.md) | Phạm vi, công nghệ, cấu trúc project, model/DAO/Servlet/JSP, hàm, transaction, trình tự và nghiệm thu |
| [03_HOP_DONG.md](03_HOP_DONG.md) | 16 bảng, kiểu/ràng buộc, quan hệ, URL/HTTP, params, request/session, ánh xạ từ phân tích |
| [04_PROMPT_GEMINI.md](04_PROMPT_GEMINI.md) | Prompt có thể copy giao Gemini thực hiện |
| [05_RA_SOAT_THUOC_TINH_BO_SUNG.md](05_RA_SOAT_THUOC_TINH_BO_SUNG.md) | Danh mục 14 trường giữ đã phần lớn có trong DOCX; không thêm lặp; cột tồn giữ lại |
| [06_KIEM_TRA_BAN_MOI.md](06_KIEM_TRA_BAN_MOI.md) | Kết luận rà soát và quyết định cuối, thay thế kết luận cũ |
| [07_DOI_CHIEU_BAO_CAO.md](07_DOI_CHIEU_BAO_CAO.md) | Mẫu triển khai 4.3.1 theo bốn bước bài giảng; ma trận và cách dùng tiếp cho 4.3.2, 4.3.3, Chương 5–6 |

Thứ tự thực hiện: đọc README → bốn tài liệu nguồn → 01/05 → 03 → 02 → 07; dùng 04 để giao việc. Toàn bộ file trong thư mục hiện tại là bản cuối, không có lựa chọn song song về tồn hay người thanh toán. Không lấy các chỉ dẫn cũ trong lịch sử trò chuyện áp ngược vào kế hoạch này.

Đầu ra đợt thực hiện: project JSP/Servlet/JDBC/DAO/MySQL chạy được hai module và đăng nhập; bản sao báo cáo đã đồng bộ, phần 4.3.1 đầy đủ; sơ đồ nguồn và ảnh giao diện thật; ma trận truy vết và kết quả kiểm thử. Các phần 4.3.2 trở đi có hướng dẫn đối chiếu và đầu vào chuẩn bị, chưa được gọi là đã hoàn thành.

**Trạng thái hiện tại:** mới chốt kế hoạch, chưa code ứng dụng hoặc sửa DOCX gốc. Không được quảng bá các kiểm thử dự kiến là đã chạy.
