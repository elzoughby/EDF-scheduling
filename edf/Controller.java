package edf;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Controller {

    private ObservableList<Task> taskList = FXCollections.observableArrayList();
    private String[] style = {"status-one", "status-two", "status-three", "status-four", "status-five",
            "status-six", "status-seven", "status-eight", "status-nine", "status-ten", "status-eleven",
            "status-twelve"};
    private List<String> styleList;

    @FXML
    private TextField txtET;
    @FXML
    private TextField txtPeriod;
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn columnId;
    @FXML
    private TableColumn columnET;
    @FXML
    private TableColumn columnPeriod;
    @FXML
    private Label lblLCM;
    @FXML
    private TableView<TableItem> scheduleTable;
    @FXML
    private TableColumn columnTime;
    @FXML
    private TableColumn columnTask;
    @FXML
    private TableColumn columnDeadlines;
    @FXML
    private SubScene chartScene;




    @FXML
    private void addTask() {

        taskList.add(new Task(Integer.parseInt(txtET.getText()),Integer.parseInt(txtPeriod.getText())));

        columnET.setCellValueFactory(new PropertyValueFactory<>("eT"));
        columnPeriod.setCellValueFactory(new PropertyValueFactory<>("period"));
        columnId.setCellValueFactory(new PropertyValueFactory<>("name"));

        taskTable.setItems(taskList);
        txtET.setText("");
        txtPeriod.setText("");
        lblLCM.setText(Integer.toString(Scheduler.calcLCM(taskList)));
    }

    @FXML
    private void schedule() {

        styleList = new ArrayList<>(Arrays.asList(style));
        Scheduler.ScheduledTasks scheduledTasks = Scheduler.schedule(taskList);
        List<Task> scheduledTaskList = new ArrayList<>(scheduledTasks.getTaskList());
        List<List<Task>> deadlinesList = new ArrayList<>(scheduledTasks.getDeadlinesList());
        fillTable(scheduledTaskList, deadlinesList);
        TimelineChart chart = drawChart(scheduledTaskList);
        chartScene.setRoot(chart);
    }

    private TimelineChart drawChart(List<Task> scheduledTaskList) {

        List<String> nameList = new ArrayList<>();
        for(Task t : taskList)
            nameList.add(t.getName());

        NumberAxis xAxis = new NumberAxis();
        xAxis.setAutoRanging(false);
        xAxis.setMinorTickCount(5);
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Double.parseDouble(lblLCM.getText()));
        xAxis.setTickUnit(1);
        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setTickLabelGap(10);
        yAxis.setAutoRanging(false);
        yAxis.setCategories(FXCollections.observableArrayList(nameList));

        TimelineChart chart = new TimelineChart(xAxis, yAxis);
        chart.setTitle("Earliest Deadline First Schedule");
        chart.setLegendVisible(false);

        ObservableList<XYChart.Series<Number, String>> chartData = FXCollections.observableArrayList();
        for(Task t : taskList) {
            ObservableList<XYChart.Data<Number, String>> seriesData = FXCollections.observableArrayList();
            String styleClass = getRandomStyle();
            for(int i = 0; i < scheduledTaskList.size(); i++) {
                if(t.equals(scheduledTaskList.get(i)))
                    seriesData.add(new XYChart.Data<>(i, t.getName(), new TimelineChart.ExtraData(styleClass)));
            }
            chartData.add(new XYChart.Series<>(seriesData));
        }
        chart.setData(chartData);
        chart.getStylesheets().add(getClass().getResource("timeline.css").toExternalForm());

        return chart;
    }

    private void fillTable(List<Task> scheduledTaskList, List<List<Task>> scheduledDeadlinesList) {

        ObservableList<TableItem> tableList = FXCollections.observableArrayList();
        for(int i = 0; i < scheduledTaskList.size(); i++)
            tableList.add(new TableItem(i, scheduledTaskList.get(i), scheduledDeadlinesList.get(i)));

        columnTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        columnTask.setCellValueFactory(new PropertyValueFactory<>("task"));
        columnDeadlines.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        scheduleTable.setItems(tableList);

    }

    private String getRandomStyle() {
        Random random = new Random();
        int randomIndex = random.nextInt(styleList.size());
        String randomStyle = styleList.get(randomIndex);
        styleList.remove(randomIndex);
        return randomStyle;
    }

    public static class TableItem {

        private int time;
        private String task;
        private String deadline;

        public TableItem(int time, Task task, List<Task> deadline) {
            this.task = (task != null)? task.toString() : "idle";
            this.deadline = deadline.toString().replaceAll("[\\[\\]]", "");
            this.time = time;
        }

        public int getTime() {
            return time;
        }

        public String getTask() {
            return task;
        }

        public String getDeadline() {
            return deadline;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public void setTask(String task) {
            this.task = task;
        }

        public void setDeadline(String deadline) {
            this.deadline = deadline;
        }
    }

}
