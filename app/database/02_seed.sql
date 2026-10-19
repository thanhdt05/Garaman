-- =====================================================================
-- Dữ liệu mẫu (Seed Data) cho GaraMan (Đề 26 - PTIT)
-- Tài khoản demo có hash PBKDF2WithHmacSHA256 thật (mật khẩu: 123456)
-- Phụ tùng PT1 khởi tạo tồn 0 để kiểm chứng kịch bản nhập 10 cái -> tồn 10
-- =====================================================================

USE `garaman4`;

-- 1. Bảng thành viên (tblMember4)
-- Hash PBKDF2 của mật khẩu '123456':
-- pbkdf2_sha256$600000$3fGGHCpOcHuqkDsfiHGjKQ==$/f0Fp96dlpDSsAg92MwD7u5SOmEwWwJ71fT79UrGoec=

INSERT INTO `tblMember4` (`id`, `hoTen`, `tenDangNhap`, `matKhau`, `ngaySinh`, `diaChi`, `email`, `dienThoai`, `vaiTro`, `trangThaiTaiKhoan`) VALUES
(1, 'Đàm Tiến Thành (Khách)', 'khach26', 'pbkdf2_sha256$600000$3fGGHCpOcHuqkDsfiHGjKQ==$/f0Fp96dlpDSsAg92MwD7u5SOmEwWwJ71fT79UrGoec=', '2005-01-15', '128 Trần Phú, Hà Đông, Hà Nội', 'khach26@garaman.vn', '0912345678', 'Customer', 'Hoạt động'),
(2, 'Trần Văn Kho (Thủ kho)', 'kho26', 'pbkdf2_sha256$600000$3fGGHCpOcHuqkDsfiHGjKQ==$/f0Fp96dlpDSsAg92MwD7u5SOmEwWwJ71fT79UrGoec=', '1995-05-20', '56 Nguyễn Trãi, Thanh Xuân, Hà Nội', 'kho26@garaman.vn', '0923456789', 'WarehouseStaff', 'Hoạt động'),
(3, 'Lê Quản Lý (Giám đốc)', 'quanly26', 'pbkdf2_sha256$600000$3fGGHCpOcHuqkDsfiHGjKQ==$/f0Fp96dlpDSsAg92MwD7u5SOmEwWwJ71fT79UrGoec=', '1988-10-10', '78 Hoàng Cầu, Đống Đa, Hà Nội', 'quanly26@garaman.vn', '0934567890', 'Manager', 'Hoạt động'),
(4, 'Phạm Bán Hàng (Tiếp tân)', 'banhang26', 'pbkdf2_sha256$600000$3fGGHCpOcHuqkDsfiHGjKQ==$/f0Fp96dlpDSsAg92MwD7u5SOmEwWwJ71fT79UrGoec=', '1998-12-05', '90 Cầu Giấy, Hà Nội', 'banhang26@garaman.vn', '0945678901', 'SalesStaff', 'Hoạt động');

-- 2. Bảng nhân viên (tblEmployee4)
INSERT INTO `tblEmployee4` (`tblMember4id`, `viTri`, `trangThaiLamViec`) VALUES
(2, 'Kho', 'Đang làm việc'),
(3, 'Quản lí', 'Đang làm việc'),
(4, 'Bán hàng', 'Đang làm việc');

-- 3. Bảng khách hàng (tblCustomer4)
INSERT INTO `tblCustomer4` (`tblMember4id`) VALUES
(1);

-- 4. Bảng kỹ thuật viên (tblTechnician4)
INSERT INTO `tblTechnician4` (`id`, `hoTen`, `ngaySinh`, `diaChi`, `email`, `dienThoai`, `chuyenMon`, `trangThai`) VALUES
(1, 'Nguyễn Văn Kỹ', '1992-04-12', 'Số 15 Định Công, Hoàng Mai, Hà Nội', 'ktv.ky@garaman.vn', '0956789012', 'Động cơ & Gầm', 'Đang làm việc'),
(2, 'Vũ Văn Thuật', '1996-08-25', 'Số 22 Khương Trung, Thanh Xuân, Hà Nội', 'ktv.thuat@garaman.vn', '0967890123', 'Hệ thống Điện & Phanh', 'Đang làm việc');

