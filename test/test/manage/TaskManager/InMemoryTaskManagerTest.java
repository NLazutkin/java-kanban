package test.manage.TaskManager;

import enums.TaskStatuses;
import exceptions.ManagerSaveException;
import manage.Managers;
import manage.TaskManager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Epic;
import templates.Subtask;
import templates.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private static TaskManager taskManager;
    Task task1;
    Task task2;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        final String taskTitle = "Задача ";
        final String epicTitle = "Эпик ";
        final String subtaskTitle = "Подзадача ";
        final String subtask1Description = "Эпик 1";

        task1 = taskManager.createTask(new Task(taskTitle + "1", taskTitle + "1", 10,
                LocalDateTime.of(2024, 7, 2, 7, 0, 0)));
        task2 = taskManager.createTask(new Task(taskTitle + "2", taskTitle + "2", 10,
                LocalDateTime.of(2024, 7, 2, 10, 0, 0)));
        epic = taskManager.createEpic(new Epic(epicTitle + "1", epicTitle + "1"));
        subtask1 = taskManager.createSubtask(new Subtask(subtaskTitle + "1", subtask1Description, epic.getId(), 5,
                LocalDateTime.of(2024, 7, 2, 9, 0, 0)));
        subtask2 = taskManager.createSubtask(new Subtask(subtaskTitle + "2", subtask1Description, epic.getId(), 5,
                LocalDateTime.of(2024, 7, 2, 9, 6, 0)));
    }

    @Test
    void isPrioritizedSetFilledByTasks() {
        List<Task> prioritizedList = taskManager.getPrioritizedTasks();

        assertEquals(5, prioritizedList.size(), "Список отсортированных по приоритету задач не заполнен!");
    }

    @Test
    void isPrioritizedSetSorted() {
        List<Task> prioritizedList = taskManager.getPrioritizedTasks();

        assertEquals(task1, prioritizedList.getFirst(), "Ошибка сортировки по приоритету(первый элемент)");
        assertEquals(task2, prioritizedList.getLast(), "Ошибка сортировки по приоритету!(последний элемент)");
    }

    @Test
    void isEpicChangeStartTimeWhenUpdateSubtask() {
        List<Task> prioritizedList = taskManager.getPrioritizedTasks();
        assertEquals(subtask1.getStartTime(), prioritizedList.get(1).getStartTime(), "Время начала жизни Эпика " +
                "не совпадает с минимальной Подзадачей, до обновления!");

        Subtask updatedSubtask = taskManager.updateSubtask(new Subtask(subtask1.getTitle(),
                subtask1.getDescription(),
                subtask1.getId(),
                TaskStatuses.IN_PROGRESS,
                subtask1.getEpicId(),
                subtask1.getDurationToMinutes(),
                subtask1.getStartTime().minusMinutes(8)));

        prioritizedList = taskManager.getPrioritizedTasks();
        assertEquals(updatedSubtask.getStartTime(), prioritizedList.get(1).getStartTime(), "Время начала жизни Эпика " +
                "не совпадает с минимальной Подзадачей, после обновления!");
    }

    @Test
    void isEpicChangeEndTimeWhenUpdateSubtask() {
        List<Task> prioritizedList = taskManager.getPrioritizedTasks();
        assertEquals(subtask2.getEndTime(), prioritizedList.get(1).getEndTime(), "Время окончания жизни Эпика " +
                "не совпадает с максимальной Подзадачей, до обновления!");

        Subtask updatedSubtask = taskManager.updateSubtask(new Subtask(subtask2.getTitle(),
                subtask2.getDescription(),
                subtask2.getId(),
                TaskStatuses.IN_PROGRESS,
                subtask2.getEpicId(),
                subtask2.getDuration().plusMinutes(5).toMinutes(),
                subtask2.getStartTime()));

        prioritizedList = taskManager.getPrioritizedTasks();
        assertEquals(updatedSubtask.getEndTime(), prioritizedList.get(1).getEndTime(), "Время начала жизни Эпика " +
                "не совпадает с максимальной Подзадачей, после обновления!");
    }

    @Test
    void isTaskCrossByLeftOffset() {
        Task taskNew = new Task("Задача смещение влево", "Задача смещение влево", 10,
                LocalDateTime.of(2024, 7, 2, 6, 55, 0));

        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(taskNew),
                "Ошибка! Добавлена задача со смещением влево!");
    }

    @Test
    void isTaskCrossRightOffset() {
        Task taskNew = new Task("Задача смещение вправо", "Задача смещение вправо", 10,
                LocalDateTime.of(2024, 7, 2, 7, 5, 0));

        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(taskNew),
                "Ошибка! Добавлена задача со смещением вправо!");
    }

    @Test
    void isTaskCrossCoverage() {
        Task taskNew = new Task("Задача смещение вправо", "Задача смещение вправо", 20,
                LocalDateTime.of(2024, 7, 2, 6, 55, 0));

        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(taskNew),
                "Ошибка! Добавлена задача с полным покрытием!");
    }

    @Test
    void isTaskInnerTask() {
        Task taskNew = new Task("Задача смещение вправо", "Задача смещение вправо", 5,
                LocalDateTime.of(2024, 7, 2, 7, 2, 0));

        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(taskNew),
                "Ошибка! Добавлена задача внутрь периода жизни другой задачи!");
    }

    @Test
    void isTaskNotCrossAnotherTask() {
        Task taskNew = new Task("Задача смещение вправо", "Задача смещение вправо", 10,
                LocalDateTime.of(2024, 7, 2, 8, 0, 0));


        assertDoesNotThrow(() -> taskManager.createTask(taskNew), "Ошибка! Добавлена задача внутрь периода " +
                "жизни другой задачи!");

        List<Task> prioritizedList = taskManager.getPrioritizedTasks();
        assertEquals(6, prioritizedList.size(), "Задача не добавлена в список отсортированных " +
                "по приоритету задач!");
    }
}
