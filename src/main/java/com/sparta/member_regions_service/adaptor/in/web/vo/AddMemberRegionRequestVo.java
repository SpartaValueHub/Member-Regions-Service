package com.sparta.member_regions_service.adaptor.in.web.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// 동네 등록 요청
public record AddMemberRegionRequestVo(
		// 지역 코드
		@NotNull(message = "지역 코드는 필수입니다.")
		@Positive(message = "지역 코드가 올바르지 않습니다.")
		Long regionCode,
		// 대표 지정 여부 (선택)
		Boolean primary
) {
}
