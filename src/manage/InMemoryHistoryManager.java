package manage;

import templates.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final int MAX_HISTORY_ELEMENTS = 10;
    private ArrayList<Task> historyList = new ArrayList<>();
    @Override
    public ArrayList<Task> getHistory() {
        return historyList;
    }

    @Override
    public void addInHistoryList(Task task) {
        if (task != null) {
            if (historyList.size() >= MAX_HISTORY_ELEMENTS) {
                historyList.removeFirst();
            }
            historyList.add(task);
        }
    }

}
