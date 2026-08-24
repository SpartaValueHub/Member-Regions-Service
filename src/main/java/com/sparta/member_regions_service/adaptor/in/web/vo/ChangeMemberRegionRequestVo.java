package com.sparta.member_regions_service.adaptor.in.web.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// 동네 변경 요청
public record ChangeMemberRegionRequestVo(
		// 새 지역 코드
		@NotNull(message = "지역 코드는 필수입니다.")
		@Positive(message = "지역 코드가 올바르지 않습니다.")
		Long regionCode
) {
}
