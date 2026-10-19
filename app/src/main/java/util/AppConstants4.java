package util;

import java.time.ZoneId;

public final class AppConstants4 {

    private AppConstants4() {
        // Private constructor
    }

    public static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    // Vai trò thành viên (tblMember4.vaiTro)
    public static final String ROLE_CUSTOMER = "Customer";
    public static final String ROLE_WAREHOUSE_STAFF = "WarehouseStaff";
    public static final String ROLE_SALES_STAFF = "SalesStaff";
    public static final String ROLE_MANAGER = "Manager";

    // Trạng thái tài khoản (tblMember4.trangThaiTaiKhoan)
    public static final String TRANG_THAI_TK_HOAT_DONG = "Hoạt động";
    public static final String TRANG_THAI_TK_DA_KHOA = "Đã khóa";

    // Vị trí làm việc (tblEmployee4.viTri)
    public static final String VI_TRI_QUAN_LY = "Quản lí";
    public static final String VI_TRI_KHO = "Kho";
    public static final String VI_TRI_BAN_HANG = "Bán hàng";

    // Trạng thái làm việc (tblEmployee4.trangThaiLamViec)
    public static final String TRANG_THAI_LAM_VIEC = "Đang làm việc";
    public static final String TRANG_THAI_LV_DANG_LAM = "Đang làm việc";
    public static final String TRANG_THAI_NGUNG_LAM_VIEC = "Ngừng làm việc";

    // Đơn vị phụ tùng (tblSparePart4.donVi)
    public static final String DON_VI_CAI = "Cái";
    public static final String DON_VI_CHIEC = "Chiếc";
    public static final String DON_VI_BO = "Bộ";
    public static final String DON_VI_LIT = "Lít";

    // Trạng thái phụ tùng (tblSparePart4.trangThai)
    public static final String TRANG_THAI_PT_CHUA_CONG_BO = "Chưa công bố";
    public static final String TRANG_THAI_PT_DANG_KINH_DOANH = "Đang kinh doanh";
    public static final String TRANG_THAI_PT_NGUNG_KINH_DOANH = "Ngừng kinh doanh";

    // Trạng thái nhóm phụ tùng (tblSparePartCategory4.trangThai)
    public static final String TRANG_THAI_NHOM_DANG_SU_DUNG = "Đang sử dụng";
    public static final String TRANG_THAI_NHOM_NGUNG_SU_DUNG = "Ngừng sử dụng";

    // Trạng thái nhà cung cấp (tblSupplier4.trangThai)
    public static final String TRANG_THAI_NCC_DANG_HOAT_DONG = "Đang hoạt động";
    public static final String TRANG_THAI_NCC_NGUNG_HOAT_DONG = "Ngừng hoạt động";

    // Trạng thái dịch vụ (tblService4.trangThai)
    public static final String TRANG_THAI_DV_DANG_CUNG_CAP = "Đang cung cấp";
    public static final String TRANG_THAI_DV_NGUNG_CUNG_CAP = "Ngừng cung cấp";

    // Tình trạng hóa đơn (tblPurchaseInvoice4 / tblCustomerInvoice4.tinhTrang)
    public static final String TINH_TRANG_HD_CHUA_THANH_TOAN = "Chưa thanh toán";
    public static final String TINH_TRANG_HD_DA_THANH_TOAN = "Đã thanh toán";

    // Session Attribute Keys
    public static final String SESSION_USER = "currentUser4";
    public static final String SESSION_CSRF = "csrfToken4";
    public static final String SESSION_PURCHASE_DRAFTS = "purchaseDrafts4";
    public static final String SESSION_DRAFT_FORM_VALUES = "draftFormValues4";
    public static final String SESSION_DRAFT_VERSIONS = "draftVersions4";
    public static final String SESSION_DRAFT_CONFIRMING = "draftConfirming4";
    public static final String SESSION_FLASH = "flashMessage4";

    // Request Attribute Keys
    public static final String ATTR_CURRENT_USER = "currentUser";
    public static final String ATTR_CSRF_TOKEN = "csrfToken";
    public static final String ATTR_ERROR_MESSAGE = "errorMessage";
    public static final String ATTR_FIELD_ERRORS = "fieldErrors";
    public static final String ATTR_FLASH_MESSAGE = "flashMessage";

    // Allowlist ảnh SVG
    public static final String ASSET_IMG_PART = "assets/images/part.svg";
    public static final String ASSET_IMG_BRAKE = "assets/images/brake.svg";
    public static final String ASSET_IMG_PLACEHOLDER = "assets/images/placeholder.svg";
}
