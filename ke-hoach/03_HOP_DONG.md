# Hợp đồng dữ liệu và luồng web — phương án cuối

Áp dụng cùng `02_KE_HOACH.md` và `07_DOI_CHIEU_BAO_CAO.md`. Giữ mô hình trong DOCX mới nhất cùng quyết định cuối của người dùng: **lưu tồn trực tiếp; một người lập và thực hiện thu/chi trên mỗi hóa đơn; người tiếp nhận lưu riêng**. Không thêm quan hệ nhân viên thanh toán riêng. Danh mục 14 trường đã thống nhất ở file 05 là phần lớn đã có trong DOCX mới, không phải 14 trường phải thêm lần nữa.

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
| Member.vaiTro | `Customer`, `Manager`, `WarehouseStaff`, `SalesStaff` |
| Member.trangThaiTaiKhoan | `Hoạt động`, `Đã khóa`; chỉ NULL khi khách chưa có tài khoản |
| Employee.viTri | `Quản lí`, `Kho`, `Bán hàng`, khớp với vaiTro tương ứng |
| Employee.trangThaiLamViec | `Đang làm việc`, `Ngừng làm việc` |
| Technician.trangThai | `Đang làm việc`, `Ngừng làm việc` |
| Supplier.trangThai | `Đang hoạt động`, `Ngừng hoạt động` |
| SparePartCategory.trangThai | `Đang sử dụng`, `Ngừng sử dụng` |
| Service.trangThai | `Đang cung cấp`, `Ngừng cung cấp` |
| SparePart.trangThai | `Chưa công bố`, `Đang kinh doanh`, `Ngừng kinh doanh` |
| Appointment.trangThai | `Đã đặt`, `Đã tiếp nhận`, `Đã hủy` |
| RepairIntake.trangThai | `Đã tiếp nhận`, `Đang xử lí`, `Hoàn thành`, `Đã hủy` |
| PurchaseInvoice/CustomerInvoice.tinhTrang | `Chưa thanh toán`, `Đã thanh toán` |

`Bản nháp` chỉ là trạng thái của object hóa đơn trong session trước lưu; không được INSERT giá trị này vào bảng hóa đơn. Các bảng hóa đơn chỉ lưu bản đã xác nhận. `thoiDiemThanhToan` NULL khi chưa trả, khác NULL khi đã trả. CHECK ở hai hóa đơn: `(tinhTrang = 'Chưa thanh toán' AND thoiDiemThanhToan IS NULL) OR (tinhTrang = 'Đã thanh toán' AND thoiDiemThanhToan IS NOT NULL)`. Chỉ có một FK nhân viên lập; chính người lập thực hiện thu/chi, không thêm FK người thanh toán.

SQL dùng CHECK cho miền trạng thái. Ràng buộc vaiTro–viTri nằm ở hai bảng nên kiểm tra ở DAO/Filter và seed; không giả rằng FK đã bảo đảm. Member không được đồng thời có subtype Employee và Customer. Đợt hiện tại không có màn hình cấp tài khoản; seed phải bảo đảm các quy tắc này.

Tìm khách: chỉ Service đang cung cấp và SparePart đang kinh doanh; không lọc part theo tồn > 0 vì đề không quy định phải còn hàng mới xem được. Tìm kho: part `Chưa công bố` hoặc `Đang kinh doanh`, loại `Ngừng kinh doanh`; nhà cung cấp đang hoạt động. Nhóm ngừng sử dụng không cho chọn cho part mới; part cũ vẫn giữ liên kết nhóm và đọc được tên nhóm, không tự ẩn part chỉ vì nhóm bị ngừng.

## 3. Từ điển bảng và ánh xạ model

### 3.1. `tblMember4`

| Cột | Kiểu/ràng buộc |
|---|---|
| id | INT PK AI |
| hoTen | VARCHAR(255) NN |
| tenDangNhap | VARCHAR(100) N UNIQUE |
| matKhau | VARCHAR(255) N; lưu chuỗi hash tự mô tả |
| ngaySinh | DATE N |
| diaChi | TEXT N |
| email | VARCHAR(255) N |
| dienThoai | VARCHAR(20) NN |
| vaiTro | VARCHAR(30) NN CHECK miền vai trò |
| trangThaiTaiKhoan | VARCHAR(30) N CHECK miền trạng thái |

CHECK: hoặc cả username/password/trạng thái đều NULL và vaiTro=Customer, hoặc cả ba có giá trị hợp lệ. Các Employee sử dụng hệ thống phải có tài khoản. `Member4` là abstract, khi map tạo Customer4/Manager4/WarehouseStaff4/SalesStaff4 theo vaiTro.

### 3.2. `tblEmployee4`

| Cột | Kiểu/ràng buộc |
|---|---|
| tblMember4id | INT PK, FK → tblMember4.id |
| viTri | VARCHAR(30) NN |
| trangThaiLamViec | VARCHAR(30) NN |

`Employee4 extends Member4`, thêm hai thuộc tính. Manager4/WarehouseStaff4/SalesStaff4 chỉ là lớp Java con, không tạo lại ba bảng con đã bị loại trong báo cáo.

### 3.3. `tblCustomer4`

`tblMember4id INT PRIMARY KEY`, FK → tblMember4.id. Không có cột khác. `Customer4 extends Member4`; FK tới Customer ở các bảng sau tham chiếu PK này.

