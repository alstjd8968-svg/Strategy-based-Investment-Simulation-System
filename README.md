📘 Quant Engine

확장형 투자 의사결정 엔진 프로젝트

---

🎯 목표  
단순 자동매매가 아니라,  
전략 실행/리스크 평가/주문 생성/포트폴리오 업데이트 등  
투자 의사결정을 모듈화 구조로 처리하는 코어 시스템 구축

장기 확장:  
- 전략 실험 플랫폼  
- 백테스트 프레임워크  
- 리스크 분석 시스템  
- API 투자 지원 플랫폼

---

🧠 설계 철학  
1. Stateless Strategy  
   └ 전략은 상태 미보관. 모든 상태는 Engine/Portfolio가 관리

2. Risk = Policy Layer  
   └ 단순 점수 아닌 설명 가능한 리스크 리포트 제공

3. Engine = Pipeline Orchestrator  
   └ 단계별 Stage구조로 절차 흐름 관리, 확장 용이

---

🏗 구조  
- engine/  
- order/  
- portfolio/  
- risk/  
- strategy/  
(모듈별 책임 분리, 도메인 중심 의존성)

---

🚀 개발 단계  
Phase 0) 엔진 코어  
 - Stateless 전략 구조  
 - RiskScore 구현  
 - 시뮬레이션 루프/Order-Portfolio  
 - 콘솔 기반 실행

Phase 1) 안정화  
 - 테스트 코드  
 - Risk Rule 구조화  
 - 리팩토링

Phase 2) API  
 - Spring Boot/REST API

Phase 3) UI  
 - 웹기반 시각화

Phase 4) AI  
 - 전략 분석, 리스크 가중치 학습

---

🧩 장기 목표  
4년 내  
투자 의사결정 지원 시스템 설계·구현 역량 확보
