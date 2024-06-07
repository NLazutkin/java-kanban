import manage.Managers;
import manage.TaskManager;
import templates.Epic;
import templates.Subtask;
import templates.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        final String taskTitle = "Задача ";
        final String epicTitle = "Эпик ";
        final String subtaskTitle = "Подзадача ";
        final String subtask1Description = "Эпик 1";
        final String subtask2Description = "Эпик 2";

        Task task1 = taskManager.createTask(new Task(taskTitle + "1", taskTitle + "1"));
        Task task2 = taskManager.createTask(new Task(taskTitle + "2", taskTitle + "2"));
        Task task3 = taskManager.createTask(new Task(taskTitle + "3", taskTitle + "3"));
        Epic epic1 = taskManager.createEpic(new Epic(epicTitle + "1", epicTitle + "1"));
        Epic epic2 = taskManager.createEpic(new Epic(epicTitle + "2", epicTitle + "2"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask(subtaskTitle + "1", subtask1Description));
        Subtask subtask2 = taskManager.createSubtask(new Subtask(subtaskTitle + "2", subtask1Description));
        Subtask subtask3 = taskManager.createSubtask(new Subtask(subtaskTitle + "3", subtask1Description));
        Subtask subtask4 = taskManager.createSubtask(new Subtask(subtaskTitle + "4", subtask1Description));
        Subtask subtask5 = taskManager.createSubtask(new Subtask(subtaskTitle + "5", subtask2Description));
        Subtask subtask6 = taskManager.createSubtask(new Subtask(subtaskTitle + "6", subtask2Description));
        Subtask subtask7 = taskManager.createSubtask(new Subtask(subtaskTitle + "7", subtask2Description));
        Subtask subtask8 = taskManager.createSubtask(new Subtask(subtaskTitle + "8", subtask2Description));
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

        boolean resultTask2 = taskManager.deleteTask(task2.getId());
        System.out.println(" ");
        System.out.println("История польз. удалена Задача 2:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }

        boolean resultEpic1 = taskManager.deleteEpic(epic1.getId());
        boolean resultSubtask1 = taskManager.deleteSubtask(subtask1.getId());
        boolean resultSubtask2 = taskManager.deleteSubtask(subtask2.getId());
        boolean resultSubtask3 = taskManager.deleteSubtask(subtask3.getId());
        boolean resultSubtask4 = taskManager.deleteSubtask(subtask4.getId());
        System.out.println(" ");
        System.out.println("История польз. удален Эпик1 и все его подзадачи:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
