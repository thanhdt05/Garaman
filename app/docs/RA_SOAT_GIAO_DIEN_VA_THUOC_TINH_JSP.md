# Rà soát giao diện và thuộc tính JSP trong DOCX

Đối chiếu trực tiếp `26_B23DCCN764.docx` ngày 28/10/2026. **Hai hình giao diện và các ô JSP trong biểu đồ lớp chưa khớp hoàn toàn.** Lệch rõ nhất ở `selectSupplier.jsp` và `purchaseInvoice.jsp`; form thêm phụ tùng đủ tên trường nhưng chưa thống nhất kiểu điều khiển và một định danh.

Đây là kết quả kiểm tra, chưa sửa hình, Word, mã nguồn hoặc bảng tên đã chốt.

## Căn cứ kiểm tra

- Hình 2.3: giao diện tra cứu, ảnh nhúng `word/media/image13.png`, kèm SVG `image14.svg`.
- Hình 2.4: lớp thiết kế tra cứu, ảnh nhúng `word/media/image15.png`.
- Hình 2.5: giao diện nhập phụ tùng, ảnh nhúng `word/media/image16.png`, kèm SVG `image17.svg`.
- Hình 2.6: lớp thiết kế nhập phụ tùng, ảnh nhúng `word/media/image18.png`.
- Bài giảng: mục 3.2.3, trang in 76–77 (PDF 77–78), mỗi thành phần nhận/hiển thị/gửi dữ liệu được đề xuất thành thuộc tính; mục 4.3.1, trang in 96 (PDF 97), thiết kế các thuộc tính đã phân tích và bổ sung dữ liệu ẩn cần trao đổi; Hình 4.4, trang in 99 (PDF 100).
- [Bảng tên đã xác nhận](TEN_CU_TEN_MOI.csv) chỉ dùng kiểm tra tên và kiểu. Các file `.puml` bên ngoài không thay thế bằng chứng ảnh trong DOCX: chúng có những dòng mà ảnh nhúng hiện tại chưa có.

SHA-256 của DOCX đã đọc: `d6d0f4b8af7c283686bcc54c3163d9d014976467b27312b58651e95b8740deda`. Văn bản/ảnh được trích vào `.review/report/`; đã xem trực tiếp cả bốn hình và các vùng JSP ở độ phân giải gốc. Không suy ra thuộc tính trong ảnh nhúng từ code hoặc PlantUML hiện có.

## Cách hiểu yêu cầu tương ứng

Ví dụ một giao diện có một bảng, bốn ô nhập độc lập và hai nút thì phải thể hiện đủ một thành phần Table, bốn thành phần nhập và hai thao tác. Đúng cả tên, vai trò và kiểu; tổng số bằng nhau chưa đủ nếu một nút bị thay bằng nút khác.

Tuy nhiên, tổng số dòng thuộc tính trong ô JSP có thể lớn hơn số điều khiển nhìn thấy. Bài giảng cho phép bổ sung đối tượng/danh sách phục vụ xử lí. Ví dụ `suppliers: Supplier4[]` chứa dữ liệu, còn `suppliersTable: Table` là thành phần hiển thị; hai dòng này có vai trò khác nhau. Hình 4.4 của bài giảng cũng có cả `listDangki` và `tblDangki`.

Đối với một bảng nhiều dòng, chỉ cần khai báo một loại thao tác Chọn/Xóa cho các dòng, không khai báo một thuộc tính nút cho từng bản ghi mẫu. Các cột thuộc bảng được mô tả qua cấu trúc dòng và model liên quan; không đếm mỗi ô dữ liệu mẫu thành một thành phần JSP độc lập.

Đối với các dòng thông tin chỉ đọc, mẫu bài giảng có thể dùng một đối tượng thực thể để cung cấp dữ liệu. Vì vậy `service: Service4`, `sparePart: SparePart4` hoặc `purchaseInvoice: PurchaseInvoice4` có thể bao phủ nhiều thông tin hiển thị. Cần chỉ được ánh xạ từng thông tin; không lấy cách gộp này để bỏ sót ô nhập, bảng hoặc thao tác độc lập.

Nếu tiêu chí riêng của giảng viên yêu cầu mỗi dòng output phải được khai báo riêng trên ô JSP, các ô chi tiết/hóa đơn hiện chưa thể hiện ở mức đó. Đó là tiêu chí chặt hơn cách gộp đối tượng đang dùng; cần áp dụng nhất quán, không vừa gộp một số màn vừa đếm tách các màn khác.

## Đối chiếu từng JSP

