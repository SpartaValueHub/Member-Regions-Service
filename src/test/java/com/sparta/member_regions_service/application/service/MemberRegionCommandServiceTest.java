package com.sparta.member_regions_service.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sparta.member_regions_service.application.port.in.dto.AddMemberRegionCommand;
import com.sparta.member_regions_service.application.port.in.dto.MemberRegionDto;
import com.sparta.member_regions_service.application.port.in.dto.VerifyMemberRegionCommand;
import com.sparta.member_regions_service.application.port.out.MemberRegionLoadPort;
import com.sparta.member_regions_service.application.port.out.MemberRegionSavePort;
import com.sparta.member_regions_service.application.port.out.RegionLoadPort;
import com.sparta.member_regions_service.config.MemberRegionsPolicyProperties;
import com.sparta.member_regions_service.domain.exception.MemberRegionLimitExceededException;
import com.sparta.member_regions_service.domain.exception.RegionVerificationFailedException;
import com.sparta.member_regions_service.domain.geo.GeoDistanceCalculator;
import com.sparta.member_regions_service.domain.model.MemberRegion;
import com.sparta.member_regions_service.domain.model.Region;

@ExtendWith(MockitoExtension.class)
class MemberRegionCommandServiceTest {

	@Mock
	private MemberRegionLoadPort memberRegionLoadPort;
	@Mock
	private MemberRegionSavePort memberRegionSavePort;
	@Mock
	private RegionLoadPort regionLoadPort;

	// 고정 시각
	private final Clock clock = Clock.fixed(Instant.parse("2026-08-24T12:00:00Z"), ZoneOffset.UTC);
	// 정책
	private final MemberRegionsPolicyProperties policy = new MemberRegionsPolicyProperties(2, 3000);
	// 거리
	private final GeoDistanceCalculator geoDistanceCalculator = new GeoDistanceCalculator();
	// 대상
	private MemberRegionCommandService service;

	@BeforeEach
	void setUp() {
		service = new MemberRegionCommandService(
				memberRegionLoadPort,
				memberRegionSavePort,
				regionLoadPort,
				policy,
				geoDistanceCalculator,
				clock
		);
	}

	@Test
	void add_rejectsWhenLimitExceeded() {
		Region region = Region.restore(
				1168010100,
				"서울특별시 강남구 역삼동",
				new BigDecimal("37.5007"),
				new BigDecimal("127.0366")
		);
		when(regionLoadPort.findByRegionCode(1168010100)).thenReturn(Optional.of(region));
		when(memberRegionLoadPort.existsByMemberUuidAndRegionCode(anyString(), anyLong())).thenReturn(false);
		when(memberRegionLoadPort.countByMemberUuid("member-1")).thenReturn(2L);

		assertThatThrownBy(() -> service.add("member-1", new AddMemberRegionCommand(1168010100, null)))
				.isInstanceOf(MemberRegionLimitExceededException.class);
		verify(memberRegionSavePort, never()).save(any());
	}

	@Test
	void verify_persistsVerifiedAt_whenWithinRadius() {
		Region region = Region.restore(
				1168010100,
				"서울특별시 강남구 역삼동",
				new BigDecimal("37.5007"),
				new BigDecimal("127.0366")
		);
		MemberRegion memberRegion = MemberRegion.restore(
				10L,
				"member-1",
				true,
				1168010100,
				"서울특별시 강남구 역삼동",
				null,
				Instant.parse("2026-08-24T10:00:00Z"),
				null
		);
		when(memberRegionLoadPort.findById(10L)).thenReturn(Optional.of(memberRegion));
		when(regionLoadPort.findByRegionCode(1168010100)).thenReturn(Optional.of(region));
		when(memberRegionSavePort.save(any(MemberRegion.class))).thenAnswer(invocation -> invocation.getArgument(0));

		MemberRegionDto dto = service.verify(
				"member-1",
				new VerifyMemberRegionCommand(10L, new BigDecimal("37.5007"), new BigDecimal("127.0366"))
		);

		assertThat(dto.verified()).isTrue();
		assertThat(dto.verifiedAt()).isEqualTo(Instant.parse("2026-08-24T12:00:00Z"));
		assertThat(dto.regionName()).isEqualTo("서울특별시 강남구 역삼동");

		ArgumentCaptor<MemberRegion> captor = ArgumentCaptor.forClass(MemberRegion.class);
		verify(memberRegionSavePort).save(captor.capture());
		assertThat(captor.getValue().isVerified()).isTrue();
	}

	@Test
	void verify_failsOutsideRadius() {
		Region region = Region.restore(
				1168010100,
				"서울특별시 강남구 역삼동",
				new BigDecimal("37.5007"),
				new BigDecimal("127.0366")
		);
		MemberRegion memberRegion = MemberRegion.restore(
				10L,
				"member-1",
				true,
				1168010100,
				"서울특별시 강남구 역삼동",
				null,
				Instant.parse("2026-08-24T10:00:00Z"),
				null
		);
		when(memberRegionLoadPort.findById(10L)).thenReturn(Optional.of(memberRegion));
		when(regionLoadPort.findByRegionCode(1168010100)).thenReturn(Optional.of(region));

		assertThatThrownBy(() -> service.verify(
				"member-1",
				new VerifyMemberRegionCommand(10L, new BigDecimal("35.1796"), new BigDecimal("129.0756"))
		)).isInstanceOf(RegionVerificationFailedException.class);
		verify(memberRegionSavePort, never()).save(any());
	}
}
