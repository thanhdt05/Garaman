/* Giao diện mẫu theo 26_B23DCCN764-full 1.docx. Không API, Model hay DAO. */
"use strict";
(() => {
  const $ = (selector, root = document) => root.querySelector(selector);
  const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];
  const page = location.pathname.split("/").pop() || "index.jsp";
  const storageKey = "garaman.frontend.full-report.v2";
  const money = value => new Intl.NumberFormat("vi-VN", { maximumFractionDigits: 2 }).format(Number(value));
  const currency = value => `${money(value)} ₫`;
  const normalized = value => String(value).toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "").replace(/đ/g, "d").trim();
  const today = () => new Intl.DateTimeFormat("en-CA", { timeZone: "Asia/Bangkok", year: "numeric", month: "2-digit", day: "2-digit" }).format(new Date());
  const dateLabel = value => value ? value.split("-").reverse().join("/") : "—";
  const timestamp = value => new Intl.DateTimeFormat("vi-VN", { timeZone: "Asia/Bangkok", day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit" }).format(new Date(value));
  const lineTotal = item => Math.round(Number(item.quantity) * Number(item.price) * 100) / 100;
  const total = () => (draft.cart || []).reduce((sum, item) => Math.round((sum + lineTotal(item)) * 100) / 100, 0);
  let draft = {};
  try { draft = JSON.parse(sessionStorage.getItem(storageKey) || "{}"); } catch { draft = {}; }
  if (!draft || typeof draft !== "object" || Array.isArray(draft)) draft = {};
  function save() {
    try { sessionStorage.setItem(storageKey, JSON.stringify(draft)); }
    catch { toast("Cho phép lưu trữ trên trình duyệt để giữ các lựa chọn khi chuyển bước."); }
  }
  function toast(message) {
    const element = $("#toast");
    element.textContent = message;
    element.hidden = false;
    clearTimeout(toast.timer);
    toast.timer = setTimeout(() => { element.hidden = true; }, 4500);
  }
  function showError(id, message) {
    const element = $(id);
    if (!element) return;
    element.textContent = message;
    element.hidden = !message;
  }
  function cell(text, className = "") {
    const node = document.createElement("td");
    node.textContent = text;
    node.className = className;
    return node;
  }
  function nameCell(item) {
    const node = cell("");
    const name = document.createElement("strong");
    const id = document.createElement("small");
    name.textContent = item.name;
    id.textContent = item.id;
    node.append(name, id);
    return node;
  }
  function clearPurchase() {
    // Các hồ sơ mới thêm vẫn nằm trong danh sách của phiên giao diện khi hủy.
    draft = { addedSuppliers: draft.addedSuppliers || [], addedParts: draft.addedParts || [], catalogSearch: draft.catalogSearch };
  }
  const customer = /KH4|TimDichVu|ChiTiet/.test(page);
  const warehouse = /NVKho|NhaCungCap|ChonPhuTung|ThemPhuTung|NhapPhuTung|HoaDon/.test(page);
  if (customer || warehouse) {
    $$("[data-role-section]").forEach(section => { section.hidden = section.dataset.roleSection !== (customer ? "customer" : "warehouse"); });
    $("#workspace-name").textContent = customer ? "Khách hàng" : "Nhân viên kho";
    $("#profile-name").textContent = customer ? "Nguyễn Minh Anh" : "Đàm Tiến Thành";
    $("#profile-role").textContent = customer ? "Khách hàng" : "Nhân viên kho";
    $("#profile-initials").textContent = customer ? "MA" : "TT";
  }
  const nav = page === "gdChinhKH4.jsp" ? "customer-home" : customer ? "search" : page === "gdChinhNVKho4.jsp" ? "warehouse-home" : warehouse ? "purchase" : null;
  if (nav) { const link = $(`[data-nav="${nav}"]`); link.classList.add("active"); link.setAttribute("aria-current", "page"); }
  if ($("h1")) document.title = `${$("h1").textContent} · GaraMan`;
  const step = /ChonNhaCungCap|ThemNhaCungCap/.test(page) ? 0 : /ChonPhuTung|ThemPhuTung|NhapPhuTung/.test(page) ? 1 : /XacNhanHoaDon/.test(page) ? 2 : 3;
  $$(".stepper li").forEach((item, index) => {
    if (index < step) item.classList.add("done");
    if (index === step) { item.classList.add("active"); item.setAttribute("aria-current", "step"); }
  });
  if ($("#resume-invoice")) $("#resume-invoice").hidden = !draft.confirmedAt;
  if ($("#resume-draft")) $("#resume-draft").hidden = Boolean(draft.confirmedAt) || !draft.supplier;
  $("#start-purchase")?.addEventListener("click", event => {
    if (!draft.confirmedAt && draft.cart?.length && !window.confirm("Bắt đầu hóa đơn mới và bỏ các dòng nhập đang lập?")) { event.preventDefault(); return; }
    clearPurchase(); draft.cart = []; draft.date = today(); save();
  });

  // Nhà cung cấp mới chỉ được giữ trong phiên giao diện, không có kho dữ liệu giả.
  const supplierTable = $("#supplier-table");
  if (supplierTable) {
    (draft.addedSuppliers || []).forEach(supplier => {
      const row = document.createElement("tr");
      Object.assign(row.dataset, supplier, { key: supplier.name });
      const contact = cell(supplier.phone);
      const email = document.createElement("small"); email.textContent = supplier.email || "Chưa cập nhật"; contact.append(email);
      const name = nameCell(supplier); name.querySelector("small").textContent = supplier.address;
      const status = cell(""); const badge = document.createElement("span"); badge.className = "badge"; badge.textContent = "Đang hợp tác"; status.append(badge);
      const action = cell("", "number"); const button = document.createElement("button");
      Object.assign(button, { type: "button", className: "btn btn-small select-supplier", textContent: "Chọn →" }); action.append(button);
      row.append(cell(supplier.id, "code"), name, contact, status, action); $("tbody", supplierTable).append(row);
    });
  }
  function setupSearch(formId, tableId, inputId, stateKey, errorId, label, typeSelect) {
    const form = $(formId);
    if (!form) return;
    const input = $(inputId);
    const saved = draft[stateKey];
    if (saved) input.value = saved.keyword || "";
    if (typeSelect) {
      const requested = new URLSearchParams(location.search).get("loai");
      $(typeSelect).value = ["dichvu", "phutung"].includes(requested) ? requested : saved?.type || "all";
    }
    function run(reportError = true) {
      const keyword = input.value.trim();
      const valid = Boolean(keyword);
      input.setAttribute("aria-invalid", String(!valid && reportError));
      showError(errorId, !valid && reportError ? "Vui lòng nhập tên hoặc một phần tên cần tìm." : "");
      $("#search-initial").hidden = valid;
      $("#search-empty").hidden = true;
      $("#search-results").hidden = !valid;
      if (!valid) {
        $("#result-count").textContent = "Chưa tìm kiếm";
        draft[stateKey] = { keyword: "", searched: false }; save();
        if (reportError) input.focus();
        return;
      }
      const type = typeSelect ? $(typeSelect).value : "all";
      let count = 0;
      $$(`${tableId} tbody tr`).forEach(row => {
        row.hidden = !normalized(row.dataset.key).includes(normalized(keyword)) || (type !== "all" && row.dataset.type !== type);
        if (!row.hidden) count++;
      });
      $("#result-count").textContent = `${count} ${label}`;
      $("#search-empty").hidden = count !== 0;
      $("#search-results").hidden = count === 0;
      draft[stateKey] = { keyword, type, searched: true }; save();
    }
    form.addEventListener("submit", event => { event.preventDefault(); run(); });
    if (typeSelect) $(typeSelect).addEventListener("change", () => { if (draft[stateKey]?.searched) run(); });
    if (saved?.searched) run(false);
  }
  setupSearch("#catalog-search", "#catalog-table", "#tuKhoa", "catalogSearch", "#catalog-search-error", "kết quả", "#loai");
  setupSearch("#supplier-search", "#supplier-table", "#tenNCC", "supplierSearch", "#supplier-search-error", "nhà cung cấp");
  const variant = new URLSearchParams(location.search).get("mau");
  const detailTemplate = $$("template[data-detail]").find(element => element.dataset.detail === variant);
  if (detailTemplate) {
    $$("[data-fill]", detailTemplate.content).forEach(element => {
      const target = document.getElementById(`detail-${element.dataset.fill}`);
      if (target) target.replaceChildren(...element.cloneNode(true).childNodes);
    });
    document.title = `${$("#detail-name").textContent} · GaraMan`;
    const image = $("#detail-image");
    if (image) { image.alt = `Hình minh họa ${$("#detail-name").textContent}`; image.src = detailTemplate.dataset.image || "assets/part.svg"; }
  }
  $$(".select-supplier").forEach(button => button.addEventListener("click", () => {
    const row = button.closest("tr");
    if (draft.confirmedAt) clearPurchase();
    draft.supplier = { id: row.dataset.id, name: row.dataset.name, address: row.dataset.address, phone: row.dataset.phone, email: row.dataset.email };
    if (!Array.isArray(draft.cart)) draft.cart = [];
    if (!draft.date) draft.date = today();
    save(); location.href = "gdNhapPhuTung4.jsp";
  }));
  $("#supplier-form")?.addEventListener("submit", event => {
    event.preventDefault();
    const form = event.currentTarget;
    if (!form.elements.ten.value.trim() || !form.elements.diaChi.value.trim()) { showError("#form-error", "Vui lòng nhập tên và địa chỉ có nội dung."); return; }
    const supplier = { id: `NCC-MOI-${(draft.addedSuppliers || []).length + 1}`, name: form.elements.ten.value.trim(), address: form.elements.diaChi.value.trim(), phone: form.elements.dienThoai.value.trim(), email: form.elements.email.value.trim() };
    if (!draft.addedSuppliers) draft.addedSuppliers = [];
    draft.addedSuppliers.push(supplier);
    draft.supplierSearch = { keyword: supplier.name, searched: true };
    save(); location.href = "gdChonNhaCungCap4.jsp";
  });
  function supplierFromScreen() {
    return { id: "NCC1", name: $("[data-supplier-name]")?.textContent.trim(), address: $("[data-supplier-address]")?.textContent.trim(), phone: $("[data-supplier-phone]")?.textContent.trim() || "0900000001", email: $("[data-supplier-email]")?.textContent.trim() || "kho@hoaphat.example" };
  }
  function fillSupplier() {
    if (!draft.supplier) return;
    for (const key of ["name", "address", "phone", "email"]) $$(`[data-supplier-${key}]`).forEach(element => { element.textContent = draft.supplier[key] || "Chưa cập nhật"; });
  }
  fillSupplier();

  const cartBody = $("#cart-table tbody");
  if (cartBody && draft.confirmedAt) { location.replace("gdXacNhanHoaDon4.jsp"); return; }
  if (cartBody) {
    if (!Array.isArray(draft.cart)) draft.cart = [];
    if (!draft.supplier) draft.supplier = supplierFromScreen();
    if (!draft.date) draft.date = today();
    save();
  }
  const countable = unit => ["cai", "bo", "chiec"].includes(normalized(unit));
  const decimalInput = (value, scale) => new RegExp(`^\\d+(?:\\.\\d{1,${scale}})?$`).test(String(value));
  function validItem(item) {
    const quantity = Number(item.quantity), price = Number(item.price);
    return ["cai", "bo", "chiec", "lit"].includes(normalized(item.unit)) && decimalInput(item.quantity, 3) && decimalInput(item.price, 2) && quantity > 0 && quantity <= 999999 && price > 0 && price <= 999999999999 && Number.isFinite(lineTotal(item)) && (!countable(item.unit) || Number.isInteger(quantity));
  }
  function readCart() {
    draft.cart = $$("tr", cartBody).map(row => ({ id: row.dataset.id, name: row.dataset.name, unit: row.dataset.unit, brand: row.dataset.brand, quantity: $(".quantity", row).value, price: $(".price", row).value }));
    save();
  }
  function updateTotals() {
    if (cartBody) {
      $$("tr", cartBody).forEach((row, index) => {
        row.cells[0].textContent = String(index + 1).padStart(2, "0");
        $(".line-total", row).textContent = money(lineTotal({ quantity: $(".quantity", row).value, price: $(".price", row).value }));
      });
      $("#cart-count").textContent = `${cartBody.rows.length} mặt hàng`;
      $("#cart-empty").hidden = cartBody.rows.length > 0;
      $("#confirm-purchase").disabled = cartBody.rows.length === 0;
    }
    $$("[data-invoice-total]").forEach(element => { element.textContent = currency(total()); });
  }
  function renderCart() {
    cartBody.replaceChildren();
    draft.cart.forEach((item, index) => {
      const row = document.createElement("tr");
      Object.assign(row.dataset, { id: item.id, name: item.name, unit: item.unit, brand: item.brand || "Chưa cập nhật" });
      row.append(cell(String(index + 1).padStart(2, "0"), "code"), nameCell(item), cell(item.brand || "Chưa cập nhật"), cell(item.unit));
      for (const key of ["quantity", "price"]) {
        const column = cell("", "number");
        const input = document.createElement("input");
        Object.assign(input, { className: `table-input ${key}`, type: "number", min: key === "quantity" ? (countable(item.unit) ? "1" : "0.001") : "0.01", max: key === "quantity" ? "999999" : "999999999999", step: key === "quantity" ? (countable(item.unit) ? "1" : "0.001") : "0.01", value: item[key], required: true, name: `${key === "quantity" ? "soLuong" : "donGiaNhap"}_${item.id}` });
        input.setAttribute("aria-label", `${key === "quantity" ? "Số lượng" : "Đơn giá nhập"} ${item.name}`);
        column.append(input); row.append(column);
      }
      row.append(cell("", "number line-total"));
      const action = cell(""); const remove = document.createElement("button");
      Object.assign(remove, { type: "button", className: "icon-button remove-part", textContent: "×" });
      remove.setAttribute("aria-label", `Xóa ${item.name}`); action.append(remove); row.append(action); cartBody.append(row);
    });
    updateTotals();
  }
  function focusPart(id) {
    const row = $$("tr", cartBody).find(item => item.dataset.id === id);
    row?.scrollIntoView({ behavior: "smooth", block: "center" });
    $(".quantity", row)?.focus({ preventScroll: true });
  }
  if (cartBody) {
    renderCart();
    cartBody.addEventListener("input", () => { readCart(); updateTotals(); showError("#cart-error", ""); });
    cartBody.addEventListener("click", event => {
      if (!event.target.closest(".remove-part")) return;
      event.target.closest("tr").remove(); readCart(); updateTotals(); showError("#cart-error", "");
      toast("Đã bỏ phụ tùng khỏi danh sách nhập.");
    });
    const select = $("#phuTung");
    const options = $$("option", $("#part-options").content).map(option => option.cloneNode(true));
    (draft.addedParts || []).forEach(item => {
      const option = document.createElement("option"); option.value = item.id;
      Object.assign(option.dataset, { name: item.name, unit: item.unit, brand: item.brand });
      option.textContent = `${item.id} · ${item.name} · ${item.unit} · ${item.brand}`; options.push(option);
    });
    function searchParts(reportError = true) {
      const input = $("#tenPhuTung"), keyword = input.value.trim();
      input.setAttribute("aria-invalid", String(!keyword && reportError));
      showError("#part-search-error", !keyword && reportError ? "Vui lòng nhập tên phụ tùng cần tìm." : "");
      $("#part-search-initial").hidden = Boolean(keyword);
      $("#part-search-empty").hidden = true;
      $("#part-results").hidden = true;
      select.replaceChildren(new Option("— Chọn phụ tùng để thêm vào hóa đơn —", ""));
      if (!keyword) { draft.partSearch = null; save(); if (reportError) input.focus(); return; }
      const matches = options.filter(option => normalized(option.dataset.name).includes(normalized(keyword)));
      matches.forEach(option => select.append(option.cloneNode(true)));
      $("#part-results").hidden = !matches.length;
      $("#part-search-empty").hidden = matches.length > 0;
      $("#part-result-count").textContent = `(${matches.length} kết quả)`;
      draft.partSearch = { keyword }; save();
    }
    $("#part-search").addEventListener("submit", event => { event.preventDefault(); searchParts(); });
    select.addEventListener("change", () => {
      const option = select.selectedOptions[0];
      if (!option.value) return;
      if (draft.cart.some(item => item.id === option.value)) {
        toast("Phụ tùng đã có trong hóa đơn. Sửa số lượng và đơn giá trên dòng đã chọn.");
      } else {
        draft.cart.push({ id: option.value, name: option.dataset.name, unit: option.dataset.unit, brand: option.dataset.brand, quantity: 0, price: 0 });
        save(); renderCart();
      }
      focusPart(option.value); select.value = "";
    });
    if (draft.partSearch?.keyword) { $("#tenPhuTung").value = draft.partSearch.keyword; searchParts(false); }
    $("#cancel-purchase").addEventListener("click", () => {
      if (!window.confirm("Hủy hóa đơn chưa xác nhận? Các dòng nhập sẽ bị bỏ; nhà cung cấp và phụ tùng mới thêm vẫn giữ trong danh sách của phiên này.")) return;
      clearPurchase(); save(); location.href = "gdChinhNVKho4.jsp";
    });
    $("#purchase-form").noValidate = true;
    $("#purchase-form").addEventListener("submit", event => {
      event.preventDefault(); readCart();
      if (!draft.cart.length) { showError("#cart-error", "Cần ít nhất một phụ tùng để xác nhận hóa đơn."); return; }
      const invalid = draft.cart.find(item => !validItem(item));
      if (invalid) { showError("#cart-error", "Số lượng và đơn giá nhập phải dương trong giới hạn hiển thị. Cái, Bộ, Chiếc dùng số nguyên; Lít tối đa 3 chữ số thập phân, đơn giá tối đa 2 chữ số. Không dùng số mũ."); focusPart(invalid.id); return; }
      draft.confirmedAt = draft.confirmedAt || new Date().toISOString();
      save(); location.href = "gdXacNhanHoaDon4.jsp";
    });
  }
  $("#part-form")?.addEventListener("submit", event => {
    event.preventDefault(); const form = event.currentTarget;
    for (const field of [form.elements.ten, form.elements.donVi, form.elements.hangSanXuat]) {
      field.setCustomValidity(field.value.trim() ? "" : "Vui lòng nhập nội dung.");
      if (!field.reportValidity()) { field.addEventListener("input", () => field.setCustomValidity(""), { once: true }); return; }
    }
    const fields = Object.fromEntries(new FormData(form));
    if (fields.trangThai === "Đang kinh doanh" && (!fields.moTa.trim() || !decimalInput(fields.giaBan, 2))) {
      showError("#part-form-error", "Cần mô tả và giá bán hợp lệ trước khi công bố. Có thể lưu ở trạng thái Chưa công bố để tiếp tục nhập."); return;
    }
    const part = { id: `PT-MOI-${(draft.addedParts || []).length + 1}`, name: fields.ten.trim(), unit: fields.donVi.trim(), brand: fields.hangSanXuat.trim() || "Chưa cập nhật", fields };
    if (!draft.addedParts) draft.addedParts = [];
    draft.addedParts.push(part); draft.partSearch = { keyword: part.name };
    save(); location.href = "gdNhapPhuTung4.jsp";
  });
  $("#hinhAnh")?.addEventListener("change", event => {
    const preview = $("#upload-preview"), selected = event.target.value;
    preview.hidden = !["assets/part.svg", "assets/brake.svg"].includes(selected);
    if (!preview.hidden) preview.src = selected;
  });

  // Hai trang chỉ đọc: hóa đơn đã xác nhận và bản in.
  if ($("[data-invoice-lines]")) {
    if (!draft.confirmedAt && Array.isArray(draft.cart)) { location.replace("gdNhapPhuTung4.jsp"); return; }
    if (!draft.confirmedAt) {
      // Mở trực tiếp một trang chứng từ: dùng chính HTML mẫu trong trang để chụp.
      draft.cart = $$("[data-invoice-lines] tr").map(row => ({ id: row.dataset.id, name: row.dataset.name, unit: row.dataset.unit, brand: row.dataset.brand, quantity: Number(row.dataset.quantity), price: Number(row.dataset.price) }));
      draft.supplier = supplierFromScreen();
      draft.date = $("[data-invoice-date]").textContent.trim().split("/").reverse().join("-");
      draft.confirmedAt = new Date().toISOString(); save();
    }
    fillSupplier();
    $$("[data-invoice-lines]").forEach(body => {
      body.replaceChildren();
      draft.cart.forEach((item, index) => {
        const row = document.createElement("tr");
        row.append(cell(String(index + 1).padStart(2, "0"), "code"), nameCell(item), cell(item.unit), cell(money(item.quantity), "number"), cell(money(item.price), "number"), cell(money(lineTotal(item)), "number"));
        body.append(row);
      });
    });
    updateTotals();
  }
  if (draft.date) $$("[data-invoice-date]").forEach(element => { element.textContent = dateLabel(draft.date); });
  if (draft.confirmedAt) $$("[data-confirmation-time]").forEach(element => { element.textContent = timestamp(draft.confirmedAt); });
  function updatePayment() {
    const paid = Boolean(draft.paidAt);
    $$("[data-payment-badge]").forEach(element => { element.textContent = paid ? "Đã thanh toán" : "Chưa thanh toán"; element.className = paid ? "badge" : "badge pending"; });
    $$("[data-payment-status]").forEach(element => { element.textContent = paid ? "Đã thanh toán" : "Chưa thanh toán"; });
    $$("[data-payment-details], #payment-time-row").forEach(element => { element.hidden = !paid; });
    if (paid) {
      $$("[data-payment-time]").forEach(element => { element.textContent = timestamp(draft.paidAt); });
      $$("[data-paid-amount]").forEach(element => { element.textContent = currency(draft.paidAmount); });
      if ($("#invoice-notice")) $("#invoice-notice").textContent = "Đã ghi nhận thanh toán thành công.";
    }
    if ($("#soTienDaTra")) {
      $("#soTienDaTra").value = paid ? draft.paidAmount : "0";
      $("#soTienDaTra").readOnly = paid;
      $("#pay-invoice").disabled = paid;
    }
  }
  updatePayment();
  $("#payment-form")?.addEventListener("submit", event => {
    event.preventDefault();
    if (draft.paidAt) { toast("Hóa đơn đã được ghi nhận thanh toán trước đó."); return; }
    const input = $("#soTienDaTra"), amount = Number(input.value);
    if (!input.checkValidity() || amount <= 0 || Math.round(amount * 100) !== Math.round(total() * 100)) {
      showError("#payment-error", `Chỉ ghi nhận khi đã trả đủ và nhập đúng tổng tiền ${currency(total())}. Hóa đơn vẫn ở trạng thái Chưa thanh toán.`);
      input.setAttribute("aria-invalid", "true"); input.focus(); return;
    }
    input.setAttribute("aria-invalid", "false"); showError("#payment-error", "");
    draft.paidAmount = amount; draft.paidAt = new Date().toISOString(); save(); updatePayment();
    toast("Ghi nhận thanh toán thành công.");
  });
  let printRequested = false;
  $("#print-invoice")?.addEventListener("click", () => { printRequested = true; window.print(); });
  if ($("#print-invoice")) window.addEventListener("afterprint", () => {
    if (printRequested) location.href = "gdXacNhanHoaDon4.jsp";
  });
})();
