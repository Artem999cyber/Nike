import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class UserLogin {
    private static String lastLogin = "";
    private static String lastRole = "";

    public static String[] loginUser(Statement statement, Scanner scanner) {
        System.out.print("Введите логин: ");
        String login = scanner.next();
        System.out.print("Введите пароль: ");
        String password = scanner.next();

        try {
            ResultSet loginResult = statement.executeQuery("SELECT * FROM users WHERE login = '" + login
                    + "' AND password = '" + password + "'");
            if (loginResult.next()) {
                System.out.println("Вход выполнен успешно!");
                lastLogin = login;
                lastRole = loginResult.getString("role");
                return new String[] {lastLogin, lastRole};
            } else {
                System.out.println("Неверный логин или пароль. Попробуйте ещё раз.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при выполнении запроса: " + e.getMessage());
            return null;
        }
    }

    public static String getLastLogin() {
        return lastLogin;
    }

    public static String getLastRole() {
        return lastRole;
    }
}