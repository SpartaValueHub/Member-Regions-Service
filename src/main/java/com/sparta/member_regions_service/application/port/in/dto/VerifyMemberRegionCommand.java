package com.sparta.member_regions_service.application.port.in.dto;

import java.math.BigDecimal;

// GPS 동네 인증 명령
public record VerifyMemberRegionCommand(
		// 회원 동네 PK
		long memberRegionId,
		// 현재 위도
		BigDecimal latitude,
		// 현재 경도
		BigDecimal longitude
) {
}
