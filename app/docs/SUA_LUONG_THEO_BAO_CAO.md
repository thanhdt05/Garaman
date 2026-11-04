# Sửa luồng code theo báo cáo — 30/10/2026

Nguồn nghiệp vụ là `26_B23DCCN764.docx`: kịch bản hai modul ở mục 1.2.1, các nhánh ngoại lệ và scenario nhập phụ tùng ở mục 2.3.2, đặc biệt bước ghi nhận người thực sự trả tiền. Áp dụng các đính chính đã chốt: nhà cung cấp là dữ liệu do nhân viên kho nhập; chọn phụ tùng bằng bảng như chọn nhà cung cấp; `[]` trong bài biểu diễn danh sách.

## Các chỗ đã sửa

- Nhân viên kho có quyền ở ca hiện tại được mở và thanh toán hóa đơn nhập do nhân viên khác lập. Không so sánh người trả với người lập để cấp quyền nữa.
- `recordPayment(invoiceId, amountPaid, currentWarehouseStaffId)` lấy mã người trả từ session qua Servlet; kiểm tra lại vai trò, trạng thái tài khoản và trạng thái làm việc trong CSDL, cùng transaction thanh toán. Tên người trả cũng lấy từ CSDL, không nhận từ form.
- Lưu trạng thái, thời điểm, mã và tên người thực sự trả tiền trong một transaction. Giữ nguyên `tblEmployee4id`, `warehouseStaffNameSnapshot` của người lập. Gửi lại yêu cầu không thay đổi người trả/thời điểm trả lần đầu.
- Danh sách hóa đơn chưa thanh toán dùng chung cho nhân viên kho có quyền, gồm cả hóa đơn do ca trước lập. Đổi nhãn giao diện cho đúng phạm vi này và hiển thị thông báo khi danh sách trống.
- Số tiền phải bằng đúng tổng hóa đơn; không làm tròn khoản tiền lệch để chấp nhận thanh toán. Thanh toán, xem và in không tăng tồn kho.
- Khi xác nhận nhập hàng, DAO kiểm tra lại cả vai trò kho, trạng thái tài khoản và trạng thái làm việc, để phiên đăng nhập cũ không vượt qua quyền đã bị thay đổi.

Các thay đổi nằm trong các hàm đã có. **Không thêm hàm ứng dụng, lớp, gói Java, model hay bảng.** Giữ chữ ký các hàm nghiệp vụ; `getUnpaidInvoices(int warehouseStaffId)` dùng tham số để kiểm tra quyền người xem, không lọc theo người lập. Các hàm hỗ trợ hiện có tiếp tục được dùng.

## Hai cột SQL cần để lưu người trả

| Cột trong tblPurchaseInvoice4 | Mục đích |
|---|---|
| `paidByEmployee4id` | Khóa ngoại tới nhân viên thực sự trả tiền. |
| `paidByNameSnapshot` | Chốt tên người trả tại thời điểm ghi nhận. |

Đây là dữ liệu lưu cho yêu cầu của scenario, không thêm ô nhập hoặc thao tác giao diện. Không thêm getter/setter hoặc trường model mới chỉ để hiển thị chúng; thông tin người lập trên hóa đơn vẫn giữ nguyên theo thiết kế giao diện đã có.

**CSDL mới:** chạy `01_schema.sql`, sau đó `02_seed.sql` theo README. Schema mới đã có hai cột này.

**CSDL tên tiếng Anh đang có:** áp dụng [06_migrate_purchase_payment.sql](../database/06_migrate_purchase_payment.sql) một lần trước khi chạy WAR mới, sau khi sao lưu và dừng ứng dụng. Không nạp lại seed lên dữ liệu đang sử dụng. Nếu CSDL còn tên tiếng Việt, cần hoàn thành migration đổi tên 04 trước migration 06.

