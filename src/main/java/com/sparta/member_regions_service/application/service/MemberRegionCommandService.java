package com.sparta.member_regions_service.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sparta.member_regions_service.application.exception.ForbiddenException;
import com.sparta.member_regions_service.application.exception.UnauthorizedException;
import com.sparta.member_regions_service.application.port.in.AddMemberRegionUseCase;
import com.sparta.member_regions_service.application.port.in.ChangeMemberRegionUseCase;
import com.sparta.member_regions_service.application.port.in.DeleteMemberRegionUseCase;
import com.sparta.member_regions_service.application.port.in.SetPrimaryMemberRegionUseCase;
import com.sparta.member_regions_service.application.port.in.VerifyMemberRegionUseCase;
import com.sparta.member_regions_service.application.port.in.dto.AddMemberRegionCommand;
import com.sparta.member_regions_service.application.port.in.dto.ChangeMemberRegionCommand;
import com.sparta.member_regions_service.application.port.in.dto.MemberRegionDto;
import com.sparta.member_regions_service.application.port.in.dto.VerifyMemberRegionCommand;
import com.sparta.member_regions_service.application.port.out.MemberRegionLoadPort;
import com.sparta.member_regions_service.application.port.out.MemberRegionSavePort;
import com.sparta.member_regions_service.application.port.out.RegionLoadPort;
import com.sparta.member_regions_service.config.MemberRegionsPolicyProperties;
import com.sparta.member_regions_service.domain.exception.DuplicateMemberRegionException;
import com.sparta.member_regions_service.domain.exception.MemberRegionLimitExceededException;
import com.sparta.member_regions_service.domain.exception.MemberRegionNotFoundException;
import com.sparta.member_regions_service.domain.exception.RegionNotFoundException;
import com.sparta.member_regions_service.domain.exception.RegionVerificationFailedException;
import com.sparta.member_regions_service.domain.geo.GeoDistanceCalculator;
import com.sparta.member_regions_service.domain.model.MemberRegion;
import com.sparta.member_regions_service.domain.model.Region;

import lombok.RequiredArgsConstructor;

