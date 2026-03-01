package quantengine.domain.engine;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import quantengine.domain.market.MarketSnapshot;
import quantengine.domain.portfolio.Portfolio;
import quantengine.domain.risk.BasicRiskPolicy;
import quantengine.domain.strategy.SimpleMomentumStrategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DailySimulationTest {

    @Test
    @DisplayName("3-day 2-asset simulation: momentum strategy with proportional allocation")
    void runDailySimulation() {
        // Given: 3-day market data for 2 assets (AAPL, MSFT)
        List<MarketSnapshot> snapshots = List.of(
                new MarketSnapshot(LocalDate.of(2025, 1, 1), Map.of("AAPL", 100.0, "MSFT", 200.0)),
                new MarketSnapshot(LocalDate.of(2025, 1, 2), Map.of("AAPL", 110.0, "MSFT", 190.0)),  // AAPL +10%, MSFT -5%
                new MarketSnapshot(LocalDate.of(2025, 1, 3), Map.of("AAPL", 115.0, "MSFT", 210.0))   // AAPL +4.5%, MSFT +10.5%
        );

        Portfolio portfolio = new Portfolio(10_000.0);
        Engine engine = new Engine(
                new SimpleMomentumStrategy(),
                new BasicRiskPolicy(),
                new AllocationService(),
                portfolio
        );

        // When
        DailySimulation simulation = new DailySimulation();
        simulation.run(snapshots, engine);

        // Then
        double finalValue = portfolio.totalValue(snapshots.get(2));

        // Day 1: no previous -> all HOLD -> no trades. Cash = 10000
        // Day 2: AAPL up -> BUY, MSFT down -> SELL (no position to sell)
        //   BUY AAPL: confidence = 0.1, only BUY -> 100% of cash * 1.0
        //   allocation = 10000, buy 10000/110 = ~90.9 shares
        //   Cash after: ~0
        // Day 3: AAPL up -> BUY, MSFT up -> BUY
        //   First SELL: nothing to sell
        //   BUY AAPL: confidence ~0.045, BUY MSFT: confidence ~0.105
        //   But cash is ~0 so minimal buy
        //   Portfolio value ~ 90.9 * 115 = ~10454

        System.out.printf("Final portfolio value: %.2f%n", finalValue);
        System.out.printf("Final cash: %.2f%n", portfolio.getCash());
        System.out.printf("Positions: %s%n", portfolio.getPositions());

        // Assertions
        assertTrue(finalValue > 0, "Portfolio should have positive value");
        assertTrue(portfolio.getPositions().containsKey("AAPL"), "Should hold AAPL position");
        assertTrue(finalValue > 10_000.0, "Portfolio value should grow (AAPL went up)");
    }

    @Test
    @DisplayName("Single day simulation: no previous snapshot -> all HOLD -> no trades")
    void singleDayNoTrades() {
        List<MarketSnapshot> snapshots = List.of(
                new MarketSnapshot(LocalDate.of(2025, 1, 1), Map.of("AAPL", 100.0))
        );

        Portfolio portfolio = new Portfolio(5_000.0);
        Engine engine = new Engine(
                new SimpleMomentumStrategy(),
                new BasicRiskPolicy(),
                new AllocationService(),
                portfolio
        );

        DailySimulation simulation = new DailySimulation();
        simulation.run(snapshots, engine);

        assertEquals(5_000.0, portfolio.getCash(), 0.01, "Cash should be unchanged");
        assertTrue(portfolio.getPositions().isEmpty(), "No positions should exist");
    }

    @Test
    @DisplayName("Multi-asset proportional allocation: cash should not be exceeded")
    void proportionalAllocationDoesNotExceedCash() {
        List<MarketSnapshot> snapshots = List.of(
                new MarketSnapshot(LocalDate.of(2025, 1, 1), Map.of("A", 10.0, "B", 20.0, "C", 30.0)),
                new MarketSnapshot(LocalDate.of(2025, 1, 2), Map.of("A", 15.0, "B", 25.0, "C", 35.0))  // all up
        );

        Portfolio portfolio = new Portfolio(1_000.0);
        Engine engine = new Engine(
                new SimpleMomentumStrategy(),
                new BasicRiskPolicy(),
                new AllocationService(),
                portfolio
        );

        DailySimulation simulation = new DailySimulation();
        simulation.run(snapshots, engine);

        // Cash should never go negative
        assertTrue(portfolio.getCash() >= -0.01, "Cash should not be negative: " + portfolio.getCash());
        // All three assets should be bought
        assertEquals(3, portfolio.getPositions().size(), "Should hold all 3 assets");
    }
}
