# Đối chiếu scenario v3.0 luồng thành công của hai modul

Ngày rà soát 28/10/2026. Bản này trình bày ở mức chi tiết của bài giảng, thay bản 221 bước nhập phụ tùng. Sản phẩm chính gồm đúng **hai sequence**, mỗi modul một hình liền mạch, cùng hai danh sách bước kịch bản thành công trong [scenario v3.0](SCENARIO_V3_VA_BIEU_DO_TUAN_TU.md). Danh sách đường sống và bảng từng thông điệp để vẽ theo nằm ở [hướng dẫn Visual Paradigm](VE_SEQUENCE_VISUAL_PARADIGM.md).

## Căn cứ trình bày

Bài giảng `BG HP TTTN 2 CNPM 2020 final.pdf`, mục 4.3.2, trang in 103–111, Hình 4.10/4.12/4.14: gọi lớp DAO; DAO gọi hàm; hàm gọi lớp thực thể; thực thể đóng gói; thực thể trả kết quả; hàm trả kết quả. Các bước đều tách riêng. Hình có số thông điệp liên tục và thanh kích hoạt của phương thức. Bài giảng cho phép lược đóng gói thuộc tính con.

Nghiệp vụ lấy từ `26_B23DCCN764.docx`, mục 1.2.1, 1.2.3 và 1.2.4. Tên lấy từ `TEN_CU_TEN_MOI.csv`, `CODE_ENGLISH_RENAME.csv` và hai hình lớp `catalog-design.puml`, `purchase-design.puml`. Giữ Servlet điều phối như source và hình lớp; không đổi thành JSP truy vấn trực tiếp chỉ để giống công nghệ của ví dụ bài giảng.

Luồng minh họa chọn NCC1 và PT1 đã có, theo kịch bản thành công chuẩn trong bài làm. Những thao tác không đi qua trong luồng này được giữ ngoài hai sequence. Bản cũ cùng các hình chia nhỏ được chuyển vào `.review/sequence-before-success-only-20261028/`; không dùng chúng để vẽ bản hiện tại.

## Đối chiếu lời gọi và thứ tự

| Đoạn nghiệp vụ | Source và điều cần giữ |
|---|---|
| Mở tra cứu | [CatalogServlet4.showSearch](../src/main/java/controller/CatalogServlet4.java): chưa truy vấn khi mở lần đầu. |
| Tìm và quay lại danh sách | CatalogServlet4.searchCatalog → [ServiceDAO4.findServicesByName](../src/main/java/dao/ServiceDAO4.java) rồi [SparePartDAO4.findSparePartsByName](../src/main/java/dao/SparePartDAO4.java), publishedOnly=true. Quay lại chạy lại hai DAO theo từ khóa/bộ lọc đã giữ. JSP áp dụng bộ lọc khi hiển thị. |
| Xem PT1 | CatalogServlet4.viewDetails → SparePartDAO4.getSparePartDetails(sparePartId,true). Hình lấy nhánh chọn PT1 như kịch bản chuẩn của bài làm. |
| Tìm NCC | [SupplierServlet4.searchSuppliers](../src/main/java/controller/SupplierServlet4.java) → [SupplierDAO4.findSuppliersByName](../src/main/java/dao/SupplierDAO4.java). |
| Bắt đầu nhập | [PurchaseInvoiceServlet4.startPurchase](../src/main/java/controller/PurchaseInvoiceServlet4.java) lấy NCC qua DAO trước, lấy nhân viên từ [WarehouseStaffDAO4](../src/main/java/dao/WarehouseStaffDAO4.java) sau; mã nhân viên lấy từ phiên. |
| Tạo và đọc nháp | startPurchase → [DraftStore4.createDraft](../src/main/java/util/DraftStore4.java); qua redirect/GET mới → showDraft → DraftStore4.getDraft. Trên hình chỉ giữ startPurchase và mốc showDraft; tạo/đọc/giữ nháp nằm bên trong bước xử lí. Không có đường sống DraftStore4 hoặc constructor hóa đơn do Servlet gọi trực tiếp. |
| Tìm PT trên hóa đơn | searchSpareParts → saveLineInputsToDraft → chuyển hướng → showDraft → getDraft → findSparePartsByName(keyword,false). Lời truy vấn không nằm trực tiếp trong searchSpareParts. |
| Chọn PT1 | addInvoiceLine lấy chi tiết PT1 trước khi giữ input; thêm dòng vào nháp; qua showDraft để hiển thị. Khởi tạo/gán thuộc tính dòng và giữ nháp được gom trong bước bổ sung dòng. Lược đường sống PurchaseInvoiceDetail4 như thuộc tính con của hóa đơn. |
| Nhập lượng/giá | [purchase.js](../src/main/webapp/assets/js/purchase.js) tính tổng dự kiến khi gõ; chưa gửi request. Request Xác nhận sau đó mới giữ đủ input của dòng ở server. |
| Xác nhận | confirmInvoice nhận dữ liệu dòng, giữ nháp và kiểm tra hợp lệ rồi gọi savePurchaseInvoice. Các hàm giữ input và beginConfirmation được lược bên trong bước confirmInvoice. |
| Lưu HN1 | [PurchaseInvoiceDAO4.savePurchaseInvoice](../src/main/java/dao/PurchaseInvoiceDAO4.java) lưu đầu hóa đơn, toàn bộ dòng và chốt thông tin; lặp increaseStockAfterPurchase bằng cùng Connection; commit rồi trả mã HN1. Các hàm private insert và chi tiết khóa nằm bên trong bước savePurchaseInvoice, không tách thành thông điệp. |
| Hiển thị chứng từ | Sau xác nhận, viewInvoice → getPurchaseInvoiceDetails → confirmedInvoice.jsp. Bỏ nháp và redirect/GET được lược. Mốc viewInvoice bắt đầu một lần thực hiện mới, sau khi thanh confirmInvoice kết thúc; không phải lời gọi Java trực tiếp bên trong confirmInvoice. DAO dùng tên đã chốt. |
| Tính tiền khi render | [PurchaseInvoice4](../src/main/java/model/PurchaseInvoice4.java) tính tổng từ [PurchaseInvoiceDetail4](../src/main/java/model/PurchaseInvoiceDetail4.java). Toàn bộ getter, tính dòng và tính tổng khi render được gom trong bước hiển thị giao diện; không vẽ chuỗi tính tổng lặp lại sau mỗi lần tải HĐ. |
| In và quay lại | printInvoice → lấy HĐ đã lưu → printInvoice.jsp; hàm JavaScript in được gom một bước. afterprint phát sinh yêu cầu viewInvoice mới → đọc lại HĐ và hiển thị; chuỗi DAO này được giữ vì là lần lấy chứng từ tiếp theo trong luồng. afterprint cho biết hộp thoại đã đóng, không chứng minh máy in đã in giấy. Luồng minh họa nhân viên thực tế có chứng từ để giao NCC. |
| Giao chứng từ/trả tiền | Tương tác giữa actor Nhân viên kho và Nhà cung cấp như bài làm; không có Servlet/cổng thanh toán hoặc chức năng lưu hóa đơn bán hàng NCC. |
| Ghi nhận thanh toán | recordPayment ở Servlet → recordPayment ở DAO; kiểm tra và đối chiếu tiền được mô tả trong bước thực hiện hàm DAO, không vẽ các lời gọi tính dòng riêng. Sau ghi nhận thành công, viewInvoice → đọc lại HĐ → hiển thị trạng thái và thông báo. Không tăng tồn thêm. |

