package com.sparta.member_regions_service.domain.exception;

// 동네 등록 개수 초과
public class MemberRegionLimitExceededException extends RuntimeException {

	public MemberRegionLimitExceededException(String message) {
		super(message);
	}

	public String getCode() {
		return "MEMBER_REGION_LIMIT_EXCEEDED";
	}
}
