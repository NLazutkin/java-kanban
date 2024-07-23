package test.http.Handlers;

import com.google.gson.Gson;
import enums.HttpStatusCode;
import http.HttpTaskServer;
import manage.Managers;
import manage.TaskManager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestPrioritizedHandler {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = taskServer.getGson();

    public TestPrioritizedHandler() throws IOException {
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
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", 3,
                LocalDateTime.of(2024, 1, 1, 10, 12, 0));
        Task task2 = new Task("Test 2", "Testing task 2", 3,
                LocalDateTime.of(2024, 1, 1, 10, 7, 0));
        Task task3 = new Task("Test 3", "Testing task 3", 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        List<Task> prioritizedTasksFromManager = taskManager.getPrioritizedTasks();

        assertNotNull(prioritizedTasksFromManager, "Задачи не возвращаются");
        assertEquals(3, prioritizedTasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 3", prioritizedTasksFromManager.getFirst().getTitle(), "Некорректное имя подзадачи 3");
        assertEquals("Test 1", prioritizedTasksFromManager.getLast().getTitle(), "Некорректное имя подзадачи 1");
    }

    @Test
    public void testGetHistoryFromEmptyMap() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode());

        List<Task> prioritizedTasksFromManager = taskManager.getPrioritizedTasks();

        assertNotNull(prioritizedTasksFromManager, "Задачи не возвращаются");
        assertEquals(0, prioritizedTasksFromManager.size(), "Некорректное количество задач");
    }
}
