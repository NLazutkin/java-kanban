package http.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manage.TaskManager.TaskManager;
import templates.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        List<Task> historyList = taskManager.getLinkedHistory();
        if (historyList.isEmpty()) {
            notFoundResponse(exchange);
        } else {
            okResponse(exchange, gson.toJson(historyList));
        }
    }

}
