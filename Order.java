package OrderSystem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Order {
    private Map<String, OrderItem> itemMap = new LinkedHashMap<>();
    private String orderId;
    private int pickupNumber;
    private String dateTime;
    private List<String> discountNames = new ArrayList<>();
    private double discountAmount = 0;

    public Order() {
        this.orderId = OrderManager.generateOrderId();
        this.pickupNumber = OrderManager.getPickupNumber();
        this.dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public void addItem(MenuItem item, int qty) {
        if (itemMap.containsKey(item.getId())) {
            int newQty = itemMap.get(item.getId()).getQuantity() + qty;
            itemMap.get(item.getId()).updateQuantity(newQty);
        } else {
            itemMap.put(item.getId(), new OrderItem(item, qty));
        }
    }

    public OrderItem removeItem(String id) {
        return itemMap.remove(id);
    }

    public double getRawTotal() {
        return itemMap.values().stream().mapToDouble(OrderItem::getTotal).sum();
    }

    public void applyDiscounts(List<DiscountStrategy> discounts) {
        discountAmount = 0;
        discountNames.clear();
        for (DiscountStrategy d : discounts) {
            double applied = d.apply(getRawTotal());
            if (applied > 0) {
                discountAmount += applied;
                discountNames.add(d.getName() + ": -" + String.format("%.1f", applied));
            }
        }
    }

    public double getFinalTotal() { return getRawTotal() - discountAmount; }

    public Collection<OrderItem> getItems() { return itemMap.values(); }
    public boolean isEmpty() { return itemMap.isEmpty(); }

    public String getOrderId() { return orderId; }
    public int getPickupNumber() { return pickupNumber; }
    public String getDateTime() { return dateTime; }

    public void printReceipt() {
        System.out.println("\n====== 🧾 訂單明細 ======");
        System.out.println("取餐號碼: " + pickupNumber);
        int idx = 1;
        for (OrderItem oi : itemMap.values()) {
            System.out.printf("%d. %-12s x %d = $%.1f%n", idx++, oi.getItem().getName(), oi.getQuantity(), oi.getTotal());
        }
        System.out.println("----------------------");
        System.out.printf("原始總額：$%.1f%n", getRawTotal());
        for (String name : discountNames) System.out.println("🎁 " + name);
        System.out.printf("💰 應付總額：$%.1f%n", getFinalTotal());
    }
}