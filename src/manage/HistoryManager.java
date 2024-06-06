package manage;

import templates.Task;
import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void addInHistoryList(Task task);

    void removeFromHistoryList(int id);
}
