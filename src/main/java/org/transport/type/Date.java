package org.transport.type;

public final class Date extends DateTimeBase<Date> {

	private Date(int a, int b, int c) {
		super(a, b, c);
	}

	@Override
	public String toString() {
		return String.format("%s%s%s", a, b, c);
	}

	@Override
	public Class<Date> returnedClass() {
		return Date.class;
	}

	@Override
	public Date deepCopy(Date date) {
		return new Date(date.a, date.b, date.c);
	}

	@Override
	protected Date decode(String data) {
		return create(data);
	}

	public static Date create(String data) {
		try {
			return new Date(Integer.parseInt(data.substring(0, 4)), Integer.parseInt(data.substring(4, 6)), Integer.parseInt(data.substring(6, 8)));
		} catch (Exception ignored) {
			return new Date(0, 0, 0);
		}
	}
}
