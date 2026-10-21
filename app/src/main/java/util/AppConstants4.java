package util;

import java.time.ZoneId;

public final class AppConstants4 {

    private AppConstants4() {
        // Private constructor
    }

    public static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    // Vai trò thành viên (tblMember4.role)
    public static final String ROLE_CUSTOMER = "Customer";
    public static final String ROLE_WAREHOUSE_STAFF = "WarehouseStaff";
    public static final String ROLE_SALES_STAFF = "SalesStaff";
    public static final String ROLE_MANAGER = "Manager";

    // Trạng thái tài khoản (tblMember4.accountStatus)
    public static final String ACCOUNT_STATUS_ACTIVE = "Hoạt động";
    public static final String ACCOUNT_STATUS_LOCKED = "Đã khóa";

    // Vị trí làm việc (tblEmployee4.position)
    public static final String POSITION_MANAGER = "Quản lí";
    public static final String POSITION_WAREHOUSE = "Kho";
    public static final String POSITION_SALES = "Bán hàng";

    // Trạng thái làm việc (tblEmployee4.employmentStatus)
    public static final String EMPLOYMENT_STATUS_WORKING = "Đang làm việc";
    public static final String EMPLOYMENT_STATUS_ACTIVE = "Đang làm việc";
    public static final String EMPLOYMENT_STATUS_INACTIVE = "Ngừng làm việc";

    // Đơn vị phụ tùng (tblSparePart4.unit)
    public static final String UNIT_PIECE = "Cái";
    public static final String UNIT_ITEM = "Chiếc";
    public static final String UNIT_SET = "Bộ";
    public static final String UNIT_LITER = "Lít";

    // Trạng thái phụ tùng (tblSparePart4.status)
    public static final String PART_STATUS_UNPUBLISHED = "Chưa công bố";
    public static final String PART_STATUS_ACTIVE = "Đang kinh doanh";
    public static final String PART_STATUS_DISCONTINUED = "Ngừng kinh doanh";

    // Trạng thái nhóm phụ tùng (tblSparePartCategory4.status)
    public static final String CATEGORY_STATUS_ACTIVE = "Đang sử dụng";
    public static final String CATEGORY_STATUS_INACTIVE = "Ngừng sử dụng";

    // Trạng thái nhà cung cấp (tblSupplier4.status)
    public static final String SUPPLIER_STATUS_ACTIVE = "Đang hoạt động";
    public static final String SUPPLIER_STATUS_INACTIVE = "Ngừng hoạt động";

    // Trạng thái dịch vụ (tblService4.status)
    public static final String SERVICE_STATUS_ACTIVE = "Đang cung cấp";
    public static final String SERVICE_STATUS_DISCONTINUED = "Ngừng cung cấp";

    // Tình trạng hóa đơn (tblPurchaseInvoice4 / tblCustomerInvoice4.paymentStatus)
    public static final String INVOICE_STATUS_UNPAID = "Chưa thanh toán";
    public static final String INVOICE_STATUS_PAID = "Đã thanh toán";

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
