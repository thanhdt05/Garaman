"""Exactly one success sequence per module; generate prose and VP message tables."""
from pathlib import Path
import json

HERE = Path(__file__).resolve().parent
DOCS = HERE.parents[1]
DIAGRAMS = HERE.parent

STYLE = '''skinparam monochrome true
skinparam shadowing false
skinparam defaultFontName "Times New Roman"
skinparam defaultFontSize 14
skinparam sequenceMessageAlign center
skinparam maxMessageSize 240
skinparam ParticipantPadding 16
skinparam responseMessageBelowArrow true
hide footbox
'''


class Sequence:
    def __init__(self, stem, title, participants):
        self.stem, self.title, self.participants = stem, title, participants
        self.steps, self.events, self.loops = [], [], []

    def name(self, alias):
        return self.participants[alias][1]

    def subject(self, alias):
        kind, name = self.participants[alias]
        return ("Trang " if kind == "boundary" else "Lớp ") + name

    def msg(self, src, dst, label, text, reply=False, start=0, stop=0):
        n = len(self.steps) + 1
        self.steps.append(dict(number=n, src=src, dst=dst, label=label, text=text,
                               kind="Trả về" if reply else "Tự gọi" if src == dst else "Gọi",
                               start=start, stop=stop))
        self.events.append(f'{src} {"-->" if reply else "->"} {dst} : {n}: {label}')
        self.events.extend([f"activate {dst}"] * start)
        self.events.extend([f"deactivate {src}"] * stop)
        return n

    def invoke(self, caller, target, method, args, purpose, detail=None):
        self.msg(caller, target, "gọi", f"{self.subject(caller)} gọi lớp {self.name(target)} yêu cầu {purpose}.", start=1)
        self.msg(target, target, f"{method}({args})", detail or f"Lớp {self.name(target)} gọi hàm {method}().", start=1)

    def result(self, target, caller, method, result):
        recipient = ("trang " if self.participants[caller][0] == 'boundary' else "lớp ") + self.name(caller)
        self.msg(target, caller, "trả về", f"Hàm {method}() trả {result} lại cho {recipient}.", reply=True, stop=2)

    def pack(self, caller, entity, method, what="thông tin thực thể"):
        self.msg(caller, entity, "gọi", f"Hàm {method}() gọi lớp {self.name(entity)} để đóng gói {what}.", start=1)
        self.msg(entity, entity, f"{self.name(entity)}()", f"Lớp {self.name(entity)} đóng gói {what}.", start=1)
        self.events.append(f"deactivate {entity}")
        self.msg(entity, caller, "trả về", f"Lớp {self.name(entity)} trả kết quả lại cho hàm {method}().", reply=True, stop=1)

    def query(self, caller, dao, entity, method, args, purpose, result):
        self.invoke(caller, dao, method, args, purpose)
        self.pack(dao, entity, method)
        self.result(dao, caller, method, result)

    def request(self, view, controller, method, purpose, detail=None):
        text = f"Trang {self.name(view)} gọi hàm {method}() của lớp {self.name(controller)} yêu cầu {purpose}."
        if detail:
            text += " " + detail
        self.msg(view, controller, f"{method}(request, response)", text, start=1)

    def display(self, controller, view, text, label="hiển thị"):
        self.msg(controller, view, "gọi", f"Lớp {self.name(controller)} chuyển dữ liệu và gọi trang {self.name(view)}.", start=1, stop=1)
        self.msg(view, "A", label, text, reply=True, stop=1)

    def next_action(self, controller, method, text):
        # A new request execution on the same control lifeline; transport is omitted.
        self.events.append(f"deactivate {controller}")
        self.msg(controller, controller, f"{method}(request, response)", text, start=1)
        self.steps[-1]["before_stop"] = 1

    def invoice(self):
        self.query("C", "ID", "I", "getPurchaseInvoiceDetails", "invoiceId",
                   "lấy thông tin hóa đơn HN1 cùng các dòng nhập và thông tin đã chốt", "thông tin hóa đơn HN1")

    def write(self):
        lines = ["@startuml", STYLE, "title " + self.title]
        for alias, (kind, name) in self.participants.items():
            label = name if kind in ("actor", "boundary") else ": " + name
            lines.append(f'{kind} "{label}" as {alias}')
        lines.extend(["", *self.events, "@enduml", ""])
        path = DIAGRAMS / (self.stem + ".puml")
        path.write_text("\n".join(lines), encoding="utf-8")
        return dict(stem=self.stem, title=self.title, participants=self.participants,
                    steps=self.steps, loops=self.loops, source="../" + path.name)


