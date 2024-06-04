package manage;

import enums.TaskStatuses;
import templates.Epic;
import templates.Subtask;
import templates.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private static int idCounter;
    private HashMap<Integer, Task> taskList = new HashMap<>();
    private HashMap<Integer, Epic> epicList = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskList = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public Task createTask(Task task) {
        if(task != null) {
            task.setId(idCounter += 1);
            taskList.put(task.getId(), task);
        }
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic != null) {
            epic.setId(idCounter += 1);
            epicList.put(epic.getId(), epic);
        }
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask != null) {
            subtask.setId(idCounter += 1);
            subtaskList.put(subtask.getId(), subtask);
        }
        return subtask;
    }

    @Override
    public void createRelation(Epic epic, Subtask subtask) {
        epic.addSubtaskCode(subtask.getId());
        subtask.setEpicId(epic.getId());
        refreshEpicStatusBySubtask(subtask);
    }

    @Override
    public ArrayList<Task> getTasks() {
        if (!taskList.isEmpty()) {
            return new ArrayList<>(taskList.values());
        }
        return null;
    }

    @Override
    public ArrayList<Epic> getEpics() {
        if (!epicList.isEmpty()) {
            return new ArrayList<>(epicList.values());
        }
        return null;
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        if (!subtaskList.isEmpty()) {
            return new ArrayList<>(subtaskList.values());
        }
        return null;
    }

    @Override
    public boolean clearTasksList() {
        if (!taskList.isEmpty()) {
            taskList.clear();
            return true;
        }
        return false;
    }

    @Override
    public boolean clearEpicList() {
        if (!epicList.isEmpty()) {
            for (Epic epic : epicList.values()) {
                for (Integer code : epic.getSubtaskCodes()){
                    subtaskList.remove(code);
                }
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
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpic(int id) {
        if (!epicList.isEmpty() && epicList.containsKey(id)) {
            ArrayList<Integer> codes =  epicList.get(id).getSubtaskCodes();
            for (Integer code : codes) {
                subtaskList.remove(code);
            }
            epicList.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSubtask(int id) {
        if (!subtaskList.isEmpty() && subtaskList.containsKey(id)) {
            Subtask subtask = subtaskList.get(id);
            if(subtask != null) {
                Epic epic = epicList.get(subtask.getEpicId());
                if (epic != null) {
                    epic.getSubtaskCodes().remove((Integer) subtask.getId());
                    subtaskList.remove(id);
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
        if (epic != null ) {
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
        if(subtask != null) {
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
    public ArrayList<Subtask> getSubtaskByEpicId(int epicId) {
        ArrayList<Subtask> subtasksArrayList = new ArrayList<>();
        if(!epicList.isEmpty() && epicList.containsKey(epicId)) {
            ArrayList<Integer> subtaskCodes = epicList.get(epicId).getSubtaskCodes();
            for (Integer code : subtaskCodes) {
                subtasksArrayList.add(subtaskList.get(code));
            }
        }

        return subtasksArrayList;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void refreshEpicStatusBySubtask(Subtask subtask) {
        refreshStatus(subtask.getEpicId());
    }

    private void refreshEpicStatus(Epic epic) {
        refreshStatus(epic.getId());
    }

    private void refreshStatus(int epicId) {
        if(epicId > 0 && !epicList.isEmpty() && epicList.containsKey(epicId)) {
            Epic epic = epicList.get(epicId);
            if (epic != null) {
                updateEpic(new Epic(epic.getTitle(), epic.getDescription(), epic.getId()
                        , calculateEpicStatus(getSubtaskStatusesList(epic.getId())), epic.getSubtaskCodes()));
            }
        }
    }

    private ArrayList<TaskStatuses> getSubtaskStatusesList(int id) {
        ArrayList<Subtask> subtaskArrayList = getSubtaskByEpicId(id);
        ArrayList<TaskStatuses> statuses = new ArrayList<>();
        if (!subtaskArrayList.isEmpty()) {
            for (Subtask subtaskFromList : subtaskArrayList) {
                statuses.add(subtaskFromList.getStatus());
            }
        }
        return statuses;
    }

    private TaskStatuses calculateEpicStatus(ArrayList<TaskStatuses> statuses) {
        TaskStatuses epicStatus = TaskStatuses.IN_PROGRESS;
        if (!statuses.isEmpty()) {
            if (!statuses.contains(TaskStatuses.DONE) && !statuses.contains(TaskStatuses.IN_PROGRESS)) {
                epicStatus = TaskStatuses.NEW;
            } else if (!statuses.contains(TaskStatuses.NEW) && !statuses.contains(TaskStatuses.IN_PROGRESS)) {
                epicStatus = TaskStatuses.DONE;
            }
        } else {
            epicStatus = TaskStatuses.NEW;
        }
        return epicStatus;
    }
}
