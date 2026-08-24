package com.sparta.member_regions_service.application.port.in;

import java.util.List;

import com.sparta.member_regions_service.application.port.in.dto.RegionDto;

// 지역 마스터 검색·목록
public interface ListRegionsUseCase {

	List<RegionDto> list(String keyword);
}
