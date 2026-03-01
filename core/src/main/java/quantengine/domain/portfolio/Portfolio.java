package quantengine.domain.portfolio;

import quantengine.domain.market.MarketSnapshot;
import quantengine.domain.order.Order;
import quantengine.domain.order.OrderType;

import java.util.*;

public class Portfolio {

    private double cash;
    private final Map<String, Position> positions;

    public Portfolio(double initialCash) {
        this.cash = initialCash;
        this.positions = new HashMap<>();
    }

    /**
     * 주문을 포트폴리오에 적용한다.
     * BUY → cash 차감 + position 추가/업데이트
     * SELL → cash 증가 + position 감소
     */
    public void apply(Order order) {
        String ticker = order.getTicker();
        double totalCost = order.getTotalCost();

        if (order.getType() == OrderType.BUY) {
            cash -= totalCost;
            positions.merge(ticker,
                    new Position(ticker, order.getQuantity(), order.getPrice()),
                    (existing, newPos) -> {
                        existing.addQuantity(order.getQuantity(), order.getPrice());
                        return existing;
                    });
        } else {    // SELL
            Position position = positions.get(ticker);
            if (position != null) {
                double sellQuantity = Math.min(order.getQuantity(), position.getQuantity());
                cash += sellQuantity * order.getPrice();
                position.reduceQuantity(sellQuantity);
                if (position.isEmpty()) {
                    positions.remove(ticker);
                }
            }
        }
    }

    /**
     * 현재 포트폴리오의 총 평가 금액을 계산한다.
     * 총 가치 = cash + Σ(position.quantity × snapshot.price)
     */
    public double totalValue(MarketSnapshot snapshot) {
        double positionValue = positions.values().stream()
                .mapToDouble(p -> p.getQuantity() * snapshot.getPrice(p.getTicker()))
                .sum();
        return cash + positionValue;
    }

    public double getCash() {
        return cash;
    }

    public Map<String, Position> getPositions() {
        return Collections.unmodifiableMap(positions);
    }
}
