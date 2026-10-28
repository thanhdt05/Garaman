# Hướng dẫn vẽ hai sequence trong Visual Paradigm

Tạo đúng hai Sequence Diagram, mỗi modul một biểu đồ. Giữ nguyên các đường sống từ đầu đến cuối; kéo dài trang để chứa toàn bộ thông điệp, không chia thành các biểu đồ con. Các bảng dưới đây chép đúng thứ tự và số thông điệp của [scenario v3.0](SCENARIO_V3_VA_BIEU_DO_TUAN_TU.md).

Vẽ lời gọi bằng nét liền, kết quả trả về bằng nét đứt, tự gọi bằng mũi tên quay về chính đường sống. Các thanh kích hoạt lồng nhau biểu diễn hàm đang thực hiện và hàm được gọi, theo mẫu Hình 4.10 của bài giảng. Self-message mang tên phương thức sau thông điệp “gọi” thể hiện bắt đầu thực hiện phương thức ở lớp nhận, không biểu diễn việc Java gọi đệ quy phương thức đó. Constructor/đóng gói thực thể cũng được tách thành bước riêng như bài giảng. Tên hiển thị dùng đúng các tên trong bảng; với DAO/Servlet/thực thể có thể đặt dạng đối tượng `: ClassName` như hình đã xuất.

Thông điệp từ trang JSP đại diện yêu cầu trang qua trình duyệt. Các mốc showDraft/viewInvoice trên cùng đường sống Servlet biểu diễn bắt đầu xử lí yêu cầu mở giao diện tiếp theo; thanh của hàm trước đã kết thúc trước mốc đó. Việc chuyển hướng và phát sinh GET mới được lược, không diễn giải mũi tên này thành lời gọi Java trực tiếp giữa hai hàm. Hàm in JavaScript được gom thành một bước trên đường sống printInvoice.jsp. Giao chứng từ, nhận tiền và nhận hóa đơn bán hàng là tương tác thực tế giữa hai actor.

Mẫu bài giảng lược các bước đóng gói thuộc tính con. Khi DAO trả PurchaseInvoice4, các dòng nhập và thông tin nhà cung cấp/nhân viên đã được đóng gói bên trong. Các bước giữ, sao chép và xóa nháp, hàm private lưu đầu/dòng hóa đơn, getter và tính tiền khi render được gom trong bước nghiệp vụ tương ứng. Riêng lời gọi tăng tồn vẫn được giữ vì thể hiện tác động nhập kho. Lớp PurchaseInvoice4 tính tổng qua calculateTotalAmount(); JSP hiển thị kết quả, DAO đối chiếu tiền từ các dòng đã lưu khi ghi nhận thanh toán. Không vẽ thêm lời gọi tính tổng từ DAO hoặc Servlet.

## Biểu đồ tuần tự thiết kế modul tìm kiếm thông tin dịch vụ/phụ tùng

Thứ tự đường sống từ trái sang phải:

| STT | Tên trên hình | Loại |
|---|---|---|
| 1 | Khách hàng | Actor |
| 2 | customerHome.jsp | Boundary |
| 3 | catalogSearch.jsp | Boundary |
| 4 | itemDetails.jsp | Boundary |
| 5 | CatalogServlet4 | Control |
| 6 | ServiceDAO4 | Control |
| 7 | SparePartDAO4 | Control |
| 8 | Service4 | Entity |
| 9 | SparePart4 | Entity |

Thông điệp từ trên xuống dưới. Cột Thanh kích hoạt ghi thao tác ngay sau khi vẽ thông điệp:

