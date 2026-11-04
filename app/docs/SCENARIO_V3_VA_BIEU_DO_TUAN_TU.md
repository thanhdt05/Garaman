# Scenario v3.0 và biểu đồ tuần tự thiết kế hai modul

## a. Thiết kế động modul tìm kiếm thông tin dịch vụ/phụ tùng

Biểu đồ tuần tự thiết kế modul tìm kiếm thông tin dịch vụ/phụ tùng được trình bày trong Hình 1. Kịch bản v3.0 của modul diễn ra như sau:

1. Tại trang customerHome.jsp, sau khi đăng nhập, khách hàng A click chọn chức năng tìm thông tin dịch vụ/phụ tùng.
2. Trang customerHome.jsp gọi lớp CatalogServlet4 yêu cầu mở giao diện tìm kiếm thông tin dịch vụ/phụ tùng.
3. Lớp CatalogServlet4 gọi hàm showSearch().
4. Lớp CatalogServlet4 chuyển dữ liệu và gọi trang catalogSearch.jsp.
5. Trang catalogSearch.jsp hiển thị ô nhập tên và nút Tìm kiếm cho khách hàng; chưa có danh sách kết quả.
6. Khách hàng A nhập từ khóa “lọc” và click Tìm kiếm.
7. Trang catalogSearch.jsp gọi lớp CatalogServlet4 yêu cầu tìm thông tin dịch vụ/phụ tùng theo từ khóa “lọc”.
8. Lớp CatalogServlet4 gọi hàm searchCatalog().
9. Lớp CatalogServlet4 gọi lớp ServiceDAO4 yêu cầu tìm các dịch vụ đang cung cấp có tên chứa từ khóa “lọc”.
10. Lớp ServiceDAO4 gọi hàm findServicesByName().
11. Hàm findServicesByName() gọi lớp Service4 để đóng gói thông tin thực thể.
12. Lớp Service4 đóng gói thông tin thực thể.
13. Lớp Service4 trả kết quả lại cho hàm findServicesByName().
14. Hàm findServicesByName() trả danh sách dịch vụ lại cho lớp CatalogServlet4.
15. Lớp CatalogServlet4 gọi lớp SparePartDAO4 yêu cầu tìm các phụ tùng đang kinh doanh đã công bố có tên chứa từ khóa “lọc”.
16. Lớp SparePartDAO4 gọi hàm findSparePartsByName().
17. Hàm findSparePartsByName() gọi lớp SparePart4 để đóng gói thông tin thực thể.
18. Lớp SparePart4 đóng gói thông tin thực thể.
19. Lớp SparePart4 trả kết quả lại cho hàm findSparePartsByName().
20. Hàm findSparePartsByName() trả danh sách phụ tùng lại cho lớp CatalogServlet4.
21. Hàm searchCatalog() trả danh sách dịch vụ và phụ tùng tìm được lại cho trang catalogSearch.jsp.
22. Trang catalogSearch.jsp hiển thị danh sách kết quả cho khách hàng, gồm mã, tên, loại và đơn giá.
23. Khách hàng A click chọn phụ tùng PT1.
24. Trang catalogSearch.jsp gọi lớp CatalogServlet4 yêu cầu xem thông tin chi tiết phụ tùng PT1 đã chọn.
25. Lớp CatalogServlet4 gọi hàm viewDetails().
26. Lớp CatalogServlet4 gọi lớp SparePartDAO4 yêu cầu lấy thông tin chi tiết phụ tùng PT1 và nhóm phụ tùng tương ứng.
27. Lớp SparePartDAO4 gọi hàm getSparePartDetails().
28. Hàm getSparePartDetails() gọi lớp SparePart4 để đóng gói thông tin thực thể.
29. Lớp SparePart4 đóng gói thông tin thực thể.
30. Lớp SparePart4 trả kết quả lại cho hàm getSparePartDetails().
31. Hàm getSparePartDetails() trả thông tin phụ tùng PT1 lại cho lớp CatalogServlet4.
32. Lớp CatalogServlet4 chuyển dữ liệu và gọi trang itemDetails.jsp.
33. Trang itemDetails.jsp hiển thị thông tin chi tiết phụ tùng PT1 và nút Quay lại cho khách hàng.

