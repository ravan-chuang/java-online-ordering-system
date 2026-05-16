package OrderSystem;

import java.io.*;
import java.util.*;

public class Menu {
    private List<MenuItem> items = new ArrayList<>();

    public Menu() { loadMenuFromFile("menu.txt"); }

    private void loadMenuFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("❗️ menu.txt 不存在，請建立檔案");
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                try {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());
                    Category category = Category.valueOf(parts[3].trim());
                    int stock = parts.length >= 5 ? Integer.parseInt(parts[4].trim()) : 50;
                    items.add(new MenuItem(id, name, price, category, stock));
                } catch (Exception e) { System.out.println("❗️ 跳過無效資料：" + line); }
            }
        } catch (IOException e) { System.out.println("❗️ 無法讀取 menu.txt"); }
    }

    public void printMenu() {
        printCategorySection("🍲 鍋物（主餐）", Category.HOTPOT);
        printCategorySection("➕ 加購", Category.ADDON);
    }

    private void printCategorySection(String title, Category category) {
        System.out.println("\n====== " + title + " ======");
        int idx = 1;
        for (MenuItem item : items) {
            if (item.getCategory() != category) continue;
            System.out.printf("%d. %-12s | $%.1f | 庫存:%d\n", idx++, item.getName(), item.getPrice(), item.getStock());
        }
    }

    public MenuItem findById(String id) {
        return items.stream().filter(i -> i.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public void saveMenuToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (MenuItem item : items) {
                bw.write(String.format("%s,%s,%.1f,%s,%d",
                        item.getId(), item.getName(), item.getPrice(), item.getCategory().name(), item.getStock()));
                bw.newLine();
            }
        } catch (IOException e) { System.err.println("❌ 更新庫存失敗：" + e.getMessage()); }
    }
}