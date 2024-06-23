package test.manage.HistoryManager;

import enums.TaskStatuses;
import manage.HistoryManager.HandMadeLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import templates.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HandMadeLinkedListTest {
    private static HandMadeLinkedList handMadeLinkedList;
    private static Task task1;
    private static Task task2;
    private static Task task3;

    @BeforeEach
    void beforeEach() {
        handMadeLinkedList = new HandMadeLinkedList();
    }

    void prepare3TasksBeforeTests() {
        task1 = new Task("Задача 1", "Задача 1", 1, TaskStatuses.NEW);
        task2 = new Task("Задача 2", "Задача 2", 2, TaskStatuses.NEW);
        task3 = new Task("Задача 3", "Задача 3", 3, TaskStatuses.NEW);

        handMadeLinkedList.add(task1);
        handMadeLinkedList.add(task2);
        handMadeLinkedList.add(task3);
    }

    @Test
    void checkAddTasksToHandMadeLinkedList() {
        Task task1 = new Task("Задача 1", "Задача 1", 1, TaskStatuses.NEW);
        handMadeLinkedList.add(task1);

        assertNotNull(handMadeLinkedList.getTasks(), "Ошибка добавления элементов в связанный список!");
    }

    @Test
    void checkAddTasksToTailOfHandMadeLinkedList() {
        Task task1 = new Task("Задача 1", "Задача 1", 1, TaskStatuses.NEW);
        handMadeLinkedList.add(task1);

        task2 = new Task("Задача 2", "Задача 2", 2, TaskStatuses.NEW);
        handMadeLinkedList.add(task2);

        assertEquals(task2, handMadeLinkedList.getLast(), "Ошибка добавления элемента в конец списка!");
    }

    @Test
    void checkAddTasksToHeadOIfHandMadeLinkedList() {
        Task task1 = new Task("Задача 1", "Задача 1", 1, TaskStatuses.NEW);
        handMadeLinkedList.add(task1);

        task2 = new Task("Задача 2", "Задача 2", 2, TaskStatuses.NEW);
        handMadeLinkedList.addFirst(task2);

        assertEquals(task2, handMadeLinkedList.getFirst(), "Ошибка добавления элемента в начало списка!");
    }

    @Test
    void checkRemoveFromHandMadeLinkedList() {
        prepare3TasksBeforeTests();

        handMadeLinkedList.remove(task3.getId());

        assertNotEquals(task3, handMadeLinkedList.getLast(), "Первый элемент в двунаправленном списке не " +
                "соответствует ожидаемому!");
    }

    @Test
    void checkHandMadeLinkedListGetLinked() {
        prepare3TasksBeforeTests();

        List<Task> list = handMadeLinkedList.getLinkedTasks();

        assertEquals(task1, list.getFirst(), "Первый элемент в двунаправленном списке не " +
                "соответствует ожидаемому!");
        assertEquals(task3, list.getLast(), "Последний элемент в двунаправленном списке не " +
                "соответствует ожидаемому!");
    }

    @Test
    void checkHandMadeLinkedListGetMapAfterChanges() {
        prepare3TasksBeforeTests();

        handMadeLinkedList.add(task1);
        handMadeLinkedList.add(task2);

        List<Task> list = handMadeLinkedList.getTasks();

        assertEquals(task1, list.getFirst(), "Первый элемент в списке после изменений" +
                "не соответствует ожидаемому!");
        assertEquals(task3, list.getLast(), "Последний элемент в списке после изменений" +
                "не соответствует ожидаемому!");
    }

    @Test
    void checkHandMadeLinkedListGetLinkedAfterChanges() {
        prepare3TasksBeforeTests();

        handMadeLinkedList.add(task1);
        handMadeLinkedList.add(task2);

        List<Task> list = handMadeLinkedList.getLinkedTasks();

        assertEquals(task3, list.getFirst(), "Первый элемент в двунаправленном списке после изменений " +
                "не соответствует ожидаемому!");
        assertEquals(task2, list.getLast(), "Последний элемент в двунаправленном списке не " +
                "соответствует ожидаемому!");
    }

    @Test
    void checkHandMadeLinkedListGetLinkedReverse() {
        prepare3TasksBeforeTests();

        List<Task> list = handMadeLinkedList.getLinkedTasksReverse();

        assertEquals(task3, list.getFirst(), "Первый элемент в обратном двунаправленном списке не " +
                "соответствует ожидаемому!");
        assertEquals(task1, list.getLast(), "Последний элемент в обратном двунаправленном списке не " +
                "соответствует ожидаемому!");
    }

    @Test
    void checkFirstElementRespondExpected() {
        prepare3TasksBeforeTests();

        assertEquals(task1, handMadeLinkedList.getFirst(), "Первый элемент в списке не соответствует " +
                "ожидаемому!");
    }

    @Test
    void checkLastElementRespondExpected() {
        prepare3TasksBeforeTests();

        assertEquals(task3, handMadeLinkedList.getLast(), "Последний элемент в списке не соответствует " +
                "ожидаемому!");
    }

    @Test
    void checkSizeOfHandMadeLinkedList() {
        prepare3TasksBeforeTests();

        assertEquals(3, handMadeLinkedList.size(), "Связанный список должен содержать 0 элем.!");
    }

    @Test
    void checkHandMadeLinkedListIsEmpty() {
        assertTrue(handMadeLinkedList.isEmpty(), "Связанный список не пустой!");
    }

    @Test
    void checkHandMadeLinkedListNotIsEmpty() {
        prepare3TasksBeforeTests();

        assertFalse(handMadeLinkedList.isEmpty(), "Ошибка добавления элементов в связанный список!");
    }

}
