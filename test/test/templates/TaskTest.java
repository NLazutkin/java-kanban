package test.templates;

import enums.TaskStatuses;
import enums.TaskTypes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    private static Task task;
    private static LocalDateTime startTime;
    private static long duration;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    @BeforeEach
    void beforeEach() {
        duration = 5;
        startTime = LocalDateTime.now();
        task = new Task("Задача 1", "Описание Задачи 1", 1,
                TaskStatuses.NEW, duration, startTime);
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
    void checkStatusGetterSetter() {
        task.setStatus(TaskStatuses.DONE);
        assertEquals(TaskStatuses.DONE, task.getStatus(), "Ошибка чтения статуса Задачи");
    }

    @Test
    void checkGetType() {
        assertEquals(TaskTypes.TASK, task.getType(), "Ошибка чтения типа Задачи");
    }

    @Test
    void checkDuration() {
        task.setDuration(Duration.ofMinutes(15));
        assertEquals(Duration.ofMinutes(15), task.getDuration(), "Ошибка чтения продолжительности Задачи");
    }

    @Test
    void checkDurationToMinutes() {
        assertEquals(duration, task.getDurationToMinutes(), "Ошибка чтения продолжительности Задачи");
    }

    @Test
    void checkStartTimeGetterSetter() {
        LocalDateTime checkTime = LocalDateTime.of(2024, 07, 21, 10, 0, 0);
        task.setStartTime(checkTime);
        assertEquals(checkTime, task.getStartTime(), "Ошибка чтения времени начала Задачи");
    }

    @Test
    void checkStartTimeToString() {
        assertEquals(startTime.plusMinutes(duration).format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yy")),
                task.getEndTimeToString(), "Ошибка чтения времени окончания Задачи");
    }

    @Test
    void checkEndTime() {
        assertEquals(startTime.plusMinutes(duration), task.getEndTime(), "Ошибка чтения времени окончания Задачи");
    }

    @Test
    void checkSpentTime() {
        assertEquals(Duration.between(startTime, LocalDateTime.now()).toMinutes(), task.getSpentTime(),
                "Ошибка расчета времени потраченного на Задачу");
    }

    @Test
    void checkRemainingTime() {
        assertEquals(Duration.between(LocalDateTime.now(), startTime.plusMinutes(duration)).toMinutes(), task.getRemainingTime(),
                "Ошибка расчета оставшегося времени на Задачу");
    }

    @Test
    void checkDurationToString() {
        assertEquals(String.valueOf(Duration.ofMinutes(duration).toMinutes()), task.getDurationToString(), "Ошибка чтения продолжительности Задачи");
    }

    @Test
    void checkToString() {
        String expectedText = task.toString();
        String text = "Task {title = 'Задача 1', description = 'Описание Задачи 1', id = 1, status = NEW, duration = " + duration
                + ", startTime = " + startTime.format(DATE_TIME_FORMATTER)
                + ", endTime = " + startTime.plusMinutes(duration).format(DATE_TIME_FORMATTER) + "}";

        assertEquals(text, expectedText, "Ошибка печати Задачи");
    }
}