-- 5. Bảng nhà cung cấp (tblSupplier4)
INSERT INTO `tblSupplier4` (`id`, `ten`, `diaChi`, `dienThoai`, `email`, `trangThai`) VALUES
(1, 'Công ty TNHH Phụ tùng Hòa Phát', 'Số 123 Giải Phóng, Hai Bà Trưng, Hà Nội', '0900000001', 'contact@hoaphatpart.vn', 'Đang hoạt động'),
(2, 'Tổng kho Phụ tùng Ô tô Miền Nam', '456 An Dương Vương, Phường 4, Quận 5, TP. Hồ Chí Minh', '0900000002', 'kinhdoanh@phutungmiennam.com', 'Đang hoạt động'),
(3, 'Đại lý Phụ tùng Thiết bị Cũ', '789 Đường Láng, Đống Đa, Hà Nội', '0900000003', 'support@phutungcu.vn', 'Ngừng hoạt động');

-- 6. Bảng xe ô tô (tblCar4)
INSERT INTO `tblCar4` (`id`, `bienSo`, `hangXe`, `dongXe`, `namSanXuat`) VALUES
(1, '30A-123.45', 'Toyota', 'Corolla Cross', 2022),
(2, '29C-678.90', 'Ford', 'Ranger Wildtrak', 2021);

-- 7. Bảng nhóm phụ tùng (tblSparePartCategory4)
INSERT INTO `tblSparePartCategory4` (`id`, `ten`, `moTa`, `trangThai`) VALUES
(1, 'Phụ tùng động cơ', 'Các linh kiện, phụ tùng thay thế định kỳ và sửa chữa động cơ đốt trong', 'Đang sử dụng'),
(2, 'Hệ thống phanh', 'Má phanh, đĩa phanh, dầu phanh và phụ kiện an toàn hệ thống phanh', 'Đang sử dụng'),
(3, 'Khung gầm cũ', 'Nhóm danh mục phụ tùng khung gầm ngưng kinh doanh', 'Ngừng sử dụng');

-- 8. Bảng dịch vụ (tblService4)
INSERT INTO `tblService4` (`id`, `ten`, `nhom`, `moTa`, `noiDung`, `hinhAnh`, `donGia`, `thoiGianDuKien`, `hanBaoHanh`, `dieuKienBaoHanh`, `trangThai`) VALUES
(1, 'Thay lọc dầu động cơ', 'Bảo dưỡng',
 'Tiền công thay lọc dầu động cơ xe du lịch, chưa bao gồm phụ tùng lọc dầu.',
 'Quy trình thực hiện: Nâng xe, kiểm tra tình trạng rò rỉ bôi trơn, tháo lọc dầu cũ, vệ sinh bề mặt tiếp xúc lốc máy, bôi trơn gioăng cao su lọc mới, xiết lực đúng tiêu chuẩn và chạy thử kiểm tra áp suất dầu.',
 'assets/images/part.svg', 120000.00, 30, '6 tháng', 'Bảo hành chất lượng lắp đặt kỹ thuật và độ kín khít lọc dầu trong điều kiện vận hành bình thường.', 'Đang cung cấp'),

(2, 'Bảo dưỡng và vệ sinh hệ thống phanh 4 bánh', 'Bảo dưỡng',
 'Tiền công bảo dưỡng, vệ sinh cùm phanh, tra mỡ chịu nhiệt má phanh và đĩa phanh.',
 'Tháo bánh xe, kiểm tra độ mòn má phanh và đĩa phanh, vệ sinh bụi phanh, tra mỡ chuyên dụng ắc thắng, lắp ráp và cân chỉnh lực phanh.',
 'assets/images/brake.svg', 250000.00, 45, '3 tháng', 'Bảo hành tiếng kêu bất thường và độ ăn phanh đồng đều giữa các bánh.', 'Đang cung cấp'),

