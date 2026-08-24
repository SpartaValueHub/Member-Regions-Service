package com.sparta.member_regions_service.application.port.out;

import java.util.List;

import com.sparta.member_regions_service.domain.model.MemberRegion;

// 회원 동네 저장·삭제
public interface MemberRegionSavePort {

	MemberRegion save(MemberRegion memberRegion);

	void delete(MemberRegion memberRegion);

	void saveAll(List<MemberRegion> memberRegions);
}
