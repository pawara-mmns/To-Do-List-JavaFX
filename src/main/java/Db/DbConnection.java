package Db;

import model.completedTask;

import java.util.ArrayList;
import java.util.List;

public class DbConnection {
    private static DbConnection instance;
    private List<completedTask> connection;

    private DbConnection() {
        connection = new ArrayList<>();
    }

    public List<completedTask> getConnection() {
        return connection;
    }

    public static DbConnection getInstance() {
        return null == instance ? instance = new DbConnection() : instance;
    }

    public void addTask(completedTask task) {
        connection.add(task);
    }
}
