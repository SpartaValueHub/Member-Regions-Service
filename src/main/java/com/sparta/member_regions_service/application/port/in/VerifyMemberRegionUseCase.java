package com.sparta.member_regions_service.application.port.in;

import com.sparta.member_regions_service.application.port.in.dto.MemberRegionDto;
import com.sparta.member_regions_service.application.port.in.dto.VerifyMemberRegionCommand;

// GPS 동네 인증
public interface VerifyMemberRegionUseCase {

	MemberRegionDto verify(String memberUuid, VerifyMemberRegionCommand command);
}