catalog = Sequence("tuan_tu_tra_cuu", "Biểu đồ tuần tự thiết kế modul tìm kiếm thông tin dịch vụ/phụ tùng", {
    "A": ("actor", "Khách hàng"), "Home": ("boundary", "customerHome.jsp"),
    "Search": ("boundary", "catalogSearch.jsp"), "Detail": ("boundary", "itemDetails.jsp"),
    "C": ("control", "CatalogServlet4"), "SD": ("control", "ServiceDAO4"),
    "PD": ("control", "SparePartDAO4"), "S": ("entity", "Service4"), "P": ("entity", "SparePart4"),
})
catalog.msg("A", "Home", "click tìm thông tin", "Tại trang customerHome.jsp, sau khi đăng nhập, khách hàng A click chọn chức năng tìm thông tin dịch vụ/phụ tùng.")
catalog.request("Home", "C", "showSearch", "mở giao diện tìm kiếm thông tin dịch vụ/phụ tùng")
catalog.display("C", "Search", "Trang catalogSearch.jsp hiển thị ô nhập tên và nút Tìm kiếm cho khách hàng; chưa có danh sách kết quả.")
catalog_search = len(catalog.steps) + 1
catalog.msg("A", "Search", "nhập lọc; click Tìm kiếm", "Khách hàng A nhập từ khóa “lọc” và click Tìm kiếm.")
catalog.request("Search", "C", "searchCatalog", "tìm thông tin dịch vụ/phụ tùng theo từ khóa “lọc”")
catalog.query("C", "SD", "S", "findServicesByName", '"lọc"', "tìm các dịch vụ đang cung cấp có tên chứa từ khóa “lọc”", "danh sách dịch vụ")
catalog.query("C", "PD", "P", "findSparePartsByName", '"lọc", true', "tìm các phụ tùng đang kinh doanh đã công bố có tên chứa từ khóa “lọc”", "danh sách phụ tùng")
catalog.display("C", "Search", "Trang catalogSearch.jsp hiển thị danh sách kết quả cho khách hàng, gồm mã, tên, loại và đơn giá.")
catalog_detail = len(catalog.steps) + 1
catalog.msg("A", "Search", "click chọn PT1", "Khách hàng A click chọn phụ tùng PT1.")
catalog.request("Search", "C", "viewDetails", "xem thông tin chi tiết phụ tùng PT1 đã chọn")
catalog.query("C", "PD", "P", "getSparePartDetails", "sparePartId, true", "lấy thông tin chi tiết phụ tùng PT1 và nhóm phụ tùng tương ứng", "thông tin phụ tùng PT1")
catalog.display("C", "Detail", "Trang itemDetails.jsp hiển thị thông tin chi tiết phụ tùng PT1 và nút Quay lại cho khách hàng.")
catalog.msg("A", "Detail", "click Quay lại", "Khách hàng A click Quay lại.")
catalog.request("Detail", "C", "searchCatalog", "tìm lại thông tin theo từ khóa “lọc” và bộ lọc đã chọn")
catalog.query("C", "SD", "S", "findServicesByName", '"lọc"', "tìm lại các dịch vụ theo từ khóa đã giữ", "danh sách dịch vụ")
catalog.query("C", "PD", "P", "findSparePartsByName", '"lọc", true', "tìm lại các phụ tùng theo từ khóa đã giữ", "danh sách phụ tùng")
catalog.display("C", "Search", "Trang catalogSearch.jsp hiển thị lại danh sách kết quả cho khách hàng, giữ từ khóa và bộ lọc đã chọn.")


