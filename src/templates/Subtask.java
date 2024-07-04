package templates;

import enums.TaskStatuses;
import enums.TaskTypes;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int epicId, long duration, LocalDateTime startTime) {
        super(title, description, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int id, TaskStatuses status, int epicId, long duration,
                   LocalDateTime startTime) {
        super(title, description, id, status, duration, startTime);
        this.epicId = epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId != getId()) {
            this.epicId = epicId;
        }
    }

    public int getEpicId() {
        return epicId;
    }

    public TaskTypes getType() {
        return TaskTypes.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask {"
                + "title = '" + super.getTitle() + '\''
                + ", description = '" + super.getDescription() + '\''
                + ", id = " + super.getId()
                + ", status = " + super.getStatus()
                + ", duration = " + super.getDurationToMinutes()
                + ", startTime = " + super.getStartTimeToString()
                + ", endTime = " + super.getEndTimeToString()
                + ", epicID = " + epicId
                + '}';
    }
}
