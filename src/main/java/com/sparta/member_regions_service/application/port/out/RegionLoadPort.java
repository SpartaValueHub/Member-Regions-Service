package com.sparta.member_regions_service.application.port.out;

import java.util.List;
import java.util.Optional;

import com.sparta.member_regions_service.domain.model.Region;

// 지역 마스터 조회
public interface RegionLoadPort {

	Optional<Region> findByRegionCode(long regionCode);

	List<Region> findAll();

	List<Region> findByRegionNameContaining(String keyword);

	long count();
}
