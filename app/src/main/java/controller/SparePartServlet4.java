package controller;

import dao.SparePartCategoryDAO4;
import dao.SparePartDAO4;
import exception.ValidationException4;
import model.PurchaseInvoice4;
import model.SparePart4;
import model.SparePartCategory4;
import util.AppConstants4;
import util.DraftStore4;
import util.Validation4;
import util.WebUtil4;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SparePartServlet4 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private SparePartCategoryDAO4 categoryDAO;
    private SparePartDAO4 sparePartDAO;

    @Override
    public void init() throws ServletException {
        categoryDAO = new SparePartCategoryDAO4();
        sparePartDAO = new SparePartDAO4();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/warehouse/parts/new".equals(path)) {
            hienThiThemPhuTung(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/warehouse/parts/create".equals(path)) {
            themPhuTung(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Yêu cầu không được hỗ trợ");
    }

    public void hienThiThemPhuTung(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String maYeuCau = request.getParameter("maYeuCau");
        HttpSession session = request.getSession(false);

        PurchaseInvoice4 draft = null;
        long draftVersion = 1L;
        if (session != null && maYeuCau != null) {
            try {
                draft = DraftStore4.layBanNhap(session, maYeuCau);
                draftVersion = DraftStore4.layPhienBan(session, maYeuCau);
            } catch (ValidationException4 ignored) {
                draft = null;
            }
        }

        if (draft == null) {
            WebUtil4.datFlash(request, "Phiên lập hóa đơn không tồn tại hoặc đã hết hạn. Vui lòng chọn lại nhà cung cấp.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }

        try {
            List<SparePartCategory4> dsNhom = categoryDAO.layDanhSachNhom();
            request.setAttribute("dsNhom", dsNhom);
            request.setAttribute("maYeuCau", maYeuCau);
            request.setAttribute("draftVersion", draftVersion);
            request.setAttribute("buocNhap", 2);

            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdThemPhuTung.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi CSDL khi tải danh mục nhóm phụ tùng.");
            request.setAttribute("buocNhap", 2);
            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdThemPhuTung.jsp").forward(request, response);
        }
    }

    public void themPhuTung(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String maYeuCau = request.getParameter("maYeuCau");
        HttpSession session = request.getSession(false);

        PurchaseInvoice4 draft = null;
        long draftVersion = 1L;
        if (session != null && maYeuCau != null) {
            try {
                draft = DraftStore4.layBanNhap(session, maYeuCau);
                draftVersion = DraftStore4.layPhienBan(session, maYeuCau);
            } catch (ValidationException4 ignored) {
                draft = null;
            }
        }

        if (draft == null) {
            WebUtil4.datFlash(request, "Phiên lập hóa đơn không tồn tại hoặc đã hết hạn.");
            response.sendRedirect(request.getContextPath() + "/warehouse/suppliers");
            return;
        }

        String ten = request.getParameter("ten");
        String nhomId = request.getParameter("nhomId");
        String donVi = request.getParameter("donVi");
        String hangSanXuat = request.getParameter("hangSanXuat");
        String giaBan = request.getParameter("giaBan");
        String thongSo = request.getParameter("thongSo");
        String dongXePhuHop = request.getParameter("dongXePhuHop");
        String hanBaoHanh = request.getParameter("hanBaoHanh");
        String trangThai = request.getParameter("trangThai");
        String dieuKienBaoHanh = request.getParameter("dieuKienBaoHanh");
        String moTa = request.getParameter("moTa");
        String hinhAnh = request.getParameter("hinhAnh");

        Map<String, String> formValues = new HashMap<>();
        formValues.put("ten", ten != null ? ten : "");
        formValues.put("nhomId", nhomId != null ? nhomId : "");
        formValues.put("donVi", donVi != null ? donVi : "");
        formValues.put("hangSanXuat", hangSanXuat != null ? hangSanXuat : "");
        formValues.put("giaBan", giaBan != null ? giaBan : "");
        formValues.put("thongSo", thongSo != null ? thongSo : "");
        formValues.put("dongXePhuHop", dongXePhuHop != null ? dongXePhuHop : "");
        formValues.put("hanBaoHanh", hanBaoHanh != null ? hanBaoHanh : "");
        formValues.put("trangThai", trangThai != null ? trangThai : AppConstants4.TRANG_THAI_PT_CHUA_CONG_BO);
        formValues.put("dieuKienBaoHanh", dieuKienBaoHanh != null ? dieuKienBaoHanh : "");
        formValues.put("moTa", moTa != null ? moTa : "");
        formValues.put("hinhAnh", hinhAnh != null ? hinhAnh : "");
        request.setAttribute("formValues", formValues);
        request.setAttribute("maYeuCau", maYeuCau);
        request.setAttribute("draftVersion", draftVersion);
        request.setAttribute("buocNhap", 2);

        SparePart4 part = new SparePart4();
        part.setTen(ten);
        part.setHangSanXuat(hangSanXuat);
        part.setDonVi(donVi);
        part.setMoTa(moTa);
        part.setHinhAnh(hinhAnh != null && !hinhAnh.trim().isEmpty() ? hinhAnh.trim() : null);
        part.setThongSo(thongSo);
        part.setDongXePhuHop(dongXePhuHop);
        part.setHanBaoHanh(hanBaoHanh);
        part.setDieuKienBaoHanh(dieuKienBaoHanh);
        part.setTrangThai(trangThai != null && !trangThai.trim().isEmpty() ? trangThai.trim() : AppConstants4.TRANG_THAI_PT_CHUA_CONG_BO);

        try {
            int nId = Validation4.idDuong(nhomId, "nhomId");
            SparePartCategory4 category = categoryDAO.layNhomDangSuDung(nId);
            if (category == null) {
                throw new ValidationException4("nhomId", "Nhóm phụ tùng đã chọn không hợp lệ hoặc đã ngừng sử dụng");
            }
            part.setNhom(category);

            if (giaBan != null && !giaBan.trim().isEmpty()) {
                part.setGiaBan(Validation4.soThapPhan(giaBan, "giaBan", 15, 2, true));
            } else {
                part.setGiaBan(null);
            }

            int newPartId = sparePartDAO.themPhuTung(part);
            WebUtil4.datFlash(request, "Đã thêm phụ tùng mới thành công: " + part.getTen());

            String encodedName = URLEncoder.encode(part.getTen(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/warehouse/purchases/draft?maYeuCau=" + maYeuCau +
                    "&selectedPartId=" + newPartId + "&tuKhoa=" + encodedName);

        } catch (ValidationException4 ve) {
            request.setAttribute("errorMessage", ve.getMessage());
            if (ve.getField() != null) {
                Map<String, String> fieldErrors = new HashMap<>();
                fieldErrors.put(ve.getField(), ve.getMessage());
                request.setAttribute("fieldErrors", fieldErrors);
            }
            try {
                request.setAttribute("dsNhom", categoryDAO.layDanhSachNhom());
            } catch (SQLException ignored) {
            }
            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdThemPhuTung.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi CSDL khi lưu thông tin phụ tùng. Vui lòng kiểm tra lại.");
            try {
                request.setAttribute("dsNhom", categoryDAO.layDanhSachNhom());
            } catch (SQLException ignored) {
            }
            request.getRequestDispatcher("/WEB-INF/view/warehouse/gdThemPhuTung.jsp").forward(request, response);
        }
    }
}
