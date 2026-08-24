package com.sparta.member_regions_service.application.port.out;

import java.util.List;
import java.util.Optional;

import com.sparta.member_regions_service.domain.model.MemberRegion;

// 회원 동네 조회
public interface MemberRegionLoadPort {

	Optional<MemberRegion> findById(long memberRegionId);

	List<MemberRegion> findByMemberUuid(String memberUuid);

	boolean existsByMemberUuidAndRegionCode(String memberUuid, long regionCode);

	long countByMemberUuid(String memberUuid);
}
