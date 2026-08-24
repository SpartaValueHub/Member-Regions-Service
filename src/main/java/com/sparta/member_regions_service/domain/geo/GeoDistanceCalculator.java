package com.sparta.member_regions_service.domain.geo;

import java.math.BigDecimal;

// Haversine 기반 거리 계산 (미터) — Spring 비의존
public class GeoDistanceCalculator {

	// 지구 반지름 (미터)
	private final double earthRadiusMeters;

	public GeoDistanceCalculator() {
		this(6_371_000d);
	}

	public GeoDistanceCalculator(double earthRadiusMeters) {
		this.earthRadiusMeters = earthRadiusMeters;
	}

	// 두 좌표 사이 거리 (미터)
	public double distanceMeters(
			BigDecimal latitudeA,
			BigDecimal longitudeA,
			BigDecimal latitudeB,
			BigDecimal longitudeB
	) {
		double lat1 = Math.toRadians(latitudeA.doubleValue());
		double lat2 = Math.toRadians(latitudeB.doubleValue());
		double dLat = lat2 - lat1;
		double dLng = Math.toRadians(longitudeB.doubleValue() - longitudeA.doubleValue());

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return earthRadiusMeters * c;
	}

	// 허용 반경 이내 여부
	public boolean isWithinRadius(
			BigDecimal latitudeA,
			BigDecimal longitudeA,
			BigDecimal latitudeB,
			BigDecimal longitudeB,
			int radiusMeters
	) {
		return distanceMeters(latitudeA, longitudeA, latitudeB, longitudeB) <= radiusMeters;
	}
}
