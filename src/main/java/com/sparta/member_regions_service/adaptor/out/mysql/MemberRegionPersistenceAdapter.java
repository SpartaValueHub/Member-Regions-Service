package com.sparta.member_regions_service.adaptor.out.mysql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sparta.member_regions_service.adaptor.out.mysql.entity.MemberRegionEntity;
import com.sparta.member_regions_service.adaptor.out.mysql.mapper.MemberRegionEntityMapper;
import com.sparta.member_regions_service.adaptor.out.mysql.repository.MemberRegionJpaRepository;
import com.sparta.member_regions_service.application.port.out.MemberRegionLoadPort;
import com.sparta.member_regions_service.application.port.out.MemberRegionSavePort;
import com.sparta.member_regions_service.domain.model.MemberRegion;

import lombok.RequiredArgsConstructor;

// 회원 동네 영속성 Adapter
@Component
@RequiredArgsConstructor
public class MemberRegionPersistenceAdapter implements MemberRegionLoadPort, MemberRegionSavePort {

	// JPA
	private final MemberRegionJpaRepository memberRegionJpaRepository;
	// 매퍼
	private final MemberRegionEntityMapper memberRegionEntityMapper;

	@Override
	public Optional<MemberRegion> findById(long memberRegionId) {
		return memberRegionJpaRepository.findById(memberRegionId)
				.map(memberRegionEntityMapper::toDomain);
	}

	@Override
	public List<MemberRegion> findByMemberUuid(String memberUuid) {
		return memberRegionJpaRepository.findByMemberUuidOrderByCreatedAtAsc(memberUuid).stream()
				.map(memberRegionEntityMapper::toDomain)
				.toList();
	}

	@Override
	public boolean existsByMemberUuidAndRegionCode(String memberUuid, int regionCode) {
		return memberRegionJpaRepository.existsByMemberUuidAndRegionCode(memberUuid, regionCode);
	}

	@Override
	public long countByMemberUuid(String memberUuid) {
		return memberRegionJpaRepository.countByMemberUuid(memberUuid);
	}

	@Override
	public MemberRegion save(MemberRegion memberRegion) {
		if (memberRegion.getMemberRegionId() == null) {
			MemberRegionEntity created = memberRegionJpaRepository.save(
					memberRegionEntityMapper.toNewEntity(memberRegion)
			);
			return memberRegionEntityMapper.toDomain(created);
		}
		MemberRegionEntity entity = memberRegionJpaRepository.findById(memberRegion.getMemberRegionId())
				.orElseThrow(() -> new IllegalStateException("회원 동네 Entity를 찾을 수 없습니다."));
		memberRegionEntityMapper.apply(memberRegion, entity);
		return memberRegionEntityMapper.toDomain(memberRegionJpaRepository.save(entity));
	}

	@Override
	public void delete(MemberRegion memberRegion) {
		if (memberRegion.getMemberRegionId() == null) {
			return;
		}
		memberRegionJpaRepository.deleteById(memberRegion.getMemberRegionId());
	}

	@Override
	public void saveAll(List<MemberRegion> memberRegions) {
		List<MemberRegionEntity> entities = new ArrayList<>();
		for (MemberRegion domain : memberRegions) {
			if (domain.getMemberRegionId() == null) {
				entities.add(memberRegionEntityMapper.toNewEntity(domain));
				continue;
			}
			MemberRegionEntity entity = memberRegionJpaRepository.findById(domain.getMemberRegionId())
					.orElseThrow(() -> new IllegalStateException("회원 동네 Entity를 찾을 수 없습니다."));
			memberRegionEntityMapper.apply(domain, entity);
			entities.add(entity);
		}
		memberRegionJpaRepository.saveAll(entities);
	}
}
