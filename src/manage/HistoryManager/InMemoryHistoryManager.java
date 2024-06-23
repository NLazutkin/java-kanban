package manage.HistoryManager;

import templates.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HandMadeLinkedList historyList = new HandMadeLinkedList();

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    @Override
    public List<Task> getLinkedHistory() {
        return historyList.getLinkedTasks();
    }

    @Override
    public void addInHistoryList(Task task) {
        historyList.add(task);
    }

    @Override
    public void removeFromHistoryList(int id) {
        historyList.remove(id);
    }
}