purchase = Sequence("tuan_tu_nhap_phu_tung", "Biểu đồ tuần tự thiết kế modul nhân viên kho nhập phụ tùng", {
    "A": ("actor", "Nhân viên kho"), "NCC": ("actor", "Nhà cung cấp"),
    "Home": ("boundary", "warehouseHome.jsp"), "Select": ("boundary", "selectSupplier.jsp"),
    "Draft": ("boundary", "purchaseInvoice.jsp"), "View": ("boundary", "confirmedInvoice.jsp"),
    "Print": ("boundary", "printInvoice.jsp"), "SC": ("control", "SupplierServlet4"),
    "C": ("control", "PurchaseInvoiceServlet4"), "SD": ("control", "SupplierDAO4"),
    "WD": ("control", "WarehouseStaffDAO4"), "PD": ("control", "SparePartDAO4"),
    "ID": ("control", "PurchaseInvoiceDAO4"), "S": ("entity", "Supplier4"),
    "W": ("entity", "WarehouseStaff4"), "P": ("entity", "SparePart4"),
    "I": ("entity", "PurchaseInvoice4"),
})
purchase.msg("A", "Home", "click nhập phụ tùng", "Tại trang warehouseHome.jsp, sau khi đăng nhập, nhân viên A click chọn chức năng nhập phụ tùng.")
purchase.request("Home", "SC", "showSupplierSelection", "mở giao diện tìm và chọn nhà cung cấp")
purchase.display("SC", "Select", "Trang selectSupplier.jsp hiển thị ô nhập tên nhà cung cấp và nút Tìm kiếm cho nhân viên.")
purchase.msg("A", "Select", "nhập Hòa Phát; click Tìm kiếm", "Nhân viên A nhập “Hòa Phát” và click Tìm kiếm.")
purchase.request("Select", "SC", "searchSuppliers", "tìm nhà cung cấp theo tên “Hòa Phát”")
purchase.query("SC", "SD", "S", "findSuppliersByName", '"Hòa Phát"', "tìm các nhà cung cấp đang hoạt động có tên chứa từ khóa “Hòa Phát”", "danh sách nhà cung cấp")
purchase.display("SC", "Select", "Trang selectSupplier.jsp hiển thị danh sách nhà cung cấp cho nhân viên.")
purchase.msg("A", "Select", "click chọn NCC1", "Nhân viên A click chọn nhà cung cấp NCC1.")
purchase.request("Select", "C", "startPurchase", "lập hóa đơn nhập với nhà cung cấp NCC1 đã chọn", "Mã nhân viên kho được lấy từ phiên đăng nhập.")
purchase.query("C", "SD", "S", "getSupplierDetails", "supplierId", "lấy thông tin nhà cung cấp NCC1", "thông tin nhà cung cấp NCC1")
purchase.query("C", "WD", "W", "getWarehouseStaffDetails", "warehouseStaffId", "lấy mã và họ tên nhân viên kho A", "thông tin nhân viên kho A")
purchase.next_action("C", "showDraft", "Sau khi tạo hóa đơn nhập dự kiến, lớp PurchaseInvoiceServlet4 thực hiện hàm showDraft() để hiển thị hóa đơn.")
purchase.display("C", "Draft", "Trang purchaseInvoice.jsp hiển thị thông tin nhà cung cấp NCC1, nhân viên A, ngày nhập hiện tại, ô tìm phụ tùng, bảng các dòng nhập đang trống, tổng tiền bằng 0 và nút Xác nhận cho nhân viên.")

