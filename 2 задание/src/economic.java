import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class economic {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty date;
    private final SimpleDoubleProperty revenue;
    private final SimpleDoubleProperty expenses;
    private final SimpleDoubleProperty profit;

    public economic(int id, String date, double revenue, double expenses, double profit) {
        this.id = new SimpleIntegerProperty(id);
        this.date = new SimpleStringProperty(date);
        this.revenue = new SimpleDoubleProperty(revenue);
        this.expenses = new SimpleDoubleProperty(expenses);
        this.profit = new SimpleDoubleProperty(profit);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public double getRevenue() {
        return revenue.get();
    }

    public void setRevenue(double revenue) {
        this.revenue.set(revenue);
    }

    public SimpleDoubleProperty revenueProperty() {
        return revenue;
    }

    public double getExpenses() {
        return expenses.get();
    }

    public void setExpenses(double expenses) {
        this.expenses.set(expenses);
    }

    public SimpleDoubleProperty expensesProperty() {
        return expenses;
    }

    public double getProfit() {
        return profit.get();
    }

    public void setProfit(double profit) {
        this.profit.set(profit);
    }

    public SimpleDoubleProperty profitProperty() {
        return profit;
    }
}