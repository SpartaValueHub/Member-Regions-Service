package com.sparta.member_regions_service.adaptor.in.web.vo;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

// GPS 인증 요청
public record VerifyMemberRegionRequestVo(
		// 현재 위도
		@NotNull(message = "위도는 필수입니다.")
		@DecimalMin(value = "-90.0", message = "위도 범위가 올바르지 않습니다.")
		@DecimalMax(value = "90.0", message = "위도 범위가 올바르지 않습니다.")
		BigDecimal latitude,
		// 현재 경도
		@NotNull(message = "경도는 필수입니다.")
		@DecimalMin(value = "-180.0", message = "경도 범위가 올바르지 않습니다.")
		@DecimalMax(value = "180.0", message = "경도 범위가 올바르지 않습니다.")
		BigDecimal longitude
) {
}
