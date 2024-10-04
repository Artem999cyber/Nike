import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class BasketController {

    @FXML
    private TableView<Basket> orderTable;
    @FXML

    private Button viewCartButton;
    @FXML
    private TableColumn<Basket, Integer> idColumn;

    @FXML
    private TableColumn<Basket, String> nameColumn;

    @FXML
    private TableColumn<Basket, Double> priceColumn;

    @FXML
    private TableColumn<Basket, Integer> lotColumn;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Label thankYouLabel;  // Новая метка для сообщения о заказе

    @FXML
    private Button logoutButton;

    @FXML
    private Button orderButton;  // Новая кнопка для оформления заказа

    private Statement statement;

    private ObservableList<Basket> cart = FXCollections.observableArrayList();

    public void setStatement(Statement statement) {
        this.statement = statement;
        if (this.statement != null) {
            loadData();
        } else {
            System.err.println("Ошибка:  не передан в BasketController.");
        }
    }

    @FXML
    public void initialize() {
        orderTable.setEditable(true);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        lotColumn.setCellValueFactory(new PropertyValueFactory<>("lot"));

        // Редактирование количества товаров
        lotColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        lotColumn.setOnEditCommit(event -> {
            Basket basket = event.getRowValue();
            basket.setLot(event.getNewValue());
            updateTotalPrice();
        });

        orderTable.setItems(cart);
        updateTotalPrice();
        //thankYouLabel.setVisible(false); // Скрыть метку при инициализации
    }

    public void loadData() {
        cart.clear();
        try {
            ResultSet rs = statement.executeQuery("SELECT * FROM Basket");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int lot = rs.getInt("lot");
                cart.add(new Basket(id, name, price, lot));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
        }
        updateTotalPrice();
    }

    public void updateTotalPrice() {
        double total = cart.stream()
                .mapToDouble(basket -> basket.getPrice() * basket.getLot())
                .sum();
        totalPriceLabel.setText(String.format("Итого: %.2f руб.", total));
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();

    }
    @FXML
    private void handleOrderButtonAction() {
        // Добавить логику для оформления заказа
       // thankYouLabel.setVisible(true);  // Показать благодарность после заказа

        showAlert("МИР    Эквайринговая система    БАНКА   ", "Платежная система подключена,     Оплата Завершина!        ЗАКАЗ Оплачен !!!");
        System.err.println("Оплата прошла УСПЕШНО !!!" );
    }



    @FXML
//    private void handleLogoutButtonAction() {
//        Stage currentStage = (Stage) logoutButton.getScene().getWindow();
//        currentStage.close();
//
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuComputer.fxml"));
//            Parent root = loader.load();
//            Stage menuStage = new Stage();
//            menuStage.setTitle("Меню");
//            menuStage.setScene(new Scene(root));
//            menuStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Не удалось загрузить интерфейс меню.");
//        }
//    }

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

    public void handleViewCart() {
        Stage currentStage = (Stage) viewCartButton.getScene().getWindow();
        currentStage.close();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Basket.fxml"));
            Parent root = loader.load();
            Stage menuStage = new Stage();
            menuStage.setTitle("корзина");
            menuStage.setScene(new Scene(root));
            menuStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Не удалось загрузить интерфейс меню.");
        }
    }
    }
