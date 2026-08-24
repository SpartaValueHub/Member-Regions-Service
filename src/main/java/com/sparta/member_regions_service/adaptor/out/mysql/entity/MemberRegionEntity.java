package com.sparta.member_regions_service.adaptor.out.mysql.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// member_regions 테이블
@Entity
@Table(
		name = "member_regions",
		uniqueConstraints = @UniqueConstraint(
				name = "uk_member_regions_member_uuid_region_code",
				columnNames = {"member_uuid", "region_code"}
		)
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberRegionEntity {

	// DB PK
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_region_id")
	private Long memberRegionId;

	// 회원 UUID
	@Column(name = "member_uuid", nullable = false, length = 36)
	private String memberUuid;

	// 대표 동네 여부
	@Column(name = "is_primary", nullable = false)
	private boolean primary;

	// 지역 코드
	@Column(name = "region_code", nullable = false)
	private int regionCode;

	// 지역명
	@Column(name = "region_name", nullable = false, length = 100)
	private String regionName;

	// GPS 인증 시각
	@Column(name = "verified_at")
	private Instant verifiedAt;

	// 등록일시
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	// 수정일시
	@Column(name = "updated_at")
	private Instant updatedAt;

	// 신규 저장용
	public static MemberRegionEntity create(
			String memberUuid,
			boolean primary,
			int regionCode,
			String regionName,
			Instant verifiedAt,
			Instant createdAt,
			Instant updatedAt
	) {
		return MemberRegionEntity.builder()
				.memberUuid(memberUuid)
				.primary(primary)
				.regionCode(regionCode)
				.regionName(regionName)
				.verifiedAt(verifiedAt)
				.createdAt(createdAt)
				.updatedAt(updatedAt)
				.build();
	}

	// 도메인 변경 반영
	public void update(
			boolean primary,
			int regionCode,
			String regionName,
			Instant verifiedAt,
			Instant updatedAt
	) {
		this.primary = primary;
		this.regionCode = regionCode;
		this.regionName = regionName;
		this.verifiedAt = verifiedAt;
		this.updatedAt = updatedAt;
	}
}
