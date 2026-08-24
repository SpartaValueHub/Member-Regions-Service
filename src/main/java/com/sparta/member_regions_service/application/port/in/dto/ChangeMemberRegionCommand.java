package com.sparta.member_regions_service.application.port.in.dto;

// 동네 변경 명령 (인증 무효화)
public record ChangeMemberRegionCommand(
		// 회원 동네 PK
		long memberRegionId,
		// 새 지역 코드
		int regionCode
) {
}
