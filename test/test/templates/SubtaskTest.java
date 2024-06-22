package test.templates;

import enums.TaskStatuses;
import enums.TaskTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Subtask;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {
    private static Subtask subtask;

    @BeforeEach
    void beforeEach() {
        subtask = new Subtask("Подзадача 1", "Описание Подзадачи 1", 1, TaskStatuses.NEW, 1);
    }

    @Test
    void checkIdGetterSetter() {
        subtask.setId(2);

        assertEquals(2, subtask.getId(), "Ошибка добавления/чтения id Подзадачи");
    }

    @Test
    void checkGetTitle() {
        assertEquals("Подзадача 1", subtask.getTitle(), "Ошибка чтения имени Подзадачи");
    }

    @Test
    void checkGetDescription() {
        assertEquals("Описание Подзадачи 1", subtask.getDescription(),
                "Ошибка чтения описания Подзадачи");
    }

    @Test
    void checkGetStatus() {
        assertEquals(TaskStatuses.NEW, subtask.getStatus(), "Ошибка чтения статуса Подзадачи");
    }

    @Test
    void checkGetType() {
        assertEquals(TaskTypes.SUBTASK, subtask.getType(), "Ошибка чтения типа Подзадачи");
    }

    @Test
    void checkEpicIdGetterSetter() {
        subtask.setEpicId(2);

        assertEquals(2, subtask.getEpicId(), "Ошибка добавления/чтения id Подзадачи");
    }

    @Test
    void checkToString() {
        String expectedText = subtask.toString();
        String text = "Subtask {title = 'Подзадача 1', " +
                "description = 'Описание Подзадачи 1', " +
                "id = 1, " +
                "status = NEW, " +
                "epicID = 1}";

        assertEquals(text, expectedText, "Ошибка чтения типа Задачи");
    }
}
