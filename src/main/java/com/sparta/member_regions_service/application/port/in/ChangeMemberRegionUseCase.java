package com.sparta.member_regions_service.application.port.in;

import com.sparta.member_regions_service.application.port.in.dto.ChangeMemberRegionCommand;
import com.sparta.member_regions_service.application.port.in.dto.MemberRegionDto;

// 선택 동네 변경 (인증 무효화)
public interface ChangeMemberRegionUseCase {

	MemberRegionDto change(String memberUuid, ChangeMemberRegionCommand command);
}
