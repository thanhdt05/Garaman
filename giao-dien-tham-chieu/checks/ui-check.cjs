/* Kiểm tra giao diện theo bản báo cáo đầy đủ; cần Tomcat đang chạy. */
const { chromium } = require(process.env.PLAYWRIGHT_MODULE || '../.tools/browser/node_modules/playwright');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const base = process.env.GARAMAN_URL || 'http://localhost:8080/GaraMan/';
const output = path.join(__dirname, '..', 'docs', 'anh-giao-dien');
const screens = [
  ['index.jsp', '00-chon-khong-gian'],
  ['gdChinhKH4.jsp', '01-trang-chinh-khach-hang'],
  ['gdTimDichVuPhuTung4.jsp', '14-tra-cuu-ban-dau'],
  ['gdChiTietDichVu4.jsp', '03-chi-tiet-dich-vu'],
  ['gdChiTietPhuTung4.jsp', '04-chi-tiet-phu-tung'],
  ['gdChinhNVKho4.jsp', '05-trang-chinh-nhan-vien-kho'],
  ['gdChonNhaCungCap4.jsp', '16-nha-cung-cap-ban-dau'],
  ['gdThemNhaCungCap4.jsp', '07-them-nha-cung-cap'],
  ['gdThemPhuTung4.jsp', '09-them-phu-tung'],
  ['gdNhapPhuTung4.jsp', '15-hoa-don-ban-dau'],
  ['gdXacNhanHoaDon4.jsp', '11-xac-nhan-hoa-don'],
  ['gdHoaDonNhap4.jsp', '12-hoa-don-nhap'],
  ['gdChonPhuTung4.jsp', null]
];
(async () => {
  fs.mkdirSync(output, { recursive: true });
  const browser = await chromium.launch({ channel: 'msedge', headless: true });
  const errors = [];
  const watch = page => {
    page.on('pageerror', error => errors.push(error.message));
    page.on('response', response => { if (response.status() >= 400) errors.push(`${response.status()} ${response.url()}`); });
  };
  const shot = async (page, name) => {
    await page.evaluate(() => { document.activeElement?.blur(); window.scrollTo(0, 0); });
    await page.waitForFunction(() => window.scrollY === 0);
    return page.screenshot({ path: path.join(output, name + '.png'), fullPage: true });
  };
  const searchPart = async (page, keyword, id) => {
    await page.locator('#tenPhuTung').fill(keyword);
    await page.locator('#part-search button').click();
    await page.locator('#phuTung').selectOption(id);
  };
  try {
    for (const [url, filename] of screens) {
      const context = await browser.newContext({ viewport: { width: 1440, height: 1000 }, locale: 'vi-VN' });
      const page = await context.newPage(); watch(page);
      const response = await page.goto(base + url, { waitUntil: 'networkidle' });
      assert.equal(response.status(), 200, url);
      assert.ok(await page.locator('h1').textContent(), url);
      assert.equal(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true, `${url}: desktop overflow`);
      assert.equal(await page.locator('main').evaluate(node => /[a-zA-Z]\?[a-zA-Z]/.test(node.textContent)), false, `${url}: damaged Vietnamese text`);
      if (filename) await shot(page, filename);
      if (url === 'gdHoaDonNhap4.jsp') await page.pdf({ path: path.join(output, 'hoa-don-nhap-A4.pdf'), format: 'A4', printBackground: true, preferCSSPageSize: true });
      await context.close();
    }
    console.log('PASS: JSP compilation, UTF-8, desktop layouts and screenshots.');
    const context = await browser.newContext({ viewport: { width: 1440, height: 1000 }, locale: 'vi-VN' });
    const page = await context.newPage(); watch(page);
    await page.goto(base + 'gdTimDichVuPhuTung4.jsp');
    assert.equal(await page.locator('#search-results').isVisible(), false);
    assert.equal(await page.locator('nav[data-role-section="warehouse"]').isVisible(), false);
    await page.locator('#tuKhoa').fill('   ');
    await page.locator('#catalog-search button').click();
    assert.equal(await page.locator('#catalog-search-error').isVisible(), true);
    assert.equal(await page.locator('#search-results').isVisible(), false);
    await page.locator('#tuKhoa').fill('loc');
    await page.locator('#catalog-search button').click();
    assert.equal(await page.locator('#catalog-table tbody tr:visible').count(), 2);
    await shot(page, '02-tim-dich-vu-phu-tung');
    await page.locator('a[href="gdChiTietPhuTung4.jsp"]').click();
    assert.equal(await page.locator('#detail-name').textContent(), 'Lọc dầu động cơ AF-01');
    assert.equal(await page.locator('#detail-price').textContent(), '150.000 ₫');
    assert.equal(await page.locator('#detail-brand').textContent(), 'AutoFilter');
    await page.getByRole('link', { name: '← Quay lại tìm kiếm' }).click();
    assert.equal(await page.locator('#tuKhoa').inputValue(), 'loc');
    assert.equal(await page.locator('#catalog-table tbody tr:visible').count(), 2);
    await page.locator('#loai').selectOption('dichvu');
    assert.equal(await page.locator('#catalog-table tbody tr:visible').count(), 1);
    await page.locator('a[href="gdChiTietDichVu4.jsp"]').click();
    assert.equal(await page.locator('#detail-name').textContent(), 'Thay lọc dầu động cơ');
    assert.equal(await page.locator('#detail-price').textContent(), '120.000 ₫');
    await page.getByRole('link', { name: '← Quay lại tìm kiếm' }).click();
    assert.equal(await page.locator('#loai').inputValue(), 'dichvu');
    await page.locator('#tuKhoa').fill('khong-co');
    await page.locator('#catalog-search button').click();
    assert.equal(await page.locator('#search-empty').isVisible(), true);
    console.log('PASS: initial search, blank rejection, document examples, preserved return results and empty results.');

    await page.goto(base + 'gdChinhNVKho4.jsp');
    await page.locator('#start-purchase').click();
    assert.equal(await page.locator('#search-results').isVisible(), false);
    await page.locator('#tenNCC').fill('Hòa Phát');
    await page.locator('#supplier-search button').click();
    assert.equal(await page.locator('#supplier-table tbody tr:visible').count(), 1);
    await shot(page, '06-chon-nha-cung-cap');
    await page.locator('.select-supplier').click();
    assert.equal(await page.locator('#cart-table tbody tr').count(), 0);
    assert.equal(await page.locator('[data-invoice-total]').textContent(), '0 ₫');
    assert.equal(await page.locator('#confirm-purchase').isDisabled(), true);
    await page.locator('#tenPhuTung').fill('Lọc');
    await page.locator('#part-search button').click();
    assert.equal(await page.locator('#phuTung option').count(), 2);
    await shot(page, '08-chon-phu-tung');
    await page.locator('#phuTung').selectOption('PT1');
    assert.equal(await page.locator('.quantity').inputValue(), '0');
    assert.equal(await page.locator('.price').inputValue(), '0');
    await page.locator('#confirm-purchase').click();
    assert.equal(await page.locator('#cart-error').isVisible(), true);
    await page.locator('.quantity').fill('1.5');
    await page.locator('.price').fill('80000');
    await page.locator('#confirm-purchase').click();
    assert.equal(await page.locator('#cart-error').isVisible(), true);
    await page.locator('.quantity').fill('10');
    await page.locator('#phuTung').selectOption('PT1');
    assert.equal(await page.locator('#cart-table tbody tr').count(), 1);
    assert.equal(await page.locator('.quantity').evaluate(input => input === document.activeElement), true);
    assert.equal(await page.locator('[data-invoice-total]').textContent(), '800.000 ₫');
    await page.reload();
    assert.equal(await page.locator('.quantity').inputValue(), '10');
    await shot(page, '10-lap-hoa-don-nhap');
    await page.locator('#confirm-purchase').click();
    await page.waitForURL('**/gdXacNhanHoaDon4.jsp');
    assert.equal(await page.locator('[data-invoice-lines] tr').count(), 1);
    assert.equal(await page.locator('[data-supplier-address], [data-supplier-phone], [data-supplier-email]').count(), 0);
    assert.equal(await page.locator('[data-invoice-lines] tr').first().locator('td').count(), 6);
    assert.equal(await page.locator('[data-invoice-total]').textContent(), '800.000 ₫');
    assert.equal(await page.locator('#soTienDaTra').inputValue(), '0');
    await shot(page, '11-xac-nhan-hoa-don');
    console.log('PASS: inline dropdown, zero initial line, integer units, duplicate focus, exact 10 x 80,000 scenario.');

    await page.locator('#soTienDaTra').fill('799999');
    await page.locator('#pay-invoice').click();
    assert.equal(await page.locator('#payment-error').isVisible(), true);
    assert.equal(await page.locator('[data-payment-badge]').first().textContent(), 'Chưa thanh toán');
    await page.locator('#open-print').click();
    assert.equal(await page.locator('[data-payment-status]').textContent(), 'Chưa thanh toán');
    await page.evaluate(() => { window.print = () => window.dispatchEvent(new Event('afterprint')); });
    await page.locator('#print-invoice').click();
    await page.waitForURL('**/gdXacNhanHoaDon4.jsp');
    await page.locator('#soTienDaTra').fill('800001');
    await page.locator('#pay-invoice').click();
    assert.equal(await page.locator('#payment-error').isVisible(), true);
    await page.locator('#soTienDaTra').fill('800000');
    await page.locator('#pay-invoice').click();
    assert.equal(await page.locator('[data-payment-badge]').first().textContent(), 'Đã thanh toán');
    assert.equal(await page.locator('#pay-invoice').isDisabled(), true);
    const paymentTime = await page.locator('[data-payment-time]').textContent();
    await shot(page, '17-da-thanh-toan');
    await page.reload();
    assert.equal(await page.locator('[data-payment-time]').textContent(), paymentTime);
    await page.goto(base + 'gdNhapPhuTung4.jsp');
    await page.waitForURL('**/gdXacNhanHoaDon4.jsp');
    assert.equal(await page.locator('[data-invoice-total]').textContent(), '800.000 ₫');
    await page.goto(base + 'gdChinhNVKho4.jsp');
    assert.equal(await page.locator('#resume-invoice').isVisible(), true);
    await page.locator('#resume-invoice').click();
    assert.equal(await page.locator('[data-payment-badge]').first().textContent(), 'Đã thanh toán');
    console.log('PASS: separate print page and return, exact full payment, persistent status, confirmed invoice stays read-only.');

    await page.goto(base + 'gdChinhNVKho4.jsp');
    await page.locator('#start-purchase').click();
    await page.getByRole('link', { name: '+ Thêm nhà cung cấp' }).click();
    await page.locator('#ten').fill('Phụ tùng Bình Minh');
    await page.locator('#diaChi').fill('82 Trần Phú, Hà Đông, Hà Nội');
    await page.locator('#dienThoai').fill('024 3382 6719');
    await page.locator('#supplier-form button[type=submit]').click();
    await page.waitForURL('**/gdChonNhaCungCap4.jsp');
    assert.equal(await page.locator('#supplier-table tbody tr:visible').count(), 1);
    await page.locator('.select-supplier:visible').click();
    assert.equal(await page.locator('[data-supplier-name]').textContent(), 'Phụ tùng Bình Minh');
    await page.getByRole('link', { name: '+ Thêm phụ tùng mới' }).first().click();
    await page.locator('#ten').fill('Dung dịch rửa kính');
    await page.locator('#nhom').selectOption('3');
    await page.locator('#donVi').selectOption('Lít');
    assert.equal(await page.locator('#hangSanXuat').getAttribute('required'), '');
    await page.locator('#hangSanXuat').fill('AutoClean');
    assert.equal(await page.locator('#trangThai').inputValue(), 'Chưa công bố');
    assert.equal(await page.locator('#trangThai option').allTextContents().then(values => values.includes('Ngừng kinh doanh')), false);
    await page.locator('#trangThai').selectOption('Đang kinh doanh');
    await page.locator('#part-form button[type=submit]').click();
    assert.equal(await page.locator('#part-form-error').isVisible(), true);
    await page.locator('#trangThai').selectOption('Chưa công bố');
    await page.locator('#part-form button[type=submit]').click();
    await page.waitForURL('**/gdNhapPhuTung4.jsp');
    assert.equal(await page.locator('#cart-table tbody tr').count(), 0);
    const newPartId = await page.locator('#phuTung option').nth(1).getAttribute('value');
    await page.locator('#phuTung').selectOption(newPartId);
    await page.locator('.quantity').fill('1.125');
    await page.locator('.price').fill('45000');
    assert.equal(await page.locator('.quantity').evaluate(input => input.checkValidity()), true);
    assert.equal(await page.locator('[data-invoice-total]').textContent(), '50.625 ₫');
    page.once('dialog', dialog => dialog.dismiss());
    await page.locator('#cancel-purchase').click();
    assert.equal(await page.locator('#cart-table tbody tr').count(), 1);
    page.once('dialog', dialog => dialog.accept());
    await page.locator('#cancel-purchase').click();
    await page.waitForURL('**/gdChinhNVKho4.jsp');
    await page.locator('#start-purchase').click();
    await page.locator('#tenNCC').fill('Bình Minh');
    await page.locator('#supplier-search button').click();
    assert.equal(await page.locator('#supplier-table tbody tr:visible').count(), 1);
    await page.locator('.select-supplier:visible').click();
    await searchPart(page, 'rửa kính', newPartId);
    assert.equal(await page.locator('#cart-table tbody tr').count(), 1);
    assert.equal(await page.locator('.quantity').inputValue(), '0');
    assert.equal(await page.locator('.price').inputValue(), '0');
    await page.locator('.remove-part').click();
    assert.equal(await page.locator('#confirm-purchase').isDisabled(), true);
    console.log('PASS: add-then-select paths, optional publication fields, fractional liters, cancel confirmation, retained new profiles.');
    await context.close();

    const mobile = await browser.newContext({ viewport: { width: 390, height: 844 }, locale: 'vi-VN', isMobile: true });
    const mobilePage = await mobile.newPage(); watch(mobilePage);
    await mobilePage.goto(base);
    for (const [url] of screens) {
      await mobilePage.evaluate(() => sessionStorage.clear());
      await mobilePage.goto(base + url, { waitUntil: 'networkidle' });
      assert.equal(await mobilePage.evaluate(() => document.documentElement.scrollWidth <= innerWidth), true, `${url}: mobile overflow`);
    }
    await mobilePage.evaluate(() => sessionStorage.clear());
    await mobilePage.goto(base + 'gdTimDichVuPhuTung4.jsp');
    await mobilePage.locator('#tuKhoa').fill('lọc');
    await mobilePage.locator('#catalog-search button').click();
    await shot(mobilePage, '13-tra-cuu-dien-thoai');
    await mobile.close();
    assert.deepEqual(errors, [], 'Browser or HTTP errors');
    console.log('PASS: mobile layouts and no JavaScript/HTTP errors.');
  } finally { await browser.close(); }
})().catch(error => { console.error(error); process.exit(1); });
