package test;

import manage.HistoryManager;
import manage.Managers;
import manage.TaskManager;
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
}