(3, 'Vệ sinh khoang máy bằng hơi nước nóng (Ngừng phục vụ)', 'Chăm sóc xe',
 'Dịch vụ làm sạch khoang động cơ bằng hơi nước nóng áp suất cao.',
 'Che chắn các cụm điện tử, phun hơi nước nóng làm mềm dầu mỡ và lau khô bằng khăn vi sợi.',
 'assets/images/garage.svg', 350000.00, 60, 'Không bảo hành', 'Khách hàng tự chịu rủi ro ẩm cụm điện.', 'Ngừng cung cấp');

-- 9. Bảng phụ tùng (tblSparePart4)
INSERT INTO `tblSparePart4` (`id`, `ten`, `moTa`, `hinhAnh`, `donVi`, `hangSanXuat`, `thongSo`, `dongXePhuHop`, `giaBan`, `hanBaoHanh`, `dieuKienBaoHanh`, `trangThai`, `tonKho`, `tblSparePartCategory4id`) VALUES
(1, 'Lọc dầu động cơ AF-01',
 'Lọc dầu động cơ chất lượng cao AF-01 tiêu chuẩn ISO, lọc sạch cặn bẩn kim loại bảo vệ động cơ.',
 'assets/images/part.svg', 'Cái', 'AutoFilter', 'Đường kính ren 76 mm, chiều cao 85 mm', 'Chưa cập nhật',
 150000.00, '6 tháng', 'Lỗi kỹ thuật từ nhà sản xuất, nguyên tem niêm phong và hóa đơn mua hàng.',
 'Đang kinh doanh', 0.000, 1),

(2, 'Má phanh đĩa trước BP-02',
 'Bộ má phanh đĩa gốm cao cấp Brembo BP-02 chịu nhiệt cao, giảm thiểu mài mòn đĩa.',
 'assets/images/brake.svg', 'Bộ', 'Brembo', 'Độ dày má phanh 15 mm, hợp kim gốm chịu nhiệt', 'Phù hợp các dòng xe Sedan cỡ C và D',
 450000.00, '12 tháng', 'Má phanh mòn không đều hoặc bong tróc lớp ma sát trong thời gian bảo hành.',
 'Đang kinh doanh', 5.000, 2),

(3, 'Dầu động cơ Castrol Edge 5W-30',
 'Dầu nhớt tổng hợp toàn phần Castrol Edge công nghệ Sức mạnh Chuyển động Titanium.',
 'assets/images/part.svg', 'Lít', 'Castrol', 'Tiêu chuẩn API SP, ILSAC GF-6, độ nhớt 5W-30', 'Các dòng xe máy xăng và máy dầu nhẹ đời mới',
 180000.00, '12 tháng', 'Bao bì nguyên vẹn, còn hạn sử dụng.',
 'Đang kinh doanh', 20.000, 1),

(4, 'Bugi Iridium IX Laser thử nghiệm',
 'Bugi đánh lửa cực bền điện cực Iridium siêu mảnh 0.6 mm.',
 'assets/images/part.svg', 'Chiếc', 'NGK', 'Ren M14x1.25, tầm nhiệt 7', 'Thử nghiệm nội bộ',
 NULL, '12 tháng', 'Chưa có thông tin bảo hành',
 'Chưa công bố', 0.000, 1),

(5, 'Lọc gió điều hòa cũ ngưng kinh doanh',
 'Lọc gió cabin than hoạt tính lọc bụi mịn PM2.5.',
 'assets/images/part.svg', 'Cái', 'Denso', 'Kích thước 215x200x30 mm', 'Xe đời cũ',
 95000.00, '1 tháng', 'Không bảo hành ẩm mốc',
 'Ngừng kinh doanh', 2.000, 1);
