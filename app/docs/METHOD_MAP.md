# Danh sách hàm trong mã nguồn hiện tại

Tự đối chiếu từ khai báo Java sau đổi tên tiếng Anh; xem thêm `CODE_ENGLISH_MIGRATION.md` và `CODE_ENGLISH_RENAME.csv`.
Tên lớp giữ hậu tố `4`; getter/setter theo JavaBeans tương ứng với EL trong JSP. Các hàm hỗ trợ được giữ nguyên hành vi.

## AuthServlet4

```java
public void init() throws ServletException
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void showLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
```

## CatalogServlet4

```java
public void init() throws ServletException
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void showSearch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void searchCatalog(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void viewDetails(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
```

## HomeServlet4

```java
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void showHome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
```

## PurchaseInvoiceServlet4

```java
public void init() throws ServletException
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void startPurchase(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void showDraft(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void searchSpareParts(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void addInvoiceLine(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void updateInvoiceLines(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void removeInvoiceLine(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void cancelDraft(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void confirmInvoice(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void viewInvoice(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void printInvoice(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void recordPayment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void showUnpaidInvoices(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
private boolean validateVersionAndConcurrency(HttpServletRequest req, HttpSession session, String requestCode, HttpServletResponse res) throws IOException
private void saveLineInputsToDraft(HttpServletRequest req, HttpSession session, String requestCode)
```

## SparePartServlet4

```java
public void init() throws ServletException
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void showNewSparePart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void addSparePart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
```

## SupplierServlet4

```java
public void init() throws ServletException
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void showSupplierSelection(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void searchSuppliers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void showNewSupplier(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
public void addSupplier(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
```

## DAO

```java
private static synchronized void loadConfiguration()
protected Connection getConnection() throws SQLException
protected String createSearchPattern(String keyword)
```

## MemberDAO4

```java
public Member4 findByUsername(String username) throws SQLException
public Member4 getActiveMember(int id) throws SQLException
private Member4 mapMember(ResultSet rs) throws SQLException
```

## PurchaseInvoiceDAO4

```java
public Integer findInvoiceIdByRequestCode(String requestCode, int warehouseStaffId) throws SQLException
public int savePurchaseInvoice(PurchaseInvoice4 purchaseInvoice) throws SQLException, ValidationException4
public PurchaseInvoice4 getPurchaseInvoiceDetails(int invoiceId) throws SQLException
public List<PurchaseInvoice4> getUnpaidInvoices(int warehouseStaffId) throws SQLException
public boolean recordPayment(int invoiceId, BigDecimal amountPaid, int currentWarehouseStaffId) throws SQLException, ValidationException4
private int insertInvoice(Connection connection, PurchaseInvoice4 purchaseInvoice) throws SQLException
private void insertInvoiceDetails(Connection connection, int invoiceId, List<PurchaseInvoiceDetail4> invoiceDetails) throws SQLException
private List<PurchaseInvoiceDetail4> getInvoiceDetails(Connection connection, int invoiceId) throws SQLException
private PurchaseInvoice4 mapInvoice(ResultSet rs) throws SQLException
```

## ServiceDAO4

```java
public List<Service4> findServicesByName(String keyword) throws SQLException
public Service4 getServiceDetails(int serviceId) throws SQLException
private Service4 mapService(ResultSet rs) throws SQLException
```

## SparePartCategoryDAO4

```java
public List<SparePartCategory4> getActiveCategories() throws SQLException
public SparePartCategory4 getActiveCategory(int id) throws SQLException
private SparePartCategory4 mapCategory(ResultSet rs) throws SQLException
```

## SparePartDAO4

```java
public List<SparePart4> findSparePartsByName(String keyword, boolean publishedOnly) throws SQLException
public SparePart4 getSparePartDetails(int sparePartId, boolean publishedOnly) throws SQLException
public BigDecimal getStockQuantity(int sparePartId) throws SQLException
void increaseStockAfterPurchase(Connection connection, int sparePartId, BigDecimal quantity) throws SQLException, ValidationException4
public int addSparePart(SparePart4 sparePart) throws SQLException, ValidationException4
private SparePart4 mapSparePart(ResultSet rs) throws SQLException
```

## SupplierDAO4

```java
public List<Supplier4> findSuppliersByName(String keyword) throws SQLException
public Supplier4 getSupplierDetails(int supplierId) throws SQLException
public int addSupplier(Supplier4 supplier) throws SQLException, ValidationException4
private Supplier4 mapSupplier(ResultSet rs) throws SQLException
```

## WarehouseStaffDAO4

```java
public WarehouseStaff4 getWarehouseStaffDetails(int warehouseStaffId) throws SQLException
```

## SessionUser4

