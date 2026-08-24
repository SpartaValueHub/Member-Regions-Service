package com.sparta.member_regions_service.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import lombok.Getter;

// 회원이 선택한 동네 (인증 결과 포함)
@Getter
public class MemberRegion {

	// DB PK (신규면 null)
	private Long memberRegionId;
	// 회원 UUID
	private final String memberUuid;
	// 대표 동네 여부
	private boolean primary;
	// 지역 코드
	private long regionCode;
	// 지역명 (시·동)
	private String regionName;
	// GPS 인증 완료 시각 (미인증이면 null)
	private Instant verifiedAt;
	// 등록일시
	private final Instant createdAt;
	// 수정일시
	private Instant updatedAt;

	private MemberRegion(
			Long memberRegionId,
			String memberUuid,
			boolean primary,
			long regionCode,
			String regionName,
			Instant verifiedAt,
			Instant createdAt,
			Instant updatedAt
	) {
		this.memberRegionId = memberRegionId;
		this.memberUuid = memberUuid;
		this.primary = primary;
		this.regionCode = regionCode;
		this.regionName = regionName;
		this.verifiedAt = verifiedAt;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	// 신규 동네 등록
	public static MemberRegion create(
			String memberUuid,
			boolean primary,
			long regionCode,
			String regionName,
			Instant createdAt
	) {
		requireMemberUuid(memberUuid);
		requireRegionCode(regionCode);
		requireRegionName(regionName);
		Objects.requireNonNull(createdAt, "createdAt must not be null");

		return new MemberRegion(
				null,
				memberUuid.trim(),
				primary,
				regionCode,
				regionName.trim(),
				null,
				createdAt,
				null
		);
	}

	// DB 복원
	public static MemberRegion restore(
			Long memberRegionId,
			String memberUuid,
			boolean primary,
			long regionCode,
			String regionName,
			Instant verifiedAt,
			Instant createdAt,
			Instant updatedAt
	) {
		return new MemberRegion(
				memberRegionId,
				memberUuid,
				primary,
				regionCode,
				regionName,
				verifiedAt,
				createdAt,
				updatedAt
		);
	}

	// GPS 인증 완료
	public void markVerified(Instant verifiedAt, Instant updatedAt) {
		Objects.requireNonNull(verifiedAt, "verifiedAt must not be null");
		Objects.requireNonNull(updatedAt, "updatedAt must not be null");
		this.verifiedAt = verifiedAt;
		this.updatedAt = updatedAt;
	}

	// 동네 변경 등으로 인증 무효화
	public void clearVerification(Instant updatedAt) {
		Objects.requireNonNull(updatedAt, "updatedAt must not be null");
		this.verifiedAt = null;
		this.updatedAt = updatedAt;
	}

	// 지역 코드·명 변경 (인증 무효화)
	public void changeRegion(long regionCode, String regionName, Instant updatedAt) {
		requireRegionCode(regionCode);
		requireRegionName(regionName);
		Objects.requireNonNull(updatedAt, "updatedAt must not be null");
		this.regionCode = regionCode;
		this.regionName = regionName.trim();
		this.verifiedAt = null;
		this.updatedAt = updatedAt;
	}

	// 대표 동네로 지정
	public void markAsPrimary(Instant updatedAt) {
		Objects.requireNonNull(updatedAt, "updatedAt must not be null");
		this.primary = true;
		this.updatedAt = updatedAt;
	}

	// 대표 동네 해제
	public void clearPrimary(Instant updatedAt) {
		Objects.requireNonNull(updatedAt, "updatedAt must not be null");
		this.primary = false;
		this.updatedAt = updatedAt;
	}

	// 인증 완료 여부
	public boolean isVerified() {
		return verifiedAt != null;
	}

	private static void requireMemberUuid(String memberUuid) {
		if (memberUuid == null || memberUuid.isBlank()) {
			throw new IllegalArgumentException("회원 정보가 필요합니다.");
		}
	}

	private static void requireRegionCode(long regionCode) {
		if (regionCode <= 0L) {
			throw new IllegalArgumentException("지역 코드가 올바르지 않습니다.");
		}
	}

	private static void requireRegionName(String regionName) {
		if (regionName == null || regionName.isBlank()) {
			throw new IllegalArgumentException("지역명이 필요합니다.");
		}
		if (regionName.trim().length() > 100) {
			throw new IllegalArgumentException("지역명은 100자 이하여야 합니다.");
		}
	}
}
