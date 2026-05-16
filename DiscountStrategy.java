package OrderSystem;

public interface DiscountStrategy {
    String getName();  // 折扣名稱
    double apply(double total); // 回傳折扣金額
}