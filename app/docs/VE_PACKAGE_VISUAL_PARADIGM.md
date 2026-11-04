# Vẽ package diagram trong Visual Paradigm

Đoạn thuyết minh để chèn vào bài nằm trong [THIET_KE_GOI.md](THIET_KE_GOI.md). Hình đã bổ sung nội dung: **23 lớp thực thể, 7 lớp DAO, 4 Servlet, 10 JSP**. Tên lấy từ `26_B23DCCN764.docx`, mục 2.1, 2.2 và 2.3.1; không tự đặt thêm lớp cho chức năng chưa thiết kế.

**Sửa ký hiệu:** các JSP, Servlet, DAO và model đều dùng **Class UML ba ngăn**, cùng loại phần tử với biểu đồ lớp thiết kế trong bài. Bản trước dùng Artifact gấp góc cho JSP và Class ẩn ngăn cho các lớp Java; bản hiện tại bỏ cách biểu diễn đó để bạn có thể vẽ theo bằng các khối Class quen thuộc trong Visual Paradigm.

| Thành phần trong hình | Phần tử Visual Paradigm |
|---|---|
| view, controller, dao, model và các gói con | **Package**, hình thư mục. |
| customerHome.jsp, catalogSearch.jsp và các JSP còn lại | **Class**, tên là tên trang JSP như hình thiết kế trong DOCX. |
| Các lớp DAO, Servlet, thực thể | **Class**. DAO, Member4, Employee4 đặt abstract. |
| Mũi tên giữa các gói | **Dependency**, nét đứt, đầu mũi tên mở. |

Không tìm khối có tên riêng “JSP”, “DAO” hoặc “Model” trong thanh công cụ; đó là tên/vai trò của các phần tử Class trong bài.

## 1. Sửa hình cũ

1. Xóa bốn gói `exception`, `util`, `filter`, `dto` và toàn bộ Dependency nối vào hoặc đi ra từ bốn gói đó.
2. Xóa gói `common` khỏi hình báo cáo. Các JSPF dùng chung vẫn là chi tiết tổ chức file giao diện trong code.
3. Giữ bốn gói ngoài cùng: `view`, `controller`, `dao`, `model`.
4. Trong `view`, giữ các gói `member`, `manager`, `warehouseStaff`, `salesStaff`, `customer`.
5. Đưa các lớp và tệp bên dưới vào đúng gói chứa; không chỉ đặt hình chồng lên nhau. Trong Model Explorer phải thấy chúng là phần tử con của gói.

`warehouseStaff` là giao diện cho nhân viên kho. `Supplier4` là thực thể trong model; nhà cung cấp không có gói giao diện vì không trực tiếp sử dụng hệ thống. `Technician4` cũng không có gói giao diện trong phạm vi bài làm.

## 2. Nội dung từng gói

### model — 23 lớp

| Nhóm để tiện bố trí | Tên lớp |
|---|---|
| Thành viên và nhân viên | `Member4`, `Employee4`, `Manager4`, `WarehouseStaff4`, `SalesStaff4`, `Customer4`, `Technician4` |
| Nhà cung cấp, xe và lịch hẹn | `Supplier4`, `Car4`, `Appointment4` |
| Tiếp nhận và hóa đơn khách hàng | `RepairIntake4`, `CustomerInvoice4`, `RepairService4`, `RepairPart4` |
| Dịch vụ, phụ tùng | `Service4`, `SparePart4`, `SparePartCategory4` |
| Nhập phụ tùng | `PurchaseInvoice4`, `PurchaseInvoiceDetail4` |
| Thống kê | `CustomerStatistics4`, `ServiceStatistics4`, `SparePartStatistics4`, `SupplierStatistics4` |

