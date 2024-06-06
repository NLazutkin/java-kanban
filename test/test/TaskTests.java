package test;

import enums.TaskStatuses;
import manage.Managers;
import manage.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Task;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTests {
    private static TaskManager taskManager;
    private static Task task;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();

        task = taskManager.createTask(new Task("Задача 1", "Задача 1"));
    }

    @Test
    void checkNewTask() {
        Task savedTask = taskManager.getTaskFromList(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        final var tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают");
    }

    @Test
    void checkDeleteTask() {
        assertNotNull(task, "Задача не найдена");

        int taskId = task.getId();
        assertTrue(taskManager.deleteTask(task.getId()), "Ошибка удаления Задачи");

        assertNull(taskManager.getTaskFromList(taskId), "Задача найдена, ошибка удаления");
    }

    @Test
    void checkClearTasks() {
        final var tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Список задач перед удалением пуст");

        assertTrue(taskManager.clearTasksList(), "Ошибка очистки списка Задач");

        assertNull(taskManager.getTasks(), "Список задач не пуст, ошибка удаления");
    }

    @Test
    void checkUpdateTask() {
        final Task savedTask = taskManager.getTaskFromList(task.getId());
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        assertNotNull(taskManager.updateTask(new Task("Задача 1.1", "Задача 1.1", task.getId(), TaskStatuses.IN_PROGRESS))
                        ,"Ошибка обновления задачи");

        final Task updatedTask = taskManager.getTaskFromList(savedTask.getId());

        assertNotNull(updatedTask, "Задача не обновлена");
        assertEquals(savedTask.getId(), updatedTask.getId(), "ID Задачи до обновления и после не равны");
        assertNotEquals(savedTask, updatedTask, "Содержимое задачи не обновлено");
    }

}
