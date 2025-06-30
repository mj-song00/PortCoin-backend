# PortCoin - 코인 포트폴리오 관리 서비스 

### 🛠️ 기술 스택
![](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![](https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white)
![](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)
![](https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white)
![](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

### 🔍 개요
PortCoin은 투자한 코인에 대한 수익률을 계산해주며, 24시간 등락률을 확인할 수 있습니다. PortCoin은 다음과 같은 서비스를 제공합니다.

- 개별 코인에 대한 24시간 등락률 제공
- 개별 코인에 대한 수익률 계산
  
### 서비스 제작 배경
코로나 팬데믹을 전후로 주식과 더불어 코인 투자에 대한 관심이 급격히 증가했습니다. 
많은 사람들이 비트코인, 이더리움과 같은 가상화폐에 투자하고 있지만, 실제 수익률을 계산하거나 자산의 변동을 체계적으로 확인하는 데 어려움을 겪고 있습니다.
또한, 다양한 수익률 계산기들이 흩어져 있어 초보자들이 혼란을 느끼기 쉬운 구조입니다. 여러 플랫폼을 오가며 정보를 취합해야 하는 번거로움도 있습니다.
이러한 문제를 해결하고자, 간편한 코인 등록만으로 수익률, 24시간 변동률 등을 직관적으로 확인할 수 있는 'PortCoin' 서비스를 개발하게 되었습니다.
PortCoin은 투자자들이 보다 쉽고 빠르게 자신의 자산을 관리하고, 변동성을 파악하여 합리적인 투자 판단을 내릴 수 있도록 돕는 데 초점을 맞추고 있습니다.

### 프로젝트 제작 기간
2025-04 ~ 2025-06

### 스웨거 사용
http://localhost:8080/swagger-ui/index.html

### 주요 기능
- JWT 기반 인증 시스템 구현
(액세스/리프레시 토큰 분리, Redis에 리프레시 토큰 저장으로 보안성 강화)
- 사용자별 포트폴리오 생성 및 관리
(코인 등록시 실시간 시세 연동)
- K6 및 Grafana를 활용한 부하 테스트 시각화 
- 실시간 코인 가격 조회
- 포트폴리오 수익률 계산   

### 📁 프로젝트 구조
``` portcoin/
├── src/
│ ├── main/
│ │ ├── java/com/portcoin/...
│ │ └── resources/
│ │ ├── application.yml
│ │ └── ...
│ └── test/
│ └── java/...
├── docker-compose.yml
├── docker-compose.monitoring.yml
├── build.gradle
└── README.md 
```
**주요 구성 요소**
- **Spring Boot** : REST API 및 전체 비즈니스 로직 구현
- **docker-compose.yml** : Redis, PostgreSQL 주요 데이터 저장소 컨테이너 샐행 관리 도구
- **docker-compose.monitoring.yml** : K6, Grafana, InfluxDB 컨테이너 실행을 위한 관리 도구 (K6 테스트 시각화 환경 구성)

### 설치 및 실행 방법 
1. 백엔트 프로젝트 프로젝트 클론 및 실행
```
git clone https://github.com/minji-song00/portcoin.git
cd portcoinㅋ
docker-compose up -d
```

2. 모니터링 환경 실행(선택사항)
```
docker-compose.monitoring.yml up -d
```

3. 프론트 코드 클론 및 실행

```
git clone https://github.com/mj-song00/PortCoin-Front
cd portcoin-front
npm run start 
```

### 트러블 슈팅
1. [K6 부하 테스트 중 timeout 발생](https://velog.io/@viento/k6로-부하테스트중-timeout-문제-발생)
- 문제:  부하 테스트 마지막 구간에서 i/o timeout 발생. 
        콘솔 확인 결과, portfolio_coin의 currentPrice 업데이트 과정에서 수십 개의 쿼리가 발생.
- 원인: JPA 연관 관계 미처리로 인해 N+1 쿼리 발생 → 응답 지연 → timeout 발생
- 해결:
  - hibernate.jdbc.batch_size 설정 (배치 업데이트로 쿼리 수 감소)
  - JPA fetch join으로 N+1 문제 해결
  - K6 시나리오 check 조건 수정
- 결과 : 전체 실패 수는 114건에서 106건으로 **약 7%** 감소

2. [N+1 문제로 인한 성능 저하 개선](https://velog.io/@viento/N1문제-해결하기)
- 문제: 포트폴리오 상세 조회 시 Hibernate가 코인 개수만큼 반복해서 쿼리를 실행하여 응답 지연 발생
- 원인: Portfolio → PortfolioCoin → Coin 간 지연 로딩으로 인한 N+1 문제
- 해결: JPQL에서 fetch join을 활용해 관련 엔티티를 한 번에 조회하도록 수정하여 쿼리 수 대폭 감소 및 응답 시간 개선


### 향후 계획 
1. SSE를 적용해 실시간 시세 변동 시 프론트에 자동 알림 설정
2. 유저별 수익률 랭킹화 및 포트폴리오 유료 공개 수익 모델 도입