Giữ bảng Customer dù chỉ có khóa để phân biệt subtype và làm đích FK khách hàng như mô hình hiện có. Bốn bảng thống kê bị loại riêng, không áp dụng quy tắc “xóa hết mọi bảng chỉ có FK” một cách máy móc.

### 3.4. `tblTechnician4`

`id INT PK AI`, `hoTen VARCHAR(255) NN`, `ngaySinh DATE N`, `diaChi TEXT N`, `email VARCHAR(255) N`, `dienThoai VARCHAR(20) NN`, `chuyenMon VARCHAR(255) NN`, `trangThai VARCHAR(30) NN`.

`Technician4` độc lập với Member4, đúng phạm vi không bắt buộc đăng nhập.

### 3.5. `tblSupplier4`

`id INT PK AI`, `ten VARCHAR(255) NN`, `diaChi TEXT NN`, `dienThoai VARCHAR(20) NN`, `email VARCHAR(255) N`, `trangThai VARCHAR(30) NN DEFAULT 'Đang hoạt động'`.

Không tự UNIQUE tên hoặc điện thoại: báo cáo chưa đặt quy tắc đó và các tên có thể giống nhau. Người nhập đối chiếu tên/địa chỉ/điện thoại để chọn. Tạo index tên nếu cần nhưng tìm chứa `%từ khóa%` không được quảng bá là truy vấn dùng index hiệu quả trong mọi trường hợp.

### 3.6. `tblCar4`

`id INT PK AI`, `bienSo VARCHAR(20) NN UNIQUE`, `hangXe VARCHAR(255) NN`, `dongXe VARCHAR(255) NN`, `namSanXuat INT N`. Java `namSanXuat:Integer` vì nullable. Không thêm ownerId cố định; người mang xe ở từng phiếu.

### 3.7. `tblSparePartCategory4`

`id INT PK AI`, `ten VARCHAR(255) NN UNIQUE`, `moTa TEXT N`, `trangThai VARCHAR(30) NN DEFAULT 'Đang sử dụng'`.

Tên nhóm duy nhất theo collation đã chọn; nhân viên kho chỉ chọn nhóm sẵn có, không tạo nhóm bằng nhập tên tự do.

### 3.8. `tblService4`

| Cột | Kiểu/ràng buộc |
|---|---|
| id | INT PK AI |
| ten | VARCHAR(255) NN |
| nhom | VARCHAR(255) NN; vẫn là chuỗi như mô hình, chưa tách bảng nhóm dịch vụ |
| moTa | TEXT NN |
| noiDung | TEXT NN |
| hinhAnh | VARCHAR(255) N |
| donGia | DECIMAL(18,2) NN CHECK >= 0 |
| thoiGianDuKien | INT NN CHECK > 0; đơn vị phút |
| hanBaoHanh | VARCHAR(255) N, ví dụ “6 tháng”; không tự đổi thành số tháng |
| dieuKienBaoHanh | TEXT N |
| trangThai | VARCHAR(30) NN |

Giá 0 có thể tồn tại trong danh mục; giá nhập ở hóa đơn nhập vẫn phải > 0. Mô hình chưa có thuế/giảm giá nên không tự thêm. Không tách Service thành bảng con của một Product chung với SparePart.

### 3.9. `tblSparePart4`

| Cột | Kiểu/ràng buộc |
|---|---|
| id | INT PK AI |
| ten | VARCHAR(255) NN |
| moTa | TEXT N |
| hinhAnh | VARCHAR(255) N |
| donVi | VARCHAR(20) NN CHECK IN ('Cái','Chiếc','Bộ','Lít') |
| hangSanXuat | VARCHAR(255) NN |
| thongSo | TEXT N |
| dongXePhuHop | TEXT N |
| giaBan | DECIMAL(18,2) N CHECK >= 0 khi không NULL |
| hanBaoHanh | VARCHAR(255) N |
| dieuKienBaoHanh | TEXT N |
| trangThai | VARCHAR(30) NN DEFAULT 'Chưa công bố' |
| tonKho | DECIMAL(12,3) NN DEFAULT 0 CHECK >= 0; lưu trực tiếp |
| tblSparePartCategory4id | INT NN FK → tblSparePartCategory4.id |

Java: `nhom:SparePartCategory4` thay FK và `tonKho:BigDecimal` ánh xạ cột tồn đã lưu. `themPhuTung` luôn khởi tạo tồn bằng 0 ở server; không nhận tồn từ form. Phụ tùng nhập mới tối thiểu cần tên, nhóm đang sử dụng, đơn vị và hãng để phân biệt; giá bán NULL được nếu `Chưa công bố`. Thiếu giá hoặc mô tả thì không được chọn `Đang kinh doanh`. Ảnh/thông số/dòng xe/bảo hành là tùy chọn, có thể hiển thị “Chưa cập nhật”/“Không có thông tin bảo hành” thay vì bịa thông tin.

Kho được chọn `Chưa công bố` hoặc `Đang kinh doanh` khi thêm mới; chọn công bố phải qua validate tối thiểu giá bán và mô tả. Không cho kho dùng form thêm mới để sửa tồn hoặc đổi part cũ thành ngừng kinh doanh. CHECK bổ sung `trangThai <> 'Đang kinh doanh' OR (giaBan IS NOT NULL AND moTa IS NOT NULL AND CHAR_LENGTH(TRIM(moTa)) > 0)`.