Kịch bản thành công kết thúc khi hiển thị thông tin chi tiết PT1, tương ứng bước 6 của kịch bản chính trong bài làm.

![Hình 1 Biểu đồ tuần tự thiết kế modul tìm kiếm thông tin dịch vụ/phụ tùng](diagrams/tuan_tu_tra_cuu.svg)

Hình 1: Biểu đồ tuần tự thiết kế modul tìm kiếm thông tin dịch vụ/phụ tùng.

[Xem PDF một biểu đồ](diagrams/sequence/catalog-sequence.pdf) · [Mã PlantUML](diagrams/tuan_tu_tra_cuu.puml).

## b. Thiết kế động modul nhân viên kho nhập phụ tùng

Biểu đồ tuần tự thiết kế modul nhân viên kho nhập phụ tùng được trình bày trong Hình 2. Kịch bản v3.0 của modul diễn ra như sau:

Điều kiện trước: nhân viên kho đã đăng nhập; nhà cung cấp đã giao hàng để nhân viên đối chiếu và nhập kho. Nhà cung cấp không phải actor của phần mềm, không có tài khoản; thông tin nhà cung cấp do nhân viên kho nhập và quản lí. Actor của modul là nhân viên kho.

1. Tại trang warehouseHome.jsp, sau khi đăng nhập, nhân viên A click chọn chức năng nhập phụ tùng.
2. Trang warehouseHome.jsp gọi lớp SupplierServlet4 yêu cầu mở giao diện tìm và chọn nhà cung cấp.
3. Lớp SupplierServlet4 gọi hàm showSupplierSelection().
4. Lớp SupplierServlet4 chuyển dữ liệu và gọi trang selectSupplier.jsp.
5. Trang selectSupplier.jsp hiển thị ô nhập tên nhà cung cấp và nút Tìm kiếm cho nhân viên.
6. Nhân viên A nhập “Hòa Phát” và click Tìm kiếm.
7. Trang selectSupplier.jsp gọi lớp SupplierServlet4 yêu cầu tìm nhà cung cấp theo tên “Hòa Phát”.
8. Lớp SupplierServlet4 gọi hàm searchSuppliers().
9. Lớp SupplierServlet4 gọi lớp SupplierDAO4 yêu cầu tìm các nhà cung cấp đang hoạt động có tên chứa từ khóa “Hòa Phát”.
10. Lớp SupplierDAO4 gọi hàm findSuppliersByName().
11. Hàm findSuppliersByName() gọi lớp Supplier4 để đóng gói thông tin thực thể.
12. Lớp Supplier4 đóng gói thông tin thực thể.
13. Lớp Supplier4 trả kết quả lại cho hàm findSuppliersByName().
14. Hàm findSuppliersByName() trả danh sách nhà cung cấp lại cho lớp SupplierServlet4.
15. Hàm searchSuppliers() trả danh sách nhà cung cấp tìm được lại cho trang selectSupplier.jsp.
16. Trang selectSupplier.jsp hiển thị danh sách nhà cung cấp cho nhân viên.
17. Nhân viên A click chọn nhà cung cấp NCC1.
18. Trang selectSupplier.jsp gọi lớp PurchaseInvoiceServlet4 yêu cầu lập hóa đơn nhập với nhà cung cấp NCC1 đã chọn.
19. Lớp PurchaseInvoiceServlet4 gọi hàm startPurchase(). Mã nhân viên kho được lấy từ phiên đăng nhập.
20. Lớp PurchaseInvoiceServlet4 gọi lớp SupplierDAO4 yêu cầu lấy thông tin nhà cung cấp NCC1.
21. Lớp SupplierDAO4 gọi hàm getSupplierDetails().
22. Hàm getSupplierDetails() gọi lớp Supplier4 để đóng gói thông tin thực thể.
23. Lớp Supplier4 đóng gói thông tin thực thể.
24. Lớp Supplier4 trả kết quả lại cho hàm getSupplierDetails().
25. Hàm getSupplierDetails() trả thông tin nhà cung cấp NCC1 lại cho lớp PurchaseInvoiceServlet4.
26. Lớp PurchaseInvoiceServlet4 gọi lớp WarehouseStaffDAO4 yêu cầu lấy mã và họ tên nhân viên kho A.
27. Lớp WarehouseStaffDAO4 gọi hàm getWarehouseStaffDetails().
28. Hàm getWarehouseStaffDetails() gọi lớp WarehouseStaff4 để đóng gói thông tin thực thể.
29. Lớp WarehouseStaff4 đóng gói thông tin thực thể.
30. Lớp WarehouseStaff4 trả kết quả lại cho hàm getWarehouseStaffDetails().
31. Hàm getWarehouseStaffDetails() trả thông tin nhân viên kho A lại cho lớp PurchaseInvoiceServlet4.
32. Sau khi tạo hóa đơn nhập dự kiến, lớp PurchaseInvoiceServlet4 thực hiện hàm showDraft() để hiển thị hóa đơn.
33. Lớp PurchaseInvoiceServlet4 chuyển dữ liệu và gọi trang purchaseInvoice.jsp.
34. Trang purchaseInvoice.jsp hiển thị thông tin nhà cung cấp NCC1, nhân viên A, ngày nhập hiện tại, ô tìm phụ tùng, bảng các dòng nhập đang trống, tổng tiền bằng 0 và nút Xác nhận cho nhân viên.
35. Nhân viên A nhập “Lọc” và click Tìm kiếm.
36. Trang purchaseInvoice.jsp gọi lớp PurchaseInvoiceServlet4 yêu cầu tìm phụ tùng theo tên “Lọc”.
37. Lớp PurchaseInvoiceServlet4 gọi hàm searchSpareParts().
38. Sau chuyển hướng, lớp PurchaseInvoiceServlet4 thực hiện hàm showDraft() để lấy dữ liệu hóa đơn dự kiến và xử lí tìm phụ tùng theo từ khóa đã nhập.
39. Lớp PurchaseInvoiceServlet4 gọi lớp SparePartDAO4 yêu cầu tìm các phụ tùng chưa công bố hoặc đang kinh doanh có tên chứa từ khóa “Lọc”.
40. Lớp SparePartDAO4 gọi hàm findSparePartsByName().
41. Hàm findSparePartsByName() gọi lớp SparePart4 để đóng gói thông tin thực thể.
42. Lớp SparePart4 đóng gói thông tin thực thể.
43. Lớp SparePart4 trả kết quả lại cho hàm findSparePartsByName().
44. Hàm findSparePartsByName() trả danh sách phụ tùng lại cho lớp PurchaseInvoiceServlet4.
45. Hàm showDraft() trả danh sách phụ tùng tìm được cùng dữ liệu hóa đơn đang lập lại cho trang purchaseInvoice.jsp.
46. Trang purchaseInvoice.jsp hiển thị bảng danh sách phụ tùng tìm được ngay trên hóa đơn, gồm Mã, Tên phụ tùng, Đơn vị tính, Hãng sản xuất và nút Chọn ở từng dòng.
47. Nhân viên A click nút Chọn tại dòng phụ tùng PT1 trong bảng kết quả tìm kiếm.
48. Trang purchaseInvoice.jsp gọi lớp PurchaseInvoiceServlet4 yêu cầu bổ sung phụ tùng PT1 vào hóa đơn nhập dự kiến.
49. Lớp PurchaseInvoiceServlet4 gọi hàm addInvoiceLine().
50. Lớp PurchaseInvoiceServlet4 gọi lớp SparePartDAO4 yêu cầu lấy thông tin chi tiết phụ tùng PT1.
51. Lớp SparePartDAO4 gọi hàm getSparePartDetails().
52. Hàm getSparePartDetails() gọi lớp SparePart4 để đóng gói thông tin thực thể.
53. Lớp SparePart4 đóng gói thông tin thực thể.
54. Lớp SparePart4 trả kết quả lại cho hàm getSparePartDetails().
55. Hàm getSparePartDetails() trả thông tin phụ tùng PT1 lại cho lớp PurchaseInvoiceServlet4.
56. Lớp PurchaseInvoiceServlet4 bổ sung dòng PT1 vào hóa đơn nhập dự kiến, giữ dữ liệu các dòng đã nhập và thực hiện hàm showDraft() để hiển thị lại hóa đơn.
57. Hàm showDraft() trả hóa đơn dự kiến sau khi bổ sung dòng PT1 lại cho trang purchaseInvoice.jsp.
58. Trang purchaseInvoice.jsp hiển thị dòng PT1 cho nhân viên; số lượng, đơn giá nhập và thành tiền ban đầu bằng 0.
59. Nhân viên A đối chiếu với nhà cung cấp, nhập số lượng 10 và đơn giá nhập 80.000 đồng/cái.
60. Trang purchaseInvoice.jsp hiển thị dòng nhập, thành tiền và tổng tiền dự kiến 800.000 đồng cho nhân viên. (Lặp lại các bước từ 35 đến 60 cho đến khi hết phụ tùng muốn nhập.)
61. Nhân viên A đối chiếu hàng thực nhận, chứng từ giao hàng và các dòng nhập, sau đó click Xác nhận.
62. Trang purchaseInvoice.jsp gọi lớp PurchaseInvoiceServlet4 yêu cầu xác nhận hóa đơn nhập.
63. Lớp PurchaseInvoiceServlet4 gọi hàm confirmInvoice(). Hàm nhận dữ liệu các dòng nhập và kiểm tra hóa đơn có ít nhất một dòng, số lượng và đơn giá nhập hợp lệ.
64. Lớp PurchaseInvoiceServlet4 gọi lớp PurchaseInvoiceDAO4 yêu cầu lưu hóa đơn nhập cùng các dòng nhập.
65. Lớp PurchaseInvoiceDAO4 gọi hàm savePurchaseInvoice(); hàm lưu hóa đơn HN1, các dòng nhập, thời điểm xác nhận và trạng thái Chưa thanh toán; chốt tên nhà cung cấp, tên nhân viên lập, tên và đơn vị tính phụ tùng.
66. Lớp PurchaseInvoiceDAO4 gọi lớp SparePartDAO4 yêu cầu tăng tồn kho theo dòng nhập của HN1.
67. Lớp SparePartDAO4 gọi hàm increaseStockAfterPurchase() với cùng Connection của transaction hóa đơn; hàm tăng tồn PT1 thêm 10 cái.
68. Hàm increaseStockAfterPurchase() trả kết quả tăng tồn kho lại cho lớp PurchaseInvoiceDAO4.
69. Hàm savePurchaseInvoice() hoàn tất transaction và trả kết quả lưu thành công cùng mã HN1 lại cho lớp PurchaseInvoiceServlet4.
70. Lớp PurchaseInvoiceServlet4 thực hiện hàm viewInvoice() để mở hóa đơn nhập HN1 đã xác nhận.
71. Lớp PurchaseInvoiceServlet4 gọi lớp PurchaseInvoiceDAO4 yêu cầu lấy thông tin hóa đơn HN1 cùng các dòng nhập và thông tin đã chốt.
72. Lớp PurchaseInvoiceDAO4 gọi hàm getPurchaseInvoiceDetails().
73. Hàm getPurchaseInvoiceDetails() gọi lớp PurchaseInvoice4 để đóng gói thông tin thực thể.
74. Lớp PurchaseInvoice4 đóng gói thông tin thực thể.
75. Lớp PurchaseInvoice4 trả kết quả lại cho hàm getPurchaseInvoiceDetails().
76. Hàm getPurchaseInvoiceDetails() trả thông tin hóa đơn HN1 lại cho lớp PurchaseInvoiceServlet4.
77. Lớp PurchaseInvoiceServlet4 chuyển dữ liệu và gọi trang confirmedInvoice.jsp.
78. Trang confirmedInvoice.jsp hiển thị HN1 đã xác nhận, các dòng nhập, tổng tiền 800.000 đồng, trạng thái Chưa thanh toán, nút In hóa đơn, ô Số tiền đã trả và nút Đã thanh toán cho nhân viên.
79. Nhân viên A click In hóa đơn.
80. Trang confirmedInvoice.jsp gọi lớp PurchaseInvoiceServlet4 yêu cầu lấy hóa đơn HN1 đã lưu để in.
81. Lớp PurchaseInvoiceServlet4 gọi hàm printInvoice().
82. Lớp PurchaseInvoiceServlet4 gọi lớp PurchaseInvoiceDAO4 yêu cầu lấy thông tin hóa đơn HN1 cùng các dòng nhập và thông tin đã chốt.
83. Lớp PurchaseInvoiceDAO4 gọi hàm getPurchaseInvoiceDetails().
84. Hàm getPurchaseInvoiceDetails() gọi lớp PurchaseInvoice4 để đóng gói thông tin thực thể.
85. Lớp PurchaseInvoice4 đóng gói thông tin thực thể.
86. Lớp PurchaseInvoice4 trả kết quả lại cho hàm getPurchaseInvoiceDetails().
87. Hàm getPurchaseInvoiceDetails() trả thông tin hóa đơn HN1 lại cho lớp PurchaseInvoiceServlet4.
88. Lớp PurchaseInvoiceServlet4 chuyển dữ liệu và gọi trang printInvoice.jsp.
89. Trang printInvoice.jsp hiển thị nội dung chứng từ HN1 và nút In cho nhân viên; chứng từ dùng các thông tin đã chốt khi xác nhận.
90. Nhân viên A click In để mở hộp thoại in của trình duyệt và thực hiện in chứng từ HN1.
91. Trang printInvoice.jsp gọi lớp PurchaseInvoiceServlet4 yêu cầu hiển thị lại hóa đơn HN1 sau khi đóng hộp thoại in.
92. Lớp PurchaseInvoiceServlet4 gọi hàm viewInvoice().
93. Lớp PurchaseInvoiceServlet4 gọi lớp PurchaseInvoiceDAO4 yêu cầu lấy thông tin hóa đơn HN1 cùng các dòng nhập và thông tin đã chốt.
94. Lớp PurchaseInvoiceDAO4 gọi hàm getPurchaseInvoiceDetails().
95. Hàm getPurchaseInvoiceDetails() gọi lớp PurchaseInvoice4 để đóng gói thông tin thực thể.
96. Lớp PurchaseInvoice4 đóng gói thông tin thực thể.
97. Lớp PurchaseInvoice4 trả kết quả lại cho hàm getPurchaseInvoiceDetails().
98. Hàm getPurchaseInvoiceDetails() trả thông tin hóa đơn HN1 lại cho lớp PurchaseInvoiceServlet4.
99. Lớp PurchaseInvoiceServlet4 chuyển dữ liệu và gọi trang confirmedInvoice.jsp.
100. Trang confirmedInvoice.jsp hiển thị lại hóa đơn HN1 và tình trạng thanh toán hiện tại cho nhân viên.
101. Sau khi đối chiếu, giao chứng từ, thực tế thanh toán đủ 800.000 đồng cho nhà cung cấp và tiếp nhận hóa đơn bán hàng, nhân viên A nhập 800.000 đồng vào ô Số tiền đã trả và click Đã thanh toán.
102. Trang confirmedInvoice.jsp gọi lớp PurchaseInvoiceServlet4 yêu cầu ghi nhận thanh toán hóa đơn HN1.
103. Lớp PurchaseInvoiceServlet4 gọi hàm recordPayment(). Mã nhân viên kho hiện tại được lấy từ phiên đăng nhập.
104. Lớp PurchaseInvoiceServlet4 gọi lớp PurchaseInvoiceDAO4 yêu cầu ghi nhận thanh toán hóa đơn HN1.
105. Lớp PurchaseInvoiceDAO4 gọi hàm recordPayment(); hàm kiểm tra quyền nhân viên kho hiện tại, HN1 chưa thanh toán và số tiền đã trả bằng tổng tiền; ghi nhận trạng thái Đã thanh toán, thời điểm thanh toán và người thực sự trả tiền, giữ nguyên người lập.
106. Hàm recordPayment() trả kết quả ghi nhận thanh toán thành công lại cho lớp PurchaseInvoiceServlet4.
107. Lớp PurchaseInvoiceServlet4 thực hiện hàm viewInvoice() để hiển thị hóa đơn HN1 sau khi ghi nhận thanh toán.
108. Lớp PurchaseInvoiceServlet4 gọi lớp PurchaseInvoiceDAO4 yêu cầu lấy thông tin hóa đơn HN1 cùng các dòng nhập và thông tin đã chốt.
109. Lớp PurchaseInvoiceDAO4 gọi hàm getPurchaseInvoiceDetails().
110. Hàm getPurchaseInvoiceDetails() gọi lớp PurchaseInvoice4 để đóng gói thông tin thực thể.
111. Lớp PurchaseInvoice4 đóng gói thông tin thực thể.
112. Lớp PurchaseInvoice4 trả kết quả lại cho hàm getPurchaseInvoiceDetails().
113. Hàm getPurchaseInvoiceDetails() trả thông tin hóa đơn HN1 lại cho lớp PurchaseInvoiceServlet4.
114. Hàm viewInvoice() trả hóa đơn HN1 đã cập nhật thanh toán cùng thông báo thành công lại cho trang confirmedInvoice.jsp.
115. Trang confirmedInvoice.jsp hiển thị HN1 với trạng thái Đã thanh toán và thông báo ghi nhận thanh toán thành công cho nhân viên.

