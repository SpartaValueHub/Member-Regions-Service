package com.sparta.member_regions_service.adaptor.in.web.vo;

import java.time.Instant;

// 회원 동네 응답 (인증 결과 즉시 반영)
public record MemberRegionResponseVo(
		Long memberRegionId,
		String memberUuid,
		boolean primary,
		long regionCode,
		String regionName,
		boolean verified,
		Instant verifiedAt,
		Instant createdAt,
		Instant updatedAt
) {
}
