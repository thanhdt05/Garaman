# Hướng dẫn vẽ hai sequence trong Visual Paradigm

Tạo đúng hai Sequence Diagram, mỗi modul một biểu đồ. Giữ nguyên các đường sống từ đầu đến cuối; kéo dài trang để chứa toàn bộ thông điệp, không chia thành các biểu đồ con. Các bảng dưới đây chép đúng thứ tự và số thông điệp của [scenario v3.0](SCENARIO_V3_VA_BIEU_DO_TUAN_TU.md).

Vẽ lời gọi bằng nét liền, kết quả trả về bằng nét đứt, tự gọi bằng mũi tên quay về chính đường sống. Các thanh kích hoạt lồng nhau biểu diễn hàm đang thực hiện và hàm được gọi, theo mẫu Hình 4.10 của bài giảng. Self-message mang tên phương thức sau thông điệp “gọi” thể hiện bắt đầu thực hiện phương thức ở lớp nhận, không biểu diễn việc Java gọi đệ quy phương thức đó. Constructor/đóng gói thực thể cũng được tách thành bước riêng như bài giảng. Tên hiển thị dùng đúng các tên trong bảng; với DAO/Servlet/thực thể có thể đặt dạng đối tượng `: ClassName` như hình đã xuất.

Mỗi yêu cầu từ JSP đến Servlet được tách thành hai thông điệp: JSP → Servlet ghi “gọi”, sau đó Servlet → chính nó ghi tên hàm xử lí đã thiết kế, ví dụ showSearch(request, response). Kịch bản tương ứng ghi riêng “Trang … gọi lớp …” và “Lớp … gọi hàm …”. Servlet trả kết quả về chính JSP đã gửi yêu cầu bằng nét đứt, nhãn “trả về”; chuyển dữ liệu để mở JSP khác dùng nét liền, nhãn “gọi”. Ví dụ kết quả tìm được trả về catalogSearch.jsp, còn mở chi tiết là gọi itemDetails.jsp. Mỗi lần hiển thị chỉ có một thông điệp dữ liệu đến JSP, sau đó JSP hiển thị cho actor.

Thông điệp từ trang JSP đại diện yêu cầu trang qua trình duyệt. Các mốc showDraft/viewInvoice trên cùng đường sống Servlet biểu diễn bắt đầu xử lí yêu cầu mở giao diện tiếp theo; thanh của hàm trước đã kết thúc trước mốc đó. Việc chuyển hướng và phát sinh GET mới được lược, không diễn giải mũi tên này thành lời gọi Java trực tiếp giữa hai hàm. Việc mở hộp thoại in của trình duyệt được gom trong thao tác click In của nhân viên; không vẽ self-call printInvoice() trên đường sống printInvoice.jsp vì lớp giao diện này không khai báo phương thức đó. Hàm printInvoice(request, response) của Servlet vẫn được giữ để chuẩn bị dữ liệu trang in.

