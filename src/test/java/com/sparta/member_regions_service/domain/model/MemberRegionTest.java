package com.sparta.member_regions_service.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class MemberRegionTest {

	@Test
	void create_startsUnverified() {
		Instant now = Instant.parse("2026-08-24T00:00:00Z");
		MemberRegion region = MemberRegion.create("member-1", true, 1168010100, "역삼동", now);

		assertThat(region.isVerified()).isFalse();
		assertThat(region.getVerifiedAt()).isNull();
		assertThat(region.isPrimary()).isTrue();
	}

	@Test
	void changeRegion_clearsVerification() {
		Instant created = Instant.parse("2026-08-24T00:00:00Z");
		Instant verified = Instant.parse("2026-08-24T01:00:00Z");
		Instant changed = Instant.parse("2026-08-24T02:00:00Z");
		MemberRegion region = MemberRegion.create("member-1", true, 1168010100, "역삼동", created);
		region.markVerified(verified, verified);

		region.changeRegion(1168010800, "논현동", changed);

		assertThat(region.isVerified()).isFalse();
		assertThat(region.getRegionCode()).isEqualTo(1168010800);
		assertThat(region.getRegionName()).isEqualTo("논현동");
	}

	@Test
	void create_rejectsBlankName() {
		assertThatThrownBy(() ->
				MemberRegion.create("member-1", false, 1, "  ", Instant.parse("2026-08-24T00:00:00Z"))
		).isInstanceOf(IllegalArgumentException.class);
	}
}
