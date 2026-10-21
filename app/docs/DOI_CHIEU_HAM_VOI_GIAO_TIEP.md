# Đối chiếu hàm thiết kế với hai biểu đồ giao tiếp

**Cập nhật tên:** hai SVG/PlantUML hiện đã chuyển sang bộ tên tiếng Anh tại [DANH_SACH_DOI_TEN_TIENG_ANH.md](DANH_SACH_DOI_TEN_TIENG_ANH.md). Các bảng tên tiếng Việt bên dưới lưu lại đối chiếu với code/DOCX hiện tại, chưa được đổi tên trong lượt này. Khi vẽ hai hình mới, dùng tên tiếng Anh trong danh sách mới; phương thức, kiểu và quan hệ giữ nguyên ý nghĩa.

Bản trình bày lại ngày 21/10/2026. Hai hình trước đưa quá nhiều chi tiết cài đặt lên hình báo cáo: hàm xử lý request của Servlet, DTO phiên và tiện ích giữ nháp. Bản hiện tại lược các chi tiết đó, giữ các thao tác nghiệp vụ có căn cứ từ biểu đồ giao tiếp và phần phân tích/kịch bản trong DOCX. Không thay đổi Java, JSP hoặc schema để làm gọn hình.

## SessionUser4 không phải bảng CSDL

`app/src/main/java/dto/SessionUser4.java` là lớp Java giữ `id`, `hoTen`, `vaiTro`, `viTri` trong phiên HTTP. Nó không có bảng SQL hoặc DAO riêng. `app/database/01_schema.sql` vẫn chỉ có 9 bảng: `tblMember4`, `tblEmployee4`, `tblCustomer4`, `tblSupplier4`, `tblSparePartCategory4`, `tblService4`, `tblSparePart4`, `tblPurchaseInvoice4`, `tblPurchaseInvoiceDetail4`.

Biểu đồ lớp có thể chứa JSP, Servlet, DAO, DTO và thực thể, nhưng không phải mỗi lớp đều trở thành một bảng. Trong hai hình báo cáo đã bỏ `SessionUser4` và `DraftStore4`; chúng vẫn là chi tiết cài đặt trong code.

## Modul tra cứu: các hàm trong ảnh người dùng gửi

| Số thông điệp trong ảnh | Hàm pha phân tích | Nơi thực hiện ở thiết kế/code |
|---|---|---|
| 6 | `Service4.timDichVuTheoTen()` | `ServiceDAO4.timDichVuTheoTen(String): List<Service4>` |
| 9 | `SparePart4.timPhuTungTheoTen()` | `SparePartDAO4.timPhuTungTheoTen(String, boolean): List<SparePart4>` |
| 15 | `SparePart4.layChiTietPhuTung()` | `SparePartDAO4.layChiTietPhuTung(int, boolean): SparePart4` |

Ảnh minh họa chọn PT1 nên không gọi hàm lấy chi tiết dịch vụ. `ServiceDAO4.layChiTietDichVu(int): Service4` có căn cứ riêng trong phần phân tích tĩnh của DOCX: “đề xuất phương thức layChiTietDichVu(), gán cho lớp Service4”. Hình thiết kế toàn modul giữ hàm này để phục vụ nhánh chọn dịch vụ.

## Modul nhập: các hàm trong ảnh người dùng gửi

| Số thông điệp trong ảnh | Hàm pha phân tích | Nơi thực hiện ở thiết kế/code |
|---|---|---|
| 6 | `Supplier4.timNhaCungCapTheoTen()` | `SupplierDAO4.timNhaCungCapTheoTen(String): List<Supplier4>` |
| 12 | `Supplier4.layThongTinNhaCungCap()` | `SupplierDAO4.layThongTinNhaCungCap(int): Supplier4` |
| 15 | `WarehouseStaff4.layThongTinNhanVienKho()` | `WarehouseStaffDAO4.layThongTinNhanVienKho(int): WarehouseStaff4` |
| 20 | `SparePart4.timPhuTungTheoTen()` | `SparePartDAO4.timPhuTungTheoTen(String, boolean): List<SparePart4>` |
| 25 | `SparePart4.layChiTietPhuTung()` | `SparePartDAO4.layChiTietPhuTung(int, boolean): SparePart4` |
| 32 | `PurchaseInvoice4.tinhTongTien()` | `PurchaseInvoice4.tinhTongTien(): BigDecimal` |
| 33 | `PurchaseInvoice4.luuHoaDonNhap()` | `PurchaseInvoiceDAO4.luuHoaDonNhap(PurchaseInvoice4): int` |
| 35 | `SparePart4.tangTonSauNhap()` | `SparePartDAO4.tangTonSauNhap(Connection, int, BigDecimal): void` |
| 40, 46, 52 | `PurchaseInvoice4.layThongTinHoaDonNhap()` | `PurchaseInvoiceDAO4.layThongTinHoaDonNhap(int): PurchaseInvoice4` |
| 57 | `PurchaseInvoice4.ghiNhanThanhToan()` | `PurchaseInvoiceDAO4.ghiNhanThanhToan(int, BigDecimal, int): boolean` |

Chuyển hàm truy xuất từ thực thể pha phân tích sang DAO là bước 3 của thiết kế tĩnh theo bài giảng. Hàm tính tổng vẫn thuộc thực thể hóa đơn. Các tham số/kiểu trả về được cụ thể hóa theo source hiện tại, không phải tên nghiệp vụ mới. `Connection` trong tăng tồn là cùng kết nối transaction lưu hóa đơn; `chiCongBo` phân biệt lọc danh mục cho khách và kho.

## Các nhánh trong DOCX nhưng không đi qua trong hai ảnh

