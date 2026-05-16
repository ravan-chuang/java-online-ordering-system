package OrderSystem;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Menu menu = new Menu();
        Scanner sc = new Scanner(System.in);

        System.out.println("✨ 歡迎使用點餐系統!");

        boolean running = true;
        while (running) {
            System.out.println("\n=== 主選單 ===");
            System.out.println("1. 顯示菜單");
            System.out.println("2. 建立新訂單");
            System.out.println("3. 查詢訂單");
            System.out.println("0. 離開");
            System.out.print("請選擇：");

            String input = sc.nextLine().trim();
            switch (input) {
                case "1" -> menu.printMenu();
                case "2" -> handleNewOrder(sc, menu);
                case "3" -> {
                    System.out.print("請輸入訂單編號或取餐號碼：");
                    String searchId = sc.nextLine().trim();
                    OrderManager.findOrderById(searchId);
                }
                case "0" -> { System.out.println("✨ 系統結束，感謝使用！"); running = false; }
                default -> System.out.println("❌ 無效選項，請重新輸入");
            }
        }
        sc.close();
    }

    private static void handleNewOrder(Scanner sc, Menu menu) {
        Order order = new Order();

        System.out.println("\n📌 指令說明：");
        System.out.println("  代碼 ➕ 數量   → 加入 / 修改餐點");
        System.out.println("  del ➕ 代碼    → 移除餐點");
        System.out.println("  info          → 查看當前訂單餐點資訊");
        System.out.println("  done          → 結帳");
        System.out.println("  cancel        → 取消訂單");

        while (true) {
            System.out.print("> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase();

            switch (command) {

                case "done":
                    if (order.isEmpty()) {
                        System.out.println("❗️ 訂單取消（未選擇餐點）");
                        return;
                    }

                    // ===== 套用折扣 =====
                    List<DiscountStrategy> discounts = new ArrayList<>();
                    discounts.add(new OverAmountDiscount()); // 滿額折扣
                    // discounts.add(new PercentageDiscount(10)); // 可加入更多折扣
                    order.applyDiscounts(discounts);

                    System.out.println("\n=== 取餐號碼：" + order.getPickupNumber() + " ===");
                    order.printReceipt();
                    OrderManager.saveToCSV(order);
                    menu.saveMenuToFile("menu.txt");
                    System.out.println("✔️ 訂單完成，已儲存並更新庫存");
                    return;

                case "cancel":
                    // 將庫存歸還
                    for (OrderItem oi : order.getItems()) {
                        oi.getItem().setStock(oi.getItem().getStock() + oi.getQuantity());
                    }
                    System.out.println("❗️ 訂單已取消，庫存已歸還");
                    return;

                case "del":
                    if (parts.length < 2) {
                        System.out.println("❌ del 指令格式錯誤，請輸入：del ➕ 代碼");
                        continue;
                    }
                    String delId = parts[1];
                    OrderItem removed = order.removeItem(delId);
                    if (removed != null) {
                        removed.getItem().setStock(removed.getItem().getStock() + removed.getQuantity());
                        System.out.printf("✔️ 已移除 %s（庫存歸還至 %d）\n",
                                removed.getItem().getName(), removed.getItem().getStock());
                    } else {
                        System.out.println("❌ 訂單中找不到此品項");
                    }
                    continue;

                case "info":
                    if (order.isEmpty()) {
                        System.out.println("❗️ 訂單目前沒有任何餐點");
                    } else {
                        System.out.println("\n📋 當前訂單餐點資訊：");
                        int idx = 1;
                        for (OrderItem oi : order.getItems()) {
                            MenuItem mi = oi.getItem();
                            System.out.printf("%d. %s x %d | 單價: $%.1f | 總額: $%.1f | 庫存剩餘: %d%n",
                                    idx++, mi.getName(), oi.getQuantity(), mi.getPrice(), oi.getTotal(), mi.getStock());
                        }
                        System.out.printf("🔹 原始總額：$%.1f%n", order.getRawTotal());
                        if (order.getFinalTotal() < order.getRawTotal()) {
                            System.out.printf("🎁 折扣後總額：$%.1f%n", order.getFinalTotal());
                        }
                    }
                    continue;

                default:
                    // 處理加入 / 修改餐點
                    if (parts.length != 2) {
                        System.out.println("❌ 指令格式錯誤，請輸入代碼+數量");
                        continue;
                    }

                    MenuItem item = menu.findById(parts[0]);
                    if (item == null) {
                        System.out.println("❌ 找不到此餐點代碼");
                        continue;
                    }

                    try {
                        int qty = Integer.parseInt(parts[1]);
                        if (qty <= 0) throw new NumberFormatException();

                        if (item.getStock() < qty) {
                            System.out.println("❌ 庫存不足，目前剩餘：" + item.getStock());
                            continue;
                        }

                        order.addItem(item, qty);
                        item.setStock(item.getStock() - qty);
                        System.out.printf("✔️ 已加入: %s x %d（庫存剩餘: %d）\n",
                                item.getName(), qty, item.getStock());

                    } catch (NumberFormatException e) {
                        System.out.println("❌ 數量必須為正整數");
                    }
            }
        }
    }
}