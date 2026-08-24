package com.sparta.member_regions_service.application.port.in;

import java.util.List;

import com.sparta.member_regions_service.application.port.in.dto.MemberRegionDto;

// 내 동네 목록 조회
public interface ListMyMemberRegionsUseCase {

	List<MemberRegionDto> listMine(String memberUuid);
}
