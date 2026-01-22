## TROUBLE SHOOTING

1. ENV 중앙관리 시스템 필요.
    * application.yml 의 중복으로 다른 서비스에서도 혼용이 될 수 있음.
   * Mapper scan 이 배포 서비스를 보고있어 혼용되기 쉬워 mapper 모듈이 필요함.

   <br>
2. Run 서비스에서 비동기식 호출시 같은 스레드에서 Redis AccessKey 요청 시 중복요청
    * Redis Lock 구현이 필요해보임.
    * 내부 필터 및 인증 필터를 거치는 로직을 재설계 필요함.

3. /api/kis/auth/oauth/token 의 용도를 재정의가 필요해보임.