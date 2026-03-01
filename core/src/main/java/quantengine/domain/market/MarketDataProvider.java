package quantengine.domain.market;

import java.util.List;

public interface MarketDataProvider {
    List<MarketSnapshot> getSnapshots();
}
