package manage;

import enums.TaskStatuses;
import templates.Epic;
import templates.Subtask;
import templates.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int count;
    private HashMap<Integer, Task> taskList = new HashMap<>();
    private HashMap<Integer, Epic> epicList = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskList = new HashMap<>();

    public static int getCount() {
        return count+= 1;
    }

    public Task createTask(Task task) {
        if(task != null) {
            task.setId(getCount());
            taskList.put(task.getId(), task);
        }
        return task;
    }

    public Epic createEpic(Epic epic) {
        if (epic != null) {
            epic.setId(getCount());
            epicList.put(epic.getId(), epic);
        }
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        if (subtask != null) {
            subtask.setId(getCount());
            subtaskList.put(subtask.getId(), subtask);
        }
        return subtask;
    }

    public void createRelation(Epic epic, Subtask subtask) {
        epic.addSubtaskCode(subtask.getId());
        subtask.setEpicId(epic.getId());
        refreshEpicStatus(subtask);
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksArrayList = new ArrayList<>();
        if (!taskList.isEmpty()) {
            for (Integer id : taskList.keySet()) {
                tasksArrayList.add(taskList.get(id));
            }
            return tasksArrayList;
        }
        return null;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicsArrayList = new ArrayList<>();
        if (!epicList.isEmpty()) {
            for (Integer id : epicList.keySet()) {
                epicsArrayList.add(epicList.get(id));
            }
            return epicsArrayList;
        }
        return null;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtasksArrayList = new ArrayList<>();
        if (!subtaskList.isEmpty()) {
            for (Integer id : subtaskList.keySet()) {
                subtasksArrayList.add(subtaskList.get(id));
            }
            return subtasksArrayList;
        }
        return null;
    }

    public boolean clearTasksList() {
        if (!taskList.isEmpty()) {
            taskList.clear();
            return true;
        }
        return false;
    }

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

    public Task getTaskFromList(int id) {
        if (!taskList.isEmpty() && taskList.containsKey(id)) {
            return taskList.get(id);
        } else {
            return null;
        }
    }

    public Epic getEpicFromList(int id) {
        if (!epicList.isEmpty() && epicList.containsKey(id)) {
            return epicList.get(id);
        } else {
            return null;
        }
    }

    public Subtask getSubtaskFromList(int id) {
        if (!subtaskList.isEmpty() && subtaskList.containsKey(id)) {
            return subtaskList.get(id);
        } else {
            return null;
        }
    }

    public boolean deleteTask(int id) {
        if (!taskList.isEmpty() && taskList.containsKey(id)) {
            taskList.remove(id);
            return true;
        }
        return false;
    }

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

    public boolean deleteSubtask(int id) {
        if (!subtaskList.isEmpty() && subtaskList.containsKey(id)) {
            Subtask subtask = subtaskList.get(id);
            if(subtask != null) {
                Epic epic = epicList.get(subtask.getEpicId()) ;
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

    public Task updateTask(Task task) {
        if (task != null) {
            int id = task.getId();
            if (id > 0 && taskList.containsKey(id) && taskList.get(id).equals(task)) {
                taskList.put(task.getId(), task);
                return task;
            }
        }

        return null;
    }

    public Epic updateEpic(Epic epic) {
        if (epic != null ) {
            int id = epic.getId();
            if (id > 0 && epicList.containsKey(id) && epicList.get(id).equals(epic)) {
                epicList.put(epic.getId(), epic);
                return epic;
            }
        }

        return null;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if(subtask != null) {
            int id = subtask.getId();
            if (id > 0 && subtaskList.containsKey(id) && subtaskList.get(id).equals(subtask)) {
                subtaskList.put(id, subtask);
                refreshEpicStatus(subtask);
                return subtask;
            }
        }

        return null;
    }

    public ArrayList<Subtask> getSubtaskByEpic (Epic epic) {
        ArrayList<Subtask> subtasksArrayList = new ArrayList<>();
        if(epic != null) {
            for (Integer id : epic.getSubtaskCodes()) {
                subtasksArrayList.add(subtaskList.get(id));
            }
        }
        return subtasksArrayList;
    }

    private void refreshEpicStatus(Subtask subtask) {
        refreshStatus(subtask.getEpicId());
    }

    private void refreshEpicStatus(Epic epic) {
        refreshStatus(epic.getId());
    }

    private void refreshStatus(int epicId) {
        Epic epic = getEpicFromList(epicId);
        if (epic != null) {
            updateEpic(new Epic(epic.getTitle(), epic.getDescription(), epic.getId()
                            , calculateEpicStatus(getSubtaskStatusesList(epic)), epic.getSubtaskCodes()));
        }
    }

    private ArrayList<TaskStatuses> getSubtaskStatusesList(Epic epic) {
        ArrayList<Subtask> subtaskArrayList = getSubtaskByEpic(epic);
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
        if(!statuses.isEmpty()) {
            if (!statuses.contains(TaskStatuses.DONE) && !statuses.contains(TaskStatuses.IN_PROGRESS)){
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
