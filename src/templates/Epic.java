package templates;

import enums.TaskStatuses;
import enums.TaskTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private LocalDateTime endTime;
    private List<Integer> subtaskCodes = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, int id, TaskStatuses status, List<Integer> subtaskCodes) {
        super(title, description, id, status);
        this.subtaskCodes = subtaskCodes;
    }

    public Epic(String title, String description, int id, TaskStatuses status, List<Integer> subtaskCodes,
                long duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(title, description, id, status, duration, startTime);
        this.subtaskCodes = subtaskCodes;
        this.endTime = endTime;
    }

    public List<Integer> getSubtaskCodes() {
        return subtaskCodes;
    }

    public void setSubtaskCodes(List<Integer> subtaskCodes) {
        this.subtaskCodes = subtaskCodes;
    }

    public TaskTypes getType() {
        return TaskTypes.EPIC;
    }

    public void addSubtaskCode(int code) {
        if ((code != getId()) && !subtaskCodes.contains(code)) {
            subtaskCodes.add(code);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getEndTimeToString() {
        if (endTime == null) {
            return "null";
        }
        return endTime.format(DATE_TIME_FORMATTER);
    }

    @Override
    public String toString() {
        return "Epic {"
                + "title = '" + super.getTitle() + '\''
                + ", description = '" + super.getDescription() + '\''
                + ", id = " + super.getId()
                + ", status = " + super.getStatus()
                + ", duration = " + super.getDurationToString()
                + ", startTime = " + super.getStartTimeToString()
                + ", endTime = " + getEndTimeToString()
                + ", subtaskCodes = " + subtaskCodes
                + '}';
    }
}
