# Hợp đồng dữ liệu và luồng web — chỉ hai module đề 26

**Áp dụng [00_PHAM_VI.md](00_PHAM_VI.md): đúng 9 bảng và 10 model cần cho hai module. Đây là tập con của báo cáo gốc; không sinh DDL/POJO cho các phần còn lại.**

Đọc bổ sung 09 về nguồn/tên file giao diện và 10 về helper, cấu hình, lỗi, version nháp và đồng thời. Các POST sửa draft có thêm `draftVersion`, GET draft/parts/new trả version; các trường kỹ thuật này không thêm vào bảng hoặc thực thể. Không dùng trạng thái/mã/tiền trong sessionStorage của prototype làm dữ liệu app.

Giao diện/luồng theo11. Để giữ select Loại thông tin của prototype, search/detail nhận thêm param UI `itemTypeFilter=all|service|part`, mặc định all; ngoài miền trả400. itemTypeFilter không phải field model/SQL, không thay tìm tên và khác `itemType` định danh chi tiết. Form và link chi tiết/quay lại giữ itemTypeFilter; render hai list trong một bảng chung.

Áp dụng cùng 02/07. Giữ tên/thuộc tính/quan hệ cần cho hai module từ bài làm: tồn lưu trực tiếp, hóa đơn nhập có một người lập và người đó ghi nhận trả tiền. Không thêm quan hệ người thanh toán riêng. File 05 chỉ kiểm tra 5 trường nhận diện/chống lặp của nhập hàng đã có trong bài làm.

## 1. Quy ước dữ liệu

- Database: `garaman4`; database test: `garaman4_test`. Engine InnoDB, charset utf8mb4, collation `utf8mb4_0900_ai_ci` cho tìm tên không phân biệt hoa/thường và dấu. Đây là lựa chọn triển khai thêm, không phải yêu cầu đề bắt buộc tìm không dấu.
- Tên bảng/cột giữ nguyên camelCase trong hình CSDL hiện có của DOCX (đang mang số Hình 2.2); không đổi sang snake_case, không bỏ hậu tố 4. MySQL trên Linux phân biệt tên bảng tùy cấu hình, nên mọi SQL phải dùng đúng chữ hoa/thường.
- `id` thường: `INT PRIMARY KEY AUTO_INCREMENT`. Cột FK `INT`; bảng kế thừa dùng FK cha làm PK, không có id tự sinh khác.
- Tiền: `DECIMAL(18,2)` ↔ `BigDecimal`. Lượng: `DECIMAL(12,3)` ↔ `BigDecimal`. Số lượt: `INT`. Mọi thời điểm giao dịch: `DATETIME` ↔ `LocalDateTime`; ngày sinh: `DATE` ↔ `LocalDate`.
- Thời điểm lấy ở server theo `Asia/Ho_Chi_Minh`; Java/DB/session thống nhất múi giờ, không dùng giờ máy trình duyệt. Tiền hiển thị đồng Việt Nam; tính từng dòng bằng BigDecimal, làm tròn HALF_UP tới 2 chữ số rồi cộng tổng.
- Các kiểu ở file này là kiểu triển khai MySQL/Java. Hình CSDL vẽ bằng Paradigm được giữ nhãn `date` khi danh sách kiểu không có datetime, kèm ghi chú ánh xạ trong file 01. Không coi nhãn đó là lỗi dữ liệu; nếu sinh DDL thì kiểm tra/sửa thành DATETIME cho cột thời điểm trước khi chạy. Không đổi hợp đồng SQL thành DATE chỉ để khớp nhãn công cụ.
- Chuỗi ngắn: `VARCHAR(255)` nếu không chỉ rõ khác; mô tả dài/nội dung/thông số/ghi chú/điều kiện bảo hành/địa chỉ dùng `TEXT` khi ghi dưới đây. Các FK không ON DELETE CASCADE tới chứng từ; dùng RESTRICT. Chưa xây endpoint xóa chứng từ đã xác nhận.
- `NN` = NOT NULL; `N` = cho phép NULL. Chuỗi tùy chọn trống chuẩn hóa thành NULL. Các trường bắt buộc phải trim và không rỗng, không chỉ kiểm tra NOT NULL.
- Field Java ứng với cột SQL cùng tên, trừ các FK được biểu diễn bằng object như phần 3. Không tạo thêm field id cha ở các lớp kế thừa.
- Mã hiển thị là prefix + id số: DV1, PT1, NCC1, HN1, KH1; không lưu thêm cột mã rời và không dùng chuỗi mã thay cho id FK.

## 2. Trạng thái và quyền

