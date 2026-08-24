package com.sparta.member_regions_service.application.port.in;

// 동네 삭제
public interface DeleteMemberRegionUseCase {

	void delete(String memberUuid, long memberRegionId);
}
