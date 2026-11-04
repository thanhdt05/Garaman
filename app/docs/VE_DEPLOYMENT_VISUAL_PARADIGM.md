# Hướng dẫn vẽ deployment diagram trong Visual Paradigm

Phần viết để chèn báo cáo nằm ở [Biểu đồ triển khai hệ thống GaraMan](THIET_KE_TRIEN_KHAI.md). Tạo một Deployment Diagram cho hạ tầng web dùng chung của hệ thống; phần code đối chiếu hiện có là hai modul và đăng nhập hỗ trợ. Các nhãn trong hình dùng tiếng Anh, nội dung thuyết minh dùng tiếng Việt.

## 1. Nguồn đã tìm và sử dụng

Tra cứu ngày 30/10/2026, ưu tiên tài liệu chính thức PTIT và tài liệu của nhà phát triển công cụ.

| Nguồn | Kết quả kiểm tra và cách sử dụng |
|---|---|
| [Thư viện số PTIT: Giáo trình phân tích và thiết kế hệ thống thông tin](https://dlib.ptit.edu.vn/handle/HVCNBCVT/1263) | Thư viện ghi tác giả Trần Đình Quế, năm 2013, nhà xuất bản Học viện Công nghệ Bưu chính Viễn thông. Tệp toàn văn có trạng thái Restricted Access; không sử dụng nội dung chưa đọc làm căn cứ cho ký hiệu trong hình. |
| Bài giảng nguồn `BG HP TTTN 2 CNPM 2020 final.pdf`, mục 4.3.3, trang in 113 | Nêu việc xác định máy cài đặt, các modul/package trên máy và cách tương tác. Hình 4.15 là biểu đồ gói, không phải mẫu deployment diagram. Dùng định hướng phạm vi và cách viết của bài giảng. |
| [Visual Paradigm: What is Deployment Diagram?](https://www.visual-paradigm.com/guide/uml-unified-modeling-language/what-is-deployment-diagram/) | Căn cứ bố cục máy khách/máy chủ, node dạng khối 3D, node có thể chứa node và đường truyền thông giữa các node. |
| [Visual Paradigm: Deployment Diagram — Notation](https://www.visual-paradigm.com/VPGallery/diagrams/Deployment.html) | Căn cứ phân biệt Device Node, Execution Environment Node và Artifact; dùng cách đặt artifact bên trong môi trường triển khai. |
| [Visual Paradigm: How to Draw Deployment Diagram?](https://www.visual-paradigm.com/support/documents/vpuserguide/94/2582/7293_drawingdeplo.html) | Căn cứ thao tác tạo Deployment Diagram và node trong công cụ. |

Nguồn chính để vẽ ký hiệu là tài liệu chính thức của Visual Paradigm. Không gán sơ đồ mới này cho bài giảng PTIT hoặc coi metadata thư viện là nội dung hướng dẫn đã đọc.

Hai ảnh slide do chủ bài bổ sung: ảnh “Biểu diễn cấu hình mạng trong UML” là deployment diagram ở mức các device và communication path, có ghi bội số; ảnh về công nghệ trên ba tầng là sơ đồ kiến trúc triển khai ở mức khái quát, dùng các node và tên công nghệ/giao thức. Ảnh thứ nhất phân biệt HTML Client và GUI Client theo kiểu công nghệ máy khách. GaraMan hiện dùng chung trình duyệt web cho các vai trò nên bản mới gộp Customer Client và Warehouse Client thành một loại Web Client. Node biểu diễn loại thiết bị/môi trường triển khai; không bắt buộc tạo node riêng cho mỗi actor.

## 2. Cấu trúc cần vẽ

Tạo diagram bằng **Diagram → New → Deployment Diagram**. Các nhãn dưới đây là tên node kiểu, không phải tên instance: không thêm dấu hai chấm và không gạch chân tên.

| Phần tử | Loại trong Visual Paradigm | Đặt bên trong |
|---|---|---|
| Web Client | Device Node, nhãn stereotype `«device»` | Ngoài cùng, bên trái; đại diện loại máy khách dùng trình duyệt |
| Web Browser | Execution Environment Node, nhãn `«executionEnvironment»` | Web Client |
| GaraMan Server | Device Node, nhãn `«device»` | Ngoài cùng, bên phải |
| JVM (Java 17) | Execution Environment Node, nhãn `«executionEnvironment»` | GaraMan Server |
| Apache Tomcat 9.0.x | Execution Environment Node, nhãn `«executionEnvironment»` | JVM (Java 17) |
| garaman.war | Artifact | Apache Tomcat 9.0.x |
| Packages in garaman.war | Note, nét đứt gắn với artifact | Bên dưới garaman.war trong Tomcat; ghi view, controller, dao, model |
| MySQL 8.4 | Execution Environment Node, nhãn `«executionEnvironment»` | GaraMan Server, bên dưới JVM |
| garaman4 | Artifact biểu diễn dữ liệu CSDL | MySQL 8.4 |
| Printer | Device Node, nhãn `«device»` | Ngoài cùng, bên dưới Web Client; dùng tại máy thực hiện in hóa đơn |

Device và Execution Environment dùng khối hộp 3D như Visual Paradigm; không dùng hình thư mục của package diagram. Artifact dùng hình chữ nhật với biểu tượng tệp nhỏ ở góc trên bên phải. Có thể hiển thị nhãn `«artifact»` cùng tên tệp hoặc tên dữ liệu. Không dùng ký hiệu Component để thay artifact garaman.war.

Giữ các node lồng bên trong đúng mô hình chứa, không chỉ chồng hình lên nhau. CSDL garaman4 được biểu diễn ở mức khối dữ liệu bền vững của MySQL, gồm chín bảng trong phạm vi hai modul; không phải một tệp tên garaman4 hoặc script SQL cần chạy mỗi lần người dùng gửi yêu cầu.

Chú thích gắn với garaman.war xác định các gói được triển khai trên máy chủ; các gói này cùng thuộc một ứng dụng. Trong WAR, JSP nằm dưới WEB-INF/view; các lớp của controller, dao, model nằm dưới WEB-INF/classes theo package Java. Nhãn HTML/CSS/JavaScript trong Web Browser xác định phần được trình duyệt tiếp nhận/thực thi. Gói JSP view không được triển khai trên máy khách.

## 3. Ba đường kết nối

Dùng **Communication Path** (Association giữa các node), nét liền, không đầu mũi tên. Không dùng Dependency hoặc Deployment cho các đường truyền thông này.

| Từ | Đến | Nhãn | Cách nối |
|---|---|---|---|
| Web Browser trong Web Client | Apache Tomcat 9.0.x | HTTP | Đi từ cạnh phải trình duyệt sang cạnh trái Tomcat |
| Apache Tomcat 9.0.x | MySQL 8.4 | JDBC / MySQL protocol; dòng dưới TCP 3306 (localhost) | Đi từ cạnh dưới Tomcat tới cạnh trên MySQL, bên trong GaraMan Server |
| Web Client | Printer | Local printing | Đường thẳng đứng từ máy khách thực hiện in tới máy in |

JDBC là API/driver phía Java; dữ liệu trao đổi với MySQL qua giao thức MySQL trên TCP. Nhãn ghi cả hai để phân biệt API ứng dụng với giao thức truyền thông. Cổng 3306 và localhost lấy từ cấu hình mặc định. HTTP không ghi cổng vì cổng Tomcat thay đổi theo cách chạy; context của ứng dụng là /garaman.

Việc đặt garaman.war trong Tomcat đã thể hiện vị trí triển khai ở mức hình này; không cần vẽ thêm một đường `«deploy»` trùng lặp. Không có đường trình duyệt → MySQL. Không có node actor Supplier, node DAO/Servlet riêng hoặc đường Tomcat → Printer. Các lớp Java và các JSP nằm trong ứng dụng WAR; trình duyệt nhận nội dung HTML/CSS/JavaScript đã được máy chủ trả về.

Local printing thể hiện kết nối in của máy khách qua dịch vụ in hệ điều hành; trong luồng đang xét, nhân viên kho thực hiện in hóa đơn nhập. Đường này không có nghĩa mọi máy khách đều được trang bị một máy in hoặc mọi tài khoản có quyền in hóa đơn nhập. Đây là giả định triển khai phù hợp luồng nghiệp vụ cần bản in; chưa ấn định USB hay máy in mạng và không khẳng định máy in vật lí đã được lắp. Việc đóng hộp thoại in không xác nhận giấy đã được in thành công.

## 4. Đối chiếu cấu hình GaraMan

| Chi tiết trên hình | Căn cứ trong workspace |
|---|---|
| Một ứng dụng garaman.war | `app/pom.xml`: packaging=war, finalName=garaman, warName=garaman |
| Java 17 | `app/pom.xml`: maven.compiler.release=17; môi trường mục tiêu theo `ke-hoach/02_KE_HOACH.md` |
| Apache Tomcat 9.0.x | Mục tiêu theo `ke-hoach/02_KE_HOACH.md`; POM dùng javax.servlet-api 4.0.1 ở scope provided; web.xml dùng Servlet 4.0 |
| MySQL 8.4 | Môi trường mục tiêu theo `ke-hoach/02_KE_HOACH.md`; POM dùng mysql-connector-j 8.4.0 |
| CSDL garaman4 trên cùng máy chủ | Dòng db.url trong `app/src/main/resources/db.properties`: host localhost, port 3306, database garaman4 |
| Kết nối JDBC | `app/src/main/java/dao/DAO.java`: com.mysql.cj.jdbc.Driver và DriverManager.getConnection |
| Trình duyệt mở hộp thoại in | `app/src/main/webapp/assets/js/print.js`: printInvoice() gọi window.print() |
| Tám gói được đóng gói trong ứng dụng | [Biểu đồ gói](THIET_KE_GOI.md): view, controller, dao, model; chú thích trên artifact liệt kê đúng cùng danh sách |

Sơ đồ là phương án triển khai mục tiêu theo kế hoạch và cấu hình mặc định, không phải kết quả dò máy đang chạy. Cấu hình GARAMAN_DB_URL có thể ghi đè URL mặc định. Nếu thực tế đổi DB sang máy riêng thì phải tách MySQL và garaman4 sang một Device Node mới; chỉ thay nhãn host không đủ mô tả việc đổi máy. Java 17/MySQL 8.4 là phiên bản mục tiêu; nhật ký kiểm thử trước đây có lần dùng JDK/MySQL khác, không dùng nhật ký đó để khẳng định đã nghiệm thu môi trường mục tiêu.

Hình dùng tên tiếng Anh, Arial thường, nền trắng, nét đen mảnh, không đổ bóng. Không thêm IP, tên hệ điều hành hoặc kiểu kết nối máy in khi chưa có lựa chọn cụ thể. Không thay mô hình này bằng hai máy chủ vật lí chỉ để giống sơ đồ web ba máy thường gặp.

## 5. Tệp và cách tái tạo

- [SVG](diagrams/deployment-diagram.svg) · [PDF một trang](diagrams/deployment-diagram.pdf) · [PNG](diagrams/deployment-diagram.png).
- [PlantUML](diagrams/deployment-diagram.puml).
- [Kết quả kiểm tra](diagrams/deployment-diagram-check.json).
- Chạy: `py -3 -X utf8 app/docs/diagrams/render_deployment_diagram.py --plantuml .review/plantuml-1.2025.2.jar`.

Script kiểm tra cấu trúc khai báo và các đường kết nối trong PlantUML, rồi xuất SVG với tọa độ cố định để giữ kiểu node 3D và artifact giống Visual Paradigm. PDF/PNG xuất từ SVG đó. PlantUML là nguồn chỉnh sửa cấu trúc; bố cục của PlantUML khi chạy trực tiếp có thể khác. Tệp bàn giao là hình vector và mã nguồn, không phải tệp dự án .vpp của Visual Paradigm.

Chèn phần viết và hình sau biểu đồ gói trong mục 2.3.3, dùng caption Hình 2.14 nếu biểu đồ gói đứng ở Hình 2.13. Giữ nguyên DOCX/PDF nguồn; chỉ cập nhật số hình khi ghép báo cáo.
