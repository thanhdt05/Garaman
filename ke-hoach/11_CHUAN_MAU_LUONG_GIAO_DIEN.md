# Tiêu chuẩn đối chiếu mẫu, luồng và giao diện — hai module

Áp dụng cùng 00. File này khóa **cách thể hiện hai module đã làm**, không thêm chức năng. Phải đọc trước khi sửa JSP, viết hình thiết kế hoặc nghiệm thu app. Kết quả kiểm tra app hiện có nằm ở 12; tiêu chuẩn dưới đây không có nghĩa app đã đạt.

**Điều chỉnh theo yêu cầu chủ bài ngày 21/10/2026:** kết quả tìm phụ tùng trên hóa đơn đổi từ dropdown sang bảng có nút Chọn ở từng dòng, giống tìm NCC. Quyết định này thay quy định dropdown trước đây và là khác biệt được cho phép so với prototype cũ. Không đổi route, DAO, CSDL hoặc quy tắc dòng nhập. Hướng dẫn sửa lời văn DOCX nằm ở `app/docs/SUA_DOCX_CHON_PHU_TUNG_DANG_BANG.md`.

## 1. Thứ tự căn cứ

1. **Bài làm `26_B23DCCN764-full.docx`:** nghiệp vụ, bước/ngoại lệ, trường dữ liệu, ví dụ và quan hệ. Chỉ đối chiếu phần hai module; các đoạn còn mâu thuẫn xử lý theo 01.
2. **`giao-dien-tham-chieu/web`:** bố cục và cách thao tác đã làm mẫu cho hai module. Dùng bản đã sửa trong workspace, không lấy lại bản Garaman1 cũ để ghi đè.
3. **Bài giảng 4.3.1 và LAB1:** phương pháp trình bày bốn bước, phân vai JSP/Servlet/DAO/model. Không lấy màu sắc hay luồng đăng ký học/nhập điểm trong bài giảng thay cho giao diện GaraMan.

Giữ sidebar/topbar, màu nền, kiểu chữ, độ rộng khung, panel, thứ tự khu vực, bố cục form/bảng, nhãn/nút và mẫu in của giao diện tham chiếu. Không tự thay bằng dashboard mới hoặc tự thêm inline style làm thay đổi toàn bộ bố cục. Đổi đường dẫn, include, form POST, hidden field và render JSTL là thay đổi kỹ thuật; không phải lý do đổi giao diện.

Cho phép các khác biệt cần để chạy thật: login thay trang chọn vai trò, tên từ phiên thay tên mẫu, dữ liệu DB thay HTML mẫu, lỗi theo trường, danh sách hóa đơn chưa trả đã có trong kịch bản và input kỹ thuật ẩn. Không hiển thị UUID, tên cột, snapshot, SQL hoặc hướng dẫn lập trình cho người sử dụng.

## 2. Đối chiếu tra cứu

| Điểm | Căn cứ trong bài làm | Hình thức/luồng phải giữ |
|---|---|---|
| TC-UI-01 | 1.2.1.a bước 1–2, P0468–P0469 | Từ trang chính khách mở tìm; ban đầu có ô tên và nút Tìm kiếm, chưa có bảng dữ liệu |
| TC-UI-02 | Bước 3–4, P0470–P0486 | Từ khóa lọc → **một bảng kết quả chung**: Mã, Tên, Loại, Đơn giá/Giá bán, Chọn/Xem chi tiết; mặc định có DV1 và PT1. Không tách hai bảng dịch vụ/phụ tùng |
| TC-UI-03 | Prototype gdTimDichVuPhuTung4.jsp | Giữ control Loại thông tin trong form như bản tham chiếu; mặc định Dịch vụ & phụ tùng. Chỉ lọc trình bày cùng kết quả đã tìm theo tên, không thay thao tác tìm tên |
| TC-UI-04 | Bước 5–6, ngoại lệ 5a | Click đúng service/part → chi tiết tương ứng. Có thể gộp hai JSP thành hai nhánh trong một JSP nhưng giữ bố cục và đủ trường riêng của mỗi loại |
| TC-UI-05 | Ngoại lệ 3a, 4a và quay lại | Trống/toàn khoảng trắng báo lỗi **trước query**; không có kết quả có thông báo riêng. Quay lại giữ từ khóa và bộ lọc đang chọn |

Quy ước để giữ control lọc mà không nhầm `loai` chi tiết: GET search và detail được truyền thêm `boLoc=all|service|part`, mặc định all. Param này chỉ là trạng thái UI; không tạo bảng/model/module. `loai=service|part` vẫn chỉ định đối tượng khi xem chi tiết. DAO tìm hai list theo tên như cũ; JSP render vào **cùng bảng**, áp boLoc khi trình bày. boLoc ngoài miền trả400. Form/query/back link giữ boLoc. Search chưa nhập tên không được trả danh sách dù đã chọn loại.

