# Scenario v3.0 và biểu đồ tuần tự thiết kế hai modul

## a. Thiết kế động modul tìm kiếm thông tin dịch vụ/phụ tùng

Biểu đồ tuần tự thiết kế modul tìm kiếm thông tin dịch vụ/phụ tùng được trình bày trong Hình 1. Kịch bản v3.0 của modul diễn ra như sau:

1. Tại trang customerHome.jsp, sau khi đăng nhập, khách hàng A click chọn chức năng tìm thông tin dịch vụ/phụ tùng.
2. Trang customerHome.jsp gọi hàm showSearch() của lớp CatalogServlet4 yêu cầu mở giao diện tìm kiếm thông tin dịch vụ/phụ tùng.
3. Lớp CatalogServlet4 chuyển dữ liệu và gọi trang catalogSearch.jsp.
4. Trang catalogSearch.jsp hiển thị ô nhập tên và nút Tìm kiếm cho khách hàng; chưa có danh sách kết quả.
5. Khách hàng A nhập từ khóa “lọc” và click Tìm kiếm.
6. Trang catalogSearch.jsp gọi hàm searchCatalog() của lớp CatalogServlet4 yêu cầu tìm thông tin dịch vụ/phụ tùng theo từ khóa “lọc”.
7. Lớp CatalogServlet4 gọi lớp ServiceDAO4 yêu cầu tìm các dịch vụ đang cung cấp có tên chứa từ khóa “lọc”.
8. Lớp ServiceDAO4 gọi hàm findServicesByName().
9. Hàm findServicesByName() gọi lớp Service4 để đóng gói thông tin thực thể.
10. Lớp Service4 đóng gói thông tin thực thể.
11. Lớp Service4 trả kết quả lại cho hàm findServicesByName().
12. Hàm findServicesByName() trả danh sách dịch vụ lại cho lớp CatalogServlet4.
13. Lớp CatalogServlet4 gọi lớp SparePartDAO4 yêu cầu tìm các phụ tùng đang kinh doanh đã công bố có tên chứa từ khóa “lọc”.
14. Lớp SparePartDAO4 gọi hàm findSparePartsByName().
15. Hàm findSparePartsByName() gọi lớp SparePart4 để đóng gói thông tin thực thể.
16. Lớp SparePart4 đóng gói thông tin thực thể.
17. Lớp SparePart4 trả kết quả lại cho hàm findSparePartsByName().
18. Hàm findSparePartsByName() trả danh sách phụ tùng lại cho lớp CatalogServlet4.
19. Lớp CatalogServlet4 chuyển dữ liệu và gọi trang catalogSearch.jsp.
20. Trang catalogSearch.jsp hiển thị danh sách kết quả cho khách hàng, gồm mã, tên, loại và đơn giá.
21. Khách hàng A click chọn phụ tùng PT1.
22. Trang catalogSearch.jsp gọi hàm viewDetails() của lớp CatalogServlet4 yêu cầu xem thông tin chi tiết phụ tùng PT1 đã chọn.
23. Lớp CatalogServlet4 gọi lớp SparePartDAO4 yêu cầu lấy thông tin chi tiết phụ tùng PT1 và nhóm phụ tùng tương ứng.
24. Lớp SparePartDAO4 gọi hàm getSparePartDetails().
25. Hàm getSparePartDetails() gọi lớp SparePart4 để đóng gói thông tin thực thể.
26. Lớp SparePart4 đóng gói thông tin thực thể.
27. Lớp SparePart4 trả kết quả lại cho hàm getSparePartDetails().
28. Hàm getSparePartDetails() trả thông tin phụ tùng PT1 lại cho lớp CatalogServlet4.
29. Lớp CatalogServlet4 chuyển dữ liệu và gọi trang itemDetails.jsp.
30. Trang itemDetails.jsp hiển thị thông tin chi tiết phụ tùng PT1 và nút Quay lại cho khách hàng.
31. Khách hàng A click Quay lại.
32. Trang itemDetails.jsp gọi hàm searchCatalog() của lớp CatalogServlet4 yêu cầu tìm lại thông tin theo từ khóa “lọc” và bộ lọc đã chọn.
33. Lớp CatalogServlet4 gọi lớp ServiceDAO4 yêu cầu tìm lại các dịch vụ theo từ khóa đã giữ.
34. Lớp ServiceDAO4 gọi hàm findServicesByName().
35. Hàm findServicesByName() gọi lớp Service4 để đóng gói thông tin thực thể.
36. Lớp Service4 đóng gói thông tin thực thể.
37. Lớp Service4 trả kết quả lại cho hàm findServicesByName().
38. Hàm findServicesByName() trả danh sách dịch vụ lại cho lớp CatalogServlet4.
39. Lớp CatalogServlet4 gọi lớp SparePartDAO4 yêu cầu tìm lại các phụ tùng theo từ khóa đã giữ.
40. Lớp SparePartDAO4 gọi hàm findSparePartsByName().
41. Hàm findSparePartsByName() gọi lớp SparePart4 để đóng gói thông tin thực thể.
42. Lớp SparePart4 đóng gói thông tin thực thể.
43. Lớp SparePart4 trả kết quả lại cho hàm findSparePartsByName().
44. Hàm findSparePartsByName() trả danh sách phụ tùng lại cho lớp CatalogServlet4.
45. Lớp CatalogServlet4 chuyển dữ liệu và gọi trang catalogSearch.jsp.
46. Trang catalogSearch.jsp hiển thị lại danh sách kết quả cho khách hàng, giữ từ khóa và bộ lọc đã chọn.

