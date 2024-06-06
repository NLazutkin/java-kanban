import manage.Managers;
import manage.TaskManager;
import templates.Epic;
import templates.Subtask;
import templates.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = taskManager.createTask(new Task("Задача 1", "Задача 1"));
        Task task2 = taskManager.createTask(new Task("Задача 2", "Задача 2"));
        Task task3 = taskManager.createTask(new Task("Задача 3", "Задача 3"));
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", "Эпик 2"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Сабтаск 1", "Эпик 1"));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Сабтаск 2", "Эпик 1"));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Сабтаск 3", "Эпик 1"));
        Subtask subtask4 = taskManager.createSubtask(new Subtask("Сабтаск 4", "Эпик 1"));
        Subtask subtask5 = taskManager.createSubtask(new Subtask("Сабтаск 5", "Эпик 2"));
        Subtask subtask6 = taskManager.createSubtask(new Subtask("Сабтаск 6", "Эпик 2"));
        Subtask subtask7 = taskManager.createSubtask(new Subtask("Сабтаск 7", "Эпик 2"));
        Subtask subtask8 = taskManager.createSubtask(new Subtask("Сабтаск 8", "Эпик 2"));
        taskManager.createRelation(epic1, subtask1);
        taskManager.createRelation(epic1, subtask2);
        taskManager.createRelation(epic1, subtask3);
        taskManager.createRelation(epic1, subtask4);
        taskManager.createRelation(epic2, subtask5);
        taskManager.createRelation(epic2, subtask6);
        taskManager.createRelation(epic2, subtask7);
        taskManager.createRelation(epic2, subtask8);

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
        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
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

        boolean resulttask2 = taskManager.deleteTask(task2.getId());
        System.out.println(" ");
        System.out.println("История польз. удалена Задача 2:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        boolean resultepic1 = taskManager.deleteEpic(epic1.getId());
        boolean resultsubtask1 = taskManager.deleteSubtask(subtask1.getId());
        boolean resultsubtask2 = taskManager.deleteSubtask(subtask2.getId());
        boolean resultsubtask3 = taskManager.deleteSubtask(subtask3.getId());
        boolean resultsubtask4 = taskManager.deleteSubtask(subtask4.getId());
        System.out.println(" ");
        System.out.println("История польз. удален Эпик1 и все его подзадачи:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
