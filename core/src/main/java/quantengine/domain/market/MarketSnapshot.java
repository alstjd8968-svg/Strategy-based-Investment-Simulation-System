package quantengine.domain.market;

import java.time.LocalDate;
import java.util.*;

public class MarketSnapshot {

    private final LocalDate date;
    private final Map<String, Double> prices;   // ticker → 가격

    public MarketSnapshot(LocalDate date, Map<String, Double> prices) {
        this.date = date;
        this.prices = Map.copyOf(prices);       // 불변 복사
    }

    public LocalDate getDate() {
        return date;
    }

    public double getPrice(String ticker) {
        if (!prices.containsKey(ticker)) {
            throw new IllegalArgumentException("Unknown ticker: " + ticker);
        }
        return prices.get(ticker);
    }

    public Set<String> getTickers() {
        return prices.keySet();
    }

    public Map<String, Double> getPrices() {
        return prices;
    }
}
