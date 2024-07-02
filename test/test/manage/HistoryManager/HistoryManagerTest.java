package test.manage.HistoryManager;

import manage.Managers;
import manage.TaskManager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Epic;
import templates.Task;

import java.time.LocalDateTime;
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
        Task task = taskManager.createTask(new Task("Задача 1", "Задача 1", 5, LocalDateTime.now()));

        assertEquals(0, taskManager.getHistory().size(), "История вызовов задач, "
                + " не пуста перед запуском!");

        final Task savedTask1 = taskManager.getTaskFromList(task.getId());
        assertEquals(1, taskManager.getHistory().size(), "В истории задач не 1 запись");
    }

    @Test
    void checkAddTasksToFirstAndLastNodesOfHistoryManager() {
        Task task1 = taskManager.createTask(new Task("Задача 1", "Задача 1", 5,
                LocalDateTime.now()));
        Task task2 = taskManager.createTask(new Task("Задача 2", "Задача 2", 5,
                LocalDateTime.now().plusMinutes(6)));
        Epic epic = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));

        assertEquals(0, taskManager.getLinkedHistory().size(), "История вызовов задач, "
                + " не пуста перед запуском!");

        final Task savedTask1 = taskManager.getTaskFromList(task1.getId());
        final Task savedTask2 = taskManager.getTaskFromList(task2.getId());
        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());

        List<Task> taskList = taskManager.getLinkedHistory();
        Task firstTask = taskList.getFirst();
        Task lastTask = taskList.getLast();

        assertEquals(3, taskManager.getLinkedHistory().size(), "История вызовов задач не заполняется!");
        assertEquals(firstTask, savedTask1, "Элемент не удален из истории!");
        assertEquals(lastTask, savedEpic, "Элемент не удален из истории!");
    }

    @Test
    void checkDeleteTasksFormMiddleOfHistoryManagerList() {
        Task task1 = taskManager.createTask(new Task("Задача 1", "Задача 1", 5,
                LocalDateTime.now()));
        Task task2 = taskManager.createTask(new Task("Задача 2", "Задача 2", 5,
                LocalDateTime.now().plusMinutes(6)));
        Epic epic = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));

        assertEquals(0, taskManager.getHistory().size(), "История вызовов задач, "
                + " не пуста перед запуском!");

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
