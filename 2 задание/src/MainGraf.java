import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.Connection;

public class MainGraf extends Application {

    private Statement statement;
    private Connection connection;

    @Override
    public void start(Stage primaryStage) {
        try {
            String url = "jdbc:sqlite:komp.db";
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Authorization.fxml"));
            Parent root = loader.load();
            UserLoginController controller = loader.getController();
            controller.setStatement(statement);

            primaryStage.setTitle("Komputer Company");
            primaryStage.setScene(new Scene(root, 514, 635));
            primaryStage.setOnCloseRequest(event -> {
                closeDatabaseConnection();
            });
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeDatabaseConnection() {
        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}