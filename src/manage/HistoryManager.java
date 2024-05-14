package manage;

import templates.Task;

import java.util.ArrayList;

public interface HistoryManager {
    public ArrayList<Task> getHistory();
    public void addInHistoryList(Task task);
}
