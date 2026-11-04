-- =====================================================================
-- Script kiểm tra tính nhất quán và toàn vẹn dữ liệu (03_verify.sql)
-- Áp dụng phạm vi 9 bảng chuẩn hóa theo 00_PHAM_VI.md và 03_HOP_DONG.md
-- Script CHỈ ĐỌC (Read-Only) đối chiếu các bất biến nghiệp vụ của 2 module
-- =====================================================================

USE `garaman4`;

-- 1. Đối chiếu tồn kho lưu trữ (tonKho) với tổng nhập thực tế từ các hóa đơn đã xác nhận
-- Trên CSDL riêng 2 module, tồn ban đầu bằng 0, tonKho phải bằng tổng nhập
SELECT 
    sp.id AS sparePartId,
    sp.name AS sparePartKeyword,
    sp.unit,
    sp.stockQuantity AS storedStockQuantity,
    COALESCE(purchases.receivedQuantity, 0) AS totalReceivedQuantity,
    CASE 
        WHEN sp.stockQuantity = COALESCE(purchases.receivedQuantity, 0) THEN 'KHỚP'
        ELSE 'LỆCH'
    END AS comparisonResult
FROM tblSparePart4 sp
LEFT JOIN (
    SELECT pid.tblSparePart4id, SUM(pid.quantity) AS receivedQuantity
    FROM tblPurchaseInvoiceDetail4 pid
    INNER JOIN tblPurchaseInvoice4 pi ON pid.tblPurchaseInvoice4id = pi.id
    GROUP BY pid.tblSparePart4id
) purchases ON sp.id = purchases.tblSparePart4id;

-- 2. Kiểm tra tính toán tổng tiền từng hóa đơn nhập so với tổng dòng chi tiết
SELECT 
    pi.id AS invoiceId,
    pi.requestCode,
    pi.paymentStatus,
    COUNT(pid.id) AS detailCount,
    SUM(ROUND(pid.quantity * pid.purchaseUnitPrice, 2)) AS detailsTotalAmount,
    CASE 
        WHEN COUNT(pid.id) = 0 THEN 'LỖI: HÓA ĐƠN KHÔNG CÓ DÒNG'
        ELSE 'HỢP LỆ'
    END AS lineStatus
FROM tblPurchaseInvoice4 pi
LEFT JOIN tblPurchaseInvoiceDetail4 pid ON pi.id = pid.tblPurchaseInvoice4id
GROUP BY pi.id, pi.requestCode, pi.paymentStatus;

-- 3. Kiểm tra trùng lặp mã yêu cầu (maYeuCau) trên hóa đơn nhập
SELECT 
    requestCode, 
    COUNT(*) AS duplicateCount
FROM tblPurchaseInvoice4
GROUP BY requestCode
HAVING COUNT(*) > 1;

-- 4. Kiểm tra trùng lặp phụ tùng trong cùng một hóa đơn nhập
SELECT 
    tblPurchaseInvoice4id, 
    tblSparePart4id, 
    COUNT(*) AS duplicateCount
FROM tblPurchaseInvoiceDetail4
GROUP BY tblPurchaseInvoice4id, tblSparePart4id
HAVING COUNT(*) > 1;

-- 5. Kiểm tra tính nhất quán giữa tình trạng hóa đơn nhập và thời điểm thanh toán
SELECT 
    id AS invoiceId,
    requestCode,
    paymentStatus,
    paidAt,
    'LỖI: BẤT NHẤT TRẠNG THÁI THANH TOÁN' AS notes
FROM tblPurchaseInvoice4
WHERE (paymentStatus = 'Chưa thanh toán' AND paidAt IS NOT NULL)
   OR (paymentStatus = 'Đã thanh toán' AND paidAt IS NULL);

-- 6. Kiểm tra mã và tên người thực sự trả; không nhầm với tblEmployee4id của người lập
SELECT id AS invoiceId, tblEmployee4id AS createdByEmployee4id,
       paidByEmployee4id, paidByNameSnapshot, paymentStatus, paidAt
FROM tblPurchaseInvoice4
WHERE (paidByEmployee4id IS NULL AND paidByNameSnapshot IS NOT NULL)
   OR (paidByEmployee4id IS NOT NULL AND paidByNameSnapshot IS NULL)
   OR (paymentStatus = 'Chưa thanh toán' AND paidByEmployee4id IS NOT NULL);

-- Hóa đơn lịch sử đã trả nhưng không có người trả: hiển thị để rà soát, không tự suy đoán
SELECT id AS legacyPaidInvoiceId, paidAt
FROM tblPurchaseInvoice4
WHERE paymentStatus = 'Đã thanh toán' AND paidByEmployee4id IS NULL;
