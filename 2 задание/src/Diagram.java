import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Diagram {
    public static void displayDiagram(Statement statement) {
        try {
            ResultSet economicResult = statement.executeQuery("SELECT * FROM economic");
            System.out.println("\nДиаграмма прибыли:");
            while (economicResult.next()) {
                String date = economicResult.getString("date");
                double profit = economicResult.getDouble("profit");
                System.out.printf("%s | ", date);
                for (int i = 0; i < (int) profit; i += 1000) { // 1000 единиц прибыли = 1 символу
                    System.out.print("#");
                }
                System.out.println(" (" + profit + ")");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при выполнении запроса: " + e.getMessage());
        }
    }
}