# 🧾 종이보다 디지털, 번거로움 대신 KB리핏

> KB의 새로운 전자영수증 서비스 KB리핏!
> KB리핏은 Receipt, Reward, Benefit의 합성어로, 영수증의 디지털화를 통해 혜택을 돌려주는 웹 애플리케이션입니다.

## 😀 프로젝트 목적

전자영수증 기반의 회계·세무 자동화 및 보험 청구 지갑 서비스 ‘KB 리핏’은 아래와 같은 목적을 위해 개발되었습니다.

### 1. 환경 개선 (ESG)

- 환경부에 따르면 2018년 기준, 연간 약 128억 건의 종이영수증 발행
- 원목 약 128,900 그루 벌목 추산 → 전자영수증으로 전환 시, 산림 보호 및 자원 절감에 직접적 기여
- 종이영수증은 감열지로 제작되어 재활용 불가
- 소비자는 언제 어디서나 디지털 방식으로 영수증 열람 가능 → 소비자 편의성 및 접근성 증대

### 2. 가치 창출

#### 개인 사업자

- 수기로 관리하던 영수증의 디지털 전환 → 입출금 내역의 체계적 관리
- 전자영수증을 통한 비용 증빙 자동화 → 세무·자금 관리 효율성 향상
- 의료비 납입 영수증을 전자화 후 보험사로 자동 전송 → 기업 복지 시스템과의 연계로 관리·운영 비용 절감

#### 일반 사용자

- 개인 정보 유출 방지 → 종이영수증 분실 및 폐기 시 발생할 수 있는 피싱, 스미싱 등의 범죄 위험 최소화
- 소비 기록의 장기 보관 가능 → 별도의 보관 노력없이 전자 방식으로 장기적인 소비 기록 확인 가능
- 소비 패턴 분석 → 카테고리 분류를 통해 개인의 소비 패턴 성향, 지출 패턴 시각화 ⇒ 자산 관리 및 소비 습관 개선에 기여
- 영수증 만으로 새로운 가치 창출 → 소비 패턴을 기반으로 맞춤형 금융 상품 연동 ⇒ 국민은행의 신규 고객으로 자연스러운 전환 유도
- 병원·약국 등에서 발생한 납입 내역을 보험사로 자동 전송 → 사용자 불편 최소화

> 본 프로젝트는 IT's Your Life 6기 실무 역량 프로젝트이며, 금융 주제를 기반한 프로젝트 설계와 경험, 완성을 목표로 합니다.

## 📅 프로젝트 기간

2025.07.09 ~ 2025.08.21

## 🛠️ 기술 스택

| 구분      | 기술 스택                                                 |
| --------- | --------------------------------------------------------- |
| Core      | Spring Legacy, Java17, Lombok                          |
| 보안/인증 | Spring Security, JWT (JwtTokenProvider), BCryptPasswordEncoder, CORS  |
| 데이터베이스  | MySQL, MyBatis                       |
| 테스트    | JUnit5                                                   |
| 문서화    | Swagger v2(Springfox)                                |
| 배포/인프라    | Docker, GitHub Actions, AWS EC2, Nginx, Let’s Encrypt(Certbot)    |
| 기타      | Firebase Cloud Message (푸시알림) |


## 🖌 와이어프레임

클릭해서 와이어프레임을 확인하세요

### 클라이언트 페이지

<details>
  <summary>로그인</summary>
<img width="282" height="526" alt="스크린샷 2025-08-18 오후 1 56 39" src="https://github.com/user-attachments/assets/9ef50632-97d4-4ac6-b50c-4729db564ca2" />
</details>

<details>
  <summary>포인트</summary>
<img width="703" height="664" alt="스크린샷 2025-08-18 오후 1 57 25" src="https://github.com/user-attachments/assets/d69cc220-04ed-4700-990d-1530a870c78b" />
</details>

<details>
  <summary>구매영수증</summary>
<img width="950" height="458" alt="스크린샷 2025-08-18 오후 1 58 04" src="https://github.com/user-attachments/assets/17eee23f-b7ad-43fb-869a-f4f915e253f3" />
</details>

<details>
  <summary>병원영수증</summary>
<img width="874" height="344" alt="스크린샷 2025-08-18 오후 1 58 35" src="https://github.com/user-attachments/assets/1cb126f2-cac3-436f-a0ff-9e51cd42d9d9" />
</details>

<details>
  <summary>영수 처리하기</summary>
