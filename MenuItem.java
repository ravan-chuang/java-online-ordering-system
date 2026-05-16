package OrderSystem;

public class MenuItem {
    private String id;
    private String name;
    private double price;
    private Category category;
    private int stock;

    public MenuItem(String id, String name, double price, Category category, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stock = stock;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public Category getCategory() { return category; }

    public synchronized int getStock() { return stock; }
    public synchronized void setStock(int stock) { this.stock = stock; }

    public synchronized boolean reserveStock(int qty) {
        if (stock >= qty) {
            stock -= qty;
            return true;
        }
        return false;
    }

    public synchronized void returnStock(int qty) {
        stock += qty;
    }
}