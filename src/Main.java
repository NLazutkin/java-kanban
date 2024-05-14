import manage.Managers;
import manage.TaskManager;
import templates.Epic;
import templates.Subtask;
import templates.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task_1 = taskManager.createTask(new Task("Задача 1", "Задача 1"));
        Task task_2 = taskManager.createTask(new Task("Задача 2", "Задача 2"));
        Task task_3 = taskManager.createTask(new Task("Задача 3", "Задача 3"));
        Epic epic_1 = taskManager.createEpic(new Epic("Эпик 1", "Эпик 1"));
        Epic epic_2 = taskManager.createEpic(new Epic("Эпик 2", "Эпик 2"));
        Subtask subtask_1 = taskManager.createSubtask(new Subtask("Сабтаск 1", "Эпик 1"));
        Subtask subtask_2 = taskManager.createSubtask(new Subtask("Сабтаск 2", "Эпик 1"));
        Subtask subtask_3 = taskManager.createSubtask(new Subtask("Сабтаск 3", "Эпик 1"));
        Subtask subtask_4 = taskManager.createSubtask(new Subtask("Сабтаск 4", "Эпик 1"));
        Subtask subtask_5 = taskManager.createSubtask(new Subtask("Сабтаск 5", "Эпик 2"));
        Subtask subtask_6 = taskManager.createSubtask(new Subtask("Сабтаск 6", "Эпик 2"));
        Subtask subtask_7 = taskManager.createSubtask(new Subtask("Сабтаск 7", "Эпик 2"));
        Subtask subtask_8 = taskManager.createSubtask(new Subtask("Сабтаск 8", "Эпик 2"));
        taskManager.createRelation(epic_1, subtask_1);
        taskManager.createRelation(epic_1, subtask_2);
        taskManager.createRelation(epic_1, subtask_3);
        taskManager.createRelation(epic_1, subtask_4);
        taskManager.createRelation(epic_2, subtask_5);
        taskManager.createRelation(epic_2, subtask_6);
        taskManager.createRelation(epic_2, subtask_7);
        taskManager.createRelation(epic_2, subtask_8);

        task_1 = taskManager.getTaskFromList(task_1.getId());
        task_2 = taskManager.getTaskFromList(task_2.getId());
        task_3 = taskManager.getTaskFromList(task_3.getId());
        epic_1 = taskManager.getEpicFromList(epic_1.getId());
        epic_2 = taskManager.getEpicFromList(epic_2.getId());
        subtask_1 = taskManager.getSubtaskFromList(subtask_1.getId());
        subtask_2 = taskManager.getSubtaskFromList(subtask_2.getId());
        subtask_3 = taskManager.getSubtaskFromList(subtask_3.getId());
        subtask_4 = taskManager.getSubtaskFromList(subtask_4.getId());
        subtask_5 = taskManager.getSubtaskFromList(subtask_5.getId());
        subtask_6 = taskManager.getSubtaskFromList(subtask_6.getId());
        subtask_7 = taskManager.getSubtaskFromList(subtask_7.getId());
        subtask_8 = taskManager.getSubtaskFromList(subtask_8.getId());

        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : taskManager.getEpics()) {
            System.out.println(epic);

            for (Task task : taskManager.getSubtaskByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }

}
