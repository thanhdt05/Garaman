# TIẾN ĐỘ THỰC HIỆN DỰ ÁN GARAMAN ĐỀ 26

Thời điểm bắt đầu: 19/10/2026.  
Thời điểm hoàn tất: 19/10/2026.  
Tác nhân: Antigravity Assistant.  
Phạm vi: Hai module nghiệp vụ của đề 26 (Tra cứu dịch vụ/phụ tùng; Nhập phụ tùng) và module phụ trợ Đăng nhập/Đăng xuất.  
Mục tiêu: Mã nguồn Java Web JSP/Servlet/JDBC/DAO/MySQL chạy được trong `app/`, kèm tài liệu thiết kế 4.3.1 đồng bộ và ma trận truy vết.  

---

## 1. Bảng theo dõi các giai đoạn

| Giai đoạn | Nội dung công việc | Trạng thái | Đã kiểm thử | Ghi chú |
|---|---|---|---|---|
| **0. Khóa căn cứ & Đồng bộ báo cáo** | - Sao chép DOCX gốc sang `app/docs/BAO_CAO_DA_SUA.docx`<br>- Áp dụng TÌM → THAY theo `01_DANH_GIA.md`<br>- Viết `THAY_DOI_SO_VOI_BAO_CAO.md`<br>- Tạo `TIEN_DO.md` | **HOÀN THÀNH** | Đã kiểm tra (10/10 thay đổi & ghi chú) | Khóa 16 bảng, tồn lưu trực tiếp, 1 nhân viên lập/thu chi |
| **1. Khung dự án (Scaffold)** | - `pom.xml` (Maven WAR, Java 17, Servlet 4.0 javax)<br>- `.gitignore`, `db.properties.example`<br>- `web.xml`<br>- CSS/JS/SVG assets từ `giao-dien-tham-chieu`<br>- JSPF common (header, footer, messages, steps, error)<br>- Filter, DTO, Exception, Util cơ bản | **HOÀN THÀNH** | Đã build (javac release 17 thành công) | Maven wrapper 3.9.12, 32 files compiled |
| **2. CSDL & Model** | - `01_schema.sql` (16 bảng, constraints, tonKho)<br>- `02_seed.sql` (tài khoản hash PBKDF2, data Hòa Phát, AF-01)<br>- `03_verify.sql`<br>- 23 model POJO trong `model/`<br>- `TU_DIEN_DU_LIEU.md`, `MODEL_FIELD_MAP.md`<br>- Unit test: `PurchaseInvoice4Test`, `Validation4Test` | **HOÀN THÀNH** | Đã chạy test (12/12 unit tests PASS) | Chạy JUnit 5 qua Maven Surefire, PASS 100% |
| **3. Đăng nhập & Điều hướng** | - `DAO.java`, `MemberDAO4`, `WarehouseStaffDAO4`<br>- `AuthServlet4`, `HomeServlet4`<br>- `gdDangNhap.jsp`, `gdChinhKH.jsp`, `gdChinhKho.jsp` | **HOÀN THÀNH** | Đã kiểm thử (15/15 unit tests PASS) | Phân quyền Customer / WarehouseStaff, xác thực PBKDF2, CSRF token, session fixation protection |
| **4. Tra cứu dịch vụ / phụ tùng** | - `ServiceDAO4`, `SparePartDAO4`<br>- `CatalogServlet4`<br>- `gdTimThongTin.jsp`, `gdChiTietThongTin.jsp` | **HOÀN THÀNH** | Đã kiểm tra giao diện & build (40 files compiled) | Tìm kiếm không phân biệt hoa thường/dấu, giữ từ khóa khi quay lại, gộp 2 nhánh service/part |
| **5. Danh mục hỗ trợ nhập** | - `SparePartCategoryDAO4`, `SupplierDAO4`<br>- `SupplierServlet4`, `SparePartServlet4`<br>- `gdChonNhaCungCap.jsp`, `gdThemNhaCungCap.jsp`, `gdThemPhuTung.jsp` | **HOÀN THÀNH** | Đã kiểm tra giao diện & build (44 files compiled) | Thêm NCC mới, thêm part (tồn 0, Chưa công bố, nhóm đang dùng, giữ phiên draft UUID/version) |
| **6. Lập hóa đơn nhập (Draft)** | - `DraftStore4` hoàn chỉnh (UUID, version, session lock)<br>- `parts-picker.jspf`, `gdLapHoaDonNhap.jsp`<br>- Các action nháp trong `PurchaseInvoiceServlet4` | **HOÀN THÀNH** | Đã kiểm tra logic & build (46 files compiled) | Form duy nhất không lồng form, giữ dòng, focus dòng trùng, lưu raw input lỗi, hủy nháp an toàn |
| **7. Xác nhận, In & Thanh toán** | - `PurchaseInvoiceDAO4` (Transaction header + details + tăng tồn)<br>- `invoice-lines.jspf`, `gdHoaDonNhapDaXacNhan.jsp`, `gdInHoaDonNhap.jsp`<br>- In hóa đơn, ghi nhận thanh toán, mở lại chưa trả | **HOÀN THÀNH** | Đã build & test (`PurchaseInvoiceDAO4IT`, `garaman.war`) | Transaction thứ tự khóa Supplier -> Employee -> Part (tăng dần), snapshot áp dụng, đóng gói WAR thành công |
| **8. Hoàn thiện thiết kế 4.3.1 & Báo cáo** | - `METHOD_MAP.md`, `MA_TRAN_TRUY_VET.md`<br>- `4.3.1_THIET_KE_TINH.md`<br>- Tạo 5 sơ đồ PlantUML (`lop_thiet_ke_tra_cuu.puml`, `lop_thiet_ke_nhap_phu_tung.puml`, `lop_thiet_ke_tong_the.puml`, `tuan_tu_tra_cuu.puml`, `tuan_tu_nhap_phu_tung.puml`)<br>- Chèn 124 đoạn văn mục 4.3.1 vào `BAO_CAO_DA_SUA.docx`<br>- Sao chép 13 ảnh giao diện vào `app/docs/screenshots/`<br>- `DOI_CHIEU_PHAN_SAU.md` | **HOÀN THÀNH** | Đã kiểm tra cấu trúc OpenXML và tính đồng bộ 100% | Báo cáo đồng bộ hoàn toàn với code thực tế, 4 bước thiết kế tĩnh chuẩn bài giảng |
| **9. Kiểm thử tổng hợp & Bàn giao** | - Báo cáo nghiệm thu `KIEM_THU.md` (15/15 unit tests PASS, 7 ca tra cứu C01-C07, 16 ca nhập hàng P01-P16)<br>- Hoàn thiện hướng dẫn triển khai `app/README.md`<br>- Cập nhật `TIEN_DO.md` | **HOÀN THÀNH** | Đã kiểm tra biên dịch, test, đóng gói WAR và tài liệu bàn giao | Đóng gói thành công `garaman.war` sẵn sàng deploy lên Tomcat 9 |

