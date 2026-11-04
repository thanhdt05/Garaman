-- Bổ sung người thực sự thanh toán theo scenario v3.0 trong DOCX.
-- Dùng một lần cho CSDL tên tiếng Anh đã có trước bản sửa luồng 30/10/2026.
-- CSDL tạo mới bằng 01_schema.sql đã có các cột này, không chạy lại migration.
-- Dừng ứng dụng và sao lưu CSDL trước khi áp dụng trên môi trường đang sử dụng.
-- DDL MySQL tự commit; không coi START TRANSACTION là bản sao lưu cho ALTER TABLE.

USE `garaman4`;

ALTER TABLE `tblPurchaseInvoice4`
    ADD COLUMN `paidByEmployee4id` INT NULL AFTER `paidAt`,
    ADD COLUMN `paidByNameSnapshot` VARCHAR(255) NULL AFTER `paidByEmployee4id`,
    ADD CONSTRAINT `chk_purchase_invoice_paidBy` CHECK (
        (`paidByEmployee4id` IS NULL AND `paidByNameSnapshot` IS NULL)
        OR (`paymentStatus` = 'Đã thanh toán' AND `paidByEmployee4id` IS NOT NULL
            AND `paidByNameSnapshot` IS NOT NULL AND CHAR_LENGTH(TRIM(`paidByNameSnapshot`)) > 0)
    ),
    ADD CONSTRAINT `fk_purchase_inv_paid_by` FOREIGN KEY (`paidByEmployee4id`)
        REFERENCES `tblEmployee4` (`tblMember4id`) ON DELETE RESTRICT;

-- Không gán người lập làm người trả cho hóa đơn lịch sử: dữ liệu cũ không chứng minh điều đó.
-- Các hóa đơn đã trả trước migration giữ nguyên trạng thái/ngày trả, để NULL phần người trả.
-- recordPayment chỉ chốt người trả ở lần ghi nhận thành công đầu tiên, không sửa khi gửi lại.
