# Đối chiếu activity với mã nguồn hai module

> **Phụ lục triển khai.** Hình chính đã được thu gọn lại theo đề 26 và phần phân tích; xem [phạm vi hình chính](PHAM_VI_ACTIVITY_NHAP_PHU_TUNG.md). Các nhận xét “đã bổ sung lên hình” bên dưới mô tả [bản chi tiết trước khi thu gọn](diagrams/activity/implementation-detail/purchase-activity.svg), không yêu cầu vẽ hết 21 phương thức trên hình báo cáo.

Ngày rà soát: 22/10/2026. Phạm vi: 21 phương thức nghiệp vụ/điều phối của bốn Servlet, các JSP/JSPF liên quan, JavaScript tính tiền/in, DAO, model và Filter hỗ trợ. Đây là kiểm tra mã nguồn, không phải kết quả chạy JSP/MySQL/trình duyệt.

## Kết luận

**Bản hình trước rà soát có các đường chuyển chưa đúng code. Đã sửa hình và xuất lại SVG/PNG/PDF/drawio.** Hình mới mô tả luồng nghiệp vụ ở mức bài giảng; bảng dưới ghi cả các nhánh kỹ thuật/lỗi được lược trên hình. Không khẳng định mọi câu lệnh hoặc mọi đường lỗi đều đã được vẽ.

Những sửa đổi chính:

| Điểm | Trước rà soát | Theo code và hình đã sửa |
|---|---|---|
| Quay lại từ chi tiết tra cứu | Đi thẳng tới hiển thị danh sách | Link GET mang keyword/bộ lọc → `searchCatalog` → truy vấn lại → hiển thị |
| Lọc loại khi tìm danh mục | Tài liệu nói chỉ gọi DAO của loại đã chọn | Servlet luôn gọi cả hai DAO; JSP lọc loại khi render |
| Thêm NCC | Đặt điều kiện hợp lệ trước bước gọi lưu | Servlet nhận/đóng gói → `SupplierDAO4.addSupplier` kiểm tra rồi INSERT; sai dữ liệu hoặc SQL lỗi trở về form |
| Tìm PT, sang form thêm PT | Lược mất bước giữ dữ liệu nháp | POST nhận lượng/giá và cập nhật nháp trước khi redirect; chung nút Cập nhật nháp, tách nhánh tìm/thêm mới |
| Chọn lại PT | Đường chính luôn qua thêm dòng | Đã có dòng thì chỉ focus; không thêm trùng; không lấy được PT cũng redirect về nháp |
| Sửa lượng/giá | Dễ hiểu là gõ ô nhập gọi Servlet | Gõ chỉ tính ước lượng bằng JS; POST Cập nhật mới cập nhật nháp và render tổng phía server |
| Xóa/hủy nháp | Xóa bị bỏ khỏi hình; hủy đi thẳng tới kết thúc | Đã bổ sung hành động xóa dòng và hủy nháp; hủy thành công redirect `/home` |
| Xác nhận lại | Chưa hiện nhánh bỏ qua lưu | Có hóa đơn theo requestCode thì mở hóa đơn cũ; không cập nhật nháp/lưu mới |
| Thanh toán thành công | Thông báo rồi kết thúc | Set flash → redirect `viewInvoice` → đọc lại DB → hiển thị hóa đơn |
| Số tiền thanh toán sai | Trở về UI mà không thể hiện đọc lại | Sai định dạng hoặc DAO báo số tiền sai → `viewInvoice` → đọc lại hóa đơn và giữ input/lỗi |
| In xong | Quay lại bản in | `afterprint` điều hướng về hóa đơn đã xác nhận và đọc lại hóa đơn |
| Mở hóa đơn chưa trả | Bỏ khỏi hình, tài liệu chưa xác nhận hàm DAO | Code có `showUnpaidInvoices` → `getUnpaidInvoices(currentUser.id)`; đã đưa lên hình |

