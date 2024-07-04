package test.manage.TaskManager;

import manage.Managers;
import manage.TaskManager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

class FileBackedSubtaskTests extends SubtaskTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp() {
        super.taskManager = Managers.getDefault();
        init();
    }
}

