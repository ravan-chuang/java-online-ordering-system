package OrderSystem;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class OrderManager {

    private static String lastDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    private static int dailyCounter = 0;
    private static final String CSV_FILE = "orders.csv";

    // 靜態區塊：程式啟動時自動執行，載入上次的編號
    static {
        loadLastOrderNumber();
    }

    public static synchronized String generateOrderId() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 如果日期跨天了，重置計數器
        if (!today.equals(lastDate)) {
            dailyCounter = 0;
            lastDate = today;
        }
        
        dailyCounter++;
        return String.format("ORD%s-%03d", today, dailyCounter);
    }

    public static int getPickupNumber() {
        return dailyCounter;
    }

    // 優化：讀取 CSV 最後一行來恢復計數器
    private static void loadLastOrderNumber() {
        File file = new File(CSV_FILE);
        if (!file.exists()) return;

        String lastLine = "";
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.trim().isEmpty()) {
                    lastLine = line;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 解析最後一行：假設格式第一欄是 ORD20230101-005
        if (!lastLine.isEmpty()) {
            String[] parts = lastLine.split(",");
            String lastOrderId = parts[0]; 
            // 簡單解析：ORDyyyyMMdd-XXX
            if (lastOrderId.startsWith("ORD" + lastDate)) {
                try {
                    String numberPart = lastOrderId.split("-")[1];
                    dailyCounter = Integer.parseInt(numberPart);
                } catch (Exception e) {
                    System.out.println("❗️ 無法解析上次訂單編號，將從 1 開始");
                }
            }
        }
    }

    public static void saveToCSV(Order order) {
        // 使用 append 模式
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE, true))) {
            for (OrderItem oi : order.getItems()) {
                bw.write(String.format("%s,%s,%s,%d,%.1f,%.1f",
                        order.getOrderId(),
                        order.getDateTime(),
                        oi.getItem().getName(),
                        oi.getQuantity(),
                        oi.getItem().getPrice(),
                        oi.getTotal()));
                bw.newLine();
            }
            // 這裡可以選擇是否要寫入總金額行，或是保持單純資料
            bw.write(String.format("%s,%s,總金額,,,%s",
                    order.getOrderId(),
                    order.getDateTime(),
                    String.format("%.1f", order.getFinalTotal())));
            bw.newLine();
        } catch (IOException e) {
            System.err.println("❌ 儲存失敗：" + e.getMessage());
        }
    }
    
    public static void findOrderById(String target) {
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            System.out.println("❌ 尚未有任何訂單記錄。");
            return;
        }
        
        String latestOrderId = null;
        String latestTime = null;
        
        // ===== 第一次掃描：找出「最新的 orderId」=====
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {                
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length < 2) continue; // 確保資料完整性
                // 
                String orderId = parts[0];
                String time = parts[1];
                
                // --- 安全解析取餐號碼 ---
                String pickupNum = "";
                if (orderId.contains("-")) {
                    try {
                        // 分割出 ORD20260104-004 的 004 部分並轉為整數
                        pickupNum = String.valueOf(Integer.parseInt(orderId.split("-")[1]));
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        // 若格式不符則跳過解析，不中斷程式
                        }
                    }
                    // 比較完整 ID 或取餐號碼
                    if (orderId.equalsIgnoreCase(target) || pickupNum.equals(target)) {
                        if (latestTime == null || time.compareTo(latestTime) > 0) {
                            latestTime = time;
                            latestOrderId = orderId;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ 查詢失敗：" + e.getMessage());
                return;
            }
        
        if (latestOrderId == null) {
            System.out.println("❌ 找不到編號或取餐號碼：" + target);
            return;
        }
        
        // ===== 第二次掃描：只顯示這筆訂單 =====
        System.out.println("\n--- 查詢結果 [" + latestOrderId + "] ---");
        
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                
                if (!parts[0].equals(latestOrderId)) continue;
                
                if (parts[2].equals("總金額")) {
                    System.out.println("----------------------");
                    System.out.println("💰 總金額：" + parts[5]);
                } else {
                    System.out.printf("%-12s x %s = $%s%n",
                    parts[2], parts[3], parts[5]);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ 查詢顯示失敗：" + e.getMessage());
        }
    }
}