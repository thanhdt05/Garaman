# Các câu cần sửa trong DOCX: chọn phụ tùng bằng bảng

Áp dụng cho `26_B23DCCN764-full.docx`, theo yêu cầu chủ bài ngày 21/10/2026. Đã đối chiếu bản nguồn có SHA-256 `d29fe3d0d462ff6a868dc951ec039389ee0da1213097ed4f3b9e6ee09dcb984f`. File Word gốc được giữ nguyên; tài liệu này chỉ rõ câu cũ → câu thay thế để chủ bài sửa.

Giao diện mới: tìm theo tên ngay trên hóa đơn → hiển thị bảng **Mã | Tên phụ tùng | ĐVT | Hãng sản xuất | Chọn** → bấm **Chọn** tại dòng cần nhập → thêm dòng nhập 0/0; chọn lại phụ tùng đã có chỉ chuyển đến dòng cũ. Các DAO, tham số `selectedPartId`, CSDL và quy tắc xác nhận/tăng tồn vẫn dùng như trước.

Các mã P dưới đây là chỉ số đoạn trong bản trích xuất, không phải số trang Word. Dùng tiêu đề mục và tìm nguyên câu bằng Ctrl+F để xác định đúng vị trí.

## 1. Mục 1.2.1.b — kịch bản chính: bước 8 (bắt buộc)

**Câu cũ — P0549:**

> 8. Giao diện hiển thị danh sách phụ tùng có tên chứa từ khóa vừa nhập dưới dạng danh sách thả xuống để nhân viên chọn, gồm mã, tên, đơn vị tính và hãng sản xuất

**Thay toàn bộ bằng:**

> 8. Giao diện hiển thị bảng danh sách phụ tùng có tên chứa từ khóa vừa nhập, gồm các cột Mã, Tên phụ tùng, Đơn vị tính, Hãng sản xuất và nút Chọn ở từng dòng.

Ngay dưới bước 8, bảng ví dụ hiện có bốn cột Mã/Tên/Đơn vị/Hãng. **Thêm cột thứ năm “Chọn”**, ghi “Chọn” ở dòng PT1:

| Mã | Tên phụ tùng | Đơn vị tính | Hãng sản xuất | Chọn |
|---|---|---|---|---|
| PT1 | Lọc dầu động cơ AF-01 | Cái | AutoFilter | Chọn |

Đây là bảng kết quả tìm kiếm để chọn. Giữ nguyên bảng các dòng nhập ở bước 10, nơi có số lượng, đơn giá nhập và thành tiền.

## 2. Mục 1.2.1.b — kịch bản chính: bước 9 (làm rõ thao tác)

**Câu cũ — P0559:**

> 9. Nhân viên A chọn phụ tùng PT1.

**Thay bằng:**

> 9. Nhân viên A bấm nút Chọn tại dòng phụ tùng PT1 trong bảng kết quả tìm kiếm.

Bước 10 vẫn giữ nguyên: bổ sung PT1 vào bảng dòng nhập với lượng/giá/thành tiền ban đầu bằng 0. Các ngoại lệ chọn trùng, thêm phụ tùng mới và hủy nháp giữ nguyên ý nghĩa.

## 3. Mục 1.2.4.b — kịch bản v.2: câu hiển thị kết quả (bắt buộc)

Trong phần **“b. Modul nhân viên kho nhập phụ tùng” → “Kịch bản v.2 của modul…”**, tìm đoạn ngay sau khi `SparePart4` trả kết quả tìm phụ tùng.

**Câu cũ — P0918:**

> Lớp PurchaseInvoiceUI4 hiển thị danh sách phụ tùng dưới dạng danh sách thả xuống cho nhân viên chọn.

**Thay bằng:**

> Lớp PurchaseInvoiceUI4 hiển thị bảng danh sách phụ tùng gồm Mã, Tên phụ tùng, Đơn vị tính, Hãng sản xuất và nút Chọn ở từng dòng cho nhân viên.

**Câu ngay sau đó — P0919:**

> Nhân viên A chọn phụ tùng PT1.

**Thay bằng:**

> Nhân viên A bấm nút Chọn tại dòng phụ tùng PT1 trong bảng kết quả tìm kiếm.

Các lời gọi tìm theo tên, lấy chi tiết phụ tùng và trả kết quả phía sau giữ nguyên. Không thêm một lời gọi “mở dropdown” hoặc một màn chọn phụ tùng riêng.

## 4. Mục 1.2.3.b — thành phần của PurchaseInvoiceUI4 (làm rõ input/output)

Trong **“b. Phân tích tĩnh cho modul nhân viên kho nhập phụ tùng” → “Bước 4, giao diện lập hóa đơn nhập…”**, thay đúng dòng sau.

**Câu cũ — P0769:**

> Danh sách phụ tùng tìm được để chọn thêm vào hóa đơn: vừa output, vừa submit

**Thay bằng:**

> Bảng kết quả tìm phụ tùng gồm Mã, Tên phụ tùng, Đơn vị tính và Hãng sản xuất: kiểu output; nút Chọn ở từng dòng: kiểu submit, gửi mã phụ tùng của dòng được chọn để thêm vào hóa đơn.

