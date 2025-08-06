package org.transport.type;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.AttributeConverter;

public record Time(int millisAfterMidnight) {

	public Time() {
		this(0);
	}

	private Time(String timeString) {
		this(parse(timeString));
	}

	@Nonnull
	@Override
	public String toString() {
		final int seconds = millisAfterMidnight / 1000;
		final int minutes = seconds / 60;
		return String.format("%02d:%02d:%02d", minutes / 60, minutes % 60, seconds % 60);
	}

	private static int parse(String data) {
		try {
			final String[] dataSplit = data.split(":");
			return ((Integer.parseInt(dataSplit[0]) * 60 + Integer.parseInt(dataSplit[1])) * 60 + Integer.parseInt(dataSplit[2])) * 1000;
		} catch (Exception ignored) {
			return 0;
		}
	}

	public static class Converter implements AttributeConverter<Time, String> {

		@Nullable
		@Override
		public String convertToDatabaseColumn(Time time) {
			return time == null ? null : time.toString();
		}

		@Nullable
		@Override
		public Time convertToEntityAttribute(String data) {
			return data == null ? null : new Time(data);
		}
	}
}
