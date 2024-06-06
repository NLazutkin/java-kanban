package test;

import enums.TaskStatuses;
import manage.Managers;
import manage.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Epic;
import templates.Subtask;
import templates.Task;
import static org.junit.jupiter.api.Assertions.*;

class EpicTests {
    private static TaskManager taskManager;
    private static Epic epic;
    private static Subtask subtask;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();

        epic = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));
        subtask = taskManager.createSubtask(new Subtask("Подзадача 1", "Эпик 1"));
    }

    @Test
    void checkNewEpic() {
        Epic savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(epic, "Эпик не создан");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final var epics = taskManager.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void isRelationCreated() {
        assertEquals(0, epic.getSubtaskCodes().size(), "Список уже заполнен!");
        assertEquals(0, subtask.getEpicId(), "Найдена ссылка на Эпик до создания связи");

        taskManager.createRelation(epic, subtask);

        assertEquals(1, epic.getSubtaskCodes().size(), "Список подзадач пуст!");
        assertEquals(epic.getSubtaskCodes().getFirst(), subtask.getId(), "Отсутствует связь Эпик -> Подзадача");
        assertEquals(subtask.getEpicId(), epic.getId(), "Отсутствует связь Подзадача -> Эпик");
    }

    @Test
    void isEpicContentedToThemSelf() {
        assertEquals(0, epic.getSubtaskCodes().size(), "Список уже заполнен!");

        epic.addSubtaskCode(epic.getId());

        assertEquals(0, epic.getSubtaskCodes().size(), "Связь Эпик -> Эпик запрещена");
    }

    @Test
    void checkDeleteEpic() {
        assertNotNull(epic, "Эпик не найден");
        assertNotNull(subtask, "Подзадача не найдена");

        taskManager.createRelation(epic, subtask);
        int epicId = epic.getId();
        int subtaskId = subtask.getId();
        assertTrue(taskManager.deleteEpic(epic.getId()), "Ошибка удаления Эпика");

        assertNull(taskManager.getEpicFromList(epicId), "Эпик найден, ошибка удаления");
        assertNull(taskManager.getSubtaskFromList(subtaskId), "Подзадача найдена, ошибка удаления");
    }

    @Test
    void checkClearEpics() {
        taskManager.createRelation(epic, subtask);

        final var epics = taskManager.getEpics();
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Список Эпиков перед удалением пуст");

        final var subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(1, subtasks.size(), "Список Подзадач перед удалением пуст");

        assertTrue(taskManager.clearEpicList(), "Ошибка очистки списка Эпиков и их подзадач");

        assertNull(taskManager.getEpics(), "Список Эпиков не пуст, ошибка удаления");
        assertNull(taskManager.getSubtasks(), "Список Подзадачи не пуст, ошибка удаления");
    }

    @Test
    void checkUpdateEpic() {
        final Task savedEpic = taskManager.getEpicFromList(epic.getId());
        assertNotNull(savedEpic, "Эпик не найдена");
        assertEquals(epic, savedEpic, "Эпики не совпадают");

        assertNotNull(taskManager.updateEpic(new Epic("Эпик 1.1",
                                                    "Эпик 1.1",
                                                    epic.getId(),
                                                    TaskStatuses.IN_PROGRESS,
                                                    epic.getSubtaskCodes())),
                    "Ошибка обновления Эпика");

        final Task updatedEpic = taskManager.getEpicFromList(savedEpic.getId());

        assertNotNull(updatedEpic, "Эпик не обновлен");
        assertEquals(savedEpic.getId(), updatedEpic.getId(), "ID Эпика до обновления и после не равны");
        assertNotEquals(savedEpic, updatedEpic, "Содержимое Эпика не обновлено");
    }
}
