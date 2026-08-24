package com.sparta.member_regions_service.adaptor.out.mysql.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// regions 기준점 마스터 테이블
@Entity
@Table(name = "regions")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionEntity {

	// 지역 코드 (PK)
	@Id
	@Column(name = "region_code")
	private Integer regionCode;

	// 지역명
	@Column(name = "region_name", nullable = false, length = 100)
	private String regionName;

	// 기준점 위도
	@Column(name = "center_latitude", nullable = false, precision = 10, scale = 7)
	private BigDecimal centerLatitude;

	// 기준점 경도
	@Column(name = "center_longitude", nullable = false, precision = 10, scale = 7)
	private BigDecimal centerLongitude;

	// 시드·저장용
	public static RegionEntity create(
			int regionCode,
			String regionName,
			BigDecimal centerLatitude,
			BigDecimal centerLongitude
	) {
		return RegionEntity.builder()
				.regionCode(regionCode)
				.regionName(regionName)
				.centerLatitude(centerLatitude)
				.centerLongitude(centerLongitude)
				.build();
	}
}
