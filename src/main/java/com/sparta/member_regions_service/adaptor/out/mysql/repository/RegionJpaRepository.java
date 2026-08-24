package com.sparta.member_regions_service.adaptor.out.mysql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.member_regions_service.adaptor.out.mysql.entity.RegionEntity;

public interface RegionJpaRepository extends JpaRepository<RegionEntity, Integer> {

	List<RegionEntity> findByRegionNameContainingIgnoreCaseOrderByRegionNameAsc(String keyword);

	List<RegionEntity> findAllByOrderByRegionNameAsc();
}
