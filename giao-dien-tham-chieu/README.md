# GaraMan — giao diện JSP mẫu

**Bản tham chiếu đã sửa ngày 19/10/2026**, sao chép từ `E:\java\Garaman1\web` vào workspace Garaman. Các file trong Garaman1 không bị sửa. Báo cáo nguồn chính khi rà soát là `../26_B23DCCN764-full.docx`. Xem [kết quả kiểm tra](docs/KET_QUA_KIEM_TRA.md), [cách đưa từng file vào app](../ke-hoach/09_TAI_SU_DUNG_GIAO_DIEN.md) và [kết quả đối chiếu nguồn](../ke-hoach/08_KET_QUA_RA_SOAT_NGUON.md).

Bản này không kèm Tomcat/Playwright để tránh chép lại runtime. Trên máy hiện tại, mở bằng:

```powershell
.\Start-GaraMan.ps1 -TomcatHome 'E:\java\Garaman1\.tools\apache-tomcat-10.1.61' -Port 8096
```

Truy cập `http://localhost:8096/GaraMan/`; dừng bằng `.\Stop-GaraMan.ps1`. Đây là Tomcat dành cho prototype, app backend theo kế hoạch vẫn dùng Tomcat9/javax.servlet. Bản copy không kèm WAR/ZIP cũ. Không triển khai WAR cũ từ Garaman1 rồi cho rằng đã có các sửa trong bản này.

Frontend cho phần thiết kế giao diện của **4.3.1 Thiết kế tĩnh**, đề 26, Đàm Tiến Thành (B23DCCN764). Đối chiếu theo bản **26_B23DCCN764-full.docx trong Garaman**, đặc biệt 1.1.2, 1.2.1 và các hình phân tích.

Dùng **JSP + HTML + CSS + JavaScript thuần**. Nội dung minh họa đặt trực tiếp trong HTML; không API, DAO, CSDL hoặc lớp cấp mock data. Lựa chọn trên giao diện chỉ giữ trong `sessionStorage` của tab. Việc xác nhận, thanh toán và thời điểm hiển thị đều là mô phỏng; chưa cập nhật tồn kho hoặc thực hiện giao dịch thực tế.

## Mở giao diện

Với máy khác, chỉ rõ đường dẫn Tomcat 10.1 đã giải nén:

```powershell
.\Start-GaraMan.ps1 -TomcatHome 'C:\Tools\apache-tomcat-10.1.x'
```

Mở **http://localhost:8080/GaraMan/**. Nếu PowerShell chặn script:

```powershell
powershell -ExecutionPolicy Bypass -File .\Start-GaraMan.ps1 -TomcatHome 'C:\Tools\apache-tomcat-10.1.x'
```

Script dùng Java trong PATH. Cần Java 17 trở lên. Ví dụ đường dẫn runtime:

```powershell
.\Start-GaraMan.ps1 -TomcatHome 'C:\Tools\apache-tomcat-10.1.x'
```

Nếu cổng 8080 bận, thêm `-Port 8081` và dùng URL với cổng 8081. Dừng bằng `Stop-GaraMan.ps1`. Nhật ký ở `.runtime/tomcat/logs/`. Server chỉ lắng nghe trên máy hiện tại.

Không mở JSP bằng nhấp đúp hoặc Live Server. Tomcat cần xử lý JSP trước khi trình duyệt hiển thị. Không cần Maven hay MySQL.

Có thể triển khai thủ công bằng cách chép nội dung `web/` của bản đã sửa vào `<Tomcat>/webapps/GaraMan/`.

## Các màn hình và lớp giao diện trong báo cáo

