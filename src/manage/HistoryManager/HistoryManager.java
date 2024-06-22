package manage.HistoryManager;

import templates.Task;

import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    List<Task> getLinkedHistory();

    void addInHistoryList(Task task);

    void removeFromHistoryList(int id);
}