## 1. Tra cứu: đối chiếu đủ ba phương thức

Nguồn: [CatalogServlet4.java](../src/main/java/controller/CatalogServlet4.java), [catalogSearch.jsp](../src/main/webapp/WEB-INF/view/customer/catalogSearch.jsp), [itemDetails.jsp](../src/main/webapp/WEB-INF/view/customer/itemDetails.jsp).

| Phương thức | Trigger và xử lí đúng code | Đầu ra/ngoại lệ |
|---|---|---|
| `showSearch` | GET `/customer/catalog` không có keyword; nhận bộ lọc mặc định all | Forward `catalogSearch.jsp`, `hasSearched=false`; lọc sai → HTTP 400 |
| `searchCatalog` | GET có keyword; trim; kiểm tra rỗng/dài >255; gọi `findServicesByName`, rồi `findSparePartsByName(keyword,true)` | Forward cùng JSP; JSP áp dụng itemTypeFilter, ghép một bảng. Rỗng/quá dài → lỗi tại form; SQL lỗi → cùng form, `hasSearched=false` |
| `viewDetails` | GET `/customer/catalog/detail`; kiểm tra ID/loại, gọi đúng `getServiceDetails` hoặc `getSparePartDetails(id,true)` | Forward `itemDetails.jsp`; sai ID/loại/lọc →400; không có →404; SQL lỗi →500 |

Link Quay lại ở `itemDetails.jsp` gửi keyword/itemTypeFilter về `/customer/catalog`. Nó không dùng bản danh sách đã cache. Code chạy tìm lại, nên danh sách có thể thay đổi nếu DB thay đổi.

## 2. NCC: đối chiếu đủ bốn phương thức

Nguồn: [SupplierServlet4.java](../src/main/java/controller/SupplierServlet4.java), [SupplierDAO4.java](../src/main/java/dao/SupplierDAO4.java), [newSupplier.jsp](../src/main/webapp/WEB-INF/view/warehouse/newSupplier.jsp), [selectSupplier.jsp](../src/main/webapp/WEB-INF/view/warehouse/selectSupplier.jsp).

| Phương thức | Trigger và xử lí đúng code | Đầu ra/ngoại lệ |
|---|---|---|
| `showSupplierSelection` | GET `/warehouse/suppliers` không có keyword | Forward chọn NCC, `daTim=false`; không truy vấn danh sách |
| `searchSuppliers` | GET có keyword; trim rồi gọi `findSuppliersByName` | Forward chọn NCC; SQL lỗi cũng forward với thông báo. Không chặn rỗng như tìm danh mục khách hàng |
| `showNewSupplier` | GET `/warehouse/suppliers/new` | Forward form thêm NCC; không gọi DAO lấy nhân viên |
| `addSupplier` | POST `/warehouse/suppliers/create`; đọc bốn trường, dựng Supplier4; gọi DAO | DAO kiểm tra dữ liệu trước INSERT. Thành công → flash và redirect tìm theo tên NCC mới, mang selectedId để tô nổi. Validation/SQL lỗi → forward form, giữ formValues |

`Nhận thông tin NCC` trên hình là đọc dữ liệu POST, khác với nhận NCC đã chọn khi bắt đầu nhập. `Nhận thông tin NV` tại các màn là ngữ cảnh phiên đăng nhập có sẵn, không phải một lời gọi DAO mới. Nhãn phần cuối JSP nói “tự động chọn NCC” nhưng code thực tế chỉ tô nổi selectedId; nhân viên vẫn bấm Chọn để POST `/warehouse/purchases/start`.

## 3. Phụ tùng mới: đối chiếu đủ hai phương thức

Nguồn: [SparePartServlet4.java](../src/main/java/controller/SparePartServlet4.java), [SparePartDAO4.java](../src/main/java/dao/SparePartDAO4.java), [SparePartCategoryDAO4.java](../src/main/java/dao/SparePartCategoryDAO4.java), [newSparePart.jsp](../src/main/webapp/WEB-INF/view/warehouse/newSparePart.jsp).

