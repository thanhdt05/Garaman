# Thiết kế giao diện tối giản cho hai mô-đun

- [Tra cứu dịch vụ / phụ tùng — SVG](catalog-ui-simple.svg) · [PNG](catalog-ui-simple.png)
- [Nhân viên kho nhập phụ tùng — SVG](purchase-ui-simple.svg) · [PNG](purchase-ui-simple.png)

Căn cứ hình thức: mục 4.3.1, trang in 96–98 (trang PDF 97–99), đặc biệt **Hình 4.3** trong `BG HP TTTN 2 CNPM 2020 final.pdf`: các khung giao diện đơn giản có trường dữ liệu, bảng, nút và mũi tên chuyển màn hình. Dữ liệu và nghiệp vụ lấy từ kịch bản hai mô-đun trong `26_B23DCCN764-full.docx`, đối chiếu `ke-hoach/00_PHAM_VI.md` và `ke-hoach/11_CHUAN_MAU_LUONG_GIAO_DIEN.md`.

Đây là hình minh họa thiết kế cho Bước 1 của phần 4.3.1. Bố cục dọc, tối đa hai màn hình mỗi hàng. Lược bỏ trang trí, nút danh sách hóa đơn chưa thanh toán và chú thích giải thích nghiệp vụ theo yêu cầu người dùng. Form thêm mới được giữ trong luồng nhập phụ tùng. Không thay đổi chức năng trong mã nguồn.

Mẫu tra cứu: “lọc” → DV1 / PT1. Mẫu nhập: Hòa Phát → PT1 → 10 × 80.000 = 800.000 đồng. Xác nhận trực tiếp từ hóa đơn nháp; màn kế tiếp là hóa đơn **đã xác nhận**. Bảng hóa đơn đã xác nhận và bản in có sáu cột. In và ghi nhận thanh toán là hai thao tác riêng.

SVG có nền trắng và chữ văn bản. Form và bảng nền trắng; chỉ nút submit có nền xám. Mũi tên khối theo bài giảng. Chèn SVG trực tiếp vào Word để giữ nét khi phóng to; PNG là bản xem nhanh. Các hình cũ và DOCX/PDF nguồn được giữ nguyên.

Tạo lại bằng Python có PySide6:

```powershell
py -3 app/docs/diagrams/render_simple_ui.py
```

Đã kiểm tra SVG hợp lệ, độ rộng chữ ở các ô/bảng và xem trực tiếp bản PNG. Đây là kiểm tra hình thiết kế, không phải nghiệm thu ứng dụng.
