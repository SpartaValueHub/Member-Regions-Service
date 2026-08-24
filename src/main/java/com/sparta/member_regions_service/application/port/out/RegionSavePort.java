package com.sparta.member_regions_service.application.port.out;

import java.util.List;

import com.sparta.member_regions_service.domain.model.Region;

// 지역 마스터 저장 (시드)
public interface RegionSavePort {

	void saveAll(List<Region> regions);
}
