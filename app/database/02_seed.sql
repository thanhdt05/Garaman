-- =====================================================================
-- Dữ liệu mẫu (Seed Data) cho GaraMan (Đề 26 - PTIT)
-- Áp dụng phạm vi chuẩn 9 bảng theo 00_PHAM_VI.md và 03_HOP_DONG.md
-- Tài khoản demo có hash PBKDF2WithHmacSHA256 thật (mật khẩu: 123456)
-- Phụ tùng PT1 khởi tạo tồn 0 để kiểm chứng kịch bản nhập 10 cái -> tồn 10
-- =====================================================================

USE `garaman4`;

-- 1. Bảng thành viên (tblMember4) - chỉ Customer và WarehouseStaff
-- Hash PBKDF2 của mật khẩu '123456':
-- pbkdf2_sha256$600000$3fGGHCpOcHuqkDsfiHGjKQ==$/f0Fp96dlpDSsAg92MwD7u5SOmEwWwJ71fT79UrGoec=

INSERT INTO `tblMember4` (`id`, `fullName`, `username`, `password`, `dateOfBirth`, `address`, `email`, `phoneNumber`, `role`, `accountStatus`) VALUES
(1, 'Đàm Tiến Thành (Khách)', 'khach26', 'pbkdf2_sha256$600000$3fGGHCpOcHuqkDsfiHGjKQ==$/f0Fp96dlpDSsAg92MwD7u5SOmEwWwJ71fT79UrGoec=', '2005-01-15', '128 Trần Phú, Hà Đông, Hà Nội', 'khach26@garaman.vn', '0912345678', 'Customer', 'Hoạt động'),
(2, 'Trần Văn Kho (Thủ kho)', 'kho26', 'pbkdf2_sha256$600000$3fGGHCpOcHuqkDsfiHGjKQ==$/f0Fp96dlpDSsAg92MwD7u5SOmEwWwJ71fT79UrGoec=', '1995-05-20', '56 Nguyễn Trãi, Thanh Xuân, Hà Nội', 'kho26@garaman.vn', '0923456789', 'WarehouseStaff', 'Hoạt động');

-- 2. Bảng nhân viên (tblEmployee4) - vị trí Kho
INSERT INTO `tblEmployee4` (`tblMember4id`, `position`, `employmentStatus`) VALUES
(2, 'Kho', 'Đang làm việc');

-- 3. Bảng khách hàng (tblCustomer4)
INSERT INTO `tblCustomer4` (`tblMember4id`) VALUES
(1);

-- 4. Bảng nhà cung cấp (tblSupplier4)
INSERT INTO `tblSupplier4` (`id`, `name`, `address`, `phoneNumber`, `email`, `status`) VALUES
(1, 'Hòa Phát', 'Hà Nội', '0900000001', NULL, 'Đang hoạt động'),
(2, 'Tổng kho Phụ tùng Ô tô Miền Nam', '456 An Dương Vương, Phường 4, Quận 5, TP. Hồ Chí Minh', '0900000002', 'kinhdoanh@phutungmiennam.com', 'Đang hoạt động'),
(3, 'Đại lý Phụ tùng Thiết bị Cũ', '789 Đường Láng, Đống Đa, Hà Nội', '0900000003', 'support@phutungcu.vn', 'Ngừng hoạt động');

-- 5. Bảng nhóm phụ tùng (tblSparePartCategory4)
INSERT INTO `tblSparePartCategory4` (`id`, `name`, `description`, `status`) VALUES
(1, 'Phụ tùng động cơ', 'Các linh kiện, phụ tùng thay thế định kỳ và sửa chữa động cơ đốt trong', 'Đang sử dụng'),
(2, 'Hệ thống phanh', 'Má phanh, đĩa phanh, dầu phanh và phụ kiện an toàn hệ thống phanh', 'Đang sử dụng'),
(3, 'Khung gầm cũ', 'Nhóm danh mục phụ tùng khung gầm ngưng kinh doanh', 'Ngừng sử dụng');

