package quantengine.domain.engine;

import quantengine.domain.market.MarketSnapshot;

import java.util.List;

/**
 * 일별 시뮬레이션 실행기.
 * MarketSnapshot 리스트를 순회하며 Engine에 처리를 위임한다.
 */
public class DailySimulation {

    public void run(List<MarketSnapshot> snapshots, Engine engine) {
        System.out.println("=== DailySimulation 시작 ===");
        System.out.printf("총 %d일 시뮬레이션%n%n", snapshots.size());

        for (MarketSnapshot snapshot : snapshots) {
            engine.process(snapshot);

            double totalValue = engine.getPortfolio().totalValue(snapshot);
            System.out.printf("[%s] 포트폴리오 총 가치: %.2f | Cash: %.2f | 보유 종목: %s%n",
                    snapshot.getDate(),
                    totalValue,
                    engine.getPortfolio().getCash(),
                    engine.getPortfolio().getPositions().keySet());
        }

        System.out.println("\n=== DailySimulation 종료 ===");
        if (!snapshots.isEmpty()) {
            MarketSnapshot lastSnapshot = snapshots.get(snapshots.size() - 1);
            double finalValue = engine.getPortfolio().totalValue(lastSnapshot);
            System.out.printf("최종 포트폴리오 가치: %.2f%n", finalValue);
            System.out.printf("최종 Cash: %.2f%n", engine.getPortfolio().getCash());
            System.out.printf("보유 포지션: %s%n", engine.getPortfolio().getPositions().keySet());
        }
    }
}