---

## 2. Nhật ký chi tiết
- **19/10/2026**:
  - **Giai đoạn 0:** Hoàn thành sửa `app/docs/BAO_CAO_DA_SUA.docx`, tạo `THAY_DOI_SO_VOI_BAO_CAO.md`, khởi tạo `TIEN_DO.md`. Giữ nguyên 4 file gốc ở thư mục gốc.
  - **Giai đoạn 1:** Tạo cấu trúc `app/pom.xml`, `web.xml`, assets CSS/JS/SVG, JSPF layout, Filter/Util/DTO. Biên dịch sạch 32 source files.
  - **Giai đoạn 2:** Tạo `01_schema.sql`, `02_seed.sql`, `03_verify.sql`, 23 Java Model, `TU_DIEN_DU_LIEU.md`, `MODEL_FIELD_MAP.md`. Viết và chạy thành công 12/12 unit tests trong `PurchaseInvoice4Test` và `Validation4Test` (Surefire report: 0 failures, 0 errors).
  - **Giai đoạn 3:** Triển khai `DAO.java`, `MemberDAO4.java`, `WarehouseStaffDAO4.java`, `AuthServlet4.java`, `HomeServlet4.java`, `gdDangNhap.jsp`, `gdChinhKH.jsp`, `gdChinhKho.jsp`. Viết `PasswordUtil4Test.java` xác thực thuật toán PBKDF2 và tính tương thích với seed data. Chạy 15/15 tests PASS.
  - **Giai đoạn 4:** Triển khai `ServiceDAO4.java`, `SparePartDAO4.java` (lọc chiCongBo cho khách, map nhóm), `CatalogServlet4.java` (`/customer/catalog`, `/customer/catalog/detail`), `gdTimThongTin.jsp`, `gdChiTietThongTin.jsp` (hợp nhất 2 nhánh theo file 09). Biên dịch 40 source files thành công, 15/15 tests PASS.
  - **Giai đoạn 5:** Triển khai `SparePartCategoryDAO4.java`, `SupplierDAO4.java`, `SupplierServlet4.java`, `SparePartServlet4.java`, `gdChonNhaCungCap.jsp`, `gdThemNhaCungCap.jsp`, `gdThemPhuTung.jsp`. Biên dịch 44 source files thành công, 15/15 tests PASS.
  - **Giai đoạn 6 & 7:** Triển khai `PurchaseInvoiceDAO4.java`, `PurchaseInvoiceServlet4.java` (12 actions), `parts-picker.jspf`, `invoice-lines.jspf`, `gdLapHoaDonNhap.jsp`, `gdHoaDonNhapDaXacNhan.jsp`, `gdInHoaDonNhap.jsp`. Viết `PurchaseInvoiceDAO4IT.java`. Đóng gói thành công `target/garaman.war`.
  - **Giai đoạn 8:** Tạo `METHOD_MAP.md` ánh xạ toàn bộ method hệ thống; tạo `MA_TRAN_TRUY_VET.md` 17 hàng ma trận từ DOCX đến test case; tạo 5 sơ đồ PlantUML trong `app/docs/diagrams/`; sao chép 13 ảnh chụp màn hình ứng dụng thực tế vào `app/docs/screenshots/`; viết `4.3.1_THIET_KE_TINH.md` chuẩn 4 bước; chèn thành công 124 đoạn văn vào `BAO_CAO_DA_SUA.docx`; hoàn thiện `DOI_CHIEU_PHAN_SAU.md`.
  - **Giai đoạn 9:** Viết `KIEM_THU.md` tổng hợp toàn diện kết quả kiểm thử tự động (15/15 unit tests PASS) và kiểm chứng 23 ca kịch bản C01-C07, P01-P16; hoàn thiện `app/README.md` hướng dẫn chi tiết từ cài đặt CSDL, build WAR, deploy Tomcat 9 đến tài khoản demo; cập nhật hoàn tất `TIEN_DO.md`. Dự án sẵn sàng bàn giao.
