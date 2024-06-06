package templates;

import enums.TaskStatuses;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description) {
        super(title, description);
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

    @Override
    public String toString() {
        return "Subtask {"
                + "title = '" + super.getTitle() + '\''
                + ", description = '" + super.getDescription() + '\''
                + ", id = " + super.getId()+ '\''
                + ", status = " + super.getStatus() + '\''
                + ", epicID = " + epicId
                + '}';
    }
}
