package controller;

import Db.DbConnection;
import com.jfoenix.controls.JFXCheckBox;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import model.completedTask;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@AllArgsConstructor
@NoArgsConstructor
public class todoController implements Initializable {

    @FXML
    private TableColumn<completedTask, LocalDate> colDate;

    @FXML
    private Button btnAddTask;

    @FXML
    private Button btnReload;

    @FXML
    private JFXCheckBox chckDone1;

    @FXML
    private JFXCheckBox chckDone2;

    @FXML
    private JFXCheckBox chckDone3;

    @FXML
    private JFXCheckBox chckDone4;

    @FXML
    private JFXCheckBox chckDone5;

    @FXML
    private TableView<completedTask> tblCompletedTask;

    @FXML
    private TextField txtAddTaskDesc;

    @FXML
    private TextField txtAddTaskTitle;

    @FXML
    private TextArea txtArea1;

    @FXML
    private TextArea txtArea2;

    @FXML
    private TextArea txtArea3;

    @FXML
    private TextArea txtArea4;

    @FXML
    private TextArea txtArea5;

    @FXML
    private DatePicker txtDate;

    @FXML
    private TableColumn<completedTask, String> colDesc;

    @FXML
    private TableColumn<completedTask, Integer> colId;

    @FXML
    private TableColumn<completedTask, String> colTitle;

    @FXML
    private TextField txtLiveDate;

    @FXML
    private TextField txtLiveTime;

    private int currentTextAreaIndex = 0;
    private TextArea[] textAreas;
    public LocalDate doneDate = LocalDate.now();

    @FXML
    void btnAddTaskOnAction(ActionEvent event) {
        String taskTitle = txtAddTaskTitle.getText();
        String taskDescription = txtAddTaskDesc.getText();
        Date taskDate = Date.valueOf(txtDate.getValue());
        String SQL = "INSERT INTO tasks (task_title, task_description, completion_date) VALUES (?, ?, ?)";
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist", "root", "12345");
            PreparedStatement psTm = connection.prepareStatement(SQL);

            psTm.setString(1, taskTitle);
            psTm.setString(2, taskDescription);
            psTm.setDate(3, taskDate);

            int dataEntered = psTm.executeUpdate();
            if (dataEntered > 0) {
                textAreas[currentTextAreaIndex].setText(taskTitle);
                currentTextAreaIndex++;
                if (currentTextAreaIndex >= textAreas.length) {
                    currentTextAreaIndex = 0;
                }
                clearInputFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnReloadOnAction(ActionEvent event) {
        checkAndAddCompletedTask(chckDone1, txtArea1);
        checkAndAddCompletedTask(chckDone2, txtArea2);
        checkAndAddCompletedTask(chckDone3, txtArea3);
        checkAndAddCompletedTask(chckDone4, txtArea4);
        checkAndAddCompletedTask(chckDone5, txtArea5);

        updateCompletedTaskTable();
    }

    private void checkAndAddCompletedTask(JFXCheckBox checkBox, TextArea textArea) {
        if (checkBox.isSelected()) {
            String taskTitle = textArea.getText();
            CompletedTaskList(taskTitle);
            textArea.clear();
            checkBox.setSelected(false);
        }
    }

    private void CompletedTaskList(String taskTitle) {
        int taskId = getTaskIdByTitle(taskTitle);
        String taskDescription = getTaskDescByTitle(taskTitle);

        if (taskId != -1 && taskDescription != null) {
            completedTask newCompletedTask = new completedTask(taskId, taskTitle, taskDescription, doneDate);

            // Add to DbConnection for other functionalities
            DbConnection.getInstance().addTask(newCompletedTask);

            // Insert into completedHistory
            insertIntoCompletedHistory(newCompletedTask);

            updateCompletedTaskTable();
            System.out.println("Task completed and added to history.");
        } else {
            System.out.println("Task not found.");
        }
    }

    private void insertIntoCompletedHistory(completedTask completedTask) {
        String SQL = "INSERT INTO completedHistory (task_id, task_title, task_description, done_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist", "root", "12345");
             PreparedStatement ps = connection.prepareStatement(SQL)) {

            ps.setInt(1, completedTask.getTaskId());
            ps.setString(2, completedTask.getTaskTitle());
            ps.setString(3, completedTask.getTaskDescription());
            ps.setDate(4, Date.valueOf(completedTask.getDoneDate()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCompletedTaskTable() {
        List<completedTask> completedTaskList = DbConnection.getInstance().getConnection();
        ObservableList<completedTask> completedTaskObservableList = FXCollections.observableArrayList(completedTaskList);
        tblCompletedTask.setItems(completedTaskObservableList);
    }

    @FXML
    void chckDone1OnAction(ActionEvent event) {}

    @FXML
    void chckDone2OnAction(ActionEvent event) {}

    @FXML
    void chckDone3OnAction(ActionEvent event) {}

    @FXML
    void chckDone4OnAction(ActionEvent event) {}

    @FXML
    void chckDone5OnAction(ActionEvent event) {}

    private void clearInputFields() {
        txtAddTaskTitle.clear();
        txtAddTaskDesc.clear();
        txtDate.setValue(null);
    }

    private int getTaskIdByTitle(String taskTitle) {
        String SQL = "SELECT task_id FROM tasks WHERE task_title = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist", "root", "12345");
             PreparedStatement psTm = connection.prepareStatement(SQL)) {

            psTm.setString(1, taskTitle);
            ResultSet rs = psTm.executeQuery();
            if (rs.next()) {
                return rs.getInt("task_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String getTaskDescByTitle(String taskTitle) {
        String SQL = "SELECT task_description FROM tasks WHERE task_title = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist", "root", "12345");
             PreparedStatement psTm = connection.prepareStatement(SQL)) {

            psTm.setString(1, taskTitle);
            ResultSet rs = psTm.executeQuery();
            if (rs.next()) {
                return rs.getString("task_description");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textAreas = new TextArea[]{txtArea1, txtArea2, txtArea3, txtArea4, txtArea5};

        chckDone1.setText("");
        chckDone2.setText("");
        chckDone3.setText("");
        chckDone4.setText("");
        chckDone5.setText("");

        colId.setCellValueFactory(new PropertyValueFactory<>("taskId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("taskTitle"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("taskDescription"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("doneDate"));

        updateCompletedTaskTable();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            txtLiveDate.setText(currentDate.toString());
            txtLiveTime.setText(currentTime.format(timeFormatter));
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}
