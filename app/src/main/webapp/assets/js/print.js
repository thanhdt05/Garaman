/**
 * print.js - Xử lý in và quay lại cho trang in hóa đơn nhập
 */

function printInvoice() {
    window.print();
}

function returnToInvoice(url) {
    if (url) {
        window.location.href = url;
    } else {
        window.history.back();
    }
}

window.addEventListener('afterprint', function() {
    var returnLink = document.querySelector('.invoice-actions a');
    if (returnLink) returnToInvoice(returnLink.href);
});