| Đối tượng | Giá trị được lưu |
|---|---|
| Member.role | `Customer`, `WarehouseStaff` trong schema tập con của app |
| Member.accountStatus | `Hoạt động`, `Đã khóa`; chỉ NULL khi khách chưa có tài khoản |
| Employee.position | `Kho`, tương ứng WarehouseStaff trong app hai module |
| Employee.employmentStatus | `Đang làm việc`, `Ngừng làm việc` |
| Supplier.status | `Đang hoạt động`, `Ngừng hoạt động` |
| SparePartCategory.status | `Đang sử dụng`, `Ngừng sử dụng` |
| Service.status | `Đang cung cấp`, `Ngừng cung cấp` |
| SparePart.status | `Chưa công bố`, `Đang kinh doanh`, `Ngừng kinh doanh` |
| PurchaseInvoice.paymentStatus | `Chưa thanh toán`, `Đã thanh toán` |

`Bản nháp` chỉ là trạng thái của object hóa đơn trong session trước lưu; không được INSERT giá trị này vào bảng hóa đơn. Bảng hóa đơn nhập chỉ lưu bản đã xác nhận. `paidAt` NULL khi chưa trả, khác NULL khi đã trả. CHECK ở hóa đơn nhập: `(paymentStatus = 'Chưa thanh toán' AND paidAt IS NULL) OR (paymentStatus = 'Đã thanh toán' AND paidAt IS NOT NULL)`. Chỉ có một FK nhân viên lập; chính người lập ghi nhận trả tiền, không thêm FK người thanh toán.

SQL dùng CHECK cho miền trạng thái. Ràng buộc role–position nằm ở hai bảng nên kiểm tra ở DAO/Filter và seed; không giả rằng FK đã bảo đảm. Member không được đồng thời có subtype Employee và Customer. Đợt hiện tại không có màn hình cấp tài khoản; seed phải bảo đảm các quy tắc này.

Tìm khách: chỉ Service đang cung cấp và SparePart đang kinh doanh; không lọc part theo tồn > 0 vì đề không quy định phải còn hàng mới xem được. Tìm kho: part `Chưa công bố` hoặc `Đang kinh doanh`, loại `Ngừng kinh doanh`; nhà cung cấp đang hoạt động. Nhóm ngừng sử dụng không cho chọn cho part mới; part cũ vẫn giữ liên kết nhóm và đọc được tên nhóm, không tự ẩn part chỉ vì nhóm bị ngừng.

## 3. Từ điển bảng và ánh xạ model

### 3.1. `tblMember4`

| Cột | Kiểu/ràng buộc |
|---|---|
| id | INT PK AI |
| fullName | VARCHAR(255) NN |
| username | VARCHAR(100) N UNIQUE |
| password | VARCHAR(255) N; lưu chuỗi hash tự mô tả |
| dateOfBirth | DATE N |
| address | TEXT N |
| email | VARCHAR(255) N |
| phoneNumber | VARCHAR(20) NN |
| role | VARCHAR(30) NN CHECK miền vai trò |
| accountStatus | VARCHAR(30) N CHECK miền trạng thái |

CHECK: hoặc cả username/password/trạng thái đều NULL và role=Customer, hoặc cả ba có giá trị hợp lệ. Các Employee sử dụng hệ thống phải có tài khoản. `Member4` là abstract, khi map chỉ tạo Customer4 hoặc WarehouseStaff4; không tạo subclass cho vai trò khác. Các giá trị role/position ở app là tập con dùng để demo hai module, không sửa mô hình vai trò tổng thể trong Word.

### 3.2. `tblEmployee4`

| Cột | Kiểu/ràng buộc |
|---|---|
| tblMember4id | INT PK, FK → tblMember4.id |
| position | VARCHAR(30) NN |
| employmentStatus | VARCHAR(30) NN |

`Employee4 extends Member4`, thêm hai thuộc tính; WarehouseStaff4 extends Employee4, không có bảng con vai trò riêng. Không tạo subclass khác trong app.

### 3.3. `tblCustomer4`

`tblMember4id INT PRIMARY KEY`, FK → tblMember4.id. Không có cột khác. `Customer4 extends Member4`; dùng cho đăng nhập khách ở module tra cứu.

Giữ bảng Customer dù chỉ có khóa để biểu diễn subtype đúng mô hình đã làm; không suy ra cần tạo thêm bảng nghiệp vụ khách hàng khác.

### 3.4. `tblSupplier4`

`id INT PK AI`, `name VARCHAR(255) NN`, `address TEXT NN`, `phoneNumber VARCHAR(20) NN`, `email VARCHAR(255) N`, `status VARCHAR(30) NN DEFAULT 'Đang hoạt động'`.