| JSP trong hình DOCX | Thành phần trên giao diện | Đối chiếu ô lớp trong DOCX | Kết luận |
|---|---|---|---|
| `customerHome.jsp` | Tên khách; liên kết Tìm dịch vụ/phụ tùng | `customer: Customer4`, `searchCatalogLink: link`; tên lấy từ đối tượng khách | Đủ ánh xạ chức năng; tên được gộp trong đối tượng. CSV ngoài dùng `fullName: String`, khác cách biểu diễn ảnh nhúng. |
| `catalogSearch.jsp` | 1 TextField, 1 Select, 1 bảng kết quả, Tìm kiếm và Xem chi tiết | Có `keyword`, `itemTypeFilter`, `resultsTable`, `searchButton`, `viewDetailsLink`; hai mảng `services`/`spareParts` cấp dữ liệu cho cùng bảng | Khớp các điều khiển độc lập. Hai dòng kết quả mẫu không đòi hai thuộc tính Xem chi tiết. |
| `itemDetails.jsp` | Hai trạng thái xem DV/PT; thông tin chi tiết, hình ảnh, Quay lại | `service`, `sparePart`, `backLink` | Đủ dữ liệu thông qua đối tượng và một thao tác Quay lại. Không phải thiếu hàng loạt TextField: các thông tin trên hình là output, không phải ô nhập. Hai khung chi tiết minh họa hai trạng thái của cùng JSP. |
| `warehouseHome.jsp` | Tên nhân viên; liên kết Nhập phụ tùng | `warehouseStaff: WarehouseStaff4`, `startPurchaseLink: link` | Đủ ánh xạ chức năng; tên nhân viên được gộp trong đối tượng. |
| `selectSupplier.jsp` | 1 ô tên NCC; 1 bảng; Tìm kiếm, Chọn theo dòng, Thêm mới, Quay lại | Có `keyword`, `searchButton`, `suppliers`, `selectSupplierButton`, `addSupplierLink`; không có Table hoặc Quay lại | **Thiếu `suppliersTable: Table` và thao tác Quay lại.** |
| `newSupplier.jsp` | 4 ô nhập: tên, địa chỉ, điện thoại, email; Lưu và Quay lại | Có đủ 4 TextField, `saveBtn: submit`, `backLink: link`; thêm `supplier: Supplier4` là đối tượng dữ liệu | Đủ số thành phần. Tên nút Lưu chưa theo CSV: ảnh ghi `saveBtn`, CSV chốt `saveButton`. |
| `purchaseInvoice.jsp` | 1 ô tìm; **2 bảng**; Tìm kiếm, Chọn PT, Thêm PT, Xóa dòng, Xác nhận, Hủy; lượng/giá nhập trong bảng dòng; thông tin đầu HĐ và tổng tiền | Có `invoiceDetailsTable`, `spareParts` và các thao tác khác; không có `sparePartsTable`, không có thao tác Xóa; có thêm `updateButton` | **Thiếu 1 Table và thao tác Xóa; thừa nút Cập nhật so với giao diện đã vẽ.** Các output đầu HĐ/tổng được cấp từ đối tượng. |
| `newSparePart.jsp` | 12 trường nhập/chọn; Lưu và Quay lại. Trên hình chỉ Nhóm và Trạng thái có mũi tên chọn | Có đủ 12 tên trường và 2 thao tác; nhưng `unit`/`imagePath` ghi Select còn hình vẽ là ô nhập thường; trường Thông số ghi `specification` | Đủ số trường; **chưa thống nhất kiểu Unit/Image và tên `specifications`**. Hai TextArea mô tả/điều kiện BH cũng chưa được phân biệt rõ trên hình. |
| `confirmedInvoice.jsp` | 1 bảng, 1 ô số tiền, In hóa đơn, Đã thanh toán; đầu HĐ/tổng/trạng thái là output | `invoiceDetailsTable`, `amountPaid`, `printInvoiceLink`, `markAsPaidButton`; `purchaseInvoice` cung cấp output | Khớp các điều khiển độc lập; output được gộp trong hóa đơn. |
| `printInvoice.jsp` | 1 bảng; In; Quay lại; đầu HĐ/tổng/trạng thái là output | Có `invoiceDetailsTable`, `backLink`, `purchaseInvoice`; dòng nút In hiển thị `printbtnLsubmit` | Có đủ vai trò điều khiển nhưng **dòng In thiếu cấu trúc tên/kiểu rõ ràng**, không theo tên `printButton: button` đã chốt. |

## Các chỗ cần chỉnh trước khi vẽ tiếp

### Hình 2.6 — selectSupplier.jsp

1. Bổ sung `suppliersTable: Table`, tên đã có trong CSV.
2. Bổ sung thuộc tính thao tác Quay lại. Có thể dùng lại quy ước `backLink: link` đang dùng ở các JSP khác; đây là đề xuất bổ sung cho trang này, chưa ghi vào CSV.
3. Giữ `suppliers: Supplier4[]`/`List<Supplier4>` làm dữ liệu của bảng; dòng dữ liệu này không thay thế dòng Table nếu áp dụng cùng ký pháp như `catalogSearch.jsp`.

### Hình 2.6 — purchaseInvoice.jsp