| Phương thức | Trigger và xử lí đúng code | Đầu ra/ngoại lệ |
|---|---|---|
| `showNewSparePart` | GET `/warehouse/parts/new`; đọc nháp theo requestCode; gọi `getActiveCategories` | Forward form; không có nháp → chọn NCC; SQL tải nhóm lỗi → form kèm lỗi |
| `addSparePart` | POST `/warehouse/parts/create`; kiểm tra nháp/version, đọc các trường; `getActiveCategory(id)` xác minh nhóm, parse giá bán, validateSparePart; gọi `addSparePart` | Thành công → redirect `/warehouse/purchases/draft` với tên và selectedPartId; không tự thêm dòng hóa đơn. Validation/SQL lỗi thông thường → tải lại nhóm và forward form; xung đột →409; mất nháp → chọn NCC |

DAO thêm PT kiểm tra lại dữ liệu, đặt stockQuantity=0 và INSERT. Danh sách kho dùng publishedOnly=false, vẫn loại phụ tùng ngừng kinh doanh. Danh sách khách dùng true và chỉ lấy danh mục công bố theo điều kiện SQL.

Quay lại trên form thêm PT chỉ GET nháp, không tạo phụ tùng. Form NCC cũng có link Quay lại, chỉ mở lại trang chọn NCC chưa tìm.

## 4. Hóa đơn: đối chiếu đủ 12 phương thức

Nguồn: [PurchaseInvoiceServlet4.java](../src/main/java/controller/PurchaseInvoiceServlet4.java), [PurchaseInvoiceDAO4.java](../src/main/java/dao/PurchaseInvoiceDAO4.java), [purchaseInvoice.jsp](../src/main/webapp/WEB-INF/view/warehouse/purchaseInvoice.jsp), [parts-picker.jspf](../src/main/webapp/WEB-INF/view/warehouse/parts-picker.jspf), [confirmedInvoice.jsp](../src/main/webapp/WEB-INF/view/warehouse/confirmedInvoice.jsp).

| Phương thức | Trigger và xử lí đúng code | Đầu ra/ngoại lệ |
|---|---|---|
| `startPurchase` | POST start; lấy NCC bằng `getSupplierDetails`, lấy NV bằng `getWarehouseStaffDetails`; tạo nháp | Redirect GET draft; NCC/NV thiếu, validation/SQL lỗi → chọn NCC |
| `showDraft` | GET draft; kiểm tra hóa đơn đã lưu theo requestCode; đọc nháp/version/formValues; chỉ tìm PT khi keyword không trắng | Đã lưu → view; mất nháp → chọn NCC; bình thường forward hóa đơn nháp; lỗi tìm PT → partSearchError trên nháp, không giả làm không có kết quả |
| `searchSpareParts` | POST search-part; kiểm tra version/quyền/concurrency, `saveLineInputsToDraft`, nhận keyword | Redirect GET draft mang keyword; query thực sự nằm trong `showDraft` |
| `addInvoiceLine` | POST add-line; nhận mã PT, `getSparePartDetails(id,false)`; kiểm tra nháp/version; giữ lượng/giá; kiểm tra trùng | Trùng → focus dòng cũ; mới → dòng 0/0; PT null → không thêm; cuối cùng GET draft. Lỗi validation mã PT/SQL bắt được → GET draft |
| `updateInvoiceLines` | POST update; kiểm tra version; lưu input dòng | destination=new-part → GET form thêm PT; còn lại → GET draft |
| `removeInvoiceLine` | POST remove-line; giữ input; bỏ dòng có mã chọn, xóa lỗi dòng, tăng version | Redirect GET draft; mã dòng validation lỗi bị bỏ qua rồi cũng quay về draft |
| `cancelDraft` | POST cancel sau confirm phía trình duyệt; kiểm tra version; xóa nháp | Flash và redirect `/home`; không xóa danh mục đã thêm, không ghi tồn |
| `confirmInvoice` | POST confirm; **tìm hóa đơn cũ trước**; nếu chưa có mới giữ input, khóa nháp, kiểm tra lỗi/rỗng/lượng/giá và gọi `savePurchaseInvoice` | Có sẵn hoặc lưu thành công → view; lỗi input/validation/SQL → GET draft. Mất phiên → login; version/quyền/concurrency sai có thể trả HTTP |
| `viewInvoice` | GET view; kiểm tra ID, `getPurchaseInvoiceDetails`; tính canRecordPayment bằng ID người lập | Forward confirmedInvoice; ID sai→400, không có→404, SQL→500 |
| `printInvoice` | GET print; kiểm tra ID, đọc hóa đơn đã lưu | Forward printInvoice; ID sai→400, không có→404, SQL→500 |
| `recordPayment` | POST pay; kiểm tra ID; parse amount; gọi DAO với ID NV hiện tại | Thành công/đã trả → flash + redirect view; lỗi tiền → gọi lại viewInvoice, giữ input; sai quyền→403, không có HĐ→404, ID không hợp lệ→400, SQL→500 |
| `showUnpaidInvoices` | GET unpaid; `getUnpaidInvoices(currentUser.id)` | Forward warehouseHome với danh sách chỉ của mình; SQL lỗi → cùng trang với lỗi. Chọn HĐ → GET view |

