package com.sparta.member_regions_service.adaptor.in.web.mapper;

import org.springframework.stereotype.Component;

import com.sparta.member_regions_service.adaptor.in.web.vo.AddMemberRegionRequestVo;
import com.sparta.member_regions_service.adaptor.in.web.vo.ChangeMemberRegionRequestVo;
import com.sparta.member_regions_service.adaptor.in.web.vo.MemberRegionResponseVo;
import com.sparta.member_regions_service.adaptor.in.web.vo.RegionResponseVo;
import com.sparta.member_regions_service.adaptor.in.web.vo.VerifyMemberRegionRequestVo;
import com.sparta.member_regions_service.application.port.in.dto.AddMemberRegionCommand;
import com.sparta.member_regions_service.application.port.in.dto.ChangeMemberRegionCommand;
import com.sparta.member_regions_service.application.port.in.dto.MemberRegionDto;
import com.sparta.member_regions_service.application.port.in.dto.RegionDto;
import com.sparta.member_regions_service.application.port.in.dto.VerifyMemberRegionCommand;

// Web VO ↔ Application DTO (UUID·시간 생성 금지)
@Component
public class MemberRegionWebMapper {

	public AddMemberRegionCommand toAddCommand(AddMemberRegionRequestVo request) {
		return new AddMemberRegionCommand(request.regionCode(), request.primary());
	}

	public ChangeMemberRegionCommand toChangeCommand(long memberRegionId, ChangeMemberRegionRequestVo request) {
		return new ChangeMemberRegionCommand(memberRegionId, request.regionCode());
	}

	public VerifyMemberRegionCommand toVerifyCommand(long memberRegionId, VerifyMemberRegionRequestVo request) {
		return new VerifyMemberRegionCommand(memberRegionId, request.latitude(), request.longitude());
	}

	public MemberRegionResponseVo toResponse(MemberRegionDto dto) {
		return new MemberRegionResponseVo(
				dto.memberRegionId(),
				dto.memberUuid(),
				dto.primary(),
				dto.regionCode(),
				dto.regionName(),
				dto.verified(),
				dto.verifiedAt(),
				dto.createdAt(),
				dto.updatedAt()
		);
	}

	public RegionResponseVo toRegionResponse(RegionDto dto) {
		return new RegionResponseVo(
				dto.regionCode(),
				dto.regionName(),
				dto.centerLatitude(),
				dto.centerLongitude()
		);
	}
}
