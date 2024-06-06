package test;

import manage.Managers;
import manage.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Epic;
import templates.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HistoryManagerTest {
    private static TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void checkAddTasksToHistoryManager() {
        Task task = taskManager.createTask(new Task("Задача 1", "Задача 1"));
        Epic epic = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));

        assertEquals(0, taskManager.getHistory().size(), "История вызовов задач, "
                + " не пуста перед запуском!");

        final Task savedTask_1 = taskManager.getTaskFromList(task.getId());
        assertEquals(1, taskManager.getHistory().size(), "В истории задач не 1 запись");

        final Epic savedEpic_1 = taskManager.getEpicFromList(epic.getId());
        assertEquals(2, taskManager.getHistory().size(), "В истории задач не 2 записи");
    }

    @Test
    void checkAddTasksToFirstAndLastNodesOfHistoryManager() {
        Task task_1 = taskManager.createTask(new Task("Задача 1", "Задача 1"));
        Task task_2 = taskManager.createTask(new Task("Задача 2", "Задача 2"));
        Epic epic = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));

        assertEquals(0, taskManager.getHistory().size(), "История вызовов задач, "
                +" не пуста перед запуском!");

        final Task savedTask_1 = taskManager.getTaskFromList(task_1.getId());
        final Task savedTask_2 = taskManager.getTaskFromList(task_2.getId());
        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());

        List<Task> taskList = taskManager.getHistory();
        Task firstTask = taskList.getFirst();
        Task lastTask = taskList.getLast();

        assertEquals(savedTask_1, firstTask, "Первый элемент в списке сохранен некорректно!");
        assertEquals(savedEpic, lastTask, "Последний элемент в списке сохранен некорректно!");
    }

    @Test
    void checkDeleteTasksFormMiddleOfHistoryManagerList() {
        Task task_1 = taskManager.createTask(new Task("Задача 1", "Задача 1"));
        Task task_2 = taskManager.createTask(new Task("Задача 2", "Задача 2"));
        Epic epic = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));

        assertEquals(0, taskManager.getHistory().size(), "История вызовов задач, "
                +" не пуста перед запуском!");

        final Task savedTask_1 = taskManager.getTaskFromList(task_1.getId());
        final Task savedTask_2 = taskManager.getTaskFromList(task_2.getId());
        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());

        List<Task> taskList = taskManager.getHistory();

        assertEquals(3, taskList.size(), "В истории задач не 3 записи");

        taskManager.deleteTask(savedTask_2.getId());

        assertNotEquals(savedTask_2, taskManager.getTaskFromList(savedTask_2.getId()),
                "Элемент не удален из списка!");

        List<Task> taskList_new = taskManager.getHistory();
        Task middleTask_new = taskList_new.get(1);

        assertNotEquals(savedTask_2, middleTask_new, "Элемент не удален из истории!");
    }
}
