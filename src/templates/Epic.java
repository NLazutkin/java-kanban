package templates;

import enums.TaskStatuses;
import enums.TaskTypes;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskCodes = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, int id, TaskStatuses status, List<Integer> subtaskCodes) {
        super(title, description, id, status);
        this.subtaskCodes = subtaskCodes;
    }

    public List<Integer> getSubtaskCodes() {
        return subtaskCodes;
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
    public String toString() {
        return "Epic {"
                + "title = '" + super.getTitle() + '\''
                + ", description = '" + super.getDescription() + '\''
                + ", id = " + super.getId()
                + ", status = " + super.getStatus()
                + ", subtaskCodes = " + subtaskCodes
                + '}';
    }
}