1. Bổ sung `sparePartsTable: Table` cho bảng kết quả tìm PT, tên đã có trong CSV.
2. Giữ `invoiceDetailsTable: Table` cho bảng các dòng nhập. Hai bảng khác nhau, không gộp thành một thuộc tính.
3. Bổ sung thao tác Xóa dòng. Model `PurchaseInvoiceDetail4.quantity` và `purchaseUnitPrice` cung cấp hai cột lượng/giá chỉnh sửa; cột Xóa còn cần thuộc tính thao tác tương ứng. CSV chưa có tên nút Xóa cho trang này, nên chưa tự chốt một tên tiếng Anh mới.
4. Xử lí `updateButton`: giao diện trong DOCX hiện không có nút Cập nhật. Nếu giữ nguyên giao diện báo cáo thì ô JSP không nên có nút này. Nếu muốn mô tả đúng giao diện code hiện có nút đó thì phải bổ sung nó vào hình giao diện. Kết quả rà soát chỉ xác định điểm lệch, chưa tự chọn thay đổi nghiệp vụ/hình.

### Form thêm phụ tùng và nút In

- `newSparePart.jsp` có đủ 12 trường, không cần thêm trường chỉ để tăng số dòng. Unit và Image phải thống nhất ô nhập thường hay Select ở cả hai hình. Theo CSV, `unit: Select` và `imagePath: Select`; nếu giữ thiết kế đã chốt này, hình form cần thể hiện hai danh sách chọn đó.
- `description: TextArea`, `warrantyTerms: TextArea`: hình form đang dùng cùng khung một dòng với TextField, nên hình chưa phân biệt rõ hai kiểu; nên thể hiện ô nhiều dòng khi chỉnh hình.
- Sửa `specification` thành `specifications`, đúng CSV/model/source; không tạo tên dịch mới.
- Sửa `saveBtn` ở `newSupplier.jsp` thành `saveButton: submit` theo CSV.
- Nút In của `printInvoice.jsp`: dùng `printButton: button` theo CSV; chức năng là mở in trình duyệt. Hình vẽ nút hình chữ nhật có thể đại diện cho button hoặc link, không tự suy ra phải là HTML submit chỉ từ hình dáng.

## Những output đã có dữ liệu, không kết luận thiếu chỉ từ số dòng

- Tên khách trên trang chính: `customer.fullName`; tên nhân viên: `warehouseStaff.fullName`.
- Chi tiết dịch vụ/phụ tùng: thuộc tính trong `service`/`sparePart`. Tên, nhóm, hình, giá, mô tả, thông số, bảo hành được đọc từ đối tượng đúng loại. Không yêu cầu hiển thị mọi thuộc tính của entity, ví dụ tồn kho nội bộ không cần xuất hiện trên giao diện khách.
- Ngày nhập, tổng, trạng thái: lấy từ `purchaseInvoice.purchaseDate`, tổng tiền tính qua `calculateTotalAmount()` và `purchaseInvoice.paymentStatus`.
- Trên chứng từ sau xác nhận, tên NCC/NV/PT và đơn vị dùng các giá trị chốt `supplierNameSnapshot`, `warehouseStaffNameSnapshot`, `sparePartNameSnapshot`, `unitSnapshot`.
- Hai ô nhập lượng/giá trong mỗi dòng bảng được ánh xạ tới `PurchaseInvoiceDetail4.quantity` và `purchaseUnitPrice`. Cần giữ cấu trúc bảng/dòng; không tạo thuộc tính riêng cho từng dòng dữ liệu mẫu.

Nếu cần biểu diễn output thành các trường JSP riêng theo yêu cầu riêng của giảng viên, phải bổ sung rõ kiểu hiển thị cho chúng. Không nên mặc định dùng `TextField` cho các nhãn chỉ đọc hoặc sao chép toàn bộ entity sang JSP.

## Ghi chú về chính bản DOCX

- Chú thích Hình 2.3 còn ghi “Thiết kế giao diện modul nhập điểm”, trong khi hình là tra cứu DV/PT; cần đổi đúng tên module.
- Một số tên/kiểu bị cắt ngay trong ô lớp, ví dụ `warehouseStaff: WarehouseSta...` và `phoneNumber: TextFi...`. Cần nới ô trong Visual Paradigm trước khi xuất lại; đây là chữ bị cắt trong hình nguồn, không phải chỉ do Word hiển thị nhỏ.
- File `purchase-design.puml` bên ngoài đã có `suppliersTable` và `sparePartsTable`, trong khi ảnh Hình 2.6 hiện chưa có. Sửa file ngoài hoặc đọc code không tự cập nhật ảnh nhúng trong DOCX.

**Kết luận:** thiết kế đã có phần lớn thành phần đúng, nhưng chưa thể xác nhận đồng bộ giao diện–JSP. Cần sửa các thiếu/thừa của hai màn chọn NCC/lập HĐ và thống nhất kiểu/tên ở form thêm PT/nút In. Tổng số dòng thuộc tính JSP không cần bằng số thành phần nhìn thấy nếu có đối tượng dữ liệu hỗ trợ; mọi thành phần độc lập trên hình phải có ánh xạ rõ ràng.
