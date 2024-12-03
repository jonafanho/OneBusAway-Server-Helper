package org.transport.type;

import jakarta.annotation.Nullable;

public abstract class DTOBase<T> {

	public abstract T convert(int sourceIndex);

	protected static String convertId(int sourceIndex, @Nullable String id) {
		return org.springframework.util.StringUtils.hasText(id) ? String.format("%s_%s", sourceIndex + 1, id) : String.valueOf(sourceIndex + 1);
	}
}
