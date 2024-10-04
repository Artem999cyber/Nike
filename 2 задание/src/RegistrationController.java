import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class RegistrationController {

    @FXML
    private TextField nicknameField;

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    private Statement statement;

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    @FXML
    private void registerUser() {
        String nickname = nicknameField.getText();
        String login = loginField.getText();
        String password = passwordField.getText();

        String sql = "INSERT INTO users (role, nickname, login, password) VALUES " +
                "('user', '" + nickname + "', '" + login + "', '" + password + "')";

        try {
            statement.executeUpdate(sql);
            System.out.println("Регистрация прошла успешно!");

            // Переход на окно авторизации
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Authorization.fxml"));
                Parent root = loader.load();
                UserLoginController loginController = loader.getController();
                loginController.setStatement(statement);
                Stage stage = (Stage) registerButton.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Авторизация");
            } catch (IOException e) {
                System.err.println("Ошибка при загрузке окна авторизации: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при регистрации: " + e.getMessage());
        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleOrderButtonAction() {
        // Добавить логику для оформления заказа
        // thankYouLabel.setVisible(true);  // Показать благодарность после заказа

        showAlert("Лицензия принадлежит Васильеву А В", "Лицензия принадлежит Васильеву А В");
        System.err.println("Оплата прошла УСПЕШНО !!!" );
    }
    }