Thông tin mẫu: DV1 Thay lọc dầu động cơ, tiền công 120000; PT1 Lọc dầu động cơ AF-01, giá bán 150000, hãng AutoFilter, nhóm Phụ tùng động cơ; bảo hành part 6 tháng và điều kiện theo DOCX; dòng xe Chưa cập nhật. Không thêm ren/kích thước/bảo hành dịch vụ chỉ vì AI cho là hợp lý. Giá công không gộp vào giá part.

## 3. Đối chiếu nhập phụ tùng

| Điểm | Căn cứ | Hình thức/luồng phải giữ |
|---|---|---|
| NH-UI-01 | Bước 1–5 | Trang chủ kho → tìm/chọn NCC theo tên; danh sách có mã/tên/địa chỉ/điện thoại; chọn Hòa Phát mới mở hóa đơn nháp |
| NH-UI-02 | Ngoại lệ 4a | Thêm NCC → lưu → về danh sách để chủ động chọn; không tự chọn NCC rồi chuyển thẳng hóa đơn |
| NH-UI-03 | Bước 6, P0547 | Nháp có NCC, nhân viên, **ngày nhập dự kiến**, tìm part, bảng dòng đang trống và **tổng 0 luôn thấy được**, Xác nhận/Hủy. UUID chỉ nằm hidden, không thay ô ngày nhập |
| NH-UI-04 | Bước 7–9, P0549 và parts-picker.jspf; điều chỉnh được chủ bài yêu cầu 21/10/2026 | Tìm part ngay trên hóa đơn; kết quả là **bảng Mã/Tên phụ tùng/ĐVT/Hãng sản xuất/Chọn**. Bấm Chọn ở dòng cần nhập; không thêm tồn kho/trạng thái vào bảng kết quả |
| NH-UI-05 | Bước 10, P0560 | Người dùng chọn part → dòng mới **0/0**, thành tiền0, giữ cột hãng/đơn vị ở nháp; không lấy giá bán điền vào giá nhập |
| NH-UI-06 | Bước 11 và vòng lặp | Sửa lượng/giá → hiển thị tiền dự kiến, tổng; thêm nhiều part; chọn lại part chỉ focus dòng cũ. Tìm/chuyển form thêm part không mất input chưa lưu |
| NH-UI-07 | Ngoại lệ 8a, P0627 | Thêm part với nhóm đang dùng, đơn vị/hãng hợp lệ; tồn0, chưa đủ thông tin thì Chưa công bố. Lưu → trở lại đúng nháp và danh sách để người dùng chọn; không tự thêm dòng |
| NH-UI-08 | Bước 12–13 | Xác nhận trực tiếp từ trang lập hóa đơn → lưu thành công → trang đã xác nhận. Không thêm màn duyệt/xác nhận lần hai |
| NH-UI-09 | Bước 14–17, P0620 | Mở bản in riêng → In → đóng/hủy hộp thoại → quay về hóa đơn, có nút Quay lại dự phòng. Không coi in là đã thanh toán |
| NH-UI-10 | Bước 18–19, 18a/18b theo01 | Ô Số tiền đã trả + Đã thanh toán trên hóa đơn; trả đúng tổng, chính người lập. Chưa trả đủ giữ Chưa thanh toán; mở lại hóa đơn của mình theo kịch bản |
| NH-UI-11 | Ngoại lệ hủy, dòng trùng/input sai | Hủy chỉ nháp có xác nhận; không xóa NCC/part đã tạo. Dòng sai giữ input để sửa và chặn confirm. Xóa một dòng không làm mất các dòng khác |

Mỗi dòng kết quả có nút submit `name="selectedPartId"`, `value` là ID phụ tùng, `formaction` trỏ tới POST add-line. Các nút nằm trong form hóa đơn để gửi cùng toàn bộ lượng/giá đang sửa; không tạo form lồng nhau. Không cần JavaScript để chọn dòng và không gửi GET để thêm dòng. Sau thêm part mới, hiển thị lại bảng kết quả, đánh dấu phụ tùng vừa tạo và chờ nhân viên bấm Chọn; không tự thêm dòng. Nếu chọn trùng vẫn đưa focus về dòng cũ.

