import http.HttpTaskServer;
import manage.Managers;
import manage.TaskManager.TaskManager;
import templates.Epic;
import templates.Subtask;
import templates.Task;
import enums.TaskStatuses;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        final String taskTitle = "Задача ";
        final String epicTitle = "Эпик ";
        final String subtaskTitle = "Подзадача ";
        final String subtask1Description = "Эпик 1";
        final String subtask2Description = "Эпик 2";

        // httpTaskServer.start();

        Task task1 = taskManager.createTask(new Task(taskTitle + "1", taskTitle + "1", 10,
                LocalDateTime.of(2024, 7, 2, 7, 0, 0)));
        Task task2 = taskManager.createTask(new Task(taskTitle + "2", taskTitle + "2", 15,
                LocalDateTime.of(2024, 7, 2, 8, 10, 0)));
        Task task3 = taskManager.createTask(new Task(taskTitle + "3", taskTitle + "3", 20,
                LocalDateTime.of(2024, 7, 2, 11, 40, 0)));

        Epic epic1 = taskManager.createEpic(new Epic(epicTitle + "1", epicTitle + "1"));
        Epic epic2 = taskManager.createEpic(new Epic(epicTitle + "2", epicTitle + "2"));

        Subtask subtask1 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "1", subtask1Description, epic1.getId(), 5,
                        LocalDateTime.of(2024, 7, 2, 9, 0, 0)));
        Subtask subtask2 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "2", subtask1Description, epic1.getId(), 5,
                        LocalDateTime.of(2024, 7, 2, 9, 6, 0)));
        Subtask subtask3 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "3", subtask1Description, epic1.getId(), 5,
                        LocalDateTime.of(2024, 7, 2, 9, 12, 0)));
        Subtask subtask4 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "4", subtask1Description, epic1.getId(), 10,
                        LocalDateTime.of(2024, 7, 2, 9, 18, 0)));
        Subtask subtask5 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "5", subtask2Description, epic2.getId(), 10,
                        LocalDateTime.of(2024, 7, 2, 10, 0, 0)));
        Subtask subtask6 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "6", subtask2Description, epic2.getId(), 10,
                        LocalDateTime.of(2024, 7, 2, 10, 11, 0)));
        Subtask subtask7 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "7", subtask2Description, epic2.getId(), 20,
                        LocalDateTime.of(2024, 7, 2, 10, 22, 0)));
        Subtask subtask8 = taskManager.createSubtask(
                new Subtask(subtaskTitle + "8", subtask2Description, epic2.getId(), 5,
                        LocalDateTime.of(2024, 7, 2, 10, 43, 0)));

        task1 = taskManager.getTaskFromList(task1.getId());
        task2 = taskManager.getTaskFromList(task2.getId());
        task3 = taskManager.getTaskFromList(task3.getId());
        epic1 = taskManager.getEpicFromList(epic1.getId());
        epic2 = taskManager.getEpicFromList(epic2.getId());
        subtask1 = taskManager.getSubtaskFromList(subtask1.getId());
        subtask2 = taskManager.getSubtaskFromList(subtask2.getId());
        subtask3 = taskManager.getSubtaskFromList(subtask3.getId());
        subtask4 = taskManager.getSubtaskFromList(subtask4.getId());
        subtask5 = taskManager.getSubtaskFromList(subtask5.getId());
        subtask6 = taskManager.getSubtaskFromList(subtask6.getId());
        subtask7 = taskManager.getSubtaskFromList(subtask7.getId());
        subtask8 = taskManager.getSubtaskFromList(subtask8.getId());

        // show all/
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
        System.out.println("Приоритеты:");
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }

        System.out.println(" ");
        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println(" ");
        System.out.println("Linked.:");
        for (Task task : taskManager.getLinkedHistory()) {
            System.out.println(task);
        }

        // user scenario
        subtask2 = taskManager.getSubtaskFromList(subtask2.getId());
        task1 = taskManager.getTaskFromList(task1.getId());
        epic2 = taskManager.getEpicFromList(epic2.getId());
        task2 = taskManager.getTaskFromList(task2.getId());
        subtask1 = taskManager.getSubtaskFromList(subtask1.getId());
        epic1 = taskManager.getEpicFromList(epic1.getId());

        System.out.println(" ");
        System.out.println("История польз.:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println(" ");
        System.out.println("Linked change.:");
        for (Task task : taskManager.getLinkedHistory()) {
            System.out.println(task);
        }

        boolean resultTask2 = taskManager.deleteTask(task2.getId());

        System.out.println(" ");
        System.out.println("История польз. удалена Задача 2:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println(" ");
        System.out.println("Linked del task 2.:");
        for (Task task : taskManager.getLinkedHistory()) {
            System.out.println(task);
        }

        System.out.println(" ");
        System.out.println("Приоритеты после удаления задачи 2:");
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }

        boolean resultSubtask1 = taskManager.deleteSubtask(subtask1.getId());
        boolean resultSubtask2 = taskManager.deleteSubtask(subtask2.getId());
        boolean resultSubtask3 = taskManager.deleteSubtask(subtask3.getId());
        boolean resultSubtask4 = taskManager.deleteSubtask(subtask4.getId());

        System.out.println(" ");
        System.out.println("История польз. удален Эпик1 и все его подзадачи:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println(" ");
        System.out.println("Linked del epic1 and his subtasks.:");
        for (Task task : taskManager.getLinkedHistory()) {
            System.out.println(task);
        }

        System.out.println(" ");
        System.out.println("Приоритеты после удаления Подзадач Эпика 1:");
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }

        Task updatedTask = taskManager.updateTask(new Task(task3.getTitle(),
                task3.getDescription(),
                task3.getId(),
                TaskStatuses.IN_PROGRESS,
                task3.getDurationToMinutes(),
                task3.getStartTime().minusMinutes(120)));

        System.out.println(" ");
        System.out.println("История польз. обновлена задача 3:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        System.out.println(" ");
        System.out.println("Linked upd task3.:");
        for (Task task : taskManager.getLinkedHistory()) {
            System.out.println(task);
        }

        System.out.println(" ");
        System.out.println("Приоритеты после обновления Задачи 3:");
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }

        Epic updatedEpic = taskManager.updateEpic(new Epic(epic2.getTitle(),
                epic2.getDescription(),
                epic2.getId(),
                TaskStatuses.IN_PROGRESS,
                epic2.getSubtaskCodes(),
                epic2.getDurationToMinutes(),
                epic2.getStartTime(),
                epic2.getEndTime()));

        System.out.println(" ");
        System.out.println("Приоритеты после обновления Эпика:");
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }

        Subtask updatedSubtask = taskManager.updateSubtask(new Subtask(subtask5.getTitle(),
                subtask5.getDescription(),
                subtask5.getId(),
                TaskStatuses.IN_PROGRESS,
                subtask5.getEpicId(),
                subtask5.getDurationToMinutes(),
                subtask5.getStartTime().plusMinutes(60)));

        System.out.println(" ");
        System.out.println("Приоритеты после обновления Подзадачи:");
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }
    }
}