### 3.10. `tblAppointment4`

| Cột | Kiểu/ràng buộc |
|---|---|
| id | INT PK AI |
| ngayGioHen, thoiDiemDat | DATETIME NN |
| yeuCau, ghiChu | TEXT N |
| trangThai | VARCHAR(30) NN |
| tblCustomer4id | INT NN FK → tblCustomer4.tblMember4id |
| tblCar4id | INT NN FK → tblCar4.id |
| hoTenLienHe **[bổ sung]** | VARCHAR(255) NN |
| dienThoaiLienHe **[bổ sung]** | VARCHAR(20) NN |

Java thêm `khachHang:Customer4`, `oTo:Car4`. Giữ liên kết xe bắt buộc như hình CSDL hiện có; module đặt lịch tương lai phải tìm/tạo hồ sơ xe khi đặt, không được tự giả định đã có xe hoặc nới FK thành NULL mà quên sửa mô hình. Chưa code module này trong đợt hiện tại.

### 3.11. `tblRepairIntake4`

`id INT PK AI`, `thoiDiemTiepNhan DATETIME NN`, `soKm DECIMAL(12,1) N CHECK >= 0`, `tinhTrangXe TEXT N`, `trangThai VARCHAR(30) NN`, `ghiChu TEXT N`.

FK: `tblCustomer4id INT NN` → Customer; `tblCar4id INT NN` → Car; `tblEmployee4id INT NN` → Employee.tblMember4id; `tblAppointment4id INT N UNIQUE` → Appointment.id.

Java thay FK bằng `khachHang:Customer4`, `oTo:Car4`, `nhanVienTiepNhan:SalesStaff4`, `lichHen:Appointment4` nullable; thêm `dsDichVu:List<RepairService4>`, `dsPhuTung:List<RepairPart4>`. `soKm` dùng BigDecimal. Ràng buộc ít nhất một dòng dịch vụ hoặc part kiểm tra khi xác nhận phiếu trong module tương lai; CHECK một bảng không tự kiểm tra được tồn tại dòng ở bảng khác.

### 3.12. `tblRepairService4`

`id INT PK AI`, `donGiaApDung DECIMAL(18,2) NN CHECK >= 0`, `hanBaoHanhApDung VARCHAR(255) N`, `dieuKienBaoHanhApDung TEXT N`, `ghiChu TEXT N`, `tenDichVuApDung VARCHAR(255) NN` **[bổ sung]**.

FK `tblRepairIntake4id INT NN`, `tblService4id INT NN`, `tblTechnician4id INT NN`. UNIQUE `(tblRepairIntake4id,tblService4id)`.

Java: `dichVu:Service4`, `nhanVienKyThuat:Technician4`. Mỗi dịch vụ một lượt trên phiếu theo báo cáo; không thêm cột số lượng dịch vụ. Tên/giá/bảo hành áp dụng phải được chốt trước xác nhận hóa đơn khách; sau đó không sửa trong phạm vi bài.

### 3.13. `tblRepairPart4`

`id INT PK AI`, `soLuongYeuCau DECIMAL(12,3) NN CHECK > 0`, `soLuongThucTe DECIMAL(12,3) NN DEFAULT 0 CHECK >= 0`, `donGiaApDung DECIMAL(18,2) NN CHECK >= 0`, `hanBaoHanhApDung VARCHAR(255) N`, `dieuKienBaoHanhApDung TEXT N`, `ghiChu TEXT N`.

**[bổ sung]** `tenPhuTungApDung VARCHAR(255) NN`, `donViApDung VARCHAR(20) NN`.

FK `tblRepairIntake4id INT NN`, `tblSparePart4id INT NN`. UNIQUE `(tblRepairIntake4id,tblSparePart4id)`. Java `phuTung:SparePart4`.

Đơn vị áp dụng quyết định kiểm tra lượng nguyên/lẻ và tính tồn lịch sử. Không tự đổi đơn vị danh mục khi đã có giao dịch sang một đơn vị có ý nghĩa khác; chưa có quy đổi đơn vị. Dòng không thực tế sử dụng có thể có lượng thực tế 0 trước khi chốt; module tương lai phải đối chiếu trước lập hóa đơn.

### 3.14. `tblCustomerInvoice4`

`id INT PK AI`, `thoiDiemLap DATETIME NN`, `thoiDiemXacNhan DATETIME NN`, `tinhTrang VARCHAR(30) NN DEFAULT 'Chưa thanh toán'`, `thoiDiemThanhToan DATETIME N`.

FK `tblRepairIntake4id INT NN UNIQUE` → RepairIntake.id.

**[bổ sung]** `tblEmployee4id INT NN` FK → Employee.tblMember4id; `hoTenKhachHangApDung VARCHAR(255) NN`; `bienSoApDung VARCHAR(20) NN`; `nhanVienTenApDung VARCHAR(255) NN`.