Mức chi tiết được giữ là thao tác người dùng, điều phối nghiệp vụ, lời gọi DAO, thực hiện hàm, đóng gói thực thể và trả kết quả theo bài giảng. DraftStore4, saveLineInputsToDraft, các hàm private insert, getter tính tiền, khóa phiên, requestCode/version và getter/setter từng trường được lược bên trong bước nghiệp vụ tương ứng. Servlet trên hình giữ hàm đã thiết kế; riêng yêu cầu từ JSP gọi trực tiếp phương thức xử lí, không thêm một bước “gọi Servlet” rồi một bước tự gọi cùng hàm nữa. Các self-message showDraft/viewInvoice đánh dấu lần xử lí tiếp theo trên cùng đường sống; đã kết thúc thanh hàm trước, lược chi tiết truyền tải HTTP.

## Phần code cần sửa theo xác nhận của chủ bài

**CP1:** Chủ bài đã chọn bám nghiệp vụ đích và ghi phần code cần sửa: nhân viên kho có quyền ở ca hiện tại được thanh toán; lưu người thực sự trả, giữ nguyên người lập. Bước xử lí PurchaseInvoiceDAO4.recordPayment trong kịch bản hiện tại viết theo quyết định đó. Code hiện tại vẫn kiểm tra người lập và chỉ cập nhật paymentStatus/paidAt; cần sửa kiểm tra quyền trong DAO và viewInvoice, bổ sung dữ liệu người trả trong cùng transaction. Giữ tên hàm và chữ ký đã xác nhận; chưa tự đặt tên tiếng Anh cho trường/cột người trả. Ghi chú này đặt ngoài hình để sequence chỉ trình bày kịch bản thành công.

**CP2:** Quyết định mở phạm vi danh sách hóa đơn chưa trả của gara từ lượt trước vẫn được ghi nhận cho lần sửa code sau. Luồng chuẩn lần này đi từ lập đến thanh toán liên tục nên không có lời gọi danh sách mở lại trên sequence.

## Tệp và kiểm tra

- [SVG tra cứu](diagrams/tuan_tu_tra_cuu.svg) và [PDF một trang tra cứu](diagrams/sequence/catalog-sequence.pdf).
- [SVG nhập phụ tùng](diagrams/tuan_tu_nhap_phu_tung.svg) và [PDF một trang nhập phụ tùng](diagrams/sequence/purchase-sequence.pdf).
- Tạo lại: `py -3 -X utf8 app/docs/diagrams/sequence/build_sequence.py`.
- Xuất hình: `py -3 -X utf8 app/docs/diagrams/sequence/render_sequence.py --plantuml .review/plantuml-1.2025.2.jar`.
- Kiểm tra tài liệu: `py -3 -X utf8 app/docs/diagrams/sequence/verify_sequence.py`.

PDF dùng trang dài theo kích thước tự nhiên để giữ toàn bộ diagram trên một trang; SVG phù hợp phóng to và nhìn theo khi vẽ Visual Paradigm. Kiểm tra số bước, tên source, thanh kích hoạt và PDF không thay thế kiểm thử phần mềm. Không sửa Java/JSP/SQL hay DOCX/PDF nguồn trong lượt này.