Không tự UNIQUE tên hoặc điện thoại: báo cáo chưa đặt quy tắc đó và các tên có thể giống nhau. Người nhập đối chiếu tên/địa chỉ/điện thoại để chọn. Tạo index tên nếu cần nhưng tìm chứa `%từ khóa%` không được quảng bá là truy vấn dùng index hiệu quả trong mọi trường hợp.

### 3.5. `tblSparePartCategory4`

`id INT PK AI`, `name VARCHAR(255) NN UNIQUE`, `description TEXT N`, `status VARCHAR(30) NN DEFAULT 'Đang sử dụng'`.

Tên nhóm duy nhất theo collation đã chọn; nhân viên kho chỉ chọn nhóm sẵn có, không tạo nhóm bằng nhập tên tự do.

### 3.6. `tblService4`

| Cột | Kiểu/ràng buộc |
|---|---|
| id | INT PK AI |
| name | VARCHAR(255) NN |
| category | VARCHAR(255) NN; vẫn là chuỗi như mô hình, chưa tách bảng nhóm dịch vụ |
| description | TEXT NN |
| content | TEXT NN |
| imagePath | VARCHAR(255) N |
| unitPrice | DECIMAL(18,2) NN CHECK >= 0 |
| estimatedDuration | INT NN CHECK > 0; đơn vị phút |
| warrantyPeriod | VARCHAR(255) N, ví dụ “6 tháng”; không tự đổi thành số tháng |
| warrantyTerms | TEXT N |
| status | VARCHAR(30) NN |

Giá 0 có thể tồn tại trong danh mục; giá nhập ở hóa đơn nhập vẫn phải > 0. Mô hình chưa có thuế/giảm giá nên không tự thêm. Không tách Service thành bảng con của một Product chung với SparePart.

### 3.7. `tblSparePart4`

| Cột | Kiểu/ràng buộc |
|---|---|
| id | INT PK AI |
| name | VARCHAR(255) NN |
| description | TEXT N |
| imagePath | VARCHAR(255) N |
| unit | VARCHAR(20) NN CHECK IN ('Cái','Chiếc','Bộ','Lít') |
| manufacturer | VARCHAR(255) NN |
| specifications | TEXT N |
| compatibleVehicleModels | TEXT N |
| sellingPrice | DECIMAL(18,2) N CHECK >= 0 khi không NULL |
| warrantyPeriod | VARCHAR(255) N |
| warrantyTerms | TEXT N |
| status | VARCHAR(30) NN DEFAULT 'Chưa công bố' |
| stockQuantity | DECIMAL(12,3) NN DEFAULT 0 CHECK >= 0; lưu trực tiếp |
| tblSparePartCategory4id | INT NN FK → tblSparePartCategory4.id |

Java: `category:SparePartCategory4` thay FK và `stockQuantity:BigDecimal` ánh xạ cột tồn đã lưu. `addSparePart` luôn khởi tạo tồn bằng 0 ở server; không nhận tồn từ form. Phụ tùng nhập mới tối thiểu cần tên, nhóm đang sử dụng, đơn vị và hãng để phân biệt; giá bán NULL được nếu `Chưa công bố`. Thiếu giá hoặc mô tả thì không được chọn `Đang kinh doanh`. Ảnh/thông số/dòng xe/bảo hành là tùy chọn, có thể hiển thị “Chưa cập nhật”/“Không có thông tin bảo hành” thay vì bịa thông tin.

Kho được chọn `Chưa công bố` hoặc `Đang kinh doanh` khi thêm mới; chọn công bố phải qua validate tối thiểu giá bán và mô tả. Không cho kho dùng form thêm mới để sửa tồn hoặc đổi part cũ thành ngừng kinh doanh. CHECK bổ sung `status <> 'Đang kinh doanh' OR (sellingPrice IS NOT NULL AND description IS NOT NULL AND CHAR_LENGTH(TRIM(description)) > 0)`.

### 3.8. `tblPurchaseInvoice4`

| Cột | Kiểu/ràng buộc |
|---|---|
| id | INT PK AI |
| purchaseDate | DATETIME NN; thời điểm xác nhận nhập, đồng thời là thời điểm nghiệp vụ nhập trong baseline |
| confirmedAt | DATETIME NN |
| paymentStatus | VARCHAR(30) NN DEFAULT 'Chưa thanh toán' |
| paidAt | DATETIME N |
| tblSupplier4id | INT NN FK → tblSupplier4.id |
| tblEmployee4id | INT NN FK → tblEmployee4.tblMember4id |
| requestCode **[bổ sung]** | CHAR(36) NN UNIQUE; UUID sinh ở server cho một lần nhập |
| supplierNameSnapshot **[bổ sung]** | VARCHAR(255) NN |
| warehouseStaffNameSnapshot **[bổ sung]** | VARCHAR(255) NN |

