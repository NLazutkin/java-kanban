package manage;

import templates.Epic;
import templates.Subtask;
import templates.Task;

import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    void createRelation(Epic epic, Subtask subtask);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    boolean clearTasksList();

    boolean clearEpicList();

    boolean clearSubtaskList();

    Task getTaskFromList(int id);

    Epic getEpicFromList(int id);

    Subtask getSubtaskFromList(int id);

    boolean deleteTask(int id);

    boolean deleteEpic(int id);

    boolean deleteSubtask(int id);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    Subtask updateSubtask(Subtask subtask);

    List<Subtask> getSubtaskByEpicId(int id);

    List<Task> getHistory();
}
