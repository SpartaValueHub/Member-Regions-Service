package com.sparta.member_regions_service.domain.exception;

// 기준점 지역 마스터 없음
public class RegionNotFoundException extends RuntimeException {

	public RegionNotFoundException(String message) {
		super(message);
	}

	public String getCode() {
		return "REGION_NOT_FOUND";
	}
}