Java: `supplier:Supplier4`, `warehouseStaff:WarehouseStaff4`, `invoiceDetails:List<PurchaseInvoiceDetail4>`, `totalAmount:BigDecimal` dẫn xuất. Không có cột totalAmount. Khi xác nhận lấy tên NCC và tên nhân viên từ DB trong transaction để chốt; không lấy hidden input. Phần đã xác nhận và bản in không hiển thị địa chỉ/điện thoại NCC hoặc hãng phụ tùng; các thông tin đó chỉ dùng ở màn hình tìm/chọn và bản nháp.

`purchaseDate` và `confirmedAt` đang cùng giá trị trong phạm vi này; giữ hai trường vì báo cáo đã có. Không tự thêm nhập lùi ngày, tách ngày nhà cung cấp xuất hàng hay số hóa đơn thuế.

### 3.9. `tblPurchaseInvoiceDetail4`

| Cột | Kiểu/ràng buộc |
|---|---|
| id | INT PK AI |
| quantity | DECIMAL(12,3) NN CHECK > 0 |
| purchaseUnitPrice | DECIMAL(18,2) NN CHECK > 0 |
| tblPurchaseInvoice4id | INT NN FK → tblPurchaseInvoice4.id |
| tblSparePart4id | INT NN FK → tblSparePart4.id |
| sparePartNameSnapshot **[bổ sung]** | VARCHAR(255) NN |
| unitSnapshot **[bổ sung]** | VARCHAR(20) NN |

UNIQUE `(tblPurchaseInvoice4id,tblSparePart4id)`. Java thêm `sparePart:SparePart4`, `lineAmount:BigDecimal` dẫn xuất; không có cột lineAmount. Chính thức chỉ có lượng/giá >0, dù object draft có thể tạm bằng 0.

### Phạm vi thông tin được chốt

- Hóa đơn nhập: mã, ngày nhập, thời điểm xác nhận, các id liên kết, tên NCC/tên nhân viên áp dụng; từng dòng giữ tên/đơn vị áp dụng, số lượng và đơn giá nhập.
- Thành tiền/tổng tiền tính từ dòng đã lưu. Trạng thái và thời điểm thanh toán được cập nhật sau xác nhận, giữ nguyên người lập; bản in hiển thị trạng thái thanh toán hiện tại. Không cam kết các bản in trước/sau thanh toán có cùng trạng thái.
- Không lấy tên nhận diện trên chứng từ đã xác nhận bằng JOIN tên hiện tại. Object master chỉ phục vụ liên kết và màn hình danh mục; bản in dùng đúng các trường áp dụng đã chốt.

### Tổng kết schema vật lí

**9 bảng** như trên, đúng danh sách 00. Không tạo bảng ngoài phạm vi, bảng vai trò con, bảng nháp hoặc Payment. Không tạo view tồn để thay cột `stockQuantity`. Bảng phụ tùng lưu tồn; `lineAmount` và `totalAmount` không lưu thành cột.

### Tồn kho và transaction đã chốt

- Xác nhận nhập: INSERT header, INSERT toàn bộ chi tiết, cộng `stockQuantity` của từng phụ tùng **cùng một Connection/transaction**. Lỗi bất kì bước nào rollback cả chứng từ, dòng và tồn.
- `SparePartDAO4.increaseStockAfterPurchase(Connection,int,BigDecimal)` chạy `UPDATE tblSparePart4 SET stockQuantity = stockQuantity + ? WHERE id = ?`; kiểm tra đúng 1 hàng, lượng dương, không vượt DECIMAL(12,3). Hàm không mở/commit/đóng Connection được truyền vào.
- Trước khi ghi, khóa nhà cung cấp, nhân viên và các phụ tùng cần dùng; khóa theo thứ tự Supplier → Employee → SparePart, các id phụ tùng tăng dần. Đọc lại trạng thái/đơn vị/tồn trên hàng đã khóa; các giao dịch nhập dùng cùng thứ tự.
- Hai lần nhập khác mã yêu cầu cùng một part đều được cộng. Hai request cùng mã yêu cầu chỉ được cộng một lần nhờ UNIQUE và rollback; không dựa riêng vào khóa session hay nút bị disable.
- In, đọc lại và ghi nhận thanh toán không cập nhật tồn. Bản nháp không cập nhật tồn. Phụ tùng mới có tồn 0. Seed có hàng phải có chứng từ và tồn tương ứng, không tạo tồn vô căn cứ.
- `03_verify.sql` trên database riêng của hai module: tồn ban đầu bằng 0, đối chiếu stockQuantity với SUM(quantity) của các hóa đơn nhập đã xác nhận theo từng part. Không có nghiệp vụ xuất trong app; không JOIN các bảng ngoài 9 bảng để tính nhập trừ xuất. Đây là kiểm tra nhất quán, màn hình vẫn đọc cột stockQuantity. Không chạy giả định này lên database nghiệp vụ khác có tồn đầu kỳ hoặc xuất kho.

