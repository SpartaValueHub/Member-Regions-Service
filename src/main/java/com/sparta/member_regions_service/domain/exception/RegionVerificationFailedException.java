package com.sparta.member_regions_service.domain.exception;

// GPS가 허용 반경 밖
public class RegionVerificationFailedException extends RuntimeException {

	public RegionVerificationFailedException(String message) {
		super(message);
	}

	public String getCode() {
		return "REGION_VERIFICATION_FAILED";
	}
}
