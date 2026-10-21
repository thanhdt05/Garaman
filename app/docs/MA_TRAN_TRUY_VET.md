# MA TRẬN TRUY VẾT YÊU CẦU - THIẾT KẾ - MÃ NGUỒN - KIỂM THỬ (MA_TRAN_TRUY_VET.md)

Tài liệu này thiết lập ma trận truy vết hai chiều (Bidirectional Traceability Matrix) từ yêu cầu nghiệp vụ trong báo cáo phân tích (`26_B23DCCN764-full.docx`), đặc tả đề 26, qua các bước thiết kế tĩnh (4.3.1), biểu đồ lớp, kịch bản chi tiết (4.3.2), cấu trúc CSDL đến mã nguồn thực tế và các kịch bản kiểm thử (Test IDs).

---

## 1. Bảng Ma trận Truy vết Toàn diện

| Mã YC | Vị trí DOCX | Bước / Ngoại lệ | Giao diện (JSP/UI) | HTTP / Route | Controller (Servlet.method) | Tầng Dữ liệu (DAO/Model.method) | Bảng & Cột CSDL liên quan | Test ID | Hình 4.3.1 | Mã nhánh cho 4.3.2 |
|---|---|---|---|---|---|---|---|---|---|---|
| **TC-01** | Mục 1.2.1.a / Kịch bản Tra cứu | Bước 1–2: Vào trang chủ, bấm tra cứu | `customerHome.jsp`, `catalogSearch.jsp` | GET `/customer/catalog` | `CatalogServlet4.showSearch` | Không gọi DAO (trả trạng thái ban đầu) | Không truy xuất | C01 | TK-TC-UI, TK-TC-CLASS | SD-TC-01 |
| **TC-02** | Mục 1.2.1.a / Kịch bản Tra cứu | Bước 3–4: Nhập từ khóa, tìm kiếm | `catalogSearch.jsp` | GET `/customer/catalog?keyword=...` | `CatalogServlet4.searchCatalog` | `ServiceDAO4.findServicesByName`, `SparePartDAO4.findSparePartsByName(keyword, true)` | `tblService4 (name, unitPrice)`, `tblSparePart4 (name, sellingPrice)` | C02, C03 | TK-TC-UI, TK-TC-CLASS | SD-TC-02 |
| **TC-03** | Mục 1.2.1.a / Kịch bản Tra cứu | Bước 5–6: Bấm xem chi tiết dịch vụ hoặc phụ tùng | `itemDetails.jsp` | GET `/customer/catalog/detail?id=...&itemType=...` | `CatalogServlet4.viewDetails` | `ServiceDAO4.getServiceDetails(id)`, `SparePartDAO4.getSparePartDetails(id, true)` | `tblService4`, `tblSparePart4`, `tblSparePartCategory4` | C04, C05 | TK-TC-UI, TK-TC-CLASS | SD-TC-03 |
| **TC-04** | Mục 1.2.1.a / Kịch bản Tra cứu | Ngoại lệ 3a (từ khóa rỗng), 4a (không thấy kết quả), quay lại | `catalogSearch.jsp`, `itemDetails.jsp` | GET `/customer/catalog?keyword=` | `CatalogServlet4.searchCatalog` | Trả thông báo lỗi hợp lệ hoặc danh sách rỗng, giữ từ khóa | `tblService4`, `tblSparePart4` | C06, C07 | TK-TC-UI, TK-TC-CLASS | SD-TC-04 |
| **NH-01** | Mục 1.2.1.b / Kịch bản Nhập | Bước 1–6: Chọn NCC, tạo phiên nhập | `warehouseHome.jsp`, `selectSupplier.jsp` | GET `/warehouse/suppliers`, POST `/warehouse/purchases/start` | `SupplierServlet4.searchSuppliers`, `PurchaseInvoiceServlet4.startPurchase` | `SupplierDAO4.findSuppliersByName`, `SupplierDAO4.getSupplierDetails`, `WarehouseStaffDAO4.getWarehouseStaffDetails`, `DraftStore4.createDraft` | `tblSupplier4 (id, name, address, phoneNumber)` | P01 | TK-NH-UI, TK-NH-CLASS | SD-NH-01 |
| **NH-02** | Mục 1.2.1.b / Kịch bản Nhập | Ngoại lệ 4a: Thêm nhà cung cấp mới | `newSupplier.jsp` | GET `/warehouse/suppliers/new`, POST `/warehouse/suppliers/create` | `SupplierServlet4.showNewSupplier`, `SupplierServlet4.addSupplier` | `SupplierDAO4.addSupplier(ncc)` | `tblSupplier4 (name, address, phoneNumber, email, status)` | P01 | TK-NH-UI, TK-NH-CLASS | SD-NH-02 |
| **NH-03** | Mục 1.2.1.b / Kịch bản Nhập | Bước 7–11: Tìm phụ tùng, thêm vào bảng, sửa lượng/giá | `purchaseInvoice.jsp` (parts-picker.jspf) | POST `/warehouse/purchases/search-part`, `add-line`, `update` | `PurchaseInvoiceServlet4.searchSpareParts`, `addInvoiceLine`, `updateInvoiceLines` | `SparePartDAO4.findSparePartsByName(keyword, false)`, `DraftStore4.saveDraft`, `PurchaseInvoice4.calculateTotalAmount` | `tblSparePart4 (id, name, unit, manufacturer, stockQuantity)` | P03, P04 | TK-NH-UI, TK-NH-CLASS | SD-NH-03 |
| **NH-04** | Mục 1.2.1.b / Kịch bản Nhập | Ngoại lệ 8a: Thêm phụ tùng mới chưa có trong kho | `newSparePart.jsp` | GET `/warehouse/parts/new`, POST `/warehouse/parts/create` | `SparePartServlet4.showNewSparePart`, `SparePartServlet4.addSparePart` | `SparePartCategoryDAO4.getActiveCategories`, `SparePartDAO4.addSparePart` | `tblSparePart4 (stockQuantity=0, status mặc định 'Chưa công bố')` | P02 | TK-NH-UI, TK-NH-CLASS | SD-NH-04 |
| **NH-05** | Mục 1.2.1.b / Kịch bản Nhập | Ngoại lệ 9a (chọn part đã có), 11a/12b (lượng/giá sai, nháp rỗng) | `purchaseInvoice.jsp` | POST `/warehouse/purchases/add-line`, `confirm` | `PurchaseInvoiceServlet4.addInvoiceLine`, `PurchaseInvoiceServlet4.confirmInvoice` | `Validation4.validateQuantity`, `Validation4.parseDecimal` | Không ghi DB nếu validate lỗi, giữ input thô | P04, P05 | TK-NH-UI, TK-NH-CLASS | SD-NH-05 |
| **NH-06** | Mục 1.2.1.b / Kịch bản Nhập | Bước 12–13, ngoại lệ 12c: Xác nhận hóa đơn, cộng dồn tồn kho | `purchaseInvoice.jsp` → redirect | POST `/warehouse/purchases/confirm` | `PurchaseInvoiceServlet4.confirmInvoice` | `PurchaseInvoiceDAO4.savePurchaseInvoice`, `SparePartDAO4.increaseStockAfterPurchase`, `DraftStore4.removeDraft` | `tblPurchaseInvoice4 (id, requestCode, confirmedAt)`, `tblPurchaseInvoiceDetail4`, `tblSparePart4 (stockQuantity)` | P06, P07, P08 | TK-NH-UI, TK-NH-CLASS | SD-NH-06 |
| **NH-07** | Mục 1.2.1.b / Kịch bản Nhập | Bước 14–17: Xem hóa đơn đã lưu và in chứng từ | `confirmedInvoice.jsp`, `printInvoice.jsp` | GET `/warehouse/purchases/view`, GET `/warehouse/purchases/print` | `PurchaseInvoiceServlet4.viewInvoice`, `PurchaseInvoiceServlet4.printInvoice` | `PurchaseInvoiceDAO4.getPurchaseInvoiceDetails(id)` | `tblPurchaseInvoice4`, `tblPurchaseInvoiceDetail4 (sparePartNameSnapshot, unitSnapshot)` | P11, P14 | TK-NH-UI, TK-NH-CLASS | SD-NH-07 |
| **NH-08** | Mục 1.2.1.b / Kịch bản Nhập | Bước 18–19, 18a: Người lập thanh toán đúng tổng tiền | `confirmedInvoice.jsp` | POST `/warehouse/purchases/pay` | `PurchaseInvoiceServlet4.recordPayment` | `PurchaseInvoiceDAO4.recordPayment(id, amount, userId)` | `tblPurchaseInvoice4 (paymentStatus, paidAt)` | P12, P13 | TK-NH-UI, TK-NH-CLASS | SD-NH-08 |
| **NH-09** | Mục 1.2.1.b / Kịch bản Nhập | Ngoại lệ 12a: Hủy lập hóa đơn trước khi xác nhận | `purchaseInvoice.jsp` | POST `/warehouse/purchases/cancel` | `PurchaseInvoiceServlet4.cancelDraft` | `DraftStore4.removeDraft(session, requestCode)` | Giữ nguyên dữ liệu NCC/part đã thêm, không phát sinh hóa đơn | P09 | TK-NH-UI, TK-NH-CLASS | SD-NH-09 |
| **NH-10** | Mục 1.2.1.b / Kịch bản Nhập | Ngoại lệ 18b: Mở lại danh sách chưa thanh toán | `warehouseHome.jsp`, `confirmedInvoice.jsp` | GET `/warehouse/purchases/unpaid` | `PurchaseInvoiceServlet4.showUnpaidInvoices` | `PurchaseInvoiceDAO4.getUnpaidInvoices(userId)` | `tblPurchaseInvoice4 (tblEmployee4id, paymentStatus)` | P13 | TK-NH-UI, TK-NH-CLASS | SD-NH-10 |
| **KT-01** | Bổ sung kỹ thuật (File 02, 03, 10) | Quyền hạn, bảo vệ CSRF, chống double submit, session fixation | `login.jsp`, toàn bộ form POST | Toàn bộ các route POST và Filter | `AuthFilter4.doFilter`, `EncodingFilter4.doFilter`, `WebUtil4`, `DraftStore4` | Quản lý Session, Token, Version draft | `tblMember4 (password hash PBKDF2)` | P10, P15, P16 | TK-NH-CLASS, TK-TC-CLASS | SD-KT-01 |

