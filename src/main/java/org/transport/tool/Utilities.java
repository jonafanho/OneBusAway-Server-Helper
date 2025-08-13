package org.transport.tool;

import jakarta.annotation.Nullable;

import java.util.List;

public final class Utilities {

	@Nullable
	public static <T> T getElement(List<T> data, int index) {
		return index >= 0 && index < data.size() ? data.get(index) : null;
	}

	public static double getDistance(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}
}
