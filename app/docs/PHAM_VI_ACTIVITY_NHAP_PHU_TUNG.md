# Phạm vi hình hoạt động module nhập phụ tùng

Nguồn quyết định phạm vi là **đề số 26 và phần phân tích trong `26_B23DCCN764-full.docx`**. Code dùng để ánh xạ các bước vào Servlet/DAO và kiểm tra thứ tự xử lí; không lấy toàn bộ nhánh của controller làm nội dung bắt buộc trên hình báo cáo.

Đã đọc lại trực tiếp ZIP/XML của DOCX hiện tại, không chỉ dùng tài liệu sinh trước đó. SHA-256 nguồn tại lần đọc này: `b9e5074406dfe7c161d04b46f4ea7827e147a243b04492cc7045254651d59c66`. Mã P dưới đây là số đoạn XML, không phải trang Word.

| Căn cứ | Nội dung chi phối hình |
|---|---|
| Đề 26, P0011 | Chọn menu nhập → tìm/chọn NCC, thêm khi chưa có → lặp tìm/chọn PT, thêm khi chưa có, nhập lượng/giá → xác nhận → in và thanh toán NCC |
| UC chi tiết, P0428–P0437, Hình 1.3 | Chọn NCC; thêm NCC mở rộng khi thiếu; chọn PT và số lượng; thêm PT mở rộng khi thiếu; xác nhận/in. Đăng nhập là tiền điều kiện của hình nhập |
| Phân tích tĩnh, P0745–P0844, Hình 1.6 | Bảy giao diện; tìm/lấy/lưu dữ liệu cần thiết, tính tổng, tăng tồn và ghi nhận thanh toán |
| Chuyển trạng thái, P0889–P0896, Hình 1.10 | Chuỗi bảy màn hình; hai nhánh thêm mới trở về màn chọn; in xong trở về hóa đơn đã xác nhận |
| Kịch bản v.2, P0899–P0962, Hình 1.11 | Tìm NCC, lấy NCC/NV, lặp tìm/chọn/nhập PT, tính tổng và lưu khi xác nhận, lấy hóa đơn/in, nhận số tiền đã trả, thông báo hoàn tất |
| Ngoại lệ, P0628–P0636 | Thêm mới, chọn trùng, dữ liệu sai, hủy, xác nhận trùng, đã thanh toán và tiếp tục hóa đơn chưa trả |

## Hình chính giữ những gì?

1. Giao diện chính của NV kho → tìm/chọn NCC.
2. Thêm NCC khi chưa có → về danh sách để chọn.
3. Lấy NCC/NV → hiển thị hóa đơn dự kiến.
4. Tìm/chọn PT ngay trên hóa đơn → thêm dòng → nhập lượng/giá → lặp đến khi đủ PT.
5. Thêm PT khi chưa có → về danh sách trên hóa đơn để chọn. Giữ bước lấy nhóm PT vì có trong phân tích.
6. Xác nhận → nhận thông tin HĐ, kiểm tra, tính tổng, lưu → mở hóa đơn đã xác nhận.
7. In → trở về hóa đơn đã xác nhận → nhập số tiền đã trả → ghi nhận thanh toán → thông báo thành công, kết thúc.
8. Hủy HĐ dự kiến là một nhánh ngắn vì nút Hủy được nêu trong phân tích tĩnh và ngoại lệ 12a.

Bảy khung JSP tiếp tục dùng đúng tên đã chốt: `warehouseHome.jsp`, `selectSupplier.jsp`, `newSupplier.jsp`, `purchaseInvoice.jsp`, `newSparePart.jsp`, `confirmedInvoice.jsp`, `printInvoice.jsp`.

## Chi tiết chuyển sang thuyết minh

| Chi tiết trước đây làm hình rộng/rối | Cách thể hiện hiện tại |
|---|---|
| Cập nhật nháp trước tìm/chọn/thêm PT | Bước nội bộ của xử lí input/redirect; không có nút Cập nhật nháp riêng trên hình |
| Nút Cập nhật riêng theo code | Hình biểu diễn nhập lượng/giá và hiển thị lại theo phân tích; cơ chế POST và tính ước lượng JS ghi trong thuyết minh |
| Xóa dòng | Không có trong luồng chính và các giao diện phân tích của module tại P0745–P0844; không đưa thành nhánh của hình báo cáo chỉ vì code đã có |
| Xác nhận lại theo requestCode, version/UUID, khóa, transaction, lỗi SQL/HTTP | Điều kiện nội bộ và ngoại lệ; không tạo nhánh/chức năng ngang cấp với chọn NCC/PT |
| Mở danh sách HĐ chưa trả | **Có trong ngoại lệ 18b của DOCX**, vẫn thuộc module; ghi trong phần ngoại lệ, không vẽ một luồng riêng từ trang chính trên hình chính |
| Thiếu/thừa tiền, đã thanh toán trước | Thuyết minh ngoại lệ; không cần vẽ mọi vòng lỗi của controller trên hình chính |
| Chi tiết reload/flash sau thanh toán | Giữ thứ tự lưu → tải lại HĐ → hiển thị → thông báo thành công; lược các hàm kỹ thuật |

Không xóa hoặc đổi code để làm nó ngắn bằng hình; cũng không coi những phần được lược là đã biến mất khỏi yêu cầu. Sơ đồ là thiết kế hoạt động ở độ chi tiết của bài giảng, không phải sơ đồ mọi nhánh điều khiển của chương trình.

## Các ngoại lệ vẫn phải giữ trong lời văn

- NCC/PT chưa có: dùng hai nhánh thêm mới trên hình.
- PT đã có trong dòng nhập: đưa về dòng cũ để sửa, không thêm trùng.
- Nháp rỗng hoặc lượng/giá sai: không lưu xác nhận; trở về hóa đơn để bổ sung/sửa.
- Hủy trước xác nhận: bỏ HĐ dự kiến, không tăng tồn và không xóa danh mục đã thêm.
- Xác nhận lại: mở HĐ đã lưu, không tạo hoặc tăng tồn thêm.
- Đã thanh toán: thông báo trạng thái có sẵn; không ghi nhận lần nữa.
- Dừng khi chưa trả đủ: giữ HĐ và tồn đã xác nhận, tiếp tục xử lí theo ngoại lệ 18b. Khác biệt quyền giữa DOCX và code đã được ghi trong bảng đối chiếu triển khai, không tự sửa yêu cầu ở đây.

## Bảo toàn bản chi tiết

Bản hình rộng mô tả triển khai đã được lưu ở [implementation-detail/purchase-activity.svg](diagrams/activity/implementation-detail/purchase-activity.svg), kèm PNG/PDF/drawio/JSON. Đây là bản tham chiếu kỹ thuật trước khi thu gọn, không phải hình chính để chèn phần thiết kế báo cáo.

[Bảng đối chiếu 21 phương thức](DOI_CHIEU_ACTIVITY_VOI_CODE.md) tiếp tục có giá trị như phụ lục triển khai. Nó không phải danh sách thao tác phải vẽ hết trong activity chính.
