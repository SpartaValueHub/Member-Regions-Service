package com.sparta.member_regions_service.domain.exception;

// 동일 동네 중복 등록
public class DuplicateMemberRegionException extends RuntimeException {

	public DuplicateMemberRegionException(String message) {
		super(message);
	}

	public String getCode() {
		return "DUPLICATE_MEMBER_REGION";
	}
}
