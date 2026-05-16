package OrderSystem;

public class PercentageDiscount implements DiscountStrategy {
    private final double percent;

    public PercentageDiscount(double percent) {
        this.percent = percent;
    }

    @Override
    public String getName() {
        return String.format("全單 %.0f%% 折扣", percent);
    }

    @Override
    public double apply(double total) {
        return total * (percent / 100.0);
    }
}