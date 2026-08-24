package com.sparta.member_regions_service.domain.exception;

// 회원 동네 없음
public class MemberRegionNotFoundException extends RuntimeException {

	public MemberRegionNotFoundException(String message) {
		super(message);
	}

	public String getCode() {
		return "MEMBER_REGION_NOT_FOUND";
	}
}
