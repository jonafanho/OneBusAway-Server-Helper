package org.obaserverhelper.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(force = true)
public abstract class AbstractData<T> {

	private final References references = new References();
	@NonNull
	private final T entry;
	private final List<T> list = new ArrayList<>();
	private boolean limitExceeded;
	private boolean outOfRange;

	protected AbstractData(@NonNull T entry) {
		this.entry = entry;
	}
}