Giữ nguyên các thuộc tính về bảng dòng nhập, số lượng, đơn giá nhập và tổng tiền. Không thêm thuộc tính nghiệp vụ hoặc phương thức DAO mới chỉ vì đổi cách hiển thị kết quả.

## 5. Mục 1.1.2 — mô tả bằng ngôn ngữ tự nhiên (nên làm rõ)

Trong tiểu mục **“Nhân viên kho nhập phụ tùng từ nhà cung cấp”**, đoạn bắt đầu bằng “Sau khi chọn nhà cung cấp…” đã ghi danh sách chung, chưa ghi dropdown nên không mâu thuẫn. Có thể thay toàn bộ đoạn P0331 bằng đoạn dưới để mô tả đồng nhất rõ ràng:

**Đoạn cũ — P0331:**

> Sau khi chọn nhà cung cấp, hệ thống hiển thị giao diện lập hóa đơn nhập gồm thông tin nhà cung cấp, nhân viên nhập, ngày nhập, ô tìm phụ tùng và bảng các phụ tùng được chọn → nhân viên nhập tên phụ tùng cần nhập và chọn Tìm kiếm → hệ thống hiển thị danh sách phụ tùng phù hợp gồm mã, tên, đơn vị tính và hãng sản xuất → nhân viên chọn phụ tùng cần nhập.

**Thay bằng:**

> Sau khi chọn nhà cung cấp, hệ thống hiển thị giao diện lập hóa đơn nhập gồm thông tin nhà cung cấp, nhân viên nhập, ngày nhập, ô tìm phụ tùng và bảng các phụ tùng được chọn → nhân viên nhập tên phụ tùng cần nhập và chọn Tìm kiếm → hệ thống hiển thị bảng danh sách phụ tùng phù hợp gồm các cột Mã, Tên phụ tùng, Đơn vị tính, Hãng sản xuất và nút Chọn ở từng dòng → nhân viên bấm nút Chọn tại dòng phụ tùng cần nhập.

Đoạn kế tiếp mô tả dòng mới 0/0, sửa lượng/giá và không tạo dòng trùng vẫn đúng, không cần sửa theo thay đổi giao diện này.

## 6. Các hình trong DOCX

- **Hình 1.6 — lớp phân tích modul nhập:** nếu thành phần tìm phụ tùng đang ghi chung “Danh sách phụ tùng” thì vẫn đúng. Nếu ghi dropdown/combobox, đổi thành “Bảng kết quả tìm phụ tùng (output)” và “Chọn tại từng dòng (submit)”. Giữ nguyên thực thể và các hàm nghiệp vụ.
- **Hình 1.11 — giao tiếp modul nhập:** nhãn thông điệp 23 trong hình đang là “chọn PT1”. Có thể đổi thành **“23: bấm Chọn tại dòng PT1”** cho khớp thao tác. Các thông điệp 17–29, đối tượng và mũi tên giữ nguyên số/thứ tự; không thêm bước mở dropdown.
- **Hình 1.10 — chuyển trạng thái:** không đổi trạng thái hoặc mũi tên chỉ vì đổi control. Thao tác tìm/chọn phụ tùng vẫn diễn ra trên giao diện lập hóa đơn nhập.
- **Use case, sơ đồ lớp thực thể và CSDL:** không cần thêm/xóa lớp, bảng, quan hệ hoặc use case cho thay đổi này.

## 7. Mục 4.3.1.b — khi viết phần thiết kế tĩnh

Trong bản DOCX đã đối chiếu, mục 4.3.1.b mới có tiêu đề, chưa có câu dropdown cần thay. Khi điền bước 1–2, dùng đoạn sau:

> Trên giao diện lập hóa đơn nhập, nhân viên nhập tên phụ tùng và chọn Tìm kiếm. Hệ thống hiển thị bảng kết quả gồm Mã, Tên phụ tùng, Đơn vị tính, Hãng sản xuất và nút Chọn ở từng dòng. Khi nhân viên bấm Chọn, hệ thống thêm phụ tùng tương ứng vào bảng dòng nhập; số lượng, đơn giá nhập và thành tiền ban đầu bằng 0. Nếu phụ tùng đã có trong hóa đơn thì chuyển đến dòng hiện có, không tạo dòng trùng. Các lượng và đơn giá đang sửa được giữ lại khi tìm hoặc chọn thêm phụ tùng.

Trên biểu đồ lớp theo mẫu bài giảng ghi **`dsPhuTung: List<SparePart4>`, `tblKetQuaPhuTung: Table`, `btnChonPhuTung: submit`**, thay cách ghi `selectedPartId: select` nếu đã sao chép hình cũ. `selectedPartId` là tên tham số khi cài đặt nút Chọn, có thể ghi trong bảng dữ liệu trao đổi bên dưới thay vì đưa lên hình chính. Mã và hình PlantUML trong `app/docs/diagrams/purchase-design.puml` cùng `purchase-ui-flow.puml` đã cập nhật theo bảng.

Ảnh giao diện chèn vào báo cáo phải chụp lại bản app có bảng; prototype cũ còn dropdown chỉ là mẫu lịch sử. Không thay toàn bộ từ “danh sách thả xuống” trong DOCX: phần chọn nhóm phụ tùng vẫn có thể dùng dropdown và không liên quan thay đổi này.