purchase.events.append("loop [lặp đến khi đủ phụ tùng cần nhập]")
line_first = len(purchase.steps) + 1
purchase.msg("A", "Draft", "nhập Lọc; click Tìm kiếm", "Nhân viên A nhập “Lọc” và click Tìm kiếm.")
purchase.request("Draft", "C", "searchSpareParts", "tìm phụ tùng theo tên “Lọc”")
purchase.next_action("C", "showDraft", "Lớp PurchaseInvoiceServlet4 giữ dữ liệu các dòng đang nhập và thực hiện hàm showDraft() theo từ khóa vừa nhận.")
purchase.query("C", "PD", "P", "findSparePartsByName", '"Lọc", false', "tìm các phụ tùng chưa công bố hoặc đang kinh doanh có tên chứa từ khóa “Lọc”", "danh sách phụ tùng")
purchase.display("C", "Draft", "Trang purchaseInvoice.jsp hiển thị danh sách phụ tùng tìm được ngay trên hóa đơn, gồm mã, tên, đơn vị tính và hãng sản xuất cho nhân viên chọn.")
purchase.msg("A", "Draft", "click chọn PT1", "Nhân viên A click chọn phụ tùng PT1.")
purchase.request("Draft", "C", "addInvoiceLine", "bổ sung phụ tùng PT1 vào hóa đơn nhập dự kiến")
purchase.query("C", "PD", "P", "getSparePartDetails", "sparePartId, false", "lấy thông tin chi tiết phụ tùng PT1", "thông tin phụ tùng PT1")
purchase.next_action("C", "showDraft", "Lớp PurchaseInvoiceServlet4 bổ sung dòng PT1 vào hóa đơn nhập dự kiến, giữ dữ liệu các dòng đã nhập và thực hiện hàm showDraft() để hiển thị lại hóa đơn.")
purchase.display("C", "Draft", "Trang purchaseInvoice.jsp hiển thị dòng PT1 cho nhân viên; số lượng, đơn giá nhập và thành tiền ban đầu bằng 0.")
purchase.msg("A", "Draft", "nhập 10 cái; 80.000 đồng/cái", "Nhân viên A đối chiếu với nhà cung cấp, nhập số lượng 10 và đơn giá nhập 80.000 đồng/cái.")
purchase.msg("Draft", "A", "hiển thị", "Trang purchaseInvoice.jsp hiển thị dòng nhập, thành tiền và tổng tiền dự kiến 800.000 đồng cho nhân viên.", reply=True)
line_last = len(purchase.steps)
purchase.steps[-1]["text"] += f" (Lặp lại các bước từ {line_first} đến {line_last} cho đến khi hết phụ tùng muốn nhập.)"
purchase.events.append("end")
purchase.loops.append(dict(first=line_first, last=line_last, condition="lặp đến khi đủ phụ tùng cần nhập"))

purchase.msg("A", "Draft", "click Xác nhận", "Nhân viên A đối chiếu hàng thực nhận, chứng từ giao hàng và các dòng nhập, sau đó click Xác nhận.")
purchase.request("Draft", "C", "confirmInvoice", "xác nhận hóa đơn nhập", "Hàm nhận dữ liệu các dòng nhập và kiểm tra hóa đơn có ít nhất một dòng, số lượng và đơn giá nhập hợp lệ.")
purchase.invoke("C", "ID", "savePurchaseInvoice", "purchaseInvoice", "lưu hóa đơn nhập cùng các dòng nhập", "Lớp PurchaseInvoiceDAO4 gọi hàm savePurchaseInvoice(); hàm lưu hóa đơn HN1, các dòng nhập, thời điểm xác nhận và trạng thái Chưa thanh toán; chốt tên nhà cung cấp, tên nhân viên lập, tên và đơn vị tính phụ tùng.")
purchase.events.append("loop [với từng phụ tùng trong hóa đơn nhập]")
stock_first = len(purchase.steps) + 1
purchase.invoke("ID", "PD", "increaseStockAfterPurchase", "connection, sparePartId, quantity", "tăng tồn kho theo dòng nhập của HN1", "Lớp SparePartDAO4 gọi hàm increaseStockAfterPurchase() với cùng Connection của transaction hóa đơn; hàm tăng tồn PT1 thêm 10 cái.")
purchase.result("PD", "ID", "increaseStockAfterPurchase", "kết quả tăng tồn kho")
stock_last = len(purchase.steps)
purchase.events.append("end")
purchase.loops.append(dict(first=stock_first, last=stock_last, condition="với từng phụ tùng trong hóa đơn nhập"))
purchase.msg("ID", "C", "trả về", "Hàm savePurchaseInvoice() hoàn tất transaction và trả kết quả lưu thành công cùng mã HN1 lại cho lớp PurchaseInvoiceServlet4.", reply=True, stop=2)
purchase.next_action("C", "viewInvoice", "Lớp PurchaseInvoiceServlet4 thực hiện hàm viewInvoice() để mở hóa đơn nhập HN1 đã xác nhận.")
purchase.invoice()
purchase.display("C", "View", "Trang confirmedInvoice.jsp hiển thị HN1 đã xác nhận, các dòng nhập, tổng tiền 800.000 đồng, trạng thái Chưa thanh toán, nút In hóa đơn, ô Số tiền đã trả và nút Đã thanh toán cho nhân viên.")

