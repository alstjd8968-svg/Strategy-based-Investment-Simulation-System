package quantengine.domain.order;

import quantengine.domain.market.MarketSnapshot;
import quantengine.domain.strategy.PortfolioDecision;
import quantengine.domain.strategy.Signal;

public class OrderFactory {

    /**
     * PortfolioDecision과 배정 금액(allocatedAmount)으로 Order를 생성한다.
     * HOLD signal이면 null을 반환한다.
     *
     * @param decision        의사결정
     * @param allocatedAmount 배정된 금액 (BUY: 매수 금액, SELL: 매도 수량으로 변환됨)
     * @param snapshot        현재 시장 스냅샷
     * @return Order 또는 null (HOLD인 경우)
     */
    public static Order from(PortfolioDecision decision, double allocatedAmount, MarketSnapshot snapshot) {
        if (decision.getSignal() == Signal.HOLD) {
            return null;
        }

        double price = snapshot.getPrice(decision.getTicker());
        OrderType type = (decision.getSignal() == Signal.BUY) ? OrderType.BUY : OrderType.SELL;
        double quantity = allocatedAmount / price;

        return new Order(decision.getTicker(), type, quantity, price);
    }
}
