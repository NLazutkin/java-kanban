package test.manage.TaskManager;

import manage.Managers;
import manage.TaskManager.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;


class FileBackedTaskTests extends TaskTest<FileBackedTaskManager> {
    @BeforeEach
    void setUp() {
        super.taskManager = Managers.getDefaultFileBacked();
        init();
    }
}

