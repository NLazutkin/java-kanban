package manage.TaskManager;

import enums.TaskStatuses;
import manage.HistoryManager.HistoryManager;
import manage.Managers;
import templates.Epic;
import templates.Subtask;
import templates.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private static int idCounter = 0;
    private final Map<Integer, Task> taskList = new HashMap<>();
    private final Map<Integer, Epic> epicList = new HashMap<>();
    private final Map<Integer, Subtask> subtaskList = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private TaskStatuses calculateEpicStatus(int id) {
        List<Subtask> subtaskList = getSubtaskByEpicId(id);
        int doneStatusCount = 0;
        int inProgressStatusCount = 0;
        int newStatusCount = 0;

        TaskStatuses epicStatus = TaskStatuses.IN_PROGRESS;

        if (!subtaskList.isEmpty()) {
            for (Subtask subtask : subtaskList) {
                TaskStatuses subtaskStatus = subtask.getStatus();
                if (subtaskStatus == TaskStatuses.DONE) {
                    doneStatusCount++;
                } else if (subtaskStatus == TaskStatuses.IN_PROGRESS) {
                    inProgressStatusCount++;
                } else {
                    newStatusCount++;
                }
            }

            if (doneStatusCount == 0 && inProgressStatusCount == 0) {
                epicStatus = TaskStatuses.NEW;
            } else if (newStatusCount == 0 && inProgressStatusCount == 0) {
                epicStatus = TaskStatuses.DONE;
            }
        } else {
            epicStatus = TaskStatuses.NEW;
        }

        return epicStatus;
    }

    private void refreshStatus(int epicId) {
        if (epicId > 0 && !epicList.isEmpty() && epicList.containsKey(epicId)) {
            Epic epic = epicList.get(epicId);
            if (epic != null) {
                updateEpic(new Epic(epic.getTitle(), epic.getDescription(), epic.getId(),
                        calculateEpicStatus(epic.getId()), epic.getSubtaskCodes()));
            }
        }
    }

    private void refreshEpicStatusBySubtask(Subtask subtask) {
        refreshStatus(subtask.getEpicId());
    }

    private void refreshEpicStatus(Epic epic) {
        refreshStatus(epic.getId());
    }

    private void createRelation(Subtask subtask) {
        epicList.get(subtask.getEpicId()).addSubtaskCode(subtask.getId());
        refreshEpicStatusBySubtask(subtask);
    }

    @Override
    public Task createTask(Task task) {
        if (task != null) {
            idCounter += 1;
            if (task.getId() == 0) {
                task.setId(idCounter);
            }
            taskList.put(task.getId(), task);
        }
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic != null) {
            idCounter += 1;
            if (epic.getId() == 0) {
                epic.setId(idCounter);
            }
            epicList.put(epic.getId(), epic);
        }
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask != null) {
            idCounter += 1;
            if (subtask.getId() == 0) {
                subtask.setId(idCounter);
            }
            subtaskList.put(subtask.getId(), subtask);
            createRelation(subtask);
        }
        return subtask;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(taskList.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicList.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtaskList.values());
    }

    @Override
    public boolean clearTasksList() {
        if (!taskList.isEmpty()) {
            for (Integer id : taskList.keySet()) {
                historyManager.removeFromHistoryList(id);
            }
            taskList.clear();
            return true;
        }
        return false;
    }

    @Override
    public boolean clearEpicList() {
        if (!epicList.isEmpty()) {
            for (Epic epic : epicList.values()) {
                for (Integer code : epic.getSubtaskCodes()) {
                    subtaskList.remove(code);
                    historyManager.removeFromHistoryList(code);
                }
            }

            for (Integer id : epicList.keySet()) {
                historyManager.removeFromHistoryList(id);
            }
            epicList.clear();
            return true;
        }
        return false;
    }

    @Override
    public boolean clearSubtaskList() {
        if (!subtaskList.isEmpty() && !epicList.isEmpty()) {
            for (Epic epic : epicList.values()) {
                epic.getSubtaskCodes().clear();
                refreshEpicStatus(epic);
            }

            for (Integer id : subtaskList.keySet()) {
                historyManager.removeFromHistoryList(id);
            }
            subtaskList.clear();
            return true;
        }
        return false;
    }

    @Override
    public Task getTaskFromList(int id) {
        if (!taskList.isEmpty() && taskList.containsKey(id)) {
            Task task = taskList.get(id);
            historyManager.addInHistoryList(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic getEpicFromList(int id) {
        if (!epicList.isEmpty() && epicList.containsKey(id)) {
            Epic epic = epicList.get(id);
            historyManager.addInHistoryList(epic);
            return epic;
        }
        return null;
    }

    @Override
    public Subtask getSubtaskFromList(int id) {
        if (!subtaskList.isEmpty() && subtaskList.containsKey(id)) {
            Subtask subtask = subtaskList.get(id);
            historyManager.addInHistoryList(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public boolean deleteTask(int id) {
        if (!taskList.isEmpty() && taskList.containsKey(id)) {
            taskList.remove(id);
            historyManager.removeFromHistoryList(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpic(int id) {
        if (!epicList.isEmpty() && epicList.containsKey(id)) {
            List<Integer> codes = epicList.get(id).getSubtaskCodes();
            for (Integer code : codes) {
                subtaskList.remove(code);
                historyManager.removeFromHistoryList(code);
            }
            epicList.remove(id);
            historyManager.removeFromHistoryList(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSubtask(int id) {
        if (!subtaskList.isEmpty() && subtaskList.containsKey(id)) {
            Subtask subtask = subtaskList.get(id);
            if (subtask != null) {
                Epic epic = epicList.get(subtask.getEpicId());
                if (epic != null) {
                    epic.getSubtaskCodes().remove((Integer) subtask.getId());
                    subtaskList.remove(id);
                    historyManager.removeFromHistoryList(id);
                    refreshEpicStatus(epic);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Task updateTask(Task task) {
        if (task != null) {
            int id = task.getId();
            if (id > 0 && taskList.containsKey(id)) {
                taskList.put(task.getId(), task);
                return task;
            }
        }
        return null;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epic != null) {
            int id = epic.getId();
            if (id > 0 && epicList.containsKey(id)) {
                epicList.put(epic.getId(), epic);
                return epic;
            }
        }
        return null;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask != null) {
            int id = subtask.getId();
            if (id > 0 && subtaskList.containsKey(id)) {
                subtaskList.put(id, subtask);
                refreshEpicStatusBySubtask(subtask);
                return subtask;
            }
        }
        return null;
    }

    @Override
    public List<Subtask> getSubtaskByEpicId(int epicId) {
        List<Subtask> subtasksList = new ArrayList<>();
        if (!epicList.isEmpty() && epicList.containsKey(epicId)) {
            List<Integer> subtaskCodes = epicList.get(epicId).getSubtaskCodes();
            for (Integer code : subtaskCodes) {
                if (subtaskList.containsKey(code)) {
                    subtasksList.add(subtaskList.get(code));
                }
            }
        }

        return subtasksList;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getLinkedHistory() {
        return historyManager.getLinkedHistory();
    }
}
