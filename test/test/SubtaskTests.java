package test;

import enums.TaskStatuses;
import manage.Managers;
import manage.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Epic;
import templates.Subtask;
import templates.Task;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTests {
    private static TaskManager taskManager;
    private static Epic epic;
    private static Subtask subtask_1;
    private static Subtask subtask_2;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();

        epic = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));
        subtask_1 = taskManager.createSubtask(new Subtask("Подзадача 1", "Эпик 1"));
        subtask_2 = taskManager.createSubtask(new Subtask("Подзадача 2", "Эпик 1"));
    }

    @Test
    void checkNewSubtask() {
        Subtask savedSubtask = taskManager.getSubtaskFromList(subtask_1.getId());

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask_1, savedSubtask, "Подзадача не совпадают.");

        final ArrayList<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество Подзадач");
        assertEquals(subtask_1, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    void isRelationCreated() {
        assertEquals(0, epic.getSubtaskCodes().size(), "Список уже заполнен!");
        assertEquals(0, subtask_1.getEpicId(), "Найдена ссылка на Эпик до создания связи");

        taskManager.createRelation(epic, subtask_1);

        assertEquals(1, epic.getSubtaskCodes().size(), "Список подзадач пуст!");
        assertEquals(epic.getSubtaskCodes().getFirst(), subtask_1.getId(), "Отсутствует связь Эпик -> Подзадача");
        assertEquals(subtask_1.getEpicId(), epic.getId(), "Отсутствует связь Подзадача -> Эпик");
    }

    @Test
    void isSubtaskContentedToThemSelf() {
        assertEquals(0, subtask_1.getEpicId(), "Найдена ссылка на Эпик до создания связи");

        subtask_1.setEpicId(subtask_1.getId());

        assertEquals(0, subtask_1.getEpicId(), "Cвязь Подзадача -> Подзадача запрещена");
    }

    @Test
    void checkDeleteSubtask() {
        assertNotNull(subtask_1, "Подзадача не найдена");

        taskManager.createRelation(epic, subtask_1);

        int subtaskId = subtask_1.getId();
        assertTrue(taskManager.deleteSubtask(subtask_1.getId()), "Ошибка удаления Подзадачи");

        assertNull(taskManager.getSubtaskFromList(subtaskId), "Подзадача найдена, ошибка удаления");
        assertEquals(0, epic.getSubtaskCodes().size(), "Связь Эпик -> Подзадача не удалена");
    }

    @Test
    void checkDeleteSubtaskAndRefreshEpicStatusDONE() {
        assertNotNull(subtask_1, "Подзадача не найдена");
        assertNotNull(subtask_2, "Подзадача не найдена");

        assertEquals(TaskStatuses.NEW, epic.getStatus(), "Статус созданного Эпика не равен" + TaskStatuses.NEW);

        taskManager.createRelation(epic, subtask_1);
        taskManager.createRelation(epic, subtask_2);

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask_1.getId(),
                        TaskStatuses.IN_PROGRESS,
                        subtask_1.getEpicId()))
                , "Ошибка обновления Подзадачи");

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask_2.getId(),
                        TaskStatuses.DONE,
                        subtask_2.getEpicId()))
                , "Ошибка обновления Подзадачи");

        int subtaskId = subtask_1.getId();
        assertTrue(taskManager.deleteSubtask(subtask_1.getId()), "Ошибка удаления Подзадачи");

        assertNull(taskManager.getSubtaskFromList(subtaskId), "Подзадача найдена, ошибка удаления");
        assertEquals(1, epic.getSubtaskCodes().size(), "Связь Эпик -> Подзадача не удалена");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");

        assertEquals(TaskStatuses.DONE, savedEpic.getStatus(), "Статус Эпика не изменен");
    }

    @Test
    void checkDeleteSubtaskAndRefreshEpicStatusINPROGRESS() {
        assertNotNull(subtask_1, "Подзадача не найдена");
        assertNotNull(subtask_2, "Подзадача не найдена");

        assertEquals(TaskStatuses.NEW, epic.getStatus(),"Статус созданного Эпика не равен" + TaskStatuses.NEW);

        taskManager.createRelation(epic, subtask_1);
        taskManager.createRelation(epic, subtask_2);

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask_1.getId(),
                        TaskStatuses.IN_PROGRESS,
                        subtask_1.getEpicId()))
                , "Ошибка обновления Подзадачи");

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask_2.getId(),
                        TaskStatuses.DONE,
                        subtask_2.getEpicId()))
                , "Ошибка обновления Подзадачи");

        int subtaskId = subtask_2.getId();
        assertTrue(taskManager.deleteSubtask(subtask_2.getId()), "Ошибка удаления Подзадачи");

        assertNull(taskManager.getSubtaskFromList(subtaskId), "Подзадача найдена, ошибка удаления");
        assertEquals(1, epic.getSubtaskCodes().size(), "Связь Эпик -> Подзадача не удалена");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");

        assertEquals(TaskStatuses.IN_PROGRESS, savedEpic.getStatus(),"Статус Эпика не изменен");
    }

    @Test
    void checkDelete2SubtaskAndRefreshEpicStatusNEW() {
        assertNotNull(subtask_1, "Подзадача не найдена");
        assertNotNull(subtask_2, "Подзадача не найдена");

        assertEquals(TaskStatuses.NEW, epic.getStatus(), "Статус созданного Эпика не равен" + TaskStatuses.NEW);

        taskManager.createRelation(epic, subtask_1);
        taskManager.createRelation(epic, subtask_2);

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask_1.getId(),
                        TaskStatuses.IN_PROGRESS,
                        subtask_1.getEpicId()))
                , "Ошибка обновления Подзадачи");

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask_2.getId(),
                        TaskStatuses.DONE,
                        subtask_2.getEpicId()))
                , "Ошибка обновления Подзадачи");


        int subtask_1_Id = subtask_2.getId();
        int subtask_2_Id = subtask_2.getId();
        assertTrue(taskManager.deleteSubtask(subtask_1.getId()), "Ошибка удаления Подзадачи");
        assertTrue(taskManager.deleteSubtask(subtask_2.getId()), "Ошибка удаления Подзадачи");

        assertNull(taskManager.getSubtaskFromList(subtask_1_Id), "Подзадача найдена, ошибка удаления");
        assertNull(taskManager.getSubtaskFromList(subtask_2_Id), "Подзадача найдена, ошибка удаления");
        assertEquals(0, epic.getSubtaskCodes().size(), "Связь Эпик -> Подзадача не удалена");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");

        assertEquals(TaskStatuses.NEW, savedEpic.getStatus(), "Статус Эпика не изменен");
    }

    @Test
    void checkClearSubtasksAndRefreshEpicStatuses() {
        taskManager.createRelation(epic, subtask_1);
        taskManager.createRelation(epic, subtask_2);

        final ArrayList<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(2, subtasks.size(), "Список Подзадач перед удалением пуст");

        assertTrue(taskManager.clearSubtaskList(), "Ошибка удаления Подзадачи");

        assertNull(taskManager.getSubtasks(), "Список Подзадачи не пуст, ошибка удаления");

        final ArrayList<Epic> epics = taskManager.getEpics();
        for (Epic epic : epics) {
            assertEquals(0, epic.getSubtaskCodes().size(), "Список Подзадач Эпика id = " + epic.getId()
                        + " title = " + epic.getTitle() + " не очищен!");

            assertEquals(TaskStatuses.NEW, epic.getStatus(), "Статус Эпика не изменен");
        }
    }

    @Test
    void checkUpdateSubtask() {
        assertNotNull(subtask_1, "Подзадача не найдена");

        taskManager.createRelation(epic, subtask_1);

        final Task savedSubtask = taskManager.getSubtaskFromList(subtask_1.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask_1, savedSubtask, "Подзадачи не совпадают");

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                                                        "Эпик 1",
                                                                  subtask_1.getId(),
                                                                  TaskStatuses.IN_PROGRESS,
                                                                  subtask_1.getEpicId()))
                        , "Ошибка обновления Подзадачи");

        final Task updatedSubtask = taskManager.getSubtaskFromList(savedSubtask.getId());

        assertNotNull(updatedSubtask, "Подзадача не обновлен");
        assertEquals(savedSubtask.getId(), updatedSubtask.getId(), "ID Подзадачи до обновления и после не равны");
        assertNotEquals(savedSubtask, updatedSubtask, "Содержимое Подзадачи не обновлено");
    }

    @Test
    void shouldEpicStatusINPROGRESSWhenUpdateSubtask() {
        assertNotNull(subtask_1, "Подзадача не найдена");

        taskManager.createRelation(epic, subtask_1);

        final Task savedSubtask = taskManager.getSubtaskFromList(subtask_1.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(TaskStatuses.NEW, epic.getStatus(),"Статус созданного Эпика не равен" + TaskStatuses.NEW);

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask_1.getId(),
                        TaskStatuses.IN_PROGRESS,
                        subtask_1.getEpicId()))
                , "Ошибка обновления Подзадачи");

        final Task updatedSubtask = taskManager.getSubtaskFromList(savedSubtask.getId());
        assertNotNull(updatedSubtask, "Подзадача не обновлен");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");

        assertEquals(TaskStatuses.IN_PROGRESS, savedEpic.getStatus(), "Статус Эпика не изменен");
    }

    @Test
    void shouldEpicStatusDONEWhenUpdateSubtask() {
        assertNotNull(subtask_1, "Подзадача не найдена");

        taskManager.createRelation(epic, subtask_1);

        final Task savedSubtask = taskManager.getSubtaskFromList(subtask_1.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(TaskStatuses.NEW, epic.getStatus(),"Статус созданного Эпика не равен" + TaskStatuses.NEW);

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask_1.getId(),
                        TaskStatuses.DONE,
                        subtask_1.getEpicId()))
                , "Ошибка обновления Подзадачи");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");

        assertEquals(TaskStatuses.DONE, savedEpic.getStatus(), "Статус Эпика не изменен");
    }

    @Test
    void shouldEpicStatusDONEWhenUpdate2Subtask() {
        assertNotNull(subtask_1, "Подзадача не найдена");
        assertNotNull(subtask_2, "Подзадача не найдена");

        taskManager.createRelation(epic, subtask_1);
        taskManager.createRelation(epic, subtask_2);

        assertEquals(TaskStatuses.NEW, epic.getStatus(), "Статус созданного Эпика не равен" + TaskStatuses.NEW);

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask_1.getId(),
                        TaskStatuses.DONE,
                        subtask_1.getEpicId()))
                , "Ошибка обновления Подзадачи");

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask_2.getId(),
                        TaskStatuses.DONE,
                        subtask_2.getEpicId()))
                , "Ошибка обновления Подзадачи");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");

        assertEquals(TaskStatuses.DONE, savedEpic.getStatus(), "Статус Эпика не изменен");
    }

    @Test
    void shouldEpicStatusINPROGRESSWhenUpdate2Subtask() {
        assertNotNull(subtask_1, "Подзадача не найдена");
        assertNotNull(subtask_2, "Подзадача не найдена");

        taskManager.createRelation(epic, subtask_1);
        taskManager.createRelation(epic, subtask_2);

        assertEquals(TaskStatuses.NEW, epic.getStatus(), "Статус созданного Эпика не равен" + TaskStatuses.NEW);

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask_1.getId(),
                        TaskStatuses.DONE,
                        subtask_1.getEpicId()))
                , "Ошибка обновления Подзадачи");

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask_2.getId(),
                        TaskStatuses.IN_PROGRESS,
                        subtask_2.getEpicId()))
                , "Ошибка обновления Подзадачи");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");

        assertEquals(TaskStatuses.IN_PROGRESS, savedEpic.getStatus(), "Статус Эпика не изменен");
    }
}

