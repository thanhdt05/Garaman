# Đối chiếu scenario v3.0 luồng thành công của hai modul

Ngày rà soát 28/10/2026. Bản này trình bày ở mức chi tiết của bài giảng, thay bản 221 bước nhập phụ tùng. Sản phẩm chính gồm đúng **hai sequence**, mỗi modul một hình liền mạch, cùng hai danh sách bước kịch bản thành công trong [scenario v3.0](SCENARIO_V3_VA_BIEU_DO_TUAN_TU.md). Danh sách đường sống và bảng từng thông điệp để vẽ theo nằm ở [hướng dẫn Visual Paradigm](VE_SEQUENCE_VISUAL_PARADIGM.md).

## Căn cứ trình bày

Bài giảng `BG HP TTTN 2 CNPM 2020 final.pdf`, mục 4.3.2, trang in 103–111, Hình 4.10/4.12/4.14: gọi lớp DAO; DAO gọi hàm; hàm gọi lớp thực thể; thực thể đóng gói; thực thể trả kết quả; hàm trả kết quả. Các bước đều tách riêng. Hình có số thông điệp liên tục và thanh kích hoạt của phương thức. Bài giảng cho phép lược đóng gói thuộc tính con.

Nghiệp vụ lấy từ `26_B23DCCN764.docx`, mục 1.2.1, 1.2.3 và 1.2.4. Tên lấy từ `TEN_CU_TEN_MOI.csv`, `CODE_ENGLISH_RENAME.csv` và hai hình lớp `catalog-design.puml`, `purchase-design.puml`. Giữ Servlet điều phối như source và hình lớp; không đổi thành JSP truy vấn trực tiếp chỉ để giống công nghệ của ví dụ bài giảng.

Luồng minh họa chọn NCC1 và PT1 đã có, theo kịch bản thành công chuẩn trong bài làm. Những thao tác không đi qua trong luồng này được giữ ngoài hai sequence. Bản cũ cùng các hình chia nhỏ được chuyển vào `.review/sequence-before-success-only-20261028/`; không dùng chúng để vẽ bản hiện tại.

Theo đính chính của chủ bài, sequence tra cứu kết thúc ở hiển thị chi tiết PT1, tương ứng bước 6 của kịch bản chính mục 1.2.1. Đoạn click Quay lại và tìm lại danh sách đã bỏ khỏi scenario v3, sequence và bảng vẽ. DOCX có nút Quay lại ở màn chi tiết và kịch bản v2 có mô tả thao tác đó; điều này không bắt buộc đưa thao tác tùy chọn ấy vào lần thực hiện thành công đang minh họa. Giữ nguyên giao diện và code của nút.

Theo xác nhận mới của chủ bài, nhà cung cấp không phải actor của GaraMan, không có tài khoản và không sử dụng phần mềm; thông tin nhà cung cấp do nhân viên kho nhập và quản lí. Modul nhập phụ tùng chỉ có actor Nhân viên kho. Bỏ đường sống actor Supplier và bốn thông điệp giao nhận ngoài phần mềm (102–105 của bản trước); gộp bối cảnh thực tế thanh toán và tiếp nhận hóa đơn bán hàng vào câu mô tả thao tác ghi nhận thanh toán của nhân viên. Giữ SupplierServlet4, SupplierDAO4 và Supplier4. Các đoạn trong DOCX nguồn đang gọi nhà cung cấp là actor tham gia gián tiếp cần được chủ bài chỉnh tương ứng khi cập nhật báo cáo; không sửa DOCX nguồn trong lượt này.

## Đối chiếu lời gọi và thứ tự

