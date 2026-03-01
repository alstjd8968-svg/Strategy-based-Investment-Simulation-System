package quantengine.domain.risk;

import java.util.List;

public final class RiskScore {

    private final double score;          // 0 ~ 1
    private final List<String> rules;    // 적용된 룰
    private final String reason;         // 설명 메시지

    public RiskScore(double score, List<String> rules, String reason) {
        this.score = score;
        this.rules = List.copyOf(rules);
        this.reason = reason;
    }

    public static RiskScore allowed(String reason) {
        return new RiskScore(1.0, List.of("ALLOW_ALL"), reason);
    }

    public static RiskScore denied(String reason) {
        return new RiskScore(0.0, List.of("DENY"), reason);
    }

    public double getScore() {
        return score;
    }

    public List<String> getRules() {
        return rules;
    }

    public String getReason() {
        return reason;
    }

    public boolean isAllowed() {
        return score > 0.5;
    }
}