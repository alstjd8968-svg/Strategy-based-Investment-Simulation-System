package quantengine.domain.engine;

import quantengine.domain.market.MarketSnapshot;
import quantengine.domain.order.Order;
import quantengine.domain.order.OrderFactory;
import quantengine.domain.portfolio.Portfolio;
import quantengine.domain.portfolio.Position;
import quantengine.domain.risk.RiskPolicy;
import quantengine.domain.risk.RiskScore;
import quantengine.domain.strategy.PortfolioDecision;
import quantengine.domain.strategy.Signal;
import quantengine.domain.strategy.Startegy;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 투자 의사결정 엔진.
 * 각 도메인 간의 흐름을 조율한다.
 *
 * 처리 흐름:
 * 1. Strategy.decide(current, previous) → List<PortfolioDecision>
 * 2. HOLD 필터링 → BUY/SELL 분리
 * 3. SELL 먼저 처리 (cash 확보)
 * 4. BUY decisions를 AllocationService에 일괄 전달 → 비중 배분
 * 5. 각 배분 결과로 Order 생성 → Portfolio 적용
 */
public class Engine {

    private final Startegy strategy;
    private final RiskPolicy riskPolicy;
    private final AllocationService allocationService;
    private final Portfolio portfolio;

    private MarketSnapshot previousSnapshot;

    public Engine(Startegy strategy, RiskPolicy riskPolicy,
                  AllocationService allocationService, Portfolio portfolio) {
        this.strategy = strategy;
        this.riskPolicy = riskPolicy;
        this.allocationService = allocationService;
        this.portfolio = portfolio;
        this.previousSnapshot = null;
    }

    public void process(MarketSnapshot snapshot) {
        // 1. Strategy: 멀티 자산 의사결정 (Stateless)
        List<PortfolioDecision> decisions = strategy.decide(snapshot, previousSnapshot);

        // 2. HOLD 필터링 후 BUY/SELL 분리
        List<PortfolioDecision> sellDecisions = decisions.stream()
                .filter(d -> d.getSignal() == Signal.SELL)
                .collect(Collectors.toList());

        List<PortfolioDecision> buyDecisions = decisions.stream()
                .filter(d -> d.getSignal() == Signal.BUY)
                .collect(Collectors.toList());

        // 3. SELL 먼저 처리 (cash 확보)
        for (PortfolioDecision sellDecision : sellDecisions) {
            RiskScore riskScore = riskPolicy.evaluate(sellDecision, snapshot);
            if (!riskScore.isAllowed()) continue;

            Position position = portfolio.getPositions().get(sellDecision.getTicker());
            if (position == null) continue;

            double sellAmount = position.getQuantity() * snapshot.getPrice(sellDecision.getTicker());
            Order sellOrder = OrderFactory.from(sellDecision, sellAmount, snapshot);
            if (sellOrder != null) {
                portfolio.apply(sellOrder);
            }
        }

        // 4. BUY: 리스크 검증 후 비중 배분
        List<PortfolioDecision> allowedBuyDecisions = new ArrayList<>();
        RiskScore buyRiskScore = null;

        for (PortfolioDecision buyDecision : buyDecisions) {
            RiskScore riskScore = riskPolicy.evaluate(buyDecision, snapshot);
            if (riskScore.isAllowed()) {
                allowedBuyDecisions.add(buyDecision);
                buyRiskScore = riskScore;   // Phase 0: 동일 RiskScore 사용
            }
        }

        if (!allowedBuyDecisions.isEmpty() && buyRiskScore != null) {
            Map<String, Double> allocations = allocationService.allocate(
                    allowedBuyDecisions, buyRiskScore, portfolio);

            // 5. 각 배분 결과로 Order 생성 → Portfolio 적용
            for (PortfolioDecision buyDecision : allowedBuyDecisions) {
                Double allocatedAmount = allocations.get(buyDecision.getTicker());
                if (allocatedAmount != null && allocatedAmount > 0) {
                    Order buyOrder = OrderFactory.from(buyDecision, allocatedAmount, snapshot);
                    if (buyOrder != null) {
                        portfolio.apply(buyOrder);
                    }
                }
            }
        }

        // previousSnapshot 갱신 (Strategy에 전달하기 위해 Engine이 관리)
        this.previousSnapshot = snapshot;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }
}