| Bước | Từ | Đến | Loại thông điệp | Nhãn trên hình | Thanh kích hoạt |
|---|---|---|---|---|---|
| 1 | Khách hàng | customerHome.jsp | Gọi | 1: click tìm thông tin | giữ các thanh đang mở |
| 2 | customerHome.jsp | CatalogServlet4 | Gọi | 2: showSearch(request, response) | mở 1 thanh tại CatalogServlet4 |
| 3 | CatalogServlet4 | catalogSearch.jsp | Gọi | 3: gọi | mở 1 thanh tại catalogSearch.jsp; đóng 1 thanh tại CatalogServlet4 |
| 4 | catalogSearch.jsp | Khách hàng | Trả về | 4: hiển thị | đóng 1 thanh tại catalogSearch.jsp |
| 5 | Khách hàng | catalogSearch.jsp | Gọi | 5: nhập lọc; click Tìm kiếm | giữ các thanh đang mở |
| 6 | catalogSearch.jsp | CatalogServlet4 | Gọi | 6: searchCatalog(request, response) | mở 1 thanh tại CatalogServlet4 |
| 7 | CatalogServlet4 | ServiceDAO4 | Gọi | 7: gọi | mở 1 thanh tại ServiceDAO4 |
| 8 | ServiceDAO4 | ServiceDAO4 | Tự gọi | 8: findServicesByName("lọc") | mở 1 thanh tại ServiceDAO4 |
| 9 | ServiceDAO4 | Service4 | Gọi | 9: gọi | mở 1 thanh tại Service4 |
| 10 | Service4 | Service4 | Tự gọi | 10: Service4() | mở 1 thanh tại Service4; đóng thanh constructor sau khi đóng gói |
| 11 | Service4 | ServiceDAO4 | Trả về | 11: trả về | đóng 1 thanh tại Service4 |
| 12 | ServiceDAO4 | CatalogServlet4 | Trả về | 12: trả về | đóng 2 thanh tại ServiceDAO4 |
| 13 | CatalogServlet4 | SparePartDAO4 | Gọi | 13: gọi | mở 1 thanh tại SparePartDAO4 |
| 14 | SparePartDAO4 | SparePartDAO4 | Tự gọi | 14: findSparePartsByName("lọc", true) | mở 1 thanh tại SparePartDAO4 |
| 15 | SparePartDAO4 | SparePart4 | Gọi | 15: gọi | mở 1 thanh tại SparePart4 |
| 16 | SparePart4 | SparePart4 | Tự gọi | 16: SparePart4() | mở 1 thanh tại SparePart4; đóng thanh constructor sau khi đóng gói |
| 17 | SparePart4 | SparePartDAO4 | Trả về | 17: trả về | đóng 1 thanh tại SparePart4 |
| 18 | SparePartDAO4 | CatalogServlet4 | Trả về | 18: trả về | đóng 2 thanh tại SparePartDAO4 |
| 19 | CatalogServlet4 | catalogSearch.jsp | Gọi | 19: gọi | mở 1 thanh tại catalogSearch.jsp; đóng 1 thanh tại CatalogServlet4 |
| 20 | catalogSearch.jsp | Khách hàng | Trả về | 20: hiển thị | đóng 1 thanh tại catalogSearch.jsp |
| 21 | Khách hàng | catalogSearch.jsp | Gọi | 21: click chọn PT1 | giữ các thanh đang mở |
| 22 | catalogSearch.jsp | CatalogServlet4 | Gọi | 22: viewDetails(request, response) | mở 1 thanh tại CatalogServlet4 |
| 23 | CatalogServlet4 | SparePartDAO4 | Gọi | 23: gọi | mở 1 thanh tại SparePartDAO4 |
| 24 | SparePartDAO4 | SparePartDAO4 | Tự gọi | 24: getSparePartDetails(sparePartId, true) | mở 1 thanh tại SparePartDAO4 |
| 25 | SparePartDAO4 | SparePart4 | Gọi | 25: gọi | mở 1 thanh tại SparePart4 |
| 26 | SparePart4 | SparePart4 | Tự gọi | 26: SparePart4() | mở 1 thanh tại SparePart4; đóng thanh constructor sau khi đóng gói |
| 27 | SparePart4 | SparePartDAO4 | Trả về | 27: trả về | đóng 1 thanh tại SparePart4 |
| 28 | SparePartDAO4 | CatalogServlet4 | Trả về | 28: trả về | đóng 2 thanh tại SparePartDAO4 |
| 29 | CatalogServlet4 | itemDetails.jsp | Gọi | 29: gọi | mở 1 thanh tại itemDetails.jsp; đóng 1 thanh tại CatalogServlet4 |
| 30 | itemDetails.jsp | Khách hàng | Trả về | 30: hiển thị | đóng 1 thanh tại itemDetails.jsp |
| 31 | Khách hàng | itemDetails.jsp | Gọi | 31: click Quay lại | giữ các thanh đang mở |
| 32 | itemDetails.jsp | CatalogServlet4 | Gọi | 32: searchCatalog(request, response) | mở 1 thanh tại CatalogServlet4 |
| 33 | CatalogServlet4 | ServiceDAO4 | Gọi | 33: gọi | mở 1 thanh tại ServiceDAO4 |
| 34 | ServiceDAO4 | ServiceDAO4 | Tự gọi | 34: findServicesByName("lọc") | mở 1 thanh tại ServiceDAO4 |
| 35 | ServiceDAO4 | Service4 | Gọi | 35: gọi | mở 1 thanh tại Service4 |
| 36 | Service4 | Service4 | Tự gọi | 36: Service4() | mở 1 thanh tại Service4; đóng thanh constructor sau khi đóng gói |
| 37 | Service4 | ServiceDAO4 | Trả về | 37: trả về | đóng 1 thanh tại Service4 |
| 38 | ServiceDAO4 | CatalogServlet4 | Trả về | 38: trả về | đóng 2 thanh tại ServiceDAO4 |
| 39 | CatalogServlet4 | SparePartDAO4 | Gọi | 39: gọi | mở 1 thanh tại SparePartDAO4 |
| 40 | SparePartDAO4 | SparePartDAO4 | Tự gọi | 40: findSparePartsByName("lọc", true) | mở 1 thanh tại SparePartDAO4 |
| 41 | SparePartDAO4 | SparePart4 | Gọi | 41: gọi | mở 1 thanh tại SparePart4 |
| 42 | SparePart4 | SparePart4 | Tự gọi | 42: SparePart4() | mở 1 thanh tại SparePart4; đóng thanh constructor sau khi đóng gói |
| 43 | SparePart4 | SparePartDAO4 | Trả về | 43: trả về | đóng 1 thanh tại SparePart4 |
| 44 | SparePartDAO4 | CatalogServlet4 | Trả về | 44: trả về | đóng 2 thanh tại SparePartDAO4 |
| 45 | CatalogServlet4 | catalogSearch.jsp | Gọi | 45: gọi | mở 1 thanh tại catalogSearch.jsp; đóng 1 thanh tại CatalogServlet4 |
| 46 | catalogSearch.jsp | Khách hàng | Trả về | 46: hiển thị | đóng 1 thanh tại catalogSearch.jsp |

