package quantengine.domain.risk;

import quantengine.domain.market.MarketSnapshot;
import quantengine.domain.strategy.PortfolioDecision;

/**
 * Phase 0 기본 리스크 정책.
 * 항상 허용 (score = 1.0).
 */
public class BasicRiskPolicy implements RiskPolicy {

    @Override
    public RiskScore evaluate(PortfolioDecision decision, MarketSnapshot snapshot) {
        return RiskScore.allowed("Phase 0: 모든 의사결정 허용");
    }
}
