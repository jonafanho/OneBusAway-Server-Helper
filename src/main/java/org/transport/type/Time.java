package org.transport.type;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.AttributeConverter;
import org.transport.tool.Constants;

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
		final int seconds = millisAfterMidnight / Constants.MILLIS_PER_SECOND;
		final int minutes = seconds / Constants.SECONDS_PER_MINUTE;
		return String.format("%02d:%02d:%02d", minutes / Constants.MINUTES_PER_HOUR, minutes % Constants.MINUTES_PER_HOUR, seconds % Constants.SECONDS_PER_MINUTE);
	}

	private static int parse(String data) {
		try {
			final String[] dataSplit = data.split(":");
			return ((Integer.parseInt(dataSplit[0]) * Constants.MINUTES_PER_HOUR + Integer.parseInt(dataSplit[1])) * Constants.SECONDS_PER_MINUTE + Integer.parseInt(dataSplit[2])) * Constants.MILLIS_PER_SECOND;
		} catch (Exception ignored) {
			return 0;
		}
	}

	public static final class Converter implements AttributeConverter<Time, String> {

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
