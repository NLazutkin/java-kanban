package test.manage.TaskManager;

import enums.TaskStatuses;
import exceptions.ManagerSaveException;
import manage.TaskManager.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import templates.Epic;
import templates.Subtask;
import templates.Task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private static File file;
    private static FileBackedTaskManager fileBackedTaskManager;
    Task task;
    Epic epic;
    Subtask subtask;

    @Test
    void fileExistTest() {
        file = new File("resources/java-kanban.csv");

        assertTrue(file.exists(), "Файл " + file.getName() + " в директории "
                + file.getAbsolutePath() + "не существует!");
    }

    @Test
    void checkLoadTaskFromFile() {
        file = new File("resources/java-kanban.csv");

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> taskList = fileBackedTaskManager.getTasks();

        assertNotNull(taskList, "Задачи не считаны из файла!");
    }

    void prepareTestData() {
        try {
            file = File.createTempFile("test", "csv");
        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }

        fileBackedTaskManager = new FileBackedTaskManager(file);
        long duration = 5;
        LocalDateTime startTime = LocalDateTime.now();
        task = fileBackedTaskManager.createTask(new Task("Задача 1", "Описание Задачи 1", duration, startTime));
        epic = fileBackedTaskManager.createEpic(new Epic("Эпик 1", "Описание Эпика 1"));
        subtask = fileBackedTaskManager.createSubtask(new Subtask("Подзадача 1",
                "Описание Подзадачи 1",
                epic.getId(),
                duration,
                startTime.plusMinutes(6)));
    }

    @Test
    void checkSaveToFileTest() {
        prepareTestData();

        FileBackedTaskManager fileBackedTaskManagerLoad = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, fileBackedTaskManagerLoad.getTasks().size(), "Обнаружены лишние Задачи");
        assertEquals(1, fileBackedTaskManagerLoad.getEpics().size(), "Обнаружены лишние Эпики");
        assertEquals(1, fileBackedTaskManagerLoad.getSubtasks().size(), "Обнаружены лишние Подзадачи");

        assertEquals(task, fileBackedTaskManagerLoad.getTaskFromList(task.getId()), "Ошибка записи Задачи в файл!");
        assertEquals(epic, fileBackedTaskManagerLoad.getEpicFromList(epic.getId()), "Ошибка записи Эпика в файл!");
        assertEquals(subtask, fileBackedTaskManagerLoad.getSubtaskFromList(subtask.getId()), "Ошибка записи Подзадачи в файл!");
    }

    @Test
    void checkUpdateFromFileTest() {
        prepareTestData();

        Subtask updatedSubtask = fileBackedTaskManager.updateSubtask(new Subtask("Подзадача 1",
                "Описание Подзадачи 1",
                subtask.getId(),
                TaskStatuses.DONE,
                subtask.getEpicId(),
                subtask.getDurationToMinutes(),
                subtask.getStartTime()));

        FileBackedTaskManager fileBackedTaskManagerLoad = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, fileBackedTaskManagerLoad.getSubtasks().size(), "Удаление задачи вместо обновления!");

        Subtask subtaskFromFile = fileBackedTaskManagerLoad.getSubtaskFromList(subtask.getId());
        Epic epicFromFile = fileBackedTaskManagerLoad.getEpicFromList(epic.getId());

        assertEquals(updatedSubtask, subtaskFromFile,
                "Ошибка Обновления Подзадачи в файл! Задача изменила место в списке.");
        assertEquals(updatedSubtask.getStatus(), subtaskFromFile.getStatus(),
                "Ошибка Обновления Подзадачи в файл! Статус задачи не обновлен.");
        assertEquals(TaskStatuses.DONE, epicFromFile.getStatus(),
                "Ошибка Обновления Подзадачи в файл! Эпик не изменил статус после обновления Подзадачи");
    }

    @Test
    void checkDeleteFromFileTest() {
        prepareTestData();

        boolean taskDeleteResult = fileBackedTaskManager.deleteTask(task.getId());

        FileBackedTaskManager fileBackedTaskManagerLoad = FileBackedTaskManager.loadFromFile(file);

        assertEquals(0, fileBackedTaskManagerLoad.getTasks().size(), "Ошибка удаления задачи!");
        assertEquals(1, fileBackedTaskManagerLoad.getEpics().size(), "Удален Эпик, ожидалось удаление Задачи");
        assertEquals(1, fileBackedTaskManagerLoad.getSubtasks().size(), "Удалена Подзадача, ожидалось удаление Задачи");
    }

    @Test
    void checkClearEpicsAndSubtasksFromFileTest() {
        prepareTestData();

        boolean epicDeleteResult = fileBackedTaskManager.deleteEpic(epic.getId());

        FileBackedTaskManager fileBackedTaskManagerLoad = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, fileBackedTaskManagerLoad.getTasks().size(), "Ошибка удаления задачи!");
        assertEquals(0, fileBackedTaskManagerLoad.getEpics().size(), "Эпик не удален");
        assertEquals(0, fileBackedTaskManagerLoad.getSubtasks().size(), "Подзадача не удалена");
    }
}