Các khung lặp trên cùng biểu đồ:

Luồng minh họa thực hiện một lượt tra cứu và xem PT1. Câu lặp lại ở cuối scenario cho biết cách tiếp tục; không vẽ thêm một sequence.

## Biểu đồ tuần tự thiết kế modul nhân viên kho nhập phụ tùng

Thứ tự đường sống từ trái sang phải:

| STT | Tên trên hình | Loại |
|---|---|---|
| 1 | Nhân viên kho | Actor |
| 2 | Nhà cung cấp | Actor |
| 3 | warehouseHome.jsp | Boundary |
| 4 | selectSupplier.jsp | Boundary |
| 5 | purchaseInvoice.jsp | Boundary |
| 6 | confirmedInvoice.jsp | Boundary |
| 7 | printInvoice.jsp | Boundary |
| 8 | SupplierServlet4 | Control |
| 9 | PurchaseInvoiceServlet4 | Control |
| 10 | SupplierDAO4 | Control |
| 11 | WarehouseStaffDAO4 | Control |
| 12 | SparePartDAO4 | Control |
| 13 | PurchaseInvoiceDAO4 | Control |
| 14 | Supplier4 | Entity |
| 15 | WarehouseStaff4 | Entity |
| 16 | SparePart4 | Entity |
| 17 | PurchaseInvoice4 | Entity |

