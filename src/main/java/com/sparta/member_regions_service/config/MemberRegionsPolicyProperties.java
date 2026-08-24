package com.sparta.member_regions_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// 동네 인증 정책 (yml member-regions.policy)
@ConfigurationProperties(prefix = "member-regions.policy")
public record MemberRegionsPolicyProperties(
		// 회원당 등록 가능한 최대 동네 수
		int maxRegionsPerMember,
		// GPS 인증 허용 반경 (미터)
		int verificationRadiusMeters
) {
}
