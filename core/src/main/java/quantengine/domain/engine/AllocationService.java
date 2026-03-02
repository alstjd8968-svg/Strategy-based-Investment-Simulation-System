package quantengine.domain.engine;

import quantengine.domain.portfolio.Portfolio;
import quantengine.domain.risk.RiskScore;
import quantengine.domain.strategy.PortfolioDecision;

import java.util.*;

/**
 *멀티 자산 비중 배분 서비스. 
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


        // 모든 매수 의사결정의 신뢰도(confidence) 합계를 계산하여 비중 배분의 기준값을 구합니다.
        // stream()으로 스트림을 생성하고 mapToDouble()로 신뢰도 값을 추출한 후 sum()으로 전체 합을 계산합니다.
        double totalConfidence = buyDecisions.stream()
                .mapToDouble(PortfolioDecision::getConfidence)
                .sum();

        // totalConfidence가 0 이하이면 배분할 자금이 없으므로 빈 맵을 반환합니다.
        if (totalConfidence <= 0) {
            return allocations;
        }

        // 리스크 점수를 적용하여 실제 투자 가능한 금액을 계산합니다.
        double availableCash = portfolio.getCash() * riskScore.getScore();

        // 각 매수 의사결정에 대해 비중(weight)을 계산하고, 이를 투자 가능 금액에 곱하여 배정 금액을 계산합니다.
        for (PortfolioDecision decision : buyDecisions) {
            double weight = decision.getConfidence() / totalConfidence;
            double allocated = weight * availableCash;
            allocations.put(decision.getTicker(), allocated);
        }

        return allocations;
    }
}
