package test.manage.TaskManager;

import enums.TaskStatuses;
import manage.TaskManager.TaskManager;
import org.junit.jupiter.api.Test;
import templates.Epic;
import templates.Subtask;
import templates.Task;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

abstract class EpicTest<T extends TaskManager> {
    T taskManager;
    private static Epic epic;
    private static Subtask subtask1;
    private static Subtask subtask2;

    void init() {
        long duration = 5;
        LocalDateTime startTime = LocalDateTime.now();
        epic = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));
        subtask1 = taskManager.createSubtask(
                new Subtask("Подзадача 1", "Эпик 1", epic.getId(), duration, startTime));
        subtask2 = taskManager.createSubtask(
                new Subtask("Подзадача 1", "Эпик 1", epic.getId(), duration, startTime.plusMinutes(6)));
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
        assertEquals(2, epic.getSubtaskCodes().size(), "Список подзадач пуст!");
        assertEquals(epic.getSubtaskCodes().getFirst(), subtask1.getId(), "Отсутствует связь Эпик -> Подзадача");
        assertEquals(subtask1.getEpicId(), epic.getId(), "Отсутствует связь Подзадача -> Эпик");
    }

    @Test
    void isEpicContentedToThemSelf() {
        assertEquals(epic.getSubtaskCodes().getFirst(), subtask1.getId(), "Отсутствует связь Эпик -> Подзадача");

        epic.addSubtaskCode(epic.getId());

        assertNotEquals(epic.getSubtaskCodes().getFirst(), epic.getId(), "Связь Эпик -> Эпик запрещена");
    }

    @Test
    void checkDeleteEpic() {
        assertNotNull(epic, "Эпик не найден");
        assertNotNull(subtask1, "Подзадача не найдена");

        int epicId = epic.getId();
        int subtaskId = subtask1.getId();
        assertTrue(taskManager.deleteEpic(epic.getId()), "Ошибка удаления Эпика");

        assertNull(taskManager.getEpicFromList(epicId), "Эпик найден, ошибка удаления");
        assertNull(taskManager.getSubtaskFromList(subtaskId), "Подзадача найдена, ошибка удаления");
    }

    @Test
    void checkClearEpics() {
        final var epics = taskManager.getEpics();
        assertNotNull(epics, "Эпики не возвращаются");
        assertEquals(1, epics.size(), "Список Эпиков перед удалением пуст");

        final var subtasks = taskManager.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращаются");
        assertEquals(2, subtasks.size(), "Список Подзадач перед удалением пуст");

        assertTrue(taskManager.clearEpicList(), "Ошибка очистки списка Эпиков и их подзадач");

        assertEquals(0, taskManager.getEpics().size(), "Список Эпиков не пуст, ошибка удаления");
        assertEquals(0, taskManager.getSubtasks().size(), "Список Подзадачи не пуст, ошибка удаления");
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
                        epic.getSubtaskCodes(),
                        epic.getDurationToMinutes(),
                        epic.getStartTime(),
                        epic.getEndTime())),
                "Ошибка обновления Эпика");

        final Task updatedEpic = taskManager.getEpicFromList(savedEpic.getId());

        assertNotNull(updatedEpic, "Эпик не обновлен");
        assertEquals(savedEpic.getId(), updatedEpic.getId(), "ID Эпика до обновления и после не равны");
        assertNotEquals(savedEpic, updatedEpic, "Содержимое Эпика не обновлено");
    }

    @Test
    void checkEpicDurationSetCorrect() {
        Epic foundEpic = taskManager.getEpicFromList(epic.getId());
        assertEquals(10, foundEpic.getDurationToMinutes(), "Продолжительность Эпика задана некорректно!");
    }

    @Test
    void checkEpicStartTimeSetCorrect() {
        Epic foundEpic = taskManager.getEpicFromList(epic.getId());
        assertEquals(subtask1.getStartTime(), foundEpic.getStartTime(), "Дата начала действия Эпика задана некорректно!");
    }

    @Test
    void checkEpicEndTimeSetCorrect() {
        Epic foundEpic = taskManager.getEpicFromList(epic.getId());
        assertEquals(subtask2.getEndTime(), foundEpic.getEndTime(), "Дата начала действия Эпика задана некорректно!");
    }
}

