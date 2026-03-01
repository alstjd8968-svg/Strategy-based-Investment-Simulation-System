package quantengine.domain.strategy;

import quantengine.domain.market.MarketSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 0 단순 모멘텀 전략.
 * Stateless — 내부 상태 없이 current/previous 스냅샷만으로 판단.
 *
 * 로직:
 * - previous가 null이면 모든 종목 HOLD
 * - 전일 대비 가격 상승 → BUY (confidence = 상승률)
 * - 전일 대비 가격 하락 → SELL (confidence = 하락률의 절대값)
 * - 변동 없음 → HOLD
 */
public class SimpleMomentumStrategy implements Startegy {

    @Override
    public List<PortfolioDecision> decide(MarketSnapshot current, MarketSnapshot previous) {
        List<PortfolioDecision> decisions = new ArrayList<>();

        for (String ticker : current.getTickers()) {
            if (previous == null || !previous.getTickers().contains(ticker)) {
                decisions.add(new PortfolioDecision(ticker, Signal.HOLD, 0.0));
                continue;
            }

            double currentPrice = current.getPrice(ticker);
            double previousPrice = previous.getPrice(ticker);
            double changeRate = (currentPrice - previousPrice) / previousPrice;

            if (changeRate > 0) {
                decisions.add(new PortfolioDecision(ticker, Signal.BUY, Math.min(changeRate, 1.0)));
            } else if (changeRate < 0) {
                decisions.add(new PortfolioDecision(ticker, Signal.SELL, Math.min(Math.abs(changeRate), 1.0)));
            } else {
                decisions.add(new PortfolioDecision(ticker, Signal.HOLD, 0.0));
            }
        }

        return decisions;
    }
}
