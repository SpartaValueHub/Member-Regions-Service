package com.sparta.member_regions_service.application.exception;

// 인증·회원 헤더 누락
public class UnauthorizedException extends RuntimeException {

	public UnauthorizedException(String message) {
		super(message);
	}

	public String getCode() {
		return "UNAUTHORIZED";
	}
}
