# Danh mục thuộc tính — bản cuối

Giữ tên/giá/đơn vị và nhận diện trên chứng từ như DOCX mới đã làm. Không mở rộng sang bản lịch sử toàn bộ hồ sơ. Danh sách sau là **14 trường từng được thống nhất so với bản đầu**, phần lớn đã có trong DOCX hiện tại: kiểm tra đủ, không thêm lại hoặc tạo tên khác.

| Bảng | Trường giữ | Số trường |
|---|---|---|
| tblAppointment4 | hoTenLienHe, dienThoaiLienHe | 2 |
| tblRepairService4 | tenDichVuApDung | 1 |
| tblRepairPart4 | tenPhuTungApDung, donViApDung | 2 |
| tblCustomerInvoice4 | tblEmployee4id, hoTenKhachHangApDung, bienSoApDung, nhanVienTenApDung | 4 |
| tblPurchaseInvoice4 | maYeuCau, nhaCungCapTenApDung, nhanVienTenApDung | 3 |
| tblPurchaseInvoiceDetail4 | tenPhuTungApDung, donViApDung | 2 |
| Tổng | | **14** |

Trong đó có 12 giá trị nghiệp vụ, 1 FK người lập hóa đơn khách và 1 mã yêu cầu kỹ thuật. Không suy ra rằng đây là 14 cột đang thiếu trong bản mới. Giữ giá, lượng và bảo hành áp dụng đã có sẵn; không đếm chúng là trường thêm.

**Tồn kho:** giữ cột `tblSparePart4.tonKho DECIMAL(12,3) NOT NULL DEFAULT 0`, CHECK >= 0. Đây là cột có trong mô hình ban đầu được quyết định giữ lại, không tính vào 14 trường. Lưu hóa đơn nhập, dòng và tăng tồn trong một transaction; mã yêu cầu UNIQUE chống tăng lặp. Không lưu `thanhTien`/`tongTien` thành cột.

**Nhân viên:** CustomerInvoice có một người lập/thu tiền; PurchaseInvoice có một người lập/trả tiền. Người tiếp nhận lưu riêng ở RepairIntake. Không thêm thuộc tính object `nhanVienThanhToan` hoặc hai cột `tblEmployeeThanhToan4id` từng xuất hiện ở kế hoạch trước. Danh sách hóa đơn nhập chưa trả chỉ của chính người đang đăng nhập lập.

Không tạo các trường bản lịch sử dư: emailLienHe/diaChiLienHe ở lịch hẹn; nhaCungCapDiaChiApDung/nhaCungCapDienThoaiApDung ở hóa đơn nhập; hangSanXuatApDung ở dòng nhập; dienThoaiKhachHangApDung ở hóa đơn khách. Các trường danh mục gốc như địa chỉ NCC, hãng phụ tùng vẫn giữ để tra cứu/chọn; chỉ không sao chép lên chứng từ.

Hóa đơn nhập và bản in dùng tên NCC, tên người lập, tên/đơn vị dòng đã chốt; không JOIN tên hiện tại để thay thế. Sau xác nhận không sửa dòng; chỉ trạng thái/thời điểm thanh toán được cập nhật. Không đổi đơn vị part có giao dịch; tạo part khác khi bản chất/đơn vị khác. Giữ đủ 16 bảng theo file 03, không thêm bảng Payment, lịch sử ca hoặc bảng nháp.