Lặp lại các bước từ 21 đến 46 nếu khách hàng muốn xem phụ tùng khác hoặc từ bước 5 nếu muốn tìm theo từ khóa khác.

![Hình 1 Biểu đồ tuần tự thiết kế modul tìm kiếm thông tin dịch vụ/phụ tùng](diagrams/tuan_tu_tra_cuu.svg)

Hình 1: Biểu đồ tuần tự thiết kế modul tìm kiếm thông tin dịch vụ/phụ tùng.

[Xem PDF một biểu đồ](diagrams/sequence/catalog-sequence.pdf) · [Mã PlantUML](diagrams/tuan_tu_tra_cuu.puml).

## b. Thiết kế động modul nhân viên kho nhập phụ tùng

Biểu đồ tuần tự thiết kế modul nhân viên kho nhập phụ tùng được trình bày trong Hình 2. Kịch bản v3.0 của modul diễn ra như sau:

1. Tại trang warehouseHome.jsp, sau khi đăng nhập, nhân viên A click chọn chức năng nhập phụ tùng.
2. Trang warehouseHome.jsp gọi hàm showSupplierSelection() của lớp SupplierServlet4 yêu cầu mở giao diện tìm và chọn nhà cung cấp.
3. Lớp SupplierServlet4 chuyển dữ liệu và gọi trang selectSupplier.jsp.
4. Trang selectSupplier.jsp hiển thị ô nhập tên nhà cung cấp và nút Tìm kiếm cho nhân viên.
5. Nhân viên A nhập “Hòa Phát” và click Tìm kiếm.
6. Trang selectSupplier.jsp gọi hàm searchSuppliers() của lớp SupplierServlet4 yêu cầu tìm nhà cung cấp theo tên “Hòa Phát”.
7. Lớp SupplierServlet4 gọi lớp SupplierDAO4 yêu cầu tìm các nhà cung cấp đang hoạt động có tên chứa từ khóa “Hòa Phát”.
8. Lớp SupplierDAO4 gọi hàm findSuppliersByName().
9. Hàm findSuppliersByName() gọi lớp Supplier4 để đóng gói thông tin thực thể.
10. Lớp Supplier4 đóng gói thông tin thực thể.
11. Lớp Supplier4 trả kết quả lại cho hàm findSuppliersByName().
12. Hàm findSuppliersByName() trả danh sách nhà cung cấp lại cho lớp SupplierServlet4.
13. Lớp SupplierServlet4 chuyển dữ liệu và gọi trang selectSupplier.jsp.
14. Trang selectSupplier.jsp hiển thị danh sách nhà cung cấp cho nhân viên.
15. Nhân viên A click chọn nhà cung cấp NCC1.
16. Trang selectSupplier.jsp gọi hàm startPurchase() của lớp PurchaseInvoiceServlet4 yêu cầu lập hóa đơn nhập với nhà cung cấp NCC1 đã chọn. Mã nhân viên kho được lấy từ phiên đăng nhập.
17. Lớp PurchaseInvoiceServlet4 gọi lớp SupplierDAO4 yêu cầu lấy thông tin nhà cung cấp NCC1.
18. Lớp SupplierDAO4 gọi hàm getSupplierDetails().
19. Hàm getSupplierDetails() gọi lớp Supplier4 để đóng gói thông tin thực thể.
20. Lớp Supplier4 đóng gói thông tin thực thể.
21. Lớp Supplier4 trả kết quả lại cho hàm getSupplierDetails().
22. Hàm getSupplierDetails() trả thông tin nhà cung cấp NCC1 lại cho lớp PurchaseInvoiceServlet4.
23. Lớp PurchaseInvoiceServlet4 gọi lớp WarehouseStaffDAO4 yêu cầu lấy mã và họ tên nhân viên kho A.
24. Lớp WarehouseStaffDAO4 gọi hàm getWarehouseStaffDetails().
25. Hàm getWarehouseStaffDetails() gọi lớp WarehouseStaff4 để đóng gói thông tin thực thể.
26. Lớp WarehouseStaff4 đóng gói thông tin thực thể.
27. Lớp WarehouseStaff4 trả kết quả lại cho hàm getWarehouseStaffDetails().
28. Hàm getWarehouseStaffDetails() trả thông tin nhân viên kho A lại cho lớp PurchaseInvoiceServlet4.
29. Sau khi tạo hóa đơn nhập dự kiến, lớp PurchaseInvoiceServlet4 thực hiện hàm showDraft() để hiển thị hóa đơn.
30. Lớp PurchaseInvoiceServlet4 chuyển dữ liệu và gọi trang purchaseInvoice.jsp.
31. Trang purchaseInvoice.jsp hiển thị thông tin nhà cung cấp NCC1, nhân viên A, ngày nhập hiện tại, ô tìm phụ tùng, bảng các dòng nhập đang trống, tổng tiền bằng 0 và nút Xác nhận cho nhân viên.
32. Nhân viên A nhập “Lọc” và click Tìm kiếm.
33. Trang purchaseInvoice.jsp gọi hàm searchSpareParts() của lớp PurchaseInvoiceServlet4 yêu cầu tìm phụ tùng theo tên “Lọc”.
34. Lớp PurchaseInvoiceServlet4 giữ dữ liệu các dòng đang nhập và thực hiện hàm showDraft() theo từ khóa vừa nhận.
35. Lớp PurchaseInvoiceServlet4 gọi lớp SparePartDAO4 yêu cầu tìm các phụ tùng chưa công bố hoặc đang kinh doanh có tên chứa từ khóa “Lọc”.
36. Lớp SparePartDAO4 gọi hàm findSparePartsByName().
37. Hàm findSparePartsByName() gọi lớp SparePart4 để đóng gói thông tin thực thể.
38. Lớp SparePart4 đóng gói thông tin thực thể.
39. Lớp SparePart4 trả kết quả lại cho hàm findSparePartsByName().
40. Hàm findSparePartsByName() trả danh sách phụ tùng lại cho lớp PurchaseInvoiceServlet4.
41. Lớp PurchaseInvoiceServlet4 chuyển dữ liệu và gọi trang purchaseInvoice.jsp.
42. Trang purchaseInvoice.jsp hiển thị danh sách phụ tùng tìm được ngay trên hóa đơn, gồm mã, tên, đơn vị tính và hãng sản xuất cho nhân viên chọn.
43. Nhân viên A click chọn phụ tùng PT1.
44. Trang purchaseInvoice.jsp gọi hàm addInvoiceLine() của lớp PurchaseInvoiceServlet4 yêu cầu bổ sung phụ tùng PT1 vào hóa đơn nhập dự kiến.
45. Lớp PurchaseInvoiceServlet4 gọi lớp SparePartDAO4 yêu cầu lấy thông tin chi tiết phụ tùng PT1.
46. Lớp SparePartDAO4 gọi hàm getSparePartDetails().
47. Hàm getSparePartDetails() gọi lớp SparePart4 để đóng gói thông tin thực thể.
48. Lớp SparePart4 đóng gói thông tin thực thể.
49. Lớp SparePart4 trả kết quả lại cho hàm getSparePartDetails().
50. Hàm getSparePartDetails() trả thông tin phụ tùng PT1 lại cho lớp PurchaseInvoiceServlet4.
51. Lớp PurchaseInvoiceServlet4 bổ sung dòng PT1 vào hóa đơn nhập dự kiến, giữ dữ liệu các dòng đã nhập và thực hiện hàm showDraft() để hiển thị lại hóa đơn.
52. Lớp PurchaseInvoiceServlet4 chuyển dữ liệu và gọi trang purchaseInvoice.jsp.
53. Trang purchaseInvoice.jsp hiển thị dòng PT1 cho nhân viên; số lượng, đơn giá nhập và thành tiền ban đầu bằng 0.
54. Nhân viên A đối chiếu với nhà cung cấp, nhập số lượng 10 và đơn giá nhập 80.000 đồng/cái.
55. Trang purchaseInvoice.jsp hiển thị dòng nhập, thành tiền và tổng tiền dự kiến 800.000 đồng cho nhân viên. (Lặp lại các bước từ 32 đến 55 cho đến khi hết phụ tùng muốn nhập.)
56. Nhân viên A đối chiếu hàng thực nhận, chứng từ giao hàng và các dòng nhập, sau đó click Xác nhận.
57. Trang purchaseInvoice.jsp gọi hàm confirmInvoice() của lớp PurchaseInvoiceServlet4 yêu cầu xác nhận hóa đơn nhập. Hàm nhận dữ liệu các dòng nhập và kiểm tra hóa đơn có ít nhất một dòng, số lượng và đơn giá nhập hợp lệ.
58. Lớp PurchaseInvoiceServlet4 gọi lớp PurchaseInvoiceDAO4 yêu cầu lưu hóa đơn nhập cùng các dòng nhập.
59. Lớp PurchaseInvoiceDAO4 gọi hàm savePurchaseInvoice(); hàm lưu hóa đơn HN1, các dòng nhập, thời điểm xác nhận và trạng thái Chưa thanh toán; chốt tên nhà cung cấp, tên nhân viên lập, tên và đơn vị tính phụ tùng.
60. Lớp PurchaseInvoiceDAO4 gọi lớp SparePartDAO4 yêu cầu tăng tồn kho theo dòng nhập của HN1.
61. Lớp SparePartDAO4 gọi hàm increaseStockAfterPurchase() với cùng Connection của transaction hóa đơn; hàm tăng tồn PT1 thêm 10 cái.
62. Hàm increaseStockAfterPurchase() trả kết quả tăng tồn kho lại cho lớp PurchaseInvoiceDAO4.
63. Hàm savePurchaseInvoice() hoàn tất transaction và trả kết quả lưu thành công cùng mã HN1 lại cho lớp PurchaseInvoiceServlet4.
64. Lớp PurchaseInvoiceServlet4 thực hiện hàm viewInvoice() để mở hóa đơn nhập HN1 đã xác nhận.
65. Lớp PurchaseInvoiceServlet4 gọi lớp PurchaseInvoiceDAO4 yêu cầu lấy thông tin hóa đơn HN1 cùng các dòng nhập và thông tin đã chốt.
66. Lớp PurchaseInvoiceDAO4 gọi hàm getPurchaseInvoiceDetails().
67. Hàm getPurchaseInvoiceDetails() gọi lớp PurchaseInvoice4 để đóng gói thông tin thực thể.
68. Lớp PurchaseInvoice4 đóng gói thông tin thực thể.
69. Lớp PurchaseInvoice4 trả kết quả lại cho hàm getPurchaseInvoiceDetails().
70. Hàm getPurchaseInvoiceDetails() trả thông tin hóa đơn HN1 lại cho lớp PurchaseInvoiceServlet4.
71. Lớp PurchaseInvoiceServlet4 chuyển dữ liệu và gọi trang confirmedInvoice.jsp.
72. Trang confirmedInvoice.jsp hiển thị HN1 đã xác nhận, các dòng nhập, tổng tiền 800.000 đồng, trạng thái Chưa thanh toán, nút In hóa đơn, ô Số tiền đã trả và nút Đã thanh toán cho nhân viên.
73. Nhân viên A click In hóa đơn.
74. Trang confirmedInvoice.jsp gọi hàm printInvoice() của lớp PurchaseInvoiceServlet4 yêu cầu lấy hóa đơn HN1 đã lưu để in.
75. Lớp PurchaseInvoiceServlet4 gọi lớp PurchaseInvoiceDAO4 yêu cầu lấy thông tin hóa đơn HN1 cùng các dòng nhập và thông tin đã chốt.
76. Lớp PurchaseInvoiceDAO4 gọi hàm getPurchaseInvoiceDetails().
77. Hàm getPurchaseInvoiceDetails() gọi lớp PurchaseInvoice4 để đóng gói thông tin thực thể.
78. Lớp PurchaseInvoice4 đóng gói thông tin thực thể.
79. Lớp PurchaseInvoice4 trả kết quả lại cho hàm getPurchaseInvoiceDetails().
80. Hàm getPurchaseInvoiceDetails() trả thông tin hóa đơn HN1 lại cho lớp PurchaseInvoiceServlet4.
81. Lớp PurchaseInvoiceServlet4 chuyển dữ liệu và gọi trang printInvoice.jsp.
82. Trang printInvoice.jsp hiển thị nội dung chứng từ HN1 và nút In cho nhân viên; chứng từ dùng các thông tin đã chốt khi xác nhận.
83. Nhân viên A click In.
84. Trang printInvoice.jsp thực hiện in chứng từ HN1 bằng hàm printInvoice() trong print.js.
85. Trang printInvoice.jsp gọi hàm viewInvoice() của lớp PurchaseInvoiceServlet4 yêu cầu hiển thị lại hóa đơn HN1 sau khi đóng hộp thoại in.
86. Lớp PurchaseInvoiceServlet4 gọi lớp PurchaseInvoiceDAO4 yêu cầu lấy thông tin hóa đơn HN1 cùng các dòng nhập và thông tin đã chốt.
87. Lớp PurchaseInvoiceDAO4 gọi hàm getPurchaseInvoiceDetails().
88. Hàm getPurchaseInvoiceDetails() gọi lớp PurchaseInvoice4 để đóng gói thông tin thực thể.
89. Lớp PurchaseInvoice4 đóng gói thông tin thực thể.
90. Lớp PurchaseInvoice4 trả kết quả lại cho hàm getPurchaseInvoiceDetails().
91. Hàm getPurchaseInvoiceDetails() trả thông tin hóa đơn HN1 lại cho lớp PurchaseInvoiceServlet4.
92. Lớp PurchaseInvoiceServlet4 chuyển dữ liệu và gọi trang confirmedInvoice.jsp.
93. Trang confirmedInvoice.jsp hiển thị lại hóa đơn HN1 và tình trạng thanh toán hiện tại cho nhân viên.
94. Nhân viên A đối chiếu với nhà cung cấp, giao chứng từ và thực tế thanh toán đủ 800.000 đồng.
95. Nhà cung cấp nhận chứng từ đối chiếu và tiền thanh toán.
96. Nhà cung cấp giao hóa đơn bán hàng cho gara.
97. Nhân viên A tiếp nhận hóa đơn bán hàng do nhà cung cấp lập.
98. Nhân viên A nhập 800.000 đồng vào ô Số tiền đã trả và click Đã thanh toán.
99. Trang confirmedInvoice.jsp gọi hàm recordPayment() của lớp PurchaseInvoiceServlet4 yêu cầu ghi nhận thanh toán hóa đơn HN1. Mã nhân viên kho hiện tại được lấy từ phiên đăng nhập.
100. Lớp PurchaseInvoiceServlet4 gọi lớp PurchaseInvoiceDAO4 yêu cầu ghi nhận thanh toán hóa đơn HN1.
101. Lớp PurchaseInvoiceDAO4 gọi hàm recordPayment(); hàm kiểm tra quyền nhân viên kho hiện tại, HN1 chưa thanh toán và số tiền đã trả bằng tổng tiền; ghi nhận trạng thái Đã thanh toán, thời điểm thanh toán và người thực sự trả tiền, giữ nguyên người lập.
102. Hàm recordPayment() trả kết quả ghi nhận thanh toán thành công lại cho lớp PurchaseInvoiceServlet4.
103. Lớp PurchaseInvoiceServlet4 thực hiện hàm viewInvoice() để hiển thị hóa đơn HN1 sau khi ghi nhận thanh toán.
104. Lớp PurchaseInvoiceServlet4 gọi lớp PurchaseInvoiceDAO4 yêu cầu lấy thông tin hóa đơn HN1 cùng các dòng nhập và thông tin đã chốt.
105. Lớp PurchaseInvoiceDAO4 gọi hàm getPurchaseInvoiceDetails().
106. Hàm getPurchaseInvoiceDetails() gọi lớp PurchaseInvoice4 để đóng gói thông tin thực thể.
107. Lớp PurchaseInvoice4 đóng gói thông tin thực thể.
108. Lớp PurchaseInvoice4 trả kết quả lại cho hàm getPurchaseInvoiceDetails().
109. Hàm getPurchaseInvoiceDetails() trả thông tin hóa đơn HN1 lại cho lớp PurchaseInvoiceServlet4.
110. Lớp PurchaseInvoiceServlet4 chuyển dữ liệu và gọi trang confirmedInvoice.jsp.
111. Trang confirmedInvoice.jsp hiển thị HN1 với trạng thái Đã thanh toán và thông báo ghi nhận thanh toán thành công cho nhân viên.

