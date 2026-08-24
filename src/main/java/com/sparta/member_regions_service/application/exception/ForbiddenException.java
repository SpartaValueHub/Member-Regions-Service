package com.sparta.member_regions_service.application.exception;

// 타인 동네 접근
public class ForbiddenException extends RuntimeException {

	public ForbiddenException(String message) {
		super(message);
	}

	public String getCode() {
		return "FORBIDDEN";
	}
}
