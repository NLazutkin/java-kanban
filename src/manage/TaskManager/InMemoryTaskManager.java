package manage.TaskManager;

import enums.TaskStatuses;
import exceptions.ManagerSaveException;
import manage.HistoryManager.HistoryManager;
import manage.Managers;
import templates.Epic;
import templates.Subtask;
import templates.Task;

import java.time.Duration;
import java.time.ZoneId;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected static int idCounter = 0;
    protected final Map<Integer, Task> taskList = new HashMap<>();
    protected final Map<Integer, Epic> epicList = new HashMap<>();
    protected final Map<Integer, Subtask> subtaskList = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected Set<Task> prioritizedSet = new TreeSet<>(comparator);
    static final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);

    private boolean isCrossTask(Task task, Task prioritizedTask) {
        if (prioritizedTask instanceof Epic || prioritizedTask.getStartTime() == null || prioritizedTask.getEndTime() == null) {
            return false;
        }

        if (task.equals(prioritizedTask)
                && task.getStartTime().isEqual(prioritizedTask.getStartTime())
                && task.getEndTime().isEqual(prioritizedTask.getEndTime())) {
            return false;
        }

        // Смещение "влево"
        if (task.getStartTime().isBefore(prioritizedTask.getStartTime())
                && task.getEndTime().isAfter(prioritizedTask.getStartTime())
                && task.getEndTime().isBefore(prioritizedTask.getEndTime())) {
            return true;
        }

        // Смещение "вправо"
        if (task.getEndTime().isAfter(prioritizedTask.getEndTime())
                && task.getStartTime().isAfter(prioritizedTask.getStartTime())
                && task.getStartTime().isBefore(prioritizedTask.getEndTime())) {
            return true;
        }

        // Охват
        if (task.getStartTime().isBefore(prioritizedTask.getStartTime())
                && task.getEndTime().isAfter(prioritizedTask.getEndTime())) {
            return true;
        }

        // Включение
        if (task.getStartTime().isAfter(prioritizedTask.getStartTime())
                && task.getEndTime().isBefore(prioritizedTask.getEndTime())) {
            return true;
        }

        return false;
    }

    private Optional<Task> isNotValid(Task task) {
        return getPrioritizedTasks().stream()
                .filter(prioritizedTask -> isCrossTask(task, prioritizedTask))
                .findFirst();
    }

    private void calculateAndSaveEpicFields(Epic epic) {
        List<Subtask> subtaskList = getSubtaskByEpicId(epic.getId());
        int doneStatusCount = 0;
        int inProgressStatusCount = 0;
        int newStatusCount = 0;

        TaskStatuses epicStatus = TaskStatuses.IN_PROGRESS;
        LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.MAX_VALUE), ZoneId.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.MIN_VALUE), ZoneId.systemDefault());
        Duration duration = Duration.ZERO;

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

                duration = duration.plus(subtask.getDuration());

                if (startTime.isAfter(subtask.getStartTime())) {
                    startTime = subtask.getStartTime();
                }

                if (endTime.isBefore(subtask.getEndTime())) {
                    endTime = subtask.getEndTime();
                }
            }

            if (doneStatusCount == 0 && inProgressStatusCount == 0) {
                epicStatus = TaskStatuses.NEW;
            } else if (newStatusCount == 0 && inProgressStatusCount == 0) {
                epicStatus = TaskStatuses.DONE;
            }
        } else {
            epicStatus = TaskStatuses.NEW;
            startTime = null;
            endTime = null;
        }

        updateEpic(new Epic(epic.getTitle(), epic.getDescription(), epic.getId(), epicStatus, epic.getSubtaskCodes(),
                duration.toMinutes(), startTime, endTime));
    }

    private void refreshStatus(int epicId) {
        if (!epicList.isEmpty() && epicList.containsKey(epicId)) {
            calculateAndSaveEpicFields(epicList.get(epicId));
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
    public Task createTask(Task task) throws RuntimeException {
        task.setId(idCounter += 1);
        if (isNotValid(task).isEmpty()) {
            taskList.put(task.getId(), task);
            prioritizedSet.add(task);
            return task;
        } else {
            throw new ManagerSaveException("Задача №" + task.getId() + " '" + task.getTitle()
                    + "' пересекается с другой задачей, исправьте дату начала задачи " + task.getStartTimeToString()
                    + " или её продолжительность " + task.getDurationToMinutes() + " мин.");
        }
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(idCounter += 1);
        epicList.put(epic.getId(), epic);
        prioritizedSet.add(epic);

        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws RuntimeException {
        subtask.setId(idCounter += 1);
        if (isNotValid(subtask).isEmpty()) {
            subtaskList.put(subtask.getId(), subtask);
            createRelation(subtask);
            prioritizedSet.add(subtask);
            return subtask;
        } else {
            throw new ManagerSaveException("Подзадача №" + subtask.getId() + " '" + subtask.getTitle()
                    + "' пересекается с другой задачей, исправьте дату начала задачи " + subtask.getStartTimeToString()
                    + " или её продолжительность " + subtask.getDurationToMinutes() + " мин.");
        }
    }

    @Override
    public List<Task> getTasks() {
        return taskList.values().stream().toList();
    }

    @Override
    public List<Epic> getEpics() {
        return epicList.values().stream().toList();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return subtaskList.values().stream().toList();
    }

    @Override
    public boolean clearTasksList() {
        if (!taskList.isEmpty()) {
            for (Integer id : taskList.keySet()) {
                historyManager.removeFromHistoryList(id);
                prioritizedSet.remove(taskList.get(id));
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
                    historyManager.removeFromHistoryList(code);
                    prioritizedSet.remove(subtaskList.get(code));
                    subtaskList.remove(code);
                }
            }

            for (Integer id : epicList.keySet()) {
                historyManager.removeFromHistoryList(id);
                prioritizedSet.remove(epicList.get(id));
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
                prioritizedSet.remove(subtaskList.get(id));
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
            prioritizedSet.remove(taskList.get(id));
            historyManager.removeFromHistoryList(id);
            taskList.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpic(int id) {
        if (!epicList.isEmpty() && epicList.containsKey(id)) {
            List<Integer> codes = epicList.get(id).getSubtaskCodes();

            for (Integer code : codes) {
                historyManager.removeFromHistoryList(code);
                prioritizedSet.remove(subtaskList.get(code));
                subtaskList.remove(code);
            }

            prioritizedSet.remove(epicList.get(id));
            historyManager.removeFromHistoryList(id);
            epicList.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSubtask(int id) {
        if (!subtaskList.isEmpty() && subtaskList.containsKey(id)) {
            Subtask subtask = subtaskList.get(id);
            Epic epic = epicList.get(subtask.getEpicId());
            if (epic != null) {
                epic.getSubtaskCodes().remove((Integer) id);
                historyManager.removeFromHistoryList(id);
                prioritizedSet.remove(subtask);
                subtaskList.remove(id);
                refreshEpicStatus(epic);
                return true;
            }
        }
        return false;
    }

    @Override
    public Task updateTask(Task task) {
        int id = task.getId();
        if (taskList.containsKey(id) && isNotValid(task).isEmpty()) {
            if (task.getStartTime() != null) {
                prioritizedSet.remove(taskList.get(task.getId()));
                prioritizedSet.add(task);
            }
            taskList.put(task.getId(), task);
            return task;
        }
        return null;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        int id = epic.getId();
        if (epicList.containsKey(id)) {
            prioritizedSet.remove(epicList.get(epic.getId()));
            prioritizedSet.add(epic);
            epicList.put(epic.getId(), epic);
            return epic;
        }
        return null;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtaskList.containsKey(id) && isNotValid(subtask).isEmpty()) {
            if (subtask.getStartTime() != null) {
                prioritizedSet.remove(subtaskList.get(subtask.getId()));
                prioritizedSet.add(subtask);
            }
            subtaskList.put(id, subtask);
            refreshEpicStatusBySubtask(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public List<Subtask> getSubtaskByEpicId(int epicId) {
        if (!epicList.isEmpty() && epicList.containsKey(epicId)) {
            return epicList.get(epicId).getSubtaskCodes().stream().filter(subtaskList::containsKey).map(subtaskList::get).toList();
        }
        return new ArrayList<>();
    }

    public List<Task> getPrioritizedTasks() {
        return prioritizedSet.stream().filter(Objects::nonNull).filter(Task -> Task.getStartTime() != null).toList();
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