| Nhánh đã có trong bài làm | Hàm giữ trên hình thiết kế | Căn cứ |
|---|---|---|
| Chọn xem dịch vụ | `ServiceDAO4.layChiTietDichVu(int)` | Phân tích tĩnh modul tra cứu |
| Thêm NCC | `SupplierDAO4.themNhaCungCap(Supplier4)` | Ngoại lệ 4a và lớp phân tích `NewSupplierUI4` |
| Chọn nhóm khi thêm phụ tùng | `SparePartCategoryDAO4.layDanhSachNhom()` | Ngoại lệ 8a và phần phân tích chọn nhóm |
| Lưu phụ tùng mới | `SparePartDAO4.themPhuTung(SparePart4)` | Ngoại lệ 8a và lớp phân tích `NewSparePartUI4` |

Vì thiết kế cho toàn modul gồm cả ngoại lệ, hình nhập giữ đủ 7 JSP. Hai nhánh thêm mới được chú thích riêng trên hình; không nói chúng xuất hiện trong ảnh giao tiếp của luồng chính.

## Các hàm kỹ thuật đã lược khỏi hình

`batDauNhap`, `hienThiBanNhap`, `themDongNhap`, `capNhatDongNhap`, `xoaDongNhap`, `huyBanNhap`, `xacNhanHoaDon`, `xemHoaDon`, `inHoaDon` là các hàm điều phối HTTP trong Servlet. Các hàm giữ nháp, tìm lại mã yêu cầu, kiểm tra nhóm và kết nối DB cũng là chi tiết cài đặt. Không xóa chúng khỏi code chỉ vì chúng không xuất hiện trong biểu đồ giao tiếp pha phân tích.

Hình hiện tại vẫn chỉ ra lớp Servlet điều phối giữa JSP và DAO để đúng kiến trúc đang chạy; lược phương thức Servlet và các helper. Cũng lược getter/setter, `getTongTien`, `getThanhTien`, hàm tính từng dòng, lifecycle, `throws` và lớp cha framework. Đây là hình lựa chọn phần tử phục vụ báo cáo, không phải bản liệt kê toàn bộ mã nguồn.

Các field dữ liệu thực thể và quan hệ vẫn lấy từ source. Việc lưu trực tiếp `tonKho` và người lập ghi nhận thanh toán là các quyết định đã ghi trong kế hoạch; không khẳng định lời văn DOCX gốc đã tự được sửa theo chúng.

Hai hình đã kiểm tra cú pháp PlantUML và xuất SVG/PNG. Kết quả này chỉ xác nhận hình render được. Phần bốn bước của mục 4.3.1 nằm trong [4.3.1_THIET_KE_TINH.md](4.3.1_THIET_KE_TINH.md).

## Cách ghi lớp giao diện theo mẫu bài giảng

Phần JSP trên hai hình là **đề xuất thuộc tính giao diện ở bước thiết kế tĩnh**, không phải bản chép các biến/ID HTML sau khi cài đặt. Tên màn hình JSP và các thao tác nghiệp vụ vẫn giữ; tên control được đặt theo vai trò để mô tả thiết kế.

| Thành phần thiết kế | Cách ghi trên hình | Ánh xạ khi cài đặt |
|---|---|---|
| Ô nhập tên | `- tuKhoa: TextField` | Ô nhập từ khóa của trang |
| Chọn loại thông tin | `- boLoc: Select` | Control lọc loại dịch vụ/phụ tùng |
| Bảng kết quả tra cứu chung | `- tblKetQua: Table` | Bảng hiển thị từ hai danh sách dịch vụ và phụ tùng |
| Nút tìm | `- btnTimKiem: submit` | Nút gửi yêu cầu tìm kiếm; không cần ghi ID `btn-search` |
| Dữ liệu dịch vụ/phụ tùng | `- dsDichVu: List<Service4>`, `- dsPhuTung: List<SparePart4>` | Các danh sách dữ liệu dùng để trình bày bảng |
| Bảng tìm phụ tùng để nhập | `- tblKetQuaPhuTung: Table` | Bảng Mã/Tên/ĐVT/Hãng/Chọn theo yêu cầu đổi giao diện |
| Nút chọn phụ tùng từng dòng | `- btnChonPhuTung: submit` | Khi triển khai, nút gửi mã phụ tùng của dòng được chọn |
| Bảng dòng nhập sửa lượng/giá | `- tblChiTietNhap: Table` | Bảng các dòng nhập của hóa đơn |
| Dữ liệu chi tiết đã chọn | `- dichVu: Service4`, `- phuTung: SparePart4` | Dữ liệu thực thể cần hiển thị trên trang chi tiết |
| Liên kết xem hoặc quay lại | `- chonThongTin: link`, `- quayLai: link` | Liên kết điều hướng, chưa mô tả cách ghép URL |

Lược khỏi hình chính: `currentUser.hoTen`, ID HTML, tên tham số query, `daTimKiem`, `errorMessage`, hidden token/version và hậu tố input theo ID dòng. Các chi tiết này thuộc hợp đồng trao đổi/cài đặt; không biến chúng thành thuộc tính nghiệp vụ hoặc bảng CSDL. Tên hiển thị khách/nhân viên trên trang chính được đề xuất gọn là `hoTen: String`, không thêm một đối tượng hồ sơ đầy đủ mà luồng hiện tại không tải.

Quy ước `TextField`, `TextArea`, `Select`, `Table`, `link`, `submit`, `button` biểu diễn vai trò thành phần giao diện theo cách trình bày của bài giảng; không phải tất cả đều là lớp Java. `-` trình bày thuộc tính của lớp giao diện trong thiết kế. Các chữ ký DAO và quan hệ thực thể không đổi vì thay ký pháp giao diện.
