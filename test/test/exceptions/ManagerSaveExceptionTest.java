package test.exceptions;

import exceptions.ManagerSaveException;
import manage.TaskManager.FileBackedTaskManager;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ManagerSaveExceptionTest {

    @Test
    void fileIsDirectoryTest() {
        File file = new File("resources/");

        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(file),
                "Ошибка проверки на содержание пути в переменной вместо файла");
    }
}
