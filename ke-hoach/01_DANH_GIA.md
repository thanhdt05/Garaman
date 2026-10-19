# Đánh giá cuối — chỉ đồng bộ những chỗ còn lệch

File này thay thế danh sách sửa cũ. Căn cứ bản `26_B23DCCN764-full.docx` mới nhất trong thư mục và quyết định cuối của người dùng. Không áp lại mọi diễn giải từng đề xuất. Các phần đã đúng trong bản mới được giữ.

## 1. Những gì đã chốt và không cần vẽ lại

- Giữ hai module đề 26: khách tra cứu dịch vụ/phụ tùng; kho nhập phụ tùng. Đăng nhập hỗ trợ chạy demo.
- Giữ mô hình toàn hệ thống, các lớp/bảng hậu tố 4, ba bảng dòng vẫn có id PK và cặp FK UNIQUE trong SQL.
- Lịch hẹn–phiếu tiếp nhận: 0..1 ở cả hai đầu. Phiếu tiếp nhận–hóa đơn khách: 1 ở đầu phiếu, 0..1 ở đầu hóa đơn. FK hiện đã đúng.
- Người lập hóa đơn khách lưu riêng với người tiếp nhận. Mỗi hóa đơn chỉ một người lập và người đó thực hiện thu/chi; không bổ sung người thanh toán riêng.
- Lưu `tonKho` trực tiếp; giữ hình giao tiếp và `tangTonSauNhap()` của pha phân tích. `thanhTien`/`tongTien` vẫn tính từ dòng, không lưu cột.
- Các trường áp dụng và liên hệ lịch hẹn đã có trong DOCX được giữ; không thêm 14 cột lần nữa. Danh mục cuối ở file 05.

## 2. Lời văn còn cần sửa — TÌM → THAY

### A. Kịch bản nhập, bước 19

TÌM:

```text
19. Hệ thống kiểm tra HN1 chưa được ghi nhận thanh toán, cập nhật đồng thời trạng thái Đã thanh toán, thời điểm thanh toán và nhân viên kho thực sự trả tiền lấy từ phiên đăng nhập; giữ nguyên nhân viên lập và thông báo thành công. Việc ghi nhận thanh toán không tăng tồn kho lần nữa.
```

THAY:

```text
19. Hệ thống kiểm tra nhân viên đang đăng nhập chính là người lập HN1, số tiền đã trả bằng tổng tiền và hóa đơn chưa thanh toán; cập nhật trạng thái Đã thanh toán cùng thời điểm thanh toán rồi thông báo thành công. Người lập hóa đơn được giữ nguyên; việc ghi nhận thanh toán không tăng tồn kho lần nữa.
```

### B. Ngoại lệ nhập 18b

TÌM:

```text
Nhân viên kho có quyền ở ca hiện tại mở danh sách hóa đơn nhập chưa thanh toán của gara, chọn hóa đơn cần xử lí, đối chiếu và thực tế trả đủ tiền rồi tiếp tục bước 18.
```

THAY:

```text
Nhân viên kho lập hóa đơn đăng nhập lại, mở danh sách hóa đơn nhập do mình lập còn chưa thanh toán, chọn hóa đơn cần xử lí, đối chiếu và thực tế trả đủ tiền rồi tiếp tục bước 18.
```

Nếu bản mới còn câu “Người lập hóa đơn được giữ nguyên; người trả tiền được ghi theo nhân viên thực hiện ở ca hiện tại.” thì bỏ câu đó; quyết định cuối không có người trả tiền khác người lập.

### C. Bước 5 xác định quan hệ đối tượng

TÌM:

```text
Thông tin nhà cung cấp, nhân viên kho lập và nhân viên kho trả tiền nếu đã thanh toán nằm trong thông tin hóa đơn nhập.
```

THAY:

```text
Thông tin nhà cung cấp và nhân viên kho lập hóa đơn nằm trong thông tin hóa đơn nhập. Nhân viên lập hóa đơn thực hiện thanh toán cho nhà cung cấp và ghi nhận kết quả trên hệ thống.
```

### D. Tối ưu CSDL, Bước 5

Bỏ dòng sau khỏi danh sách thuộc tính dẫn xuất bị loại:

```text
tonKho trong bảng tblSparePart4
```

Thêm ngay sau danh sách:

```text
Riêng tonKho trong bảng tblSparePart4 được giữ lại để truy xuất nhanh. Khi xác nhận hóa đơn nhập, hệ thống tăng tồn theo số lượng nhập; khi xác nhận hóa đơn khách hàng, hệ thống giảm tồn theo lượng phụ tùng thực tế. Lưu chứng từ, chi tiết và cập nhật tồn được thực hiện trong cùng một giao dịch CSDL. In lại hoặc ghi nhận thanh toán không cập nhật tồn thêm.
```

