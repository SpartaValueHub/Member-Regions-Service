package com.sparta.member_regions_service.adaptor.in.web;

// Gateway가 JWT 검증 후 내려주는 내부 헤더 이름
public final class InternalAuthHeaders {

	// 회원 UUID
	public static final String MEMBER_UUID = "X-Member-Uuid";

	private InternalAuthHeaders() {
	}
}
