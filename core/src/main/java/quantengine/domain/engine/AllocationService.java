package quantengine.domain.engine;

import quantengine.domain.portfolio.Portfolio;
import quantengine.domain.risk.RiskScore;
import quantengine.domain.strategy.PortfolioDecision;

import java.util.*;

/**
 * 멀티 자산 비중 배분 서비스.
 * BUY decision 전체를 받아 confidence 비율 기준으로 cash를 분배한다.
 * allocation(i) = (confidence_i / totalConfidence) * cash * riskScore
 */
public class AllocationService {

    /**
     * BUY decisions에 대해 proportional allocation을 수행한다.
     *
     * @param buyDecisions BUY signal인 의사결정 목록
     * @param riskScore    리스크 평가 결과
     * @param portfolio    현재 포트폴리오 (가용 cash 참조)
     * @return ticker → 배정 금액
     */
    public Map<String, Double> allocate(List<PortfolioDecision> buyDecisions, RiskScore riskScore, Portfolio portfolio) {
        Map<String, Double> allocations = new HashMap<>();

        if (buyDecisions.isEmpty()) {
            return allocations;
        }

        double totalConfidence = buyDecisions.stream()
                .mapToDouble(PortfolioDecision::getConfidence)
                .sum();

        if (totalConfidence <= 0) {
            return allocations;
        }

        double availableCash = portfolio.getCash() * riskScore.getScore();

        for (PortfolioDecision decision : buyDecisions) {
            double weight = decision.getConfidence() / totalConfidence;
            double allocated = weight * availableCash;
            allocations.put(decision.getTicker(), allocated);
        }

        return allocations;
    }
}
