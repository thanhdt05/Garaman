package controller;

import dao.PurchaseInvoiceDAO4;
import dao.SparePartDAO4;
import dao.SupplierDAO4;
import dao.WarehouseStaffDAO4;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.servlet.http.*;
import javax.servlet.RequestDispatcher;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

/** Controller regression tests with session/request doubles; no MySQL or JSP runtime. */
class PurchaseInvoiceServlet4Test {
    private HttpSession session;
    private PurchaseInvoice4 draft;
    private PurchaseInvoiceServlet4 servlet;
    private final AtomicInteger saved = new AtomicInteger();
    private PurchaseInvoice4 committed;
    private final Map<String,Object> requestAttributes = new HashMap<>();
    private String forwardedTo;
    private String redirectedTo;

    @BeforeEach void setup() throws Exception {
        Map<String,Object> values = new HashMap<>();
        session = (HttpSession) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{HttpSession.class}, (p,m,a) -> {
            switch (m.getName()) {
                case "getAttribute": return values.get(a[0]);
                case "setAttribute": values.put((String)a[0], a[1]); return null;
                case "removeAttribute": values.remove(a[0]); return null;
                default: return defaultValue(m.getReturnType());
            }
        });
        WarehouseStaff4 user = new WarehouseStaff4(); user.setId(2); user.setFullName("Kho"); user.setRole("WarehouseStaff");
        session.setAttribute("currentUser4", user);
        Supplier4 supplier = new Supplier4(); supplier.setId(1);
        WarehouseStaff4 staff = new WarehouseStaff4(); staff.setId(2);
        draft = new PurchaseInvoice4(); draft.setRequestCode(UUID.randomUUID().toString());
        draft.setSupplier(supplier); draft.setWarehouseStaff(staff); draft.setInvoiceDetails(new ArrayList<>());
        draft.setPaymentStatus("Chưa thanh toán");
        session.setAttribute("purchaseDrafts4", new HashMap<>(Map.of(draft.getRequestCode(), draft)));
        session.setAttribute("draftVersions4", new HashMap<>(Map.of(draft.getRequestCode(), 0L)));
        session.setAttribute("draftFormValues4", new HashMap<String, Map<String,String>>());
        session.setAttribute("draftConfirming4", new HashSet<String>());
        servlet = new PurchaseInvoiceServlet4();
        inject("sparePartDAO", new SparePartDAO4() {
            @Override public SparePart4 getSparePartDetails(int id, boolean published) {
                assertFalse(Thread.holdsLock(session), "JDBC lookup must stay outside session lock");
                SparePart4 sparePart = new SparePart4(); sparePart.setId(id); sparePart.setName("Lọc dầu");
                sparePart.setUnit("Cái"); sparePart.setSellingPrice(new BigDecimal("150000")); return sparePart;
            }
        });
        inject("purchaseInvoiceDAO", new PurchaseInvoiceDAO4() {
            @Override public Integer findInvoiceIdByRequestCode(String uuid, int author) { return saved.get() == 0 ? null : 42; }
            @Override public int savePurchaseInvoice(PurchaseInvoice4 invoice) {
                assertFalse(Thread.holdsLock(session), "Transaction must stay outside session lock");
                committed = invoice; saved.incrementAndGet(); return 42;
            }
        });
    }

    private void inject(String name, Object value) throws Exception {
        Field field = PurchaseInvoiceServlet4.class.getDeclaredField(name); field.setAccessible(true); field.set(servlet,value);
    }
    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        return null;
    }
    private Map<String,String[]> params() throws Exception {
        Map<String,String[]> params = new HashMap<>();
        params.put("requestCode", new String[]{draft.getRequestCode()});
        params.put("draftVersion", new String[]{String.valueOf(((Map<String,Long>) session.getAttribute("draftVersions4")).get(draft.getRequestCode()))});
        params.put("selectedPartId", new String[]{"1"});
        params.put("partId", new String[]{"1"});
        params.put("quantity_1", new String[]{"10"});
        params.put("purchaseUnitPrice_1", new String[]{"80000"});
        return params;
    }
    private HttpServletRequest request(Map<String,String[]> params) {
        return (HttpServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{HttpServletRequest.class}, (p,m,a) -> {
            switch (m.getName()) {
                case "getParameter": String[] v=params.get(a[0]); return v==null?null:v[0];
                case "getParameterValues": return params.get(a[0]);
                case "getSession": return session;
                case "getContextPath": return "";
                case "setAttribute": requestAttributes.put((String)a[0],a[1]); return null;
                case "getAttribute": return requestAttributes.get(a[0]);
                case "getRequestDispatcher":
                    String path = (String)a[0];
                    return (RequestDispatcher) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{RequestDispatcher.class}, (dp,dm,da) -> {
                        if (dm.getName().equals("forward")) forwardedTo=path;
                        return null;
                    });
                default: return defaultValue(m.getReturnType());
            }
        });
    }
    private HttpServletResponse response(AtomicInteger status) {
        return (HttpServletResponse) Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{HttpServletResponse.class},(p,m,a)->{
            if (m.getName().equals("sendError")) status.set((Integer)a[0]);
            if (m.getName().equals("sendRedirect")) { status.set(302); redirectedTo=(String)a[0]; }
            return defaultValue(m.getReturnType());
        });
    }
    private void add() throws Exception { servlet.addInvoiceLine(request(params()),response(new AtomicInteger())); }
    private PurchaseInvoice4 current() throws Exception { return ((Map<String,PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).get(draft.getRequestCode()); }

    @Test void addAndUpdatePersistAndKeepSalePrice() throws Exception {
        add(); assertEquals(1,current().getInvoiceDetails().size());
        assertEquals(0,current().getInvoiceDetails().get(0).getQuantity().signum());
        servlet.updateInvoiceLines(request(params()),response(new AtomicInteger()));
        assertEquals(0,new BigDecimal("800000").compareTo(current().getTotalAmount()));
        assertEquals(0,new BigDecimal("150000").compareTo(current().getInvoiceDetails().get(0).getSparePart().getSellingPrice()));
    }
    @Test void duplicateSelectKeepsOneLineAndUpdatedInputs() throws Exception {
        add(); add(); assertEquals(1,current().getInvoiceDetails().size());
        assertEquals(0,new BigDecimal("800000").compareTo(current().getTotalAmount()));
    }
    @Test void searchAndNewPartNavigationPreserveInput() throws Exception {
        add(); servlet.searchSpareParts(request(params()),response(new AtomicInteger()));
        assertEquals(0,new BigDecimal("800000").compareTo(current().getTotalAmount()));
        Map<String,String[]> p=params(); p.put("destination",new String[]{"new-part"}); p.put("quantity_1",new String[]{"12"});
        servlet.updateInvoiceLines(request(p),response(new AtomicInteger()));
        assertEquals(0,new BigDecimal("960000").compareTo(current().getTotalAmount()));
    }
    @Test void removeDropsLineAndItsErrors() throws Exception {
        add(); Map<String,String[]> p=params(); p.put("quantity_1",new String[]{"abc"}); p.put("removePartId",new String[]{"1"});
        servlet.removeInvoiceLine(request(p),response(new AtomicInteger()));
        assertTrue(current().getInvoiceDetails().isEmpty());
        assertTrue(((Map<String,Map<String,String>>) session.getAttribute("draftFormValues4")).getOrDefault(draft.getRequestCode(), Collections.emptyMap()).isEmpty());
    }
    @Test void blankOrInvalidInputCannotConfirmOldPositiveValues() throws Exception {
        add(); servlet.updateInvoiceLines(request(params()),response(new AtomicInteger()));
        Map<String,String[]> p=params(); p.put("quantity_1",new String[]{""}); p.put("purchaseUnitPrice_1",new String[]{"bad"});
        servlet.confirmInvoice(request(p),response(new AtomicInteger()));
        assertEquals(0,saved.get()); assertEquals(0,current().getTotalAmount().signum());
        Map<String,String> raw=((Map<String,Map<String,String>>) session.getAttribute("draftFormValues4")).getOrDefault(draft.getRequestCode(), Collections.emptyMap());
        assertEquals("",raw.get("quantity_1")); assertEquals("bad",raw.get("purchaseUnitPrice_1"));
        assertTrue(raw.containsKey("error_quantity_1")); assertTrue(raw.containsKey("error_purchaseUnitPrice_1"));
        servlet.updateInvoiceLines(request(params()),response(new AtomicInteger()));
        assertEquals(0,new BigDecimal("800000").compareTo(current().getTotalAmount()));
    }
    @Test void missingVersionAndStaleVersionAreRejected() throws Exception {
        Map<String,String[]> stale=params(); add(); AtomicInteger status=new AtomicInteger();
        servlet.updateInvoiceLines(request(stale),response(status)); assertEquals(409,status.get());
        Map<String,String[]> missing=params(); missing.remove("draftVersion");
        servlet.cancelDraft(request(missing),response(status)); assertEquals(400,status.get()); assertNotNull(current());
    }
    @Test void confirmingDraftRejectsCancelAndEdit() throws Exception {
        add(); ((Set<String>) session.getAttribute("draftConfirming4")).add(draft.getRequestCode());
        AtomicInteger status=new AtomicInteger();
        servlet.cancelDraft(request(params()),response(status)); assertEquals(409,status.get());
        servlet.updateInvoiceLines(request(params()),response(status)); assertEquals(409,status.get());
        assertNotNull(current()); ((Set<String>) session.getAttribute("draftConfirming4")).remove(draft.getRequestCode());
    }
    @Test void concurrentUpdatesAcceptOnlyOneSubmittedVersion() throws Exception {
        add(); Map<String,String[]> p=params(); ExecutorService pool=Executors.newFixedThreadPool(2);
        try {
            CountDownLatch ready=new CountDownLatch(2), go=new CountDownLatch(1);
            Callable<Integer> task=()->{ready.countDown(); go.await(); AtomicInteger status=new AtomicInteger(); servlet.updateInvoiceLines(request(p),response(status)); return status.get();};
            Future<Integer> first=pool.submit(task),second=pool.submit(task);
            assertTrue(ready.await(3,TimeUnit.SECONDS)); go.countDown();
            Set<Integer> statuses=new HashSet<>(Arrays.asList(first.get(3,TimeUnit.SECONDS),second.get(3,TimeUnit.SECONDS)));
            assertEquals(new HashSet<>(Arrays.asList(302,409)),statuses);
        } finally { pool.shutdownNow(); }
    }
    @Test void confirmationUsesUpdatedSnapshotAndRetryDoesNotSaveTwice() throws Exception {
        add(); Map<String,String[]> p=params();
        servlet.confirmInvoice(request(p),response(new AtomicInteger()));
        assertEquals(1,saved.get()); assertEquals(0,new BigDecimal("800000").compareTo(committed.getTotalAmount()));
        servlet.confirmInvoice(request(p),response(new AtomicInteger())); assertEquals(1,saved.get());
    }
    @Test void failedTransactionReleasesConfirmationForRetry() throws Exception {
        add(); inject("purchaseInvoiceDAO",new PurchaseInvoiceDAO4(){
            @Override public Integer findInvoiceIdByRequestCode(String uuid,int author){return null;}
            @Override public int savePurchaseInvoice(PurchaseInvoice4 invoice) throws SQLException {throw new SQLException("test failure");}
        });
        servlet.confirmInvoice(request(params()),response(new AtomicInteger()));
        AtomicInteger status=new AtomicInteger(); servlet.updateInvoiceLines(request(params()),response(status));
        assertEquals(302,status.get()); assertNotNull(current());
    }

    @Test void startLoadsSupplierThenWarehouseStaffBeforeCreatingDraft() throws Exception {
        List<String> calls = new ArrayList<>();
        inject("supplierDAO",new SupplierDAO4(){
            @Override public Supplier4 getSupplierDetails(int id) {
                calls.add("supplier:"+id);
                Supplier4 supplier=new Supplier4(); supplier.setId(id); supplier.setName("Hòa Phát"); return supplier;
            }
        });
        inject("warehouseStaffDAO",new WarehouseStaffDAO4(){
            @Override public WarehouseStaff4 getWarehouseStaffDetails(int id) {
                calls.add("staff:"+id);
                WarehouseStaff4 staff=new WarehouseStaff4(); staff.setId(id); staff.setFullName("Tên từ CSDL"); return staff;
            }
        });
        Map<String,String[]> p=new HashMap<>(); p.put("supplierId",new String[]{"1"});
        servlet.startPurchase(request(p),response(new AtomicInteger()));
        assertEquals(List.of("supplier:1","staff:2"),calls);
        String uuid=redirectedTo.substring(redirectedTo.indexOf("requestCode=")+"requestCode=".length());
        PurchaseInvoice4 started=((Map<String,PurchaseInvoice4>) session.getAttribute("purchaseDrafts4")).get(uuid);
        assertEquals("Tên từ CSDL",started.getWarehouseStaff().getFullName());
        assertEquals("Hòa Phát",started.getSupplier().getName());
        assertTrue(started.getInvoiceDetails().isEmpty());
        assertEquals(0,started.getTotalAmount().signum());
    }

    @Test void missingWarehouseStaffDoesNotCreateAnIncompleteDraft() throws Exception {
        inject("supplierDAO",new SupplierDAO4(){
            @Override public Supplier4 getSupplierDetails(int id) { Supplier4 supplier=new Supplier4(); supplier.setId(id); return supplier; }
        });
        inject("warehouseStaffDAO",new WarehouseStaffDAO4(){
            @Override public WarehouseStaff4 getWarehouseStaffDetails(int id) { return null; }
        });
        int before=((Map<?,?>)session.getAttribute("purchaseDrafts4")).size();
        Map<String,String[]> p=new HashMap<>(); p.put("supplierId",new String[]{"1"});
        servlet.startPurchase(request(p),response(new AtomicInteger()));
        assertEquals("/warehouse/suppliers",redirectedTo);
        assertEquals(before,((Map<?,?>)session.getAttribute("purchaseDrafts4")).size());
    }

    @Test void partSearchFailureKeepsDraftAndReportsQueryError() throws Exception {
        add(); servlet.updateInvoiceLines(request(params()),response(new AtomicInteger()));
        inject("sparePartDAO",new SparePartDAO4(){
            @Override public List<SparePart4> findSparePartsByName(String keyword,boolean published) throws SQLException { throw new SQLException("test query failure"); }
        });
        Map<String,String[]> p=params(); p.put("keyword",new String[]{"lọc"});
        servlet.showDraft(request(p),response(new AtomicInteger()));
        assertEquals("/WEB-INF/view/warehouseStaff/purchaseInvoice.jsp",forwardedTo);
        assertNotNull(requestAttributes.get("partSearchError"));
        assertEquals("lọc",requestAttributes.get("keyword"));
        assertEquals(0,new BigDecimal("800000").compareTo(((PurchaseInvoice4)requestAttributes.get("purchaseInvoice")).getTotalAmount()));
    }

    @Test void paymentErrorsStayOnConfirmedInvoiceAndKeepEnteredAmount() throws Exception {
        add(); servlet.updateInvoiceLines(request(params()),response(new AtomicInteger()));
        PurchaseInvoice4 invoice=current(); invoice.setId(42);
        AtomicInteger attempts=new AtomicInteger();
        inject("purchaseInvoiceDAO",new PurchaseInvoiceDAO4(){
            @Override public PurchaseInvoice4 getPurchaseInvoiceDetails(int id) { return invoice; }
            @Override public boolean recordPayment(int id,BigDecimal amount,int author) throws IllegalArgumentException {
                attempts.incrementAndGet();
                throw new IllegalArgumentException("amountPaid" + ": " + "Số tiền không khớp tổng hóa đơn");
            }
        });
        for (String raw:List.of("","0","799999","800001")) {
            Map<String,String[]> p=new HashMap<>(); p.put("id",new String[]{"42"}); p.put("amountPaid",new String[]{raw});
            AtomicInteger status=new AtomicInteger();
            servlet.recordPayment(request(p),response(status));
            assertEquals(0,status.get(),"Lỗi số tiền phải hiển thị tại hóa đơn, không trả trang lỗi");
            assertEquals("/WEB-INF/view/warehouseStaff/confirmedInvoice.jsp",forwardedTo);
            assertEquals(raw,((Map<?,?>)requestAttributes.get("formValues")).get("amountPaid"));
            assertNotNull(requestAttributes.get("errorMessage"));
            assertEquals("Chưa thanh toán",invoice.getPaymentStatus());
        }
        assertEquals(2,attempts.get(),"Giá trị rỗng/0 phải bị chặn trước DAO");
    }

    @Test void exactPaymentRedirectsToTheSameInvoice() throws Exception {
        inject("purchaseInvoiceDAO",new PurchaseInvoiceDAO4(){
            @Override public boolean recordPayment(int id,BigDecimal amount,int author) {
                assertEquals(42,id); assertEquals(2,author);
                assertEquals(0,new BigDecimal("800000").compareTo(amount)); return true;
            }
        });
        Map<String,String[]> p=new HashMap<>(); p.put("id",new String[]{"42"}); p.put("amountPaid",new String[]{"800000.00"});
        p.put("currentWarehouseStaffId",new String[]{"999"});
        p.put("paidByEmployee4id",new String[]{"999"});
        AtomicInteger status=new AtomicInteger();
        servlet.recordPayment(request(p),response(status));
        assertEquals(302,status.get()); assertEquals("/warehouse/purchases/view?id=42",redirectedTo);
    }

    @Test void anotherWarehouseStaffCanOpenPaymentFormWithoutChangingCreator() throws Exception {
        WarehouseStaff4 creator=new WarehouseStaff4(); creator.setId(99);
        PurchaseInvoice4 invoice=new PurchaseInvoice4(); invoice.setId(42); invoice.setWarehouseStaff(creator);
        invoice.setPaymentStatus("Chưa thanh toán");
        inject("purchaseInvoiceDAO",new PurchaseInvoiceDAO4(){
            @Override public PurchaseInvoice4 getPurchaseInvoiceDetails(int id) { return invoice; }
        });
        Map<String,String[]> p=new HashMap<>(); p.put("id",new String[]{"42"});
        servlet.viewInvoice(request(p),response(new AtomicInteger()));
        assertEquals(Boolean.TRUE,requestAttributes.get("canRecordPayment"));
        assertEquals(99,invoice.getWarehouseStaff().getId());
        assertEquals("/WEB-INF/view/warehouseStaff/confirmedInvoice.jsp",forwardedTo);
    }
}
