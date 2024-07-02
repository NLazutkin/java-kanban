package test.manage.TaskManager;

import manage.Managers;
import manage.TaskManager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskTests extends TaskTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp() {
        super.taskManager = Managers.getDefault();
        init();
    }
}