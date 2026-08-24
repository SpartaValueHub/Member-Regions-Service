package com.sparta.member_regions_service.domain.geo;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class GeoDistanceCalculatorTest {

	// 거리 계산기
	private final GeoDistanceCalculator calculator = new GeoDistanceCalculator();

	@Test
	void samePoint_isWithinRadius() {
		BigDecimal lat = new BigDecimal("37.5007");
		BigDecimal lng = new BigDecimal("127.0366");

		assertThat(calculator.isWithinRadius(lat, lng, lat, lng, 3000)).isTrue();
	}

	@Test
	void farPoint_isOutsideRadius() {
		BigDecimal centerLat = new BigDecimal("37.5007");
		BigDecimal centerLng = new BigDecimal("127.0366");
		// 대략 부산 근처
		BigDecimal farLat = new BigDecimal("35.1796");
		BigDecimal farLng = new BigDecimal("129.0756");

		assertThat(calculator.isWithinRadius(farLat, farLng, centerLat, centerLng, 3000)).isFalse();
	}
}
