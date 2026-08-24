package com.sparta.member_regions_service.adaptor.out.mysql.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.member_regions_service.adaptor.out.mysql.entity.MemberRegionEntity;

public interface MemberRegionJpaRepository extends JpaRepository<MemberRegionEntity, Long> {

	List<MemberRegionEntity> findByMemberUuidOrderByCreatedAtAsc(String memberUuid);

	boolean existsByMemberUuidAndRegionCode(String memberUuid, long regionCode);

	long countByMemberUuid(String memberUuid);

	Optional<MemberRegionEntity> findByMemberRegionIdAndMemberUuid(Long memberRegionId, String memberUuid);
}
