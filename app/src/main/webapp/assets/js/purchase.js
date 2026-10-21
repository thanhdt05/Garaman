/**
 * purchase.js - Hỗ trợ tương tác giao diện lập hóa đơn nhập
 * Kết quả tính phía server mới là căn cứ lưu chính thức.
 */

function formatVND(amount) {
    return new Intl.NumberFormat('vi-VN', { maximumFractionDigits: 2 }).format(amount) + ' ₫';
}

function recalculateEstimatedTotal() {
    var rows = document.querySelectorAll('#cart-table tbody tr');
    var totalAmount = 0;

    rows.forEach(function(row) {
        var qtyInput = row.querySelector('.qty-input');
        var priceInput = row.querySelector('.price-input');
        var lineTotalElem = row.querySelector('.line-total');

        if (qtyInput && priceInput && lineTotalElem) {
            var qty = parseFloat(qtyInput.value) || 0;
            var price = parseFloat(priceInput.value) || 0;
            var lineAmount = Math.round(qty * price * 100) / 100;
            lineTotalElem.textContent = formatVND(lineAmount);
            totalAmount += lineAmount;
        }
    });

    var grandTotalElem = document.getElementById('grand-total');
    if (grandTotalElem) {
        grandTotalElem.textContent = formatVND(totalAmount);
    }
}

function focusExistingLine(partId) {
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

document.addEventListener('DOMContentLoaded', function() {
    // Lắng nghe thay đổi trên các ô nhập số lượng và đơn giá
    var inputs = document.querySelectorAll('#cart-table .qty-input, #cart-table .price-input');
    inputs.forEach(function(input) {
        input.addEventListener('input', recalculateEstimatedTotal);
    });

    // Tự động focus dòng nếu có focusPartId
    var focusPartIdElem = document.getElementById('focusPartId');
    if (focusPartIdElem && focusPartIdElem.value) {
        focusExistingLine(focusPartIdElem.value);
    }
});
