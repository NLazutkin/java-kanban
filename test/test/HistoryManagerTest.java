package test;

import manage.Managers;
import manage.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Epic;
import templates.Subtask;
import templates.Task;

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
                +" не пуста перед запуском!");

        final Task savedTask = taskManager.getTaskFromList(task.getId());
        assertEquals(1, taskManager.getHistory().size(), "В истории задач не 1 запись");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertEquals(2, taskManager.getHistory().size(), "В истории задач не 2 записи");
    }

    @Test
    void checkAddValueOutOfLimitInHistory() {
        assertEquals(0, taskManager.getHistory().size(), "История вызовов задач, "
                +" не пуста перед запуском!");

        Task task_1 = taskManager.createTask(new Task("Задача 1", "Задача 1"));
        Task task_2 = taskManager.createTask(new Task("Задача 2", "Задача 2"));
        Task task_3 = taskManager.createTask(new Task("Задача 3", "Задача 3"));
        Epic epic_1 = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));
        Epic epic_2 = taskManager.createEpic(new Epic("Эпик 2", "Эпик 2"));
        Subtask subtask_1 = taskManager.createSubtask(new Subtask("Сабтаск 1", "Эпик 1"));
        Subtask subtask_2 = taskManager.createSubtask(new Subtask("Сабтаск 2", "Эпик 1"));
        Subtask subtask_3 = taskManager.createSubtask(new Subtask("Сабтаск 3", "Эпик 1"));
        Subtask subtask_4 = taskManager.createSubtask(new Subtask("Сабтаск 4", "Эпик 1"));
        Subtask subtask_5 = taskManager.createSubtask(new Subtask("Сабтаск 5", "Эпик 2"));
        Subtask subtask_6 = taskManager.createSubtask(new Subtask("Сабтаск 6", "Эпик 2"));
        taskManager.createRelation(epic_1, subtask_1);
        taskManager.createRelation(epic_1, subtask_2);
        taskManager.createRelation(epic_1, subtask_3);
        taskManager.createRelation(epic_2, subtask_4);
        taskManager.createRelation(epic_2, subtask_5);
        taskManager.createRelation(epic_2, subtask_6);

        task_1 = taskManager.getTaskFromList(1);
        task_2 = taskManager.getTaskFromList(2);
        task_3 = taskManager.getTaskFromList(3);
        epic_1 = taskManager.getEpicFromList(4);
        epic_2 = taskManager.getEpicFromList(5);
        subtask_1 = taskManager.getSubtaskFromList(6);
        subtask_2 = taskManager.getSubtaskFromList(7);
        subtask_3 = taskManager.getSubtaskFromList(8);
        subtask_4 = taskManager.getSubtaskFromList(9);
        subtask_5 = taskManager.getSubtaskFromList(10);
        assertEquals(task_1.getId(), taskManager.getHistory().getFirst().getId(), "Первый элемент списка истории "
                + "задач, изменился до превышения лимита");

        subtask_6 = taskManager.getSubtaskFromList(11);
        assertNotEquals(task_1.getId(), taskManager.getHistory().getFirst().getId(), "Первый элемент списка истории "
                + "задач не изменился после превышения лимита");

        assertEquals(task_2.getId(), taskManager.getHistory().getFirst().getId(), "Первый элемент списка истории "
                + "задач не корректно изменился после превышения лимита");
    }
}
