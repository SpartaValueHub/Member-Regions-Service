package com.sparta.member_regions_service.adaptor.out.mysql.mapper;

import org.springframework.stereotype.Component;

import com.sparta.member_regions_service.adaptor.out.mysql.entity.MemberRegionEntity;
import com.sparta.member_regions_service.domain.model.MemberRegion;

// MemberRegion Entity ↔ Domain
@Component
public class MemberRegionEntityMapper {

	public MemberRegion toDomain(MemberRegionEntity entity) {
		return MemberRegion.restore(
				entity.getMemberRegionId(),
				entity.getMemberUuid(),
				entity.isPrimary(),
				entity.getRegionCode(),
				entity.getRegionName(),
				entity.getVerifiedAt(),
				entity.getCreatedAt(),
				entity.getUpdatedAt()
		);
	}

	public MemberRegionEntity toNewEntity(MemberRegion domain) {
		return MemberRegionEntity.create(
				domain.getMemberUuid(),
				domain.isPrimary(),
				domain.getRegionCode(),
				domain.getRegionName(),
				domain.getVerifiedAt(),
				domain.getCreatedAt(),
				domain.getUpdatedAt()
		);
	}

	public void apply(MemberRegion domain, MemberRegionEntity entity) {
		entity.update(
				domain.isPrimary(),
				domain.getRegionCode(),
				domain.getRegionName(),
				domain.getVerifiedAt(),
				domain.getUpdatedAt()
		);
	}
}