Java: `phieuTiepNhan:RepairIntake4`, `nhanVienLap:SalesStaff4`, `tongTien:BigDecimal` dẫn xuất. Không có bảng CustomerInvoiceDetail mới: trong phạm vi hiện tại dùng dòng RepairService/RepairPart của phiếu, chốt bất biến khi có hóa đơn. Nếu về sau cần sửa phiếu sau lập hóa đơn thì phải thiết kế phiên bản/chi tiết hóa đơn riêng, không tự mở chức năng sửa trong đợt này.

### 3.15. `tblPurchaseInvoice4`

| Cột | Kiểu/ràng buộc |
|---|---|
| id | INT PK AI |
| ngayNhap | DATETIME NN; thời điểm xác nhận nhập, đồng thời là thời điểm nghiệp vụ nhập trong baseline |
| thoiDiemXacNhan | DATETIME NN |
| tinhTrang | VARCHAR(30) NN DEFAULT 'Chưa thanh toán' |
| thoiDiemThanhToan | DATETIME N |
| tblSupplier4id | INT NN FK → tblSupplier4.id |
| tblEmployee4id | INT NN FK → tblEmployee4.tblMember4id |
| maYeuCau **[bổ sung]** | CHAR(36) NN UNIQUE; UUID sinh ở server cho một lần nhập |
| nhaCungCapTenApDung **[bổ sung]** | VARCHAR(255) NN |
| nhanVienTenApDung **[bổ sung]** | VARCHAR(255) NN |

Java: `nhaCungCap:Supplier4`, `nhanVienKho:WarehouseStaff4`, `dsChiTietNhap:List<PurchaseInvoiceDetail4>`, `tongTien:BigDecimal` dẫn xuất. Không có cột tongTien. Khi xác nhận lấy tên NCC và tên nhân viên từ DB trong transaction để chốt; không lấy hidden input. Phần đã xác nhận và bản in không hiển thị địa chỉ/điện thoại NCC hoặc hãng phụ tùng; các thông tin đó chỉ dùng ở màn hình tìm/chọn và bản nháp.

`ngayNhap` và `thoiDiemXacNhan` đang cùng giá trị trong phạm vi này; giữ hai trường vì báo cáo đã có. Không tự thêm nhập lùi ngày, tách ngày nhà cung cấp xuất hàng hay số hóa đơn thuế.

### 3.16. `tblPurchaseInvoiceDetail4`

| Cột | Kiểu/ràng buộc |
|---|---|
| id | INT PK AI |
| soLuong | DECIMAL(12,3) NN CHECK > 0 |
| donGiaNhap | DECIMAL(18,2) NN CHECK > 0 |
| tblPurchaseInvoice4id | INT NN FK → tblPurchaseInvoice4.id |
| tblSparePart4id | INT NN FK → tblSparePart4.id |
| tenPhuTungApDung **[bổ sung]** | VARCHAR(255) NN |
| donViApDung **[bổ sung]** | VARCHAR(20) NN |

UNIQUE `(tblPurchaseInvoice4id,tblSparePart4id)`. Java thêm `phuTung:SparePart4`, `thanhTien:BigDecimal` dẫn xuất; không có cột thanhTien. Chính thức chỉ có lượng/giá >0, dù object draft có thể tạm bằng 0.

### Phạm vi thông tin được chốt

- Hóa đơn nhập: mã, ngày nhập, thời điểm xác nhận, các id liên kết, tên NCC/tên nhân viên áp dụng; từng dòng giữ tên/đơn vị áp dụng, số lượng và đơn giá nhập.
- Hóa đơn khách: mã, thời điểm lập/xác nhận, phiếu/người lập, họ tên khách/biển số/tên người lập áp dụng; các dòng giữ tên, đơn vị phụ tùng, số lượng thực tế, đơn giá và bảo hành áp dụng.
- Lịch hẹn: họ tên và điện thoại liên hệ lúc đặt; email/địa chỉ vẫn là thông tin hồ sơ Customer, không phải dữ liệu lịch sử riêng của lịch.
- Tên trên các dòng sửa được khởi tạo khi tiếp nhận và chốt lần cuối khi xác nhận hóa đơn khách. Sau khi có hóa đơn, không sửa các dòng của phiếu. Đơn vị của phụ tùng đã có giao dịch không được đổi sang đơn vị khác.
- Thành tiền/tổng tiền tính từ dòng đã lưu. Trạng thái và thời điểm thanh toán được cập nhật sau xác nhận, giữ nguyên người lập; bản in hiển thị trạng thái thanh toán hiện tại. Không cam kết các bản in trước/sau thanh toán có cùng trạng thái.
- Không lấy tên nhận diện trên chứng từ đã xác nhận bằng JOIN tên hiện tại. Object master chỉ phục vụ liên kết và màn hình danh mục; bản in dùng đúng các trường áp dụng đã chốt.

### Tổng kết schema vật lí

**16 bảng** như trên. Không tạo bảng vai trò con đã loại, bốn bảng Statistics, bảng nháp hoặc Payment. Không tạo view tồn để thay cột `tonKho`. Bảng phụ tùng lưu tồn; `thanhTien` và `tongTien` không lưu thành cột.

### Tồn kho và transaction đã chốt

