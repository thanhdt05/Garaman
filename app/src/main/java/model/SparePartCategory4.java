package model;

import java.io.Serializable;

public class SparePartCategory4 implements Serializable {
    private static final long serialVersionUID = 2L;

    private int id;
    private String name;
    private String description;
    private String status = "Đang sử dụng";

    public SparePartCategory4() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
