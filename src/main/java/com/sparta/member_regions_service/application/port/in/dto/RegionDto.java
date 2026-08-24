package com.sparta.member_regions_service.application.port.in.dto;

import java.math.BigDecimal;

// 지역 마스터 조회 DTO
public record RegionDto(
		// 지역 코드
		long regionCode,
		// 지역명
		String regionName,
		// 기준점 위도
		BigDecimal centerLatitude,
		// 기준점 경도
		BigDecimal centerLongitude
) {
}
