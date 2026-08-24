package com.sparta.member_regions_service.adaptor.in.web.vo;

import java.math.BigDecimal;

// 지역 마스터 응답
public record RegionResponseVo(
		int regionCode,
		String regionName,
		BigDecimal centerLatitude,
		BigDecimal centerLongitude
) {
}
