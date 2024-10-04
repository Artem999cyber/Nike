import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class DBConnect {

    private static final String DB_URL = "jdbc:sqlite:comp.db";

    public static DefaultTableModel loadTableData(String tableName) {
        DefaultTableModel model = null;

        createComputerPartsTable();
        createClientsTable();

        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }

            model = new DefaultTableModel(columnNames, 0);
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public static void createComputerPartsTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                String sql = "CREATE TABLE IF NOT EXISTS computer_parts (" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "part_number TEXT NOT NULL," + "name TEXT NOT NULL," + "price TEXT," + "year_of_manufacture TEXT," + "additional_info TEXT," + "image_url TEXT)";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createClientsTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                String sql = "CREATE TABLE IF NOT EXISTS clients (" + "id INTEGER PRIMARY KEY AUTOINCREMENT," + "order_number TEXT NOT NULL," + "name TEXT NOT NULL," + "phone TEXT NOT NULL," + "additional_info TEXT)";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
