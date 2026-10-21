# Kết luận sau sửa phạm vi — chỉ hai module

Áp dụng [00_PHAM_VI.md](00_PHAM_VI.md). Yêu cầu cũ tạo đủ model/schema hệ thống đã được bỏ khỏi kế hoạch.

- Chỉ triển khai khách tra cứu dịch vụ/phụ tùng và kho nhập phụ tùng theo bài làm đề 26.
- Đăng nhập/đăng xuất chỉ hỗ trợ hai vai trò Customer/WarehouseStaff, dùng tài khoản seed.
- Chỉ 9 bảng và 10 model liên quan; không tạo thêm POJO/DDL/khung chức năng ngoài phạm vi.
- Giữ tên/quan hệ liên quan, tonKho lưu trực tiếp, một người lập/trả tiền trên hóa đơn nhập. Hình CSDL gốc đã có tonKho decimal(12,3), không sửa lặp.
- Giao diện tham chiếu đã sửa giữ làm đầu vào; chuyển từng file theo 09, backend theo 02/03/10.
- Chuẩn bị nội dung 4.3.1 và bốn hình thiết kế của hai module theo 07; giữ nguyên DOCX và mô hình tổng thể. Không yêu cầu sửa cả Word hoặc chuẩn bị phần/chương sau.

Các điểm lời văn còn lệch liên quan hai module được ghi ở 01 để thiết kế/code thống nhất. Đầu ra hoàn thành khi hai module chạy, có kết quả test thật và tài liệu tương ứng; không lấy các chức năng khác làm điều kiện nghiệm thu.
