package http.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.TaskStatuses;
import exceptions.ManagerSaveException;
import manage.TaskManager.TaskManager;
import templates.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    protected Subtask getSubtaskFromRequestBody(HttpExchange exchange) throws IOException {
        JsonObject jsonObject = getJsonFromRequestBody(exchange);
        return gson.fromJson(jsonObject, Subtask.class);
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем GET запрос выводим список всех подзадач");
        List<Subtask> subtasksList = taskManager.getSubtasks();
        if (subtasksList.isEmpty()) {
            notFoundResponse(exchange);
            return;
        }

        okResponse(exchange, gson.toJson(subtasksList));
    }

    private void handleUpdateSubtasks(HttpExchange exchange, Subtask subtask) throws IOException {
        Optional<Subtask> updatedSubtask = Optional.ofNullable(taskManager.updateSubtask(subtask));
        if (updatedSubtask.isPresent()) {
            System.out.println("Обновляем подзадачу N " + subtask.getId() + " " + subtask.getTitle());
            createdResponse(exchange);
            return;
        }

        System.out.println("Ошибка обновления подзадачи N " + subtask.getId() + " " + subtask.getTitle());
        notAcceptableResponse(exchange);
    }

    private void handleCreateSubtasks(HttpExchange exchange, Subtask subtask) throws IOException {
        try {
            System.out.println("Создаем подзадачу " + subtask.getTitle());
            subtask.setStatus(TaskStatuses.NEW);
            taskManager.createSubtask(subtask);
            createdResponse(exchange);
        } catch (ManagerSaveException | IOException exception) {
            notAcceptableResponse(exchange);
            System.out.println(exception.getMessage());
        }
    }

    private void handlePostSubtasks(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем POST запрос");
        Subtask subtask = getSubtaskFromRequestBody(exchange);
        if (subtask.getId() != 0) {
            handleUpdateSubtasks(exchange, subtask);
        } else {
            handleCreateSubtasks(exchange, subtask);
        }
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем DELETE запрос удаляем все подзадачи");
        if (taskManager.clearSubtaskList()) {
            okResponse(exchange);
            return;
        }

        notFoundResponse(exchange);
    }

    private void handleGetSubtaskById(HttpExchange exchange, Optional<Integer> subtaskId) throws IOException {
        System.out.println("Выполняем GET запрос выводим Подзадачу по номеру");
        if (subtaskId.isPresent()) {
            Optional<Subtask> subtask = Optional.ofNullable(taskManager.getSubtaskFromList(subtaskId.get()));
            if (subtask.isPresent()) {
                System.out.println("Подзадача по номеру " + subtaskId.get() + " найдена");
                okResponse(exchange, gson.toJson(subtask.get()));
            } else {
                System.out.println("Подзадача не найдена");
                notFoundResponse(exchange);
            }

            return;
        }

        System.out.println("Ошибка в URL");
        badRequestResponse(exchange);
    }

    private void handleDeleteSubtaskById(HttpExchange exchange, Optional<Integer> subtaskId) throws IOException {
        System.out.println("Выполняем DELETE запрос");
        if (subtaskId.isPresent()) {
            if (taskManager.deleteSubtask(subtaskId.get())) {
                System.out.println("Подзадача удалена");
                okResponse(exchange);
            } else {
                System.out.println("Подзадача не найдена");
                notFoundResponse(exchange);
            }

            return;
        }

        System.out.println("Ошибка в URL");
        badRequestResponse(exchange);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем запрос для ветки /subtasks");

        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        String method = exchange.getRequestMethod();

        if (splitStrings.length == 2) {
            switch (method) {
                case "GET" -> handleGetSubtasks(exchange);
                case "POST" -> handlePostSubtasks(exchange);
                case "DELETE" -> handleDeleteSubtasks(exchange);
                default -> badMethodResponse(exchange);
            }
        } else if (splitStrings.length == 3) {
            Optional<Integer> subtaskId = getIdFromPath(exchange);
            switch (method) {
                case "GET" -> handleGetSubtaskById(exchange, subtaskId);
                case "DELETE" -> handleDeleteSubtaskById(exchange, subtaskId);
                default -> badMethodResponse(exchange);
            }
        } else {
            System.out.println("Ошибка в URL");
            badRequestResponse(exchange);
        }
    }
}
