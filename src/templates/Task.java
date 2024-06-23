package templates;

import enums.TaskStatuses;
import enums.TaskTypes;

import java.util.Objects;

public class Task {
    private int id;
    private final String title;
    private final String description;
    private TaskStatuses status = TaskStatuses.NEW;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, int id, TaskStatuses status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatuses getStatus() {
        return status;
    }

    public TaskTypes getType() {
        return TaskTypes.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title)
                && Objects.equals(description, task.description)
                && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }

    @Override
    public String toString() {
        return "Task {"
                + "title='" + title + '\''
                + ", description='" + description + '\''
                + ", id=" + id
                + ", status=" + status
                + '}';
    }
}


