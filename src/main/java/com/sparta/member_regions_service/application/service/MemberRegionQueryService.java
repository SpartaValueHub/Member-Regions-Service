package com.sparta.member_regions_service.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sparta.member_regions_service.application.exception.UnauthorizedException;
import com.sparta.member_regions_service.application.port.in.ListMyMemberRegionsUseCase;
import com.sparta.member_regions_service.application.port.in.ListRegionsUseCase;
import com.sparta.member_regions_service.application.port.in.dto.MemberRegionDto;
import com.sparta.member_regions_service.application.port.in.dto.RegionDto;
import com.sparta.member_regions_service.application.port.out.MemberRegionLoadPort;
import com.sparta.member_regions_service.application.port.out.RegionLoadPort;
import com.sparta.member_regions_service.domain.model.MemberRegion;
import com.sparta.member_regions_service.domain.model.Region;

import lombok.RequiredArgsConstructor;

// 회원 동네·지역 마스터 조회
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRegionQueryService implements ListMyMemberRegionsUseCase, ListRegionsUseCase {

	// 회원 동네 조회
	private final MemberRegionLoadPort memberRegionLoadPort;
	// 지역 마스터 조회
	private final RegionLoadPort regionLoadPort;

	@Override
	public List<MemberRegionDto> listMine(String memberUuid) {
		String ownerUuid = requireMemberUuid(memberUuid);
		return memberRegionLoadPort.findByMemberUuid(ownerUuid).stream()
				.map(this::toMemberRegionDto)
				.toList();
	}

	@Override
	public List<RegionDto> list(String keyword) {
		List<Region> regions = StringUtils.hasText(keyword)
				? regionLoadPort.findByRegionNameContaining(keyword.trim())
				: regionLoadPort.findAll();
		return regions.stream()
				.map(this::toRegionDto)
				.toList();
	}

	private String requireMemberUuid(String memberUuid) {
		if (!StringUtils.hasText(memberUuid)) {
			throw new UnauthorizedException("회원 정보가 없습니다.");
		}
		return memberUuid.trim();
	}

	private MemberRegionDto toMemberRegionDto(MemberRegion memberRegion) {
		return new MemberRegionDto(
				memberRegion.getMemberRegionId(),
				memberRegion.getMemberUuid(),
				memberRegion.isPrimary(),
				memberRegion.getRegionCode(),
				memberRegion.getRegionName(),
				memberRegion.isVerified(),
				memberRegion.getVerifiedAt(),
				memberRegion.getCreatedAt(),
				memberRegion.getUpdatedAt()
		);
	}

	private RegionDto toRegionDto(Region region) {
		return new RegionDto(
				region.getRegionCode(),
				region.getRegionName(),
				region.getCenterLatitude(),
				region.getCenterLongitude()
		);
	}
}
