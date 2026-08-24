package com.sparta.member_regions_service.application.port.in;

import com.sparta.member_regions_service.application.port.in.dto.AddMemberRegionCommand;
import com.sparta.member_regions_service.application.port.in.dto.MemberRegionDto;

// 동네 등록
public interface AddMemberRegionUseCase {

	MemberRegionDto add(String memberUuid, AddMemberRegionCommand command);
}
