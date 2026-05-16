package OrderSystem;

public enum Category {
    HOTPOT("鍋物"), ADDON("加購");
    private final String displayName;
    Category(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
}