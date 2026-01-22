# common 모듈

`common`은 `trading` 멀티모듈 프로젝트에서 **공통 기능을 제공하는 모듈**입니다.  
서비스 모듈(예: `service:auth`)에서 `implementation project(':common')` 형태로 의존하여 재사용합니다.

## 역할

- 공통 API/유틸리티/설정 제공
- (필요 시) 헬스체크 등 공통 엔드포인트/로직 제공
- JPA 기반 DB 연결 체크(`SELECT 1`) 같은 “인프라 공통 기능”을 포함할 수 있음

> 참고: `common`이 “독립 실행 애플리케이션”인지, “라이브러리 모듈”인지는 프로젝트 운영 방식에 따라 달라질 수 있습니다.  
> 현재는 다른 서비스가 가져다 쓰는 **라이브러리 성격**을 기본으로 권장합니다.

## 프로젝트 구조(요약)

- `common/src/main/java`  
  공통 코드 (controller / service / repository 등)
- `common/src/main/resources/application.yml`  
  common 모듈 설정(필요 시 DB 연결 정보 포함)
- `common/build.gradle`  
  common 모듈 의존성 설정

## 의존성

`common`은 기본적으로 아래를 사용합니다.

- Spring Web (공통 컨트롤러가 필요한 경우)
- Spring Data JPA (DB 접근/헬스체크 등)
- MySQL Driver (런타임)

## 헬스체크 (DB Ready 체크)

DB readiness 확인이 필요할 때, JPA의 `EntityManager`로 아래 네이티브 쿼리를 실행하는 방식으로 체크할 수 있습니다.

- 쿼리: `SELECT 1`

권장 동작:

- `/live`: 프로세스 생존 확인 (DB 연결 없이도 OK)
- `/ready`: DB 연결 및 쿼리 수행 가능하면 OK, 실패하면 503

## 설정 (application.yml)

DB를 실제로 붙여서 `/ready` 같은 기능을 쓰려면 datasource 설정이 필요합니다.


### TODO 
- `전체적인 application.yml 중앙 관리를 위한 cloudConfig 구현'
- 'mapper 만 읽어 올수 있는 배포 서비스 x 모듈식 mapper 모듈 구현'
- '전체적인 서비스 정리'


