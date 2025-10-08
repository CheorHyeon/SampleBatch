# 🧩 SampleBatch

- **Spring Batch 5** 학습용 실습 프로젝트입니다.  
- **JPA 기반 Batch 구성**, **Excel 입출력 처리**, **ItemStreamReader/Writer**, **ExecutionContext 관리** 등 Spring Batch의 핵심 구조를 단계별로 구현했습니다.
- 강의를 참고하며 직접 코드로 검증하고, 각 실습별로 PR을 통해 정리했습니다.
- Spring Batch의 개념 및 이론은 블로그에 상세히 정리되어 있으며, 해당 레포지토리는 각 실습 단위별 **실행 코드와 핵심 포인트**를 다룹니다

---

## 📘 프로젝트 개요

| 항목 | 내용 |
|------|------|
| **Framework** | Spring Boot 3.x / Spring Batch 5 |
| **ORM / DB** | JPA (Hibernate), MySQL |
| **Build Tool** | Gradle |
| **Language** | Java 21 |
| **IDE** | IntelliJ IDEA |
| **테스트 목적** | Batch 구조 이해 및 Reader/Writer/Processor 동작 검증 |

---

## 🧠 실습 정리

### ✅ 사전 Setting
- 메타데이터용, 실제 Data용 DB 분리
- 각각 DataConfig 설정
- 🔗 [PR 보기](https://github.com/CheorHyeon/SampleBatch/pull/1)

### ✅ 실습 1 : JPA 기반 단순 Table 이관
- JPA 사용 Before -> After 테이블 데이터 이관
- Job, Step 등 간단한 Spring Batch 실습
- Controller, Scheduling으로 Batch 작업 실행 트리거 설정

- 🔗 [PR 보기](https://github.com/CheorHyeon/SampleBatch/pull/2)

---

### ✅ 실습 2 : JPA 기반 데이터 조회 시 조건절로 Read
- JPA Reader/Writer를 이용한 기본 데이터 처리
- win 컬럼 값이 10이 넘으면 reward 컬럼 값에 true 세팅

- 🔗 [PR 보기](https://github.com/CheorHyeon/SampleBatch/pull/3)

---

### ✅ 실습 3  : Excel -> Table Batch
- Apache POI를 활용한 Excel → Table 변환
- ItemStreamReader 생명주기 이해
- open / write / update / close 흐름 및 ExecutionContext 저장

- 🔗 [PR 보기](https://github.com/CheorHyeon/SampleBatch/pull/4)

---

### ✅ 실습 4 : Table -> Excel Batch
- Apache POI를 활용한 Table → Excel 변환
- BeforeEntity 목록을 받아 엑셀(xlsx) 로 내보내는 배치 Writer 구현
- ItemStreamWriter 생명주기 이해
- open / write / update / close 흐름
- isClosed 플래그의 필요성 이해 등

- 🔗 [PR 보기](https://github.com/CheorHyeon/SampleBatch/pull/5)


---

## 📂 참고
- 블로그: [Spring Batch 알아보기](https://velog.io/@puar12/Spring-Batch)
- 강의: [스프링 배치 5](https://www.youtube.com/watch?v=MNzPsOQ3NJk&list=PLJkjrxxiBSFCaxkvfuZaK5FzqQWJwmTfR)