- Xác nhận nhập: INSERT header, INSERT toàn bộ chi tiết, cộng `tonKho` của từng phụ tùng **cùng một Connection/transaction**. Lỗi bất kì bước nào rollback cả chứng từ, dòng và tồn.
- `SparePartDAO4.tangTonSauNhap(Connection,int,BigDecimal)` chạy `UPDATE tblSparePart4 SET tonKho = tonKho + ? WHERE id = ?`; kiểm tra đúng 1 hàng, lượng dương, không vượt DECIMAL(12,3). Hàm không mở/commit/đóng Connection được truyền vào.
- Trước khi ghi, khóa nhà cung cấp, nhân viên và các phụ tùng cần dùng; khóa theo thứ tự loại bảng cố định Supplier → Employee → SparePart, các id phụ tùng tăng dần. Đọc lại trạng thái/đơn vị/tồn trên hàng đã khóa. Những thao tác viết tương lai phải cùng quy ước thứ tự khóa.
- Hai lần nhập khác mã yêu cầu cùng một part đều được cộng. Hai request cùng mã yêu cầu chỉ được cộng một lần nhờ UNIQUE và rollback; không dựa riêng vào khóa session hay nút bị disable.
- Khi triển khai module khách tương lai: khóa hàng part theo cùng thứ tự, kiểm tra tồn còn cấp và trừ lượng thực tế cùng transaction xác nhận hóa đơn khách. Chưa code module này trong đợt đề 26.
- In, đọc lại và ghi nhận thanh toán không cập nhật tồn. Bản nháp không cập nhật tồn. Phụ tùng mới có tồn 0. Seed có hàng phải có chứng từ và tồn tương ứng, không tạo tồn vô căn cứ.
- `03_verify.sql` đối chiếu tồn lưu với tổng nhập đã xác nhận trừ tổng lượng thực tế của phiếu đã có hóa đơn khách xác nhận. Tổng nhập và tổng xuất phải aggregate riêng trước khi JOIN để tránh nhân bản. Đây là kiểm tra nhất quán, không phải nguồn đọc tồn của các màn hình.
- Lượng giữ cho phiếu đang làm chưa trừ vào tồn đã lưu. Công thức tương lai `conCoTheCap = tonKho - luongDaGiu`; các dòng phiếu chưa có hóa đơn và chưa hủy đóng góp lượng giữ. Không code DAO giữ/xuất hàng chỉ để hoàn thiện module nhập.

Tổng tiền nhập = tổng `ROUND(soLuong * donGiaNhap,2)` từng dòng. Giá trị Java `setScale(2, HALF_UP)` phải khớp SQL. Tổng tiền khách = tổng đơn giá dịch vụ + tổng `ROUND(soLuongThucTe * donGiaApDung,2)` của phiếu đã chốt. Không dùng giá danh mục hiện tại để tính lại hóa đơn.

## 4. URL, HTTP method và Servlet method

`{ctx}` thường là `/garaman`; mọi URL sau bỏ prefix này để dễ đọc. Chỉ đăng ký chính xác các mapping dưới đây; action không biết trả 400, HTTP method không được hỗ trợ trả 405. GET chỉ đọc; POST thay đổi dữ liệu/nháp/thanh toán.

| HTTP + URL | Controller/hàm | Input chính | Kết quả |
|---|---|---|---|
| GET `/login` | Auth.hienThiDangNhap | — | JSP login, tạo token nếu chưa có |
| POST `/login` | Auth.dangNhap | tenDangNhap, matKhau, csrfToken | Thành công redirect `/home`, sai render login lỗi |
| POST `/logout` | Auth.dangXuat | csrfToken | invalidate session, redirect login |
| GET `/home` | Home.hienThiTrangChu | — | JSP đúng vai trò |
| GET `/customer/catalog` | Catalog.hienThiTimKiem/timKiem | tuKhoa tùy chọn | Không có param: chưa tìm; có param: validate và tìm |
| GET `/customer/catalog/detail` | Catalog.xemChiTiet | id, loai, tuKhoa | Chi tiết hoặc 404; giữ từ khóa khi quay lại |
| GET `/warehouse/suppliers` | Supplier.hienThiChonNhaCungCap/timNhaCungCap | tuKhoa, selectedId tùy chọn | Trang chọn NCC; selectedId chỉ dùng làm nổi bật NCC vừa thêm |
| GET `/warehouse/suppliers/new` | Supplier.hienThiThemNhaCungCap | tuKhoa tùy chọn | Form thêm |
| POST `/warehouse/suppliers/create` | Supplier.themNhaCungCap | ten,diaChi,dienThoai,email,csrfToken | Lưu, redirect danh sách với từ khóa tên mới + selectedId |
| POST `/warehouse/purchases/start` | Purchase.batDauNhap | supplierId,csrfToken | Tạo draft, redirect draft GET |
| GET `/warehouse/purchases/draft` | Purchase.hienThiBanNhap | maYeuCau,tuKhoa,selectedPartId,focusPartId tùy chọn | Render draft + kết quả tìm; selectedPartId chỉ chọn sẵn, chưa tự thêm dòng |
| POST `/warehouse/purchases/search-part` | Purchase.timPhuTung | maYeuCau,tuKhoa, toàn bộ dòng,csrfToken | Giữ dòng, redirect GET draft và tìm từ khóa |
| POST `/warehouse/purchases/add-line` | Purchase.themDongNhap | maYeuCau,selectedPartId, toàn bộ dòng,csrfToken | Thêm dòng 0 hoặc focus dòng cũ, redirect draft |
| POST `/warehouse/purchases/update` | Purchase.capNhatDongNhap | maYeuCau, toàn bộ dòng,csrfToken,dichDen? | Cập nhật; nếu dichDen=new-part thì redirect form thêm part |
| POST `/warehouse/purchases/remove-line` | Purchase.xoaDongNhap | maYeuCau,removePartId, toàn bộ dòng,csrfToken | Bỏ dòng được chọn, giữ dòng khác, redirect draft |
| POST `/warehouse/purchases/cancel` | Purchase.huyBanNhap | maYeuCau,csrfToken | Xóa draft, redirect home kho |
| POST `/warehouse/purchases/confirm` | Purchase.xacNhanHoaDon | maYeuCau, toàn bộ dòng,csrfToken | Lưu chính thức hoặc mở hóa đơn cũ; redirect view |
| GET `/warehouse/parts/new` | SparePart.hienThiThemPhuTung | maYeuCau | Form thêm, danh sách nhóm |
| POST `/warehouse/parts/create` | SparePart.themPhuTung | maYeuCau, dữ liệu part,csrfToken | Tạo part, redirect draft có selectedPartId |
| GET `/warehouse/purchases/view` | Purchase.xemHoaDon | id | Hóa đơn đã xác nhận |
| GET `/warehouse/purchases/print` | Purchase.inHoaDon | id | Bản in dữ liệu hóa đơn cũ |
| POST `/warehouse/purchases/pay` | Purchase.ghiNhanThanhToan | id,soTienDaTra,csrfToken | Ghi nhận đúng tổng hoặc đã trả, redirect view |
| GET `/warehouse/purchases/unpaid` | Purchase.hienThiHoaDonChuaThanhToan | — | Trang chủ kho kèm các hóa đơn chưa trả do chính người đang đăng nhập lập |

