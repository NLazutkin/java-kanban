package manage;

import templates.Task;
import java.util.*;

public class HandMadeLinkedList {
    private Node<Task> head;
    private Node<Task> tail;
    private final Map<Integer, Node<Task>> nodeHashMap = new HashMap<>();

    private Node<Task> linkFirst(Task task) {
        final Node<Task> oldHead = head;
        final Node<Task> newNode = new Node<>(null, task, oldHead);
        head = newNode;
        if (oldHead == null) {
            tail = newNode;
        } else {
            oldHead.prev = newNode;
        }

        return newNode;
    }

    private Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }

        return newNode;
    }

    private void removeNode(Node<Task> node) {
        if (head == null || node == null) {
            return;
        }

        if (head.task.equals(node.task)) {
            head = node.next;
        }

        if (tail.task.equals(node.task)) {
            tail = node.prev;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        }
    }

    public void addFirst(Task task) {
        int taskId = task.getId();
        if (!nodeHashMap.isEmpty() && nodeHashMap.containsKey(taskId)) {
            removeNode(nodeHashMap.get(taskId));
        }
        nodeHashMap.put(taskId, linkFirst(task));
    }

    public void add(Task task) {
        int taskId = task.getId();
        if (!nodeHashMap.isEmpty() && nodeHashMap.containsKey(taskId)) {
            removeNode(nodeHashMap.get(taskId));
        }
        nodeHashMap.put(taskId, linkLast(task));
    }

    public void remove(int id) {
        if (!nodeHashMap.isEmpty() && nodeHashMap.containsKey(id)) {
            removeNode(nodeHashMap.get(id));
            nodeHashMap.remove(id);
        }
    }

    public List<Task> getTasks() {
        List<Task> listTasks = new ArrayList<>();
        for (Node<Task> node : nodeHashMap.values()) {
            listTasks.add(node.task);
        }

        return listTasks;
    }

    public List<Task> getLinkedTasks() {
        List<Task> listTasks = new ArrayList<>();
        HandMadeListIterator nodesIter = new HandMadeListIterator(head);
        while (nodesIter.hasNext()) {
            listTasks.add(nodesIter.next().task);
        }

        return listTasks;
    }

    public List<Task> getLinkedTasksReverse() {
        List<Task> listTasks = new ArrayList<>();
        HandMadeListIterator nodesIter = new HandMadeListIterator(tail);
        while (nodesIter.hasNext()) {
            listTasks.add(nodesIter.prev().task);
        }

        return listTasks;
    }

    public Task getFirst() {
        final Node<Task> curHead = head;
        if (curHead == null) {
            throw new NoSuchElementException();
        }

        return head.task;
    }

    public Task getLast() {
        final Node<Task> curTail = tail;
        if (curTail == null){
            throw new NoSuchElementException();
        }

        return tail.task;
    }

    public boolean isEmpty() {
        return nodeHashMap.isEmpty();
    }

    public int size() {
        return nodeHashMap.size();
    }
}