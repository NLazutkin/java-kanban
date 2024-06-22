package test.templates;

import enums.TaskStatuses;
import enums.TaskTypes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Task;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    private static Task task;

    @BeforeEach
    void beforeEach() {
        task = new Task("Задача 1", "Описание Задачи 1", 1, TaskStatuses.NEW);

    }

    @Test
    void checkIdGetterSetter() {
        task.setId(2);

        assertEquals(2, task.getId(), "Ошибка добавления/чтения id Задачи");
    }

    @Test
    void checkGetTitle() {
        assertEquals("Задача 1", task.getTitle(), "Ошибка чтения имени Задачи");
    }

    @Test
    void checkGetDescription() {
        assertEquals("Описание Задачи 1", task.getDescription(), "Ошибка чтения описания Задачи");
    }

    @Test
    void checkGetStatus() {
        assertEquals(TaskStatuses.NEW, task.getStatus(), "Ошибка чтения статуса Задачи");
    }

    @Test
    void checkGetType() {
        assertEquals(TaskTypes.TASK, task.getType(), "Ошибка чтения типа Задачи");
    }

    @Test
    void checkToString() {
        String expectedText = task.toString();
        String text = "Task {title='Задача 1', description='Описание Задачи 1', id=1, status=NEW}";

        assertEquals(text, expectedText, "Ошибка чтения типа Задачи");
    }
}
