# Thuộc tính cần đối chiếu — chỉ hai module

Áp dụng [00_PHAM_VI.md](00_PHAM_VI.md). Chỉ kiểm tra những trường có trong hai module, giữ đúng bài làm; không thêm các lớp/bảng khác để hoàn thiện mô hình tổng thể.

| Bảng | Trường cần giữ từ bài làm | Số trường |
|---|---|---|
| tblPurchaseInvoice4 | maYeuCau, nhaCungCapTenApDung, nhanVienTenApDung | 3 |
| tblPurchaseInvoiceDetail4 | tenPhuTungApDung, donViApDung | 2 |
| Tổng thuộc phạm vi nhập hàng | | **5** |

Đây là danh sách kiểm tra trường đã có, không phải yêu cầu thêm lặp 5 cột. maYeuCau là mã chống xác nhận trùng; bốn trường còn lại giữ thông tin nhận diện khi xác nhận.

- Giữ tonKho DECIMAL(12,3) NOT NULL DEFAULT 0, không âm; part mới tồn 0. Xác nhận nhập lưu hóa đơn/dòng/tăng tồn cùng transaction.
- thanhTien/tongTien tính từ lượng/giá đã lưu, không có cột riêng; không dùng giá bán danh mục thay giá nhập.
- Hóa đơn nhập có một người lập, chính người đó ghi nhận trả tiền. Không thêm object/FK người thanh toán riêng.
- Giữ địa chỉ/điện thoại/email NCC và hãng trong master để tìm/chọn/đối chiếu trước xác nhận; không sao chép chúng lên chứng từ. Hóa đơn xác nhận/bản in dùng tên NCC, nhân viên, part và đơn vị áp dụng đã chốt.
- Không đổi đơn vị part đã có giao dịch. Đơn vị, công bố và các trường bắt buộc theo 03/09/10.

Không áp danh sách thuộc tính của lịch hẹn, tiếp nhận, hóa đơn khách hoặc thống kê vào app hai module. Chỉ tạo 9 bảng và 10 model đã khóa ở 00.
