/**
 * purchase.js - Hỗ trợ tương tác giao diện lập hóa đơn nhập
 * Kết quả tính phía server mới là căn cứ lưu chính thức.
 */

function formatVND(amount) {
    return new Intl.NumberFormat('vi-VN').format(amount) + ' đ';
}

function tinhLaiTienDuKien() {
    var rows = document.querySelectorAll('#cart-table tbody tr');
    var tongTien = 0;

    rows.forEach(function(row) {
        var qtyInput = row.querySelector('.qty-input');
        var priceInput = row.querySelector('.price-input');
        var lineTotalElem = row.querySelector('.line-total');

        if (qtyInput && priceInput && lineTotalElem) {
            var qty = parseFloat(qtyInput.value) || 0;
            var price = parseFloat(priceInput.value) || 0;
            var thanhTien = Math.round(qty * price * 100) / 100;
            lineTotalElem.textContent = formatVND(thanhTien);
            tongTien += thanhTien;
        }
    });

    var grandTotalElem = document.getElementById('grand-total');
    if (grandTotalElem) {
        grandTotalElem.textContent = formatVND(tongTien);
    }
}

function focusDongDaCo(partId) {
    if (!partId) return;
    var row = document.getElementById('row-part-' + partId);
    if (row) {
        row.scrollIntoView({ behavior: 'smooth', block: 'center' });
        var input = row.querySelector('.qty-input') || row.querySelector('input');
        if (input) {
            input.focus();
            if (typeof input.select === 'function') {
                input.select();
            }
        }
    }
}

function xacNhanHuy() {
    return confirm('Bạn có chắc chắn muốn hủy bản nháp này? Mọi thông tin các dòng chưa xác nhận sẽ bị xóa.');
}

document.addEventListener('DOMContentLoaded', function() {
    var inputs = document.querySelectorAll('#cart-table .table-input');
    inputs.forEach(function(input) {
        input.addEventListener('input', tinhLaiTienDuKien);
    });

    var focusPartIdElem = document.getElementById('focusPartId');
    if (focusPartIdElem && focusPartIdElem.value) {
        focusDongDaCo(focusPartIdElem.value);
    }
});
