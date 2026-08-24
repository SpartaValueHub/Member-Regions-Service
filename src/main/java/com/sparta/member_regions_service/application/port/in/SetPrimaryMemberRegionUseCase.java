package com.sparta.member_regions_service.application.port.in;

import com.sparta.member_regions_service.application.port.in.dto.MemberRegionDto;

// 대표 동네 지정
public interface SetPrimaryMemberRegionUseCase {

	MemberRegionDto setPrimary(String memberUuid, long memberRegionId);
}
