package test.http.Handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.HttpStatusCode;
import http.HttpTaskServer;
import manage.Managers;
import manage.TaskManager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestTaskHandlers {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = taskServer.getGson();

    public TestTaskHandlers() throws IOException {
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
    public void testWrongPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_REQUEST.getValue(), response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testWrongMethod() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 5, LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_METHOD.getValue(), response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testWrongMethodWithIdInPath() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 5, LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_METHOD.getValue(), response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 5, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        Type taskType = new TypeToken<List<Task>>() {
        }.getType();

        List<Task> tasksList = gson.fromJson(response.body(), taskType);

        assertNotNull(tasksList, "Задачи не возвращаются");
        assertEquals(1, tasksList.size(), "Некорректное количество задач");
    }

    @Test
    public void testGetTasksFromEmptyMap() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке напечатать пустой список задач ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 5, LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_CREATED.getValue(), response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTaskWithCrossStartTime() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createTask(task1);

        Task task2 = new Task("Test 2", "Testing task 2", 2,
                LocalDateTime.of(2024, 1, 1, 10, 2, 0));
        String taskJson = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_ACCEPTABLE.getValue(), response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createTask(task1);

        Task task2 = new Task("Test 2", "Testing task 2", task1.getId(), task1.getStatus(),
                task1.getDuration().toMinutes(), task1.getStartTime());
        String taskJson = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_CREATED.getValue(), response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTaskWithCrossStartTime() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1", 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        Task task2 = new Task("Test 2", "Testing task 2", 3,
                LocalDateTime.of(2024, 1, 1, 10, 7, 0));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Task task3 = new Task("Test 3", "Testing task 3", task2.getId(), task2.getStatus(),
                task2.getDuration().toMinutes(), task2.getStartTime().minusMinutes(4));
        String taskJson = gson.toJson(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_ACCEPTABLE.getValue(), response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи 1");
        assertEquals("Test 2", tasksFromManager.getLast().getTitle(), "Некорректное имя задачи 2");
    }

    @Test
    public void testDeleteTasks() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 5, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        List<Task> tasksList = taskManager.getTasks();

        assertNotNull(tasksList, "Задачи не возвращаются");
        assertEquals(0, tasksList.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteTasksFromEmptyMap() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке напечатать пустой список задач ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 5, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        Task tasksFromManager = gson.fromJson(response.body(), Task.class);

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(task.getTitle(), tasksFromManager.getTitle(), "Некорректное имя задачи 1");
    }

    @Test
    public void testGetTaskByWrongId() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 5, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/999");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке найти не существующую задачу ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testGetTaskByNotCorrectId() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 5, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/qwerty");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_REQUEST.getValue(), response.statusCode(),
                "При попытке найти задачу введен некорректный тип ID, ожидалось " + HttpStatusCode.HTTP_BAD_REQUEST.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 5, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        List<Task> taskList = taskManager.getTasks();

        assertNotNull(taskList, "Задачи не возвращаются");
        assertEquals(0, taskList.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteTaskByWrongId() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 5, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/999");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке удалить не существующую задачу ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());

        List<Task> taskList = taskManager.getTasks();

        assertNotNull(taskList, "Задачи не возвращаются");
        assertEquals(1, taskList.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteTaskByNotCorrectId() throws IOException, InterruptedException {
        Task task = new Task("Test 1", "Testing task 1", 5, LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/qwerty");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_REQUEST.getValue(), response.statusCode(),
                "При попытке найти задачу введен некорректный тип ID, ожидалось " + HttpStatusCode.HTTP_BAD_REQUEST.getValue()
                        + ", но получено " + response.statusCode());

        List<Task> taskList = taskManager.getTasks();

        assertNotNull(taskList, "Задачи не возвращаются");
        assertEquals(1, taskList.size(), "Некорректное количество задач");
    }
}

