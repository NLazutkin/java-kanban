package test.templates;

import enums.TaskStatuses;
import enums.TaskTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {
    private static Subtask subtask;
    private static LocalDateTime startTime;
    private static long duration;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    @BeforeEach
    void beforeEach() {
        duration = 5;
        startTime = LocalDateTime.now();
        subtask = new Subtask("Подзадача 1", "Описание Подзадачи 1", 1,
                TaskStatuses.NEW, 1, duration, startTime);
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
    void checkDuration() {
        assertEquals(Duration.ofMinutes(duration), subtask.getDuration(), "Ошибка чтения продолжительности Задачи");
    }

    @Test
    void checkDurationToMinutes() {
        assertEquals(duration, subtask.getDurationToMinutes(), "Ошибка чтения продолжительности Задачи");
    }

    @Test
    void checkStartTime() {
        assertEquals(startTime, subtask.getStartTime(), "Ошибка чтения времени начала Задачи");
    }

    @Test
    void checkEndTime() {
        assertEquals(startTime.plusMinutes(duration), subtask.getEndTime(), "Ошибка чтения времени окончания Задачи");
    }

    @Test
    void checkSpentTime() {
        assertEquals(Duration.between(startTime, LocalDateTime.now()).toMinutes(), subtask.getSpentTime(),
                "Ошибка расчета времени потраченного на Подзадачу");
    }

    @Test
    void checkRemainingTime() {
        assertEquals(Duration.between(LocalDateTime.now(), startTime.plusMinutes(duration)).toMinutes(), subtask.getRemainingTime(),
                "Ошибка расчета оставшегося времени на Подзадачу");
    }

    @Test
    void checkToString() {
        String expectedText = subtask.toString();
        String text = "Subtask {title = 'Подзадача 1', description = 'Описание Подзадачи 1', id = 1, status = NEW"
                + ", duration = " + duration
                + ", startTime = " + startTime.format(DATE_TIME_FORMATTER)
                + ", endTime = " + startTime.plusMinutes(duration).format(DATE_TIME_FORMATTER)
                + ", epicID = 1}";

        assertEquals(text, expectedText, "Ошибка печати Подзадачи");
    }
}
