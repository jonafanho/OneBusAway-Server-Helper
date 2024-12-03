package org.transport.type;

public final class Time extends DateTimeBase<Time> {

	private Time(int a, int b, int c) {
		super(a, b, c);
	}

	@Override
	public String toString() {
		return String.format("%s:%s:%s", a, b, c);
	}

	@Override
	public Class<Time> returnedClass() {
		return Time.class;
	}

	@Override
	public Time deepCopy(Time time) {
		return new Time(time.a, time.b, time.c);
	}

	@Override
	protected Time decode(String data) {
		return create(data);
	}

	public static Time create(String data) {
		try {
			final String[] dataSplit = data.split(":");
			return new Time(Integer.parseInt(dataSplit[0]), Integer.parseInt(dataSplit[1]), Integer.parseInt(dataSplit[2]));
		} catch (Exception ignored) {
			return new Time(0, 0, 0);
		}
	}
}
