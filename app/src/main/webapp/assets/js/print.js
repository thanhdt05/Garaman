/**
 * print.js - Xử lý in và quay lại cho trang in hóa đơn nhập
 */

function inHoaDon() {
    window.print();
}

function quayLaiHoaDon(url) {
    if (url) {
        window.location.href = url;
    } else {
        window.history.back();
    }
}
