package com.sparta.member_regions_service.adaptor.in.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.member_regions_service.adaptor.in.web.mapper.MemberRegionWebMapper;
import com.sparta.member_regions_service.adaptor.in.web.vo.AddMemberRegionRequestVo;
import com.sparta.member_regions_service.adaptor.in.web.vo.ChangeMemberRegionRequestVo;
import com.sparta.member_regions_service.adaptor.in.web.vo.MemberRegionResponseVo;
import com.sparta.member_regions_service.adaptor.in.web.vo.VerifyMemberRegionRequestVo;
import com.sparta.member_regions_service.application.port.in.AddMemberRegionUseCase;
import com.sparta.member_regions_service.application.port.in.ChangeMemberRegionUseCase;
import com.sparta.member_regions_service.application.port.in.DeleteMemberRegionUseCase;
import com.sparta.member_regions_service.application.port.in.ListMyMemberRegionsUseCase;
import com.sparta.member_regions_service.application.port.in.SetPrimaryMemberRegionUseCase;
import com.sparta.member_regions_service.application.port.in.VerifyMemberRegionUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// 마이페이지 동네 인증 API
@Tag(name = "Member Regions", description = "회원 동네 선택·인증")
@RestController
@RequestMapping("/api/v1/member-regions")
@RequiredArgsConstructor
public class MemberRegionController {

	// 등록
	private final AddMemberRegionUseCase addMemberRegionUseCase;
	// 변경
	private final ChangeMemberRegionUseCase changeMemberRegionUseCase;
	// 대표 지정
	private final SetPrimaryMemberRegionUseCase setPrimaryMemberRegionUseCase;
	// 인증
	private final VerifyMemberRegionUseCase verifyMemberRegionUseCase;
	// 삭제
	private final DeleteMemberRegionUseCase deleteMemberRegionUseCase;
	// 목록
	private final ListMyMemberRegionsUseCase listMyMemberRegionsUseCase;
	// VO 매퍼
	private final MemberRegionWebMapper memberRegionWebMapper;

	@Operation(summary = "내 동네 목록 조회")
	@GetMapping
	public List<MemberRegionResponseVo> listMine(
			@RequestHeader(value = InternalAuthHeaders.MEMBER_UUID, required = false) String memberUuid
	) {
		return listMyMemberRegionsUseCase.listMine(memberUuid).stream()
				.map(memberRegionWebMapper::toResponse)
				.toList();
	}

	@Operation(summary = "동네 등록 (최대 2개)")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MemberRegionResponseVo add(
			@RequestHeader(value = InternalAuthHeaders.MEMBER_UUID, required = false) String memberUuid,
			@Valid @RequestBody AddMemberRegionRequestVo request
	) {
		return memberRegionWebMapper.toResponse(
				addMemberRegionUseCase.add(memberUuid, memberRegionWebMapper.toAddCommand(request))
		);
	}

	@Operation(summary = "선택 동네 변경 (기존 인증 무효화)")
	@PatchMapping("/{memberRegionId}")
	public MemberRegionResponseVo change(
			@RequestHeader(value = InternalAuthHeaders.MEMBER_UUID, required = false) String memberUuid,
			@PathVariable long memberRegionId,
			@Valid @RequestBody ChangeMemberRegionRequestVo request
	) {
		return memberRegionWebMapper.toResponse(
				changeMemberRegionUseCase.change(
						memberUuid,
						memberRegionWebMapper.toChangeCommand(memberRegionId, request)
				)
		);
	}

	@Operation(summary = "대표 동네 지정")
	@PatchMapping("/{memberRegionId}/primary")
	public MemberRegionResponseVo setPrimary(
			@RequestHeader(value = InternalAuthHeaders.MEMBER_UUID, required = false) String memberUuid,
			@PathVariable long memberRegionId
	) {
		return memberRegionWebMapper.toResponse(
				setPrimaryMemberRegionUseCase.setPrimary(memberUuid, memberRegionId)
		);
	}

	@Operation(summary = "GPS 동네 인증 (통과 시 verifiedAt 즉시 반영)")
	@PostMapping("/{memberRegionId}/verify")
	public MemberRegionResponseVo verify(
			@RequestHeader(value = InternalAuthHeaders.MEMBER_UUID, required = false) String memberUuid,
			@PathVariable long memberRegionId,
			@Valid @RequestBody VerifyMemberRegionRequestVo request
	) {
		return memberRegionWebMapper.toResponse(
				verifyMemberRegionUseCase.verify(
						memberUuid,
						memberRegionWebMapper.toVerifyCommand(memberRegionId, request)
				)
		);
	}

	@Operation(summary = "동네 삭제")
	@DeleteMapping("/{memberRegionId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@RequestHeader(value = InternalAuthHeaders.MEMBER_UUID, required = false) String memberUuid,
			@PathVariable long memberRegionId
	) {
		deleteMemberRegionUseCase.delete(memberUuid, memberRegionId);
	}
}
