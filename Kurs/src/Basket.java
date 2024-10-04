import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class Basket {
    private int id;
    private String name;
    private double price;
    private int lot;

    public Basket(int id, String name, double price, int lot) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.lot = lot;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getLot() {
        return lot;
    }

    public void setLot(int lot) {
        this.lot = lot;
    }
}