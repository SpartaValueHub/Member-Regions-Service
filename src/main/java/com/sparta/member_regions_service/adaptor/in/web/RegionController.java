package com.sparta.member_regions_service.adaptor.in.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.member_regions_service.adaptor.in.web.mapper.MemberRegionWebMapper;
import com.sparta.member_regions_service.adaptor.in.web.vo.RegionResponseVo;
import com.sparta.member_regions_service.application.port.in.ListRegionsUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

// 지역 마스터(기준점) 조회 — 동네 선택 UI용
@Tag(name = "Regions", description = "지역 기준점 마스터")
@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController {

	// 목록
	private final ListRegionsUseCase listRegionsUseCase;
	// 매퍼
	private final MemberRegionWebMapper memberRegionWebMapper;

	@Operation(summary = "지역 목록·검색")
	@GetMapping
	public List<RegionResponseVo> list(
			@RequestParam(required = false) String keyword
	) {
		return listRegionsUseCase.list(keyword).stream()
				.map(memberRegionWebMapper::toRegionResponse)
				.toList();
	}
}
