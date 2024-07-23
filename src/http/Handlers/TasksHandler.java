package http.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.TaskStatuses;
import exceptions.ManagerSaveException;
import manage.TaskManager.TaskManager;
import templates.Task;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    private Task getTaskFromRequestBody(HttpExchange exchange) throws IOException {
        JsonObject jsonObject = getJsonFromRequestBody(exchange);
        return gson.fromJson(jsonObject, Task.class);
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем GET запрос выводим список всех задач");
        List<Task> tasksList = taskManager.getTasks();
        if (tasksList.isEmpty()) {
            notFoundResponse(exchange);
            return;
        }

        okResponse(exchange, gson.toJson(tasksList));
    }

    private void handleUpdateTask(HttpExchange exchange, Task task) throws IOException {
        Optional<Task> updatedTask = Optional.ofNullable(taskManager.updateTask(task));
        if (updatedTask.isPresent()) {
            System.out.println("Обновляем задачу N " + task.getId() + " " + task.getTitle());
            createdResponse(exchange);
            return;
        }

        System.out.println("Ошибка обновления задачи N " + task.getId() + " " + task.getTitle());
        notAcceptableResponse(exchange);
    }

    private void handleCreateTask(HttpExchange exchange, Task task) throws IOException {
        try {
            System.out.println("Создаем задачу " + task.getTitle());
            task.setStatus(TaskStatuses.NEW);
            taskManager.createTask(task);
            createdResponse(exchange);
        } catch (ManagerSaveException | IOException exception) {
            notAcceptableResponse(exchange);
            System.out.println(exception.getMessage());
        }
    }

    private void handlePostTasks(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем POST запрос");
        Task task = getTaskFromRequestBody(exchange);
        if (task.getId() != 0) {
            handleUpdateTask(exchange, task);
        } else {
            handleCreateTask(exchange, task);
        }
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем DELETE запрос удаляем все задачи");
        if (taskManager.clearTasksList()) {
            okResponse(exchange);
            return;
        }

        notFoundResponse(exchange);
    }

    private void handleGetTaskById(HttpExchange exchange, Optional<Integer> taskId) throws IOException {
        System.out.println("Выполняем GET запрос выводим Задачу по номеру");
        if (taskId.isPresent()) {
            Optional<Task> task = Optional.ofNullable(taskManager.getTaskFromList(taskId.get()));
            if (task.isPresent()) {
                System.out.println("Задача по номеру " + taskId.get() + " найдена");
                okResponse(exchange, gson.toJson(task.get()));
            } else {
                System.out.println("Задача не найдена");
                notFoundResponse(exchange);
            }

            return;
        }

        System.out.println("Ошибка в URL");
        badRequestResponse(exchange);
    }

    private void handleDeleteTaskById(HttpExchange exchange, Optional<Integer> taskId) throws IOException {
        System.out.println("Выполняем DELETE запрос");
        if (taskId.isPresent()) {
            if (taskManager.deleteTask(taskId.get())) {
                System.out.println("Задача удалена");
                okResponse(exchange);
            } else {
                System.out.println("Задача не найдена");
                notFoundResponse(exchange);
            }

            return;
        }

        System.out.println("Ошибка в URL");
        badRequestResponse(exchange);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Выполняем запрос для ветки /tasks");

        String path = exchange.getRequestURI().getPath();
        String[] splitStrings = path.split("/");
        String method = exchange.getRequestMethod();

        if (splitStrings.length == 2) {
            switch (method) {
                case "GET" -> handleGetTasks(exchange);
                case "POST" -> handlePostTasks(exchange);
                case "DELETE" -> handleDeleteTasks(exchange);
                default -> badMethodResponse(exchange);
            }
        } else if (splitStrings.length == 3) {
            Optional<Integer> taskId = getIdFromPath(exchange);
            switch (method) {
                case "GET" -> handleGetTaskById(exchange, taskId);
                case "DELETE" -> handleDeleteTaskById(exchange, taskId);
                default -> badMethodResponse(exchange);
            }
        } else {
            System.out.println("Ошибка в URL");
            badRequestResponse(exchange);
        }
    }
}