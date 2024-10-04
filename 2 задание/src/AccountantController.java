import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;

public class AccountantController {

    private Statement statement;

    @FXML
    private TableView<economic> economicTable;

    @FXML
    private TableColumn<economic, Integer> idColumn;

    @FXML
    private TableColumn<economic, String> dateColumn;

    @FXML
    private TableColumn<economic, Double> revenueColumn;

    @FXML
    private TableColumn<economic, Double> expensesColumn;

    @FXML
    private TableColumn<economic, Double> profitColumn;

    @FXML
    private BarChart<String, Number> profitChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private Button logoutButton;

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        expensesColumn.setCellValueFactory(new PropertyValueFactory<>("expenses"));
        profitColumn.setCellValueFactory(new PropertyValueFactory<>("profit"));

        xAxis.setLabel("Дата");
        yAxis.setLabel("Прибыль");
    }

    public void loadData() {
        loadeconomicData();
        loadProfitData();
    }




    public void loadeconomicData() {
        ObservableList<economic> data = FXCollections.observableArrayList();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM economic");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String date = resultSet.getString("date");
                double revenue = resultSet.getDouble("revenue");
                double expenses = resultSet.getDouble("expenses");
                double profit = resultSet.getDouble("profit");
                data.add(new economic(id, date, revenue, expenses, profit));
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке данных в таблицу: " + e.getMessage());
        }
        economicTable.setItems(data);
    }

    public void loadProfitData() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        ObservableList<String> categories = FXCollections.observableArrayList();

        try {
            ResultSet resultSet = statement.executeQuery("SELECT date, profit FROM economic");
            while (resultSet.next()) {
                String date = resultSet.getString("date");
                double profit = resultSet.getDouble("profit");
                categories.add(date);
                series.getData().add(new XYChart.Data<>(date, profit));
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке данных в гистограмму: " + e.getMessage());
        }
        xAxis.setCategories(categories);
        profitChart.getData().add(series);
        profitChart.setLegendVisible(false);
    }

    @FXML
    private void handleLogoutButtonAction() {
        try {
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Authorization.fxml"));
            Parent root = loader.load();
            UserLoginController loginController = loader.getController();
            loginController.setStatement(statement);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Авторизация");
            stage.show();
        } catch (IOException e) {
            System.err.println("Ошибка при открытии окна авторизации: " + e.getMessage());
            e.printStackTrace();
        }
    }
}