---

## 2. Danh mục Mã Test Case tương ứng

- **C01–C07 (Tra cứu danh mục)**:
  - `C01`: Mở trang tìm kiếm mặc định (chưa có từ khóa).
  - `C02`: Tìm từ khóa "lọc" trả về cả dịch vụ "Thay lọc dầu" và phụ tùng "Lọc dầu AF-01".
  - `C03`: Tìm từ khóa không dấu "loc" hoặc chữ hoa "LỌC" vẫn ra kết quả chính xác.
  - `C04`: Xem chi tiết dịch vụ: hiển thị mô tả, tiền công 120.000đ.
  - `C05`: Xem chi tiết phụ tùng: hiển thị thông số kỹ thuật, hãng AutoFilter, giá bán 150.000đ, không lộ giá nhập.
  - `C06`: Tìm kiếm với từ khóa rỗng / khoảng trắng báo lỗi hợp lệ.
  - `C07`: Tìm từ khóa không tồn tại ("xyz123") báo "Không tìm thấy kết quả phù hợp", nút quay lại giữ nguyên từ khóa.

- **P01–P16 (Nhập phụ tùng)**:
  - `P01`: Chọn nhà cung cấp có sẵn hoặc thêm mới bắt buộc tên, địa chỉ, số điện thoại.
  - `P02`: Thêm phụ tùng mới: gán nhóm đang dùng, tồn kho khởi tạo bằng 0, trạng thái Chưa công bố.
  - `P03`: Chọn phụ tùng vào hóa đơn, cập nhật số lượng 10, đơn giá 80.000đ -> thành tiền 800.000đ; giá bán danh mục không bị ảnh hưởng.
  - `P04`: Thêm nhiều dòng phụ tùng, tính đúng tổng tiền; chọn lại phụ tùng đã có thì focus dòng cũ, không thêm dòng trùng; đổi từ khóa tìm kiếm không mất các dòng đang nhập.
  - `P05`: Kiểm tra tính hợp lệ dữ liệu: chặn xác nhận nếu hóa đơn trống dòng, số lượng <= 0, đơn giá <= 0, số lượng lẻ cho đơn vị "Cái".
  - `P06`: Xử lý lỗi trong transaction: lỗi ghi 1 dòng bất kỳ thì rollback toàn bộ, không tạo hóa đơn dở dang, tồn kho không tăng.
  - `P07`: Xử lý submit trùng lặp đồng thời (cùng `requestCode` UUID): bảo đảm chỉ tạo 1 hóa đơn và tăng tồn 1 lần duy nhất (idempotent).
  - `P08`: Hai mã yêu cầu khác nhau nhập cùng một phụ tùng: lưu cả 2 hóa đơn hợp lệ, tồn kho tăng bằng tổng 2 lần nhập.
  - `P09`: Hủy lập hóa đơn trước khi xác nhận: xóa bản nháp, giữ nguyên nhà cung cấp và phụ tùng đã thêm mới, không ghi hóa đơn vào CSDL.
  - `P10`: Thao tác nhiều tab độc lập: mỗi tab quản lý bản nháp riêng qua `draftId`, không bị trộn lẫn dòng nhập giữa các tab.
  - `P11`: Xem và in hóa đơn: hiển thị đúng định dạng A4, in nhiều lần không làm thay đổi trạng thái thanh toán hoặc tồn kho.
  - `P12`: Người lập thanh toán hóa đơn: nhập đúng số tiền bằng tổng tiền, ghi nhận trạng thái ĐÃ THANH TOÁN và thời điểm thanh toán; tồn kho không đổi.
  - `P13`: Phân quyền thanh toán: nhân viên kho khác không thể thanh toán hóa đơn do người khác lập (HTTP 403 Forbidden); người lập đăng nhập lại thấy trong danh sách chưa thanh toán và thực hiện được.
  - `P14`: Bảo toàn snapshot lịch sử: sửa thông tin danh mục phụ tùng hoặc nhà cung cấp sau khi lập thì bản in hóa đơn cũ vẫn giữ nguyên tên và đơn vị áp dụng tại thời điểm lập.
  - `P15`: Chặn giả mạo dữ liệu: server bỏ qua các tham số tổng tiền, ngày lập, trạng thái do client cố tình gửi; kiểm tra CSRF token cho mọi form POST.
  - `P16`: Nhân viên lập ngừng làm việc: tài khoản bị khóa không đăng nhập/thanh toán được, nhưng hóa đơn lịch sử vẫn hiển thị đầy đủ tên người lập cho các nhân viên khác tra cứu.
