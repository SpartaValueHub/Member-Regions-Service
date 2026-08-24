package com.sparta.member_regions_service.adaptor.out.mysql.mapper;

import org.springframework.stereotype.Component;

import com.sparta.member_regions_service.adaptor.out.mysql.entity.RegionEntity;
import com.sparta.member_regions_service.domain.model.Region;

// Region Entity ↔ Domain
@Component
public class RegionEntityMapper {

	public Region toDomain(RegionEntity entity) {
		return Region.restore(
				entity.getRegionCode(),
				entity.getRegionName(),
				entity.getCenterLatitude(),
				entity.getCenterLongitude()
		);
	}

	public RegionEntity toEntity(Region domain) {
		return RegionEntity.create(
				domain.getRegionCode(),
				domain.getRegionName(),
				domain.getCenterLatitude(),
				domain.getCenterLongitude()
		);
	}
}
