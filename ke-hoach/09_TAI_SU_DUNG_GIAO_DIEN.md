# Chuyển giao diện tham chiếu sang app JSP/Servlet

## 1. Nguồn và đích

Nguồn ban đầu: `E:\java\Garaman1\web`. Bản đã sửa, dùng để bàn giao cùng kế hoạch: `Garaman/giao-dien-tham-chieu/web`. Không yêu cầu AI nhận bàn giao phải có ổ E hoặc còn thư mục Garaman1. Chi tiết kiểm tra nằm ở file 08.

Prototype vẫn dùng sessionStorage và HTML mẫu để xem bố cục. Chỉ app trong `app/` mới thực hiện hợp đồng backend. Không đem `sessionStorage`, tên người dùng hardcode, mã HN1 hardcode hay tự tạo hóa đơn khi mở trang vào app thật. Giữ bố cục/màu sắc/ảnh minh họa của bản đã sửa; không tự thay thành một sản phẩm khác.

Đích JSP đều nằm trong `app/src/main/webapp/WEB-INF/view/`; người dùng đi qua Servlet, không dùng URL JSP trực tiếp. Tên JSP đích tuân thủ bộ 02/03 hiện có, dù không có hậu tố 4; hậu tố 4 vẫn giữ ở lớp Java và bảng. Không duy trì hai bộ tên trong app.

## 2. Ánh xạ từng file

| Nguồn tương đối trong web/ | Đích tương đối trong app/src/main/webapp/ | Thao tác |
|---|---|---|
| index.jsp | Không nhập | Là màn chọn vai trò demo; thay bằng GET /home và login thật |
| gdChinhKH4.jsp | WEB-INF/view/customer/gdChinhKH.jsp | Giữ layout; tên lấy currentUser; link tới /customer/catalog; logout POST |
| gdTimDichVuPhuTung4.jsp | WEB-INF/view/customer/gdTimThongTin.jsp | Thay bảng mẫu bằng hai list DAO; GET tuKhoa; chưa submit thì chưa có kết quả |
| gdChiTietDichVu4.jsp | WEB-INF/view/customer/gdChiTietThongTin.jsp | Nhánh loai=service; nội dung từ dichVu |
| gdChiTietPhuTung4.jsp | Cùng file gdChiTietThongTin.jsp | Nhánh loai=part; nội dung từ phuTung; không tạo file đích thứ hai |
| gdChinhNVKho4.jsp | WEB-INF/view/warehouse/gdChinhKho.jsp | Thay tên; thêm danh sách chưa trả của người lập lấy từ DB, không dùng resume một hóa đơn trong tab |
| gdChonNhaCungCap4.jsp | WEB-INF/view/warehouse/gdChonNhaCungCap.jsp | GET tìm, POST chọn supplierId để start; dsNhaCungCap từ DAO |
| gdThemNhaCungCap4.jsp | WEB-INF/view/warehouse/gdThemNhaCungCap.jsp | Form POST create, lỗi và formValues từ server; lưu xong quay về để chọn |
| gdNhapPhuTung4.jsp | WEB-INF/view/warehouse/gdLapHoaDonNhap.jsp | Một form chứa dòng và các nút formaction; mã/version nháp; tìm part ngay trên trang |
| gdThemPhuTung4.jsp | WEB-INF/view/warehouse/gdThemPhuTung.jsp | dsNhom từ DB, nhom→nhomId, allowlist asset, không nhận tồn client; giữ UUID/version |
| gdXacNhanHoaDon4.jsp | WEB-INF/view/warehouse/gdHoaDonNhapDaXacNhan.jsp | Trang SAU commit; dữ liệu snapshot từ DB; POST pay chỉ người lập; không thêm bước duyệt trước xác nhận |
| gdHoaDonNhap4.jsp | WEB-INF/view/warehouse/gdInHoaDonNhap.jsp | GET print theo id DB; nút In/Quay lại; không tự tạo/chốt/pay |
| gdChonPhuTung4.jsp | Không nhập | Chỉ là redirect tên cũ; tìm/chọn part vẫn ngay trên hóa đơn |
| WEB-INF/jspf/header.jspf | WEB-INF/view/common/header.jspf | Đổi include và URL asset, currentUser, navigation đúng vai trò |
| WEB-INF/jspf/footer.jspf | WEB-INF/view/common/footer.jspf | Bỏ JavaScript mô phỏng, giữ footer ngắn |
| WEB-INF/jspf/steps.jspf | WEB-INF/view/common/steps.jspf | Đánh bước bằng request attribute buocNhap:int; không đoán bằng tên file trên URL |
| WEB-INF/jspf/parts-picker.jspf | WEB-INF/view/warehouse/parts-picker.jspf | Dùng dsPhuTung; không template chứa part mẫu; fragment nằm trong form chính |
| WEB-INF/jspf/invoice-lines.jspf | WEB-INF/view/warehouse/invoice-lines.jspf | c:forEach qua dsChiTietNhap; 6 cột, không hãng; dùng tên/đơn vị áp dụng |
| assets/styles.css | assets/css/styles.css + assets/css/print.css | Giữ layout trong styles.css; chuyển @page và @media print sang print.css, cả hai được header include; kiểm tra đường ảnh |
| assets/forms.css | assets/css/forms.css | Giữ form/table; lỗi server dùng cùng style |
| assets/part.svg | assets/images/part.svg | Ảnh minh họa cục bộ |
| assets/brake.svg | assets/images/brake.svg | Ảnh minh họa cục bộ |
| assets/garage.svg | assets/images/garage.svg | Ảnh minh họa cục bộ |
| assets/app.js | Không sao chép | Chỉ chuyển ba hàm preview/focus/hủy vào purchase.js và hai hàm in/quay lại vào print.js theo 02 |
| WEB-INF/web.xml | Viết mới WEB-INF/web.xml | Servlet 4.0 theo 10, không chép schema Jakarta 5.0 |

