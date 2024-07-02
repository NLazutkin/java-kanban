package templates;

import enums.TaskStatuses;
import enums.TaskTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private int id;
    private final String title;
    private final String description;
    private TaskStatuses status = TaskStatuses.NEW;
    private Duration duration = Duration.ZERO;
    private LocalDateTime startTime;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, long duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
    }

    public Task(String title, String description, int id, TaskStatuses status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String title, String description, int id, TaskStatuses status, long duration,
                LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = Duration.ofMinutes(duration);
        this.startTime = startTime;
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

    public Duration getDuration() {
        return duration;
    }

    public long getDurationToMinutes() {
        return duration.toMinutes();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeToString() {
        if (startTime == null) {
            return "null";
        }
        return startTime.format(DATE_TIME_FORMATTER);
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public String getEndTimeToString() {
        if (startTime == null) {
            return "null";
        }
        return getEndTime().format(DATE_TIME_FORMATTER);
    }

    public long getSpentTime() {
        return Duration.between(getStartTime(), LocalDateTime.now()).toMinutes();
    }

    public long getRemainingTime() {
        return Duration.between(LocalDateTime.now(), getEndTime()).toMinutes();
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
                + "title = '" + title + '\''
                + ", description = '" + description + '\''
                + ", id = " + id
                + ", status = " + status
                + ", duration = " + duration.toMinutes()
                + ", startTime = " + getStartTimeToString()
                + ", endTime = " + getEndTimeToString()
                + '}';
    }
}


