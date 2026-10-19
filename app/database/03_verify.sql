-- =====================================================================
-- Script kiểm tra tính nhất quán và toàn vẹn dữ liệu (03_verify.sql)
-- Script CHỈ ĐỌC (Read-Only) đối chiếu các bất biến nghiệp vụ
-- =====================================================================

USE `garaman4`;

-- 1. Đối chiếu tồn kho lưu trữ với lịch sử nhập/xuất thực tế
-- Lưu ý: Tổng nhập và tổng xuất được aggregate riêng trước khi JOIN để tránh lỗi nhân bản bản ghi
SELECT 
    sp.id AS phuTungId,
    sp.ten AS tenPhuTung,
    sp.donVi,
    sp.tonKho AS tonKhoLuuTru,
    COALESCE(nhap.tongNhap, 0) AS tongLuongNhap,
    COALESCE(xuat.tongXuat, 0) AS tongLuongXuat,
    (COALESCE(nhap.tongNhap, 0) - COALESCE(xuat.tongXuat, 0)) AS tonKhoTinhToan,
    CASE 
        WHEN sp.tonKho = (COALESCE(nhap.tongNhap, 0) - COALESCE(xuat.tongXuat, 0)) THEN 'KHỚP'
        ELSE 'LỆCH'
    END AS ketQuaDoiChieu
FROM tblSparePart4 sp
LEFT JOIN (
    SELECT pid.tblSparePart4id, SUM(pid.soLuong) AS tongNhap
    FROM tblPurchaseInvoiceDetail4 pid
    INNER JOIN tblPurchaseInvoice4 pi ON pid.tblPurchaseInvoice4id = pi.id
    GROUP BY pid.tblSparePart4id
) nhap ON sp.id = nhap.tblSparePart4id
LEFT JOIN (
    SELECT rp.tblSparePart4id, SUM(rp.soLuongThucTe) AS tongXuat
    FROM tblRepairPart4 rp
    INNER JOIN tblRepairIntake4 ri ON rp.tblRepairIntake4id = ri.id
    INNER JOIN tblCustomerInvoice4 ci ON ri.id = ci.tblRepairIntake4id
    GROUP BY rp.tblSparePart4id
) xuat ON sp.id = xuat.tblSparePart4id;

-- 2. Kiểm tra tính toán tổng tiền từng hóa đơn nhập so với tổng dòng chi tiết
SELECT 
    pi.id AS hoaDonId,
    pi.maYeuCau,
    pi.tinhTrang,
    COUNT(pid.id) AS soDongChiTiet,
    SUM(ROUND(pid.soLuong * pid.donGiaNhap, 2)) AS tongTienChiTiet,
    CASE 
        WHEN COUNT(pid.id) = 0 THEN 'LỖI: HÓA ĐƠN KHÔNG CÓ DÒNG'
        ELSE 'HỢP LỆ'
    END AS trangThaiDong
FROM tblPurchaseInvoice4 pi
LEFT JOIN tblPurchaseInvoiceDetail4 pid ON pi.id = pid.tblPurchaseInvoice4id
GROUP BY pi.id, pi.maYeuCau, pi.tinhTrang;

-- 3. Kiểm tra trùng lặp mã yêu cầu (maYeuCau) trên hóa đơn nhập
SELECT 
    maYeuCau, 
    COUNT(*) AS soLuongLap
FROM tblPurchaseInvoice4
GROUP BY maYeuCau
HAVING COUNT(*) > 1;

-- 4. Kiểm tra trùng lặp phụ tùng trong cùng một hóa đơn nhập
SELECT 
    tblPurchaseInvoice4id, 
    tblSparePart4id, 
    COUNT(*) AS soLuongTrung
FROM tblPurchaseInvoiceDetail4
GROUP BY tblPurchaseInvoice4id, tblSparePart4id
HAVING COUNT(*) > 1;

-- 5. Kiểm tra tính nhất quán giữa tình trạng hóa đơn và thời điểm thanh toán
SELECT 
    id AS hoaDonId,
    'PurchaseInvoice' AS loaiHoaDon,
    tinhTrang,
    thoiDiemThanhToan,
    'LỖI: BẤT NHẤT TRẠNG THÁI THANH TOÁN' AS ghiChu
FROM tblPurchaseInvoice4
WHERE (tinhTrang = 'Chưa thanh toán' AND thoiDiemThanhToan IS NOT NULL)
   OR (tinhTrang = 'Đã thanh toán' AND thoiDiemThanhToan IS NULL)
UNION ALL
SELECT 
    id AS hoaDonId,
    'CustomerInvoice' AS loaiHoaDon,
    tinhTrang,
    thoiDiemThanhToan,
    'LỖI: BẤT NHẤT TRẠNG THÁI THANH TOÁN' AS ghiChu
FROM tblCustomerInvoice4
WHERE (tinhTrang = 'Chưa thanh toán' AND thoiDiemThanhToan IS NOT NULL)
   OR (tinhTrang = 'Đã thanh toán' AND thoiDiemThanhToan IS NULL);
