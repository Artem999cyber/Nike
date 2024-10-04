import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Analytics extends JFrame {

    private static final String DB_URL = "jdbc:sqlite:comp.db";

    public Analytics() {
        setTitle("Analytics window");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        // --- 1
        DefaultCategoryDataset datasetForPartsByPrice = createDatasetForPartsByPrice();
        JFreeChart chartForPartsByPrice = ChartFactory.createBarChart("Total Price of Computer Parts by Name", "Part Name", "Total Price", datasetForPartsByPrice);

        ChartPanel chartPanelForPartsByPrice = new ChartPanel(chartForPartsByPrice);
        chartPanelForPartsByPrice.setPreferredSize(new java.awt.Dimension(500, 270));
        add(chartPanelForPartsByPrice, BorderLayout.NORTH);

        // --- 2
        DefaultPieDataset datasetForClientsByRecordCount = createDatasetForClientsByRecordCount();
        JFreeChart chartForClientsByRecordCount = ChartFactory.createPieChart("Distribution of Clients by Number of Records", datasetForClientsByRecordCount);

        ChartPanel chartPanelForClientsByRecordCount = new ChartPanel(chartForClientsByRecordCount);
        chartPanelForClientsByRecordCount.setPreferredSize(new java.awt.Dimension(500, 270));
        add(chartPanelForClientsByRecordCount, BorderLayout.CENTER);

        // --- тож самое, что 2, только по заказам
        //DefaultPieDataset datasetForClientsByOrderType = createDatasetForClientsByOrderType();
        //JFreeChart chartForClientsByOrderType = ChartFactory.createPieChart("Distribution of Clients by Preferred Order Type", datasetForClientsByOrderType);

        //ChartPanel chartPanelForClientsByOrderType = new ChartPanel(chartForClientsByOrderType);
        //chartPanelForClientsByOrderType.setPreferredSize(new java.awt.Dimension(500, 270));
        //add(chartPanelForClientsByOrderType, BorderLayout.CENTER);

        // --- 3
        XYDataset datasetForSalesTrend = createDatasetForSalesTrend();
        JFreeChart chartForSalesTrend = ChartFactory.createTimeSeriesChart("Trend of Computer Parts Sales Over Time", "Date", "Sales", datasetForSalesTrend);

        ChartPanel chartPanelForSalesTrend = new ChartPanel(chartForSalesTrend);
        chartPanelForSalesTrend.setPreferredSize(new java.awt.Dimension(500, 270));
        add(chartPanelForSalesTrend, BorderLayout.SOUTH);

        // --- тож самое, что и 3, только столбиками
        //DefaultCategoryDataset datasetForPartsByYear = createDatasetForPartsByYear();
        //JFreeChart chartForPartsByYear = ChartFactory.createBarChart("Number of Computer Parts by Year of Manufacture", "Year", "Count", datasetForPartsByYear);

        //ChartPanel chartPanelForPartsByYear = new ChartPanel(chartForPartsByYear);
        //chartPanelForPartsByYear.setPreferredSize(new java.awt.Dimension(500, 270));
        //add(chartPanelForPartsByYear, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Analytics().setVisible(true);
        });
    }

    private DefaultCategoryDataset createDatasetForPartsByPrice() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, SUM(price) FROM computer_parts GROUP BY name")) {

            while (rs.next()) {
                String partName = rs.getString("name");
                double totalPrice = rs.getDouble("SUM(price)");
                dataset.addValue(totalPrice, "Computer Parts", partName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }


    private DefaultCategoryDataset createDatasetForPartsByYear() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT year_of_manufacture, COUNT(*) FROM computer_parts GROUP BY year_of_manufacture")) {

            while (rs.next()) {
                String year = rs.getString("year_of_manufacture");
                int count = rs.getInt("COUNT(*)");
                dataset.addValue(count, "Computer Parts", year);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private DefaultPieDataset createDatasetForClientsByOrderType() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT order_number, COUNT(*) FROM clients GROUP BY order_number")) {

            while (rs.next()) {
                String orderType = rs.getString("order_number");
                int count = rs.getInt("COUNT(*)");
                dataset.setValue(orderType, count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private DefaultPieDataset createDatasetForClientsByRecordCount() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, COUNT(*) FROM clients GROUP BY name")) {

            while (rs.next()) {
                String clientName = rs.getString("name");
                int recordCount = rs.getInt("COUNT(*)");
                dataset.setValue(clientName, recordCount);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }


    private XYDataset createDatasetForSalesTrend() {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        TimeSeries salesTrend = new TimeSeries("Sales Trend");

        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT year_of_manufacture, COUNT(*) FROM computer_parts GROUP BY year_of_manufacture")) {

            while (rs.next()) {
                String year = rs.getString("year_of_manufacture");
                int count = rs.getInt("COUNT(*)");

                Year yearObj = new Year(Integer.parseInt(year));
                salesTrend.addOrUpdate(yearObj, count);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        dataset.addSeries(salesTrend);
        return dataset;
    }
}
