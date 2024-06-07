package manage;

public class Node<T> {
    public T task;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, T task, Node<T> next) {
        this.prev = prev;
        this.task = task;
        this.next = next;
    }
}