Tổng tiền nhập = tổng ROUND(quantity * purchaseUnitPrice,2) từng dòng; Java setScale(2,HALF_UP) khớp SQL. Giá nhập lấy theo dòng đã lưu, không dùng giá bán danh mục để tính hóa đơn nhập.

## 4. URL, HTTP method và Servlet method

`{ctx}` thường là `/garaman`; mọi URL sau bỏ prefix này để dễ đọc. Chỉ đăng ký chính xác các mapping dưới đây; action không biết trả 400, HTTP method không được hỗ trợ trả 405. GET chỉ đọc; POST thay đổi dữ liệu/nháp/thanh toán.

| HTTP + URL | Controller/hàm | Input chính | Kết quả |
|---|---|---|---|
| GET `/login` | Auth.showLogin | — | JSP login, tạo token nếu chưa có |
| POST `/login` | Auth.login | username, password, csrfToken | Thành công redirect `/home`, sai render login lỗi |
| POST `/logout` | Auth.logout | csrfToken | invalidate session, redirect login |
| GET `/home` | Home.showHome | — | JSP đúng vai trò |
| GET `/customer/catalog` | Catalog.showSearch/searchCatalog | keyword tùy chọn, itemTypeFilter mặc định all | Không có keyword: chưa tìm; có keyword: validate và tìm theo tên rồi áp bộ lọc trình bày trong một bảng |
| GET `/customer/catalog/detail` | Catalog.viewDetails | id, itemType, keyword, itemTypeFilter | Chi tiết hoặc404; giữ từ khóa/bộ lọc khi quay lại |
| GET `/warehouse/suppliers` | Supplier.showSupplierSelection/searchSuppliers | keyword, selectedId tùy chọn | Trang chọn NCC; selectedId chỉ dùng làm nổi bật NCC vừa thêm |
| GET `/warehouse/suppliers/new` | Supplier.showNewSupplier | keyword tùy chọn | Form thêm |
| POST `/warehouse/suppliers/create` | Supplier.addSupplier | name,address,phoneNumber,email,csrfToken | Lưu, redirect danh sách với từ khóa tên mới + selectedId |
| POST `/warehouse/purchases/start` | Purchase.startPurchase | supplierId,csrfToken | Tạo draft, redirect draft GET |
| GET `/warehouse/purchases/draft` | Purchase.showDraft | requestCode,keyword,selectedPartId,focusPartId tùy chọn | Render draft + kết quả tìm; selectedPartId chỉ chọn sẵn, chưa tự thêm dòng |
| POST `/warehouse/purchases/search-part` | Purchase.searchSpareParts | requestCode,keyword, toàn bộ dòng,csrfToken | Giữ dòng, redirect GET draft và tìm từ khóa |
| POST `/warehouse/purchases/add-line` | Purchase.addInvoiceLine | requestCode,selectedPartId, toàn bộ dòng,csrfToken | Thêm dòng 0 hoặc focus dòng cũ, redirect draft |
| POST `/warehouse/purchases/update` | Purchase.updateInvoiceLines | requestCode, toàn bộ dòng,csrfToken,destination? | Cập nhật; nếu destination=new-part thì redirect form thêm part |
| POST `/warehouse/purchases/remove-line` | Purchase.removeInvoiceLine | requestCode,removePartId, toàn bộ dòng,csrfToken | Bỏ dòng được chọn, giữ dòng khác, redirect draft |
| POST `/warehouse/purchases/cancel` | Purchase.cancelDraft | requestCode,csrfToken | Xóa draft, redirect home kho |
| POST `/warehouse/purchases/confirm` | Purchase.confirmInvoice | requestCode, toàn bộ dòng,csrfToken | Lưu chính thức hoặc mở hóa đơn cũ; redirect view |
| GET `/warehouse/parts/new` | SparePart.showNewSparePart | requestCode | Form thêm, danh sách nhóm |
| POST `/warehouse/parts/create` | SparePart.addSparePart | requestCode, dữ liệu part,csrfToken | Tạo part, redirect draft có selectedPartId |
| GET `/warehouse/purchases/view` | Purchase.viewInvoice | id | Hóa đơn đã xác nhận |
| GET `/warehouse/purchases/print` | Purchase.printInvoice | id | Bản in dữ liệu hóa đơn cũ |
| POST `/warehouse/purchases/pay` | Purchase.recordPayment | id,amountPaid,csrfToken | Ghi nhận đúng tổng hoặc đã trả, redirect view |
| GET `/warehouse/purchases/unpaid` | Purchase.showUnpaidInvoices | — | Trang chủ kho kèm các hóa đơn chưa trả do chính người đang đăng nhập lập |

