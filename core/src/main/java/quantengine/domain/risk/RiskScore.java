package quantengine.domain.risk;

import java.util.List;

public final class RiskScore {

    // 0 ~ 1 사이의 실수 값으로, 리스크 수준을 나타냅니다.
    // 1.0에 가까울수록 리스크가 낮고 투자가 허용됨을 의미하며, 0.0에 가까울수록 리스크가 높고 투자가 제한됨을 의미합니다.
    private final double score;          
    
    // 적용된 룰의 목록입니다. 어떤 리스크 규칙들이 이 점수에 영향을 미쳤는지 추적합니다.
    private final List<String> rules;    
    
    // 이 리스크 점수가 산출된 이유나 배경을 설명하는 메시지입니다.
    private final String reason;         

    // 생성자: 점수, 적용된 룰 목록, 사유를 받아 객체를 초기화합니다.
    // List.copyOf(rules)를 사용하여 외부에서 전달된 리스트를 복사함으로써, 
    // 외부 리스트가 변경되어도 내부 상태가 영향을 받지 않도록 불변성을 보장합니다.
    public RiskScore(double score, List<String> rules, String reason) {
        this.score = score;
        this.rules = List.copyOf(rules);
        this.reason = reason;
    }

    // 모든 조건이 허용되어 투자가 가능한 경우에 대한 헬퍼 메서드입니다.
    // score를 1.0으로 설정하고, 규칙 목록에 "ALLOW_ALL"을 추가하며, 주어진 사유를 사용합니다.
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