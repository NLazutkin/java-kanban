package templates;

import enums.TaskStatuses;
import enums.TaskTypes;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int id, TaskStatuses status, int epicId) {
        super(title, description, id, status);
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
                + ", epicID = " + epicId
                + '}';
    }
}