Modul nhập phụ tùng chỉ có actor Nhân viên kho. Nhà cung cấp không sử dụng phần mềm và không có tài khoản; thông tin do nhân viên kho nhập và quản lí. Không vẽ actor Supplier hoặc thông điệp giao hàng, giao chứng từ và tiền giữa hai actor. Việc đối chiếu, thực tế thanh toán và tiếp nhận hóa đơn bán hàng được ghi trong câu mô tả thao tác của nhân viên trước khi ghi nhận thanh toán trên phần mềm. Giữ các lớp SupplierServlet4, SupplierDAO4 và Supplier4 để xử lí thông tin nhà cung cấp.

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
| 2 | customerHome.jsp | CatalogServlet4 | Gọi | 2: gọi | mở 1 thanh tại CatalogServlet4 |
| 3 | CatalogServlet4 | CatalogServlet4 | Tự gọi | 3: showSearch(request, response) | mở 1 thanh tại CatalogServlet4 |
| 4 | CatalogServlet4 | catalogSearch.jsp | Gọi | 4: gọi | mở 1 thanh tại catalogSearch.jsp; đóng 2 thanh tại CatalogServlet4 |
| 5 | catalogSearch.jsp | Khách hàng | Trả về | 5: hiển thị | đóng 1 thanh tại catalogSearch.jsp |
| 6 | Khách hàng | catalogSearch.jsp | Gọi | 6: nhập lọc; click Tìm kiếm | giữ các thanh đang mở |
| 7 | catalogSearch.jsp | CatalogServlet4 | Gọi | 7: gọi | mở 1 thanh tại CatalogServlet4 |
| 8 | CatalogServlet4 | CatalogServlet4 | Tự gọi | 8: searchCatalog(request, response) | mở 1 thanh tại CatalogServlet4 |
| 9 | CatalogServlet4 | ServiceDAO4 | Gọi | 9: gọi | mở 1 thanh tại ServiceDAO4 |
| 10 | ServiceDAO4 | ServiceDAO4 | Tự gọi | 10: findServicesByName("lọc") | mở 1 thanh tại ServiceDAO4 |
| 11 | ServiceDAO4 | Service4 | Gọi | 11: gọi | mở 1 thanh tại Service4 |
| 12 | Service4 | Service4 | Tự gọi | 12: Service4() | mở 1 thanh tại Service4; đóng thanh constructor sau khi đóng gói |
| 13 | Service4 | ServiceDAO4 | Trả về | 13: trả về | đóng 1 thanh tại Service4 |
| 14 | ServiceDAO4 | CatalogServlet4 | Trả về | 14: trả về | đóng 2 thanh tại ServiceDAO4 |
| 15 | CatalogServlet4 | SparePartDAO4 | Gọi | 15: gọi | mở 1 thanh tại SparePartDAO4 |
| 16 | SparePartDAO4 | SparePartDAO4 | Tự gọi | 16: findSparePartsByName("lọc", true) | mở 1 thanh tại SparePartDAO4 |
| 17 | SparePartDAO4 | SparePart4 | Gọi | 17: gọi | mở 1 thanh tại SparePart4 |
| 18 | SparePart4 | SparePart4 | Tự gọi | 18: SparePart4() | mở 1 thanh tại SparePart4; đóng thanh constructor sau khi đóng gói |
| 19 | SparePart4 | SparePartDAO4 | Trả về | 19: trả về | đóng 1 thanh tại SparePart4 |
| 20 | SparePartDAO4 | CatalogServlet4 | Trả về | 20: trả về | đóng 2 thanh tại SparePartDAO4 |
| 21 | CatalogServlet4 | catalogSearch.jsp | Trả về | 21: trả về | mở 1 thanh tại catalogSearch.jsp; đóng 2 thanh tại CatalogServlet4 |
| 22 | catalogSearch.jsp | Khách hàng | Trả về | 22: hiển thị | đóng 1 thanh tại catalogSearch.jsp |
| 23 | Khách hàng | catalogSearch.jsp | Gọi | 23: click chọn PT1 | giữ các thanh đang mở |
| 24 | catalogSearch.jsp | CatalogServlet4 | Gọi | 24: gọi | mở 1 thanh tại CatalogServlet4 |
| 25 | CatalogServlet4 | CatalogServlet4 | Tự gọi | 25: viewDetails(request, response) | mở 1 thanh tại CatalogServlet4 |
| 26 | CatalogServlet4 | SparePartDAO4 | Gọi | 26: gọi | mở 1 thanh tại SparePartDAO4 |
| 27 | SparePartDAO4 | SparePartDAO4 | Tự gọi | 27: getSparePartDetails(sparePartId, true) | mở 1 thanh tại SparePartDAO4 |
| 28 | SparePartDAO4 | SparePart4 | Gọi | 28: gọi | mở 1 thanh tại SparePart4 |
| 29 | SparePart4 | SparePart4 | Tự gọi | 29: SparePart4() | mở 1 thanh tại SparePart4; đóng thanh constructor sau khi đóng gói |
| 30 | SparePart4 | SparePartDAO4 | Trả về | 30: trả về | đóng 1 thanh tại SparePart4 |
| 31 | SparePartDAO4 | CatalogServlet4 | Trả về | 31: trả về | đóng 2 thanh tại SparePartDAO4 |
| 32 | CatalogServlet4 | itemDetails.jsp | Gọi | 32: gọi | mở 1 thanh tại itemDetails.jsp; đóng 2 thanh tại CatalogServlet4 |
| 33 | itemDetails.jsp | Khách hàng | Trả về | 33: hiển thị | đóng 1 thanh tại itemDetails.jsp |