Không thêm `action` không được liệt kê để làm route khác. Tên class đầy đủ là các tên kết thúc `Servlet4` trong kế hoạch; cột trên viết ngắn để dễ đọc. `HomeServlet4` map `/home`; các Servlet khác nhận nhiều path chính xác trong web.xml.

Giá trị nhập số ở request dùng biểu diễn chuẩn như `80000` hoặc `80000.00`, không gửi `80.000` để biểu diễn tám mươi nghìn. Hiển thị định dạng Việt Nam tách riêng với value input; ví dụ bài báo cáo “80.000 đồng” là định dạng trình bày.

## 5. Tên param, request attributes, session

### Request param của part mới

`name`, `categoryId`, `description`, `imagePath`, `unit`, `manufacturer`, `specifications`, `compatibleVehicleModels`, `sellingPrice`, `warrantyPeriod`, `warrantyTerms`, `status`, `requestCode`, `csrfToken`.

Chỉ nhận danh sách này; `stockQuantity`, `idNhanVien`, `totalAmount`, `lineAmount`, `confirmedAt` không phải input được phép. Giá nhập không có trong form danh mục.

### Dòng hóa đơn nháp

- Dùng `partId` lặp lại cho từng dòng, `soLuong_<partId>`, `donGiaNhap_<partId>`; partId trong draft là duy nhất.
- Các giá trị nhập gửi đầy đủ khi tìm/chọn/xóa/cập nhật/xác nhận. Server chỉ cập nhật id đã có trong draft; muốn thêm phải qua add-line và đọc lại part từ DB.
- Đối với nút chọn part và nút xóa, dùng tên `selectedPartId` và `removePartId` để không nhầm với mảng `partId` của các dòng. Controller chuẩn hóa chúng thành id phụ tùng của hành động tương ứng.
- Một input parse lỗi được giữ dưới `formValues:Map<String,String>` ở request hoặc một mục form lỗi gắn draft trong session; không dùng giá trị cũ để xác nhận thay cho input lỗi. Baseline giữ `draftFormValues4:Map<String,Map<String,String>>` theo requestCode để không mất dữ liệu qua redirect.
- Form lỗi của bản nháp được cập nhật/xóa cùng draft và dưới cùng khóa session; confirm phải từ chối khi draft còn lỗi. Không để lỗi từ hai tab ghi đè nhau.
- Một form hóa đơn có thể dùng các nút `formaction` để tìm/chọn/cập nhật/xóa/xác nhận với cùng toàn bộ input; không tạo form lồng trong form. Khi xóa một dòng lỗi, chỉ bỏ lỗi của dòng đó; các dòng khác vẫn phải giữ nguyên input và lỗi.

### Request attributes

| JSP | Thuộc tính |
|---|---|
| Tất cả | `currentUser:SessionUser4`, `csrfToken:String` (lấy từ session), `errorMessage:String`, `fieldErrors:Map<String,String>`, `flashMessage:String`; thuộc tính không dùng có thể vắng |
| Login | `username:String`; không truyền password trở lại |
| Search | `keyword:String`, `itemTypeFilter:String`, `hasSearched:boolean`, `services:List<Service4>`, `spareParts:List<SparePart4>` |
| Detail | `itemType:String`, `service:Service4` hoặc `sparePart:SparePart4`, `keyword:String`, `itemTypeFilter:String` |
| Chọn NCC | `keyword:String`, `suppliers:List<Supplier4>`, `selectedId:Integer` |
| Thêm NCC | `formValues:Map<String,String>` |
| Lập hóa đơn | `purchaseInvoice:PurchaseInvoice4`, `requestCode:String`, `keyword:String`, `spareParts:List<SparePart4>`, `formValues`, `selectedPartId:Integer`, `focusPartId:Integer` |
| Thêm part | `requestCode:String`, `categories:List<SparePartCategory4>`, `formValues` |
| Hóa đơn/bản in | `purchaseInvoice:PurchaseInvoice4`, `canRecordPayment:boolean`; render các trường `...ApDung`; nút trả tiền chỉ bật khi chưa trả và người hiện tại là người lập |
| Home kho khi xem unpaid | `dsHoaDonChuaThanhToan:List<PurchaseInvoice4>` chỉ của người đang đăng nhập |

Search hiển thị hai list trong một bảng, có cột Loại; itemTypeFilter=all cho cả hai, service/part chỉ lọc các dòng hiển thị theo11. Không tạo CatalogItem4 hoặc bảng gộp mới. Thứ tự dịch vụ theo tên/id rồi part theo tên/id. `itemType` chi tiết chỉ chấp nhận service/part; itemTypeFilter chỉ all/service/part.

### Session attributes

