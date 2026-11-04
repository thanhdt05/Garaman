# Đối chiếu hàm trong code với DOCX — 30/10/2026

Nguồn: hình 2.4 và hình 2.6 nhúng trong `26_B23DCCN764.docx` hiện tại; đối chiếu thêm phần scenario khi xét hành vi thanh toán. Không lấy riêng PlantUML làm nguồn thay thế các hình trong Word.

Theo xác nhận của người làm bài, `[]` trong báo cáo có nghĩa là danh sách; `List<T>` trong Java được chấp nhận tương ứng. Vì vậy các kiểu trả về `Service4[]`, `SparePart4[]`, `Supplier4[]`, `SparePartCategory4[]` trong hình không được tính là khác hợp đồng với `List<T>` trong code.

## Kết luận

**Có đủ 36 hàm nghiệp vụ được thể hiện trong hai hình thiết kế, đúng tên, thứ tự/kiểu tham số theo bản thiết kế chi tiết và kiểu kết quả theo quy ước danh sách đã chốt. Tuy nhiên tập hàm trong code nhiều hơn tập hàm được vẽ trong báo cáo.**

Các hàm Servlet trong hình Word ghi `request, response`; code cụ thể hóa bằng `HttpServletRequest`, `HttpServletResponse`. Constructor DAO không viết tường minh trong source được Java cung cấp constructor mặc định không tham số; không coi là thiếu constructor.

| Tầng | Hàm nghiệp vụ trong hai hình | Kết quả |
|---|---:|---|
| Servlet | 21 | Có đủ 3 hàm CatalogServlet4, 4 hàm SupplierServlet4, 2 hàm SparePartServlet4, 12 hàm PurchaseInvoiceServlet4. |
| DAO | 14 | Có đủ; `increaseStockAfterPurchase` giữ phạm vi package như dấu `~` trong hình. |
| Model | 1 | `PurchaseInvoice4.calculateTotalAmount(): BigDecimal` có trong code. |

## Hàm public/package trong các lớp hai modul chưa được vẽ

| Lớp | Hàm có trong code, chưa được thể hiện ở hai hình |
|---|---|
| SparePartDAO4 | `getStockQuantity(int sparePartId): BigDecimal` |
| SparePartCategoryDAO4 | `getActiveCategory(int id): SparePartCategory4` |
| PurchaseInvoiceDAO4 | `findInvoiceIdByRequestCode(String requestCode, int warehouseStaffId): Integer`; `getUnpaidInvoices(int warehouseStaffId): List<PurchaseInvoice4>` |
| PurchaseInvoiceDetail4 | `calculateLineAmount(): BigDecimal`; getter dẫn xuất `getLineAmount(): BigDecimal` |
| PurchaseInvoice4 | Getter dẫn xuất `getTotalAmount(): BigDecimal` |

`getActiveCategory` đang được gọi khi thêm phụ tùng; `findInvoiceIdByRequestCode` đang dùng để chống lưu trùng; `getUnpaidInvoices` phục vụ hàm `showUnpaidInvoices` đã được vẽ trong Servlet; `calculateLineAmount` đang được gọi khi tính tổng. Không thể xóa trực tiếp các hàm đang được gọi mà không sửa nơi sử dụng. `SparePartDAO4.getStockQuantity` và `MemberDAO4.getActiveMember` không có nơi gọi trong `src/main/java` hiện tại; hàm đọc tồn có được dùng trong kiểm thử.

## Hàm kỹ thuật chưa được vẽ

| Lớp | Hàm kỹ thuật trong code |
|---|---|
| Các Servlet | `init`, `doGet`, `doPost` tùy lớp; đây là điểm khởi tạo/nhận yêu cầu HTTP. |
| PurchaseInvoiceServlet4 | `validateVersionAndConcurrency`, `saveLineInputsToDraft` (private) |
| DAO | `loadConfiguration` (private), `getConnection`, `createSearchPattern` (protected) |
| ServiceDAO4 | `mapService` (private) |
| SparePartDAO4 | `mapSparePart` (private) |
| SupplierDAO4 | `mapSupplier` (private) |
| SparePartCategoryDAO4 | `mapCategory` (private) |
| PurchaseInvoiceDAO4 | `insertInvoice`, `insertInvoiceDetails`, `getInvoiceDetails`, `mapInvoice` (private) |
| Các model | Constructor và getter/setter để đọc, gán thuộc tính; hai hình không liệt kê toàn bộ các hàm này. |

Phần đăng nhập/điều hướng hỗ trợ còn có các lớp ngoài hai hình: `AuthServlet4` (`init`, `doGet`, `doPost`, `showLogin`, `login`, `logout`), `HomeServlet4` (`doGet`, `showHome`), `MemberDAO4` (`findByUsername`, `getActiveMember`, `mapMember`). Các tên này đã có trong code trước lần rút gọn; chưa được thiết kế thành các lớp tương ứng trong hai hình DOCX.

## Hành vi đã sửa sau lần đối chiếu

Theo yêu cầu sửa code tiếp theo, `PurchaseInvoiceDAO4.recordPayment(...)` đã cho nhân viên kho có quyền hiện tại ghi nhận thanh toán, lưu riêng người trả và giữ người lập; `getUnpaidInvoices(...)` đã trả danh sách chưa trả của gara. Chữ ký các hàm giữ nguyên. Xem [thay đổi và kết quả kiểm chứng](SUA_LUONG_THEO_BAO_CAO.md).

## Đính chính phạm vi kiểm tra trước

Kết quả “không thêm hàm” ở lần rút gọn trước là đối chiếu source trước/sau khi bỏ bốn gói phụ, **không phải kết quả so toàn bộ hàm với DOCX**. Những hàm chưa vẽ nêu trên đã tồn tại trong source và vẫn được giữ lại. Lượt đối chiếu ban đầu chỉ đọc code; lần sửa luồng tiếp theo giữ nguyên danh sách hàm và không sửa DOCX.

[Danh sách đối chiếu từng hàm và các hàm ngoài hình](diagrams/report-method-comparison.json).