```java
public SessionUser4(int id, String fullName, String role, String position)
public static SessionUser4 fromMember(Member4 member)
public int getId()
public String getFullName()
public String getRole()
public String getPosition()
```

## ValidationException4

```java
public ValidationException4(String message)
public ValidationException4(String field, String message)
public String getField()
```

## AuthFilter4

```java
public void init(FilterConfig filterConfig) throws ServletException
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
public void destroy()
```

## EncodingFilter4

```java
public void init(FilterConfig filterConfig) throws ServletException
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
public void destroy()
```

## Customer4

```java
public Customer4()
```

## Employee4

```java
public Employee4()
public String getPosition()
public void setPosition(String position)
public String getEmploymentStatus()
public void setEmploymentStatus(String employmentStatus)
```

## Member4

```java
public Member4()
public int getId()
public void setId(int id)
public String getFullName()
public void setFullName(String fullName)
public String getUsername()
public void setUsername(String username)
public String getPassword()
public void setPassword(String password)
public LocalDate getDateOfBirth()
public void setDateOfBirth(LocalDate dateOfBirth)
public String getAddress()
public void setAddress(String address)
public String getEmail()
public void setEmail(String email)
public String getPhoneNumber()
public void setPhoneNumber(String phoneNumber)
public String getRole()
public void setRole(String role)
public String getAccountStatus()
public void setAccountStatus(String accountStatus)
```

## PurchaseInvoice4

```java
public PurchaseInvoice4()
public BigDecimal calculateTotalAmount()
public BigDecimal getTotalAmount()
public int getId()
public void setId(int id)
public String getRequestCode()
public void setRequestCode(String requestCode)
public LocalDateTime getPurchaseDate()
public void setPurchaseDate(LocalDateTime purchaseDate)
public LocalDateTime getConfirmedAt()
public void setConfirmedAt(LocalDateTime confirmedAt)
public String getPaymentStatus()
public void setPaymentStatus(String paymentStatus)
public LocalDateTime getPaidAt()
public void setPaidAt(LocalDateTime paidAt)
public Supplier4 getSupplier()
public void setSupplier(Supplier4 supplier)
public WarehouseStaff4 getWarehouseStaff()
public void setWarehouseStaff(WarehouseStaff4 warehouseStaff)
public List<PurchaseInvoiceDetail4> getInvoiceDetails()
public void setInvoiceDetails(List<PurchaseInvoiceDetail4> invoiceDetails)
public String getSupplierNameSnapshot()
public void setSupplierNameSnapshot(String supplierNameSnapshot)
public String getWarehouseStaffNameSnapshot()
public void setWarehouseStaffNameSnapshot(String warehouseStaffNameSnapshot)
```

## PurchaseInvoiceDetail4

```java
public PurchaseInvoiceDetail4()
public BigDecimal calculateLineAmount()
public BigDecimal getLineAmount()
public int getId()
public void setId(int id)
public BigDecimal getQuantity()
public void setQuantity(BigDecimal quantity)
public BigDecimal getPurchaseUnitPrice()
public void setPurchaseUnitPrice(BigDecimal purchaseUnitPrice)
public String getSparePartNameSnapshot()
public void setSparePartNameSnapshot(String sparePartNameSnapshot)
public String getUnitSnapshot()
public void setUnitSnapshot(String unitSnapshot)
public SparePart4 getSparePart()
public void setSparePart(SparePart4 sparePart)
```

## Service4

```java
public Service4()
public int getId()
public void setId(int id)
public String getName()
public void setName(String name)
public String getCategory()
public void setCategory(String category)
public String getDescription()
public void setDescription(String description)
public String getContent()
public void setContent(String content)
public String getImagePath()
public void setImagePath(String imagePath)
public BigDecimal getUnitPrice()
public void setUnitPrice(BigDecimal unitPrice)
public int getEstimatedDuration()
public void setEstimatedDuration(int estimatedDuration)
public String getWarrantyPeriod()
public void setWarrantyPeriod(String warrantyPeriod)
public String getWarrantyTerms()
public void setWarrantyTerms(String warrantyTerms)
public String getStatus()
public void setStatus(String status)
```

## SparePart4

```java
public SparePart4()
public int getId()
public void setId(int id)
public String getName()
public void setName(String name)
public String getDescription()
public void setDescription(String description)
public String getImagePath()
public void setImagePath(String imagePath)
public String getUnit()
public void setUnit(String unit)
public String getManufacturer()
public void setManufacturer(String manufacturer)
public String getSpecifications()
public void setSpecifications(String specifications)
public String getCompatibleVehicleModels()
public void setCompatibleVehicleModels(String compatibleVehicleModels)
public BigDecimal getSellingPrice()
public void setSellingPrice(BigDecimal sellingPrice)
public String getWarrantyPeriod()
public void setWarrantyPeriod(String warrantyPeriod)
public String getWarrantyTerms()
public void setWarrantyTerms(String warrantyTerms)
public String getStatus()
public void setStatus(String status)
public BigDecimal getStockQuantity()
public void setStockQuantity(BigDecimal stockQuantity)
public SparePartCategory4 getCategory()
public void setCategory(SparePartCategory4 category)
```