Các bước từ 35 đến 60 được lặp lại cho từng phụ tùng tiếp theo cho đến khi hết phụ tùng muốn nhập. PT1 là dữ liệu minh họa của lượt đầu; tổng tiền khi có nhiều dòng bằng tổng thành tiền của tất cả các dòng nhập.

![Hình 2 Biểu đồ tuần tự thiết kế modul nhân viên kho nhập phụ tùng](diagrams/tuan_tu_nhap_phu_tung.svg)

Hình 2: Biểu đồ tuần tự thiết kế modul nhân viên kho nhập phụ tùng.

[Xem PDF một biểu đồ](diagrams/sequence/purchase-sequence.pdf) · [Mã PlantUML](diagrams/tuan_tu_nhap_phu_tung.puml).

---

Nguồn trình bày: mục 4.3.2, Hình 4.10, 4.12 và 4.14 của bài giảng. Nguồn nghiệp vụ: mục 1.2.1 và 1.2.4 trong 26_B23DCCN764.docx. Các tên lớp, trang và phương thức giữ bộ tên tiếng Anh đã xác nhận trong docs. Mỗi modul chỉ có một biểu đồ tuần tự liền mạch cho kịch bản thành công. Bảng đường sống, từng thông điệp và phạm vi thanh kích hoạt để vẽ theo nằm trong [hướng dẫn Visual Paradigm](VE_SEQUENCE_VISUAL_PARADIGM.md). Căn cứ source và phần code còn cần sửa theo nghiệp vụ đã chốt nằm riêng trong [bảng đối chiếu](SCENARIO_V3_DOI_CHIEU.md).
