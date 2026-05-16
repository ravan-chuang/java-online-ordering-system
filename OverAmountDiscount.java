package OrderSystem;

public class OverAmountDiscount implements DiscountStrategy {
    private final double threshold;
    private final double discount;

    public OverAmountDiscount() {
        this.threshold = 500;
        this.discount = 50;
    }

    @Override
    public String getName() {
        return String.format("滿 %.0f 折 %.0f", threshold, discount);
    }

    @Override
    public double apply(double total) {
        return total >= threshold ? discount : 0;
    }
}