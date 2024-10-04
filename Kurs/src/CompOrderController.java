import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.control.SelectionMode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Alert;
import java.sql.PreparedStatement;

public class CompOrderController {

    private Statement statement;

    @FXML
    private Button logoutButton;

    private Button viewCartButton;
    @FXML
    private TableView<CompOrder> orderTable;

    @FXML
    private TableColumn<CompOrder, Integer> idColumn;

    @FXML
    private TableColumn<CompOrder, String> nameColumn;

    @FXML
    private TableColumn<CompOrder, Double> priceColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button addToCartButton;

    private ObservableList<CompOrder> data;
    private ObservableList<CompOrder> cart = FXCollections.observableArrayList();

    public void setStatement(Statement statement) {
        this.statement = statement;
        if (this.statement != null) {
            clearBasketTable(); // Очистить корзину сразу после установки statement
            loadData();
        } else {
            System.err.println("Ошибка: Statement не передан в BasketController.");
        }
    }


    private void clearBasketTable() {
        try {
            if (statement != null) {
                statement.executeUpdate("DELETE FROM basket");
                System.out.println("Корзина очищена успешно.");
            } else {
                System.err.println("Ошибка: Statement не инициализирован.");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при очистке корзины: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        orderTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        addToCartButton.setDisable(true);
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            addToCartButton.setDisable(newSelection == null);
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(newValue);
        });
    }


    public void loadData() {
        data = FXCollections.observableArrayList();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM comp");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                data.add(new CompOrder(id, name, price));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            e.printStackTrace();
        }
        orderTable.setItems(data);
    }

    @FXML
    public void handleAddToCart() {
        ObservableList<CompOrder> selectedOrders = orderTable.getSelectionModel().getSelectedItems();
        if (selectedOrders != null && !selectedOrders.isEmpty()) {
            try {
                for (CompOrder order : selectedOrders) {
                    String sql = "INSERT INTO basket (id, name, price, lot) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = statement.getConnection().prepareStatement(sql);
                    pstmt.setInt(1, order.getId());
                    pstmt.setString(2, order.getName());
                    pstmt.setDouble(3, order.getPrice());
                    pstmt.setInt(4, 1);
                    pstmt.executeUpdate();
                }
                showAlert("Добавление в корзину", "Товар успешно добавлен в корзину!");
            } catch (SQLException e) {
                System.err.println("Ошибка при добавлении в корзину: " + e.getMessage());
                showAlert("Ошибка базы данных", "Не удалось добавить товар в корзину.");
            }
        } else {
            showAlert("Выбор продуктов", "Пожалуйста, выберите один или несколько продуктов в таблице перед добавлением в корзину.");
        }
    }

    private void filterTable(String filter) {
        if (filter == null || filter.isEmpty()) {
            orderTable.setItems(data);
            return;
        }

        ObservableList<CompOrder> filteredData = FXCollections.observableArrayList();
        for (CompOrder order : data) {
            if (order.getName().toLowerCase().contains(filter.toLowerCase())) {
                filteredData.add(order);
            }
        }
        orderTable.setItems(filteredData);
    }

    @FXML
    private void handleViewCart() throws IOException {
        System.out.println("Проверка  перед передачей: " + (statement != null ? "заказ Сформирован" : "не инициализирован"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Basket.fxml"));
        Parent root = loader.load();
        BasketController controller = loader.getController();
        controller.setStatement(statement);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Корзина");

        Stage currentStage = (Stage) addToCartButton.getScene().getWindow();
        currentStage.close();

        stage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();

    }

    public void handleLogoutButtonAction() {
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
        }}

//    public void setViewCartButton(Button viewCartButton) {
//        this.viewCartButton = viewCartButton;

}
    

