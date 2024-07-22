package http.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manage.TaskManager.TaskManager;
import templates.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        List<Task> prioritizedList = taskManager.getPrioritizedTasks();
        if (prioritizedList.isEmpty()) {
            notFoundResponse(exchange);
        } else {
            okResponse(exchange, gson.toJson(prioritizedList));
        }
    }
}
