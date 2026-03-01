package quantengine.domain.strategy;

public final class PortfolioDecision {

    private final String ticker;
    private final Signal signal;        // BUY, SELL, HOLD
    private final double confidence;    // 0.0 ~ 1.0

    public PortfolioDecision(String ticker, Signal signal, double confidence) {
        this.ticker = ticker;
        this.signal = signal;
        this.confidence = confidence;
    }

    public String getTicker() {
        return ticker;
    }

    public Signal getSignal() {
        return signal;
    }

    public double getConfidence() {
        return confidence;
    }
}
