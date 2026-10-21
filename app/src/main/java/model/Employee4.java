package model;

public abstract class Employee4 extends Member4 {
    private static final long serialVersionUID = 2L;

    private String position;
    private String employmentStatus;

    public Employee4() {
        super();
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
}
