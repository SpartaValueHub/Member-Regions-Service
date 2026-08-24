package com.sparta.member_regions_service.config;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.member_regions_service.application.port.out.RegionLoadPort;
import com.sparta.member_regions_service.application.port.out.RegionSavePort;
import com.sparta.member_regions_service.domain.model.Region;

import lombok.RequiredArgsConstructor;

// regions 기준점 시드 — 없는 region_code만 추가
@Component
@RequiredArgsConstructor
public class RegionSeedRunner implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(RegionSeedRunner.class);

	// 시드 파일 경로
	private final String seedClasspath = "db/regions-seed.json";
	// 조회
	private final RegionLoadPort regionLoadPort;
	// 저장
	private final RegionSavePort regionSavePort;
	// JSON
	private final ObjectMapper objectMapper;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<Region> seedRegions = loadSeedRegions();
		Set<Long> existingCodes = new HashSet<>();
		for (Region region : regionLoadPort.findAll()) {
			existingCodes.add(region.getRegionCode());
		}

		List<Region> toInsert = new ArrayList<>();
		for (Region region : seedRegions) {
			if (!existingCodes.contains(region.getRegionCode())) {
				toInsert.add(region);
			}
		}
		if (toInsert.isEmpty()) {
			return;
		}
		regionSavePort.saveAll(toInsert);
		log.info("regions seed inserted: {} rows (seed total {})", toInsert.size(), seedRegions.size());
	}

	private List<Region> loadSeedRegions() throws Exception {
		ClassPathResource resource = new ClassPathResource(seedClasspath);
		try (InputStream inputStream = resource.getInputStream()) {
			List<SeedRow> rows = objectMapper.readValue(inputStream, new TypeReference<>() {
			});
			List<Region> regions = new ArrayList<>();
			for (SeedRow row : rows) {
				regions.add(Region.restore(
						row.regionCode(),
						row.regionName(),
						row.centerLatitude(),
						row.centerLongitude()
				));
			}
			return regions;
		}
	}

	// JSON 한 행
	private record SeedRow(
			long regionCode,
			String regionName,
			BigDecimal centerLatitude,
			BigDecimal centerLongitude
	) {
	}
}
