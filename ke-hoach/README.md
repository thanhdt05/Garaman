# Kế hoạch — chỉ hai module đề 26

**Luồng code cập nhật 30/10/2026:** đã sửa quyền thanh toán theo nhân viên kho hiện tại, lưu người thực sự trả và mở danh sách hóa đơn chưa thanh toán của gara; không thêm hàm ứng dụng. [Chi tiết và migration CSDL](../app/docs/SUA_LUONG_THEO_BAO_CAO.md) thay các mô tả cũ còn giới hạn theo người lập.

**Yêu cầu cấu trúc mới của chủ bài 30/10/2026:** bỏ các gói Java `exception`, `util`, `filter`, `dto`, chuyển xử lí cần thiết vào các hàm Servlet/DAO đã có, không thêm hàm ứng dụng. Chỉ dẫn cũ trong 02/03/10 về các gói hỗ trợ được thay bằng [bản rút gọn và kiểm chứng](../app/docs/RUT_GON_CODE_THEO_GOI.md). JSP nhân viên kho chuyển sang `view/warehouseStaff`; URL `/warehouse/...` giữ nguyên. Package diagram dùng toàn bộ 23 thực thể trong báo cáo, DAO/Servlet/JSP chỉ lấy các tên đã thiết kế cho hai modul; không mở rộng phạm vi code ngoài 00.

**Đọc [00_PHAM_VI.md](00_PHAM_VI.md) trước.** Kế hoạch chỉ triển khai hai module bạn đã làm: khách tra cứu dịch vụ/phụ tùng và nhân viên kho nhập phụ tùng. Đăng nhập/đăng xuất chỉ hỗ trợ tiền điều kiện hai module.

**Quy ước tên mới được chủ bài chốt 21/10/2026:** [bộ tên tiếng Anh](../app/docs/DANH_SACH_DOI_TEN_TIENG_ANH.md) và [CSV cũ → mới](../app/docs/TEN_CU_TEN_MOI.csv). Hai biểu đồ lớp thiết kế/SVG và các định danh trong DOCX đã đồng bộ tên tiếng Anh theo yêu cầu của chủ bài, gồm 124 lượt đổi trong văn bản và 7 hình nhúng. Xem [nhật ký đổi tên DOCX](../app/docs/DOI_TEN_TOAN_BO_DOCX.md) và [CSV toàn báo cáo](../app/docs/TEN_CU_TEN_MOI_TOAN_BO_DOCX.csv). Mã nguồn `app/`, schema SQL và hợp đồng triển khai đã đồng bộ tên mới; xem [kết quả/backup/migration](../app/docs/CODE_ENGLISH_MIGRATION.md). CSDL và Tomcat đang chạy tại 8080 chưa được nâng cấp, chờ chủ bài đồng ý; VPP gốc giữ nguyên. Khi đồng bộ các phần sau phải dùng lại đúng bộ tên này, không tự đặt thêm tên dịch khác.

Đã bỏ yêu cầu cũ tạo model/schema cho cả hệ thống. App chỉ có **10 model và 9 bảng** liên quan; không tạo POJO/bảng/khung chức năng cho các module khác. Mô hình tổng thể trong DOCX giữ nguyên làm nguồn tham chiếu.

| File | Nội dung |
|---|---|
| [00_PHAM_VI.md](00_PHAM_VI.md) | Phạm vi cao nhất; danh sách model/bảng được tạo và đầu ra hai module |
| [01_DANH_GIA.md](01_DANH_GIA.md) | Những chỗ cần thống nhất giữa hai module và bài làm, không giao sửa cả báo cáo |
| [02_KE_HOACH.md](02_KE_HOACH.md) | Cây file, hàm, thứ tự code và test của hai module |
| [03_HOP_DONG.md](03_HOP_DONG.md) | 9 bảng, ánh xạ model, route/param/request/session |
| [04_PROMPT_GEMINI.md](04_PROMPT_GEMINI.md) | Prompt dùng cho OpenCode/DeepSeek, Gemini hoặc AI khác |
| [05_RA_SOAT_THUOC_TINH_BO_SUNG.md](05_RA_SOAT_THUOC_TINH_BO_SUNG.md) | Kiểm tra các trường liên quan hóa đơn nhập đã có trong bài làm |
| [06_KIEM_TRA_BAN_MOI.md](06_KIEM_TRA_BAN_MOI.md) | Kết luận và phạm vi đã sửa |
| [07_DOI_CHIEU_BAO_CAO.md](07_DOI_CHIEU_BAO_CAO.md) | 4.3.1 theo bốn bước bài giảng cho đúng hai module |
| [08_KET_QUA_RA_SOAT_NGUON.md](08_KET_QUA_RA_SOAT_NGUON.md) | Căn cứ nguồn; mô hình tổng thể chỉ để tham chiếu |
| [09_TAI_SU_DUNG_GIAO_DIEN.md](09_TAI_SU_DUNG_GIAO_DIEN.md) | Chuyển từng file giao diện tham chiếu sang hai module |
| [10_HOP_DONG_TRIEN_KHAI_BO_SUNG.md](10_HOP_DONG_TRIEN_KHAI_BO_SUNG.md) | Chi tiết kỹ thuật phục vụ hai module, không mở rộng nghiệp vụ |
| [11_CHUAN_MAU_LUONG_GIAO_DIEN.md](11_CHUAN_MAU_LUONG_GIAO_DIEN.md) | Khóa bố cục/mẫu/luồng; từng bước DOCX → màn hình → điều kiện nghiệm thu |
| [12_RA_SOAT_APP_HIEN_TAI.md](12_RA_SOAT_APP_HIEN_TAI.md) | Lỗi đã thấy ở app hiện có, unit15 PASS thực tế và các phần chưa kiểm chứng |

Thứ tự đọc: 00 → phần hai module trong tài liệu gốc → 01/05 → 03/02 → 09/10/11 → 07; đọc12 để sửa app hiện có. Dùng04 giao AI thực hiện. 00 khóa phạm vi,11 khóa mẫu/luồng/UI; không sửa kế hoạch để hợp thức hóa bố cục AI tự đổi. Nếu chỉ dẫn cũ đòi tạo phần khác, áp dụng00.

Đầu ra: `app/` chạy được hai module, cấu hình/schema/seed tương ứng, README demo, kết quả test thật, nội dung 4.3.1 và bốn hình giao diện/lớp thiết kế của hai module. Không yêu cầu sửa toàn bộ DOCX, vẽ lại mô hình tổng thể hoặc chuẩn bị các chương tiếp theo.

Giao diện tham chiếu ở [giao-dien-tham-chieu](../giao-dien-tham-chieu/README.md), kết quả prototype ở [KET_QUA_KIEM_TRA.md](../giao-dien-tham-chieu/docs/KET_QUA_KIEM_TRA.md). App hiện đã có source và unit15 PASS nhưng còn lệch theo12, chưa nghiệm thu UI/luồng Tomcat/MySQL. PASS prototype hoặc unit không chứng minh backend/E2E đã chạy. Bốn tài liệu gốc giữ nguyên.
