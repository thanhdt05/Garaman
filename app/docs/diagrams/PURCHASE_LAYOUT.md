# Bố cục hai sơ đồ module

`purchase-design.svg/png` và `catalog-design.svg/png` dùng bố cục chủ động theo bốn tầng: JSP → Servlet → DAO → thực thể. Mỗi quan hệ có cổng và làn riêng; các đường độc lập cắt nhau có khoảng hở để không bị hiểu thành điểm nối chung.

Module nhập phụ tùng có 24 lớp/35 quan hệ; module tra cứu có 12 lớp/12 quan hệ. Các thuộc tính và hàm Servlet hiện lấy theo code: tổng cộng 9 thuộc tính DAO và 21 hàm điều phối chính trên 4 Servlet. Lược `init()`, `doGet()`, `doPost()`, `serialVersionUID`, `throws` và các hàm kỹ thuật. Không thêm biến cục bộ làm thuộc tính.

PlantUML ghi đủ kiểu tham số `request: HttpServletRequest`, `response: HttpServletResponse`. Trong SVG, mỗi hàm ghi `(request, response): void` và giải thích chung hai kiểu HTTP dưới hình để tránh lặp dài. Các chú thích được đưa xuống cuối hình để không che đường nối. Nền trắng được khai báo trực tiếp trong SVG.

Xuất lại đúng bố cục này bằng Python có PySide6:

```powershell
python app/docs/diagrams/render_purchase_layout.py
python app/docs/diagrams/render_purchase_layout.py --catalog
```

Script đọc nội dung lớp/quan hệ từ PlantUML và kiểm tra hình học trước khi xuất: không có hai đường chung một đoạn và không có đường xuyên qua ô lớp. Kết quả ở `purchase-layout-check.json` và `catalog-layout-check.json`. PlantUML vẫn là nguồn nội dung UML; xuất trực tiếp bằng bộ tự dàn trang PlantUML sẽ có bố cục khác với bản SVG được điều tuyến chủ động này.