purchase.msg("A", "View", "click In hóa đơn", "Nhân viên A click In hóa đơn.")
purchase.request("View", "C", "printInvoice", "lấy hóa đơn HN1 đã lưu để in")
purchase.invoice()
purchase.display("C", "Print", "Trang printInvoice.jsp hiển thị nội dung chứng từ HN1 và nút In cho nhân viên; chứng từ dùng các thông tin đã chốt khi xác nhận.")
purchase.msg("A", "Print", "click In", "Nhân viên A click In.")
purchase.msg("Print", "Print", "printInvoice()", "Trang printInvoice.jsp thực hiện in chứng từ HN1 bằng hàm printInvoice() trong print.js.", start=1)
purchase.events.append("deactivate Print")
purchase.request("Print", "C", "viewInvoice", "hiển thị lại hóa đơn HN1 sau khi đóng hộp thoại in")
purchase.invoice()
purchase.display("C", "View", "Trang confirmedInvoice.jsp hiển thị lại hóa đơn HN1 và tình trạng thanh toán hiện tại cho nhân viên.")
purchase.msg("A", "NCC", "giao chứng từ; trả 800.000 đồng", "Nhân viên A đối chiếu với nhà cung cấp, giao chứng từ và thực tế thanh toán đủ 800.000 đồng.")
purchase.msg("NCC", "NCC", "nhận chứng từ và tiền", "Nhà cung cấp nhận chứng từ đối chiếu và tiền thanh toán.")
purchase.msg("NCC", "A", "giao hóa đơn bán hàng", "Nhà cung cấp giao hóa đơn bán hàng cho gara.")
purchase.msg("A", "A", "tiếp nhận hóa đơn bán hàng", "Nhân viên A tiếp nhận hóa đơn bán hàng do nhà cung cấp lập.")
pay_first = len(purchase.steps) + 1
purchase.msg("A", "View", "nhập 800.000; click Đã thanh toán", "Nhân viên A nhập 800.000 đồng vào ô Số tiền đã trả và click Đã thanh toán.")
purchase.request("View", "C", "recordPayment", "ghi nhận thanh toán hóa đơn HN1", "Mã nhân viên kho hiện tại được lấy từ phiên đăng nhập.")
purchase.invoke("C", "ID", "recordPayment", "invoiceId, amountPaid, currentWarehouseStaffId", "ghi nhận thanh toán hóa đơn HN1", "Lớp PurchaseInvoiceDAO4 gọi hàm recordPayment(); hàm kiểm tra quyền nhân viên kho hiện tại, HN1 chưa thanh toán và số tiền đã trả bằng tổng tiền; ghi nhận trạng thái Đã thanh toán, thời điểm thanh toán và người thực sự trả tiền, giữ nguyên người lập.")
purchase.result("ID", "C", "recordPayment", "kết quả ghi nhận thanh toán thành công")
purchase.next_action("C", "viewInvoice", "Lớp PurchaseInvoiceServlet4 thực hiện hàm viewInvoice() để hiển thị hóa đơn HN1 sau khi ghi nhận thanh toán.")
purchase.invoice()
purchase.display("C", "View", "Trang confirmedInvoice.jsp hiển thị HN1 với trạng thái Đã thanh toán và thông báo ghi nhận thanh toán thành công cho nhân viên.", "thông báo thành công")


def prose(seq):
    return "\n".join(f'{s["number"]}. {s["text"]}' for s in seq.steps)


