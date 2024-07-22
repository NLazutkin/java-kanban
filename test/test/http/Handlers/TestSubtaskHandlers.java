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
import templates.Epic;
import templates.Subtask;
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

public class TestSubtaskHandlers {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = taskServer.getGson();

    public TestSubtaskHandlers() throws IOException {
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
        URI url = URI.create("http://localhost:8080/subtasks/tasks/1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_REQUEST.getValue(), response.statusCode());

        List<Subtask> SubtasksFromManager = taskManager.getSubtasks();

        assertNotNull(SubtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(0, SubtasksFromManager.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testWrongMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5, LocalDateTime.now());
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_METHOD.getValue(), response.statusCode());

        List<Subtask> SubtasksFromManager = taskManager.getSubtasks();

        assertNotNull(SubtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(0, SubtasksFromManager.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testWrongMethodWithIdInPath() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5, LocalDateTime.now());
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_METHOD.getValue(), response.statusCode());

        List<Subtask> SubtasksFromManager = taskManager.getSubtasks();

        assertNotNull(SubtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(0, SubtasksFromManager.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5, LocalDateTime.now());
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        Type taskType = new TypeToken<List<Subtask>>() {
        }.getType();

        List<Task> tasksList = gson.fromJson(response.body(), taskType);

        assertNotNull(tasksList, "Подзадачи не возвращаются");
        assertEquals(1, tasksList.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testGetSubtasksFromEmptyMap() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке напечатать пустой список подзадач ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5, LocalDateTime.now());
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_CREATED.getValue(), response.statusCode());

        List<Subtask> SubtasksFromManager = taskManager.getSubtasks();

        assertNotNull(SubtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, SubtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 1 Subtask", SubtasksFromManager.getFirst().getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testAddSubtaskWithCrossStartTime() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test 2 Subtask", "Testing subtask 2", epic.getId(), 2,
                LocalDateTime.of(2024, 1, 1, 10, 2, 0));
        String taskJson = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_ACCEPTABLE.getValue(), response.statusCode());

        List<Subtask> SubtasksFromManager = taskManager.getSubtasks();

        assertNotNull(SubtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, SubtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 1 Subtask", SubtasksFromManager.getFirst().getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test 2 Subtask", "Testing subtask 2", subtask1.getId(), subtask1.getStatus(),
                subtask1.getEpicId(), subtask1.getDuration().toMinutes(), subtask1.getStartTime());
        String taskJson = gson.toJson(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_CREATED.getValue(), response.statusCode());

        List<Subtask> SubtasksFromManager = taskManager.getSubtasks();

        assertNotNull(SubtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, SubtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 2 Subtask", SubtasksFromManager.getFirst().getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubtaskWithCrossStartTime() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        Subtask subtask2 = new Subtask("Test 2 Subtask", "Testing subtask 2", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 16, 0));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Test 3 Subtask", "Testing subtask 3", subtask2.getId(), subtask2.getStatus(),
                subtask2.getEpicId(), 2, subtask2.getStartTime().minusMinutes(14));
        String taskJson = gson.toJson(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_ACCEPTABLE.getValue(), response.statusCode());

        List<Subtask> SubtasksFromManager = taskManager.getSubtasks();

        assertNotNull(SubtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(2, SubtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1 Subtask", SubtasksFromManager.getFirst().getTitle(), "Некорректное имя подзадачи 1");
        assertEquals("Test 2 Subtask", SubtasksFromManager.getLast().getTitle(), "Некорректное имя подзадачи 2");
    }

    @Test
    public void testDeleteSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        List<Task> tasksList = taskManager.getTasks();

        assertNotNull(tasksList, "Подзадачи не возвращаются");
        assertEquals(0, tasksList.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testDeleteSubtasksFromEmptyMap() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке напечатать пустой список подзадач ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testGeSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        Task SubtasksFromManager = gson.fromJson(response.body(), Task.class);

        assertNotNull(SubtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(subtask.getTitle(), SubtasksFromManager.getTitle(), "Некорректное имя подзадачи 1");
    }

    @Test
    public void testGetSubtaskByWrongId() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/999");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке найти не существующую подзадачу ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testGetSubtaskByNotCorrectId() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/qwerty");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_REQUEST.getValue(), response.statusCode(),
                "При попытке найти подзадачу введен некорректный тип ID, ожидалось " + HttpStatusCode.HTTP_BAD_REQUEST.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        assertNotNull(taskManager.getTasks(), "Подзадачи не возвращаются");
        assertEquals(0, taskManager.getTasks().size(), "Некорректное количество подзадач");
    }

    @Test
    public void testDeleteSubtaskByWrongId() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/999");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке удалить не существующую подзадачу ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());

        List<Subtask> subtaskList = taskManager.getSubtasks();

        assertNotNull(subtaskList, "Подзадачи не возвращаются");
        assertEquals(1, subtaskList.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testDeleteSubtaskByNotCorrectId() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/qwerty");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_REQUEST.getValue(), response.statusCode(),
                "При попытке найти подзадачу введен некорректный тип ID, ожидалось " + HttpStatusCode.HTTP_BAD_REQUEST.getValue()
                        + ", но получено " + response.statusCode());

        List<Subtask> subtaskList = taskManager.getSubtasks();

        assertNotNull(subtaskList, "Подзадачи не возвращаются");
        assertEquals(1, subtaskList.size(), "Некорректное количество подзадач");
    }
}