Các file mới không có nguồn prototype: `member/gdDangNhap.jsp`, `common/messages.jspf`, `common/error.jsp` và toàn bộ Java/SQL/config theo cây file 02. Không tạo thêm CRUD/hồ sơ/đặt lịch/thống kê để lấp chỗ trống trong navbar.

## 3. Trường và hành vi bắt buộc khi chuyển

| Nội dung | Prototype đã sửa | App phải làm |
|---|---|---|
| Người đăng nhập | Vẫn minh họa tên cố định | SessionUser4 từ login, Filter đọc lại quyền/hoạt động |
| Danh mục | HTML mẫu + danh mục thêm trong tab | SQL/DAO; PreparedStatement; dữ liệu công bố theo 03 |
| Search loại | Bộ lọc all/dichvu/phutung ở prototype | Baseline không có param lọc loại; tìm đồng thời hai loại, bỏ select lọc khỏi app. Link chi tiết chỉ dùng loai=service/part |
| Nháp | Một lần nhập trong tab, số tiền Number | UUID/session + version theo 10; BigDecimal server; không lấy JS total làm căn cứ |
| Part mới | Hãng bắt buộc, enum đơn vị, Chưa công bố mặc định | Cùng điều kiện tại Servlet/DAO, nhóm đang dùng, lưu tồn 0 |
| Giá bán | Không bắt buộc với Chưa công bố | Trống→NULL, không đổi thành 0. Đang kinh doanh cần giá >=0 và mô tả có nội dung |
| Hình | Chọn ảnh có sẵn | Lưu NULL hoặc `assets/images/part.svg`, `assets/images/brake.svg`; client không được gửi đường tùy ý |
| Lượng Lít | 3 số thập phân; prototype có giới hạn nhỏ hơn | Giới hạn đầy đủ DECIMAL trong 10, kiểm tra tại server |
| Xác nhận | Prototype tạo trạng thái trong tab | POST confirm, DB transaction, redirect GET view, tải snapshot |
| Tiền đã trả | Prototype Number | Parse BigDecimal, so sánh compareTo với tổng từ DB; pay/in không cộng tồn |
| Hóa đơn/bản in | Bỏ hãng, địa chỉ, điện thoại, email NCC | Chỉ các trường áp dụng và id lịch sử; không JOIN tên mới để in |
| Mở trực tiếp trang hóa đơn | Prototype có HTML fallback để chụp | App không có fallback; id không tồn tại→404; tuyệt đối không tạo chứng từ từ GET |
| In | window.print và afterprint quay lại | In/đóng/hủy hộp thoại chỉ quay lại; không chứng minh đã giao tiền |
| Sau logout/timeout | Prototype giữ trạng thái theo tab | App invalidate session; draft mất, hóa đơn commit vẫn trong DB và mở lại được |

Ngày/giờ prototype do trình duyệt dựng chỉ để trình bày. App lấy giờ server theo 03/10. Không lấy ngày 17/10/2026 trong HTML mẫu làm ngày mặc định của giao dịch mới.

## 4. Cách triển khai để giữ đúng UI

1. Chép CSS/SVG và chuyển JSP theo bảng; tạo 3 JSPF được bổ sung vào cây 02. Giữ đủ form, bảng và luồng, thay toàn bộ link bằng c:url tới Servlet.
2. Viết login/home và Filter; render màn hình qua request attributes, không cho JSP mở trực tiếp ngoài WEB-INF.
3. Thay từng nhóm dữ liệu theo 03: catalog → supplier → part → draft → confirmed/print/pay. Loại bỏ từng đoạn sessionStorage khi đã chuyển, không để hai nguồn dữ liệu đồng thời điều khiển một màn hình.
4. `gdLapHoaDonNhap.jsp`: không lồng form. Tìm, chọn, cập nhật, xóa, confirm đều gửi đủ ô dòng; trước thêm part dùng POST update với dichDen=new-part. Cancel chỉ cần UUID/version/CSRF, không bắt validate các ô đang sai mới được hủy.
5. Bảng hóa đơn xác nhận/in dùng chung invoice-lines.jspf. Nháp vẫn có hãng để đối chiếu; hai loại bảng có số cột khác nhau, không dùng nhầm fragment.
6. Chạy lại ảnh/kịch bản Hòa Phát–AF-01–10×80000; ảnh app chụp mới vào app/docs/screenshots. Ảnh prototype đặt ở giao-dien-tham-chieu/docs, không ghi là ảnh backend đã hoạt động.

Không nhập `.tools`, `.runtime`, WAR/ZIP, `tmp-doc-review`, các bản DOCX khác hoặc ảnh cũ của Garaman1 vào source app. `Start-GaraMan.ps1` của prototype chạy Tomcat 10.1 chỉ phục vụ review UI; app dùng hướng dẫn deploy Tomcat 9 riêng.