### Nháp, cập nhật và tính tiền

`saveLineInputsToDraft` nhận các ô quantity/purchaseUnitPrice của dòng đã có, parse, kiểm tra đơn vị, lưu dữ liệu và lỗi theo từng ô, tăng version. Các POST tìm PT, chọn PT, cập nhật, xóa dòng và xác nhận đều có bước giữ input này sau các kiểm tra tương ứng. Chọn PT truy vấn chi tiết trước bước giữ input; không đảo thứ tự đó thành tìm sau khi thêm.

Trên hình, `Nhận thông tin PT` gom nhận ID và lấy chi tiết; `Thêm PT vào HĐ` gồm giữ input và bổ sung dòng. `Nhận thông tin HĐ` gom xử lí request xác nhận; nhánh đã xác nhận được kiểm tra trước cập nhật nháp. Những thao tác nội bộ này không thêm HTTP request mới.

[purchase.js](../src/main/webapp/assets/js/purchase.js) chỉ tính tổng ước lượng khi gõ. POST cập nhật mới lưu nháp; khi JSP render, getter/model tính thành tiền và tổng tiền phía server. Nút `Tính tổng tiền` trên hình mô tả phép tính này, không phải route hay nút bấm riêng. Form thêm PT giữ dữ liệu của nháp cũ bằng POST update trước khi mở form.

### Xác nhận và tồn kho

`savePurchaseInvoice` quản lí: kiểm tra trùng/validation; transaction khóa NCC, NV, PT; chốt tên/đơn vị; lưu header; batch toàn bộ chi tiết; gọi `increaseStockAfterPurchase` cho từng dòng với cùng Connection; commit. Validation/SQL lỗi thì rollback; trùng requestCode có nhánh trả hóa đơn cũ. Hình gom tất cả dưới `Lưu HĐ`, không diễn giải lưu mỗi dòng là một lần xác nhận.

### Thanh toán và in

DAO `recordPayment`: kiểm tra tiền dương; khóa hóa đơn; kiểm tra người lập; nếu đã trả thì trả false; nếu chưa trả thì tính tổng chi tiết và yêu cầu amountPaid **bằng** tổng (thiếu hoặc thừa đều sai); cập nhật trạng thái và paidAt, commit. Không cập nhật tồn.

