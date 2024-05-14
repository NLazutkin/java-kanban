package manage;

import templates.Epic;
import templates.Subtask;
import templates.Task;

import java.util.ArrayList;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    void createRelation(Epic epic, Subtask subtask);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

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

    ArrayList<Subtask> getSubtaskByEpicId(int id);

    ArrayList<Task> getHistory();
}
