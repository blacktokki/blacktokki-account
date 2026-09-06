# blacktokki-account

blacktokki-account는 사용자 인증, 계정 관리 기능을 제공하는 Spring Boot 기반 백엔드 서비스입니다.

다른 서비스/프로젝트들([tokki-tok](https://github.com/blacktokki/tokki-tok), [blacktokki-notebook](https://github.com/blacktokki/blacktokki-notebook))의 계정 기능을 재사용하기 위해 해당 프로젝트를 분리하여 개발하고 있습니다.

## Installation

저장소를 복제하고 데이터베이스 및 환경 설정을 구성합니다.

```bash
git clone https://github.com/blacktokki/blacktokki-account.git
cd blacktokki-account
bash scripts/setup.sh <database_user> <database_password>
```

## Usage

Gradle 래퍼를 사용하여 각 마이크로서비스를 실행합니다.

```bash
# 1. Discovery Service 실행 (Eureka)
./gradlew :discovery-service:bootRun

# 2. Gateway Service 실행
./gradlew :gateway-service:bootRun

# 3. Account Service 실행
./gradlew :account-service:bootRun
```

또는 스크립트를 사용하여 백그라운드로 일괄 실행할 수 있습니다.

```bash
bash scripts/runserver.sh discovery gateway account
```

## Contributing

본 프로젝트는 개인 프로젝트로 외부 기여(Pull Request)나 기능 제안을 받지 않습니다. 필요한 경우 자유롭게 Fork하여 사용하시기 바랍니다.

## License

[MIT](https://choosealicense.com/licenses/mit/)
