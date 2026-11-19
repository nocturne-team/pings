package com.github._0x00acab.callouts.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class VanillaPingMathTest {
	@Test
	void pointsAheadWhenFacingTarget() {
		// Yaw 0 faces +Z.
		assertEquals("↑", VanillaPingMath.arrow(0, 0, 10));
		// Yaw 90 faces -X.
		assertEquals("↑", VanillaPingMath.arrow(90, -10, 0));
	}

	@Test
	void pointsRightAndBehind() {
		// Facing +Z, your right is -X.
		assertEquals("→", VanillaPingMath.arrow(0, -10, 0));
		assertEquals("←", VanillaPingMath.arrow(0, 10, 0));
		assertEquals("↓", VanillaPingMath.arrow(0, 0, -10));
		assertEquals("↗", VanillaPingMath.arrow(0, -10, 10));
	}

	@Test
	void handlesUnwrappedYaw() {
		assertEquals("↑", VanillaPingMath.arrow(720, 0, 10));
		assertEquals("↑", VanillaPingMath.arrow(-270, -10, 0));
	}

	@Test
	void dotWhenDirectlyAboveOrBelow() {
		assertEquals("•", VanillaPingMath.arrow(0, 0.5, 0.5));
	}

	@Test
	void roundsDistance() {
		assertEquals("42m", VanillaPingMath.distance(41.6));
	}
}
