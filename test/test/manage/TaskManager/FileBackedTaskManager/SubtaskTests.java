package test.manage.TaskManager.FileBackedTaskManager;

import enums.TaskStatuses;
import manage.Managers;
import manage.TaskManager.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Epic;
import templates.Subtask;
import templates.Task;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTests {
    private static FileBackedTaskManager taskManager;
    private static Epic epic;
    private static Subtask subtask1;
    private static Subtask subtask2;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefaultFileBacked();

        epic = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));
        subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", "Эпик 1", epic.getId()));
        subtask2 = taskManager.createSubtask(new Subtask("Подзадача 2", "Эпик 1", epic.getId()));
    }

    @Test
    void checkNewSubtask() {
        Subtask savedSubtask = taskManager.getSubtaskFromList(subtask1.getId());

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask1, savedSubtask, "Подзадача не совпадают.");

        final var subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество Подзадач");
        assertEquals(subtask1, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    void isRelationCreated() {
        assertEquals(2, epic.getSubtaskCodes().size(), "Список подзадач пуст!");
        assertEquals(epic.getSubtaskCodes().getFirst(), subtask1.getId(), "Отсутствует связь Эпик -> Подзадача");
        assertEquals(subtask1.getEpicId(), epic.getId(), "Отсутствует связь Подзадача -> Эпик");
    }

    @Test
    void isSubtaskContentedToThemSelf() {
        assertEquals(subtask1.getEpicId(), epic.getId(), "Отсутствует связь Подзадача -> Эпик");

        subtask1.setEpicId(subtask1.getId());

        assertNotEquals(subtask1.getEpicId(), subtask1.getId(), "Cвязь Подзадача -> Подзадача запрещена");
    }

    @Test
    void checkDeleteSubtask() {
        assertNotNull(subtask1, "Подзадача не найдена");

        int subtaskId = subtask1.getId();
        assertTrue(taskManager.deleteSubtask(subtask1.getId()), "Ошибка удаления Подзадачи");
        assertNull(taskManager.getSubtaskFromList(subtaskId), "Подзадача найдена, ошибка удаления");
        assertEquals(1, epic.getSubtaskCodes().size(), "Связь Эпик -> Подзадача не удалена");
    }

    void prepareDataForTestingEpicStatusWhenDeleteSubtask() {
        assertNotNull(subtask1, "Подзадача не найдена");
        assertNotNull(subtask2, "Подзадача не найдена");
        assertEquals(TaskStatuses.NEW, epic.getStatus(), "Статус созданного Эпика не равен" + TaskStatuses.NEW);

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask1.getId(),
                        TaskStatuses.IN_PROGRESS,
                        subtask1.getEpicId())),
                "Ошибка обновления Подзадачи");

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 2.1",
                        "Эпик 1",
                        subtask2.getId(),
                        TaskStatuses.DONE,
                        subtask2.getEpicId())),
                "Ошибка обновления Подзадачи");
    }

    @Test
    void checkDeleteSubtaskAndRefreshEpicStatusDONE() {
        prepareDataForTestingEpicStatusWhenDeleteSubtask();

        int subtaskId = subtask1.getId();
        assertTrue(taskManager.deleteSubtask(subtask1.getId()), "Ошибка удаления Подзадачи");

        assertNull(taskManager.getSubtaskFromList(subtaskId), "Подзадача найдена, ошибка удаления");
        assertEquals(1, epic.getSubtaskCodes().size(), "Связь Эпик -> Подзадача не удалена");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");

        assertEquals(TaskStatuses.DONE, savedEpic.getStatus(), "Статус Эпика не изменен");
    }

    @Test
    void checkDeleteSubtaskAndRefreshEpicStatusINPROGRESS() {
        prepareDataForTestingEpicStatusWhenDeleteSubtask();

        int subtaskId = subtask2.getId();
        assertTrue(taskManager.deleteSubtask(subtask2.getId()), "Ошибка удаления Подзадачи");

        assertNull(taskManager.getSubtaskFromList(subtaskId), "Подзадача найдена, ошибка удаления");
        assertEquals(1, epic.getSubtaskCodes().size(), "Связь Эпик -> Подзадача не удалена");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");

        assertEquals(TaskStatuses.IN_PROGRESS, savedEpic.getStatus(), "Статус Эпика не изменен");
    }

    @Test
    void checkDelete2SubtaskAndRefreshEpicStatusNEW() {
        prepareDataForTestingEpicStatusWhenDeleteSubtask();

        int subtask1Id = subtask2.getId();
        int subtask2Id = subtask2.getId();
        assertTrue(taskManager.deleteSubtask(subtask1.getId()), "Ошибка удаления Подзадачи");
        assertTrue(taskManager.deleteSubtask(subtask2.getId()), "Ошибка удаления Подзадачи");

        assertNull(taskManager.getSubtaskFromList(subtask1Id), "Подзадача найдена, ошибка удаления");
        assertNull(taskManager.getSubtaskFromList(subtask2Id), "Подзадача найдена, ошибка удаления");
        assertEquals(0, epic.getSubtaskCodes().size(), "Связь Эпик -> Подзадача не удалена");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");

        assertEquals(TaskStatuses.NEW, savedEpic.getStatus(), "Статус Эпика не изменен");
    }

    @Test
    void checkClearSubtasksAndRefreshEpicStatuses() {
        final var subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(2, subtasks.size(), "Список Подзадач перед удалением пуст");

        assertTrue(taskManager.clearSubtaskList(), "Ошибка удаления Подзадачи");

        assertEquals(0, taskManager.getSubtasks().size(), "Список Подзадачи не пуст, ошибка удаления");

        final var epics = taskManager.getEpics();
        for (Epic epic : epics) {
            assertEquals(0, epic.getSubtaskCodes().size(), "Список Подзадач Эпика id = "
                    + epic.getId()
                    + " title = "
                    + epic.getTitle()
                    + " не очищен!");

            assertEquals(TaskStatuses.NEW, epic.getStatus(), "Статус Эпика не изменен");
        }
    }

    @Test
    void checkUpdateSubtask() {
        assertNotNull(subtask1, "Подзадача не найдена");

        final Task savedSubtask = taskManager.getSubtaskFromList(subtask1.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(subtask1, savedSubtask, "Подзадачи не совпадают");

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask1.getId(),
                        TaskStatuses.IN_PROGRESS,
                        subtask1.getEpicId())),
                "Ошибка обновления Подзадачи");

        final Task updatedSubtask = taskManager.getSubtaskFromList(savedSubtask.getId());

        assertNotNull(updatedSubtask, "Подзадача не обновлен");
        assertEquals(savedSubtask.getId(), updatedSubtask.getId(), "ID Подзадачи до обновления "
                + "и после не равны");
        assertNotEquals(savedSubtask, updatedSubtask, "Содержимое Подзадачи не обновлено");
    }

    void prepareDataForTestingEpicStatusWhenUpdateSubtask(TaskStatuses updateTo, TaskStatuses expectedResult) {
        assertNotNull(subtask1, "Подзадача не найдена");

        final Task savedSubtask = taskManager.getSubtaskFromList(subtask1.getId());
        assertNotNull(savedSubtask, "Подзадача не найдена");
        assertEquals(TaskStatuses.NEW, epic.getStatus(), "Статус созданного Эпика не равен"
                + TaskStatuses.NEW);

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask1.getId(),
                        updateTo,
                        subtask1.getEpicId())),
                "Ошибка обновления Подзадачи");

        final Task updatedSubtask = taskManager.getSubtaskFromList(savedSubtask.getId());
        assertNotNull(updatedSubtask, "Подзадача не обновлен");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");

        assertEquals(expectedResult, savedEpic.getStatus(), "Статус Эпика не изменен");
    }

    @Test
    void shouldEpicStatusINPROGRESSWhenUpdateSubtask() {
        prepareDataForTestingEpicStatusWhenUpdateSubtask(TaskStatuses.IN_PROGRESS, TaskStatuses.IN_PROGRESS);
    }

    @Test
    void shouldEpicStatusDONEWhenUpdateSubtask() {
        prepareDataForTestingEpicStatusWhenUpdateSubtask(TaskStatuses.DONE, TaskStatuses.IN_PROGRESS);
    }

    void prepareDataForTestingEpicStatusWhenUpdate2Subtask(TaskStatuses expectedResult) {
        assertNotNull(subtask1, "Подзадача не найдена");
        assertNotNull(subtask2, "Подзадача не найдена");

        assertEquals(TaskStatuses.NEW, epic.getStatus(), "Статус созданного Эпика не равен"
                + TaskStatuses.NEW);

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask1.getId(),
                        TaskStatuses.DONE,
                        subtask1.getEpicId())),
                "Ошибка обновления Подзадачи");

        assertNotNull(taskManager.updateSubtask(new Subtask("Подзадача 1.1",
                        "Эпик 1",
                        subtask2.getId(),
                        expectedResult,
                        subtask2.getEpicId())),
                "Ошибка обновления Подзадачи");

        final Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден");

        assertEquals(expectedResult, savedEpic.getStatus(), "Статус Эпика не изменен");
    }

    @Test
    void shouldEpicStatusDONEWhenUpdate2Subtask() {
        prepareDataForTestingEpicStatusWhenUpdate2Subtask(TaskStatuses.DONE);
    }

    @Test
    void shouldEpicStatusINPROGRESSWhenUpdate2Subtask() {
        prepareDataForTestingEpicStatusWhenUpdate2Subtask(TaskStatuses.IN_PROGRESS);
    }
}

