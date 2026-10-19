package dto;

import model.Employee4;
import model.Member4;

public class SessionUser4 {
    private final int id;
    private final String hoTen;
    private final String vaiTro;
    private final String viTri;

    public SessionUser4(int id, String hoTen, String vaiTro, String viTri) {
        this.id = id;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
        this.viTri = viTri;
    }

    public static SessionUser4 fromMember(Member4 member) {
        if (member == null) {
            return null;
        }
        String viTri = null;
        if (member instanceof Employee4) {
            viTri = ((Employee4) member).getViTri();
        }
        return new SessionUser4(member.getId(), member.getHoTen(), member.getVaiTro(), viTri);
    }

    public int getId() {
        return id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public String getViTri() {
        return viTri;
    }
}
