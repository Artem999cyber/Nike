import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import javafx.scene.control.Alert;

public class UserLoginController {

    private Statement statement;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @FXML
    private void handleLoginButtonAction() {
        if (statement == null) {
            showAlert("Initialization Error", "Database connection not initialized.");
            return; // Прекращаем выполнение, если statement не инициализирован
        }

        String login = loginField.getText();
        String password = passwordField.getText();

        try {
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM users WHERE login='" + login + "' AND password='" + password + "'"
            );
            if (resultSet.next()) {
                String role = resultSet.getString("role");
                if ("user".equals(role)) {
                    openUserWindow();
                } else if ("accountant".equals(role)) {
                    openAccountantWindow();
                } else if ("admin".equals(role)) {
                    openAdminWindow();
                }
            } else {
                showAlert("Login Error", "Incorrect username or password.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
        }
    }

    private void openWindow(String fxmlFile, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.close();
    }

    private void openAdminWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Admin.fxml"));
        Parent root = loader.load();
        AdminController controller = loader.getController();
        controller.setStatement(statement);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Личный кабинет администратора");
        stage.show();
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.close();
        controller.loadData();
    }

    private void openAccountantWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Accountant.fxml"));
        Parent root = loader.load();
        AccountantController controller = loader.getController();
        controller.setStatement(statement);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Личный кабинет Экономиста");
        stage.show();
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.close();
        controller.loadData();
    }

    private void openUserWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuComputer.fxml"));
        Parent root = loader.load();
        CompOrderController controller = loader.getController();
        controller.setStatement(statement);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Личный кабинет пользователя");
        stage.show();
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.close();
        controller.loadData();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleRegisterButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Registration.fxml"));
            Parent root = loader.load();
            RegistrationController registrationController = loader.getController();
            registrationController.setStatement(statement);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Регистрация");
            stage.show();
        } catch (Exception e) {
            System.err.println("Ошибка при открытии окна регистрации: " + e.getMessage());
        }
    }
}