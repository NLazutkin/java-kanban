package test.http.Handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.HttpStatusCode;
import enums.TaskStatuses;
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

public class TestEpicHandlers {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = taskServer.getGson();

    public TestEpicHandlers() throws IOException {
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
        URI url = URI.create("http://localhost:8080/epics/tasks/1");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_REQUEST.getValue(), response.statusCode());

        List<Epic> EpicsFromManager = taskManager.getEpics();

        assertNotNull(EpicsFromManager, "Эпики не возвращаются");
        assertEquals(0, EpicsFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testWrongMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_METHOD.getValue(), response.statusCode());

        List<Epic> EpicsFromManager = taskManager.getEpics();

        assertNotNull(EpicsFromManager, "Эпики не возвращаются");
        assertEquals(0, EpicsFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testWrongMethodWithIdInPath() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_METHOD.getValue(), response.statusCode());

        List<Epic> EpicsFromManager = taskManager.getEpics();

        assertNotNull(EpicsFromManager, "Эпики не возвращаются");
        assertEquals(0, EpicsFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5, LocalDateTime.now());
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        Type taskType = new TypeToken<List<Epic>>() {
        }.getType();

        List<Epic> EpicsFromManager = gson.fromJson(response.body(), taskType);

        assertNotNull(EpicsFromManager, "Эпики не возвращаются");
        assertEquals(1, EpicsFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testGetEpicsFromEmptyMap() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке напечатать пустой список эпиков ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_CREATED.getValue(), response.statusCode());

        List<Epic> EpicsFromManager = taskManager.getEpics();

        assertNotNull(EpicsFromManager, "Эпики не возвращаются");
        assertEquals(1, EpicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 1 Epic", EpicsFromManager.getFirst().getTitle(), "Некорректное имя эпиков");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask);

        Epic epic1 = new Epic("Test 2 Epic", "Testing Epic 2", epic.getId(), TaskStatuses.IN_PROGRESS,
                epic.getSubtaskCodes(), epic.getDurationToMinutes(), epic.getStartTime(), epic.getEndTime());
        String taskJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_CREATED.getValue(), response.statusCode());

        List<Epic> EpicsFromManager = taskManager.getEpics();

        assertNotNull(EpicsFromManager, "Эпики не возвращаются");
        assertEquals(1, EpicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 2 Epic", EpicsFromManager.getFirst().getTitle(), "Некорректное имя эпиков");
    }

    @Test
    public void testUpdateEpicWithWrongId() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask);

        Epic epic1 = new Epic("Test 2 Epic", "Testing Epic 2", 999, TaskStatuses.IN_PROGRESS,
                epic.getSubtaskCodes(), epic.getDurationToMinutes(), epic.getStartTime(), epic.getEndTime());
        String taskJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_ACCEPTABLE.getValue(), response.statusCode());

        List<Epic> EpicsFromManager = taskManager.getEpics();

        assertNotNull(EpicsFromManager, "Эпики не возвращаются");
        assertEquals(1, EpicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 1 Epic", EpicsFromManager.getFirst().getTitle(), "Некорректное имя эпиков");
    }

    @Test
    public void testDeleteEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        List<Epic> EpicsFromManager = taskManager.getEpics();

        assertNotNull(EpicsFromManager, "Эпики не возвращаются");
        assertEquals(0, EpicsFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testDeleteEpicsFromEmptyMap() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке напечатать пустой список эпиков ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        Task SubtasksFromManager = gson.fromJson(response.body(), Task.class);

        assertNotNull(SubtasksFromManager, "Эпики не возвращаются");
        assertEquals(epic.getTitle(), SubtasksFromManager.getTitle(), "Некорректное имя Эпика 1");
    }

    @Test
    public void testGetEpicByWrongId() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epic/999");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке найти не существующий Эпик ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testGetEpicByNotCorrectId() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/qwerty");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_REQUEST.getValue(), response.statusCode(),
                "При попытке найти Эпик введен некорректный тип ID, ожидалось " + HttpStatusCode.HTTP_BAD_REQUEST.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        assertNotNull(taskManager.getTasks(), "Эпики не возвращаются");
        assertEquals(0, taskManager.getTasks().size(), "Некорректное количество эпиков");
    }

    @Test
    public void testDeleteEpicByWrongId() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/999");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке удалить не существующий Эпик ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());

        List<Epic> EpicsFromManager = taskManager.getEpics();

        assertNotNull(EpicsFromManager, "Эпики не возвращаются");
        assertEquals(1, EpicsFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteEpicByNotCorrectId() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/qwerty");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_REQUEST.getValue(), response.statusCode(),
                "При попытке найти Эпик введен некорректный тип ID, ожидалось " + HttpStatusCode.HTTP_BAD_REQUEST.getValue()
                        + ", но получено " + response.statusCode());

        List<Epic> EpicsFromManager = taskManager.getEpics();

        assertNotNull(EpicsFromManager, "Эпики не возвращаются");
        assertEquals(1, EpicsFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test 2 Subtask", "Testing subtask 2", epic.getId(), 2,
                LocalDateTime.of(2024, 1, 1, 10, 12, 0));
        taskManager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_OK.getValue(), response.statusCode());

        List<Subtask> EpicSubtasksFromManager = taskManager.getSubtaskByEpicId(epic.getId());

        assertNotNull(EpicSubtasksFromManager, "Подзадачи эпика не возвращаются");
        assertEquals(2, EpicSubtasksFromManager.size(), "Некорректное количество подзадач эпика");
    }

    @Test
    public void testGetEpicSubtaskWithEmptyMap() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке напечатать пустой список подзадач эпика ожидалось " + HttpStatusCode.HTTP_NOT_FOUND.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testGetEpicSubtasksWithWrongMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test 2 Subtask", "Testing subtask 2", epic.getId(), 2,
                LocalDateTime.of(2024, 1, 1, 10, 12, 0));
        taskManager.createSubtask(subtask2);


        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(taskJson);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(body)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_METHOD.getValue(), response.statusCode(),
                "При попытке найти подзадачи эпика введен некорректный метод, ожидалось \"GET\"");
    }

    @Test
    public void testGetEpicSubtasksWithNotCorrectIdInPath() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test 2 Subtask", "Testing subtask 2", epic.getId(), 2,
                LocalDateTime.of(2024, 1, 1, 10, 12, 0));
        taskManager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/qwerty/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_BAD_REQUEST.getValue(), response.statusCode(),
                "При попытке получить список подзадач введен некорректный тип ID, ожидалось " + HttpStatusCode.HTTP_BAD_REQUEST.getValue()
                        + ", но получено " + response.statusCode());
    }

    @Test
    public void testGetEpicSubtasksWithWrongIdInPath() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 1 Epic", "Testing Epic 1");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Test 1 Subtask", "Testing subtask 1", epic.getId(), 5,
                LocalDateTime.of(2024, 1, 1, 10, 0, 0));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test 2 Subtask", "Testing subtask 2", epic.getId(), 2,
                LocalDateTime.of(2024, 1, 1, 10, 12, 0));
        taskManager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/999/subtasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(HttpStatusCode.HTTP_NOT_FOUND.getValue(), response.statusCode(),
                "При попытке найти подзадачи эпика введен некорректный метод, ожидалось \"GET\"");
    }
}
