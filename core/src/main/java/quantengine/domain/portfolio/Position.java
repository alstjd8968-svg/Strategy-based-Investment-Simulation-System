package quantengine.domain.portfolio;

public class Position {

    private final String ticker;
    private double quantity;
    private double avgPrice;

    public Position(String ticker, double quantity, double avgPrice) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.avgPrice = avgPrice;
    }

    /**
     * 매수 시 수량 추가 및 평균 단가 재계산.
     */
    public void addQuantity(double addedQuantity, double price) {
        double totalCost = (this.quantity * this.avgPrice) + (addedQuantity * price);
        this.quantity += addedQuantity;
        this.avgPrice = (this.quantity > 0) ? totalCost / this.quantity : 0;
    }

    /**
     * 매도 시 수량 감소. 평균 단가는 유지.
     */
    public void reduceQuantity(double reducedQuantity) {
        this.quantity -= reducedQuantity;
        if (this.quantity < 0) {
            this.quantity = 0;
        }
    }

    public String getTicker() {
        return ticker;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getAvgPrice() {
        return avgPrice;
    }

    public boolean isEmpty() {
        return quantity <= 0;
    }
}