Không thêm `action` không được liệt kê để làm route khác. Tên class đầy đủ là các tên kết thúc `Servlet4` trong kế hoạch; cột trên viết ngắn để dễ đọc. `HomeServlet4` map `/home`; các Servlet khác nhận nhiều path chính xác trong web.xml.

Giá trị nhập số ở request dùng biểu diễn chuẩn như `80000` hoặc `80000.00`, không gửi `80.000` để biểu diễn tám mươi nghìn. Hiển thị định dạng Việt Nam tách riêng với value input; ví dụ bài báo cáo “80.000 đồng” là định dạng trình bày.

## 5. Tên param, request attributes, session

### Request param của part mới

`ten`, `nhomId`, `moTa`, `hinhAnh`, `donVi`, `hangSanXuat`, `thongSo`, `dongXePhuHop`, `giaBan`, `hanBaoHanh`, `dieuKienBaoHanh`, `trangThai`, `maYeuCau`, `csrfToken`.

Chỉ nhận danh sách này; `tonKho`, `idNhanVien`, `tongTien`, `thanhTien`, `thoiDiemXacNhan` không phải input được phép. Giá nhập không có trong form danh mục.

### Dòng hóa đơn nháp

- Dùng `partId` lặp lại cho từng dòng, `soLuong_<partId>`, `donGiaNhap_<partId>`; partId trong draft là duy nhất.
- Các giá trị nhập gửi đầy đủ khi tìm/chọn/xóa/cập nhật/xác nhận. Server chỉ cập nhật id đã có trong draft; muốn thêm phải qua add-line và đọc lại part từ DB.
- Đối với nút chọn part và nút xóa, dùng tên `selectedPartId` và `removePartId` để không nhầm với mảng `partId` của các dòng. Controller chuẩn hóa chúng thành id phụ tùng của hành động tương ứng.
- Một input parse lỗi được giữ dưới `formValues:Map<String,String>` ở request hoặc một mục form lỗi gắn draft trong session; không dùng giá trị cũ để xác nhận thay cho input lỗi. Baseline giữ `draftFormValues4:Map<String,Map<String,String>>` theo maYeuCau để không mất dữ liệu qua redirect.
- Form lỗi của bản nháp được cập nhật/xóa cùng draft và dưới cùng khóa session; confirm phải từ chối khi draft còn lỗi. Không để lỗi từ hai tab ghi đè nhau.
- Một form hóa đơn có thể dùng các nút `formaction` để tìm/chọn/cập nhật/xóa/xác nhận với cùng toàn bộ input; không tạo form lồng trong form. Khi xóa một dòng lỗi, chỉ bỏ lỗi của dòng đó; các dòng khác vẫn phải giữ nguyên input và lỗi.

### Request attributes

| JSP | Thuộc tính |
|---|---|
| Tất cả | `currentUser:SessionUser4`, `csrfToken:String` (lấy từ session), `errorMessage:String`, `fieldErrors:Map<String,String>`, `flashMessage:String`; thuộc tính không dùng có thể vắng |
| Login | `tenDangNhap:String`; không truyền password trở lại |
| Search | `tuKhoa:String`, `daTimKiem:boolean`, `dsDichVu:List<Service4>`, `dsPhuTung:List<SparePart4>` |
| Detail | `loai:String`, `dichVu:Service4` hoặc `phuTung:SparePart4`, `tuKhoa:String` |
| Chọn NCC | `tuKhoa:String`, `dsNhaCungCap:List<Supplier4>`, `selectedId:Integer` |
| Thêm NCC | `formValues:Map<String,String>` |
| Lập hóa đơn | `hoaDonNhap:PurchaseInvoice4`, `maYeuCau:String`, `tuKhoa:String`, `dsPhuTung:List<SparePart4>`, `formValues`, `selectedPartId:Integer`, `focusPartId:Integer` |
| Thêm part | `maYeuCau:String`, `dsNhom:List<SparePartCategory4>`, `formValues` |
| Hóa đơn/bản in | `hoaDonNhap:PurchaseInvoice4`, `duocThanhToan:boolean`; render các trường `...ApDung`; nút trả tiền chỉ bật khi chưa trả và người hiện tại là người lập |
| Home kho khi xem unpaid | `dsHoaDonChuaThanhToan:List<PurchaseInvoice4>` chỉ của người đang đăng nhập |

