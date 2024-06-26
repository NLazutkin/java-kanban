package test.manage;

import manage.HistoryManager.HistoryManager;
import manage.Managers;
import manage.TaskManager.FileBackedTaskManager;
import manage.TaskManager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManagesTests {
    @Test
    void checkTaskManagerCallCorrect() {
        TaskManager testSample = Managers.getDefault();
        assertNotNull(testSample, "Ошибка получения Экземпляра класса TaskManager");
    }

    @Test
    void checkHistoryManagerCallCorrect() {
        HistoryManager testSample = Managers.getDefaultHistory();
        assertNotNull(testSample, "Ошибка получения Экземпляра класса HistoryManager");
    }

    @Test
    void checkFileBackedTaskManagerCallCorrect() {
        FileBackedTaskManager testSample = Managers.getDefaultFileBacked();
        assertNotNull(testSample, "Ошибка получения Экземпляра класса HistoryManager");
    }
}