<img width="933" height="252" alt="스크린샷 2025-08-18 오후 1 59 13" src="https://github.com/user-attachments/assets/f615e57e-a478-4510-ab49-ba7152e4c984" />
</details>

<details>
  <summary>보험 처리하기</summary>
<img width="882" height="281" alt="스크린샷 2025-08-18 오후 2 00 27" src="https://github.com/user-attachments/assets/8eb60c26-72d7-443b-90c1-a4729e6989fa" />
</details>

<details>
  <summary>지갑 꾸미기</summary>
<img width="876" height="792" alt="스크린샷 2025-08-18 오후 2 00 49" src="https://github.com/user-attachments/assets/a66b369e-c346-405c-9a16-48429419ae73" />
</details>

### 사장님 페이지

<details>
  <summary>경비 처리</summary>
<img width="647" height="733" alt="스크린샷 2025-08-18 오후 2 02 59" src="https://github.com/user-attachments/assets/360e9cc2-f779-4612-9c54-ce2b1af3d7d3" />
<img width="673" height="511" alt="스크린샷 2025-08-18 오후 2 03 19" src="https://github.com/user-attachments/assets/501eb31c-cbc1-499d-982a-576762b09f44" />

</details>

<details>
  <summary>사업자 영수증</summary>
<img width="839" height="519" alt="스크린샷 2025-08-18 오후 2 03 40" src="https://github.com/user-attachments/assets/df4b25b2-6127-45ce-9e36-ed3e0490b0c1" />
</details>

## ⚙ 설치 및 실행 방법

### 1. 레포지토리 클론

```sh
git clone https://github.com/KB-PBL-GGABEES/kb_refit_server.git
cd kb_refit_server
```

### 2. 필수 설치

Java 17 (Temurin 권장)

Gradle(래퍼 사용 권장: ./gradlew)

MySQL 8+ (로컬 또는 Docker)



## 📌 주요 기능

### ✅ 세무·자금 관리 및 법적 증빙 수단용 전자영수증 발급

- pos 시뮬레이터를 활용한 전자영수증 발급 과정 시뮬레이션
- 일반 사용자 및 사업자 사용자 유저 결제 내역 연계

### ✅ 소비 패턴 분석 기반 맞춤형 혜택 제공

- 전자 영수증 내역 기반 소비 카테고리 분석
- 배지 시스템을 통한 맞춤형 혜택 리워드 제공

### ✅ 전자영수증 기반 의료비 청구 간소화

- 의료 영수증 연계 실비 보험금 청구 간소화
- 보험사 연계 진료비 세부 산정 내역 송신 과정 funnel 구조로 연계

### ✅ 경비 처리

- 사업체 연계 경비 처리 과정 간소화
- 법인 카드 사용 반려 및 환수 조치 과정 간소화

👥 팀원
| 이름 | 사진 | GitHub | 역할 | 담당 기능|
|---|---|---|---|---|
| 강수민 | <img width="150" height="150" alt="image" src="https://github.com/user-attachments/assets/5434ed55-144b-400f-947a-40b31c79d42b" />| @suminiee | 백엔드 리딩 & 인프라 | spring security적용 및 로그인 구현</br> 전자 지갑 꾸미기 코드 구현</br> blue-green 무중단 배포  |
| 최은서 | <img width="150" height="150" alt="image" src="https://github.com/user-attachments/assets/7c56b67e-d30c-4fd6-9784-8388bde02cbf" />| @Cho2unseo | 백엔드 | 전자영수증 발급 및 리워드 적립 |
| 조경환 | <img width="150" height="150" alt="image" src="https://github.com/user-attachments/assets/89557d9e-28a8-4631-9064-80bf362bf486" />| @ghks027 | 백엔드 | 경비 처리 및 법인카드 승인/반려 |
| 김연후 | <img width="150" height="150" alt="image" src="https://github.com/user-attachments/assets/efea8193-39ab-471a-881a-59a8284a1795" />| @yeonhookim | 백엔드 | 병원 영수증 보험 청구 및 영수 처리 |


## 👥 개선 사항 및 후기

### 🐱 강수민

#### 📝 프로젝트 후기
처음으로 금융권 프로젝트 백엔드 개발에 참여하면서 많은 경험을 쌓을 수 있었습니다. 단순히 프로젝트를 진행하고 코드를 작성하는 데 그치지 않고, 팀 내 지식 공유와 문서화에 집중하여 함께 성장할 수 있도록 노력했습니다. 또한 프론트엔드와의 원활한 협업을 위해 API 설계와 통신 방식의 편의성을 고민했으며, 다양한 상황에서도 안정적으로 동작할 수 있도록 JUnit5 기반 테스트 코드와 QA sheet를 작성해 검증을 강화했습니다.

