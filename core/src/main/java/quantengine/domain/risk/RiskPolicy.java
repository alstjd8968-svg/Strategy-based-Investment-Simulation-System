package quantengine.domain.risk;

import quantengine.domain.strategy.*;
import quantengine.domain.market.*;

public interface RiskPolicy {
    RiskScore evaluate(PortfolioDecision decision, MarketSnapshot snapshot);
    
}
