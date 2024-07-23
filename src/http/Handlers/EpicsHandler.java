package http.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.TaskStatuses;
import manage.TaskManager.TaskManager;
import templates.Epic;
import templates.Subtask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    private Epic getEpicFromRequestBody(HttpExchange exchange) throws IOException {
        JsonObject jsonObject = getJsonFromRequestBody(exchange);
        return gson.fromJson(jsonObject, Epic.class);
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем GET запрос выводим список всех Эпиков");
        List<Epic> epicsList = taskManager.getEpics();
        if (epicsList.isEmpty()) {
            notFoundResponse(exchange);
            return;
        }

        okResponse(exchange, gson.toJson(epicsList));
    }

    private void handleUpdateEpic(HttpExchange exchange, Epic epic) throws IOException {
        Optional<Epic> updatedEpic = Optional.ofNullable(taskManager.updateEpic(epic));
        if (updatedEpic.isPresent()) {
            System.out.println("Обновляем Эпик N " + epic.getId() + " " + epic.getTitle());
            createdResponse(exchange);
            return;
        }

        System.out.println("Ошибка обновления Эпика N " + epic.getId() + " " + epic.getTitle());
        notAcceptableResponse(exchange);
    }

    private void handleCreateEpic(HttpExchange exchange, Epic epic) throws IOException {
        System.out.println("Создаем Эпик " + epic.getTitle());
        epic.setStatus(TaskStatuses.NEW);
        epic.setSubtaskCodes(new ArrayList<>());
        taskManager.createEpic(epic);
        createdResponse(exchange);
    }

    private void handlePostEpics(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем POST запрос");
        Epic epic = getEpicFromRequestBody(exchange);
        if (epic.getId() != 0) {
            handleUpdateEpic(exchange, epic);
        } else {
            handleCreateEpic(exchange, epic);
        }
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем DELETE запрос удаляем все Эпики и их подзадачи");
        if (taskManager.clearEpicList()) {
            okResponse(exchange);
            return;
        }

        notFoundResponse(exchange);
    }

    private void handleGetEpicById(HttpExchange exchange, Optional<Integer> epicId) throws IOException {
        System.out.println("Выполняем GET запрос выводим Эпик по номеру");
        if (epicId.isPresent()) {
            Optional<Epic> epic = Optional.ofNullable(taskManager.getEpicFromList(epicId.get()));
            if (epic.isPresent()) {
                System.out.println("Эпик по номеру " + epicId.get() + " найден");
                okResponse(exchange, gson.toJson(epic.get()));
            } else {
                System.out.println("Эпик не найден");
                notFoundResponse(exchange);
            }

            return;
        }

        System.out.println("Ошибка в URL");
        badRequestResponse(exchange);
    }

    private void handleDeleteEpicById(HttpExchange exchange, Optional<Integer> epicId) throws IOException {
        System.out.println("Выполняем DELETE запрос");
        if (epicId.isPresent()) {
            if (taskManager.deleteEpic(epicId.get())) {
                System.out.println("Эпик и его подзадачи удалены");
                okResponse(exchange);
            } else {
                System.out.println("Эпик не найден");
                notFoundResponse(exchange);
            }

            return;
        }

        System.out.println("Ошибка в URL");
        badRequestResponse(exchange);
    }

    private void handleGetEpicSubtasksById(HttpExchange exchange, Optional<Integer> epicId) throws IOException {
        System.out.println("Выполняем GET запрос выводим все подзадачи Эпика по номеру");
        if (epicId.isPresent()) {
            List<Subtask> subtasksList = taskManager.getSubtaskByEpicId(epicId.get());
            if (subtasksList.isEmpty()) {
                System.out.println("Нет подзадач у Эпика по номеру " + epicId.get());
                notFoundResponse(exchange);
                return;
            }

            System.out.println("подзадачи Эпика по номеру " + epicId.get() + " найдены");
            okResponse(exchange, gson.toJson(subtasksList));
        }

        System.out.println("Ошибка в URL");
        badRequestResponse(exchange);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем запрос для ветки /epics");

        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        String method = exchange.getRequestMethod();

        if (splitStrings.length == 2) {
            switch (method) {
                case "GET" -> handleGetEpics(exchange);
                case "POST" -> handlePostEpics(exchange);
                case "DELETE" -> handleDeleteEpics(exchange);
                default -> badMethodResponse(exchange);
            }
        } else if (splitStrings.length == 3) {
            Optional<Integer> epicId = getIdFromPath(exchange);
            switch (method) {
                case "GET" -> handleGetEpicById(exchange, epicId);
                case "DELETE" -> handleDeleteEpicById(exchange, epicId);
                default -> badMethodResponse(exchange);
            }
        } else if (splitStrings.length == 4 && splitStrings[3].equals("subtasks")) {
            Optional<Integer> epicId = getIdFromPath(exchange);
            switch (method) {
                case "GET" -> handleGetEpicSubtasksById(exchange, epicId);
                default -> badMethodResponse(exchange);
            }
        } else {
            System.out.println("Ошибка в URL");
            badRequestResponse(exchange);
        }
    }
}
