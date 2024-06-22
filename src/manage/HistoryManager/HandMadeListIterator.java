package manage.HistoryManager;

import templates.Task;

import java.util.*;

public class HandMadeListIterator implements Iterator<Node<Task>> {
    private Node<Task> current;

    HandMadeListIterator(Node<Task> startNode) {
        this.current = startNode;
    }

    public boolean hasNext() {
        return current != null;
    }

    public Node<Task> next() {
        if (!hasNext()) throw new NoSuchElementException();
        Node<Task> node = current;
        current = current.next;
        return node;
    }

    public Node<Task> prev() {
        if (!hasNext()) throw new NoSuchElementException();
        Node<Task> node = current;
        current = current.prev;
        return node;
    }

}
