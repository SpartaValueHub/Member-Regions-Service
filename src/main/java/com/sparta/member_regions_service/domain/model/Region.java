package com.sparta.member_regions_service.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.Getter;

// 인증 거리 계산용 지역 기준점
@Getter
public class Region {

	// 지역 코드 (PK)
	private final int regionCode;
	// 지역명 (시·동)
	private final String regionName;
	// 기준점 위도
	private final BigDecimal centerLatitude;
	// 기준점 경도
	private final BigDecimal centerLongitude;

	private Region(
			int regionCode,
			String regionName,
			BigDecimal centerLatitude,
			BigDecimal centerLongitude
	) {
		this.regionCode = regionCode;
		this.regionName = regionName;
		this.centerLatitude = centerLatitude;
		this.centerLongitude = centerLongitude;
	}

	// DB·시드 복원
	public static Region restore(
			int regionCode,
			String regionName,
			BigDecimal centerLatitude,
			BigDecimal centerLongitude
	) {
		if (regionCode <= 0) {
			throw new IllegalArgumentException("지역 코드가 올바르지 않습니다.");
		}
		if (regionName == null || regionName.isBlank()) {
			throw new IllegalArgumentException("지역명이 필요합니다.");
		}
		Objects.requireNonNull(centerLatitude, "centerLatitude must not be null");
		Objects.requireNonNull(centerLongitude, "centerLongitude must not be null");
		return new Region(regionCode, regionName.trim(), centerLatitude, centerLongitude);
	}
}