Thông điệp từ trên xuống dưới. Cột Thanh kích hoạt ghi thao tác ngay sau khi vẽ thông điệp:

| Bước | Từ | Đến | Loại thông điệp | Nhãn trên hình | Thanh kích hoạt |
|---|---|---|---|---|---|
| 1 | Nhân viên kho | warehouseHome.jsp | Gọi | 1: click nhập phụ tùng | giữ các thanh đang mở |
| 2 | warehouseHome.jsp | SupplierServlet4 | Gọi | 2: showSupplierSelection(request, response) | mở 1 thanh tại SupplierServlet4 |
| 3 | SupplierServlet4 | selectSupplier.jsp | Gọi | 3: gọi | mở 1 thanh tại selectSupplier.jsp; đóng 1 thanh tại SupplierServlet4 |
| 4 | selectSupplier.jsp | Nhân viên kho | Trả về | 4: hiển thị | đóng 1 thanh tại selectSupplier.jsp |
| 5 | Nhân viên kho | selectSupplier.jsp | Gọi | 5: nhập Hòa Phát; click Tìm kiếm | giữ các thanh đang mở |
| 6 | selectSupplier.jsp | SupplierServlet4 | Gọi | 6: searchSuppliers(request, response) | mở 1 thanh tại SupplierServlet4 |
| 7 | SupplierServlet4 | SupplierDAO4 | Gọi | 7: gọi | mở 1 thanh tại SupplierDAO4 |
| 8 | SupplierDAO4 | SupplierDAO4 | Tự gọi | 8: findSuppliersByName("Hòa Phát") | mở 1 thanh tại SupplierDAO4 |
| 9 | SupplierDAO4 | Supplier4 | Gọi | 9: gọi | mở 1 thanh tại Supplier4 |
| 10 | Supplier4 | Supplier4 | Tự gọi | 10: Supplier4() | mở 1 thanh tại Supplier4; đóng thanh constructor sau khi đóng gói |
| 11 | Supplier4 | SupplierDAO4 | Trả về | 11: trả về | đóng 1 thanh tại Supplier4 |
| 12 | SupplierDAO4 | SupplierServlet4 | Trả về | 12: trả về | đóng 2 thanh tại SupplierDAO4 |
| 13 | SupplierServlet4 | selectSupplier.jsp | Gọi | 13: gọi | mở 1 thanh tại selectSupplier.jsp; đóng 1 thanh tại SupplierServlet4 |
| 14 | selectSupplier.jsp | Nhân viên kho | Trả về | 14: hiển thị | đóng 1 thanh tại selectSupplier.jsp |
| 15 | Nhân viên kho | selectSupplier.jsp | Gọi | 15: click chọn NCC1 | giữ các thanh đang mở |
| 16 | selectSupplier.jsp | PurchaseInvoiceServlet4 | Gọi | 16: startPurchase(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 17 | PurchaseInvoiceServlet4 | SupplierDAO4 | Gọi | 17: gọi | mở 1 thanh tại SupplierDAO4 |
| 18 | SupplierDAO4 | SupplierDAO4 | Tự gọi | 18: getSupplierDetails(supplierId) | mở 1 thanh tại SupplierDAO4 |
| 19 | SupplierDAO4 | Supplier4 | Gọi | 19: gọi | mở 1 thanh tại Supplier4 |
| 20 | Supplier4 | Supplier4 | Tự gọi | 20: Supplier4() | mở 1 thanh tại Supplier4; đóng thanh constructor sau khi đóng gói |
| 21 | Supplier4 | SupplierDAO4 | Trả về | 21: trả về | đóng 1 thanh tại Supplier4 |
| 22 | SupplierDAO4 | PurchaseInvoiceServlet4 | Trả về | 22: trả về | đóng 2 thanh tại SupplierDAO4 |
| 23 | PurchaseInvoiceServlet4 | WarehouseStaffDAO4 | Gọi | 23: gọi | mở 1 thanh tại WarehouseStaffDAO4 |
| 24 | WarehouseStaffDAO4 | WarehouseStaffDAO4 | Tự gọi | 24: getWarehouseStaffDetails(warehouseStaffId) | mở 1 thanh tại WarehouseStaffDAO4 |
| 25 | WarehouseStaffDAO4 | WarehouseStaff4 | Gọi | 25: gọi | mở 1 thanh tại WarehouseStaff4 |
| 26 | WarehouseStaff4 | WarehouseStaff4 | Tự gọi | 26: WarehouseStaff4() | mở 1 thanh tại WarehouseStaff4; đóng thanh constructor sau khi đóng gói |
| 27 | WarehouseStaff4 | WarehouseStaffDAO4 | Trả về | 27: trả về | đóng 1 thanh tại WarehouseStaff4 |
| 28 | WarehouseStaffDAO4 | PurchaseInvoiceServlet4 | Trả về | 28: trả về | đóng 2 thanh tại WarehouseStaffDAO4 |
| 29 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 29: showDraft(request, response) | đóng thanh hàm trước tại PurchaseInvoiceServlet4 trước thông điệp này; mở 1 thanh tại PurchaseInvoiceServlet4 |
| 30 | PurchaseInvoiceServlet4 | purchaseInvoice.jsp | Gọi | 30: gọi | mở 1 thanh tại purchaseInvoice.jsp; đóng 1 thanh tại PurchaseInvoiceServlet4 |
| 31 | purchaseInvoice.jsp | Nhân viên kho | Trả về | 31: hiển thị | đóng 1 thanh tại purchaseInvoice.jsp |
| 32 | Nhân viên kho | purchaseInvoice.jsp | Gọi | 32: nhập Lọc; click Tìm kiếm | giữ các thanh đang mở |
| 33 | purchaseInvoice.jsp | PurchaseInvoiceServlet4 | Gọi | 33: searchSpareParts(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 34 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 34: showDraft(request, response) | đóng thanh hàm trước tại PurchaseInvoiceServlet4 trước thông điệp này; mở 1 thanh tại PurchaseInvoiceServlet4 |
| 35 | PurchaseInvoiceServlet4 | SparePartDAO4 | Gọi | 35: gọi | mở 1 thanh tại SparePartDAO4 |
| 36 | SparePartDAO4 | SparePartDAO4 | Tự gọi | 36: findSparePartsByName("Lọc", false) | mở 1 thanh tại SparePartDAO4 |
| 37 | SparePartDAO4 | SparePart4 | Gọi | 37: gọi | mở 1 thanh tại SparePart4 |
| 38 | SparePart4 | SparePart4 | Tự gọi | 38: SparePart4() | mở 1 thanh tại SparePart4; đóng thanh constructor sau khi đóng gói |
| 39 | SparePart4 | SparePartDAO4 | Trả về | 39: trả về | đóng 1 thanh tại SparePart4 |
| 40 | SparePartDAO4 | PurchaseInvoiceServlet4 | Trả về | 40: trả về | đóng 2 thanh tại SparePartDAO4 |
| 41 | PurchaseInvoiceServlet4 | purchaseInvoice.jsp | Gọi | 41: gọi | mở 1 thanh tại purchaseInvoice.jsp; đóng 1 thanh tại PurchaseInvoiceServlet4 |
| 42 | purchaseInvoice.jsp | Nhân viên kho | Trả về | 42: hiển thị | đóng 1 thanh tại purchaseInvoice.jsp |
| 43 | Nhân viên kho | purchaseInvoice.jsp | Gọi | 43: click chọn PT1 | giữ các thanh đang mở |
| 44 | purchaseInvoice.jsp | PurchaseInvoiceServlet4 | Gọi | 44: addInvoiceLine(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 45 | PurchaseInvoiceServlet4 | SparePartDAO4 | Gọi | 45: gọi | mở 1 thanh tại SparePartDAO4 |
| 46 | SparePartDAO4 | SparePartDAO4 | Tự gọi | 46: getSparePartDetails(sparePartId, false) | mở 1 thanh tại SparePartDAO4 |
| 47 | SparePartDAO4 | SparePart4 | Gọi | 47: gọi | mở 1 thanh tại SparePart4 |
| 48 | SparePart4 | SparePart4 | Tự gọi | 48: SparePart4() | mở 1 thanh tại SparePart4; đóng thanh constructor sau khi đóng gói |
| 49 | SparePart4 | SparePartDAO4 | Trả về | 49: trả về | đóng 1 thanh tại SparePart4 |
| 50 | SparePartDAO4 | PurchaseInvoiceServlet4 | Trả về | 50: trả về | đóng 2 thanh tại SparePartDAO4 |
| 51 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 51: showDraft(request, response) | đóng thanh hàm trước tại PurchaseInvoiceServlet4 trước thông điệp này; mở 1 thanh tại PurchaseInvoiceServlet4 |
| 52 | PurchaseInvoiceServlet4 | purchaseInvoice.jsp | Gọi | 52: gọi | mở 1 thanh tại purchaseInvoice.jsp; đóng 1 thanh tại PurchaseInvoiceServlet4 |
| 53 | purchaseInvoice.jsp | Nhân viên kho | Trả về | 53: hiển thị | đóng 1 thanh tại purchaseInvoice.jsp |
| 54 | Nhân viên kho | purchaseInvoice.jsp | Gọi | 54: nhập 10 cái; 80.000 đồng/cái | giữ các thanh đang mở |
| 55 | purchaseInvoice.jsp | Nhân viên kho | Trả về | 55: hiển thị | giữ các thanh đang mở |
| 56 | Nhân viên kho | purchaseInvoice.jsp | Gọi | 56: click Xác nhận | giữ các thanh đang mở |
| 57 | purchaseInvoice.jsp | PurchaseInvoiceServlet4 | Gọi | 57: confirmInvoice(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 58 | PurchaseInvoiceServlet4 | PurchaseInvoiceDAO4 | Gọi | 58: gọi | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 59 | PurchaseInvoiceDAO4 | PurchaseInvoiceDAO4 | Tự gọi | 59: savePurchaseInvoice(purchaseInvoice) | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 60 | PurchaseInvoiceDAO4 | SparePartDAO4 | Gọi | 60: gọi | mở 1 thanh tại SparePartDAO4 |
| 61 | SparePartDAO4 | SparePartDAO4 | Tự gọi | 61: increaseStockAfterPurchase(connection, sparePartId, quantity) | mở 1 thanh tại SparePartDAO4 |
| 62 | SparePartDAO4 | PurchaseInvoiceDAO4 | Trả về | 62: trả về | đóng 2 thanh tại SparePartDAO4 |
| 63 | PurchaseInvoiceDAO4 | PurchaseInvoiceServlet4 | Trả về | 63: trả về | đóng 2 thanh tại PurchaseInvoiceDAO4 |
| 64 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 64: viewInvoice(request, response) | đóng thanh hàm trước tại PurchaseInvoiceServlet4 trước thông điệp này; mở 1 thanh tại PurchaseInvoiceServlet4 |
| 65 | PurchaseInvoiceServlet4 | PurchaseInvoiceDAO4 | Gọi | 65: gọi | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 66 | PurchaseInvoiceDAO4 | PurchaseInvoiceDAO4 | Tự gọi | 66: getPurchaseInvoiceDetails(invoiceId) | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 67 | PurchaseInvoiceDAO4 | PurchaseInvoice4 | Gọi | 67: gọi | mở 1 thanh tại PurchaseInvoice4 |
| 68 | PurchaseInvoice4 | PurchaseInvoice4 | Tự gọi | 68: PurchaseInvoice4() | mở 1 thanh tại PurchaseInvoice4; đóng thanh constructor sau khi đóng gói |
| 69 | PurchaseInvoice4 | PurchaseInvoiceDAO4 | Trả về | 69: trả về | đóng 1 thanh tại PurchaseInvoice4 |
| 70 | PurchaseInvoiceDAO4 | PurchaseInvoiceServlet4 | Trả về | 70: trả về | đóng 2 thanh tại PurchaseInvoiceDAO4 |
| 71 | PurchaseInvoiceServlet4 | confirmedInvoice.jsp | Gọi | 71: gọi | mở 1 thanh tại confirmedInvoice.jsp; đóng 1 thanh tại PurchaseInvoiceServlet4 |
| 72 | confirmedInvoice.jsp | Nhân viên kho | Trả về | 72: hiển thị | đóng 1 thanh tại confirmedInvoice.jsp |
| 73 | Nhân viên kho | confirmedInvoice.jsp | Gọi | 73: click In hóa đơn | giữ các thanh đang mở |
| 74 | confirmedInvoice.jsp | PurchaseInvoiceServlet4 | Gọi | 74: printInvoice(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 75 | PurchaseInvoiceServlet4 | PurchaseInvoiceDAO4 | Gọi | 75: gọi | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 76 | PurchaseInvoiceDAO4 | PurchaseInvoiceDAO4 | Tự gọi | 76: getPurchaseInvoiceDetails(invoiceId) | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 77 | PurchaseInvoiceDAO4 | PurchaseInvoice4 | Gọi | 77: gọi | mở 1 thanh tại PurchaseInvoice4 |
| 78 | PurchaseInvoice4 | PurchaseInvoice4 | Tự gọi | 78: PurchaseInvoice4() | mở 1 thanh tại PurchaseInvoice4; đóng thanh constructor sau khi đóng gói |
| 79 | PurchaseInvoice4 | PurchaseInvoiceDAO4 | Trả về | 79: trả về | đóng 1 thanh tại PurchaseInvoice4 |
| 80 | PurchaseInvoiceDAO4 | PurchaseInvoiceServlet4 | Trả về | 80: trả về | đóng 2 thanh tại PurchaseInvoiceDAO4 |
| 81 | PurchaseInvoiceServlet4 | printInvoice.jsp | Gọi | 81: gọi | mở 1 thanh tại printInvoice.jsp; đóng 1 thanh tại PurchaseInvoiceServlet4 |
| 82 | printInvoice.jsp | Nhân viên kho | Trả về | 82: hiển thị | đóng 1 thanh tại printInvoice.jsp |
| 83 | Nhân viên kho | printInvoice.jsp | Gọi | 83: click In | giữ các thanh đang mở |
| 84 | printInvoice.jsp | printInvoice.jsp | Tự gọi | 84: printInvoice() | mở 1 thanh tại printInvoice.jsp; đóng thanh xử lí in khi hộp thoại đóng |
| 85 | printInvoice.jsp | PurchaseInvoiceServlet4 | Gọi | 85: viewInvoice(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 86 | PurchaseInvoiceServlet4 | PurchaseInvoiceDAO4 | Gọi | 86: gọi | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 87 | PurchaseInvoiceDAO4 | PurchaseInvoiceDAO4 | Tự gọi | 87: getPurchaseInvoiceDetails(invoiceId) | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 88 | PurchaseInvoiceDAO4 | PurchaseInvoice4 | Gọi | 88: gọi | mở 1 thanh tại PurchaseInvoice4 |
| 89 | PurchaseInvoice4 | PurchaseInvoice4 | Tự gọi | 89: PurchaseInvoice4() | mở 1 thanh tại PurchaseInvoice4; đóng thanh constructor sau khi đóng gói |
| 90 | PurchaseInvoice4 | PurchaseInvoiceDAO4 | Trả về | 90: trả về | đóng 1 thanh tại PurchaseInvoice4 |
| 91 | PurchaseInvoiceDAO4 | PurchaseInvoiceServlet4 | Trả về | 91: trả về | đóng 2 thanh tại PurchaseInvoiceDAO4 |
| 92 | PurchaseInvoiceServlet4 | confirmedInvoice.jsp | Gọi | 92: gọi | mở 1 thanh tại confirmedInvoice.jsp; đóng 1 thanh tại PurchaseInvoiceServlet4 |
| 93 | confirmedInvoice.jsp | Nhân viên kho | Trả về | 93: hiển thị | đóng 1 thanh tại confirmedInvoice.jsp |
| 94 | Nhân viên kho | Nhà cung cấp | Gọi | 94: giao chứng từ; trả 800.000 đồng | giữ các thanh đang mở |
| 95 | Nhà cung cấp | Nhà cung cấp | Tự gọi | 95: nhận chứng từ và tiền | giữ các thanh đang mở |
| 96 | Nhà cung cấp | Nhân viên kho | Gọi | 96: giao hóa đơn bán hàng | giữ các thanh đang mở |
| 97 | Nhân viên kho | Nhân viên kho | Tự gọi | 97: tiếp nhận hóa đơn bán hàng | giữ các thanh đang mở |
| 98 | Nhân viên kho | confirmedInvoice.jsp | Gọi | 98: nhập 800.000; click Đã thanh toán | giữ các thanh đang mở |
| 99 | confirmedInvoice.jsp | PurchaseInvoiceServlet4 | Gọi | 99: recordPayment(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 100 | PurchaseInvoiceServlet4 | PurchaseInvoiceDAO4 | Gọi | 100: gọi | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 101 | PurchaseInvoiceDAO4 | PurchaseInvoiceDAO4 | Tự gọi | 101: recordPayment(invoiceId, amountPaid, currentWarehouseStaffId) | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 102 | PurchaseInvoiceDAO4 | PurchaseInvoiceServlet4 | Trả về | 102: trả về | đóng 2 thanh tại PurchaseInvoiceDAO4 |
| 103 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 103: viewInvoice(request, response) | đóng thanh hàm trước tại PurchaseInvoiceServlet4 trước thông điệp này; mở 1 thanh tại PurchaseInvoiceServlet4 |
| 104 | PurchaseInvoiceServlet4 | PurchaseInvoiceDAO4 | Gọi | 104: gọi | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 105 | PurchaseInvoiceDAO4 | PurchaseInvoiceDAO4 | Tự gọi | 105: getPurchaseInvoiceDetails(invoiceId) | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 106 | PurchaseInvoiceDAO4 | PurchaseInvoice4 | Gọi | 106: gọi | mở 1 thanh tại PurchaseInvoice4 |
| 107 | PurchaseInvoice4 | PurchaseInvoice4 | Tự gọi | 107: PurchaseInvoice4() | mở 1 thanh tại PurchaseInvoice4; đóng thanh constructor sau khi đóng gói |
| 108 | PurchaseInvoice4 | PurchaseInvoiceDAO4 | Trả về | 108: trả về | đóng 1 thanh tại PurchaseInvoice4 |
| 109 | PurchaseInvoiceDAO4 | PurchaseInvoiceServlet4 | Trả về | 109: trả về | đóng 2 thanh tại PurchaseInvoiceDAO4 |
| 110 | PurchaseInvoiceServlet4 | confirmedInvoice.jsp | Gọi | 110: gọi | mở 1 thanh tại confirmedInvoice.jsp; đóng 1 thanh tại PurchaseInvoiceServlet4 |
| 111 | confirmedInvoice.jsp | Nhân viên kho | Trả về | 111: thông báo thành công | đóng 1 thanh tại confirmedInvoice.jsp |

Các khung lặp trên cùng biểu đồ:

- Bao các thông điệp **32–55** trong khung `loop [lặp đến khi đủ phụ tùng cần nhập]`.
- Bao các thông điệp **60–62** trong khung `loop [với từng phụ tùng trong hóa đơn nhập]`.
