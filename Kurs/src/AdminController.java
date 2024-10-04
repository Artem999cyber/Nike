import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminController {

    private Statement statement;

    @FXML
    private TableView<Admin> adminTable;

    @FXML
    private TableColumn<Admin, Integer> idColumn;

    @FXML
    private TableColumn<Admin, String> roleColumn;

    @FXML
    private TableColumn<Admin, String> nicknameColumn;

    @FXML
    private TableColumn<Admin, String> loginColumn;

    @FXML
    private TableColumn<Admin, String> passwordColumn;

    @FXML
    private Button logoutButton;

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
    }

    public void loadData() {
        loadAdminData();
    }

    public void loadAdminData() {
        ObservableList<Admin> data = FXCollections.observableArrayList();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String role = resultSet.getString("role");
                String nickname = resultSet.getString("nickname");
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                data.add(new Admin(id, role, nickname, login, password));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Общая ошибка при загрузке данных администраторов: " + e.getMessage());
            e.printStackTrace();
        }
        adminTable.setItems(data);
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