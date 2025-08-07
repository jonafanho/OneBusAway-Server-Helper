package org.transport.type;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.AttributeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record Date(LocalDate localDate) {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	public Date() {
		this(LocalDate.EPOCH);
	}

	private Date(String dateString) {
		this(LocalDate.parse(dateString, FORMATTER));
	}

	@Nonnull
	@Override
	public String toString() {
		return localDate.format(FORMATTER);
	}

	public static final class Converter implements AttributeConverter<Date, String> {

		@Nullable
		@Override
		public String convertToDatabaseColumn(Date date) {
			return date == null ? null : date.toString();
		}

		@Nullable
		@Override
		public Date convertToEntityAttribute(String data) {
			return data == null ? null : new Date(data);
		}
	}
}