Search hiển thị hai list trong cùng một bảng, có loại tương ứng; không cần tạo một lớp CatalogItem4 hoặc bảng gộp mới. Nếu muốn thứ tự cố định: các dịch vụ tăng theo tên/id, sau đó phụ tùng tăng theo tên/id. `loai` chỉ chấp nhận đúng `service` hoặc `part`.

### Session attributes

| Khóa | Kiểu/ý nghĩa |
|---|---|
| `currentUser4` | SessionUser4; không lưu object có hash mật khẩu |
| `csrfToken4` | Token ngẫu nhiên chống request giả, khác mã lần nhập |
| `purchaseDrafts4` | Map<String,PurchaseInvoice4>, khóa là UUID maYeuCau |
| `draftFormValues4` | Map<String,Map<String,String>> giữ input dòng chưa hợp lệ theo draft |
| `flashMessage4` | String, đọc một lần sau redirect |

Không cần lưu kết quả tìm kiếm chung trong session: giữ từ khóa ở URL GET để tìm lại khi quay về. Logout invalidate toàn session, các draft chưa xác nhận mất; hóa đơn đã lưu mở lại được từ DB. Không cam kết phục hồi draft sau đóng trình duyệt/timeout.

CSRF: cả login và các POST khác đều bắt buộc. GET login tạo session/token, POST login verify trước khi xác thực, sau login đổi session id/tạo phiên mới và cấp token mới. AuthFilter không được chặn tạo token ở GET login vì chưa có session.

## 6. Tìm kiếm, validate và lỗi

- Từ khóa trim, bắt buộc 1–255 ký tự khi đã submit. Tìm `ten LIKE ? ESCAPE '!'`; escape `!` thành `!!`, `%` thành `!%`, `_` thành `!_`, sau đó thêm `%` hai bên. SQL luôn PreparedStatement. Không dùng tên bảng/cột lấy từ request.
- Danh sách rỗng khác với SQL exception. SQL lỗi hiển thị lỗi hệ thống và log phía server, không báo “không tìm thấy”.
- id/loai sai định dạng: 400. Chưa login: redirect login. Sai quyền/CSRF: 403. Không có dữ liệu hoặc mục không được công bố cho khách: 404. Sự cố DB: 500. Validation form: render form kèm lỗi (có thể HTTP 400), giữ dữ liệu nhập phù hợp.
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
| Service4.timDichVuTheoTen | ServiceDAO4.timDichVuTheoTen(String) |
| Service4.layChiTietDichVu | ServiceDAO4.layChiTietDichVu(int) |
| SparePart4.timPhuTungTheoTen | SparePartDAO4.timPhuTungTheoTen(String,boolean) |
| SparePart4.layChiTietPhuTung | SparePartDAO4.layChiTietPhuTung(int,boolean) |
| SparePart4.themPhuTung | SparePartDAO4.themPhuTung(SparePart4) |
| SparePart4.tangTonSauNhap | SparePartDAO4.tangTonSauNhap(Connection,int,BigDecimal), được PurchaseInvoiceDAO4 gọi trong cùng transaction xác nhận |
| Supplier4.timNhaCungCapTheoTen | SupplierDAO4.timNhaCungCapTheoTen(String) |
| Supplier4.themNhaCungCap | SupplierDAO4.themNhaCungCap(Supplier4) |
| Supplier4.layThongTinNhaCungCap | SupplierDAO4.layThongTinNhaCungCap(int) |
| WarehouseStaff4.layThongTinNhanVienKho | WarehouseStaffDAO4.layThongTinNhanVienKho(int) |
| SparePartCategory4.layDanhSachNhom | SparePartCategoryDAO4.layDanhSachNhom() |
| PurchaseInvoice4.tinhTongTien | Giữ tại model PurchaseInvoice4.tinhTongTien() |
| PurchaseInvoice4.luuHoaDonNhap | PurchaseInvoiceDAO4.luuHoaDonNhap(PurchaseInvoice4) |
| PurchaseInvoice4.layThongTinHoaDonNhap | PurchaseInvoiceDAO4.layThongTinHoaDonNhap(int) |
| PurchaseInvoice4.ghiNhanThanhToan | PurchaseInvoiceDAO4.ghiNhanThanhToan(int,BigDecimal,int) |

Biên → JSP chính xác:

