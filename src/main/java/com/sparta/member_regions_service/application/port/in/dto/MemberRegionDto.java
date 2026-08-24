package com.sparta.member_regions_service.application.port.in.dto;

import java.time.Instant;

// 회원 동네 응답 DTO (마이페이지 즉시 반영용)
public record MemberRegionDto(
		// 회원 동네 PK
		Long memberRegionId,
		// 회원 UUID
		String memberUuid,
		// 대표 여부
		boolean primary,
		// 지역 코드
		int regionCode,
		// 지역명
		String regionName,
		// 인증 완료 여부
		boolean verified,
		// 인증 시각
		Instant verifiedAt,
		// 등록일시
		Instant createdAt,
		// 수정일시
		Instant updatedAt
) {
}
