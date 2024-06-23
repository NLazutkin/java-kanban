package test.templates;

import enums.TaskStatuses;
import enums.TaskTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Epic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private static Epic epic;

    @BeforeEach
    void beforeEach() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        epic = new Epic("Эпик 1", "Описание Эпика 1", 1, TaskStatuses.NEW, list);
    }

    @Test
    void checkIdGetterSetter() {
        epic.setId(2);

        assertEquals(2, epic.getId(), "Ошибка добавления/чтения id Эпика");
    }

    @Test
    void checkGetTitle() {
        assertEquals("Эпик 1", epic.getTitle(), "Ошибка чтения имени Эпика");
    }

    @Test
    void checkGetDescription() {
        assertEquals("Описание Эпика 1", epic.getDescription(), "Ошибка чтения описания Эпика");
    }

    @Test
    void checkGetStatus() {
        assertEquals(TaskStatuses.NEW, epic.getStatus(), "Ошибка чтения статуса Эпика");
    }

    @Test
    void checkGetType() {
        assertEquals(TaskTypes.EPIC, epic.getType(), "Ошибка чтения типа Эпика");
    }

    @Test
    void checkAddSubtaskCodes() {
        epic.addSubtaskCode(3);

        List<Integer> list = epic.getSubtaskCodes();

        assertEquals(3, list.getLast(), "Ошибка чтения списка подзадач Эпика");
    }

    @Test
    void checkGetSubtaskCodes() {
        assertNotNull(epic.getSubtaskCodes(), "Ошибка чтения списка подзадач Эпика");
    }

    @Test
    void checkToString() {
        String expectedText = epic.toString();
        String text = "Epic {title = 'Эпик 1', description = 'Описание Эпика 1', id = 1, status = NEW, " +
                        "subtaskCodes = [1, 2]}";

        assertEquals(text, expectedText, "Ошибка чтения типа Задачи");
    }
}