def drawing_guide(seq):
    lines = [f"## {seq.title}", "", "Thứ tự đường sống từ trái sang phải:", "",
             "| STT | Tên trên hình | Loại |", "|---|---|---|"]
    for i, (kind, name) in enumerate(seq.participants.values(), 1):
        lines.append(f'| {i} | {name} | {dict(actor="Actor", boundary="Boundary", control="Control", entity="Entity")[kind]} |')
    lines += ["", "Thông điệp từ trên xuống dưới. Cột Thanh kích hoạt ghi thao tác ngay sau khi vẽ thông điệp:", "",
              "| Bước | Từ | Đến | Loại thông điệp | Nhãn trên hình | Thanh kích hoạt |",
              "|---|---|---|---|---|---|"]
    for s in seq.steps:
        commands = []
        if s["start"]:
            commands.append("mở " + str(s["start"]) + " thanh tại " + seq.name(s["dst"]))
        if s["stop"]:
            commands.append("đóng " + str(s["stop"]) + " thanh tại " + seq.name(s["src"]))
        if s.get("before_stop"):
            commands.insert(0, "đóng thanh hàm trước tại " + seq.name(s["src"]) + " trước thông điệp này")
        # Constructors and browser print close their short self-activation immediately.
        if s["label"] == seq.name(s["src"]) + "()":
            commands.append("đóng thanh constructor sau khi đóng gói")
        if s["src"] == s["dst"] == "Print":
            commands.append("đóng thanh xử lí in khi hộp thoại đóng")
        label = s["label"].replace("|", "\\|")
        lines.append(f'| {s["number"]} | {seq.name(s["src"])} | {seq.name(s["dst"])} | {s["kind"]} | {s["number"]}: {label} | {"; ".join(commands) or "giữ các thanh đang mở"} |')
    lines += ["", "Các khung lặp trên cùng biểu đồ:", ""]
    for loop in seq.loops:
        lines.append(f'- Bao các thông điệp **{loop["first"]}–{loop["last"]}** trong khung `loop [{loop["condition"]}]`.')
    if not seq.loops:
        lines.append("Luồng minh họa thực hiện một lượt tra cứu và xem PT1. Câu lặp lại ở cuối scenario cho biết cách tiếp tục; không vẽ thêm một sequence.")
    lines.append("")
    return "\n".join(lines)


