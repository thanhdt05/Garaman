# Bố trí đường nối activity diagram

Hình nhập hiện theo [phạm vi phân tích](../../PHAM_VI_ACTIVITY_NHAP_PHU_TUNG.md), đã thu gọn các nhánh kỹ thuật khỏi hình chính. Khi định tuyến, giữ nguyên luồng nghiệp vụ của bản đã thu gọn; chỉ đổi vị trí, kích thước ô cần nhiều điểm gắn, đường nối và nhãn.

Yêu cầu ưu tiên của bản hiện tại: **không dùng chung đoạn, không dùng chung điểm gắn**. Giảm số giao cắt là tiêu chí sau hai điều kiện này.

- Mỗi đầu của từng đường nối có tọa độ riêng trên ô, kể cả khi nhiều đường cùng đi vào hoặc đi ra một ô Hiển thị GD.
- Một đoạn ngang/dọc chỉ thuộc một đường. Bộ định tuyến cấm chồng đoạn, thay vì chỉ cộng điểm phạt như bản trước.
- Hai đường chỉ được giao vuông góc rồi tiếp tục đi thẳng qua giao điểm; không được chạm tại góc gấp hoặc đầu mút để tạo cảm giác nhập nhánh.
- Đường hai đầu mũi tên DAO là một connector gọi–trả, không phải hai đường điều khiển ghép chung.
- SVG có khoảng trắng nhỏ tại giao cắt để phân biệt đường đi qua và đường bên dưới. Bản drawio giữ các tuyến độc lập và các điểm gắn riêng.

| Kiểm tra bản hiện tại | Tra cứu | Nhập phụ tùng |
|---|---:|---:|
| Độ dài đoạn chồng giữa các đường | 0 | 0 |
| Điểm đầu/cuối dùng chung | 0 | 0 |
| Chạm nhau tại góc gấp/đầu mút | 0 | 0 |
| Đường xuyên ô hành động khác | 0 | 0 |
| Giao cắt vuông góc | 3 | 12 |

Các bản trước chỉ ưu tiên giảm giao cắt nhưng còn dùng chung đoạn và điểm gắn, gây khó lần theo đường. Không dùng số giao cắt thấp để thay thế điều kiện tách riêng từng đường. Chưa tuyên bố đây là tối ưu toán học toàn cục.

Nguồn bố trí: `layout_activity.py`. Chạy `python3 render_activity.py`, rồi `python3 export_activity.py` để sinh SVG/PNG/PDF/drawio/JSON. Chạy `python3 check_activity_layout.py` để kiểm tra cả giao cắt, chồng đoạn, trùng đầu nối, chạm góc và xuyên ô. Kết quả đối chiếu với bản trước ghi ở `activity-layout-check.json`.

PDF tra cứu: A3 ngang; PDF nhập phụ tùng: A1 dọc. SVG có thể phóng to để đọc từng đường. Bản drawio giữ điểm gấp khúc và vị trí nhãn để chỉnh sửa tiếp.
