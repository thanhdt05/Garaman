package dto;

import model.Employee4;
import model.Member4;

public class SessionUser4 {
    private final int id;
    private final String fullName;
    private final String role;
    private final String position;

    public SessionUser4(int id, String fullName, String role, String position) {
        this.id = id;
        this.fullName = fullName;
        this.role = role;
        this.position = position;
    }

    public static SessionUser4 fromMember(Member4 member) {
        if (member == null) {
            return null;
        }
        String position = null;
        if (member instanceof Employee4) {
            position = ((Employee4) member).getPosition();
        }
        return new SessionUser4(member.getId(), member.getFullName(), member.getRole(), position);
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    public String getPosition() {
        return position;
    }
}
