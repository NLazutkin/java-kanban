package test.manage.TaskManager;

import manage.Managers;
import manage.TaskManager.TaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryEpicTests extends EpicTest<TaskManager> {
    @BeforeEach
    void setUp() {
        super.taskManager = Managers.getDefault();
        init();
    }
}
