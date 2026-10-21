-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: garaman4
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `tblcustomer4`
--

DROP TABLE IF EXISTS `tblcustomer4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblcustomer4` (
  `tblMember4id` int NOT NULL,
  PRIMARY KEY (`tblMember4id`),
  CONSTRAINT `fk_customer_member` FOREIGN KEY (`tblMember4id`) REFERENCES `tblmember4` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblcustomer4`
--

LOCK TABLES `tblcustomer4` WRITE;
/*!40000 ALTER TABLE `tblcustomer4` DISABLE KEYS */;
INSERT INTO `tblcustomer4` VALUES (1);
/*!40000 ALTER TABLE `tblcustomer4` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblemployee4`
--

DROP TABLE IF EXISTS `tblemployee4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblemployee4` (
  `tblMember4id` int NOT NULL,
  `viTri` varchar(30) NOT NULL,
  `trangThaiLamViec` varchar(30) NOT NULL,
  PRIMARY KEY (`tblMember4id`),
  CONSTRAINT `fk_employee_member` FOREIGN KEY (`tblMember4id`) REFERENCES `tblmember4` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `chk_employee_trangThai` CHECK ((`trangThaiLamViec` in (_utf8mb4'Đang làm việc',_utf8mb4'Ngừng làm việc'))),
  CONSTRAINT `chk_employee_viTri` CHECK ((`viTri` = _utf8mb4'Kho'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblemployee4`
--

LOCK TABLES `tblemployee4` WRITE;
/*!40000 ALTER TABLE `tblemployee4` DISABLE KEYS */;
INSERT INTO `tblemployee4` VALUES (2,'Kho','Đang làm việc');
/*!40000 ALTER TABLE `tblemployee4` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblmember4`
--

DROP TABLE IF EXISTS `tblmember4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblmember4` (
  `id` int NOT NULL AUTO_INCREMENT,
  `hoTen` varchar(255) NOT NULL,
  `tenDangNhap` varchar(100) DEFAULT NULL,
  `matKhau` varchar(255) DEFAULT NULL,
  `ngaySinh` date DEFAULT NULL,
  `diaChi` text,
  `email` varchar(255) DEFAULT NULL,
  `dienThoai` varchar(20) NOT NULL,
  `vaiTro` varchar(30) NOT NULL,
  `trangThaiTaiKhoan` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tenDangNhap` (`tenDangNhap`),
  CONSTRAINT `chk_member_account` CHECK ((((`tenDangNhap` is null) and (`matKhau` is null) and (`trangThaiTaiKhoan` is null) and (`vaiTro` = _utf8mb4'Customer')) or ((`tenDangNhap` is not null) and (`matKhau` is not null) and (`trangThaiTaiKhoan` is not null)))),
  CONSTRAINT `chk_member_trangThai` CHECK (((`trangThaiTaiKhoan` is null) or (`trangThaiTaiKhoan` in (_utf8mb4'Hoạt động',_utf8mb4'Đã khóa')))),
  CONSTRAINT `chk_member_vaiTro` CHECK ((`vaiTro` in (_utf8mb4'Customer',_utf8mb4'WarehouseStaff')))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblmember4`
--

LOCK TABLES `tblmember4` WRITE;
/*!40000 ALTER TABLE `tblmember4` DISABLE KEYS */;
INSERT INTO `tblmember4` VALUES (1,'Đàm Tiến Thành (Khách)','khach26','pbkdf2_sha256$600000$3fGGHCpOcHuqkDsfiHGjKQ==$/f0Fp96dlpDSsAg92MwD7u5SOmEwWwJ71fT79UrGoec=','2005-01-15','128 Trần Phú, Hà Đông, Hà Nội','khach26@garaman.vn','0912345678','Customer','Hoạt động'),(2,'Trần Văn Kho (Thủ kho)','kho26','pbkdf2_sha256$600000$3fGGHCpOcHuqkDsfiHGjKQ==$/f0Fp96dlpDSsAg92MwD7u5SOmEwWwJ71fT79UrGoec=','1995-05-20','56 Nguyễn Trãi, Thanh Xuân, Hà Nội','kho26@garaman.vn','0923456789','WarehouseStaff','Hoạt động');
/*!40000 ALTER TABLE `tblmember4` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblpurchaseinvoice4`
--

DROP TABLE IF EXISTS `tblpurchaseinvoice4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblpurchaseinvoice4` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ngayNhap` datetime NOT NULL,
  `thoiDiemXacNhan` datetime NOT NULL,
  `tinhTrang` varchar(30) NOT NULL DEFAULT 'Chưa thanh toán',
  `thoiDiemThanhToan` datetime DEFAULT NULL,
  `tblSupplier4id` int NOT NULL,
  `tblEmployee4id` int NOT NULL,
  `maYeuCau` char(36) NOT NULL,
  `nhaCungCapTenApDung` varchar(255) NOT NULL,
  `nhanVienTenApDung` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_purchase_request` (`maYeuCau`),
  KEY `fk_purchase_inv_supplier` (`tblSupplier4id`),
  KEY `fk_purchase_inv_employee` (`tblEmployee4id`),
  CONSTRAINT `fk_purchase_inv_employee` FOREIGN KEY (`tblEmployee4id`) REFERENCES `tblemployee4` (`tblMember4id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_purchase_inv_supplier` FOREIGN KEY (`tblSupplier4id`) REFERENCES `tblsupplier4` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `chk_purchase_invoice_thanhToan` CHECK ((((`tinhTrang` = _utf8mb4'Chưa thanh toán') and (`thoiDiemThanhToan` is null)) or ((`tinhTrang` = _utf8mb4'Đã thanh toán') and (`thoiDiemThanhToan` is not null)))),
  CONSTRAINT `chk_purchase_invoice_tinhTrang` CHECK ((`tinhTrang` in (_utf8mb4'Chưa thanh toán',_utf8mb4'Đã thanh toán')))
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblpurchaseinvoice4`
--

LOCK TABLES `tblpurchaseinvoice4` WRITE;
/*!40000 ALTER TABLE `tblpurchaseinvoice4` DISABLE KEYS */;
INSERT INTO `tblpurchaseinvoice4` VALUES (100,'2026-10-01 08:00:00','2026-10-01 08:00:00','Đã thanh toán','2026-10-01 08:30:00',1,2,'00000000-0000-0000-0000-000000000001','Hòa Phát','Trần Văn Kho (Thủ kho)'),(101,'2026-10-21 17:22:25','2026-10-21 17:22:25','Đã thanh toán','2026-10-21 17:22:41',1,2,'f40555b2-21c3-47b3-8c25-a0871a8dc9ec','Hòa Phát','Trần Văn Kho (Thủ kho)');
/*!40000 ALTER TABLE `tblpurchaseinvoice4` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblpurchaseinvoicedetail4`
--

DROP TABLE IF EXISTS `tblpurchaseinvoicedetail4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblpurchaseinvoicedetail4` (
  `id` int NOT NULL AUTO_INCREMENT,
  `soLuong` decimal(12,3) NOT NULL,
  `donGiaNhap` decimal(18,2) NOT NULL,
  `tblPurchaseInvoice4id` int NOT NULL,
  `tblSparePart4id` int NOT NULL,
  `tenPhuTungApDung` varchar(255) NOT NULL,
  `donViApDung` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_purchase_part` (`tblPurchaseInvoice4id`,`tblSparePart4id`),
  KEY `fk_purchase_detail_part` (`tblSparePart4id`),
  CONSTRAINT `fk_purchase_detail_inv` FOREIGN KEY (`tblPurchaseInvoice4id`) REFERENCES `tblpurchaseinvoice4` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_purchase_detail_part` FOREIGN KEY (`tblSparePart4id`) REFERENCES `tblsparepart4` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `chk_purchase_detail_donGia` CHECK ((`donGiaNhap` > 0)),
  CONSTRAINT `chk_purchase_detail_soLuong` CHECK ((`soLuong` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblpurchaseinvoicedetail4`
--

LOCK TABLES `tblpurchaseinvoicedetail4` WRITE;
/*!40000 ALTER TABLE `tblpurchaseinvoicedetail4` DISABLE KEYS */;
INSERT INTO `tblpurchaseinvoicedetail4` VALUES (1,5.000,300000.00,100,2,'Má phanh đĩa trước BP-02','Bộ'),(2,20.000,120000.00,100,3,'Dầu động cơ Castrol Edge 5W-30','Lít'),(3,2.000,60000.00,100,5,'Lọc gió điều hòa cũ ngưng kinh doanh','Cái'),(4,1.000,10.00,101,1,'Lọc dầu động cơ AF-01','Cái');
/*!40000 ALTER TABLE `tblpurchaseinvoicedetail4` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblservice4`
--

DROP TABLE IF EXISTS `tblservice4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblservice4` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ten` varchar(255) NOT NULL,
  `nhom` varchar(255) NOT NULL,
  `moTa` text NOT NULL,
  `noiDung` text NOT NULL,
  `hinhAnh` varchar(255) DEFAULT NULL,
  `donGia` decimal(18,2) NOT NULL,
  `thoiGianDuKien` int NOT NULL,
  `hanBaoHanh` varchar(255) DEFAULT NULL,
  `dieuKienBaoHanh` text,
  `trangThai` varchar(30) NOT NULL DEFAULT 'Đang cung cấp',
  PRIMARY KEY (`id`),
  CONSTRAINT `chk_service_donGia` CHECK ((`donGia` >= 0)),
  CONSTRAINT `chk_service_thoiGian` CHECK ((`thoiGianDuKien` > 0)),
  CONSTRAINT `chk_service_trangThai` CHECK ((`trangThai` in (_utf8mb4'Đang cung cấp',_utf8mb4'Ngừng cung cấp')))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblservice4`
--

LOCK TABLES `tblservice4` WRITE;
/*!40000 ALTER TABLE `tblservice4` DISABLE KEYS */;
INSERT INTO `tblservice4` VALUES (1,'Thay lọc dầu động cơ','Bảo dưỡng','Tiền công thay lọc dầu động cơ xe du lịch, chưa bao gồm phụ tùng lọc dầu.','Quy trình thực hiện: Nâng xe, kiểm tra tình trạng rò rỉ bôi trơn, tháo lọc dầu cũ, vệ sinh bề mặt tiếp xúc lốc máy, bôi trơn gioăng cao su lọc mới, xiết lực đúng tiêu chuẩn và chạy thử kiểm tra áp suất dầu.','assets/images/part.svg',120000.00,30,NULL,NULL,'Đang cung cấp'),(2,'Bảo dưỡng và vệ sinh hệ thống phanh 4 bánh','Bảo dưỡng','Tiền công bảo dưỡng, vệ sinh cùm phanh, tra mỡ chịu nhiệt má phanh và đĩa phanh.','Tháo bánh xe, kiểm tra độ mòn má phanh và đĩa phanh, vệ sinh bụi phanh, tra mỡ chuyên dụng ắc thắng, lắp ráp và cân chỉnh lực phanh.','assets/images/brake.svg',250000.00,45,'3 tháng','Bảo hành tiếng kêu bất thường và độ ăn phanh đồng đều giữa các bánh.','Đang cung cấp'),(3,'Vệ sinh khoang máy bằng hơi nước nóng (Ngừng phục vụ)','Chăm sóc xe','Dịch vụ làm sạch khoang động cơ bằng hơi nước nóng áp suất cao.','Che chắn các cụm điện tử, phun hơi nước nóng làm mềm dầu mỡ và lau khô bằng khăn vi sợi.','assets/images/garage.svg',350000.00,60,'Không bảo hành','Khách hàng tự chịu rủi ro ẩm cụm điện.','Ngừng cung cấp');
/*!40000 ALTER TABLE `tblservice4` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblsparepart4`
--

DROP TABLE IF EXISTS `tblsparepart4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblsparepart4` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ten` varchar(255) NOT NULL,
  `moTa` text,
  `hinhAnh` varchar(255) DEFAULT NULL,
  `donVi` varchar(20) NOT NULL,
  `hangSanXuat` varchar(255) NOT NULL,
  `thongSo` text,
  `dongXePhuHop` text,
  `giaBan` decimal(18,2) DEFAULT NULL,
  `hanBaoHanh` varchar(255) DEFAULT NULL,
  `dieuKienBaoHanh` text,
  `trangThai` varchar(30) NOT NULL DEFAULT 'Chưa công bố',
  `tonKho` decimal(12,3) NOT NULL DEFAULT '0.000',
  `tblSparePartCategory4id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_sparepart_category` (`tblSparePartCategory4id`),
  CONSTRAINT `fk_sparepart_category` FOREIGN KEY (`tblSparePartCategory4id`) REFERENCES `tblsparepartcategory4` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `chk_sparepart_donVi` CHECK ((`donVi` in (_utf8mb4'Cái',_utf8mb4'Chiếc',_utf8mb4'Bộ',_utf8mb4'Lít'))),
  CONSTRAINT `chk_sparepart_giaBan` CHECK (((`giaBan` is null) or (`giaBan` >= 0))),
  CONSTRAINT `chk_sparepart_kinhDoanh` CHECK (((`trangThai` <> _utf8mb4'Đang kinh doanh') or ((`giaBan` is not null) and (`moTa` is not null) and (char_length(trim(`moTa`)) > 0)))),
  CONSTRAINT `chk_sparepart_tonKho` CHECK ((`tonKho` >= 0)),
  CONSTRAINT `chk_sparepart_trangThai` CHECK ((`trangThai` in (_utf8mb4'Chưa công bố',_utf8mb4'Đang kinh doanh',_utf8mb4'Ngừng kinh doanh')))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblsparepart4`
--

LOCK TABLES `tblsparepart4` WRITE;
/*!40000 ALTER TABLE `tblsparepart4` DISABLE KEYS */;
INSERT INTO `tblsparepart4` VALUES (1,'Lọc dầu động cơ AF-01','Lọc cặn bẩn trong dầu động cơ, dùng thay thế khi bảo dưỡng xe','assets/images/part.svg','Cái','AutoFilter','Lọc dầu dạng vặn; đường kính 76 mm; chiều cao 85 mm','Chưa cập nhật',150000.00,'6 tháng','Áp dụng với lỗi sản xuất; không áp dụng khi lắp sai hoặc sử dụng sai hướng dẫn','Đang kinh doanh',1.000,1),(2,'Má phanh đĩa trước BP-02','Bộ má phanh đĩa gốm cao cấp Brembo BP-02 chịu nhiệt cao, giảm thiểu mài mòn đĩa.','assets/images/brake.svg','Bộ','Brembo','Độ dày má phanh 15 mm, hợp kim gốm chịu nhiệt','Phù hợp các dòng xe Sedan cỡ C và D',450000.00,'12 tháng','Má phanh mòn không đều hoặc bong tróc lớp ma sát trong thời gian bảo hành.','Đang kinh doanh',5.000,2),(3,'Dầu động cơ Castrol Edge 5W-30','Dầu nhớt tổng hợp toàn phần Castrol Edge công nghệ Sức mạnh Chuyển động Titanium.','assets/images/part.svg','Lít','Castrol','Tiêu chuẩn API SP, ILSAC GF-6, độ nhớt 5W-30','Các dòng xe máy xăng và máy dầu nhẹ đời mới',180000.00,'12 tháng','Bao bì nguyên vẹn, còn hạn sử dụng.','Đang kinh doanh',20.000,1),(4,'Bugi Iridium IX Laser thử nghiệm','Bugi đánh lửa cực bền điện cực Iridium siêu mảnh 0.6 mm.','assets/images/part.svg','Chiếc','NGK','Ren M14x1.25, tầm nhiệt 7','Thử nghiệm nội bộ',NULL,'12 tháng','Chưa có thông tin bảo hành','Chưa công bố',0.000,1),(5,'Lọc gió điều hòa cũ ngưng kinh doanh','Lọc gió cabin than hoạt tính lọc bụi mịn PM2.5.','assets/images/part.svg','Cái','Denso','Kích thước 215x200x30 mm','Xe đời cũ',95000.00,'1 tháng','Không bảo hành ẩm mốc','Ngừng kinh doanh',2.000,1);
/*!40000 ALTER TABLE `tblsparepart4` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblsparepartcategory4`
--

DROP TABLE IF EXISTS `tblsparepartcategory4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblsparepartcategory4` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ten` varchar(255) NOT NULL,
  `moTa` text,
  `trangThai` varchar(30) NOT NULL DEFAULT 'Đang sử dụng',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ten` (`ten`),
  CONSTRAINT `chk_category_trangThai` CHECK ((`trangThai` in (_utf8mb4'Đang sử dụng',_utf8mb4'Ngừng sử dụng')))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblsparepartcategory4`
--

LOCK TABLES `tblsparepartcategory4` WRITE;
/*!40000 ALTER TABLE `tblsparepartcategory4` DISABLE KEYS */;
INSERT INTO `tblsparepartcategory4` VALUES (1,'Phụ tùng động cơ','Các linh kiện, phụ tùng thay thế định kỳ và sửa chữa động cơ đốt trong','Đang sử dụng'),(2,'Hệ thống phanh','Má phanh, đĩa phanh, dầu phanh và phụ kiện an toàn hệ thống phanh','Đang sử dụng'),(3,'Khung gầm cũ','Nhóm danh mục phụ tùng khung gầm ngưng kinh doanh','Ngừng sử dụng');
/*!40000 ALTER TABLE `tblsparepartcategory4` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tblsupplier4`
--

DROP TABLE IF EXISTS `tblsupplier4`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tblsupplier4` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ten` varchar(255) NOT NULL,
  `diaChi` text NOT NULL,
  `dienThoai` varchar(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `trangThai` varchar(30) NOT NULL DEFAULT 'Đang hoạt động',
  PRIMARY KEY (`id`),
  CONSTRAINT `chk_supplier_trangThai` CHECK ((`trangThai` in (_utf8mb4'Đang hoạt động',_utf8mb4'Ngừng hoạt động')))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tblsupplier4`
--

LOCK TABLES `tblsupplier4` WRITE;
/*!40000 ALTER TABLE `tblsupplier4` DISABLE KEYS */;
INSERT INTO `tblsupplier4` VALUES (1,'Hòa Phát','Hà Nội','0900000001',NULL,'Đang hoạt động'),(2,'Tổng kho Phụ tùng Ô tô Miền Nam','456 An Dương Vương, Phường 4, Quận 5, TP. Hồ Chí Minh','0900000002','kinhdoanh@phutungmiennam.com','Đang hoạt động'),(3,'Đại lý Phụ tùng Thiết bị Cũ','789 Đường Láng, Đống Đa, Hà Nội','0900000003','support@phutungcu.vn','Ngừng hoạt động');
/*!40000 ALTER TABLE `tblsupplier4` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-21 21:23:51