Bài giảng mục 4.2 hướng dẫn loại thuộc tính dẫn xuất; việc giữ tồn là điều chỉnh triển khai theo quyết định người dùng để tra cứu nhanh, phải giải thích chủ đích thay vì khẳng định bài giảng bắt buộc lưu tồn. Các bước thiết kế module vẫn theo đúng 4.3.1 của mẫu.

### E. Các cụm mô tả tồn cũ

| TÌM đúng cụm | THAY |
|---|---|
| lượng tồn tính từ các giao dịch tăng đúng số lượng nhập một lần | hệ thống cập nhật tồn kho đã lưu, tăng đúng số lượng nhập một lần |
| tồn PT1 tính từ các giao dịch tăng thêm 10 cái đúng một lần | tồn kho đã lưu của PT1 tăng thêm 10 cái đúng một lần |
| lượng tồn tính từ các giao dịch tăng theo số lượng đã xác nhận | hệ thống cập nhật tồn kho đã lưu, tăng theo số lượng đã xác nhận |

### F. Sau khi bỏ bảng vai trò trong tối ưu CSDL

TÌM:

```text
Các bảng tblManager4, tblWarehouseStaff4 và tblSalesStaff4 không còn thuộc tính nào ngoài khóa ngoại kế thừa → loại bỏ các bảng này
```

THAY:

```text
Các bảng tblManager4, tblWarehouseStaff4 và tblSalesStaff4 không còn thuộc tính nào ngoài khóa ngoại kế thừa → loại bỏ các bảng này. Các khóa ngoại tham chiếu các bảng vai trò được chuyển sang tham chiếu tblEmployee4.tblMember4id; các lớp vai trò vẫn giữ trong mô hình lớp.
```

## 3. Hình/thuộc tính cần đồng bộ

| Vị trí | Sửa cụ thể |
|---|---|
| Hình CSDL, bảng tblSparePart4 | tonKho dùng DECIMAL(12,3), NOT NULL, mặc định 0, CHECK >= 0; không dùng integer khi lượng giao dịch cho phép lẻ |
| Phân tích module nhập | tangTonSauNhap() thuộc SparePart4; câu hiện tại đã sửa đúng, giữ nguyên hình giao tiếp |
| Các hình nhân viên–hóa đơn | Giữ một quan hệ người lập; không thêm đường/FK thanh toán |
| Biểu đồ lớp thiết kế dùng làm đầu vào code | Ghi ánh xạ kiểu tiền/lượng float → BigDecimal; Date giao dịch → LocalDateTime; ngày sinh → LocalDate; mảng dòng → List. Cập nhật chữ ký thiết kế theo code, không thay nghiệp vụ hoặc nối lại quan hệ vì đổi kiểu triển khai |

Nhãn `date` trên hình ERD Paradigm có thể giữ. Chèn ghi chú:

```text
Các thuộc tính biểu diễn thời điểm trên mô hình được triển khai bằng DATETIME trong MySQL; ngày sinh dùng DATE. Nhãn date trong hình theo kiểu công cụ mô hình hóa đang hỗ trợ. Tiền và lượng triển khai bằng DECIMAL và BigDecimal để giữ độ chính xác.
```

Không buộc chuyển công cụ, không ép vẽ UNIQUE ghép nếu Paradigm không tiện; SQL phải có UNIQUE cặp FK và giữ id PK.

## 4. Cách sửa báo cáo khi Gemini thực hiện

Tạo `app/docs/BAO_CAO_DA_SUA.docx` từ bản sao bản mới. Chỉ áp những thay thế còn tìm thấy, nếu người dùng đã sửa rồi thì đánh dấu đã có, không chèn lặp. Giữ các đoạn không bị tác động và hình giao tiếp đã chốt. Ghi bảng vị trí/số hình trước–sau và thay đổi trong `THAY_DOI_SO_VOI_BAO_CAO.md`.

Phần trước đang đánh số Chương 1–2, phần mới muốn dùng 4.3.1: lập bảng ánh xạ tiêu đề/hình theo cấu trúc bài giảng rồi cập nhật tham chiếu đồng bộ trong bản sao. Không tự viết thêm các chương chưa có, không tìm-thay số mù làm sai tên field/hình. Nội dung mới theo file 07; nếu không thể chỉnh nhúng hình hoặc bảo toàn DOCX thì bàn giao đoạn/hình sẵn chèn kèm vị trí chính xác và ghi rõ phần chưa chèn, không tuyên bố đã hoàn thành bản DOCX.
