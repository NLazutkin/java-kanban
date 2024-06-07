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

        final Task savedTask1 = taskManager.getTaskFromList(task.getId());
        assertEquals(1, taskManager.getHistory().size(), "В истории задач не 1 запись");

        final Epic savedEpic1 = taskManager.getEpicFromList(epic.getId());
        assertEquals(2, taskManager.getHistory().size(), "В истории задач не 2 записи");
    }

    @Test
    void checkAddTasksToFirstAndLastNodesOfHistoryManager() {
        Task task1 = taskManager.createTask(new Task("Задача 1", "Задача 1"));
        Task task2 = taskManager.createTask(new Task("Задача 2", "Задача 2"));
        Epic epic = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));

        assertEquals(0, taskManager.getHistory().size(), "История вызовов задач, "
                +" не пуста перед запуском!");

        final Task savedTask1 = taskManager.getTaskFromList(task1.getId());
        final Task savedTask2 = taskManager.getTaskFromList(task2.getId());
        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());

        List<Task> taskList = taskManager.getHistory();
        Task firstTask = taskList.getFirst();
        Task lastTask = taskList.getLast();

        assertEquals(savedTask1, firstTask, "Первый элемент в списке сохранен некорректно!");
        assertEquals(savedEpic, lastTask, "Последний элемент в списке сохранен некорректно!");
    }

    @Test
    void checkDeleteTasksFormMiddleOfHistoryManagerList() {
        Task task1 = taskManager.createTask(new Task("Задача 1", "Задача 1"));
        Task task2 = taskManager.createTask(new Task("Задача 2", "Задача 2"));
        Epic epic = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));

        assertEquals(0, taskManager.getHistory().size(), "История вызовов задач, "
                +" не пуста перед запуском!");

        final Task savedTask1 = taskManager.getTaskFromList(task1.getId());
        final Task savedTask2 = taskManager.getTaskFromList(task2.getId());
        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());

        List<Task> taskList = taskManager.getHistory();

        assertEquals(3, taskList.size(), "В истории задач не 3 записи");

        taskManager.deleteTask(savedTask2.getId());

        assertNotEquals(savedTask2, taskManager.getTaskFromList(savedTask2.getId()),
                "Элемент не удален из списка!");

        List<Task> taskListNew = taskManager.getHistory();
        Task middleTaskNew = taskListNew.get(1);

        assertNotEquals(savedTask2, middleTaskNew, "Элемент не удален из истории!");
    }
}
