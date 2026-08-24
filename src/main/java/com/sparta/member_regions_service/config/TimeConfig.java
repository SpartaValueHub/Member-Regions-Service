package com.sparta.member_regions_service.config;

import java.time.Clock;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sparta.member_regions_service.domain.geo.GeoDistanceCalculator;

// 시간·거리 계산 빈 등록
@Configuration
@EnableConfigurationProperties(MemberRegionsPolicyProperties.class)
public class TimeConfig {

	// UTC 기준 시스템 시계
	@Bean
	Clock clock() {
		return Clock.systemUTC();
	}

	// GPS 거리 계산기
	@Bean
	GeoDistanceCalculator geoDistanceCalculator() {
		return new GeoDistanceCalculator();
	}
}
