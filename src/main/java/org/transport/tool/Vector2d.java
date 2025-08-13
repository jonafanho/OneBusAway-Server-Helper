package org.transport.tool;

import lombok.Getter;

@Getter
public final class Vector2d {

	private double x;
	private double y;

	public void normalizeAndAdd(double x, double y) {
		resizeAndAdd(x, y, 1);
	}

	public void resizeAndAdd(double x, double y, double length) {
		if (x != 0 || y != 0) {
			final double multiplier = length / Utilities.getDistance(x, y);
			this.x += x * multiplier;
			this.y += y * multiplier;
		}
	}
}
