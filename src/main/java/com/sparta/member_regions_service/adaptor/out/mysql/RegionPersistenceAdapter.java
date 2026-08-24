package com.sparta.member_regions_service.adaptor.out.mysql;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.sparta.member_regions_service.adaptor.out.mysql.mapper.RegionEntityMapper;
import com.sparta.member_regions_service.adaptor.out.mysql.repository.RegionJpaRepository;
import com.sparta.member_regions_service.application.port.out.RegionLoadPort;
import com.sparta.member_regions_service.application.port.out.RegionSavePort;
import com.sparta.member_regions_service.domain.model.Region;

import lombok.RequiredArgsConstructor;

// 지역 마스터 영속성 Adapter
@Component
@RequiredArgsConstructor
public class RegionPersistenceAdapter implements RegionLoadPort, RegionSavePort {

	// JPA
	private final RegionJpaRepository regionJpaRepository;
	// 매퍼
	private final RegionEntityMapper regionEntityMapper;

	@Override
	public Optional<Region> findByRegionCode(long regionCode) {
		return regionJpaRepository.findById(regionCode)
				.map(regionEntityMapper::toDomain);
	}

	@Override
	public List<Region> findAll() {
		return regionJpaRepository.findAllByOrderByRegionNameAsc().stream()
				.map(regionEntityMapper::toDomain)
				.toList();
	}

	@Override
	public List<Region> findByRegionNameContaining(String keyword) {
		return regionJpaRepository.findByRegionNameContainingIgnoreCaseOrderByRegionNameAsc(keyword).stream()
				.map(regionEntityMapper::toDomain)
				.toList();
	}

	@Override
	public long count() {
		return regionJpaRepository.count();
	}

	@Override
	public void saveAll(List<Region> regions) {
		regionJpaRepository.saveAll(
				regions.stream()
						.map(regionEntityMapper::toEntity)
						.toList()
		);
	}
}
