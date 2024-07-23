package test.enums;

import enums.HttpStatusCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHttpStatusCode {
    @Test
    public void testGetByValue() {
        HttpStatusCode value = HttpStatusCode.getByValue(200);

        assertEquals(200, value.getValue(), "Код не соответствует запрашиваемому");
        assertEquals("OK", value.getReason(), "Сообщение не соответствует запрашиваемому");
    }

    @Test
    public void testToString() {
        String value = HttpStatusCode.HTTP_NOT_FOUND.toString();

        assertEquals("404 Not Found", value, "Код и сообщение не соответствует запрашиваемому");
    }
}
