# Member Regions API

마이페이지 동네 선택·GPS 인증 API. Gateway 경유 시 `X-Member-Uuid` 필요 (로그인).

공통 Error Response: `timestamp`, `status`, `code`, `message`, `path` (+ validation 시 `fieldErrors`)

## 지역 시드 (`regions`)

- 런타임 JSON: `src/main/resources/db/regions-seed.json` (기동 시 없는 코드만 INSERT)
- **초기화/재적재용 SQL**: `src/main/resources/db/regions-seed.sql` (동일 내용 `docs/regions-seed.sql`)
  - `CREATE TABLE IF NOT EXISTS` + `INSERT ... ON DUPLICATE KEY UPDATE` (재실행 안전)
  - EC2 예: `mysql -u... -p member_regions_db < regions-seed.sql`
- 출처: [vuski/admdongkor](https://github.com/vuski/admdongkor) `HangJeongDong_ver20260401` (전국 행정동, WGS84 중심점)
- `regionCode`: 행정동 10자리 (`adm_cd2`), DB 타입 **BIGINT**
- FE(카카오맵)는 동일 행정동 코드로 `regionCode` 전달

---

## GET /api/v1/regions

| 항목 | 내용 |
|---|---|
| Summary | 지역 마스터(기준점) 목록·검색 |
| Auth | 불필요 (선택 UI용) |
| Query | `keyword` (optional, 지역명 부분 검색) |
| Response | `200` — `[{ regionCode, regionName, centerLatitude, centerLongitude }]` |

---

## GET /api/v1/member-regions

| 항목 | 내용 |
|---|---|
| Summary | 내 동네 목록 |
| Auth | Bearer → Gateway `X-Member-Uuid` |
| Response | `200` — `MemberRegionResponseVo[]` (`verified`, `verifiedAt`, `regionName` 포함) |

---

## POST /api/v1/member-regions

| 항목 | 내용 |
|---|---|
| Summary | 동네 등록 (회원당 최대 2개) |
| Auth | 필요 |
| Request | `{ "regionCode": 1168010100, "primary": true }` (`primary` 생략 가능, 첫 동네는 자동 대표) |
| Response | `201` — 등록된 동네 |
| Errors | `401 UNAUTHORIZED` · `404 REGION_NOT_FOUND` · `409 MEMBER_REGION_LIMIT_EXCEEDED` · `409 DUPLICATE_MEMBER_REGION` · `400 VALIDATION_FAILED` |

```json
{
  "regionCode": 1168010100,
  "primary": true
}
```

---

## PATCH /api/v1/member-regions/{memberRegionId}

| 항목 | 내용 |
|---|---|
| Summary | 선택 동네 변경 (기존 `verifiedAt` 무효화) |
| Auth | 필요 |
| Request | `{ "regionCode": 1168010800 }` |
| Response | `200` — 변경된 동네 (`verified=false`) |
| Errors | `401` · `403 FORBIDDEN` · `404` · `409 DUPLICATE_MEMBER_REGION` |

---

## PATCH /api/v1/member-regions/{memberRegionId}/primary

| 항목 | 내용 |
|---|---|
| Summary | 대표 동네 지정 |
| Auth | 필요 |
| Response | `200` — 대표로 지정된 동네 |

---

## POST /api/v1/member-regions/{memberRegionId}/verify

| 항목 | 내용 |
|---|---|
| Summary | GPS 동네 인증. 기준점과 거리 ≤ 정책 반경(기본 3000m)이면 `verifiedAt` 저장 후 **동일 응답으로 즉시 반영** |
| Auth | 필요 |
| Request | `{ "latitude": 37.5007, "longitude": 127.0366 }` |
| Response | `200` — `{ ..., "verified": true, "verifiedAt": "...", "regionName": "서울특별시 강남구 역삼동" }` |
| Errors | `409 REGION_VERIFICATION_FAILED` (반경 밖) · `404` · `401` · `403` |

---

## DELETE /api/v1/member-regions/{memberRegionId}

| 항목 | 내용 |
|---|---|
| Summary | 동네 삭제 (대표 삭제 시 남은 동네 중 하나를 대표로 승격) |
| Auth | 필요 |
| Response | `204` |

---

## MemberRegionResponseVo

| 필드 | 타입 | 설명 |
|---|---|---|
| memberRegionId | long | PK |
| memberUuid | string | 회원 UUID |
| primary | boolean | 대표 여부 |
| regionCode | int | 지역 코드 |
| regionName | string | 시·동 표기 |
| verified | boolean | 인증 여부 |
| verifiedAt | instant \| null | 인증 시각 |
| createdAt | instant | 등록 |
| updatedAt | instant \| null | 수정 |