-- 6. Bảng dịch vụ (tblService4)
INSERT INTO `tblService4` (`id`, `name`, `category`, `description`, `content`, `imagePath`, `unitPrice`, `estimatedDuration`, `warrantyPeriod`, `warrantyTerms`, `status`) VALUES
(1, 'Thay lọc dầu động cơ', 'Bảo dưỡng',
 'Tiền công thay lọc dầu động cơ xe du lịch, chưa bao gồm phụ tùng lọc dầu.',
 'Quy trình thực hiện: Nâng xe, kiểm tra tình trạng rò rỉ bôi trơn, tháo lọc dầu cũ, vệ sinh bề mặt tiếp xúc lốc máy, bôi trơn gioăng cao su lọc mới, xiết lực đúng tiêu chuẩn và chạy thử kiểm tra áp suất dầu.',
 'assets/images/part.svg', 120000.00, 30, NULL, NULL, 'Đang cung cấp'),

(2, 'Bảo dưỡng và vệ sinh hệ thống phanh 4 bánh', 'Bảo dưỡng',
 'Tiền công bảo dưỡng, vệ sinh cùm phanh, tra mỡ chịu nhiệt má phanh và đĩa phanh.',
 'Tháo bánh xe, kiểm tra độ mòn má phanh và đĩa phanh, vệ sinh bụi phanh, tra mỡ chuyên dụng ắc thắng, lắp ráp và cân chỉnh lực phanh.',
 'assets/images/brake.svg', 250000.00, 45, '3 tháng', 'Bảo hành tiếng kêu bất thường và độ ăn phanh đồng đều giữa các bánh.', 'Đang cung cấp'),

(3, 'Vệ sinh khoang máy bằng hơi nước nóng (Ngừng phục vụ)', 'Chăm sóc xe',
 'Dịch vụ làm sạch khoang động cơ bằng hơi nước nóng áp suất cao.',
 'Che chắn các cụm điện tử, phun hơi nước nóng làm mềm dầu mỡ và lau khô bằng khăn vi sợi.',
 'assets/images/garage.svg', 350000.00, 60, 'Không bảo hành', 'Khách hàng tự chịu rủi ro ẩm cụm điện.', 'Ngừng cung cấp');

-- 7. Bảng phụ tùng (tblSparePart4)
INSERT INTO `tblSparePart4` (`id`, `name`, `description`, `imagePath`, `unit`, `manufacturer`, `specifications`, `compatibleVehicleModels`, `sellingPrice`, `warrantyPeriod`, `warrantyTerms`, `status`, `stockQuantity`, `tblSparePartCategory4id`) VALUES
(1, 'Lọc dầu động cơ AF-01',
 'Lọc cặn bẩn trong dầu động cơ, dùng thay thế khi bảo dưỡng xe',
 'assets/images/part.svg', 'Cái', 'AutoFilter', 'Lọc dầu dạng vặn; đường kính 76 mm; chiều cao 85 mm', 'Chưa cập nhật',
 150000.00, '6 tháng', 'Áp dụng với lỗi sản xuất; không áp dụng khi lắp sai hoặc sử dụng sai hướng dẫn',
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

-- 8 & 9. Chứng từ nhập khởi tạo tồn ban đầu cho part 2, 3, 5 (đảm bảo 03_verify.sql đối chiếu tồn kho nhất quán)
INSERT INTO `tblPurchaseInvoice4` (`id`, `purchaseDate`, `confirmedAt`, `paymentStatus`, `paidAt`, `tblSupplier4id`, `tblEmployee4id`, `requestCode`, `supplierNameSnapshot`, `warehouseStaffNameSnapshot`) VALUES
(100, '2026-10-01 08:00:00', '2026-10-01 08:00:00', 'Đã thanh toán', '2026-10-01 08:30:00', 1, 2, '00000000-0000-0000-0000-000000000001', 'Hòa Phát', 'Trần Văn Kho (Thủ kho)');

INSERT INTO `tblPurchaseInvoiceDetail4` (`id`, `quantity`, `purchaseUnitPrice`, `tblPurchaseInvoice4id`, `tblSparePart4id`, `sparePartNameSnapshot`, `unitSnapshot`) VALUES
(1, 5.000, 300000.00, 100, 2, 'Má phanh đĩa trước BP-02', 'Bộ'),
(2, 20.000, 120000.00, 100, 3, 'Dầu động cơ Castrol Edge 5W-30', 'Lít'),
(3, 2.000, 60000.00, 100, 5, 'Lọc gió điều hòa cũ ngưng kinh doanh', 'Cái');
