# Kết quả giao diện tham chiếu — 19/10/2026

Nguồn: copy `E:\java\Garaman1\web` rồi sửa trong `E:\java\Garaman\giao-dien-tham-chieu`. **Không sửa file ở Garaman1**. Không chỉnh bốn tài liệu gốc trong Garaman. Bản này là frontend mô phỏng, không có Servlet nghiệp vụ/DAO/CSDL.

## Thay đổi đã thực hiện

- Thêm part: hãng bắt buộc theo ERD/hợp đồng; đơn vị select Cái/Chiếc/Bộ/Lít; mặc định Chưa công bố; không cho tạo Ngừng kinh doanh; công bố cần giá bán và mô tả.
- Chọn ảnh SVG sẵn có, bỏ upload; tồn 0 chỉ hiển thị, không có name để submit thành input nghiệp vụ.
- Lít có step 0.001, giá nhập step 0.01; chặn lượng sai đơn vị/scale, số mũ và giá không dương khi confirm. Các giới hạn Number của prototype nhỏ hơn DECIMAL của backend, không dùng lại làm miền chính thức.
- Hóa đơn đã xác nhận/bản in: bỏ địa chỉ, điện thoại, email NCC và cột hãng; sửa cả HTML mẫu lẫn JavaScript render.
- Cho checks nhận PLAYWRIGHT_MODULE để chạy bằng thư viện có sẵn ngoài bản copy; thêm kiểm tra ràng buộc công bố, hãng, đơn vị, bước lẻ Lít và cột chứng từ.

## Đã chạy

Tomcat **10.1.61** phục vụ JSP tại localhost:8096/GaraMan. Edge headless qua Playwright có sẵn trong `Garaman1/.tools/browser`; ảnh desktop 1440×1000, mobile 390×844. Chạy bộ kiểm tra hiện có `checks/ui-check.cjs`, kết thúc **exit code 0**, sáu nhóm PASS:

| Nhóm | Kết quả thực tế |
|---|---|
| Biên dịch/mở JSP, UTF-8, bố cục desktop, ảnh | PASS |
| Tìm ban đầu/rỗng, ví dụ lọc, chi tiết/quay lại giữ kết quả, không tìm thấy | PASS |
| Dropdown ngay trên hóa đơn, dòng 0/0, số nguyên, chọn trùng focus, 10×80000=800000 | PASS |
| In riêng/quay lại, trả đủ tiền mô phỏng, hóa đơn chỉ đọc | PASS |
| Thêm rồi chọn NCC/part, công bố thiếu dữ liệu bị chặn, Lít lẻ, hủy và giữ danh mục | PASS |
| Bố cục mobile, không lỗi JavaScript/HTTP ghi nhận bởi bộ kiểm tra | PASS |

Lệnh tái hiện tại thư mục Garaman sau khi khởi động server:

```powershell
$env:PLAYWRIGHT_MODULE = 'E:\java\Garaman1\.tools\browser\node_modules\playwright'
$env:GARAMAN_URL = 'http://localhost:8096/GaraMan/'
node giao-dien-tham-chieu/checks/ui-check.cjs
```

Ảnh mới: [form part](anh-giao-dien/09-them-phu-tung.png), [hóa đơn xác nhận](anh-giao-dien/11-xac-nhan-hoa-don.png), [bản in](anh-giao-dien/12-hoa-don-nhap.png), [PDF A4](anh-giao-dien/hoa-don-nhap-A4.pdf). Bộ kiểm tra sinh 18 PNG và một PDF. Có thể xem toàn bộ trong `docs/anh-giao-dien/`.

## Chưa kiểm chứng vì chưa triển khai backend

Đăng nhập/phân quyền thật, CSRF, MySQL/schema, transaction/rollback, chống xác nhận trùng ở DB, BigDecimal, tồn kho, snapshot DB, mở hóa đơn của người lập sau đăng nhập lại. Prototype chỉ có một hóa đơn đang mô phỏng trong tab, HTML mẫu khi mở trực tiếp trang chứng từ và tiền tính bằng Number. Kế hoạch 09/10 quy định thay toàn bộ phần này khi chuyển sang app. Không lấy PASS giao diện làm PASS cho các tính năng đó.