Đặt tất cả trong **một gói model**; các dòng nhóm trong bảng chỉ giúp bố trí, không tạo thêm package. Đặt `Member4`, `Employee4` là abstract, tên in nghiêng. Dùng Class ba ngăn: ngăn tên, ngăn thuộc tính, ngăn phương thức. Ở hình gói chỉ ghi tên lớp; không chép lại toàn bộ thuộc tính/hàm của biểu đồ lớp. Giữ hai ngăn thành viên để trống trên hình, không xóa thành viên khỏi mô hình gốc. Quan hệ giữa từng lớp đã trình bày ở biểu đồ lớp, không vẽ lại trong hình gói này.

### dao — 7 lớp

`DAO` (abstract), `ServiceDAO4`, `SparePartDAO4`, `SupplierDAO4`, `WarehouseStaffDAO4`, `SparePartCategoryDAO4`, `PurchaseInvoiceDAO4`.

### controller — 4 lớp

`CatalogServlet4`, `SupplierServlet4`, `SparePartServlet4`, `PurchaseInvoiceServlet4`.

### view — các gói theo người dùng

| Gói con | Trang JSP đã có tên trong thiết kế |
|---|---|
| customer | `customerHome.jsp`, `catalogSearch.jsp`, `itemDetails.jsp` |
| warehouseStaff | `warehouseHome.jsp`, `selectSupplier.jsp`, `newSupplier.jsp`, `purchaseInvoice.jsp`, `newSparePart.jsp`, `confirmedInvoice.jsp`, `printInvoice.jsp` |
| member | Bài chưa thiết kế tên JSP cụ thể cho phần tài khoản; giữ gói, không tự đặt tên trang. |
| manager | Bài chưa thiết kế tên JSP cụ thể; giữ gói, không tự đặt tên trang. |
| salesStaff | Bài chưa thiết kế tên JSP cụ thể; giữ gói, không tự đặt tên trang. |

Mỗi JSP dùng một **Class** mang đúng tên trang, giống phần tử JSP trong biểu đồ lớp thiết kế của bài. Kéo lại chính phần tử JSP đã có để tạo thêm view của nó, không tạo một Class trùng tên khác. Trong hình gói chỉ hiện tên; hai ngăn thuộc tính/phương thức được giữ nhưng không liệt kê thành viên. Không dùng hình tệp gấp góc, Artifact hoặc hình chữ nhật vẽ tự do cho JSP trong bản này.

## 3. Vẽ Dependency

Tạo sơ đồ rồi đặt các Package hình thư mục. Bố trí `view` phía trên, `controller` và `dao` bên trái phía dưới, `model` bên phải. Các Class nằm bên trong Package tương ứng. Dùng Dependency nét đứt, đầu mũi tên mở hướng tới gói được sử dụng:

| Từ | Đến | Căn cứ |
|---|---|---|
| view | controller | Form/link gửi yêu cầu xử lí. |
| controller | view | Servlet forward và chuyển dữ liệu tới JSP. |
| view | model | JSP hiển thị thông tin thực thể qua EL/JSTL. |
| controller | dao | Servlet gọi các hàm DAO. |
| controller | model | Servlet tạo và sử dụng đối tượng thực thể. |
| dao | model | DAO nhận và trả dữ liệu thực thể. |

Hai chiều giữa view và controller dùng **hai Dependency riêng**, lệch vị trí để nhìn rõ đầu mũi tên. Không đánh số thông điệp; không dùng Association có bội số hoặc Generalization để nối các gói. Việc gói con nằm trong view đã biểu diễn quan hệ chứa, không thêm Dependency chỉ vì quan hệ chứa.

### Thao tác theo thanh công cụ trong ảnh người dùng

Ảnh người dùng gửi là thanh công cụ không có Class; có Package, Note, Containment và các công cụ vẽ chung. Không yêu cầu tìm Class/Artifact trong danh sách này, không khẳng định kéo Class vào Package Diagram đó sẽ được hỗ trợ.

Để dựng **hình tổ chức gói có liệt kê các lớp bên trong** như bản bàn giao, dùng cách đã được tài liệu Visual Paradigm hướng dẫn: vẽ Package và Class trên một **Class Diagram**.

