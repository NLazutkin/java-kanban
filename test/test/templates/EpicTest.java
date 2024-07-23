package test.templates;

import enums.TaskStatuses;
import enums.TaskTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import templates.Epic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private static Epic epic;
    private static LocalDateTime startTime;
    private static long duration;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    @BeforeEach
    void beforeEach() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        duration = 5;
        startTime = LocalDateTime.of(2024, 7, 2, 7, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 7, 2, 7, 5, 0);
        epic = new Epic("Эпик 1", "Описание Эпика 1", 1, TaskStatuses.NEW, list, duration, startTime, endTime);
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
    void checkSetSubtaskCodes() {
        List<Integer> inputlist = new ArrayList<>();
        inputlist.add(5);
        inputlist.add(6);
        inputlist.add(7);

        epic.setSubtaskCodes(inputlist);

        List<Integer> outpuList = epic.getSubtaskCodes();

        assertEquals(inputlist.size(), outpuList.size(), "Ошибка записи списка подзадач Эпика");
        assertEquals(inputlist.getLast(), outpuList.getLast(), "Ошибка записи списка подзадач Эпика");
    }

    @Test
    void checkGetSubtaskCodes() {
        assertNotNull(epic.getSubtaskCodes(), "Ошибка чтения списка подзадач Эпика");
    }

    @Test
    void checkDuration() {
        assertEquals(Duration.ofMinutes(duration), epic.getDuration(), "Ошибка чтения продолжительности Задачи");
    }

    @Test
    void checkDurationToMinutes() {
        assertEquals(duration, epic.getDurationToMinutes(), "Ошибка чтения продолжительности Задачи");
    }

    @Test
    void checkStartTime() {
        assertEquals(startTime, epic.getStartTime(), "Ошибка чтения времени начала Задачи");
    }

    @Test
    void checkEndTime() {
        assertEquals(startTime.plusMinutes(duration), epic.getEndTime(), "Ошибка чтения времени окончания Задачи");
    }

    @Test
    void checkEndTimeToString() {
        assertEquals(startTime.plusMinutes(duration).format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yy")),
                epic.getEndTimeToString(), "Ошибка чтения времени окончания Задачи");
    }


    @Test
    void checkSpentTime() {
        assertEquals(Duration.between(startTime, LocalDateTime.now()).toMinutes(), epic.getSpentTime(),
                "Ошибка расчета времени потраченного на Эпик");
    }

    @Test
    void checkRemainingTime() {
        assertEquals(Duration.between(LocalDateTime.now(), startTime.plusMinutes(duration)).toMinutes(), epic.getRemainingTime(),
                "Ошибка расчета оставшегося времени на Эпик");
    }

    @Test
    void checkToString() {
        String expectedText = epic.toString();
        String text = "Epic {title = 'Эпик 1', description = 'Описание Эпика 1', id = 1, status = NEW, duration = "
                + duration
                + ", startTime = " + startTime.format(DATE_TIME_FORMATTER)
                + ", endTime = " + startTime.plusMinutes(duration).format(DATE_TIME_FORMATTER)
                + ", subtaskCodes = [1, 2]}";

        assertEquals(text, expectedText, "Ошибка печати Эпика");
    }
}
