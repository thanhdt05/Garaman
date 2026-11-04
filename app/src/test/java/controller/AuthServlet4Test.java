package controller;

import dao.MemberDAO4;
import model.Customer4;
import model.Member4;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;
import java.lang.reflect.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** Exercises the real Servlet entry points after removal of filters/DTO/util. */
class AuthServlet4Test {
    private final Map<String,Object> sessionValues = new HashMap<>();
    private final Map<String,Object> attributes = new HashMap<>();
    private final Map<String,String> parameters = new HashMap<>();
    private String method = "GET", path = "/login", redirect, forward, requestEncoding, responseEncoding;
    private int status;
    private boolean invalidated;
    private AuthServlet4 servlet;
    private Customer4 member;
    private HttpSession session;
    private HttpServletRequest request;
    private HttpServletResponse response;

    private static Object empty(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        return null;
    }
    @BeforeEach void setup() throws Exception {
        session = (HttpSession) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{HttpSession.class}, (p,m,a) -> {
            switch (m.getName()) {
                case "getAttribute": return sessionValues.get(a[0]);
                case "setAttribute": sessionValues.put((String)a[0],a[1]); return null;
                case "removeAttribute": sessionValues.remove(a[0]); return null;
                case "invalidate": invalidated=true; sessionValues.clear(); return null;
                default: return empty(m.getReturnType());
            }
        });
        request = (HttpServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{HttpServletRequest.class}, (p,m,a) -> {
            switch (m.getName()) {
                case "getSession": return session;
                case "getParameter": return parameters.get(a[0]);
                case "getMethod": return method;
                case "getServletPath": return path;
                case "getContextPath": return "";
                case "setCharacterEncoding": requestEncoding=(String)a[0]; return null;
                case "setAttribute": attributes.put((String)a[0],a[1]); return null;
                case "getAttribute": return attributes.get(a[0]);
                case "getRequestDispatcher":
                    String target=(String)a[0];
                    return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{RequestDispatcher.class},(x,n,b)->{
                        if (n.getName().equals("forward")) forward=target;
                        return null;
                    });
                default: return empty(m.getReturnType());
            }
        });
        response = (HttpServletResponse) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{HttpServletResponse.class}, (p,m,a) -> {
            if (m.getName().equals("sendRedirect")) redirect=(String)a[0];
            if (m.getName().equals("sendError")) status=(Integer)a[0];
            if (m.getName().equals("setCharacterEncoding")) responseEncoding=(String)a[0];
            return empty(m.getReturnType());
        });
        member = new Customer4(); member.setId(3); member.setRole("Customer"); member.setAccountStatus("Hoạt động");
        member.setPassword("pbkdf2_sha256$600000$3fGGHCpOcHuqkDsfiHGjKQ==$/f0Fp96dlpDSsAg92MwD7u5SOmEwWwJ71fT79UrGoec=");
        servlet = new AuthServlet4();
        Field field=AuthServlet4.class.getDeclaredField("memberDAO"); field.setAccessible(true);
        field.set(servlet,new MemberDAO4(){ @Override public Member4 findByUsername(String name){ return member; }});
    }
    @Test void seedLoginRenewsSessionAndDoesNotStorePassword() throws Exception {
        servlet.doGet(request,response);
        assertEquals("UTF-8",requestEncoding); assertEquals("UTF-8",responseEncoding);
        String token=(String)sessionValues.get("csrfToken4"); assertNotNull(token);
        method="POST"; parameters.put("username","khach26"); parameters.put("password","123456"); parameters.put("csrfToken",token);
        servlet.doPost(request,response);
        assertEquals("/home",redirect); assertTrue(invalidated);
        assertSame(member,sessionValues.get("currentUser4")); assertNull(member.getPassword());
        assertNotEquals(token,sessionValues.get("csrfToken4"));
    }
    @Test void wrongPasswordAndMalformedHashDoNotAuthenticate() throws Exception {
        method="POST"; parameters.put("username","khach26"); parameters.put("password","wrong");
        sessionValues.put("csrfToken4","test-token"); parameters.put("csrfToken","test-token");
        servlet.doPost(request,response);
        assertNull(sessionValues.get("currentUser4")); assertNotNull(attributes.get("errorMessage"));
        member.setPassword("invalid"); parameters.put("password","123456"); servlet.doPost(request,response);
        assertNull(sessionValues.get("currentUser4")); assertEquals("/WEB-INF/view/member/login.jsp",forward);
    }
    @Test void postWithoutTokenAndGetLogoutAreRejected() throws Exception {
        method="POST"; servlet.doPost(request,response); assertEquals(403,status);
        method="GET"; path="/logout"; servlet.doGet(request,response); assertEquals(405,status); assertFalse(invalidated);
    }
    @Test void moduleEntryPointsStillEnforceRoleAndLogin() throws Exception {
        new CatalogServlet4().doGet(request,response); assertEquals("/login",redirect);
        sessionValues.put("currentUser4",member);
        new SupplierServlet4().doGet(request,response); assertEquals(403,status);
        new PurchaseInvoiceServlet4().doPost(request,response); assertEquals(403,status);
    }
}