## SparePartCategory4

```java
public SparePartCategory4()
public int getId()
public void setId(int id)
public String getName()
public void setName(String name)
public String getDescription()
public void setDescription(String description)
public String getStatus()
public void setStatus(String status)
```

## Supplier4

```java
public Supplier4()
public int getId()
public void setId(int id)
public String getName()
public void setName(String name)
public String getAddress()
public void setAddress(String address)
public String getPhoneNumber()
public void setPhoneNumber(String phoneNumber)
public String getEmail()
public void setEmail(String email)
public String getStatus()
public void setStatus(String status)
```

## WarehouseStaff4

```java
public WarehouseStaff4()
```

## AppConstants4

```java
private AppConstants4()
```

## DraftStore4

```java
private DraftStore4()
private static void validateRequestCode(String requestCode) throws ValidationException4
private static Map<String, PurchaseInvoice4> getDraftMap(HttpSession session)
private static Map<String, Long> getVersionMap(HttpSession session)
private static Set<String> getConfirmingSet(HttpSession session)
private static Map<String, Map<String, String>> getFormValuesMap(HttpSession session)
public static void validateDraftLimit(HttpSession session) throws ValidationException4
public static PurchaseInvoice4 createDraft(HttpSession session, Supplier4 supplier, WarehouseStaff4 staff) throws ValidationException4
public static PurchaseInvoice4 getDraft(HttpSession session, String requestCode) throws ValidationException4
public static void saveDraft(HttpSession session, PurchaseInvoice4 draft) throws ValidationException4
public static void removeDraft(HttpSession session, String requestCode) throws ValidationException4
public static long getDraftVersion(HttpSession session, String requestCode) throws ValidationException4
public static void validateDraftVersion(HttpSession session, String requestCode, long version) throws ValidationException4
public static long incrementDraftVersion(HttpSession session, String requestCode) throws ValidationException4
public static PurchaseInvoice4 beginConfirmation(HttpSession session, String requestCode) throws ValidationException4
public static PurchaseInvoice4 beginConfirmation(HttpSession session, String requestCode, long version) throws ValidationException4
public static void endConfirmation(HttpSession session, String requestCode)
public static Map<String, String> getFormValue(HttpSession session, String requestCode) throws ValidationException4
public static Map<String, String> getFormValues(HttpSession session, String requestCode)
public static void saveFormValue(HttpSession session, String requestCode, Map<String, String> values) throws ValidationException4
public static void updateFormValues(HttpSession session, String requestCode, Map<String, String> values) throws ValidationException4
public static void clearSparePartErrors(HttpSession session, String requestCode, int partId) throws ValidationException4
public static PurchaseInvoice4 copyDraft(PurchaseInvoice4 draft)
```

## PasswordUtil4

```java
private PasswordUtil4()
public static String hashPassword(char[] password)
public static boolean validatePassword(String password, String storedHash)
public static boolean verifyPassword(String password, String storedHash)
public static boolean validatePassword(char[] password, String storedHash)
public static void main(String[] args)
```

## Validation4

```java
private Validation4()
public static String requireText(String value, String field, int maxLength) throws ValidationException4
public static String optionalText(String value, int maxLength) throws ValidationException4
public static int positiveId(String value, String field) throws ValidationException4
public static BigDecimal parseDecimal(String value, String field, int precision, int scale, boolean allowZero) throws ValidationException4
public static void validateQuantity(BigDecimal qty, String unit) throws ValidationException4
public static void validateEmail(String email) throws ValidationException4
public static void validatePhoneNumber(String phone) throws ValidationException4
public static void validateSupplier(Supplier4 supplier) throws ValidationException4
public static void validateSparePart(SparePart4 sparePart) throws ValidationException4
public static void validatePurchaseInvoice(PurchaseInvoice4 purchaseInvoice) throws ValidationException4
```

## WebUtil4

```java
private WebUtil4()
public static void forward(HttpServletRequest req, HttpServletResponse res, String view) throws ServletException, IOException
public static void redirect(HttpServletRequest req, HttpServletResponse res, String path) throws IOException
public static SessionUser4 requireUser(HttpServletRequest req) throws ValidationException4
public static String createCsrfToken(HttpSession session)
public static boolean isValidCsrfToken(HttpServletRequest req)
public static void flash(HttpServletRequest req, String message)
public static void setFlash(HttpServletRequest req, String message)
public static String getFlash(HttpServletRequest req)
public static String formatDateTime(LocalDateTime value)
```