// 회원 동네 등록·변경·인증·삭제
@Service
@RequiredArgsConstructor
public class MemberRegionCommandService implements
		AddMemberRegionUseCase,
		ChangeMemberRegionUseCase,
		SetPrimaryMemberRegionUseCase,
		VerifyMemberRegionUseCase,
		DeleteMemberRegionUseCase {

	// 회원 동네 조회
	private final MemberRegionLoadPort memberRegionLoadPort;
	// 회원 동네 저장
	private final MemberRegionSavePort memberRegionSavePort;
	// 지역 마스터 조회
	private final RegionLoadPort regionLoadPort;
	// 정책
	private final MemberRegionsPolicyProperties policy;
	// 거리 계산
	private final GeoDistanceCalculator geoDistanceCalculator;
	// 시각
	private final Clock clock;

	@Override
	@Transactional
	public MemberRegionDto add(String memberUuid, AddMemberRegionCommand command) {
		String ownerUuid = requireMemberUuid(memberUuid);
		Region region = requireRegion(command.regionCode());

		if (memberRegionLoadPort.existsByMemberUuidAndRegionCode(ownerUuid, region.getRegionCode())) {
			throw new DuplicateMemberRegionException("이미 등록된 동네입니다.");
		}
		if (memberRegionLoadPort.countByMemberUuid(ownerUuid) >= policy.maxRegionsPerMember()) {
			throw new MemberRegionLimitExceededException(
					"동네는 최대 " + policy.maxRegionsPerMember() + "개까지 등록할 수 있습니다."
			);
		}

		Instant now = Instant.now(clock);
		List<MemberRegion> existing = memberRegionLoadPort.findByMemberUuid(ownerUuid);
		boolean makePrimary = resolvePrimaryOnAdd(command.primary(), existing.isEmpty());

		List<MemberRegion> toUpdate = new ArrayList<>();
		if (makePrimary) {
			for (MemberRegion item : existing) {
				if (item.isPrimary()) {
					item.clearPrimary(now);
					toUpdate.add(item);
				}
			}
		}

		MemberRegion created = MemberRegion.create(
				ownerUuid,
				makePrimary,
				region.getRegionCode(),
				region.getRegionName(),
				now
		);
		if (!toUpdate.isEmpty()) {
			memberRegionSavePort.saveAll(toUpdate);
		}
		MemberRegion saved = memberRegionSavePort.save(created);
		return toDto(saved);
	}

	@Override
	@Transactional
	public MemberRegionDto change(String memberUuid, ChangeMemberRegionCommand command) {
		String ownerUuid = requireMemberUuid(memberUuid);
		MemberRegion memberRegion = requireOwned(ownerUuid, command.memberRegionId());
		Region region = requireRegion(command.regionCode());

		if (memberRegion.getRegionCode() != region.getRegionCode()
				&& memberRegionLoadPort.existsByMemberUuidAndRegionCode(ownerUuid, region.getRegionCode())) {
			throw new DuplicateMemberRegionException("이미 등록된 동네입니다.");
		}

		Instant now = Instant.now(clock);
		memberRegion.changeRegion(region.getRegionCode(), region.getRegionName(), now);
		return toDto(memberRegionSavePort.save(memberRegion));
	}

	@Override
	@Transactional
	public MemberRegionDto setPrimary(String memberUuid, long memberRegionId) {
		String ownerUuid = requireMemberUuid(memberUuid);
		MemberRegion target = requireOwned(ownerUuid, memberRegionId);
		Instant now = Instant.now(clock);

		List<MemberRegion> toUpdate = new ArrayList<>();
		for (MemberRegion item : memberRegionLoadPort.findByMemberUuid(ownerUuid)) {
			if (item.getMemberRegionId().equals(target.getMemberRegionId())) {
				continue;
			}
			if (item.isPrimary()) {
				item.clearPrimary(now);
				toUpdate.add(item);
			}
		}
		target.markAsPrimary(now);
		toUpdate.add(target);
		memberRegionSavePort.saveAll(toUpdate);
		return toDto(target);
	}

	@Override
	@Transactional
	public MemberRegionDto verify(String memberUuid, VerifyMemberRegionCommand command) {
		String ownerUuid = requireMemberUuid(memberUuid);
		MemberRegion memberRegion = requireOwned(ownerUuid, command.memberRegionId());
		Region region = requireRegion(memberRegion.getRegionCode());

		boolean within = geoDistanceCalculator.isWithinRadius(
				command.latitude(),
				command.longitude(),
				region.getCenterLatitude(),
				region.getCenterLongitude(),
				policy.verificationRadiusMeters()
		);
		if (!within) {
			throw new RegionVerificationFailedException(
					"선택한 동네 인증 범위(" + policy.verificationRadiusMeters() + "m) 밖에 있습니다."
			);
		}

		Instant now = Instant.now(clock);
		memberRegion.markVerified(now, now);
		MemberRegion saved = memberRegionSavePort.save(memberRegion);
		return toDto(saved);
	}

	@Override
	@Transactional
	public void delete(String memberUuid, long memberRegionId) {
		String ownerUuid = requireMemberUuid(memberUuid);
		MemberRegion target = requireOwned(ownerUuid, memberRegionId);
		boolean wasPrimary = target.isPrimary();
		memberRegionSavePort.delete(target);

		if (!wasPrimary) {
			return;
		}

		List<MemberRegion> remainings = memberRegionLoadPort.findByMemberUuid(ownerUuid);
		if (remainings.isEmpty()) {
			return;
		}
		Instant now = Instant.now(clock);
		MemberRegion nextPrimary = remainings.get(0);
		nextPrimary.markAsPrimary(now);
		memberRegionSavePort.save(nextPrimary);
	}

	// 첫 동네이거나 요청이 true면 대표
	private boolean resolvePrimaryOnAdd(Boolean requestedPrimary, boolean isFirst) {
		if (isFirst) {
			return true;
		}
		return Boolean.TRUE.equals(requestedPrimary);
	}

	private MemberRegion requireOwned(String memberUuid, long memberRegionId) {
		MemberRegion memberRegion = memberRegionLoadPort.findById(memberRegionId)
				.orElseThrow(() -> new MemberRegionNotFoundException("동네 정보를 찾을 수 없습니다."));
		if (!memberRegion.getMemberUuid().equals(memberUuid)) {
			throw new ForbiddenException("해당 동네에 대한 권한이 없습니다.");
		}
		return memberRegion;
	}

	private Region requireRegion(long regionCode) {
		return regionLoadPort.findByRegionCode(regionCode)
				.orElseThrow(() -> new RegionNotFoundException("지원하지 않는 지역 코드입니다."));
	}

	private String requireMemberUuid(String memberUuid) {
		if (!StringUtils.hasText(memberUuid)) {
			throw new UnauthorizedException("회원 정보가 없습니다.");
		}
		return memberUuid.trim();
	}

	private MemberRegionDto toDto(MemberRegion memberRegion) {
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
}