| Lớp giao diện pha phân tích | Trang JSP | Chức năng |
| --- | --- | --- |
| — | `index.jsp` | Chọn không gian để xem mẫu sau đăng nhập |
| `CustomerHomeUI4` | `gdChinhKH4.jsp` | Trang chính khách hàng |
| `CatalogSearchUI4` | `gdTimDichVuPhuTung4.jsp` | Nhập tên, tìm kiếm và xem kết quả |
| `ItemDetailsUI4` | `gdChiTietDichVu4.jsp`, `gdChiTietPhuTung4.jsp` | Hai cách hiển thị chi tiết theo loại đối tượng |
| `WarehouseHomeUI4` | `gdChinhNVKho4.jsp` | Bắt đầu hoặc tiếp tục thao tác nhập |
| `SelectSupplierUI4` | `gdChonNhaCungCap4.jsp` | Tìm và chọn nhà cung cấp |
| `NewSupplierUI4` | `gdThemNhaCungCap4.jsp` | Lưu tạm hồ sơ mới rồi trở lại danh sách để chọn |
| `PurchaseInvoiceUI4` | `gdNhapPhuTung4.jsp` | Tìm và chọn phụ tùng bằng dropdown ngay trên hóa đơn, sửa số lượng và đơn giá nhập |
| `NewSparePartUI4` | `gdThemPhuTung4.jsp` | Lưu tạm phụ tùng mới rồi trở lại hóa đơn để chọn |
| `ConfirmedInvoiceUI4` | `gdXacNhanHoaDon4.jsp` | Hóa đơn đã xác nhận, số tiền đã trả, ghi nhận thanh toán, mở bản in |
| `PrintInvoiceUI4` | `gdHoaDonNhap4.jsp` | Bản in riêng và nút In, quay lại hóa đơn sau khi đóng hộp thoại in |

Tên cũ `gdChonPhuTung4.jsp` được giữ như đường dẫn chuyển tiếp đến `gdNhapPhuTung4.jsp`. Không còn bước chọn phụ tùng trên một màn hình riêng. `gdXacNhanHoaDon4.jsp` hiện là hóa đơn **đã xác nhận**, không còn màn kiểm tra bổ sung trước xác nhận.

## Thao tác theo đúng kịch bản

**Khách hàng:** Trang chính → Tìm dịch vụ / phụ tùng. Ban đầu chưa có kết quả. Nhập **lọc** → Tìm kiếm. Có hai kết quả DV1 **Thay lọc dầu động cơ** (120.000 đồng tiền công) và PT1 **Lọc dầu động cơ AF-01** (giá bán 150.000 đồng). Chọn chi tiết rồi Quay lại: từ khóa, bộ lọc và kết quả được giữ. Từ khóa trống hoặc chỉ khoảng trắng được báo lỗi.

**Nhân viên kho:** Bắt đầu nhập phụ tùng → nhập **Hòa Phát** và Tìm kiếm → chọn NCC1. Màn hình lập hóa đơn có danh sách trống, tổng tiền 0 và ngày nhập hiện tại. Tại chính màn hình này, tìm **Lọc**, chọn **PT1 · Lọc dầu động cơ AF-01 · Cái · AutoFilter** trong dropdown. Dòng mới có số lượng, đơn giá và thành tiền bằng 0. Nhập **10** và **80000**, tổng **800.000 đồng**. Chọn Xác nhận hóa đơn → HN1 đã xác nhận, Chưa thanh toán. Chọn In hóa đơn nhập → bản in → In. Sau khi đóng hộp thoại in sẽ quay lại HN1. Nhập **800000** vào Số tiền đã trả → Đã thanh toán.

Phải nhập đúng tổng tiền để mô phỏng thanh toán đủ một lần. Nhập thiếu hoặc thừa chưa đổi trạng thái. In lại không thay đổi nội dung hoặc thanh toán. Hóa đơn đã xác nhận được hiển thị chỉ đọc; quay lại đường dẫn lập hóa đơn sẽ mở hóa đơn đã xác nhận. Từ trang chính có nút mở lại hóa đơn trong phiên.

