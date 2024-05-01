package templates;

import enums.TaskStatuses;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskCodes = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, int id, TaskStatuses status, ArrayList<Integer> subtaskCodes) {
        super(title, description, id, status);
        this.subtaskCodes = subtaskCodes;
    }

    public ArrayList<Integer> getSubtaskCodes() {
        return subtaskCodes;
    }

    public void addSubtaskCode(int code) {
        subtaskCodes.add(code);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "title='" + super.getTitle() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId()+ '\'' +
                ", status=" + super.getStatus() + '\'' +
                ", subtaskCodes=" + subtaskCodes +
                '}';
    }
}