1. Giữ sơ đồ Package Diagram đang có. Chọn **Diagram > New > Class Diagram**, đặt tên “Biểu đồ thiết kế gói”.
2. Trong Class Diagram mới, tạo các **Package**: view, controller, dao, model và các gói con đã liệt kê. Package là phần tử có thể dùng trong nhiều loại sơ đồ UML.
3. Tạo **Class** bên trong Package. Với lớp/JSP đã có, mở **View > Panes > Model Explorer**, kéo phần tử vào **Class Diagram mới** để tái sử dụng, không tạo lại các lớp trùng tên. Đảm bảo lớp thuộc đúng gói trong Model Explorer; đặt hình chồng lên Package chưa đủ để chứng minh đã đổi gói sở hữu.
4. Khi dùng lại Class có thành viên, mở **Presentation Options** để ẩn danh sách thuộc tính/phương thức trên view này; bật **Class Members Display Options > Show Class Empty Compartments** để giữ các ngăn. Thao tác hiển thị không xóa thuộc tính/phương thức khỏi phần tử ở các sơ đồ khác.
5. Nối các Package bằng **Dependency** theo bảng trên. Không dùng Rectangle, Note hoặc Element Group thay cho Class.

Tên loại sơ đồ trong công cụ là Class Diagram để có các công cụ dựng lớp; nội dung hình vẫn trình bày tổ chức các lớp/JSP theo gói. Đây không phải yêu cầu thêm lớp/hàm hoặc sửa code. Nếu chỉ vẽ các gói và quan hệ giữa gói, dùng Package Diagram hiện tại là phù hợp; hình chi tiết có các lớp dùng cách dựng phía trên.

Căn cứ thao tác: [tái sử dụng phần tử từ Model Explorer](https://knowhow.visual-paradigm.com/uml/reuse-model-element/), [vẽ Class bên trong Package](https://knowhow.visual-paradigm.com/uml/how-to-draw-a-class-diagram-with-separate-distinct-classes-with-same-name/), [các tùy chọn hiển thị ngăn Class](https://knowhow.visual-paradigm.com/diagramming/class-diagram-presentation-options/). Tên menu có thể khác nhẹ theo phiên bản/giao diện của Visual Paradigm.

## 4. Phạm vi hình và code

- Theo lựa chọn của người làm bài, model trong hình bao gồm toàn bộ **23 lớp của báo cáo**. App chỉ cài đặt **10 model, 9 bảng** cho hai modul; không tạo 13 file model hoặc bảng còn lại chỉ để khớp số lượng trên hình tổng thể.
- DAO, Servlet và JSP trong hình lấy đúng các tên nêu ở phần thiết kế hai modul. Phần code đăng nhập đã có `AuthServlet4`, `HomeServlet4`, `MemberDAO4`, `member/login.jsp`; giữ để ứng dụng chạy nhưng không tự bổ sung các tên này vào hình thiết kế hai modul khi bài chưa đặt tên chúng.
- Đã đổi thư mục JSP từ `view/warehouse` thành `view/warehouseStaff` và sửa forward/include tương ứng. Các URL `/warehouse/...` giữ nguyên để các form/link đang có tiếp tục hoạt động.
- Thư mục `view/common` trong source chứa trang lỗi và các JSPF dùng chung. Đây là chi tiết tái sử dụng giao diện, không phải một nhóm người dùng mới.
- Việc bỏ bốn gói phụ và kiểm chứng không thêm hàm ứng dụng được ghi trong [RUT_GON_CODE_THEO_GOI.md](RUT_GON_CODE_THEO_GOI.md).

[SVG để nhìn và vẽ theo](diagrams/package-diagram.svg) · [PDF](diagrams/package-diagram.pdf) · [PlantUML](diagrams/package-diagram.puml) · [Kết quả đối chiếu tên](diagrams/package-diagram-check.json).