def main():
    manifest = [catalog.write(), purchase.write()]
    (HERE / "sequence-manifest.json").write_text(json.dumps(manifest, ensure_ascii=False, indent=2)+"\n", encoding="utf-8")
    report = f'''# Scenario v3.0 và biểu đồ tuần tự thiết kế hai modul

## a. Thiết kế động modul tìm kiếm thông tin dịch vụ/phụ tùng

Biểu đồ tuần tự thiết kế modul tìm kiếm thông tin dịch vụ/phụ tùng được trình bày trong Hình 1. Kịch bản v3.0 của modul diễn ra như sau:

{prose(catalog)}

Lặp lại các bước từ {catalog_detail} đến {len(catalog.steps)} nếu khách hàng muốn xem phụ tùng khác hoặc từ bước {catalog_search} nếu muốn tìm theo từ khóa khác.

![Hình 1 Biểu đồ tuần tự thiết kế modul tìm kiếm thông tin dịch vụ/phụ tùng](diagrams/tuan_tu_tra_cuu.svg)

Hình 1: Biểu đồ tuần tự thiết kế modul tìm kiếm thông tin dịch vụ/phụ tùng.

[Xem PDF một biểu đồ](diagrams/sequence/catalog-sequence.pdf) · [Mã PlantUML](diagrams/tuan_tu_tra_cuu.puml).

## b. Thiết kế động modul nhân viên kho nhập phụ tùng

Biểu đồ tuần tự thiết kế modul nhân viên kho nhập phụ tùng được trình bày trong Hình 2. Kịch bản v3.0 của modul diễn ra như sau:

{prose(purchase)}

Các bước từ {line_first} đến {line_last} được lặp lại cho từng phụ tùng tiếp theo cho đến khi hết phụ tùng muốn nhập. PT1 là dữ liệu minh họa của lượt đầu; tổng tiền khi có nhiều dòng bằng tổng thành tiền của tất cả các dòng nhập.

![Hình 2 Biểu đồ tuần tự thiết kế modul nhân viên kho nhập phụ tùng](diagrams/tuan_tu_nhap_phu_tung.svg)

Hình 2: Biểu đồ tuần tự thiết kế modul nhân viên kho nhập phụ tùng.

[Xem PDF một biểu đồ](diagrams/sequence/purchase-sequence.pdf) · [Mã PlantUML](diagrams/tuan_tu_nhap_phu_tung.puml).

---

Nguồn trình bày: mục 4.3.2, Hình 4.10, 4.12 và 4.14 của bài giảng. Nguồn nghiệp vụ: mục 1.2.1 và 1.2.4 trong 26_B23DCCN764.docx. Các tên lớp, trang và phương thức giữ bộ tên tiếng Anh đã xác nhận trong docs. Mỗi modul chỉ có một biểu đồ tuần tự liền mạch cho kịch bản thành công. Bảng đường sống, từng thông điệp và phạm vi thanh kích hoạt để vẽ theo nằm trong [hướng dẫn Visual Paradigm](VE_SEQUENCE_VISUAL_PARADIGM.md). Căn cứ source và phần code còn cần sửa theo nghiệp vụ đã chốt nằm riêng trong [bảng đối chiếu](SCENARIO_V3_DOI_CHIEU.md).
'''
    (DOCS / "SCENARIO_V3_VA_BIEU_DO_TUAN_TU.md").write_text(report, encoding="utf-8")
    guide = '''# Hướng dẫn vẽ hai sequence trong Visual Paradigm

Tạo đúng hai Sequence Diagram, mỗi modul một biểu đồ. Giữ nguyên các đường sống từ đầu đến cuối; kéo dài trang để chứa toàn bộ thông điệp, không chia thành các biểu đồ con. Các bảng dưới đây chép đúng thứ tự và số thông điệp của [scenario v3.0](SCENARIO_V3_VA_BIEU_DO_TUAN_TU.md).

Vẽ lời gọi bằng nét liền, kết quả trả về bằng nét đứt, tự gọi bằng mũi tên quay về chính đường sống. Các thanh kích hoạt lồng nhau biểu diễn hàm đang thực hiện và hàm được gọi, theo mẫu Hình 4.10 của bài giảng. Self-message mang tên phương thức sau thông điệp “gọi” thể hiện bắt đầu thực hiện phương thức ở lớp nhận, không biểu diễn việc Java gọi đệ quy phương thức đó. Constructor/đóng gói thực thể cũng được tách thành bước riêng như bài giảng. Tên hiển thị dùng đúng các tên trong bảng; với DAO/Servlet/thực thể có thể đặt dạng đối tượng `: ClassName` như hình đã xuất.

Thông điệp từ trang JSP đại diện yêu cầu trang qua trình duyệt. Các mốc showDraft/viewInvoice trên cùng đường sống Servlet biểu diễn bắt đầu xử lí yêu cầu mở giao diện tiếp theo; thanh của hàm trước đã kết thúc trước mốc đó. Việc chuyển hướng và phát sinh GET mới được lược, không diễn giải mũi tên này thành lời gọi Java trực tiếp giữa hai hàm. Hàm in JavaScript được gom thành một bước trên đường sống printInvoice.jsp. Giao chứng từ, nhận tiền và nhận hóa đơn bán hàng là tương tác thực tế giữa hai actor.

Mẫu bài giảng lược các bước đóng gói thuộc tính con. Khi DAO trả PurchaseInvoice4, các dòng nhập và thông tin nhà cung cấp/nhân viên đã được đóng gói bên trong. Các bước giữ, sao chép và xóa nháp, hàm private lưu đầu/dòng hóa đơn, getter và tính tiền khi render được gom trong bước nghiệp vụ tương ứng. Riêng lời gọi tăng tồn vẫn được giữ vì thể hiện tác động nhập kho. Lớp PurchaseInvoice4 tính tổng qua calculateTotalAmount(); JSP hiển thị kết quả, DAO đối chiếu tiền từ các dòng đã lưu khi ghi nhận thanh toán. Không vẽ thêm lời gọi tính tổng từ DAO hoặc Servlet.

'''
    (DOCS / "VE_SEQUENCE_VISUAL_PARADIGM.md").write_text(guide + drawing_guide(catalog) + "\n" + drawing_guide(purchase), encoding="utf-8")
    print(json.dumps({s.stem: len(s.steps) for s in (catalog, purchase)}))
    print("purchase line loop", line_first, line_last, "payment starts", pay_first)


if __name__ == "__main__":
    main()
