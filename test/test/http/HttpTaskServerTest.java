package test.http;

import com.google.gson.Gson;
import enums.HttpStatusCode;
import http.HttpTaskServer;
import manage.Managers;
import manage.TaskManager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = taskServer.getGson();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.clearTasksList();
        taskManager.clearSubtaskList();
        taskManager.clearEpicList();

        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetGson() throws IOException {
        assertNotNull(taskServer.getGson(), "Объект Gson не возвращается");
    }

    @Test
    public void testWrongPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/qwerty");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При обращении к несуществующем EndPoint ожидалась ошибка с кодом " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получена " + response.statusCode());
    }
}
