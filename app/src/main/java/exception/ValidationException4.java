package exception;

public class ValidationException4 extends Exception {
    private static final long serialVersionUID = 1L;
    private final String field;

    public ValidationException4(String message) {
        super(message);
        this.field = null;
    }

    public ValidationException4(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
