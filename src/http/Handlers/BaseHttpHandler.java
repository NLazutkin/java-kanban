package http.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import enums.HttpStatusCode;
import manage.TaskManager.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected Gson gson;
    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected Optional<Integer> getIdFromPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");

        try {
            return Optional.of(Integer.parseInt(splitStrings[2]));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    protected JsonObject getJsonFromRequestBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        return JsonParser.parseString(body).getAsJsonObject();
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        exchange.sendResponseHeaders(responseCode, responseString.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
    }

    protected void okResponse(HttpExchange exchange, String responseString) throws IOException {
        writeResponse(exchange, responseString, HttpStatusCode.HTTP_OK.getValue());
    }

    protected void okResponse(HttpExchange exchange) throws IOException {
        writeResponse(exchange, HttpStatusCode.HTTP_OK.getReason(), HttpStatusCode.HTTP_OK.getValue());
    }

    protected void createdResponse(HttpExchange exchange) throws IOException {
        writeResponse(exchange, HttpStatusCode.HTTP_CREATED.getReason(), HttpStatusCode.HTTP_CREATED.getValue());
    }

    protected void notAcceptableResponse(HttpExchange exchange) throws IOException {
        writeResponse(exchange, HttpStatusCode.HTTP_NOT_ACCEPTABLE.getReason(), HttpStatusCode.HTTP_NOT_ACCEPTABLE.getValue());
    }

    protected void badRequestResponse(HttpExchange exchange) throws IOException {
        writeResponse(exchange, HttpStatusCode.HTTP_BAD_REQUEST.getReason(), HttpStatusCode.HTTP_BAD_REQUEST.getValue());
    }

    protected void notFoundResponse(HttpExchange exchange) throws IOException {
        writeResponse(exchange, HttpStatusCode.HTTP_NOT_FOUND.getReason(), HttpStatusCode.HTTP_NOT_FOUND.getValue());
    }

    protected void badMethodResponse(HttpExchange exchange) throws IOException {
        writeResponse(exchange, HttpStatusCode.HTTP_BAD_METHOD.getReason(), HttpStatusCode.HTTP_BAD_METHOD.getValue());
    }
}