| Đoạn nghiệp vụ | Source và điều cần giữ |
|---|---|
| Mở tra cứu | [CatalogServlet4.showSearch](../src/main/java/controller/CatalogServlet4.java): chưa truy vấn khi mở lần đầu. |
| Tìm danh sách | CatalogServlet4.searchCatalog → [ServiceDAO4.findServicesByName](../src/main/java/dao/ServiceDAO4.java) rồi [SparePartDAO4.findSparePartsByName](../src/main/java/dao/SparePartDAO4.java), publishedOnly=true. JSP áp dụng bộ lọc khi hiển thị. Sequence không mở thêm một lần tìm từ thao tác Quay lại. |
| Xem PT1 | CatalogServlet4.viewDetails → SparePartDAO4.getSparePartDetails(sparePartId,true). Hình lấy nhánh chọn PT1 như kịch bản chuẩn của bài làm. |
| Tìm NCC | [SupplierServlet4.searchSuppliers](../src/main/java/controller/SupplierServlet4.java) → [SupplierDAO4.findSuppliersByName](../src/main/java/dao/SupplierDAO4.java). |
| Bắt đầu nhập | [PurchaseInvoiceServlet4.startPurchase](../src/main/java/controller/PurchaseInvoiceServlet4.java) lấy NCC qua DAO trước, lấy nhân viên từ [WarehouseStaffDAO4](../src/main/java/dao/WarehouseStaffDAO4.java) sau; mã nhân viên lấy từ phiên. |
| Tạo và đọc nháp | startPurchase tạo nháp trong session; qua redirect/GET mới → showDraft đọc dữ liệu session. Trên hình giữ startPurchase và mốc showDraft; tạo/đọc/giữ nháp nằm bên trong bước xử lí. Gói util và DraftStore4 đã được bỏ khỏi code theo yêu cầu rút gọn. |
| Tìm PT trên hóa đơn | searchSpareParts → saveLineInputsToDraft → chuyển hướng → showDraft → đọc nháp session → findSparePartsByName(keyword,false). Lời truy vấn không nằm trực tiếp trong searchSpareParts. |
| Chọn PT1 | addInvoiceLine lấy chi tiết PT1 trước khi giữ input; thêm dòng vào nháp; qua showDraft để hiển thị. Khởi tạo/gán thuộc tính dòng và giữ nháp được gom trong bước bổ sung dòng. Lược đường sống PurchaseInvoiceDetail4 như thuộc tính con của hóa đơn. |
| Nhập lượng/giá | [purchase.js](../src/main/webapp/assets/js/purchase.js) tính tổng dự kiến khi gõ; chưa gửi request. Request Xác nhận sau đó mới giữ đủ input của dòng ở server. |
| Xác nhận | confirmInvoice nhận dữ liệu dòng, giữ nháp và kiểm tra hợp lệ rồi gọi savePurchaseInvoice. Giữ input và đánh dấu đang xác nhận nằm bên trong bước confirmInvoice. |
| Lưu HN1 | [PurchaseInvoiceDAO4.savePurchaseInvoice](../src/main/java/dao/PurchaseInvoiceDAO4.java) lưu đầu hóa đơn, toàn bộ dòng và chốt thông tin; lặp increaseStockAfterPurchase bằng cùng Connection; commit rồi trả mã HN1. Các hàm private insert và chi tiết khóa nằm bên trong bước savePurchaseInvoice, không tách thành thông điệp. |
| Hiển thị chứng từ | Sau xác nhận, viewInvoice → getPurchaseInvoiceDetails → confirmedInvoice.jsp. Bỏ nháp và redirect/GET được lược. Mốc viewInvoice bắt đầu một lần thực hiện mới, sau khi thanh confirmInvoice kết thúc; không phải lời gọi Java trực tiếp bên trong confirmInvoice. DAO dùng tên đã chốt. |
| Tính tiền khi render | [PurchaseInvoice4](../src/main/java/model/PurchaseInvoice4.java) tính tổng từ [PurchaseInvoiceDetail4](../src/main/java/model/PurchaseInvoiceDetail4.java). Toàn bộ getter, tính dòng và tính tổng khi render được gom trong bước hiển thị giao diện; không vẽ chuỗi tính tổng lặp lại sau mỗi lần tải HĐ. |
| In và quay lại | printInvoice của Servlet → lấy HĐ đã lưu → printInvoice.jsp. Click In mở hộp thoại in qua JavaScript; gom việc này vào thao tác của nhân viên, không vẽ self-call printInvoice() thành phương thức của JSP. afterprint phát sinh yêu cầu viewInvoice mới → đọc lại HĐ và hiển thị; chuỗi DAO này được giữ vì là lần lấy chứng từ tiếp theo trong luồng. afterprint cho biết hộp thoại đã đóng, không chứng minh máy in đã in giấy. Luồng minh họa nhân viên thực tế có chứng từ để giao NCC. |
| Giao chứng từ/trả tiền | Hoạt động ngoài phần mềm, được nêu trong câu mô tả thao tác của nhân viên trước khi nhập số tiền và click Đã thanh toán; không có actor Supplier, Servlet/cổng thanh toán hoặc chức năng lưu hóa đơn bán hàng NCC. |
| Ghi nhận thanh toán | recordPayment ở Servlet → recordPayment ở DAO; kiểm tra và đối chiếu tiền được mô tả trong bước thực hiện hàm DAO, không vẽ các lời gọi tính dòng riêng. Sau ghi nhận thành công, viewInvoice → đọc lại HĐ → hiển thị trạng thái và thông báo. Không tăng tồn thêm. |

Mức chi tiết được giữ là thao tác người dùng, điều phối nghiệp vụ, lời gọi DAO, thực hiện hàm, đóng gói thực thể và trả kết quả theo bài giảng. DraftStore4, saveLineInputsToDraft, các hàm private insert, getter tính tiền, khóa phiên, requestCode/version và getter/setter từng trường được lược bên trong bước nghiệp vụ tương ứng. Theo cách trình bày chủ bài đã chọn, JSP → Servlet ghi “gọi”, tiếp theo Servlet tự gọi hàm xử lí có tên tương ứng trong lớp thiết kế. Hai bước được tách riêng trong scenario, thống nhất cách thể hiện DAO. Trong source, doGet/doPost điều phối tới các hàm này; lược doGet/doPost trên hình. Các self-message showDraft/viewInvoice sau chuyển xử lí đánh dấu lần yêu cầu tiếp theo trên cùng đường sống; đã kết thúc các thanh xử lí trước, lược chi tiết truyền tải HTTP.

