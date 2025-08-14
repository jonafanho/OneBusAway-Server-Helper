package org.transport.tool;

public final class Constants {

	public static final int MILLIS_PER_SECOND = 1000;
	public static final int SECONDS_PER_MINUTE = 60;
	public static final int MINUTES_PER_HOUR = 60;
	public static final int HOURS_PER_DAY = 24;

	public static final int MILLIS_PER_MINUTE = SECONDS_PER_MINUTE * MILLIS_PER_SECOND;
	public static final int MILLIS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND;
	public static final int MILLIS_PER_DAY = HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLIS_PER_SECOND;

	public static final float METERS_PER_FOOT = 0.3048F;
}