**Thêm mới:** Lưu nhà cung cấp mới quay lại danh sách để chọn. Lưu phụ tùng mới quay lại hóa đơn, tìm sẵn tên vừa thêm trong dropdown, chưa tự tạo dòng. Tên, đơn vị, nhóm và hãng bắt buộc. Chọn đơn vị Cái/Chiếc/Bộ/Lít; mặc định Chưa công bố, chỉ được Đang kinh doanh khi có giá bán và mô tả. Ảnh dùng lựa chọn SVG cục bộ. Ô tồn ban đầu hiển thị 0, chỉ là minh họa.

**Các trạng thái khác:** Phụ tùng trùng đưa về dòng cũ. Lượng/giá phải dương; Cái/Bộ/Chiếc dùng lượng nguyên, Lít tối đa ba số thập phân. Hủy cần đồng ý, bỏ dòng nhưng giữ danh mục mới trong tab. Sau xác nhận và trên bản in không hiển thị hãng phụ tùng, địa chỉ/điện thoại/email NCC; bản nháp vẫn có thông tin đối chiếu.

Bắt đầu hóa đơn mới sẽ thay bản nháp / hóa đơn đang mô phỏng trong tab. Dữ liệu giao diện không phải lịch sử giao dịch. Có thể xóa toàn bộ trạng thái bằng cửa sổ riêng tư mới hoặc xóa sessionStorage của trang. Sau khi cập nhật mã, nhấn Ctrl+F5 để tải lại CSS/JavaScript nếu trình duyệt còn cache.

## Ảnh chụp và bản in

Xem [kết quả và ảnh kiểm tra](docs/KET_QUA_KIEM_TRA.md) và [hóa đơn A4](docs/anh-giao-dien/hoa-don-nhap-A4.pdf). Ảnh từ JSP chạy trên Tomcat. Có trạng thái ban đầu, kết quả tìm và hóa đơn đã điền; ví dụ Hòa Phát / AF-01 / 800.000 đồng thống nhất kịch bản.

Các trang hóa đơn đã xác nhận và bản in có nội dung HTML mẫu để mở trực tiếp trong một tab sạch phục vụ chụp hình. Luồng thao tác bình thường vẫn bắt đầu bằng hóa đơn trống; nếu tab đang có bản nháp chưa xác nhận thì cần xác nhận hợp lệ trước khi mở chứng từ.

## Kiểm tra và cấu trúc

Bộ kiểm tra ở `checks/ui-check.cjs`; khởi động Tomcat rồi chỉ rõ thư viện Playwright và URL:

```powershell
$env:PLAYWRIGHT_MODULE = 'E:\java\Garaman1\.tools\browser\node_modules\playwright'
$env:GARAMAN_URL = 'http://localhost:8096/GaraMan/'
node checks/ui-check.cjs
```

Kiểm tra trạng thái tìm kiếm ban đầu, kết quả sau khi quay lại, các nhánh thêm mới, dòng trùng, số lượng theo đơn vị, hủy, xác nhận, tiền thanh toán, bản in riêng và bố cục desktop / điện thoại. Các ảnh được cập nhật khi chạy kiểm tra.

```text
web/
  *.jsp                 Các màn hình
  assets/styles.css     Bố cục chung và bản in A4
  assets/forms.css      Form, dropdown, dòng nhập và thanh toán
  assets/app.js         Tương tác trình duyệt
  assets/*.svg          Hình minh họa cục bộ
  WEB-INF/jspf/         Phần giao diện dùng chung, nội dung dòng hóa đơn mẫu
  WEB-INF/web.xml       Cấu hình trang mặc định
```

Đối chiếu cập nhật tại [kế hoạch 09](../ke-hoach/09_TAI_SU_DUNG_GIAO_DIEN.md). Khi có backend, giữ HTML/CSS, thay dữ liệu mẫu bằng Java và submit Servlet; thực hiện xác thực, kiểm tra, lưu chứng từ/tồn tại máy chủ theo 02/03/10. Không sao chép sessionStorage vào app.
