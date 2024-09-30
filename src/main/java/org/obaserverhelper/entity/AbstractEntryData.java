package org.obaserverhelper.entity;

import lombok.Getter;
import org.springframework.lang.NonNull;

@Getter
public abstract class AbstractEntryData<T> extends AbstractData {

	private final T entry;

	protected AbstractEntryData(@NonNull T entry) {
		this.entry = entry;
	}
}
