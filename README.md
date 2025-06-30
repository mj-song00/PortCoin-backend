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
PortCoin은 투자한 코인에 대한 수익률을 계산해주며, 24시간 등락률을 확인할 수 있습니다. 
PortCoin은 다음과 같은 서비스를 제공합니다.

- 개별 코인에 대한 24시간 등락률 제공
- 개별 코인에 대한 수익률을 계산
  
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
├── build.gradle
└── README.md 
```
**주요 구성 요소**
- **Spring Boot** : REST API 및 전체 비즈니스 로직 구현
- **PostgreSQL** : 유저, 포트폴리오, 코인 등 주요 데이터 저장소
- **Redis** : CoinGecko API 데이터 캐싱 
- **docker-compose.yml** : K6, Grafana, InfluxDB 컨테이너 실행을 위한 관리 도구 (K6 테스트 시각화 환경 구성)

### 설치 및 실행 방법 
1. 프로젝트 클론
```
git clone https://github.com/minji-song00/portcoin.git
```

2. docker-compose.yml 실행
```
docker-compose up -d 
```

3. 모니터링 환경 실행(선택사항)
```
docker-compose.monitoring.yml up -d
```
4. 프론트 코드 실행
