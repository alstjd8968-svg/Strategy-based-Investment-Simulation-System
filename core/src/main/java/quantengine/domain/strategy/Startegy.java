package quantengine.domain.strategy;

import quantengine.domain.market.MarketSnapshot;

import java.util.List;

public interface Startegy {
    /**
     * 현재 스냅샷과 이전 스냅샷을 받아 멀티 자산 의사결정을 반환한다.
     * Strategy는 Stateless — 시계열 의존성은 Engine이 주입한다.
     *
     * @param current  현재 날짜의 시장 스냅샷
     * @param previous 이전 날짜의 시장 스냅샷 (첫 번째 날에는 null)
     * @return 각 종목에 대한 의사결정 목록
     */
    List<PortfolioDecision> decide(MarketSnapshot current, MarketSnapshot previous);
}