Các khung lặp trên cùng biểu đồ:

Kịch bản thành công kết thúc ở hiển thị chi tiết PT1. Không có khung lặp hoặc thao tác click Quay lại trong sequence này.

## Biểu đồ tuần tự thiết kế modul nhân viên kho nhập phụ tùng

Thứ tự đường sống từ trái sang phải:

| STT | Tên trên hình | Loại |
|---|---|---|
| 1 | Nhân viên kho | Actor |
| 2 | warehouseHome.jsp | Boundary |
| 3 | selectSupplier.jsp | Boundary |
| 4 | purchaseInvoice.jsp | Boundary |
| 5 | confirmedInvoice.jsp | Boundary |
| 6 | printInvoice.jsp | Boundary |
| 7 | SupplierServlet4 | Control |
| 8 | PurchaseInvoiceServlet4 | Control |
| 9 | SupplierDAO4 | Control |
| 10 | WarehouseStaffDAO4 | Control |
| 11 | SparePartDAO4 | Control |
| 12 | PurchaseInvoiceDAO4 | Control |
| 13 | Supplier4 | Entity |
| 14 | WarehouseStaff4 | Entity |
| 15 | SparePart4 | Entity |
| 16 | PurchaseInvoice4 | Entity |

Thông điệp từ trên xuống dưới. Cột Thanh kích hoạt ghi thao tác ngay sau khi vẽ thông điệp:

| Bước | Từ | Đến | Loại thông điệp | Nhãn trên hình | Thanh kích hoạt |
|---|---|---|---|---|---|
| 1 | Nhân viên kho | warehouseHome.jsp | Gọi | 1: click nhập phụ tùng | giữ các thanh đang mở |
| 2 | warehouseHome.jsp | SupplierServlet4 | Gọi | 2: gọi | mở 1 thanh tại SupplierServlet4 |
| 3 | SupplierServlet4 | SupplierServlet4 | Tự gọi | 3: showSupplierSelection(request, response) | mở 1 thanh tại SupplierServlet4 |
| 4 | SupplierServlet4 | selectSupplier.jsp | Gọi | 4: gọi | mở 1 thanh tại selectSupplier.jsp; đóng 2 thanh tại SupplierServlet4 |
| 5 | selectSupplier.jsp | Nhân viên kho | Trả về | 5: hiển thị | đóng 1 thanh tại selectSupplier.jsp |
| 6 | Nhân viên kho | selectSupplier.jsp | Gọi | 6: nhập Hòa Phát; click Tìm kiếm | giữ các thanh đang mở |
| 7 | selectSupplier.jsp | SupplierServlet4 | Gọi | 7: gọi | mở 1 thanh tại SupplierServlet4 |
| 8 | SupplierServlet4 | SupplierServlet4 | Tự gọi | 8: searchSuppliers(request, response) | mở 1 thanh tại SupplierServlet4 |
| 9 | SupplierServlet4 | SupplierDAO4 | Gọi | 9: gọi | mở 1 thanh tại SupplierDAO4 |
| 10 | SupplierDAO4 | SupplierDAO4 | Tự gọi | 10: findSuppliersByName("Hòa Phát") | mở 1 thanh tại SupplierDAO4 |
| 11 | SupplierDAO4 | Supplier4 | Gọi | 11: gọi | mở 1 thanh tại Supplier4 |
| 12 | Supplier4 | Supplier4 | Tự gọi | 12: Supplier4() | mở 1 thanh tại Supplier4; đóng thanh constructor sau khi đóng gói |
| 13 | Supplier4 | SupplierDAO4 | Trả về | 13: trả về | đóng 1 thanh tại Supplier4 |
| 14 | SupplierDAO4 | SupplierServlet4 | Trả về | 14: trả về | đóng 2 thanh tại SupplierDAO4 |
| 15 | SupplierServlet4 | selectSupplier.jsp | Trả về | 15: trả về | mở 1 thanh tại selectSupplier.jsp; đóng 2 thanh tại SupplierServlet4 |
| 16 | selectSupplier.jsp | Nhân viên kho | Trả về | 16: hiển thị | đóng 1 thanh tại selectSupplier.jsp |
| 17 | Nhân viên kho | selectSupplier.jsp | Gọi | 17: click chọn NCC1 | giữ các thanh đang mở |
| 18 | selectSupplier.jsp | PurchaseInvoiceServlet4 | Gọi | 18: gọi | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 19 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 19: startPurchase(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 20 | PurchaseInvoiceServlet4 | SupplierDAO4 | Gọi | 20: gọi | mở 1 thanh tại SupplierDAO4 |
| 21 | SupplierDAO4 | SupplierDAO4 | Tự gọi | 21: getSupplierDetails(supplierId) | mở 1 thanh tại SupplierDAO4 |
| 22 | SupplierDAO4 | Supplier4 | Gọi | 22: gọi | mở 1 thanh tại Supplier4 |
| 23 | Supplier4 | Supplier4 | Tự gọi | 23: Supplier4() | mở 1 thanh tại Supplier4; đóng thanh constructor sau khi đóng gói |
| 24 | Supplier4 | SupplierDAO4 | Trả về | 24: trả về | đóng 1 thanh tại Supplier4 |
| 25 | SupplierDAO4 | PurchaseInvoiceServlet4 | Trả về | 25: trả về | đóng 2 thanh tại SupplierDAO4 |
| 26 | PurchaseInvoiceServlet4 | WarehouseStaffDAO4 | Gọi | 26: gọi | mở 1 thanh tại WarehouseStaffDAO4 |
| 27 | WarehouseStaffDAO4 | WarehouseStaffDAO4 | Tự gọi | 27: getWarehouseStaffDetails(warehouseStaffId) | mở 1 thanh tại WarehouseStaffDAO4 |
| 28 | WarehouseStaffDAO4 | WarehouseStaff4 | Gọi | 28: gọi | mở 1 thanh tại WarehouseStaff4 |
| 29 | WarehouseStaff4 | WarehouseStaff4 | Tự gọi | 29: WarehouseStaff4() | mở 1 thanh tại WarehouseStaff4; đóng thanh constructor sau khi đóng gói |
| 30 | WarehouseStaff4 | WarehouseStaffDAO4 | Trả về | 30: trả về | đóng 1 thanh tại WarehouseStaff4 |
| 31 | WarehouseStaffDAO4 | PurchaseInvoiceServlet4 | Trả về | 31: trả về | đóng 2 thanh tại WarehouseStaffDAO4 |
| 32 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 32: showDraft(request, response) | đóng 2 thanh xử lí trước tại PurchaseInvoiceServlet4 trước thông điệp này; mở 1 thanh tại PurchaseInvoiceServlet4 |
| 33 | PurchaseInvoiceServlet4 | purchaseInvoice.jsp | Gọi | 33: gọi | mở 1 thanh tại purchaseInvoice.jsp; đóng 1 thanh tại PurchaseInvoiceServlet4 |
| 34 | purchaseInvoice.jsp | Nhân viên kho | Trả về | 34: hiển thị | đóng 1 thanh tại purchaseInvoice.jsp |
| 35 | Nhân viên kho | purchaseInvoice.jsp | Gọi | 35: nhập Lọc; click Tìm kiếm | giữ các thanh đang mở |
| 36 | purchaseInvoice.jsp | PurchaseInvoiceServlet4 | Gọi | 36: gọi | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 37 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 37: searchSpareParts(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 38 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 38: showDraft(request, response) | đóng 2 thanh xử lí trước tại PurchaseInvoiceServlet4 trước thông điệp này; mở 1 thanh tại PurchaseInvoiceServlet4 |
| 39 | PurchaseInvoiceServlet4 | SparePartDAO4 | Gọi | 39: gọi | mở 1 thanh tại SparePartDAO4 |
| 40 | SparePartDAO4 | SparePartDAO4 | Tự gọi | 40: findSparePartsByName("Lọc", false) | mở 1 thanh tại SparePartDAO4 |
| 41 | SparePartDAO4 | SparePart4 | Gọi | 41: gọi | mở 1 thanh tại SparePart4 |
| 42 | SparePart4 | SparePart4 | Tự gọi | 42: SparePart4() | mở 1 thanh tại SparePart4; đóng thanh constructor sau khi đóng gói |
| 43 | SparePart4 | SparePartDAO4 | Trả về | 43: trả về | đóng 1 thanh tại SparePart4 |
| 44 | SparePartDAO4 | PurchaseInvoiceServlet4 | Trả về | 44: trả về | đóng 2 thanh tại SparePartDAO4 |
| 45 | PurchaseInvoiceServlet4 | purchaseInvoice.jsp | Trả về | 45: trả về | mở 1 thanh tại purchaseInvoice.jsp; đóng 1 thanh tại PurchaseInvoiceServlet4 |
| 46 | purchaseInvoice.jsp | Nhân viên kho | Trả về | 46: hiển thị | đóng 1 thanh tại purchaseInvoice.jsp |
| 47 | Nhân viên kho | purchaseInvoice.jsp | Gọi | 47: click Chọn tại dòng PT1 | giữ các thanh đang mở |
| 48 | purchaseInvoice.jsp | PurchaseInvoiceServlet4 | Gọi | 48: gọi | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 49 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 49: addInvoiceLine(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 50 | PurchaseInvoiceServlet4 | SparePartDAO4 | Gọi | 50: gọi | mở 1 thanh tại SparePartDAO4 |
| 51 | SparePartDAO4 | SparePartDAO4 | Tự gọi | 51: getSparePartDetails(sparePartId, false) | mở 1 thanh tại SparePartDAO4 |
| 52 | SparePartDAO4 | SparePart4 | Gọi | 52: gọi | mở 1 thanh tại SparePart4 |
| 53 | SparePart4 | SparePart4 | Tự gọi | 53: SparePart4() | mở 1 thanh tại SparePart4; đóng thanh constructor sau khi đóng gói |
| 54 | SparePart4 | SparePartDAO4 | Trả về | 54: trả về | đóng 1 thanh tại SparePart4 |
| 55 | SparePartDAO4 | PurchaseInvoiceServlet4 | Trả về | 55: trả về | đóng 2 thanh tại SparePartDAO4 |
| 56 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 56: showDraft(request, response) | đóng 2 thanh xử lí trước tại PurchaseInvoiceServlet4 trước thông điệp này; mở 1 thanh tại PurchaseInvoiceServlet4 |
| 57 | PurchaseInvoiceServlet4 | purchaseInvoice.jsp | Trả về | 57: trả về | mở 1 thanh tại purchaseInvoice.jsp; đóng 1 thanh tại PurchaseInvoiceServlet4 |
| 58 | purchaseInvoice.jsp | Nhân viên kho | Trả về | 58: hiển thị | đóng 1 thanh tại purchaseInvoice.jsp |
| 59 | Nhân viên kho | purchaseInvoice.jsp | Gọi | 59: nhập 10 cái; 80.000 đồng/cái | giữ các thanh đang mở |
| 60 | purchaseInvoice.jsp | Nhân viên kho | Trả về | 60: hiển thị | giữ các thanh đang mở |
| 61 | Nhân viên kho | purchaseInvoice.jsp | Gọi | 61: click Xác nhận | giữ các thanh đang mở |
| 62 | purchaseInvoice.jsp | PurchaseInvoiceServlet4 | Gọi | 62: gọi | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 63 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 63: confirmInvoice(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 64 | PurchaseInvoiceServlet4 | PurchaseInvoiceDAO4 | Gọi | 64: gọi | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 65 | PurchaseInvoiceDAO4 | PurchaseInvoiceDAO4 | Tự gọi | 65: savePurchaseInvoice(purchaseInvoice) | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 66 | PurchaseInvoiceDAO4 | SparePartDAO4 | Gọi | 66: gọi | mở 1 thanh tại SparePartDAO4 |
| 67 | SparePartDAO4 | SparePartDAO4 | Tự gọi | 67: increaseStockAfterPurchase(connection, sparePartId, quantity) | mở 1 thanh tại SparePartDAO4 |
| 68 | SparePartDAO4 | PurchaseInvoiceDAO4 | Trả về | 68: trả về | đóng 2 thanh tại SparePartDAO4 |
| 69 | PurchaseInvoiceDAO4 | PurchaseInvoiceServlet4 | Trả về | 69: trả về | đóng 2 thanh tại PurchaseInvoiceDAO4 |
| 70 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 70: viewInvoice(request, response) | đóng 2 thanh xử lí trước tại PurchaseInvoiceServlet4 trước thông điệp này; mở 1 thanh tại PurchaseInvoiceServlet4 |
| 71 | PurchaseInvoiceServlet4 | PurchaseInvoiceDAO4 | Gọi | 71: gọi | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 72 | PurchaseInvoiceDAO4 | PurchaseInvoiceDAO4 | Tự gọi | 72: getPurchaseInvoiceDetails(invoiceId) | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 73 | PurchaseInvoiceDAO4 | PurchaseInvoice4 | Gọi | 73: gọi | mở 1 thanh tại PurchaseInvoice4 |
| 74 | PurchaseInvoice4 | PurchaseInvoice4 | Tự gọi | 74: PurchaseInvoice4() | mở 1 thanh tại PurchaseInvoice4; đóng thanh constructor sau khi đóng gói |
| 75 | PurchaseInvoice4 | PurchaseInvoiceDAO4 | Trả về | 75: trả về | đóng 1 thanh tại PurchaseInvoice4 |
| 76 | PurchaseInvoiceDAO4 | PurchaseInvoiceServlet4 | Trả về | 76: trả về | đóng 2 thanh tại PurchaseInvoiceDAO4 |
| 77 | PurchaseInvoiceServlet4 | confirmedInvoice.jsp | Gọi | 77: gọi | mở 1 thanh tại confirmedInvoice.jsp; đóng 1 thanh tại PurchaseInvoiceServlet4 |
| 78 | confirmedInvoice.jsp | Nhân viên kho | Trả về | 78: hiển thị | đóng 1 thanh tại confirmedInvoice.jsp |
| 79 | Nhân viên kho | confirmedInvoice.jsp | Gọi | 79: click In hóa đơn | giữ các thanh đang mở |
| 80 | confirmedInvoice.jsp | PurchaseInvoiceServlet4 | Gọi | 80: gọi | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 81 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 81: printInvoice(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 82 | PurchaseInvoiceServlet4 | PurchaseInvoiceDAO4 | Gọi | 82: gọi | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 83 | PurchaseInvoiceDAO4 | PurchaseInvoiceDAO4 | Tự gọi | 83: getPurchaseInvoiceDetails(invoiceId) | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 84 | PurchaseInvoiceDAO4 | PurchaseInvoice4 | Gọi | 84: gọi | mở 1 thanh tại PurchaseInvoice4 |
| 85 | PurchaseInvoice4 | PurchaseInvoice4 | Tự gọi | 85: PurchaseInvoice4() | mở 1 thanh tại PurchaseInvoice4; đóng thanh constructor sau khi đóng gói |
| 86 | PurchaseInvoice4 | PurchaseInvoiceDAO4 | Trả về | 86: trả về | đóng 1 thanh tại PurchaseInvoice4 |
| 87 | PurchaseInvoiceDAO4 | PurchaseInvoiceServlet4 | Trả về | 87: trả về | đóng 2 thanh tại PurchaseInvoiceDAO4 |
| 88 | PurchaseInvoiceServlet4 | printInvoice.jsp | Gọi | 88: gọi | mở 1 thanh tại printInvoice.jsp; đóng 2 thanh tại PurchaseInvoiceServlet4 |
| 89 | printInvoice.jsp | Nhân viên kho | Trả về | 89: hiển thị | đóng 1 thanh tại printInvoice.jsp |
| 90 | Nhân viên kho | printInvoice.jsp | Gọi | 90: click In | giữ các thanh đang mở |
| 91 | printInvoice.jsp | PurchaseInvoiceServlet4 | Gọi | 91: gọi | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 92 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 92: viewInvoice(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 93 | PurchaseInvoiceServlet4 | PurchaseInvoiceDAO4 | Gọi | 93: gọi | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 94 | PurchaseInvoiceDAO4 | PurchaseInvoiceDAO4 | Tự gọi | 94: getPurchaseInvoiceDetails(invoiceId) | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 95 | PurchaseInvoiceDAO4 | PurchaseInvoice4 | Gọi | 95: gọi | mở 1 thanh tại PurchaseInvoice4 |
| 96 | PurchaseInvoice4 | PurchaseInvoice4 | Tự gọi | 96: PurchaseInvoice4() | mở 1 thanh tại PurchaseInvoice4; đóng thanh constructor sau khi đóng gói |
| 97 | PurchaseInvoice4 | PurchaseInvoiceDAO4 | Trả về | 97: trả về | đóng 1 thanh tại PurchaseInvoice4 |
| 98 | PurchaseInvoiceDAO4 | PurchaseInvoiceServlet4 | Trả về | 98: trả về | đóng 2 thanh tại PurchaseInvoiceDAO4 |
| 99 | PurchaseInvoiceServlet4 | confirmedInvoice.jsp | Gọi | 99: gọi | mở 1 thanh tại confirmedInvoice.jsp; đóng 2 thanh tại PurchaseInvoiceServlet4 |
| 100 | confirmedInvoice.jsp | Nhân viên kho | Trả về | 100: hiển thị | đóng 1 thanh tại confirmedInvoice.jsp |
| 101 | Nhân viên kho | confirmedInvoice.jsp | Gọi | 101: nhập 800.000; click Đã thanh toán | giữ các thanh đang mở |
| 102 | confirmedInvoice.jsp | PurchaseInvoiceServlet4 | Gọi | 102: gọi | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 103 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 103: recordPayment(request, response) | mở 1 thanh tại PurchaseInvoiceServlet4 |
| 104 | PurchaseInvoiceServlet4 | PurchaseInvoiceDAO4 | Gọi | 104: gọi | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 105 | PurchaseInvoiceDAO4 | PurchaseInvoiceDAO4 | Tự gọi | 105: recordPayment(invoiceId, amountPaid, currentWarehouseStaffId) | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 106 | PurchaseInvoiceDAO4 | PurchaseInvoiceServlet4 | Trả về | 106: trả về | đóng 2 thanh tại PurchaseInvoiceDAO4 |
| 107 | PurchaseInvoiceServlet4 | PurchaseInvoiceServlet4 | Tự gọi | 107: viewInvoice(request, response) | đóng 2 thanh xử lí trước tại PurchaseInvoiceServlet4 trước thông điệp này; mở 1 thanh tại PurchaseInvoiceServlet4 |
| 108 | PurchaseInvoiceServlet4 | PurchaseInvoiceDAO4 | Gọi | 108: gọi | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 109 | PurchaseInvoiceDAO4 | PurchaseInvoiceDAO4 | Tự gọi | 109: getPurchaseInvoiceDetails(invoiceId) | mở 1 thanh tại PurchaseInvoiceDAO4 |
| 110 | PurchaseInvoiceDAO4 | PurchaseInvoice4 | Gọi | 110: gọi | mở 1 thanh tại PurchaseInvoice4 |
| 111 | PurchaseInvoice4 | PurchaseInvoice4 | Tự gọi | 111: PurchaseInvoice4() | mở 1 thanh tại PurchaseInvoice4; đóng thanh constructor sau khi đóng gói |
| 112 | PurchaseInvoice4 | PurchaseInvoiceDAO4 | Trả về | 112: trả về | đóng 1 thanh tại PurchaseInvoice4 |
| 113 | PurchaseInvoiceDAO4 | PurchaseInvoiceServlet4 | Trả về | 113: trả về | đóng 2 thanh tại PurchaseInvoiceDAO4 |
| 114 | PurchaseInvoiceServlet4 | confirmedInvoice.jsp | Trả về | 114: trả về | mở 1 thanh tại confirmedInvoice.jsp; đóng 1 thanh tại PurchaseInvoiceServlet4 |
| 115 | confirmedInvoice.jsp | Nhân viên kho | Trả về | 115: thông báo thành công | đóng 1 thanh tại confirmedInvoice.jsp |

Các khung lặp trên cùng biểu đồ:

- Bao các thông điệp **35–60** trong khung `loop [lặp đến khi đủ phụ tùng cần nhập]`.
- Bao các thông điệp **66–68** trong khung `loop [với từng phụ tùng trong hóa đơn nhập]`.