Migration không gán người lập thành người trả cho hóa đơn lịch sử. Hóa đơn đã thanh toán trước migration giữ nguyên trạng thái/thời điểm và để trống người trả vì dữ liệu cũ không chứng minh ai đã thực sự trả. Các lần thanh toán mới được code ghi đầy đủ hai cột; bản ghi đã trả không bị ghi đè khi gửi lại yêu cầu.

## Đối chiếu luồng hai modul

| Luồng trong bài | Hành vi code được giữ/kiểm chứng |
|---|---|
| Mở tìm kiếm → nhập tên → chọn kết quả → chi tiết | Mở lần đầu không truy vấn danh sách; tìm đồng thời dịch vụ và phụ tùng được công bố; chọn đúng loại để xem. Không lộ phụ tùng mới chưa công bố. |
| Tìm/chọn NCC → lập hóa đơn dự kiến | Tìm NCC đang hoạt động; lấy NCC rồi lấy nhân viên kho từ session qua DAO. Nháp chưa tạo hóa đơn chính thức và chưa tăng tồn. |
| Tìm/chọn/thêm phụ tùng | Hiển thị kết quả dạng bảng theo đính chính đã chốt; phụ tùng mới tồn bằng 0; phải chọn để thêm dòng. Giữ lượng/giá đã nhập khi tìm hoặc mở form thêm mới. |
| Chọn trùng phụ tùng | Chuyển tới dòng đã có, không thêm dòng trùng. Dòng mới có số lượng, đơn giá, thành tiền bằng 0. |
| Nhập lượng/giá → xác nhận | Chặn hóa đơn rỗng, lượng/giá không hợp lệ và lượng lẻ cho đơn vị nguyên. Lưu đầu hóa đơn, dòng, thông tin áp dụng và cộng tồn trong cùng transaction; retry không cộng tồn thêm. |
| Hủy trước xác nhận | Hỏi xác nhận hủy, bỏ nháp, về trang chính; không đổi tồn/hóa đơn chính thức hoặc xóa danh mục đã thêm. |
| Xác nhận → in → quay lại | Đọc hóa đơn đã lưu để in; sau hộp thoại in quay lại đúng hóa đơn đã xác nhận. In không tự đánh dấu đã thanh toán. |
| Chưa trả đủ/dừng sau xác nhận → mở lại | Hóa đơn/tồn đã xác nhận được giữ; hóa đơn chưa trả xuất hiện trong danh sách dùng chung cho nhân viên kho có quyền. |
| Nhân viên ca hiện tại ghi nhận đã trả đủ | Kiểm tra quyền hiện tại, số tiền, trạng thái; chốt người trả và thời điểm; giữ người lập và tồn kho. Hai người cùng ghi nhận chỉ một transaction thành công. |

In và giao tiền thực tế là thao tác của người dùng; ứng dụng không tự trả tiền cho nhà cung cấp hoặc coi việc mở trang in là đã thanh toán.

## Kiểm chứng và phạm vi triển khai

Kết quả chi tiết được lưu trong [report-flow-check.json](diagrams/report-flow-check.json). Bộ kiểm tra gồm unit Servlet/model/validation, MySQL thật cho transaction, rollback, quyền hiện tại, đổi ca và thanh toán đồng thời; kiểm tra migration từ schema cũ và hai luồng trên trình duyệt.

Kết quả: **28 unit test, 7 MySQL integration test, không bỏ qua kiểm thử; 8 nhóm kiểm tra trình duyệt đạt, không có lỗi JavaScript**. Đối chiếu source trước/sau: 24 lớp Java, 9 bảng, không thêm hàm hoặc thay chữ ký; DOCX/PDF nguồn giữ nguyên.

Môi trường kiểm thử riêng: Tomcat 9.0.86, MySQL 8.0.43, JDK 25.0.2 biên dịch `--release 17`. Java 17/MySQL 8.4 vẫn là cấu hình mục tiêu, chưa được chạy trực tiếp trong lượt này. Không triển khai lên Tomcat/CSDL đang sử dụng; không sửa DOCX/PDF nguồn.
