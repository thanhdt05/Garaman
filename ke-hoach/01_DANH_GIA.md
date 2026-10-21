# Đối chiếu bài làm — chỉ hai module đề 26

Áp dụng [00_PHAM_VI.md](00_PHAM_VI.md). File này chỉ ghi những chỗ cần thống nhất giữa bài làm và thiết kế/code của hai module; không giao AI sửa toàn bộ báo cáo. Giữ nguyên `26_B23DCCN764-full.docx` và mô hình tổng thể, ghi đối chiếu vào tài liệu hai module.

## 1. Những gì đã chốt và không cần vẽ lại

- Giữ hai module đề 26: khách tra cứu dịch vụ/phụ tùng; kho nhập phụ tùng. Đăng nhập hỗ trợ chạy demo.
- Chỉ cài đặt 10 model và 9 bảng theo 00, giữ hậu tố 4 và quan hệ liên quan từ báo cáo. Dòng nhập giữ id PK và UNIQUE cặp hóa đơn–phụ tùng.
- Hóa đơn nhập có một người lập; người đó ghi nhận trả tiền, không bổ sung người thanh toán riêng.
- Lưu `tonKho` trực tiếp; giữ hình giao tiếp và `tangTonSauNhap()` của pha phân tích. `thanhTien`/`tongTien` vẫn tính từ dòng, không lưu cột.
- Các trường nhận diện/chống lặp thuộc nhập hàng đã có trong DOCX được giữ, không thêm lặp. Chỉ đối chiếu danh sách 5 trường ở 05.

## 2. Lời văn cần thống nhất khi thiết kế/code — TÌM → THAY

Các thay thế dưới đây ghi vào `app/docs/THAY_DOI_SO_VOI_BAO_CAO.md` và dùng cho nội dung hai module. Không tự áp lên Word gốc; người dùng có thể dùng các đoạn này để cập nhật bài làm khi cần.

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
Riêng tonKho trong bảng tblSparePart4 được giữ lại để truy xuất nhanh. Trong phạm vi hai module, khi xác nhận hóa đơn nhập, hệ thống lưu chứng từ, chi tiết và tăng tồn theo số lượng nhập trong cùng một giao dịch CSDL. In lại hoặc ghi nhận thanh toán không cập nhật tồn thêm. App không triển khai nghiệp vụ xuất kho.
```

Bài giảng mục 4.2 hướng dẫn loại thuộc tính dẫn xuất; việc giữ tồn là điều chỉnh triển khai theo quyết định người dùng để tra cứu nhanh, phải giải thích chủ đích thay vì khẳng định bài giảng bắt buộc lưu tồn. Các bước thiết kế module vẫn theo đúng 4.3.1 của mẫu.

### E. Các cụm mô tả tồn cũ

| TÌM đúng cụm | THAY |
|---|---|
| lượng tồn tính từ các giao dịch tăng đúng số lượng nhập một lần | hệ thống cập nhật tồn kho đã lưu, tăng đúng số lượng nhập một lần |
| tồn PT1 tính từ các giao dịch tăng thêm 10 cái đúng một lần | tồn kho đã lưu của PT1 tăng thêm 10 cái đúng một lần |
| lượng tồn tính từ các giao dịch tăng theo số lượng đã xác nhận | hệ thống cập nhật tồn kho đã lưu, tăng theo số lượng đã xác nhận |

## 3. Hình/thuộc tính cần đồng bộ

| Vị trí | Sửa cụ thể |
|---|---|
| Hình CSDL, bảng tblSparePart4 | Đã kiểm tra Hình 2.2: tonKho hiện là decimal(12,3), không cần sửa lại kiểu. SQL bổ sung NOT NULL, DEFAULT 0, CHECK >= 0 theo hợp đồng; còn sửa lời văn loại tồn ở mục D |
| Phân tích module nhập | tangTonSauNhap() thuộc SparePart4; câu hiện tại đã sửa đúng, giữ nguyên hình giao tiếp |
| Các hình nhân viên–hóa đơn | Giữ một quan hệ người lập; không thêm đường/FK thanh toán |
| Biểu đồ lớp thiết kế dùng làm đầu vào code | Ghi ánh xạ kiểu tiền/lượng float → BigDecimal; Date giao dịch → LocalDateTime; ngày sinh → LocalDate; mảng dòng → List. Cập nhật chữ ký thiết kế theo code, không thay nghiệp vụ hoặc nối lại quan hệ vì đổi kiểu triển khai |

Nhãn `date` trên hình ERD Paradigm có thể giữ. Chèn ghi chú:

```text
Các thuộc tính biểu diễn thời điểm trên mô hình được triển khai bằng DATETIME trong MySQL; ngày sinh dùng DATE. Nhãn date trong hình theo kiểu công cụ mô hình hóa đang hỗ trợ. Tiền và lượng triển khai bằng DECIMAL và BigDecimal để giữ độ chính xác.
```

Không buộc chuyển công cụ, không ép vẽ UNIQUE ghép nếu Paradigm không tiện; SQL phải có UNIQUE cặp FK và giữ id PK.

Ở phần trích phương thức ghiNhanThanhToan (P0836–P0837 theo công cụ trích nguồn 08), bổ sung kiểm tra nhân viên hiện tại là người lập vào điều kiện và input; không bổ sung thuộc tính người trả tiền. ERD dùng VARCHAR(255) còn SQL chọn TEXT/độ dài riêng ở một số trường: ghi khác biệt trong từ điển và sơ đồ tập con 9 bảng theo 10; không yêu cầu vẽ lại ERD tổng thể.

## 4. Bàn giao đối chiếu hai module

Ghi vị trí nguồn, phương án thiết kế/code và lý do vào `app/docs/THAY_DOI_SO_VOI_BAO_CAO.md`, chỉ đối với hai module. Nội dung 4.3.1 và bốn hình thiết kế bàn giao theo 07 ở dạng sẵn chèn. Nếu câu/hình nguồn đã đúng thì ghi đã khớp, không tạo yêu cầu sửa lại.

Không yêu cầu tạo bản Word sửa toàn bộ, đánh số lại các chương, thay mô hình tổng thể hoặc chuẩn bị các phần sau. Những phần đó giữ nguyên trong tài liệu gốc và không phải điều kiện hoàn thành code hai module.