Phân biệt kết quả trả cho trang gửi yêu cầu với chuyển sang trang khác: Servlet → chính JSP gửi yêu cầu dùng “trả về” và nét đứt; Servlet → JSP khác dùng “gọi” và nét liền. Vì vậy thông điệp 21 của tra cứu là searchCatalog trả kết quả về catalogSearch.jsp; thông điệp mở itemDetails.jsp vẫn là “gọi”. Các kết quả tìm NCC, tìm/chọn PT trên hóa đơn và cập nhật thanh toán trên cùng trang được áp dụng nhất quán. Đây là cách thể hiện phản hồi ở mức giao diện nghiệp vụ; source vẫn dùng request attributes và forward/redirect như đã đối chiếu, không đổi thành lời gọi Java trả về một đối tượng JSP.

Các phản hồi Servlet → trang gửi yêu cầu đã kiểm tra trên toàn bộ hai sequence:

| Modul | Thông điệp | Kết quả trả về | JSP nhận |
|---|---|---|---|
| Tra cứu | 21 | Danh sách dịch vụ và phụ tùng tìm được | catalogSearch.jsp |
| Nhập phụ tùng | 15 | Danh sách nhà cung cấp tìm được | selectSupplier.jsp |
| Nhập phụ tùng | 45 | Danh sách phụ tùng và dữ liệu hóa đơn đang lập | purchaseInvoice.jsp |
| Nhập phụ tùng | 57 | Hóa đơn dự kiến sau khi bổ sung dòng PT1 | purchaseInvoice.jsp |
| Nhập phụ tùng | 114 | Hóa đơn đã cập nhật thanh toán và thông báo thành công | confirmedInvoice.jsp |

Cả năm thông điệp dùng nét đứt và nhãn “trả về”; scenario ghi rõ dữ liệu trả về. Đã kiểm tra riêng các phản hồi thực thể → DAO, DAO → Servlet và DAO → DAO: đều dùng “trả về”. Các bước chuyển mở giao diện khác, gọi DAO hoặc gọi lớp thực thể để đóng gói giữ nhãn “gọi”. Bảng hướng dẫn Visual Paradigm và hình xuất lấy cùng dữ liệu thông điệp với scenario.

Đối chiếu bước in: bài giảng mục 4.3.2, trang in 103 yêu cầu tên hành động của lớp tương ứng phương thức đã thiết kế trong biểu đồ lớp. Các kịch bản mẫu không có JSP tự gọi hàm printInvoice(). Thiết kế hiện tại chỉ khai báo printButton: button ở printInvoice.jsp; hàm printInvoice() trong assets/js/print.js là hàm JavaScript gọi window.print(), không phải phương thức Java của lớp JSP. Đã bỏ self-message 91 của bản trước và mô tả xử lí phía trình duyệt trong bước click In. Không chuyển thao tác mở hộp thoại in sang Servlet; phương thức printInvoice(request, response) của Servlet chỉ chuẩn bị dữ liệu và mở trang in.

## Phần code đã sửa theo xác nhận của chủ bài — 30/10/2026

**CP1 đã sửa:** nhân viên kho có quyền ở ca hiện tại được ghi nhận thanh toán; DAO kiểm tra lại quyền trong CSDL, lưu paymentStatus/paidAt và paidByEmployee4id/paidByNameSnapshot trong cùng transaction, giữ người lập. viewInvoice mở form theo vai trò kho thay vì so sánh người lập. Giữ tên/chữ ký các hàm đã chốt, không thêm hàm hoặc trường model.

**CP2 đã sửa:** danh sách hóa đơn chưa trả của gara dùng chung cho nhân viên kho có quyền; không lọc theo người lập. Luồng chuẩn trên sequence đi từ lập đến thanh toán liên tục nên không bổ sung lời gọi mở lại danh sách vào hình. Xem [thay đổi, migration và kiểm chứng](SUA_LUONG_THEO_BAO_CAO.md).

## Tệp và kiểm tra

- [SVG tra cứu](diagrams/tuan_tu_tra_cuu.svg) và [PDF một trang tra cứu](diagrams/sequence/catalog-sequence.pdf).
- [SVG nhập phụ tùng](diagrams/tuan_tu_nhap_phu_tung.svg) và [PDF một trang nhập phụ tùng](diagrams/sequence/purchase-sequence.pdf).
- Tạo lại: `py -3 -X utf8 app/docs/diagrams/sequence/build_sequence.py`.
- Xuất hình: `py -3 -X utf8 app/docs/diagrams/sequence/render_sequence.py --plantuml .review/plantuml-1.2025.2.jar`.
- Kiểm tra tài liệu: `py -3 -X utf8 app/docs/diagrams/sequence/verify_sequence.py`.

PDF dùng trang dài theo kích thước tự nhiên để giữ toàn bộ diagram trên một trang; SVG phù hợp phóng to và nhìn theo khi vẽ Visual Paradigm. Kiểm tra số bước, tên source, thanh kích hoạt và PDF không thay thế kiểm thử phần mềm. Code Java/JSP/SQL đã được sửa theo yêu cầu tiếp theo; DOCX/PDF nguồn giữ nguyên.
