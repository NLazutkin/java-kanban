package enums;

public enum HttpStatusCode {
    HTTP_OK(200, "OK"),
    HTTP_CREATED(201, "Created"),
    HTTP_BAD_REQUEST(400, "Bad request"),
    HTTP_NOT_FOUND(404, "Not Found"),
    HTTP_BAD_METHOD(405, "Method Not Allowed"),
    HTTP_NOT_ACCEPTABLE(406, "Not Acceptable"),
    HTTP_INTERNAL_ERROR(500, "Internal Server Error");

    private final int value;
    private final String reason;

    HttpStatusCode(int value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public static HttpStatusCode getByValue(int value) {
        return switch (value) {
            case 200 -> HTTP_OK;
            case 201 -> HTTP_CREATED;
            case 400 -> HTTP_BAD_REQUEST;
            case 404 -> HTTP_NOT_FOUND;
            case 405 -> HTTP_BAD_METHOD;
            case 406 -> HTTP_NOT_ACCEPTABLE;
            case 500 -> HTTP_INTERNAL_ERROR;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    public int getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return this.value + " " + this.reason;
    }
}

