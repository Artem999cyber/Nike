import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Registration {
    public static void registerUser(Statement statement, Scanner scanner) throws SQLException {
        System.out.println("\nРегистрация");
        System.out.print("Никнейм: ");
        String nickname = scanner.next();
        System.out.print("Логин: ");
        String login = scanner.next();
        System.out.print("Пароль: ");
        String password = scanner.next();

        String sql = "INSERT INTO users (role, nickname, login, password) VALUES " +
                "('user', '" + nickname + "', '" + login + "', '" + password + "')";
        statement.executeUpdate(sql);

        System.out.println("Регистрация прошла успешно!");
    }
}