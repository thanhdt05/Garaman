package model;

public abstract class Employee4 extends Member4 {
    private static final long serialVersionUID = 1L;

    private String viTri;
    private String trangThaiLamViec;

    public Employee4() {
        super();
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public String getTrangThaiLamViec() {
        return trangThaiLamViec;
    }

    public void setTrangThaiLamViec(String trangThaiLamViec) {
        this.trangThaiLamViec = trangThaiLamViec;
    }
}
