package com.sparta.member_regions_service.application.port.in.dto;

// 동네 등록 명령
public record AddMemberRegionCommand(
		// 지역 코드
		long regionCode,
		// 대표로 지정할지 여부 (null이면 첫 동네만 자동 대표)
		Boolean primary
) {
}
