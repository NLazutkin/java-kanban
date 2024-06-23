package test.manage.TaskManager.FileBackedTaskManager;

import enums.TaskStatuses;
import enums.TaskTypes;
import exceptions.ManagerSaveException;
import manage.TaskManager.FileBackedTaskManager;
import manage.TaskManager.InMemoryTaskManager;
import manage.TaskManager.TaskManager;
import org.junit.jupiter.api.Test;
import templates.Epic;
import templates.Subtask;
import templates.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    private Task fromString(String value) {
        String[] dataArray = value.split(",");
        int id = Integer.parseInt(dataArray[0]);
        TaskTypes type = TaskTypes.valueOf(dataArray[1]);
        String title = dataArray[2];
        TaskStatuses status = TaskStatuses.valueOf(dataArray[3]);
        String description = dataArray[4];

        switch (type) {
            case TASK:
                return new Task(title, description, id, status);
            case EPIC:
                List<Integer> codes = new ArrayList<>();
                for (String code : dataArray[5].split(";")) {
                    codes.add(Integer.parseInt(code));
                }
                return new Epic(title, description, id, status, codes);
            case SUBTASK:
                return new Subtask(title, description, id, status, Integer.parseInt(dataArray[5]));
            default:
                throw new IllegalStateException("Неожиданное значение: " + type);
        }
    }

    private List<Task> getTaskListFromFile(File file) {
        String TABLE_HEADER = "id,type,name,status,description,epic_id";
        List<Task> list = new ArrayList<>();
        TaskManager taskManager = new InMemoryTaskManager();

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if (line.equals(TABLE_HEADER)) {
                    continue;
                }
                Task taskFromFile = fromString(line);

                switch (taskFromFile.getType()) {
                    case TASK:
                        list.add(taskManager.createTask(taskFromFile));
                        break;
                    case EPIC:
                        if (taskFromFile instanceof Epic) {
                            list.add(taskManager.createEpic((Epic) taskFromFile));
                        }
                        break;
                    case SUBTASK:
                        if (taskFromFile instanceof Subtask) {
                            list.add(taskManager.createSubtask((Subtask) taskFromFile));
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + taskFromFile.getType());
                }
            }
        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }

        return list;
    }

    void prepareTestData() {
        try {
            file = File.createTempFile("test", "csv");
        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }

        fileBackedTaskManager = new FileBackedTaskManager(file);

        task = fileBackedTaskManager.createTask(new Task("Задача 1", "Описание Задачи 1"));
        epic = fileBackedTaskManager.createEpic(new Epic("Эпик 1", "Описание Эпика 1"));
        subtask = fileBackedTaskManager.createSubtask(new Subtask("Подзадача 1",
                "Описание Подзадачи 1",
                epic.getId()));
    }

    @Test
    void checkSaveToFileTest() {
        prepareTestData();

        List<Task> taskListFromFile = getTaskListFromFile(file);

        assertEquals(task, taskListFromFile.getFirst(), "Ошибка записи Задачи в файл!");
        assertEquals(epic, taskListFromFile.get(1), "Ошибка записи Эпика в файл!");
        assertEquals(subtask, taskListFromFile.getLast(), "Ошибка записи Подзадачи в файл!");
    }

    @Test
    void checkUpdateFromFileTest() {
        prepareTestData();

        Subtask updatedSubtask = fileBackedTaskManager.updateSubtask(new Subtask("Подзадача 1",
                "Описание Подзадачи 1",
                subtask.getId(),
                TaskStatuses.DONE,
                subtask.getEpicId()));

        List<Task> taskListFromFile = getTaskListFromFile(file);

        assertEquals(3, taskListFromFile.size(), "Удаление задачи вместо обновления!");
        assertEquals(updatedSubtask, taskListFromFile.getLast(),
                "Ошибка Обновления Подзадачи в файл! Задача изменила место в списке.");
        assertEquals(updatedSubtask.getStatus(), taskListFromFile.getLast().getStatus(),
                "Ошибка Обновления Подзадачи в файл.! Статус задачи не обновлен.");
        assertEquals(TaskStatuses.DONE, taskListFromFile.get(1).getStatus(),
                "Ошибка Обновления Подзадачи в файл! Эпик не изменил статус после обновления Подзадачи");
    }

    @Test
    void checkDeleteFromFileTest() {
        prepareTestData();

        boolean taskDeleteResult = fileBackedTaskManager.deleteTask(task.getId());

        List<Task> taskListFromFile = getTaskListFromFile(file);

        assertEquals(2, taskListFromFile.size(), "Ошибка удаления задачи!");
        assertEquals(epic, taskListFromFile.getFirst(), "Удален Эпик, ожидалось удаление Задачи");
        assertEquals(subtask, taskListFromFile.getLast(), "Удалена Подзадача, ожидалось удаление Задачи");
    }

    @Test
    void checkClearEpicsAndSubtasksFromFileTest() {
        prepareTestData();

        boolean epicDeleteResult = fileBackedTaskManager.deleteEpic(epic.getId());

        List<Task> taskListFromFile = getTaskListFromFile(file);

        assertEquals(1, taskListFromFile.size(), "Ошибка очистки списка Эпиков и Подзадач!");
        assertEquals(task, taskListFromFile.getFirst(), "Вместо Эпиков и Подзадач удалена Задача");
    }
}