`Thông báo kết quả` trên hình là `setFlash`, chưa phải cửa sổ thông báo riêng. Sau đó redirect view; Filter đưa flash vào request để trang hóa đơn hiển thị trạng thái và thông báo. SQL lỗi khi thanh toán trả HTTP 500, không tự tải lại hóa đơn như lỗi dữ liệu tiền.

[print.js](../src/main/webapp/assets/js/print.js) gọi `window.print()`. Sự kiện `afterprint` chuyển về link view hóa đơn; sự kiện này có thể xảy ra cả khi đóng/hủy hộp thoại, nên không dùng nó để khẳng định chứng từ đã được in thành công. Link Quay lại cũng GET view. Không có thao tác lưu/incrementStock trong in.

## 5. Các nhánh kỹ thuật/lỗi không vẽ thành khung riêng

Để giữ văn phong và bố cục bài giảng, hình chính không mở thêm khung cho các lỗi sau. Đây là phạm vi lược có chủ đích; không thay các nhánh HTTP bằng đường tự quay lại UI.

- AuthFilter: chưa đăng nhập → login; sai vai trò hoặc CSRF →403. Ngữ cảnh đăng nhập là tiền điều kiện của hai hình.
- Nháp/version: thiếu/sai mã hoặc version →400; sai người sửa →403; version cũ/đang xác nhận →409; nháp mất/hết hạn ở các GET → chọn NCC. Mỗi phương thức có cách bắt lỗi riêng theo bảng, không phải lỗi nào cũng quay lại form.
- `showDraft` còn kiểm tra hóa đơn đã lưu và có thể chuyển view khi refresh nháp cũ. Nút lấy nháp/đặt request attributes được lược trong các đường trở về `Hiển thị GD`; không có nghĩa Servlet bỏ qua GET draft.
- HTTP 400/403/404/405/409/500 được web.xml ánh xạ đến `common/error.jsp`. Không vẽ chúng thành màn nghiệp vụ mới.
- Khung `Hiển thị GD` có thể bao gồm thông báo không tìm thấy hoặc lỗi form. Hiển thị danh sách HĐ chưa trả vẫn dùng warehouseHome.jsp, không tạo trang JSP mới.
- Nút kết thúc trong hình biểu diễn rời phạm vi nghiệp vụ: link về `/home`, hoặc hủy nháp xong redirect `/home`; không có API tên kết thúc.

## 6. Khác biệt còn lại giữa hình lớp DOCX và source

Source hiện có `getActiveCategory`, `getUnpaidInvoices`, `findInvoiceIdByRequestCode`; hình lớp DOCX chưa liệt kê đủ các phương thức này. **Không còn đúng nếu nói code chưa có hàm DAO lấy hóa đơn chưa thanh toán.** Tài liệu trước rà soát chỉ nói hình lớp thiếu hàm đó, nhưng chưa đủ đối chiếu code. Bản này giữ nguyên hình lớp/DOCX, dùng đúng những hàm hiện có khi giải thích activity.

Hình chính lược các hàm hỗ trợ kỹ thuật, getter, lock, SQL và nhánh HTTP; đọc cùng bảng này để đối chiếu toàn bộ các đường ra của 21 phương thức. Tính đúng ở runtime, JSP compile, rollback thực tế, tranh chấp transaction và luồng trình duyệt vẫn cần kiểm thử riêng; rà soát tĩnh không thay thế các kiểm thử đó.

## 7. Kiểm tra sản phẩm thiết kế

Đã tái sinh SVG/PNG/PDF/drawio từ cùng một renderer, xem PNG, kiểm tra XML/UTF-8, kiểm tra mọi cạnh dùng ID hợp lệ, đường nối ngang/dọc, mọi nhãn click đều xuất phát từ Hiển thị GD. Đã đối chiếu trực tiếp các tên hàm trong ô DAO với source. Không sửa Java, JSP, JS, SQL hoặc DOCX/PDF nguồn trong lần rà soát này.