| Biên phân tích | JSP |
|---|---|
| CustomerHomeUI4 | customer/gdChinhKH.jsp |
| CatalogSearchUI4 | customer/gdTimThongTin.jsp |
| ItemDetailsUI4 | customer/gdChiTietThongTin.jsp |
| WarehouseHomeUI4 | warehouse/gdChinhKho.jsp |
| SelectSupplierUI4 | warehouse/gdChonNhaCungCap.jsp |
| NewSupplierUI4 | warehouse/gdThemNhaCungCap.jsp |
| PurchaseInvoiceUI4 | warehouse/gdLapHoaDonNhap.jsp |
| NewSparePartUI4 | warehouse/gdThemPhuTung.jsp |
| ConfirmedInvoiceUI4 | warehouse/gdHoaDonNhapDaXacNhan.jsp |
| PrintInvoiceUI4 | warehouse/gdInHoaDonNhap.jsp |

Không tạo Java class `CustomerHomeUI4.java`, `PurchaseInvoiceUI4.java` để thay thế JSP trong project web. Trong UML có thể giữ tên biên cũ ở bảng ánh xạ; class diagram thiết kế dùng tên JSP/Servlet thực tế.

## 9. Quy tắc người lập, xe lưu qua ngày và phạm vi tương lai

1. Phiếu tiếp nhận có nhân viên tiếp nhận. Hóa đơn khách có nhân viên lập riêng; người lập có thể khác người tiếp nhận. Nhân viên lập hóa đơn khách cũng là người nhận tiền; nhân viên lập hóa đơn nhập cũng là người trả nhà cung cấp. Không thêm đối tượng hay FK người thanh toán riêng.
2. Danh sách hóa đơn nhập chưa trả chỉ lấy chứng từ của người hiện tại. Có thể mở/in chứng từ đã xác nhận theo quyền kho để tra cứu, nhưng thao tác pay phải kiểm tra `idNhanVienHienTai == tblEmployee4id`. Cả Servlet và DAO kiểm tra quyền; không chỉ ẩn nút. Nếu quay lại sau đăng xuất thì chính người lập đăng nhập lại. Không có nghiệp vụ bàn giao hóa đơn chưa trả cho nhân viên khác trong đợt này.
3. Ghi nhận thanh toán khóa header, kiểm tra người lập, kiểm tra số tiền đúng tổng và trạng thái; lần đầu ghi đồng thời trạng thái/thời điểm. Request lặp trả kết quả đã thanh toán, không sửa thời điểm hoặc tồn, không đổi người lập. Tài khoản ngừng làm không được đăng nhập/thanh toán, nhưng chứng từ và tên lịch sử vẫn đọc/in được bởi nhân viên có quyền; không giả định đã có chức năng bàn giao thay thế.
4. Chỉ hủy phiếu khi trạng thái Đã tiếp nhận, chưa bắt đầu công việc, chưa sử dụng phụ tùng thực tế và chưa có hóa đơn. Phiếu Đang xử lí/Hoàn thành không được hủy để giải phóng toàn bộ lượng giữ; nếu khách dừng sửa, đối chiếu công việc và vật tư thực tế rồi chốt hóa đơn theo phạm vi đã có. Không thêm nghiệp vụ trả hàng/hoàn tiền ở đợt này.
5. Đơn giá và bảo hành áp dụng được lấy khi thêm dịch vụ/part vào phiếu, giữ qua ngày và qua ca. Khi lập hóa đơn không tự thay bằng giá danh mục mới. Dòng phát sinh dùng giá khi thêm dòng đó; điều chỉnh giá đã thỏa thuận phải được đối chiếu với khách và ghi nhận trước xác nhận hóa đơn. Tên/đơn vị chốt cuối như quy tắc lịch sử; không ghi đè giá bằng một lần reload master.
6. Gắn lịch vào phiếu chỉ khi lịch Đã đặt và khớp khách đặt/xe với khách/xe trên phiếu. Khóa lịch trong transaction, tạo phiếu và chuyển lịch sang Đã tiếp nhận cùng transaction. Lịch đã nhận/hủy không được dùng lại hoặc đổi/hủy trực tiếp. FK và UNIQUE không tự kiểm tra được việc khớp khách/xe.
7. Mỗi xe chỉ có một phiếu chưa hủy và chưa thanh toán. Trước tạo phiếu, khóa hàng Car và kiểm tra phiếu cũ chưa có hóa đơn hoặc hóa đơn chưa trả; nếu có thì mở lại phiếu đó. Có hóa đơn đã thanh toán mới được tạo lần tiếp nhận sau. Đây là giới hạn một hồ sơ sửa đang mở cho mỗi xe, không bổ sung chức năng quản lí bàn giao/chìa khóa/giao xe thực tế.
8. Phiếu chỉ được Hoàn thành khi các công việc đã kết thúc. Nếu thêm dịch vụ chưa thực hiện sau khi đánh dấu Hoàn thành, chuyển lại Đang xử lí và chỉ chốt hóa đơn khi hoàn thành lại. Không sửa trạng thái/dòng phiếu sau khi hóa đơn đã xác nhận.

Các quy tắc 4–8 giữ theo nghiệp vụ DOCX, dành cho mô hình toàn hệ thống và module tiếp nhận/thanh toán khách tương lai; không tự xây thêm các module đó trong đợt code đề 26. Không thêm bảng log/phân công nhiều người/lịch sử đổi ca khi bài chỉ cần một kỹ thuật viên phụ trách mỗi dịch vụ.
