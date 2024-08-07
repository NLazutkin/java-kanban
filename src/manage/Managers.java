package manage;

import manage.HistoryManager.HistoryManager;
import manage.HistoryManager.InMemoryHistoryManager;
import manage.TaskManager.InMemoryTaskManager;
import manage.TaskManager.FileBackedTaskManager;

import java.io.File;

public final class Managers {
    private Managers() {
    }

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBacked() {
        return new FileBackedTaskManager(new File("./resources/java-kanban.csv"));
    }
}