| Khóa | Kiểu/ý nghĩa |
|---|---|
| `currentUser4` | SessionUser4; không lưu object có hash mật khẩu |
| `csrfToken4` | Token ngẫu nhiên chống request giả, khác mã lần nhập |
| `purchaseDrafts4` | Map<String,PurchaseInvoice4>, khóa là UUID requestCode |
| `draftFormValues4` | Map<String,Map<String,String>> giữ input dòng chưa hợp lệ theo draft |
| `draftVersions4` | Map<String,Long>, phiên bản từng draft để phát hiện form cũ |
| `draftConfirming4` | Set<String>, UUID đang xác nhận; sửa/hủy đồng thời bị409 theo 10 |
| `flashMessage4` | String, đọc một lần sau redirect |

Không cần lưu kết quả tìm kiếm chung trong session: giữ từ khóa ở URL GET để tìm lại khi quay về. Logout invalidate toàn session, các draft chưa xác nhận mất; hóa đơn đã lưu mở lại được từ DB. Không cam kết phục hồi draft sau đóng trình duyệt/timeout.

CSRF: cả login và các POST khác đều bắt buộc. GET login tạo session/token, POST login verify trước khi xác thực, sau login đổi session id/tạo phiên mới và cấp token mới. AuthFilter không được chặn tạo token ở GET login vì chưa có session.

## 6. Tìm kiếm, validate và lỗi

- Từ khóa trim, bắt buộc 1–255 ký tự khi đã submit. Tìm `name LIKE ? ESCAPE '!'`; escape `!` thành `!!`, `%` thành `!%`, `_` thành `!_`, sau đó thêm `%` hai bên. SQL luôn PreparedStatement. Không dùng tên bảng/cột lấy từ request.
- Danh sách rỗng khác với SQL exception. SQL lỗi hiển thị lỗi hệ thống và log phía server, không báo “không tìm thấy”.
- id/itemType sai định dạng: 400. Chưa login: redirect login. Sai quyền/CSRF: 403. Không có dữ liệu hoặc mục không được công bố cho khách: 404. Sự cố DB: 500. Validation form: render form kèm lỗi (có thể HTTP 400), giữ dữ liệu nhập phù hợp.
- Dữ liệu tiền nhập dạng thường, không exponent, NaN, Infinity, phân cách nghìn hay dấu phẩy. Không âm. Vượt scale/precision thì báo lỗi, không cắt ngầm. Kiểm tra precision số lượng và đơn giá, cả giá trị thành tiền/tổng không được vượt giới hạn tiền hỗ trợ.
- Lượng Cái/Chiếc/Bộ phải nguyên; Lít tối đa 3 chữ số thập phân. Không đổi đơn vị của part đã có giao dịch sang đơn vị khác; chưa triển khai màn hình sửa nhưng seed/verify phải nhất quán.
- Điện thoại giữ dạng String, 8–15 chữ số, có thể có dấu `+` ở đầu; loại khoảng trắng/gạch nối khi validate theo quy tắc được ghi trong README. Email tùy chọn, tối đa 255, kiểm tra định dạng cơ bản.
- Mọi field TEXT giới hạn input 5000 ký tự ở server; field VARCHAR theo độ dài cột. Không cho nhập URL ảnh bên ngoài, đường dẫn `..`, javascript: hoặc HTML vào đường dẫn hình.
- Dữ liệu form đơn giản lưu plain text, HTML escape khi render. Không render tùy ý mô tả dưới dạng HTML chưa kiểm soát.

## 7. Seed và kịch bản minh họa thống nhất

- Customer `khach26`, kho `kho26`; mật khẩu demo được chọn và ghi README, SQL chỉ chứa hash thật. Không có credential thật của người dùng. Vai trò, Employee và Customer subtype đầy đủ.
- Nhóm “Phụ tùng động cơ” đang dùng; một nhóm ngừng dùng để kiểm tra không xuất hiện khi thêm mới.
- DV1: Thay lọc dầu động cơ, nhóm Bảo dưỡng, giá 120000.00, thời gian 30 phút; mô tả tiền công chưa bao gồm part.
- PT1: Lọc dầu động cơ AF-01, Cái, AutoFilter, giá bán 150000.00, mô tả/thông số như kịch bản báo cáo, bảo hành 6 tháng và điều kiện như bản gốc, dòng xe chưa cập nhật được. Đang kinh doanh.
- NCC1: Hòa Phát, Hà Nội, 0900000001; email tùy chọn.
- Thêm ít nhất một dịch vụ ngừng cung cấp, một part chưa công bố, một part ngừng kinh doanh, một part đơn vị Lít để kiểm thử.
- Seed PT1 tồn 0 trước demo. Thực hiện nhập 10 × 80000.00 thì hóa đơn đầu tiên có thể là HN1, tổng 800000.00, tồn 10. Nếu seed tạo giao dịch cho part khác thì dùng id tường minh tránh chiếm HN1; hoặc README không cam kết id hóa đơn luôn bằng 1 sau nhiều lượt test.
- Không hardcode DV1/PT1/NCC1 trong Servlet; các id seed chỉ giúp tái hiện minh họa.
- Bản in là chứng từ nhập nội bộ theo đề. Không tự dựng mã số thuế, hóa đơn điện tử hay thông tin pháp nhân không có trong tài liệu. Không cần khẳng định phần mềm biết nhà cung cấp đã nhận tiền; nhân viên xác nhận thao tác sau thực tế trả đủ.