**Hóa đơn đã xác nhận/bản in:** tên NCC, nhân viên, part và đơn vị từ giá trị đã chốt; bảng **6 cột STT/Tên/ĐVT/Số lượng/Đơn giá nhập/Thành tiền**. Không hãng part, địa chỉ/điện thoại/email NCC; các thông tin đối chiếu vẫn có trước xác nhận. Trạng thái thanh toán hiện tại được hiển thị. Nhãn người dùng vẫn là Nhà cung cấp/Nhân viên kho/Ngày nhập, không ghi tên kỹ thuật “áp dụng”, “snapshot” hoặc “mã yêu cầu nháp”. Bản in A4 giữ mẫu nguồn, không tự thêm thông tin pháp nhân/thuế.

## 4. Chuẩn bài giảng và LAB1

- Mỗi module có đủ bốn bước: thiết kế giao diện → ánh xạ lớp biên sang JSP/Servlet và dữ liệu trao đổi → DAO (input/output/phạm vi) → thực thể và quan hệ liên quan.
- Có bốn hình chính: giao diện/luồng tra cứu, lớp thiết kế tra cứu, giao diện/luồng nhập, lớp thiết kế nhập. Hình giao diện phải thể hiện form/bảng/nút như bài giảng; không chỉ có các ô tên màn hình hoặc thay bằng hình tuần tự.
- JSP là view render, không bịa các method Java như formTimKiem/hienThiKetQua nếu không có trong code. Trên hình ghi stereotype JSP và danh sách input/output/nút hoặc note dữ liệu, không giả thành Java class đã viết.
- Servlet điều phối đúng LAB1; hình dùng đúng method đã triển khai, tham số/return/phạm vi truy cập. Không đưa method/field không có trong code lên UML. `+`, `-`, `#`, `~` phải đúng thực tế.
- DAO có lớp cơ sở abstract theo mẫu, truy vấn dùng Connection của thao tác; model giữ dữ liệu và công thức. Thay float→BigDecimal, Date→LocalDateTime và mảng→List phải có ánh xạ; không đổi quan hệ nghiệp vụ vì thay kiểu.
- 4.3.1 chỉ hai module; schema9/model10 theo00. Không chèn mô hình tổng thể hoặc các phần sau để tăng số lượng hình.

## 5. Kiểm chứng trước khi nói “đạt”

1. Đối chiếu mỗi hàng TC-UI/NH-UI với source DOCX, JSP nguồn và JSP app; ghi file/vị trí lỗi nếu khác.
2. Build Java và **kiểm tra JSP trên Tomcat9**; `mvn test` thông thường không biên dịch JSP và không chứng minh UI chạy.
3. Chạy app với DB test/demo riêng, thực hiện ví dụ lọc → DV1/PT1; Hòa Phát → PT1 →10×80000→800000; xem/in/pay và các ngoại lệ theo02/10.
4. Chụp cùng trạng thái ở app và prototype với viewport1440×1000, mobile390×844; so sidebar/panel/form/bảng/dropdown/nút/thứ tự. Chụp bản in dưới print media. Chỉ cho phép khác do dữ liệu thật và đăng nhập hỗ trợ; không yêu cầu so từng pixel do trình duyệt/font.
5. Ảnh prototype không được chép sang thư mục app rồi gọi là ảnh app thật. Ghi rõ ảnh nào của prototype, ảnh nào của app đang chạy.
6. Unit PASS, integration chưa chạy và E2E chưa chạy là ba kết quả khác nhau. Các ca có đọc code nhưng chưa chạy phải ghi Chưa chạy, không đánh PASS hoặc “đồng bộ100%”.

Chỉ nghiệm thu giao diện/luồng khi các hàng trên có bằng chứng. Nếu thiếu Tomcat/DB, bàn giao mã và ghi rõ phần chưa kiểm chứng; không bảo đảm bằng lời hoặc lấy số lượng unit test thay cho kiểm tra hai luồng.


## Ràng buộc sửa chữa theo yêu cầu chủ bài

Giữ nguyên các màn hình, nút/thao tác và luồng gọi trong thiết kế hai module của `26_B23DCCN764-full.docx` và giao diện tham chiếu. Sửa lỗi bên trong các hàm nghiệp vụ hiện có; không tự thêm màn, route, action, DAO nghiệp vụ hoặc hàm nghiệp vụ mới để xử lý lỗi. Dùng lại POST cập nhật khi rời hóa đơn sang form thêm phụ tùng để giữ dữ liệu. UUID/version/CSRF là tham số ẩn hỗ trợ kỹ thuật, không thành chức năng mới trên giao diện. Các hàm kỹ thuật hỗ trợ đã có không phải use case mới; không vẽ chúng thành nút hoặc luồng nghiệp vụ riêng. Nếu thiết kế gốc chưa đủ để quyết định một thay đổi nghiệp vụ, nêu đúng điểm thiếu để chủ bài quyết định.
