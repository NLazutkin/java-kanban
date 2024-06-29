package manage.TaskManager;

import enums.TaskStatuses;
import enums.TaskTypes;
import exceptions.ManagerSaveException;

import templates.Epic;
import templates.Subtask;
import templates.Task;

import java.nio.charset.StandardCharsets;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String TABLE_HEADER = "id,type,name,status,description,epic_id";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        TaskManager taskManager = new FileBackedTaskManager(new File("./resources/java-kanban.csv"));

        final String taskTitle = "Задача ";
        final String epicTitle = "Эпик ";
        final String subtaskTitle = "Подзадача ";
        final String subtask1Description = "Эпик 1";
        final String subtask2Description = "Эпик 2";

        System.out.println("Заводим несколько разных задач, эпиков и подзадач");
        Task task1 = taskManager.createTask(new Task(taskTitle + "1", taskTitle + "1"));
        Task task2 = taskManager.createTask(new Task(taskTitle + "2", taskTitle + "2"));
        Task task3 = taskManager.createTask(new Task(taskTitle + "3", taskTitle + "3"));
        Epic epic1 = taskManager.createEpic(new Epic(epicTitle + "1", epicTitle + "1"));
        Subtask subtask1 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "1", subtask1Description, epic1.getId()));
        Subtask subtask2 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "2", subtask1Description, epic1.getId()));
        Subtask subtask3 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "3", subtask1Description, epic1.getId()));
        Subtask subtask4 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "4", subtask1Description, epic1.getId()));
        Epic epic2 = taskManager.createEpic(new Epic(epicTitle + "2", epicTitle + "2"));
        Subtask subtask5 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "5", subtask2Description, epic2.getId()));
        Subtask subtask6 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "6", subtask2Description, epic2.getId()));
        Subtask subtask7 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "7", subtask2Description, epic2.getId()));
        Subtask subtask8 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "8", subtask2Description, epic2.getId()));

        System.out.println("Проверяем содержимое");

        // show all
        System.out.println(" ");
        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }

        System.out.println(" ");
        System.out.println("Эпики:");
        for (Task epic : taskManager.getEpics()) {
            System.out.println(epic);

            for (Task task : taskManager.getSubtaskByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }

        System.out.println(" ");
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println(" ");
        System.out.println("Создаем новый FileBackedTaskManager-менеджер из этого же файла");
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(
                new File("resources/java-kanban.csv"));

        System.out.println("Проверяем, что все Задачи/Эпики/Подзадачи которые были в старом менеджере, есть в новом");

        // show all
        System.out.println(" ");
        System.out.println("Задачи:");
        for (Task task : fileBackedTaskManager.getTasks()) {
            System.out.println(task);
        }

        System.out.println(" ");
        System.out.println("Эпики:");
        for (Task epic : fileBackedTaskManager.getEpics()) {
            System.out.println(epic);

            for (Task task : fileBackedTaskManager.getSubtaskByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }

        System.out.println(" ");
        System.out.println("Подзадачи:");
        for (Task subtask : fileBackedTaskManager.getSubtasks()) {
            System.out.println(subtask);
        }
    }

    private String toString(Task task) {
        return task.getId() + ","
                + task.getType() + ","
                + task.getTitle() + ","
                + task.getStatus() + ","
                + task.getDescription();
    }

    private String toString(Epic epic) {
        return epic.getId() + ","
                + epic.getType() + ","
                + epic.getTitle() + ","
                + epic.getStatus() + ","
                + epic.getDescription() + ","
                + epic.getSubtaskCodes().toString().replace(", ", ";")
                .replace("[", "")
                .replace("]", "");
    }

    private String toString(Subtask subtask) {
        return subtask.getId() + ","
                + subtask.getType() + ","
                + subtask.getTitle() + ","
                + subtask.getStatus() + ","
                + subtask.getDescription() + ","
                + subtask.getEpicId();
    }

    private static Task fromString(String value) {
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

    private void save() {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            fileWriter.write(TABLE_HEADER);
            fileWriter.newLine();
            for (Task task : getTasks()) {
                fileWriter.write(toString(task));
                fileWriter.newLine();
            }
            for (Epic epic : getEpics()) {
                fileWriter.write(toString(epic));
                fileWriter.newLine();
            }
            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(toString(subtask));
                fileWriter.newLine();
            }
        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        if (file.isDirectory()) {
            throw new ManagerSaveException("Вместо файла указана директория.");
        } else if (!file.exists()) {
            try {
                Files.createFile(file.toPath());
            } catch (IOException exception) {
                throw new ManagerSaveException("Файл не найден. Ошибка при попытке создать новый файл.");
            }
        }

        int maxId = 0;
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if (line.equals(TABLE_HEADER)) {
                    continue;
                }
                Task task = fromString(line);

                if (task.getId() > maxId) {
                    maxId = task.getId();
                }

                switch (task.getType()) {
                    case TASK:
                        fileBackedTaskManager.taskList.put(task.getId(), task);
                        break;
                    case EPIC:
                        if (task instanceof Epic) {
                            fileBackedTaskManager.epicList.put(task.getId(), (Epic) task);
                            break;
                        }
                    case SUBTASK:
                        if (task instanceof Subtask) {
                            fileBackedTaskManager.subtaskList.put(task.getId(), (Subtask) task);
                            break;
                        }
                    default:
                        throw new IllegalStateException("Unexpected value: " + task.getType());
                }
            }

            idCounter = maxId;
        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }

        return fileBackedTaskManager;
    }

    @Override
    public Task createTask(Task task) {
        Task newtask = super.createTask(task);
        save();
        return newtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public boolean clearTasksList() {
        boolean isClear = super.clearTasksList();
        save();
        return isClear;
    }

    @Override
    public boolean clearEpicList() {
        boolean isClear = super.clearEpicList();
        save();
        return isClear;
    }

    @Override
    public boolean clearSubtaskList() {
        boolean isClear = super.clearSubtaskList();
        save();
        return isClear;
    }

    @Override
    public boolean deleteTask(int id) {
        boolean isDelete = super.deleteTask(id);
        save();
        return isDelete;
    }

    @Override
    public boolean deleteEpic(int id) {
        boolean isDelete = super.deleteEpic(id);
        save();
        return isDelete;
    }

    @Override
    public boolean deleteSubtask(int id) {
        boolean isDelete = super.deleteSubtask(id);
        save();
        return isDelete;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }
}