## 8. Ma trận chuyển từ phân tích bản gốc sang thiết kế đã chốt

| Pha phân tích trong báo cáo | Thiết kế triển khai |
|---|---|
| Service4.findServicesByName | ServiceDAO4.findServicesByName(String) |
| Service4.getServiceDetails | ServiceDAO4.getServiceDetails(int) |
| SparePart4.findSparePartsByName | SparePartDAO4.findSparePartsByName(String,boolean) |
| SparePart4.getSparePartDetails | SparePartDAO4.getSparePartDetails(int,boolean) |
| SparePart4.addSparePart | SparePartDAO4.addSparePart(SparePart4) |
| SparePart4.increaseStockAfterPurchase | SparePartDAO4.increaseStockAfterPurchase(Connection,int,BigDecimal), được PurchaseInvoiceDAO4 gọi trong cùng transaction xác nhận |
| Supplier4.findSuppliersByName | SupplierDAO4.findSuppliersByName(String) |
| Supplier4.addSupplier | SupplierDAO4.addSupplier(Supplier4) |
| Supplier4.getSupplierDetails | SupplierDAO4.getSupplierDetails(int) |
| WarehouseStaff4.getWarehouseStaffDetails | WarehouseStaffDAO4.getWarehouseStaffDetails(int) |
| SparePartCategory4.getActiveCategories | SparePartCategoryDAO4.getActiveCategories() |
| PurchaseInvoice4.calculateTotalAmount | Giữ tại model PurchaseInvoice4.calculateTotalAmount() |
| PurchaseInvoice4.savePurchaseInvoice | PurchaseInvoiceDAO4.savePurchaseInvoice(PurchaseInvoice4) |
| PurchaseInvoice4.getPurchaseInvoiceDetails | PurchaseInvoiceDAO4.getPurchaseInvoiceDetails(int) |
| PurchaseInvoice4.recordPayment | PurchaseInvoiceDAO4.recordPayment(int,BigDecimal,int) |

Biên → JSP chính xác:

| Biên phân tích | JSP |
|---|---|
| CustomerHomeUI4 | customer/customerHome.jsp |
| CatalogSearchUI4 | customer/catalogSearch.jsp |
| ItemDetailsUI4 | customer/itemDetails.jsp |
| WarehouseHomeUI4 | warehouse/warehouseHome.jsp |
| SelectSupplierUI4 | warehouse/selectSupplier.jsp |
| NewSupplierUI4 | warehouse/newSupplier.jsp |
| PurchaseInvoiceUI4 | warehouse/purchaseInvoice.jsp |
| NewSparePartUI4 | warehouse/newSparePart.jsp |
| ConfirmedInvoiceUI4 | warehouse/confirmedInvoice.jsp |
| PrintInvoiceUI4 | warehouse/printInvoice.jsp |

Không tạo Java class `CustomerHomeUI4.java`, `PurchaseInvoiceUI4.java` để thay thế JSP trong project web. Trong UML có thể giữ tên biên cũ ở bảng ánh xạ; class diagram thiết kế dùng tên JSP/Servlet thực tế.

## 9. Người lập và mở lại hóa đơn nhập

1. Hóa đơn nhập có một người lập; chính người đó ghi nhận trả tiền NCC, không có FK người thanh toán riêng.
2. Danh sách chưa trả chỉ lấy chứng từ do người hiện tại lập. Kho đang hoạt động có thể đọc/in hóa đơn theo quyền, nhưng chỉ người lập được pay. Kiểm tra tại Servlet và DAO.
3. Pay khóa header, kiểm tra người lập/hoạt động, trạng thái và tổng từ dòng lịch sử; cập nhật trạng thái/thời điểm đúng một lần. Retry giữ mốc cũ, không cập nhật tồn.
4. Người lập ngừng làm việc không được đăng nhập/pay; chứng từ cũ vẫn đọc/in được bởi kho đang hoạt động, giữ tên snapshot. Không xây thêm bàn giao thanh toán hoặc quản trị nhân viên.