특히 인프라 리소스가 제한된 환경에서도 효율적인 배포 및 인프라 구성을 최적화하여 프로젝트를 무사히 운영할 수 있었습니다. 한 달이라는 짧은 기간 동안 쉽지 않은 도전이었지만, 계획대로 프로젝트를 완수하며 많은 성장을 이룰 수 있었던 값진 경험이었습니다.


#### 🔄 이번 프로젝트에서 참여한 부분들
- Spring Legacy 전반적인 세팅
- Spring Security 적용
- JWT 기반 로그인 기능 구현
- 전자지갑 꾸미기(지갑 브랜드, 배지 착용, 배지 프리셋) 기능 구현
- JavaMailSender & OpenCSV 사용하여 데이터 CSV파일로 만들어 이메일에 첨부 후 보내는 기능 구현
- FCM 실시간 알림 기능 구현
- EC2 & Docker & Nginx & Github Actions 사용하여 Blue-Green 무중단배포
- Let’s Encrypt(Certbot) 사용하여 Https 적용


#### 🐞 이슈 트래킹 기록
https://velog.io/@suminiee/FCM-spring-legacy%EC%97%90%EC%84%9C-FCM-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84


#### 트러블 슈팅 요약
Spring Legacy에서 FCM 연동 과정에서 임의 토큰으로는 테스트가 불가능해 오류가 발생했고, 이를 해결하기 위해 Firebase 클라이언트 SDK 기반 토큰 발급용 웹 페이지(get_fcm_token.html + service worker)를 만들어 로컬 서버(https:// 대신 localhost)에서 실행하여 유효한 FCM 토큰을 발급받아 정상적으로 알림 전송을 검증했다는 트러블슈팅입니다.

 

### 👩‍💻 최은서

#### 📝 프로젝트 후기



#### 🔄 이번 프로젝트에서 참여한 부분들



#### 🐞 이슈 트래킹 기록



#### 트러블 슈팅 요약




### 👩‍💻 조경환

#### 📝 프로젝트 후기



#### 🔄 이번 프로젝트에서 참여한 부분들
- 사장님 API(경비 처리·법카 조회) 전반 구현 및 커서 기반 페이지네이션·동적 필터링 적용
- 프로젝트 관리 문서 (WBS/Gantt) 작성
- 기술 명세서 작성


#### 🐞 이슈 트래킹 기록
https://ghks027.tistory.com/2


#### 트러블 슈팅 요약
배포 서버에서 receiptId와 receiptProcessId가 다르게 저장되어 있음에도 불구하고 프론트에서는 receiptId만 전달, 백엔드는 이를 receiptProcessId로 오인해 잘못된 영수증이 처리되는 문제가 발생했다. 로컬 테스트 환경에서는 두 값이 우연히 같아서 문제가 드러나지 않았으며 이를 해결하기 위해 프론트 요청을 receiptId 기준으로 통일하고 백엔드에서 매핑하도록 로직을 수정했다.



### 👩‍💻 김연후 

#### 📝 프로젝트 후기



#### 🔄 이번 프로젝트에서 참여한 부분들



#### 🐞 이슈 트래킹 기록
https://velog.io/@rladusgn3/%EC%82%AC%EC%97%85%EC%9E%90-%EC%A7%84%EC%9C%84%ED%99%95%EC%9D%B8-%EB%A1%9C%EC%A7%81-%EA%B0%9C%EC%84%A0-Insert-%EA%B8%B0%EB%B0%98%EC%97%90%EC%84%9C-%EC%A1%B0%ED%9A%8C-%EA%B8%B0%EB%B0%98%EC%9C%BC%EB%A1%9C-%EC%A0%84%ED%99%98


#### 트러블 슈팅 요약
국세청 OpenAPI 사업자 진위 확인 기능에서 데이터 중복 삽입과 로직 불일치 문제가 발생했습니다. 원인은 Insert 중심 설계, DTO 타입 불일치, 예외 처리 부족이었으며, 해결책은 Insert 로직 제거 후 조회 기반 검증으로 전환, DTO 일관성 유지, 방어적 코딩 적용이었습니다. 이를 통해 데이터 무결성 확보와 코드-DB 동기화의 중요성을 확인했습니다.


