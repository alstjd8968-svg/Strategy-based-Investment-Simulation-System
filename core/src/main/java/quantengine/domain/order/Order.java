package quantengine.domain.order;

public final class Order {

    private final String ticker;
    private final OrderType type;
    private final double quantity;
    private final double price;

    public Order(String ticker, OrderType type, double quantity, double price) {
        this.ticker = ticker;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
    }

    public String getTicker() {
        return ticker;
    }

    public OrderType getType() {
        return type;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalCost() {
        return quantity * price;
    }
}