Các bước từ 32 đến 55 được lặp lại cho từng phụ tùng tiếp theo cho đến khi hết phụ tùng muốn nhập. PT1 là dữ liệu minh họa của lượt đầu; tổng tiền khi có nhiều dòng bằng tổng thành tiền của tất cả các dòng nhập.

![Hình 2 Biểu đồ tuần tự thiết kế modul nhân viên kho nhập phụ tùng](diagrams/tuan_tu_nhap_phu_tung.svg)

Hình 2: Biểu đồ tuần tự thiết kế modul nhân viên kho nhập phụ tùng.

[Xem PDF một biểu đồ](diagrams/sequence/purchase-sequence.pdf) · [Mã PlantUML](diagrams/tuan_tu_nhap_phu_tung.puml).

---

Nguồn trình bày: mục 4.3.2, Hình 4.10, 4.12 và 4.14 của bài giảng. Nguồn nghiệp vụ: mục 1.2.1 và 1.2.4 trong 26_B23DCCN764.docx. Các tên lớp, trang và phương thức giữ bộ tên tiếng Anh đã xác nhận trong docs. Mỗi modul chỉ có một biểu đồ tuần tự liền mạch cho kịch bản thành công. Bảng đường sống, từng thông điệp và phạm vi thanh kích hoạt để vẽ theo nằm trong [hướng dẫn Visual Paradigm](VE_SEQUENCE_VISUAL_PARADIGM.md). Căn cứ source và phần code còn cần sửa theo nghiệp vụ đã chốt nằm riêng trong [bảng đối chiếu](SCENARIO_V3_DOI_CHIEU.md).
